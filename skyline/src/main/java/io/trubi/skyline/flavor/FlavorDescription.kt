package io.trubi.skyline.flavor

/**
 * Created by Fitz on 2018/3/2.
 *
 */
data class FlavorDescription(

        /**
         * name
         * */
        val name: String,

        /**
         * background color
         * */
        val backgroundColor      : Int,

        /**
         * border color
         * */
        val borderColor          : Int,

        /**
         * X axis
         * */
        val xAxisColor           : Int,
        val xAxisLabelColor      : Int,

        /**
         * Y axis
         * */
        val yAxisColor           : Int,
        val yAxisLabelColor      : Int,

        /**
         * increasing color
         * */
        val increasingColor      : Int,

        /**
         * decreasing color
         * */
        val decreasingColor      : Int,

        /**
         * time line color
         * */
        val timeLineColor        : Int,

        /**
         * mark label text color
         * */
        val markLabelTextColor   : Int,

        /**
         * cross line color
         * */
        val crossLineColor       : Int,
        val crossLabelColor      : Int

)