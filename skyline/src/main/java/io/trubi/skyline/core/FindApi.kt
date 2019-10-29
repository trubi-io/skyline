package io.trubi.skyline.core

import io.trubi.skyline.bean.Candle
import io.trubi.skyline.module.CandleRect

/**
 * author       : Fitz Lu
 * created on   : 12/12/2018 17:04
 * description  :
 */
interface FindApi {

    /**
     * Find touched candle index on screen
     * @param touchX
     * @param touchY
     * @return index or null
     * */
    fun findTouchedCandleIndexOnScreen(touchX: Float, touchY: Float): Int?

    /**
     * Find touched candle
     * @param touchX
     * @param touchY
     * @return candle or null
     * */
    fun findTouchedCandle(touchX: Float, touchY: Float): Candle?

    /**
     * Find candle by it`s index on screen
     * @param indexOnScreen
     * @return candle or null
     * */
    fun findCandleByIos(indexOnScreen: Int): Candle?

    /**
     * @param candle
     * @return
     * */
    fun findCandleIndexOnScreen(candle: Candle): Int

    /**
     * @param candle
     * @return
     * */
    fun findCandleLoc(candle: Candle): CandleRect?

    /**
     * Find touched candle drawn area
     * @param touchX
     * @param touchY
     * @return CandleRect or null
     * */
    fun findTouchedCandleRect(touchX: Float, touchY: Float): CandleRect?

    /**
     * Find candle drawn area
     * @param candleIndexOnScreen
     * @return CandleRect or null
     * */
    fun findCandleRectByIos(candleIndexOnScreen: Int): CandleRect?

    /**
     * @param touchY
     * @return
     * */
    fun findTouchedYAxisValue(touchY: Float): Double?
}