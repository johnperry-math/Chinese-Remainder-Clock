package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;

import name.cantanima.chineseremainderclock.CRC_View.Modification;

/**
 * Created by cantanima on 6/5/17.
 */

public class CRC_View_Polly extends Clock_Drawer {

    // constructor
    public CRC_View_Polly(CRC_View owner) {

        initialize_fields(owner);
        backPath = new Path();
        digi_paint = new Paint(ANTI_ALIAS_FLAG);
        digi_paint.setColor(WHITE);
        digi_paint.setTextAlign(CENTER);

    }

    float preferred_step() { return 0.49f; }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void draw(Canvas canvas) {

        // get the current time, compare to previous time, and animate accordingly
        // my_viewer.time_guide is used to help determine what sort of animation to use (if any)

        Calendar time = Calendar.getInstance();

        int hour, minute, second;
        if (my_viewer.time_guide == CALENDAR) { // moving according to the clock
            hour = time.get(my_viewer.which_hour);
            minute = time.get(MINUTE);
            second = time.get(SECOND);
        } else if (my_viewer.time_guide == NEW_VALUE) { // user has manually entered a time
            hour = my_viewer.last_h;
            minute = my_viewer.last_m;
            second = my_viewer.last_s;
            switch (my_viewer.unitSelecter.getSelectedItemPosition()) {
                case 0: hour = my_viewer.new_time_value; break;
                case 1: minute = my_viewer.new_time_value; break;
                case 2: second = my_viewer.new_time_value; break;
            }
        } else if (my_viewer.time_guide == LEAVE_BE) { // use previous time
            hour = my_viewer.last_h;
            minute = my_viewer.last_m;
            second = my_viewer.last_s;
        } else { // user pressed increment or decrement button
            hour = my_viewer.last_h;
            minute = my_viewer.last_m;
            second = my_viewer.last_s;
            int direction = 0;
            if (my_viewer.time_guide == INCREMENT) direction = 1;
            else direction = -1;
            switch (my_viewer.which_unit_to_modify) {
                case HOURS:
                    hour += direction;
                    break;
                case MINUTES:
                    minute += direction;
                    break;
                case SECONDS:
                    second += direction;
                    break;
            }
        }

        // modify for better writing/graphics
        int hour_max = (my_viewer.hour_modulus == 4) ? 12 : 24;
        if (hour < 0) hour += hour_max;
        else if (hour >= hour_max) hour %= hour_max;
        if (minute < 0) minute += 60;
        else if (minute > 59) minute %= 60;
        if (second < 0) second += 60;
        else if (second > 59) second %= 60;

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

        // in digital, we draw and fill polygons
        // the fill's transparency varies according to the remainder:
        // the closer the remainder is to the modulus, the more opaque the fill

        // hour, modulo 3

        ball_paint.setColor(hour_color);
        ball_paint.setStyle(STROKE);
        ball_paint.setStrokeWidth(3);
        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(85 * (hmod3 % 3 + 1));
        backPath.rewind();
        backPath.moveTo(digi_h3_pts[0], digi_h3_pts[1]); backPath.lineTo(digi_h3_pts[2], digi_h3_pts[3]);
        backPath.lineTo(digi_h3_pts[4], digi_h3_pts[5]); backPath.lineTo(digi_h3_pts[6], digi_h3_pts[7]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_h3_pts, circle_paint);
        if (hmod3 == 3)
            canvas.drawText(zero_str, cx - digi_step, digi_hty1, digi_paint);
        else
            canvas.drawText(String.valueOf(hmod3), cx - digi_step, digi_hty1, digi_paint);
        canvas.drawLines(digi_h3_pts, 0, hmod3 << 2, ball_paint);

        // hour, modulo 4 or 8, depending on the kind of time

        if (my_viewer.hour_modulus == 4) {

            ball_paint.setStyle(FILL);
            ball_paint.setAlpha(63 * (hmod4 % 4 + 1));
            backPath.rewind();
            backPath.moveTo(digi_h4_pts[0], digi_h4_pts[1]);
            backPath.lineTo(digi_h4_pts[2], digi_h4_pts[3]);
            backPath.lineTo(digi_h4_pts[4], digi_h4_pts[5]);
            backPath.lineTo(digi_h4_pts[6], digi_h4_pts[7]);
            backPath.lineTo(digi_h4_pts[8], digi_h4_pts[9]);
            backPath.lineTo(digi_h4_pts[10], digi_h4_pts[11]);
            backPath.lineTo(digi_h4_pts[12], digi_h4_pts[13]);
            backPath.lineTo(digi_h4_pts[14], digi_h4_pts[15]);
            canvas.drawPath(backPath, ball_paint);
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            //canvas.drawLines(digi_h4_pts, circle_paint);
            if (hmod4 == 4)
                canvas.drawText(zero_str, cx - digi_step, digi_hty2, digi_paint);
            else
                canvas.drawText(String.valueOf(hmod4), cx - digi_step, digi_hty2, digi_paint);
            canvas.drawLines(digi_h4_pts, 0, hmod4 << 2, ball_paint);

        } else { // hour modulo 8

            ball_paint.setStyle(FILL);
            ball_paint.setAlpha(31 * (hmod4 % 8 + 1));
            backPath.rewind();
            backPath.moveTo(digi_h8_pts[0], digi_h8_pts[1]);
            backPath.lineTo(digi_h8_pts[2], digi_h8_pts[3]);
            backPath.lineTo(digi_h8_pts[4], digi_h8_pts[5]);
            backPath.lineTo(digi_h8_pts[6], digi_h8_pts[7]);
            backPath.lineTo(digi_h8_pts[8], digi_h8_pts[9]);
            backPath.lineTo(digi_h8_pts[10], digi_h8_pts[11]);
            backPath.lineTo(digi_h8_pts[12], digi_h8_pts[13]);
            backPath.lineTo(digi_h8_pts[14], digi_h8_pts[15]);
            backPath.moveTo(digi_h8_pts[16], digi_h8_pts[17]);
            backPath.lineTo(digi_h8_pts[18], digi_h8_pts[19]);
            backPath.lineTo(digi_h8_pts[20], digi_h8_pts[21]);
            backPath.lineTo(digi_h8_pts[22], digi_h8_pts[23]);
            backPath.lineTo(digi_h8_pts[24], digi_h8_pts[25]);
            backPath.lineTo(digi_h8_pts[26], digi_h8_pts[27]);
            backPath.lineTo(digi_h8_pts[28], digi_h8_pts[29]);
            backPath.lineTo(digi_h8_pts[30], digi_h8_pts[31]);
            canvas.drawPath(backPath, ball_paint);
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            //canvas.drawLines(digi_h8_pts, circle_paint);
            if (hmod4 == 8)
                canvas.drawText(zero_str, cx - digi_step, digi_hty2, digi_paint);
            else
                canvas.drawText(String.valueOf(hmod4), cx - digi_step, digi_hty2, digi_paint);
            canvas.drawLines(digi_h8_pts, 0, hmod4 << 2, ball_paint);

        }

        // minute, modulo 3

        ball_paint.setColor(minute_color);
        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(85 * (mmod3 % 3 + 1));
        backPath.rewind();
        backPath.moveTo(digi_m3_pts[0], digi_m3_pts[1]); backPath.lineTo(digi_m3_pts[2], digi_m3_pts[3]);
        backPath.lineTo(digi_m3_pts[4], digi_m3_pts[5]); backPath.lineTo(digi_m3_pts[6], digi_m3_pts[7]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_m3_pts, circle_paint);
        if (mmod3 == 3)
            canvas.drawText(zero_str, cx, digi_msty1, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod3), cx, digi_msty1, digi_paint);
        canvas.drawLines(digi_m3_pts, 0, mmod3 << 2, ball_paint);

