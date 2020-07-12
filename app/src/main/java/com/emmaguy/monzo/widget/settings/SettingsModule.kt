package com.emmaguy.monzo.widget.settings

import com.emmaguy.monzo.widget.AppModule
import com.emmaguy.monzo.widget.storage.Repository

class SettingsModule(private val repository: Repository) {

    fun provideSettingsPresenter(widgetId: Int): SettingsPresenter {
        return SettingsPresenter(AppModule.uiScheduler(), widgetId, repository = repository)
    }
}