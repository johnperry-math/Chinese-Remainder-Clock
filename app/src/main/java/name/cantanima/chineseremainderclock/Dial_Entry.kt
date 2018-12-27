package name.cantanima.chineseremainderclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class Dial_Entry(context: Context, attrs: AttributeSet)
    : View(context, attrs), View.OnTouchListener
{

    private val pi = 3.14159f

    // here's how the hands work
    // *_hand tells you the hand's raw value as measured on the dial
    //    so 0 is always at the top, then 1 is clockwise to the right, etc.
    // *_min tells you the minimum value of the corresponding hand
    // short_num tells you the number of values short will measure
    //    hence the max value of the short hand will be short_min + short_num - 1
    // long_per_short tells you the number of values the long hand has _per_ value of the short hand
    //    a standard clock, for instance, has 5 values of the long hand for each value of the short
    // *_start_top tells you whether the counting starts at the top
    //    if true, then the hand's min value occurs at the top
    //    if false, then the hand's max value occurs at the top
    private var long_hand = 0
    private var short_hand = 0
    var long_min = 0
        set(value) {
            field = value
            invalidate()
        }
    var short_min = 1
        set(value) {
            field = value
            invalidate()
        }
    var short_num = 12
        set(value) {
            field = value
            invalidate()
        }
    var long_per_short = 5
        set(value) {
            field = value
            invalidate()
        }
    var long_start_top = true
        set(value) {
            field = value
            invalidate()
        }
    var short_start_top = false
        set(value) {
            field = value
            invalidate()
        }

    private var highlight_by_color = false
    private var highlight_width = 10f

    private val background_default = Color.WHITE
    private val hatch_default = Color.GRAY
    private var highlight_color = Color.RED
    private val hand_default = Color.BLACK
    private val text_color_default = Color.GRAY

    private val background_paint = Paint()
    private val hour_paint = Paint()
    private val min_paint = Paint()
    private val hatch_paint = Paint()
    private val text_paint = Paint()
    private val text_bounds = Rect()

    private var time_text = true
    private var two_handed = true

    private var moving = false
    private var moving_hour_hand = false
    private var moving_minute_hand = false

    private var hour_width = 6f
    private var min_width = 4f

    init {
        val choices = context.obtainStyledAttributes(attrs, R.styleable.Dial_Entry)
        // get highlighting options
        highlight_by_color = choices
                .getBoolean(R.styleable.Dial_Entry_highlight_by_color, highlight_by_color)
        highlight_width = choices
                .getInteger(R.styleable.Dial_Entry_high_width, highlight_width.toInt()).toFloat()
        // get colors
        highlight_color = choices.getColor(R.styleable.Dial_Entry_high_color, highlight_color)
        background_paint.color = choices
                .getColor(R.styleable.Dial_Entry_back_color, background_default)
        hour_paint.color = choices.getColor(R.styleable.Dial_Entry_hand_color, hand_default)
        min_paint.color = choices.getColor(R.styleable.Dial_Entry_hand_color, hand_default)
        hatch_paint.color = choices.getColor(R.styleable.Dial_Entry_hatch_color, hatch_default)
        text_paint.color = choices.getColor(R.styleable.Dial_Entry_text_color, text_color_default)
        // stroke widths
        hour_paint.strokeWidth = choices
                .getFloat(R.styleable.Dial_Entry_hour_size, hour_width)
        min_paint.strokeWidth = choices
                .getFloat(R.styleable.Dial_Entry_min_size, min_width)
        hatch_paint.strokeWidth = choices.getFloat(R.styleable.Dial_Entry_hatch_size, 4f)
        // text size
        text_paint.textSize = choices.getFloat(R.styleable.Dial_Entry_text_size, 24f)
        // anti-alias everything
        background_paint.flags = Paint.ANTI_ALIAS_FLAG
        hour_paint.flags = Paint.ANTI_ALIAS_FLAG
        min_paint.flags = Paint.ANTI_ALIAS_FLAG
        hatch_paint.flags = Paint.ANTI_ALIAS_FLAG
        text_paint.flags = Paint.ANTI_ALIAS_FLAG
        // values that determine the, um, values
        two_handed = choices.getBoolean(R.styleable.Dial_Entry_two_handed, two_handed)
        short_min = choices.getInt(R.styleable.Dial_Entry_short_min, short_min)
        short_num = choices.getInt(R.styleable.Dial_Entry_short_num, short_num)
        long_min = choices.getInt(R.styleable.Dial_Entry_long_min, long_min)
        long_per_short = choices.getInt(R.styleable.Dial_Entry_long_per_short, long_per_short)
        time_text = choices.getBoolean(R.styleable.Dial_Entry_time_text, time_text)
        // listen to yourself, dammit
        setOnTouchListener(this)
        // shut up, Android Studio (and thanks!)
        choices.recycle()
    }

    fun long_value(): Int =
        when (long_start_top) {
            true -> long_min + long_hand
            else ->
                if (long_hand == 0) long_min + long_per_short * short_num - 1
                else long_min + long_hand
        }

    fun short_value(): Int =
        when (short_start_top) {
            true -> short_min + short_hand
            else ->
                if (short_hand == 0) short_min + short_num - 1
                else short_min + short_hand - 1
        }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cx = width.toFloat() / 2f
        val cy = width.toFloat() / 2f
        val radius = min(cx, cy)
        canvas!!.drawCircle(cx, cy, radius, background_paint)
        // draw a mark every short_hand / minute
        for (i in 0..short_num) {
            val alpha = i.toFloat() * (2*pi)/short_num
            canvas.drawLine(
                cx + (radius * 8.25f / 10f) * sin(alpha),
                cx - (radius * 8.25f / 10f) * cos(alpha),
                cx + (radius * 9.75f / 10f) * sin(alpha),
                cx - (radius * 9.75f / 10f) * cos(alpha),
                hatch_paint
            )
            for (j in 1.until(long_per_short)) {
                val beta = alpha + j.toFloat() * (2 * pi) / long_per_short
                canvas.drawLine(
                        cx + (radius * 9.25f / 10f) * sin(beta),
                        cx - (radius * 9.25f / 10f) * cos(beta),
                        cx + (radius * 9.75f / 10f) * sin(beta),
                        cx - (radius * 9.75f / 10f) * cos(beta),
                        hatch_paint
                )
            }
        }
        if (time_text) {
            var text = long_value().toString()
            if (two_handed) {
                if (long_hand < 10) text = "0$text"
                text = short_value().toString() + ":$text"
            }
            text_paint.getTextBounds(text, 0, text.length, text_bounds)
            canvas.drawText(
                    text,
                    cx - text_bounds.width() / 2, cy + (radius - text_bounds.height()) / 2,
                    text_paint
            )
        }
        val long_max = short_num * long_per_short
        val min_x = cx + radius * sin(long_hand.toFloat()*(2*pi)/long_max) * 8f / 10f
        val min_y = cy - radius * cos(long_hand.toFloat()*(2*pi)/long_max) * 8f / 10f
        canvas.drawLine(cx, cy, min_x, min_y, min_paint)
        if (two_handed) {
            val hour_x = cx + radius * sin(short_hand.toFloat() * (2 * pi) / short_num) / 2f
            val hour_y = cy - radius * cos(short_hand.toFloat() * (2 * pi) / short_num) / 2f
            canvas.drawLine(cx, cy, hour_x, hour_y, hour_paint)
        }
    }

    /**
     *
     *
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by [.measure] and
     * should be overridden by subclasses to provide accurate and efficient
     * measurement of their contents.
     *
     *
     *
     *
     * **CONTRACT:** When overriding this method, you
     * *must* call [.setMeasuredDimension] to store the
     * measured width and height of this view. Failure to do so will trigger an
     * `IllegalStateException`, thrown by
     * [.measure]. Calling the superclass'
     * [.onMeasure] is a valid use.
     *
     *
     *
     *
     * The base class implementation of measure defaults to the background size,
     * unless a larger size is allowed by the MeasureSpec. Subclasses should
     * override [.onMeasure] to provide better measurements of
     * their content.
     *
     *
     *
     *
     * If this method is overridden, it is the subclass's responsibility to make
     * sure the measured height and width are at least the view's minimum height
     * and width ([.getSuggestedMinimumHeight] and
     * [.getSuggestedMinimumWidth]).
     *
     *
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent.
     * The requirements are encoded with
     * [android.view.View.MeasureSpec].
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     * The requirements are encoded with
     * [android.view.View.MeasureSpec].
     *
     * @see .getMeasuredWidth
     * @see .getMeasuredHeight
     * @see .setMeasuredDimension
     * @see .getSuggestedMinimumHeight
     * @see .getSuggestedMinimumWidth
     * @see android.view.View.MeasureSpec.getMode
     * @see android.view.View.MeasureSpec.getSize
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desired_width = MeasureSpec.getSize(widthMeasureSpec)
        val desired_height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(desired_width, desired_height)
        setMeasuredDimension(size, size)
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     * the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        val x = event!!.x
        val y = event.y


        if (event.action == MotionEvent.ACTION_UP) {

            moving = false
            moving_hour_hand = false
            moving_minute_hand = false
            hour_paint.color = hand_default
            min_paint.color = hand_default
            hour_paint.strokeWidth = hour_width
            min_paint.strokeWidth = min_width
            invalidate()

        } else {

            val cx = width.toFloat() / 2f
            val cy = height.toFloat() / 2f
            val d = sqrt((x - cx)*(x - cx) + (y - cy)*(y - cy))
            var touch_alpha = atan((y - cy)/(x - cx))
            touch_alpha = if (x > cx)
                pi / 2f + touch_alpha
            else
                3f * pi / 2f + touch_alpha

            if (event.action == MotionEvent.ACTION_DOWN) {

                if (d != 0f) {

                    moving = true

                    val hour_alpha = short_hand.toFloat()*(2*pi)/short_num
                    val min_alpha = long_hand.toFloat()*(2*pi)/(short_num * long_per_short)
                    if (
                            (abs(touch_alpha - min_alpha) < pi/6f) ||
                            (abs(touch_alpha - (min_alpha + 2*pi)) < pi/6f)
                    ) {
                        moving = true
                        moving_minute_hand = true
                        if (!highlight_by_color) min_paint.strokeWidth = highlight_width
                        else min_paint.color = highlight_color
                    } else if (
                            (abs(touch_alpha - hour_alpha) < pi/6f) ||
                            (abs(touch_alpha - (hour_alpha + 2*pi)) < pi/6f)
                    ) {
                        moving = true
                        moving_hour_hand = true
                        if (!highlight_by_color) hour_paint.strokeWidth = highlight_width
                        else hour_paint.color = highlight_color
                    }

                }

            } else if ((event.action == MotionEvent.ACTION_MOVE) and moving) {

                if (moving_hour_hand) {
                    short_hand = round(touch_alpha * short_num / (2f*pi)).toInt()
                } else if (moving_minute_hand) {
                    long_hand = round(touch_alpha * short_num * long_per_short / (2f*pi)).toInt()
                    if (long_hand == long_min + short_num * long_per_short)
                        long_hand = 0
                }
                invalidate()

            }
        }

        return true
    }
}