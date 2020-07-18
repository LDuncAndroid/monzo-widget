package com.emmav.monzo.widget.data.api

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface MonzoApi {

    @FormUrlEncoded
    @POST("oauth2/token")
    fun requestAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Single<ApiToken>

    @FormUrlEncoded
    @POST("oauth2/token")
    fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Call<ApiToken>

    /**
     * In order to test the user has done strong customer authentication (aka 2FA, and accepted an
     * in-app push notification sent to the official Monzo app). If they have not, we'll get a 403.
     *
     * SCA is used to protect sensitive information. We call /accounts as it's an endpoint that will
     * always require SCA. If we call something like /ping/whoami we'll get a 200 regardless of whether SCA
     * has been done or not, because that API doesn't need it.
     */
    @GET("accounts")
    fun testSCA(): Call<Unit>

    @GET("accounts")
    fun accounts(): Single<AccountsResponse>

    @GET("balance")
    fun balance(@Query("account_id") accountId: String): Single<ApiBalance>

    @GET("pots")
    fun pots(@Query("current_account_id") accountId: String): Single<PotsResponse>
}
