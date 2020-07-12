package com.emmaguy.monzo.widget.settings

import com.emmaguy.monzo.widget.WidgetType
import com.emmaguy.monzo.widget.common.BasePresenter
import com.emmaguy.monzo.widget.common.plus
import com.emmaguy.monzo.widget.room.DbPot
import com.emmaguy.monzo.widget.room.PotsDao
import com.emmaguy.monzo.widget.storage.UserStorage
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber


class SettingsPresenter(
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler,
        private val appWidgetId: Int,
        private val userStorage: UserStorage,
        private val potsDao: PotsDao
) : BasePresenter<SettingsPresenter.View>() {

    override fun attachView(view: View) {
        super.attachView(view)

        disposables += potsDao.pots()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe { view.showPots(it) }

        disposables += view.currentAccountClicks()
                .doOnNext { userStorage.saveAccountType(appWidgetId, WidgetType.CURRENT_ACCOUNT) }
                .subscribe({ view.finish(appWidgetId) }, Timber::e)
    }

    interface View : BasePresenter.View {
        fun currentAccountClicks(): Observable<Unit>
        fun showPots(pots: List<DbPot>)

        fun finish(appWidgetId: Int)
    }
}