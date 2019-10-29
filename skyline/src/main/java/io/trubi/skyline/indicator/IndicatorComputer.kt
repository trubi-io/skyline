package io.trubi.skyline.indicator

import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.bean.Candle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

object IndicatorComputer {

    /**
     * AVG
     * @param candles
     * @param indicatorWrapper
     * @param startIndex
     * @param endIndex
     * */
    fun avg(candles: ArrayList<Candle>, indicatorWrapper: IndicatorWrapper, startIndex: Int, endIndex: Int){
        var totalVolSum = 0.0  // start index ... current index volume sum
        var totalQAVSum = 0.0  // start index ... current index quote asset volume sum

        val indicators =  arrayListOf<Indicator>()

        try{
            candles.withIndex().forEach {
                if (it.index in startIndex..endIndex){

                    totalVolSum += it.value.volume
                    totalQAVSum += it.value.quoteAssetVolume

                    if (it.index == startIndex){
                        indicators.add(Indicator(time = it.value.closeTime, value = it.value.close, isEmpty = false))
                    }
                    else if (it.value.volume == 0.0){
                        val last = indicators.last()
                        indicators.add(Indicator(time = last.time, value = last.value, isEmpty = last.isEmpty))
                    }
                    else{
                        indicators.add(Indicator(time = it.value.closeTime, value = totalQAVSum / totalVolSum, isEmpty = false))
                    }

                }else{
                    indicators.add(Indicator(time = it.value.closeTime, isEmpty = true))
                }
            }

            indicatorWrapper.indicators.clear()
            indicators.mapTo(indicatorWrapper.indicators) { it }
        }catch (e: Exception){
            Sogger.e("avg", e.toString())
        }
    }

    /**
     * SMA
     * @param candles
     * @param indicatorWrapper
     * @param cycle
     * */
    fun sma(candles: ArrayList<Candle>, indicatorWrapper: IndicatorWrapper, cycle: Int) {
        if (cycle < 1 || candles.isEmpty() || cycle > candles.size) {
            return
        }

        try {
            indicatorWrapper.type = Indicator.SMA
            indicatorWrapper.cycle = cycle

            var sum = 0.toDouble()
            val maValues = arrayListOf<Indicator>()

            for (i in cycle - 1 until candles.size) {
                if (i == cycle - 1) {
                    for (j in i - cycle + 1..i) {
                        sum += candles[j].close
                    }
                } else {
                    sum = sum + candles[i].close - candles[i - cycle].close
                }
                maValues.add(Indicator(candles[i].closeTime, sum / cycle))
            }

            makeAlign(maValues, candles)
            maValues.mapTo(indicatorWrapper.indicators) { it }
        }catch (e: Exception){
            Sogger.e("sma", e.toString())
        }
    }

    /**
     * EMA
     * @param candles
     * @param indicatorWrapper
     * @param cycle
     * */
    fun ema(candles: ArrayList<Candle>, indicatorWrapper: IndicatorWrapper, cycle: Int) {
        if (cycle < 1 || candles.isEmpty() || cycle > candles.size)
            return

        try {
            indicatorWrapper.type = Indicator.EMA
            indicatorWrapper.cycle = cycle

            val indicators = ArrayList<Indicator>()
            var lastEma = candles[0].close
            for (i in 1 until candles.size) {
                if (i <= cycle - 1) {
                    val currentEma = 2 * (candles[i].close - lastEma) / (cycle + 1) + lastEma
                    lastEma = currentEma
                    continue
                } else {
                    lastEma += 2 * (candles[i].close - lastEma) / (cycle + 1)
                    indicators.add(Indicator(candles[i].closeTime, lastEma))
                }
            }

            makeAlign(indicators, candles)
            indicators.mapTo(indicatorWrapper.indicators) { it }
        }catch (e: Exception){
            Sogger.e("ema", e.toString())
        }
    }

