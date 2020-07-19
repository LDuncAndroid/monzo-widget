package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.data.storage.MonzoRepository

class SettingsModule(private val monzoRepository: MonzoRepository) {

    fun provideSettingsViewModel(widgetId: Int): SettingsViewModel {
        return SettingsViewModel(
            widgetId,
            monzoRepository = monzoRepository
        )
    }
}