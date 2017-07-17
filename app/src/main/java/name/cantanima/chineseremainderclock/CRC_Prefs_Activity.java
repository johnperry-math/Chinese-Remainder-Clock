package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.RED;
import static name.cantanima.chineseremainderclock.Clock_Drawer.VERYLIGHTGRAY;

public class CRC_Prefs_Activity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        crc_prefs = new CRC_Preferences();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, crc_prefs)
                .commit();
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
}
