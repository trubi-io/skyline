package io.trubi.skyline.cross

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import io.trubi.skyline.R
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.module.Viewport
import io.trubi.skyline.core.HelperView
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.flavor.FlavorDescription
import io.trubi.skyline.flavor.FlavorFactory
import io.trubi.skyline.render.AxisRender
import io.trubi.skyline.util.NumberUtil

/**
 * Cross line View over KGraphView
 *
 * ----------------------------------|-------------------------|
 * | Main title area                 |                         |
 * |---------------------------------|-------------------------|
 * |                                 |                         |
 * |=================================o=========================|
 * |                                 |                         |
 * |                                 |                         |
 * |                                 |                         |
 * |---------------------------------|-------------------------|
 * | Sub title area                  |                         |
 * |---------------------------------|-------------------------|
 * |                                 |                         |
 * |                                 |                         |
 * ----------------------------------|-------------------------|
 * | Bottom text area                |                         |
 * ----------------------------------|-------------------------|
 *
 * @author Fitz
 * */
class CrossLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HelperView(context, attrs, defStyleAttr) {

    protected var mFlavor  : FlavorDescription = FlavorFactory.default()

    /**
     * Selected candle
     * */
    private var candle: Candle? = null
    /**
     * Selected candle rectF on screen
     * */
    private val candleRectF      = CandleRect()
    /**
     * Text display on x axis
     * */
    private var xAxisStr         = ""
    /**
     * Text display on y axis
     * */
    private var yAxisStr         = ""

    /**
     * Axis text color
     * */
    private var axisColor           = Color.parseColor("#ffffff")

    /**
     * Axis background rect color
     * */
    private var axisBackgroundColor = Color.parseColor("#959eb1")

    private var xAxisFm             = Paint.FontMetrics()
    private var yAxisFm             = Paint.FontMetrics()
    private var volAxisFm           = Paint.FontMetrics()

    private val borderWidth      = resources.getDimension(R.dimen.prom_1dp)
    private val paint            = Paint().also {
        it.style       = Paint.Style.STROKE
        it.strokeWidth = borderWidth
        it.color       = Color.parseColor("#939FA9")
    }

    private val measureTextPaint = TextPaint()

    private val mainTitleArea = Viewport()
    private val subTitleArea  = Viewport()

    //title, color
    private val mainTitles = arrayListOf<Pair<String, Int>>()
    private val subTitles  = arrayListOf<Pair<String, Int>>()

    private val titleTextSize = resources.getDimension(R.dimen.prom_default_axis_label_text_size)
    private val titleGap      = resources.getDimension(R.dimen.prom_3dp)

    private var mainTitlesWidth = 0f
    private var subTitlesWidth = 0f

    private var typeface: Typeface? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //x axis
        canvas?.drawLine(paddingLeft.toFloat(), candleRectF.close, measuredWidth.toFloat() - paddingRight, candleRectF.close, paint)
        if (xAxisStr.isNotEmpty()){
            measureTextPaint.reset()
            measureTextPaint.textSize = titleTextSize
            measureTextPaint.color    = axisBackgroundColor
            measureTextPaint.getFontMetrics(xAxisFm)

            val x = paddingLeft.toFloat()
            val y = candleRectF.close

            val textHeight = measureTextHeight(titleTextSize)

            canvas?.drawRect(x , y + xAxisFm.top + textHeight / 2f,
                    x + measureTextPaint.measureText(xAxisStr), y + textHeight / 2f, measureTextPaint)

            AxisRender.drawAxisTextAlignLeft(xAxisStr, x, y - xAxisFm.descent / 2 + textHeight / 2f, titleTextSize, axisColor, canvas, typeface)
        }

