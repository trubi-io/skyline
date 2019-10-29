package io.trubi.skyline.util

import java.math.RoundingMode
import kotlin.math.roundToInt

object NumberUtil {

    fun format(num: Double, scale: Int): String{
        return format(num.toString(), scale)
    }

    fun format(num: String, scale: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): String{
        return num.toBigDecimalOrNull()?.setScale(scale, roundingMode)?.toPlainString()?:num
    }

    fun formatDown(num: String, scale: Int): String{
        return format(num, scale, roundingMode = RoundingMode.DOWN)
    }

    fun roundToInt(d: Double): Int = d.roundToInt()

}