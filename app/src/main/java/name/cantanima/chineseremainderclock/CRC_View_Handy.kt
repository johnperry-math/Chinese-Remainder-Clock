package name.cantanima.chineseremainderclock

import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import java.util.*
import kotlin.math.*

class CRC_View_Handy(owner: CRC_View) : Clock_Drawer() {

    init {
        super.initialize_fields(owner)
        stringID = R.string.handy_manual_hint
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) {
            // default setup
            if (!manual_mode) {
                setup_time()
                drawTimeAndRectangle(canvas, hour, minute, second, diam)
            } else {
                drawTimeAndRectangle(canvas, my_viewer.last_h, my_viewer.last_m, my_viewer.last_s, diam)
            }
            recalculate_positions()
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = radius / 10f
            val text_offset = paint.textSize / 3
            paint.strokeWidth = 10f
            paint.color = WHITE
            paint.style = Paint.Style.STROKE
            canvas.drawPath(edge, paint)
            val time_radius = radius * 17f / 20f
            paint.strokeWidth = 3f
            paint.color = mod3_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (h in 1.until(3)) {
                val hangle = 2f * PI / 3 * h - PI / 2f
                canvas.drawText(
                    "$h",
                    cx + time_radius * cos(hangle).toFloat(),
                    cy + time_radius * sin(hangle).toFloat() + text_offset,
                    paint
                )
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(hour3, paint)
            canvas.drawPath(min3, paint)
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawPath(hour_hand3, paint)
            canvas.drawPath(min_hand3, paint)
            if (show_seconds) canvas.drawPath(sec3, paint)
            paint.color = mod4_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (m in 1.until(my_viewer.hour_modulus)) {
                val hangle = 2f * PI / my_viewer.hour_modulus * m - PI / 2f
                canvas.drawText(
                    "$m",
                    cx + time_radius * cos(hangle).toFloat(),
                    cy + time_radius * sin(hangle).toFloat() + text_offset,
                    paint
                )
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(hour4, paint)
            canvas.drawPath(min4, paint)
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawPath(hour_hand4, paint)
            canvas.drawPath(min_hand4, paint)
            if (show_seconds) canvas.drawPath(sec4, paint)
            paint.color = mod5_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (s in 1.until(5)) {
                val hangle = 2f * PI / 5 * s - PI / 2f
                canvas.drawText(
                    "$s",
                    cx + time_radius * cos(hangle).toFloat(),
                    cy + time_radius * sin(hangle).toFloat() + text_offset,
                    paint
                )
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(min5, paint)
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawPath(min_hand5, paint)
            if (show_seconds) canvas.drawPath(sec5, paint)
        }
        usual_cleanup()
    }

    fun angle(value: Int, old_value: Int, offset: Float, modulus: Int, unchanging_time: Boolean): Float {
        return if (unchanging_time) {
            (2 * PI / modulus * value - PI / 2).toFloat()
        } else {
            (2 * PI / modulus * (value * offset + old_value * (1 - offset)) - PI / 2).toFloat()
        }
    }
    
    override fun recalculate_positions() {
        setup_time()
        super.recalculate_positions()
        mod4_color = hour_color
        mod3_color = second_color
        mod5_color = minute_color
        radius = min(w / 2f, h / 2f)
        hrad = radius / 2f
        mrad = radius * 7f / 10f
        srad = radius * 3f / 4f
        hand_width = radius / 20f
        edge = Path()
        edge.addCircle(cx, cy, radius * 19f / 20f, Path.Direction.CW)
        edge.close()
        val offset = min(my_viewer.my_offset, 1f)
        if (dragging) {
            when (dragged_unit) {
                TOUCHED_UNIT.HOUR3 -> {
                    val h3 = round((new_angle + PI/2)/(2*PI/3)).toInt()
                    hour = if (my_viewer.hour_modulus == 4) (4*h3 + 9*(hour % 4)) % 12
                    else (16*h3 + 9*(hour % 4)) % 24
                }
                TOUCHED_UNIT.HOURH -> {
                    val h4 = round((new_angle + PI/2)/(2*PI/my_viewer.hour_modulus)).toInt()
                    hour = if (my_viewer.hour_modulus == 4) (4*(hour % 3) + 9*h4) % 12
                    else (16*(hour % 3) + 9*h4) % 24
                }
                TOUCHED_UNIT.MIN3 -> {
                    val m3 = round((new_angle + PI/2)/(2*PI/3)).toInt()
                    minute = (m3 * 40 + (minute % 4) * 45 + (minute % 5) * 36) % 60
                }
                TOUCHED_UNIT.MIN4 -> {
                    val m4 = round((new_angle + PI/2)/(2*PI/4)).toInt()
                    minute = ((minute % 3) * 40 + m4 * 45 + (minute % 5) * 36) % 60
                }
                TOUCHED_UNIT.MIN5 -> {
                    val m5 = round((new_angle + PI/2)/(2*PI/5)).toInt()
                    minute = ((minute % 3) * 40 + (minute % 4) * 45 + m5 * 36) % 60
                }
                TOUCHED_UNIT.SEC3 -> {
                    val s3 = round((new_angle + PI/2)/(2*PI/3)).toInt()
                    second = (s3 * 40 + (second % 4) * 45 + (second % 5) * 36) % 60
                }
                TOUCHED_UNIT.SEC4 -> {
                    val s4 = round((new_angle + PI/2)/(2*PI/4)).toInt()
                    second = ((second % 3) * 40 + s4 * 45 + (second % 5) * 36) % 60
                }
                TOUCHED_UNIT.SEC5 -> {
                    val s5 = round((new_angle + PI/2)/(2*PI/5)).toInt()
                    second = ((second % 3) * 40 + (second % 4) * 45 + s5 * 36) % 60
                }
                else -> {}
            }
            while (hour < 0)
                hour += if (my_viewer.hour_modulus == 4) 12 else 24
            while (minute < 0) minute %= 60
            while (second < 0) second %= 60
            my_viewer.last_h = hour
            my_viewer.last_m = minute
            my_viewer.last_s = second
        }
        val h3 = if (hour % 3 == 0) 3 else hour % 3
        val h4 = if (hour % my_viewer.hour_modulus == 0) my_viewer.hour_modulus else hour % my_viewer.hour_modulus
        val lh3 = my_viewer.last_h % 3
        val lh4 = my_viewer.last_h % my_viewer.hour_modulus
        val hangle3 = angle(h3, lh3, offset, 3, hour == my_viewer.last_h)
        val hangle4 = angle(h4, lh4, offset, my_viewer.hour_modulus, hour == my_viewer.last_h)
        hour3 = hand_box(hangle3, hand_width, hrad, 1)
        hour_hand3 = hand_box(hangle3, hand_width, hrad, 2)
        hour4 = hand_box(hangle4, hand_width, hrad, 1)
        hour_hand4 = hand_box(hangle4, hand_width, hrad, 3)
        val m3 = if (minute % 3 == 0) 3 else minute % 3
        val m4 = if (minute % 4 == 0) 4 else minute % 4
        val m5 = if (minute % 5 == 0) 5 else minute % 5
        val lm3 = my_viewer.last_m % 3
        val lm4 = my_viewer.last_m % 4
        val lm5 = my_viewer.last_m % 5
        val mangle3 = angle(m3, lm3, offset, 3, minute == my_viewer.last_m)
        val mangle4 = angle(m4, lm4, offset, 4, minute == my_viewer.last_m)
        val mangle5 = angle(m5, lm5, offset, 5, minute == my_viewer.last_m)
        min3 = hand_box(mangle3, hand_width, mrad, 1)
        min_hand3 = hand_box(mangle3, hand_width, mrad, 2)
        min4 = hand_box(mangle4, hand_width, mrad, 1)
        min_hand4 = hand_box(mangle4, hand_width, mrad, 4)
        min5 = hand_box(mangle5, hand_width, mrad, 1)
        min_hand5 = hand_box(mangle5, hand_width, mrad, 8)
        val s3 = if (second % 3 == 0) 3 else second % 3
        val s4 = if (second % 4 == 0) 4 else second % 4
        val s5 = if (second % 5 == 0) 5 else second % 5
        val ls3 = my_viewer.last_s % 3
        val ls4 = my_viewer.last_s % 4
        val ls5 = my_viewer.last_s % 5
        val sangle3 = angle(s3, ls3, offset, 3, second == my_viewer.last_s)
        val sangle4 = angle(s4, ls4, offset, 4, second == my_viewer.last_s)
        val sangle5 = angle(s5, ls5, offset, 5, second == my_viewer.last_s)
        sec3 = hand_box(sangle3, 1f, srad, 1)
        sec4 = hand_box(sangle4, 1f, srad, 1)
        sec5 = hand_box(sangle5, 1f, srad, 1)
        if (dragging) {
            val dragged_path = when (dragged_unit) {
                in arrayOf(TOUCHED_UNIT.HOUR3, TOUCHED_UNIT.HOURH) -> {
                    hand_box(new_angle, hand_width, hrad, 1)
                }
                in arrayOf(TOUCHED_UNIT.MIN3, TOUCHED_UNIT.MIN4, TOUCHED_UNIT.MIN5) -> {
                    hand_box(new_angle, hand_width, mrad, 1)
                }
                else -> {
                    hand_box(new_angle, hand_width, srad, 1)
                }
            }
            when (dragged_unit) {
                TOUCHED_UNIT.HOUR3 -> {
                    hour3 = dragged_path
                    hour_hand3 = hand_box(new_angle, hand_width, hrad, 2)
                }
                TOUCHED_UNIT.HOURH -> {
                    hour4 = dragged_path
                    hour_hand4 = hand_box(new_angle, hand_width, hrad, 3)
                }
                TOUCHED_UNIT.MIN3 -> {
                    min3 = dragged_path
                    min_hand3 = hand_box(new_angle, hand_width, mrad, 2)
                }
                TOUCHED_UNIT.MIN4 -> {
                    min4 = dragged_path
                    min_hand4 = hand_box(new_angle, hand_width, mrad, 4)
                }
                TOUCHED_UNIT.MIN5 -> {
                    min5 = dragged_path
                    min_hand5 = hand_box(new_angle, hand_width, mrad, 8)
                }
                TOUCHED_UNIT.SEC3 -> sec3 = dragged_path
                TOUCHED_UNIT.SEC4 -> sec4 = dragged_path
                TOUCHED_UNIT.SEC5 -> sec5 = dragged_path
                else -> {}
            }
        }
    }

    /**
     * draws a box of given `width`, at a distance of `outer_radius` from the center
     * to a distance of `outer_distance` * (1 - 1/`inner_ratio`) from the center
     */
    fun hand_box(angle: Float, width: Float, outer_radius: Float, inner_ratio: Int): Path {
        val result = Path()
        val dx = cx + outer_radius * cos(angle)
        val dy = cy + outer_radius * sin(angle)
        val sx = cx + (outer_radius * (1f - 1f/inner_ratio.toFloat())) * cos(angle)
        val sy = cy + (outer_radius * (1f - 1f/inner_ratio.toFloat())) * sin(angle)
        if (sx == dx) { // vertical
            result.moveTo(sx - width / 2f, sy)
            result.lineTo(sx + width / 2f, sy)
            result.lineTo(dx + width / 2f, dy)
            result.lineTo(dx - width / 2f, dy)
            result.close()
        } else if (sy == dy) {
            result.moveTo(sx, sy - width / 2f)
            result.lineTo(sx, sy + width / 2f)
            result.lineTo(dx, dy + width / 2f)
            result.lineTo(dx, dy - width / 2f)
            result.close()
        } else {
            result.moveTo(sx - width / 2f * cos(PI.toFloat() / 2f - angle), sy + width / 2f * sin(PI.toFloat() / 2f - angle))
            result.lineTo(sx + width / 2f * cos(PI.toFloat() / 2f - angle), sy - width / 2f * sin(PI.toFloat() / 2f - angle))
            result.lineTo(dx + width / 2f * cos(PI.toFloat() / 2f - angle), dy - width / 2f * sin(PI.toFloat() / 2f - angle))
            result.lineTo(dx - width / 2f * cos(PI.toFloat() / 2f - angle), dy + width / 2f * sin(PI.toFloat() / 2f - angle))
            result.close()
        }
        return result
    }

    override fun preferred_step(): Float {
        return 0.15f
    }

    fun matches(x: Float, y: Float, touch_radius: Float, time: Int, last_time: Int, offset: Float, modulus: Int): Boolean {
        val mod_time = time % modulus
        val mod_last = last_time % modulus
        val time_angle = angle(mod_time, mod_last, offset, modulus, time == last_time)
        val time_x = cx + touch_radius * cos(time_angle)
        val time_y = cy + touch_radius * sin(time_angle)
        return sqrt((x - time_x).pow(2) + (y - time_y).pow(2)) < 20
    }

    override fun notify_touched(e: MotionEvent?) {
        if (e != null) {
            var searching = true
            val x = e.x
            val y = e.y
            val touch_radius = (sqrt((cx - x).pow(2) + (cy - y).pow(2)))
            val offset = minOf(1f, my_viewer.my_offset)
            if (matches(x, y, touch_radius, hour, my_viewer.last_h, offset, 3) && touch_radius <= hrad) {
                dragged_unit = TOUCHED_UNIT.HOUR3
                searching = false
            } else if (matches(x, y, touch_radius, hour, my_viewer.last_h, offset, my_viewer.hour_modulus) && touch_radius <= hrad) {
                dragged_unit = TOUCHED_UNIT.HOURH
                searching = false
            } else if (matches(x, y, touch_radius, minute, my_viewer.last_m, offset, 3)) {
                dragged_unit = TOUCHED_UNIT.MIN3
                searching = false
            } else if (matches(x, y, touch_radius, minute, my_viewer.last_m, offset, 4)) {
                dragged_unit = TOUCHED_UNIT.MIN4
                searching = false
            } else if (matches(x, y, touch_radius, minute, my_viewer.last_m, offset, 5)) {
                dragged_unit = TOUCHED_UNIT.MIN5
                searching = false
            } else if (matches(x, y, touch_radius, second, my_viewer.last_s, offset, 3)) {
                dragged_unit = TOUCHED_UNIT.SEC3
                searching = false
            } else if (matches(x, y, touch_radius, second, my_viewer.last_s, offset, 4)) {
                dragged_unit = TOUCHED_UNIT.SEC4
                searching = false
            } else if (matches(x, y, touch_radius, second, my_viewer.last_s, offset, 5)) {
                dragged_unit = TOUCHED_UNIT.SEC5
                searching = false
            }

            if (!searching) {
                dragging = true
                step = 0f
            }
        }
    }

    override fun notify_released(e: MotionEvent?) {

        if (dragging) {
            dragging = false
            step = 0.04f
            if (dragged_unit != null) {
                when (dragged_unit) {
                    TOUCHED_UNIT.HOUR3, TOUCHED_UNIT.MIN3, TOUCHED_UNIT.SEC3 -> {
                        last_mod = round(3f / (2f * PI) * new_angle  + 3f / 4f).toInt()
                        if (last_mod < 0) last_mod += 3
                    }
                    TOUCHED_UNIT.HOURH -> {
                        last_mod = round((my_viewer.hour_modulus) / (6f * PI) * new_angle  + my_viewer.hour_modulus / 12f).toInt()
                        if (last_mod < 0) last_mod += my_viewer.hour_modulus
                    }
                    TOUCHED_UNIT.MIN4, TOUCHED_UNIT.SEC4 -> {
                        last_mod = round(2f / PI * new_angle + 1f).toInt()
                        if (last_mod < 0) last_mod += 4

                    }
                    TOUCHED_UNIT.MIN5, TOUCHED_UNIT.SEC5 -> {
                        last_mod = round(5f / (2f * PI) * new_angle + 5f / 4f).toInt()
                        if (last_mod < 0) last_mod += 5
                    }
                    else -> TODO()
                }
            }
        }
        just_released = true
        my_viewer.invalidate()

    }

    override fun notify_dragged(e: MotionEvent?) {
        if (e != null) {
            val new_millis = Calendar.getInstance()[Calendar.MILLISECOND]
            // position is determined using basic trigonometry
            // (arctangent of y/x after translated to center of View)
            // position is determined using basic trigonometry
            // (arctangent of y/x after translated to center of View)
            if (new_millis - millis > 100 || millis - new_millis > 0) {
                val x = e.x
                val y = e.y
                new_angle = atan2((y - cy).toDouble(), (x - cx).toDouble()).toFloat()
                my_viewer.invalidate()
            }

        }
    }

    // distances (to be initialized later)
    var radius = 0f
    var hand_width = 0f
    var hrad = 0f
    var mrad = 0f
    var srad = 0f

    // paths (to be initialized later)
    lateinit var edge: Path         // this should be the outer circle
    lateinit var hour3: Path
    lateinit var hour4: Path
    lateinit var min3: Path
    lateinit var min4: Path
    lateinit var min5: Path
    lateinit var sec3: Path
    lateinit var sec4: Path
    lateinit var sec5: Path
    lateinit var hour_hand3: Path   // the _hand variables draw the solid box
    lateinit var hour_hand4: Path
    lateinit var min_hand3: Path
    lateinit var min_hand4: Path
    lateinit var min_hand5: Path

    // colors of hands and labels
    var mod3_color = GREEN
    var mod4_color = BLUE
    var mod5_color = RED

    // animation
    var step = 0.4f
    var new_angle = 0f

    // manipulation
    var dragging = false
    var last_mod = 0
    var just_released = false

    // debugging
    @Suppress("Unused", "PropertyName")
    val TAG = "CRC_View_Handy"
}