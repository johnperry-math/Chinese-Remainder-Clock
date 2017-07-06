package name.cantanima.chineseremainderclock;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.FILL_AND_STROKE;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.min;

public class CRC_View_Arcy extends Clock_Drawer {

    // constructor
    public CRC_View_Arcy(CRC_View owner) {

        initialize_fields(owner);

    }

    float preferred_step() { return 0.1f; }

    @Override
    void draw(Canvas canvas) {

        setup_time();

        drawTimeAndRectangle(canvas, hour, minute, second, cx, cy, diam, my_viewer.analog);

        ball_paint.setColor(hour_color);
        ball_paint.setStyle(STROKE);
        ball_paint.setStrokeWidth(1f);
        for (int i = 0; i < 3; ++i) {
            if (my_viewer.last_h != hour) {
                if (i == my_viewer.last_h % 3 && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_h3[i], ball_paint);
                } else if (hour % 3 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                    canvas.drawPath(path_h3[i], ball_paint);
                }
            } else if (hour % 3 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h3[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_h3[i], ball_paint);
        }
        if (my_viewer.hour_modulus == 4) {
            for (int i = 0; i < 4; ++i) {
                if (my_viewer.last_m != minute) {
                    if (i == my_viewer.last_h % 4 && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_h4[i], ball_paint);
                    } else if (hour % 4 == i) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                        canvas.drawPath(path_h4[i], ball_paint);
                    }
                } else if (hour % 4 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_h4[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h4[i], ball_paint);
            }
        } else {
            for (int i = 0; i < 8; ++i) {
                if (my_viewer.last_h != hour) {
                    if (i == my_viewer.last_h % 8 && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_h8[i], ball_paint);
                    } else if (hour % 8 == i) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                        canvas.drawPath(path_h8[i], ball_paint);
                    }
                } else if (hour % 8 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_h8[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_h8[i], ball_paint);
            }
        }

        ball_paint.setColor(minute_color);
        for (int i = 0; i < 3; ++i) {
            if (my_viewer.last_m != minute) {
                if (i == my_viewer.last_m % 3 && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m3[i], ball_paint);
                } else if (minute % 3 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                    canvas.drawPath(path_m3[i], ball_paint);
                }
            } else if (minute % 3 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m3[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m3[i], ball_paint);
        }
        for (int i = 0; i < 4; ++i) {
            if (my_viewer.last_m != minute) {
                if (i == my_viewer.last_m % 4 && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m4[i], ball_paint);
                } else if (minute % 4 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                    canvas.drawPath(path_m4[i], ball_paint);
                }
            } else if (minute % 4 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m4[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m4[i], ball_paint);
        }
        for (int i = 0; i < 5; ++i) {
            if (my_viewer.last_m != minute) {
                if (i == my_viewer.last_m % 5 && my_viewer.my_offset < 1.0) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                    canvas.drawPath(path_m5[i], ball_paint);
                } else if (minute % 5 == i) {
                    ball_paint.setStyle(FILL);
                    ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                    canvas.drawPath(path_m5[i], ball_paint);
                }
            } else if (minute % 5 == i) {
                ball_paint.setStyle(FILL_AND_STROKE);
                ball_paint.setAlpha(255);
                canvas.drawPath(path_m5[i], ball_paint);
            }
            ball_paint.setStyle(STROKE);
            ball_paint.setAlpha(255);
            canvas.drawPath(path_m5[i], ball_paint);
        }

        if (show_seconds) {
            ball_paint.setColor(second_color);
            for (int i = 0; i < 3; ++i) {
                /*if (my_viewer.last_s != second) {
                    if (i == my_viewer.last_s % 3 && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_s3[i], ball_paint);
                    } else if (second % 3 == i) {
                        ball_paint.setStyle(FILL);
                        //ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                        ball_paint.setAlpha(255);
                        canvas.drawPath(path_s3[i], ball_paint);
                    }
                } else*/ if (second % 3 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_s3[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                //canvas.drawPath(path_s3[i], ball_paint);
            }
            for (int i = 0; i < 4; ++i) {
                /*if (my_viewer.last_s != second) {
                    if (i == my_viewer.last_s % 4 && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_s4[i], ball_paint);
                    } else if (second % 4 == i) {
                        ball_paint.setStyle(FILL);
                        //ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                        ball_paint.setAlpha(255);
                        canvas.drawPath(path_s4[i], ball_paint);
                    }
                } else*/ if (second % 4 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_s4[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                //canvas.drawPath(path_s4[i], ball_paint);
            }
            for (int i = 0; i < 5; ++i) {
                /*if (my_viewer.last_s != second) {
                    if (i == my_viewer.last_s % 5 && my_viewer.my_offset < 1.0) {
                        ball_paint.setStyle(FILL);
                        ball_paint.setAlpha((int) (255 * (1 - my_viewer.my_offset)));
                        canvas.drawPath(path_s5[i], ball_paint);
                    } else if (second % 5 == i) {
                        ball_paint.setStyle(FILL);
                        //ball_paint.setAlpha(min(255, (int) (255 * my_viewer.my_offset)));
                        ball_paint.setAlpha(255);
                        canvas.drawPath(path_s5[i], ball_paint);
                    }
                } else*/ if (second % 5 == i) {
                    ball_paint.setStyle(FILL_AND_STROKE);
                    ball_paint.setAlpha(255);
                    canvas.drawPath(path_s5[i], ball_paint);
                }
                ball_paint.setStyle(STROKE);
                ball_paint.setAlpha(255);
                //canvas.drawPath(path_s5[i], ball_paint);
            }
        }

        usual_cleanup();

    }
    
    @Override
    void recalculate_positions() {

        super.recalculate_positions();

        float r_off;

        if (reverse_orientation) {

            if (show_seconds) {
                rh3 = diam * 7f / 10f;
                rhh = diam * 8f / 10f;
                rm3 = diam * 6f / 10f;
                rs3 = diam * 5f / 10f;
                rm4 = diam * 4f / 10f;
                rs4 = diam * 3f / 10f;
                rm5 = diam * 2f / 10f;
                rs5 = diam * 1f / 10f;
                r_off = diam / 40f;
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
                rh3 = diam * 1f / 10f;
                rhh = diam * 2f / 10f;
                rs3 = diam * 3f / 10f;
                rm3 = diam * 4f / 10f;
                rs4 = diam * 5f / 10f;
                rm4 = diam * 6f / 10f;
                rs5 = diam * 7f / 10f;
                rm5 = diam * 8f / 10f;
                r_off = diam / 40f;
            } else {
                rh3 = diam * 1f / 6f;
                rhh = diam * 2f / 6f;
                rm3 = diam * 3f / 6f;
                rm4 = diam * 4f / 6f;
                rm5 = diam * 5f / 6f;
                r_off = diam / 25f;
            }

        }

        path_h3 = new Path [] { new Path(), new Path(), new Path() };
        path_h4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_h8 = new Path [] { new Path(), new Path(), new Path(), new Path() ,
                                new Path(), new Path(), new Path(), new Path() };
        path_m3 = new Path [] { new Path(), new Path(), new Path() };
        path_m4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_m5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };
        path_s3 = new Path [] { new Path(), new Path(), new Path() };
        path_s4 = new Path [] { new Path(), new Path(), new Path(), new Path() };
        path_s5 = new Path [] { new Path(), new Path(), new Path(), new Path(), new Path() };

        for (int i = 0; i < 3; ++i) {

            path_h3[i].rewind(); path_m3[i].rewind(); path_s3[i].rewind();

            RectF hrect = new RectF(
                    cx - (rh3 - r_off), cy - (rh3 - r_off), cx + (rh3 - r_off), cy + (rh3 - r_off)
            );
            path_h3[i].arcTo(hrect, 270 + i*120 - 55, 110, true);
            hrect = new RectF(
                    cx - (rh3 + r_off), cy - (rh3 + r_off), cx + (rh3 + r_off), cy + (rh3 + r_off)
            );
            path_h3[i].arcTo(hrect, 270 + i*120 + 55, -110, false);
            path_h3[i].close();

            RectF mrect = new RectF(
                    cx - (rm3 - r_off), cy - (rm3 - r_off), cx + (rm3 - r_off), cy + (rm3 - r_off)
            );
            path_m3[i].arcTo(mrect, 270 + i*120 - 55, 110, true);
            mrect = new RectF(
                    cx - (rm3 + r_off), cy - (rm3 + r_off), cx + (rm3 + r_off), cy + (rm3 + r_off)
            );
            path_m3[i].arcTo(mrect, 270 + i*120 + 55, -110, false);
            path_m3[i].close();

            RectF srect = new RectF(
                    cx - (rs3 - r_off), cy - (rs3 - r_off), cx + (rs3 - r_off), cy + (rs3 - r_off)
            );
            path_s3[i].arcTo(srect, 270 + i*120 - 55, 110, true);
            srect = new RectF(
                    cx - (rs3 + r_off), cy - (rs3 + r_off), cx + (rs3 + r_off), cy + (rs3 + r_off)
            );
            path_s3[i].arcTo(srect, 270 + i*120 + 55, -110, false);
            path_s3[i].close();


        }

        for (int i = 0; i < 4; ++i) {
            
            path_h4[i].rewind(); path_m4[i].rewind(); path_s4[i].rewind();

            RectF hrect = new RectF(
                    cx - (rhh - r_off), cy - (rhh - r_off), cx + (rhh - r_off), cy + (rhh - r_off)
            );
            path_h4[i].arcTo(hrect, 270 + i*90 - 40, 80, true);
            hrect = new RectF(
                    cx - (rhh + r_off), cy - (rhh + r_off), cx + (rhh + r_off), cy + (rhh + r_off)
            );
            path_h4[i].arcTo(hrect, 270 + i*90 + 40, -80, false);
            path_h4[i].close();

            RectF mrect = new RectF(
                    cx - (rm4 - r_off), cy - (rm4 - r_off), cx + (rm4 - r_off), cy + (rm4 - r_off)
            );
            path_m4[i].arcTo(mrect, 270 + i*90 - 40, 80, true);
            mrect = new RectF(
                    cx - (rm4 + r_off), cy - (rm4 + r_off), cx + (rm4 + r_off), cy + (rm4 + r_off)
            );
            path_m4[i].arcTo(mrect, 270 + i*90 + 40, -80, false);
            path_m4[i].close();

            RectF srect = new RectF(
                    cx - (rs4 - r_off), cy - (rs4 - r_off), cx + (rs4 - r_off), cy + (rs4 - r_off)
            );
            path_s4[i].arcTo(srect, 270 + i*90 - 40, 80, true);
            srect = new RectF(
                    cx - (rs4 + r_off), cy - (rs4 + r_off), cx + (rs4 + r_off), cy + (rs4 + r_off)
            );
            path_s4[i].arcTo(srect, 270 + i*90 + 40, -80, false);
            path_s4[i].close();


        }

        for (int i = 0; i < 8; ++i) {

            path_h8[i].rewind();

            RectF hrect = new RectF(
                    cx - (rhh - r_off), cy - (rhh - r_off), cx + (rhh - r_off), cy + (rhh - r_off)
            );
            path_h8[i].arcTo(hrect, 270 + i * 45 - 17.5f, 35, true);
            hrect = new RectF(
                    cx - (rhh + r_off), cy - (rhh + r_off), cx + (rhh + r_off), cy + (rhh + r_off)
            );
            path_h8[i].arcTo(hrect, 270 + i * 45 + 17.5f, -35, false);
            path_h8[i].close();

        }
        
        for (int i = 0; i < 5; ++i) {

            path_m5[i].rewind(); path_s5[i].rewind();

            RectF mrect = new RectF(
                    cx - (rm5 - r_off), cy - (rm5 - r_off), cx + (rm5 - r_off), cy + (rm5 - r_off)
            );
            path_m5[i].arcTo(mrect, 270 + i*72 - 31, 62, true);
            mrect = new RectF(
                    cx - (rm5 + r_off), cy - (rm5 + r_off), cx + (rm5 + r_off), cy + (rm5 + r_off)
            );
            path_m5[i].arcTo(mrect, 270 + i*72 + 31, -62, false);
            path_m5[i].close();

            RectF srect = new RectF(
                    cx - (rs5 - r_off), cy - (rs5 - r_off), cx + (rs5 - r_off), cy + (rs5 - r_off)
            );
            path_s5[i].arcTo(srect, 270 + i*72 - 31, 62, true);
            srect = new RectF(
                    cx - (rs5 + r_off), cy - (rs5 + r_off), cx + (rs5 + r_off), cy + (rs5 + r_off)
            );
            path_s5[i].arcTo(srect, 270 + i*72 + 31, -62, false);
            path_s5[i].close();


        }

    }

    float rh3, rhh, rm3, rm4, rm5, rs3, rs4, rs5;

    Path [] path_h3, path_h4, path_h8, path_m3, path_m4, path_m5, path_s3, path_s4, path_s5;

    final static String tag = "Archy";
}
