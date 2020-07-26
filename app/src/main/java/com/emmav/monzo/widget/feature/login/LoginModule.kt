package com.emmav.monzo.widget.feature.login

import android.content.Context
import androidx.work.WorkManager
import com.emmav.monzo.widget.data.auth.LoginRepository

class LoginModule(
    private val context: Context,
    private val redirectUri: String,
    private val loginRepository: LoginRepository
) {

    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(
            loginRepository,
            redirectUri,
            WorkManager.getInstance(context)
        )
    }
}