package com.emmaguy.monzo.widget.sync


import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.emmaguy.monzo.widget.App
import com.emmaguy.monzo.widget.AppModule
import com.emmaguy.monzo.widget.WidgetProvider
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class SyncWorker(
        private val context: Context,
        workerParams: WorkerParameters
) : RxWorker(context, workerParams) {

    private val repository by lazy { App.get(context).repository }

    override fun createWork(): Single<Result> {
        return repository.syncAccounts()
                .flatMapCompletable { accounts ->
                    Completable.merge(
                            accounts.map { account ->
                                repository.syncBalance(accountId = account.id)
                                        .andThen(repository.syncPots(accountId = account.id))
                            }
                    )
                }
                .doOnComplete {
                    Timber.d("Successfully refreshed data")
                    WidgetProvider.updateAllWidgets(context, AppWidgetManager.getInstance(context))
                }
                .toSingleDefault(Result.success())
                .subscribeOn(AppModule.ioScheduler())
    }
}