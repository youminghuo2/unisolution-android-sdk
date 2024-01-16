package com.example.xuece_android_network.api

import com.example.retrofit_net.common.BaseResult
import com.example.xuece_android_network.entity.TermEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface UserCenterApi {
    //获取学校学期信息

    @GET("/api/usercenter/common/loginuserinfo/terminfo")
    suspend fun getTermInfo(
        @Query("schoolId") schoolId: Long,
    ): BaseResult<MutableList<TermEntity>>
}