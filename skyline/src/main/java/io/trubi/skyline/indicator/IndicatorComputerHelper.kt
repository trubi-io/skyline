package io.trubi.skyline.indicator

import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.bean.Candle

object IndicatorComputerHelper {

    fun getEmaValueByIndex(candles: ArrayList<Candle>, n: Int): ArrayList<Indicator>? {
        if (candles.isEmpty())
            return null

        val indicator = ArrayList<Indicator>()
        var lastEma = candles[0].close

        indicator.add(Indicator(candles[0].closeTime, lastEma))

        for (i in 1 until candles.size) {
            val currentEma = 2 * (candles[i].close - lastEma) / (n + 1) + lastEma
            lastEma = currentEma
            indicator.add(Indicator(candles[i].closeTime, lastEma))
        }

        return indicator
    }

    //for macd
    fun getDifData(candles: ArrayList<Candle>, x: Int, y: Int): ArrayList<Indicator>? {
        if (candles.isEmpty())
            return null

        var cycle = Math.max(x, y)
        if (cycle < 20)
            cycle = 20

        if (cycle > candles.size) {
            return null
        }
        val listX = getEmaValueByIndex(candles, x)
        val listY = getEmaValueByIndex(candles, y)

        val difs = ArrayList<Indicator>()

        var start = cycle
        if (start < 1)
            start = 1
        start--

        if (listX != null && listY != null) {
            for (i in start until candles.size) {
                difs.add(Indicator(0, listX[i].value - listY[i].value))
            }
        }

        return difs
    }

    //for macd
    fun getDemData(list: ArrayList<Candle>, x: Int, y: Int, z: Int): java.util.ArrayList<Indicator>? {

        val difData = getDifData(list, x, y)
        if (difData == null || difData.isEmpty())
            return null

        val data = ArrayList<Indicator>()

        var lastEma = difData[0].value
        var lastDEA = 0.0

        for (i in 1 until difData.size) {
            if (i < z) {
                val currentEma = 2 * (difData[i].value - lastEma) / (z + 1) + lastEma
                lastEma = currentEma
                if (i == z - 1)
                    lastDEA = lastEma
                continue
            } else {
                lastDEA = lastDEA * (z - 1) / (z + 1) + difData[i].value * 2 / (z + 1)
                data.add(Indicator(0, lastDEA))
            }
        }
        return data
    }

    //for kdj
    fun initKDJMaxMin(candles: ArrayList<Candle>, maxIndicators: ArrayList<Indicator>, minIndicators: ArrayList<Indicator>, n: Int) {
        if (candles.size == 0)
            return
        if (n > candles.size) {
            return
        }
        var maxValue: Double
        var minValue = 0.0

        for (i in n - 1 until candles.size) {
            maxValue = 0.0
            for (j in i - n + 1..i) {
                if (j == i - n + 1) {
                    maxValue = candles[j].high
                    minValue = candles[j].low
                } else {
                    if (maxValue < candles[j].high) {
                        maxValue = candles[j].high
                    }
                    if (minValue > candles[j].low) {
                        minValue = candles[j].low
                    }
                }
            }
            maxIndicators.add(Indicator(candles[i].closeTime, maxValue))
            minIndicators.add(Indicator(candles[i].closeTime, minValue))
        }
    }

    /**
     * for RSI
     * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     * LC := REF(CLOSE,1);
     * RSI$1:SMA(MAX(CLOSE-LC,0),N1,1)/SMA(ABS(CLOSE-LC),N1,1)*100;
     */
    fun countRSIdatas(candles: ArrayList<Candle>, days: Int): ArrayList<Indicator>?{
        val values: ArrayList<Indicator> = arrayListOf()

        try {
            if (days > candles.size)
                return null

            var smaMax = 0.0
            var smaAbs = 0.0//默认0
            var lc = 0.0//默认0
            var close = 0.0
            var rsi = 0.0
            for (i in days - 1 until candles.size) {
                val entity = candles[i]
                if (i <= days - 1) {
                    if (i == 0 || i == days - 1) {
                        lc = 0.0
                        close = entity.close

                    } else {
                        lc = candles[i - 1].close
                        close = entity.close
                    }

                } else {
                    lc = candles[i - 1].close
                    close = entity.close
                }
                smaMax = countSMA(Math.max(close - lc, 0.0), days.toDouble(), 1.0, smaMax)
                smaAbs = countSMA(Math.abs(close - lc), days.toDouble(), 1.0, smaAbs)
                rsi = smaMax / smaAbs * 100
                values.add(Indicator(candles[i].closeTime, rsi))
            }

            return values
        }catch (e: Exception){
            Sogger.e("countRSIdatas", e.toString())
            return null
        }
    }

    /**
     * SMA(C,N,M) = (M*C+(N-M)*Y')/N
     *
     * @param c
     * @param n
     * @param m
     * @param sma
     * @return
     */
    private fun countSMA(c: Double, n: Double, m: Double, sma: Double): Double {
        return (m * c + (n - m) * sma) / n
    }

}