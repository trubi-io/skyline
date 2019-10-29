package io.trubi.skyline.cross.dcfw.window

import android.graphics.*
import android.text.TextPaint
import io.trubi.skyline.cross.dcfw.FloatWindowAlign
import io.trubi.skyline.cross.dcfw.FloatWindow

/**
 * author       : Fitz Lu
 * created on   : 17/12/2018 15:46
 * description  : Double column float windows
 */
class DCFWFloatWindow: FloatWindow() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val mDigitalPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val measurePaint: TextPaint = TextPaint()

    private val childView: MutableSet<Pair<Label, Label>> = mutableSetOf()
    private val measuredChildView: MutableSet<LabelView> = mutableSetOf()

    private var backgroundRectF: RectF = RectF()
    var backgroundColor: Int = Color.TRANSPARENT
    var backgroundBorderColor: Int = Color.TRANSPARENT

    var textSize: Float = 10f
    var lineSpaceExtra: Float = 0f
    var columnGap: Float = 0f
    var corner: Float = 0f

    private val leftAlign = FloatWindowAlign.Left
    private val rightAlign = FloatWindowAlign.Right

    override fun draw(canvas: Canvas?) {
        onMeasure()
        onLayout()
        onDraw(canvas)
    }

    private fun onMeasure(){
        mTextPaint.textSize = textSize
        mDigitalPaint.textSize = textSize
        measurePaint.textSize = textSize
        if (digitalTypeface != null) {
            mDigitalPaint.typeface = digitalTypeface
        }

        width = 0f
        height = 0f

        var maxLeftTextWidth  = 0f
        flatSetLeft().forEach {
            maxLeftTextWidth = Math.max(maxLeftTextWidth, measureTextWidth(it.textSize, it.text))
        }

        var maxRightTextWidth = 0f
        flatSetRight().forEach {
            maxRightTextWidth = Math.max(maxRightTextWidth, measureTextWidth(it.textSize, it.text))
        }

        width = leftPadding + maxLeftTextWidth + columnGap + maxRightTextWidth + rightPadding

        if (alignment is FloatWindowAlign.Right){
            //RTL
            val rightColumnEndX   = x - rightMargin - rightPadding
            val rightColumnStartX = x - rightMargin - rightPadding - maxRightTextWidth

            val leftColumnEndX    = x - rightMargin - rightPadding - maxRightTextWidth - columnGap
            val leftColumnStartX  = x - rightMargin - rightPadding - maxRightTextWidth - columnGap - maxLeftTextWidth

            measureChild(leftColumnStartX, leftColumnEndX, rightColumnStartX, rightColumnEndX)

            backgroundRectF.right = x - rightMargin
            backgroundRectF.left = x - rightMargin - width
            backgroundRectF.top  = y + topMargin
            backgroundRectF.bottom = backgroundRectF.top + height

        }else{
            //LTR
            val leftColumnStartX = x + leftPadding + leftMargin
            val leftColumnEndX   = x + leftPadding + leftMargin + maxLeftTextWidth

            val rightColumnStartX = x + leftPadding + leftMargin + maxLeftTextWidth + columnGap
            val rightColumnEndX   = x + leftPadding + leftMargin + maxLeftTextWidth + columnGap + maxRightTextWidth

            measureChild(leftColumnStartX, leftColumnEndX, rightColumnStartX, rightColumnEndX)

            backgroundRectF.left = x + leftMargin
            backgroundRectF.right = x + leftMargin + width
            backgroundRectF.top  = y + topMargin
            backgroundRectF.bottom = backgroundRectF.top + height
        }
    }

    private fun measureChild(leftColumnStartX: Float, leftColumnEndX: Float, rightColumnStartX: Float, rightColumnEndX: Float){
        measuredChildView.clear()

        height = y + topMargin
        height += topPadding
        childView.withIndex().forEach { labelPair ->
            val leftLabel: Label = labelPair.value.first
            val rightLabel: Label = labelPair.value.second
            val maxTextHeight = measureTextLineHeight(Math.max(leftLabel.textSize, rightLabel.textSize))
            if (!leftLabel.isEmpty()) {
                measuredChildView.add(LabelView(leftLabel, RectF(leftColumnStartX, height, leftColumnEndX, height + maxTextHeight), leftAlign))
            }
            if (!rightLabel.isEmpty()) {
                measuredChildView.add(LabelView(rightLabel, RectF(rightColumnStartX, height, rightColumnEndX, height + maxTextHeight), rightAlign))
            }
            height += maxTextHeight.toInt()
            if (labelPair.index < childView.size - 1){
                height += lineSpaceExtra
            }
        }
        height += bottomPadding
        height -= topMargin
    }

    private fun onLayout(){

    }

    private fun onDraw(canvas: Canvas?){
        mPaint.color = backgroundColor
        mPaint.style = Paint.Style.FILL
        canvas?.drawRoundRect(backgroundRectF, corner, corner, mPaint)
        mPaint.color = backgroundBorderColor
        mPaint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(backgroundRectF, corner, corner, mPaint)

        measuredChildView.withIndex().forEach {
            mTextPaint.textSize = it.value.label.textSize
            mTextPaint.color = it.value.label.color
            mDigitalPaint.textSize = it.value.label.textSize
            mDigitalPaint.color = it.value.label.color
            if (it.value.align is FloatWindowAlign.Left){
                if (it.index == 0){
                    mDigitalPaint.textAlign = Paint.Align.LEFT
                    canvas?.drawText(it.value.label.text, it.value.layoutRectF.left,
                            it.value.layoutRectF.bottom - getTextDescent(it.value.label.textSize), mDigitalPaint)
                }else{
                    mTextPaint.textAlign = Paint.Align.LEFT
                    canvas?.drawText(it.value.label.text, it.value.layoutRectF.left,
                            it.value.layoutRectF.bottom - getTextDescent(it.value.label.textSize), mTextPaint)
                }
            }else{
                mDigitalPaint.textAlign = Paint.Align.RIGHT
                canvas?.drawText(it.value.label.text, it.value.layoutRectF.right,
                        it.value.layoutRectF.bottom - getTextDescent(it.value.label.textSize), mDigitalPaint)
            }
        }
    }

    private fun flatSetLeft(): ArrayList<Label>{
        val flatPairs = arrayListOf<Label>()
        childView.forEach {
            flatPairs.add(it.first)
        }
        return flatPairs
    }

    private fun flatSetRight(): ArrayList<Label>{
        val flatPairs = arrayListOf<Label>()
        childView.forEach {
            flatPairs.add(it.second)
        }
        return flatPairs
    }

    private fun flatSet(): ArrayList<Label>{
        val flatPairs = arrayListOf<Label>()
        childView.forEach {
            flatPairs.add(it.first)
            flatPairs.add(it.second)
        }
        return flatPairs
    }

    fun clear(){
        childView.clear()
        measuredChildView.clear()
    }

    fun addContent(left: Label, right: Label){
        childView.add(Pair(left, right))
    }

    fun getTextDescent(size: Float): Float =
            measurePaint.let {
                it.textSize = size
                it.fontMetrics.descent
            }

    fun getTextAscent(size: Float): Float =
            measurePaint.let {
                it.textSize = size
                it.fontMetrics.ascent
            }

    private fun measureTextWidth(size: Float, text: String): Float =
            measurePaint.let {
                it.textSize = size
                it.measureText(text)
            }

    private fun measureTextLineHeight(size: Float): Float =
            measurePaint.let {
                it.textSize = size
                it.fontMetrics.bottom - it.fontMetrics.top
            }

}