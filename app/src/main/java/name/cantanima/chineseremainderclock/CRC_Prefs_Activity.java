package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.List;

/**
 * Created by cantanima on 6/15/17.
 */

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
        }

    }

    protected CRC_Preferences crc_prefs;
}
