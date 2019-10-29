package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.CallSuper
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.render.AxisRender
import io.trubi.skyline.util.NumberUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Fitz on 2018/3/19.
 *
 * Responsible for drawing axis
 *
 * */
abstract class KAxisView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : KBaseGraphView(context, attrs, defStyleAttr) {

    private val mDateFormat = SimpleDateFormat(defaultSimpleDateFormatPattern, Locale.getDefault())

    override fun onDrawAxis(canvas: Canvas?) {
        super.onDrawAxis(canvas)
        drawYAxis(canvas)
    }

    @CallSuper
    override fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?) {
        val startIndex = endIndex - candlesCountPerScreen
        if ((startIndex + candleIndexOnScreen) % NumberUtil.roundToInt(candlesMaxCountPerScreen / 4.toDouble()) == 0){

            AxisRender.drawAxisLine(candleMiddleX, rootViewport.bottom - xAxisLineHeight, candleMiddleX,
                    rootViewport.top, mFlavor.xAxisColor, borderWidth, canvas, mTypeface)

            var timeFormatPattern = candle.timeFormatPattern
            if (timeFormatPattern.isNullOrEmpty()) {
                timeFormatPattern = defaultSimpleDateFormatPattern
            }

            var xAxisLabel: String
            xAxisLabel = considerConvertTimeDisplay(candle.closeTime)

            if (xAxisLabel.isEmpty()){
                mDateFormat.applyPattern(timeFormatPattern)
                xAxisLabel = mDateFormat.format(Date(candle.closeTime))
            }

            AxisRender.drawAxisTextAlignCenter(xAxisLabel, candleMiddleX, rootViewport.bottom - getTextDescent(xAxisTextSize),
                    xAxisTextSize, mFlavor.xAxisLabelColor, canvas, mTypeface)
        }
    }

    private fun drawYAxis(canvas: Canvas?){
        if (currentPriceRange[getMaxPrice] > 0){
            val scale = (kLineContentViewPort.top - rootViewport.top) / kLineContentViewPort.height()
            val hp = considerConvertPriceDisplay((currentPriceRange[getMaxPrice] * (1 + scale)).toString())
            AxisRender.drawAxisTextAlignRight(hp, kLineContentViewPort.right - ((candleWidth / 2) + candleGap), rootViewport.top + measureTextHeight(yAxisTextSize) - getTextDescent(yAxisTextSize),
                    yAxisTextSize, mFlavor.yAxisLabelColor, canvas, mTypeface)
        }
        if (currentPriceRange[getMinPrice] > 0){
            val scale = (mainGraphViewport.bottom - xAxisLineHeight - kLineContentViewPort.bottom) / kLineContentViewPort.height()
            val lp = considerConvertPriceDisplay((currentPriceRange[getMinPrice] * (1 - scale)).toString())
            AxisRender.drawAxisTextAlignRight(lp, kLineContentViewPort.right - ((candleWidth / 2) + candleGap), mainGraphViewport.bottom - getTextDescent(yAxisTextSize),
                    yAxisTextSize, mFlavor.yAxisLabelColor, canvas, mTypeface)
        }
    }
}