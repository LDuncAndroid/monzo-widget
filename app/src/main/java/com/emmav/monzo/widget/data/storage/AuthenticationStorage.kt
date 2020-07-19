package com.emmav.monzo.widget.data.storage

import android.content.Context
import com.emmav.monzo.widget.data.api.ApiToken

private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
private const val KEY_TOKEN_TYPE = "KEY_TOKEN_TYPE"

private const val KEY_STATE = "KEY_STATE"

class AuthStorage(context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    }

    var state: String?
        get() = sharedPreferences.getString(KEY_STATE, null)
        set(state) {
            sharedPreferences.edit().putString(KEY_STATE, state).apply()
        }

    fun saveToken(token: ApiToken) {
        sharedPreferences
            .edit()
            .putString(KEY_REFRESH_TOKEN, token.refreshToken)
            .putString(KEY_ACCESS_TOKEN, token.accessToken)
            .putString(KEY_TOKEN_TYPE, token.tokenType)
            .apply()
    }

    val hasToken: Boolean
        get() = accessToken != null && refreshToken != null

    val tokenType: String?
        get() = sharedPreferences.getString(KEY_TOKEN_TYPE, null)

    val accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    val refreshToken: String?
        get() = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
}