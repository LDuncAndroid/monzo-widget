package com.emmav.monzo.widget.data.appwidget

sealed class Widget {
    abstract val id: String
    abstract val balance: Long
    abstract val currency: String

    data class Account(
        override val id: String,
        override val balance: Long,
        override val currency: String,
        val accountId: String,
        val type: String
    ) : Widget()

    data class Pot(
        override val id: String,
        override val balance: Long,
        override val currency: String,
        val potId: String,
        val name: String
    ) : Widget()
}
