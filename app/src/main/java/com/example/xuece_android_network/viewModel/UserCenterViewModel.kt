package com.example.xuece_android_network.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.module_frame.utils.logError

import com.example.retrofit_net.common.BaseViewModel
import com.example.xuece_android_network.entity.TermEntity
import com.example.xuece_android_network.repository.UserCenterRepository

class UserCenterViewModel: BaseViewModel() {
    private val userCenterRepository by lazy { UserCenterRepository() }

    val termList = MutableLiveData<MutableList<TermEntity>>()

    //获取学校学期信息
    fun getTermInfo(schoolId:Long) {
        launchUI(onFailure = {
            logError(this.javaClass.simpleName, "getTermInfo")
        }, onResponse = {
            val data = userCenterRepository.getTermInfo(schoolId)
            data?.let {
                termList.value = it
            } ?: kotlin.run {
                termList.value = mutableListOf()
            }
        })

    }
}