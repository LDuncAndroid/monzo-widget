package com.emmav.monzo.widget.data.api

import com.emmav.monzo.widget.data.db.DbAccount
import com.emmav.monzo.widget.data.db.DbBalance
import com.emmav.monzo.widget.data.db.DbPot

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

fun String.toShortAccountType(): String {
    return when (this) {
        "uk_prepaid" -> "Prepaid"
        "uk_retail" -> "Current"
        "uk_retail_joint" -> "Joint"
        else -> "Other"
    }
}

fun String.toLongAccountType(): String {
    return when (this) {
        "uk_prepaid" -> "Prepaid account"
        "uk_retail" -> "Current account"
        "uk_retail_joint" -> "Joint account"
        else -> "Other account"
    }
}