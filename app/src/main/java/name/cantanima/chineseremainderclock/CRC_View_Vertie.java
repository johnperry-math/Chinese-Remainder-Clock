package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import static java.lang.Math.sin;

/*
 * Created by cantanima on 6/5/17.
 */

/**
 * This class extends Clock_Drawer for the Vertie design,
 * a polygonal design that rotates a point around a polygon,
 * to the position corresponding to the remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Vertie works.
 * @see Clock_Drawer
 */
public class CRC_View_Vertie extends Clock_Drawer {

  // constructor
  CRC_View_Vertie(CRC_View owner) {

    // aside from default setup, we also setup the paint for the polygons
    initialize_fields(owner);
    poly_paint = new Paint(ANTI_ALIAS_FLAG);
    poly_paint.setColor(WHITE);
    poly_paint.setStrokeWidth(2);
    poly_paint.setStyle(STROKE);

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
          hmod4 = round((moved_offset + 1/4) * 8 - 0.5f) - 1;
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

  /**
   *
   * We set up points of the polygons as arrays of floats.
   * Due to the way drawLines() works, intermediate endpoints must be repeated twice.
   * So the number of points to draw is found by shifting the actual number (<< 2).
   * @see android.graphics.Canvas#drawLines(float[], int, int, Paint)
   *
   */
  void recalculate_positions() {

    super.recalculate_positions();

    // first find basic positions and sizes

    cradius = diam / 28;

    float digi_step = diam / 4;
    obj_w2 = diam / 4;

    digi_hcy1 = cy - obj_w2;
    digi_hcy2 = cy + obj_w2;
    digi_mscy1 = cy - obj_w2 * 5 / 2;
    digi_mscy3 = cy + obj_w2 * 5 / 2;

    // the vertices of each polygon are determined using basic trigonometry on a circle; however,
    // in order to have the polygons align in a pleasing way, they the centers of the
    // circumscribing circles may be offset somewhat, hence not all the same

    if (show_seconds) {

      int dir = reverse_orientation ? -1 : 1;

      digi_hx = cx - 2.5f*dir*digi_step;
      digi_mx = cx;
      digi_sx = cx + 2.5f*dir*digi_step;

      digi_h3_pts = new float[]{
          digi_hx - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
          digi_hx - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_hx - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_hx - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
          digi_hx - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
          digi_hx - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
      };

      digi_h4_pts = new float[]{
          digi_hx, digi_hcy2 - obj_w2,
          digi_hx + obj_w2, digi_hcy2,
          digi_hx + obj_w2, digi_hcy2,
          digi_hx, digi_hcy2 + obj_w2,
          digi_hx, digi_hcy2 + obj_w2,
          digi_hx - obj_w2, digi_hcy2,
          digi_hx - obj_w2, digi_hcy2,
          digi_hx, digi_hcy2 - obj_w2,
      };

      digi_h8_pts = new float[]{
          digi_hx - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
          digi_hx - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
          digi_hx - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
          digi_hx - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
          digi_hx - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
          digi_hx - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
          digi_hx - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
          digi_hx - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
          digi_hx - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
          digi_hx - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
          digi_hx - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
          digi_hx - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
          digi_hx - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
          digi_hx - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
          digi_hx - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
          digi_hx - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
      };

      digi_m3_pts = new float[]{
          digi_mx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
          digi_mx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_mx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_mx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_mx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_mx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
      };

      digi_m4_pts = new float[]{
          cx, cy - obj_w2,
          cx + obj_w2, cy,
          cx + obj_w2, cy,
          cx, cy + obj_w2,
          cx, cy + obj_w2,
          digi_mx - obj_w2, cy,
          digi_mx - obj_w2, cy,
          cx, cy - obj_w2,
      };

      digi_m5_pts = new float[]{
          digi_mx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
          digi_mx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_mx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_mx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_mx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_mx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_mx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_mx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_mx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_mx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
      };

      digi_s3_pts = new float[]{
          digi_sx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
          digi_sx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_sx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_sx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_sx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_sx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
      };

      digi_s4_pts = new float[]{
          digi_sx, cy - obj_w2,
          digi_sx + obj_w2, cy,
          digi_sx + obj_w2, cy,
          digi_sx, cy + obj_w2,
          digi_sx, cy + obj_w2,
          digi_sx - obj_w2, cy,
          digi_sx - obj_w2, cy,
          digi_sx, cy - obj_w2,
      };

      digi_s5_pts = new float[]{
          digi_sx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
          digi_sx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_sx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_sx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_sx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_sx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_sx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_sx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_sx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_sx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
      };

    } else {

      float dir = reverse_orientation ? -1f : 1f;

      digi_hx = cx - 1.5f*dir*digi_step;
      digi_mx = cx + 1.5f*dir*digi_step;

      digi_h3_pts = new float[]{
          digi_hx - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
          digi_hx - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_hx - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_hx - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
          digi_hx - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
          digi_hx - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
      };

      digi_h4_pts = new float[]{
          digi_hx, digi_hcy2 - obj_w2,
          digi_hx + obj_w2, digi_hcy2,
          digi_hx + obj_w2, digi_hcy2,
          digi_hx, digi_hcy2 + obj_w2,
          digi_hx, digi_hcy2 + obj_w2,
          digi_hx - obj_w2, digi_hcy2,
          digi_hx - obj_w2, digi_hcy2,
          digi_hx, digi_hcy2 - obj_w2,
      };

      digi_h8_pts = new float[]{
          digi_hx - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
          digi_hx - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
          digi_hx - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
          digi_hx - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
          digi_hx - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
          digi_hx - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
          digi_hx - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
          digi_hx - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
          digi_hx - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
          digi_hx - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
          digi_hx - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
          digi_hx - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
          digi_hx - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
          digi_hx - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
          digi_hx - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
          digi_hx - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
      };

      digi_m3_pts = new float[]{
          digi_mx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
          digi_mx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_mx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
          digi_mx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_mx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
          digi_mx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
      };

      digi_m4_pts = new float[]{
          digi_mx, cy - obj_w2,
          digi_mx + obj_w2, cy,
          digi_mx + obj_w2, cy,
          digi_mx, cy + obj_w2,
          digi_mx, cy + obj_w2,
          digi_mx - obj_w2, cy,
          digi_mx - obj_w2, cy,
          digi_mx, cy - obj_w2,
      };

      digi_m5_pts = new float[]{
          digi_mx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
          digi_mx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_mx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
          digi_mx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_mx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
          digi_mx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_mx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
          digi_mx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_mx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
          digi_mx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
      };

    }

    // now set up the paths

    Path h_tria, h_quad, h_octo, m_tria, m_quad, m_pent, s_tria, s_quad, s_pent;

    h_tria = new Path();
    h_tria.rewind();
    h_tria.moveTo(digi_h3_pts[0], digi_h3_pts[1]); h_tria.lineTo(digi_h3_pts[2], digi_h3_pts[3]);
    h_tria.lineTo(digi_h3_pts[4], digi_h3_pts[5]); h_tria.lineTo(digi_h3_pts[6], digi_h3_pts[7]);

    h_quad = new Path();
    h_quad.rewind();
    h_quad.moveTo(digi_h4_pts[0], digi_h4_pts[1]);
    h_quad.lineTo(digi_h4_pts[2], digi_h4_pts[3]);
    h_quad.lineTo(digi_h4_pts[4], digi_h4_pts[5]);
    h_quad.lineTo(digi_h4_pts[6], digi_h4_pts[7]);
    h_quad.lineTo(digi_h4_pts[8], digi_h4_pts[9]);
    h_quad.lineTo(digi_h4_pts[10], digi_h4_pts[11]);
    h_quad.lineTo(digi_h4_pts[12], digi_h4_pts[13]);
    h_quad.lineTo(digi_h4_pts[14], digi_h4_pts[15]);

    h_octo = new Path();
    h_octo.rewind();
    h_octo.moveTo(digi_h8_pts[0], digi_h8_pts[1]);
    h_octo.lineTo(digi_h8_pts[2], digi_h8_pts[3]);
    h_octo.lineTo(digi_h8_pts[4], digi_h8_pts[5]);
    h_octo.lineTo(digi_h8_pts[6], digi_h8_pts[7]);
    h_octo.lineTo(digi_h8_pts[8], digi_h8_pts[9]);
    h_octo.lineTo(digi_h8_pts[10], digi_h8_pts[11]);
    h_octo.lineTo(digi_h8_pts[12], digi_h8_pts[13]);
    h_octo.lineTo(digi_h8_pts[14], digi_h8_pts[15]);
    h_octo.moveTo(digi_h8_pts[16], digi_h8_pts[17]);
    h_octo.lineTo(digi_h8_pts[18], digi_h8_pts[19]);
    h_octo.lineTo(digi_h8_pts[20], digi_h8_pts[21]);
    h_octo.lineTo(digi_h8_pts[22], digi_h8_pts[23]);
    h_octo.lineTo(digi_h8_pts[24], digi_h8_pts[25]);
    h_octo.lineTo(digi_h8_pts[26], digi_h8_pts[27]);
    h_octo.lineTo(digi_h8_pts[28], digi_h8_pts[29]);
    h_octo.lineTo(digi_h8_pts[30], digi_h8_pts[31]);

    m_tria = new Path();
    m_tria.rewind();
    m_tria.moveTo(digi_m3_pts[0], digi_m3_pts[1]); m_tria.lineTo(digi_m3_pts[2], digi_m3_pts[3]);
    m_tria.lineTo(digi_m3_pts[4], digi_m3_pts[5]); m_tria.lineTo(digi_m3_pts[6], digi_m3_pts[7]);

    m_quad = new Path();
    m_quad.rewind();
    m_quad.moveTo(digi_m4_pts[0], digi_m4_pts[1]); m_quad.lineTo(digi_m4_pts[2], digi_m4_pts[3]);
    m_quad.lineTo(digi_m4_pts[4], digi_m4_pts[5]); m_quad.lineTo(digi_m4_pts[6], digi_m4_pts[7]);
    m_quad.lineTo(digi_m4_pts[8], digi_m4_pts[9]); m_quad.lineTo(digi_m4_pts[10], digi_m4_pts[11]);
    m_quad.lineTo(digi_m4_pts[12], digi_m4_pts[13]); m_quad.lineTo(digi_m4_pts[14], digi_m4_pts[15]);

    m_pent = new Path();
    m_pent.rewind();
    m_pent.moveTo(digi_m5_pts[0], digi_m5_pts[1]); m_pent.lineTo(digi_m5_pts[2], digi_m5_pts[3]);
    m_pent.lineTo(digi_m5_pts[4], digi_m5_pts[5]); m_pent.lineTo(digi_m5_pts[6], digi_m5_pts[7]);
    m_pent.lineTo(digi_m5_pts[8], digi_m5_pts[9]); m_pent.lineTo(digi_m5_pts[10], digi_m5_pts[11]);
    m_pent.lineTo(digi_m5_pts[12], digi_m5_pts[13]); m_pent.lineTo(digi_m5_pts[14], digi_m5_pts[15]);
    m_pent.lineTo(digi_m5_pts[16], digi_m5_pts[17]); m_pent.lineTo(digi_m5_pts[18], digi_m5_pts[19]);

    s_tria = new Path();
    s_quad = new Path();
    s_pent = new Path();

    if (show_seconds) {

      s_tria.rewind();
      s_tria.moveTo(digi_s3_pts[0], digi_s3_pts[1]);
      s_tria.lineTo(digi_s3_pts[2], digi_s3_pts[3]);
      s_tria.lineTo(digi_s3_pts[4], digi_s3_pts[5]);
      s_tria.lineTo(digi_s3_pts[6], digi_s3_pts[7]);

      s_quad.rewind();
      s_quad.moveTo(digi_s4_pts[0], digi_s4_pts[1]);
      s_quad.lineTo(digi_s4_pts[2], digi_s4_pts[3]);
      s_quad.lineTo(digi_s4_pts[4], digi_s4_pts[5]);
      s_quad.lineTo(digi_s4_pts[6], digi_s4_pts[7]);
      s_quad.lineTo(digi_s4_pts[8], digi_s4_pts[9]);
      s_quad.lineTo(digi_s4_pts[10], digi_s4_pts[11]);
      s_quad.lineTo(digi_s4_pts[12], digi_s4_pts[13]);
      s_quad.lineTo(digi_s4_pts[14], digi_s4_pts[15]);

      s_pent.rewind();
      s_pent.moveTo(digi_s5_pts[0], digi_s5_pts[1]);
      s_pent.lineTo(digi_s5_pts[2], digi_s5_pts[3]);
      s_pent.lineTo(digi_s5_pts[4], digi_s5_pts[5]);
      s_pent.lineTo(digi_s5_pts[6], digi_s5_pts[7]);
      s_pent.lineTo(digi_s5_pts[8], digi_s5_pts[9]);
      s_pent.lineTo(digi_s5_pts[10], digi_s5_pts[11]);
      s_pent.lineTo(digi_s5_pts[12], digi_s5_pts[13]);
      s_pent.lineTo(digi_s5_pts[14], digi_s5_pts[15]);
      s_pent.lineTo(digi_s5_pts[16], digi_s5_pts[17]);
      s_pent.lineTo(digi_s5_pts[18], digi_s5_pts[19]);

    }

  }

  /** user has touched finger to clock */
  @Override
  protected void notify_touched(MotionEvent e) {
    super.notify_touched(e);
    float x = e.getX(), y = e.getY();
    if (abs(digi_hx - x) < obj_w2) {
      if (abs(digi_hcy1 - y) < obj_w2 + cradius && (y < digi_hcy1 + obj_w2 / 2 + cradius)) {
        dragged_unit = TOUCHED_UNIT.HOUR3;
      } else if (abs(digi_hcy2 - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.HOURH;
      } else {
        dragged_unit = TOUCHED_UNIT.NONE;
      }
    } else if (abs(digi_mx - x) < obj_w2) {
      if (abs(digi_mscy1 - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.MIN3;
      } else if (abs(cy - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.MIN4;
      } else if (abs(digi_mscy3 - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.MIN5;
      } else {
        dragged_unit = TOUCHED_UNIT.NONE;
      }
    } else if (abs(digi_sx - x) < obj_w2) {
      if (abs(digi_mscy1 - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.SEC3;
      } else if (abs(cy - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.SEC4;
      } else if (abs(digi_mscy3 - y) < obj_w2 + cradius) {
        dragged_unit = TOUCHED_UNIT.SEC5;
      } else {
        dragged_unit = TOUCHED_UNIT.NONE;
      }
    } else {
      dragged_unit = TOUCHED_UNIT.NONE;
    }
    move_to(x, y);
  }

  private void move_to(float x, float y) {
    float x0, y0;
    switch (dragged_unit) {
      case HOUR3: x0 = digi_hx; y0 = digi_hcy1;  break;
      case HOURH: x0 = digi_hx; y0 = digi_hcy2;  break;
      case MIN3:  x0 = digi_mx; y0 = digi_mscy1; break;
      case MIN4:  x0 = digi_mx; y0 = cy;         break;
      case MIN5:  x0 = digi_mx; y0 = digi_mscy3; break;
      case SEC3:  x0 = digi_sx; y0 = digi_mscy1; break;
      case SEC4:  x0 = digi_sx; y0 = cy;         break;
      case SEC5:  x0 = digi_sx; y0 = digi_mscy3; break;
      default: x0 = cx; y0 = cy; break;
    }
    moved_offset = (float) atan2(y0 - y, x0 - x);
    moved_offset /= 2*PI;
    if (moved_offset < 0) moved_offset += 1;
    my_viewer.invalidate();
  }

  /**
   * user is dragging finger around the clock
   * <p>
   * The basic implementation does nothing.
   * Your drawer will want to override this, probably to determine the new position
   * of the unit being manipulated.
   */
  @Override
  protected void notify_dragged(MotionEvent e) {
    super.notify_dragged(e);
    move_to(e.getX(), e.getY());
  }

  /**
   * user has lifted finger off clock
   * <p>
   * The basic implementation sets dragged_unit to NONE.
   */
  @Override
  protected void notify_released(MotionEvent e) {
    int r;
    switch (dragged_unit) {
      case HOUR3:
        r = round(moved_offset * 3 - 0.5f);
        if (my_viewer.hour_modulus == 4) {
          hour = my_viewer.last_h = (r * 4 - (hour % 4) * 3) % 12;
          while (hour < 0) { hour += 12; my_viewer.last_h += 12; }
        } else {
          hour = my_viewer.last_h = (r * 16 - (hour % 8) * 15) % 24;
          while (hour < 0) { hour += 24; my_viewer.last_h += 24; }
        }
        break;
      case HOURH:
        if (my_viewer.hour_modulus == 4) {
          r = round(moved_offset * 4 - 1);
          hour = my_viewer.last_h = ( -r * 3 + (hour % 3) * 4 ) % 12;
          while (hour < 0) { hour += 12; my_viewer.last_h += 12; }
        } else {
          r = round(moved_offset * 8 - 2);
          hour = my_viewer.last_h = ( -r * 15 + (hour % 3) * 16 ) % 24;
          while (hour < 0) { hour += 24; my_viewer.last_h += 24; }
        }
        break;
      case MIN3:
        r = round(moved_offset * 3 - 0.5f);
        minute = my_viewer.last_m = ( -r * 20 + (minute % 20) * 21 ) % 60;
        while (minute < 0) { minute += 60; my_viewer.last_m += 60; }
        break;
      case MIN4:
        r = round(moved_offset * 4 - 1);
        minute = my_viewer.last_m = ( -r * 15 + (minute % 15) * 16 ) % 60;
        while (minute < 0) { minute += 60; my_viewer.last_m += 60; }
        break;
      case MIN5:
        r = round(moved_offset * 5 - 1);
        minute = my_viewer.last_m = ( -r * 24 + (minute % 12) * 25 ) % 60;
        while (minute < 0) { minute += 60; my_viewer.last_m += 60; }
        break;
      case SEC3:
        r = round(moved_offset * 3 - 0.5f);
        second = my_viewer.last_s = ( -r * 20 + (second % 20) * 21 ) % 60;
        while (second < 0) { second += 60; my_viewer.last_s += 60; }
        break;
      case SEC4:
        r = round(moved_offset * 4 - 1);
        second = my_viewer.last_s = ( -r * 15 + (second % 15) * 16 ) % 60;
        while (second < 0) { second += 60; my_viewer.last_s += 60; }
        break;
      case SEC5:
        r = round(moved_offset * 5 - 1);
        second = my_viewer.last_s = ( -r * 24 + (second % 12) * 25 ) % 60;
        while (second < 0) { second += 60; my_viewer.last_s += 60; }
        break;
      default: break;
    }
    moved_offset = 0;
    super.notify_released(e);
  }

  /** arrays that store points to draw the polygons */
  private float [] digi_h3_pts, digi_h4_pts, digi_h8_pts, digi_m3_pts, digi_m4_pts, digi_m5_pts,
          digi_s3_pts, digi_s4_pts, digi_s5_pts;

  /** how to paint the polygons */
  private Paint poly_paint;

  /** positions */
  private float cradius;

  /** fields related to positions of elements */
  private float digi_hx, digi_mx, digi_sx;
  private float digi_hcy1, digi_hcy2, digi_mscy1, digi_mscy3, obj_w2;
  private float moved_offset;

}
