package com.emmav.monzo.widget.data.storage

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class WidgetRepository(private val storage: Storage) {

    fun widgetById(id: Int): Single<List<Widget>> {
        return storage.accountsWithBalance()
            .flatMap { dbAccountsWithBalance -> storage.pots().map { Pair(dbAccountsWithBalance, it) } }
            .firstOrError()
            .subscribeOn(Schedulers.io())
            .flatMap { (dbAccountsWithBalance, dbPots) ->
                storage.widgetById(id = id)
                    .subscribeOn(Schedulers.io())
                    .flatMap { dbWidgets -> toWidgets(dbWidgets, dbAccountsWithBalance, dbPots) }
            }
    }

    private fun toWidgets(
        dbWidgets: List<DbWidget>,
        dbAccountsWithBalance: List<DbAccountWithBalance>,
        dbPots: List<DbPot>
    ): Single<List<Widget>> {
        val singles = dbWidgets.mapNotNull { dbWidget ->
            when (WidgetType.find(dbWidget.type)) {
                WidgetType.ACCOUNT_BALANCE -> {
                    val accountWithBalance = dbAccountsWithBalance
                        .firstOrNull { it.account.id == dbWidget.accountId }
                    if (accountWithBalance == null) {
                        null
                    } else {
                        Single.just(accountWithBalance.toWidget(widgetId = dbWidget.id.toString()))
                    }
                }
                WidgetType.POT_BALANCE -> {
                    val pot = dbPots.firstOrNull { it.id == dbWidget.potId }
                    if (pot == null) {
                        null
                    } else {
                        Single.just(pot.toWidget(widgetId = dbWidget.id.toString()))
                    }
                }
                else -> null
            }
        }
        return Single.merge(singles).toList()
    }

    fun allWidgets(): Observable<List<Widget>> {
        return storage.accountsWithBalance()
            .flatMap { dbAccountsWithBalance -> storage.pots().map { Pair(dbAccountsWithBalance, it) } }
            .flatMapSingle { (dbAccountsWithBalance, dbPots) ->
                storage.widgets()
                    .subscribeOn(Schedulers.io())
                    .flatMap { dbWidgets -> toWidgets(dbWidgets, dbAccountsWithBalance, dbPots) }
            }
            .subscribeOn(Schedulers.io())
    }
}

private fun DbAccountWithBalance.toWidget(widgetId: String): Widget {
    val type = when (account.type) {
        "uk_prepaid" -> "Prepaid"
        "uk_retail" -> "Current"
        "uk_retail_joint" -> "Joint"
        else -> "Other"
    }

    return Widget.AccountBalance(
        id = widgetId,
        accountType = type,
        balance = balance.balance,
        currency = balance.currency
    )
}

private fun DbPot.toWidget(widgetId: String): Widget {
    return Widget.PotBalance(id = widgetId, name = name, balance = balance, currency = currency)
}
