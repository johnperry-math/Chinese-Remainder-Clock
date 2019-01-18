package name.cantanima.chineseremainderclock

import android.os.Bundle
import android.preference.PreferenceFragment

class CRC_Preferences_Preview : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preview_preferences)
    }
}