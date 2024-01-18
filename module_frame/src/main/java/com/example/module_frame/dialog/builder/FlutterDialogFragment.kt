package com.example.module_frame.dialog.builder

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import com.example.module_frame.databinding.FragmentDialogXueceBinding
import com.example.module_frame.extend.dip

class FlutterDialogFragment(title: String,message: String,negativeButtonText:String,positiveButtonText:String,dialogCancelable:Boolean,listener: OnClickListener) : DialogFragment() {
    private var dialogBinding: FragmentDialogXueceBinding? = null
    private val binding get() = dialogBinding!!

    private var title: String? = null
    private var message: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonText: String? = null
    var dialogCancelable = true
    private var positiveButtonListener: OnClickListener? = null
    private var negativeButtonListener: OnClickListener? = null
    private var isCenter = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogBinding = FragmentDialogXueceBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    init {
        this.title=title
        this.message=message
        this.negativeButtonText=negativeButtonText
        this.positiveButtonText=positiveButtonText
        this.dialogCancelable=dialogCancelable
        this.positiveButtonListener=listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title?.let {
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvTitle.text = it
        } ?: kotlin.run {
            binding.tvTitle.visibility = View.GONE
        }
        message?.let {
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = it

            val gravity = when {
                isCenter -> Gravity.CENTER_HORIZONTAL
                title.isNullOrEmpty() -> Gravity.CENTER_HORIZONTAL
                else -> Gravity.NO_GRAVITY
            }
            val marginTop =
                if (title.isNullOrEmpty()) requireContext().dip(26) else requireContext().dip(12)
            binding.tvMessage.gravity = gravity
            binding.tvMessage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = marginTop
            }
        } ?: kotlin.run {
            binding.tvMessage.visibility = View.GONE
        }
        negativeButtonText?.let {
            binding.btnNegative.visibility = View.VISIBLE
            binding.btnNegative.text = it
        } ?: kotlin.run {
            binding.btnNegative.visibility = View.GONE
        }
        positiveButtonText?.let {
            binding.btnPositive.visibility = View.VISIBLE
            binding.btnPositive.text = it
            if (negativeButtonText.isNullOrEmpty()) {
                binding.btnPositive.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    width = requireContext().dip(127)
                }
            }
        } ?: kotlin.run {
            binding.btnPositive.visibility = View.GONE
        }
        binding.btnPositive.setOnClickListener {
            positiveButtonListener?.onClick(dialog) ?: kotlin.run {

            }

        }
        binding.btnNegative.setOnClickListener {
            negativeButtonListener?.onClick(dialog) ?: kotlin.run { dismiss() }
        }
        dialog?.setCancelable(dialogCancelable)
    }



    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.5f)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroy() {
        dialogBinding = null
        super.onDestroy()
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setNegativeButton(text: String) {
        this.negativeButtonText = text
    }

    fun setNegativeButton(text: String, listener: OnClickListener) {
        this.negativeButtonText = text
        this.negativeButtonListener = listener
    }

    fun setPositiveButton(text: String, listener: OnClickListener) {
        this.positiveButtonText = text
        this.positiveButtonListener = listener
    }

    fun isDialogShowing() = dialog?.isShowing


    interface OnClickListener {
        fun onClick(dialog: Dialog?)
    }

    fun setDialogCenter(center: Boolean) {
        this.isCenter = center
    }

}