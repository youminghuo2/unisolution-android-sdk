package com.example.module_frame.utils

import android.util.Log

fun logError(
    className: String, methodName: String, message: String = "",tag: String = "networkError",
) {
    Log.d(tag, "$className.$methodName: $message")
}