package com.example.xuece_android_network.repository

import com.example.retrofit_net.common.BaseRepository
import com.example.xuece_android_network.entity.TermEntity
import com.example.xuece_android_network.manager.ApiManager

class UserCenterRepository :BaseRepository(){

    //获取学期，测试数据
    suspend fun getTermInfo(schoolId:Long): MutableList<TermEntity>? {
        return requestResult {
            ApiManager.userCenterApi().getTermInfo(schoolId)
        }
    }
}