package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint

/**
 * Created by AndyL on 2018/3/19.
 *
 */
object AxisRender {

    private val axisLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).also { it.isAntiAlias = true }
    private val axisTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).also { it.isAntiAlias = true }

    fun drawAxisLine(startX: Float, startY: Float, endX: Float, endY: Float, lineColor: Int, lineWidth: Float, canvas: Canvas?, typeface: Typeface?){
        axisLinePaint.reset()

        axisLinePaint.isAntiAlias = true
        axisLinePaint.style       = Paint.Style.FILL_AND_STROKE
        axisLinePaint.color       = lineColor
        axisLinePaint.strokeWidth = lineWidth

        canvas?.drawLine(startX, startY, endX, endY, axisLinePaint)
    }

    fun drawAxisTextAlignCenter(text: String, centerX: Float, centerY: Float, textSize: Float, textColor: Int, canvas: Canvas?, typeface: Typeface?){
        axisTextPaint.reset()

        axisTextPaint.isAntiAlias = true
        axisTextPaint.style       = Paint.Style.FILL_AND_STROKE
        axisTextPaint.color       = textColor
        axisTextPaint.textSize    = textSize
        axisTextPaint.textAlign   = Paint.Align.CENTER
        axisTextPaint.typeface    = typeface

        canvas?.drawText(text, centerX, centerY, axisTextPaint)
    }

    fun drawAxisTextAlignLeft(text: String, x: Float, y: Float, textSize: Float, textColor: Int, canvas: Canvas?, typeface: Typeface?){
        axisTextPaint.reset()

        axisTextPaint.isAntiAlias = true
        axisTextPaint.style       = Paint.Style.FILL_AND_STROKE
        axisTextPaint.color       = textColor
        axisTextPaint.textSize    = textSize
        axisTextPaint.textAlign   = Paint.Align.LEFT
        axisTextPaint.typeface    = typeface

        canvas?.drawText(text, x, y, axisTextPaint)
    }

    fun drawAxisTextAlignRight(text: String, x: Float, y: Float, textSize: Float, textColor: Int, canvas: Canvas?, typeface: Typeface?){
        axisTextPaint.reset()

        axisTextPaint.isAntiAlias = true
        axisTextPaint.style       = Paint.Style.FILL_AND_STROKE
        axisTextPaint.color       = textColor
        axisTextPaint.textSize    = textSize
        axisTextPaint.textAlign   = Paint.Align.RIGHT
        axisTextPaint.typeface    = typeface

        canvas?.drawText(text, x, y, axisTextPaint)
    }

}