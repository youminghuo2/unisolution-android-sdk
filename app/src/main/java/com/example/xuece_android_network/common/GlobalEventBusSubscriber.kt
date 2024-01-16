package com.example.xuece_android_network.common

import androidx.datastore.preferences.core.stringPreferencesKey
import com.dylanc.longan.application
import com.dylanc.longan.context
import com.example.module_frame.dialog.DialogManager
import com.example.module_frame.utils.ActivityCollector
import com.example.module_frame.utils.CommonUtils
import com.example.module_frame.utils.toast
import com.example.retrofit_net.manager.exception.MessageEvent
import com.example.retrofit_net.manager.exception.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

object GlobalEventBusSubscriber {


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.type) {

            //更新token
            MessageType.updateToken -> {
                val newToken = event.getString()
                // 这里是你处理新Token的代码
                val key = stringPreferencesKey(ComDaraStore.server_token)

                CoroutineScope(Dispatchers.Main).launch {
                    CommonUtils.editDataStore(
                        application.applicationContext,
                        key,
                        newToken as String
                    )
                }
            }

            //跳转登陆页面
            MessageType.jumpToLogin -> {
                jumpToLogin()
            }

            //退出
            MessageType.loginOut -> {
                ActivityCollector.getCurrentActivity()?.let {
                    val dialogBuilder = DialogManager.getDialog(it) {

                    }
                    if (dialogBuilder.isDialogShowing() != true) {
                        dialogBuilder.show()
                    }
                }
                jumpToLogin()
            }

            //弹出tost
            MessageType.errorMsg -> {
                val textMsg = event.getString().toString()
                application.context.toast(textMsg)
            }

            //服务器不可用
            MessageType.ServerUnenable -> {

            }
        }
    }


    fun start() {
        EventBus.getDefault().register(this)
    }

    fun stop() {
        EventBus.getDefault().unregister(this)
    }

    //处理跳转回登录页,并且清除数据
    fun jumpToLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            CommonUtils.clearDataStore(application.applicationContext)
        }
    }
}