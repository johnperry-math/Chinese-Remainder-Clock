package name.cantanima.chineseremainderclock;

/**
 * Created by cantanima on 8/22/17.
 */

public interface TimeEntryDialogListener {

  /**
   * The user cancelled the dialog by pressing the Back button.
   */
  public void cancelled();

  /**
   * The user entered a time.
   * @param h the hour entered
   * @param m the minute entered
   */
  public void time_received(int h, int m);

}
