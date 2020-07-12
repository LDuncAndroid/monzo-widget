package com.emmaguy.monzo.widget.storage

import com.emmaguy.monzo.widget.api.*
import io.reactivex.*
import io.reactivex.Observable
import java.util.*

class Repository(
        private val clientId: String,
        private val clientSecret: String,
        private val ioScheduler: Scheduler,
        private val monzoApi: MonzoApi,
        private val userStorage: AuthStorage,
        private val storage: Storage
) {
    fun authenticated(): Boolean {
        return userStorage.hasToken()
    }

    fun startLogin(): String {
        return UUID.randomUUID().toString().also { userStorage.state = it }
    }

    fun syncAccounts(): Single<List<ApiAccount>> {
        return monzoApi.accounts()
                .map { it.accounts }
                .doOnSuccess {
                    storage.saveAccounts(it.map { apiAccount -> apiAccount.toDbAccount() })
                }
                .subscribeOn(ioScheduler)
    }

    fun syncBalance(accountId: String): Completable {
        return monzoApi.balance(accountId = accountId)
                .doOnSuccess { storage.saveBalance(it.toDbBalance(accountId = accountId)) }
                .ignoreElement()
                .subscribeOn(ioScheduler)
    }

    fun syncPots(accountId: String): Completable {
        return monzoApi.pots(accountId = accountId)
                .doOnSuccess {
                    val pots = it.pots
                            .filter { apiPot -> !apiPot.deleted }
                            .map { apiPot -> apiPot.toDbPot(accountId = accountId) }
                    storage.savePots(pots)
                }
                .ignoreElement()
                .subscribeOn(ioScheduler)
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

    fun saveAccountWidget(accountId: String, id: Int): Single<Unit> {
        return Single.fromCallable {
            val dbWidget = DbWidget(id = id, type = WidgetType.ACCOUNT.key, accountId = accountId, potId = null)
            storage.saveWidget(dbWidget)
        }.subscribeOn(ioScheduler)
    }

    fun savePotWidget(potId: String, id: Int): Single<Unit> {
        return Single.fromCallable {
            val dbWidget = DbWidget(id = id, type = WidgetType.POT.key, accountId = null, potId = potId)
            storage.saveWidget(dbWidget)
        }.subscribeOn(ioScheduler)
    }

    fun accounts(): Observable<List<DbAccount>> {
        return storage.accounts()
                .subscribeOn(ioScheduler)
    }

    fun pots(): Observable<List<DbPot>> {
        return storage.pots()
                .subscribeOn(ioScheduler)
    }

    fun widgetById(id: Int): Single<List<Widget>> {
        return storage.accountsWithBalance()
                .flatMap { dbAccountsWithBalance -> storage.pots().map { Pair(dbAccountsWithBalance, it) } }
                .subscribeOn(ioScheduler)
                .firstOrError()
                .flatMap { (dbAccountsWithBalance, dbPots) ->
                    storage.widgetById(id = id)
                            .subscribeOn(ioScheduler)
                            .flatMap { dbWidgets ->
                                val singles = dbWidgets.mapNotNull { dbWidget ->
                                    when (WidgetType.find(dbWidget.type)) {
                                        WidgetType.ACCOUNT -> {
                                            val accountWithBalance = dbAccountsWithBalance
                                                    .firstOrNull { it.account.id == dbWidget.accountId }
                                            if (accountWithBalance == null) {
                                                null
                                            } else {
                                                Single.just(accountWithBalance.toWidget())
                                            }
                                        }
                                        WidgetType.POT -> {
                                            val pot = dbPots.firstOrNull { it.id == dbWidget.potId }
                                            if (pot == null) {
                                                null
                                            } else {
                                                Single.just(pot.toWidget())
                                            }
                                        }
                                        else -> null
                                    }
                                }
                                Single.merge(singles).toList()
                            }
                }
    }
}

private fun DbAccountWithBalance.toWidget(): Widget {
    val type = when (account.type) {
        "uk_prepaid" -> "prepaid"
        "uk_retail" -> "current"
        "uk_retail_joint" -> "joint"
        else -> ""
    }

    return Widget.AccountWidget(type = type, balance = balance.balance, currency = balance.currency)
}

private fun DbPot.toWidget(): Widget {
    return Widget.PotWidget(name = name, balance = balance, currency = currency)
}
