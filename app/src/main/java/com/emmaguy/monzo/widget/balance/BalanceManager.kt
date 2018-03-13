package com.emmaguy.monzo.widget.balance

import com.emmaguy.monzo.widget.storage.UserStorage
import com.emmaguy.monzo.widget.api.MonzoApi
import io.reactivex.Completable

class BalanceManager(
        private val monzoApi: MonzoApi,
        private val userStorage: UserStorage
) {
    fun refreshBalances(): Completable {
        return Completable.defer {
            val currentAccountId = userStorage.currentAccountId
            if (currentAccountId == null) {
                Completable.complete()
            } else {
                monzoApi.balance(currentAccountId)
                        .doOnSuccess { balance -> userStorage.currentAccountBalance = balance }
                        .toCompletable()
            }
        }
    }
}