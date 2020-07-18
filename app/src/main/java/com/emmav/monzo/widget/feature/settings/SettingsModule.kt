package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.AppModule
import com.emmav.monzo.widget.data.storage.Repository

class SettingsModule(private val repository: Repository) {

    fun provideSettingsPresenter(widgetId: Int): SettingsPresenter {
        return SettingsPresenter(
            AppModule.uiScheduler(),
            widgetId,
            repository = repository
        )
    }
}