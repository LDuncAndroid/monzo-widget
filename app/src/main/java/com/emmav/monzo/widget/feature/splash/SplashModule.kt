package com.emmav.monzo.widget.feature.splash

import com.emmav.monzo.widget.data.auth.ClientRepository

class SplashModule(
    private val clientRepository: ClientRepository
) {

    fun provideSplashViewModel(): SplashViewModel {
        return SplashViewModel(clientRepository)
    }
}