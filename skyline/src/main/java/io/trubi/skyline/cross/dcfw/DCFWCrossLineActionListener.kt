package io.trubi.skyline.cross.dcfw

import io.trubi.skyline.bean.Candle
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.module.CandleRect

/**
 * author       : Fitz Lu
 * created on   : 12/12/2018 14:16
 * description  :
 */
interface DCFWCrossLineActionListener {

    fun onShowCrossLine(candle: Candle, candleRect: CandleRect, previousCandle: Candle?,
                        indicatorWrappers: ArrayList<IndicatorWrapper>?,
                        subIndicatorWrappers: ArrayList<IndicatorWrapper>?,
                        touchYCoordinate: Float, touchYAxisValue: Double?)

    fun onMoveCrossLine(candle: Candle, candleRect: CandleRect, previousCandle: Candle?,
                        indicatorWrappers: ArrayList<IndicatorWrapper>?,
                        subIndicatorWrappers: ArrayList<IndicatorWrapper>?,
                        touchYCoordinate: Float, touchYAxisValue: Double?)

    fun onDismissCrossLine()

    fun onLostFocus()

}