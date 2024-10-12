package com.example.module_frame.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.yalantis.ucrop.UCrop
import java.io.File

object UCropHelper {
    //配置裁剪
    fun getUCropIntent(context: Context, uri: Uri, destinationFileName: String): Intent {
        val options = UCrop.Options()
        // 修改标题栏颜色
        options.setToolbarColor(context.getColor(android.R.color.white))
        // 修改状态栏颜色
        options.setStatusBarColor(context.getColor(android.R.color.black))
        // 隐藏底部工具
        options.setHideBottomControls(true)
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        // 让用户调整范围
        options.setFreeStyleCropEnabled(true)
        // 设置图片压缩质量
        options.setCompressionQuality(100)
        // 圆形裁剪
        options.setCircleDimmedLayer(false)
        // 显示网格线
        options.setShowCropGrid(true)
        options.withAspectRatio(4f, 3f) // 设置默认比例
        // 设置显示角标和尺寸变化
        options.setShowCropFrame(true)
        options.setShowCropFrameUpDown(true)
        options.setShowChangingSize(true)

        // 获取文件存储路径
        val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val destinationUri = Uri.fromFile(File(fileDir, destinationFileName))

        // 创建UCrop裁剪意图并返回
        return UCrop.of(uri, destinationUri)
            .withMaxResultSize(1080, 1920)
            .withOptions(options)
            .getIntent(context)
    }

    //把content的Uri转换为本地的真是路径地址
    fun getRealPathFromUri(context: Context, uri: Uri?): String? {
        if (uri == null) return null

        if (uri.scheme == "content") {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        } else if (uri.scheme == "file") {
            return uri.path
        }

        return null
    }
}