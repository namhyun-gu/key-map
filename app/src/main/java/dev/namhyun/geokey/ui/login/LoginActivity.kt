/*
 * Copyright 2020 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.namhyun.geokey.ui.login

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val viewModel by viewModels<LoginViewModel>()
    private val auth = Firebase.auth

    private var showOneTap = true

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signInClient: GoogleSignInClient

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            val currentUser = auth.currentUser
            updateUi(currentUser)
        } else {
            Toast.makeText(this, "Required location permission", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    private val requestSignInWithGoogle = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                signInToFirebase(idToken)
            }
        } catch (e: ApiException) {
            setLoadingProgress(false)
            setLoginButtonEnable(true)
            showFailedLoginToast()
            Timber.e(e.localizedMessage)
        }
    }

    private val requestSignInWithOneTap = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
            val idToken = credential.googleIdToken

            if (idToken != null) {
                signInToFirebase(idToken)
            }
        } catch (e: ApiException) {
            setLoadingProgress(false)
            setLoginButtonEnable(true)
            showFailedLoginToast()
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Timber.d("One-tap dialog was closed.")
                    showOneTap = false
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Timber.d("One-tap encountered a network error.")
                }
                else -> {
                    Timber.d(
                        "Couldn't get credential from result. (${e.localizedMessage})"
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        signInClient = GoogleSignIn.getClient(this, signInOptions)

        login.setOnClickListener {
            setLoadingProgress(true)
            setLoginButtonEnable(false)
            signInWithGoogleSignIn()
        }
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermission.launch()
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            if (showOneTap) {
                setLoadingProgress(true)
                setLoginButtonEnable(false)
                signInWithOneTap()
            }
        }
    }

    private fun setLoadingProgress(visible: Boolean) {
        loading_progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setLoginButtonEnable(enable: Boolean) {
        login.isEnabled = enable
    }

    private fun showFailedLoginToast() {
        Toast.makeText(this, R.string.msg_login_failed, Toast.LENGTH_SHORT).show()
    }

    private fun signInToFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    updateUi(currentUser)
                } else {
                    Timber.e(it.exception)
                    updateUi(null)
                }
            }
    }

    private fun signInWithOneTap() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                            .setFillInIntent(null)
                            .build()
                    requestSignInWithOneTap.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Timber.e("Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Timber.e(e.localizedMessage)
            }
    }

    private fun signInWithGoogleSignIn() {
        requestSignInWithGoogle.launch(signInClient.signInIntent)
    }
}
