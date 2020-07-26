package com.emmav.monzo.widget.feature.setupclient

import com.emmav.monzo.widget.data.auth.ClientRepository

class SetupClientModule(
    private val clientRepository: ClientRepository,
    private val redirectUri: String
) {

    fun provideSetupClientViewModel(): SetupClientViewModel {
        return SetupClientViewModel(clientRepository, redirectUri)
    }
}