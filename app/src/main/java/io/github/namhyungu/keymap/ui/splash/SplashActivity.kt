package io.github.namhyungu.keymap.ui.splash

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.ui.home.HomeActivity
import io.github.namhyungu.keymap.ui.signin.SignInActivity
import io.github.namhyungu.keymap.util.EventObserver

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.moveHomeEvent.observe(this, EventObserver {
            startActivity(Intent(this, HomeActivity::class.java))
        })

        viewModel.requestSignInEvent.observe(this, EventObserver {
            startActivity(Intent(this, SignInActivity::class.java))
        })

        viewModel.start()
    }
}