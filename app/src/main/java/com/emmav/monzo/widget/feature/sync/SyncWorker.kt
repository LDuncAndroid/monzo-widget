package com.emmav.monzo.widget.feature.sync


import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.emmav.monzo.widget.App
import com.emmav.monzo.widget.feature.appwidget.WidgetProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : RxWorker(context, workerParams) {

    private val monzoRepository by lazy { App.get(context).monzoRepository }

    override fun createWork(): Single<Result> {
        return monzoRepository.syncAccounts()
            .flatMapCompletable { accounts ->
                Completable.merge(
                    accounts.map { account ->
                        monzoRepository.syncBalance(accountId = account.id)
                            .andThen(monzoRepository.syncPots(accountId = account.id))
                    }
                )
            }
            .doOnComplete {
                Timber.d("Successfully refreshed data")
                WidgetProvider.updateAllWidgets(context, AppWidgetManager.getInstance(context))
            }
            .toSingleDefault(Result.success())
            .subscribeOn(Schedulers.io())
    }
}