        //y axis
        canvas?.drawLine(candleRectF.left + candleRectF.width() / 2, paddingTop.toFloat(),
                candleRectF.left + candleRectF.width() / 2, measuredHeight.toFloat() - paddingBottom, paint)
        if (yAxisStr.isNotEmpty()){
            measureTextPaint.reset()
            measureTextPaint.textSize = titleTextSize
            measureTextPaint.color    = axisBackgroundColor
            measureTextPaint.getFontMetrics(yAxisFm)

            val textWidth = measureTextPaint.measureText(yAxisStr)

            val x = candleRectF.left + candleRectF.width() / 2
            val y = measuredHeight.toFloat() - paddingBottom

            if (x - textWidth / 2f < 0){
                canvas?.drawRect(x, y + yAxisFm.top, x + textWidth + 2, y, measureTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x, y - yAxisFm.descent / 2, titleTextSize, axisColor, canvas, typeface)
            }else if (textWidth / 2f + x + 2 < measuredWidth - paddingRight){
                canvas?.drawRect(x - textWidth / 2f , y + yAxisFm.top, x + textWidth / 2f + 2, y, measureTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x - textWidth / 2f, y - yAxisFm.descent / 2, titleTextSize, axisColor, canvas, typeface)
            }else{
                canvas?.drawRect(x - textWidth - 2, y + yAxisFm.top, x, y, measureTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x - textWidth - 2, y, titleTextSize, axisColor, canvas, typeface)
            }

        }

        measureTextPaint.reset()
        measureTextPaint.textSize = titleTextSize
        measureTextPaint.color    = axisBackgroundColor
        measureTextPaint.getFontMetrics(volAxisFm)

        mainTitlesWidth = mainTitleArea.left + titleGap
        mainTitles.forEach {
            AxisRender.drawAxisTextAlignLeft(it.first, mainTitlesWidth, mainTitleArea.bottom - getTextDescent(titleTextSize), titleTextSize, it.second, canvas, typeface)
            mainTitlesWidth += measureTextWidth(titleTextSize, it.first)
            mainTitlesWidth += titleGap
        }

        subTitlesWidth = subTitleArea.left + titleGap

        val volTip = "Vol: ${NumberUtil.formatDown((candle?.volume?.toString())?:"", 0)}"
        canvas?.drawRect(subTitleArea.left, subTitleArea.top, subTitleArea.left + measureTextPaint.measureText(volTip), subTitleArea.bottom, measureTextPaint)
        AxisRender.drawAxisTextAlignLeft(volTip, subTitleArea.left, subTitleArea.bottom - volAxisFm.descent / 2, titleTextSize, axisColor, canvas, typeface)
        subTitlesWidth += measureTextWidth(titleTextSize, volTip)
        subTitlesWidth += titleGap * 2

        subTitles.forEach {
            AxisRender.drawAxisTextAlignLeft(it.first, subTitlesWidth, subTitleArea.bottom - getTextDescent(titleTextSize), titleTextSize, it.second, canvas, typeface)
            subTitlesWidth += measureTextWidth(titleTextSize, it.first)
            subTitlesWidth += titleGap
        }

    }

    fun setFlavor(newFlavor: FlavorDescription){
        mFlavor = newFlavor
    }

    fun getterFlavor() = mFlavor

    fun updateCandleRect(candle: Candle, rectF: CandleRect){
        this.candle = candle
        candleRectF.copyProperties(rectF)
    }

    fun setBorderWidth(width: Float){
        paint.strokeWidth = width
    }

    fun setBorderColor(color: Int){
        paint.color = color
    }

    fun updateMainTitleArea(area: RectF){
        mainTitleArea.left   = area.left
        mainTitleArea.top    = area.top
        mainTitleArea.right  = area.right
        mainTitleArea.bottom = area.bottom
    }

    fun clearMainTitles(){
        mainTitles.clear()
    }

    fun addMainTitle(title: String, color: Int){
        mainTitles.add(Pair(title, color))
    }

    fun updateSubTitleArea(area: RectF){
        subTitleArea.left   = area.left
        subTitleArea.top    = area.top
        subTitleArea.right  = area.right
        subTitleArea.bottom = area.bottom
    }

    fun updateXAxisStr(str: String){
        xAxisStr = str
    }

    fun updateYAxisStr(str: String){
        yAxisStr = str
    }

    fun clearSubTitles(){
        subTitles.clear()
    }

    fun addSubTitle(title: String, color: Int){
        subTitles.add(Pair(title, color))
    }

    fun setTypeface(tp: Typeface?){
        typeface = tp
    }

    fun updateAxisColor(color: Int){
        axisColor = color
    }

    fun updateAxisBackgroundColor(color: Int){
        axisBackgroundColor = color
    }

}