package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import io.trubi.skyline.module.Viewport

/**
 * Created by AndyL on 2018/3/5.
 *
 */
object BorderRender {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dashPaint   = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawBorder(border: Viewport, borderColor: Int, borderWidth: Float, canvas: Canvas?){
        borderPaint.reset()

        borderPaint.isAntiAlias = true
        borderPaint.style       = Paint.Style.STROKE
        borderPaint.color       = borderColor
        borderPaint.strokeWidth = borderWidth

        canvas?.run {
            drawLine(border.left, border.top, border.right - borderWidth, border.top, borderPaint)
            drawLine(border.right - borderWidth, border.top, border.right - borderWidth, border.bottom - borderWidth, borderPaint)
            drawLine(border.right - borderWidth, border.bottom - borderWidth, border.left, border.bottom - borderWidth, borderPaint)
            drawLine(border.left, border.bottom - borderWidth, border.left, border.top, borderPaint)
        }
    }

    fun drawBorder(border: Viewport, borderColor: Int, borderWidth: Float, canvas: Canvas?, left: Boolean, top: Boolean, right: Boolean, bottom: Boolean){
        borderPaint.reset()

        borderPaint.isAntiAlias = true
        borderPaint.style       = Paint.Style.STROKE
        borderPaint.color       = borderColor
        borderPaint.strokeWidth = borderWidth

        canvas?.run {
            if (top) drawLine(border.left, border.top, border.right - borderWidth, border.top, borderPaint)
            if (right) drawLine(border.right - borderWidth, border.top, border.right - borderWidth, border.bottom - borderWidth, borderPaint)
            if (bottom) drawLine(border.right - borderWidth, border.bottom - borderWidth, border.left, border.bottom - borderWidth, borderPaint)
            if (left) drawLine(border.left, border.bottom - borderWidth, border.left, border.top, borderPaint)
        }
    }

    fun drawDashEffectPath(startX: Float, startY: Float, endX: Float, endY: Float, intervals: FloatArray, phase: Float, color: Int, canvas: Canvas?){
        dashPaint.style = Paint.Style.STROKE
        dashPaint.color = color
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        val effects = DashPathEffect(intervals, phase)
        dashPaint.pathEffect = effects
        canvas?.drawPath(path, dashPaint)
    }

}