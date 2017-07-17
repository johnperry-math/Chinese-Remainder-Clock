package name.cantanima.chineseremainderclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
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

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;
import static name.cantanima.chineseremainderclock.Clock_Drawer.VERYLIGHTGRAY;

/**
 * Created by cantanima on 4/22/17.
 */

public class CRC_View
    extends View
    implements OnTouchListener, OnCheckedChangeListener, OnClickListener,
               OnItemSelectedListener, OnEditorActionListener,
               SharedPreferences.OnSharedPreferenceChangeListener
{

  // constructor
  public CRC_View(Context context, AttributeSet attrs) {

    super(context, attrs);

    hour_modulus = 4;
    which_hour = HOUR;
    time_guide = CALENDAR;

    if (isInEditMode()) {
      my_drawer = new CRC_View_Ballsy(this);
    } else {
      my_owner = (Chinese_Remainder) context;
      SharedPreferences prefs = my_owner.getPreferences(MODE_PRIVATE);
      boolean saved_hour = prefs.getBoolean(my_owner.getString(R.string.saved_hour), false);
      boolean saved_seconds = prefs.getBoolean(my_owner.getString(R.string.saved_show_seconds), false);
      boolean saved_time = prefs.getBoolean(my_owner.getString(R.string.saved_show_time), false);
      boolean saved_unit_orientation = prefs.getBoolean(my_owner.getString(R.string.saved_reverse_orientation), false);
      int saved_drawer = prefs.getInt(my_owner.getString(R.string.saved_drawer), 0);
      int saved_bg_color = prefs.getInt(my_owner.getString(R.string.saved_bg_color), GRAY);
      int saved_line_color = prefs.getInt(my_owner.getString(R.string.saved_line_color), WHITE);
      int saved_hour_color = prefs.getInt(my_owner.getString(R.string.saved_hour_color), BLUE);
      int saved_minute_color = prefs.getInt(my_owner.getString(R.string.saved_minute_color), RED);
      int saved_second_color = prefs.getInt(my_owner.getString(R.string.saved_second_color), GREEN);
      setPrefs(
              prefs,
              saved_hour, saved_seconds, saved_time, saved_unit_orientation,
              saved_drawer, saved_bg_color, saved_line_color,
              saved_hour_color, saved_minute_color, saved_second_color
      );
      setOnTouchListener(this);
      PreferenceManager.getDefaultSharedPreferences(my_owner)
              .registerOnSharedPreferenceChangeListener(this);
      if (saved_time) {
        if (tv != null) tv.setVisibility(VISIBLE);
      } else {
        if (tv != null) tv.setVisibility(INVISIBLE);
      }
    }

    my_animator = new CRC_Animation(this);

  }

  @Override
  public void onMeasure(int widthSpec, int heightSpec) {

    super.onMeasure(widthSpec, heightSpec);

    int w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    if (w < h) h = w;
    else if (h < w) w = h;

    setMeasuredDimension(
        w + getPaddingLeft() + getPaddingRight(), h + getPaddingTop() + getPaddingBottom()
    );

  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    PreferenceManager.getDefaultSharedPreferences(my_owner)
            .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    my_drawer.draw(canvas);
    my_animator.set_step(my_drawer.preferred_step());
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    my_drawer.recalculate_positions();
  }

  // used to control the animation
  public void set_offset(float offset) {
      my_offset = offset;
  }

  // set default values for certain fields
  // prefs is needed in order to write new values
  // hour_12_24 is the last saved setting for 12 v. 24 hour clock
  // prefer_mono is the last saved setting for monochrome v. color
  // prefer_digi is the last saved setting for analog v. digital clock
  void setPrefs(
      SharedPreferences prefs,
      boolean hour_12_24, boolean show_seconds, boolean show_time,
      boolean saved_unit_orientation,
      int which_drawer, int bg_color, int line_color,
      int hour_color, int minute_color, int second_color
  ) {

    my_prefs = prefs;

    switch (which_drawer) {
      case 0: my_drawer = new CRC_View_Arcy(this); break;
      case 1: my_drawer = new CRC_View_Archy_Fade(this); break;
      case 2: my_drawer = new CRC_View_Ballsy(this); break;
      case 3: my_drawer = new CRC_View_Bubbly(this); break;
      case 4: my_drawer = new CRC_View_Linus(this); break;
      case 5: my_drawer = new CRC_View_Shady(this); break;
      case 6: my_drawer = new CRC_View_Vertie(this); break;
      default: my_drawer = new CRC_View_Arcy(this); break;
    }
    my_drawer.set_show_seconds(show_seconds);
    my_drawer.set_show_time(show_time);
    my_drawer.set_reverse_orientation(saved_unit_orientation);
    if (hour_12_24) {
      hour_modulus = 8;
      which_hour = HOUR_OF_DAY;
    } else {
      hour_modulus = 4;
      which_hour = HOUR;
    }
    my_drawer.recalculate_positions();
    my_drawer.set_time_textview(tv);

    my_drawer.set_line_color(line_color);
    my_drawer.set_bg_color(bg_color);
    my_drawer.set_hour_color(hour_color);
    my_drawer.set_minute_color(minute_color);
    my_drawer.set_second_color(second_color);

  }

  // we need to listen to certain buttons
  // see code for indication of which parameter corresponds to which button
  void setButtonsToListen(ToggleButton pb, Button dn, Button up, Spinner spin, EditText vb) {
    activeToggle = pb;
    incrementer = up;
    decrementer = dn;
    unitSelecter = spin;
    valueEditor = vb;
  }

  void set_time_textview(TextView ttv) {
    tv = ttv;
    if (my_drawer != null) my_drawer.set_time_textview(tv);
    if (tv != null) {
      SharedPreferences pref = my_owner.getPreferences(MODE_PRIVATE);
      boolean show_time = pref.getBoolean(my_owner.getString(R.string.saved_show_time), false);
      if (show_time) tv.setVisibility(VISIBLE);
      else tv.setVisibility(INVISIBLE);
    }
  }

  // handle pressing of toggle buttons
  // every received signal should call an invalidate() to redraw the clock
  // several will write new preferences data
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    if (buttonView == activeToggle) {

      if (my_animator.is_paused()) {

        my_animator.resume();
        incrementer.setVisibility(View.INVISIBLE);
        decrementer.setVisibility(View.INVISIBLE);
        unitSelecter.setVisibility(View.INVISIBLE);
        valueEditor.setVisibility(View.INVISIBLE);
        time_guide = CALENDAR;

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

    } // activeToggle

  }

  // show or hide buttons, enable dragging
  @Override
  public boolean onTouch(View v, MotionEvent event) {

    float x = event.getRawX();
    float y = event.getRawY();
    int my_loc [] = { 0, 0 };
    getLocationOnScreen(my_loc);
    float xmax = my_loc[0] + getWidth();
    float ymax = my_loc[1] + getHeight();
    Log.d(tag, "touch event at " + String.valueOf(x) + ", " + String.valueOf(y));
    Log.d(tag, "my positions are (" + String.valueOf(my_loc[0]) + ", " + String.valueOf(my_loc[1]) + "), (" + String.valueOf(xmax) + ", " + String.valueOf(ymax));

    if (event.getAction() == ACTION_UP) {
        if (my_drawer.getClass() == CRC_View_Ballsy.class) {
          my_drawer.notify_released(event);
          dragging = false;
          Log.d(tag, "released");
        }
    } else if (event.getAction() == ACTION_MOVE) {
      if (dragging) my_drawer.notify_dragged(event);
      Log.d(tag, "dragged ");
    } else if (event.getAction() == ACTION_DOWN) {
      if (activeToggle.getVisibility() == VISIBLE &&
          (x < my_loc[0] || y < my_loc[1] || x > xmax || y > ymax)
      ) {
          activeToggle.setVisibility(INVISIBLE);
          valueEditor.setVisibility(INVISIBLE);
          unitSelecter.setVisibility(INVISIBLE);
          incrementer.setVisibility(INVISIBLE);
          decrementer.setVisibility(INVISIBLE);
      } else {
        if (x > my_loc[0] && y > my_loc[1] && x < xmax && y < ymax) {
          if (
              my_drawer.getClass() == CRC_View_Ballsy.class &&
              activeToggle.getVisibility() == View.VISIBLE && activeToggle.isChecked()
          ) {
            my_drawer.notify_touched(event);
            dragging = true;
            time_guide = LEAVE_BE;
            Log.d(tag, "touched");
          }
        } else {
          if (activeToggle.getVisibility() == View.INVISIBLE) {
            activeToggle.setVisibility(VISIBLE);
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
    } else if (v == valueEditor)
      new_time_value = Integer.valueOf(valueEditor.getText().toString());
  }

  // handle selection of unit by user
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (parent == unitSelecter) {
      switch (position) {
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
    }
    valueEditor.selectAll();
  }

  // interface requires me to implement this, but nothing needs doing
  @Override
  public void onNothingSelected(AdapterView<?> parent) { }

  // handle when the user enters a new time
  @Override
  public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
    new_time_value = Integer.valueOf(textView.getText().toString());
    time_guide = Modification.NEW_VALUE;
    valueEditor.setEnabled(false);
    valueEditor.setEnabled(true);
    valueEditor.selectAll();
    my_offset = 0.0f;
    my_animator.resume();
    my_animator.pause();
    return true;
  }

  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

    boolean saved_hour = pref.getBoolean(my_owner.getString(R.string.saved_hour), false);
    boolean saved_seconds = pref.getBoolean(my_owner.getString(R.string.saved_show_seconds), false);
    boolean saved_time = pref.getBoolean(my_owner.getString(R.string.saved_show_time), false);
    boolean saved_reverse_orientation = pref.getBoolean(my_owner.getString(R.string.saved_reverse_orientation), false);
    int saved_drawer = Integer.valueOf(pref.getString(my_owner.getString(R.string.saved_drawer), "0"));
    int saved_hour_color = pref.getInt(my_owner.getString(R.string.saved_hour_color), BLUE);
    int saved_minute_color = pref.getInt(my_owner.getString(R.string.saved_minute_color), RED);
    int saved_second_color = pref.getInt(my_owner.getString(R.string.saved_second_color), GREEN);
    int saved_bg_color = pref.getInt(my_owner.getString(R.string.saved_bg_color), GRAY);
    int saved_line_color = pref.getInt(my_owner.getString(R.string.saved_line_color), WHITE);
    SharedPreferences.Editor editor = my_owner.getPreferences(MODE_PRIVATE).edit();
    editor.putBoolean(my_owner.getString(R.string.saved_hour), saved_hour);
    editor.putBoolean(my_owner.getString(R.string.saved_show_seconds), saved_seconds);
    editor.putBoolean(my_owner.getString(R.string.saved_show_time), saved_time);
    editor.putBoolean(my_owner.getString(R.string.saved_reverse_orientation), saved_reverse_orientation);
    editor.putInt(my_owner.getString(R.string.saved_drawer), saved_drawer);
    editor.putInt(my_owner.getString(R.string.saved_hour_color), saved_hour_color);
    editor.putInt(my_owner.getString(R.string.saved_minute_color), saved_minute_color);
    editor.putInt(my_owner.getString(R.string.saved_second_color), saved_second_color);
    editor.putInt(my_owner.getString(R.string.saved_bg_color), saved_bg_color);
    editor.putInt(my_owner.getString(R.string.saved_line_color), saved_line_color);
    editor.apply();

    setPrefs(
            pref,
            saved_hour, saved_seconds, saved_time, saved_reverse_orientation,
            saved_drawer, saved_bg_color, saved_line_color,
            saved_hour_color, saved_minute_color, saved_second_color
    );
    if (saved_time) {
      if (tv != null) tv.setVisibility(VISIBLE);
    } else {
      if (tv != null) tv.setVisibility(INVISIBLE);
    }

  }


  protected float my_offset; // how far along the animation is (should range from 0 to 1)

  // the Runnable that controls animation
  protected CRC_Animation my_animator;

  // activity controlling this clock
  protected Chinese_Remainder my_owner;

  // UI elements
  protected ToggleButton hourButton, monochromeButton, activeToggle;
  protected Button incrementer, decrementer, helpButton;
  protected Spinner unitSelecter, drawSelecter;
  protected EditText valueEditor;
  protected TextView tv;

  // various useful constant strings
  protected static final String tag = "CRC_View"; // debugging

  // preferences file
  protected SharedPreferences my_prefs;

  // drawer
  protected Clock_Drawer my_drawer;
  protected boolean analog;

  // useful constants to make code more legible
  enum Units { HOURS, MINUTES, SECONDS }
  protected Units which_unit_to_modify;
  enum Modification { LEAVE_BE, CALENDAR, INCREMENT, DECREMENT, NEW_VALUE }
  protected Modification time_guide;
  protected int new_time_value;

  // whether we are calculating 12- or 24-hour time
  protected int which_hour;
  protected int hour_modulus;
  // remember the previous time
  protected int last_h, last_m, last_s;

  final protected String [] hour12_strings = {
          "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
  };
  final protected String [] hour24_strings = {
          "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
          "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
  };
  final protected String [] minsec_strings = {
          "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
          "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
          "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
          "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
          "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
          "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
  };

    protected boolean dragging = false;
}
