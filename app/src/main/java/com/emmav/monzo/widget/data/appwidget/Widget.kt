package com.emmav.monzo.widget.data.appwidget

sealed class Widget {
    abstract val appWidgetId: Int
    abstract val widgetTypeId: String
    abstract val balance: Long
    abstract val currency: String

    data class Account(
        override val appWidgetId: Int,
        override val widgetTypeId: String,
        override val balance: Long,
        override val currency: String,
        val name: String
    ) : Widget()

    data class Pot(
        override val appWidgetId: Int,
        override val widgetTypeId: String,
        override val balance: Long,
        override val currency: String,
        val name: String
    ) : Widget()
}
