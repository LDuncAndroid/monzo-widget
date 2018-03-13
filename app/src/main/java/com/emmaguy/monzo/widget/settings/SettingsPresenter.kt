package com.emmaguy.monzo.widget.settings

import com.emmaguy.monzo.widget.storage.UserStorage
import com.emmaguy.monzo.widget.WidgetType
import com.emmaguy.monzo.widget.common.BasePresenter
import com.emmaguy.monzo.widget.common.plus
import io.reactivex.Observable
import timber.log.Timber


class SettingsPresenter(
        private val appWidgetId: Int,
        private val userStorage: UserStorage
) : BasePresenter<SettingsPresenter.View>() {

    override fun attachView(view: View) {
        super.attachView(view)

        disposables += view.currentAccountClicks()
                .doOnNext { userStorage.saveAccountType(appWidgetId, WidgetType.CURRENT_ACCOUNT) }
                .subscribe({ view.finish(appWidgetId) }, Timber::e)
    }

    interface View : BasePresenter.View {
        fun currentAccountClicks(): Observable<Unit>

        fun finish(appWidgetId: Int)
    }
}