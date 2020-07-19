package com.emmav.monzo.widget

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emmav.monzo.widget.data.api.ApiModule
import com.emmav.monzo.widget.data.storage.LoginStorage
import com.emmav.monzo.widget.data.storage.LoginRepository
import com.emmav.monzo.widget.data.storage.Database
import com.emmav.monzo.widget.data.storage.Repository
import com.emmav.monzo.widget.feature.login.LoginModule
import com.emmav.monzo.widget.feature.settings.SettingsModule
import timber.log.Timber

class App : Application() {
    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    private val loginStorage by lazy { LoginStorage(this) }
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
            loginStorage = loginStorage
        )
    }
    private val loginRepository by lazy {
        LoginRepository(
            clientId = clientId,
            clientSecret = clientSecret,
            monzoApi = apiModule.monzoApi,
            loginStorage = loginStorage
        )
    }

    val repository by lazy {
        Repository(
            monzoApi = apiModule.monzoApi,
            storage = database.storage()
        )
    }

    val loginModule by lazy {
        LoginModule(
            context = this,
            clientId = clientId,
            loginRepository = loginRepository
        )
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
