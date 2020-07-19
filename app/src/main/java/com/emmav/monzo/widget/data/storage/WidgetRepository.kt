package com.emmav.monzo.widget.data.storage

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class WidgetRepository(private val storage: Storage) {

    fun widgetById(id: Int): Maybe<Widget> {
        return storage.widgetById(id = id)
            .filter { it.isNotEmpty() }
            .map { dbWidgets -> dbWidgets.map { it.toWidget() }.first() }
            .subscribeOn(Schedulers.io())
    }

    fun allWidgets(): Observable<List<Widget>> {
        return storage.widgets()
            .map { dbWidgets -> dbWidgets.map { it.toWidget() } }
            .subscribeOn(Schedulers.io())
    }

    fun deleteRemovedWidgets(widgetIds: List<Int>): Completable {
        return Completable.fromCallable {
            storage.deleteAllWidgetsExcept(widgetIds = widgetIds)
        }.subscribeOn(Schedulers.io())
    }
}

private fun DbWidgetWithRelations.toWidget(): Widget {
    val id = widget.id.toString()
    return if (pot != null) {
        Widget.Balance.Pot(
            id = id,
            potId = pot.id,
            name = pot.name,
            balance = pot.balance,
            currency = pot.currency
        )
    } else if (account != null && balance != null) {
        Widget.Balance.Account(
            id = id,
            type = account.type,
            accountId = account.id,
            balance = balance.balance,
            currency = balance.currency
        )
    } else {
        throw IllegalArgumentException("Invalid type of widget")
    }
}
