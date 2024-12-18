package com.example.module_frame.extend

import android.content.Context
import android.content.Intent
import com.dylanc.longan.startActivity
import com.example.camera.PreviewViewActivity
import com.example.module_frame.interfaces.PreviewCallback
import java.io.Serializable


object CameraHelper {
//    fun  startPreviewActivity(context: Context,providerAuthorities:String, callback: PreviewCallback? = null){
//        val intent = Intent(context, PreviewViewActivity::class.java)
//        // 将回调对象存储在 Intent 中
//
//        intent.putExtra("callback", callback)
//        intent.putExtra("authorities",providerAuthorities)
//        context.startActivity(intent)
//    }
    var callback: PreviewCallback? = null

    fun startPreviewActivity(context: Context, providerAuthorities: String, callback: PreviewCallback? = null) {
        this.callback = callback
        val intent = Intent(context, PreviewViewActivity::class.java)
        intent.putExtra("authorities", providerAuthorities)
        context.startActivity(intent)
    }
}

