package com.example.camera

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import coil.load
import com.dylanc.longan.Logger
import com.dylanc.longan.context
import com.dylanc.longan.logDebug
import com.example.module_frame.databinding.ActivityPreviewViewBinding
import com.example.module_frame.interfaces.PreviewCallback
import com.example.module_frame.utils.CropFileUtils
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PreviewViewActivity : BaseViewBindingActivity<ActivityPreviewViewBinding>() {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var mSavedUri: Uri? = null
    private var callback: PreviewCallback? = null
    private var mFacingFront = false  //是否是前置摄像头

   private var authorities=""

    override fun initView() {
        startCamera()
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 从 Intent 中获取回调对象并调用回调方法
        callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("callback", PreviewCallback::class.java) as? PreviewCallback
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("callback") as? PreviewCallback
        }
        authorities= intent.getStringExtra("authorities").toString()
    }

    private fun startCamera() {
        switchCamera(false)
    }


    private fun switchCamera(switch:Boolean){
        if (switch){
            mFacingFront = !mFacingFront
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewImg.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(if (mFacingFront) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                .build()


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner, cameraSelector, preview)
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Logger("PreviewActivity").logDebug("Use case binding failed${exc}")
            }

        }, ContextCompat.getMainExecutor(this))


    }


    override fun initClick() {
        //拍摄
        binding.takePhotoImg.setOnClickListener {
            takePhoto()
        }

        //关闭
        binding.closeImg.setOnClickListener {
           if (binding.photosGroup.isVisible){
               binding.cameraGroup.isVisible=true
               binding.photosGroup.isVisible=false
           }else{
               finish()
           }
        }

        //保存
//        binding.saveImg.setOnClickListener {
//            if (mSavedUri != null) {
//                getRealPathFromUri(this, mSavedUri)?.let { it1 -> callback?.onPreviewFinished(it1) }
//                finish()
//            }
//        }

        //聚焦
        binding.root.rootView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN && binding.cameraGroup.isVisible) {
                val x = event.x.toInt()
                val y = event.y.toInt()

                // 将 ImageView 移动到触摸位置
                binding.focusIcon.x = x.toFloat() -  binding.focusIcon.width / 2
                binding.focusIcon.y = y.toFloat() -  binding.focusIcon.height / 2
                binding.focusIcon.isVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.focusIcon.isVisible = false
                }, 4500)
                v.performClick()
            }
            true
        }

        //旋转摄像头
        binding.switchCameraImg.setOnClickListener {
            switchCamera(true)
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())+"_"+ UUID.randomUUID().toString()

//        //存在私有目录
        val fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File(fileDir, name)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

//        存在共有目录
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//         Create output options object which contains file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(
//                contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            )
//            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)

                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    mSavedUri = output.savedUri

                    Logger("PreviewActivity").logDebug(output.savedUri)
                    binding.cameraGroup.isVisible = false
                    binding.photosGroup.isVisible = true
//                    binding.photosImg.load(output.savedUri)   //加载图片
                    val imagePath: String = getRealPathFromUri(context, output.savedUri!!).toString()
                    if (imagePath != null) {
                        val degree =CropFileUtils.readPictureDegree(imagePath)
                        if (degree == 0) {
                            startPhotoZoom(output.savedUri!!)
                        }else{
                            val bitmap = BitmapFactory.decodeFile(imagePath)
                            val rotatedBitmap: Bitmap = CropFileUtils.rotaingImageView(degree, bitmap)
                            val newImagePath: String = saveBitmapToFile(rotatedBitmap)
                            val file = File(newImagePath)
                            val newImageUri = Uri.fromFile(file)
                            startPhotoZoom(newImageUri)
                        }

                    } else {
                        // 处理路径获取失败的情况
                        startPhotoZoom(output.savedUri!!)
                    }
                }
            }
        )
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

    companion object {
        private const val TAG = "PreviewViewActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    }



    private var uriClipUri: Uri? = null //裁剪图片的的地址，最终加载它
    /**
     * 图片裁剪的方法
     * @param uri
     */
    private fun startPhotoZoom(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP")//com.android.camera.action.CROP，这个action是调用系统自带的图片裁切功能
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val contentUri = FileProvider.getUriForFile(
            this,
            authorities,
            File(uri.path!!) // 将 Uri 转换为 File 对象
        )

        intent.setDataAndType(contentUri, "image/*") //裁剪的图片uri和图片类型
        intent.putExtra("crop", "true") //设置允许裁剪，如果不设置，就会跳过裁剪的过程，还可以设置putExtra("crop", "circle")

        intent.putExtra("scale", true)
        // 裁剪框的比例（根据需要显示的图片比例进行设置）
        if (Build.MANUFACTURER.contains("HUAWEI")) {
//            //硬件厂商为华为的，默认是圆形裁剪框，这里让它无法成圆形
//            intent.putExtra("aspectX", 9999)
//            intent.putExtra("aspectY", 9998)
            // 移除 aspectX 和 aspectY 参数，允许自由裁剪
            intent.removeExtra("aspectX")
            intent.removeExtra("aspectY")
        } else {
            //其他手机一般默认为方形。
            intent.putExtra("aspectX", 1)
            intent.putExtra("aspectY", 1)
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val cropFile = CropFileUtils.createImageFile(this, true)
            //设置裁剪的图片地址Uri
            uriClipUri = CropFileUtils.uri
        }else{
            val cropFile = createFile("Crop")
            uriClipUri = Uri.fromFile(cropFile)
        }


        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriClipUri)

        // 设置图片的输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

        // return-data=true传递的为缩略图，小米手机默认传递大图， Android 11以上设置为true会闪退
        intent.putExtra("return-data", false)

        cropImageLauncher.launch(intent)
    }

    private var cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // 裁剪完成后的操作
                if (uriClipUri != null){
                    Log.d(TAG, "Photo capture failed:$uriClipUri")
//                    getRealPathFromUri(this, uriClipUri)?.let { it1 -> callback?.onPreviewFinished(it1) }
//                    finish()
                    binding.photosImg.load(uriClipUri)
                }
            else  {
                // 裁剪被取消，处理取消情况
                // 例如，显示一个 Toast 消息或者重新启动图片选择流程
                    initConfig()
            }
        }else{
           //裁剪取消的情况下
            initConfig()
        }
    }


    private fun initConfig(){
        binding.cameraGroup.isVisible = true
        binding.photosGroup.isVisible = false
        switchCamera(false)

    }


    private fun createFile(type: String): File {
        // 在相册创建一个临时文件
        val picFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${type}_${System.currentTimeMillis()}.jpg")
        try {
            if (picFile.exists()) {
                picFile.delete()
            }
            picFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 临时文件，后面会加long型随机数
//        return File.createTempFile(
//            type,
//            ".jpg",
//            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        )

        return picFile
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        // 创建文件以保存旋转后的图片
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "rotated_image.jpg")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) // 将Bitmap保存为JPEG格式
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath // 返回保存后的文件路径
    }

}