package com.example.xuece_android_network.activity

import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.dylanc.longan.Logger
import com.dylanc.longan.context
import com.dylanc.longan.lifecycleOwner
import com.dylanc.longan.logDebug
import com.example.module_frame.dialog.DialogManager
import com.example.module_frame.dialog.builder.SingleDialogBuilder
import com.example.module_frame.viewBinding.BaseViewBindingActivity
import com.example.xuece_android_network.common.ComDaraStore
import com.example.module_frame.utils.CommonUtils
import com.example.xuece_android_network.common.TagData
import com.example.xuece_android_network.databinding.ActivityMainBinding
import com.example.xuece_android_network.viewModel.UserCenterViewModel
import kotlinx.coroutines.launch

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<UserCenterViewModel>()
    private lateinit var dialogBuilder: SingleDialogBuilder

    override fun initView() {
        //测试代码
//        lifecycleScope.launch {
//            val key = stringPreferencesKey(ComDaraStore.server_token)
//            CommonUtils.editDataStore(context, key, "36288cd4-7997-4cad-b204-0757f91eb6ec")
//        }

    }

    override fun initData() {
        viewModel.getTermInfo(21)
    }

    override fun registerObserver() {
        viewModel.termList.observe(lifecycleOwner) {
            Logger(TagData.MainActivity).logDebug(it.toString())

//            /**
//             * 测试单例dialog代码
//             */
//            val handler = Handler(Looper.getMainLooper())
//            val runnable = object : Runnable {
//                override fun run() {
//                    val dialogBuilder = DialogManager.getDialog(this@MainActivity){
//
//                    }
//                    if (dialogBuilder.isDialogShowing() != true) {
//                        dialogBuilder.show()
//                    }
//
//                    handler.postDelayed(this, 1000)
//                }
//            }
//            handler.postDelayed(runnable, 5000)

        }
    }

    override fun initClick() {
        binding.tvClick.setOnClickListener {
            initData()
        }
    }

}