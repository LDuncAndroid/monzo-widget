package com.emmav.monzo.widget.feature.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.feature.home.HomeActivity
import com.emmav.monzo.widget.feature.login.LoginActivity
import com.emmav.monzo.widget.feature.setupclient.SetupClientActivity
import com.emmav.monzo.widget.feature.splash.SplashViewModel.AppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, Observer {
            when (it.appState) {
                AppState.REQUIRES_CLIENT -> startActivity(SetupClientActivity.buildIntent(this))
                AppState.REQUIRES_TOKEN -> startActivity(LoginActivity.buildIntent(this))
                AppState.AUTHENTICATED -> startActivity(HomeActivity.buildIntent(this))
            }
        })
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, SplashActivity::class.java)
        }
    }
}