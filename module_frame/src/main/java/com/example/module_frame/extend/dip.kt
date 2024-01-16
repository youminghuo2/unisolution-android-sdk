package com.example.module_frame.extend

import android.content.Context

fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()