package com.emmav.monzo.widget.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEY_CLIENT_ID = "KEY_CLIENT_ID"
private const val KEY_CLIENT_SECRET = "KEY_CLIENT_SECRET"

class ClientStorage @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences("client_storage", Context.MODE_PRIVATE)
    }

    var clientId: String?
        get() = sharedPreferences.getString(KEY_CLIENT_ID, null)
        set(state) {
            sharedPreferences.edit().putString(KEY_CLIENT_ID, state).apply()
        }

    var clientSecret: String?
        get() = sharedPreferences.getString(KEY_CLIENT_SECRET, null)
        set(state) {
            sharedPreferences.edit().putString(KEY_CLIENT_SECRET, state).apply()
        }
}
