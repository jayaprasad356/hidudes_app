package com.gmwapp.hi_dude.utils

import android.os.SystemClock
import android.view.View

private const val DEBOUNCE_TIME = 500L

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    var lastClickTime = 0L
    this.setOnClickListener { view ->
        if (SystemClock.elapsedRealtime() - lastClickTime >= DEBOUNCE_TIME) {
            lastClickTime = SystemClock.elapsedRealtime()
            onSingleClick(view)
        }
    }
}
