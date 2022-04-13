package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static java.lang.Math.round;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.NONE;

/**
 * This class extends Clock_Drawer for the Shady design,
 * a polygonal design that varies the shade of a polygon in a manner corresponding to the remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Shady works.
 * @see Clock_Drawer
 */
public class CRC_View_Shady extends CRC_View_Polygonal {

  CRC_View_Shady(CRC_View owner) {

    // we set up the paint for the text here, since that is invariant
    initialize_fields(owner);
    digi_paint = new Paint(ANTI_ALIAS_FLAG);
    digi_paint.setColor(WHITE);
    digi_paint.setTextAlign(CENTER);

    stringID = R.string.shady_manual_hint;

  }

  /** the step is 1/10 of a second */
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

    switch (dragged_unit) {
      case HOUR3:
        lhmod3 = hmod3 = last_step;
        break;
      case HOURH:
        lhmod4 = hmod4 = last_step;
        break;
      case MIN3:
        lmmod3 = mmod3 = last_step;
        break;
      case MIN4:
        lmmod4 = mmod4 = last_step;
        break;
      case MIN5:
        lmmod5 = mmod5 = last_step;
        break;
      case SEC3:
        lsmod3 = smod3 = last_step;
        break;
      case SEC4:
        lsmod4 = smod4 = last_step;
        break;
      case SEC5:
        lsmod5 = smod5 = last_step;
        break;
      default: break;
    }

    // in digital, we draw and fill polygons
    // the fill's transparency varies according to the remainder:
    // the closer the remainder is to the modulus, the more opaque the fill

    // short_hand, modulo 3

    ball_paint.setColor(hour_color);
    ball_paint.setStyle(FILL);
    if (my_viewer.last_h != hour) {
      ball_paint.setAlpha((int) (
          (1 - my_viewer.my_offset)*63*((lhmod3 % 3) + 1) + my_viewer.my_offset*63*((hmod3 % 3) + 1))
      );
    } else {
      ball_paint.setAlpha(63 * ((hmod3 % 3) + 1));
    }
    canvas.drawPath(h_tria, ball_paint);
    if (hmod3 == 3)
      canvas.drawText(zero_str, digi_hx, digi_hty1, digi_paint);
    else
      canvas.drawText(String.valueOf(hmod3), digi_hx, digi_hty1, digi_paint);

    // short_hand, modulo 4 or 8, depending on the kind of time

    if (my_viewer.hour_modulus == 4) {

      if (my_viewer.last_h != hour) {
        ball_paint.setAlpha((int) (
                (1 - my_viewer.my_offset)*51*((lhmod4 % 4) + 1) + my_viewer.my_offset*51*((hmod4 % 4) + 1))
        );
      } else {
        ball_paint.setAlpha(51 * ((hmod4 % 4) + 1));
      }
      canvas.drawPath(h_quad, ball_paint);
      if (hmod4 == 4)
        canvas.drawText(zero_str, digi_hx, digi_hty2, digi_paint);
      else
        canvas.drawText(String.valueOf(hmod4), digi_hx, digi_hty2, digi_paint);

    } else { // short_hand modulo 8

      if (my_viewer.last_h != hour) {
        ball_paint.setAlpha((int) (
            (1 - my_viewer.my_offset)*27*((lhmod4 % 8) + 1) + my_viewer.my_offset*27*((hmod4 % 8) + 1))
        );
      } else {
        ball_paint.setAlpha(27 * ((hmod4 % 8) + 1));
      }
      canvas.drawPath(h_octo, ball_paint);
      if (hmod4 == 8)
        canvas.drawText(zero_str, digi_hx, digi_hty2, digi_paint);
      else
        canvas.drawText(String.valueOf(hmod4), digi_hx, digi_hty2, digi_paint);

    }

    // minute, modulo 3

    ball_paint.setColor(minute_color);
    if (my_viewer.last_m != minute) {
      ball_paint.setAlpha((int) (
          (1 - my_viewer.my_offset)*63*((lmmod3 % 3) + 1) + my_viewer.my_offset*63*((mmod3 % 3) + 1))
      );
    } else {
      ball_paint.setAlpha(63 * ((mmod3 % 3) + 1));
    }
    canvas.drawPath(m_tria, ball_paint);
    if (mmod3 == 3)
      canvas.drawText(zero_str, digi_mx, digi_msty1, digi_paint);
    else
      canvas.drawText(String.valueOf(mmod3), digi_mx, digi_msty1, digi_paint);

