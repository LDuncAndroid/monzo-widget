package com.emmaguy.monzo.widget.api

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

    @GET("accounts")
    fun accounts(): Single<AccountsResponse>

    @GET("balance")
    fun balance(@Query("account_id") accountId: String): Single<ApiBalance>

    @GET("pots")
    fun pots(@Query("current_account_id") accountId: String): Single<PotsResponse>
}
