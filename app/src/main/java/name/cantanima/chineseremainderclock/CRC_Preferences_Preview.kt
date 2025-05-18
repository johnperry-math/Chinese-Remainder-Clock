package name.cantanima.chineseremainderclock

import android.os.Bundle

import androidx.preference.PreferenceFragmentCompat

class CRC_Preferences_Preview : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preview_preferences, rootKey)
    }
}