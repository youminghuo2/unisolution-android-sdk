package com.example.retrofit_net.manager.interceptor

import com.blankj.utilcode.BuildConfig
import com.example.retrofit_net.manager.HttpManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    fun createRetrofit(baseUrl: String, token: String): Retrofit {
        val mOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest=chain.request()
                val resultWithToken=originalRequest.newBuilder()
                    .header("AuthToken", HttpManager.token)
                    .build()
                chain.proceed(resultWithToken)
            }
            .addInterceptor(mHttpLogInterceptor)
            .addInterceptor(TokenInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(mOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val mHttpLogInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }
}

fun <T> create(baseUrl: String, token: String, service: Class<T>): T {
    val mRetrofit = RetrofitFactory.createRetrofit(baseUrl, token)
    return mRetrofit.create(service)
}
