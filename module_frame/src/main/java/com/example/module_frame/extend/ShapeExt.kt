package com.example.module_frame.extend

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

fun Context.shapeBackground(
    fillColor: Int = Color.WHITE,
    borderWidth: Float = 0f,
    borderColor: Int = Color.GRAY,
    allCorner: Float = 0f,
    topLeftCorner: Float = 0f,
    topRightCorner: Float = 0f,
    bottomLeftCorner: Float = 0f,
    bottomRightCorner: Float = 0f,
): MaterialShapeDrawable {
    val shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
        setAllCorners(RoundedCornerTreatment())
        setTopLeftCornerSize(if (topLeftCorner == 0f) dip(allCorner).toFloat() else dip(topLeftCorner).toFloat())
        setTopRightCornerSize(if (topRightCorner == 0f) dip(allCorner).toFloat() else dip(topRightCorner).toFloat())
        setBottomLeftCornerSize(if (bottomLeftCorner == 0f) dip(allCorner).toFloat() else dip(bottomLeftCorner).toFloat())
        setBottomRightCornerSize(if (bottomRightCorner == 0f) dip(allCorner).toFloat() else dip(bottomRightCorner).toFloat())
    }.build()

    val drawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
        setTint(fillColor)
        paintStyle = Paint.Style.FILL_AND_STROKE
        strokeWidth = dip(borderWidth).toFloat()
        strokeColor = ColorStateList.valueOf(borderColor)
    }
    return drawable
}