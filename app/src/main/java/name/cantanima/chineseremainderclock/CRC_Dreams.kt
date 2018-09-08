package name.cantanima.chineseremainderclock

import android.annotation.TargetApi
import android.service.dreams.DreamService
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView

@TargetApi(17)
class CRC_Dream : DreamService() {

    override fun onDreamingStarted() {
        super.onDreamingStarted()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setInteractive(false)
        setScreenBright(false)
        setContentView(R.layout.dream_layout)
        val time_display : TextView = findViewById(R.id.time_display)
        val clock_display : CRC_View = findViewById(R.id.CRC_View)
        clock_display.set_time_textview(time_display)
    }

}