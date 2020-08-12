package com.emmav.monzo.widget.data.auth

import javax.inject.Inject

class ClientRepository @Inject constructor(private val clientStorage: ClientStorage) {

    val clientConfigured: Boolean
        get() = clientId != null && clientSecret != null

    var clientId: String?
        get() = clientStorage.clientId
        set(value) {
            clientStorage.clientId = value
        }

    var clientSecret: String?
        get() = clientStorage.clientSecret
        set(value) {
            clientStorage.clientSecret = value
        }
}
