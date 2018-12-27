package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
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
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_TIME;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;

/**
 *
 * Draws the clock. Implement all designs as a subclass of this one.
 * When you implement the constructor, you can call initialize_fields() in order to gain access
 * to routine and desirable information.
 * The two functions you absolutely have to implement is draw() and preferred_step();
 * see them for details.
 * You will probably also want to implement recalculate_positions().
 * In many cases, and probably most, you should use recalculate_positions() to set up Paths
 * for the various pieces of the design.
 *
 * @see #initialize_fields(CRC_View)
 * @see #draw(Canvas)
 * @see #preferred_step()
 * @see #recalculate_positions()
 * @see android.graphics.Path
 *
 */
public abstract class Clock_Drawer {

  /**
   * This should draw the clock onto the specified Canvas.
   * The usual order of events should be to invoke setup_time() FIRST, then (if desired)
   * drawTimeAndRectangle(), which takes care of the background.
   * Only then should you perform the drawing specific to the design!
   * At the end, a subclass should also invoke usual_cleanup().
   * If you follow this course of events, then the values short_hand, minute, second,
   * last_h, last_m, last_s will be set up properly so that you can read them for animation,
   * and all other "ordinary" set up and destruction will be taken care of.
   * @param canvas Canvas on which to draw the clock
   */
  abstract void draw(Canvas canvas);

