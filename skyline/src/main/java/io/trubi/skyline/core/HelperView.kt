package io.trubi.skyline.core

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by Fitz on 2018/3/20.
 *
 */
open class HelperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    //width
    protected var mWidth  = 0
    //height
    protected var mHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth  = measuredWidth - paddingStart - paddingEnd
        mHeight = measuredHeight - paddingTop - paddingBottom
    }

    fun measureTextHeight(size: Float): Float =
            Paint().let {
                it.textSize = size
                it.fontMetrics.descent - it.fontMetrics.ascent
            }

    fun measureTextLineHeight(size: Float): Float =
            Paint().let {
                it.textSize = size
                it.fontMetrics.bottom - it.fontMetrics.top
            }

    fun measureTextWidth(size: Float, text: String): Float =
        Paint().let {
            it.textSize = size
            it.measureText(text)
        }

    fun getTextDescent(size: Float): Float =
            Paint().let {
                it.textSize = size
                it.fontMetrics.descent
            }

    fun getTextAscent(size: Float): Float =
            Paint().let {
                it.textSize = size
                it.fontMetrics.ascent
            }

    fun dp2px(dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }

}
