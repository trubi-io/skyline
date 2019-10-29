package io.trubi.skyline.touch.dcfw

import android.view.MotionEvent
import io.trubi.skyline.cross.dcfw.DCFWCrossLineActionListener
import io.trubi.skyline.touch.AbstractKToucherImpl
import io.trubi.skyline.touch.SkylineTransformListener
import kotlin.math.abs

/**
 * author       : Fitz Lu
 * created on   : 12/12/2018 13:46
 * description  :
 */
class DCFWKToucher: AbstractKToucherImpl() {

    private var mCrossLineActionListener: DCFWCrossLineActionListener? = null

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

    private var mTransformListener: SkylineTransformListener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action.and(MotionEvent.ACTION_MASK)){
            MotionEvent.ACTION_DOWN -> {
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
                            val touchYAxisValue           = mKView?.findTouchedYAxisValue(touchPoints.first.y)
                            val yCoordinateInKContent     = mKView?.getYCoordinateInKLineContentView(touchPoints.first.y)

                            if (touchCandle != null && touchCandleRect != null && yCoordinateInKContent != null){
                                mCrossLineActionListener?.onShowCrossLine(touchCandle, touchCandleRect, previousCandle,
                                        touchIndicatorWrappers, touchSubIndicatorWrappers, yCoordinateInKContent, touchYAxisValue)
                            }
                        }
                    }
                }

                mKView?.removeCallbacks(longPressRunnable)
                mKView?.postDelayed(longPressRunnable, longPressedDuration)

                if (longPressedActive){
                    longPressedActive = false
                    mCrossLineActionListener?.onDismissCrossLine()
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                mTouchMode = POINT_DOWN
                mKView?.removeCallbacks(longPressRunnable)
                if (event.pointerCount >= 2){
                    saveTouchDataWhenActionPointerDown(event)
                    //Distance between two pointers in x axis
                    mSavedXDist = getXDistance(event)
                    //Distance between two pointers in y axis
                    mSavedYDist = getYDistance(event)
                    //Straight line distance between two pointers
                    mSavedDist  = spacing(event)

                    if (mSavedDist > minScaleDistance) {
                        mTouchMode = if (mSavedXDist >= mSavedYDist) X_ZOOM else Y_ZOOM
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(distance(event.x, touchPoints.first.x, event.y, touchPoints.first.y)) > minDragDistance){
                    mKView?.removeCallbacks(longPressRunnable)
                }
                if (longPressedActive){
                    mKView?.findTouchedCandleIndexOnScreen(event.x, event.y)?.let { indexOnScreen ->
                        val previousCandle            = mKView?.findCandleByIos(indexOnScreen - 1)
                        val touchCandle               = mKView?.findCandleByIos(indexOnScreen)
                        val touchCandleRect           = mKView?.findCandleRectByIos(indexOnScreen)
                        val touchIndicatorWrappers    = mKView?.getIndicatorsByIos(indexOnScreen)
                        val touchSubIndicatorWrappers = mKView?.getSubGraphIndicatorsByIos(indexOnScreen)
                        val touchYAxisValue           = mKView?.findTouchedYAxisValue(event.y)
                        val yCoordinateInKContent     = mKView?.getYCoordinateInKLineContentView(event.y)

                        if (touchCandle != null && touchCandleRect != null && yCoordinateInKContent != null){
                            mCrossLineActionListener?.onShowCrossLine(touchCandle, touchCandleRect, previousCandle,
                                    touchIndicatorWrappers, touchSubIndicatorWrappers, yCoordinateInKContent, touchYAxisValue)
                        }
                    }
                }else{
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
            MotionEvent.ACTION_UP -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onLostFocus()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onLostFocus()
            }
            MotionEvent.ACTION_CANCEL -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onLostFocus()
            }
            MotionEvent.ACTION_OUTSIDE -> {
                mTouchMode = NONE
                mKView?.removeCallbacks(longPressRunnable)
                mCrossLineActionListener?.onLostFocus()
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
        if (event.pointerCount < 2){
            //Anti pointerIndex out of range???
            return
        }
        mTransformListener?.beforeZoomX()
        zoomX(event)
        mTransformListener?.afterZoomX()
    }

    private fun performZoomY(event: MotionEvent){
        mTransformListener?.beforeZoomY()
        zoomY(event)
        mTransformListener?.afterZoomY()
    }

    fun setCrossLineActionListener(listener: DCFWCrossLineActionListener){
        mCrossLineActionListener = listener
    }

    fun setSkylineTransformListener(listener: SkylineTransformListener){
        mTransformListener = listener
    }

    /**
     * This is a dangerous method, it can
     * interrupt existing touch logic and
     * active or disable long press directly
     * @param active true or false
     * */
    @Deprecated("")
    fun activeLongPress(active: Boolean){
        longPressedActive = active
        if (longPressRunnable != null) {
            mKView?.removeCallbacks(longPressRunnable)
        }
    }

}