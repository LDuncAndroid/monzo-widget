package com.emmav.monzo.widget.common

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

object NumberFormat {

    fun formatBalance(currency: String, amount: Long, showFractionalDigits: Boolean): String {
        return NumberFormat.getCurrencyInstance()
            .apply {
                this.currency = Currency.getInstance(currency)
                if (!showFractionalDigits) {
                    maximumFractionDigits = 0
                }
            }
            .format(BigDecimal(amount).scaleByPowerOfTen(-2).toBigInteger())
    }
}