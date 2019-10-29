package io.trubi.skyline.core

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.CallSuper
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.module.Viewport
import io.trubi.skyline.dev.Sogger
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.module.PointF
import io.trubi.skyline.util.NumberUtil

/**
 * Created by Fitz on 2018/3/19.
 * */
abstract class KBaseGraphView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : KBaseView(context, attrs, defStyleAttr), FindApi {

    //Used to limit whole k line view border
    protected val kLineViewport                 = Viewport()
    //Used to limit mCandles content area inner k line view
    protected val kLineContentViewPort          = Viewport()

    //Max price and min price current screen
    protected var currentPriceRange = arrayOf(0.0, 0.0)
    protected val getMinPrice       = 0
    protected val getMaxPrice       = 1

    protected var mHighestPrice      = 0.toDouble()
    protected var mLowestPrice       = 0.toDouble()
    protected var mHighestCandleTopPoint         = PointF()
    protected var mLowestCandleIndexBottomPoint  = PointF()
    protected var mHighestCandleIndex = 0
    protected var mLowestCandleIndex  = 0

    //RectF help to draw candle and it`s shadow line
    private val mCandleRect         = CandleRect()
    private val mCandleUpShadow     = CandleRect()
    private val mCandleBottomShadow = CandleRect()

    /**
     * Call when drawing single candle in mCandles list
     * @param candle                current candle be drawn
     * @param candleIndex           current candle index in whole data list
     * @param candleIndexOnScreen   current candle index on screen
     * @param candleRect            current candle drawn area on screen
     * @param candleUpShadow        current candle up shadow drawn area on screen
     * @param candleBottomShadow    current candle bottom shadow drawn area on screen
     * @param candleMiddleX         current candle drawn area middle X coordinate
     * @param lastCandleMiddleX     last candle drawn area middle X coordinate
     * @param candleColor           current candle color
     * @param canvas                canvas
     * */
    abstract fun onDrawEachCandle(candle: Candle, candleIndex: Int, candleIndexOnScreen: Int, candleRect: CandleRect, candleUpShadow: CandleRect, candleBottomShadow: CandleRect, candleMiddleX: Float, lastCandleMiddleX: Float, candleColor: Int, canvas: Canvas?)

    /**
     * Call on compute price range
     * */
    @CallSuper
    open fun onComputePriceRange(){}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        kLineViewport.left   = mainGraphViewport.left
        kLineViewport.right  = mainGraphViewport.right
        kLineViewport.top    = mainGraphViewport.top + yAxisLineHeight * 2
        kLineViewport.bottom = mainGraphViewport.bottom - yAxisLineHeight * 2

