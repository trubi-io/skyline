package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.render.AxisRender
import io.trubi.skyline.render.GeometryRender
import io.trubi.skyline.render.GraphRender
import io.trubi.skyline.render.TimeViewRender

/**
 * Created by Fitz on 2018/3/19.
 *
 * Responsible for drawing main graph (candle view or closeTime line)
 */
open class KMainGraphView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : KAxisView(context, attrs, defStyleAttr) {

    //RectF help to draw candle and it`s shadow line
    private val candleRect         = CandleRect()
    private val candleUpShadow     = CandleRect()
    private val candleBottomShadow = CandleRect()

    private var highLowMarkEnable  = true

    override fun computeCandleDrawnCount() {
        candlesCountPerScreen = ((kLineContentViewPort.right - kLineContentViewPort.left) / (candleWidth + candleGap)).toInt()
        candlesMaxCountPerScreen = candlesCountPerScreen
        if (candlesCountPerScreen > mCandles.size){
            candlesCountPerScreen = mCandles.size
        }
    }

    override fun beforeDrawCandles(canvas: Canvas?) {
        super.beforeDrawCandles(canvas)
        lastCandleRect.reset()
        currentCandleRect.reset()
        TimeViewRender.resetPath()
        lastCandleMiddleX    = 0f
        currentCandleMiddleX = 0f
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        super.onDrawEachCandle(candle, candleIndex, candleIndexOnScreen, candleRect, candleUpShadow, candleBottomShadow, candleMiddleX, lastCandleMiddleX, candleColor, canvas)
        if (viewMode == modeLine){
            drawLineView(candleRect, candleMiddleX, candleIndexOnScreen, canvas)
        }else {
            GraphRender.drawCandle(candleRect, candleColor, canvas)
            GraphRender.drawShadowLine(candleUpShadow, candleColor, canvas)
            GraphRender.drawShadowLine(candleBottomShadow, candleColor, canvas)
        }
    }


    private val lastCandleRect   : CandleRect = CandleRect()    //used for draw closeTime line
    private val currentCandleRect: CandleRect = CandleRect()    //used for draw closeTime line

    private var lastCandleMiddleX   : Float    = 0f
    private var currentCandleMiddleX: Float    = 0f

    private fun drawLineView(candleRect: CandleRect, cmx: Float, candleIndexOnScreen: Int, canvas: Canvas?){
        lastCandleRect.copyProperties(currentCandleRect)
        currentCandleRect.copyProperties(candleRect)

        lastCandleMiddleX    = currentCandleMiddleX
        currentCandleMiddleX = cmx

        if (candleIndexOnScreen == 0){
            TimeViewRender.movePathTo(currentCandleMiddleX, mainGraphViewport.bottom)
            TimeViewRender.linePathTo(currentCandleMiddleX, currentCandleRect.close)
        }

        if (lastCandleRect.close >= 0 && currentCandleRect.close >= 0 && lastCandleMiddleX > 0 && currentCandleMiddleX > 0){
            TimeViewRender.drawLine(lastCandleMiddleX, lastCandleRect.close, currentCandleMiddleX, currentCandleRect.close, mFlavor.timeLineColor, timeLineWidth, canvas)
            TimeViewRender.linePathTo(currentCandleMiddleX, currentCandleRect.close)

            if (candleIndexOnScreen == candlesWillBeDrawn.size - 1){
                TimeViewRender.linePathTo(currentCandleMiddleX, mainGraphViewport.bottom)
                TimeViewRender.closePath(mFlavor.timeLineColor, canvas)
            }
        }
    }

    override fun afterDrawCandles(canvas: Canvas?) {
        super.afterDrawCandles(canvas)
        if (highLowMarkEnable) {
            markHighLowPrice(canvas)
        }
    }

    private fun markHighLowPrice(canvas: Canvas?){
        val arrowHeight = measureTextHeight(yAxisTextSize)
        val arrowWidth  = arrowHeight * 1.5f
        if (!mHighestCandleTopPoint.isBlank()){

            val arrowRect = RectF()

            val hp = considerConvertPriceDisplay(mHighestPrice.toString())

            if (mHighestCandleIndex < candlesMaxCountPerScreen / 2){
                //left screen
                arrowRect.left   = mHighestCandleTopPoint.x
                arrowRect.bottom = mHighestCandleTopPoint.y
                arrowRect.right  = arrowRect.left + arrowWidth
                arrowRect.top    = arrowRect.bottom - arrowHeight
                GeometryRender.drawArrow(arrowRect, GeometryRender.left, mFlavor.markLabelTextColor, canvas)

                AxisRender.drawAxisTextAlignLeft(hp, mHighestCandleTopPoint.x + arrowWidth + commonGap, mHighestCandleTopPoint.y - getTextDescent(yAxisTextSize),
                        yAxisTextSize, mFlavor.markLabelTextColor, canvas, mTypeface)
            }else{
                //right screen
                arrowRect.right   = mHighestCandleTopPoint.x
                arrowRect.bottom  = mHighestCandleTopPoint.y
                arrowRect.left    = arrowRect.right - arrowWidth
                arrowRect.top    = arrowRect.bottom - arrowHeight
                GeometryRender.drawArrow(arrowRect, GeometryRender.right, mFlavor.markLabelTextColor, canvas)

                AxisRender.drawAxisTextAlignRight(hp, mHighestCandleTopPoint.x - arrowWidth - commonGap, mHighestCandleTopPoint.y- getTextDescent(yAxisTextSize),
                        yAxisTextSize, mFlavor.markLabelTextColor, canvas, mTypeface)
            }
        }
        if (!mLowestCandleIndexBottomPoint.isBlank()){

            val arrowRect = RectF()

            val lp = considerConvertPriceDisplay(mLowestPrice.toString())
            if (mLowestCandleIndex < candlesMaxCountPerScreen / 2){
                //left screen
                arrowRect.left   = mLowestCandleIndexBottomPoint.x
                arrowRect.top    = mLowestCandleIndexBottomPoint.y
                arrowRect.right  = arrowRect.left + arrowWidth
                arrowRect.bottom = arrowRect.top + arrowHeight

                GeometryRender.drawArrow(arrowRect, GeometryRender.left, mFlavor.markLabelTextColor, canvas)

                AxisRender.drawAxisTextAlignLeft(lp, mLowestCandleIndexBottomPoint.x + arrowWidth + commonGap, mLowestCandleIndexBottomPoint.y + measureTextHeight(yAxisTextSize) - getTextDescent(yAxisTextSize),
                        yAxisTextSize, mFlavor.markLabelTextColor, canvas, mTypeface)
            }else{
                //right screen
                arrowRect.right   = mLowestCandleIndexBottomPoint.x
                arrowRect.top    = mLowestCandleIndexBottomPoint.y
                arrowRect.left    = arrowRect.right - arrowWidth
                arrowRect.bottom = arrowRect.top + arrowHeight

                GeometryRender.drawArrow(arrowRect, GeometryRender.right, mFlavor.markLabelTextColor, canvas)

                AxisRender.drawAxisTextAlignRight(lp, mLowestCandleIndexBottomPoint.x - arrowWidth - commonGap, mLowestCandleIndexBottomPoint.y + measureTextHeight(yAxisTextSize) - getTextDescent(yAxisTextSize),
                        yAxisTextSize, mFlavor.markLabelTextColor, canvas, mTypeface)
            }
        }
    }

    fun enableHighLowMark(enable: Boolean){
        highLowMarkEnable = enable
    }

}