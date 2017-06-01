package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

/**
 * Created by cantanima on 4/22/17.
 */

public class CRC_View
        extends View
        implements OnTouchListener, OnCheckedChangeListener, OnClickListener,
                   OnItemSelectedListener, OnEditorActionListener
{

    // constructor
    public CRC_View(Context context, AttributeSet attrs) {

        super(context, attrs);

        // this line is needed to preview the layout in Android Studio
        if (!isInEditMode())
            my_owner = (Chinese_Remainder) context;

        // initialize fields:

        // fields related to options that user toggles
        hour_modulus = 4;
        which_hour = Calendar.HOUR;
        analog = true;
        color = true;

        // fields related to drawing
        my_offset = 0.0f;
        back_paint = new Paint(ANTI_ALIAS_FLAG);
        back_paint.setColor(BACKGROUND);
        text_paint = new Paint(ANTI_ALIAS_FLAG);
        text_paint.setColor(WHITE);
        text_paint.setTextAlign(CENTER);
        digi_paint = new Paint(ANTI_ALIAS_FLAG);
        digi_paint.setColor(WHITE);
        digi_paint.setTextAlign(CENTER);
        ball_paint = new Paint(ANTI_ALIAS_FLAG);
        circle_paint = new Paint(ANTI_ALIAS_FLAG);
        circle_paint.setColor(WHITE);
        circle_paint.setStyle(STROKE);
        second_color = GOODGREEN;
        minute_color = RED;
        hour_color = BLUE;
        backPath = new Path();

        // fields related to animation:
        my_animator = new CRC_Animation(this);
        time_guide = Modification.CALENDAR;

        // respond to touch
        setOnTouchListener(this);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        recalculate_positions();
    }

    // used to control the animation
    public void set_offset(float offset) {
        my_offset = offset;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {

        // get the current time, compare to previous time, and animate accordingly
        // time_guide is used to help determine what sort of animation to use (if any)

        Calendar time = Calendar.getInstance();

        int hour, minute, second;
        if (time_guide == Modification.CALENDAR) { // moving according to the clock
            hour = time.get(which_hour);
            minute = time.get(Calendar.MINUTE);
            second = time.get(Calendar.SECOND);
        } else if (time_guide == Modification.NEW_VALUE) { // user has manually entered a time
            hour = last_h;
            minute = last_m;
            second = last_s;
            switch (unitSelecter.getSelectedItemPosition()) {
                case 0: hour = new_time_value; break;
                case 1: minute = new_time_value; break;
                case 2: second = new_time_value; break;
            }
        } else if (time_guide == Modification.LEAVE_BE) { // use previous time
            hour = last_h;
            minute = last_m;
            second = last_s;
        } else { // user pressed increment or decrement button
            hour = last_h;
            minute = last_m;
            second = last_s;
            int direction = 0;
            if (time_guide == Modification.INCREMENT) direction = 1;
            else direction = -1;
            switch (which_unit_to_modify) {
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
        int hour_max = (hour_modulus == 4) ? 12 : 24;
        if (hour < 0) hour += hour_max;
        else if (hour >= hour_max) hour %= hour_max;
        if (minute < 0) minute += 60;
        else if (minute > 59) minute %= 60;
        if (second < 0) second += 60;
        else if (second > 59) second %= 60;

        drawTimeAndRectangle(canvas, hour, minute, second, cx, cy, diam, analog);

        // in what follows, xmodi is the time unit x modulo i, while
        // lxmodi is last_x % i

        int lhmod3 = last_h % 3;
        int lhmod4 = last_h % hour_modulus;
        int hmod3 = hour % 3;
        int hmod4 = hour % hour_modulus;
        if (hmod3 == 0) hmod3 = 3;
        if (hmod4 == 0) hmod4 = hour_modulus;

        int lmmod3 = last_m % 3;
        int lmmod4 = last_m % 4;
        int lmmod5 = last_m % 5;
        int mmod3 = minute % 3;
        int mmod4 = minute % 4;
        int mmod5 = minute % 5;
        if (mmod3 == 0) mmod3 = 3;
        if (mmod4 == 0) mmod4 = 4;
        if (mmod5 == 0) mmod5 = 5;

        int lsmod3 = last_s % 3;
        int lsmod4 = last_s % 4;
        int lsmod5 = last_s % 5;
        int smod3 = second % 3;
        int smod4 = second % 4;
        int smod5 = second % 5;
        if (smod3 == 0) smod3 = 3;
        if (smod4 == 0) smod4 = 4;
        if (smod5 == 0) smod5 = 5;

        // there are two basic drawing routines: one for the analog clock, and one for the digital
        if (!analog) {

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

            if (hour_modulus == 4) {

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

        } else {

            // the analog version draws several concentric circles,
            // and moves differently-colored balls on those circles
            // to positions that reflect the remainder

            // make sure the ball doesn't move too far in the animation
            float ball_offset = (my_offset > 0.96) ? 1.0f : my_offset;

            // draw concentric circles for the main paths
            canvas.drawCircle(cx, cy, anal_hr3, circle_paint);
            canvas.drawCircle(cx, cy, anal_hr4, circle_paint);
            canvas.drawCircle(cx, cy, anal_mr3, circle_paint);
            canvas.drawCircle(cx, cy, anal_mr4, circle_paint);
            canvas.drawCircle(cx, cy, anal_mr5, circle_paint);

            // draw hatch marks for positions modulo 3
            for (int i = 0; i < 3; ++i) {
                canvas.drawLine(
                        (float) (cx + anal_hatch_hr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
                        (float) (cy + anal_hatch_hr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
                        (float) (cx + anal_hatch_h3_outer * cos(2 * PI / 3 * i - PI  / 2)),
                        (float) (cy + anal_hatch_h3_outer * sin(2 * PI / 3 * i - PI  / 2)),
                        circle_paint
                );
                canvas.drawLine(
                        (float) (cx + anal_hatch_mr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
                        (float) (cy + anal_hatch_mr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
                        (float) (cx + anal_hatch_mr3_outer * cos(2 * PI / 3 * i - PI  / 2)),
                        (float) (cy + anal_hatch_mr3_outer * sin(2 * PI / 3 * i - PI  / 2)),
                        circle_paint
                );
            }

            // draw hatch marks for hour's second positions (modulo 4 or 8 as appropriate)
            for (int i = 0; i < hour_modulus; ++i) {
                canvas.drawLine(
                        (float) (cx + anal_hatch_hr4_inner * cos(2 * PI / hour_modulus * i - PI / 2)),
                        (float) (cy + anal_hatch_hr4_inner * sin(2 * PI / hour_modulus * i - PI / 2)),
                        (float) (cx + anal_hatch_h4_outer * cos(2 * PI / hour_modulus * i - PI / 2)),
                        (float) (cy + anal_hatch_h4_outer * sin(2 * PI / hour_modulus * i - PI / 2)),
                        circle_paint
                );
            }

            // draw hatch marks for minute's positions modulo 4
            for (int i = 0; i < 4; ++i) {
                canvas.drawLine(
                        (float) (cx + anal_hatch_mr4_inner * cos(2 * PI / 4 * i - PI / 2)),
                        (float) (cy + anal_hatch_mr4_inner * sin(2 * PI / 4 * i - PI / 2)),
                        (float) (cx + anal_hatch_m4_outer * cos(2 * PI / 4 * i - PI / 2)),
                        (float) (cy + anal_hatch_m4_outer * sin(2 * PI / 4 * i - PI / 2)),
                        circle_paint
                );
            }

            // draw hatch marks for minute's positions modulo 5
            for (int i = 0; i < 5; ++i) {
                canvas.drawLine(
                        (float) (cx + anal_hatch_mr5_inner * cos(2 * PI / 5 * i - PI / 2)),
                        (float) (cy + anal_hatch_mr5_inner * sin(2 * PI / 5 * i - PI / 2)),
                        (float) (cx + anal_hatch_m5_outer * cos(2 * PI / 5 * i - PI / 2)),
                        (float) (cy + anal_hatch_m5_outer * sin(2 * PI / 5 * i - PI / 2)),
                        circle_paint
                );
            }

            // determine the hour ball's position:
            // hangle3 determines the position for the inner ball (modulo 3)
            // and hangle4 determines the position for the outer ball (modulo 4 or 8)
            float hangle3, hangle4;
            if (last_h == hour) {
                hangle3 = (float) (2 * PI / 3 * hmod3 - PI / 2);
                hangle4 = (float) (2 * PI / hour_modulus * hmod4 - PI / 2);
            } else {
                if (time_guide == Modification.DECREMENT) {
                    if (hmod3 == 3) hmod3 = 0;
                    else if (lhmod3 == 0) lhmod3 = 3;
                    if (hmod4 == hour_modulus) hmod4 = 0;
                    else if (lhmod4 == 0) lhmod4 = hour_modulus;
                }
                hangle3 = (float) (2 * PI / 3 * (hmod3 * ball_offset + lhmod3 * (1 - ball_offset)) - PI / 2);
                hangle4 = (float) (2 * PI / hour_modulus * (hmod4 * ball_offset + lhmod4 * (1 - ball_offset)) - PI / 2);
            }

            // determine the minute ball's position:
            // manglei determines the position for the ball corresponding to the remainder modulo i
            float mangle3, mangle4, mangle5;
            if (last_m == minute) {
                mangle3 = (float) (2 * PI / 3 * mmod3 - PI / 2);
                mangle4 = (float) (2 * PI / 4 * mmod4 - PI / 2);
                mangle5 = (float) (2 * PI / 5 * mmod5 - PI / 2);
            } else {
                if (time_guide == Modification.DECREMENT) {
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
            if (last_s == second) {
                sangle3 = (float) (2 * PI / 3 * smod3 - PI / 2);
                sangle4 = (float) (2 * PI / 4 * smod4 - PI / 2);
                sangle5 = (float) (2 * PI / 5 * smod5 - PI / 2);
            } else {
                if (time_guide == Modification.DECREMENT) {
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

            float x = (float) (cx + anal_hr3 * cos(hangle3));
            float y = (float) (cy + anal_hr3 * sin(hangle3));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            x = (float) (cx + anal_hr4 * cos(hangle4));
            y = (float) (cy + anal_hr4 * sin(hangle4));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            ball_paint.setColor(minute_color);

            x = (float) (cx + anal_mr3 * cos(mangle3));
            y = (float) (cy + anal_mr3 * sin(mangle3));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            x = (float) (cx + anal_mr4 * cos(mangle4));
            y = (float) (cy + anal_mr4 * sin(mangle4));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            x = (float) (cx + anal_mr5 * cos(mangle5));
            y = (float) (cy + anal_mr5 * sin(mangle5));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            ball_paint.setColor(second_color);

            x = (float) (cx + anal_sr3 * cos(sangle3));
            y = (float) (cy + anal_sr3 * sin(sangle3));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            x = (float) (cx + anal_sr4 * cos(sangle4));
            y = (float) (cy + anal_sr4 * sin(sangle4));
            canvas.drawCircle(x, y, anal_br, ball_paint);

            x = (float) (cx + anal_sr5 * cos(sangle5));
            y = (float) (cy + anal_sr5 * sin(sangle5));
            canvas.drawCircle(x, y, anal_br, ball_paint);

        }

        // adjust certain settings once the animation ends

        if (my_offset >= 0.97f) {

            // remember this time as the last time
            last_h = hour;
            last_m = minute;
            last_s = second;

            // if the user manually modified the time, instructor the animator to leave it be
            // on the next pass
            if (time_guide == Modification.INCREMENT || time_guide == Modification.DECREMENT) {
                time_guide = Modification.LEAVE_BE;
            }

            // this seems necessary for some reason I haven't yet worked out
            switch (unitSelecter.getSelectedItemPosition()) {
                case 0:
                //default:
                    switch (hour_modulus) {
                        case 4:
                        //default:
                            valueEditor.setText(hour12_strings[hour]);
                            valueEditor.selectAll();
                            break;
                        case 8:
                            valueEditor.setText(hour24_strings[hour]);
                            valueEditor.selectAll();
                            break;
                    }
                    break;
                case 1:
                    valueEditor.setText(String.valueOf(minute));
                    valueEditor.selectAll();
                    break;
                case 2:
                    valueEditor.setText(String.valueOf(second));
                    valueEditor.selectAll();
                    break;
            }
        }

    }

    // draws the background rectangle and writes in the time
    void drawTimeAndRectangle(
            Canvas canvas, int hour, int minute, int second,
            float cx, float cy, float diam, boolean nextTime
    ) {
        // draw a rounded rectangle if we can
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(min_x, min_y, max_x, max_y, diam / 6f, diam / 6f, back_paint);
        } else {
            canvas.drawRect(min_x, min_y, max_x, max_y, back_paint);
        }

        // if the offset is roughly at 1, print the new time, else print the old time
        // (if someone is looking carefully, the clock will seem to be 1 second behind)
        // TODO
        // maybe rework the animation so that the clock will not seem 1 second behind
        int print_hour, print_minute, print_second;
        if (my_offset >= 0.96f || !nextTime) {
            print_hour = hour;
            print_minute = minute;
            print_second = second;
        } else {
            print_hour = last_h;
            print_minute = last_m;
            print_second = last_s;
        }

        // print the correct times
        String to_print;
        if (print_hour % 12 == 0) {
            if (which_hour == Calendar.HOUR && analog) {
                to_print = twelve_str;
            } else {
                if (print_hour == 0 || print_hour == 24)
                    to_print = dbl_zero_str;
                else to_print = twelve_str;
            }
        } else if (print_hour > 0 && print_hour < 10) {
            //to_print = zero_str + String.valueOf(print_hour);
            to_print = zero_str + hour12_strings[print_hour];
        } else {
            to_print = String.valueOf(print_hour);
        }
        canvas.drawText(
                to_print,
                cx - diam * (2f / 3f + 1f / 8f), cy - diam * (2f / 3f + 1f / 8f) + textYOffset,
                text_paint
        );
        if (print_minute == 60)
            to_print = dbl_zero_str;
        else {
            if (print_minute < 10)
                to_print = zero_str + minsec_strings[print_minute];
            else
                to_print = minsec_strings[print_minute];
        }
        canvas.drawText(
                to_print,
                cx + diam * (2f /3f + 1f / 24f), cy - diam * (2f / 3f + 1f / 8f) + textYOffset,
                text_paint
        );
        if (print_second == 60)
            to_print = dbl_zero_str;
        else {
            if (print_second < 10)
                to_print = zero_str + minsec_strings[print_second];
            else
                to_print = minsec_strings[print_second];
        }
        canvas.drawText(
                to_print,
                cx + diam * (2f / 3f + 1f / 8f), cy + diam * (2f / 3f + 1f / 8f) + textYOffset,
                text_paint
        );
    }

    // set default values for certain fields
    // prefs is needed in order to write new values
    // hour_12_24 is the last saved setting for 12 v. 24 hour clock
    // prefer_mono is the last saved setting for monochrome v. color
    // prefer_digi is the last saved setting for analog v. digital clock
    void setPrefs(
            SharedPreferences prefs, boolean hour_12_24, boolean prefer_mono, boolean prefer_digi
    ) {

        my_prefs = prefs;

        color = !prefer_mono;
        set_color_or_monochrome();
        analog = !prefer_digi;
        if (hour_12_24) hour_modulus = 8;
        else hour_modulus = 4;

    }

    // we need to listen to certain buttons
    // see code for indication of which parameter corresponds to which button
    void setButtonsToListen(
            ToggleButton hb, ToggleButton mb, ToggleButton ab, ToggleButton pb,
            Button dn, Button up, Spinner spin, EditText vb, Button ib
            ) {
        hourButton = hb;
        monochromeButton = mb;
        analogButton = ab;
        activeToggle = pb;
        incrementer = up;
        decrementer = dn;
        unitSelecter = spin;
        valueEditor = vb;
        helpButton = ib;
    }

    public void set_color_or_monochrome() {
        if (color) {
            second_color = GOODGREEN;
            minute_color = RED;
            hour_color = BLUE;
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putBoolean(my_owner.getString(R.string.saved_color), false);
            edit.commit();
        } else {
            second_color = BLACK;
            minute_color = LTGRAY;
            hour_color = VERYLIGHTGRAY;
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putBoolean(my_owner.getString(R.string.saved_color), true);
            edit.commit();
        }
    }

    // handle pressing of toggle buttons
    // every received signal should call an invalidate() to redraw the clock
    // several will write new preferences data
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == hourButton) {
            if (hour_modulus == 4) {
                hour_modulus = 8;
                which_hour = Calendar.HOUR_OF_DAY;
                SharedPreferences.Editor edit = my_prefs.edit();
                edit.putBoolean(my_owner.getString(R.string.saved_hour), true);
                edit.commit();
            }
            else {
                hour_modulus = 4;
                which_hour = Calendar.HOUR;
                SharedPreferences.Editor edit = my_prefs.edit();
                edit.putBoolean(my_owner.getString(R.string.saved_hour), false);
                edit.commit();
            }
            invalidate();
        } else if (buttonView == monochromeButton) {
            color = !color;
            set_color_or_monochrome();
            invalidate();
        } else if (buttonView == analogButton) {
            analog = !analog;
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putBoolean(my_owner.getString(R.string.saved_anadig), !analog);
            edit.commit();
            invalidate();
        } else if (buttonView == activeToggle) {
            if (my_animator.is_paused()) {
                my_animator.resume();
                incrementer.setVisibility(View.INVISIBLE);
                decrementer.setVisibility(View.INVISIBLE);
                unitSelecter.setVisibility(View.INVISIBLE);
                valueEditor.setVisibility(View.INVISIBLE);
                time_guide = Modification.CALENDAR;
            } else {
                my_animator.pause();
                unitSelecter.setSelection(0);
                switch (hour_modulus) {
                case 4:
                //default:
                    valueEditor.setText(hour12_strings[last_h]);
                    break;
                case 8:
                    valueEditor.setText(hour24_strings[last_h]);
                    break;
                }
                valueEditor.selectAll();
                incrementer.setVisibility(View.VISIBLE);
                decrementer.setVisibility(View.VISIBLE);
                unitSelecter.setVisibility(View.VISIBLE);
                valueEditor.setVisibility(View.VISIBLE);
                valueEditor.selectAll();
            }
        }
        Log.d(tag, String.valueOf(my_prefs.getBoolean(my_owner.getString(R.string.saved_color), false)));

    }

    // show or hide the buttons
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == ACTION_DOWN) {
            if (hourButton.getVisibility() == VISIBLE) {
                hourButton.setVisibility(INVISIBLE);
                monochromeButton.setVisibility(INVISIBLE);
                analogButton.setVisibility(INVISIBLE);
                activeToggle.setVisibility(INVISIBLE);
                valueEditor.setVisibility(INVISIBLE);
                unitSelecter.setVisibility(INVISIBLE);
                incrementer.setVisibility(INVISIBLE);
                decrementer.setVisibility(INVISIBLE);
                helpButton.setVisibility(INVISIBLE);
            } else {
                hourButton.setVisibility(VISIBLE);
                monochromeButton.setVisibility(VISIBLE);
                analogButton.setVisibility(VISIBLE);
                activeToggle.setVisibility(VISIBLE);
                helpButton.setVisibility(VISIBLE);
                if (!activeToggle.isChecked()) {
                    valueEditor.setVisibility(INVISIBLE);
                    unitSelecter.setVisibility(INVISIBLE);
                    incrementer.setVisibility(INVISIBLE);
                    decrementer.setVisibility(INVISIBLE);

                } else {
                    valueEditor.setVisibility(VISIBLE);
                    valueEditor.selectAll();
                    unitSelecter.setVisibility(VISIBLE);
                    incrementer.setVisibility(VISIBLE);
                    decrementer.setVisibility(VISIBLE);
                }
            }
        }
        return true;
    }

    // handle buttons (+1, -1, info) and clicking in the text editor
    @Override
    public void onClick(View v) {
        if (v == incrementer) {
            time_guide = Modification.INCREMENT;
            my_offset = 0.0f;
            my_animator.resume();
            my_animator.pause();
        } else if (v == decrementer) {
            time_guide = Modification.DECREMENT;
            my_offset = 0.0f;
            my_animator.resume();
            my_animator.pause();
        } else if (v == helpButton) {
            Intent help_view = new Intent(my_owner.getApplicationContext(), HelpActivity.class);
            my_owner.startActivity(help_view);
        } else if (v == valueEditor)
            new_time_value = Integer.valueOf(valueEditor.getText().toString()).intValue();
    }

    // handle seleciton of unit by user
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                which_unit_to_modify = Units.HOURS;
                switch (hour_modulus) {
                    case 4:
                    //default:
                        valueEditor.setText(hour12_strings[last_h]);
                        break;
                    case 8:
                        valueEditor.setText(hour24_strings[last_h]);
                        break;
                }
                break;
            case 1:
                which_unit_to_modify = Units.MINUTES;
                valueEditor.setText(minsec_strings[last_m]);
                break;
            case 2:
                which_unit_to_modify = Units.SECONDS;
                valueEditor.setText(minsec_strings[last_s]);
                break;
        }
        valueEditor.selectAll();
    }

    // interface requires me to implement this, but nothing needs doing
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // handle when the user enters a new time
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        new_time_value = Integer.valueOf(textView.getText().toString()).intValue();
        time_guide = Modification.NEW_VALUE;
        valueEditor.setEnabled(false);
        valueEditor.setEnabled(true);
        valueEditor.selectAll();
        my_offset = 0.0f;
        my_animator.resume();
        my_animator.pause();
        return true;
    }

    // this should be needed only once in the lifetime of the View,
    // but this can be many times because the View is re-constructed
    // every time the orientation changes
    void recalculate_positions() {

        w = (float) getWidth();
        h = (float) getHeight();
        cx = (float) (w / 2.0);
        cy = (float) (h / 2.0);

        diam = min(cx, cy);
        min_x = cx - diam; max_x = cx + diam;
        min_y = cy - diam; max_y = cy + diam;

        digi_step = diam / 2;

        obj_w2 = diam / 5;
        digi_paint.setTextSize(obj_w2 * 0.75f);
        Paint.FontMetrics fm = digi_paint.getFontMetrics();
        float digi_t_adjust = -fm.ascent;
        digi_ty = cy + digi_t_adjust / 2.5f;
        text_paint.setTextSize(diam / 6f);
        text_paint.setShadowLayer(diam / 24f, diam / 48f, diam / 48f, BLACK);
        textYOffset = diam / 24f;
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

        anal_hr3 = (float) (diam / 6.0);
        anal_hr4 = (float) (diam / 3.0);
        anal_mr3 = (float) (diam / 2.0);
        anal_mr4 = (float) (diam * 2.0 / 3.0);
        anal_mr5 = (float) (diam * 5.0 / 6.0);
        anal_sr3 = (float) (diam / 12.0 * 5.0);
        anal_sr4 = (float) (diam / 12.0 * 7.0);
        anal_sr5 = (float) (diam / 12.0 * 9.0);
        anal_br = diam / 28;
        anal_hatch_dist = diam / 60;
        anal_hatch_hr3_inner = anal_hr3 - anal_hatch_dist;
        anal_hatch_h3_outer = anal_hr3 + anal_hatch_dist;
        anal_hatch_hr4_inner = anal_hr4 - anal_hatch_dist;
        anal_hatch_h4_outer = anal_hr4 + anal_hatch_dist;
        anal_hatch_mr3_inner = anal_mr3 - anal_hatch_dist;
        anal_hatch_mr3_outer = anal_mr3 + anal_hatch_dist;
        anal_hatch_mr4_inner = anal_mr4 - anal_hatch_dist;
        anal_hatch_m4_outer = anal_mr4 + anal_hatch_dist;
        anal_hatch_mr5_inner = anal_mr5 - anal_hatch_dist;
        anal_hatch_m5_outer = anal_mr5 + anal_hatch_dist;



    }

    private float my_offset; // how far along the animation is (should range from 0 to 1)

    // fields that control layout of all clock elements
    private float min_x, min_y, max_x, max_y, w, h, cx, cy, diam;
    // fields that control layout of analog clock elements
    private float anal_hr3, anal_hr4, anal_mr3, anal_mr4, anal_mr5, anal_sr3, anal_sr4, anal_sr5,
                    anal_br, anal_hatch_dist, anal_hatch_hr3_inner, anal_hatch_h3_outer,
                    anal_hatch_hr4_inner, anal_hatch_h4_outer, anal_hatch_mr3_inner,
                    anal_hatch_mr3_outer, anal_hatch_mr4_inner, anal_hatch_m4_outer,
                    anal_hatch_mr5_inner, anal_hatch_m5_outer;
    // fields that control layout of digital clock elements (except the polygons)
    private float digi_step, digi_ty, digi_hcy1, digi_hcy2, digi_hty1, digi_hty2,
                    digi_mscy1, digi_mscy3, digi_msty1, digi_msty3, obj_w2, textYOffset;
    // arrays that store points to draw the polygons
    private float [] digi_h3_pts, digi_h4_pts, digi_h8_pts, digi_m3_pts, digi_m4_pts, digi_m5_pts,
                    digi_s3_pts, digi_s4_pts, digi_s5_pts;
    // remember the previous time
    private int last_h, last_m, last_s;
    // the Runnable that controls animation
    private CRC_Animation my_animator;
    // fields that control aspects of painting
    private Paint ball_paint, text_paint, back_paint, circle_paint, digi_paint;
    private static int GOODGREEN = Color.rgb(0, 224, 0);
    private static int BACKGROUND = Color.argb(192, 128, 128, 128);
    private static int VERYLIGHTGRAY = Color.rgb(223, 223, 233);
    private int second_color, minute_color, hour_color;
    // used to draw polygons
    private Path backPath;

    // activity controlling this clock
    private Chinese_Remainder my_owner;

    // whether we are calculating 12- or 24-hour time
    private int hour_modulus;

    // UI elements
    private ToggleButton hourButton, monochromeButton, analogButton, activeToggle;
    private Button incrementer, decrementer, helpButton;
    private Spinner unitSelecter;
    private EditText valueEditor;
    // fields related to the UI elements
    private boolean color, analog;
    private int which_hour;

    // useful constants to make code more legible
    private enum Units { HOURS, MINUTES, SECONDS };
    private Units which_unit_to_modify;
    private enum Modification { LEAVE_BE, CALENDAR, INCREMENT, DECREMENT, NEW_VALUE };
    private Modification time_guide;
    private int new_time_value;

    // various useful constant strings
    private static final String tag = "CRC_View"; // debugging
    private static final String zero_str = "0";
    private static final String twelve_str = "12";
    private static final String dbl_zero_str = "00";
    private static final String colon_str = ":";
    final private String [] hour12_strings = {
            "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
    };
    final private String [] hour24_strings = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    };
    final private String [] minsec_strings = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
    };

    // preferences file
    private SharedPreferences my_prefs;

}
