package com.example.module_frame.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 常用工具列
 */
object CommonUtils {

    suspend fun <T> editDataStore(context: Context, key: Preferences.Key<T>, data: T) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { settings ->
                settings[key] = data
            }
        }
    }

    suspend fun clearDataStore(context: Context){
        context.dataStore.edit { settings->
            settings.clear()
        }
    }
}