package com.emmaguy.monzo.widget.storage

import android.content.Context
import com.emmaguy.monzo.widget.api.ApiToken

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

    fun hasToken(): Boolean {
        return getAccessToken() != null && getRefreshToken() != null
    }

    fun getTokenType(): String? {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, null)
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
}