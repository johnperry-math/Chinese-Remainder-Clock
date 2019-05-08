package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.os.Build;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.floor;
import static java.lang.Math.round;

/*
 * Created by cantanima on 6/5/17.
 * Seriously revised big-time since then, especially extending to CRC_View_Polygonal
 */

/**
 * This class extends Clock_Drawer for the Vertie design,
 * a polygonal design that rotates a point around a polygon,
 * to the position corresponding to the remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Vertie works.
 * @see Clock_Drawer
 */
public class CRC_View_Vertie extends CRC_View_Polygonal {

  // constructor
  CRC_View_Vertie(CRC_View owner) {

    // aside from default setup, we also setup the paint for the polygons
    initialize_fields(owner);
    poly_paint.setColor(WHITE);
    poly_paint.setStrokeWidth(2);
    poly_paint.setStyle(STROKE);

    stringID = R.string.vertie_manual_hint;

  }

  /** one frame every tenth of a second */
  float preferred_step() { return 0.1f; }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void draw(Canvas canvas) {

    // usual setup

    setup_time();

    drawTimeAndRectangle(canvas, hour, minute, second, diam);

    // in what follows, xmodi is the time unit x modulo i, while
    // lxmodi is last_x % i

    int lhmod3 = my_viewer.last_h % 3;
    int lhmod4 = my_viewer.last_h % my_viewer.hour_modulus;
    int hmod3 = hour % 3;
    int hmod4 = hour % my_viewer.hour_modulus;
    if (hmod3 == 0) hmod3 = 3;
    if (hmod4 == 0) hmod4 = my_viewer.hour_modulus;

    int lmmod3 = my_viewer.last_m % 3;
    int lmmod4 = my_viewer.last_m % 4;
    int lmmod5 = my_viewer.last_m % 5;
    int mmod3 = minute % 3;
    int mmod4 = minute % 4;
    int mmod5 = minute % 5;
    if (mmod3 == 0) mmod3 = 3;
    if (mmod4 == 0) mmod4 = 4;
    if (mmod5 == 0) mmod5 = 5;

    int lsmod3 = my_viewer.last_s % 3;
    int lsmod4 = my_viewer.last_s % 4;
    int lsmod5 = my_viewer.last_s % 5;
    int smod3 = second % 3;
    int smod4 = second % 4;
    int smod5 = second % 5;
    if (smod3 == 0) smod3 = 3;
    if (smod4 == 0) smod4 = 4;
    if (smod5 == 0) smod5 = 5;

    // Vertie rotates a ball about a polygon: "ball paint" controls the drawing of the ball

    ball_paint.setAlpha(255);
    ball_paint.setStyle(FILL);
    ball_paint.setStrokeWidth(1);

    // make sure the ball doesn't overshoot its point
    float offset = my_viewer.my_offset > 0.99 ? 1 : my_viewer.my_offset;

    // for moving
    float h3off = offset, h48off = offset,
          m3off = offset, m4off = offset, m5off = offset,
          s3off = offset, s4off = offset, s5off = offset;

    switch (dragged_unit) {
      case HOUR3:
        hmod3 = round(moved_offset * 3 - 0.5f);
        lhmod3 = (hmod3 == 0) ? 2 : hmod3 - 1;
        h3off = 3*moved_offset - (float) floor(3*moved_offset) + 0.25f;
        if (h3off > 1) {
          h3off -= 1;
          hmod3 = (hmod3 + 1) % 3;
          lhmod3 = (lhmod3 + 1) % 3;
        }
        break;
      case HOURH:
        if (my_viewer.hour_modulus == 4) {
          hmod4 = round(moved_offset * 4 - 0.5f);
          lhmod4 = (hmod4 == 0) ? 3 : hmod4 - 1;
          h48off = 4 * moved_offset - (float) floor(4 * moved_offset);
          if (h48off > 1) {
            h48off -= 1;
            hmod4 = (hmod4 + 1) % 4;
            lhmod4 = (lhmod4 + 1) % 4;
          }
        } else {
          hmod4 = round((moved_offset + 0.25f) * 8 - 0.5f) - 1;
          if (hmod4 < 0) hmod4 = 7;
          lhmod4 = (hmod4 == 0) ? 7 : hmod4 - 1;
          h48off = 8 * moved_offset - (float) floor(8 * moved_offset);
          if (h48off > 1) {
            h48off -= 1;
            hmod4 = (hmod4 + 1) % 8;
            lhmod4 = (lhmod4 + 1) % 8;
          }
        }
        break;
      case MIN3:
        mmod3 = round(moved_offset * 3 - 0.5f);
        lmmod3 = (mmod3 == 0) ? 2 : mmod3 - 1;
        m3off = 3*moved_offset - (float) floor(3*moved_offset) + 0.25f;
        if (m3off > 1) {
          m3off -= 1;
          mmod3 = (mmod3 + 1) % 3;
          lmmod3 = (lmmod3 + 1) % 3;
        }
        break;
      case MIN4:
        mmod4 = round(moved_offset * 4 - 0.5f);
        lmmod4 = (mmod4 == 0) ? 3 : mmod4 - 1;
        m4off = 4*moved_offset - (float) floor(4*moved_offset);
        if (m4off > 1) {
          m4off -= 1;
          mmod4 = (mmod4 + 1) % 4;
          lmmod4 = (lmmod4 + 1) % 4;
        }
        break;
      case MIN5:
        mmod5 = round(moved_offset * 5 - 0.5f);
        lmmod5 = (mmod5 == 0) ? 4 : mmod5 - 1;
        m5off = 5*moved_offset - (float) floor(5*moved_offset);
        if (m5off > 1) {
          m5off -= 1;
          mmod5 = (mmod5 + 1) % 5;
          lmmod5 = (lmmod5 + 1) % 5;
        }
        break;
      case SEC3:
        smod3 = round(moved_offset * 3 - 0.5f);
        lsmod3 = (smod3 == 0) ? 2 : smod3 - 1;
        s3off = 3*moved_offset - (float) floor(3*moved_offset) + 0.25f;
        if (s3off > 1) {
          s3off -= 1;
          smod3 = (smod3 + 1) % 3;
          lsmod3 = (lsmod3 + 1) % 3;
        }
        break;
      case SEC4:
        smod4 = round(moved_offset * 4 - 0.5f);
        lsmod4 = (smod4 == 0) ? 3 : smod4 - 1;
        s4off = 4*moved_offset - (float) floor(4*moved_offset);
        if (s4off > 1) {
          s4off -= 1;
          smod4 = (smod4 + 1) % 4;
          lsmod4 = (lsmod4 + 1) % 4;
        }
        break;
      case SEC5:
        smod5 = round(moved_offset * 5 - 0.5f);
        lsmod5 = (smod5 == 0) ? 4 : smod5 - 1;
        s5off = 5*moved_offset - (float) floor(5*moved_offset);
        if (s5off > 1) {
          s5off -= 1;
          smod5 = (smod5 + 1) % 5;
          lsmod5 = (lsmod5 + 1) % 5;
        }
        break;
      default: break;
    }

    // draw each remainder as follows:
    // 1) draw the polygon (lines, oddly, not a Path -- should perhaps look into that, efficiency)
    // 2) find the right place for the circle:
    //    a) if the time unit is changing, use the offset to find the correct place on the line
    //       for the ball
    //    b) if the time unit is not changing, draw the ball in the correct position

    // short_hand, modulo 3

    ball_paint.setColor(hour_color);

    poly_paint.setColor(line_color);
    canvas.drawLines(digi_h3_pts, poly_paint);
    //if (my_viewer.last_h != hour) {
    if (lhmod3 != hmod3) {
      canvas.drawCircle(
          digi_h3_pts[(lhmod3 % 3) << 2]*(1 - h3off) + digi_h3_pts[(hmod3 % 3) << 2]*h3off,
          digi_h3_pts[((lhmod3 % 3) << 2) + 1]*(1 - h3off)
                  + digi_h3_pts[((hmod3 % 3) << 2) + 1]*h3off,
          cradius,
          ball_paint
      );
    } else {
      canvas.drawCircle(
          digi_h3_pts[(hmod3 % 3) << 2], digi_h3_pts[((hmod3 % 3) << 2) + 1],
          cradius, ball_paint
      );
    }

    // short_hand, modulo 4 or 8, depending on the kind of time

    if (my_viewer.hour_modulus == 4) {

      canvas.drawLines(digi_h4_pts, poly_paint);
      ball_paint.setColor(hour_color);
      if (lhmod4 != hmod4) {
        canvas.drawCircle(
            digi_h4_pts[(lhmod4 % 4) << 2]*(1 - h48off) + digi_h4_pts[(hmod4 % 4) << 2]*h48off,
            digi_h4_pts[((lhmod4 % 4) << 2) + 1]*(1 - h48off)
                + digi_h4_pts[((hmod4 % 4) << 2) + 1]*h48off,
            cradius,
            ball_paint
        );
      } else {
        canvas.drawCircle(
            digi_h4_pts[(hmod4 % 4) << 2], digi_h4_pts[((hmod4 % 4) << 2) + 1],
            cradius, ball_paint
        );
      }

  } else { // short_hand modulo 8

      canvas.drawLines(digi_h8_pts, poly_paint);
      ball_paint.setColor(hour_color);
      if (lhmod4 != hmod4) {
        canvas.drawCircle(
            digi_h8_pts[(lhmod4 % 8) << 2]*(1 - h48off) + digi_h8_pts[(hmod4 % 8) << 2]*h48off,
            digi_h8_pts[((lhmod4 % 8) << 2) + 1]*(1 - h48off)
                + digi_h8_pts[((hmod4 % 8) << 2) + 1]*h48off,
            cradius,
            ball_paint
        );
      } else {
        canvas.drawCircle(
            digi_h8_pts[(hmod4 % 8) << 2], digi_h8_pts[((hmod4 % 8) << 2) + 1],
            cradius, ball_paint
        );
      }

    }

    // minute, modulo 3

    canvas.drawLines(digi_m3_pts, poly_paint);
    ball_paint.setColor(minute_color);
    if (lmmod3 != mmod3) {
      canvas.drawCircle(
          digi_m3_pts[(lmmod3 % 3) << 2]*(1 - m3off) + digi_m3_pts[(mmod3 % 3) << 2]*m3off,
          digi_m3_pts[((lmmod3 % 3) << 2) + 1]*(1 - m3off)
              + digi_m3_pts[((mmod3 % 3) << 2) + 1]*m3off,
          cradius,
          ball_paint
      );
    } else {
      canvas.drawCircle(
          digi_m3_pts[(mmod3 % 3) << 2], digi_m3_pts[((mmod3 % 3) << 2) + 1],
          cradius, ball_paint
      );
    }

    // minute, modulo 4

    canvas.drawLines(digi_m4_pts, poly_paint);
    ball_paint.setColor(minute_color);
    if (lmmod4 != mmod4) {
      canvas.drawCircle(
            digi_m4_pts[(lmmod4 % 4) << 2]*(1 - m4off) + digi_m4_pts[(mmod4 % 4) << 2]*m4off,
            digi_m4_pts[((lmmod4 % 4) << 2) + 1]*(1 - m4off)
                + digi_m4_pts[((mmod4 % 4) << 2) + 1]*m4off,
            cradius,
            ball_paint
      );
    } else {
      canvas.drawCircle(
          digi_m4_pts[(mmod4 % 4) << 2], digi_m4_pts[((mmod4 % 4) << 2) + 1],
          cradius, ball_paint
      );
    }

    // minute, modulo 5

    canvas.drawLines(digi_m5_pts, poly_paint);
    ball_paint.setColor(minute_color);
    if (lmmod5 != mmod5) {
      canvas.drawCircle(
          digi_m5_pts[(lmmod5 % 5) << 2]*(1 - m5off) + digi_m5_pts[(mmod5 % 5) << 2]*m5off,
          digi_m5_pts[((lmmod5 % 5) << 2) + 1]*(1 - m5off)
              + digi_m5_pts[((mmod5 % 5) << 2) + 1]*m5off,
          cradius,
          ball_paint
      );
    } else {
      canvas.drawCircle(
          digi_m5_pts[(mmod5 % 5) << 2], digi_m5_pts[((mmod5 % 5) << 2) + 1],
          cradius, ball_paint
      );
    }

    if (show_seconds) {

      // second, modulo 3

      canvas.drawLines(digi_s3_pts, poly_paint);
      ball_paint.setColor(second_color);
      if (lsmod3 != smod3) {
        canvas.drawCircle(
            digi_s3_pts[(lsmod3 % 3) << 2] * (1 - s3off) + digi_s3_pts[(smod3 % 3) << 2] * s3off,
            digi_s3_pts[((lsmod3 % 3) << 2) + 1] * (1 - s3off)
                + digi_s3_pts[((smod3 % 3) << 2) + 1] * s3off,
            cradius,
            ball_paint
        );
      } else {
        canvas.drawCircle(
            digi_s3_pts[(smod3 % 3) << 2], digi_s3_pts[((smod3 % 3) << 2) + 1],
            cradius, ball_paint
        );
      }

      // second, modulo 4

      canvas.drawLines(digi_s4_pts, poly_paint);
      ball_paint.setColor(second_color);
      if (lsmod4 != smod4) {
        canvas.drawCircle(
            digi_s4_pts[(lsmod4 % 4) << 2] * (1 - s4off) + digi_s4_pts[(smod4 % 4) << 2] * s4off,
            digi_s4_pts[((lsmod4 % 4) << 2) + 1] * (1 - s4off)
                + digi_s4_pts[((smod4 % 4) << 2) + 1] * s4off,
            cradius,
            ball_paint
        );
      } else {
        canvas.drawCircle(
            digi_s4_pts[(smod4 % 4) << 2], digi_s4_pts[((smod4 % 4) << 2) + 1],
            cradius, ball_paint
        );
      }

      // second, modulo 5

      canvas.drawLines(digi_s5_pts, poly_paint);
      ball_paint.setColor(second_color);
      if (lsmod5 != smod5) {
        canvas.drawCircle(
            digi_s5_pts[(lsmod5 % 5) << 2] * (1 - s5off) + digi_s5_pts[(smod5 % 5) << 2] * s5off,
            digi_s5_pts[((lsmod5 % 5) << 2) + 1] * (1 - s5off)
                    + digi_s5_pts[((smod5 % 5) << 2) + 1] * s5off,
            cradius,
            ball_paint
        );
      } else {
        canvas.drawCircle(
            digi_s5_pts[(smod5 % 5) << 2], digi_s5_pts[((smod5 % 5) << 2) + 1],
            cradius, ball_paint
        );
      }

    }

    usual_cleanup();

  }

}
