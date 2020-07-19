package com.emmav.monzo.widget

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emmav.monzo.widget.data.api.ApiModule
import com.emmav.monzo.widget.data.storage.*
import com.emmav.monzo.widget.feature.home.HomeModule
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

    val monzoRepository by lazy {
        MonzoRepository(
            monzoApi = apiModule.monzoApi,
            storage = database.storage()
        )
    }

    val widgetRepository by lazy { WidgetRepository(storage = database.storage()) }

    val loginModule by lazy {
        LoginModule(
            context = this,
            clientId = clientId,
            loginRepository = loginRepository
        )
    }
    val settingsModule by lazy { SettingsModule(monzoRepository = monzoRepository) }
    val homeModule by lazy { HomeModule(widgetRepository = widgetRepository) }

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
