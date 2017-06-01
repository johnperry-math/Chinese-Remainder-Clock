package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;


public class Chinese_Remainder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese__remainder);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            return true;
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
                    prefs.getBoolean(getString(R.string.saved_hour), default_hour) : false;
            boolean default_color = false;
            boolean saved_color = (prefs.contains(getString(R.string.saved_color))) ?
                    prefs.getBoolean(getString(R.string.saved_color), default_color) : false;
            boolean default_anadig = false;
            boolean saved_anadig = (prefs.contains(getString(R.string.saved_anadig))) ?
                    prefs.getBoolean(getString(R.string.saved_anadig), default_anadig) : false  ;
            crc_view.setPrefs(prefs, saved_hour, saved_color, saved_anadig);

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

            ToggleButton ab = (ToggleButton) rootView.findViewById(R.id.analogToggle);
            ab.setOnCheckedChangeListener(crc_view);
            ab.setVisibility(View.INVISIBLE);
            ab.setChecked(saved_anadig);

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

            crc_view.setButtonsToListen(hb, mb, ab, pb, upButton, dnButton, spinner, timeEditor, helpButton);

            return rootView;
        }

    }

    // used when debugging w/Log.d()
    private static final String tag = "Chinese Remainer";

}
