package com.example.camera

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.module_frame.R
import com.example.module_frame.databinding.ActivityPreviewViewBinding
import com.example.module_frame.databinding.ActivitySelectImgBinding
import com.example.module_frame.utils.PictureSelectorUtil
import com.example.module_frame.utils.UCropHelper
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import com.yalantis.ucrop.UCrop

class SelectImgActivity: BaseViewBindingActivity<ActivitySelectImgBinding>(){
    private lateinit var pictureSelectorUtil: PictureSelectorUtil

    override fun initView() {
        pictureSelectorUtil = PictureSelectorUtil()
        pictureSelectorUtil.showSelector(this,3, uCropLauncher) { }
    }

    private var uCropLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    val resultUri = UCrop.getOutput(intent)
                    // 处理裁剪结果，比如显示图片或保存图片
                    if (resultUri != null) {
                        val list = UCropHelper.getRealPathFromUri(this, resultUri)
                        list?.let { pictureSelectorUtil.pictureList.add(it) }
                        Log.d("PictureList", "保存的图片路径: ${pictureSelectorUtil.pictureList}")
                    }
                }
                // 每次裁剪结束后，递归处理下一张图片
                pictureSelectorUtil.currentIndex++
                pictureSelectorUtil.processNextImage(pictureSelectorUtil.resultList, pictureSelectorUtil.currentIndex) { selectedImagePaths ->
                    // 处理选择完所有图片后的结果
                    Log.d("SelectedImages", "所有保存的图片路径: $selectedImagePaths")
                }
            }
        }
}