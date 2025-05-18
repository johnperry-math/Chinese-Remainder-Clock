package name.cantanima.chineseremainderclock;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

public class CRC_Prefs_Activity
        extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_frame_layout);
        crc_prefs = new CRC_Preferences();
        crc_prefs_preview = new CRC_Preferences_Preview();
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.first_block, crc_prefs_preview)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.second_block, crc_prefs)
                .commit();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {

        if (key.equals(getString(R.string.saved_drawer))) {
            ListPreference lp = crc_prefs.findPreference(getString(R.string.saved_drawer));
            if (lp != null)
              lp.setSummary(lp.getEntry());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    protected CRC_Preferences crc_prefs;
    protected CRC_Preferences_Preview crc_prefs_preview;
}
