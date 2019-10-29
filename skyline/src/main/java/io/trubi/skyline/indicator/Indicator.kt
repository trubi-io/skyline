package io.trubi.skyline.indicator

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * */
data class Indicator @JvmOverloads constructor (var time: Long = 0, var value: Double = 0.0, var isEmpty: Boolean = false): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readDouble(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeDouble(value)
        parcel.writeByte(if (isEmpty) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Indicator> {
        override fun createFromParcel(parcel: Parcel): Indicator {
            return Indicator(parcel)
        }

        override fun newArray(size: Int): Array<Indicator?> {
            return arrayOfNulls(size)
        }

        const val BLANK = "BLANK"

        const val AVG   = "AVG"

        //main graph
        const val SMA   = "SMA"
        const val EMA   = "EMA"
        const val BOLL  = "BOLL"

        //sub graph
        const val VOL   = "VOL"
        const val MACD  = "MACD"
        const val KDJ   = "KDJ"
        const val RSI   = "RSI"
        const val WR    = "WR"
    }


}