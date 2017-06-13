package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.Calendar;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.FILL_AND_STROKE;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;

/**
 * Created by cantanima on 6/6/17.
 */

public class CRC_View_Bubbly extends Clock_Drawer {

    // constructor
    public CRC_View_Bubbly(CRC_View owner) {

        initialize_fields(owner);

    }

    @Override
    void draw(Canvas canvas) {

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

        ball_paint.setColor(hour_color);
        ball_paint.setStyle(FILL);
        canvas.drawPath(h_tria, ball_paint);
        int hmod3 = (hour % 3) == 0 ? 3 : hour % 3;
        for (int i = 0; i < 2; ++i) {
            ball_paint.setStyle(FILL);
            if (my_viewer.last_h == hour) {
                if (i + 1 <= hour % 3) {
                    canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            } else {
                if (hmod3 == 3) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else {
                    if (i + 1 < hmod3 || (i + 1 == hmod3 && my_viewer.my_offset > 0.9)) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else if (i + 1 == hmod3) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
        ball_paint.setStyle(FILL);
        if (my_viewer.hour_modulus == 4) {
            canvas.drawPath(h_quad, ball_paint);
            int hmod4 = (hour % 4) == 0 ? 4 : hour % 4;
            for (int i = 0; i < 3; ++i) {
                ball_paint.setStyle(FILL);
                if (my_viewer.last_h == hour) {
                    if (i + 1 <= hour % 4) {
                        canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                } else {
                    if (hmod4 == 4) {
                        ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                        canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else {
                        if (i + 1 < hmod4 || (i + 1 == hmod4 && my_viewer.my_offset > 0.9)) {
                            ball_paint.setAlpha(255);
                            canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                        } else if (i + 1 == hmod4) {
                            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                            canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                        }
                    }
                }
                ball_paint.setAlpha(255);
                ball_paint.setStyle(STROKE);
                canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
            }
        } else {
            canvas.drawPath(h_octo, ball_paint);
            int hmod8 = (hour % 8) == 0 ? 8 : hour % 8;
            float r = cradius / 1.1f;
            for (int i = 0; i < 6; ++i) {
                float x = h_xh - r;
                ball_paint.setStyle(FILL);
                if (my_viewer.last_h == hour) {
                    if (i + 1 <= hour % 8)
                        canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - (i / 2) * cstep, r/2f, ball_paint);
                } else {
                    if (hmod8 == 8) {
                        ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                        canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - (i / 2) * cstep, r/2f, ball_paint);
                    } else {
                        if (i + 1 < hmod8 || (i + 1 == hmod8 && my_viewer.my_offset > 0.9)) {
                            ball_paint.setAlpha(255);
                            canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - (i / 2) * cstep, r/2f, ball_paint);
                        } else if (i + 1 == hmod8) {
                            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                            canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - (i / 2) * cstep, r/2f, ball_paint);
                        }
                    }
                }
                ball_paint.setAlpha(255);
                ball_paint.setStyle(STROKE);
                canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - (i / 2) * cstep, r/2f, ball_paint);
            }
            ball_paint.setStyle(FILL);
            if (my_viewer.last_h == hour) {
                if (6 < hour % 8)
                    canvas.drawCircle(h_xh, h_y - vstep * 0.85f - 3 * cstep, r / 2f, ball_paint);
            } else {
                if (hmod8 == 8) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
                } else {
                    if (7 < hmod8  || 7 == hmod8 && my_viewer.my_offset > 0.9) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
                    } else if (7 == hmod8) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
        }

        ball_paint.setColor(minute_color);
        ball_paint.setStyle(FILL);
        canvas.drawPath(m_tria, ball_paint);
        int mmod3 = (minute % 3) == 0 ? 3 : minute % 3;
        for (int i = 0; i < 2; ++i) {
            ball_paint.setStyle(FILL);
            if (my_viewer.last_m == minute) {
                if (i + 1 <= minute % 3) {
                    canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            } else {
                if (mmod3 == 3) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else {
                    if (i + 1 < mmod3 || (i + 1 == mmod3 && my_viewer.my_offset > 0.9)) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else if (i + 1 == mmod3) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
        ball_paint.setAlpha(255); ball_paint.setStyle(FILL);
        canvas.drawPath(m_quad, ball_paint);
        int mmod4 = (minute % 4) == 0 ? 4 : minute % 4;
        for (int i = 0; i < 3; ++i) {
            ball_paint.setStyle(FILL);
            if (my_viewer.last_m == minute) {
                if (i + 1 <= minute % 4) {
                    canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            } else {
                if (mmod4 == 4) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else {
                    if (i + 1 < mmod4 || (i + 1 == mmod4 && my_viewer.my_offset > 0.9)) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else if (i + 1 == mmod4) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
        ball_paint.setStyle(FILL);
        canvas.drawPath(m_pent, ball_paint);
        int mmod5 = (minute % 5) == 0 ? 5 : minute % 5;
        for (int i = 0; i < 4; ++i) {
            ball_paint.setStyle(FILL);
            if (my_viewer.last_m == minute) {
                if (i + 1 <= minute % 5) {
                    canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            } else {
                if (mmod5 == 5) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else {
                    if (i + 1 < mmod5 || (i + 1 == mmod5 && my_viewer.my_offset > 0.9)) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else if (i + 1 == mmod5) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }

        ball_paint.setColor(second_color);
        ball_paint.setStyle(FILL);
        canvas.drawPath(s_tria, ball_paint);
        int smod3 = (second % 3) == 0 ? 3 : second % 3;
        for (int i = 0; i < 2; ++i) {
            ball_paint.setStyle(FILL);
            if (smod3 == 3) {
                ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                canvas.drawCircle(s_x3, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
            } else {
                if (i + 1 < smod3 || (i + 1 == smod3 && my_viewer.my_offset > 0.9)) {
                    ball_paint.setAlpha(255);
                    canvas.drawCircle(s_x3, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else if (i + 1 == smod3) {
                    ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                    canvas.drawCircle(s_x3, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            }
            ball_paint.setAlpha(255); ball_paint.setStyle(STROKE);
            canvas.drawCircle(s_x3, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
        ball_paint.setAlpha(255); ball_paint.setStyle(FILL);
        canvas.drawPath(s_quad, ball_paint);
        int smod4 = (second % 4) == 0 ? 4 : second % 4;
        for (int i = 0; i < 3; ++i) {
            ball_paint.setStyle(FILL);
            if (my_viewer.last_s == second) {
                if (i + 1 <= second % 4) {
                    canvas.drawCircle(s_x4, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            } else {
                if (smod4 == 4) {
                    ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                    canvas.drawCircle(s_x4, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else {
                    if (i + 1 < smod4 || (i + 1 == smod4 && my_viewer.my_offset > 0.9)) {
                        ball_paint.setAlpha(255);
                        canvas.drawCircle(s_x4, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    } else if (i + 1 == smod4) {
                        ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                        canvas.drawCircle(s_x4, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                    }
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(s_x4, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
        ball_paint.setStyle(FILL);
        canvas.drawPath(s_pent, ball_paint);
        int smod5 = (second % 5) == 0 ? 5 : second % 5;
        for (int i = 0; i < 4; ++i) {
            ball_paint.setStyle(FILL);
            if (smod5 == 5) {
                ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
                canvas.drawCircle(s_x5, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
            } else {
                if (i + 1 < smod5 || (i + 1 == smod5 && my_viewer.my_offset > 0.9)) {
                    ball_paint.setAlpha(255);
                    canvas.drawCircle(s_x5, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                } else if (i + 1 == smod5) {
                    ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
                    canvas.drawCircle(s_x5, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
                }
            }
            ball_paint.setAlpha(255);
            ball_paint.setStyle(STROKE);
            canvas.drawCircle(s_x5, s_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }

        // adjust certain settings once the animation ends

        if (my_viewer.my_offset >= 0.97f) {

            // remember this time as the last time
            my_viewer.last_h = hour;
            my_viewer.last_m = minute;
            my_viewer.last_s = second;

            // if the user manually modified the time, instructor the animator to leave it be
            // on the next pass
            if (my_viewer.time_guide == INCREMENT || my_viewer.time_guide == DECREMENT) {
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

    @Override
    void recalculate_positions() {

        super.recalculate_positions();

        hstep = w / 8f;
        vstep = h / 11f;
        radius = min(hstep, vstep) / 2.25f;
        cradius = radius * 0.7f;
        cstep = cradius * 2.35f;

        h_x3 = 2*hstep; h_xh = 3*hstep;
        m_x3 = 4*hstep; m_x4 = 5*hstep; m_x5 = 6*hstep;
        s_x3 = 3f*hstep; s_x4 = 4f*hstep; s_x5 = 5f*hstep;
        h_y = m_y = 5f*vstep;
        s_y = 9*vstep;

        h_tria = new Path();
        h_tria.moveTo(h_x3 - radius*1.15f * (float) cos(9 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
        h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(5 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(5 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(9 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
        
        h_quad = new Path();
        h_quad.moveTo(h_xh, h_y - radius);
        h_quad.lineTo(h_xh + radius, h_y);
        h_quad.lineTo(h_xh + radius, h_y);
        h_quad.lineTo(h_xh, h_y + radius);
        h_quad.lineTo(h_xh, h_y + radius);
        h_quad.lineTo(h_xh - radius, h_y);
        h_quad.lineTo(h_xh - radius, h_y);
        h_quad.lineTo(h_xh, h_y - radius);
        
        h_octo = new Path();
        h_octo.moveTo(h_xh - radius * (float) cos(12 * PI / 8), h_y + radius * (float) sin(12 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(10 * PI / 8), h_y + radius * (float) sin(10 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(10 * PI / 8), h_y + radius * (float) sin(10 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(8 * PI / 8), h_y + radius * (float) sin(8 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(8 * PI / 8), h_y + radius * (float) sin(8 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(6 * PI / 8), h_y + radius * (float) sin(6 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(6 * PI / 8), h_y + radius * (float) sin(6 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(4 * PI / 8), h_y + radius * (float) sin(4 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(4 * PI / 8), h_y + radius * (float) sin(4 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(2 * PI / 8), h_y + radius * (float) sin(2 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(2 * PI / 8), h_y + radius * (float) sin(2 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(0 * PI / 8), h_y + radius * (float) sin(0 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(0 * PI / 8), h_y + radius * (float) sin(0 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(14 * PI / 8), h_y + radius * (float) sin(14 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(14 * PI / 8), h_y + radius * (float) sin(14 * PI / 8));
        h_octo.lineTo(h_xh - radius * (float) cos(12 * PI / 8), h_y + radius * (float) sin(12 * PI / 8));

        m_tria = new Path();
        m_tria.moveTo(m_x3 - radius*1.15f * (float) cos(9 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
        m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(5 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(5 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(9 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));

        m_quad = new Path();
        m_quad.moveTo(m_x4, m_y - radius);
        m_quad.lineTo(m_x4 + radius, m_y);
        m_quad.lineTo(m_x4 + radius, m_y);
        m_quad.lineTo(m_x4, m_y + radius);
        m_quad.lineTo(m_x4, m_y + radius);
        m_quad.lineTo(m_x4 - radius, m_y);
        m_quad.lineTo(m_x4 - radius, m_y);
        m_quad.lineTo(m_x4, m_y - radius);
        
        m_pent = new Path();
        m_pent.moveTo(m_x5 - radius * (float) cos(15 * PI / 10), m_y + radius * (float) sin(15 *  PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(11 * PI / 10), m_y + radius * (float) sin(11 *  PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(11 * PI / 10), m_y + radius * (float) sin(11 * PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(7 * PI / 10), m_y + radius * (float) sin(7 *  PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(7 * PI / 10), m_y + radius * (float) sin(7 * PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(3 * PI / 10), m_y + radius * (float) sin(3 * PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(3 * PI / 10), m_y + radius * (float) sin(3 *  PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(19 * PI / 10), m_y + radius * (float) sin(19 *  PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(19 * PI / 10), m_y + radius * (float) sin(19 * PI / 10));
        m_pent.lineTo(m_x5 - radius * (float) cos(15 * PI / 10), m_y + radius * (float) sin(15 * PI / 10));

        s_tria = new Path();
        s_tria.moveTo(s_x3 - radius*1.15f * (float) cos(9 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
        s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(5 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(5 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
        s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
        s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(9 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));

        s_quad = new Path();
        s_quad.moveTo(s_x4, s_y - radius);
        s_quad.lineTo(s_x4 + radius, s_y);
        s_quad.lineTo(s_x4 + radius, s_y);
        s_quad.lineTo(s_x4, s_y + radius);
        s_quad.lineTo(s_x4, s_y + radius);
        s_quad.lineTo(s_x4 - radius, s_y);
        s_quad.lineTo(s_x4 - radius, s_y);
        s_quad.lineTo(s_x4, s_y - radius);

        s_pent = new Path();
        s_pent.moveTo(s_x5 - radius * (float) cos(15 * PI / 10), s_y + radius * (float) sin(15 *  PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(11 * PI / 10), s_y + radius * (float) sin(11 *  PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(11 * PI / 10), s_y + radius * (float) sin(11 * PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(7 * PI / 10), s_y + radius * (float) sin(7 *  PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(7 * PI / 10), s_y + radius * (float) sin(7 * PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(3 * PI / 10), s_y + radius * (float) sin(3 * PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(3 * PI / 10), s_y + radius * (float) sin(3 *  PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(19 * PI / 10), s_y + radius * (float) sin(19 *  PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(19 * PI / 10), s_y + radius * (float) sin(19 * PI / 10));
        s_pent.lineTo(s_x5 - radius * (float) cos(15 * PI / 10), s_y + radius * (float) sin(15 * PI / 10));

    }

    float preferred_step() { return 0.15f; }

    float hstep, vstep, radius, cradius, cstep;
    
    float h_x3, h_xh, m_x3, m_x4, m_x5, s_x3, s_x4, s_x5, h_y, m_y, s_y;
    
    Path h_tria, h_quad, h_octo, m_tria, m_quad, m_pent, s_tria, s_quad, s_pent;

    private static final String tag = "Bubbly";
}
