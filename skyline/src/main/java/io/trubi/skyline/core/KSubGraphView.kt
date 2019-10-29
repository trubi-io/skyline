package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.module.Viewport
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.subgraph.SubGraphDrawer

/**
 * Created by Fitz on 2018/3/19.
 *
 * Responsible for drawing sub graph (indicators such as vol, macd and so on)
 */
open class KSubGraphView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : KMainGraphView(context, attrs, defStyleAttr) {

    private val subGraphIndicatorViewport = Viewport()

    private var mSubGraphDrawer: SubGraphDrawer? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        subGraphIndicatorViewport.left   = subGraphViewport.left
        subGraphIndicatorViewport.top    = subGraphViewport.top + measureTextHeight(yAxisTextSize)
        subGraphIndicatorViewport.right  = subGraphViewport.right
        subGraphIndicatorViewport.bottom = subGraphViewport.bottom
    }

    override fun beforeDrawCandles(canvas: Canvas?) {
        super.beforeDrawCandles(canvas)
        mSubGraphDrawer?.beforeDrawCandles(canvas)
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        super.onDrawEachCandle(candle, candleIndex, candleIndexOnScreen, candleRect, candleUpShadow, candleBottomShadow, candleMiddleX, lastCandleMiddleX, candleColor, canvas)
        mSubGraphDrawer?.onDrawEachCandle(candle, candleIndex, candleIndexOnScreen, candleRect, candleUpShadow, candleBottomShadow, candleMiddleX, lastCandleMiddleX, candleColor, canvas)
    }

    override fun afterDrawCandles(canvas: Canvas?) {
        super.afterDrawCandles(canvas)
        mSubGraphDrawer?.afterDrawCandles(canvas)
    }

    fun getterSubGraphViewport() = subGraphIndicatorViewport

    fun setSubGraphDrawer(drawer: SubGraphDrawer){
        mSubGraphDrawer = drawer
    }

    fun getterSubGraphTitleArea() = Viewport().also {
        it.top    = subGraphViewport.top
        it.bottom = subGraphViewport.top + measureTextHeight(yAxisTextSize)
        it.left   = subGraphViewport.left
        it.right  = subGraphViewport.right
    }

    fun getSubGraphTouchedIndicators(touchX: Float, touchY: Float): ArrayList<IndicatorWrapper>?{
        val candleIndexOnScreen = findTouchedCandleIndexOnScreen(touchX, touchY) ?: return null
        return mSubGraphDrawer?.getTouchedIndicatorsByIos(candleIndexOnScreen)
    }

    fun getSubGraphIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper>?{
        if (indexOnScreen >= candlesWillBeDrawn.size || indexOnScreen < 0) {
            Sogger.w("getSubGraphTouchedIndicators", "index out of screen")
            return null
        }
        return mSubGraphDrawer?.getTouchedIndicatorsByIos(indexOnScreen)
    }

}