        updateKLineContentArea()
    }

    override fun onCompute() {
        super.onCompute()
        resetComputer()
        computeCandleDrawnCount()
        computeDrawnOffset()
        computeCandleDrawnIndex()
        pickDrawnCandles()
        computeCurrentPriceRange()
    }

    override fun updateConfig() {
        super.updateConfig()
        updateKLineContentArea()
    }

    override fun onDrawCandles(canvas: Canvas?) {
        super.onDrawCandles(canvas)
        iteratorCandles(canvas)
    }

    private fun updateKLineContentArea(){
        kLineContentViewPort.left   = kLineViewport.left
        kLineContentViewPort.right  = kLineViewport.right
        val maxZoomOutHeight        = kLineViewport.height() / 2f
        kLineContentViewPort.top    = kLineViewport.top + maxZoomOutHeight * zoomYScale
        kLineContentViewPort.bottom = kLineViewport.bottom - maxZoomOutHeight * zoomYScale
    }

    private fun resetComputer(){
        mHighestCandleTopPoint         = PointF()
        mLowestCandleIndexBottomPoint  = PointF()
        mHighestPrice                  = 0.toDouble()
        mLowestPrice                   = 0.toDouble()
        mHighestCandleIndex            = 0
        mLowestCandleIndex             = 0
        leftOffsetGap                  = 0f
    }

    /**
     * Compute how many mCandles will be drawn on current screen
     * */
    abstract fun computeCandleDrawnCount()

    protected fun computeDrawnOffset(){
        if (candlesCountPerScreen == candlesMaxCountPerScreen && mCandles.size > candlesMaxCountPerScreen){
            val totalWidth = candlesCountPerScreen * (candleWidth + candleGap)
            leftOffsetGap = Math.max(measuredWidth - totalWidth, 0f)
            Sogger.w("watchoffset", "offset is $leftOffsetGap")
        }
    }

    /**
     * Compute the first candle`s index drawn on screen and the last one
     * */
    private fun computeCandleDrawnIndex() {
        if (mCandles.isNotEmpty()) {
            if (endIndex == 0) {
                endIndex = getLastCandleIndex()
            }
            if (endIndex < 0) {
                endIndex = 0
            }

            startIndex = Math.max(0, endIndex - candlesCountPerScreen + 1)

            if (startIndex > endIndex){
                startIndex = endIndex
            }
        }
    }

    /**
     * Pick the mCandles will be drawn
     * */
    private fun pickDrawnCandles(){
        candlesWillBeDrawn.clear()
        if (mCandles.isNotEmpty()) {
            mCandles.subList(startIndex, endIndex + 1).forEach {
                candlesWillBeDrawn.add(it)
            }
        }
    }

    /**
     * Compute the max price and min price on current screen
     * */
    private fun computeCurrentPriceRange(){
        if (candlesWillBeDrawn.isNotEmpty()) {
            currentPriceRange[getMinPrice] = candlesWillBeDrawn.minBy { it.low  }?.low?:0.toDouble()
            currentPriceRange[getMaxPrice] = candlesWillBeDrawn.maxBy { it.high }?.high?:0.toDouble()
        }
        onComputePriceRange()
    }

    /**
     * This is the core method
     * Iterator each mCandles, dispatch data
     * */
    private fun iteratorCandles(canvas: Canvas?){
        val priceRange      = currentPriceRange[getMaxPrice] - currentPriceRange[getMinPrice]
        val viewHeightRange = kLineContentViewPort.bottom - kLineContentViewPort.top

        var lastCandleMiddleX = 0f
        candlesWillBeDrawn.withIndex().forEach {
            mCandleRect.reset()
            mCandleUpShadow.reset()
            mCandleBottomShadow.reset()

            val candleColor: Int

            mCandleRect.left   = (it.index * (candleWidth + candleGap)) + leftOffsetGap
            mCandleRect.right  = mCandleRect.left + candleWidth

            when{
                it.value.open <= it.value.close  -> {
                    //increase
                    mCandleRect.top            = (kLineContentViewPort.bottom - (it.value.close - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleRect.bottom         = (kLineContentViewPort.bottom - (it.value.open - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    if (mCandleRect.bottom - mCandleRect.top < 1f){
                        mCandleRect.top = mCandleRect.bottom - 1f
                    }

                    mCandleRect.close          = mCandleRect.top
                    mCandleRect.open           = mCandleRect.bottom

                    mCandleUpShadow.top        = (kLineContentViewPort.bottom - (it.value.high - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleUpShadow.bottom     = (kLineContentViewPort.bottom - (it.value.close - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    mCandleBottomShadow.top    = (kLineContentViewPort.bottom - (it.value.open - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleBottomShadow.bottom = (kLineContentViewPort.bottom - (it.value.low - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    candleColor = mFlavor.increasingColor
                }
                else                             -> {
                    //decrease
                    mCandleRect.top            = (kLineContentViewPort.bottom - (it.value.open - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleRect.bottom         = (kLineContentViewPort.bottom - (it.value.close - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    if (mCandleRect.bottom - mCandleRect.top < 1f){
                        mCandleRect.top = mCandleRect.bottom + 1f
                    }

                    mCandleRect.open           = mCandleRect.top
                    mCandleRect.close          = mCandleRect.bottom

                    mCandleUpShadow.top        = (kLineContentViewPort.bottom - (it.value.high - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleUpShadow.bottom     = (kLineContentViewPort.bottom - (it.value.open - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    mCandleBottomShadow.top    = (kLineContentViewPort.bottom - (it.value.close - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
                    mCandleBottomShadow.bottom = (kLineContentViewPort.bottom - (it.value.low - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

                    candleColor = mFlavor.decreasingColor
                }
            }

            val candleMiddle         = mCandleRect.left + candleWidth / 2f
            mCandleUpShadow.left      = candleMiddle - shadowLineWidth / 2
            mCandleUpShadow.right     = candleMiddle + shadowLineWidth / 2
            mCandleBottomShadow.left  = candleMiddle - shadowLineWidth / 2
            mCandleBottomShadow.right = candleMiddle + shadowLineWidth / 2

            //pick high low price

            if (mHighestPrice <= 0){
                mHighestPrice = it.value.high
                mHighestCandleTopPoint.x = candleMiddle
                mHighestCandleTopPoint.y = mCandleUpShadow.top
                mHighestCandleIndex = it.index
            }
            if (mLowestPrice <= 0){
                mLowestPrice = it.value.low
                mLowestCandleIndexBottomPoint.x = candleMiddle
                mLowestCandleIndexBottomPoint.y = mCandleBottomShadow.bottom
                mLowestCandleIndex = it.index
            }

            if (mHighestPrice < it.value.high){
                mHighestPrice = it.value.high
                mHighestCandleTopPoint.x = candleMiddle
                mHighestCandleTopPoint.y = mCandleUpShadow.top
                mHighestCandleIndex = it.index
            }

            if (mLowestPrice > it.value.low){
                mLowestPrice = it.value.low
                mLowestCandleIndexBottomPoint.x = candleMiddle
                mLowestCandleIndexBottomPoint.y = mCandleBottomShadow.bottom
                mLowestCandleIndex = it.index
            }

            onDrawEachCandle(it.value, it.index + startIndex, it.index, mCandleRect,
                    mCandleUpShadow, mCandleBottomShadow, candleMiddle, lastCandleMiddleX, candleColor, canvas)

            lastCandleMiddleX = candleMiddle
        }
    }

    override fun findTouchedCandleIndexOnScreen(touchX: Float, touchY: Float): Int? {
        if (touchY < rootViewport.top || touchY > rootViewport.bottom || touchX < rootViewport.left || touchX > rootViewport.right) {
            Sogger.w("findTouchedCandleIndexOnScreen", "touch outside main graph view")
            return null
        }
        val touchToStart = touchX - rootViewport.left - leftOffsetGap
        val candleIndexOnScreen = NumberUtil.roundToInt(touchToStart.toDouble() / (candleWidth + candleGap)) - 1
        if (candleIndexOnScreen >= candlesWillBeDrawn.size || candleIndexOnScreen < 0) {
            Sogger.w("findTouchedCandleIndexOnScreen", "touch outside kLine mCandles current screen")
            return null
        }

        return candleIndexOnScreen
    }

    override fun findTouchedCandle(touchX: Float, touchY: Float): Candle?{
        val candleIndexOnScreen = findTouchedCandleIndexOnScreen(touchX, touchY) ?: return null
        return candlesWillBeDrawn[candleIndexOnScreen]
    }

    override fun findCandleByIos(indexOnScreen: Int): Candle? {
        if (!iosSafeCheck(indexOnScreen)){
            return null
        }
        return candlesWillBeDrawn[indexOnScreen]
    }

    /**
     * Check index on screen is in [candlesWillBeDrawn] bound
     * {ios} means index in [candlesWillBeDrawn] on screen
     * */
    fun iosSafeCheck(indexOnScreen: Int): Boolean {
        return indexOnScreen >= 0 && indexOnScreen < candlesWillBeDrawn.size
    }

    override fun findCandleIndexOnScreen(candle: Candle): Int {
        return candlesWillBeDrawn.indexOfFirst { it.closeTime == candle.closeTime }
    }

    fun isCandleOnScreen(candle: Candle): Boolean {
        val indexOnScreen = findCandleIndexOnScreen(candle)
        if (indexOnScreen < 0 ){
            //Candle out of the screen
            return false
        }
        if (indexOnScreen >= candlesWillBeDrawn.size){
            //Candle out of the screen
            //Should not happen, this is a safe check
            return false
        }

        return true
    }

    override fun findCandleLoc(candle: Candle): CandleRect?{
        val indexOnScreen = candlesWillBeDrawn.indexOfFirst { it.closeTime == candle.closeTime }
        if (indexOnScreen < 0 ){
            //Candle out of the screen
            return null
        }
        if (indexOnScreen >= candlesWillBeDrawn.size){
            //Candle out of the screen
            //Should not happen, this is a safe check
            return null
        }

        return findCandleRectByIos(indexOnScreen)
    }

    override fun findTouchedCandleRect(touchX: Float, touchY: Float): CandleRect?{
        val candleIndexOnScreen = findTouchedCandleIndexOnScreen(touchX, touchY) ?: return null
        return findCandleRectByIos(candleIndexOnScreen)
    }


    override fun findCandleRectByIos(candleIndexOnScreen: Int): CandleRect?{
        if (!iosSafeCheck(candleIndexOnScreen)) {
            Sogger.w("findCandleRectByIos", "index out of screen")
            return null
        }

        val touchCandle = candlesWillBeDrawn[candleIndexOnScreen]

        val priceRange      = currentPriceRange[getMaxPrice] - currentPriceRange[getMinPrice]
        val viewHeightRange = kLineContentViewPort.bottom - kLineContentViewPort.top

        val candleRectF    = CandleRect()

        candleRectF.left   = (candleIndexOnScreen) * (candleWidth + candleGap) + leftOffsetGap
        candleRectF.right  = candleRectF.left + candleWidth

        candleRectF.open  = (kLineContentViewPort.bottom - (touchCandle.open - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
        candleRectF.close = (kLineContentViewPort.bottom - (touchCandle.close - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

        candleRectF.top    = (kLineContentViewPort.bottom - (touchCandle.high - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()
        candleRectF.bottom = (kLineContentViewPort.bottom - (touchCandle.low  - currentPriceRange[0]) / priceRange * viewHeightRange).toFloat()

        return candleRectF
    }

    override fun findTouchedYAxisValue(touchY: Float): Double?{
        if (touchY < kLineContentViewPort.top){
            Sogger.w("findTouchedYAxisValue", "outside of area")
            return currentPriceRange[getMaxPrice]
        }
        if (touchY > kLineContentViewPort.bottom){
            Sogger.w("findTouchedYAxisValue", "outside of area")
            return currentPriceRange[getMinPrice]
        }
        val delta = (kLineContentViewPort.bottom - touchY) * 1.0f / kLineContentViewPort.height()
        val priceOffset = delta * (currentPriceRange[getMaxPrice] - currentPriceRange[getMinPrice])
        val yAxisValue = currentPriceRange[getMinPrice] + priceOffset
        if (yAxisValue > currentPriceRange[getMaxPrice]){
            return currentPriceRange[getMaxPrice]
        }
        if (yAxisValue < currentPriceRange[getMinPrice]){
            return currentPriceRange[getMinPrice]
        }
        return yAxisValue
    }

    fun getKLineTitleArea() = Viewport().also {
        it.top    = mainGraphViewport.top
        it.bottom = mainGraphViewport.top + yAxisLineHeight
        it.left   = mainGraphViewport.left
        it.right  = mainGraphViewport.right
    }

    fun getYCoordinateInKLineContentView(touchY: Float): Float {
        if (touchY < kLineContentViewPort.top){
            return kLineContentViewPort.top
        }
        if (touchY > kLineContentViewPort.bottom){
            return kLineContentViewPort.bottom
        }

        return touchY
    }

    fun getTotalWidth() = (candleWidth + candleGap) * mCandles.count()

    fun getTranslateX() = (candleWidth + candleGap) * (startIndex + 1) + leftOffsetGap

}