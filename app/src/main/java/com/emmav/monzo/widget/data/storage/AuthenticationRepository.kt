package com.emmav.monzo.widget.data.storage

import com.emmav.monzo.widget.data.api.MonzoApi
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.*

class AuthenticationRepository(
    private val clientId: String,
    private val clientSecret: String,
    private val ioScheduler: Scheduler,
    private val monzoApi: MonzoApi,
    private val userStorage: AuthStorage
) {
    fun authenticated(): Boolean = userStorage.hasToken

    fun startLogin(): String {
        return UUID.randomUUID().toString().also { userStorage.state = it }
    }

    fun login(redirectUri: String, code: String, state: String): Maybe<Unit> {
        return Single.fromCallable {
            if (state != userStorage.state) {
                throw IllegalArgumentException("Cannot log in - state mismatch")
            }
        }
            .flatMapMaybe {
                monzoApi.requestAccessToken(clientId, clientSecret, redirectUri, code)
                    .doOnSuccess { token ->
                        userStorage.saveToken(token)
                        userStorage.state = null
                    }
                    .map { }
                    .subscribeOn(ioScheduler)
                    .toMaybe()
                    .onErrorResumeNext(Maybe.empty())
            }
            .doOnError {
                // TODO: test state mismatch clears state
                userStorage.state = null
            }
    }
}