package io.trubi.skyline.subgraph

import android.graphics.Canvas
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.indicator.IndicatorWrapper

interface SubGraphDrawer {

    fun getTouchedIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper>

    fun beforeDrawCandles(canvas: Canvas?)

    fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?)

    fun afterDrawCandles(canvas: Canvas?)

}