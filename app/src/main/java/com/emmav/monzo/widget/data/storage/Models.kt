package com.emmav.monzo.widget.data.storage

sealed class Widget {
    abstract val id: String

    sealed class Balance : Widget() {
        abstract val balance: Long
        abstract val currency: String

        data class Account(
            override val id: String,
            override val balance: Long,
            override val currency: String,
            val accountId: String,
            val type: String
        ) : Balance()

        data class Pot(
            override val id: String,
            override val balance: Long,
            override val currency: String,
            val potId: String,
            val name: String
        ) : Balance()
    }
}
