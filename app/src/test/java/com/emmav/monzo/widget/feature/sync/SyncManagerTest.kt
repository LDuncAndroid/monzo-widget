package com.emmav.monzo.widget.feature.sync

import com.emmav.monzo.widget.data.storage.AuthStorage
import com.emmav.monzo.widget.data.api.MonzoApi
import com.emmav.monzo.widget.data.api.model.Balance
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Mockito.`when` as whenMock

class SyncManagerTest {
    private val DEFAULT_CA_ID = "ca_id"

    private val BALANCE_CA = Balance(13579, "GBP")

    private lateinit var syncManager: SyncManager

    @Mock private lateinit var monzoApi: MonzoApi
    @Mock private lateinit var userStorage: AuthStorage

    @Before fun setUp() {
        initMocks(this)

        whenMock(userStorage.currentAccountId).thenReturn(DEFAULT_CA_ID)

        whenMock(monzoApi.balance(DEFAULT_CA_ID)).thenReturn(Single.just(BALANCE_CA))

        syncManager = SyncManager(monzoApi, userStorage)
    }

    @Test fun refreshBalances_noErrors() {
        syncManager
                .sync()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun refreshBalances_noCurrentAccount_noErrors() {
        whenMock(userStorage.currentAccountId).thenReturn(null)

        syncManager
                .sync()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun refreshBalances_savesCurrentAccountBalance() {
        syncManager
                .sync()
                .test()
                .assertNoErrors()
                .assertComplete()

        verify(userStorage).currentAccountBalance = BALANCE_CA
    }
}