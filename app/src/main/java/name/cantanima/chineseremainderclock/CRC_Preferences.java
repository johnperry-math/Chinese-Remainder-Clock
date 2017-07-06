package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.LTGRAY;
import static name.cantanima.chineseremainderclock.Clock_Drawer.VERYLIGHTGRAY;

public class CRC_Preferences extends PreferenceFragment
{

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListPreference lp = (ListPreference) findPreference(getString(R.string.saved_drawer));
        lp.setSummary(lp.getEntries()[Integer.valueOf(lp.getValue())]);
        PreferenceManager pm = getPreferenceManager();
        SharedPreferences pref = pm.getSharedPreferences();
        boolean is_mono = pref.getBoolean(getString(R.string.saved_color), false);
        if (is_mono) {
            AmbilWarnaPreference hp = (AmbilWarnaPreference) findPreference(getString(R.string.saved_hour_color));
            hp.setKey(getString(R.string.saved_bw_hour_color));
            hp.forceSetValue(pref.getInt(getString(R.string.saved_bw_hour_color), VERYLIGHTGRAY));
            AmbilWarnaPreference mp = (AmbilWarnaPreference) findPreference(getString(R.string.saved_minute_color));
            mp.setKey(getString(R.string.saved_bw_minute_color));
            mp.forceSetValue(pref.getInt(getString(R.string.saved_bw_minute_color), LTGRAY));
            AmbilWarnaPreference sp = (AmbilWarnaPreference) findPreference(getString(R.string.saved_second_color));
            sp.setKey(getString(R.string.saved_bw_second_color));
            sp.forceSetValue(pref.getInt(getString(R.string.saved_bw_second_color), BLACK));
        }

    }

}
