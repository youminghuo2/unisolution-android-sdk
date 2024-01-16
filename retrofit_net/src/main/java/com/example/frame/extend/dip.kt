package com.example.frame.extend

import android.content.Context

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()