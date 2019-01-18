package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

public class CRC_Prefs_Activity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        crc_prefs = new CRC_Preferences();
        crc_prefs_preview = new CRC_Preferences_Preview();
        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, crc_prefs)
                .commit();*/
        View v = findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = parent.indexOfChild(v);
        parent.removeView(v);
        v = getLayoutInflater().inflate(R.layout.preferences_frame_layout, parent, false);
        parent.addView(v, index);
        getFragmentManager().beginTransaction().add(R.id.first_block, crc_prefs_preview).commit();
        getFragmentManager().beginTransaction().add(R.id.second_block, crc_prefs).commit();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

        if (key.equals(getString(R.string.saved_drawer))) {
            ListPreference lp = (ListPreference) crc_prefs.findPreference(getString(R.string.saved_drawer));
            if (lp != null)
              lp.setSummary(lp.getEntry());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    protected CRC_Preferences crc_prefs;
    protected CRC_Preferences_Preview crc_prefs_preview;
}
