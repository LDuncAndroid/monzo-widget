package com.emmav.monzo.widget.data.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.emmav.monzo.widget.data.auth.ClientStorage
import com.emmav.monzo.widget.data.auth.LoginStorage
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.IOException

class ApiModule(
    context: Context,
    clientStorage: ClientStorage,
    loginStorage: LoginStorage
) {
    private val baseHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    val monzoApi by lazy {
        baseHttpClient.newBuilder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .also {
                        if (loginStorage.hasToken) {
                            it.header("Authorization", "${loginStorage.tokenType} ${loginStorage.accessToken}")
                        }
                    }

                chain.proceed(requestBuilder.build())
            }
            .authenticator(MonzoAuthenticator(baseHttpClient, clientStorage, loginStorage))
            .build()
            .createMonzoApi()
    }

    class MonzoAuthenticator(
        private val baseHttpClient: OkHttpClient,
        private val clientStorage: ClientStorage,
        private val loginStorage: LoginStorage
    ) : Authenticator {

        override fun authenticate(route: Route?, response: Response): Request? {
            return synchronized(this) {
                // Make a new instance to avoid making another call with our expired access token
                val monzoApi = baseHttpClient.createMonzoApi()
                val call = monzoApi.refreshToken(
                    clientId = clientStorage.clientId!!,
                    clientSecret = clientStorage.clientSecret!!,
                    refreshToken = loginStorage.refreshToken!!
                )
                try {
                    val tokenResponse = call.execute()
                    if (tokenResponse.code() == 200) {
                        val newToken = tokenResponse.body()!!
                        loginStorage.saveToken(newToken)

                        response.request.newBuilder()
                            .header("Authorization", "${newToken.tokenType} ${newToken.accessToken}")
                            .build()
                    }
                } catch (e: IOException) {
                    Timber.e(e, "Exception whilst trying to refresh token")
                }
                null
            }
        }
    }

    companion object {

        private fun OkHttpClient.createMonzoApi(): MonzoApi {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            return Retrofit.Builder()
                .baseUrl("https://api.monzo.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(this)
                .build()
                .create(MonzoApi::class.java)
        }
    }
}