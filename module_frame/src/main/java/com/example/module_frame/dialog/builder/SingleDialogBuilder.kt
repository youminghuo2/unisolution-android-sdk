package com.example.module_frame.dialog.builder

import androidx.appcompat.app.AppCompatActivity


class SingleDialogBuilder(private val activity: AppCompatActivity) {

    private val dialog by lazy { SingleDialogFragment.newInstance() }

    fun setTitle(title: String): SingleDialogBuilder {
        dialog.setTitle(title)
        return this

    }

    fun setMessage(message: String): SingleDialogBuilder {
        dialog.setMessage(message)
        return this
    }

    fun setNegativeButton(text: String): SingleDialogBuilder {
        dialog.setNegativeButton(text)
        return this
    }

    fun setNegativeButton(
        text: String, listener: SingleDialogFragment.OnClickListener
    ): SingleDialogBuilder {
        dialog.setNegativeButton(text, listener)
        return this
    }

    fun setPositiveButton(
        text: String, listener: SingleDialogFragment.OnClickListener
    ): SingleDialogBuilder {
        dialog.setPositiveButton(text, listener)
        return this
    }

    fun setCancelable(cancelable: Boolean): SingleDialogBuilder {
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

    fun setCenter(isCenter: Boolean): SingleDialogBuilder {
        dialog.setDialogCenter(isCenter)
        return this
    }
}