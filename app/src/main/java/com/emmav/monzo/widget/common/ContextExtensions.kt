package com.emmav.monzo.widget.common

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent

fun Context.openUrl(url:String) {
    CustomTabsIntent.Builder()
        .setToolbarColor(androidx.core.content.ContextCompat.getColor(this, com.emmav.monzo.widget.R.color.colorPrimary))
        .build()
        .launchUrl(this, android.net.Uri.parse(url))
}