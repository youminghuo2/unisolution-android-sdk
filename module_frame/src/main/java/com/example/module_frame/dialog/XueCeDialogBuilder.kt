package com.example.module_frame.dialog

import androidx.appcompat.app.AppCompatActivity
import com.example.module_frame.dialog.builder.XueCeDialogFragment

/**
 * 通用学测的dialog
 */
class XueCeDialogBuilder(private val activity: AppCompatActivity) {

    private val dialog by lazy { XueCeDialogFragment() }

    fun setTitle(title: String): XueCeDialogBuilder {
        dialog.setTitle(title)
        return this

    }

    fun setMessage(message: String): XueCeDialogBuilder {
        dialog.setMessage(message)
        return this
    }

    fun setNegativeButton(text: String): XueCeDialogBuilder {
        dialog.setNegativeButton(text)
        return this
    }

    fun setNegativeButton(text: String, listener: XueCeDialogFragment.OnClickListener): XueCeDialogBuilder {
        dialog.setNegativeButton(text, listener)
        return this
    }

    fun setPositiveButton(text: String, listener: XueCeDialogFragment.OnClickListener): XueCeDialogBuilder {
        dialog.setPositiveButton(text, listener)
        return this
    }

    fun setCancelable(cancelable: Boolean): XueCeDialogBuilder {
        dialog.dialogCancelable = cancelable
        return this
    }

    fun show(tag: String = "") {
        dialog.show(activity.supportFragmentManager, "")
    }

    fun isDialogShowing() = dialog.isDialogShowing()

    fun show() {
        dialog.show(activity.supportFragmentManager, "")
    }

    fun setCenter(isCenter: Boolean): XueCeDialogBuilder {
        dialog.setDialogCenter(isCenter)
        return this
    }
}