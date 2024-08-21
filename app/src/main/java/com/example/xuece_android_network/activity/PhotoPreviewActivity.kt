package com.example.xuece_android_network.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import com.example.xuece_android_network.R
import com.example.xuece_android_network.databinding.ActivityPhotoPreviewBinding
import com.example.xuece_android_network.databinding.ActivityPreviewViewBinding

class PhotoPreviewActivity  : BaseViewBindingActivity<ActivityPhotoPreviewBinding>(){
    override fun initData() {
        val url=intent.getStringExtra("url")
        binding.img.load(url)
    }
}