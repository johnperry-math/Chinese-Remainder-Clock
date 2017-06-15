package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.min;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;

public abstract class Clock_Drawer {

    abstract void draw(Canvas canvas);

    void setup_time() {

        // get the current time, compare to previous time, and animate accordingly
        // my_viewer.time_guide is used to help determine what sort of animation to use (if any)

        Calendar time = Calendar.getInstance();

        millis = time.get(Calendar.MILLISECOND);

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
        hour_max = (my_viewer.hour_modulus == 4) ? 12 : 24;
        if (hour < 0) hour += hour_max;
        else if (hour >= hour_max) hour %= hour_max;
        if (minute < 0) minute += 60;
        else if (minute > 59) minute %= 60;
        if (second < 0) second += 60;
        else if (second > 59) second %= 60;

    }

    void usual_cleanup() {
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

    abstract float preferred_step();

    void recalculate_positions() {
        w = (float) my_viewer.getWidth();
        h = (float) my_viewer.getHeight();
        cx = (float) (w / 2.0);
        cy = (float) (h / 2.0);

        diam = min(cx, cy);
        min_x = cx - diam; max_x = cx + diam;
        min_y = cy - diam; max_y = cy + diam;

        textYOffset = diam / 24f;

        text_paint.setTextSize(diam / 6f);
        text_paint.setShadowLayer(diam / 24f, diam / 48f, diam / 48f, BLACK);

    }

    void initialize_fields(CRC_View view) {
        // initialize fields:

        my_viewer = view;

        // fields related to options that user toggles
        color = true;

        // fields related to drawing
        back_paint = new Paint(ANTI_ALIAS_FLAG);
        back_paint.setColor(BACKGROUND);
        text_paint = new Paint(ANTI_ALIAS_FLAG);
        text_paint.setColor(WHITE);
        text_paint.setTextAlign(CENTER);
        ball_paint = new Paint(ANTI_ALIAS_FLAG);
        circle_paint = new Paint(ANTI_ALIAS_FLAG);
        circle_paint.setColor(WHITE);
        circle_paint.setStyle(STROKE);
        if (color) {
            second_color = GOODGREEN;
            minute_color = RED;
            hour_color = BLUE;
        } else {
            second_color = BLACK;
            minute_color = LTGRAY;
            hour_color = VERYLIGHTGRAY;
        }

        minsec_strings = my_viewer.minsec_strings;
        hour12_strings = my_viewer.hour12_strings;
        hour24_strings = my_viewer.hour24_strings;

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
        int print_hour, print_minute, print_second;
        if (my_viewer.my_offset >= 0.96f || !nextTime) {
            print_hour = hour;
            print_minute = minute;
            print_second = second;
        } else {
            print_hour = my_viewer.last_h;
            print_minute = my_viewer.last_m;
            print_second = my_viewer.last_s;
        }

        // print the correct times
        String to_print;
        if (print_hour % 12 == 0) {
            if (my_viewer.which_hour == Calendar.HOUR && (this instanceof CRC_View_Ballsy)) {
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

    protected void set_color(boolean yesno) {
        color = yesno; adjust_color();
    }
    protected void toggle_color() {
        color = !color; adjust_color();
    }
    protected void adjust_color() {
        if (color) {
            second_color = GOODGREEN;
            minute_color = RED;
            hour_color = BLUE;
        } else {
            second_color = BLACK;
            minute_color = LTGRAY;
            hour_color = VERYLIGHTGRAY;
        }
    }

    boolean is_color() { return color; }

    // fields that control aspects of painting
    protected final static int GOODGREEN = Color.rgb(0, 224, 0);
    protected final static int BACKGROUND = Color.argb(192, 128, 128, 128);
    protected final static int VERYLIGHTGRAY = Color.rgb(223, 223, 233);
    protected int second_color, minute_color, hour_color;

    // fields that control layout of all clock elements
    protected float min_x, min_y, max_x, max_y, w, h, cx, cy, diam, textYOffset;

    protected Paint ball_paint, text_paint, back_paint, circle_paint;

    // fields related to the UI elements
    protected boolean color;

    protected static final String zero_str = "0";
    protected static final String twelve_str = "12";
    protected static final String dbl_zero_str = "00";
    protected static final String colon_str = ":";

    protected static String [] minsec_strings = {};
    protected static String [] hour12_strings = {};
    protected static String [] hour24_strings = {};

    protected CRC_View my_viewer;

    protected int hour, minute, second, millis, hour_max;

}
