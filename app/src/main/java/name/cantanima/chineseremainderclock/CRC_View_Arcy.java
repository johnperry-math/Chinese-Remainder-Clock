package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.Calendar;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.FILL_AND_STROKE;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;

/**
 * Created by cantanima on 6/6/17.
 */

public class CRC_View_Arcy extends Clock_Drawer {

    // constructor
    public CRC_View_Arcy(CRC_View owner) {

        initialize_fields(owner);

    }

    float preferred_step() { return 0.05f; }

    @Override
    void draw(Canvas canvas) {

        Calendar time = Calendar.getInstance();

        int hour, minute, second;
        if (my_viewer.time_guide == CALENDAR) { // moving according to the clock
            hour = time.get(my_viewer.which_hour);
            minute = time.get(Calendar.MINUTE);
            second = time.get(Calendar.SECOND);
        } else if (my_viewer.time_guide == CRC_View.Modification.NEW_VALUE) { // user has manually entered a time
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
        ball_paint.setStyle(STROKE);
        ball_paint.setStrokeWidth(1f);
        for (int i = 0; i < 3; ++i) {
            if (my_viewer.last_h != hour) {
                if ((hour + 2) % 3 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_h3[i], ball_paint);
                } else if (hour % 3 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_h3[i], ball_paint);
                }
            } else if (hour % 3 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h3[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_h3[i], ball_paint);
        }
        if (my_viewer.hour_modulus == 4) {
            for (int i = 0; i < 4; ++i) {
                if (my_viewer.last_m != minute) {
                    if ((hour + 3) % 4 == i && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_h4[i], ball_paint);
                    } else if (hour % 4 == i) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                        canvas.drawPath(path_h4[i], ball_paint);
                    }
                } else if (hour % 4 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_h4[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h4[i], ball_paint);
            }
        } else {
            for (int i = 0; i < 8; ++i) {
                if (my_viewer.last_h != hour) {
                    if ((hour + 7) % 8 == i && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_h8[i], ball_paint);
                    } else if (hour % 8 == i) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                        canvas.drawPath(path_h8[i], ball_paint);
                    }
                } else if (hour % 8 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_h8[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h8[i], ball_paint);
            }
        }
        ball_paint.setColor(minute_color);
        for (int i = 0; i < 3; ++i) {
            if (my_viewer.last_m != minute) {
                if ((minute + 2) % 3 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m3[i], ball_paint);
                } else if (minute % 3 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_m3[i], ball_paint);
                }
            } else if (minute % 3 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m3[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m3[i], ball_paint);
        }
        for (int i = 0; i < 4; ++i) {
            if (my_viewer.last_m != minute) {
                if ((minute + 3) % 4 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m4[i], ball_paint);
                } else if (minute % 4 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_m4[i], ball_paint);
                }
            } else if (minute % 4 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m4[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m4[i], ball_paint);
        }
        for (int i = 0; i < 5; ++i) {
            if (my_viewer.last_m != minute) {
                if ((minute + 4) % 5 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m5[i], ball_paint);
                } else if (minute % 5 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_m5[i], ball_paint);
                }
            } else if (minute % 5 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m5[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m5[i], ball_paint);
        }
        ball_paint.setColor(second_color);
        for (int i = 0; i < 3; ++i) {
            if (my_viewer.last_s != second) {
                if ((second + 2) % 3 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_s3[i], ball_paint);
                } else if (second % 3 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_s3[i], ball_paint);
                }
            } else if (second % 3 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_s3[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_s3[i], ball_paint);
        }
        for (int i = 0; i < 4; ++i) {
            if (my_viewer.last_s != second) {
                if ((second + 3) % 4 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_s4[i], ball_paint);
                } else if (second % 4 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_s4[i], ball_paint);
                }
            } else if (second % 4 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_s4[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_s4[i], ball_paint);
        }
        for (int i = 0; i < 5; ++i) {
            if (my_viewer.last_s != second) {
                if ((second + 4) % 5 == i && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_s5[i], ball_paint);
                } else if (second % 5 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * my_viewer.my_offset));
                    canvas.drawPath(path_s5[i], ball_paint);
                }
            } else if (second % 5 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_s5[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_s5[i], ball_paint);
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

        rh3 = diam * 1f / 10f;
        rhh = diam * 2f / 10f;
        rm3 = diam * 3f / 10f;
        rm4 = diam * 4f / 10f;
        rm5 = diam * 5f / 10f;
        rs3 = diam * 6f / 10f;
        rs4 = diam * 7f / 10f;
        rs5 = diam * 8f / 10f;

        float r_off = diam / 40f;

        path_h3 = new Path [] { new Path(), new Path(), new Path() };
        path_h4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_h8 = new Path [] { new Path(), new Path(), new Path(), new Path() ,
                                new Path(), new Path(), new Path(), new Path() };
        path_m3 = new Path [] { new Path(), new Path(), new Path() };
        path_m4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_m5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };
        path_s3 = new Path [] { new Path(), new Path(), new Path() };
        path_s4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_s5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };

        for (int i = 0; i < 3; ++i) {

            path_h3[i].rewind(); path_m3[i].rewind(); path_s3[i].rewind();

            RectF hrect = new RectF(
                    cx - (rh3 - r_off), cy - (rh3 - r_off), cx + (rh3 - r_off), cy + (rh3 - r_off)
            );
            path_h3[i].arcTo(hrect, 270 + i*120 - 55, 110, true);
            hrect = new RectF(
                    cx - (rh3 + r_off), cy - (rh3 + r_off), cx + (rh3 + r_off), cy + (rh3 + r_off)
            );
            path_h3[i].arcTo(hrect, 270 + i*120 + 55, -110, false);
            path_h3[i].close();

            RectF mrect = new RectF(
                    cx - (rm3 - r_off), cy - (rm3 - r_off), cx + (rm3 - r_off), cy + (rm3 - r_off)
            );
            path_m3[i].arcTo(mrect, 270 + i*120 - 55, 110, true);
            mrect = new RectF(
                    cx - (rm3 + r_off), cy - (rm3 + r_off), cx + (rm3 + r_off), cy + (rm3 + r_off)
            );
            path_m3[i].arcTo(mrect, 270 + i*120 + 55, -110, false);
            path_m3[i].close();

            RectF srect = new RectF(
                    cx - (rs3 - r_off), cy - (rs3 - r_off), cx + (rs3 - r_off), cy + (rs3 - r_off)
            );
            path_s3[i].arcTo(srect, 270 + i*120 - 55, 110, true);
            srect = new RectF(
                    cx - (rs3 + r_off), cy - (rs3 + r_off), cx + (rs3 + r_off), cy + (rs3 + r_off)
            );
            path_s3[i].arcTo(srect, 270 + i*120 + 55, -110, false);
            path_s3[i].close();


        }

        for (int i = 0; i < 4; ++i) {
            
            path_h4[i].rewind(); path_m4[i].rewind(); path_s4[i].rewind();

            RectF hrect = new RectF(
                    cx - (rhh - r_off), cy - (rhh - r_off), cx + (rhh - r_off), cy + (rhh - r_off)
            );
            path_h4[i].arcTo(hrect, 270 + i*90 - 40, 80, true);
            hrect = new RectF(
                    cx - (rhh + r_off), cy - (rhh + r_off), cx + (rhh + r_off), cy + (rhh + r_off)
            );
            path_h4[i].arcTo(hrect, 270 + i*90 + 40, -80, false);
            path_h4[i].close();

            RectF mrect = new RectF(
                    cx - (rm4 - r_off), cy - (rm4 - r_off), cx + (rm4 - r_off), cy + (rm4 - r_off)
            );
            path_m4[i].arcTo(mrect, 270 + i*90 - 40, 80, true);
            mrect = new RectF(
                    cx - (rm4 + r_off), cy - (rm4 + r_off), cx + (rm4 + r_off), cy + (rm4 + r_off)
            );
            path_m4[i].arcTo(mrect, 270 + i*90 + 40, -80, false);
            path_m4[i].close();

            RectF srect = new RectF(
                    cx - (rs4 - r_off), cy - (rs4 - r_off), cx + (rs4 - r_off), cy + (rs4 - r_off)
            );
            path_s4[i].arcTo(srect, 270 + i*90 - 40, 80, true);
            srect = new RectF(
                    cx - (rs4 + r_off), cy - (rs4 + r_off), cx + (rs4 + r_off), cy + (rs4 + r_off)
            );
            path_s4[i].arcTo(srect, 270 + i*90 + 40, -80, false);
            path_s4[i].close();


        }

        for (int i = 0; i < 8; ++i) {

            path_h8[i].rewind();

            RectF hrect = new RectF(
                    cx - (rhh - r_off), cy - (rhh - r_off), cx + (rhh - r_off), cy + (rhh - r_off)
            );
            path_h8[i].arcTo(hrect, 270 + i * 45 - 17.5f, 35, true);
            hrect = new RectF(
                    cx - (rhh + r_off), cy - (rhh + r_off), cx + (rhh + r_off), cy + (rhh + r_off)
            );
            path_h8[i].arcTo(hrect, 270 + i * 45 + 17.5f, -35, false);
            path_h8[i].close();

        }
        
        for (int i = 0; i < 5; ++i) {

            path_m5[i].rewind(); path_s5[i].rewind();

            RectF mrect = new RectF(
                    cx - (rm5 - r_off), cy - (rm5 - r_off), cx + (rm5 - r_off), cy + (rm5 - r_off)
            );
            path_m5[i].arcTo(mrect, 270 + i*72 - 31, 62, true);
            mrect = new RectF(
                    cx - (rm5 + r_off), cy - (rm5 + r_off), cx + (rm5 + r_off), cy + (rm5 + r_off)
            );
            path_m5[i].arcTo(mrect, 270 + i*72 + 31, -62, false);
            path_m5[i].close();

            RectF srect = new RectF(
                    cx - (rs5 - r_off), cy - (rs5 - r_off), cx + (rs5 - r_off), cy + (rs5 - r_off)
            );
            path_s5[i].arcTo(srect, 270 + i*72 - 31, 62, true);
            srect = new RectF(
                    cx - (rs5 + r_off), cy - (rs5 + r_off), cx + (rs5 + r_off), cy + (rs5 + r_off)
            );
            path_s5[i].arcTo(srect, 270 + i*72 + 31, -62, false);
            path_s5[i].close();


        }

    }

    float rh3, rhh, rm3, rm4, rm5, rs3, rs4, rs5;

    Path [] path_h3, path_h4, path_h8, path_m3, path_m4, path_m5, path_s3, path_s4, path_s5;

    final static String tag = "CRC_View_Arcy";
}