    /**
     * BOLL
     * @param candles
     * @param middleTrack
     * @param upTrack
     * @param lowerTrack
     * @param cycle boll T
     * @param k     boll K
     * */
    fun boll(candles: ArrayList<Candle>, middleTrack: IndicatorWrapper, upTrack: IndicatorWrapper, lowerTrack: IndicatorWrapper, cycle: Int, k: Int){
        if (cycle < 1 || k < 1 || candles.isEmpty() || cycle > candles.size){
            return
        }

        try {
            upTrack.type = Indicator.BOLL
            middleTrack.type = Indicator.BOLL
            lowerTrack.type = Indicator.BOLL

            upTrack.cycle = cycle
            middleTrack.cycle = cycle
            lowerTrack.cycle = cycle

            var sum = 0.toDouble()
            val maValues = arrayListOf<Indicator>()
            for (i in cycle - 1 until candles.size) {
                if (i == cycle - 1) {
                    for (j in i - cycle + 1..i) {
                        sum += candles[j].close
                    }
                } else {
                    sum = sum + candles[i].close - candles[i - cycle].close
                }
                maValues.add(Indicator(candles[i].closeTime, sum / cycle))
            }

            val upTrackValues = arrayListOf<Indicator>()
            val middleTrackValues = arrayListOf<Indicator>()
            val downTrackValues = arrayListOf<Indicator>()

            var standard: Double
            var squareSum: Double

            for (i in cycle - 1 until candles.size) {
                val smaValue = maValues[i - cycle + 1].value

                standard = 0.0

                for (j in i - cycle + 1..i) {
                    standard += (candles[j].close - smaValue) * (candles[j].close - smaValue)
                }
                squareSum = sqrt(standard / cycle)

                upTrackValues.add(Indicator(candles[i].closeTime, smaValue + squareSum * k))
                middleTrackValues.add(Indicator(candles[i].closeTime, smaValue))
                downTrackValues.add(Indicator(candles[i].closeTime, smaValue - squareSum * k))
            }

            makeAlign(upTrackValues, candles)
            makeAlign(middleTrackValues, candles)
            makeAlign(downTrackValues, candles)

            upTrackValues.mapTo(upTrack.indicators) { it }
            middleTrackValues.mapTo(middleTrack.indicators) { it }
            downTrackValues.mapTo(lowerTrack.indicators) { it }
        }catch (e: Exception){
            Sogger.e("boll", e.toString())
        }
    }

    /**
     * MACD
     * @param candles
     * @param dif
     * @param dem
     * @param stick
     * @param x
     * @param y
     * @param z
     * */
    fun macd(candles: ArrayList<Candle>?, dif: IndicatorWrapper, dem: IndicatorWrapper, stick: IndicatorWrapper, x: Int, y: Int, z: Int) {
        if (candles == null || candles.size == 0)
            return

        try {
            val difData = IndicatorComputerHelper.getDifData(candles, x, y) ?: return
            val demData = IndicatorComputerHelper.getDemData(candles, x, y, z)?: return

            val stickData = arrayListOf<Indicator>()

            if (difData.isNotEmpty() && demData.isNotEmpty()) {
                for (i in demData.indices) {
                    if (i + z >= difData.size) {
                        break
                    }
                    stickData.add(Indicator(0, difData[i + z].value - demData[i].value))
                }
            }

            makeAlign(difData, candles)
            makeAlign(demData, candles)
            makeAlign(stickData, candles)

            difData.mapTo(dif.indicators) {it}
            demData.mapTo(dem.indicators) {it}
            stickData.mapTo(stick.indicators) {it}

        } catch (e: Exception) {
            Sogger.e("macd", e.toString())
        }
    }

    /**
     * KDJ
     * @param candles
     * @param kIndicatorWrapper
     * @param dIndicatorWrapper
     * @param jIndicatorWrapper
     * @param cycle
     * */
    fun kdj(candles: ArrayList<Candle>, kIndicatorWrapper: IndicatorWrapper, dIndicatorWrapper: IndicatorWrapper, jIndicatorWrapper: IndicatorWrapper, cycle: Int){
        try {
            if (candles.size == 0) {
                return
            }
            if (cycle > candles.size) {
                return
            }

            val kValue = ArrayList<Indicator>()
            val dValue = ArrayList<Indicator>()
            val jValue = ArrayList<Indicator>()

            var lastK = 50.0
            var lastD = 50.0

            val maxs = arrayListOf<Indicator>()
            val mins = arrayListOf<Indicator>()

            IndicatorComputerHelper.initKDJMaxMin(candles, maxs, mins, cycle)
            if (maxs.isEmpty() || mins.isEmpty()) {
                return
            }

            var count = 0
            var rsv = 0.0

            for (i in cycle - 1 until candles.size) {
                if (count >= maxs.size)
                    break
                if (count >= mins.size)
                    break

                rsv = if (maxs[count].value - mins[count].value == 0.toDouble()) {
                    0.0
                } else {
                    100 * (candles[i].close - mins.get(count).value) / (maxs.get(count).value - mins.get(count).value)
                }
                val k = lastK * 2 / 3.0 + rsv / 3.0
                val d = lastD * 2 / 3.0 + k / 3.0
                val j = 3 * k - 2 * d
                lastK = k
                lastD = d
                count++
                kValue.add(Indicator(candles[i].closeTime, k))
                dValue.add(Indicator(candles[i].closeTime, d))
                jValue.add(Indicator(candles[i].closeTime, j))
            }

            makeAlign(kValue, candles)
            makeAlign(dValue, candles)
            makeAlign(jValue, candles)

            kIndicatorWrapper.displayName = "K"
            dIndicatorWrapper.displayName = "D"
            jIndicatorWrapper.displayName = "J"

            kValue.mapTo(kIndicatorWrapper.indicators) {it}
            dValue.mapTo(dIndicatorWrapper.indicators) {it}
            jValue.mapTo(jIndicatorWrapper.indicators) {it}
        }catch (e: Exception){
            Sogger.e("kdj", e.toString())
        }
    }

