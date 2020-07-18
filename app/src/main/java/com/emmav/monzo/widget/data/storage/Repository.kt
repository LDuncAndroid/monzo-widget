package com.emmav.monzo.widget.data.storage

import com.emmav.monzo.widget.data.api.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class Repository(
    private val monzoApi: MonzoApi,
    private val storage: Storage
) {
    fun syncAccounts(): Single<List<ApiAccount>> {
        return monzoApi.accounts()
            .map { it.accounts }
            .doOnSuccess {
                storage.saveAccounts(it.map { apiAccount -> apiAccount.toDbAccount() })
            }
            .subscribeOn(Schedulers.io())
    }

    fun syncBalance(accountId: String): Completable {
        return monzoApi.balance(accountId = accountId)
            .doOnSuccess { storage.saveBalance(it.toDbBalance(accountId = accountId)) }
            .ignoreElement()
            .subscribeOn(Schedulers.io())
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
            .subscribeOn(Schedulers.io())
    }

    fun saveAccountWidget(accountId: String, id: Int): Single<Unit> {
        return Single.fromCallable {
            val dbWidget = DbWidget(id = id, type = WidgetType.ACCOUNT.key, accountId = accountId, potId = null)
            storage.saveWidget(dbWidget)
        }.subscribeOn(Schedulers.io())
    }

    fun savePotWidget(potId: String, id: Int): Single<Unit> {
        return Single.fromCallable {
            val dbWidget = DbWidget(id = id, type = WidgetType.POT.key, accountId = null, potId = potId)
            storage.saveWidget(dbWidget)
        }.subscribeOn(Schedulers.io())
    }

    fun accounts(): Observable<List<DbAccount>> {
        return storage.accounts()
            .subscribeOn(Schedulers.io())
    }

    fun pots(): Observable<List<DbPot>> {
        return storage.pots()
            .subscribeOn(Schedulers.io())
    }

    fun widgetById(id: Int): Single<List<Widget>> {
        return storage.accountsWithBalance()
            .flatMap { dbAccountsWithBalance -> storage.pots().map { Pair(dbAccountsWithBalance, it) } }
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .flatMap { (dbAccountsWithBalance, dbPots) ->
                storage.widgetById(id = id)
                    .subscribeOn(Schedulers.io())
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
        "uk_prepaid" -> "Prepaid"
        "uk_retail" -> "Current"
        "uk_retail_joint" -> "Joint"
        else -> ""
    }

    return Widget.AccountWidget(type = type, balance = balance.balance, currency = balance.currency)
}

private fun DbPot.toWidget(): Widget {
    return Widget.PotWidget(name = name, balance = balance, currency = currency)
}