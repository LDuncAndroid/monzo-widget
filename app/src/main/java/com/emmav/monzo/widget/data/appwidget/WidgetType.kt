package com.emmav.monzo.widget.data.appwidget

enum class WidgetType(val key: String) {
    ACCOUNT_BALANCE("account_balance"),
    POT_BALANCE("pot_balance");

    companion object {

        fun find(key: String): WidgetType? {
            return values().firstOrNull { it.key == key }
        }
    }
}