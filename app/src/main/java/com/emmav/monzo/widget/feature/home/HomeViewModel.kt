package com.emmav.monzo.widget.feature.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.Item
import com.emmav.monzo.widget.common.NumberFormat.formatBalance
import com.emmav.monzo.widget.data.api.toLongAccountType
import com.emmav.monzo.widget.data.appwidget.Widget
import com.emmav.monzo.widget.data.appwidget.WidgetRepository
import com.emmav.monzo.widget.feature.sync.SyncWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    workManager: WorkManager,
    widgetRepository: WidgetRepository
) : BaseViewModel<HomeViewModel.State>(initialState = State()) {

    init {
        workManager.enqueue(OneTimeWorkRequest.Builder(SyncWorker::class.java).build())

        disposables += widgetRepository.allWidgets()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(loading = false, widgets = it.map { it.toRow() }) } }
    }

    data class State(val loading: Boolean = true, val widgets: List<WidgetRow> = emptyList())
}

private fun Widget.toRow(): WidgetRow {
    val type = when (this) {
        is Widget.Account -> "💳 ${type.toLongAccountType()}"
        is Widget.Pot -> "🍯 $name"
    }
    val amount = formatBalance(currency = currency, amount = balance, showFractionalDigits = true)

    return WidgetRow(id = id, title = type, amount = amount)
}

data class WidgetRow(override val id: String, val title: String, val amount: String) : Item