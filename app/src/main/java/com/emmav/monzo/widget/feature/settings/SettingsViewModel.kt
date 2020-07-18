package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.Item
import com.emmav.monzo.widget.data.storage.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign

class SettingsViewModel(
    private val appWidgetId: Int,
    private val repository: Repository
) : BaseViewModel<SettingsViewModel.State>(initialState = State()) {

    private val accountsObservable = repository.accounts()
        .map {
            it.map { account ->
                Row.Account(
                    id = account.id,
                    type = account.type,
                    click = { onAccountClicked(account.id) })
            }
        }
        .map { listOf(Row.Header(id = "1", title = "Accounts")) + it }
        .replay(1)
        .refCount()

    private val potsObservable: Observable<List<Row>> = repository.pots()
        .map { it.map { pot -> Row.Pot(id = pot.id, name = pot.name, click = { onPotClicked(pot.id) }) } }
        .map { listOf(Row.Header(id = "2", title = "Pots")) + it }
        .replay(1)
        .refCount()

    init {
        disposables += Observable.combineLatest(
            accountsObservable,
            potsObservable, BiFunction<List<Row>, List<Row>, List<Row>> { accounts, pots ->
                accounts + pots
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(loading = false, rows = it) } }
    }

    private fun onAccountClicked(accountId: String) {
        disposables += repository.saveAccountWidget(accountId = accountId, id = appWidgetId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(complete = true) } }
    }

    private fun onPotClicked(potId: String) {
        disposables += repository.savePotWidget(potId = potId, id = appWidgetId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(complete = true) } }
    }

    data class State(val loading: Boolean = true, val rows: List<Row> = emptyList(), val complete: Boolean = false)
}

sealed class Row : Item {
    data class Header(override val id: String, val title: String) : Row()
    data class Account(override val id: String, val type: String, val click: ((Unit) -> Unit)) : Row()
    data class Pot(override val id: String, val name: String, val click: ((Unit) -> Unit)) : Row()
}
