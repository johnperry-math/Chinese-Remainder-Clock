package name.cantanima.chineseremainderclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class Twohanded_Clock(context: Context, attrs: AttributeSet)
    : View(context, attrs), View.OnTouchListener
{

    val background_color = Color.WHITE
    val highlight_color = Color.RED
    val hand_color = Color.BLACK
    val background_paint = Paint()
    val hour_paint = Paint()
    val min_paint = Paint()
    val hatch_paint = Paint()
    var hour = 12
    var min = 0
    val pi = 3.14159f

    var moving = false
    var moving_hour_hand = false
    var moving_minute_hand = false

    init {
        background_paint.color = background_color
        background_paint.flags = Paint.ANTI_ALIAS_FLAG
        hour_paint.color = hand_color
        hour_paint.strokeWidth = 6f
        hour_paint.flags = Paint.ANTI_ALIAS_FLAG
        min_paint.color = hand_color
        min_paint.strokeWidth = 4f
        min_paint.flags = Paint.ANTI_ALIAS_FLAG
        hatch_paint.color = Color.GRAY
        hatch_paint.strokeWidth = 4f
        hatch_paint.flags = Paint.ANTI_ALIAS_FLAG
        setOnTouchListener(this)
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
        // draw a mark every hour / minute
        for (i in 0..11) {
            val alpha = i.toFloat() * (2*pi)/12f
            canvas.drawLine(
                cx + (radius * 8.25f / 10f) * sin(alpha),
                cx - (radius * 8.25f / 10f) * cos(alpha),
                cx + (radius * 9.75f / 10f) * sin(alpha),
                cx - (radius * 9.75f / 10f) * cos(alpha),
                hatch_paint
            )
            for (j in 1..4) {
                val beta = alpha + j.toFloat() * (2*pi)/60f
                canvas.drawLine(
                    cx + (radius * 9.25f / 10f) * sin(beta),
                    cx - (radius * 9.25f / 10f) * cos(beta),
                    cx + (radius * 9.75f / 10f) * sin(beta),
                    cx - (radius * 9.75f / 10f) * cos(beta),
                    hatch_paint
                )
            }
        }
        val min_x = cx + radius * sin(min.toFloat()*(2*pi)/60f) * 8f / 10f
        val min_y = cy - radius * cos(min.toFloat()*(2*pi)/60f) * 8f / 10f
        canvas.drawLine(cx, cy, min_x, min_y, min_paint)
        val hour_x = cx + radius * sin(hour.toFloat()*(2*pi)/12f) / 2f
        val hour_y = cy - radius * cos(hour.toFloat()*(2*pi)/12f) / 2f
        canvas.drawLine(cx, cy, hour_x, hour_y, hour_paint)
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
            hour_paint.color = hand_color
            min_paint.color = hand_color
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

                    val hour_alpha = hour.toFloat()*(2*pi)/12f
                    val min_alpha = min.toFloat()*(2*pi)/60f
                    if (abs(touch_alpha - hour_alpha) < pi/6f) {
                        moving = true
                        moving_hour_hand = true
                        hour_paint.color = highlight_color
                    } else if (abs(touch_alpha - min_alpha) < pi/6f) {
                        moving = true
                        moving_minute_hand = true
                        min_paint.color = highlight_color
                    }

                }

            } else if ((event.action == MotionEvent.ACTION_MOVE) and moving) {

                if (moving_hour_hand) {
                    hour = round(12f*touch_alpha / (2f*pi)).toInt()
                } else if (moving_minute_hand) {
                    min = round(60f*touch_alpha / (2f*pi)).toInt()
                }
                invalidate()

            }
        }

        return true
    }
}