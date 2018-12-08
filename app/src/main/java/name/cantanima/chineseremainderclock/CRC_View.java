package name.cantanima.chineseremainderclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_TIME;

/*
 * Created by cantanima on 4/22/17.
 */

/**
 *
 * Custom View for a Chinese Remainder Clock
 * In order to facilitate modularity without replacing the view every time,
 *    drawing the view is subcontracted to the Clock_Drawer class. Now, that is an abstract class,
 *    so you'll need to look at its numerous descendants to see the details of how and why
 *    things are drawn the way they are. Meanwhile, this view coordinates the listening for
 *    pretty much every user interface action that isn't either a system action (e.g., dismissal,
 *    moving between activities, or menu response -- for menu response see Chinese_Remainder).
 *
 */
public class CRC_View
    extends View
    implements OnTouchListener, OnCheckedChangeListener, OnClickListener,
               OnItemSelectedListener, OnEditorActionListener,
               SharedPreferences.OnSharedPreferenceChangeListener
{

  /**
   * Constructs the CRC_View (obviously) and sets up various options.
   */
  public CRC_View(Context context, AttributeSet attrs) {

    super(context, attrs);

    // defaults
    hour_modulus = 4;
    which_hour = HOUR;
    time_guide = CALENDAR;

    // this test is in here so that the layout designer will show a basic CRC_View; don't remove!
    if (isInEditMode()) {

      my_drawer = new CRC_View_Ringy(this);

    } else {

      // remember Activity; needed for preferences if nothing else
      my_owner = context;


      // read and set up preferences
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(my_owner);
      boolean saved_hour = prefs.getBoolean(my_owner.getString(R.string.saved_hour), false);
      boolean saved_seconds = prefs.getBoolean(my_owner.getString(R.string.saved_show_seconds), false);
      boolean saved_time = prefs.getBoolean(my_owner.getString(R.string.saved_show_time), false);
      boolean saved_unit_orientation = prefs.getBoolean(my_owner.getString(R.string.saved_reverse_orientation), false);
      int saved_drawer;
      try {
        saved_drawer = Integer.valueOf(prefs.getString(my_owner.getString(R.string.saved_drawer), String.valueOf(3)));
      } catch (java.lang.ClassCastException e) {
        saved_drawer = prefs.getInt(my_owner.getString(R.string.saved_drawer), 0);
      }
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
      PreferenceManager.getDefaultSharedPreferences(my_owner)
          .registerOnSharedPreferenceChangeListener(this);

      // handle own touch events
      setOnTouchListener(this);

      // display the time in conventional format, if that was desired
      if (saved_time) {
        if (tv != null) tv.setVisibility(VISIBLE);
      } else {
        if (tv != null) tv.setVisibility(INVISIBLE);
      }
    }

    // set up an animation
    my_animator = new CRC_Animation(this);

  }

  /**
   * Determines the height and width of a CRC_View: this forces it to be square, and fits it
   * into the allowed space given by widthSpec and heightSpec.
   * @param widthSpec allowed width
   * @param heightSpec allowed height
   */
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

  /**
   * Need to detach preferences when closing up the view.
   */
  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    PreferenceManager.getDefaultSharedPreferences(my_owner)
            .unregisterOnSharedPreferenceChangeListener(this);
  }

  /**
   *
   * Typically, this would be a very long and difficult-to-decipher method.
   * I have opted to subcontract this to six (or more) long and difficult-to-decipher classes.
   * @see Clock_Drawer
   * @param canvas where to draw it
   */
  @Override
  protected void onDraw(Canvas canvas) {
    my_drawer.draw(canvas);
    my_animator.set_step(my_drawer.preferred_step());
  }

  /**
   * If the size changes for whatever reason (and I can't think of a reason it would, aside from
   * startup or orientation change) this forces the drawers to recalculate drawing information.
   * @param w width
   * @param h height
   * @param oldw old width
   * @param oldh old height
   */
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    my_drawer.recalculate_positions();
  }

  /**
   *
   * Used to control the animation.
   * @see CRC_Animation
   *
   */
  public void set_offset(float offset) {
      my_offset = offset;
  }

  /**
   *
   * Sets the default values for certain fields
   *
   * @param prefs no longer needed; we should remove this
   * @param hour_12_24 whether we are using a 12-short_hand or a 24-short_hand clock
   * @param show_seconds whether to show the seconds
   * @param show_time whether to display the time in conventional format
   * @param saved_unit_orientation whether to keep put minutes inside/left of hours
   * @param which_drawer which @link{Clock_Drawer} to use
   * @param bg_color clock's background color
   * @param line_color clock's line color (when appropriate)
   * @param hour_color color used to draw objects that represent the current short_hand
   * @param minute_color color used to draw objects that represent the current minute
   * @param second_color color used to draw objects that represent the current second
   */
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
      case 1: my_drawer = new CRC_View_Bubbly(this); break;
      case 2: my_drawer = new CRC_View_Linus(this); break;
      case 3: my_drawer = new CRC_View_Ringy(this); break;
      case 4: my_drawer = new CRC_View_Shady(this); break;
      case 5: my_drawer = new CRC_View_Vertie(this); break;
      default: my_drawer = new CRC_View_Ringy(this); break;
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

  /**
   * Buttons we need to listen to in order to implement manual mode.
   * //@param pb "pause button", currently labeled "Manual" because that's how we use it
   * @param dn decrement the time (Manual mode)
   * @param up increment the time (Manual mode)
   * @param spin which time unit to modify (Manual mode)
   * @param vb specify a specific value for the time unit (Manual mode)
   */
  //
  // see code for indication of which parameter corresponds to which button
  void setButtonsToListen(
      /*Switch pb, */Button dn, Button up, Spinner spin, EditText vb, LinearLayout ll
  ) {
    incrementer = up;
    decrementer = dn;
    unitSelecter = spin;
    valueEditor = vb;
    manual_button_layout = ll;
  }

  /**
   * Assign a TextView to show the time.
   * @param ttv TextView to show/hide/update
   */
  void set_time_textview(TextView ttv) {
    tv = ttv;
    if (my_drawer != null) my_drawer.set_time_textview(tv);
    if (tv != null) {
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(my_owner);
      boolean show_time = pref.getBoolean(my_owner.getString(R.string.saved_show_time), false);
      if (show_time) tv.setVisibility(VISIBLE);
      else tv.setVisibility(INVISIBLE);
    }
  }

  /**
   * Handle pressing of toggle buttons.
   * Every received signal should call an invalidate() to redraw the clock.
   * Several will write new preferences data.
   * @param buttonView the button that was pressed
   * @param isChecked whether value is on or off
   */
  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    /* This is not used. */

  }

  public void switchMode(MenuItem item) {

    if (currentMode == Mode.MANUAL) {

      item.setTitle("Manual");
      currentMode = Mode.AUTOMATIC;
      // animation is paused, let's resume it
      my_animator.resume();
      // outside & showing buttons; hide them
      valueEditor.setVisibility(INVISIBLE);
      unitSelecter.setVisibility(INVISIBLE);
      incrementer.setVisibility(INVISIBLE);
      decrementer.setVisibility(INVISIBLE);
      manual_button_layout.setVisibility(INVISIBLE);
      // when drawing, we need to check the calendar for the time
      time_guide = CALENDAR;

    } else {

      item.setTitle("Automatic");
      currentMode = Mode.MANUAL;
      // animation is current, let's pause it
      my_animator.pause();
      // set up manual interface
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
      valueEditor.selectAll(); // (this doesn't actually seem to work)
      // show manual buttons
      incrementer.setVisibility(VISIBLE);
      decrementer.setVisibility(VISIBLE);
      unitSelecter.setVisibility(VISIBLE);
      valueEditor.setVisibility(VISIBLE);
      manual_button_layout.setVisibility(VISIBLE);
      valueEditor.selectAll(); // (this might work)

    }

  }

  /**
   *
   * Show or hide buttons, enable dragging.
   * @param v the View that was touched (might not be this)
   * @param event the MotionEvent that occurred
   *
   */
  @Override
  public boolean onTouch(View v, MotionEvent event) {

    // let's get some information on where the touch occurred
    float x = event.getRawX();
    float y = event.getRawY();
    int my_loc [] = { 0, 0 };
    getLocationOnScreen(my_loc);
    float xmax = my_loc[0] + getWidth();
    float ymax = my_loc[1] + getHeight();

    if (event.getAction() == ACTION_UP) { // released

      // add drawer here if it's enabled for touch and drag
      if (my_drawer.getClass() == CRC_View_Ringy.class) {
        my_drawer.notify_released(event);
        dragging = false;
      }

    } else if (event.getAction() == ACTION_MOVE) { // moved/dragged

      if (dragging) my_drawer.notify_dragged(event);

    } else if (event.getAction() == ACTION_DOWN) { // pressed down

      // if touched inside view, allow dragging for appropriate class
      if (x > my_loc[0] && y > my_loc[1] && x < xmax && y < ymax) {

        // inside; set up for dragging
        // add drawer here if it's enabled for touch and drag
        if (
            my_drawer.getClass() == CRC_View_Ringy.class &&
            currentMode == Mode.MANUAL
        ) {

          my_drawer.notify_touched(event);
          dragging = true;
          time_guide = LEAVE_BE;

        }

      }

    }

    return true;
  }

  /**
   * Call this view's OnClickListener, if it is defined.  Performs all normal
   * actions associated with clicking: reporting accessibility event, playing
   * a sound, etc.
   *
   * @return True there was an assigned OnClickListener that was called, false
   * otherwise is returned.
   */
  @Override
  public boolean performClick() {
    return super.performClick();
  }

  /**
   * handle buttons (+1, -1, info) and clicking in the text editor
   * @param v which view was clocked
   */
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

  /**
   * Handle selection of unit by user.
   * @param parent should be the ListAdapter of units
   * @param view should be the Spinner
   * @param position which position in the list was chosen
   * @param id I dunno; not used
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    // only unitSelecter should be talking to us, but you never know
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

  /** Interface requires me to implement this, but nothing needs doing. */
  @Override
  public void onNothingSelected(AdapterView<?> parent) { }

  /** Handle when the user enters a new time. */
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

  /**
   * Handle a change to the preferences files. Calls setPrefs().
   * @param pref preference file that changed
   * @param key I guess the key that changed, but I don't use it.
   */
  public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

    // first read from the given file
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

    // the preference file may actual be that for the PreferenceActivity,
    // in which case we need to save to the Activity's preference file
    // (figuring out this boneheaded design took place on a bad, bad day)
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(my_owner).edit();
    editor.putBoolean(my_owner.getString(R.string.saved_hour), saved_hour);
    editor.putBoolean(my_owner.getString(R.string.saved_show_seconds), saved_seconds);
    editor.putBoolean(my_owner.getString(R.string.saved_show_time), saved_time);
    editor.putBoolean(my_owner.getString(R.string.saved_reverse_orientation), saved_reverse_orientation);
    editor.putString(my_owner.getString(R.string.saved_drawer), String.valueOf(saved_drawer));
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

  /**
   * Pauses the animation of the clock.
   * Does not change any related flags; it ONLY pauses the animation.
   */
  public void pause_animation() { my_animator.pause(); }

  /**
   * Restarts (if not already started) the animation and sets the time guide to CALENDAR.
   */
  public void restart_animation_by_calendar() {
    time_guide = CALENDAR;
    my_animator.resume();
  }

  /**
   * Moves the clock to h hours, m minutes, then pauses the animation.
   * If the animation is not already paused, it will be after this method completes.
   * @param h  hours on the clock
   * @param m  minutes on the clock
   */
  public void move_time_to(int h, int m) {
    new_hour_value = h;
    new_minute_value = m;
    time_guide = NEW_TIME;
    my_animator.resume();
    my_animator.pause();
  }

  public Mode getCurrentMode() { return currentMode; }

  /** how far along the animation is (should range from 0 to 1) */
  protected float my_offset;

  /** a Runnable that controls animation */
  protected CRC_Animation my_animator;

  /** activity controlling this clock */
  protected Context my_owner;

  /** enable or disable Manual mode */
  public enum Mode { AUTOMATIC, MANUAL }
  private Mode currentMode = Mode.AUTOMATIC;

  /** increment or decrement the time (Manual mode only) */
  protected Button incrementer, decrementer;
  /** select which unit to modify (Manual mode only) */
  protected Spinner unitSelecter;
  /** layout that contains the manual buttons */
  protected LinearLayout manual_button_layout;

  /**
   *  enter the new value for the time unit specified by unitSelecter (Manual mode only)
   *  @see #unitSelecter
   */
  protected EditText valueEditor;
  /**
   * where to print the time
   */
  protected TextView tv;

  /** useful for Log.d when debugging */
  protected static final String tag = "CRC_View";

  /** preferences file */
  protected SharedPreferences my_prefs;

  /** which clock design to use */
  protected Clock_Drawer my_drawer;

  /** which unit is being modified (Manual mode only) */
  enum Units { HOURS, MINUTES, SECONDS }
  /** which unit is being modified (Manual mode only) */
  protected Units which_unit_to_modify;

  /** How to determine the time when re-drawing the clock. */
  enum Modification {
    /** do not change the time */
    LEAVE_BE,
    /** read the time from the calendar */
    CALENDAR,
    /** increment the specified time unit by 1 */
    INCREMENT,
    /** decrement the specified time unit by 1 */
    DECREMENT,
    /**
     *  a new value for the specified unit only; read from valueEditor
     *  @see Units
     */
    NEW_VALUE,
    /** a new value for all units of time; read from short_hand, minute, second */
    NEW_TIME
  }
  /** how time is being modified */
  protected Modification time_guide;
  /** new value for the time when using {@link Modification#NEW_VALUE} */
  protected int new_time_value;

  /** whether we are calculating 12- or 24-short_hand time */
  protected int which_hour;
  /** which short_hand modulus (for 12- or 24-short_hand time) */
  protected int hour_modulus;

  /** used to store previous time (useful for animation) */
  protected int last_h, last_m, last_s;

  /** hard-coded strings to speed up drawing of time */
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

  /** is user dragging on the clock? */
  protected boolean dragging = false;

  /** quiz data: new time to show */
  int new_hour_value, new_minute_value;
}
