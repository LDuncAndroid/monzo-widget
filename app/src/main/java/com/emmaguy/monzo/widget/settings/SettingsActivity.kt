package com.emmaguy.monzo.widget.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.emmaguy.monzo.widget.MonzoWidgetApp
import com.emmaguy.monzo.widget.R
import com.emmaguy.monzo.widget.WidgetProvider
import com.emmaguy.monzo.widget.room.PotEntity
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), SettingsPresenter.View {
    private val presenter by lazy {
        MonzoWidgetApp.get(this).settingsModule.provideSettingsPresenter(widgetId)
    }
    private val widgetId by lazy {
        intent.extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
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

    override fun showPots(pots: List<PotEntity>) {
        Toast.makeText(this, TextUtils.join(", ", pots.map { it.name }), Toast.LENGTH_SHORT).show()
    }

    override fun finish(appWidgetId: Int) {
        setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        finish()
        WidgetProvider.updateWidget(this, appWidgetId)
    }
}