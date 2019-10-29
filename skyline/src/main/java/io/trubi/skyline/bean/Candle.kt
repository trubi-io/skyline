package io.trubi.skyline.bean

/**
 * Created by Fitz on 2018/3/5.
 */

data class Candle(
        //The open time
        var openTime            : Long    = 0.toLong(),
        //The close time
        var closeTime           : Long    = 0.toLong(),
        //The time format pattern
        var timeFormatPattern   : String? = null,
        //The open price
        var open                : Double  = 0.toDouble(),
        //The close price
        var close               : Double  = 0.toDouble(),
        //The high price
        var high                : Double  = 0.toDouble(),
        //The low price
        var low                 : Double  = 0.toDouble(),
        //The volume
        var volume              : Double  = 0.toDouble(),
        //The quote asset volume
        var quoteAssetVolume    : Double  = 0.toDouble())