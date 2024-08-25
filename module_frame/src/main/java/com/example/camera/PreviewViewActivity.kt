package com.example.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import coil.load
import com.dylanc.longan.Logger
import com.dylanc.longan.logDebug
import com.example.module_frame.databinding.ActivityPreviewViewBinding
import com.example.module_frame.interfaces.PreviewCallback
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import java.io.File
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
    }

    private fun startCamera() {
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
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    lifecycleOwner, cameraSelector, preview)
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
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
        binding.saveImg.setOnClickListener {
            if (mSavedUri != null) {
                getRealPathFromUri(this, mSavedUri)?.let { it1 -> callback?.onPreviewFinished(it1) }
                finish()
            }
        }

        //聚焦
        binding.root.rootView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
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
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())+"_"+ UUID.randomUUID().toString()

        //存在私有目录
        val fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File(fileDir, name)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //存在共有目录
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
        // Create output options object which contains file + metadata
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
                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Logger("PreviewActivity").logDebug(output.savedUri)
                    binding.cameraGroup.isVisible = false
                    binding.photosGroup.isVisible = true
                    binding.photosImg.load(output.savedUri)
                    Log.d(TAG, msg)
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
}