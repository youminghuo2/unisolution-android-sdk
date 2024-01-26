package com.example.xuece_android_network.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.dylanc.longan.Logger
import com.dylanc.longan.logDebug

class Application: Application() {

    /**
     * 全局监听，项目内跨进程通信统一会以EventBus传递
     * 并且在此处监听
     */
    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                Logger<Application>().logDebug("onActivityCreated")
                if (!GlobalEventBusSubscriber.isRegisitener()){
                    GlobalEventBusSubscriber.start()
                }
            }

            override fun onActivityStarted(p0: Activity) {
                Logger<Application>().logDebug("onActivityStarted")
            }

            override fun onActivityResumed(p0: Activity) {
                Logger<Application>().logDebug("onActivityResumed")
            }

            override fun onActivityPaused(p0: Activity) {
                Logger<Application>().logDebug("onActivityPaused")
            }

            override fun onActivityStopped(p0: Activity) {
                Logger<Application>().logDebug("onActivityStopped")
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                Logger<Application>().logDebug("onActivitySaveInstanceState")
            }

            override fun onActivityDestroyed(p0: Activity) {
                Logger<Application>().logDebug("onActivityDestroyed")
                GlobalEventBusSubscriber.stop()
            }

        })
    }

    override fun onTerminate() {
        super.onTerminate()
        GlobalEventBusSubscriber.stop()
    }
}