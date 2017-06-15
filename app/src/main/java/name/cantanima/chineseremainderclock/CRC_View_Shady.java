package name.cantanima.chineseremainderclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.CALENDAR;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.DECREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.INCREMENT;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.LEAVE_BE;
import static name.cantanima.chineseremainderclock.CRC_View.Modification.NEW_VALUE;

import name.cantanima.chineseremainderclock.CRC_View.Modification;

/**
 * Created by cantanima on 6/5/17.
 */

public class CRC_View_Shady extends Clock_Drawer {

    // constructor
    public CRC_View_Shady(CRC_View owner) {

        initialize_fields(owner);
        digi_paint = new Paint(ANTI_ALIAS_FLAG);
        digi_paint.setColor(WHITE);
        digi_paint.setTextAlign(CENTER);

    }

    float preferred_step() { return 0.1f; }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void draw(Canvas canvas) {

        setup_time();

        drawTimeAndRectangle(canvas, hour, minute, second, cx, cy, diam, my_viewer.analog);

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

        // in digital, we draw and fill polygons
        // the fill's transparency varies according to the remainder:
        // the closer the remainder is to the modulus, the more opaque the fill

        // hour, modulo 3

        ball_paint.setColor(hour_color);
        ball_paint.setStyle(FILL);
        if (my_viewer.last_h != hour) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*85*(lhmod3 % 3) + my_viewer.my_offset*85*(hmod3 % 3))
            );
            canvas.drawPath(h_tria, ball_paint);
        } else {
            ball_paint.setAlpha(85 * (hmod3 % 3));
            canvas.drawPath(h_tria, ball_paint);
        }
        if (hmod3 == 3)
            canvas.drawText(zero_str, cx - digi_step, digi_hty1, digi_paint);
        else
            canvas.drawText(String.valueOf(hmod3), cx - digi_step, digi_hty1, digi_paint);

        // hour, modulo 4 or 8, depending on the kind of time

        if (my_viewer.hour_modulus == 4) {

            if (my_viewer.last_h != hour) {
                ball_paint.setAlpha((int) (
                        (1 - my_viewer.my_offset)*63*(lhmod4 % 4) + my_viewer.my_offset*63*(hmod4 % 4))
                );
                canvas.drawPath(h_quad, ball_paint);
            } else {
                ball_paint.setAlpha(63 * (hmod4 % 4));
                canvas.drawPath(h_quad, ball_paint);
            }
            if (hmod4 == 4)
                canvas.drawText(zero_str, cx - digi_step, digi_hty2, digi_paint);
            else
                canvas.drawText(String.valueOf(hmod4), cx - digi_step, digi_hty2, digi_paint);

        } else { // hour modulo 8

            if (my_viewer.last_h != hour) {
                ball_paint.setAlpha((int) (
                        (1 - my_viewer.my_offset)*31*(lhmod4 % 8) + my_viewer.my_offset*31*(hmod4 % 8))
                );
                canvas.drawPath(h_octo, ball_paint);
            } else {
                ball_paint.setAlpha(31 * (hmod4 % 8));
                canvas.drawPath(h_octo, ball_paint);
            }
            if (hmod4 == 8)
                canvas.drawText(zero_str, cx - digi_step, digi_hty2, digi_paint);
            else
                canvas.drawText(String.valueOf(hmod4), cx - digi_step, digi_hty2, digi_paint);

        }

        // minute, modulo 3

        ball_paint.setColor(minute_color);
        if (my_viewer.last_m != minute) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*85*(lmmod3 % 3) + my_viewer.my_offset*85*(mmod3 % 3))
            );
            canvas.drawPath(m_tria, ball_paint);
        } else {
            ball_paint.setAlpha(85 * (mmod3 % 3));
            canvas.drawPath(m_tria, ball_paint);
        }
        if (mmod3 == 3)
            canvas.drawText(zero_str, cx, digi_msty1, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod3), cx, digi_msty1, digi_paint);

        // minute, modulo 4

        if (my_viewer.last_m != minute) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*63*(lmmod4 % 4) + my_viewer.my_offset*63*(mmod4 % 4))
            );
            canvas.drawPath(m_quad, ball_paint);
        } else {
            ball_paint.setAlpha(63 * (mmod4 % 4));
            canvas.drawPath(m_quad, ball_paint);
        }
        if (mmod4 == 4)
            canvas.drawText(zero_str, cx, digi_ty, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod4), cx, digi_ty, digi_paint);

        // minute, modulo 5

        if (my_viewer.last_m != minute) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*51*(lmmod5 % 5) + my_viewer.my_offset*51*(mmod5 % 5))
            );
            canvas.drawPath(m_pent, ball_paint);
        } else {
            ball_paint.setAlpha(51 * (mmod5 % 5));
            canvas.drawPath(m_pent, ball_paint);
        }
        if (mmod5 == 5)
            canvas.drawText(zero_str, cx, digi_msty3, digi_paint);
        else
            canvas.drawText(String.valueOf(mmod5), cx, digi_msty3, digi_paint);

        // second, modulo 3

        ball_paint.setColor(second_color);
        if (my_viewer.last_s != second) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*85*(lsmod3 % 3) + my_viewer.my_offset*85*(smod3 % 3))
            );
            canvas.drawPath(s_tria, ball_paint);
        } else {
            ball_paint.setAlpha(85 * (smod3 % 3));
            canvas.drawPath(s_tria, ball_paint);
        }
        if (smod3 == 3)
            canvas.drawText(zero_str, cx + digi_step, digi_msty1, digi_paint);
        else
            canvas.drawText(String.valueOf(smod3), cx + digi_step, digi_msty1, digi_paint);

        // second, modulo 4

        if (my_viewer.last_s != second) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*63*(lsmod4 % 4) + my_viewer.my_offset*63*(smod4 % 4))
            );
            canvas.drawPath(s_quad, ball_paint);
        } else {
            ball_paint.setAlpha(63 * (smod4 % 4));
            canvas.drawPath(s_quad, ball_paint);
        }
        if (smod4 == 4)
            canvas.drawText(zero_str, cx + digi_step, digi_ty, digi_paint);
        else
            canvas.drawText(String.valueOf(smod4), cx + digi_step, digi_ty, digi_paint);

        // minute, modulo 5

        if (my_viewer.last_s != second) {
            ball_paint.setAlpha((int) (
                    (1 - my_viewer.my_offset)*51*(lsmod5 % 5) + my_viewer.my_offset*51*(smod5 % 5))
            );
            canvas.drawPath(s_pent, ball_paint);
        } else {
            ball_paint.setAlpha(51 * (smod5 % 5));
            canvas.drawPath(s_pent, ball_paint);
        }
        if (smod5 == 5)
            canvas.drawText(zero_str, cx + digi_step, digi_msty3, digi_paint);
        else
            canvas.drawText(String.valueOf(smod5), cx + digi_step, digi_msty3, digi_paint);

        usual_cleanup();

    }

    // this should be needed only once in the lifetime of the View,
    // but this can be many times because the View is re-constructed
    // every time the orientation changes
    void recalculate_positions() {

        super.recalculate_positions();

        obj_w2 = diam / 5;
        digi_paint.setTextSize(obj_w2 * 0.75f);
        Paint.FontMetrics fm = digi_paint.getFontMetrics();
        float digi_t_adjust = -fm.ascent;
        digi_ty = cy + digi_t_adjust / 2.5f;
        digi_step = diam / 2;
        digi_hcy1 = cy - obj_w2;
        digi_hcy2 = cy + obj_w2;
        digi_hty1 = digi_hcy1 + digi_t_adjust / 2.5f;
        digi_hty2 = digi_hcy2 + digi_t_adjust / 2.5f;
        digi_mscy1 = cy - obj_w2 * 5 / 2;
        digi_mscy3 = cy + obj_w2 * 5 / 2;
        digi_msty1 = digi_mscy1 + digi_t_adjust / 2.5f;
        digi_msty3 = digi_mscy3 + digi_t_adjust / 2.5f;

        digi_h3_pts = new float [] {
                cx - digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - digi_step - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
                cx - digi_step - obj_w2 * (float) cos(PI / 6), digi_hcy1 + obj_w2 * (float) sin(PI / 6),
                cx - digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_hcy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        h_tria = new Path();
        h_tria.rewind();
        h_tria.moveTo(digi_h3_pts[0], digi_h3_pts[1]); h_tria.lineTo(digi_h3_pts[2], digi_h3_pts[3]);
        h_tria.lineTo(digi_h3_pts[4], digi_h3_pts[5]); h_tria.lineTo(digi_h3_pts[6], digi_h3_pts[7]);

        digi_h4_pts = new float [] {
                cx - digi_step, digi_hcy2 - obj_w2,
                cx - digi_step + obj_w2, digi_hcy2,
                cx - digi_step + obj_w2, digi_hcy2,
                cx - digi_step, digi_hcy2 + obj_w2,
                cx - digi_step, digi_hcy2 + obj_w2,
                cx - digi_step - obj_w2, digi_hcy2,
                cx - digi_step - obj_w2, digi_hcy2,
                cx - digi_step, digi_hcy2 - obj_w2,
        };
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


        digi_h8_pts = new float [] {
                cx - digi_step - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(10 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(10 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(8 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(8 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(6 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(6 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(4 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(4 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(2 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(2 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(0 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(0 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(14 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(14 * PI / 8),
                cx - digi_step - obj_w2 * (float) cos(12 * PI / 8), digi_hcy2 + obj_w2 * (float) sin(12 * PI / 8),
        };
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

        digi_m3_pts = new float [] {
                cx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        m_tria = new Path();
        m_tria.rewind();
        m_tria.moveTo(digi_m3_pts[0], digi_m3_pts[1]); m_tria.lineTo(digi_m3_pts[2], digi_m3_pts[3]);
        m_tria.lineTo(digi_m3_pts[4], digi_m3_pts[5]); m_tria.lineTo(digi_m3_pts[6], digi_m3_pts[7]);

        digi_m4_pts = new float [] {
                cx, cy - obj_w2,
                cx + obj_w2, cy,
                cx + obj_w2, cy,
                cx, cy + obj_w2,
                cx, cy + obj_w2,
                cx - obj_w2, cy,
                cx - obj_w2, cy,
                cx, cy - obj_w2,
        };
        m_quad = new Path();
        m_quad.rewind();
        m_quad.moveTo(digi_m4_pts[0], digi_m4_pts[1]); m_quad.lineTo(digi_m4_pts[2], digi_m4_pts[3]);
        m_quad.lineTo(digi_m4_pts[4], digi_m4_pts[5]); m_quad.lineTo(digi_m4_pts[6], digi_m4_pts[7]);
        m_quad.lineTo(digi_m4_pts[8], digi_m4_pts[9]); m_quad.lineTo(digi_m4_pts[10], digi_m4_pts[11]);
        m_quad.lineTo(digi_m4_pts[12], digi_m4_pts[13]); m_quad.lineTo(digi_m4_pts[14], digi_m4_pts[15]);

        digi_m5_pts = new float [] {
                cx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 *  PI / 10),
                cx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 *  PI / 10),
                cx - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
                cx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 *  PI / 10),
                cx - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
                cx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
                cx - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 *  PI / 10),
                cx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 *  PI / 10),
                cx - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
                cx - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
        };
        m_pent = new Path();
        m_pent.rewind();
        m_pent.moveTo(digi_m5_pts[0], digi_m5_pts[1]); m_pent.lineTo(digi_m5_pts[2], digi_m5_pts[3]);
        m_pent.lineTo(digi_m5_pts[4], digi_m5_pts[5]); m_pent.lineTo(digi_m5_pts[6], digi_m5_pts[7]);
        m_pent.lineTo(digi_m5_pts[8], digi_m5_pts[9]); m_pent.lineTo(digi_m5_pts[10], digi_m5_pts[11]);
        m_pent.lineTo(digi_m5_pts[12], digi_m5_pts[13]); m_pent.lineTo(digi_m5_pts[14], digi_m5_pts[15]);
        m_pent.lineTo(digi_m5_pts[16], digi_m5_pts[17]); m_pent.lineTo(digi_m5_pts[18], digi_m5_pts[19]);

        digi_s3_pts = new float [] {
                cx + digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(5 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(5 * PI / 6),
                cx + digi_step - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx + digi_step - obj_w2 * (float) cos(PI / 6), digi_mscy1 + obj_w2 * (float) sin(PI / 6),
                cx + digi_step - obj_w2 * (float) cos(9 * PI / 6), digi_mscy1 + obj_w2 * (float) sin(9 * PI / 6),
        };
        s_tria = new Path();
        s_tria.rewind();
        s_tria.moveTo(digi_s3_pts[0], digi_s3_pts[1]); s_tria.lineTo(digi_s3_pts[2], digi_s3_pts[3]);
        s_tria.lineTo(digi_s3_pts[4], digi_s3_pts[5]); s_tria.lineTo(digi_s3_pts[6], digi_s3_pts[7]);

        digi_s4_pts = new float [] {
                cx + digi_step, cy - obj_w2,
                cx + digi_step + obj_w2, cy,
                cx + digi_step + obj_w2, cy,
                cx + digi_step, cy + obj_w2,
                cx + digi_step, cy + obj_w2,
                cx + digi_step - obj_w2, cy,
                cx + digi_step - obj_w2, cy,
                cx + digi_step, cy - obj_w2,
        };
        s_quad = new Path();
        s_quad.rewind();
        s_quad.moveTo(digi_s4_pts[0], digi_s4_pts[1]); s_quad.lineTo(digi_s4_pts[2], digi_s4_pts[3]);
        s_quad.lineTo(digi_s4_pts[4], digi_s4_pts[5]); s_quad.lineTo(digi_s4_pts[6], digi_s4_pts[7]);
        s_quad.lineTo(digi_s4_pts[8], digi_s4_pts[9]); s_quad.lineTo(digi_s4_pts[10], digi_s4_pts[11]);
        s_quad.lineTo(digi_s4_pts[12], digi_s4_pts[13]); s_quad.lineTo(digi_s4_pts[14], digi_s4_pts[15]);

        digi_s5_pts = new float [] {
                cx + digi_step - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(11 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(11 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(7 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(7 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(3 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(3 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 *  PI / 10),
                cx + digi_step - obj_w2 * (float) cos(19 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(19 * PI / 10),
                cx + digi_step - obj_w2 * (float) cos(15 * PI / 10), digi_mscy3 + obj_w2 * (float) sin(15 * PI / 10),
        };
        s_pent = new Path();
        s_pent.rewind();
        s_pent.moveTo(digi_s5_pts[0], digi_s5_pts[1]); s_pent.lineTo(digi_s5_pts[2], digi_s5_pts[3]);
        s_pent.lineTo(digi_s5_pts[4], digi_s5_pts[5]); s_pent.lineTo(digi_s5_pts[6], digi_s5_pts[7]);
        s_pent.lineTo(digi_s5_pts[8], digi_s5_pts[9]); s_pent.lineTo(digi_s5_pts[10], digi_s5_pts[11]);
        s_pent.lineTo(digi_s5_pts[12], digi_s5_pts[13]); s_pent.lineTo(digi_s5_pts[14], digi_s5_pts[15]);
        s_pent.lineTo(digi_s5_pts[16], digi_s5_pts[17]); s_pent.lineTo(digi_s5_pts[18], digi_s5_pts[19]);

    }

    // fields that control layout of digital clock elements (except the polygons)
    protected float digi_step, digi_ty, digi_hcy1, digi_hcy2, digi_hty1, digi_hty2,
            digi_mscy1, digi_mscy3, digi_msty1, digi_msty3, obj_w2;
    // arrays that store points to draw the polygons
    protected float [] digi_h3_pts, digi_h4_pts, digi_h8_pts, digi_m3_pts, digi_m4_pts, digi_m5_pts,
            digi_s3_pts, digi_s4_pts, digi_s5_pts;
    // used to draw polygons
    protected Path h_tria, h_quad, h_octo, m_tria, m_quad, m_pent, s_tria, s_quad, s_pent;

    protected Paint digi_paint;

    protected static final String tag = "Shady";

}
