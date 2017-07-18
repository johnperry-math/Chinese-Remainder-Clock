package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


public class Chinese_Remainder
    extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chinese__remainder);
    FragmentManager fm = getFragmentManager();
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

      new AlertDialog.Builder(this).setTitle(getString(R.string.welcome_string))
                                   .setMessage(getString(R.string.welcome_dialog_text))
                                   .setIcon(R.drawable.ic_action_info)
                                   .setPositiveButton(
                                       getString(R.string.proceed_string),
                                       new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                         }
                                       }
                                   ).show();
      SharedPreferences.Editor edit = pref.edit();
      edit.putBoolean(getString(R.string.first_run), false);
      edit.putString(getString(R.string.version), getString(R.string.current_version));
      edit.apply();
    }

    setTitle(getString(R.string.app_menu_title));

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_chinese__remainder, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Intent i = new Intent(this, CRC_Prefs_Activity.class);
      startActivity(i);
      return true;
    } else if (id == R.id.information) {
      Intent i = new Intent(this, HelpActivity.class);
      startActivity(i);
      return true;
    } else if (id == R.id.quiz) {
      CRC_View crc_view = (CRC_View) findViewById(R.id.crc_view);
      crc_view.start_quiz();
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() { }

    @Override
    public View onCreateView(
          LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState
    ) {

      RelativeLayout rootView = (RelativeLayout) inflater.inflate(
              R.layout.fragment_chinese__remainder, container, false
      );

      // initialize root view for touch
      CRC_View crc_view = (CRC_View) rootView.findViewById(R.id.crc_view);
      rootView.setOnTouchListener(crc_view);

      // Read preferences, assign to crc_view
      SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
      boolean saved_hour = (prefs.contains(getString(R.string.saved_hour))) &&
              prefs.getBoolean(getString(R.string.saved_hour), false);
      boolean saved_seconds = (prefs.contains(getString(R.string.saved_show_seconds))) &&
              prefs.getBoolean(getString(R.string.saved_show_seconds), false);
      boolean saved_time = (prefs.contains(getString(R.string.saved_show_time))) &&
              prefs.getBoolean(getString(R.string.saved_show_time), false);
      boolean saved_unit_orientation = prefs.contains(getString(R.string.saved_reverse_orientation)) &&
              prefs.getBoolean(getString(R.string.saved_reverse_orientation), false);

      final int default_drawer = 1;
      int saved_drawer = (prefs.contains(getString(R.string.saved_drawer))) ?
              prefs.getInt(getString(R.string.saved_drawer), default_drawer) : default_drawer;
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
          editor.putInt(getString(R.string.saved_drawer), saved_drawer);
          editor.apply();
      }

      // setup user interface elements: all should start off invisible,
      // and should have crc_view as an appropriate listener
      // (since that's where I take care of the interaction)

      ToggleButton pb = (ToggleButton) rootView.findViewById(R.id.activeToggle);
      pb.setOnCheckedChangeListener(crc_view);
      pb.setVisibility(View.INVISIBLE);

      Button upButton = (Button) rootView.findViewById(R.id.decrementButton);
      upButton.setOnClickListener(crc_view);
      upButton.setVisibility(View.INVISIBLE);

      Button dnButton = (Button) rootView.findViewById(R.id.incrementButton);
      dnButton.setOnClickListener(crc_view);
      dnButton.setVisibility(View.INVISIBLE);

      Spinner spinner = (Spinner) rootView.findViewById(R.id.selectUnit);
      spinner.setOnItemSelectedListener(crc_view);
      spinner.setVisibility(View.INVISIBLE);

      EditText timeEditor = (EditText) rootView.findViewById(R.id.timeEditor);
      timeEditor.setOnEditorActionListener(crc_view);
      timeEditor.setVisibility(View.INVISIBLE);
      timeEditor.setSelectAllOnFocus(true);
      timeEditor.setOnClickListener(crc_view);

      crc_view.setButtonsToListen(pb, upButton, dnButton, spinner, timeEditor);

      TextView tv = (TextView) rootView.findViewById(R.id.time_display);
      tv.setVisibility(View.INVISIBLE);
      crc_view.set_time_textview(tv);

      return rootView;
    }

  }

  // used when debugging w/Log.d()
  private static final String tag = "Chinese Remainer";

}
