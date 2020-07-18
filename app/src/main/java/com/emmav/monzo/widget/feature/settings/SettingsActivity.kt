package com.emmav.monzo.widget.feature.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.feature.appwidget.WidgetProvider
import com.emmav.monzo.widget.common.SimpleAdapter
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.item_row.view.*

class SettingsActivity : AppCompatActivity(),
    SettingsPresenter.View {

    private val rowClicks = PublishRelay.create<Row>()
    private val rowsAdapter =
        RowsAdapter()

    private val presenter by lazy {
        App.get(this).settingsModule.provideSettingsPresenter(widgetId)
    }
    private val widgetId by lazy {
        intent.extras!!.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        setResult(RESULT_CANCELED)

        presenter.attachView(this)

        settingsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = rowsAdapter.apply {
                clickListener = { rowClicks.accept(it) }
            }
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun rowClicks(): Observable<Row> {
        return rowClicks
    }

    override fun showWidgetOptions(rows: List<Row>) {
        rowsAdapter.submitList(rows)
    }

    override fun finish(appWidgetId: Int) {
        val intent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, intent)
        finish()
        WidgetProvider.updateWidget(this, appWidgetId, AppWidgetManager.getInstance(this))
    }

    class RowsAdapter : SimpleAdapter<Row>() {
        var clickListener: ((Row) -> Unit)? = null

        override fun getLayoutRes(item: Row): Int {
            return when (item) {
                is Row.Header -> R.layout.item_row
                is Row.Account -> R.layout.item_row
                is Row.Pot -> R.layout.item_row
            }
        }

        override fun onBind(holder: ViewHolder, item: Row) {
            when (item) {
                is Row.Header -> item.bind(holder)
                is Row.Account -> item.bind(holder)
                is Row.Pot -> item.bind(holder)
            }
        }

        private fun Row.Header.bind(holder: ViewHolder) {
            holder.containerView.textView.text = title
        }

        private fun Row.Account.bind(holder: ViewHolder) {
            holder.containerView.textView.text = type
            holder.containerView.setOnClickListener { clickListener?.invoke(this) }
        }

        private fun Row.Pot.bind(holder: ViewHolder) {
            holder.containerView.textView.text = name
            holder.containerView.setOnClickListener { clickListener?.invoke(this) }
        }
    }
}