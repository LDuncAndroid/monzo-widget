package com.emmav.monzo.widget.feature.login

import android.content.Context
import androidx.work.WorkManager
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.data.storage.LoginRepository

class LoginModule(
    private val context: Context,
    private val clientId: String,
    private val loginRepository: LoginRepository
) {

    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(
            clientId,
            provideRedirectUri(),
            loginRepository,
            WorkManager.getInstance(context)
        )
    }

    private fun provideRedirectUri(): String {
        return context.getString(R.string.callback_url_scheme) + "://" + context.getString(R.string.callback_url_host)
    }
}