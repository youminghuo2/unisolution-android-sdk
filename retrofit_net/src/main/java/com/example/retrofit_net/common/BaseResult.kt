package com.example.retrofit_net.common

data class BaseResult<T>(
    val data: T?,
    val code: String,
    val subCode: String,
    val msg: String,
    val msgDetail: String,
) {
    fun isSuccess(): Boolean {
        return code == "SUCCESS"

    }
}
