package name.cantanima.chineseremainderclock

import android.preference.Preference
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CRC_View_Preference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    /**
     * Make sure to call through to the superclass's implementation.
     *
     * @param parent The parent that this View will eventually be attached to.
     * @return The View that displays this Preference.
     * @see .onBindView
     */
    override fun onCreateView(parent: ViewGroup?): View {
        super.onCreateView(parent)
        val li : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return li.inflate(R.layout.clock_only_layout, parent, false)
    }
}