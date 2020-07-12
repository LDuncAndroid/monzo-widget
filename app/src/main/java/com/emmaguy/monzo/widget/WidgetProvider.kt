package com.emmaguy.monzo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.emmaguy.monzo.widget.common.TypefaceSpan
import com.emmaguy.monzo.widget.common.toPx
import com.emmaguy.monzo.widget.settings.SettingsActivity
import com.emmaguy.monzo.widget.storage.Widget
import timber.log.Timber
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // TODO: update db to remove and keep in sync
        updateAllWidgets(context, appWidgetManager)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // TODO: update db to remove and keep in sync
        for (widgetId in appWidgetIds) {
//            userStorage.removeAccountType(widgetId)
        }
    }

    companion object {
        private const val ROBOTO_LIGHT = "sans-serif-light"
        private const val ROBOTO_REGULAR = "sans-serif"
        private val numberFormat = NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }

        fun updateAllWidgets(context: Context, appWidgetManager: AppWidgetManager) {
            val thisWidget = ComponentName(context, WidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            for (i in allWidgetIds) {
                updateWidget(context, i, appWidgetManager)
            }
        }

        fun updateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
            val repository = App.get(context = context).repository

            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val widgets = repository.widgetById(id = appWidgetId).blockingGet()
            widgets.forEach {
                Timber.d("Widget, id: $appWidgetId, obj: $it")
                // TODO: Theming
                val backgroundResId = R.drawable.background_light
                val textColour = ContextCompat.getColor(context, R.color.monzo_dark)

                when (it) {
                    is Widget.AccountWidget -> {
                        val currency = Currency.getInstance(it.currency)
                        numberFormat.currency = Currency.getInstance(it.currency)
                        val balance = BigDecimal(it.balance).scaleByPowerOfTen(-2).toBigInteger()
                        val spannableString = createSpannableForBalance(context, currency.symbol,
                                numberFormat.format(balance), textColour)

                        updateWidget(context, pendingIntent, spannableString, it.type, backgroundResId, appWidgetManager, appWidgetId)
                    }
                    is Widget.PotWidget -> {
                        val currency = Currency.getInstance(it.currency)
                        numberFormat.currency = Currency.getInstance(it.currency)
                        val balance = BigDecimal(it.balance).scaleByPowerOfTen(-2).toBigInteger()
                        val spannableString = createSpannableForBalance(context, currency.symbol, numberFormat.format(balance), textColour)

                        updateWidget(context, pendingIntent, spannableString, it.name, backgroundResId, appWidgetManager, appWidgetId)
                    }
                }
            }
        }

        private fun updateWidget(
                context: Context,
                pendingIntent: PendingIntent,
                amount: SpannableString,
                subtitle: String,
                backgroundResId: Int,
                appWidgetManager: AppWidgetManager,
                appWidgetId: Int
        ) {
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_balance)
            remoteViews.setOnClickPendingIntent(R.id.widgetViewGroup, pendingIntent)
            remoteViews.setTextViewText(R.id.widgetAmountTextView, amount)
            remoteViews.setTextViewText(R.id.widgetSubtitleTextView, subtitle)
            remoteViews.setInt(R.id.widgetBackgroundView, "setBackgroundResource", backgroundResId)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        private fun createSpannableForBalance(
                context: Context,
                currency: String,
                balance: String,
                textColour: Int
        ): SpannableString {
            // Some supremely crude scaling as balance gets larger
            val currencySize = when {
                balance.length < 2 -> 23f
                balance.length == 2 -> 18f
                balance.length == 3 -> 14f
                balance.length == 4 -> 12f
                else -> 9f
            }
            val integerPartSize = when {
                balance.length < 2 -> 35f
                balance.length == 2 -> 27f
                balance.length == 3 -> 23f
                balance.length == 4 -> 18f
                else -> 14f
            }

            val span = SpannableString(balance)
            applyToCurrency(span, currency, AbsoluteSizeSpan(currencySize.toPx(context)))
            applyToCurrency(span, currency, ForegroundColorSpan(textColour))
            applyToCurrency(span, currency, TypefaceSpan(Typeface.create(ROBOTO_LIGHT, Typeface.NORMAL)))

            applyToIntegerPart(span, currency, balance, AbsoluteSizeSpan(integerPartSize.toPx(context)))
            applyToIntegerPart(span, currency, balance, ForegroundColorSpan(textColour))
            applyToIntegerPart(span, currency, balance, TypefaceSpan(Typeface.create(ROBOTO_REGULAR, Typeface.NORMAL)))

            return span
        }

        private fun applyToCurrency(spannable: SpannableString, currency: String, span: Any) {
            spannable.setSpan(span, 0, currency.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        private fun applyToIntegerPart(spannable: SpannableString, currency: String, wholeSting: String, span: Any) {
            spannable.setSpan(span, currency.length, wholeSting.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }
}