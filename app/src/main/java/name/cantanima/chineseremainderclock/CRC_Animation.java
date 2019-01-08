package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;

import java.util.Calendar;

/**
 * Controls animation of the clock.
 * The main points to understand are that every animation will be started roughly half a second
 * before the clock hits a new second -- that is, right around 500 milliseconds time --
 * and that the subsequent number of frames over the followed half seconds need to complete within
 * the next half second. Time between frames will be determined by the Clock_Drawer's
 * preferred_step() method, and when it is time to draw a new frame the animator will call
 * the Clock_Drawer's draw() method (indirectly, through CRC_View's onDraw).
 * @see Clock_Drawer#draw(Canvas)
 * @see CRC_View#onDraw(Canvas)
 */
public class CRC_Animation implements Runnable {

  /**
   * create an animator to animate the named view
   * @param view a CRC_View to animate
   */
  CRC_Animation(CRC_View view) {

    my_view = view;
    step = 0.0f;
    Calendar time = Calendar.getInstance();
    // animation seems to take less than half a second, so:
    // setup to start at the next half-second
    int millis = time.get(Calendar.MILLISECOND);
    if (millis < 500)
        my_view.postOnAnimationDelayed(this, 500 - millis);
    else
        my_view.postOnAnimationDelayed(this, 1500 - millis);

  }

  /**
   * At each time alert this function will call the drawer.
   */
  public void run() {

    // redraw with new position
    my_view.set_offset(step);
    my_view.invalidate();

    // step forward
    step += step_delta;
    // restart animation:
    if (immediate || step < 1f + step_delta) { // more to do? wait the desired step
      immediate = false;
      my_view.postOnAnimationDelayed(this, (int) (step_delta * 2.5));
    } else if (!paused) { // no, move to next half-second
      step = 0.0f;
      Calendar time = Calendar.getInstance();
      int millis = time.get(Calendar.MILLISECOND);
      if (millis < 500)
          my_view.postOnAnimationDelayed(this, 500 - millis);
      else
          my_view.postOnAnimationDelayed(this, 1500 - millis);
    }

  }

  /** set the step for animation -- don't invoke this unless you know what you're doing */
  void set_step(float new_step) { step_delta = new_step; }

  /** whether the animator is currently paused */
  @SuppressWarnings("unused method")
  public boolean is_paused() { return paused; }

  /** pause the animator (for Manual mode) */
  void pause() { paused = true; }

  /** resume the animation (leaving Manual mode) */
  void resume() {

    paused = false;
    step = 0.0f;
    Calendar time = Calendar.getInstance();
    if (immediate) {
      immediate = false;
      my_view.postOnAnimationDelayed(this, (int) (step_delta * 2.5));
    } else {
      // restart on next half-second
      int millis = time.get(Calendar.MILLISECOND);
      if (millis < 500)
        my_view.postOnAnimationDelayed(this, 500 - millis);
      else
        my_view.postOnAnimationDelayed(this, 1500 - millis);
    }

  }

  public void immediate_animation() { immediate = true; }

  /** CRC_View that we are animating */
  private CRC_View my_view;
  /** fields related to controlling time steps for each frame */
  private float step, step_delta;
  /** whether the animator is paused */
  private boolean paused;
  /** whether to start the animation immediately */
  private boolean immediate = false;

}
