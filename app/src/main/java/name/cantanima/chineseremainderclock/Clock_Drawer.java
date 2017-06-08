package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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

/**
 * Created by cantanima on 6/5/17.
 */

public abstract class Clock_Drawer {

    abstract void draw(Canvas canvas);

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
}
