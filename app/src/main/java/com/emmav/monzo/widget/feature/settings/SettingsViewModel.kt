package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.Item
import com.emmav.monzo.widget.data.api.toLongAccountType
import com.emmav.monzo.widget.data.storage.MonzoRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign

class SettingsViewModel(
    private val appWidgetId: Int,
    private val widgetTypeId: String?,
    private val monzoRepository: MonzoRepository
) : BaseViewModel<SettingsViewModel.State>(initialState = State()) {

    private val accountsObservable = monzoRepository.accountsWithBalance()
        .map {
            it.map { dbAccountWithBalance ->
                val id = dbAccountWithBalance.account.id
                Row.Account(
                    id = id,
                    type = dbAccountWithBalance.account.type.toLongAccountType(),
                    isSelected = id == widgetTypeId,
                    click = { onAccountClicked(id) })
            }
        }
        .map { listOf(Row.Header(id = "1", title = "Accounts")) + it }
        .replay(1)
        .refCount()

    private val potsObservable: Observable<List<Row>> = monzoRepository.pots()
        .map {
            it.map { pot ->
                Row.Pot(
                    id = pot.id,
                    name = pot.name,
                    isSelected = pot.id == widgetTypeId,
                    click = { onPotClicked(pot.id) })
            }
        }
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
        disposables += monzoRepository.saveAccountWidget(accountId = accountId, id = appWidgetId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(complete = true) } }
    }

    private fun onPotClicked(potId: String) {
        disposables += monzoRepository.savePotWidget(potId = potId, id = appWidgetId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(complete = true) } }
    }

    data class State(val loading: Boolean = true, val rows: List<Row> = emptyList(), val complete: Boolean = false)
}

sealed class Row : Item {
    data class Header(override val id: String, val title: String) : Row()
    data class Account(
        override val id: String,
        val type: String,
        val isSelected: Boolean,
        val click: ((Unit) -> Unit)
    ) : Row()

    data class Pot(
        override val id: String,
        val name: String,
        val isSelected: Boolean,
        val click: ((Unit) -> Unit)
    ) : Row()
}
