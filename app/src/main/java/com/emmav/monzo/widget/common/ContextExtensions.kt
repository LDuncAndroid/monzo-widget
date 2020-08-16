package com.emmav.monzo.widget.common

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Context.openUrl(url: String, toolbarColor: Color) {
    CustomTabsIntent.Builder()
        .setToolbarColor(toolbarColor.toArgb())
        .build()
        .launchUrl(this, android.net.Uri.parse(url))
}