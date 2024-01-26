package com.example.module_frame.dialog

import androidx.fragment.app.FragmentManager
import com.example.module_frame.dialog.builder.PermissionExplainDialog
import com.example.module_frame.entity.PermissionEntity

/**
 * 权限说明
 */
object PermissionExplainHelper {
    private var dialog: PermissionExplainDialog? = null

    fun showExplain(fragmentManager: FragmentManager, dataList: List<PermissionEntity>) {
        dialog = PermissionExplainDialog.newInstance(dataList)
        dialog?.show(fragmentManager, "")
    }

    fun dismissExplain() {
        dialog?.dismiss()
        dialog = null
    }

}