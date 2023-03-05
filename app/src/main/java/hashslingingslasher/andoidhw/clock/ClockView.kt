package hashslingingslasher.andoidhw.clock

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*  This view displays a clock which shows current time
 *
 *  Available attributes:
 *      size - the size of the clock, which is used if
 *      its value satisfies the bounds derived from the
 *      onMeasure() method
 *
 *      utcOffset - UTC offset of time to be
 *      displayed (in hours); if not specified,
 *      default system time zone offset is used
 */

class ClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var size: Int
    private var utcOffset: Int
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val clockFace by lazy {
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.clockface,
            null
        )
    }

    private var centerX = 0F
    private var centerY = 0F

    private var handLength = 0
    private var handWidth = 0F
    private val lengthAdjuster = 0.3472
    private val widthAdjuster = 0.007

    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ClockView, 0, 0
        )
        try {
            size = a.getDimensionPixelSize(
                R.styleable.ClockView_clockSize,
                1000
            )
            utcOffset = a.getInt(
                R.styleable.ClockView_utcOffset,
                TimeZone.getDefault().rawOffset / 3600 / 1000
            )
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = minOf(measuredHeight, measuredWidth, size)
        setMeasuredDimension(size, size)
        handLength = (size * lengthAdjuster).toInt()
        handWidth = (size * widthAdjuster).toFloat()
        centerX = (size / 2).toFloat()
        centerY = (size / 2).toFloat()
    }

    private fun Canvas.drawHand(
        length: Int,
        time: Float,
        adjuster: Int,
        paintColor: Int,
        width: Float
    ) {
        drawLine(
            centerX,
            centerY,
            centerX + length * sin(time * PI / adjuster).toFloat(),
            centerY - length * cos(time * PI / adjuster).toFloat(),
            paint.apply {
                color = paintColor
                strokeWidth = width
            }
        )
    }

    private fun time() = Calendar.getInstance().time.time + utcOffset * 1000 * 3600

    private var s = 0L
    private var m = 0L
    private var h = 0L

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        s = (time() / 1000) % 60
        m = (time() / 1000 / 60) % 60
        h = (time() / 1000 / 3600) % 12

        if (canvas != null) {
            clockFace?.apply {
                setBounds(0, 0, size, size)
                draw(canvas)
            }

            canvas.apply {
                drawHand(
                    handLength,
                    s.toFloat(),
                    30,
                    Color.RED,
                    handWidth
                )
                drawHand(
                    (handLength * 0.92).toInt(),
                    m + s / 60F,
                    30,
                    Color.DKGRAY,
                    handWidth * 2
                )
                drawHand(
                    (handLength * 0.7).toInt(),
                    h + m / 60F,
                    6,
                    Color.DKGRAY,
                    handWidth * 4
                )
                drawCircle(
                    centerX,
                    centerY,
                    handWidth * 4,
                    paint.apply {
                        color = Color.DKGRAY
                        style = Paint.Style.FILL
                    }
                )
            }
        }
        invalidate()
    }


}