package com.emmav.monzo.widget.feature.settings

import com.emmav.monzo.widget.data.storage.AuthStorage
import com.emmav.monzo.widget.data.storage.WidgetType
import com.emmav.monzo.widget.data.storage.Storage
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Mockito.`when` as whenever

class SettingsPresenterTest {
    private val appWidgetId = 1
    private val currentAccountRelay = PublishRelay.create<Unit>()

    private val userStorage = mock<AuthStorage>()
    private val potsDao = mock<Storage>()

    private lateinit var presenter: SettingsPresenter
    private val view = mock<SettingsPresenter.View>()

//    @Before fun setUp() {
//        initMocks(this)
//
//        whenever(view.currentAccountClicks()).thenReturn(currentAccountRelay)
//
//        presenter = SettingsPresenter(Schedulers.trampoline(), Schedulers.trampoline(),
//                appWidgetId, userStorage, potsDao)
//    }
//
//    @Test fun `current account clicks, save current account`() {
//        presenter.attachView(view)
//
//        currentAccountRelay.accept(Unit)
//
//        verify(userStorage).saveAccountType(appWidgetId, WidgetType.ACCOUNT)
//    }
//
//    @Test fun `current account clicked, finish`() {
//        presenter.attachView(view)
//
//        currentAccountRelay.accept(Unit)
//
//        verify(view).finish(appWidgetId)
//    }
}