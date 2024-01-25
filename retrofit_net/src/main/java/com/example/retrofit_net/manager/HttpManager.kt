package com.example.retrofit_net.manager

import com.blankj.utilcode.BuildConfig
import com.blankj.utilcode.util.SPUtils
import com.example.retrofit_net.manager.interceptor.RetrofitFactory
import com.example.retrofit_net.manager.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object HttpManager {

    var baseUrl=""

    var token=""

//    private val mRetrofit by lazy {
//
//        Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .client(mOkHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

//     val mOkHttpClient by lazy {
//        OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor { chain ->
//                val originalRequest=chain.request()
//                val resultWithToken=originalRequest.newBuilder()
//                    .header("AuthToken", token)
//                    .build()
//                chain.proceed(resultWithToken)
//            }
//            .addInterceptor(mHttpLogInterceptor)
//            .addInterceptor(TokenInterceptor())
//            .build()
//    }

//    val mHttpLogInterceptor by lazy {
//        HttpLoggingInterceptor().apply {
//            level = if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor.Level.BODY
//            } else {
//                HttpLoggingInterceptor.Level.BASIC
//            }
//        }
//    }


    private fun setBase(baseUrl: String, token: String){
        this.baseUrl=baseUrl
        this.token=token
    }


    fun <T> create(baseUrl: String, token: String,service: Class<T>): T {
        setBase(baseUrl,token)
//        return  mRetrofit.create(service)
        val mRetrofit = RetrofitFactory.createRetrofit(baseUrl, token)
        return mRetrofit.create(service)
    }
}