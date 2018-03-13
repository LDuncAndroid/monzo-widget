package com.emmaguy.monzo.widget.sync


import android.app.job.JobParameters
import android.app.job.JobService
import com.emmaguy.monzo.widget.AppModule
import com.emmaguy.monzo.widget.MonzoWidgetApp
import com.emmaguy.monzo.widget.WidgetProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber

class SyncJobService : JobService() {
    private lateinit var syncManager: SyncManager
    private var disposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()

        val app = MonzoWidgetApp.get(this)
        syncManager = SyncManager(app.apiModule.monzoApi, app.storageModule.userStorage, app.database.potsDao())
    }

    override fun onStartJob(jobParams: JobParameters?): Boolean {
        disposable = syncManager.sync()
                .subscribeOn(AppModule.ioScheduler())
                .subscribe({
                    Timber.d("Successfully refreshed balance(s)")
                    jobFinished(jobParams, false)
                    WidgetProvider.updateAllWidgets(this)
                }, { error ->
                    jobFinished(jobParams, false)
                    Timber.e(error, "Failed to refresh balance(s)")
                })
        return true
    }

    override fun onStopJob(jobParams: JobParameters?): Boolean {
        if (disposable?.isDisposed == true) {
            disposable?.dispose()
        }
        return true
    }

    override fun onDestroy() {
        if (disposable?.isDisposed == true) {
            disposable?.dispose()
        }
        super.onDestroy()
    }
}