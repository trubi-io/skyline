package io.trubi.skyline.module

import android.graphics.RectF

/**
 * Created by AndyL on 2018/3/19.
 *
 */
class Viewport : RectF(){

    override fun toString(): String {
        return "[left=$left, top=$top, right=$right, bottom=$bottom]"
    }
}