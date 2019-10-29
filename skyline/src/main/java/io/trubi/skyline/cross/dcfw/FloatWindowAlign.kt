package io.trubi.skyline.cross.dcfw

/**
 * author       : Fitz Lu
 * created on   : 17/12/2018 17:09
 * description  :
 */
sealed class FloatWindowAlign {

    object Left : FloatWindowAlign()

    object Right : FloatWindowAlign()

}