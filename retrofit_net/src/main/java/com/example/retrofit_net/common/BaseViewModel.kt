package com.example.retrofit_net.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    fun launchUI(
        onFailure: suspend () -> Unit,
        onResponse: suspend () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            safeApiCall(onFailure, onResponse)
        }
    }

    private suspend fun <T> safeApiCall(
        onFailure: suspend () -> Unit,
        onResponse: suspend () -> T?,
    ): T? {
        try {
            return onResponse()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(e)
            ExceptionHandler.handleException(e)
            onFailure()
        }
        return null
    }

}