        // minute, modulo 4

        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(63 * (mmod4 % 4 + 1));
        backPath.rewind();
        backPath.moveTo(digi_m4_pts[0], digi_m4_pts[1]); backPath.lineTo(digi_m4_pts[2], digi_m4_pts[3]);
        backPath.lineTo(digi_m4_pts[4], digi_m4_pts[5]); backPath.lineTo(digi_m4_pts[6], digi_m4_pts[7]);
        backPath.lineTo(digi_m4_pts[8], digi_m4_pts[9]); backPath.lineTo(digi_m4_pts[10], digi_m4_pts[11]);
        backPath.lineTo(digi_m4_pts[12], digi_m4_pts[13]); backPath.lineTo(digi_m4_pts[14], digi_m4_pts[15]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_m4_pts, circle_paint);
        if (mmod4 == 4)
            canvas.drawText(zero_str, cx, digi_ty, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod4), cx, digi_ty, digi_paint);
        canvas.drawLines(digi_m4_pts, 0, mmod4 << 2, ball_paint);

        // minute, modulo 5

        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(51 * (mmod5 % 5 + 1));
        backPath.rewind();
        backPath.moveTo(digi_m5_pts[0], digi_m5_pts[1]); backPath.lineTo(digi_m5_pts[2], digi_m5_pts[3]);
        backPath.lineTo(digi_m5_pts[4], digi_m5_pts[5]); backPath.lineTo(digi_m5_pts[6], digi_m5_pts[7]);
        backPath.lineTo(digi_m5_pts[8], digi_m5_pts[9]); backPath.lineTo(digi_m5_pts[10], digi_m5_pts[11]);
        backPath.lineTo(digi_m5_pts[12], digi_m5_pts[13]); backPath.lineTo(digi_m5_pts[14], digi_m5_pts[15]);
        backPath.lineTo(digi_m5_pts[16], digi_m5_pts[17]); backPath.lineTo(digi_m5_pts[18], digi_m5_pts[19]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_m5_pts, circle_paint);
        if (mmod5 == 5)
            canvas.drawText(zero_str, cx, digi_msty3, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod5), cx, digi_msty3, digi_paint);
        canvas.drawLines(digi_m5_pts, 0, mmod5 << 2, ball_paint);

        // second, modulo 3

        ball_paint.setColor(second_color);
        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(63 * (smod3 % 3 + 1));
        backPath.rewind();
        backPath.moveTo(digi_s3_pts[0], digi_s3_pts[1]); backPath.lineTo(digi_s3_pts[2], digi_s3_pts[3]);
        backPath.lineTo(digi_s3_pts[4], digi_s3_pts[5]); backPath.lineTo(digi_s3_pts[6], digi_s3_pts[7]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_s3_pts, circle_paint);
        if (smod3 == 3)
            canvas.drawText(zero_str, cx + digi_step, digi_msty1, digi_paint);
        else
            canvas.drawText(String.valueOf(smod3), cx + digi_step, digi_msty1, digi_paint);
        canvas.drawLines(digi_s3_pts, 0, smod3 << 2, ball_paint);

        // second, modulo 4

        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(63 * (smod4 % 4 + 1));
        backPath.rewind();
        backPath.moveTo(digi_s4_pts[0], digi_s4_pts[1]); backPath.lineTo(digi_s4_pts[2], digi_s4_pts[3]);
        backPath.lineTo(digi_s4_pts[4], digi_s4_pts[5]); backPath.lineTo(digi_s4_pts[6], digi_s4_pts[7]);
        backPath.lineTo(digi_s4_pts[8], digi_s4_pts[9]); backPath.lineTo(digi_s4_pts[10], digi_s4_pts[11]);
        backPath.lineTo(digi_s4_pts[12], digi_s4_pts[13]); backPath.lineTo(digi_s4_pts[14], digi_s4_pts[15]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_s4_pts, circle_paint);
        if (smod4 == 4)
            canvas.drawText(zero_str, cx + digi_step, digi_ty, digi_paint);
        else
            canvas.drawText(String.valueOf(smod4), cx + digi_step, digi_ty, digi_paint);
        canvas.drawLines(digi_s4_pts, 0, smod4 << 2, ball_paint);

        // minute, modulo 5

        ball_paint.setStyle(FILL);
        ball_paint.setAlpha(51 * (smod5 % 5 + 1));
        // TODO
        // The following worked on my phone (until I deleted and rewrote it, at least),
        //  but I'm not sure about slower devices, so it's commented out.
        // If we can make this more efficient, change it so that the alpha increases, like so:
        /*if (my_offset > 0.96)
            ball_paint.setAlpha(51 * (smod5 % 5 + 1));
        else
            ball_paint.setAlpha(51 * (int) (smod5 % 5 + my_offset));*/
        backPath.rewind();
        backPath.moveTo(digi_s5_pts[0], digi_s5_pts[1]); backPath.lineTo(digi_s5_pts[2], digi_s5_pts[3]);
        backPath.lineTo(digi_s5_pts[4], digi_s5_pts[5]); backPath.lineTo(digi_s5_pts[6], digi_s5_pts[7]);
        backPath.lineTo(digi_s5_pts[8], digi_s5_pts[9]); backPath.lineTo(digi_s5_pts[10], digi_s5_pts[11]);
        backPath.lineTo(digi_s5_pts[12], digi_s5_pts[13]); backPath.lineTo(digi_s5_pts[14], digi_s5_pts[15]);
        backPath.lineTo(digi_s5_pts[16], digi_s5_pts[17]); backPath.lineTo(digi_s5_pts[18], digi_s5_pts[19]);
        canvas.drawPath(backPath, ball_paint);
        ball_paint.setStyle(STROKE);
        ball_paint.setAlpha(255);
        //canvas.drawLines(digi_s5_pts, circle_paint);
        if (smod5 == 5)
            canvas.drawText(zero_str, cx + digi_step, digi_msty3, digi_paint);
        else
            canvas.drawText(String.valueOf(smod5), cx + digi_step, digi_msty3, digi_paint);
        canvas.drawLines(digi_s5_pts, 0, smod5 << 2, ball_paint);

        // adjust certain settings once the animation ends

        if (my_viewer.my_offset >= 0.97f) {

            // remember this time as the last time
            my_viewer.last_h = hour;
            my_viewer.last_m = minute;
            my_viewer.last_s = second;

            // if the user manually modified the time, instructor the animator to leave it be
            // on the next pass
            if (my_viewer.time_guide == INCREMENT || my_viewer.time_guide == Modification.DECREMENT) {
                my_viewer.time_guide = LEAVE_BE;
            }

            // this seems necessary for some reason I haven't yet worked out
            switch (my_viewer.unitSelecter.getSelectedItemPosition()) {
                case 0:
                    //default:
                    switch (my_viewer.hour_modulus) {
                        case 4:
                            //default:
                            my_viewer.valueEditor.setText(hour12_strings[hour]);
                            my_viewer.valueEditor.selectAll();
                            break;
                        case 8:
                            my_viewer.valueEditor.setText(hour24_strings[hour]);
                            my_viewer.valueEditor.selectAll();
                            break;
                    }
                    break;
                case 1:
                    my_viewer.valueEditor.setText(String.valueOf(minute));
                    my_viewer.valueEditor.selectAll();
                    break;
                case 2:
                    my_viewer.valueEditor.setText(String.valueOf(second));
                    my_viewer.valueEditor.selectAll();
                    break;
            }
        }

    }

    // this should be needed only once in the lifetime of the View,
    // but this can be many times because the View is re-constructed
    // every time the orientation changes
    void recalculate_positions() {

        super.recalculate_positions();

        obj_w2 = diam / 5;
        digi_paint.setTextSize(obj_w2 * 0.75f);
        Paint.FontMetrics fm = digi_paint.getFontMetrics();
        float digi_t_adjust = -fm.ascent;
        digi_ty = cy + digi_t_adjust / 2.5f;
        digi_step = diam / 2;
        digi_hcy1 = cy - obj_w2;
        digi_hcy2 = cy + obj_w2;
        digi_hty1 = digi_hcy1 + digi_t_adjust / 2.5f;
        digi_hty2 = digi_hcy2 + digi_t_adjust / 2.5f;
        digi_mscy1 = cy - obj_w2 * 5 / 2;
        digi_mscy3 = cy + obj_w2 * 5 / 2;
        digi_msty1 = digi_mscy1 + digi_t_adjust / 2.5f;
        digi_msty3 = digi_mscy3 + digi_t_adjust / 2.5f;

        digi_h3_pts = new float [] {
                cx - digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
                cx - digi_step - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
                cx - digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        digi_h4_pts = new float [] {
                cx - digi_step, digi_hcy2 - obj_w2,
                cx - digi_step + obj_w2, digi_hcy2,
                cx - digi_step + obj_w2, digi_hcy2,
                cx - digi_step, digi_hcy2 + obj_w2,
                cx - digi_step, digi_hcy2 + obj_w2,
                cx - digi_step - obj_w2, digi_hcy2,
                cx - digi_step - obj_w2, digi_hcy2,
                cx - digi_step, digi_hcy2 - obj_w2,
        };
        digi_h8_pts = new float [] {
                cx - digi_step - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
        };
        digi_m3_pts = new float [] {
                cx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        digi_m4_pts = new float [] {
                cx, cy - obj_w2,
                cx + obj_w2, cy,
                cx + obj_w2, cy,
                cx, cy + obj_w2,
                cx, cy + obj_w2,
                cx - obj_w2, cy,
                cx - obj_w2, cy,
                cx, cy - obj_w2,
        };
        digi_m5_pts = new float [] {
                cx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 *  PI / 10),
                cx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 *  PI / 10),
                cx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
                cx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 *  PI / 10),
                cx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
                cx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
                cx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 *  PI / 10),
                cx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 *  PI / 10),
                cx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
                cx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
        };
        digi_s3_pts = new float [] {
                cx + digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx + digi_step - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx + digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        digi_s4_pts = new float [] {
                cx + digi_step, cy - obj_w2,
                cx + digi_step + obj_w2, cy,
                cx + digi_step + obj_w2, cy,
                cx + digi_step, cy + obj_w2,
                cx + digi_step, cy + obj_w2,
                cx + digi_step - obj_w2, cy,
                cx + digi_step - obj_w2, cy,
                cx + digi_step, cy - obj_w2,
        };
        digi_s5_pts = new float [] {
                cx + digi_step - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
        };

    }

    // fields that control layout of digital clock elements (except the polygons)
    protected float digi_step, digi_ty, digi_hcy1, digi_hcy2, digi_hty1, digi_hty2,
            digi_mscy1, digi_mscy3, digi_msty1, digi_msty3, obj_w2;
    // arrays that store points to draw the polygons
    protected float [] digi_h3_pts, digi_h4_pts, digi_h8_pts, digi_m3_pts, digi_m4_pts, digi_m5_pts,
            digi_s3_pts, digi_s4_pts, digi_s5_pts;
    // used to draw polygons
    protected Path backPath;

    protected Paint digi_paint;

}
