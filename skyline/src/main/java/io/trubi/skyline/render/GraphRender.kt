package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

/**
 * Created by AndyL on 2018/3/5.
 *
 */
object GraphRender {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 绘制蜡烛
     * */
    fun drawCandle(candle: RectF, color: Int, canvas: Canvas?){
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
        canvas?.drawRect(candle, mPaint)
    }

    /**
     * 绘制影线
     * */
    fun drawShadowLine(shadow: RectF, color: Int, canvas: Canvas?){
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
        canvas?.drawRect(shadow, mPaint)
    }

    /**
     * draw rectF
     * */
    fun drawRectF(rect: RectF, color: Int, canvas: Canvas?){
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
        canvas?.drawRect(rect, mPaint)
    }

    /**
     * draw rectF
     * */
    fun drawRect(rect: Rect, color: Int, fillColor: Boolean, canvas: Canvas?){
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = if (fillColor) Paint.Style.FILL else Paint.Style.STROKE
        canvas?.drawRect(rect, mPaint)
    }

}