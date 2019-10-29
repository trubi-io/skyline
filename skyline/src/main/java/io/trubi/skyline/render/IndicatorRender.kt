package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.Paint

object IndicatorRender {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, lineColor: Int, lineWidth: Float, canvas: Canvas?){
        resetPaint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = lineColor
        mPaint.strokeWidth = lineWidth
        canvas?.drawLine(startX, startY, endX, endY, mPaint)
    }

    private fun resetPaint(){
        mPaint.reset()
    }

}