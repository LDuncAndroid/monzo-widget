package com.emmav.monzo.widget.feature.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.emmav.monzo.widget.common.BaseViewModel
import com.emmav.monzo.widget.common.NumberFormat.formatBalance
import com.emmav.monzo.widget.data.appwidget.Widget
import com.emmav.monzo.widget.data.appwidget.WidgetRepository
import com.emmav.monzo.widget.feature.sync.SyncWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class HomeViewModel @ViewModelInject constructor(
    workManager: WorkManager,
    widgetRepository: WidgetRepository
) : BaseViewModel<HomeViewModel.State>(initialState = State()) {

    init {
        workManager.enqueue(OneTimeWorkRequest.Builder(SyncWorker::class.java).build())

        disposables += widgetRepository.allWidgets()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState { copy(loading = false, widgets = it.map { it.toRow() }, clickedWidget = null) } }
    }

    private fun onWidgetClicked(appWidgetId: Int, widgetTypeId: String) {
        setState { copy(clickedWidget = Pair(appWidgetId, widgetTypeId)) }
    }

    data class State(
        val loading: Boolean = true,
        val widgets: List<WidgetRow> = emptyList(),
        val clickedWidget: Pair<Int, String>? = null
    )

    private fun Widget.toRow(): WidgetRow {
        val amount = formatBalance(currency = currency, amount = balance, showFractionalDigits = true)

        return WidgetRow(title = toString(), amount = amount, click = { onWidgetClicked(appWidgetId, widgetTypeId) })
    }
}

data class WidgetRow(
    val title: String,
    val amount: String,
    val click: ((Unit) -> Unit)
)