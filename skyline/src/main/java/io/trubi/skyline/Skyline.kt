package io.trubi.skyline

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import io.trubi.skyline.touch.IKToucher
import io.trubi.skyline.core.KIndicatorGraphView
import io.trubi.skyline.dev.Sogger

/**
 * Created by Fitz on 26/3/2018.
 *
 * Skyline
 * Candle stick view
 *
 * ------------------------------------------------------------|
 * | Main graph top text area, draw indicator value            |
 * |-----------------------------------------------------------|
 * |                                                           |
 * | Main graph area                                           |
 * | Draw K line                                               |
 * | Draw indicators                                           |
 * @see io.trubi.skyline.core.KMainGraphView                 |
 * @see io.trubi.skyline.core.KIndicatorGraphView            |
 * |                                                           |
 * |-----------------------------------------------------------|
 * | Sub graph top text area, draw sub graph`s indicator value |
 * |-----------------------------------------------------------|
 * |                                                           |
 * | Sub graph area                                            |
 * | Draw sub graph indicators                                 |
 * @see io.trubi.skyline.core.KSubGraphView                  |
 * |                                                           |
 * ------------------------------------------------------------|
 * | Bottom text area, draw x axis value                       |
 * ------------------------------------------------------------|
 *
 * @author Fitz
 */
class Skyline @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : KIndicatorGraphView(context, attrs, defStyleAttr) {

    private val mTouchers = arrayListOf<IKToucher>()

    private var valueAnimator: ValueAnimator? = null

    private val clipRect = Rect()

    private var clipProgress = 100

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        clipRect.left = paddingStart
        clipRect.top  = paddingTop
        clipRect.bottom = measuredHeight - paddingBottom
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var callback = true
        mTouchers.forEach {
            callback = callback && it.onTouchEvent(event)
        }
        return callback
    }

    override fun beforeDrawCandles(canvas: Canvas?) {
        super.beforeDrawCandles(canvas)
        if (clipProgress < 100){
            clipRect.right = (clipRect.left + (measuredWidth - paddingStart - paddingEnd) * (clipProgress / 100f)).toInt()
            Sogger.i("beforeDrawCandles", "${clipRect.right}")
            canvas?.clipRect(clipRect)
        }
    }

    /**
     * Add touch event handler
     * @see io.trubi.skyline.touch.IKToucher
     * @param toucher
     * */
    fun addToucher(toucher: IKToucher){
        if (!mTouchers.contains(toucher)){
            toucher.attach(this)
            mTouchers.add(toucher)
        }
    }

    /**
     * Remove touch event handler
     * @param toucher
     * */
    fun removeToucher(toucher: IKToucher){
        if (mTouchers.contains(toucher)){
            mTouchers.remove(toucher)
        }
    }

    fun invalidateWithAnimation(){
        valueAnimator?.cancel()
        valueAnimator?.removeAllListeners()
        valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator?.duration = 700
        valueAnimator?.addUpdateListener { animation ->
            val progress = animation?.animatedValue as? Int?:100
            clipProgress = progress
            invalidate()
        }
        valueAnimator?.start()
    }

}