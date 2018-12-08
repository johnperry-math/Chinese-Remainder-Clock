package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.FILL_AND_STROKE;
import static android.graphics.Paint.Style.STROKE;

/**
 * This class extends Clock_Drawer for the Archy design,
 * a circular design that highlights an arch to show the correct remainder.
 * For general documentation on Clock_Drawer, see that class.
 * This file only documents groups of lines to explain how Archy works.
 * @see Clock_Drawer
 */
public class CRC_View_Arcy extends Clock_Drawer {

  // default construction
  CRC_View_Arcy(CRC_View owner) { initialize_fields(owner); }

  // animate every tenth of a second
  float preferred_step() { return 0.1f; }

  @Override
  void draw(Canvas canvas) {

    // default setup

    setup_time();

    drawTimeAndRectangle(canvas, hour, minute, second, diam);

    // each arch can be drawn in one of six ways:
    // 1) if the last short_hand is not equal to the short_hand, then
    //    a) if drawing the last short_hand, and we are animating (my_offset < 1.0) then draw bar
    //    b) if drawing the current short_hand, choose alpha & draw bar
    //    c) otherwise, draw arc
    // 2) if the short_hand equals the current short_hand,
    //    a) if drawing the current short_hand, draw bar
    //    b) otherwise, draw arc

    if (my_viewer.my_offset > 1.0) my_viewer.my_offset = 1.0f;

    // hours: 3-remainder
    ball_paint.setColor(hour_color);
    ball_paint.setAlpha(255);
    ball_paint.setStrokeWidth(1.5f);
    ball_paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    for (int i = 0; i < 3; ++i) {
      if (my_viewer.last_h != hour) {
        if (i == my_viewer.last_h % 3 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_h3[i][j], ball_paint);
        } else if (hour % 3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_h3[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_h3[i], ball_paint);
        }
      } else if (hour % 3 == i) {
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
        if (my_viewer.last_h != hour) {
          if (i == my_viewer.last_h % 4 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_h4[i][j], ball_paint);
          } else if (hour % 4 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_h4[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_h4[i], ball_paint);
          }
        } else if (hour % 4 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_h4[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_h4[i], ball_paint);
        }
      }
    } else {
      for (int i = 0; i < 8; ++i) {
        if (my_viewer.last_h != hour) {
          if (i == my_viewer.last_h % 8 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_h8[i][j], ball_paint);
          } else if (hour % 8 == i) {
            ball_paint.setStyle(FILL);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_h8[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_h8[i], ball_paint);
          }
        } else if (hour % 8 == i) {
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
      if (my_viewer.last_m != minute) {
        if (i == my_viewer.last_m % 3 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m3[i][j], ball_paint);
        } else if (minute % 3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m3[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m3[i], ball_paint);
        }
      } else if (minute % 3 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_m3[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_m3[i], ball_paint);
      }
    }
    // minutes: 4-remainder
    for (int i = 0; i < 4; ++i) {
      if (my_viewer.last_m != minute) {
        if (i == my_viewer.last_m % 4 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m4[i][j], ball_paint);
        } else if (minute % 4 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m4[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m4[i], ball_paint);
        }
      } else if (minute % 4 == i) {
        ball_paint.setStyle(FILL_AND_STROKE);
        canvas.drawPath(path_m4[i][10], ball_paint);
      } else {
        ball_paint.setStyle(STROKE);
        canvas.drawPath(arc_m4[i], ball_paint);
      }
    }
    // minutes: 5-remainder
    for (int i = 0; i < 5; ++i) {
      if (my_viewer.last_m != minute) {
        if (i == my_viewer.last_m % 5 && my_viewer.my_offset < 1.0) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
          canvas.drawPath(path_m5[i][j], ball_paint);
        } else if (minute % 5 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          int j = (int) (my_viewer.my_offset * 10);
          canvas.drawPath(path_m5[i][j], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_m5[i], ball_paint);
        }
      } else if (minute % 5 == i) {
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
        if (my_viewer.last_s != second) {
          if (i == my_viewer.last_s % 3 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s3[i][j], ball_paint);
          } else if (second % 3 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s3[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s3[i], ball_paint);
          }
        } else if (second % 3 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_s3[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_s3[i], ball_paint);
        }
      }
      // seconds: 4-remainder
      for (int i = 0; i < 4; ++i) {
        if (my_viewer.last_s != second) {
          if (i == my_viewer.last_s % 4 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s4[i][j], ball_paint);
          } else if (second % 4 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s4[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s4[i], ball_paint);
          }
        } else if (second % 4 == i) {
          ball_paint.setStyle(FILL_AND_STROKE);
          canvas.drawPath(path_s4[i][10], ball_paint);
        } else {
          ball_paint.setStyle(STROKE);
          canvas.drawPath(arc_s4[i], ball_paint);
        }
      }
      // seconds: 5-remainder
      for (int i = 0; i < 5; ++i) {
        if (my_viewer.last_s != second) {
          if (i == my_viewer.last_s % 5 && my_viewer.my_offset < 1.0) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) ((1 - (my_viewer.my_offset + .1)) * 10);
            canvas.drawPath(path_s5[i][j], ball_paint);
          } else if (second % 5 == i) {
            ball_paint.setStyle(FILL_AND_STROKE);
            int j = (int) (my_viewer.my_offset * 10);
            canvas.drawPath(path_s5[i][j], ball_paint);
          } else {
            ball_paint.setStyle(STROKE);
            canvas.drawPath(arc_s5[i], ball_paint);
          }
        } else if (second % 5 == i) {
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

    float rh3, rhh, rm3, rm4, rm5, rs3 = 0, rs4 = 0, rs5 = 0;

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

        float r_off_j = r_off / 11 * (j + 1);

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

  private Path [] [] path_h3, path_h4, path_h8, path_m3, path_m4, path_m5, path_s3, path_s4, path_s5;

  private Path [] arc_h3, arc_h4, arc_h8, arc_m3, arc_m4, arc_m5, arc_s3, arc_s4, arc_s5;

}
