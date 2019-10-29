package io.trubi.skyline.touch

import android.view.MotionEvent
import io.trubi.skyline.module.PointF
import io.trubi.skyline.Skyline
import kotlin.math.abs

/**
 * author       : Fitz Lu
 * created on   : 12/12/2018 13:09
 * description  : IKToucher abstract implement
 */
abstract class AbstractKToucherImpl : BaseToucher(), IKToucher {

    protected var mKView: Skyline? = null

    protected var lastTouchPoint = PointF()
    //support multi points
    protected var touchPoints = Pair(PointF(), PointF())

    override fun attach(skyline: Skyline) {
        mKView = skyline
        minDragDistance = skyline.getCurCandleWidth() / 2f
    }

    protected fun saveTouchDataWhenActionDown(event: MotionEvent) {
        touchPoints.first.x = event.x
        touchPoints.first.y = event.y
        lastTouchPoint.x = event.x
        lastTouchPoint.y = event.y
    }

    protected fun saveTouchDataWhenActionPointerDown(event: MotionEvent) {
        touchPoints.first.x = event.getX(0)
        touchPoints.first.y = event.getY(0)
        touchPoints.second.x = event.getX(1)
        touchPoints.second.y = event.getY(1)
    }

    protected fun drag(event: MotionEvent): Int {
        val moveX = event.x - touchPoints.first.x
        val moveUnit = mKView?.moveX(moveX)
        if (moveUnit != null && abs(moveUnit) > 0) {
            touchPoints.first.x = event.x
            touchPoints.first.y = event.y
        }

        return moveUnit ?: 0
    }

    protected fun dragByOffset(offset: Float): Int {
        val moveUnit = mKView?.moveX(offset)
        return moveUnit ?: 0
    }

    protected fun zoomX(event: MotionEvent) {
        val startDis = distance(touchPoints.second.x, touchPoints.first.x, touchPoints.second.y, touchPoints.first.y)
        val currentDis = spacing(event)
        val zoomMiddlePoint = PointF()
        midPoint(zoomMiddlePoint, event)
        val scale = currentDis / startDis
        mKView?.zoomX(scale, zoomMiddlePoint)
        touchPoints.first.x = event.getX(0)
        touchPoints.first.y = event.getY(0)
        touchPoints.second.x = event.getX(1)
        touchPoints.second.y = event.getY(1)
    }

    protected fun zoomY(event: MotionEvent) {
        val startDis = distance(touchPoints.second.x, touchPoints.first.x, touchPoints.second.y, touchPoints.first.y)
        val currentDis = spacing(event)
        val scale = currentDis / startDis
        mKView?.zoomY(scale)
        touchPoints.first.x = event.getX(0)
        touchPoints.first.y = event.getY(0)
        touchPoints.second.x = event.getX(1)
        touchPoints.second.y = event.getY(1)
    }

}