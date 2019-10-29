package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.CallSuper
import io.trubi.skyline.R
import io.trubi.skyline.module.Viewport
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.flavor.FlavorDescription
import io.trubi.skyline.flavor.FlavorFactory
import io.trubi.skyline.module.PointF
import io.trubi.skyline.render.BorderRender
import io.trubi.skyline.util.NumberUtil
import io.trubi.skyline.render.AxisValueRender
import java.util.*

/**
 * Created by Fitz on 2018/3/20.
 *
 */
abstract class KBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HelperView(context, attrs, defStyleAttr) {

    protected val defaultSimpleDateFormatPattern = "MM-dd HH:mm"

    protected val modeCandle = "candle"
    protected val modeLine   = "line"

    protected var mFlavor  : FlavorDescription = FlavorFactory.default()

    private val mainGraphWeight = 3f
    private val subGraphWeight  = 1f

    protected var viewMode               = modeCandle

    /**
     * View port of the whole graph
     * */
    protected val rootViewport           = Viewport()
    /**
     * Main graph
     * */
    protected val mainGraphViewport      = Viewport()
    /**
     * Sub graph
     * */
    protected val subGraphViewport       = Viewport()

    protected val defaultBorderWidth     = context.resources.getDimension(R.dimen.prom_default_border_width)
    protected val defaultCandleWidth     = context.resources.getDimension(R.dimen.prom_default_candle_width)
    protected val defaultCandleGap       = context.resources.getDimension(R.dimen.prom_default_candle_gap)
    protected val defaultShadowLineWidth = context.resources.getDimension(R.dimen.prom_default_shadow_line_width)
    protected val defaultTimeWidth       = context.resources.getDimension(R.dimen.prom_default_time_line_width)

    protected val defaultTextSize        = context.resources.getDimension(R.dimen.prom_default_axis_label_text_size)

    protected var xAxisTextSize          = defaultTextSize
    protected var yAxisTextSize          = defaultTextSize
    protected var borderWidth            = defaultBorderWidth
    protected var candleWidth            = defaultCandleWidth
    protected var maxCandleWidth         = defaultCandleWidth * 4f
    protected var minCandleWidth         = defaultCandleWidth / 8f
    protected var candleGap              = defaultCandleGap
    protected var shadowLineWidth        = defaultShadowLineWidth
    protected var commonGap              = context.resources.getDimension(R.dimen.prom_default_common_gap)

    protected var timeLineWidth          = defaultTimeWidth

    //Candles metadata
    protected var mCandles               = arrayListOf<Candle>()

    //How many candles can be drawn on screen
    protected var candlesMaxCountPerScreen         = 0
    //How many candles will draw on screen
    protected var candlesCountPerScreen            = 0
    //The first candle`s index on screen
    protected var startIndex                       = 0
    //The last candle`s index on screen
    protected var endIndex                         = 0
    //Candles will be drawn on screen
    protected var candlesWillBeDrawn               = arrayListOf<Candle>()
    //Offset candles to align the end of view
    protected var leftOffsetGap                    = 0f

    protected var yValueDecimalTickSize            = 2

    protected var mXAxisValueRender: AxisValueRender? = null
    protected var mYAxisValueRender: AxisValueRender? = null

    protected var xAxisLineHeight                   = 0f
    protected var yAxisLineHeight                   = 0f

    protected var mTypeface: Typeface?              = null

    var zoomYMinScale                               = 0f
    var zoomYMaxScale                               = 0.8f
    //Range [0...1)
    var zoomYScale                                  = 0f

    var zoomYEnable                                 = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        xAxisLineHeight = measureTextLineHeight(xAxisTextSize)
        yAxisLineHeight = measureTextLineHeight(yAxisTextSize)

        rootViewport.left       = paddingStart.toFloat()
        rootViewport.top        = paddingTop.toFloat()
        rootViewport.bottom     = paddingTop.toFloat() + mHeight
        rootViewport.right      = rootViewport.left + mWidth - borderWidth

        mainGraphViewport.left   = rootViewport.left
        mainGraphViewport.top    = rootViewport.top
        mainGraphViewport.bottom = rootViewport.top + rootViewport.height() * (mainGraphWeight / (mainGraphWeight + subGraphWeight)) - borderWidth
        mainGraphViewport.right  = rootViewport.right

