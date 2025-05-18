package name.cantanima.chineseremainderclock

import android.content.Context
import android.util.AttributeSet

import androidx.preference.Preference

class CRC_View_Preference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    init {
        layoutResource = R.layout.clock_only_layout
    }

}