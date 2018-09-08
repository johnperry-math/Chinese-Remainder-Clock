package name.cantanima.chineseremainderclock;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by cantanima on 8/22/17.
 */

public abstract class CRC_Quiz {

  /**
   * User wants to take a quiz. Only sets up the basics, including hiding the conventional time!
   * Then it calls show_question, where quiz-specific interface issues and the rest should occur.
   * Children should call this via super() before performing their own setup,
   * and should always call show_question() after performing their own setup.
   * @see #show_question()
   * @param context the Chinese_Remainder context
   */
  public CRC_Quiz(Chinese_Remainder context) {
    crc_context = context;
    android.support.v7.app.ActionBar ab = crc_context.getSupportActionBar();
    if (ab != null) ab.hide();
    crc_view = crc_context.findViewById(R.id.crc_view);
    crc_view.pause_animation();
    activeToggle = crc_view.activeToggle;
    unitSelecter = crc_view.unitSelecter;
    incrementer = crc_view.incrementer;
    decrementer = crc_view.decrementer;
    valueEditor = crc_view.valueEditor;
    button_row = crc_view.manual_button_layout;
    tv = crc_view.tv;
    crc_drawer = crc_view.my_drawer;
    // visibility
    quiz_previous_time_visibility = tv.getVisibility();
    quiz_previous_seconds_visibility = crc_drawer.get_show_seconds();
    // we need to hide buttons if we were in manual mode
    if (activeToggle.getVisibility() != VISIBLE)
      quiz_active_toggle_was_visible = false;
    else {
      quiz_active_toggle_was_visible = true;
      activeToggle.setVisibility(INVISIBLE);
      if (activeToggle.isChecked()) {
        unitSelecter.setVisibility(INVISIBLE);
        incrementer.setVisibility(INVISIBLE);
        decrementer.setVisibility(INVISIBLE);
        valueEditor.setVisibility(INVISIBLE);
        button_row.setVisibility(INVISIBLE);
      }
    }

    crc_drawer.set_show_seconds(false);
    crc_drawer.recalculate_positions();
    tv.setVisibility(INVISIBLE);

  }

  /**
   * Create a new quiz question.
   * This is the place to start a new dialog, which in its turn should invoke
   * (directly or otherwise) accept_answer().
   * @see #accept_answer(int, int)
   */
  abstract public void show_question();

  /**
   * User has answered quiz question. This function processes the answer.
   * If the quiz is not complete, a new question is generated; otherwise,
   * a result w/a light-hearted comment is displayed in an AlertDialog.
   * @see #show_question() ()
   * @param hr the hour entered by the user
   * @param min the minute entered by the user
   */
  abstract public void accept_answer(int hr, int min);

  /**
   * Reacts to the answer, perhaps displaying a new AlertDialog or a Toast.
   * Call this from show_question().
   * This is optional, and the default does nothing.
   */
  public void react_to_answer(int hr, int min) {}

  /**
   * Quiz was cancelled. Restores visibility of various views and,
   * if appropriate, restarts the clock.
   * Call this from either accept_answer() or react_to_answer(), probably after performing
   * your own cleanup.
   */
  public void quiz_cancelled() {
    tv.setVisibility(quiz_previous_time_visibility);
    crc_drawer.set_show_seconds(quiz_previous_seconds_visibility);
    crc_drawer.recalculate_positions();
    android.support.v7.app.ActionBar ab = crc_context.getSupportActionBar();
    if (ab != null) ab.show();
    if (quiz_active_toggle_was_visible) {
      activeToggle.setVisibility(VISIBLE);
      if (activeToggle.isChecked()) {
        unitSelecter.setVisibility(VISIBLE);
        incrementer.setVisibility(VISIBLE);
        decrementer.setVisibility(VISIBLE);
        valueEditor.setVisibility(VISIBLE);
        button_row.setVisibility(VISIBLE);
      }
    }
    if (!activeToggle.isChecked())
      crc_view.restart_animation_by_calendar();
  }

  Chinese_Remainder crc_context;
  protected CRC_View crc_view;
  private Clock_Drawer crc_drawer;
  private int quiz_previous_time_visibility;
  private boolean quiz_previous_seconds_visibility, quiz_active_toggle_was_visible;
  private Switch activeToggle;
  private View unitSelecter, incrementer, decrementer, valueEditor, tv;
  private LinearLayout button_row;
  
}
