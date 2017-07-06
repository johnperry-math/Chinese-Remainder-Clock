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
            lp.setSummary(lp.getEntry());
        } else if (key.equals(getString(R.string.saved_color))) {
            SwitchPreference cp = (SwitchPreference) crc_prefs.findPreference(getString(R.string.saved_color));
            AmbilWarnaPreference hp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_hour_color));
            AmbilWarnaPreference mp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_minute_color));
            AmbilWarnaPreference sp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_second_color));
            if (hp == null) hp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_bw_hour_color));
            if (mp == null) mp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_bw_minute_color));
            if (sp == null) sp = (AmbilWarnaPreference) crc_prefs.findPreference(getString(R.string.saved_bw_second_color));
            if (cp.isChecked()) {
                int saved_bw_hour_color = pref.getInt(getString(R.string.saved_bw_hour_color), BLUE);
                int saved_bw_minute_color = pref.getInt(getString(R.string.saved_bw_minute_color), RED);
                int saved_bw_second_color = pref.getInt(getString(R.string.saved_bw_second_color), GREEN);
                hp.setKey(getString(R.string.saved_bw_hour_color));
                mp.setKey(getString(R.string.saved_bw_minute_color));
                sp.setKey(getString(R.string.saved_bw_second_color));
                hp.forceSetValue(saved_bw_hour_color);
                mp.forceSetValue(saved_bw_minute_color);
                sp.forceSetValue(saved_bw_second_color);
            } else {
                int saved_hour_color = pref.getInt(getString(R.string.saved_hour_color), BLUE);
                int saved_minute_color = pref.getInt(getString(R.string.saved_minute_color), RED);
                int saved_second_color = pref.getInt(getString(R.string.saved_second_color), GREEN);
                hp.setKey(getString(R.string.saved_hour_color));
                mp.setKey(getString(R.string.saved_minute_color));
                sp.setKey(getString(R.string.saved_second_color));
                hp.forceSetValue(saved_hour_color);
                mp.forceSetValue(saved_minute_color);
                sp.forceSetValue(saved_second_color);
            }
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
