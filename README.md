# Skyline
Skyline is a kline library for android client, provide professional market charts. Skyline only draw the candles displayed on current screen. So its render speed will be faster than all drawing. Skyline has rich support of technical indicators. You can customize indicators on main graph area and sub graph area as you want. Skyline already have build-in indicator algorithm implementations. And if you want to support any other indicators, only need to wrapper your own calculate data with ```IndicatorWrapper``` or create your own ```SubGraphDrawer```, then place into skyline, skyline will automatically adjust the view based on the incoming data. Skyline also support multi-gestures include drag, scale(x direction and y direction).

# Why named Skyline
Skyline hope each candle can rise to sky high!

# How to use
In gradle
```
repositories {
    mavenCentral()
}

implementation 'io.trubi.android:skyline:1.0.0'
```

# Preview
![skyline](/images/skyline.gif)

# Structure
```
 |-----------------------------------------------------------|
 | Main graph top text area, draw indicator value            |
 |-----------------------------------------------------------|
 |                                                           |
 | Main graph area                                           |
 | - Draw K line                                             |
 | - Draw indicators                                         |           
 |                                                           |
 |-----------------------------------------------------------|
 | Sub graph top text area, draw sub graph indicator value   |
 |-----------------------------------------------------------|
 |                                                           |
 | Sub graph area                                            |
 | - Draw sub graph indicators                               |
 |                                                           |
 |-----------------------------------------------------------|
 | Bottom text area, draw x axis value                       |
 |-----------------------------------------------------------|
 ```

# How to use

## Add skyline
In xml
```
<io.trubi.skyline.Skyline
    android:id="@+id/skyline"
    android:layout_width="match_parent"
    android:layout_height="300dp" />
```
In kotlin
```
val candles = ... // prepare candle data
skyline.setCandles(candles)
skyline.invalidate()
```

## Switch mode
```
// show simple line
kView?.modeLine()
// show candle stick
kView?.modeCandle()
```

## How to custom style
In skyline, use ```FlavorDescription``` to describe how skyline looks

```
FlavorDescription(
    name                  = "default",

    backgroundColor       = Color.TRANSPARENT,
    borderColor           = Color.parseColor("#45FFFFFF"),

    xAxisColor            = Color.parseColor("#45FFFFFF"),
    xAxisLabelColor       = Color.parseColor("#45FFFFFF"),

    yAxisColor            = Color.parseColor("#45FFFFFF"),
    yAxisLabelColor       = Color.parseColor("#45FFFFFF"),

    increasingColor       = Color.parseColor("#2EBD85"),
    decreasingColor       = Color.parseColor("#E24537"),

    timeLineColor         = Color.parseColor("#2587b4"),

    markLabelTextColor    = Color.parseColor("#FFFFFFFF"),

    crossLineColor        = Color.LTGRAY,
    crossLabelColor       = Color.YELLOW
)
```
```
val flavor = FlavorDescription(...)
skyline?.setFlavor(flavor)
```

## Custom main graph indicator
Use ```IndicatorWrapper``` to describe custome indicator.
```
val wrapper = IndicatorWrapper()
wrapper.color = Color.parseColor("#20acea")
wrapper.width = 2f
wrapper.displayName = "indicator name"
wrapper.indicators = arraylistof(...) // compute indicator value

skyline.addMainGraphIndicator(wrapper)
```

## Custom sub graph
Interface ```SubGraphDrawer``` define how to draw sub graph.
```
interface SubGraphDrawer {

    fun getTouchedIndicatorsByIos(indexOnScreen: Int): ArrayList<IndicatorWrapper>

    fun beforeDrawCandles(...)

    fun onDrawEachCandle(...)

    fun afterDrawCandles(...)

}
```
Implement interface ```SubGraphDrawer``` to draw custom graph and set into skyline
```
class VolDrawer: SubGraphDrawer {
    ...
}

skyline.setSubGraphDrawer(VolDrawer())
```

## Add crossline
There is two build-in crossline implementation ```CrossLineView``` and ```DCFWCrossLineView```.

In xml
```
<RelativeLayout
    android:layout_width="match_parent"
    android:layout_height="300dp">

    <io.trubi.skyline.Skyline
        android:id="@+id/skyline"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <io.trubi.skyline.cross.CrossLineView
        android:id="@+id/crossLine"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone"/>

</RelativeLayout>
```

Crossline need to place above skyline.

In kotlin
```
crossLine?.updateCandleRect(candle, candleRect)
crossLine?.updateXAxisStr(...)
crossLine?.updateYCoordinate(...)
...
```

## Gesture
Implement interface ```IKToucher``` to handle gesture logic. Skyline already have two build-in implementation ```SimpleKToucher``` and ```DCFWKToucher```
```
skyline.addToucher(SimpleKToucher())
```

## Build-in indicator support
### Algorithm
- SMA
- EMA
- BOLL
- MACD
- KDJ
- RSI
- WR

### Sub graph drawer
- VolDrawer
- MacdDrawer
- WilliamsRDrawer
- LineDrawer