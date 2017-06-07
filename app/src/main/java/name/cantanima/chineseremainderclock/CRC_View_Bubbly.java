package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;

/**
 * Created by cantanima on 6/6/17.
 */

public class CRC_View_Bubbly extends Clock_Drawer {

    // constructor
    public CRC_View_Bubbly(CRC_View owner) {

        initialize_fields(owner);

    }

    @Override
    void draw(Canvas canvas) {

    }

    float preferred_step() { return 0.49f; }
}
