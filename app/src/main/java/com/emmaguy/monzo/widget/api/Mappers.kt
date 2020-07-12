package com.emmaguy.monzo.widget.api

import com.emmaguy.monzo.widget.storage.DbAccount
import com.emmaguy.monzo.widget.storage.DbBalance
import com.emmaguy.monzo.widget.storage.DbPot

internal fun ApiAccount.toDbAccount(): DbAccount {
    return DbAccount(id = id, type = type)
}

internal fun ApiBalance.toDbBalance(accountId: String): DbBalance {
    return DbBalance(accountId = accountId, currency = currency, balance = balance)
}

internal fun ApiPot.toDbPot(accountId: String): DbPot {
    return DbPot(
            id = id,
            accountId = accountId,
            name = name,
            balance = balance,
            currency = currency
    )
}

