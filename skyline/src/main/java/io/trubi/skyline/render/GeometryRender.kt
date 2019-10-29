package io.trubi.skyline.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import io.trubi.skyline.dev.Sogger

object GeometryRender {

    const val left  = 0
    const val right = 1

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawArrow(area: RectF, direction: Int, color: Int, canvas: Canvas?){
        mPaint.reset()

        if (area.width() < area.height()){
            Sogger.e("GeometryRender", "area width must larger than height")
            return
        }

        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = color
        val arrowPath = Path()
        val halfHeight       = area.height() / 3 * 2
        val halfHeightOffset = halfHeight / 9 * 2
        val arrowHandleWidth = area.height() / 6 / 2
        val arrowHandleTopY    = area.centerY() - arrowHandleWidth
        val arrowHandleBottomY = area.centerY() + arrowHandleWidth
        if (direction == left){
            arrowPath.moveTo(area.left, area.centerY())
            arrowPath.lineTo(area.left + halfHeight, area.top)
            arrowPath.lineTo(area.left + halfHeight - halfHeightOffset, arrowHandleTopY)
            arrowPath.lineTo(area.right, arrowHandleTopY)
            arrowPath.lineTo(area.right, arrowHandleBottomY)
            arrowPath.lineTo(area.left + halfHeight - halfHeightOffset, arrowHandleBottomY)
            arrowPath.lineTo(area.left + halfHeight, area.bottom)
            arrowPath.close()
        }else{
            arrowPath.moveTo(area.right, area.centerY())
            arrowPath.lineTo(area.right - halfHeight, area.top)
            arrowPath.lineTo(area.right - halfHeight + halfHeightOffset, arrowHandleTopY)
            arrowPath.lineTo(area.left, arrowHandleTopY)
            arrowPath.lineTo(area.left, arrowHandleBottomY)
            arrowPath.lineTo(area.right - halfHeight + halfHeightOffset, arrowHandleBottomY)
            arrowPath.lineTo(area.right - halfHeight, area.bottom)
            arrowPath.close()
        }
        canvas?.drawPath(arrowPath, mPaint)
    }

}