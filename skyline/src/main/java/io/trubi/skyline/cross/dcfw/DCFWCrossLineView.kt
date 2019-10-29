package io.trubi.skyline.cross.dcfw

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import io.trubi.skyline.R
import io.trubi.skyline.bean.Candle
import io.trubi.skyline.core.HelperView
import io.trubi.skyline.module.CandleRect
import io.trubi.skyline.render.AxisRender
import android.graphics.DashPathEffect
import io.trubi.skyline.flavor.FlavorDescription
import io.trubi.skyline.flavor.FlavorFactory
import io.trubi.skyline.module.Viewport


/**
 * author       : Fitz Lu
 * created on   : 12/12/2018 14:15
 */
class DCFWCrossLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : HelperView(context, attrs, defStyleAttr) {

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

    private var xCoordinate      = 0f

    private var yCoordinate      = 0f

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

    private val borderWidth      = resources.getDimension(R.dimen.prom_1dp)
    private val paint            = Paint().also {
        it.style       = Paint.Style.STROKE
        it.strokeWidth = borderWidth
        it.color       = Color.parseColor("#939FA9")
        it.pathEffect  = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }
    private val xPath  = Path()
    private val yPath  = Path()

    private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val defaultTextSize = resources.getDimension(R.dimen.prom_default_axis_label_text_size)

    private var floatWindowRect = Rect()
    private var floatWindowWidth = 0
    private var floatWindowHeight = 0

    private var typeface: Typeface? = null

    private var floatWindow: FloatWindow? = null

    private var alignLeft = FloatWindowAlign.Left
    private var alignRight = FloatWindowAlign.Right

    private val mainTitleArea = Viewport()
    private val subTitleArea  = Viewport()

    //title, color
    private val mainTitles = arrayListOf<Pair<String, Int>>()
    private val subTitles  = arrayListOf<Pair<String, Int>>()

    private val titleTextSize = resources.getDimension(R.dimen.prom_default_axis_label_text_size)
    private val titleGap      = resources.getDimension(R.dimen.prom_3dp)

    private var mainTitlesWidth = 0f
    private var subTitlesWidth = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        floatWindowWidth = mWidth / 4
        floatWindowHeight = mHeight / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var yAxisAlignLeft = true

        //Why div 2?, because main title area contains highest value arrow
        floatWindow?.y = mainTitleArea.bottom + paddingTop.toFloat()
        floatWindowRect.top = paddingTop
        floatWindowRect.bottom = floatWindowRect.top + floatWindowHeight
        if (xCoordinate < mWidth / 2f){
            //draw float window on the right
            floatWindowRect.right = measuredWidth - paddingRight
            floatWindowRect.left = floatWindowRect.right - floatWindowWidth
            floatWindow?.alignment = alignRight
            floatWindow?.x = paddingLeft + width.toFloat()
            yAxisAlignLeft = true
        }else{
            //draw float window on the left
            floatWindowRect.left = paddingStart
            floatWindowRect.right = floatWindowRect.left + floatWindowWidth
            floatWindow?.alignment = alignLeft
            floatWindow?.x = paddingLeft.toFloat()
            yAxisAlignLeft = false
        }

        //x axis
        xPath.reset()
        xPath.moveTo(paddingLeft.toFloat(), yCoordinate)
        xPath.lineTo(measuredWidth.toFloat() - paddingRight, yCoordinate)
        canvas?.drawPath(xPath, paint)
        if (xAxisStr.isNotEmpty()){
            mTextPaint.reset()
            mTextPaint.isAntiAlias = true
            mTextPaint.textSize = defaultTextSize
            mTextPaint.color    = axisBackgroundColor
            mTextPaint.getFontMetrics(xAxisFm)

            val textHeight = measureTextHeight(defaultTextSize)

            if (yAxisAlignLeft){
                val x = paddingLeft.toFloat()
                val y = yCoordinate
                canvas?.drawRect(x , y + xAxisFm.top + textHeight / 2f,
                        x + mTextPaint.measureText(xAxisStr), y + textHeight / 2f, mTextPaint)
                AxisRender.drawAxisTextAlignLeft(xAxisStr, x, y - xAxisFm.descent / 2 + textHeight / 2f, defaultTextSize, axisColor, canvas, typeface)
            }else{
                val x = width - paddingEnd - measureTextWidth(mTextPaint.textSize, xAxisStr)
                val y = yCoordinate
                canvas?.drawRect(x , y + xAxisFm.top + textHeight / 2f,
                        width - paddingEnd.toFloat(), y + textHeight / 2f, mTextPaint)
                AxisRender.drawAxisTextAlignLeft(xAxisStr, x, y - xAxisFm.descent / 2 + textHeight / 2f, defaultTextSize, axisColor, canvas, typeface)
            }
        }

