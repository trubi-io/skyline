package io.trubi.skyline.touch

import android.view.MotionEvent
import io.trubi.skyline.Skyline

/**
 * Usually, Toucher and CrossLineView are one-to-one correspondence
 * @see [io.trubi.skyline.touch.simple.SimpleKToucher] to [io.trubi.skyline.cross.CrossLineView]
 * @see [io.trubi.skyline.touch.dcfw.DCFWKToucher] to [io.trubi.skyline.cross.dcfw.DCFWCrossLineView]
 * */
interface IKToucher {

    fun attach(skyline: Skyline)

    fun onTouchEvent(event: MotionEvent): Boolean

}