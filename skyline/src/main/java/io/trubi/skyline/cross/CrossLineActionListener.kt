package io.trubi.skyline.cross

import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.bean.Candle

interface CrossLineActionListener {

    fun onCrossLineShow(candle: Candle, candleRect: CandleRect, indicatorWrappers: ArrayList<IndicatorWrapper>?,
                        subIndicatorWrappers: ArrayList<IndicatorWrapper>?, previousCandle: Candle?)

    fun onCrossLineMoved(candle: Candle, candleRect: CandleRect, indicatorWrappers: ArrayList<IndicatorWrapper>?,
                         subIndicatorWrappers: ArrayList<IndicatorWrapper>?, previousCandle: Candle?)

    fun onCrossLineDismiss()

    fun onLostFocus()

}