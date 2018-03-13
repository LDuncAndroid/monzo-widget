package com.emmaguy.monzo.widget

enum class WidgetType(val key: String) {
    CURRENT_ACCOUNT("retail"),
    POT("pot");

    companion object {
        fun find(key: String?): WidgetType? {
            return WidgetType.values().firstOrNull { it.key == key }
        }
    }
}