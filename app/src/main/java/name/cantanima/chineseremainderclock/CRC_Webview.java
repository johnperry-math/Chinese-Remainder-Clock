package name.cantanima.chineseremainderclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.app.Activity;

/**
 * Created by cantanima on 4/26/17.
 */

public class CRC_Webview extends WebView {

    public CRC_Webview(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadUrl("file:///android_asset/Help.html");
    }

}
