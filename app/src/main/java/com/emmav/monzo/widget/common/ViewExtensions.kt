package com.emmav.monzo.widget.common

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.TextView

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setVisibility(visible: Boolean) {
    if (visible) visible() else gone()
}

fun TextView.bindText(txt: Text) {
    if (txt == Text.Empty) {
        gone()
    } else {
        visible()
        text = resources.resolveText(txt)
    }
}

fun Context.resolveText(text: Text): String {
    return when (text) {
        is Text.String -> text.value.toString()
        is Text.ResString -> resolveTextRes(text.resId, text.args)
    }
}

private fun Context.resolveArgs(args: List<Any>): Array<Any> {
    return args.map {
        if (it is Text) resolveText(it) else it
    }.toTypedArray()
}

private fun Context.resolveTextRes(resId: Int, args: List<Any> = emptyList()): String {
    return if (args.isEmpty()) {
        getString(resId)
    } else {
        getString(resId, *resolveArgs(args))
    }
}

fun Resources.resolveText(text: Text): CharSequence {
    return when (text) {
        is Text.String -> text.value
        is Text.ResString -> resolveTextRes(text.resId, text.args)
    }
}

private fun Resources.resolveArgs(args: List<Any>): Array<Any> {
    return args.map {
        if (it is Text) resolveText(it) else it
    }.toTypedArray()
}

private fun Resources.resolveTextRes(resId: Int, args: List<Any> = emptyList()): String {
    return if (args.isEmpty()) {
        getString(resId)
    } else {
        getString(resId, *resolveArgs(args))
    }
}
