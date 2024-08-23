package com.example.camera

import coil.load
import com.example.module_frame.databinding.ActivityPhotoPreviewBinding
import com.example.module_frame.viewBinding.BaseViewBindingActivity

class PhotoPreviewActivity  : BaseViewBindingActivity<ActivityPhotoPreviewBinding>(){
    override fun initData() {
        val url=intent.getStringExtra("url")
        binding.img.load(url)
    }
}