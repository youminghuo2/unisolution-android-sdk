package com.example.module_frame.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

/**
 * 统一toast
 */
fun Context.toast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
