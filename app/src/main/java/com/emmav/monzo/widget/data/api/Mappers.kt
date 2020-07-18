package com.emmav.monzo.widget.data.api

import com.emmav.monzo.widget.data.storage.DbAccount
import com.emmav.monzo.widget.data.storage.DbBalance
import com.emmav.monzo.widget.data.storage.DbPot

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

