package com.emmav.monzo.widget.data.storage

enum class WidgetType(val key: String) {
    ACCOUNT("account"),
    POT("pot");

    companion object {

        fun find(key: String): WidgetType? {
            return values().firstOrNull { it.key == key }
        }
    }
}