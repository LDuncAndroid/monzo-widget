package com.emmav.monzo.widget.feature.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.SimpleAdapter
import com.emmav.monzo.widget.feature.appwidget.WidgetProvider
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.item_row.view.*

class SettingsActivity : AppCompatActivity() {
    private val widgetId by lazy {
        intent.extras!!.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }
    private val viewModel by lazy { App.get(this).settingsModule.provideSettingsViewModel(widgetId) }
    private val rowsAdapter by lazy { RowsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        setResult(RESULT_CANCELED)

        settingsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = rowsAdapter
        }

        viewModel.state.observe(this, Observer { state ->
            rowsAdapter.submitList(state.rows)

            if (state.complete) {
                finishWidgetSetup()
            }
        })
    }

    private fun finishWidgetSetup() {
        val intent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, intent)
        finish()
        WidgetProvider.updateWidget(this, widgetId, AppWidgetManager.getInstance(this))
    }

    class RowsAdapter : SimpleAdapter<Row>() {
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
            holder.containerView.setOnClickListener { click.invoke(Unit) }
        }

        private fun Row.Pot.bind(holder: ViewHolder) {
            holder.containerView.textView.text = name
            holder.containerView.setOnClickListener { click.invoke(Unit) }
        }
    }
}