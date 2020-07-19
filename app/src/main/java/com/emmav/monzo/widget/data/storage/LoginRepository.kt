package com.emmav.monzo.widget.data.storage

import com.emmav.monzo.widget.data.api.MonzoApi
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class LoginRepository(
    private val clientId: String,
    private val clientSecret: String,
    private val monzoApi: MonzoApi,
    private val loginStorage: LoginStorage
) {
    val hasToken: Boolean
        get() = loginStorage.hasToken

    fun startLogin(): String {
        return UUID.randomUUID().toString().also { loginStorage.state = it }
    }

    fun login(redirectUri: String, code: String, state: String): Maybe<Unit> {
        return Single.fromCallable {
            if (state != loginStorage.state) {
                throw IllegalArgumentException("Cannot log in - state mismatch")
            }
        }
            .flatMapMaybe {
                monzoApi.requestAccessToken(clientId, clientSecret, redirectUri, code)
                    .doOnSuccess { token ->
                        loginStorage.saveToken(token)
                        loginStorage.state = null
                    }
                    .map { }
                    .subscribeOn(Schedulers.io())
                    .toMaybe()
                    .onErrorResumeNext(Maybe.empty())
            }
            .doOnError {
                // TODO: test state mismatch clears state
                loginStorage.state = null
            }
    }

    fun testAuthentication(): Single<Boolean> {
        return Single.fromCallable {
            val response = monzoApi.testSCA().execute()
            response.code() != 403 && response.isSuccessful
        }.subscribeOn(Schedulers.io())
    }
}
