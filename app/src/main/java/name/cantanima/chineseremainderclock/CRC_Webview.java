package name.cantanima.chineseremainderclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.app.Activity;

import java.util.Locale;

import static java.util.Locale.GERMAN;
import static java.util.Locale.ITALIAN;

/**
 * Created by cantanima on 4/26/17.
 */

public class CRC_Webview extends WebView {

  public CRC_Webview(Context context, AttributeSet attrs) {
    super(context, attrs);

    String language = Locale.getDefault().getLanguage();
    if (language.equals(ITALIAN.getLanguage()))
      loadUrl("file:///android_asset/Help-it.html");
    else if (language.equals(GERMAN.getLanguage()))
      loadUrl("file:///android_asset/Help-de.html");
    else
      loadUrl("file:///android_asset/Help.html");
  }

}
