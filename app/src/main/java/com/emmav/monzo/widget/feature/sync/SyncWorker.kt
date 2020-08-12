package com.emmav.monzo.widget.feature.sync

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.emmav.monzo.widget.data.appwidget.WidgetRepository
import com.emmav.monzo.widget.data.db.MonzoRepository
import com.emmav.monzo.widget.feature.appwidget.WidgetProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SyncWorker @WorkerInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : RxWorker(context, workerParams) {

    @Inject lateinit var monzoRepository: MonzoRepository
    @Inject lateinit var widgetRepository: WidgetRepository

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
                WidgetProvider.updateAllWidgets(context, AppWidgetManager.getInstance(context), widgetRepository)
            }
            .toSingleDefault(Result.success())
            .subscribeOn(Schedulers.io())
    }
}