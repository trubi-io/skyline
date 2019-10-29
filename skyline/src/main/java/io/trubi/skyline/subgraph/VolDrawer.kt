package io.trubi.skyline.subgraph

import android.graphics.Canvas
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.render.AxisRender
import io.trubi.skyline.render.GraphRender
import io.trubi.skyline.core.KSubGraphView
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.util.NumberUtil

class VolDrawer(private val subGraphView: KSubGraphView): SubGraphDrawer {

    override fun getTouchedIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper> {
        return arrayListOf()
    }

    private var volHighestValue = 0.toDouble()
    private var currentVolRange = arrayOf(0.0, 0.0)

    private var volRange        = 0.toDouble()
    private var viewHeightRange = 0f
    private val volRect         = CandleRect()

    override fun beforeDrawCandles(canvas: Canvas?) {
        volHighestValue = 0.toDouble()
        currentVolRange[0] = 0.0
        currentVolRange[1] = subGraphView.getCandlesBeDrawn().maxBy { it.volume }?.volume?:0.0
        volRange           = currentVolRange[1] - currentVolRange[0]
        viewHeightRange    = subGraphView.getterSubGraphViewport().bottom - subGraphView.getterSubGraphViewport().top
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        volRect.bottom = subGraphView.getterSubGraphViewport().bottom
        volRect.left   = candleRect.left
        volRect.right  = candleRect.right
        volRect.top    = (volRect.bottom - (candle.volume - currentVolRange[0]) / volRange * viewHeightRange).toFloat()

        if (candle.volume > 0 && volRect.bottom - volRect.top < 1f){
            volRect.top = volRect.bottom - 1f
        }

        GraphRender.drawRectF(volRect,
                if (candle.open <= candle.close) subGraphView.getFlavor().increasingColor
                else subGraphView.getFlavor().decreasingColor, canvas)

        volHighestValue = Math.max(volHighestValue, candle.volume)
    }

    override fun afterDrawCandles(canvas: Canvas?) {
        AxisRender.drawAxisTextAlignLeft(NumberUtil.format(volHighestValue, 0), subGraphView.getterSubGraphViewport().left,
                subGraphView.getterSubGraphViewport().top - subGraphView.getTextDescent(subGraphView.getCurYAxisTextSize()), subGraphView.getCurYAxisTextSize(),
                subGraphView.getFlavor().yAxisLabelColor, canvas, subGraphView.getTypeface())
    }
}