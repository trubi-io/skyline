package io.trubi.skyline.cross.dcfw.window

/**
 * author       : Fitz Lu
 * created on   : 17/12/2018 15:53
 * description  :
 */
data class Label(val text: String = "", val color: Int = 0, val textSize: Float = 0f){

    fun isEmpty() = text.isBlank()

}