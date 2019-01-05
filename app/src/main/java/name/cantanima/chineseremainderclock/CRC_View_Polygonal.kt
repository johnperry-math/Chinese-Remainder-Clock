package name.cantanima.chineseremainderclock

import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.view.MotionEvent
import java.lang.Math.*

abstract class CRC_View_Polygonal : Clock_Drawer()
{

    /** arrays that store points to draw the polygons  */
    @JvmField var digi_h3_pts = FloatArray(12) { 0f }
    @JvmField var digi_h4_pts = FloatArray(16) { 0f }
    @JvmField var digi_h8_pts = FloatArray(32) { 0f }
    @JvmField var digi_m3_pts = FloatArray(12) { 0f }
    @JvmField var digi_m4_pts = FloatArray(16) { 0f }
    @JvmField var digi_m5_pts = FloatArray(20) { 0f }
    @JvmField var digi_s3_pts = FloatArray(12) { 0f }
    @JvmField var digi_s4_pts = FloatArray(16) { 0f }
    @JvmField var digi_s5_pts = FloatArray(20) { 0f }

    /** how to paint the polygons  */
    @JvmField val poly_paint = Paint(ANTI_ALIAS_FLAG)

    /** positions  */
    @JvmField var cradius = 0f

    /** fields related to positions of elements  */
    @JvmField var digi_hx = 0f
    @JvmField var digi_mx = 0f
    @JvmField var digi_sx = 0f
    @JvmField var digi_hcy1 = 0f
    @JvmField var digi_hcy2 = 0f
    @JvmField var digi_mscy1 = 0f
    @JvmField var digi_mscy3 = 0f
    @JvmField var obj_w2 = 0f
    @JvmField var moved_offset = 0f

