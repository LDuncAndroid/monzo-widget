package com.emmaguy.monzo.widget.settings

import com.emmaguy.monzo.widget.storage.UserStorage
import com.emmaguy.monzo.widget.WidgetType
import com.emmaguy.monzo.widget.room.PotsDao
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Mockito.`when` as whenever

class SettingsPresenterTest {
    private val appWidgetId = 1
    private val currentAccountRelay = PublishRelay.create<Unit>()

    private val userStorage = mock<UserStorage>()
    private val potsDao = mock<PotsDao>()

    private lateinit var presenter: SettingsPresenter
    private val view = mock<SettingsPresenter.View>()

    @Before fun setUp() {
        initMocks(this)

        whenever(view.currentAccountClicks()).thenReturn(currentAccountRelay)

        presenter = SettingsPresenter(Schedulers.trampoline(), Schedulers.trampoline(),
                appWidgetId, userStorage, potsDao)
    }

    @Test fun `current account clicks, save current account`() {
        presenter.attachView(view)

        currentAccountRelay.accept(Unit)

        verify(userStorage).saveAccountType(appWidgetId, WidgetType.CURRENT_ACCOUNT)
    }

    @Test fun `current account clicked, finish`() {
        presenter.attachView(view)

        currentAccountRelay.accept(Unit)

        verify(view).finish(appWidgetId)
    }
}