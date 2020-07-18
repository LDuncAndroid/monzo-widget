package com.emmav.monzo.widget.data.api

import com.squareup.moshi.Json

data class AccountsResponse(val accounts: List<ApiAccount>)

data class ApiAccount(
    val id: String,
    val type: String
)

data class ApiBalance(
    val balance: Long,
    val currency: String
)

data class PotsResponse(val pots: List<ApiPot> = emptyList())

data class ApiPot(
    val id: String,
    val name: String,
    val balance: Long,
    val currency: String,
    val deleted: Boolean
)

data class ApiToken(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "token_type") val tokenType: String
)
