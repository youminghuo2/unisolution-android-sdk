package com.example.module_frame.extend

import android.content.Context
import android.content.Intent

import com.example.camera.SelectImgActivity
import com.example.module_frame.interfaces.PreviewCallback

object SelectImgHelper {
    var callback: PreviewCallback? = null

    fun startPreviewActivity(context: Context, providerAuthorities: String, callback: PreviewCallback? = null) {
        this.callback = callback
        val intent = Intent(context, SelectImgActivity::class.java)
        intent.putExtra("authorities", providerAuthorities)
        context.startActivity(intent)
    }

}