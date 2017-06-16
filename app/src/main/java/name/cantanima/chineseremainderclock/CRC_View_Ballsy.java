package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Units.HOURS;

/**
 * Created by cantanima on 6/5/17.
 */

public class CRC_View_Ballsy extends Clock_Drawer {

    // constructor
    public CRC_View_Ballsy(CRC_View owner) {

        initialize_fields(owner);

    }

    float preferred_step() { return 0.04f; }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void draw(Canvas canvas) {

        setup_time();

        drawTimeAndRectangle(canvas, hour, minute, second, cx, cy, diam, my_viewer.analog);

        // in what follows, xmodi is the time unit x modulo i, while
        // lxmodi is last_x % i

        int lhmod3 = my_viewer.last_h % 3;
        int lhmod4 = my_viewer.last_h % my_viewer.hour_modulus;
        int hmod3 = hour % 3;
        int hmod4 = hour % my_viewer.hour_modulus;
        if (hmod3 == 0) hmod3 = 3;
        if (hmod4 == 0) hmod4 = my_viewer.hour_modulus;

        int lmmod3 = my_viewer.last_m % 3;
        int lmmod4 = my_viewer.last_m % 4;
        int lmmod5 = my_viewer.last_m % 5;
        int mmod3 = minute % 3;
        int mmod4 = minute % 4;
        int mmod5 = minute % 5;
        if (mmod3 == 0) mmod3 = 3;
        if (mmod4 == 0) mmod4 = 4;
        if (mmod5 == 0) mmod5 = 5;

        int lsmod3 = my_viewer.last_s % 3;
        int lsmod4 = my_viewer.last_s % 4;
        int lsmod5 = my_viewer.last_s % 5;
        int smod3 = second % 3;
        int smod4 = second % 4;
        int smod5 = second % 5;
        if (smod3 == 0) smod3 = 3;
        if (smod4 == 0) smod4 = 4;
        if (smod5 == 0) smod5 = 5;

        // the analog version draws several concentric circles,
        // and moves differently-colored balls on those circles
        // to positions that reflect the remainder

        // make sure the ball doesn't move too far in the animation
        float ball_offset = (my_viewer.my_offset > 0.96) ? 1.0f : my_viewer.my_offset;

        // draw concentric circles for the main paths
        canvas.drawCircle(cx, cy, bally_hr3, circle_paint);
        canvas.drawCircle(cx, cy, bally_hr4, circle_paint);
        canvas.drawCircle(cx, cy, bally_mr3, circle_paint);
        canvas.drawCircle(cx, cy, bally_mr4, circle_paint);
        canvas.drawCircle(cx, cy, bally_mr5, circle_paint);

        // draw hatch marks for positions modulo 3
        for (int i = 0; i < 3; ++i) {
            canvas.drawLine(
                    (float) (cx + bally_hatch_hr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
                    (float) (cy + bally_hatch_hr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
                    (float) (cx + bally_hatch_h3_outer * cos(2 * PI / 3 * i - PI  / 2)),
                    (float) (cy + bally_hatch_h3_outer * sin(2 * PI / 3 * i - PI  / 2)),
                    circle_paint
            );
            canvas.drawLine(
                    (float) (cx + bally_hatch_mr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
                    (float) (cy + bally_hatch_mr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
                    (float) (cx + bally_hatch_mr3_outer * cos(2 * PI / 3 * i - PI  / 2)),
                    (float) (cy + bally_hatch_mr3_outer * sin(2 * PI / 3 * i - PI  / 2)),
                    circle_paint
            );
        }

        // draw hatch marks for hour's second positions (modulo 4 or 8 as appropriate)
        for (int i = 0; i < my_viewer.hour_modulus; ++i) {
            canvas.drawLine(
                    (float) (cx + bally_hatch_hr4_inner * cos(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
                    (float) (cy + bally_hatch_hr4_inner * sin(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
                    (float) (cx + bally_hatch_h4_outer * cos(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
                    (float) (cy + bally_hatch_h4_outer * sin(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
                    circle_paint
            );
        }

        // draw hatch marks for minute's positions modulo 4
        for (int i = 0; i < 4; ++i) {
            canvas.drawLine(
                    (float) (cx + bally_hatch_mr4_inner * cos(2 * PI / 4 * i - PI / 2)),
                    (float) (cy + bally_hatch_mr4_inner * sin(2 * PI / 4 * i - PI / 2)),
                    (float) (cx + bally_hatch_m4_outer * cos(2 * PI / 4 * i - PI / 2)),
                    (float) (cy + bally_hatch_m4_outer * sin(2 * PI / 4 * i - PI / 2)),
                    circle_paint
            );
        }

        // draw hatch marks for minute's positions modulo 5
        for (int i = 0; i < 5; ++i) {
            canvas.drawLine(
                    (float) (cx + bally_hatch_mr5_inner * cos(2 * PI / 5 * i - PI / 2)),
                    (float) (cy + bally_hatch_mr5_inner * sin(2 * PI / 5 * i - PI / 2)),
                    (float) (cx + bally_hatch_m5_outer * cos(2 * PI / 5 * i - PI / 2)),
                    (float) (cy + bally_hatch_m5_outer * sin(2 * PI / 5 * i - PI / 2)),
                    circle_paint
            );
        }

        // determine the hour ball's position:
        // hangle3 determines the position for the inner ball (modulo 3)
        // and hangle4 determines the position for the outer ball (modulo 4 or 8)
        float hangle3, hangle4;
        if (my_viewer.last_h == hour) {
            hangle3 = (float) (2 * PI / 3 * hmod3 - PI / 2);
            hangle4 = (float) (2 * PI / my_viewer.hour_modulus * hmod4 - PI / 2);
        } else {
            if (my_viewer.time_guide == DECREMENT) {
                if (hmod3 == 3) hmod3 = 0;
                else if (lhmod3 == 0) lhmod3 = 3;
                if (hmod4 == my_viewer.hour_modulus) hmod4 = 0;
                else if (lhmod4 == 0) lhmod4 = my_viewer.hour_modulus;
            }
            hangle3 = (float) (2 * PI / 3 * (hmod3 * ball_offset + lhmod3 * (1 - ball_offset)) - PI / 2);
            hangle4 = (float) (2 * PI / my_viewer.hour_modulus * (hmod4 * ball_offset + lhmod4 * (1 - ball_offset)) - PI / 2);
        }

        // determine the minute ball's position:
        // manglei determines the position for the ball corresponding to the remainder modulo i
        float mangle3, mangle4, mangle5;
        if (my_viewer.last_m == minute) {
            mangle3 = (float) (2 * PI / 3 * mmod3 - PI / 2);
            mangle4 = (float) (2 * PI / 4 * mmod4 - PI / 2);
            mangle5 = (float) (2 * PI / 5 * mmod5 - PI / 2);
        } else {
            if (my_viewer.time_guide == DECREMENT) {
                if (mmod3 == 3) mmod3 = 0;
                else if (lmmod3 == 0) lmmod3 = 3;
                if (mmod4 == 4) mmod4 = 0;
                else if (lmmod4 == 0) lmmod4 = 4;
                if (mmod5 == 5) mmod5 = 0;
                else if (lmmod5 == 0) lmmod5 = 5;
            }
            mangle3 = (float) (2 * PI / 3 * (mmod3 * ball_offset + lmmod3 * (1 - ball_offset)) - PI / 2);
            mangle4 = (float) (2 * PI / 4 * (mmod4 * ball_offset + lmmod4 * (1 - ball_offset)) - PI / 2);
            mangle5 = (float) (2 * PI / 5 * (mmod5 * ball_offset + lmmod5 * (1 - ball_offset)) - PI / 2);
        }

        // determine the second ball's position:
        // sanglei determines the position for the ball corresponding to the remainder modulo i
        float sangle3, sangle4, sangle5;
        if (my_viewer.last_s == second) {
            sangle3 = (float) (2 * PI / 3 * smod3 - PI / 2);
            sangle4 = (float) (2 * PI / 4 * smod4 - PI / 2);
            sangle5 = (float) (2 * PI / 5 * smod5 - PI / 2);
        } else {
            if (my_viewer.time_guide == DECREMENT) {
                if (smod3 == 3) smod3 = 0;
                else if (lsmod3 == 0) lsmod3 = 3;
                if (smod4 == 4) smod4 = 0;
                else if (lsmod4 == 0) lsmod4 = 4;
                if (smod5 == 5) smod5 = 0;
                else if (lsmod5 == 0) lsmod5 = 5;
            }
            sangle3 = (float) (2 * PI / 3 * (smod3 * ball_offset + lsmod3 * (1 - ball_offset)) - PI / 2);
            sangle4 = (float) (2 * PI / 4 * (smod4 * ball_offset + lsmod4 * (1 - ball_offset)) - PI / 2);
            sangle5 = (float) (2 * PI / 5 * (smod5 * ball_offset + lsmod5 * (1 - ball_offset)) - PI / 2);
        }

        // draw the balls

        ball_paint.setColor(hour_color);
        ball_paint.setStyle(FILL);

        float x = (float) (cx + bally_hr3 * cos(hangle3));
        float y = (float) (cy + bally_hr3 * sin(hangle3));
        canvas.drawCircle(x, y, bally_br, ball_paint);

        x = (float) (cx + bally_hr4 * cos(hangle4));
        y = (float) (cy + bally_hr4 * sin(hangle4));
        canvas.drawCircle(x, y, bally_br, ball_paint);

        ball_paint.setColor(minute_color);

        x = (float) (cx + bally_mr3 * cos(mangle3));
        y = (float) (cy + bally_mr3 * sin(mangle3));
        canvas.drawCircle(x, y, bally_br, ball_paint);

        x = (float) (cx + bally_mr4 * cos(mangle4));
        y = (float) (cy + bally_mr4 * sin(mangle4));
        canvas.drawCircle(x, y, bally_br, ball_paint);

        x = (float) (cx + bally_mr5 * cos(mangle5));
        y = (float) (cy + bally_mr5 * sin(mangle5));
        canvas.drawCircle(x, y, bally_br, ball_paint);

        if (show_seconds) {

            ball_paint.setColor(second_color);

            x = (float) (cx + bally_sr3 * cos(sangle3));
            y = (float) (cy + bally_sr3 * sin(sangle3));
            canvas.drawCircle(x, y, bally_br, ball_paint);

            x = (float) (cx + bally_sr4 * cos(sangle4));
            y = (float) (cy + bally_sr4 * sin(sangle4));
            canvas.drawCircle(x, y, bally_br, ball_paint);

            x = (float) (cx + bally_sr5 * cos(sangle5));
            y = (float) (cy + bally_sr5 * sin(sangle5));
            canvas.drawCircle(x, y, bally_br, ball_paint);

        }

        usual_cleanup();

    }

    // this should be needed only once in the lifetime of the View,
    // but this can be many times because the View is re-constructed
    // every time the orientation changes
    void recalculate_positions() {

        super.recalculate_positions();

        bally_hr3 = (float) (diam / 6.0);
        bally_hr4 = (float) (diam / 3.0);
        bally_mr3 = (float) (diam / 2.0);
        bally_mr4 = (float) (diam * 2.0 / 3.0);
        bally_mr5 = (float) (diam * 5.0 / 6.0);
        bally_sr3 = (float) (diam / 12.0 * 5.0);
        bally_sr4 = (float) (diam / 12.0 * 7.0);
        bally_sr5 = (float) (diam / 12.0 * 9.0);
        bally_br = diam / 28;
        bally_hatch_dist = diam / 60;
        bally_hatch_hr3_inner = bally_hr3 - bally_hatch_dist;
        bally_hatch_h3_outer = bally_hr3 + bally_hatch_dist;
        bally_hatch_hr4_inner = bally_hr4 - bally_hatch_dist;
        bally_hatch_h4_outer = bally_hr4 + bally_hatch_dist;
        bally_hatch_mr3_inner = bally_mr3 - bally_hatch_dist;
        bally_hatch_mr3_outer = bally_mr3 + bally_hatch_dist;
        bally_hatch_mr4_inner = bally_mr4 - bally_hatch_dist;
        bally_hatch_m4_outer = bally_mr4 + bally_hatch_dist;
        bally_hatch_mr5_inner = bally_mr5 - bally_hatch_dist;
        bally_hatch_m5_outer = bally_mr5 + bally_hatch_dist;

    }

    // fields that control layout of analog clock elements
    protected float bally_hr3, bally_hr4, bally_mr3, bally_mr4, bally_mr5, bally_sr3, bally_sr4, bally_sr5,
            bally_br, bally_hatch_dist, bally_hatch_hr3_inner, bally_hatch_h3_outer,
            bally_hatch_hr4_inner, bally_hatch_h4_outer, bally_hatch_mr3_inner,
            bally_hatch_mr3_outer, bally_hatch_mr4_inner, bally_hatch_m4_outer,
            bally_hatch_mr5_inner, bally_hatch_m5_outer;
}
