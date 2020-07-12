package com.emmaguy.monzo.widget.sync

import android.annotation.SuppressLint
import com.emmaguy.monzo.widget.api.MonzoApi
import com.emmaguy.monzo.widget.api.model.Pot
import com.emmaguy.monzo.widget.room.DbPot
import com.emmaguy.monzo.widget.room.PotsDao
import com.emmaguy.monzo.widget.storage.UserStorage
import io.reactivex.Completable

class SyncManager(
        private val monzoApi: MonzoApi,
        private val userStorage: UserStorage,
        private val potsDao: PotsDao
) {
    fun sync(): Completable {
        return Completable.defer { refreshAccountBalance().andThen(refreshPots()) }
    }

    @SuppressLint("CheckResult")
    private fun refreshAccountBalance(): Completable {
        val currentAccountId = userStorage.currentAccountId
        return if (currentAccountId == null) {
            Completable.complete()
        } else {
            monzoApi.balance(currentAccountId)
                    .doOnSuccess { balance -> userStorage.currentAccountBalance = balance }
                    .ignoreElement()
        }
    }

    @SuppressLint("CheckResult")
    private fun refreshPots(): Completable {
        val currentAccountId = userStorage.currentAccountId
        return if (currentAccountId == null) {
            Completable.complete()
        } else {
            monzoApi.pots(currentAccountId)
                    .doOnSuccess {
                        it.pots?.forEach { pot ->
                            potsDao.insert(pot.toEntity())
                        }
                    }
                    .ignoreElement()
        }
    }
}

private fun Pot.toEntity(): DbPot {
    return DbPot(id, name, balance, currency)
}
