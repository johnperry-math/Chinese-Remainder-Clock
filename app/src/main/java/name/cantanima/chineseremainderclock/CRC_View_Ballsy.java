package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Units.HOURS;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.HOUR3;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.HOURH;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.MIN3;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.MIN4;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.MIN5;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.SEC3;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.SEC4;
import static name.cantanima.chineseremainderclock.CRC_View_Ballsy.DRAGGED_BALL.SEC5;

/**
 * Created by cantanima on 6/5/17.
 */

public class CRC_View_Ballsy extends Clock_Drawer {

  // constructor
  public CRC_View_Ballsy(CRC_View owner) {
  
    initialize_fields(owner);
  
  }
  
  float preferred_step() { return step; }
  
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void draw(Canvas canvas) {

    setup_time();

    drawTimeAndRectangle(canvas, hour, minute, second, cx, cy, diam);

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

    if (dragging) {
      Log.d(tag, "adjusting for dragging: " + String.valueOf(lhmod3) + "," + String.valueOf(lhmod4) + ";"
            + String.valueOf(lmmod3) + "," + String.valueOf(lmmod4) + "," + String.valueOf(lmmod5) + ";"
            + String.valueOf(lsmod3) + "," + String.valueOf(lsmod4) + "," + String.valueOf(lsmod5));
      hmod3 = lhmod3; hmod4 = lhmod4;
      mmod3 = lmmod3; mmod4 = lmmod4; mmod5 = lmmod5;
      smod3 = lsmod3; smod4 = lsmod4; smod5 = lsmod5;
      Log.d(tag, "adjusted for dragging: " + String.valueOf(lhmod3) + "," + String.valueOf(lhmod4) + ";"
          + String.valueOf(lmmod3) + "," + String.valueOf(lmmod4) + "," + String.valueOf(lmmod5) + ";"
          + String.valueOf(lsmod3) + "," + String.valueOf(lsmod4) + "," + String.valueOf(lsmod5));
    } else if (just_released && dragging_which_ball != null) {
      Log.d(tag, "adjusting " + String.valueOf(dragging_which_ball));
      switch (dragging_which_ball) {
        case HOUR3: hmod3 = lhmod3 = last_mod; break;
        case HOURH: hmod4 = lhmod4 = last_mod; break;
        case MIN3 : mmod3 = lmmod3 = last_mod; break;
        case MIN4 : mmod4 = lmmod4 = last_mod; break;
        case MIN5 : mmod5 = lmmod5 = last_mod; break;
        case SEC3 : smod3 = lsmod3 = last_mod; break;
        case SEC4 : smod4 = lsmod4 = last_mod; break;
        case SEC5 : smod5 = lsmod5 = last_mod; break;
      }
      if (my_viewer.hour_modulus == 4)
        my_viewer.last_h = hour = (lhmod3 * 4 + lhmod4 * 9) % 12;
      else
        my_viewer.last_h = hour = (lhmod3 * 16 + lhmod4 * 9) % 24;
      my_viewer.last_m = minute = (lmmod3 * 40 + lmmod4 * 45 + lmmod5 * 36) % 60;
      my_viewer.last_s = second = (lsmod3 * 40 + lsmod4 * 45 + lsmod5 * 36) % 60;
      switch (my_viewer.which_unit_to_modify) {
        case HOURS:
          my_viewer.valueEditor.setText(String.valueOf(hour));
          break;
        case MINUTES:
          my_viewer.valueEditor.setText(String.valueOf(minute));
          break;
        case SECONDS:
          my_viewer.valueEditor.setText(String.valueOf(second));
          break;
      }
      draw_time(hour, minute, second);
      Log.d(tag, "remainders: " + lhmod3 + "," + lhmod4 + ";" + lmmod3 + "," + lmmod4 + "," + lmmod5 + ";" + lsmod3 + "," + lsmod4 + "," + lsmod5);
      Log.d(tag, "saved time: " + String.valueOf(my_viewer.last_h) + ":" + String.valueOf(my_viewer.last_m) + ":" + String.valueOf(my_viewer.last_s));
    }

    // the analog version draws several concentric circles,
    // and moves differently-colored balls on those circles
    // to positions that reflect the remainder

    // make sure the ball doesn't move too far in the animation
    float ball_offset = (my_viewer.my_offset > 0.96) ? 1.0f : my_viewer.my_offset;

    // draw concentric circles for the main paths
    circle_paint.setColor(line_color);
    canvas.drawCircle(cx, cy, bally_hr3, circle_paint);
    canvas.drawCircle(cx, cy, bally_hr4, circle_paint);
    canvas.drawCircle(cx, cy, bally_mr3, circle_paint);
    canvas.drawCircle(cx, cy, bally_mr4, circle_paint);
    canvas.drawCircle(cx, cy, bally_mr5, circle_paint);
    if (reverse_orientation) {
      canvas.drawCircle(cx, cy, bally_sr3, circle_paint);
      canvas.drawCircle(cx, cy, bally_sr4, circle_paint);
      canvas.drawCircle(cx, cy, bally_sr5, circle_paint);
    }

    // draw hatch marks for positions modulo 3
    for (int i = 0; i < 3; ++i) {
      canvas.drawLine(
              (float) (cx + bally_hatch_hr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
              (float) (cy + bally_hatch_hr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
              (float) (cx + bally_hatch_h3_outer * cos(2 * PI / 3 * i - PI  / 2)),
              (float) (cy + bally_hatch_h3_outer * sin(2 * PI / 3 * i - PI  / 2)),
              circle_paint
      );
      canvas.drawLine(
              (float) (cx + bally_hatch_mr3_inner * cos(2 * PI / 3 * i - PI  / 2)),
              (float) (cy + bally_hatch_mr3_inner * sin(2 * PI / 3 * i - PI  / 2)),
              (float) (cx + bally_hatch_mr3_outer * cos(2 * PI / 3 * i - PI  / 2)),
              (float) (cy + bally_hatch_mr3_outer * sin(2 * PI / 3 * i - PI  / 2)),
              circle_paint
      );
    }

    // draw hatch marks for hour's second positions (modulo 4 or 8 as appropriate)
    for (int i = 0; i < my_viewer.hour_modulus; ++i) {
      canvas.drawLine(
          (float) (cx + bally_hatch_hr4_inner * cos(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
          (float) (cy + bally_hatch_hr4_inner * sin(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
          (float) (cx + bally_hatch_h4_outer * cos(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
          (float) (cy + bally_hatch_h4_outer * sin(2 * PI / my_viewer.hour_modulus * i - PI / 2)),
          circle_paint
      );
    }

    // draw hatch marks for minute's positions modulo 4
    for (int i = 0; i < 4; ++i) {
      canvas.drawLine(
        (float) (cx + bally_hatch_mr4_inner * cos(2 * PI / 4 * i - PI / 2)),
        (float) (cy + bally_hatch_mr4_inner * sin(2 * PI / 4 * i - PI / 2)),
        (float) (cx + bally_hatch_m4_outer * cos(2 * PI / 4 * i - PI / 2)),
        (float) (cy + bally_hatch_m4_outer * sin(2 * PI / 4 * i - PI / 2)),
        circle_paint
      );
    }

    // draw hatch marks for minute's positions modulo 5
    for (int i = 0; i < 5; ++i) {
      canvas.drawLine(
        (float) (cx + bally_hatch_mr5_inner * cos(2 * PI / 5 * i - PI / 2)),
        (float) (cy + bally_hatch_mr5_inner * sin(2 * PI / 5 * i - PI / 2)),
        (float) (cx + bally_hatch_m5_outer * cos(2 * PI / 5 * i - PI / 2)),
        (float) (cy + bally_hatch_m5_outer * sin(2 * PI / 5 * i - PI / 2)),
        circle_paint
      );
    }

    // determine the hour ball's position:
    // hangle3 determines the position for the inner ball (modulo 3)
    // and hangle4 determines the position for the outer ball (modulo 4 or 8)
    float hangle3, hangle4;

    if (my_viewer.last_h == hour) {
      hangle3 = (float) (2 * PI / 3 * hmod3 - PI / 2);
      hangle4 = (float) (2 * PI / my_viewer.hour_modulus * hmod4 - PI / 2);
    } else {
      if (my_viewer.time_guide == DECREMENT) {
        if (hmod3 == 3) hmod3 = 0;
        else if (lhmod3 == 0) lhmod3 = 3;
        if (hmod4 == my_viewer.hour_modulus) hmod4 = 0;
        else if (lhmod4 == 0) lhmod4 = my_viewer.hour_modulus;
      }
      hangle3 = (float) (2 * PI / 3 * (hmod3 * ball_offset + lhmod3 * (1 - ball_offset)) - PI / 2);
      hangle4 = (float) (2 * PI / my_viewer.hour_modulus * (hmod4 * ball_offset + lhmod4 * (1 - ball_offset)) - PI / 2);
    }

    // determine the minute ball's position:
    // manglei determines the position for the ball corresponding to the remainder modulo i
    float mangle3, mangle4, mangle5;
    if (my_viewer.last_m == minute) {
      mangle3 = (float) (2 * PI / 3 * mmod3 - PI / 2);
      mangle4 = (float) (2 * PI / 4 * mmod4 - PI / 2);
      mangle5 = (float) (2 * PI / 5 * mmod5 - PI / 2);
    } else {
      if (my_viewer.time_guide == DECREMENT) {
        if (mmod3 == 3) mmod3 = 0;
        else if (lmmod3 == 0) lmmod3 = 3;
        if (mmod4 == 4) mmod4 = 0;
        else if (lmmod4 == 0) lmmod4 = 4;
        if (mmod5 == 5) mmod5 = 0;
        else if (lmmod5 == 0) lmmod5 = 5;
      }
      mangle3 = (float) (2 * PI / 3 * (mmod3 * ball_offset + lmmod3 * (1 - ball_offset)) - PI / 2);
      mangle4 = (float) (2 * PI / 4 * (mmod4 * ball_offset + lmmod4 * (1 - ball_offset)) - PI / 2);
      mangle5 = (float) (2 * PI / 5 * (mmod5 * ball_offset + lmmod5 * (1 - ball_offset)) - PI / 2);
    }

    // determine the second ball's position:
    // sanglei determines the position for the ball corresponding to the remainder modulo i
    float sangle3, sangle4, sangle5;
    if (my_viewer.last_s == second) {
      sangle3 = (float) (2 * PI / 3 * smod3 - PI / 2);
      sangle4 = (float) (2 * PI / 4 * smod4 - PI / 2);
      sangle5 = (float) (2 * PI / 5 * smod5 - PI / 2);
    } else {
      if (my_viewer.time_guide == DECREMENT) {
        if (smod3 == 3) smod3 = 0;
        else if (lsmod3 == 0) lsmod3 = 3;
        if (smod4 == 4) smod4 = 0;
        else if (lsmod4 == 0) lsmod4 = 4;
        if (smod5 == 5) smod5 = 0;
        else if (lsmod5 == 0) lsmod5 = 5;
      }
      sangle3 = (float) (2 * PI / 3 * (smod3 * ball_offset + lsmod3 * (1 - ball_offset)) - PI / 2);
      sangle4 = (float) (2 * PI / 4 * (smod4 * ball_offset + lsmod4 * (1 - ball_offset)) - PI / 2);
      sangle5 = (float) (2 * PI / 5 * (smod5 * ball_offset + lsmod5 * (1 - ball_offset)) - PI / 2);
    }

    // if dragging, we need to modify stuff
    if (dragging) {
      switch (dragging_which_ball) {
        case HOUR3: hangle3 = new_angle; break;
        case HOURH: hangle4 = new_angle; break;
        case MIN3 : mangle3 = new_angle; break;
        case MIN4 : mangle4 = new_angle; break;
        case MIN5 : mangle5 = new_angle; break;
        case SEC3 : sangle3 = new_angle; break;
        case SEC4 : sangle4 = new_angle; break;
        case SEC5 : sangle5 = new_angle; break;
      }
      Log.d(tag, "angle " + String.valueOf(initial_angle) + " to " + String.valueOf(new_angle));
      Log.d(tag, String.valueOf(dragging_which_ball));
    }

    // draw the balls

    ball_paint.setStyle(FILL);

    if (dragging && dragging_which_ball == HOUR3) ball_paint.setColor(WHITE);
    else ball_paint.setColor(hour_color);
    float x = (float) (cx + bally_hr3 * cos(hangle3));
    float y = (float) (cy + bally_hr3 * sin(hangle3));
    canvas.drawCircle(x, y, bally_br, ball_paint);
    last_h3_x = x;
    last_h3_y = y;

    if (dragging && dragging_which_ball == HOURH) ball_paint.setColor(WHITE);
    else ball_paint.setColor(hour_color);
    x = (float) (cx + bally_hr4 * cos(hangle4));
    y = (float) (cy + bally_hr4 * sin(hangle4));
    canvas.drawCircle(x, y, bally_br, ball_paint);
    last_hh_x = x;
    last_hh_y = y;

    if (dragging && dragging_which_ball == MIN3) ball_paint.setColor(WHITE);
    else ball_paint.setColor(minute_color);
    x = (float) (cx + bally_mr3 * cos(mangle3));
    y = (float) (cy + bally_mr3 * sin(mangle3));
    canvas.drawCircle(x, y, bally_br, ball_paint);
    last_m3_x = x;
    last_m3_y = y;

    if (dragging && dragging_which_ball == MIN4) ball_paint.setColor(WHITE);
    else ball_paint.setColor(minute_color);
    x = (float) (cx + bally_mr4 * cos(mangle4));
    y = (float) (cy + bally_mr4 * sin(mangle4));
    canvas.drawCircle(x, y, bally_br, ball_paint);
    last_m4_x = x;
    last_m4_y = y;

    if (dragging && dragging_which_ball == MIN5) ball_paint.setColor(WHITE);
    else ball_paint.setColor(minute_color);
    x = (float) (cx + bally_mr5 * cos(mangle5));
    y = (float) (cy + bally_mr5 * sin(mangle5));
    canvas.drawCircle(x, y, bally_br, ball_paint);
    last_m5_x = x;
    last_m5_y = y;

    if (show_seconds) {

      if (dragging && dragging_which_ball == SEC3) ball_paint.setColor(WHITE);
      else ball_paint.setColor(second_color);
      x = (float) (cx + bally_sr3 * cos(sangle3));
      y = (float) (cy + bally_sr3 * sin(sangle3));
      canvas.drawCircle(x, y, bally_br, ball_paint);
      last_s3_x = x;
      last_s3_y = y;

      if (dragging && dragging_which_ball == SEC4) ball_paint.setColor(WHITE);
      else ball_paint.setColor(second_color);
      x = (float) (cx + bally_sr4 * cos(sangle4));
      y = (float) (cy + bally_sr4 * sin(sangle4));
      canvas.drawCircle(x, y, bally_br, ball_paint);
      last_s4_x = x;
      last_s4_y = y;

      if (dragging && dragging_which_ball == SEC5) ball_paint.setColor(WHITE);
      else ball_paint.setColor(second_color);
      x = (float) (cx + bally_sr5 * cos(sangle5));
      y = (float) (cy + bally_sr5 * sin(sangle5));
      canvas.drawCircle(x, y, bally_br, ball_paint);
      last_s5_x = x;
      last_s5_y = y;

    }

    just_released = false;
    usual_cleanup();
  
  }

  @Override
  protected void notify_touched(MotionEvent e) {
    
    boolean searching = true;
    
    float x = e.getX();
    float y = e.getY();

    if (
        x > last_h3_x - bally_br && x < last_h3_x + bally_br &&
            y > last_h3_y - bally_br && y < last_h3_y + bally_br
        ) {
      dragging_which_ball = HOUR3;
      searching = false;
    } else if (
        x > last_hh_x - bally_br && x < last_hh_x + bally_br &&
            y > last_hh_y - bally_br && y < last_hh_y + bally_br
        ) {
      dragging_which_ball = HOURH;
      searching = false;
    } else if (
        x > last_m3_x - bally_br && x < last_m3_x + bally_br &&
        y > last_m3_y - bally_br && y < last_m3_y + bally_br
    ) {
      dragging_which_ball = MIN3;
      searching = false;
    } else if (
        x > last_m4_x - bally_br && x < last_m4_x + bally_br &&
            y > last_m4_y - bally_br && y < last_m4_y + bally_br
        ) {
      dragging_which_ball = MIN4;
      searching = false;
    } else if (
        x > last_m5_x - bally_br && x < last_m5_x + bally_br &&
            y > last_m5_y - bally_br && y < last_m5_y + bally_br
        ) {
      dragging_which_ball = MIN5;
      searching = false;
    } else if (
        x > last_s3_x - bally_br && x < last_s3_x + bally_br &&
            y > last_s3_y - bally_br && y < last_s3_y + bally_br
        ) {
      dragging_which_ball = SEC3;
      searching = false;
    } else if (
        x > last_s4_x - bally_br && x < last_s4_x + bally_br &&
            y > last_s4_y - bally_br && y < last_s4_y + bally_br
        ) {
      dragging_which_ball = SEC4;
      searching = false;
    } else if (
        x > last_s5_x - bally_br && x < last_s5_x + bally_br &&
            y > last_s5_y - bally_br && y < last_s5_y + bally_br
        ) {
      dragging_which_ball = SEC5;
      searching = false;
    }

    if (!searching) {
      dragging = true;
      initial_angle = (float) atan2(y - cy, x - cx);
      step = 0f;
    }

  }

  @Override
  protected void notify_dragged(MotionEvent e) {

    int new_millis = Calendar.getInstance().get(Calendar.MILLISECOND);
    if (new_millis - millis > 100 || millis - new_millis > 0) {
      float x = e.getX();
      float y = e.getY();
      new_angle = (float) atan2(y - cy, x - cx);
      my_viewer.invalidate();
    }

  }

  @Override
  protected void notify_released(MotionEvent e) {
    if (dragging) {
      dragging = false;
      step = 0.04f;
      if (dragging_which_ball != null) {
        switch (dragging_which_ball) {
          case HOUR3:
          case MIN3:
          case SEC3:
            last_mod = (int) round(3f / (2f * PI) * new_angle + 3f / 4f);
            if (last_mod < 0) last_mod += 3;
            break;
          case HOURH:
            if (my_viewer.hour_modulus == 4) {
              last_mod = (int) round(2f / PI * new_angle + 1f);
              if (last_mod < 0) last_mod += 4;
            } else {
              last_mod = (int) round(4f / PI * new_angle + 2f);
              if (last_mod < 0) last_mod += 8;
            }
            break;
          case MIN4:
          case SEC4:
            last_mod = (int) round(2f / PI * new_angle + 1f);
            if (last_mod < 0) last_mod += 4;
            break;
          case MIN5:
          case SEC5:
            last_mod = (int) round(5f / (2f * PI) * new_angle + 5f / 4f);
            if (last_mod < 0) last_mod += 5;
            break;
        }
      }
      Log.d(tag, "last mod: " + String.valueOf(last_mod));
      just_released = true;
      my_viewer.invalidate();
    }
  }

  // this should be needed only once in the lifetime of the View,
  // but this can be many times because the View is re-constructed
  // every time the orientation changes
  void recalculate_positions() {
  
    super.recalculate_positions();

    if (reverse_orientation) {

      if (show_seconds) {
        bally_sr3 = diam / 9f;
        bally_sr4 = diam / 9f * 2f;
        bally_sr5 = diam / 9f * 3f;
        bally_mr3 = diam / 9f * 4f;
        bally_mr4 = diam / 9f * 5f;
        bally_mr5 = diam / 9f * 6f;
        bally_hr3 = diam / 9f * 7f;
        bally_hr4 = diam / 9f * 8f;
        bally_br = diam / 28;
      } else {
        bally_mr3 = diam / 6f;
        bally_mr4 = diam / 6f * 2f;
        bally_mr5 = diam / 6f * 3f;
        bally_hr3 = diam / 6f * 4f;
        bally_hr4 = diam / 6f * 5f;
        bally_br = diam / 20;
      }

    } else {

      if (show_seconds) {
        bally_hr3 = diam / 6.0f;
        bally_hr4 = diam / 3.0f;
        bally_mr3 = diam / 2.0f;
        bally_mr4 = diam * 2.0f / 3.0f;
        bally_mr5 = diam * 5.0f / 6.0f;
        bally_sr3 = diam / 12.0f * 5.0f;
        bally_sr4 = diam / 12.0f * 7.0f;
        bally_sr5 = diam / 12.0f * 9.0f;
        bally_br = diam / 28;
      } else {
        bally_hr3 = diam / 6f;
        bally_hr4 = diam / 6f * 2f;
        bally_mr3 = diam / 6f * 3f;
        bally_mr4 = diam / 6f * 4f;
        bally_mr5 = diam / 6f * 5f;
        bally_br = diam / 20;
      }

    }

    bally_hatch_dist = diam / 60;
    bally_hatch_hr3_inner = bally_hr3 - bally_hatch_dist;
    bally_hatch_h3_outer = bally_hr3 + bally_hatch_dist;
    bally_hatch_hr4_inner = bally_hr4 - bally_hatch_dist;
    bally_hatch_h4_outer = bally_hr4 + bally_hatch_dist;
    bally_hatch_mr3_inner = bally_mr3 - bally_hatch_dist;
    bally_hatch_mr3_outer = bally_mr3 + bally_hatch_dist;
    bally_hatch_mr4_inner = bally_mr4 - bally_hatch_dist;
    bally_hatch_m4_outer = bally_mr4 + bally_hatch_dist;
    bally_hatch_mr5_inner = bally_mr5 - bally_hatch_dist;
    bally_hatch_m5_outer = bally_mr5 + bally_hatch_dist;
  
  }
  
  // fields that control layout of analog clock elements
  protected float bally_hr3, bally_hr4, bally_mr3, bally_mr4, bally_mr5, bally_sr3, bally_sr4, bally_sr5,
          bally_br, bally_hatch_dist, bally_hatch_hr3_inner, bally_hatch_h3_outer,
          bally_hatch_hr4_inner, bally_hatch_h4_outer, bally_hatch_mr3_inner,
          bally_hatch_mr3_outer, bally_hatch_mr4_inner, bally_hatch_m4_outer,
          bally_hatch_mr5_inner, bally_hatch_m5_outer;

  // fields that control dragging of balls
  protected boolean dragging = false;
  protected boolean just_released = false;
  protected enum DRAGGED_BALL { HOUR3, HOURH, MIN3, MIN4, MIN5, SEC3, SEC4, SEC5 };
  protected DRAGGED_BALL dragging_which_ball;
  protected float initial_angle, new_angle;
  protected int last_mod, saved_time;
  
  protected float last_h3_x, last_h3_y, last_hh_x, last_hh_y,
                  last_m3_x, last_m3_y, last_m4_x, last_m4_y, last_m5_x, last_m5_y,
                  last_s3_x, last_s3_y, last_s4_x, last_s4_y, last_s5_x, last_s5_y;

  protected float step = 0.04f;

  final protected static String tag = "Bally";
}
