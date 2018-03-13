package com.emmaguy.monzo.widget.balance

import com.emmaguy.monzo.widget.storage.UserStorage
import com.emmaguy.monzo.widget.api.MonzoApi
import com.emmaguy.monzo.widget.api.model.Balance
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.Mockito.`when` as whenMock

class BalanceManagerTest {
    private val DEFAULT_CA_ID = "ca_id"

    private val BALANCE_CA = Balance(13579, "GBP")

    private lateinit var balanceManager: BalanceManager

    @Mock private lateinit var monzoApi: MonzoApi
    @Mock private lateinit var userStorage: UserStorage

    @Before fun setUp() {
        initMocks(this)

        whenMock(userStorage.currentAccountId).thenReturn(DEFAULT_CA_ID)

        whenMock(monzoApi.balance(DEFAULT_CA_ID)).thenReturn(Single.just(BALANCE_CA))

        balanceManager = BalanceManager(monzoApi, userStorage)
    }

    @Test fun refreshBalances_noErrors() {
        balanceManager
                .refreshBalances()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun refreshBalances_noCurrentAccount_noErrors() {
        whenMock(userStorage.currentAccountId).thenReturn(null)

        balanceManager
                .refreshBalances()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun refreshBalances_savesCurrentAccountBalance() {
        balanceManager
                .refreshBalances()
                .test()
                .assertNoErrors()
                .assertComplete()

        verify(userStorage).currentAccountBalance = BALANCE_CA
    }
}