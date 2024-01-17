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

    //获取本地的服务器地址
    private suspend fun getBaseUrlFromDataStore(): Pair<String, String> {
        val urlKey = stringPreferencesKey(ComDaraStore.server_url)
        val tokenKey = stringPreferencesKey(ComDaraStore.server_token)

        val urlAndToken = application.context.dataStore.data.map { preferences ->
            Pair(
                preferences[urlKey] ?: "https://xqdsj-stagingtest2.xuece.cn/api/",
                preferences[tokenKey] ?: ""
            )
        }.first()
        return urlAndToken
    }

    /**
     * userCenterApi
     */
    private val userCenterApiDeferred by lazy {
        flow {
            val urlAndToken= getBaseUrlFromDataStore()
            val baseUrl = urlAndToken.first
            val token= urlAndToken.second

            Logger(TagData.ApiManager).logDebug("baseUrl地址是：$baseUrl")
            Logger(TagData.ApiManager).logDebug("token是：$token")

            emit(HttpManager.create(baseUrl,token, UserCenterApi::class.java))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun userCenterApi(): UserCenterApi = userCenterApiDeferred.first()

}