package io.trubi.skyline.module

/**
 * Created by AndyL on 2018/3/6.
 *
 */
class PointF(newX: Float, newY: Float) {

    var x: Float = newX
    var y: Float = newY

    constructor() : this(-1f, -1f)

    fun isBlank() = x == -1f && y == -1f

    override fun toString(): String {
        return "[x=$x, y=$y]"
    }
}