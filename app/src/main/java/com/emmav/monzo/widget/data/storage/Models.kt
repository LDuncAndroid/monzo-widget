package com.emmav.monzo.widget.data.storage

sealed class Widget {
    data class AccountWidget(val type: String, val balance: Long, val currency: String) : Widget()
    data class PotWidget(val name: String, val balance: Long, val currency: String) : Widget()
}
