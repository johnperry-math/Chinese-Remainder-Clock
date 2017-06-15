package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

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
    }

}
