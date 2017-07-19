package name.cantanima.chineseremainderclock;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by cantanima on 7/19/17.
 */

public class TimeEntryDialog extends Dialog implements View.OnClickListener {

  public TimeEntryDialog(Activity my_activity, CRC_View owning_view, int finished, int total) {

    super(my_activity);

    cr_activity = my_activity;
    crc_view = owning_view;
    number_complete = finished;
    number_total = total;

  }

  /**
   * Similar to {@link Activity#onCreate}, you should initialize your dialog
   * in this method, including calling {@link #setContentView}.
   *
   * @param savedInstanceState If this dialog is being reinitialized after a
   *                           the hosting activity was previously shut down, holds the result from
   *                           the most recent call to {@link #onSaveInstanceState}, or null if this
   *                           is the first time.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setCancelable(false);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    if (cr_activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
      setContentView(R.layout.time_dialog_landscape);
    else
      setContentView(R.layout.time_dialog);
    next_button = (Button) findViewById(R.id.quiz_accept_button);
    hour_text = (EditText) findViewById(R.id.quiz_hour_entry);
    minute_text = (EditText) findViewById(R.id.quiz_min_entry);
    String title = cr_activity.getString(R.string.quiz_what_time_is_it) + " "
        + String.valueOf(number_complete + 1) + "/"
        + String.valueOf(number_total);
    TextView message_text = (TextView) findViewById(R.id.quiz_message);
    message_text.setText(title);
    next_button.setOnClickListener(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

  }

  /**
   * Called when a view has been clicked.
   *
   * @param v The view that was clicked.
   */
  @Override
  public void onClick(View v) {

    Editable hour_editable = hour_text.getText();
    Editable minute_editable = minute_text.getText();
    int hour = hour_editable == null ? 0 : Integer.valueOf(hour_editable.toString());
    int minute = minute_editable == null ? 0 : Integer.valueOf(minute_editable.toString());

    crc_view.quiz_answered(hour, minute);
    dismiss();

  }

  protected Activity cr_activity;
  protected CRC_View crc_view;
  protected Button next_button;
  protected EditText hour_text, minute_text;
  protected int number_complete, number_total;
}
