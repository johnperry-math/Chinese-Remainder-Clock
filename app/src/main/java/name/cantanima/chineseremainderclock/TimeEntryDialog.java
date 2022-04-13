package name.cantanima.chineseremainderclock;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Created by cantanima on 7/19/17.
 *
 * Used to obtain user's input in a "What time is it?" quiz.
 *
 */
public class TimeEntryDialog extends Dialog implements View.OnClickListener {

  /**
   * assigns values passed in
   * @param activity Activity that opened this dialog
   * @param new_listener TimeEntryListener to receive events
   * @param finished how many questions have been finished (from 0 to total - 1 (we hope!))
   * @param total how many total questions are desired
   */
  TimeEntryDialog(
      Activity activity, TimeEntryDialogListener new_listener, int finished, int total
  ) {

    super(activity);

    cr_activity = activity;
    listeners = new LinkedList<>();
    listeners.add(new_listener);
    number_complete = finished;
    number_total = total;
    setCanceledOnTouchOutside(false);

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
    setContentView(R.layout.time_dialog);
    // find and remember important interface elements
    Button next_button = findViewById(R.id.quiz_accept_button);
    time_entry_clock = findViewById(R.id.twohanded_clock);
    // message to indicate quiz name & progress on question
    String title = cr_activity.getString(R.string.quiz_what_time_is_it) + " " + number_complete + 1 + "/" + number_total;
    TextView message_text = findViewById(R.id.quiz_message);
    message_text.setText(title);
    // listen for next button
    next_button.setOnClickListener(this);
    // hide keyboard
    Window win = getWindow();
    if (win != null)
      win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    if (number_complete == 0) {
      Toast toast = Toast.makeText(
          cr_activity, cr_activity.getString(R.string.dial_use), Toast.LENGTH_SHORT
      );
      toast.show();
    }

  }

  /**
   * When the next button is clicked (and we're only listening to the next button)
   * we collect information, pass it to the listeners time_received(), and dismiss the dialog.
   * @see TimeEntryDialogListener#time_received(int, int)
   *
   * @param v The view that was clicked.
   */
  @Override
  public void onClick(View v) {

    int hour = time_entry_clock.short_value();
    int minute = time_entry_clock.long_value();

    for (TimeEntryDialogListener listener : listeners)
      listener.time_received(hour, minute);
    dismiss();

  }

  /**
   * When the user presses the back button, we assume s/he wants to quit the quiz.
   * This calls crc_view's quiz_cancelled() routine.
   */
  @Override
  public void onBackPressed() {

    dismiss();
    for (TimeEntryDialogListener listener : listeners)
      listener.cancelled();

  }

  /** Activity that started this dialog */
  private final Activity cr_activity;
  /** CRC_View with which we must interact */
  private final LinkedList<TimeEntryDialogListener> listeners;
  /** Dial_Entry for time entry */
  private Dial_Entry time_entry_clock;
  /** number of questions completed and total */
  private final int number_complete;
  private final int number_total;

}
