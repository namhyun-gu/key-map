package io.github.namhyungu.keymap.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.ui.KeyMapTheme
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeyMapTheme {
                val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val userState by userViewModel.user.collectAsStateWithLifecycle()

                HomeScreen(
                    homeUiState = uiState,
                    userState = userState,
                    onLocationChange = {
                        homeViewModel.updateLocation(it)
                    },
                    onSignInClick = {
                        userViewModel.startSignIn()
                        requestSignIn.launch(googleSignInClient.signInIntent)
                    }
                )
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

        userViewModel.signIn(idToken)
    }
}