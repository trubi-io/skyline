package io.trubi.skyline.touch

import android.view.MotionEvent
import io.trubi.skyline.module.PointF

/**
 * Created by AndyL on 2018/3/6.
 *
 */
abstract class BaseToucher {

    companion object {
        const val NONE       = 0    //
        const val DOWN       = 1    //
        const val POINT_DOWN = 2    //
        const val DRAG       = 3    //Drag the view
        const val X_ZOOM     = 4    //Scale in x axis
        const val Y_ZOOM     = 5    //Scale in y axis
        const val PINCH_ZOOM = 6    //pinch
        const val POST_ZOOM  = 7    //
        const val ROTATE     = 8    //rotate
    }

    protected var minScaleDistance = 10f
    protected var minDragDistance  = 10f

    private var scaleXEnable = true
    private var scaleYEnable = false

    protected var mTouchMode  = NONE

    /**
     *
     * */
    protected fun canZoomOutMoreX() = scaleXEnable

    /**
     *
     * */
    protected fun canZoomInMoreX()  = scaleXEnable

    /**
     *
     * */
    protected fun canZoomOutMoreY() = scaleYEnable

    /**
     *
     * */
    protected fun canZoomInMoreY()  = scaleYEnable

    /**
     * calculates the distance on the x-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    protected fun getXDistance(e: MotionEvent): Float {
        return Math.abs(e.getX(0) - e.getX(1))
    }

    /**
     * calculates the distance on the y-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    protected fun getYDistance(e: MotionEvent): Float {
        return Math.abs(e.getY(0) - e.getY(1))
    }

    /**
     * returns the distance between two pointer touch points
     *
     * @param event
     * @return
     */
    protected fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * returns the distance between two points
     *
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    protected fun distance(eventX: Float, startX: Float, eventY: Float, startY: Float): Float {
        val dx = eventX - startX
        val dy = eventY - startY
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    /**
     * Determines the center point between two pointer touch points.
     *
     * @param point
     * @param event
     */
    protected fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.x  = x / 2f
        point.y  = y / 2f
    }

    /**
     * Determines the center point between two pointer touch points.
     *
     * @param middlePoint
     * @param event
     */
    protected fun midPoint(middlePoint: PointF, startPoint: Pair<PointF, PointF>) {
        val x = startPoint.first.x + startPoint.second.x
        val y = startPoint.first.y + startPoint.second.y
        middlePoint.x  = x / 2f
        middlePoint.y  = y / 2f
    }

    fun setScaleXEnable(enable: Boolean){
        scaleXEnable = enable
    }

    fun setScaleYEnable(enable: Boolean){
        scaleYEnable = enable
    }

}