package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.common.BasePresenter
import com.emmav.monzo.widget.common.Item
import com.emmav.monzo.widget.common.plus
import com.emmav.monzo.widget.data.storage.Repository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction

class SettingsPresenter(
        private val uiScheduler: Scheduler,
        private val appWidgetId: Int,
        private val repository: Repository
) : BasePresenter<SettingsPresenter.View>() {

    private val accountsObservable = repository.accounts()
            .map { it.map { account ->
                Row.Account(
                    id = account.id,
                    type = account.type
                )
            } }
            .map { listOf(
                Row.Header(
                    id = "1",
                    title = "Accounts"
                )
            ) + it }
            .replay(1)
            .refCount()

    private val potsObservable: Observable<List<Row>> = repository.pots()
            .map { it.map { pot ->
                Row.Pot(
                    id = pot.id,
                    name = pot.name
                )
            } }
            .map { listOf(
                Row.Header(
                    id = "2",
                    title = "Pots"
                )
            ) + it }
            .replay(1)
            .refCount()

    override fun attachView(view: View) {
        super.attachView(view)

        disposables += Observable.combineLatest(
                accountsObservable,
                potsObservable, BiFunction<List<Row>, List<Row>, List<Row>> { accounts, pots ->
            accounts + pots
        })
                .observeOn(uiScheduler)
                .subscribe { view.showWidgetOptions(it) }

        disposables += view.rowClicks()
                .flatMapSingle {
                    when (it) {
                        is Row.Account -> repository.saveAccountWidget(
                                accountId = it.id,
                                id = appWidgetId
                        )
                        is Row.Pot -> repository.savePotWidget(
                                potId = it.id,
                                id = appWidgetId
                        )
                        else -> throw IllegalArgumentException("Can't make a widget from the header")
                    }
                }
                .observeOn(uiScheduler)
                .subscribe { view.finish(appWidgetId) }
    }

    interface View : BasePresenter.View {
        fun rowClicks(): Observable<Row>
        fun showWidgetOptions(rows: List<Row>)

        fun finish(appWidgetId: Int)
    }
}

sealed class Row : Item {
    data class Header(override val id: String, val title: String) : Row()
    data class Account(override val id: String, val type: String) : Row()
    data class Pot(override val id: String, val name: String) : Row()
}