    // minute, modulo 4

    if (my_viewer.last_m != minute) {
      ball_paint.setAlpha((int) (
          (1 - my_viewer.my_offset)*51*((lmmod4 % 4) + 1) + my_viewer.my_offset*51*((mmod4 % 4) + 1))
      );
    } else {
      ball_paint.setAlpha(51 * ((mmod4 % 4) + 1));
    }
    canvas.drawPath(m_quad, ball_paint);
    if (mmod4 == 4)
      canvas.drawText(zero_str, digi_mx, digi_ty, digi_paint);
    else
      canvas.drawText(String.valueOf(mmod4), digi_mx, digi_ty, digi_paint);

    // minute, modulo 5

    if (my_viewer.last_m != minute) {
      ball_paint.setAlpha((int) (
          (1 - my_viewer.my_offset)*45*((lmmod5 % 5) + 1) + my_viewer.my_offset*45*((mmod5 % 5) + 1))
      );
    } else {
      ball_paint.setAlpha(45 * ((mmod5 % 5) + 1));
    }
    canvas.drawPath(m_pent, ball_paint);
    if (mmod5 == 5)
      canvas.drawText(zero_str, digi_mx, digi_msty3, digi_paint);
    else
      canvas.drawText(String.valueOf(mmod5), digi_mx, digi_msty3, digi_paint);

    if (show_seconds) {

      // second, modulo 3

      ball_paint.setColor(second_color);
      if (my_viewer.last_s != second) {
        ball_paint.setAlpha((int) (
            (1 - my_viewer.my_offset) * 63 * ((lsmod3 % 3) + 1) + my_viewer.my_offset * 63 * ((smod3 % 3) + 1))
        );
      } else {
        ball_paint.setAlpha(63 * ((smod3 % 3) + 1));
      }
      canvas.drawPath(s_tria, ball_paint);
      if (smod3 == 3)
        canvas.drawText(zero_str, digi_sx, digi_msty1, digi_paint);
      else
        canvas.drawText(String.valueOf(smod3), digi_sx, digi_msty1, digi_paint);

      // second, modulo 4

      if (my_viewer.last_s != second) {
        ball_paint.setAlpha((int) (
            (1 - my_viewer.my_offset) * 51 * ((lsmod4 % 4) + 1) + my_viewer.my_offset * 51* ((smod4 % 4) + 1))
        );
      } else {
        ball_paint.setAlpha(51 * ((smod4 % 4) + 1));
      }
      canvas.drawPath(s_quad, ball_paint);
      if (smod4 == 4)
        canvas.drawText(zero_str, digi_sx, digi_ty, digi_paint);
      else
        canvas.drawText(String.valueOf(smod4), digi_sx, digi_ty, digi_paint);

      // second, modulo 5

      if (my_viewer.last_s != second) {
        ball_paint.setAlpha((int) (
                (1 - my_viewer.my_offset) * 45 * ((lsmod5 % 5) + 1) + my_viewer.my_offset * 45 * ((smod5 % 5) + 1))
        );
      } else {
        ball_paint.setAlpha(45 * ((smod5 % 5) + 1));
      }
      canvas.drawPath(s_pent, ball_paint);
      if (smod5 == 5)
        canvas.drawText(zero_str, digi_sx, digi_msty3, digi_paint);
      else
        canvas.drawText(String.valueOf(smod5), digi_sx, digi_msty3, digi_paint);

    }

