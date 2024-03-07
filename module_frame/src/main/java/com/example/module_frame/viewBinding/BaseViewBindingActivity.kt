package com.example.module_frame.viewBinding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.example.module_frame.dialog.builder.LoadingDialogFragment
import com.example.module_frame.extend.saveAs
import com.example.module_frame.extend.saveAsUnChecked
import com.example.module_frame.utils.ActivityCollector
import java.lang.reflect.ParameterizedType


abstract class BaseViewBindingActivity<VB : ViewBinding> : AppCompatActivity(),
    View.OnClickListener {
    private var loadingDialog: LoadingDialogFragment? = null

    protected val binding by lazy {
        getViewBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //透明状态栏
        val controller =WindowCompat.getInsetsController(window,window.decorView);
        controller.isAppearanceLightStatusBars=true

        setContentView(binding.root)
        ActivityCollector.addActivity(this)
        initView()
        initData()
        initClick()
        registerObserver()
    }

    private fun getViewBinding(): VB {
        val type = javaClass.genericSuperclass
        val vbClass: Class<VB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        Log.d("当前Activity", javaClass.simpleName)
        return method.invoke(this, layoutInflater)!!.saveAsUnChecked()
    }

    open fun initView() {}

    open fun initData() {}

    open fun registerObserver() {}

    override fun onClick(v: View?) {
    }

    fun showLoadingDialog(cancelable: Boolean = true) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
        loadingDialog?.isCancelable = cancelable
        loadingDialog!!.show(supportFragmentManager, "")
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        hideLoadingDialog()
    }

    open fun initClick() {

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

}
