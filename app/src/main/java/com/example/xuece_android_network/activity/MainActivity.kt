package com.example.xuece_android_network.activity

import android.Manifest
import android.app.Dialog
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.dylanc.longan.Logger
import com.dylanc.longan.activity
import com.dylanc.longan.context
import com.dylanc.longan.launchAppSettings
import com.dylanc.longan.lifecycleOwner
import com.dylanc.longan.logDebug
import com.example.module_frame.dialog.DialogManager
import com.example.module_frame.dialog.PermissionExplainHelper
import com.example.module_frame.dialog.PermissionExplainHelper.dismissExplain
import com.example.module_frame.dialog.PermissionExplainHelper.showExplain
import com.example.module_frame.dialog.XueCeDialogBuilder
import com.example.module_frame.dialog.builder.FlutterDialogFragment
import com.example.module_frame.dialog.builder.SingleDialogBuilder
import com.example.module_frame.dialog.builder.XueCeDialogFragment
import com.example.module_frame.entity.PermissionEntity
import com.example.module_frame.entity.PermissionListEntity
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import com.example.xuece_android_network.common.ComDaraStore
import com.example.module_frame.utils.CommonUtils
import com.example.module_frame.utils.CommonUtils.processPermissions
import com.example.module_frame.utils.dataStore
import com.example.xuece_android_network.common.TagData
import com.example.xuece_android_network.databinding.ActivityMainBinding
import com.example.xuece_android_network.viewModel.UserCenterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File.separator

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<UserCenterViewModel>()
    private lateinit var dialogBuilder: SingleDialogBuilder

    private var dialogMsg=""

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
                    val dialogBuilder = DialogManager.getDialog(this@MainActivity){

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
                usertypeCodeFlow.collect{token->
                    binding.dataStoreTv.text=token
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
         * 申请权限
         */
        binding.permissionBtn.setOnClickListener {
            /**
             * 测试权限说明，多权限申请 案例
             */
            val permissionList= listOf(
                PermissionListEntity(listOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_ADVERTISE,Manifest.permission.BLUETOOTH_CONNECT) ,PermissionEntity("蓝牙权限","当您在我们的产品中使蓝牙")),
                PermissionListEntity(listOf(Manifest.permission.CAMERA) , PermissionEntity("相机权限","当您在我们的产品中使用数据上传功能时")),
                PermissionListEntity(listOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION) , PermissionEntity("地理位置权限","当您在我们的产品中使用地理位置权限"))
            )

            val multipleList=CommonUtils.checkSelfPermissionMultiple(context,
                permissionList)

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


    }



    /**
     * 回调处监听，ActivityResultAPI
     */
    private val multiPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                it ->
            dismissExplain()
            if (it.values.any { !it }){
//                XueCeDialogBuilder(this).setTitle("权限申请").setMessage(dialogMsg).setNegativeButton("取消").setCancelable(false).setPositiveButton("确定",object:XueCeDialogFragment.OnClickListener{
//                    override fun onClick(dialog: Dialog?) {
//                        dialog?.dismiss()
//                        launchAppSettings()
//                    }
//                }).show()
               FlutterDialogFragment("权限申请",dialogMsg,"取消","确定",false,object :FlutterDialogFragment.OnClickListener{
                   override fun onClick(dialog: Dialog?) {
                       dialog?.dismiss()
                       launchAppSettings()
                   }
               }).show(supportFragmentManager,"")

            }
        }



}