    usual_cleanup();

  }

  @Override
  public void recalculate_positions() {

    super.recalculate_positions();

    digi_paint.setTextSize(obj_w2 * 0.75f);
    Paint.FontMetrics fm = digi_paint.getFontMetrics();
    float digi_t_adjust = -fm.ascent;
    digi_hty1 = digi_hcy1 + digi_t_adjust / 2.5f;
    digi_hty2 = digi_hcy2 + digi_t_adjust / 2.5f;
    digi_ty = cy + digi_t_adjust / 2.5f;
    digi_msty1 = digi_mscy1 + digi_t_adjust / 2.5f;
    digi_msty3 = digi_mscy3 + digi_t_adjust / 2.5f;

  }

  @Override
  public void move_to(float x, float y) {
    int mod = 0;
    float y0 = 0, ystep;
    switch (dragged_unit) {
      case HOUR3:
        y0 = digi_hcy1;
        mod = 3;
        break;
      case HOURH:
        y0 = digi_hcy2;
        mod = my_viewer.hour_modulus;
        break;
      case MIN3: case SEC3:
        y0 = digi_mscy1;
        mod = 3;
        break;
      case MIN4: case SEC4:
        y0 = cy;
        mod = 4;
        break;
      case MIN5: case SEC5:
        y0 = digi_mscy3;
        mod = 5;
        break;
      default: break;
    }
    if (mod != 0) {
      ystep = ((float) my_viewer.getHeight()) / mod;
      final int delta = round((y - y0) / ystep);
      if ((original_value + delta) % mod != last_step) {
        redraw = true;
        last_step = (original_value + delta) % mod;
        if (last_step < 0) last_step += mod;
      }
    }
  }

  @Override
  protected void notify_released(@NotNull MotionEvent e) {
    switch (dragged_unit) {
      case HOUR3:
        if (my_viewer.hour_modulus == 4) {
          hour = (last_step * 4 - hour % 4 * 3) % 12;
          while (hour < 0) hour += 12;
        } else {
          hour = (last_step * 16 - hour % 8 * 15) % 24;
          while (hour < 0) hour += 24;
        }
        my_viewer.last_h = hour;
        break;
      case HOURH:
        if (my_viewer.hour_modulus == 4) {
          hour = (-last_step * 3 + hour % 3 * 4) % 12;
          while (hour < 0) hour += 12;
        } else {
          hour = (-last_step * 15 + hour % 3 * 16) % 24;
          while (hour < 0) hour += 24;
        }
        my_viewer.last_h = hour;
        break;
      case MIN3:
        minute = (-last_step * 20 + minute % 20 * 21) % 60;
        while (minute < 0) minute += 60;
        my_viewer.last_m = minute;
        break;
      case MIN4:
        minute = (-last_step * 15 + minute % 15 * 16) % 60;
        while (minute < 0) minute += 60;
        my_viewer.last_m = minute;
        break;
      case MIN5:
        minute = (-last_step * 24 + minute % 12 * 25) % 60;
        while (minute < 0) minute += 60;
        my_viewer.last_m = minute;
        break;
      case SEC3:
        second = (-last_step * 20 + second % 20 * 21) % 60;
        while (second < 0) second += 60;
        my_viewer.last_s = second;
        break;
      case SEC4:
        second = (-last_step * 15 + second % 15 * 16) % 60;
        while (second < 0) second += 60;
        my_viewer.last_s = second;
        break;
      case SEC5:
        second = (-last_step * 24 + second % 12 * 25) % 60;
        while (second < 0) second += 60;
        my_viewer.last_s = second;
        break;
    }
    dragged_unit = NONE;
    last_step = 0;
    my_viewer.invalidate();
  }

  @Override
  protected void notify_touched(@NotNull MotionEvent e) {
    super.notify_touched(e);
    switch (dragged_unit) {
      case HOUR3: last_step = original_value = my_viewer.last_h % 3; break;
      case HOURH: last_step = original_value = my_viewer.last_h % my_viewer.hour_modulus; break;
      case MIN3:  last_step = original_value = my_viewer.last_m % 3; break;
      case MIN4:  last_step = original_value = my_viewer.last_m % 4; break;
      case MIN5:  last_step = original_value = my_viewer.last_m % 5; break;
      case SEC3:  last_step = original_value = my_viewer.last_s % 3; break;
      case SEC4:  last_step = original_value = my_viewer.last_s % 4; break;
      case SEC5:  last_step = original_value = my_viewer.last_s % 5; break;
    }
  }

  @Override
  protected void notify_dragged(@NotNull MotionEvent e) {
    super.notify_dragged(e);
    if (redraw) {
      redraw = false;
      my_viewer.invalidate();
    }
  }

  /** used to paint the remainders in the polygons */
  private final Paint digi_paint;

  /** fields that control layout of digital clock elements (except the polygons) */
  private float digi_ty, digi_hty1, digi_hty2,
      digi_msty1, digi_msty3;

  /** last saved step (valid only while dragging) */
  private int last_step = 0, original_value = 0;

  /** whether a redraw is needed in manual mode */
  private boolean redraw = false;

}
