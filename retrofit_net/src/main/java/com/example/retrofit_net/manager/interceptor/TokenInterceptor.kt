package com.example.retrofit_net.manager.interceptor

import com.example.retrofit_net.manager.HttpManager
import com.example.retrofit_net.manager.exception.MessageEvent
import com.example.retrofit_net.manager.exception.MessageType
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var originalResponse = chain.proceed(request)
        val responseBody=originalResponse.body
        val source=responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer=source?.buffer
        val data= buffer?.clone()?.readString(Charset.forName("UTF-8"))
        if (data!!.isEmpty()) {
            return chain.proceed(request)
        }
        if (data.contains("<html")) {
            return chain.proceed(request)
        }
        val code = JSONObject(data).optString("code")
        if (code == "AUTH_EXPIRED") {
             val refreshRequest=getGetRequest(
                 HttpManager.baseUrl+"/api/usercenter/nnauth/user/token/refresh",
                 mutableMapOf()
             )
            val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).build()
            val response = client.newCall(refreshRequest).execute()
            if (response.isSuccessful) {
                val loginResponseBody = response.body ?: return response
                val json = String(loginResponseBody.bytes())
                val jsonObject = JSONObject(json)
                if (jsonObject.optString("code") == "SUCCESS") {
                    val newToken = JSONObject(json).getJSONObject("data").optString("authtoken")

                    /**
                     * 这边通过EventBus通知主module
                     * 更新token，避免因多处维护token造成混乱
                     */
                    EventBus.getDefault().post(MessageEvent(MessageType.updateToken).put(newToken))

                    val newRequest = request.newBuilder()
                        .removeHeader("AuthToken")
                        .addHeader("AuthToken", newToken)
                        .build()
                    return chain.proceed(newRequest)
                }else {
                    jumpToLogin()
                }
            }else {
                jumpToLogin()
            }
        }
        return originalResponse
    }

    private fun jumpToLogin(){
        EventBus.getDefault().post(MessageEvent(MessageType.jumpToLogin))
    }


    private fun getGetRequest(url: String, params: Map<String, String>): Request {
        val reqBuild = Request.Builder()
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
        if (params.isNotEmpty()) {
            for ((key, value) in params) {
                urlBuilder?.addQueryParameter(key, value)
            }
        }
        reqBuild.url(urlBuilder!!.build())
        reqBuild.addHeader("AuthToken", HttpManager.baseUrl)
        return reqBuild.build()
    }

}