    /** user has touched finger to clock  */
    override fun notify_touched(e: MotionEvent) {
        super.notify_touched(e)
        val x = e.x
        val y = e.y
        dragged_unit = if (abs(digi_hx - x) < obj_w2) {
            if (abs(digi_hcy1 - y) < obj_w2 + cradius && y < digi_hcy1 + obj_w2 / 2 + cradius)
                Clock_Drawer.TOUCHED_UNIT.HOUR3
            else if (abs(digi_hcy2 - y) < obj_w2 + cradius)
                Clock_Drawer.TOUCHED_UNIT.HOURH
            else
                Clock_Drawer.TOUCHED_UNIT.NONE
        } else if (abs(digi_mx - x) < obj_w2) {
            when {
                abs(digi_mscy1 - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.MIN3
                abs(cy - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.MIN4
                abs(digi_mscy3 - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.MIN5
                else -> Clock_Drawer.TOUCHED_UNIT.NONE
            }
        } else if (abs(digi_sx - x) < obj_w2) {
            when {
                abs(digi_mscy1 - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.SEC3
                abs(cy - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.SEC4
                abs(digi_mscy3 - y) < obj_w2 + cradius -> Clock_Drawer.TOUCHED_UNIT.SEC5
                else -> Clock_Drawer.TOUCHED_UNIT.NONE
            }
        } else
            Clock_Drawer.TOUCHED_UNIT.NONE
        move_to(x, y)
    }

    private fun move_to(x: Float, y: Float) {
        val x0: Float
        val y0: Float
        when (dragged_unit) {
            Clock_Drawer.TOUCHED_UNIT.HOUR3 -> {
                x0 = digi_hx
                y0 = digi_hcy1
            }
            Clock_Drawer.TOUCHED_UNIT.HOURH -> {
                x0 = digi_hx
                y0 = digi_hcy2
            }
            Clock_Drawer.TOUCHED_UNIT.MIN3 -> {
                x0 = digi_mx
                y0 = digi_mscy1
            }
            Clock_Drawer.TOUCHED_UNIT.MIN4 -> {
                x0 = digi_mx
                y0 = cy
            }
            Clock_Drawer.TOUCHED_UNIT.MIN5 -> {
                x0 = digi_mx
                y0 = digi_mscy3
            }
            Clock_Drawer.TOUCHED_UNIT.SEC3 -> {
                x0 = digi_sx
                y0 = digi_mscy1
            }
            Clock_Drawer.TOUCHED_UNIT.SEC4 -> {
                x0 = digi_sx
                y0 = cy
            }
            Clock_Drawer.TOUCHED_UNIT.SEC5 -> {
                x0 = digi_sx
                y0 = digi_mscy3
            }
            else -> {
                x0 = cx
                y0 = cy
            }
        }
        moved_offset = atan2((y0 - y).toDouble(), (x0 - x).toDouble()).toFloat()
        moved_offset /= (2 * PI).toFloat()
        if (moved_offset < 0) moved_offset += 1f
        my_viewer.invalidate()
    }

    /**
     * user is dragging finger around the clock
     *
     *
     * The basic implementation does nothing.
     * Your drawer will want to override this, probably to determine the new position
     * of the unit being manipulated.
     */
    override fun notify_dragged(e: MotionEvent) {
        super.notify_dragged(e)
        move_to(e.x, e.y)
    }

    /**
     * user has lifted finger off clock
     *
     *
     * The basic implementation sets dragged_unit to NONE.
     */
    override fun notify_released(e: MotionEvent) {
        val r: Int
        when (dragged_unit) {
            Clock_Drawer.TOUCHED_UNIT.HOUR3 -> {
                r = round(moved_offset * 3 - 0.5f)
                if (my_viewer.hour_modulus == 4) {
                    my_viewer.last_h = (r * 4 - hour % 4 * 3) % 12
                    hour = my_viewer.last_h
                    while (hour < 0) {
                        hour += 12
                        my_viewer.last_h += 12
                    }
                } else {
                    my_viewer.last_h = (r * 16 - hour % 8 * 15) % 24
                    hour = my_viewer.last_h
                    while (hour < 0) {
                        hour += 24
                        my_viewer.last_h += 24
                    }
                }
            }
            Clock_Drawer.TOUCHED_UNIT.HOURH -> if (my_viewer.hour_modulus == 4) {
                r = round(moved_offset * 4 - 1)
                my_viewer.last_h = (-r * 3 + hour % 3 * 4) % 12
                hour = my_viewer.last_h
                while (hour < 0) {
                    hour += 12
                    my_viewer.last_h += 12
                }
            } else {
                r = round(moved_offset * 8 - 2)
                my_viewer.last_h = (-r * 15 + hour % 3 * 16) % 24
                hour = my_viewer.last_h
                while (hour < 0) {
                    hour += 24
                    my_viewer.last_h += 24
                }
            }
            Clock_Drawer.TOUCHED_UNIT.MIN3 -> {
                r = round(moved_offset * 3 - 0.5f)
                my_viewer.last_m = (-r * 20 + minute % 20 * 21) % 60
                minute = my_viewer.last_m
                while (minute < 0) {
                    minute += 60
                    my_viewer.last_m += 60
                }
            }
            Clock_Drawer.TOUCHED_UNIT.MIN4 -> {
                r = round(moved_offset * 4 - 1)
                my_viewer.last_m = (-r * 15 + minute % 15 * 16) % 60
                minute = my_viewer.last_m
                while (minute < 0) {
                    minute += 60
                    my_viewer.last_m += 60
                }
            }
            Clock_Drawer.TOUCHED_UNIT.MIN5 -> {
                r = round(moved_offset * 5 - 1)
                my_viewer.last_m = (-r * 24 + minute % 12 * 25) % 60
                minute = my_viewer.last_m
                while (minute < 0) {
                    minute += 60
                    my_viewer.last_m += 60
                }
            }
            Clock_Drawer.TOUCHED_UNIT.SEC3 -> {
                r = round(moved_offset * 3 - 0.5f)
                my_viewer.last_s = (-r * 20 + second % 20 * 21) % 60
                second = my_viewer.last_s
                while (second < 0) {
                    second += 60
                    my_viewer.last_s += 60
                }
            }
            Clock_Drawer.TOUCHED_UNIT.SEC4 -> {
                r = round(moved_offset * 4 - 1)
                my_viewer.last_s = (-r * 15 + second % 15 * 16) % 60
                second = my_viewer.last_s
                while (second < 0) {
                    second += 60
                    my_viewer.last_s += 60
                }
            }
            Clock_Drawer.TOUCHED_UNIT.SEC5 -> {
                r = round(moved_offset * 5 - 1)
                my_viewer.last_s = (-r * 24 + second % 12 * 25) % 60
                second = my_viewer.last_s
                while (second < 0) {
                    second += 60
                    my_viewer.last_s += 60
                }
            }
            else -> {
            }
        }
        moved_offset = 0f
        super.notify_released(e)
    }

    /**
     *
     * We set up points of the polygons as arrays of floats.
     * Due to the way drawLines() works, intermediate endpoints must be repeated twice.
     * So the number of points to draw is found by shifting the actual number (<< 2).
     * @see android.graphics.Canvas.drawLines
     */
    internal override fun recalculate_positions() {

        super.recalculate_positions()

        // first find basic positions and sizes

        cradius = diam / 28

        val digi_step = diam / 4
        obj_w2 = diam / 4

        digi_hcy1 = cy - obj_w2
        digi_hcy2 = cy + obj_w2
        digi_mscy1 = cy - obj_w2 * 5 / 2
        digi_mscy3 = cy + obj_w2 * 5 / 2

        // the vertices of each polygon are determined using basic trigonometry on a circle; however,
        // in order to have the polygons align in a pleasing way, they the centers of the
        // circumscribing circles may be offset somewhat, hence not all the same

        if (show_seconds) {

            val dir = if (reverse_orientation) -1 else 1

            digi_hx = cx - 2.5f * dir.toFloat() * digi_step
            digi_mx = cx
            digi_sx = cx + 2.5f * dir.toFloat() * digi_step

            digi_h3_pts = floatArrayOf(
                digi_hx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(9 * PI / 6).toFloat(),
				digi_hx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_hx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_hx - obj_w2 * cos(PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_hx - obj_w2 * cos(PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_hx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_hcy1 + obj_w2 * sin(9 * PI / 6).toFloat()
            )

            digi_h4_pts = floatArrayOf(
                digi_hx,
				digi_hcy2 - obj_w2,
				digi_hx + obj_w2,
				digi_hcy2,
				digi_hx + obj_w2,
				digi_hcy2,
				digi_hx,
				digi_hcy2 + obj_w2,
				digi_hx,
				digi_hcy2 + obj_w2,
				digi_hx - obj_w2,
				digi_hcy2,
				digi_hx - obj_w2,
				digi_hcy2,
				digi_hx,
				digi_hcy2 - obj_w2
            )

            digi_h8_pts = floatArrayOf(
                digi_hx - obj_w2 * cos(12 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(12 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(10 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(10 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(10 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(10 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(8 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(8 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(8 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(8 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(6 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(6 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(6 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(6 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(4 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(4 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(4 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(4 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(2 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(2 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(2 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(2 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(0 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(0 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(0 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(0 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(14 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(14 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(14 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(14 * PI / 8).toFloat(),
				digi_hx - obj_w2 * cos(12 * PI / 8).toFloat(),
				digi_hcy2 + obj_w2 * sin(12 * PI / 8).toFloat()
            )

            digi_m3_pts = floatArrayOf(digi_mx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat(),
				digi_mx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_mx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_mx - obj_w2 * cos(PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_mx - obj_w2 * cos(PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_mx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat()
            )

            digi_m4_pts = floatArrayOf(cx,
				cy - obj_w2,
				cx + obj_w2,
				cy,
				cx + obj_w2,
				cy,
				cx,
				cy + obj_w2,
				cx,
				cy + obj_w2,
				digi_mx - obj_w2,
				cy,
				digi_mx - obj_w2,
				cy,
				cx,
				cy - obj_w2)

            digi_m5_pts = floatArrayOf(digi_mx - obj_w2 * cos(15 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(11 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(11 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(7 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(7 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(3 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(3 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(19 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(19 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(),
				digi_mx - obj_w2 * cos(15 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat())

            digi_s3_pts = floatArrayOf(digi_sx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat(),
				digi_sx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_sx - obj_w2 * cos(5 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(),
				digi_sx - obj_w2 * cos(PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_sx - obj_w2 * cos(PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(),
				digi_sx - obj_w2 * cos(9 * PI / 6).toFloat(),
				digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat())

            digi_s4_pts = floatArrayOf(digi_sx,
				cy - obj_w2,
				digi_sx + obj_w2,
				cy,
				digi_sx + obj_w2,
				cy,
				digi_sx,
				cy + obj_w2,
				digi_sx,
				cy + obj_w2,
				digi_sx - obj_w2,
				cy,
				digi_sx - obj_w2,
				cy,
				digi_sx,
				cy - obj_w2)

            digi_s5_pts = floatArrayOf(digi_sx - obj_w2 * cos(15 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(11 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(11 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(7 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(7 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(3 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(3 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(19 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(19 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(),
				digi_sx - obj_w2 * cos(15 * PI / 10).toFloat(),
				digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat())

        } else {

            val dir = if (reverse_orientation) -1f else 1f

            digi_hx = cx - 1.5f * dir * digi_step
            digi_mx = cx + 1.5f * dir * digi_step

            digi_h3_pts = floatArrayOf(digi_hx - obj_w2 * cos(9 * PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(9 * PI / 6).toFloat(), digi_hx - obj_w2 * cos(5 * PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(5 * PI / 6).toFloat(), digi_hx - obj_w2 * cos(5 * PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(5 * PI / 6).toFloat(), digi_hx - obj_w2 * cos(PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(PI / 6).toFloat(), digi_hx - obj_w2 * cos(PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(PI / 6).toFloat(), digi_hx - obj_w2 * cos(9 * PI / 6).toFloat(), digi_hcy1 + obj_w2 * sin(9 * PI / 6).toFloat())

            digi_h4_pts = floatArrayOf(digi_hx, digi_hcy2 - obj_w2, digi_hx + obj_w2, digi_hcy2, digi_hx + obj_w2, digi_hcy2, digi_hx, digi_hcy2 + obj_w2, digi_hx, digi_hcy2 + obj_w2, digi_hx - obj_w2, digi_hcy2, digi_hx - obj_w2, digi_hcy2, digi_hx, digi_hcy2 - obj_w2)

            digi_h8_pts = floatArrayOf(digi_hx - obj_w2 * cos(12 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(12 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(10 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(10 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(10 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(10 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(8 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(8 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(8 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(8 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(6 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(6 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(6 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(6 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(4 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(4 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(4 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(4 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(2 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(2 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(2 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(2 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(0 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(0 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(0 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(0 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(14 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(14 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(14 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(14 * PI / 8).toFloat(), digi_hx - obj_w2 * cos(12 * PI / 8).toFloat(), digi_hcy2 + obj_w2 * sin(12 * PI / 8).toFloat())

            digi_m3_pts = floatArrayOf(digi_mx - obj_w2 * cos(9 * PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat(), digi_mx - obj_w2 * cos(5 * PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(), digi_mx - obj_w2 * cos(5 * PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(5 * PI / 6).toFloat(), digi_mx - obj_w2 * cos(PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(), digi_mx - obj_w2 * cos(PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(PI / 6).toFloat(), digi_mx - obj_w2 * cos(9 * PI / 6).toFloat(), digi_mscy1 + obj_w2 * sin(9 * PI / 6).toFloat())

            digi_m4_pts = floatArrayOf(digi_mx, cy - obj_w2, digi_mx + obj_w2, cy, digi_mx + obj_w2, cy, digi_mx, cy + obj_w2, digi_mx, cy + obj_w2, digi_mx - obj_w2, cy, digi_mx - obj_w2, cy, digi_mx, cy - obj_w2)

            digi_m5_pts = floatArrayOf(digi_mx - obj_w2 * cos(15 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(11 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(11 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(11 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(7 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(7 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(7 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(3 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(3 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(3 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(19 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(19 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(19 * PI / 10).toFloat(), digi_mx - obj_w2 * cos(15 * PI / 10).toFloat(), digi_mscy3 + obj_w2 * sin(15 * PI / 10).toFloat())

        }

        // now set up the paths

        val h_tria = Path()
        h_tria.rewind()
        h_tria.moveTo(digi_h3_pts[0], digi_h3_pts[1])
        h_tria.lineTo(digi_h3_pts[2], digi_h3_pts[3])
        h_tria.lineTo(digi_h3_pts[4], digi_h3_pts[5])
        h_tria.lineTo(digi_h3_pts[6], digi_h3_pts[7])

        val h_quad = Path()
        h_quad.rewind()
        h_quad.moveTo(digi_h4_pts[0], digi_h4_pts[1])
        h_quad.lineTo(digi_h4_pts[2], digi_h4_pts[3])
        h_quad.lineTo(digi_h4_pts[4], digi_h4_pts[5])
        h_quad.lineTo(digi_h4_pts[6], digi_h4_pts[7])
        h_quad.lineTo(digi_h4_pts[8], digi_h4_pts[9])
        h_quad.lineTo(digi_h4_pts[10], digi_h4_pts[11])
        h_quad.lineTo(digi_h4_pts[12], digi_h4_pts[13])
        h_quad.lineTo(digi_h4_pts[14], digi_h4_pts[15])

        val h_octo = Path()
        h_octo.rewind()
        h_octo.moveTo(digi_h8_pts[0], digi_h8_pts[1])
        h_octo.lineTo(digi_h8_pts[2], digi_h8_pts[3])
        h_octo.lineTo(digi_h8_pts[4], digi_h8_pts[5])
        h_octo.lineTo(digi_h8_pts[6], digi_h8_pts[7])
        h_octo.lineTo(digi_h8_pts[8], digi_h8_pts[9])
        h_octo.lineTo(digi_h8_pts[10], digi_h8_pts[11])
        h_octo.lineTo(digi_h8_pts[12], digi_h8_pts[13])
        h_octo.lineTo(digi_h8_pts[14], digi_h8_pts[15])
        h_octo.moveTo(digi_h8_pts[16], digi_h8_pts[17])
        h_octo.lineTo(digi_h8_pts[18], digi_h8_pts[19])
        h_octo.lineTo(digi_h8_pts[20], digi_h8_pts[21])
        h_octo.lineTo(digi_h8_pts[22], digi_h8_pts[23])
        h_octo.lineTo(digi_h8_pts[24], digi_h8_pts[25])
        h_octo.lineTo(digi_h8_pts[26], digi_h8_pts[27])
        h_octo.lineTo(digi_h8_pts[28], digi_h8_pts[29])
        h_octo.lineTo(digi_h8_pts[30], digi_h8_pts[31])

        val m_tria = Path()
        m_tria.rewind()
        m_tria.moveTo(digi_m3_pts[0], digi_m3_pts[1])
        m_tria.lineTo(digi_m3_pts[2], digi_m3_pts[3])
        m_tria.lineTo(digi_m3_pts[4], digi_m3_pts[5])
        m_tria.lineTo(digi_m3_pts[6], digi_m3_pts[7])

        val m_quad = Path()
        m_quad.rewind()
        m_quad.moveTo(digi_m4_pts[0], digi_m4_pts[1])
        m_quad.lineTo(digi_m4_pts[2], digi_m4_pts[3])
        m_quad.lineTo(digi_m4_pts[4], digi_m4_pts[5])
        m_quad.lineTo(digi_m4_pts[6], digi_m4_pts[7])
        m_quad.lineTo(digi_m4_pts[8], digi_m4_pts[9])
        m_quad.lineTo(digi_m4_pts[10], digi_m4_pts[11])
        m_quad.lineTo(digi_m4_pts[12], digi_m4_pts[13])
        m_quad.lineTo(digi_m4_pts[14], digi_m4_pts[15])

        val m_pent = Path()
        m_pent.rewind()
        m_pent.moveTo(digi_m5_pts[0], digi_m5_pts[1])
        m_pent.lineTo(digi_m5_pts[2], digi_m5_pts[3])
        m_pent.lineTo(digi_m5_pts[4], digi_m5_pts[5])
        m_pent.lineTo(digi_m5_pts[6], digi_m5_pts[7])
        m_pent.lineTo(digi_m5_pts[8], digi_m5_pts[9])
        m_pent.lineTo(digi_m5_pts[10], digi_m5_pts[11])
        m_pent.lineTo(digi_m5_pts[12], digi_m5_pts[13])
        m_pent.lineTo(digi_m5_pts[14], digi_m5_pts[15])
        m_pent.lineTo(digi_m5_pts[16], digi_m5_pts[17])
        m_pent.lineTo(digi_m5_pts[18], digi_m5_pts[19])

        val s_tria = Path()
        val s_quad = Path()
        val s_pent = Path()

        if (show_seconds) {

            s_tria.rewind()
            s_tria.moveTo(digi_s3_pts[0], digi_s3_pts[1])
            s_tria.lineTo(digi_s3_pts[2], digi_s3_pts[3])
            s_tria.lineTo(digi_s3_pts[4], digi_s3_pts[5])
            s_tria.lineTo(digi_s3_pts[6], digi_s3_pts[7])

            s_quad.rewind()
            s_quad.moveTo(digi_s4_pts[0], digi_s4_pts[1])
            s_quad.lineTo(digi_s4_pts[2], digi_s4_pts[3])
            s_quad.lineTo(digi_s4_pts[4], digi_s4_pts[5])
            s_quad.lineTo(digi_s4_pts[6], digi_s4_pts[7])
            s_quad.lineTo(digi_s4_pts[8], digi_s4_pts[9])
            s_quad.lineTo(digi_s4_pts[10], digi_s4_pts[11])
            s_quad.lineTo(digi_s4_pts[12], digi_s4_pts[13])
            s_quad.lineTo(digi_s4_pts[14], digi_s4_pts[15])

            s_pent.rewind()
            s_pent.moveTo(digi_s5_pts[0], digi_s5_pts[1])
            s_pent.lineTo(digi_s5_pts[2], digi_s5_pts[3])
            s_pent.lineTo(digi_s5_pts[4], digi_s5_pts[5])
            s_pent.lineTo(digi_s5_pts[6], digi_s5_pts[7])
            s_pent.lineTo(digi_s5_pts[8], digi_s5_pts[9])
            s_pent.lineTo(digi_s5_pts[10], digi_s5_pts[11])
            s_pent.lineTo(digi_s5_pts[12], digi_s5_pts[13])
            s_pent.lineTo(digi_s5_pts[14], digi_s5_pts[15])
            s_pent.lineTo(digi_s5_pts[16], digi_s5_pts[17])
            s_pent.lineTo(digi_s5_pts[18], digi_s5_pts[19])

        }

    }

}