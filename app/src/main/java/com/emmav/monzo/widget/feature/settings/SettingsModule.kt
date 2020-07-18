package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.data.storage.Repository

class SettingsModule(private val repository: Repository) {

    fun provideSettingsViewModel(widgetId: Int): SettingsViewModel {
        return SettingsViewModel(
            widgetId,
            repository = repository
        )
    }
}