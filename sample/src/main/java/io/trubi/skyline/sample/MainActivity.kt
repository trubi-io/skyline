package io.trubi.skyline.sample

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.trubi.skyline.Skyline
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.indicator.IndicatorWrapper
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.cross.dcfw.DCFWCrossLineActionListener
import io.trubi.skyline.cross.dcfw.window.DCFWFloatWindow
import io.trubi.skyline.cross.dcfw.window.Label
import io.trubi.skyline.indicator.IndicatorComputer
import io.trubi.skyline.subgraph.MacdDrawer
import io.trubi.skyline.touch.dcfw.DCFWKToucher
import io.trubi.skyline.util.NumberUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val inputString = assets.open("btcusdt.txt").bufferedReader().use { it.readText() }
        val candles = convertKLineData(inputString)
        kView?.addToucher(DCFWKToucher().also {
            it.setCrossLineActionListener(object : DCFWCrossLineActionListener {

                override fun onLostFocus() {

                }

                override fun onShowCrossLine(candle: Candle, candleRect: CandleRect, previousCandle: Candle?,
                                             indicatorWrappers: ArrayList<IndicatorWrapper>?,
                                             subIndicatorWrappers: ArrayList<IndicatorWrapper>?,
                                             touchYCoordinate: Float, touchYAxisValue: Double?) {
                    crossLine?.visibility = View.VISIBLE
                    updateCrossLine(candle, candleRect, previousCandle, indicatorWrappers,
                            subIndicatorWrappers, touchYCoordinate, touchYAxisValue)
                }

                override fun onMoveCrossLine(candle: Candle, candleRect: CandleRect, previousCandle: Candle?, indicatorWrappers: ArrayList<IndicatorWrapper>?, subIndicatorWrappers: ArrayList<IndicatorWrapper>?, touchYCoordinate: Float, touchYAxisValue: Double?) {
                    crossLine?.visibility = View.VISIBLE
                    updateCrossLine(candle, candleRect, previousCandle, indicatorWrappers,
                            subIndicatorWrappers, touchYCoordinate, touchYAxisValue)
                }

                override fun onDismissCrossLine() {
                    crossLine?.visibility = View.GONE
                }

            })
        })
        kView?.setCandles(candles)

        kView?.zoomYScale = 0f
        kView?.zoomYEnable = true


        //show sma in main graph
        kView?.clearMainGraphIndicator()
        defaultSma(this@MainActivity, kView!!, candles)

        //show macd in sub graph
        val dif = IndicatorWrapper()
        dif.color = Color.parseColor("#20acea")
        dif.width = 2f

        val dem = IndicatorWrapper()
        dem.color = Color.parseColor("#ff79d4")
        dem.width = 2f

        val stick = IndicatorWrapper()
        IndicatorComputer.macd(candles, dif, dem, stick, 12, 26, 9)
        val macdDrawer = MacdDrawer(kView!!, dif, dem, stick)

        kView?.setSubGraphDrawer(macdDrawer)

        kView?.modeCandle()

        //move to the end
        kView?.moveToEnd()
        //invalidate
        kView?.invalidateWithAnimation()
    }

    private fun convertKLineData(data: String): ArrayList<Candle> {
        val jsonArray = JSONArray(data)
        val candles = arrayListOf<Candle>()

        for (i in 0 until jsonArray.length()) {
            try {
                val jsonObjArr = jsonArray.getJSONArray(i)
                val entity = Candle()
                entity.closeTime = jsonObjArr.getLong(0)
                entity.open = jsonObjArr.getDouble(1)
                entity.high = jsonObjArr.getDouble(2)
                entity.low = jsonObjArr.getDouble(3)
                entity.close = jsonObjArr.getDouble(4)
                entity.volume = jsonObjArr.getDouble(5)
                entity.quoteAssetVolume = jsonObjArr.getDouble(7)
                entity.timeFormatPattern = "MM-dd HH:mm"
                candles.add(entity)
            } catch (e: Exception) {
                Log.e("foreach", e.toString())
            }
        }

        return candles
    }

    private val mSimpleDateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

    private fun updateCrossLine(candle: Candle, candleRect: CandleRect, previousCandle: Candle?,
                                indicatorWrappers: ArrayList<IndicatorWrapper>?, subIndicatorWrappers: ArrayList<IndicatorWrapper>?,
                                touchYCoordinate: Float, touchXAxisValue: Double?) {

        if (touchXAxisValue != null) {
            crossLine?.updateYAxisStr(NumberUtil.format(touchXAxisValue, 2))
        } else {
            crossLine?.updateYAxisStr("")
        }

        if (kView != null) {
            crossLine?.updateMainTitleArea(kView!!.getKLineTitleArea())
            crossLine?.updateSubTitleArea(kView!!.getterSubGraphTitleArea())
        }

        mSimpleDateFormat.applyPattern(candle.timeFormatPattern)
        val timeFormat: String = mSimpleDateFormat.format(candle.closeTime)

        crossLine?.updateXAxisStr(timeFormat)

        crossLine?.updateCandleRect(candle, candleRect)
        crossLine?.updateYCoordinate(touchYCoordinate)

        crossLine?.clearMainTitles()
        indicatorWrappers?.forEach {
            if (it.indicators.size > 0) {
                if (!it.indicators[0].isEmpty) {
                    crossLine?.addMainTitle("${it.displayName}:${NumberUtil.format(it.indicators[0].value, 2)}", it.color)
                }
            }
        }

        crossLine?.clearSubTitles()
        subIndicatorWrappers?.forEach {
            if (it.indicators.size > 0) {
                if (!it.indicators[0].isEmpty) {
                    crossLine?.addSubTitle("${it.displayName}:${NumberUtil.format(it.indicators[0].value, 2)}", it.color)
                }
            }
        }

        val dcfwFloatWindow = DCFWFloatWindow()
        dcfwFloatWindow.addContent(
                left = Label(text = timeFormat, color = Color.parseColor("#959EB1"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label())

        dcfwFloatWindow.addContent(
                left = Label(text = "Open", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = NumberUtil.format(candle.open, 2), color = Color.parseColor("#242D3D"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "High", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = NumberUtil.format(candle.high, 2), color = Color.parseColor("#242D3D"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "Low", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = NumberUtil.format(candle.low, 2), color = Color.parseColor("#242D3D"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "Close", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = NumberUtil.format(candle.close, 2), color = Color.parseColor("#242D3D"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "Chg", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = "1.3400", color = Color.parseColor("#E50370"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "%Chg", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = "-10.35%", color = Color.parseColor("#E50370"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.addContent(
                left = Label(text = "Vol", color = Color.parseColor("#5A667C"), textSize = resources.getDimension(R.dimen.prom_text_size_12)),
                right = Label(text = NumberUtil.format(candle.low, 2), color = Color.parseColor("#242D3D"), textSize = resources.getDimension(R.dimen.prom_text_size_12)))

        dcfwFloatWindow.backgroundColor = Color.parseColor("#F9FBFC")
        dcfwFloatWindow.backgroundBorderColor = Color.parseColor("#D4DCED")
        dcfwFloatWindow.columnGap = resources.getDimension(R.dimen.prom_default_candle_width)
        dcfwFloatWindow.lineSpaceExtra = resources.getDimension(R.dimen.prom_default_common_gap)
        dcfwFloatWindow.setPadding(resources.getDimension(R.dimen.prom_8dp))
        dcfwFloatWindow.leftMargin = resources.getDimension(R.dimen.prom_12dp)
        dcfwFloatWindow.rightMargin = resources.getDimension(R.dimen.prom_12dp)
        dcfwFloatWindow.corner = resources.getDimension(R.dimen.prom_default_common_gap)

        crossLine?.setFloatWindow(dcfwFloatWindow)

        crossLine?.postInvalidate()
    }

    /**
     * Default sma indicator lines
     * Cycle 7
     * Cycle 25
     * Cycle 99
     * @param context
     * @param skyline
     * @param candles
     * */
    private fun defaultSma(context: Context, skyline: Skyline?, candles: ArrayList<Candle>) {
        val sma7 = IndicatorWrapper()
        sma7.width = context.resources.getDimension(R.dimen.prom_1dp)
        sma7.color = Color.parseColor("#f0c706")
        sma7.displayName = "SMA(7)"
        IndicatorComputer.sma(candles, sma7, 7)

        val sma25 = IndicatorWrapper()
        sma25.width = context.resources.getDimension(R.dimen.prom_1dp)
        sma25.color = Color.parseColor("#20acea")
        sma25.displayName = "SMA(25)"
        IndicatorComputer.sma(candles, sma25, 25)

        val sma99 = IndicatorWrapper()
        sma99.width = context.resources.getDimension(R.dimen.prom_1dp)
        sma99.color = Color.parseColor("#ff79d4")
        sma99.displayName = "SMA(99)"
        IndicatorComputer.sma(candles, sma99, 99)

        skyline?.clearMainGraphIndicator()
        skyline?.addMainGraphIndicator(sma7)
        skyline?.addMainGraphIndicator(sma25)
    }
}
