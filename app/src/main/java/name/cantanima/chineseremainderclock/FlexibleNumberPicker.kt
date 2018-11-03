package name.cantanima.chineseremainderclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlin.math.max

class FlexibleNumberPicker(context: Context, attrs: AttributeSet)
    : View(context, attrs), View.OnTouchListener
{

    val tag = "FlexibleNumberPicker"

    var min   : Int = 0
        set(new_min) {
            field = new_min
            value = (min + max) / 2
        }

    var max   : Int = 10
        set(new_max) {
            field = new_max
            value = (min + max) / 2
        }

    var skip  : Int = 1
    var value : Int = ( max + min ) / 2

    var text_size  = 12f
    var high_size  = 14f
    var typeface   = if (Typeface.DEFAULT != null) Typeface.DEFAULT else null
    var back_color = BLACK
    var text_color = WHITE
    var high_color = YELLOW
    var horizontal = true

    var text_paint = Paint()
    var high_paint = Paint()
    var back_paint = Paint()
    val bounds = Rect()

    var single_width    = 0
    var view_width      = 0
    var view_height     = 0
    var draw_offset     = 0f
    var padding_above   = 5
    var padding_below   = 5
    var padding_left    = 5
    var padding_right   = 5
    var padding_between = 5

    var moving = false
    var xprev  = 0f

    init {

        val resources = context.resources

        val choices = context.obtainStyledAttributes(attrs, R.styleable.FlexibleNumberPicker)
        min   = choices.getInt(R.styleable.FlexibleNumberPicker_min,     min)
        max   = choices.getInt(R.styleable.FlexibleNumberPicker_max,     max)
        skip  = choices.getInt(R.styleable.FlexibleNumberPicker_skip,    skip)
        value = choices.getInt(R.styleable.FlexibleNumberPicker_initial, value)
        // TODO: typeface
        text_size = choices.getFloat(R.styleable.FlexibleNumberPicker_text_size, text_size)
        high_size = choices.getFloat(R.styleable.FlexibleNumberPicker_high_size, high_size)
        back_color = choices.getColor(R.styleable.FlexibleNumberPicker_back_color, back_color)
        high_color = choices.getColor(R.styleable.FlexibleNumberPicker_high_color, high_color)
        text_color = choices.getColor(R.styleable.FlexibleNumberPicker_text_color, text_color)
        horizontal = choices.getBoolean(R.styleable.FlexibleNumberPicker_horizontal, horizontal)
        choices.recycle()

        text_size = TypedValue.applyDimension(COMPLEX_UNIT_SP, text_size, resources.displayMetrics)
        high_size = TypedValue.applyDimension(COMPLEX_UNIT_SP, high_size, resources.displayMetrics)

        text_paint.typeface    = typeface
        text_paint.textSize    = text_size
        text_paint.color       = text_color
        text_paint.isAntiAlias = true

        high_paint.typeface    = typeface
        high_paint.textSize    = high_size
        high_paint.color       = high_color
        high_paint.isAntiAlias = true

        back_paint.color    = back_color
        back_paint.style    = Paint.Style.FILL

        setOnTouchListener(this)

    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawRect(
                0f, 0f,
                view_width.toFloat(),
                view_height.toFloat(),
                back_paint
        )
        canvas.drawText(
                value.toString(),
                view_width / 2f - single_width / 2 + draw_offset,
                (view_height.toFloat() - high_paint.descent() - high_paint.ascent()) / 2,
                high_paint
        )
        if (value > min)
            canvas.drawText(
                    (value - skip).toString(),
                    view_width / 2f - single_width * 3 / 2 + draw_offset,
                    (view_height.toFloat() - text_paint.descent() - text_paint.ascent()) / 2,
                    text_paint
            )
        if (value > min + skip)
            canvas.drawText(
                    (value - 2 * skip).toString(),
                    view_width / 2f - single_width * 5 / 2 + draw_offset,
                    (view_height.toFloat() - text_paint.descent() - text_paint.ascent()) / 2,
                    text_paint
            )
        if (value < max)
            canvas.drawText(
                    " " + (value + skip).toString(),
                    view_width / 2f + single_width / 2 + draw_offset,
                    (view_height.toFloat() - text_paint.descent() - text_paint.ascent()) / 2,
                    text_paint
            )
        if (value < max - skip)
            canvas.drawText(
                    " " + (value + 2 * skip).toString(),
                    view_width / 2f + 3 * single_width / 2 + draw_offset,
                    (view_height.toFloat() - text_paint.descent() - text_paint.ascent()) / 2,
                    text_paint
            )
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
        high_paint.getTextBounds((min * 10).toString(), 0, (min * 10).toString().length, bounds)
        val min_width = bounds.width()
        high_paint.getTextBounds((max * 10).toString(), 0, (max * 10).toString().length, bounds)
        val max_width = bounds.width()
        single_width = max(min_width, max_width)
        view_width  = single_width * 3 + padding_between * 2 + padding_left + padding_right
        view_height = bounds.height() + padding_above + padding_below
        setMeasuredDimension(view_width, view_height)
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
        // let's get some information on where the touch occurred
        val x = event!!.rawX
        val y = event.rawY
        val my_loc = intArrayOf(0, 0)
        getLocationOnScreen(my_loc)

        if (event.action == ACTION_UP) { // released

            moving = false
            Log.d(tag, "up")

        } else if (event.action == ACTION_MOVE) { // moved/dragged

            if (moving) {

                var redraw = true

                Log.d(tag, "move to " + x.toString() + " , " + y.toString())

                draw_offset += x - xprev
                if (draw_offset <= -single_width) {
                    if (value < max)
                        value += skip
                    else
                        redraw = false
                    draw_offset = 0f
                } else if (draw_offset >= single_width) {
                    if (value > min)
                        value -= skip
                    else
                        redraw = false
                    draw_offset = 0f
                }
                xprev = x
                if (redraw) invalidate()
                Log.d(tag, "offset is " + draw_offset.toString())
                Log.d(tag, "value is " + value.toString())

            }

        } else if (event.action == ACTION_DOWN) { // pressed down

            Log.d(tag, "down at " + x.toString() + " , " + y.toString() )
            Log.d(tag, "checking within " +
                    my_loc[0].toString() + "-" + (my_loc[0] + view_width).toString() + " , " +
                    my_loc[1].toString() + "-" + (my_loc[1] + view_height).toString()
            )

            if (
                    (x > my_loc[0]) and (y > my_loc[1]) and
                    (x < my_loc[0] + view_width) and (y < my_loc[1] + view_height)
            ) {
                moving = true
                xprev = x
            }

        }

        return true

    }
}