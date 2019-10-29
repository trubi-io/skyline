package io.trubi.skyline.indicator


class IndicatorWrapper(var type        : String,
                       var cycle       : Int,
                       var color       : Int,
                       var width       : Float,
                       var displayName : String,
                       var indicators: ArrayList<Indicator>){

    constructor(): this(Indicator.BLANK, 0, 0, 0f, "", arrayListOf())

    fun copyAttrFrom(another: IndicatorWrapper){
        type        = another.type
        cycle       = another.cycle
        color       = another.color
        width       = another.width
        displayName = another.displayName
    }

    fun clear(){
        indicators.clear()
    }

}