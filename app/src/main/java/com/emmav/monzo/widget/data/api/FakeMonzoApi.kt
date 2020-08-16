package com.emmav.monzo.widget.data.api

import io.reactivex.Single
import retrofit2.Call

class FakeMonzoApi : MonzoApi {
    override fun requestAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ): Single<ApiToken> {
        return Single.just(
            ApiToken(
                accessToken = "access_token",
                refreshToken = "refresh_token",
                tokenType = "token_type"
            )
        )
    }

    override fun refreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
        grantType: String
    ): Call<ApiToken> {
        TODO("Not yet implemented")
    }

    override fun testSCA(): Call<Unit> {
        TODO("Not yet implemented")
    }

    override fun accounts(): Single<AccountsResponse> {
        return Single.just(
            AccountsResponse(
                accounts = listOf(
                    ApiAccount(id = "id1", type = "uk_retail"),
                    ApiAccount(id = "id2", type = "uk_retail_joint")
                )
            )
        )
    }

    override fun balance(accountId: String): Single<ApiBalance> {
        return Single.just(ApiBalance(balance = 100_10, currency = "GBP"))
    }

    override fun pots(accountId: String): Single<PotsResponse> {
        return Single.just(
            PotsResponse(
                pots = listOf(
                    ApiPot(
                        id = "pot1",
                        name = "Savings",
                        balance = 34_10,
                        currency = "GBP",
                        deleted = false
                    ),
                    ApiPot(
                        id = "pot2",
                        name = "Rainy Day",
                        balance = 112_78,
                        currency = "GBP",
                        deleted = false
                    )
                )
            )
        )
    }
}