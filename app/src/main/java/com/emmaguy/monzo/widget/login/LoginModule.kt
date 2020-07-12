package com.emmaguy.monzo.widget.login

import android.content.Context
import com.emmaguy.monzo.widget.AppModule
import com.emmaguy.monzo.widget.R
import com.emmaguy.monzo.widget.storage.Repository

class LoginModule(
        private val context: Context,
        private val clientId: String,
        private val repository: Repository
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