package com.emmav.monzo.widget.data.storage

sealed class Widget {
    abstract val id: String

    data class AccountBalance(
        override val id: String,
        val accountType: String, val balance: Long, val currency: String
    ) : Widget()

    data class PotBalance(override val id: String, val name: String, val balance: Long, val currency: String) : Widget()
}
