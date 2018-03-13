package com.emmaguy.monzo.widget

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.emmaguy.monzo.widget.api.ApiModule
import com.emmaguy.monzo.widget.login.LoginModule
import com.emmaguy.monzo.widget.room.Database
import com.emmaguy.monzo.widget.settings.SettingsModule
import com.emmaguy.monzo.widget.storage.StorageModule
import timber.log.Timber


class MonzoWidgetApp : Application() {
    lateinit var storageModule: StorageModule
    lateinit var apiModule: ApiModule
    lateinit var loginModule: LoginModule
    lateinit var settingsModule: SettingsModule
    lateinit var database: Database

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        storageModule = StorageModule(this)
        apiModule = ApiModule(this, storageModule)
        loginModule = LoginModule(this, storageModule, apiModule)
        database = Room.databaseBuilder(this, Database::class.java, "db").build()
        settingsModule = SettingsModule(storageModule, database.potsDao())
    }

    companion object {

        fun get(context: Context): MonzoWidgetApp {
            return context.applicationContext as MonzoWidgetApp
        }
    }
}
