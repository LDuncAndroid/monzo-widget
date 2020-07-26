package com.emmav.monzo.widget.feature.splash

import com.emmav.monzo.widget.data.auth.ClientRepository
import com.emmav.monzo.widget.data.auth.LoginRepository

class SplashModule(
    private val clientRepository: ClientRepository,
    private val loginRepository: LoginRepository
) {

    fun provideSplashViewModel(): SplashViewModel {
        return SplashViewModel(clientRepository, loginRepository)
    }
}