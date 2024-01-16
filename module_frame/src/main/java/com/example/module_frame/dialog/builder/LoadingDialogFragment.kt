package com.example.module_frame.dialog.builder

import android.annotation.SuppressLint
import com.example.module_frame.databinding.FragmentLoadingDialogBinding
import com.example.module_frame.dialog.builder.BaseViewBindingDialogFragment

class LoadingDialogFragment : BaseViewBindingDialogFragment<FragmentLoadingDialogBinding>() {

    @SuppressLint("UseCompatLoadingForDrawables")

    fun isShowing(): Boolean? {
        return dialog?.isShowing
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.0f)
    }

}