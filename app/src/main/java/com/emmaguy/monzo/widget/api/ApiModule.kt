package com.emmaguy.monzo.widget.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.emmaguy.monzo.widget.BuildConfig
import com.emmaguy.monzo.widget.storage.StorageModule
import com.emmaguy.monzo.widget.storage.UserStorage
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
        storageModule: StorageModule
) {
    val clientId = BuildConfig.CLIENT_ID
    val clientSecret = BuildConfig.CLIENT_SECRET

    private val userStorage = storageModule.userStorage
    private val baseHttpClient = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .build()

    val monzoApi by lazy {
        baseHttpClient.newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Content-type", "application/json")

                    if (userStorage.hasToken()) {
                        requestBuilder.header("Authorization", userStorage.getTokenType() + " " + userStorage.getAccessToken())
                    }

                    chain.proceed(requestBuilder.build())
                }
                .authenticator(MonzoAuthenticator(baseHttpClient, clientId, clientSecret, userStorage))
                .build()
                .createMonzoApi()
    }

    class MonzoAuthenticator(
            private val baseHttpClient: OkHttpClient,
            private val clientId: String,
            private val clientSecret: String,
            private val userStorage: UserStorage
    ) : Authenticator {

        override fun authenticate(route: Route?, response: Response): Request? {
            return synchronized(this) {
                // Make a new instance to avoid making another call with our expired access token
                val monzoApi = baseHttpClient.createMonzoApi()
                val call = monzoApi.refreshToken(clientId, clientSecret, userStorage.getRefreshToken()!!)
                try {
                    val tokenResponse = call.execute()
                    if (tokenResponse.code() == 200) {
                        val newToken = tokenResponse.body()!!
                        userStorage.saveToken(newToken)

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