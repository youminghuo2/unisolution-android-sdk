package com.example.retrofit_net.common



import com.example.retrofit_net.manager.exception.ApiException
import com.example.retrofit_net.manager.exception.MessageEvent
import com.example.retrofit_net.manager.exception.MessageType
import com.example.retrofit_net.manager.exception.NoNetworkException
import org.greenrobot.eventbus.EventBus


object ExceptionHandler {


    fun handleException(e: Throwable) {
        when (e) {
            is ApiException -> {
                val errCode = (e as ApiException).errCode
                val errSubCode = (e as ApiException).errSubCode
                val errMsg = (e as ApiException).errMsg

                when (errCode) {
                    "AUTH_ANOTHERNEW" -> {// 其他设备登录（用户accesstoken失效）
                        showLoginOutDialog()
                    }

                    "AUTH_EXPIRED" -> {//token失效
                        jumpToLogin()
                    }

                    "CIRCUIT_BREAK" -> {//服务器不可用
                        ServerUnenable()
                    }

                    "ERROR" -> {
                        toastShow(errMsg)
                    }

                    "FAILURE" -> {
                        when (errSubCode) {
                            "MARKING_MISSION_NOT_PRESENT", "MARKING_QUESTION_NOT_PRESENT" -> {

                            }

                            else -> {
                                toastShow(errMsg)
                            }
                        }

                    }

                    else -> {
                        toastShow(errMsg)
                    }
                }
            }

            is NoNetworkException -> {
                toastShow("网络连接失败或者超时")
            }

        }

    }


    private fun  toastShow(errMsg:String){
        EventBus.getDefault().post(MessageEvent(MessageType.errorMsg).put(errMsg))
    }

    private fun showLoginOutDialog() {
        EventBus.getDefault().post(MessageEvent(MessageType.loginOut))
    }

    private fun jumpToLogin() {
        EventBus.getDefault().post(MessageEvent(MessageType.jumpToLogin))
    }

    private fun ServerUnenable(){
        EventBus.getDefault().post(MessageEvent(MessageType.ServerUnenable))
    }


}