        //y axis
        yPath.reset()
        yPath.moveTo(xCoordinate, paddingTop.toFloat())
        yPath.lineTo(xCoordinate, measuredHeight.toFloat() - paddingBottom)
        canvas?.drawPath(yPath, paint)
        if (yAxisStr.isNotEmpty()){
            mTextPaint.reset()
            mTextPaint.isAntiAlias = true
            mTextPaint.textSize = defaultTextSize
            mTextPaint.color    = axisBackgroundColor
            mTextPaint.getFontMetrics(yAxisFm)

            val x = xCoordinate
            val y = measuredHeight.toFloat() - paddingBottom

            val textWidth = mTextPaint.measureText(yAxisStr)

            if (x - textWidth / 2f < 0){
                canvas?.drawRect(x, y + yAxisFm.top, x + textWidth + 2, y, mTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x, y - yAxisFm.descent / 2, defaultTextSize, axisColor, canvas, typeface)
            }else if (textWidth / 2f + x + 2 < measuredWidth - paddingRight){
                canvas?.drawRect(x - textWidth / 2f , y + yAxisFm.top, x + textWidth / 2f + 2, y, mTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x - textWidth / 2f, y - yAxisFm.descent / 2, defaultTextSize, axisColor, canvas, typeface)
            }else{
                canvas?.drawRect(x - textWidth - 2, y + yAxisFm.top, x, y, mTextPaint)
                AxisRender.drawAxisTextAlignLeft(yAxisStr, x - textWidth - 2, y, defaultTextSize, axisColor, canvas, typeface)
            }

        }

        mainTitlesWidth = mainTitleArea.left + titleGap
        mainTitles.forEach {
            AxisRender.drawAxisTextAlignLeft(it.first, mainTitlesWidth, mainTitleArea.bottom - getTextDescent(titleTextSize), titleTextSize, it.second, canvas, typeface)
            mainTitlesWidth += measureTextWidth(titleTextSize, it.first)
            mainTitlesWidth += titleGap
        }

        if (yAxisAlignLeft){
            subTitlesWidth = subTitleArea.left + titleGap
            subTitles.forEach {
                AxisRender.drawAxisTextAlignLeft(it.first, subTitlesWidth, subTitleArea.bottom - getTextDescent(titleTextSize), titleTextSize, it.second, canvas, typeface)
                subTitlesWidth += measureTextWidth(titleTextSize, it.first)
                subTitlesWidth += titleGap
            }
        }else{
            subTitlesWidth = titleGap
            subTitles.forEach {
                AxisRender.drawAxisTextAlignRight(it.first, subTitleArea.right- subTitlesWidth, subTitleArea.bottom - getTextDescent(titleTextSize), titleTextSize, it.second, canvas, typeface )
                subTitlesWidth += measureTextWidth(titleTextSize, it.first)
                subTitlesWidth += titleGap
            }
        }

        floatWindow?.draw(canvas)
    }

    fun setFlavor(newFlavor: FlavorDescription){
        mFlavor = newFlavor
    }

    fun getterFlavor() = mFlavor

    fun updateCandleRect(candle: Candle, rectF: CandleRect){
        this.candle = candle
        candleRectF.copyProperties(rectF)
        xCoordinate = candleRectF.left + candleRectF.width() / 2
    }

    fun setFloatWindow(newWindow: FloatWindow?){
        floatWindow = newWindow
    }

    fun updateXAxisStr(str: String){
        yAxisStr = str
    }

    fun updateYAxisStr(str: String){
        xAxisStr = str
    }

    fun updateYCoordinate(coordinate: Float){
        yCoordinate = coordinate
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

    fun clearSubTitles(){
        subTitles.clear()
    }

    fun addSubTitle(title: String, color: Int){
        subTitles.add(Pair(title, color))
    }

    fun setTypeface(tp: Typeface){
        typeface = tp
    }

}