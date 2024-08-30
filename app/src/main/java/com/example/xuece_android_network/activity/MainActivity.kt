package com.example.xuece_android_network.activity

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import coil.load
import com.dylanc.longan.Logger
import com.dylanc.longan.context
import com.dylanc.longan.launchAppSettings
import com.dylanc.longan.lifecycleOwner
import com.dylanc.longan.logDebug
import com.example.module_frame.dialog.DialogManager
import com.example.module_frame.dialog.PermissionExplainHelper.dismissExplain
import com.example.module_frame.dialog.PermissionExplainHelper.showExplain
import com.example.module_frame.dialog.builder.FlutterDialogFragment
import com.example.module_frame.dialog.builder.SingleDialogBuilder
import com.example.module_frame.entity.PermissionEntity
import com.example.module_frame.entity.PermissionListEntity
import com.example.module_frame.extend.CameraHelper
import com.example.module_frame.interfaces.PreviewCallback
import com.example.module_frame.utils.CommonUtils
import com.example.module_frame.utils.CommonUtils.processPermissions
import com.example.module_frame.utils.dataStore
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import com.example.xuece_android_network.common.ComDaraStore
import com.example.xuece_android_network.common.TagData
import com.example.xuece_android_network.databinding.ActivityMainBinding
import com.example.xuece_android_network.viewModel.UserCenterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.Serializable


class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<UserCenterViewModel>()
    private lateinit var dialogBuilder: SingleDialogBuilder
    private var dialogMsg = ""
    private var fileName = ""
    private lateinit var photoURI: Uri

    override fun initView() {

    }

    override fun initData() {
        //测试接口
        viewModel.getTermInfo(21)
    }

    override fun registerObserver() {
        viewModel.termList.observe(lifecycleOwner) {
            Logger(TagData.MainActivity).logDebug(it.toString())

            /**
             * 测试全局单例dialog代码
             */
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    val dialogBuilder = DialogManager.getDialog(this@MainActivity) {

                    }
                    if (dialogBuilder.isDialogShowing() != true) {
                        dialogBuilder.show()
                    }

                    handler.postDelayed(this, 1000)
                }
            }
            handler.postDelayed(runnable, 5000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun initClick() {
        /**
         *dataStore存储代码
         */
        val key = stringPreferencesKey(ComDaraStore.server_token)

        //存储dataStore
        binding.dataStoreSaveBtn.setOnClickListener {
            lifecycleScope.launch {
                CommonUtils.editDataStore(context, key, "36288cd4-7997-4cad-b204-0757f91eb6ec")
            }
        }

        //读取dataStore
        binding.dataStoreReadBtn.setOnClickListener {
            val usertypeCodeFlow: Flow<String> =
                context.dataStore.data.map { preferences ->
                    preferences[key] ?: ""
                }

            lifecycleScope.launch {
                usertypeCodeFlow.collect { token ->
                    binding.dataStoreTv.text = token
                }
            }
        }

        /**
         *   全局单例弹窗
         */
        binding.singleDialogBtn.setOnClickListener {
            initData()
        }

        /**
         * 申请权限 多个权限,RequestMultiplePermissions
         */
        binding.permissionBtn.setOnClickListener {

//             测试权限说明，多权限申请 案例
            val permissionList = listOf(
                PermissionListEntity(
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), PermissionEntity("蓝牙权限", "当您在我们的产品中使蓝牙")
                ),
                PermissionListEntity(
                    listOf(Manifest.permission.CAMERA),
                    PermissionEntity("相机权限", "当您在我们的产品中使用数据上传功能时")
                ),
                PermissionListEntity(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PermissionEntity("地理位置权限", "当您在我们的产品中使用地理位置权限")
                ),
                PermissionListEntity(
                    listOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE,
                    ), PermissionEntity("存储权限", "当您在我们的产品中使用存储权限")
                )

            )

            val multipleList = CommonUtils.checkSelfPermissionMultiple(
                context,
                permissionList
            )

            if (multipleList.isNotEmpty()) {
                val (explainList, requireList) = processPermissions(multipleList)

                val msg = explainList.joinToString(separator = ",") { it.title }
                dialogMsg = "请在设置中开启${msg}，以正常使用App功能"

                showExplain(supportFragmentManager, explainList)

                multiPermissionLauncher.launch(
                    requireList.toTypedArray()
                )
            }
        }

        /**
         * 申请单个权限 ,RequestPermission
         */
        binding.permissionBtn2.setOnClickListener {
            val permission = Manifest.permission.CAMERA
            when (CommonUtils.checkSelfPermission(context, permission)) {
                true -> {
                    //授权
                    Toast.makeText(context, "已经授予权限", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    //未授权，开始授权
                    mPermissionLauncher.launch(permission)
                }
            }

        }

        /**
         *跳转自定义拍照，假设已经授予了camera
         */
        binding.cameraBtn.setOnClickListener {
            CameraHelper.startPreviewActivity(context, "com.example.xuece_android_network.fileprovider",MyPreviewCallback())
        }

        /**
         *跳转系统拍照，假设已经授予了camera
         */
        binding.cameraSystemBtn.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                try {
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        photoURI = createTempPictureUri()
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        takePhotoLauncher.launch(photoURI)
                    }

                } catch (e: Exception) {
                    Logger(TagData.MainActivity).logDebug("error ---> ${e.message}")
                }
            }
        }
    }

    //跳转拍照
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
            if (isSaved) {
                //保存成功
                Toast.makeText(context, "我收到了系统的消息，可以开始旋转了", Toast.LENGTH_SHORT)
                    .show()
                saveImageToGallery(photoURI)
            }
        }

    private fun saveImageToGallery(imageUri: Uri) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    contentResolver.openInputStream(imageUri)?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

        // 广播媒体扫描
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri))
    }

    /**
     * 回调处监听，ActivityResultAPI
     */
    private val multiPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            dismissExplain()
            if (it.values.any { !it }) {
                //常规弹窗
//                XueCeDialogBuilder(this).setTitle("权限申请").setMessage(dialogMsg).setNegativeButton("取消").setCancelable(false).setPositiveButton("确定",object:XueCeDialogFragment.OnClickListener{
//                    override fun onClick(dialog: Dialog?) {
//                        dialog?.dismiss()
//                        launchAppSettings()
//                    }
//                }).show()
                //可以在Flutter弹窗
                FlutterDialogFragment(
                    "权限申请",
                    dialogMsg,
                    "取消",
                    "确定",
                    false,
                    object : FlutterDialogFragment.OnClickListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                            launchAppSettings()
                        }
                    }).show(supportFragmentManager, "")

            }
        }

    //监听单个权限
    private val mPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //已授权
                Toast.makeText(context, "已成功授予权限", Toast.LENGTH_SHORT).show()
            } else {
                //未授权
                Toast.makeText(context, "授予权限失败", Toast.LENGTH_SHORT).show()
            }
        }


    fun createTempPictureUri(
        fileName: String = "picture_${System.currentTimeMillis()}",
        fileExtension: String = ".png"
    ): Uri {
        val tempFile =
            File.createTempFile(fileName, fileExtension, cacheDir).apply { createNewFile() }
        this.fileName = fileName

        return FileProvider.getUriForFile(
            this,
            "com.example.xuece_android_network.fileprovider",
            tempFile
        )
    }

    companion object {
        class MyPreviewCallback: PreviewCallback, Serializable {
            override fun onPreviewFinished(url: String) {
                // 处理回调，例如使用 Log 打印 URL
                Log.d("MyPreviewCallback", "Received image URL: $url")

            }
        }
    }
}


