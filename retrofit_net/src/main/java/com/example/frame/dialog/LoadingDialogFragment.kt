package com.example.frame.dialog

import android.annotation.SuppressLint
import com.example.retrofit_net.databinding.FragmentLoadingDialogBinding


class LoadingDialogFragment : BaseViewBindingDialogFragment<FragmentLoadingDialogBinding>() {


    @SuppressLint("UseCompatLoadingForDrawables")

    fun isShowing(): Boolean? {
        return dialog?.isShowing
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.0f)
    }

    fun setTitle(title: String) {
        binding.tvLoading.text = title
    }
}