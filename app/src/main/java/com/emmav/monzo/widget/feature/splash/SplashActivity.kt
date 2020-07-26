package com.emmav.monzo.widget.feature.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.feature.login.LoginActivity
import com.emmav.monzo.widget.feature.setupclient.SetupClientActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by lazy {
        App.get(this).splashModule.provideSplashViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.state.observe(this, Observer {
            when (it) {
                SplashViewModel.State.RequiresClientIdAndSecret -> startActivity(SetupClientActivity.buildIntent(this))
                SplashViewModel.State.HasClientIdAndSecret -> startActivity(LoginActivity.buildIntent(this))
            }
        })
    }
}