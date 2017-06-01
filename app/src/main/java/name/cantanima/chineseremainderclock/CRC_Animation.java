package name.cantanima.chineseremainderclock;

import java.util.Calendar;

/**
 * Created by cantanima on 4/23/17.
 */

public class CRC_Animation implements Runnable {

    public CRC_Animation(CRC_View view) {

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

    public void run() {

        // redraw with new position
        my_view.set_offset(step);
        my_view.invalidate();

        // step forward
        step += .04f;
        // restart animation:
        if (step < 1.04f) // more to do? wait 10 ms
            my_view.postOnAnimationDelayed(this, 10);
        else if (!paused) { // no, move to next half-second
            step = 0.0f;
            Calendar time = Calendar.getInstance();
            int millis = time.get(Calendar.MILLISECOND);
            if (millis < 500)
                my_view.postOnAnimationDelayed(this, 500 - millis);
            else
                my_view.postOnAnimationDelayed(this, 1500 - millis);
        }

    }

    public boolean is_paused() { return paused; }

    public void pause() { paused = true; }

    public void resume() {
        paused = false;
        step = 0.0f;
        Calendar time = Calendar.getInstance();
        // restart on next half-second
        int millis = time.get(Calendar.MILLISECOND);
        if (millis < 500)
            my_view.postOnAnimationDelayed(this, 500 - millis);
        else
            my_view.postOnAnimationDelayed(this, 1500 - millis);
    }

    private CRC_View my_view;
    private float step;
    private static final String tag = "CRC_Animation";
    private boolean paused;

}
