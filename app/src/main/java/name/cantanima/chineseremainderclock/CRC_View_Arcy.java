package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.FILL_AND_STROKE;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.HOUR3;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.HOURH;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.MIN3;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.MIN4;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.MIN5;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.NONE;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.SEC3;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.SEC4;
import static name.cantanima.chineseremainderclock.Clock_Drawer.TOUCHED_UNIT.SEC5;

/**
 * This class extends Clock_Drawer for the Archy design,
 * a circular design that highlights an arch to show the correct remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Archy works.
 * @see Clock_Drawer
 */
public class CRC_View_Arcy extends Clock_Drawer {

  // default construction
  CRC_View_Arcy(CRC_View owner) {

    initialize_fields(owner);
    stringID = R.string.archy_manual_hint;

  }

  // animate every tenth of a second
  float preferred_step() { return 0.1f; }

  @Override
  void draw(Canvas canvas) {

    // default setup

    setup_time();

    drawTimeAndRectangle(canvas, hour, minute, second, diam);

    int hmod3 = hour % 3;
    int lhmod3 = my_viewer.last_h % 3;
    int hmodh = hour % my_viewer.hour_modulus;
    int lhmodh = my_viewer.last_h % my_viewer.hour_modulus;
    int mmod3 = minute % 3;
    int lmmod3 = my_viewer.last_m % 3;
    int mmod4 = minute % 4;
    int lmmod4 = my_viewer.last_m % 4;
    int mmod5 = minute % 5;
    int lmmod5 = my_viewer.last_m % 5;
    int smod3 = minute % 3;
    int lsmod3 = my_viewer.last_m % 3;
    int smod4 = minute % 4;
    int lsmod4 = my_viewer.last_m % 4;
    int smod5 = minute % 5;
    int lsmod5 = my_viewer.last_m % 5;
    
    switch (dragged_unit) {
      case HOUR3: hmod3 = dragged_h3; break;
      case HOURH: hmodh = dragged_hh; break;
      case MIN3:  mmod3 = dragged_m3; break;
      case MIN4:  mmod4 = dragged_m4; break;
      case MIN5:  mmod5 = dragged_m5; break;
      case SEC3:  smod3 = dragged_s3; break;
      case SEC4:  smod4 = dragged_s4; break;
      case SEC5:  smod5 = dragged_s5; break;
    }

    // each arch can be drawn in one of six ways:
    // 1) if the last short_hand is not equal to the short_hand, then
    //    a) if drawing the last short_hand, and we are animating (my_offset < 1.0) then draw bar
    //    b) if drawing the current short_hand, choose alpha & draw bar
    //    c) otherwise, draw arc
    // 2) if the short_hand equals the current short_hand,
    //    a) if drawing the current short_hand, draw bar
    //    b) otherwise, draw arc

    if (my_viewer.my_offset >= 1 - preferred_step()) {
      my_viewer.my_offset = 1.0f;
      if (dragged_unit != NONE) {
        dragged_unit = NONE;
        reconstruct_time();
        my_viewer.last_h = hour;
        my_viewer.last_m = minute;
        my_viewer.last_s = second;
      }
    }

    // hours: 3-remainder
    ball_paint.setColor(hour_color);
    ball_paint.setAlpha(255);
    ball_paint.setStrokeWidth(1.5f);
    ball_paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    for (int i = 0; i < 3; ++i) {
      if (lhmod3 != hmod3) {
        if (i == lhmod3 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_h3[i][j], ball_paint);
        } else if (hmod3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_h3[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_h3[i], ball_paint);
        }
      } else if (hmod3 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_h3[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_h3[i], ball_paint);
      }
    }
    // hours: 4- or 8-remainder
    if (my_viewer.hour_modulus == 4) {
      for (int i = 0; i < 4; ++i) {
        if (lhmodh != hmodh) {
          if (i == lhmodh && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_h4[i][j], ball_paint);
          } else if (hmodh == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_h4[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_h4[i], ball_paint);
          }
        } else if (hmodh == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_h4[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_h4[i], ball_paint);
        }
      }
    } else {
      for (int i = 0; i < 8; ++i) {
        if (lhmodh != hmodh) {
          if (i == lhmodh && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_h8[i][j], ball_paint);
          } else if (hmodh == i) {
            ball_paint.setStyle(FILL);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_h8[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_h8[i], ball_paint);
          }
        } else if (hmodh == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_h8[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_h8[i], ball_paint);
        }
      }
    }

    // minutes: 3-remainder
    ball_paint.setColor(minute_color);
    ball_paint.setAlpha(255);
    for (int i = 0; i < 3; ++i) {
      if (lmmod3 != mmod3) {
        if (i == lmmod3 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m3[i][j], ball_paint);
        } else if (mmod3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m3[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m3[i], ball_paint);
        }
      } else if (mmod3 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_m3[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_m3[i], ball_paint);
      }
    }
    // minutes: 4-remainder
    for (int i = 0; i < 4; ++i) {
      if (lmmod4 != mmod4) {
        if (i == lmmod4 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m4[i][j], ball_paint);
        } else if (mmod4 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m4[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m4[i], ball_paint);
        }
      } else if (mmod4 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_m4[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_m4[i], ball_paint);
      }
    }
    // minutes: 5-remainder
    for (int i = 0; i < 5; ++i) {
      if (lmmod5 != mmod5) {
        if (i == lmmod5 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m5[i][j], ball_paint);
        } else if (mmod5 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m5[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m5[i], ball_paint);
        }
      } else if (mmod5 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_m5[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_m5[i], ball_paint);
      }
    }

    // showing seconds?
    if (show_seconds) {
      ball_paint.setColor(second_color);
      ball_paint.setAlpha(255);
      // seconds: 3-remainder
      for (int i = 0; i < 3; ++i) {
        if (lsmod3 != smod3) {
          if (i == lsmod3 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s3[i][j], ball_paint);
          } else if (smod3 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s3[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s3[i], ball_paint);
          }
        } else if (smod3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_s3[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_s3[i], ball_paint);
        }
      }
      // seconds: 4-remainder
      for (int i = 0; i < 4; ++i) {
        if (lsmod4 != smod4) {
          if (i == lsmod4 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s4[i][j], ball_paint);
          } else if (smod4 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s4[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s4[i], ball_paint);
          }
        } else if (smod4 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_s4[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_s4[i], ball_paint);
        }
      }
      // seconds: 5-remainder
      for (int i = 0; i < 5; ++i) {
        if (lsmod5 != smod5) {
          if (i == lsmod5 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s5[i][j], ball_paint);
          } else if (smod5 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s5[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s5[i], ball_paint);
          }
        } else if (smod5 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_s5[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_s5[i], ball_paint);
        }
      }
    }

    usual_cleanup();

  }
  
  @Override
  void recalculate_positions() {

    super.recalculate_positions();

    float r_off;

    // first determine the radii of each circle
    if (reverse_orientation) {

      if (show_seconds) {
        rh3 = diam * 7f / 9f;
        rhh = diam * 8f / 9f;
        rm3 = diam * 6f / 9f; // was: 6/9
        rm4 = diam * 5f / 9f; // was: 4/9
        rm5 = diam * 4f / 9f; // was: 2/9
        rs3 = diam * 3f / 9f; // was: 5/9
        rs4 = diam * 2f / 9f; // was: 3/9
        rs5 = diam * 1f / 9f; // was: 1/9
        r_off = diam / 35f;
      } else {
        rm3 = diam * 1f / 6f;
        rm4 = diam * 2f / 6f;
        rm5 = diam * 3f / 6f;
        rh3 = diam * 4f / 6f;
        rhh = diam * 5f / 6f;
        r_off = diam / 25f;
      }

    } else {

      if (show_seconds) {
        rh3 = diam * 1f / 9f;
        rhh = diam * 2f / 9f;
        rm3 = diam * 3f / 9f; // was: 4/9
        rm4 = diam * 4f / 9f; // was: 6/9
        rm5 = diam * 5f / 9f; // was: 8/9
        rs3 = diam * 6f / 9f; // was: 3/9
        rs4 = diam * 7f / 9f; // was: 5/9
        rs5 = diam * 8f / 9f; // was: 7/9
        r_off = diam / 35f;
      } else {
        rh3 = diam * 1f / 6f;
        rhh = diam * 2f / 6f;
        rm3 = diam * 3f / 6f;
        rm4 = diam * 4f / 6f;
        rm5 = diam * 5f / 6f;
        r_off = diam / 25f;
      }

    }

    // prepare paths
    path_h3 = new Path [3][];
    path_h4 = new Path [4][];
    path_h8 = new Path [8][];
    path_m3 = new Path [3][];
    path_m4 = new Path [4][];
    path_m5 = new Path [5][];
    path_s3 = new Path [3][];
    path_s4 = new Path [4][];
    path_s5 = new Path [5][];
    for (int i = 0; i < 3; ++i) {
      path_h3[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
      path_m3[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
      path_s3[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
    }
    for (int i = 0; i < 4; ++i) {
      path_h4[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
      path_m4[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
      path_s4[i] = new Path[]{
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
    }
    for (int i = 0; i < 5; ++i) {
      path_m5[i] = new Path[] {
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
      path_s5[i] = new Path[] {
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
    }
    for (int i = 0; i < 8; ++i) {
      path_h8[i] = new Path[] {
          new Path(), new Path(), new Path(), new Path(), new Path(), new Path(),
          new Path(), new Path(), new Path(), new Path(), new Path()
      };
    }

    arc_h3 = new Path [] { new Path(), new Path(), new Path() };
    arc_h4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
    arc_h8 = new Path [] { new Path(), new Path(), new Path(), new Path() ,
        new Path(), new Path(), new Path(), new Path() };
    arc_m3 = new Path [] { new Path(), new Path(), new Path() };
    arc_m4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
    arc_m5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };
    arc_s3 = new Path [] { new Path(), new Path(), new Path() };
    arc_s4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
    arc_s5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };

    // paths for 3-remainders: each is determined using basic trigonometry along the circle
    for (int i = 0; i < 3; ++i) {

      for (int j = 0; j < 11; ++j) {

        r_off_j = r_off / 11 * (j + 1);

        RectF hrect = new RectF(
            cx - (rh3 - r_off_j), cy - (rh3 - r_off_j), cx + (rh3 - r_off_j), cy + (rh3 - r_off_j)
        );
        path_h3[i][j].arcTo(hrect, 270 + i*120 - 55, 110, true);
        hrect = new RectF(
            cx - (rh3 + r_off_j), cy - (rh3 + r_off_j), cx + (rh3 + r_off_j), cy + (rh3 + r_off_j)
        );
        path_h3[i][j].arcTo(hrect, 270 + i*120 + 55, -110, false);
        path_h3[i][j].close();

        RectF mrect = new RectF(
            cx - (rm3 - r_off_j), cy - (rm3 - r_off_j), cx + (rm3 - r_off_j), cy + (rm3 - r_off_j)
        );
        path_m3[i][j].arcTo(mrect, 270 + i * 120 - 55, 110, true);
        mrect = new RectF(
            cx - (rm3 + r_off_j), cy - (rm3 + r_off_j), cx + (rm3 + r_off_j), cy + (rm3 + r_off_j)
        );
        path_m3[i][j].arcTo(mrect, 270 + i * 120 + 55, -110, false);
        path_m3[i][j].close();

        RectF srect = new RectF(
            cx - (rs3 - r_off_j), cy - (rs3 - r_off_j), cx + (rs3 - r_off_j), cy + (rs3 - r_off_j)
        );
        path_s3[i][j].arcTo(srect, 270 + i * 120 - 55, 110, true);
        srect = new RectF(
            cx - (rs3 + r_off_j), cy - (rs3 + r_off_j), cx + (rs3 + r_off_j), cy + (rs3 + r_off_j)
        );
        path_s3[i][j].arcTo(srect, 270 + i * 120 + 55, -110, false);
        path_s3[i][j].close();
        
      }

      RectF arect = new RectF(cx - rh3, cy - rh3, cx + rh3, cy + rh3);
      arc_h3[i].addArc(arect, 270 + i*120 - 55, 110);
      arect = new RectF(cx - rm3, cy - rm3, cx + rm3, cy + rm3);
      arc_m3[i].addArc(arect, 270 + i*120 - 55, 110);
      arect = new RectF(cx - rs3, cy - rs3, cx + rs3, cy + rs3);
      arc_s3[i].addArc(arect, 270 + i*120 - 55, 110);

    }

    // paths for 4-remainders: each is determined using basic trigonometry along the circle
    for (int i = 0; i < 4; ++i) {
        
      for (int j = 0; j < 11; ++j) {
        
        float r_off_j = r_off / 11 * (j + 1);

        RectF hrect = new RectF(
            cx - (rhh - r_off_j), cy - (rhh - r_off_j), cx + (rhh - r_off_j), cy + (rhh - r_off_j)
        );
        path_h4[i][j].arcTo(hrect, 270 + i*90 - 40, 80, true);
        hrect = new RectF(
            cx - (rhh + r_off_j), cy - (rhh + r_off_j), cx + (rhh + r_off_j), cy + (rhh + r_off_j)
        );
        path_h4[i][j].arcTo(hrect, 270 + i*90 + 40, -80, false);
        path_h4[i][j].close();

        RectF mrect = new RectF(
            cx - (rm4 - r_off_j), cy - (rm4 - r_off_j), cx + (rm4 - r_off_j), cy + (rm4 - r_off_j)
        );
        path_m4[i][j].arcTo(mrect, 270 + i*90 - 40, 80, true);
        mrect = new RectF(
            cx - (rm4 + r_off_j), cy - (rm4 + r_off_j), cx + (rm4 + r_off_j), cy + (rm4 + r_off_j)
        );
        path_m4[i][j].arcTo(mrect, 270 + i*90 + 40, -80, false);
        path_m4[i][j].close();

        RectF srect = new RectF(
            cx - (rs4 - r_off_j), cy - (rs4 - r_off_j), cx + (rs4 - r_off_j), cy + (rs4 - r_off_j)
        );
        path_s4[i][j].arcTo(srect, 270 + i * 90 - 40, 80, true);
        srect = new RectF(
            cx - (rs4 + r_off_j), cy - (rs4 + r_off_j), cx + (rs4 + r_off_j), cy + (rs4 + r_off_j)
        );
        path_s4[i][j].arcTo(srect, 270 + i * 90 + 40, -80, false);
        path_s4[i][j].close();
        
      }

      RectF arect = new RectF(cx - rhh, cy - rhh, cx + rhh, cy + rhh);
      arc_h4[i].addArc(arect, 270 + i*90 - 40, 80);
      arect = new RectF(cx - rm4, cy - rm4, cx + rm4, cy + rm4);
      arc_m4[i].addArc(arect, 270 + i*90 - 40, 80);
      arect = new RectF(cx - rs4, cy - rs4, cx + rs4, cy + rs4);
      arc_s4[i].addArc(arect, 270 + i*90 - 40, 80);

    }

    // paths for 8-remainders: each is determined using basic trigonometry along the circle
    for (int i = 0; i < 8; ++i) {
      
      for (int j = 0; j < 11; ++j) {
        
        float r_off_j = r_off / 11 * (j + 1);

        RectF hrect = new RectF(
            cx - (rhh - r_off_j), cy - (rhh - r_off_j), cx + (rhh - r_off_j), cy + (rhh - r_off_j)
        );
        path_h8[i][j].arcTo(hrect, 270 + i * 45 - 17.5f, 35, true);
        hrect = new RectF(
            cx - (rhh + r_off_j), cy - (rhh + r_off_j), cx + (rhh + r_off_j), cy + (rhh + r_off_j)
        );
        path_h8[i][j].arcTo(hrect, 270 + i * 45 + 17.5f, -35, false);
        path_h8[i][j].close();

        RectF arect = new RectF(cx - rhh, cy - rhh, cx + rhh, cy + rhh);
        arc_h8[i].addArc(arect, 270 + i * 45 - 17.5f, 35);
        
      }

    }

    // paths for 5-remainders: each is determined using basic trigonometry along the circle
    for (int i = 0; i < 5; ++i) {

      for (int j = 0; j < 11; ++j) {
        
        float r_off_j = r_off / 11 * (j + 1);

        RectF mrect = new RectF(
            cx - (rm5 - r_off_j), cy - (rm5 - r_off_j), cx + (rm5 - r_off_j), cy + (rm5 - r_off_j)
        );
        path_m5[i][j].arcTo(mrect, 270 + i*72 - 31, 62, true);
        mrect = new RectF(
            cx - (rm5 + r_off_j), cy - (rm5 + r_off_j), cx + (rm5 + r_off_j), cy + (rm5 + r_off_j)
        );
        path_m5[i][j].arcTo(mrect, 270 + i*72 + 31, -62, false);
        path_m5[i][j].close();

        RectF srect = new RectF(
            cx - (rs5 - r_off_j), cy - (rs5 - r_off_j), cx + (rs5 - r_off_j), cy + (rs5 - r_off_j)
        );
        path_s5[i][j].arcTo(srect, 270 + i * 72 - 31, 62, true);
        srect = new RectF(
            cx - (rs5 + r_off_j), cy - (rs5 + r_off_j), cx + (rs5 + r_off_j), cy + (rs5 + r_off_j)
        );
        path_s5[i][j].arcTo(srect, 270 + i * 72 + 31, -62, false);
        path_s5[i][j].close();
        
      }

      RectF arect = new RectF(cx - rm5, cy - rm5, cx + rm5, cy + rm5);
      arc_m5[i].addArc(arect, 270 + i*72 - 31, 62);
      arect = new RectF(cx - rs5, cy - rs5, cx + rs5, cy + rs5);
      arc_s5[i].addArc(arect, 270 + i*72 - 31, 62);

    }

  }

  /**
   * user has touched finger to clock
   * <p>
   * The basic implementation does nothing.
   * Your drawer will want to override this, probably to determine which unit is being manipulated.
   */
  @Override
  protected void notify_touched(MotionEvent e) {
    super.notify_touched(e);
    float x = e.getX(), y = e.getY();
    my_viewer.my_animator.immediate_animation();
    move_to(x, y);
  }

  /**
   * user is dragging finger around the clock
   * <p>
   * The basic implementation does nothing.
   * Your drawer will want to override this, probably to determine the new position
   * of the unit being manipulated.
   *
   */
  @Override
  protected void notify_dragged(MotionEvent e) {
      super.notify_dragged(e);
      move_to(e.getX(), e.getY());
  }

  private void move_to(float x, float y) {
    float r = (float )sqrt((x - cx)*(x - cx) + (y - cy)*(y - cy));
    double a = atan2(y - cy, x - cx) + ((float )PI)/2;
    if (rh3 - r_off_j < r && r < rh3 + r_off_j) {
      dragged_unit = HOUR3;
      dragged_h3 = (int )round(3 * a / (2*PI));
    } else if (rhh - r_off_j < r && r < rhh + r_off_j) {
      dragged_unit = HOURH;
      dragged_hh = (int )round(my_viewer.hour_modulus * a / (2*PI));
    } else if (rm3 - r_off_j < r && r < rm3 + r_off_j) {
      dragged_unit = MIN3;
      dragged_m3 = (int )round(3 * a / (2*PI));
    } else if (rm4 - r_off_j < r && r < rm4 + r_off_j) {
      dragged_unit = MIN4;
      dragged_m4 = (int )round(4 * a / (2*PI));
    } else if (rm5 - r_off_j < r && r < rm5 + r_off_j) {
      dragged_unit = MIN5;
      dragged_m5 = (int )round(5 * a / (2*PI));
    } else if (show_seconds) {
      if (rs3 - r_off_j < r && r < rs3 + r_off_j) {
        dragged_unit = SEC3;
        dragged_s3 = (int )round(3 * a / (2*PI));
      } else if (rs4 - r_off_j < r && r < rs4 + r_off_j) {
        dragged_unit = SEC4;
        dragged_s4 = (int )round(4 * a / (2*PI));
      } else if (rs5 - r_off_j < r && r < rs5 + r_off_j) {
        dragged_unit = SEC5;
        dragged_s5 = (int )round(5 * a / (2*PI));
      }
      else dragged_unit = NONE;
    } else
      dragged_unit = NONE;

    // reconstruct time and update
    reconstruct_time();
    switch (my_viewer.which_unit_to_modify) {
      case HOURS:
        my_viewer.valueEditor.setText(String.valueOf(my_viewer.last_h));
        break;
      case MINUTES:
        my_viewer.valueEditor.setText(String.valueOf(my_viewer.last_m));
        break;
      case SECONDS:
        my_viewer.valueEditor.setText(String.valueOf(my_viewer.last_s));
        break;
    }
    my_viewer.move_time_to(hour, minute, second);
  }

  private void reconstruct_time() {
    if (my_viewer.hour_modulus == 4) {
      hour = (dragged_h3 * 4 + dragged_hh * 9) % 12;
      while (hour < 0) hour += 12;
    } else {
      hour = (dragged_h3 * 16 + dragged_hh * 9) % 24;
      while (hour < 0) hour += 24;
    }
    minute = (dragged_m3 * 40 + dragged_m4 * 45 + dragged_m5 * 36) % 60;
    while (minute < 0) minute += 60;
    second = (dragged_s3 * 40 + dragged_s4 * 45 + dragged_s5 * 36) % 60;
    while (second < 0) second += 60;
  }

  /**
   * user has lifted finger off clock
   * <p>
   * Disable the usual notification in order to complete animation when touched & released.
   *
   */
  @Override
  protected void notify_released(MotionEvent e) { }

  private float rh3, rhh, rm3, rm4, rm5, rs3 = 0, rs4 = 0, rs5 = 0, r_off_j;

  private Path [] [] path_h3, path_h4, path_h8, path_m3, path_m4, path_m5, path_s3, path_s4, path_s5;

  private Path [] arc_h3, arc_h4, arc_h8, arc_m3, arc_m4, arc_m5, arc_s3, arc_s4, arc_s5;

}
