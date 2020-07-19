package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.data.storage.MonzoRepository

class SettingsModule(private val monzoRepository: MonzoRepository) {

    fun provideSettingsViewModel(appWidgetId: Int, widgetTypeId: String?): SettingsViewModel {
        return SettingsViewModel(
            appWidgetId = appWidgetId,
            widgetTypeId = widgetTypeId,
            monzoRepository = monzoRepository
        )
    }
}