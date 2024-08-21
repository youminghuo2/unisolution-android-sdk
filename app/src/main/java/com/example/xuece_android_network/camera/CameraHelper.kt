package com.example.xuece_android_network.camera

import android.content.Context
import com.dylanc.longan.startActivity
import com.example.xuece_android_network.activity.MainActivity


object CameraHelper {
    fun  startPreviewActivity(){
       startActivity<MainActivity>()
    }
}