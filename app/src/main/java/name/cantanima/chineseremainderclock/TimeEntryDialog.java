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

/**
 *
 * Used to obtain user's input in a "What time is it?" quiz.
 *
 */
public class TimeEntryDialog extends Dialog implements View.OnClickListener {

  /**
   * assigns values passed in
   * @param my_activity Activity that opened this dialog
   * @param owning_view view that owns this dialog
   * @param finished how many questions have been finished (from 0 to total - 1 (we hope!))
   * @param total how many total questions are desired
   */
  public TimeEntryDialog(Activity my_activity, CRC_View owning_view, int finished, int total) {

    super(my_activity);

    cr_activity = my_activity;
    crc_view = owning_view;
    number_complete = finished;
    number_total = total;

  }

  /**
   *
   * Sets up the dialog's interface.
   *
   * @param savedInstanceState If this dialog is being reinitialized after a
   *                           the hosting activity was previously shut down, holds the result from
   *                           the most recent call to {@link #onSaveInstanceState}, or null if this
   *                           is the first time.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    // no title bar (space is at a premium)
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    // set up layout according to device orientation
    if (cr_activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
      setContentView(R.layout.time_dialog_landscape);
    else
      setContentView(R.layout.time_dialog);
    // find and remember important interface elements
    next_button = (Button) findViewById(R.id.quiz_accept_button);
    hour_text = (EditText) findViewById(R.id.quiz_hour_entry);
    minute_text = (EditText) findViewById(R.id.quiz_min_entry);
    // message to indicate quiz name & progress on question
    String title = cr_activity.getString(R.string.quiz_what_time_is_it) + " "
        + String.valueOf(number_complete + 1) + "/"
        + String.valueOf(number_total);
    TextView message_text = (TextView) findViewById(R.id.quiz_message);
    message_text.setText(title);
    // listen for next button
    next_button.setOnClickListener(this);
    // hide keyboard
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

  }

  /**
   * When the next button is clicked (and we're only listening to the next button)
   * we collect information, pass it to our CRC_View via quiz_answered(), and dismiss the dialog.
   * @see CRC_View#quiz_answered(int, int)
   *
   * @param v The view that was clicked.
   */
  @Override
  public void onClick(View v) {

    Editable hour_editable = hour_text.getText();
    Editable minute_editable = minute_text.getText();
    int hour = 0, minute = 0;
    if (hour_editable != null && hour_editable.length() != 0)
      hour = Integer.valueOf(hour_editable.toString());
    if (minute_editable != null && minute_editable.length() != 0)
      minute = Integer.valueOf(minute_editable.toString());

    crc_view.quiz_answered(hour, minute);
    dismiss();

  }

  /**
   * When the user presses the back button, we assume s/he wants to quit the quiz.
   * This calls crc_view's quiz_cancelled() routine.
   */
  @Override
  public void onBackPressed() {

    dismiss();
    crc_view.quiz_cancelled();

  }

  /** Activity that started this dialog */
  protected Activity cr_activity;
  /** CRC_View with which we must interact */
  protected CRC_View crc_view;
  /** button to request next question */
  protected Button next_button;
  /** text for hour, minute */
  protected EditText hour_text, minute_text;
  /** number of questions completed and total */
  protected int number_complete, number_total;

}
