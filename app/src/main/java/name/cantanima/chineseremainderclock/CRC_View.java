package name.cantanima.chineseremainderclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
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

import static android.view.MotionEvent.ACTION_DOWN;
import static java.util.Calendar.HOUR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;

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
        if (!isInEditMode()) {
            my_owner = (Chinese_Remainder) context;
        }

        my_drawer = new CRC_View_Ballsy(this);
        setOnTouchListener(this);

        hour_modulus = 4;
        which_hour = HOUR;
        time_guide = CALENDAR;

        my_animator = new CRC_Animation(this);

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
            boolean hour_12_24, boolean prefer_mono, boolean show_seconds, boolean show_time,
            int which_drawer
    ) {

        my_prefs = prefs;

        switch (which_drawer) {
            case 0: my_drawer = new CRC_View_Arcy(this); break;
            case 1: my_drawer = new CRC_View_Ballsy(this); break;
            case 2: my_drawer = new CRC_View_Bubbly(this); break;
            case 3: my_drawer = new CRC_View_Linus(this); break;
            case 4: my_drawer = new CRC_View_Shady(this); break;
            case 5: my_drawer = new CRC_View_Vertie(this); break;
        }
        my_drawer.recalculate_positions();
        my_drawer.set_color(!prefer_mono);
        my_drawer.set_show_seconds(show_seconds);
        my_drawer.set_show_time(show_time);
        set_color_or_monochrome();
        if (hour_12_24) hour_modulus = 8;
        else hour_modulus = 4;

    }

    // we need to listen to certain buttons
    // see code for indication of which parameter corresponds to which button
    void setButtonsToListen(
            ToggleButton hb, ToggleButton mb, Spinner db, ToggleButton pb,
            Button dn, Button up, Spinner spin, EditText vb, Button ib
            ) {
        hourButton = hb;
        monochromeButton = mb;
        drawSelecter = db;
        activeToggle = pb;
        incrementer = up;
        decrementer = dn;
        unitSelecter = spin;
        valueEditor = vb;
        helpButton = ib;
    }

    public void set_color_or_monochrome() {
        if (my_drawer.is_color()) {
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putBoolean(my_owner.getString(R.string.saved_color), false);
            edit.apply();
        } else {
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putBoolean(my_owner.getString(R.string.saved_color), true);
            edit.apply();
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
                edit.apply();
            }
            else {
                hour_modulus = 4;
                which_hour = HOUR;
                SharedPreferences.Editor edit = my_prefs.edit();
                edit.putBoolean(my_owner.getString(R.string.saved_hour), false);
                edit.apply();
            }
            invalidate();
        } else if (buttonView == monochromeButton) {
            my_drawer.toggle_color();
            set_color_or_monochrome();
            invalidate();
        } else if (buttonView == activeToggle) {
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
        }

    }

    // show or hide the buttons
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == ACTION_DOWN) {
            if (hourButton.getVisibility() == VISIBLE) {
                hourButton.setVisibility(INVISIBLE);
                monochromeButton.setVisibility(INVISIBLE);
                drawSelecter.setVisibility(INVISIBLE);
                activeToggle.setVisibility(INVISIBLE);
                valueEditor.setVisibility(INVISIBLE);
                unitSelecter.setVisibility(INVISIBLE);
                incrementer.setVisibility(INVISIBLE);
                decrementer.setVisibility(INVISIBLE);
                helpButton.setVisibility(INVISIBLE);
            } else {
                hourButton.setVisibility(VISIBLE);
                monochromeButton.setVisibility(VISIBLE);
                drawSelecter.setVisibility(VISIBLE);
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
            new_time_value = Integer.valueOf(valueEditor.getText().toString());
    }

    // handle seleciton of unit by user
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
        } else if (parent == drawSelecter) {
            boolean show_time = my_drawer.get_show_time();
            boolean show_seconds = my_drawer.get_show_seconds();
            SharedPreferences.Editor edit = my_prefs.edit();
            edit.putInt(my_owner.getString(R.string.saved_drawer), position);
            edit.apply();
            switch (position) { // these need to line up with the values in drawString0s.xml !!!
                case 0:
                    my_drawer = new CRC_View_Arcy(this);
                    break;
                case 1:
                    my_drawer = new CRC_View_Ballsy(this);
                    break;
                case 2:
                    my_drawer = new CRC_View_Bubbly(this);
                    break;
                case 3:
                    my_drawer = new CRC_View_Linus(this);
                    break;
                case 4:
                    my_drawer = new CRC_View_Shady(this);
                    break;
                case 5:
                    my_drawer = new CRC_View_Vertie(this);
                    break;
            }
            my_drawer.set_show_time(show_time);
            my_drawer.set_show_seconds(show_seconds);
            my_drawer.set_color(!monochromeButton.isChecked());
            my_drawer.recalculate_positions();
            invalidate();
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
}
