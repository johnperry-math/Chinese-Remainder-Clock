package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
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
import android.widget.ToggleButton;


public class Chinese_Remainder
    extends Activity
    implements SharedPreferences.OnSharedPreferenceChangeListener
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
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        boolean saved_hour = pref.getBoolean(getString(R.string.saved_hour), false);
        boolean saved_color = pref.getBoolean(getString(R.string.saved_color), false);
        boolean saved_seconds = pref.getBoolean(getString(R.string.saved_show_seconds), false);
        boolean saved_time = pref.getBoolean(getString(R.string.saved_show_time), false);
        int saved_drawer = Integer.valueOf(pref.getString(getString(R.string.saved_drawer), "0"));
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean(getString(R.string.saved_hour), saved_hour);
        editor.putBoolean(getString(R.string.saved_color), saved_color);
        editor.putBoolean(getString(R.string.saved_show_seconds), saved_seconds);
        editor.putBoolean(getString(R.string.saved_show_time), saved_time);
        editor.putInt(getString(R.string.saved_drawer), saved_drawer);
        editor.apply();

        CRC_View crc_view = (CRC_View) findViewById(R.id.crc_view);
        crc_view.setPrefs(pref, saved_hour, saved_color, saved_seconds, saved_time, saved_drawer);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            RelativeLayout rootView = (RelativeLayout) inflater.inflate(
                    R.layout.fragment_chinese__remainder, container, false
            );

            // initialize root view for touch
            CRC_View crc_view = (CRC_View) rootView.findViewById(R.id.crc_view);
            rootView.setOnTouchListener(crc_view);

            // Read preferences, assign to crc_view
            SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
            boolean default_hour = false;
            boolean saved_hour = (prefs.contains(getString(R.string.saved_hour))) ?
                    prefs.getBoolean(getString(R.string.saved_hour), default_hour) : default_hour;
            boolean default_color = false;
            boolean saved_color = (prefs.contains(getString(R.string.saved_color))) ?
                    prefs.getBoolean(getString(R.string.saved_color), default_color) : default_color;
            boolean default_seconds = false;
            boolean saved_seconds = (prefs.contains(getString(R.string.saved_show_seconds))) ?
                    prefs.getBoolean(getString(R.string.saved_show_seconds), default_seconds) : default_seconds;
            boolean default_time = false;
            boolean saved_time = (prefs.contains(getString(R.string.saved_show_time))) ?
                    prefs.getBoolean(getString(R.string.saved_show_time), default_time) : default_time;
            
            int default_drawer = 1;
            int saved_drawer = (prefs.contains(getString(R.string.saved_drawer))) ?
                    prefs.getInt(getString(R.string.saved_drawer), default_drawer) : default_drawer;
            if (!prefs.contains(getString(R.string.version)) || !prefs.getString("version", "1.2").equals("1.2")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.putString(getString(R.string.version), "1.2");
                editor.putBoolean(getString(R.string.saved_hour), saved_hour);
                editor.putBoolean(getString(R.string.saved_color), saved_color);
                editor.putBoolean(getString(R.string.saved_show_seconds), saved_seconds);
                editor.putBoolean(getString(R.string.saved_show_time), saved_time);
                editor.putInt(getString(R.string.saved_drawer), saved_drawer);
                editor.apply();
            }
            crc_view.setPrefs(prefs, saved_hour, saved_color, saved_seconds, saved_time, saved_drawer);

            // setup user interface elements: all should start off invisible,
            // and should have crc_view as an appropriate listener
            // (since that's where I take care of the interaction)

            ToggleButton hb = (ToggleButton) rootView.findViewById(R.id.hourToggle);
            hb.setVisibility(View.INVISIBLE);
            hb.setOnCheckedChangeListener(crc_view);
            hb.setChecked(saved_hour);

            ToggleButton mb = (ToggleButton) rootView.findViewById(R.id.monochromeToggle);
            mb.setOnCheckedChangeListener(crc_view);
            mb.setVisibility(View.INVISIBLE);
            mb.setChecked(saved_color);

            Spinner db = (Spinner) rootView.findViewById(R.id.drawSpinner);
            db.setOnItemSelectedListener(crc_view);
            db.setSelection(saved_drawer);
            db.setVisibility(View.INVISIBLE);

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

            Button helpButton = (Button) rootView.findViewById(R.id.helpButton);
            helpButton.setVisibility(View.INVISIBLE);
            helpButton.setOnClickListener(crc_view);

            crc_view.setButtonsToListen(hb, mb, db, pb, upButton, dnButton, spinner, timeEditor, helpButton);

            return rootView;
        }

    }

    // used when debugging w/Log.d()
    private static final String tag = "Chinese Remainer";

    Fragment mainFragment;
    Fragment prefFragment;

}
