package name.cantanima.chineseremainderclock;

/**
 * Created by cantanima on 8/22/17.
 */

public interface TimeEntryDialogListener {

  /**
   * The user cancelled the dialog by pressing the Back button.
   */
  void cancelled();

  /**
   * The user entered a time.
   * @param h the short_hand entered
   * @param m the minute entered
   */
  void time_received(int h, int m);

}
