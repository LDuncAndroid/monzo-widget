package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.data.auth.LoginRepository
import com.emmav.monzo.widget.data.db.MonzoRepository

class SettingsModule(private val loginRepository: LoginRepository, private val monzoRepository: MonzoRepository) {

    fun provideSettingsViewModel(appWidgetId: Int, widgetTypeId: String?): SettingsViewModel {
        return SettingsViewModel(
            appWidgetId = appWidgetId,
            widgetTypeId = widgetTypeId,
            loginRepository = loginRepository,
            monzoRepository = monzoRepository
        )
    }
}