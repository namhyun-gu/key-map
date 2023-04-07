package io.github.namhyungu.keymap.ui.signin

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.ui.home.HomeActivity
import io.github.namhyungu.keymap.ui.home.SignInEvent
import io.github.namhyungu.keymap.ui.home.UserViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()
    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val googleSignInClient by lazy { GoogleSignIn.getClient(this, googleSignInOptions) }
    private val requestSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleSignIn(result)
    }

    private fun startSignIn() {
        requestSignIn.launch(googleSignInClient.signInIntent)
    }

    private fun collectWhenStarted() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInEvent.collect { handleSignInEvent(it) }
            }
        }
    }

    private fun handleSignInEvent(it: SignInEvent) {
        when (it) {
            SignInEvent.Success -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            SignInEvent.Fail -> {
                Toast.makeText(this, getString(R.string.msg_failed_sign_in), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignIn(result: ActivityResult) {
        val idToken = kotlin.runCatching {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.getResult(ApiException::class.java)?.idToken
        }.onFailure { e ->
            Timber.e(e)
        }.getOrNull()

        viewModel.signIn(idToken)
    }
}