package com.emmav.monzo.widget

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.emmav.monzo.widget.data.api.ApiModule
import com.emmav.monzo.widget.data.appwidget.WidgetRepository
import com.emmav.monzo.widget.data.auth.ClientRepository
import com.emmav.monzo.widget.data.auth.ClientStorage
import com.emmav.monzo.widget.data.auth.LoginRepository
import com.emmav.monzo.widget.data.auth.LoginStorage
import com.emmav.monzo.widget.data.db.AppDatabase
import com.emmav.monzo.widget.data.db.MonzoRepository
import com.emmav.monzo.widget.feature.home.HomeModule
import com.emmav.monzo.widget.feature.login.LoginModule
import com.emmav.monzo.widget.feature.settings.SettingsModule
import com.emmav.monzo.widget.feature.setupclient.SetupClientModule
import com.emmav.monzo.widget.feature.splash.SplashModule
import timber.log.Timber

class App : Application() {
    private val loginStorage by lazy { LoginStorage(this) }
    private val clientStorage by lazy { ClientStorage(this) }
    private val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .fallbackToDestructiveMigration()
            .build()
            .storage()
    }

    private val apiModule by lazy {
        ApiModule(context = this, loginStorage = loginStorage, clientStorage = clientStorage)
    }
    private val clientRepository by lazy { ClientRepository(clientStorage = clientStorage) }
    private val loginRepository by lazy {
        LoginRepository(monzoApi = apiModule.monzoApi, loginStorage = loginStorage, clientStorage = clientStorage)
    }

    val monzoRepository by lazy { MonzoRepository(monzoApi = apiModule.monzoApi, monzoStorage = database) }
    val widgetRepository by lazy { WidgetRepository(monzoStorage = database) }

    val splashModule by lazy { SplashModule(clientRepository = clientRepository) }
    val setupClientModule by lazy { SetupClientModule(clientRepository = clientRepository, redirectUri = redirectUri) }
    val loginModule by lazy {
        LoginModule(context = this, loginRepository = loginRepository, redirectUri = redirectUri)
    }
    val homeModule by lazy { HomeModule(widgetRepository = widgetRepository) }
    val settingsModule by lazy { SettingsModule(monzoRepository = monzoRepository, loginRepository = loginRepository) }

    private val redirectUri by lazy {
        getString(R.string.callback_url_scheme) + "://" + getString(R.string.callback_url_host)
    }

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
