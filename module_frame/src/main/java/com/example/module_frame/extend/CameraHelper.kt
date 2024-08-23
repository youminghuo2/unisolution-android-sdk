package com.example.module_frame.extend

import com.dylanc.longan.startActivity
import com.example.camera.PreviewViewActivity


object CameraHelper {
    fun  startPreviewActivity(){
           startActivity<PreviewViewActivity>()
    }
}