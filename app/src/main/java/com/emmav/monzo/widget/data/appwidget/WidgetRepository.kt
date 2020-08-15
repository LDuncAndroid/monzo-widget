package com.emmav.monzo.widget.data.appwidget

import com.emmav.monzo.widget.data.db.DbWidgetWithRelations
import com.emmav.monzo.widget.data.db.MonzoStorage
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WidgetRepository @Inject constructor(private val monzoStorage: MonzoStorage) {

    fun widgetById(id: Int): Maybe<Widget> {
        return monzoStorage.widgetById(id = id)
            .filter { it.isNotEmpty() }
            .map { dbWidgets -> dbWidgets.map { it.toWidget() }.first() }
            .subscribeOn(Schedulers.io())
    }

    fun allWidgets(): Observable<List<Widget>> {
        return monzoStorage.widgets()
            .map { dbWidgets -> dbWidgets.map { it.toWidget() } }
            .subscribeOn(Schedulers.io())
    }

    fun deleteRemovedWidgets(widgetIds: List<Int>): Completable {
        return Completable.fromCallable {
            monzoStorage.deleteAllWidgetsExcept(widgetIds = widgetIds)
        }.subscribeOn(Schedulers.io())
    }
}

private fun DbWidgetWithRelations.toWidget(): Widget {
    return if (pot != null) {
        Widget.Pot(
            appWidgetId = widget.id,
            widgetTypeId = pot.id,
            name = pot.name,
            balance = pot.balance,
            currency = pot.currency
        )
    } else if (account != null && balance != null) {
        Widget.Account(
            appWidgetId = widget.id,
            widgetTypeId = account.id,
            name = account.type,
            balance = balance.balance,
            currency = balance.currency
        )
    } else {
        throw IllegalArgumentException("Invalid type of widget")
    }
}
