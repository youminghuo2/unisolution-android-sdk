package com.example.module_frame.interfaces

import java.io.Serializable




interface PreviewCallback : Serializable {
    fun onPreviewFinished(url:String)
}