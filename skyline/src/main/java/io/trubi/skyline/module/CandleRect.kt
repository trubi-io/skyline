package io.trubi.skyline.module

import android.graphics.RectF

/**
 * Created by AndyL on 2018/3/21.
 *
 */
class CandleRect : RectF() {

    var open   = -1f
    var close  = -1f

    fun reset(){
        top    = 0f
        bottom = 0f
        left   = 0f
        right  = 0f

        open   = 0f
        close  = 0f
    }

    fun middleX() = (left + right) / 2f

    fun copyProperties(other: CandleRect){
        open  = other.open
        close = other.close

        top     = other.top
        bottom  = other.bottom
        left    = other.left
        right   = other.right
    }

    override fun toString(): String {
        return "CandleRect(open=$open, close=$close)"
    }


}