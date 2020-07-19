package com.emmav.monzo.widget.feature.home

import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.Item
import com.emmav.monzo.widget.data.api.toLongAccountType
import com.emmav.monzo.widget.data.storage.Widget
import com.emmav.monzo.widget.data.storage.WidgetRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class HomeViewModel(widgetRepository: WidgetRepository) : BaseViewModel<HomeViewModel.State>(initialState = State()) {

    init {
        disposables += widgetRepository.allWidgets()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(loading = false, widgets = it.map { it.toRow() }) } }
    }

    data class State(val loading: Boolean = true, val widgets: List<WidgetRow> = emptyList())
}

private fun Widget.toRow(): WidgetRow {
    val type = when (this) {
        is Widget.Balance.Account -> type.toLongAccountType()
        is Widget.Balance.Pot -> name
    }

    return WidgetRow.Widget(id = id, title = type)
}

sealed class WidgetRow : Item {
    data class Widget(override val id: String, val title: String) : WidgetRow()
}