package name.cantanima.chineseremainderclock

import android.graphics.*
import android.graphics.Color.*
import name.cantanima.chineseremainderclock.CRC_View.Modification
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
            paint.strokeWidth = 10f
            paint.color = WHITE
            paint.style = Paint.Style.STROKE
            canvas.drawPath(edge, paint)
            val time_radius = radius * 9f / 10f
            paint.strokeWidth = 3f
            paint.color = mod3_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (h in 1.until(3)) {
                val hangle = 2f * PI / 3 * h - PI / 2f
                canvas.drawText("$h", cx + time_radius * cos(hangle).toFloat(), cy + time_radius * sin(hangle).toFloat(), paint)
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(hour3, paint)
            canvas.drawPath(min3, paint)
            if (show_seconds) canvas.drawPath(sec3, paint)
            paint.color = mod4_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (h in 1.until(my_viewer.hour_modulus)) {
                val hangle = 2f * PI / my_viewer.hour_modulus * h - PI / 2f
                canvas.drawText("$h", cx + time_radius * cos(hangle).toFloat(), cy + time_radius * sin(hangle).toFloat(), paint)
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(hour4, paint)
            canvas.drawPath(min4, paint)
            if (show_seconds) canvas.drawPath(sec4, paint)
            paint.color = mod5_color
            paint.style = Paint.Style.FILL_AND_STROKE
            for (h in 1.until(5)) {
                val hangle = 2f * PI / 5 * h - PI / 2f
                canvas.drawText("$h", cx + time_radius * cos(hangle).toFloat(), cy + time_radius * sin(hangle).toFloat(), paint)
            }
            paint.style = Paint.Style.STROKE
            canvas.drawPath(min5, paint)
            if (show_seconds) canvas.drawPath(sec5, paint)
        }
        usual_cleanup()
    }

    override fun recalculate_positions() {
        setup_time()
        super.recalculate_positions()
        radius = if (w < h) w / 2f else h / 2f
        edge = Path()
        edge.addCircle(cx, cy, radius * 19f / 20f, Path.Direction.CW)
        edge.close()
        val offset = minOf(my_viewer.my_offset, 1f)
        val h3 = if (hour % 3 == 0) 3 else hour % 3
        val h4 = if (hour % my_viewer.hour_modulus == 0) my_viewer.hour_modulus else hour % my_viewer.hour_modulus
        val lh3 = my_viewer.last_h % 3
        val lh4 = my_viewer.last_h % my_viewer.hour_modulus
        val hangle3: Double
        val hangle4 : Double
        if (hour == my_viewer.last_h) {
            hangle3 = 2 * PI / 3 * h3 - PI / 2
            hangle4 = 2 * PI / my_viewer.hour_modulus * h4 - PI / 2
        } else {
            hangle3 = 2 * Math.PI / 3 * (h3 * offset + lh3 * (1 - offset)) - Math.PI / 2
            hangle4 = 2 * Math.PI / my_viewer.hour_modulus * (h4 * offset + lh4 * (1 - offset)) - Math.PI / 2
        }
        val hrad = radius / 2f
        hour3 = hand_box(cx, cy, hangle3.toFloat(), 10f, hrad)
        hour4 = hand_box(cx, cy, hangle4.toFloat(), 12f, hrad)
        val m3 = if (minute % 3 == 0) 3 else minute % 3
        val m4 = if (minute % 4 == 0) 4 else minute % 4
        val m5 = if (minute % 5 == 0) 5 else minute % 5
        val lm3 = my_viewer.last_m % 3
        val lm4 = my_viewer.last_m % 4
        val lm5 = my_viewer.last_m % 5
        val mangle3: Double
        val mangle4: Double
        val mangle5: Double
        if (my_viewer.last_m == minute) {
            mangle3 = 2 * PI / 3 * m3 - PI / 2
            mangle4 = 2 * PI / 4 * m4 - PI / 2
            mangle5 = 2 * PI / 5 * m5 - PI / 2
        } else {
            mangle3 = 2 * PI / 3 * (m3 * offset + lm3 * (1 - offset)) - PI / 2
            mangle4 = 2 * PI / 4 * (m4 * offset + lm4 * (1 - offset)) - PI / 2
            mangle5 = 2 * PI / 5 * (m5 * offset + lm5 * (1 - offset)) - PI / 2
        }
        val mrad = radius * 3f / 4f
        min3 = hand_box(cx, cy, mangle3.toFloat(), 20f, mrad)
        min4 = hand_box(cx, cy, mangle4.toFloat(), 30f, mrad)
        min5 = hand_box(cx, cy, mangle5.toFloat(), 40f, mrad)
        val s3 = if (second % 3 == 0) 3 else second % 3
        val s4 = if (second % 4 == 0) 4 else second % 4
        val s5 = if (second % 5 == 0) 5 else second % 5
        val ls3 = my_viewer.last_s % 3
        val ls4 = my_viewer.last_s % 4
        val ls5 = my_viewer.last_s % 5
        val sangle3: Double
        val sangle4: Double
        val sangle5: Double
        if (my_viewer.last_s == second) {
            sangle3 = 2 * PI / 3 * s3 - PI / 2
            sangle4 = 2 * PI / 4 * s4 - PI / 2
            sangle5 = 2 * PI / 5 * s5 - PI / 2
        } else {
            sangle3 = 2 * PI / 3 * (s3 * offset + ls3 * (1 - offset)) - PI / 2
            sangle4 = 2 * PI / 4 * (s4 * offset + ls4 * (1 - offset)) - PI / 2
            sangle5 = 2 * PI / 5 * (s5 * offset + ls5 * (1 - offset)) - PI / 2
        }
        val srad = radius * 4f / 5f
        sec3 = hand_box(cx, cy, sangle3.toFloat(), 2f, srad)
        sec4 = hand_box(cx, cy, sangle4.toFloat(), 2f, srad)
        sec5 = hand_box(cx, cy, sangle5.toFloat(), 2f, srad)
    }
    
    fun hand_box(cx: Float, cy: Float, angle: Float, width: Float, length: Float): Path {
        val result = Path()
        val dx = cx + length * cos(angle)
        val dy = cy + length * sin(angle)
        if (cx == dx) { // vertical
            result.moveTo(cx - width / 2f, cy)
            result.lineTo(cx + width / 2f, cy)
            result.lineTo(dx + width / 2f, dy)
            result.lineTo(dx - width / 2f, dy)
            result.close()
        } else if (dy == cy) {
            result.moveTo(cx, cy - width / 2f)
            result.lineTo(cx, cy + width / 2f)
            result.lineTo(dx, dy + width / 2f)
            result.lineTo(dx, dy - width / 2f)
            result.close()
        } else {
            result.moveTo(cx - width * cos(PI.toFloat() / 2f - angle), cy + width * sin(PI.toFloat() / 2f - angle))
            result.lineTo(cx + width * cos(PI.toFloat() / 2f - angle), cy - width * sin(PI.toFloat() / 2f - angle))
            result.lineTo(dx + width * cos(PI.toFloat() / 2f - angle), dy - width * sin(PI.toFloat() / 2f - angle))
            result.lineTo(dx - width * cos(PI.toFloat() / 2f - angle), dy + width * sin(PI.toFloat() / 2f - angle))
            result.close()
        }
        return result
    }

    override fun preferred_step(): Float {
        return 0.15f
    }

    var radius = 0f
    lateinit var edge: Path
    lateinit var hour3: Path
    lateinit var hour4: Path
    lateinit var min3: Path
    lateinit var min4: Path
    lateinit var min5: Path
    lateinit var sec3: Path
    lateinit var sec4: Path
    lateinit var sec5: Path
    
    val mod3_color = GREEN
    val mod4_color = BLUE
    val mod5_color = RED

    @Suppress("Unused", "PropertyName")
    val TAG = "CRC_View_Handy"
}