package com.example.retrofit_net.common


import com.example.retrofit_net.manager.exception.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

open class BaseRepository {

    suspend fun <T> requestResult(requestCall: suspend () -> BaseResult<T>?): T? {
        val result = withContext(Dispatchers.IO) {
            withTimeout(10 * 1000) {
                requestCall()
            }
        } ?: return null

        if (!result.isSuccess()) {
            throw ApiException(result.code, result.subCode, result.msg)
        }
        return result.data
    }

}