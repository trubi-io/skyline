package io.trubi.skyline.touch

/**
 * author       : Fitz Lu
 * created on   : 26/12/2018 14:58
 * description  : Listen each transform action in skyline
 */
interface SkylineTransformListener {

    fun beforeDrag()

    fun afterDragLeft()

    fun afterDragRight()

    fun beforeZoomX()

    fun afterZoomX()

    fun beforeZoomY()

    fun afterZoomY()

}