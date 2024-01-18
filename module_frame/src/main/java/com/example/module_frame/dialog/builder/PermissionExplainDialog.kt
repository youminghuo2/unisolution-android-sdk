package com.example.module_frame.dialog.builder

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.module_frame.adapter.PermissionExplainAdapter
import com.example.module_frame.databinding.FragmentDialogPermissionExplainBinding
import com.example.module_frame.entity.PermissionEntity
import com.example.module_frame.extend.shapeBackground

class PermissionExplainDialog : DialogFragment() {

    private var dialogBinding: FragmentDialogPermissionExplainBinding? = null
    private val binding get() = dialogBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogBinding = FragmentDialogPermissionExplainBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.background = requireContext().shapeBackground(
            fillColor = Color.parseColor("#EAF8FF"),
            allCorner = 12f,
            borderColor = Color.parseColor("#80BCFF"),
            borderWidth = 0.5f
        )

        val dataList = arguments?.getParcelableArrayList(
            "permission",
            PermissionEntity::class.java
        ) as List<PermissionEntity>
        val adapter = PermissionExplainAdapter(requireContext(), dataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.0f)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun onDestroy() {
        dialogBinding = null
        super.onDestroy()
    }


    companion object {
        fun newInstance(dataList: List<PermissionEntity>): PermissionExplainDialog {
            val args = Bundle()
            args.putParcelableArrayList("permission", ArrayList(dataList))
            val fragment = PermissionExplainDialog()
            fragment.arguments = args
            return fragment
        }
    }

}