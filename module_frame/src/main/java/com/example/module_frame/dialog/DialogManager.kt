package com.example.module_frame.dialog

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import com.example.module_frame.dialog.builder.SingleDialogBuilder
import com.example.module_frame.dialog.builder.SingleDialogFragment

/**
 * 单例全局的dialog
 * 确保永远只会弹出一次
 */
object DialogManager {
    private var dialogBuilder: SingleDialogBuilder? = null

    fun getDialog(activity: Activity,callback:()->Unit): SingleDialogBuilder {
        if (dialogBuilder == null) {
            dialogBuilder = SingleDialogBuilder(activity as AppCompatActivity)
                .setMessage("已在其他地方登录")
                .setCancelable(false)
                .setPositiveButton("确定", object : SingleDialogFragment.OnClickListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                        callback()
                    }
                })
        }
        return dialogBuilder!!
    }
}