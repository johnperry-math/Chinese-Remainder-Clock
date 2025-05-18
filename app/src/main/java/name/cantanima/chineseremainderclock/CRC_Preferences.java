package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.LTGRAY;
import static name.cantanima.chineseremainderclock.Clock_Drawer.VERYLIGHTGRAY;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

import com.rarepebble.colorpicker.ColorPreference;

public class CRC_Preferences extends PreferenceFragmentCompat
{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListPreference lp = findPreference(getString(R.string.saved_drawer));
        if (lp != null) {
            lp.setSummary(lp.getEntries()[Integer.parseInt(lp.getValue())]);
        }
        PreferenceManager pm = getPreferenceManager();
        SharedPreferences pref = pm.getSharedPreferences();
        if (pref != null) {
            boolean is_mono = pref.getBoolean(getString(R.string.saved_color), false);
            if (is_mono) {
                ColorPreference hp = findPreference(getString(R.string.saved_hour_color));
                if (hp != null) {
                    hp.setKey(getString(R.string.saved_bw_hour_color));
                    hp.setColor(pref.getInt(getString(R.string.saved_bw_hour_color), VERYLIGHTGRAY));
                }
                ColorPreference mp = findPreference(getString(R.string.saved_minute_color));
                if (mp != null) {
                    mp.setKey(getString(R.string.saved_bw_minute_color));
                    mp.setColor(pref.getInt(getString(R.string.saved_bw_minute_color), LTGRAY));
                }
                ColorPreference sp = findPreference(getString(R.string.saved_second_color));
                if (sp != null) {
                    sp.setKey(getString(R.string.saved_bw_second_color));
                    sp.setColor(pref.getInt(getString(R.string.saved_bw_second_color), BLACK));
                }
            }
        }

    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof ColorPreference) {
            ((ColorPreference) preference).showDialog(this, 0);
        } else super.onDisplayPreferenceDialog(preference);
    }

}
