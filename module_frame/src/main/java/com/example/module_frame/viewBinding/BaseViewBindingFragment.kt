package com.example.module_frame.viewBinding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.module_frame.dialog.builder.LoadingDialogFragment
import com.example.module_frame.extend.saveAs
import com.example.module_frame.extend.saveAsUnChecked
import java.lang.reflect.ParameterizedType

abstract class BaseViewBindingFragment<VB : ViewBinding> : Fragment(), View.OnClickListener {
    private var _binding: VB? = null
    private val binding get() = _binding!!
    private var loadingDialog: LoadingDialogFragment? = null

    private var isViewCreated = false//布局是否被创建
    private var isFirstVisible = true//是否第一次可见

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type = javaClass.genericSuperclass
        val vbClass: Class<VB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        _binding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        Log.d("当前fragment", javaClass.simpleName)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initClick()
        registerObserver()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isVisible && this.isAdded) {
            if (isFirstVisible) {
                isFirstVisible = false
            }
        }
    }

    open fun initView() {}

    open fun initData() {

    }

    open fun registerObserver() {}

    override fun onClick(v: View?) {
    }

    open fun initClick() {

    }

    fun showLoadingDialog(cancelable: Boolean = true) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment()
        }
        loadingDialog?.isCancelable = cancelable
        loadingDialog!!.show(requireActivity().supportFragmentManager, "")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }


    override fun onResume() {
        super.onResume()
        onResumeData()
        if (!isFirstVisible) {
            if (isVisible) {
                onSupportVisible()
            }
        }

    }

    open fun onResumeData() {

    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        isFirstVisible = true
        _binding = null
    }

    open fun onSupportVisible() {
    }


}