package io.trubi.skyline.subgraph

import android.graphics.Canvas
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.render.IndicatorRender
import io.trubi.skyline.render.GraphRender
import io.trubi.skyline.core.KSubGraphView

class MacdDrawer(private val subGraphView   : KSubGraphView,
                 private val dif            : IndicatorWrapper,
                 private val dem            : IndicatorWrapper,
                 private val stick          : IndicatorWrapper): SubGraphDrawer {

    override fun getTouchedIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper> {
        val wrappers = arrayListOf<IndicatorWrapper>()

        if (indexOnScreen < difWillBeDrawnWrapper.indicators.size){
            val wrapper = IndicatorWrapper()
            wrapper.copyAttrFrom(difWillBeDrawnWrapper)
            wrapper.indicators = arrayListOf(difWillBeDrawnWrapper.indicators[indexOnScreen])
            wrappers.add(wrapper)
        }

        if (indexOnScreen < demWillBeDrawnWrapper.indicators.size){
            val wrapper = IndicatorWrapper()
            wrapper.copyAttrFrom(demWillBeDrawnWrapper)
            wrapper.indicators = arrayListOf(demWillBeDrawnWrapper.indicators[indexOnScreen])
            wrappers.add(wrapper)
        }

        if (indexOnScreen < stickWillBeDrawnWrapper.indicators.size){
            val wrapper = IndicatorWrapper()
            wrapper.copyAttrFrom(stickWillBeDrawnWrapper)
            wrapper.indicators = arrayListOf(stickWillBeDrawnWrapper.indicators[indexOnScreen])
            wrappers.add(wrapper)
        }

        return wrappers
    }

    private val currentValueRange = arrayOf(0.0, 0.0)
    private var valueRange = 0.0

    private val stickRectF = CandleRect()

    private var zeroLine       = 0f
    private var upperZeroRange = 0f
    private var lowerZeroRange = 0f

    private var difWillBeDrawnWrapper   = IndicatorWrapper()
    private var demWillBeDrawnWrapper   = IndicatorWrapper()
    private var stickWillBeDrawnWrapper = IndicatorWrapper()

    override fun beforeDrawCandles(canvas: Canvas?) {
        difWillBeDrawnWrapper.clear()
        demWillBeDrawnWrapper.clear()
        stickWillBeDrawnWrapper.clear()
        currentValueRange[0] = 0.0
        currentValueRange[1] = 0.0

        if (dif.indicators.isNotEmpty() && dif.indicators.size == subGraphView.getAllCandles().size){
            val difWillBeDrawn = dif.indicators.subList(subGraphView.getCurStartIndex(),
                    subGraphView.getCurEndIndex() + 1)
            difWillBeDrawnWrapper.type  = dif.type
            difWillBeDrawnWrapper.cycle = dif.cycle
            difWillBeDrawnWrapper.color = dif.color
            difWillBeDrawnWrapper.width = dif.width
            difWillBeDrawnWrapper.displayName = dif.displayName
            difWillBeDrawn.mapTo(difWillBeDrawnWrapper.indicators) {it}

            val difMin = difWillBeDrawnWrapper.indicators.minBy { it.value }?.value
            if (difMin != null) {
                currentValueRange[0] = Math.min(difMin, currentValueRange[0])
            }
            val difMax = difWillBeDrawnWrapper.indicators.maxBy { it.value }?.value
            if (difMax != null) {
                currentValueRange[1] = Math.max(difMax, currentValueRange[1])
            }
        }else{
            Sogger.w("macd", "dif size is not equal with candle size")
        }

        if (dem.indicators.isNotEmpty() && dem.indicators.size == subGraphView.getAllCandles().size){
            val demWillBeDrawn = dem.indicators.subList(subGraphView.getCurStartIndex(),
                    subGraphView.getCurEndIndex() + 1)
            demWillBeDrawnWrapper.type = dem.type
            demWillBeDrawnWrapper.cycle = dem.cycle
            demWillBeDrawnWrapper.color = dem.color
            demWillBeDrawnWrapper.width = dem.width
            demWillBeDrawnWrapper.displayName = dem.displayName
            demWillBeDrawn.mapTo(demWillBeDrawnWrapper.indicators) {it}

            val demMin = demWillBeDrawnWrapper.indicators.minBy { it.value }?.value
            if (demMin != null) {
                currentValueRange[0] = Math.min(demMin, currentValueRange[0])
            }
            val demMax = demWillBeDrawnWrapper.indicators.maxBy { it.value }?.value
            if (demMax != null) {
                currentValueRange[1] = Math.max(demMax, currentValueRange[1])
            }
        }else{
            Sogger.w("macd", "dem size is not equal with candle size")
        }

        if (stick.indicators.isNotEmpty() && stick.indicators.size == subGraphView.getAllCandles().size){
            val stickWillBeDrawn = stick.indicators.subList(subGraphView.getCurStartIndex(),
                    subGraphView.getCurEndIndex() + 1)
            stickWillBeDrawnWrapper.type = stick.type
            stickWillBeDrawnWrapper.cycle = stick.cycle
            stickWillBeDrawnWrapper.color = stick.color
            stickWillBeDrawnWrapper.width = stick.width
            stickWillBeDrawnWrapper.displayName = stick.displayName
            stickWillBeDrawn.mapTo(stickWillBeDrawnWrapper.indicators) {it}

            val stickMin = stickWillBeDrawnWrapper.indicators.minBy { it.value }?.value
            if (stickMin != null) {
                currentValueRange[0] = Math.min(stickMin, currentValueRange[0])
            }
            val stickMax = stickWillBeDrawnWrapper.indicators.maxBy { it.value }?.value
            if (stickMax != null) {
                currentValueRange[1] = Math.max(stickMax, currentValueRange[1])
            }
        }else{
            Sogger.w("macd", "stick size is not equal with candle size")
        }

        valueRange = currentValueRange[1] - currentValueRange[0]

        zeroLine = subGraphView.getterSubGraphViewport().bottom
        if (currentValueRange[0] < 0){
            zeroLine = (subGraphView.getterSubGraphViewport().bottom - (0 - currentValueRange[0] / valueRange) * subGraphView.getterSubGraphViewport().height()).toFloat()
        }

        upperZeroRange = zeroLine - subGraphView.getterSubGraphViewport().top
        lowerZeroRange = subGraphView.getterSubGraphViewport().bottom - zeroLine
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {

        //draw stick
        if (stickWillBeDrawnWrapper.indicators.size > candleIndexOnScreen && !stickWillBeDrawnWrapper.indicators[candleIndexOnScreen].isEmpty) {
            stickRectF.reset()
            val stickValue = stickWillBeDrawnWrapper.indicators[candleIndexOnScreen].value
            val color: Int
            if (stickValue > 0) {
                stickRectF.top = (zeroLine - (stickValue / currentValueRange[1]) * upperZeroRange).toFloat()
                stickRectF.bottom = zeroLine

                if (stickRectF.bottom - stickRectF.top < 1f){
                    stickRectF.top = stickRectF.bottom - 1f
                }

                color = subGraphView.getFlavor().increasingColor

                stickRectF.left = candleRect.left
                stickRectF.right = candleRect.right
                GraphRender.drawRectF(stickRectF, color, canvas)
            }
            else if (stickValue < 0) {
                stickRectF.top = zeroLine
                stickRectF.bottom = (zeroLine + (stickValue / currentValueRange[0]) * lowerZeroRange).toFloat()

                if (stickRectF.bottom - stickRectF.top < 1f){
                    stickRectF.bottom = stickRectF.top + 1f
                }

                color = subGraphView.getFlavor().decreasingColor

                stickRectF.left = candleRect.left
                stickRectF.right = candleRect.right
                GraphRender.drawRectF(stickRectF, color, canvas)
            }
        }

        //draw dif
        if (difWillBeDrawnWrapper.indicators.size > candleIndexOnScreen) {
            when {
                candleIndexOnScreen == 0 -> Sogger.d("macd draw dif", "first index, jump over it!")
                difWillBeDrawnWrapper.indicators[candleIndexOnScreen - 1].value == 0.toDouble() -> Sogger.d("macd draw dif", "last is empty, jump over it!")
                else -> {
                    val lastY = (subGraphView.getterSubGraphViewport().bottom - (difWillBeDrawnWrapper.indicators[candleIndexOnScreen - 1].value
                            - currentValueRange[0]) / valueRange * subGraphView.getterSubGraphViewport().height()).toFloat()
                    val y = (subGraphView.getterSubGraphViewport().bottom - (difWillBeDrawnWrapper.indicators[candleIndexOnScreen].value
                            - currentValueRange[0]) / valueRange * subGraphView.getterSubGraphViewport().height()).toFloat()

                    IndicatorRender.drawLine(lastCandleMiddleX, lastY, candleMiddleX, y, difWillBeDrawnWrapper.color, difWillBeDrawnWrapper.width, canvas)
                }
            }
        }

        //draw dem
        if (demWillBeDrawnWrapper.indicators.size > candleIndexOnScreen) {
            when {
                candleIndexOnScreen == 0 -> Sogger.d("macd draw dem", "first index, jump over it!")
                demWillBeDrawnWrapper.indicators[candleIndexOnScreen - 1].value == 0.toDouble() -> Sogger.d("macd draw dem", "last is empty, jump over it!")
                else -> {
                    val lastY = (subGraphView.getterSubGraphViewport().bottom - (demWillBeDrawnWrapper.indicators[candleIndexOnScreen - 1].value
                            - currentValueRange[0]) / valueRange * subGraphView.getterSubGraphViewport().height()).toFloat()
                    val y = (subGraphView.getterSubGraphViewport().bottom - (demWillBeDrawnWrapper.indicators[candleIndexOnScreen].value
                            - currentValueRange[0]) / valueRange * subGraphView.getterSubGraphViewport().height()).toFloat()

                    IndicatorRender.drawLine(lastCandleMiddleX, lastY, candleMiddleX, y, demWillBeDrawnWrapper.color, demWillBeDrawnWrapper.width, canvas)
                }
            }
        }
    }

    override fun afterDrawCandles(canvas: Canvas?) {

    }

}