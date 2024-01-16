package com.example.module_frame.utils

import android.app.Activity

object ActivityCollector {

    private val activities = mutableListOf<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        activities.forEach { activity ->
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }

    /**
     * 获取当前 Activity
     */
    fun getCurrentActivity(): Activity? {
        return if (activities.isNotEmpty()) activities.last() else null
    }
}