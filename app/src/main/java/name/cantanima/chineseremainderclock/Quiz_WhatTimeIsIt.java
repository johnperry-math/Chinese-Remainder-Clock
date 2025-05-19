package name.cantanima.chineseremainderclock;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;

/**
 * Created by cantanima on 8/22/17.
 */

public class Quiz_WhatTimeIsIt extends CRC_Quiz implements TimeEntryDialogListener {

  Quiz_WhatTimeIsIt(Chinese_Remainder context) {
    super(context);
    quiz_number_correct = quiz_number_complete = 0;
    quiz_number_total = 5;
    twelve_hour_clock = crc_view.hour_modulus == 4;
    quiz_generator = new Random();
    show_question();
  }

  @Override
  public void show_question() {


    quiz_dialog = new TimeEntryDialog(crc_context, this, quiz_number_complete, quiz_number_total);
    Window quiz_win = quiz_dialog.getWindow();

    if (quiz_win != null) {

      //quiz_win.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

      // we DO NOT want to dim the clock; user needs to see it
      int screen_config = crc_context.getResources().getConfiguration().orientation;
      WindowManager.LayoutParams win_attr = quiz_win.getAttributes();
      if (screen_config == Configuration.ORIENTATION_PORTRAIT)
        win_attr.gravity = Gravity.BOTTOM;
      else if (screen_config == Configuration.ORIENTATION_LANDSCAPE)
        win_attr.gravity = Gravity.START;
      win_attr.alpha = 0.75f;
      quiz_win.setAttributes(win_attr);
      quiz_dialog.getWindow().clearFlags(FLAG_DIM_BEHIND);
      quiz_dialog.show();

      // move the clock to the quiz question's desired position
      new_hour_value = quiz_generator.nextInt(twelve_hour_clock ? 12 : 24) + 1;
      new_minute_value = quiz_generator.nextInt(60);
      crc_view.move_time_to(new_hour_value, new_minute_value);

    }

  }

  @Override
  public void accept_answer(int hr, int min) {

    // check response according to time
    int hmod = twelve_hour_clock ? 12 : 24;
    int mmod = 60;
    if ((hr % hmod) == (new_hour_value % hmod) && (min % mmod) == (new_minute_value % mmod))
      ++quiz_number_correct;
    ++quiz_number_complete;
    react_to_answer(hr, min);

    // either provide a new question or wrap up the quiz
    if (quiz_number_complete < quiz_number_total)

      show_question();

    else {

      if (quiz_dialog.isShowing())
        quiz_dialog.dismiss();
      String quiz_message, dialog_dismiss;
      if (quiz_number_correct == quiz_number_total) {
        quiz_message = crc_context.getString(R.string.quiz_result_great_job);
        dialog_dismiss = crc_context.getString(R.string.quiz_result_dismiss_dialog_great);
      } else if (quiz_number_correct == 0) {
        quiz_message = crc_context.getString(R.string.quiz_result_keep_day_job);
        dialog_dismiss = crc_context.getString(R.string.quiz_result_dismiss_dialog_keep);
      } else {
        quiz_message = crc_context.getString(R.string.quiz_result_better_luck);
        dialog_dismiss = crc_context.getString(R.string.quiz_result_dismiss_dialog_better);
      }
      new AlertDialog.Builder(crc_context).setTitle(crc_context.getString(R.string.quiz_result_title))
          .setMessage(
              quiz_message + ": " + crc_context.getString(R.string.quiz_result_you_earned)
                      + " " + quiz_number_correct + "/" + quiz_number_total
          )
          .setIcon(R.drawable.ic_action_info)
          .setPositiveButton(
              dialog_dismiss,
                  (dialog, which) -> dialog.dismiss()
          ).show();

      quiz_cancelled();

    }

  }

  /**
   * Reacts to the answer, perhaps displaying a new AlertDialog or a Toast.
   * Call this from show_question().
   * This is optional, and the default does nothing.
   *
   * @param hr short_hand chosen
   * @param min minute chosen
   */
  @Override
  public void react_to_answer(int hr, int min) {
    super.react_to_answer(hr, min);
    String message = (hr == new_hour_value && min == new_minute_value)
        ? crc_context.getString(R.string.quiz_correct)
        : crc_context.getString(R.string.quiz_sorry) + " "
            + new_hour_value + ":"
            + ( ( new_minute_value < 10 ) ? "0" : "" )
            + new_minute_value;
    Toast toast = Toast.makeText(crc_context, message, Toast.LENGTH_LONG);
    toast.show();
  }

  @Override
  public void cancelled() { quiz_cancelled(); }

  @Override
  public void time_received(int h, int m) { accept_answer(h, m); }

  private TimeEntryDialog quiz_dialog;
  private int new_hour_value, new_minute_value;
  private int quiz_number_correct;
  private int quiz_number_complete;
  private final int quiz_number_total;
  private final boolean twelve_hour_clock;
  private final Random quiz_generator;

//  private final String tag = "Quiz_WhatTiemIsIt";

}
