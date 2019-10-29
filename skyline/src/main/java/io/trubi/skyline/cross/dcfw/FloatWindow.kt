package io.trubi.skyline.cross.dcfw

import android.graphics.Canvas
import android.graphics.Typeface

/**
 * author       : Fitz Lu
 * created on   : 17/12/2018 15:09
 * description  :
 */
abstract class FloatWindow {

    var x: Float = 0f

    var y: Float = 0f

    var digitalTypeface: Typeface? = null

    var width: Float = 0f

    var height: Float = 0f

    var leftPadding: Float = 0f

    var topPadding: Float = 0f

    var rightPadding: Float = 0f

    var bottomPadding: Float = 0f

    var leftMargin: Float = 0f

    var rightMargin: Float = 0f

    var topMargin: Float = 0f

    var bottomMargin: Float = 0f

    var alignment: FloatWindowAlign = FloatWindowAlign.Left

    abstract fun draw(canvas: Canvas?)

    fun setPadding(padding: Float){
        leftPadding = padding
        topPadding = padding
        rightPadding = padding
        bottomPadding = padding
    }

}