package com.example.retrofit_net.manager.interceptor

import com.blankj.utilcode.util.NetworkUtils
import com.example.retrofit_net.manager.exception.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (NetworkUtils.isAvailable()) {
            val request = chain.request()
            return chain.proceed(request)
        } else {
            throw NoNetworkException(1002, "网络异常")
        }
    }
}