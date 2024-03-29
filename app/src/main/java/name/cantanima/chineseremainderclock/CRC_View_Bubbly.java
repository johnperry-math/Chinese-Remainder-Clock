package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Path;
import android.view.MotionEvent;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;

/*
 * Created by cantanima on 6/6/17.
 */

/**
 * This class extends Clock_Drawer for the Bubbly design,
 * a polygonal design that highlights the number of balls corresponding to the correct remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Bubbly works.
 * @see Clock_Drawer
 */
public class CRC_View_Bubbly extends Clock_Drawer {

  // constructor
  CRC_View_Bubbly(CRC_View owner) {
    initialize_fields(owner);
    stringID = R.string.bubbly_manual_hint;
  }

  @Override
  void draw(Canvas canvas) {

    // default setup

    if (!manual_mode) {
      setup_time();
      drawTimeAndRectangle(canvas, hour, minute, second, diam);
    } else {
      drawTimeAndRectangle(canvas, my_viewer.last_h, my_viewer.last_m, my_viewer.last_s, diam);
    }

    // draw the hours
    // the paths are already set up, so it's basically a question of how many bubbles to fill in
    // for any particular remainder, we do the following:
    // 1) draw the polygon hint as to the kind of remainder (triangle for 3-remainder, etc.)
    // 2) if the last short_hand is the same as the current short_hand, shade corresponding number of bubbles
    // 3) otherwise,
    //    a) if the current short_hand corresponds to the modulus, fade (out) all the balls
    //    b) otherwise, increase the ball's shade (fade in)
    // 4) draw a circle (necessary in case the ball isn't shaded)

    // hours
    ball_paint.setColor(hour_color);
    ball_paint.setStyle(FILL);
    canvas.drawPath(h_tria, ball_paint);
    int hmod3;
    if (dragged_unit != TOUCHED_UNIT.NONE) hmod3 = dragged_h3;
    else hmod3 = (hour % 3) == 0 ? 3 : hour % 3;
    for (int i = 0; i < 2; ++i) {
      ball_paint.setStyle(FILL);
      if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_h == hour) {
        if (i + 1 <= hour % 3) {
          canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
      } else {
        if (hmod3 == 3) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < hmod3 || (i + 1 == hmod3 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == hmod3) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          }
        }
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(STROKE);
      canvas.drawCircle(h_x3, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
    }
    ball_paint.setStyle(FILL);
    if (my_viewer.hour_modulus == 4) {
      canvas.drawPath(h_quad, ball_paint);
      int hmod4;
      if (dragged_unit != TOUCHED_UNIT.NONE) hmod4 = dragged_hh;
      else hmod4 = (hour % 4) == 0 ? 4 : hour % 4;
      for (int i = 0; i < 3; ++i) {
        ball_paint.setStyle(FILL);
        if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_h == hour) {
          if (i + 1 <= hour % 4) {
            canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          }
        } else {
          if (hmod4 == 4) {
            ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
            canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          } else {
            if (i + 1 < hmod4 || (i + 1 == hmod4 && my_viewer.my_offset > 0.9)) {
              ball_paint.setAlpha(255);
              canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
            } else if (i + 1 == hmod4) {
              ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
              canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
            }
          }
        }
        ball_paint.setAlpha(255);
        ball_paint.setStyle(STROKE);
        canvas.drawCircle(h_xh, h_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
      }
    } else {
      canvas.drawPath(h_octo, ball_paint);
      int hmod8;
      if (dragged_unit != TOUCHED_UNIT.NONE) hmod8 = dragged_hh;
      else hmod8 = (hour % 8) == 0 ? 8 : hour % 8;
      float r = cradius / 1.1f;
      for (int i = 0; i < 6; ++i) {
        float x = h_xh - r;
        ball_paint.setStyle(FILL);
        if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_h == hour) {
          if (i + 1 <= hour % 8)
            canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - ((float)i / 2) * cstep, r/2f, ball_paint);
        } else {
          if (hmod8 == 8) {
            ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
            canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - ((float)i / 2) * cstep, r/2f, ball_paint);
          } else {
            if (i + 1 < hmod8 || (i + 1 == hmod8 && my_viewer.my_offset > 0.9)) {
              ball_paint.setAlpha(255);
              canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - ((float)i / 2) * cstep, r/2f, ball_paint);
            } else if (i + 1 == hmod8) {
              ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
              canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - ((float)i / 2) * cstep, r/2f, ball_paint);
            }
          }
        }
        ball_paint.setAlpha(255);
        ball_paint.setStyle(STROKE);
        canvas.drawCircle(x + 2*r*(i % 2), h_y - vstep*0.85f - ((float)i / 2) * cstep, r/2f, ball_paint);
      }
      ball_paint.setStyle(FILL);
      if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_h == hour) {
        if (6 < hour % 8)
          canvas.drawCircle(h_xh, h_y - vstep * 0.85f - 3 * cstep, r / 2f, ball_paint);
      } else {
        if (hmod8 == 8) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
        } else {
          if (7 == hmod8 && my_viewer.my_offset > 0.9) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
          } else if (7 == hmod8) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
          }
        }
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(STROKE);
      canvas.drawCircle(h_xh, h_y - vstep*0.85f - 3*cstep, r/2f, ball_paint);
    }

    // minutes
    ball_paint.setColor(minute_color);
    ball_paint.setStyle(FILL);
    canvas.drawPath(m_tria, ball_paint);
    int mmod3;
    if (dragged_unit != TOUCHED_UNIT.NONE) mmod3 = dragged_m3;
    else mmod3 = (minute % 3) == 0 ? 3 : minute % 3;
    for (int i = 0; i < 2; ++i) {
      ball_paint.setStyle(FILL);
      if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_m == minute) {
        if (i + 1 <= minute % 3) {
          canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
      } else {
        if (mmod3 == 3) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < mmod3 || (i + 1 == mmod3 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == mmod3) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          }
        }
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(STROKE);
      canvas.drawCircle(m_x3, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
    }
    ball_paint.setAlpha(255); ball_paint.setStyle(FILL);
    canvas.drawPath(m_quad, ball_paint);
    int mmod4;
    if (dragged_unit != TOUCHED_UNIT.NONE) mmod4 = dragged_m4;
    else mmod4 = (minute % 4) == 0 ? 4 : minute % 4;
    for (int i = 0; i < 3; ++i) {
      ball_paint.setStyle(FILL);
      if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_m == minute) {
        if (i + 1 <= minute % 4) {
          canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
      } else {
        if (mmod4 == 4) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < mmod4 || (i + 1 == mmod4 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == mmod4) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          }
        }
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(STROKE);
      canvas.drawCircle(m_x4, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
    }
    ball_paint.setStyle(FILL);
    canvas.drawPath(m_pent, ball_paint);
    int mmod5;
    if (dragged_unit != TOUCHED_UNIT.NONE) mmod5 = dragged_m5;
    else mmod5 = (minute % 5) == 0 ? 5 : minute % 5;
    for (int i = 0; i < 4; ++i) {
      ball_paint.setStyle(FILL);
      if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_m == minute) {
        if (i + 1 <= minute % 5) {
          canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        }
      } else {
        if (mmod5 == 5) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < mmod5 || (i + 1 == mmod5 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == mmod5) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
          }
        }
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(STROKE);
      canvas.drawCircle(m_x5, m_y - (vstep*0.85f + cstep * i), cradius, ball_paint);
    }

    if (show_seconds) {

      ball_paint.setColor(second_color);
      ball_paint.setStyle(FILL);
      canvas.drawPath(s_tria, ball_paint);
      int smod3;
      if (dragged_unit != TOUCHED_UNIT.NONE) smod3 = dragged_s3;
      else smod3 = (second % 3) == 0 ? 3 : second % 3;
      for (int i = 0; i < 2; ++i) {
        ball_paint.setStyle(FILL);
        if (smod3 == 3) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(s_x3, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < smod3 || (i + 1 == smod3 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(s_x3, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == smod3) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(s_x3, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          }
        }
        ball_paint.setAlpha(255);
        ball_paint.setStyle(STROKE);
        canvas.drawCircle(s_x3, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
      }
      ball_paint.setAlpha(255);
      ball_paint.setStyle(FILL);
      canvas.drawPath(s_quad, ball_paint);
      int smod4;
      if (dragged_unit != TOUCHED_UNIT.NONE) smod4 = dragged_s4;
      else smod4 = (second % 4) == 0 ? 4 : second % 4;
      for (int i = 0; i < 3; ++i) {
        ball_paint.setStyle(FILL);
        if (dragged_unit == TOUCHED_UNIT.NONE && my_viewer.last_s == second) {
          if (i + 1 <= second % 4) {
            canvas.drawCircle(s_x4, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          }
        } else {
          if (smod4 == 4) {
            ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
            canvas.drawCircle(s_x4, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          } else {
            if (i + 1 < smod4 || (i + 1 == smod4 && my_viewer.my_offset > 0.9)) {
              ball_paint.setAlpha(255);
              canvas.drawCircle(s_x4, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
            } else if (i + 1 == smod4) {
              ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
              canvas.drawCircle(s_x4, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
            }
          }
        }
        ball_paint.setAlpha(255);
        ball_paint.setStyle(STROKE);
        canvas.drawCircle(s_x4, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
      }
      ball_paint.setStyle(FILL);
      canvas.drawPath(s_pent, ball_paint);
      int smod5;
      if (dragged_unit != TOUCHED_UNIT.NONE) smod5 = dragged_s5;
      else smod5 = (second % 5) == 0 ? 5 : second % 5;
      for (int i = 0; i < 4; ++i) {
        ball_paint.setStyle(FILL);
        if (smod5 == 5) {
          ball_paint.setAlpha(max(0, (int) ((1 - my_viewer.my_offset) * 255)));
          canvas.drawCircle(s_x5, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
        } else {
          if (i + 1 < smod5 || (i + 1 == smod5 && my_viewer.my_offset > 0.9)) {
            ball_paint.setAlpha(255);
            canvas.drawCircle(s_x5, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          } else if (i + 1 == smod5) {
            ball_paint.setAlpha((int) (my_viewer.my_offset * 255));
            canvas.drawCircle(s_x5, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
          }
        }
        ball_paint.setAlpha(255);
        ball_paint.setStyle(STROKE);
        canvas.drawCircle(s_x5, s_y - (vstep * 0.85f + cstep * i), cradius, ball_paint);
      }

    }

    usual_cleanup();

  }

  /**
   *
   * Calculates positions of the polygons and bubbles.
   * The polygons are set up as paths, so as to ease their drawing later.
   *
   */
  @Override
  void recalculate_positions() {

    float radius;

    super.recalculate_positions();

    // find appropriate locations
    if (show_seconds) {

      hstep = w / 8f;
      vstep = h / 11f;
      radius = min(hstep, vstep) / 2.25f;
      cradius = radius * 0.7f;
      cstep = cradius * 2.35f;

      h_y = m_y = 5f * vstep;
      s_y = 9 * vstep;
      if (reverse_orientation) {
        h_x3 = 5.25f * hstep;
        h_xh = 6.25f * hstep;
        m_x3 = 1.75f * hstep;
        m_x4 = 2.75f * hstep;
        m_x5 = 3.75f * hstep;
      } else {
        h_x3 = 1.75f * hstep;
        h_xh = 2.75f * hstep;
        m_x3 = 4.25f * hstep;
        m_x4 = 5.25f * hstep;
        m_x5 = 6.25f * hstep;
      }
      s_x3 = 3f * hstep;
      s_x4 = 4f * hstep;
      s_x5 = 5f * hstep;

    } else {

      hstep = w / 6f;
      vstep = h / 7f;
      radius = min(hstep, vstep) / 2.25f;
      cradius = radius * 0.7f;
      cstep = cradius * 2.35f;

      h_y = m_y = cy + 3 * radius;
      if (reverse_orientation) {
        m_x3 = 0.75f * hstep;
        m_x4 = 1.75f * hstep;
        m_x5 = 2.75f * hstep;
        h_x3 = 4.25f * hstep;
        h_xh = 5.25f * hstep;
      } else {
        h_x3 = 0.75f * hstep;
        h_xh = 1.75f * hstep;
        m_x3 = 3.25f * hstep;
        m_x4 = 4.25f * hstep;
        m_x5 = 5.25f * hstep;
      }

    }

    // set up the paths for the polygons
    // most are set up according to strict trigonometry,
    // but for alignment purposes triangles are somewhat offset

    h_tria = new Path();
    h_tria.moveTo(h_x3 - radius*1.15f * (float) cos(9 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
    h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(5 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(5 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    h_tria.lineTo(h_x3 - radius*1.15f * (float) cos(9 * PI / 6), h_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));

    h_quad = new Path();
    h_quad.moveTo(h_xh, h_y - radius);
    h_quad.lineTo(h_xh + radius, h_y);
    h_quad.lineTo(h_xh + radius, h_y);
    h_quad.lineTo(h_xh, h_y + radius);
    h_quad.lineTo(h_xh, h_y + radius);
    h_quad.lineTo(h_xh - radius, h_y);
    h_quad.lineTo(h_xh - radius, h_y);
    h_quad.lineTo(h_xh, h_y - radius);

    h_octo = new Path();
    h_octo.moveTo(h_xh - radius * (float) cos(12 * PI / 8), h_y + radius * (float) sin(12 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(10 * PI / 8), h_y + radius * (float) sin(10 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(10 * PI / 8), h_y + radius * (float) sin(10 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(8 * PI / 8), h_y + radius * (float) sin(8 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(8 * PI / 8), h_y + radius * (float) sin(8 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(6 * PI / 8), h_y + radius * (float) sin(6 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(6 * PI / 8), h_y + radius * (float) sin(6 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(4 * PI / 8), h_y + radius * (float) sin(4 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(4 * PI / 8), h_y + radius * (float) sin(4 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(2 * PI / 8), h_y + radius * (float) sin(2 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(2 * PI / 8), h_y + radius * (float) sin(2 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(0 * PI / 8), h_y + radius * (float) sin(0 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(0 * PI / 8), h_y + radius * (float) sin(0 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(14 * PI / 8), h_y + radius * (float) sin(14 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(14 * PI / 8), h_y + radius * (float) sin(14 * PI / 8));
    h_octo.lineTo(h_xh - radius * (float) cos(12 * PI / 8), h_y + radius * (float) sin(12 * PI / 8));

    m_tria = new Path();
    m_tria.moveTo(m_x3 - radius*1.15f * (float) cos(9 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
    m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(5 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(5 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    m_tria.lineTo(m_x3 - radius*1.15f * (float) cos(9 * PI / 6), m_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));

    m_quad = new Path();
    m_quad.moveTo(m_x4, m_y - radius);
    m_quad.lineTo(m_x4 + radius, m_y);
    m_quad.lineTo(m_x4 + radius, m_y);
    m_quad.lineTo(m_x4, m_y + radius);
    m_quad.lineTo(m_x4, m_y + radius);
    m_quad.lineTo(m_x4 - radius, m_y);
    m_quad.lineTo(m_x4 - radius, m_y);
    m_quad.lineTo(m_x4, m_y - radius);

    m_pent = new Path();
    m_pent.moveTo(m_x5 - radius * (float) cos(15 * PI / 10), m_y + radius * (float) sin(15 *  PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(11 * PI / 10), m_y + radius * (float) sin(11 *  PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(11 * PI / 10), m_y + radius * (float) sin(11 * PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(7 * PI / 10), m_y + radius * (float) sin(7 *  PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(7 * PI / 10), m_y + radius * (float) sin(7 * PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(3 * PI / 10), m_y + radius * (float) sin(3 * PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(3 * PI / 10), m_y + radius * (float) sin(3 *  PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(19 * PI / 10), m_y + radius * (float) sin(19 *  PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(19 * PI / 10), m_y + radius * (float) sin(19 * PI / 10));
    m_pent.lineTo(m_x5 - radius * (float) cos(15 * PI / 10), m_y + radius * (float) sin(15 * PI / 10));

    s_tria = new Path();
    s_tria.moveTo(s_x3 - radius*1.15f * (float) cos(9 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));
    s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(5 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(5 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(5 * PI / 6));
    s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(PI / 6));
    s_tria.lineTo(s_x3 - radius*1.15f * (float) cos(9 * PI / 6), s_y + radius*0.25f + radius*1.15f * (float) sin(9 * PI / 6));

    s_quad = new Path();
    s_quad.moveTo(s_x4, s_y - radius);
    s_quad.lineTo(s_x4 + radius, s_y);
    s_quad.lineTo(s_x4 + radius, s_y);
    s_quad.lineTo(s_x4, s_y + radius);
    s_quad.lineTo(s_x4, s_y + radius);
    s_quad.lineTo(s_x4 - radius, s_y);
    s_quad.lineTo(s_x4 - radius, s_y);
    s_quad.lineTo(s_x4, s_y - radius);

    s_pent = new Path();
    s_pent.moveTo(s_x5 - radius * (float) cos(15 * PI / 10), s_y + radius * (float) sin(15 *  PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(11 * PI / 10), s_y + radius * (float) sin(11 *  PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(11 * PI / 10), s_y + radius * (float) sin(11 * PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(7 * PI / 10), s_y + radius * (float) sin(7 *  PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(7 * PI / 10), s_y + radius * (float) sin(7 * PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(3 * PI / 10), s_y + radius * (float) sin(3 * PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(3 * PI / 10), s_y + radius * (float) sin(3 *  PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(19 * PI / 10), s_y + radius * (float) sin(19 *  PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(19 * PI / 10), s_y + radius * (float) sin(19 * PI / 10));
    s_pent.lineTo(s_x5 - radius * (float) cos(15 * PI / 10), s_y + radius * (float) sin(15 * PI / 10));

  }

  /**
   * user has touched finger to clock
   *
   * @param e information regarding the touch
   */
  @Override
  protected void notify_touched(MotionEvent e) {
    super.notify_touched(e);
    float x = e.getX(), y = e.getY();
    float radius = min(hstep, vstep) / 2.25f;

    if ((abs(h_x3 - x) < radius) && (h_y + radius - y > 0) && (h_y + radius - y < radius * 2 + cstep * 2))
      dragged_unit = TOUCHED_UNIT.HOUR3;
    else if ((abs(h_xh - x) < radius) && (h_y + radius - y > 0) && (h_y + radius - y < radius * 2 + cstep * 3))
      dragged_unit = TOUCHED_UNIT.HOURH;
    else if ((abs(m_x3 - x) < radius) && (m_y + radius - y > 0) && (m_y + radius - y < radius * 2 + cstep * 2))
      dragged_unit = TOUCHED_UNIT.MIN3;
    else if ((abs(m_x4 - x) < radius) && (m_y + radius - y > 0) && (m_y + radius - y < radius * 2 + cstep * 3))
      dragged_unit = TOUCHED_UNIT.MIN4;
    else if ((abs(m_x5 - x) < radius) && (m_y + radius - y > 0) && (m_y + radius - y < radius * 2 + cstep * 4))
      dragged_unit = TOUCHED_UNIT.MIN5;
    else if ((abs(s_x3 - x) < radius) && (s_y + radius - y > 0) && (s_y + radius - y < radius * 2 + cstep * 2))
      dragged_unit = TOUCHED_UNIT.SEC3;
    else if ((abs(s_x4 - x) < radius) && (s_y + radius - y > 0) && (s_y + radius - y < radius * 2 + cstep * 3))
      dragged_unit = TOUCHED_UNIT.SEC4;
    else if ((abs(s_x5 - x) < radius) && (s_y + radius - y > 0) && (s_y + radius - y < radius * 2 + cstep * 4))
      dragged_unit = TOUCHED_UNIT.SEC5;
    else
      dragged_unit = TOUCHED_UNIT.NONE;

    move_to(x, y, radius);
  }

  private void move_to(float x, float y, float radius) {
    switch(dragged_unit) {
      case HOUR3:
        if (abs(h_y - y) < radius || y > h_y) dragged_h3 = 0;
        else dragged_h3 = round((h_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case HOURH:
        if (abs(h_y - y) < radius || y > h_y) dragged_hh = 0;
        else {
          dragged_hh = round((h_y - radius - y) / cstep - 0.5f) + 1;
          if (my_viewer.hour_modulus == 8) {
            dragged_hh *= 2;
            if (dragged_hh < 7 && x < h_xh) dragged_hh -= 1;
            else if (dragged_hh == 8) dragged_hh = 7;
          }
        }
        break;
      case MIN3:
        if (abs(m_y - y) < radius || y > m_y) dragged_m3 = 0;
        else dragged_m3 = round((m_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case MIN4:
        if (abs(m_y - y) < radius || y > m_y) dragged_m4 = 0;
        else dragged_m4 = round((m_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case MIN5:
        if (abs(m_y - y) < radius || y > m_y) dragged_m5 = 0;
        else dragged_m5 = round((m_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case SEC3:
        if (abs(s_y - y) < radius || y > s_y) dragged_s3 = 0;
        else dragged_s3 = round((s_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case SEC4:
        if (abs(s_y - y) < radius || y > s_y) dragged_s4 = 0;
        else dragged_s4 = round((s_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      case SEC5:
        if (abs(s_y - y) < radius || y > s_y) dragged_s5 = 0;
        else dragged_s5 = round((s_y - radius - y ) / cstep - 0.5f) + 1;
        break;
      default:
        break;
    }
    // reconstruct time and update
    if (my_viewer.hour_modulus == 4) {
      hour = my_viewer.last_h = (dragged_h3 * 4 + dragged_hh * 9) % 12;
      my_viewer.hr_ed.setText(my_viewer.hour12_strings[my_viewer.last_h]);
    } else {
      hour = my_viewer.last_h = (dragged_h3 * 16 + dragged_hh * 9) % 24;
      my_viewer.hr_ed.setText(my_viewer.hour24_strings[my_viewer.last_h]);
    }
    minute = my_viewer.last_m = (dragged_m3 * 40 + dragged_m4 * 45 + dragged_m5 * 36) % 60;
    second = my_viewer.last_s = (dragged_s3 * 40 + dragged_s4 * 45 + dragged_s5 * 36) % 60;
    my_viewer.min_ed.setText(String.valueOf(my_viewer.last_m));
    my_viewer.sec_ed.setText(String.valueOf(my_viewer.last_s));
    my_viewer.invalidate();
  }

  /**
   * user is dragging finger around the clock
   */
  @Override
  protected void notify_dragged(MotionEvent e) {
    super.notify_dragged(e);
    float radius = min(hstep, vstep) / 2.25f;
    move_to(e.getX(), e.getY(), radius);
  }

  /** Returns .15f, because we don't need very many frames for this kind of animation */
  float preferred_step() { return 0.15f; }

  /** data points regarding the polygons' and bubbles' sizes and/or positions */
  private float hstep, vstep, cradius, cstep;

  /** data points regarding the polygons' and bubbles' positions */
  private float h_x3, h_xh, m_x3, m_x4, m_x5, s_x3, s_x4, s_x5, h_y, m_y, s_y;

  /** Paths to record how to draw each polygon */
  private Path h_tria, h_quad, h_octo, m_tria, m_quad, m_pent, s_tria, s_quad, s_pent;

}
