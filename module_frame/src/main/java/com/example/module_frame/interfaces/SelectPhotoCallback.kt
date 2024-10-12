package com.example.module_frame.interfaces

import java.io.Serializable



interface SelectPhotoCallback : Serializable {
    fun onPreviewFinished(url:List<String>)
}