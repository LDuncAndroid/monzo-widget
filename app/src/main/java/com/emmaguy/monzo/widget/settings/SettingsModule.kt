package com.emmaguy.monzo.widget.settings

import com.emmaguy.monzo.widget.AppModule
import com.emmaguy.monzo.widget.room.PotsDao
import com.emmaguy.monzo.widget.storage.StorageModule

class SettingsModule(private val storageModule: StorageModule, private val potsDao: PotsDao) {

    fun provideSettingsPresenter(widgetId: Int): SettingsPresenter {
        return SettingsPresenter(AppModule.uiScheduler(), AppModule.ioScheduler(), widgetId,
                storageModule.userStorage, potsDao)
    }
}