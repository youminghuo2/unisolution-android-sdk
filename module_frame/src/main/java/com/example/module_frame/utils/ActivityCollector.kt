package com.example.module_frame.utils

import android.app.Activity

/**
 * 监听activity入栈出栈
 */
object ActivityCollector {

    private val activities = mutableListOf<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 随时随地直接退出App
     */
    fun finishAll() {
        activities.forEach { activity ->
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }

    /**
     * 获取当前的Activity
     */
    fun getCurrentActivity(): Activity? {
        return if (activities.isNotEmpty()) activities.last() else null
    }
}