    /**
     * RSI
     * @param candles
     * @param xIndicatorWrapper
     * @param yIndicatorWrapper
     * @param zIndicatorWrapper
     * @param x
     * @param y
     * @param z
     * */
    fun rsi(candles: ArrayList<Candle>, xIndicatorWrapper: IndicatorWrapper, yIndicatorWrapper: IndicatorWrapper, zIndicatorWrapper: IndicatorWrapper, x: Int, y: Int, z: Int) {
        try {
            val listXLine = IndicatorComputerHelper.countRSIdatas(candles, x) ?: return
            val listYLine = IndicatorComputerHelper.countRSIdatas(candles, y) ?: return
            val listZLine = IndicatorComputerHelper.countRSIdatas(candles, z) ?: return

            makeAlign(listXLine, candles)
            makeAlign(listYLine, candles)
            makeAlign(listZLine, candles)

            xIndicatorWrapper.displayName = "RSI$x"
            yIndicatorWrapper.displayName = "RSI$y"
            zIndicatorWrapper.displayName = "RSI$z"

            listXLine.mapTo(xIndicatorWrapper.indicators) { it }
            listYLine.mapTo(yIndicatorWrapper.indicators) { it }
            listZLine.mapTo(zIndicatorWrapper.indicators) { it }
        }catch (e: Exception){
            Sogger.e("rsi", e.toString())
        }
    }

    /**
     * Williams %R, or just %R, is a technical analysis oscillator showing
     * the current closing price in relation to the high and low of the
     * past N days (for a given N).The oscillator is on a negative scale,
     * from −100 (lowest) up to 0 (highest), obverse of the more common 0 to
     * 100 scale found in many Technical Analysis oscillators. A value of −100
     * means the close today was the lowest low of the past N days, and 0 means
     * today's close was the highest high of the past N days. (Although
     * sometimes the %R is adjusted by adding 100.)
     * @param candles
     * @param n
     * @param wrIndicatorWrapper
     * */
    fun wr(candles: ArrayList<Candle>, wrIndicatorWrapper: IndicatorWrapper, n: Int){
        try{
            if (candles.size < n){
                return
            }

            val lookBack = ArrayDeque<Double>()
            val indicators = arrayListOf<Indicator>()
            var low: Double = 1e15
            var high: Double = -1e15

            candles.withIndex().forEach { indexedValue ->
                val close = indexedValue.value.close

                lookBack.addFirst(close)
                high = Math.max(high, close)
                low  = Math.min(low, close)

                if (lookBack.size > n){
                    //Pop the last value
                    val lv = lookBack.removeLast()

                    if (lv >= high || lv <= low){
                        low = 1e15
                        high = -1e15

                        lookBack.forEach {
                            high = Math.max(high, it)
                            low  = Math.min(low, it)
                        }
                    }

                    val r = if (high == low){
                        100.0
                    }else{
                        100 * (close - low) / (high - low)
                    }
                    indicators.add(Indicator(time = indexedValue.value.closeTime, value = r))

                    Sogger.i(javaClass.name, r.toString())
                }
            }

            makeAlign(indicators, candles)
            indicators.mapTo(wrIndicatorWrapper.indicators) {it}
            wrIndicatorWrapper.displayName = "%R($n)"
            wrIndicatorWrapper.cycle = n
        }catch (e: Exception){
            Sogger.e("wr", e.toString())
        }
    }

    /**
     * Make indicator size same as mCandles size
     * @param indicators
     * @param candles
     * */
    private fun makeAlign(indicators: ArrayList<Indicator>, candles: ArrayList<Candle>){
        if (indicators.size < candles.size){
            val sub = candles.size - indicators.size
            for (i in 0 until sub){
                indicators.add(0, Indicator(0, 0.0, true))
            }
        }
    }

}