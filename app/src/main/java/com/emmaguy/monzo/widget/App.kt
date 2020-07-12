package com.emmaguy.monzo.widget

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emmaguy.monzo.widget.api.ApiModule
import com.emmaguy.monzo.widget.login.LoginModule
import com.emmaguy.monzo.widget.settings.SettingsModule
import com.emmaguy.monzo.widget.storage.AuthStorage
import com.emmaguy.monzo.widget.storage.Database
import com.emmaguy.monzo.widget.storage.Repository
import timber.log.Timber

class App : Application() {
    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    private val userStorage by lazy { AuthStorage(this) }
    private val database by lazy {
        Room.databaseBuilder(this, Database::class.java, "db")
                .fallbackToDestructiveMigration()
                .build()
    }

    private val apiModule by lazy {
        ApiModule(
                clientId = clientId,
                clientSecret = clientSecret,
                context = this,
                userStorage = userStorage
        )
    }
    val repository by lazy {
        Repository(
                clientId = clientId,
                clientSecret = clientSecret,
                ioScheduler = AppModule.ioScheduler(),
                monzoApi = apiModule.monzoApi,
                userStorage = userStorage,
                storage = database.storage()
        )
    }

    val loginModule by lazy {
        LoginModule(context = this, clientId = clientId, repository = repository)
    }
    val settingsModule by lazy { SettingsModule(repository = repository) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {

        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}
