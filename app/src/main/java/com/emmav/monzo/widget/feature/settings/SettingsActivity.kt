package com.emmav.monzo.widget.feature.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.R
import com.emmav.monzo.widget.common.SimpleAdapter
import com.emmav.monzo.widget.common.gone
import com.emmav.monzo.widget.common.visible
import com.emmav.monzo.widget.feature.appwidget.EXTRA_WIDGET_TYPE_ID
import com.emmav.monzo.widget.feature.appwidget.WidgetProvider
import com.emmav.monzo.widget.feature.splash.SplashActivity

class SettingsActivity : AppCompatActivity() {
    private val appWidgetId by lazy {
        intent.extras!!.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }
    private val widgetTypeId by lazy { intent.extras?.getString(EXTRA_WIDGET_TYPE_ID, null) }
    private val viewModel by lazy {
        App.get(this).settingsModule.provideSettingsViewModel(appWidgetId, widgetTypeId)
    }
    private val settingsRecyclerView by lazy { findViewById<RecyclerView>(R.id.settingsRecyclerView) }
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

            if (state.error) {
                startActivity(SplashActivity.buildIntent(this))
                finish()
            }

            if (state.complete) {
                finishWidgetSetup()
            }
        })
    }

    private fun finishWidgetSetup() {
        setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
        finish()
        WidgetProvider.updateWidget(this, appWidgetId, AppWidgetManager.getInstance(this))
    }

    @SuppressLint("SetTextI18n")
    class RowsAdapter : SimpleAdapter<Row>() {
        override fun getLayoutRes(item: Row): Int {
            return when (item) {
                is Row.Header -> R.layout.item_widget_settings_header
                is Row.Account -> R.layout.item_widget_settings_row
                is Row.Pot -> R.layout.item_widget_settings_row
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
            holder.containerView.findViewById<TextView>(R.id.widgetSettingsHeaderRowTextView).text = title
        }

        private fun Row.Account.bind(holder: ViewHolder) {
            holder.containerView.findViewById<TextView>(R.id.widgetSettingsRowTextView).text = "💳 $type"
            holder.containerView.setOnClickListener { click.invoke(Unit) }
            showOrHideSelected(isSelected, holder)
        }

        private fun Row.Pot.bind(holder: ViewHolder) {
            holder.containerView.findViewById<TextView>(R.id.widgetSettingsRowTextView).text = "🍯 $name"
            holder.containerView.setOnClickListener { click.invoke(Unit) }
            showOrHideSelected(isSelected, holder)
        }

        private fun showOrHideSelected(isSelected: Boolean, holder: ViewHolder) {
            if (isSelected) {
                holder.containerView.findViewById<TextView>(R.id.widgetSettingsRowTextView).visible()
            } else {
                holder.containerView.findViewById<TextView>(R.id.widgetSettingsRowTextView).gone()
            }
        }
    }
}