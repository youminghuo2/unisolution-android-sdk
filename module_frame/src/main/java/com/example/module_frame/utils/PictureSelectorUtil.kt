package com.example.module_frame.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.dylanc.longan.randomUUIDString
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener

class PictureSelectorUtil {
    var currentIndex=0
    lateinit var resultList: java.util.ArrayList<LocalMedia>
    var pictureList= mutableListOf<String>()
    private lateinit var context:Context
   private lateinit var uCropLauncher: ActivityResultLauncher<Intent>


    fun showSelector(context: Context,maxSelectNum:Int=1, uCropLauncher: ActivityResultLauncher<Intent>,onSelected: (imagePaths: List<String>) -> Unit) {
        this.context=context
        this.uCropLauncher=uCropLauncher
        PictureSelector.create(context).openGallery(SelectMimeType.ofAll()).isWithSelectVideoImage(true).setImageEngine(
            GlideEngine.createGlideEngine())
            .setMaxSelectNum(maxSelectNum)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: java.util.ArrayList<LocalMedia>) {
                    resultList = result
                    currentIndex = 0  // 初始化索引
                    processNextImage(resultList, currentIndex, onSelected)  // 从第一张图片开始处理
                }

                override fun onCancel() {

                }
            })

    }

    /**
     * 视频=>忽略，图片=>裁剪
     */
    fun processNextImage(result: java.util.ArrayList<LocalMedia>, index: Int, onSelected: (imagePaths: List<String>) -> Unit) {
        if (index >= result.size) {
            onSelected(pictureList)
            return  // 所有图片处理完毕
        }

        val uri = Uri.parse(result[index].path)

        if (CommonUtils.isVideoUri(context,uri)){
            UCropHelper.getRealPathFromUri(context, uri)?.let { pictureList.add(it) }
            currentIndex++
            processNextImage(result, currentIndex, onSelected)
        }else{
            startPhotoZoomByCrop(uri, uCropLauncher, onSelected)
        }
    }

    private fun startPhotoZoomByCrop(uri: Uri, uCropLauncher: ActivityResultLauncher<Intent>, onSelected: (imagePaths: List<String>) -> Unit) {
        val uCropIntent = UCropHelper.getUCropIntent(context, uri, "${System.currentTimeMillis()}_$randomUUIDString.jpg")
        uCropLauncher.launch(uCropIntent)
    }

}