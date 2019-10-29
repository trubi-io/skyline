package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

object TimeViewRender {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath  = Path()

    fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, lineColor: Int, lineWidth: Float, canvas: Canvas?){
        resetPaint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = lineColor
        mPaint.strokeWidth = lineWidth
        canvas?.drawLine(startX, startY, endX, endY, mPaint)
    }

    fun movePathTo(endX: Float, endY: Float){
        mPath.moveTo(endX, endY)
    }

    fun linePathTo(endX: Float, endY: Float){
        mPath.lineTo(endX, endY)
    }

    fun closePath(color: Int, canvas: Canvas?){
        resetPaint()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue  = Color.blue(color)
        val newColor = Color.argb((255 * 0.2f).toInt(), red, green, blue)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = newColor
        mPath.close()
        canvas?.drawPath(mPath, mPaint)
    }

    fun resetPath(){
        mPath.reset()
    }

    private fun resetPaint(){
        mPaint.reset()
    }

}