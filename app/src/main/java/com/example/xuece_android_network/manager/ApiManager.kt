package com.example.xuece_android_network.manager

import androidx.datastore.preferences.core.stringPreferencesKey
import com.dylanc.longan.Logger
import com.dylanc.longan.application
import com.dylanc.longan.context
import com.dylanc.longan.logDebug
import com.example.module_frame.utils.dataStore
import com.example.retrofit_net.manager.HttpManager
import com.example.xuece_android_network.api.UserCenterApi
import com.example.xuece_android_network.common.ComDaraStore
import com.example.xuece_android_network.common.TagData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

object ApiManager {

//    /**
//     *获取本地的服务器地址,这个是针对固定的单个头部的
//     */
//
//    private suspend fun getBaseUrlFromDataStore(): Pair<String, String> {
//        val urlKey = stringPreferencesKey(ComDaraStore.server_url)
//        val tokenKey = stringPreferencesKey(ComDaraStore.server_token)
//
//        val urlAndToken = application.context.dataStore.data.map { preferences ->
//            Pair(
//                preferences[urlKey] ?: "https://xqdsj-stagingtest2.xuece.cn/api/",
//                preferences[tokenKey] ?: ""
//            )
//        }.first()
//        return urlAndToken
//    }


    //获取本地的服务器地址,这个是针对多个头部的
    private suspend fun getBaseUrlFromDataStore(livingUrl: Boolean=false): Pair<String, String> {
        val urlKey = if (livingUrl) stringPreferencesKey(ComDaraStore.live_url) else stringPreferencesKey(ComDaraStore.server_url)
        val tokenKey = stringPreferencesKey(ComDaraStore.server_token)

        val urlAndToken =
            application.context.dataStore.data.map { preferences ->
                Pair(
                    preferences[urlKey] ?: "https://xqdsj-stagingtest2.xuece.cn/api/",
                    preferences[tokenKey] ?: ""
                )
            }.first()
        return urlAndToken
    }




//    /**
//     * userCenterApi：单个url头部地址，by lazy委托属性效率更高
//     */
//    private val userCenterApiDeferred by lazy {
//        flow {
//            val urlAndToken= getBaseUrlFromDataStore()
//            val baseUrl = urlAndToken.first
//            val token= urlAndToken.second
//
//            Logger(TagData.ApiManager).logDebug("baseUrl地址是：$baseUrl")
//            Logger(TagData.ApiManager).logDebug("token是：$token")
//
//            emit(HttpManager.create(baseUrl,token, UserCenterApi::class.java))
//        }.flowOn(Dispatchers.IO)
//    }

    /**
     * userCenterApi：多个头部地址的
     */

    private fun <T> apiDeferred(livingUrl: Boolean, apiClass: Class<T>) = flow {
        val urlAndToken= getBaseUrlFromDataStore(livingUrl)
        val baseUrl = urlAndToken.first
        val token= urlAndToken.second

        Logger(TagData.ApiManager).logDebug("baseUrl地址是：$baseUrl")
        Logger(TagData.ApiManager).logDebug("token是：$token")

        emit(HttpManager.create(baseUrl,token, apiClass))
    }.flowOn(Dispatchers.IO)


//    //单个头部
//    suspend fun userCenterApi(): UserCenterApi = userCenterApiDeferred.first()

    //适配多个头部的
    suspend fun <T> api(apiClass: Class<T>,livingUrl: Boolean = false): T = apiDeferred(livingUrl, apiClass).first()
}