package name.cantanima.chineseremainderclock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlin.math.*

class NumberArray(context: Context, attrs: AttributeSet)
    : View(context, attrs), View.OnTouchListener
{
    
    var rows: Int = 3
    var columns: Int = 4
    var start: Int = 0
    var num: Int = 12
    var which_num_chosen = 0

    var text_size  = 12f
    var typeface   = if (Typeface.DEFAULT != null) Typeface.DEFAULT else null
    var back_color = Color.BLACK
    var text_color = Color.WHITE
    var high_color = Color.YELLOW
    var horizontal = true

    var text_paint = Paint()
    var high_paint = Paint()
    var back_paint = Paint()
    val bounds = Rect()

    var digit_width     = 0
    var digit_height    = 0
    var number_width    = 0
    var view_width      = 0
    var view_height     = 0
    var num_digits      = 0
    var pad_with_zeros  = true

    var moving = false

    init {

        val resources = context.resources

        val choices = context.obtainStyledAttributes(attrs, R.styleable.NumberArray)
        // TODO: typeface
        text_size = choices.getFloat(R.styleable.NumberArray_text_size, text_size)
        back_color = choices.getColor(R.styleable.NumberArray_back_color, back_color)
        high_color = choices.getColor(R.styleable.NumberArray_high_color, high_color)
        text_color = choices.getColor(R.styleable.NumberArray_text_color, text_color)
        rows = choices.getInt(R.styleable.NumberArray_rows, rows)
        columns = choices.getInt(R.styleable.NumberArray_columns, columns)
        start = choices.getInt(R.styleable.NumberArray_start, start)
        which_num_chosen = start
        num = choices.getInt(R.styleable.NumberArray_number, num)
        num_digits = max(numberOfDigits(start), numberOfDigits((start + num)))
        pad_with_zeros = choices.getBoolean(R.styleable.NumberArray_pad_zeros, pad_with_zeros)
        choices.recycle()

        text_size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, text_size, resources.displayMetrics)

        text_paint.typeface    = typeface
        text_paint.textSize    = text_size
        text_paint.color       = text_color
        text_paint.isAntiAlias = true

        high_paint.typeface    = typeface
        high_paint.textSize    = text_size
        high_paint.color       = high_color
        high_paint.isAntiAlias = true

        back_paint.color    = back_color
        back_paint.style    = Paint.Style.FILL

        setOnTouchListener(this)

    }

    fun numberOfDigits(x: Int): Int =
            when {

                x < 0 -> truncate(log10((-x).toFloat())).toInt() + 2
                x == 0 -> 1
                else -> truncate(log10(x.toFloat())).toInt()+1
            }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawRect(0f, 0f, view_width.toFloat(), view_height.toFloat(), back_paint)
        var i = 0
        var j = 0
        var a = start
        while (a < start + num) {
            var x = ( number_width + digit_width ) * j + ( digit_width / 2 )
            val y = digit_height * (i + 1)
            if (pad_with_zeros) {
                for (k in 1..(num_digits - numberOfDigits(a))) {
                    canvas.drawText(" ", x.toFloat(), y.toFloat(),
                            if (a == which_num_chosen) high_paint else text_paint
                    )
                    x += digit_width
                }
            }
            canvas.drawText(a.toString(), x.toFloat(), y.toFloat(),
                    if (a == which_num_chosen) high_paint else text_paint
            )
            j += 1
            a += 1
            if (j % columns == 0) {
                j = 0
                i += 1
            }
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
        high_paint.getTextBounds("9", 0, start.toString().length, bounds)
        digit_width = bounds.width()
        digit_height = bounds.height() + high_paint.descent().toInt()
        high_paint.getTextBounds((start + num).toString(), 0, (start + num).toString().length, bounds)
        number_width = bounds.width()
        view_width = (digit_width + number_width) * columns
        view_height = digit_height * rows + ( digit_height / 2)
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

        val x = event!!.x
        val y = event.y

        if (event.action == ACTION_UP) {

            moving = false

        } else if (((event.action == ACTION_MOVE) and moving) or (event.action == ACTION_DOWN)) {

            which_num_chosen = start +
                    truncate( x / (number_width + digit_width).toFloat() ).toInt() +
                    truncate(y / digit_height.toFloat()).toInt() * columns

            invalidate()

            moving = true

        }

        return true
    }

}