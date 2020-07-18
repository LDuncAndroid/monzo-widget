package com.emmav.monzo.widget.feature.login

import android.content.Context
import com.emmav.monzo.widget.AppModule
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.data.storage.AuthenticationRepository

class LoginModule(
    private val context: Context,
    private val clientId: String,
    private val repository: AuthenticationRepository
) {

    fun provideLoginPresenter(): LoginPresenter {
        return LoginPresenter(
            AppModule.uiScheduler(),
            clientId,
            provideRedirectUri(),
            repository
        )
    }

    private fun provideRedirectUri(): String {
        return context.getString(R.string.callback_url_scheme) + "://" + context.getString(R.string.callback_url_host)
    }
}