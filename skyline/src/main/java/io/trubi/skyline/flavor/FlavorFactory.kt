package io.trubi.skyline.flavor

import android.graphics.Color

/**
 * Created by Fitz on 2018/3/2.
 *
 */
object FlavorFactory {

    fun create(name: String,
               backgroundColor      : Int,
               borderColor          : Int,
               xAxisColor           : Int,
               xAxisLabelColor      : Int,
               yAxisColor           : Int,
               yAxisLabelColor      : Int,
               increasingCandleColor: Int,
               decreasingCandleColor: Int,
               timeLineColor        : Int,
               markLabelTextColor   : Int,
               crossLineColor       : Int,
               crossLabelColor      : Int): FlavorDescription =

            FlavorDescription(name, backgroundColor, borderColor,
                    xAxisColor, xAxisLabelColor, yAxisColor, yAxisLabelColor,
                    increasingCandleColor, decreasingCandleColor, timeLineColor,
                    markLabelTextColor, crossLineColor, crossLabelColor)

    fun default() = FlavorDescription(
            name                  = "default",

            backgroundColor       = Color.TRANSPARENT,
            borderColor           = Color.parseColor("#45FFFFFF"),

            xAxisColor            = Color.parseColor("#45FFFFFF"),
            xAxisLabelColor       = Color.parseColor("#45FFFFFF"),

            yAxisColor            = Color.parseColor("#45FFFFFF"),
            yAxisLabelColor       = Color.parseColor("#45FFFFFF"),

            increasingColor       = Color.parseColor("#2EBD85"),
            decreasingColor       = Color.parseColor("#E24537"),

            timeLineColor         = Color.parseColor("#2587b4"),

            markLabelTextColor    = Color.parseColor("#FFFFFFFF"),

            crossLineColor        = Color.LTGRAY,
            crossLabelColor       = Color.YELLOW)

}