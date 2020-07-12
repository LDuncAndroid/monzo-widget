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
import timber.log.Timber
import java.math.BigDecimal
import java.util.*


class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val userStorage = MonzoWidgetApp.get(context).storageModule.userStorage
        for (widgetId in appWidgetIds) {
            userStorage.removeAccountType(widgetId)
        }
    }

    companion object {
        private const val ROBOTO_LIGHT = "sans-serif-light"
        private const val ROBOTO_REGULAR = "sans-serif"

        fun updateAllWidgets(context: Context) {
            val thisWidget = ComponentName(context, WidgetProvider::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            for (i in allWidgetIds) {
                updateWidget(context, i)
            }
        }

        fun updateWidget(context: Context, appWidgetId: Int) {
            val userStorage = MonzoWidgetApp.get(context).storageModule.userStorage
            val isCurrentAccount = userStorage.widgetType(appWidgetId) == WidgetType.CURRENT_ACCOUNT

            val accountBalance = userStorage.currentAccountBalance
            if (accountBalance == null) {
                Timber.d("No account balance for widgetId: $appWidgetId is current account: $isCurrentAccount")
                return
            }

            val backgroundResId = if (isCurrentAccount) R.drawable.background_ca else R.drawable.background_prepaid
            val textColour = ContextCompat.getColor(context, if (isCurrentAccount) R.color.monzo_dark else R.color.monzo_light)

            val balance = BigDecimal(accountBalance.balance).scaleByPowerOfTen(-2).toBigInteger()
            val currency = Currency.getInstance(accountBalance.currency).symbol
            val spannableString = createSpannableForBalance(context, currency, balance.toString(), textColour)

            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val remoteViews = RemoteViews(context.packageName, R.layout.widget_balance)
            remoteViews.setOnClickPendingIntent(R.id.widgetViewGroup, pendingIntent)
            remoteViews.setTextViewText(R.id.widgetAmountTextView, spannableString)
            remoteViews.setInt(R.id.widgetBackgroundView, "setBackgroundResource", backgroundResId)
            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, remoteViews)
        }

        private fun createSpannableForBalance(context: Context, currency: String, balance: String, textColour: Int)
                : SpannableString {
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

            val fullString = currency + balance
            val span = SpannableString(fullString)
            applyToCurrency(span, currency, AbsoluteSizeSpan(currencySize.toPx(context)))
            applyToCurrency(span, currency, ForegroundColorSpan(textColour))
            applyToCurrency(span, currency, TypefaceSpan(Typeface.create(ROBOTO_LIGHT, Typeface.NORMAL)))

            applyToIntegerPart(span, currency, fullString, AbsoluteSizeSpan(integerPartSize.toPx(context)))
            applyToIntegerPart(span, currency, fullString, ForegroundColorSpan(textColour))
            applyToIntegerPart(span, currency, fullString, TypefaceSpan(Typeface.create(ROBOTO_REGULAR, Typeface.NORMAL)))

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