  /**
   * Sets up time values according to CRC_View's time_guide.
   */
  void setup_time() {

    // get the current time, compare to previous time, and animate accordingly
    // my_viewer.time_guide is used to help determine what sort of animation to use (if any)

    Calendar time = Calendar.getInstance();

    millis = time.get(Calendar.MILLISECOND);

    if (my_viewer.time_guide == CALENDAR) { // moving according to the clock
      hour = time.get(my_viewer.which_hour);
      minute = time.get(MINUTE);
      second = time.get(SECOND);
    } else if (my_viewer.time_guide == NEW_VALUE) { // user has manually entered a value for a unit
      hour = my_viewer.last_h;
      minute = my_viewer.last_m;
      second = my_viewer.last_s;
      switch (my_viewer.unitSelecter.getSelectedItemPosition()) {
        case 0: hour = my_viewer.new_time_value; break;
        case 1: minute = my_viewer.new_time_value; break;
        case 2: second = my_viewer.new_time_value; break;
      }
    } else if (my_viewer.time_guide == NEW_TIME) { // quiz has manually set the time
      hour = my_viewer.new_hour_value;
      minute = my_viewer.new_minute_value;
      second = my_viewer.last_s;
    } else if (my_viewer.time_guide == LEAVE_BE) { // use previous time
      hour = my_viewer.last_h;
      minute = my_viewer.last_m;
      second = my_viewer.last_s;
    } else { // user pressed increment or decrement button
      hour = my_viewer.last_h;
      minute = my_viewer.last_m;
      second = my_viewer.last_s;
      int direction;
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

  }

  /**
   *  Adjusts certain settings once the animation ends.
   *  This includes adjusting CRC_View's time_guide when necessary, as well as the remembered
   *  times last_h, last_m, last_s, and updating some interface elements.
   *  A guard is placed on this so that it only works if the animation is really and truly complete.
   */
  void usual_cleanup() {

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
      if (my_viewer.unitSelecter != null) {
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

  }

  /** Returns the preferred time step for an animation. */
  abstract float preferred_step();

  /**
   *  Recalculates positions necessary to drawing.
   *  You should probably override this, but if so you may want to call
   *  super.recalculate_positions() first, so that w, h, cx, cy, diam, min_x, max_x, min_y, max_y,
   *  textYOffset, text_paint are set up properly.
   *  When you override this, it is a good idea to set up Paths to store various objects that make
   *  up the design; then draw() can simply call drawPath() on the paths using different paints.
   *  @see android.graphics.Path
   */
  void recalculate_positions() {
    w = (float) my_viewer.getWidth();
    h = (float) my_viewer.getHeight();
    cx = (float) (w / 2.0);
    cy = (float) (h / 2.0);

    diam = min(cx, cy);
    min_x = cx - diam; max_x = cx + diam;
    min_y = cy - diam; max_y = cy + diam;

    text_paint.setTextSize(diam / 6f);
    text_paint.setShadowLayer(diam / 24f, diam / 48f, diam / 48f, BLACK);

  }

  /**
   *
   * Sets up my_viewer, back_paint, text_paint, ball_paint, circle_paint, second_color,
   * minute_color, hour_color, minsec_strings, hour12_strings, hour24_strings.
   * @param view  CRC_View we are responsible ot
   */
  void initialize_fields(CRC_View view) {

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
    second_color = GOODGREEN;
    minute_color = RED;
    hour_color = BLUE;

    minsec_strings = my_viewer.minsec_strings;
    hour12_strings = my_viewer.hour12_strings;
    hour24_strings = my_viewer.hour24_strings;

  }

  /**
   * Draws a background rectangle with rounded corners and then calls draw_time().
   * @see #draw_time(int, int, int)
   * @param canvas Canvas to draw onto
   * @param hour short_hand to write
   * @param minute minute to write
   * @param second second to write
   * @param diam "radius" of the Canvas (don't ask why it's called diam)
   */
  void drawTimeAndRectangle(Canvas canvas, int hour, int minute, int second, float diam) {

    // draw a rounded rectangle if we can
    back_paint.setColor(bg_color);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      canvas.drawRoundRect(min_x, min_y, max_x, max_y, diam / 6f, diam / 6f, back_paint);
    } else {
      canvas.drawRect(min_x, min_y, max_x, max_y, back_paint);
    }

    // show time if desired
    if (show_time && tv != null) {
      // if the offset is roughly at 1, print the new time, else print the old time
      // (if someone is looking carefully, the clock will seem to be 1 second behind)
      int print_hour, print_minute, print_second;
      if (my_viewer.my_offset >= 0.96f) {
        print_hour = hour;
        print_minute = minute;
        print_second = second;
      } else {
        print_hour = my_viewer.last_h;
        print_minute = my_viewer.last_m;
        print_second = my_viewer.last_s;
      }

      draw_time(print_hour, print_minute, print_second);
    }

  }

  /**
   * Draws the indicated time onto the TextView for the time (tv).
   * @param print_hour short_hand to print
   * @param print_minute minute to print
   * @param print_second second to print
   */
  void draw_time(int print_hour, int print_minute, int print_second) {

    // print the correct times
    String to_print;
    if (print_hour % 12 == 0) {
      if (my_viewer.which_hour == Calendar.HOUR) {
        to_print = twelve_str;
      } else {
        if (print_hour == 0 || print_hour == 24)
          to_print = dbl_zero_str;
        else to_print = twelve_str;
      }
    } else if (print_hour > 0 && print_hour < 10) {
      to_print = zero_str + hour12_strings[print_hour];
    } else {
      to_print = String.valueOf(print_hour);
    }
    to_print += colon_str;
    if (print_minute == 60)
      to_print += dbl_zero_str;
    else {
      if (print_minute < 10)
        to_print += zero_str + minsec_strings[print_minute];
      else
        to_print += minsec_strings[print_minute];
    }
    if (show_seconds) {
      if (print_second == 60)
        to_print += colon_str + dbl_zero_str;
      else {
        if (print_second < 10)
          to_print += colon_str + zero_str + minsec_strings[print_second];
        else
          to_print += colon_str + minsec_strings[print_second];
      }
    }
    tv.setText(to_print);

  }

  /** remembers the TextView where we should write the time */
  void set_time_textview(TextView ttv) { tv = ttv; }

  /** whether to show a representation of seconds in the clock */
  void set_show_seconds(boolean yesno) {
      show_seconds = yesno;
  }

  /** whether to show a representation of seconds in the clock */
  boolean get_show_seconds() { return show_seconds; }

  /**
   *  whether to write the time in a TextView
   *  @see #set_time_textview(TextView)
   */
  void set_show_time(boolean yesno) {
      show_time = yesno;
  }

  /** whether to draw minutes inside/left of hours */
  void set_reverse_orientation(boolean yesno) { reverse_orientation = yesno; }

  /** which color to use when drawing lines */
  void set_line_color(int new_line_color) { line_color = new_line_color; }

  /** which color to use when drawing short_hand objects */
  void set_hour_color(int new_hour_color) { hour_color = new_hour_color; }

  /** which color to use when drawing minute objects */
  void set_minute_color(int new_minute_color) { minute_color = new_minute_color; }

  /** which color to use when drawing second objects */
  void set_second_color(int new_second_color) { second_color = new_second_color; }

  /** which color to use when drawing the background */
  void set_bg_color(int new_bg_color) { bg_color = new_bg_color; }

  /** user has touched finger to clock */
  protected void notify_touched(MotionEvent e) { }

  /** user has lifted finger off clock */
  protected void notify_released(MotionEvent e) { }

  /** user is dragging finger around the clock */
  protected void notify_dragged(MotionEvent e) { }

  /** user has switched to manual mode; make necessary adjustments */
  protected void notify_manual(boolean switched_on) { }

  /** fields that control aspects of painting */
  private final static int GOODGREEN = Color.rgb(0, 224, 0);
  private final static int BACKGROUND = Color.argb(192, 128, 128, 128);
  final static int VERYLIGHTGRAY = Color.rgb(223, 223, 233);
  int second_color, minute_color, hour_color, line_color;
  private int bg_color;

  /** fields that control layout of all clock elements */
  private float min_x, min_y, max_x, max_y;
  float w, h, cx, cy, diam;

  /** fields that control how to paint various objects */
  Paint ball_paint, circle_paint;
  private Paint text_paint, back_paint;

  // fields related to the UI elements
  protected boolean color;
  boolean show_seconds;
  private boolean show_time;
  boolean reverse_orientation;

  /** fields related to writing the time */
  static final String zero_str = "0";
  private static final String twelve_str = "12";
  private static final String dbl_zero_str = "00";
  private static final String colon_str = ":";
  private static String [] minsec_strings = {};
  private static String [] hour12_strings = {};
  private static String [] hour24_strings = {};

  /** stuff to listen to or update */
  CRC_View my_viewer;
  private TextView tv;

  /**
   *  time information: short_hand, minute, second will record current time (to draw),
   *  hours_max will indicate whether it's a 12- or 24-short_hand clock
   */
  int hour, minute, second, millis;

}
