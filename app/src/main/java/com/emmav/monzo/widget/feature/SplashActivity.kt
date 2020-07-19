package com.emmav.monzo.widget.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmav.monzo.widget.feature.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Check if we have an auth token/we've SCA'd at least once - if so, go to home. Else, go to login
        startActivity(HomeActivity.buildIntent(this))
    }
}