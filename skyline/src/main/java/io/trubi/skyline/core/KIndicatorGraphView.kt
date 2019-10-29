package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.indicator.Indicator
import io.trubi.skyline.indicator.IndicatorComputer
import io.trubi.skyline.render.IndicatorRender
import kotlin.collections.ArrayList

/**
 * Created by Fitz on 2018/3/19.
 * */
open class KIndicatorGraphView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : KSubGraphView(context, attrs, defStyleAttr) {

    private var mainGraphIndicatorWrappers: ArrayList<IndicatorWrapper> = arrayListOf()

    private var indicatorsWillBeDrawn = arrayListOf<IndicatorWrapper>()

    private fun prepareAvgIfExist(){
        mainGraphIndicatorWrappers.find { it.type == Indicator.AVG }?.let {
            IndicatorComputer.avg(mCandles, it, startIndex, endIndex)
        }
    }

    private fun pickIndicatorsCanDrawn(){
        indicatorsWillBeDrawn.clear()
        mainGraphIndicatorWrappers.forEach { indicatorWrapper ->
            if (indicatorWrapper.indicators.isEmpty()){
                Sogger.w("KIndicatorGraphView", "indicator ${indicatorWrapper.type} is empty")
                return@forEach
            }
            if (indicatorWrapper.indicators.size != mCandles.size){
                Sogger.w("KIndicatorGraphView", "indicator`s size should equal with candle`s size")
                return@forEach
            }
            val willBeDrawn = indicatorWrapper.indicators.subList(startIndex, endIndex + 1)
            val wrapper     = IndicatorWrapper()
            wrapper.copyAttrFrom(indicatorWrapper)
            willBeDrawn.mapTo(wrapper.indicators) {it}
            indicatorsWillBeDrawn.add(wrapper)
        }
    }

    override fun onComputePriceRange() {
        super.onComputePriceRange()
        prepareAvgIfExist()
        pickIndicatorsCanDrawn()
        indicatorsWillBeDrawn.forEach { indicatorWrapper ->
            val min = indicatorWrapper.indicators.filter { !it.isEmpty }.minBy { it.value}?.value
            if (min != null){
                currentPriceRange[getMinPrice] = Math.min(currentPriceRange[getMinPrice], min)
            }
            val max = indicatorWrapper.indicators.filter { !it.isEmpty }.maxBy { it.value }?.value
            if (max != null){
                currentPriceRange[getMaxPrice] = Math.max(currentPriceRange[getMaxPrice], max)
            }
        }
    }

    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        super.onDrawEachCandle(candle, candleIndex, candleIndexOnScreen, candleRect, candleUpShadow, candleBottomShadow, candleMiddleX, lastCandleMiddleX, candleColor, canvas)
        indicatorsWillBeDrawn.forEach {
            if (candleIndexOnScreen == 0){
                return@forEach
            }
            if (it.indicators[candleIndexOnScreen - 1].value == 0.toDouble()){
                return@forEach
            }

            val priceRange        = currentPriceRange[getMaxPrice] - currentPriceRange[getMinPrice]
            val minPrice          = currentPriceRange[getMinPrice]
            val viewHeightRange   = kLineContentViewPort.bottom   - kLineContentViewPort.top

            val lastY             = (kLineContentViewPort.bottom  - (it.indicators[candleIndexOnScreen - 1].value - minPrice) / priceRange * viewHeightRange).toFloat()
            val y                 = (kLineContentViewPort.bottom  - (it.indicators[candleIndexOnScreen].value - minPrice) / priceRange * viewHeightRange).toFloat()

            IndicatorRender.drawLine(lastCandleMiddleX, lastY, candleMiddleX, y, it.color, it.width, canvas)
        }
    }

    fun getTouchedIndicators(touchX: Float, touchY: Float): ArrayList<IndicatorWrapper>?{
        val candleIndexOnScreen = findTouchedCandleIndexOnScreen(touchX, touchY) ?: return null
        return getIndicatorsByIos(candleIndexOnScreen)
    }

    fun getIndicatorsByIos(candleIndexOnScreen: Int): ArrayList<IndicatorWrapper>{
        val wrappers = arrayListOf<IndicatorWrapper>()
        indicatorsWillBeDrawn.forEach {
            if (candleIndexOnScreen >= 0 && candleIndexOnScreen < it.indicators.size){
                val wrapper = IndicatorWrapper()
                wrapper.copyAttrFrom(it)
                wrapper.indicators  = arrayListOf(it.indicators[candleIndexOnScreen])
                wrappers.add(wrapper)
            }
        }

        return wrappers
    }

    fun addMainGraphIndicator(indicatorWrapper: IndicatorWrapper){
        mainGraphIndicatorWrappers.add(indicatorWrapper)
    }

    fun clearMainGraphIndicator(){
        mainGraphIndicatorWrappers.clear()
    }

}