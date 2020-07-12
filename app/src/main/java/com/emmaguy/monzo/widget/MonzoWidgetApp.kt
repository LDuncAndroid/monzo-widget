package com.emmaguy.monzo.widget

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emmaguy.monzo.widget.api.ApiModule
import com.emmaguy.monzo.widget.login.LoginModule
import com.emmaguy.monzo.widget.room.Database
import com.emmaguy.monzo.widget.settings.SettingsModule
import com.emmaguy.monzo.widget.storage.StorageModule
import timber.log.Timber


class MonzoWidgetApp : Application() {
    val storageModule by lazy { StorageModule(this) }
    val apiModule by lazy { ApiModule(this, storageModule) }
    val database by lazy {
        Room.databaseBuilder(this, Database::class.java, "db").build()
    }

    val loginModule by lazy { LoginModule(this, storageModule, apiModule) }
    val settingsModule by lazy { SettingsModule(storageModule, database.potsDao()) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {

        fun get(context: Context): MonzoWidgetApp {
            return context.applicationContext as MonzoWidgetApp
        }
    }
}
