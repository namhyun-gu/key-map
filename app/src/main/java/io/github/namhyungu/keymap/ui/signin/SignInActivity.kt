package io.github.namhyungu.keymap.ui.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.databinding.ActivitySigninBinding
import io.github.namhyungu.keymap.ui.home.HomeActivity
import io.github.namhyungu.keymap.util.EventObserver
import io.github.namhyungu.keymap.util.viewBindings
import timber.log.Timber

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private val binding: ActivitySigninBinding by viewBindings(ActivitySigninBinding::inflate)
    private val viewModel: SignInViewModel by viewModels()

    private val googleSignInClient by lazy { GoogleSignIn.getClient(this, gso) }
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val requestSignIn = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { handleSignIn(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            viewModel.startSignIn()
            requestSignIn.launch(googleSignInClient.signInIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.uiState.observe(this, {
            val (isRequestSignIn) = it

            binding.progressOverlay.isVisible = isRequestSignIn
        })

        viewModel.signInSuccess.observe(this, EventObserver {
            startActivity(Intent(this, HomeActivity::class.java))
        })

        viewModel.signInFailedEvent.observe(this, EventObserver {
            Toast.makeText(this, getString(R.string.msg_failed_sign_in), Toast.LENGTH_SHORT).show()
        })
    }

    private fun handleSignIn(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.signIn(idToken)
            } else {
                viewModel.signInFailed()
            }
        } catch (e: ApiException) {
            viewModel.signInFailed()
            Timber.e(e)
        }
        viewModel.stopSignIn()
    }
}