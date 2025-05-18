package name.cantanima.chineseremainderclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

/**
 *
 * The main Activity for the clock!
 * This is the main Activity that controls the clock.
 *    However, very little is done here; the meat of the work is taken care of in CRC_View,
 *    and the meat of the layout is taken care of by the usual Android layout files
 *    (see res/layout and res/layout-land).
 *    Here we only take care of the minimum that has to be done: welcome screen, menu response,
 *    and setting up some communication between this Activity and the CRC_View.
 *    Note that the SharedPreferences are not necessarily the same in the various places they
 *    are read (apparently the "Shared"Preferences aren't actually "shared").
 * <p>
 *    To implement a new design, extend Clock_Drawer, add information to the strings.xml file,
 *    and modify CRC_View accordingly (look for where drawers are assigned, etc.).
 *
 */
public class Chinese_Remainder
    extends AppCompatActivity
{

  /**
   *
   * Aside from what the superclass does, this checks the version and opens a welcome dialog
   *    if we are running for the first time, OR if the version has changed.
   * @param savedInstanceState necessary for this
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chinese__remainder);
    FragmentManager fm = getSupportFragmentManager();
    if (savedInstanceState == null) {
        fm.beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();
    }

    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    boolean first_run = pref.getBoolean(getString(R.string.first_run), true);
    String version_string = getString(R.string.current_version);
    if (first_run || !pref.contains(getString(R.string.version)) ||
        !pref.getString(getString(R.string.version), version_string).equals(version_string)) {

      new AlertDialog.Builder(this).setTitle(
                                        getString(R.string.welcome_string)
                                   )
                                   .setMessage(getString(R.string.welcome_dialog_text))
                                   .setIcon(R.drawable.ic_action_info)
                                   .setPositiveButton(
                                       getString(R.string.proceed_string),
                                           (dialog, which) -> dialog.dismiss()
                                   ).show();
      SharedPreferences.Editor edit = pref.edit();
      edit.putBoolean(getString(R.string.first_run), false);
      edit.putString(getString(R.string.version), getString(R.string.current_version));
      edit.apply();
    }

    setTitle(getString(R.string.app_menu_title));

    Log.i(tag, "successfully initialized Chinese_Remainder");

  }


  /**
   * Sets up the options menu.
   * @param menu menu to inflate into
   * @return always returns true
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_chinese__remainder, menu);
    return true;
  }

  /**
   * Takes care of the menu items.
   * @param item item that has been selected
   * @return true if one of our items; otherwise, whatever the superclass returns when we pass the call
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    Log.i(tag, String.format("selected action %d", id));

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      // starts the settings panel
      Intent i = new Intent(this, CRC_Prefs_Activity.class);
      startActivity(i);
      return true;
    } else if (id == R.id.information) {
      // starts the info page
      Intent i = new Intent(this, HelpActivity.class);
      startActivity(i);
      return true;
    } else if (id == R.id.number34) {
      // starts a quiz!
      findViewById(R.id.crc_view).setVisibility(View.INVISIBLE);
      new Quiz_abTime(this, 3, 4);
    } else if (id == R.id.number35) {
      // starts a quiz!
      findViewById(R.id.crc_view).setVisibility(View.INVISIBLE);
      new Quiz_abTime(this, 3, 5);
    } else if (id == R.id.number45) {
      // starts a quiz!
      findViewById(R.id.crc_view).setVisibility(View.INVISIBLE);
      new Quiz_abTime(this, 4, 5);
    } else if (id == R.id.number345) {
      // starts a quiz!
      findViewById(R.id.crc_view).setVisibility(View.INVISIBLE);
      new Quiz_abcTime(this, 3, 4, 5);
    } else if (id == R.id.clockmaster) {
      new Quiz_WhatTimeIsIt(this);
    } else if (id == R.id.automanual) {
      CRC_View view = findViewById(R.id.crc_view);
      view.switchMode(item);
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A placeholder fragment containing a simple view.
   * This was created automatically by Android Studio and is apparently how Google wants
   *    everything done henceforth and hereafter. Aside from the default,
   *    this reads from the preferences file and assigns values to the CRC_View.
   */
  public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() { }

    /**
     * Aside from the default behavior, we read preferences, and pass information to the CRC_View.
     * @param inflater layout to inflate
     * @param container container in which to inflate it (I guess)
     * @param savedInstanceState past data, if saved
     * @return must return rootView; Android will pout big-time if you don't
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      RelativeLayout rootView = (RelativeLayout) inflater.inflate(
              R.layout.fragment_chinese__remainder, container, false
      );

      // initialize root view for touch
      CRC_View crc_view = rootView.findViewById(R.id.crc_view);
      crc_view.setOnTouchListener(crc_view);

      // Read preferences, assign to crc_view
      SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);

      // various boolean options
      boolean saved_hour = (prefs.contains(getString(R.string.saved_hour))) &&
              prefs.getBoolean(getString(R.string.saved_hour), false);
      boolean saved_seconds = (prefs.contains(getString(R.string.saved_show_seconds))) &&
              prefs.getBoolean(getString(R.string.saved_show_seconds), false);
      boolean saved_time = (prefs.contains(getString(R.string.saved_show_time))) &&
              prefs.getBoolean(getString(R.string.saved_show_time), false);
      boolean saved_unit_orientation = prefs.contains(getString(R.string.saved_reverse_orientation)) &&
              prefs.getBoolean(getString(R.string.saved_reverse_orientation), false);

      // read the preferred drawer/design
      final int default_drawer = 3;
      int saved_drawer = (prefs.contains(getString(R.string.saved_drawer)))
          ? Integer.parseInt(prefs.getString(getString(R.string.saved_drawer), String.valueOf(default_drawer)))
          : default_drawer;

      // read the version and if there isn't one (because this is a first run) save the preferences
      String version_string = getString(R.string.current_version);
      if (
              !prefs.contains(getString(R.string.version)) ||
              !prefs.getString(getString(R.string.version), version_string).equals(version_string)
      ) {
          SharedPreferences.Editor editor = prefs.edit();
          editor.clear();
          editor.putString(getString(R.string.version), version_string);
          editor.putBoolean(getString(R.string.saved_hour), saved_hour);
          editor.putBoolean(getString(R.string.saved_show_seconds), saved_seconds);
          editor.putBoolean(getString(R.string.saved_show_time), saved_time);
          editor.putBoolean(getString(R.string.saved_reverse_orientation), saved_unit_orientation);
          editor.putString(getString(R.string.saved_drawer), String.valueOf(saved_drawer));
          editor.apply();
      }

      // setup user interface elements: all should start off invisible,
      // and should have crc_view as an appropriate listener
      // (since that's where I take care of the interaction)

      //Button upButton = rootView.findViewById(R.id.decrementButton);
      ImageButton upButton = rootView.findViewById(R.id.hour_up);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);
      upButton = rootView.findViewById(R.id.hour_down);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);
      upButton = rootView.findViewById(R.id.minute_up);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);
      upButton = rootView.findViewById(R.id.minute_down);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);
      upButton = rootView.findViewById(R.id.second_up);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);
      upButton = rootView.findViewById(R.id.second_down);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);

      EditText timeEditor = rootView.findViewById(R.id.hour_edit);
      timeEditor.setOnEditorActionListener(crc_view);
      timeEditor.setVisibility(View.INVISIBLE);
      timeEditor.setSelectAllOnFocus(true);
      timeEditor.setOnClickListener(crc_view);
      timeEditor = rootView.findViewById(R.id.minute_edit);
      timeEditor.setOnEditorActionListener(crc_view);
      timeEditor.setVisibility(View.INVISIBLE);
      timeEditor.setSelectAllOnFocus(true);
      timeEditor.setOnClickListener(crc_view);
      timeEditor = rootView.findViewById(R.id.second_edit);
      timeEditor.setOnEditorActionListener(crc_view);
      timeEditor.setVisibility(View.INVISIBLE);
      timeEditor.setSelectAllOnFocus(true);
      timeEditor.setOnClickListener(crc_view);

      LinearLayout button_row = rootView.findViewById(R.id.manual_buttons);
      button_row.setVisibility(View.INVISIBLE);

      // make crc_view listen to things that have to be shown/hidden and such
      crc_view.setButtonsToListen(rootView);

      // crc_view also needs to show/hide the time, and for some reason
      // I separated that from the buttons
      TextView tv = rootView.findViewById(R.id.time_display);
      tv.setVisibility(View.INVISIBLE);
      crc_view.set_time_textview(tv);

      return rootView;
    }

  }

  /**
   * Called to process touch screen events.  You can override this to
   * intercept all touch screen events before they are dispatched to the
   * window.  Be sure to call this implementation for touch screen events
   * that should be handled normally.
   *
   * @param ev The touch screen event.
   * @return boolean Return true if this event was consumed.
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean consumed = super.dispatchTouchEvent(ev);
    if (!consumed) {
      CRC_View crc_view = findViewById(R.id.crc_view);
      crc_view.onTouch(null, ev);
    }
    return true;
  }

  // used when debugging w/Log.d()
   private static final String tag = "Chinese Remainder";

}
