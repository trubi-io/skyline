package io.trubi.skyline.subgraph

import android.graphics.Canvas
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.core.KSubGraphView
import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.render.IndicatorRender

/**
 * author       : Fitz Lu
 * created on   : 18/12/2018 16:12
 * description  : Sub graph drawer to render williams %R
 */
class WilliamsRDrawer(private val subGraphView : KSubGraphView,
                      private val indicators   : ArrayList<IndicatorWrapper>): SubGraphDrawer {

    override fun getTouchedIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper> {
        val wrappers = arrayListOf<IndicatorWrapper>()

        indicatorsWillBeDrawn.forEach {
            if (indexOnScreen < it.indicators.size){
                val wrapper = IndicatorWrapper()
                wrapper.copyAttrFrom(it)
                wrapper.indicators  = arrayListOf(it.indicators[indexOnScreen])
                wrappers.add(wrapper)
            }
        }

        return wrappers
    }

    private var indicatorsWillBeDrawn = arrayListOf<IndicatorWrapper>()

    private var valueRange = arrayOf(0.0, 0.0)

    override fun beforeDrawCandles(canvas: Canvas?) {
        indicatorsWillBeDrawn.clear()
        indicators.forEach { indicatorWrapper ->
            if (indicatorWrapper.indicators.isEmpty()){
                Sogger.w("WilliamsRDrawer", "indicator ${indicatorWrapper.type} is empty")
                return@forEach
            }
            if (indicatorWrapper.indicators.size != subGraphView.getAllCandles().size){
                Sogger.w("WilliamsRDrawer", "indicator`s size should equal with candle`s size")
                return@forEach
            }
            val willBeDrawn = indicatorWrapper.indicators.subList(subGraphView.getCurStartIndex(),
                    subGraphView.getCurEndIndex() + 1)
            val wrapper   = IndicatorWrapper()
            wrapper.type  = indicatorWrapper.type
            wrapper.cycle = indicatorWrapper.cycle
            wrapper.color = indicatorWrapper.color
            wrapper.width = indicatorWrapper.width
            wrapper.displayName = indicatorWrapper.displayName
            willBeDrawn.mapTo(wrapper.indicators) {it}
            indicatorsWillBeDrawn.add(wrapper)
        }

        indicatorsWillBeDrawn.forEach { indicatorWrapper ->
            val min = indicatorWrapper.indicators.minBy { it.value }?.value
            if (min != null){
                valueRange[0] = Math.min(valueRange[0], min)
            }
            val max = indicatorWrapper.indicators.maxBy { it.value }?.value
            if (max != null){
                valueRange[1] = Math.max(valueRange[1], max)
            }
        }
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect,
                                  candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float,
                                  lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        indicatorsWillBeDrawn.forEach {
            if (candleIndexOnScreen == 0){
                Sogger.d("WilliamsRDrawer", "first index, jump over it!")
                return@forEach
            }
            if (it.indicators[candleIndexOnScreen - 1].isEmpty){
                Sogger.d("WilliamsRDrawer", "empty item, jump over it!")
                return@forEach
            }

            val indicatorRange    = valueRange[1] - valueRange[0]
            val minValue          = valueRange[0]
            val viewHeightRange   = subGraphView.getterSubGraphViewport().height()

            val lastY             = (subGraphView.getterSubGraphViewport().bottom  - (it.indicators[candleIndexOnScreen - 1].value - minValue) / indicatorRange * viewHeightRange).toFloat()
            val y                 = (subGraphView.getterSubGraphViewport().bottom  - (it.indicators[candleIndexOnScreen].value - minValue) / indicatorRange * viewHeightRange).toFloat()

            IndicatorRender.drawLine(lastCandleMiddleX, lastY, candleMiddleX, y, it.color, it.width, canvas)
        }
    }

    override fun afterDrawCandles(canvas: Canvas?) {

    }

}