        subGraphViewport.left    = rootViewport.left
        subGraphViewport.top     = mainGraphViewport.bottom + borderWidth
        subGraphViewport.bottom  = rootViewport.bottom - xAxisLineHeight
        subGraphViewport.right   = rootViewport.right
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        updateConfig()
        onCompute()
        onDrawBorder(canvas)
        onDrawAxis(canvas)
        beforeDrawCandles(canvas)
        onDrawCandles(canvas)
        afterDrawCandles(canvas)
    }

    /**
     * Check any configs has been changed and update them
     * */
    @CallSuper
    open fun updateConfig(){

    }

    /**
     * compute data
     * */
    @CallSuper
    open fun onCompute(){}

    /**
     * draw border
     * */
    @CallSuper
    open fun onDrawBorder(canvas: Canvas?){ drawBaseBorder(canvas) }

    /**
     * draw axis
     * */
    @CallSuper
    open fun onDrawAxis(canvas: Canvas?){}

    /**
     * before draw mCandles
     * */
    @CallSuper
    open fun beforeDrawCandles(canvas: Canvas?){}

    /**
     * on draw mCandles
     * */
    @CallSuper
    open fun onDrawCandles(canvas: Canvas?){}

    /**
     * after draw mCandles
     * */
    @CallSuper
    open fun afterDrawCandles(canvas: Canvas?){}

    private fun drawBaseBorder(canvas: Canvas?){
        BorderRender.drawBorder(rootViewport, mFlavor.borderColor, borderWidth, canvas)
        BorderRender.drawBorder(subGraphViewport, mFlavor.borderColor, borderWidth, canvas, false, true, false, false)
    }

    fun considerConvertPriceDisplay(p: String): String{
        return mYAxisValueRender?.render(p)?:NumberUtil.format(p, yValueDecimalTickSize)
    }

    fun considerConvertTimeDisplay(p: Long): String{
        return mXAxisValueRender?.render(p.toString())?:""
    }

    fun setTypeFace(tp: Typeface){
        mTypeface = tp
    }

    fun setCandles(data: ArrayList<Candle>){
        mCandles = data
    }

    fun setXAxisValueRender(render: AxisValueRender?){
        mXAxisValueRender = render
    }

    fun setYAxisValueRender(render: AxisValueRender?){
        mYAxisValueRender = render
    }

    fun modeLine(){
        viewMode = modeLine
    }

    fun modeCandle(){
        viewMode = modeCandle
    }

    /**
     * Move graph
     * @param distance
     * */
    fun moveX(distance: Float): Int{
        val moveFactor = distance / (candleWidth * 1f)
        val moveUnit   = NumberUtil.roundToInt(moveFactor.toDouble())
        endIndex -= moveUnit

        ensureCandleWillBeDrawnEndIndex()
        postInvalidate()

        return moveUnit
    }

    private var deltaOffset = 0f
    /**
     * Zoom X
     * Any one can override this method
     * @param scale scale factor
     * @param zoomMiddlePoint zoom middle point
     * */
    open fun zoomX(scale: Float, zoomMiddlePoint: PointF){
        candleWidth *= scale
        if (candleWidth > maxCandleWidth){
            candleWidth = maxCandleWidth
        }
        if (candleWidth < minCandleWidth){
            candleWidth = minCandleWidth
        }

        ensureCandleWillBeDrawnEndIndex()
        postInvalidate()
    }


    /**
     * Zoom Y
     * Any one can override this method
     * @param scale
     * */
    open fun zoomY(scale: Float){
        if (zoomYEnable) {
            zoomYScale += (1f - scale)
            if (zoomYScale < zoomYMinScale) {
                zoomYScale = zoomYMinScale
            }
            if (zoomYScale > zoomYMaxScale) {
                zoomYScale = zoomYMaxScale
            }
            postInvalidate()
        }
    }

    private fun ensureCandleWillBeDrawnEndIndex(){
        //不能超过总蜡烛数
        endIndex = Math.min(endIndex, mCandles.size - 1)
        //不能小于0
        endIndex = Math.max(0, endIndex)
        //数据不满一屏时，取实际末尾索引
        endIndex = Math.max(endIndex, candlesCountPerScreen - 1)
    }

    fun setFlavor(newFlavor: FlavorDescription){
        mFlavor = newFlavor
    }

    fun getFlavor() = mFlavor

    fun getAllCandles()            = mCandles

    fun getCandlesBeDrawn()        = candlesWillBeDrawn

    /**
     * Get current [candleWidth] value
     * */
    fun getCurCandleWidth()        = candleWidth

    /**
     * Get current [candleGap] value
     * */
    fun getCurCandleGap()          = candleGap

    /**
     * Get current [leftOffsetGap] value
     * */
    fun getCurCandlesOffset()      = leftOffsetGap

    /**
     * Get current [xAxisTextSize] value
     * */
    fun getCurXAxisTextSize()      = xAxisTextSize

    /**
     * Get current [yAxisTextSize] value
     * */
    fun getCurYAxisTextSize()      = yAxisTextSize

    /**
     * Get last candle index in [mCandles]
     * */
    fun getLastCandleIndex()       = mCandles.size - 1

    /**
     * Get current [startIndex]
     * */
    fun getCurStartIndex() = startIndex

    /**
     * Get current [endIndex]
     * */
    fun getCurEndIndex()   = endIndex

    /**
     * Get current [mTypeface]
     * */
    fun getTypeface()           = mTypeface

    /**
     * Move [endIndex] to the last candle
     * */
    fun moveToEnd(){
        endIndex = mCandles.size - 1
    }

    /**
     * Check current graph is on the end,
     * means last candle is drawn on screen
     * */
    fun isOnEnd() = getCurEndIndex() == getLastCandleIndex()
}