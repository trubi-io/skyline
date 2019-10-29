package io.trubi.skyline.touch.simple

import android.view.MotionEvent
import io.trubi.skyline.cross.CrossLineActionListener
import io.trubi.skyline.touch.AbstractKToucherImpl
import io.trubi.skyline.touch.SkylineTransformListener

/**
 * Created by AndyL on 2018/3/22.
 *
 */
class SimpleKToucher : AbstractKToucherImpl(){

    private var mSavedXDist = 1f
    private var mSavedYDist = 1f
    private var mSavedDist  = 1f

    /**
     * Mark long pressed is active
     * */
    private var longPressedActive     = false
    /**
     * Long pressed duration in milliseconds
     * */
    private val longPressedDuration   = 200L
    /**
     *
     * */
    private var longPressRunnable: Runnable? = null

    private var mCrossLineActionListener: CrossLineActionListener? = null

    private var mTransformListener: SkylineTransformListener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action.and(MotionEvent.ACTION_MASK)){
            MotionEvent.ACTION_DOWN         -> {
                mTouchMode = DRAG
                saveTouchDataWhenActionDown(event)

                if (longPressRunnable == null){
                    longPressRunnable = Runnable {
                        longPressedActive = true

                        mKView?.findTouchedCandleIndexOnScreen(touchPoints.first.x, touchPoints.first.y)?.let { indexOnScreen ->
                            val previousCandle            = mKView?.findCandleByIos(indexOnScreen - 1)
                            val touchCandle               = mKView?.findCandleByIos(indexOnScreen)
                            val touchCandleRect           = mKView?.findCandleRectByIos(indexOnScreen)
                            val touchIndicatorWrappers    = mKView?.getIndicatorsByIos(indexOnScreen)
                            val touchSubIndicatorWrappers = mKView?.getSubGraphIndicatorsByIos(indexOnScreen)

                            if (touchCandle != null && touchCandleRect != null){
                                mCrossLineActionListener?.onCrossLineShow(touchCandle, touchCandleRect,
                                        touchIndicatorWrappers, touchSubIndicatorWrappers, previousCandle)
                            }
                        }
                    }
                }
                mKView?.removeCallbacks(longPressRunnable)
                mKView?.postDelayed(longPressRunnable, longPressedDuration)

                if (longPressedActive){
                    longPressedActive = false
                    mCrossLineActionListener?.onCrossLineDismiss()
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mTouchMode = POINT_DOWN
                mKView?.removeCallbacks(longPressRunnable)
                if (event.pointerCount >= 2){
                    saveTouchDataWhenActionPointerDown(event)
                    //两点 X 轴方向距离
                    mSavedXDist = getXDistance(event)
                    //两点 Y 轴方向距离
                    mSavedYDist = getYDistance(event)
                    //两点直线距离
                    mSavedDist  = spacing(event)

                    if (mSavedDist > minScaleDistance) {
                        mTouchMode = if (mSavedXDist >= mSavedYDist) X_ZOOM else Y_ZOOM
                    }
                }
            }
            MotionEvent.ACTION_MOVE         -> {
                if (Math.abs(distance(event.x, touchPoints.first.x, event.y, touchPoints.first.y)) > minDragDistance){
                    mKView?.removeCallbacks(longPressRunnable)
                }
                if (longPressedActive){
                    mKView?.findTouchedCandleIndexOnScreen(event.x, event.y)?.let { indexOnScreen ->
                        val previousCandle            = mKView?.findCandleByIos(indexOnScreen - 1)
                        val touchCandle               = mKView?.findCandleByIos(indexOnScreen)
                        val touchCandleRect           = mKView?.findCandleRectByIos(indexOnScreen)
                        val touchIndicatorWrappers    = mKView?.getIndicatorsByIos(indexOnScreen)
                        val touchSubIndicatorWrappers = mKView?.getSubGraphIndicatorsByIos(indexOnScreen)

                        if (touchCandle != null && touchCandleRect != null){
                            mCrossLineActionListener?.onCrossLineMoved(touchCandle, touchCandleRect,
                                    touchIndicatorWrappers, touchSubIndicatorWrappers, previousCandle)
                        }
                    }
                }else {
                    when (mTouchMode) {
                        DRAG -> {
                            performDrag(event)
                        }
                        X_ZOOM -> {
                            performZoomX(event)
                        }
                        Y_ZOOM -> {
                            performZoomY(event)
                        }
                        PINCH_ZOOM -> {
                            //not implement
                        }
                        DOWN -> {

                        }
                    }
                }
            }
            MotionEvent.ACTION_UP           -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onCrossLineDismiss()
                mCrossLineActionListener?.onLostFocus()
                longPressedActive = false
            }
            MotionEvent.ACTION_POINTER_UP   -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onCrossLineDismiss()
                mCrossLineActionListener?.onLostFocus()
                longPressedActive = false
            }
            MotionEvent.ACTION_CANCEL       -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onCrossLineDismiss()
                mCrossLineActionListener?.onLostFocus()
                longPressedActive = false
            }
            MotionEvent.ACTION_OUTSIDE      -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onCrossLineDismiss()
                mCrossLineActionListener?.onLostFocus()
                longPressedActive = false
            }
        }

        return true
    }

    private fun performDrag(event: MotionEvent){
        mTransformListener?.beforeDrag()
        val moveUnit = drag(event)
        if (moveUnit > 0){
            //左滑，图向左移动
            mTransformListener?.afterDragLeft()
        }else if(moveUnit < 0){
            //右滑，图向右移动
            mTransformListener?.afterDragRight()
        }
    }

    private fun performZoomX(event: MotionEvent){
        mTransformListener?.beforeZoomX()
        zoomX(event)
        mTransformListener?.afterZoomX()
    }

    private fun performZoomY(event: MotionEvent){
        mTransformListener?.beforeZoomY()
        zoomY(event)
        mTransformListener?.afterZoomY()
    }

    fun setCrossLineActionListener(listener: CrossLineActionListener){
        mCrossLineActionListener = listener
    }

    fun setSkylineTransformListener(listener: SkylineTransformListener){
        mTransformListener = listener
    }

}