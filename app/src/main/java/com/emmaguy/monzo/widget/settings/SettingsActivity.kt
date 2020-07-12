package com.emmaguy.monzo.widget.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmaguy.monzo.widget.MonzoWidgetApp
import com.emmaguy.monzo.widget.R
import com.emmaguy.monzo.widget.WidgetProvider
import com.emmaguy.monzo.widget.room.DbPot
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), SettingsPresenter.View {
    private val adapter = PotEntityAdapter()

    private val presenter by lazy {
        MonzoWidgetApp.get(this).settingsModule.provideSettingsPresenter(widgetId)
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
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun currentAccountClicks(): Observable<Unit> {
        return currentAccountButton.clicks()
    }

    override fun showPots(pots: List<DbPot>) {
        adapter.submitList(pots)
    }

    override fun finish(appWidgetId: Int) {
        val intent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, intent)
        finish()
        WidgetProvider.updateWidget(this, appWidgetId)
    }

    class PotEntityAdapter : ListAdapter<DbPot, PotViewHolder>(DbPot.DIFF_CALLBACK) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotViewHolder {
            return PotViewHolder(TextView(parent.context))
        }

        override fun onBindViewHolder(holder: PotViewHolder, position: Int) {
            holder.bindTo(getItem(position))
        }
    }

    class PotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTo(item: DbPot) {
            (itemView as TextView).text = item.name
        }
    }
}
