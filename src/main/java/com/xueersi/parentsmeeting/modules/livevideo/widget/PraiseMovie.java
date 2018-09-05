package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

/**
 * 文科表扬榜
 * @author chekun
 * created  at 2018/7/20 10:24
 */
public class PraiseMovie {

    static class Curve {
        final float radioX;
        final float radioY;
        public Curve(float radioX, float radioY) {
            this.radioX = radioX;
            this.radioY = radioY;
        }
    }

    static int countflag;
    private Drawable drawable;
    private Curve[] movepath;
    private float progress;
    private long duration;
    private long fromtime;

    public PraiseMovie(Drawable drawable, long duration) {
        this.drawable = drawable;
        this.duration = duration;
        this.fromtime = 0;
        countflag++;

        if (countflag % 3 == 0) {
            movepath = new Curve[5];
            movepath[0] = new Curve(0.50f, 0.90f);
            movepath[1] = new Curve(0.44f, 0.70f);
            movepath[2] = new Curve(0.38f, 0.50f);
            movepath[3] = new Curve(0.32f, 0.30f);
            movepath[4] = new Curve(0.26f, 0.10f);
        } else if (countflag % 3 == 1) {
            movepath = new Curve[5];
            movepath[0] = new Curve(0.50f, 0.90f);
            movepath[1] = new Curve(0.50f, 0.70f);
            movepath[2] = new Curve(0.50f, 0.50f);
            movepath[3] = new Curve(0.50f, 0.30f);
            movepath[4] = new Curve(0.50f, 0.10f);
        } else {
            movepath = new Curve[5];
            movepath[0] = new Curve(0.50f, 0.90f);
            movepath[1] = new Curve(0.56f, 0.70f);
            movepath[2] = new Curve(0.62f, 0.50f);
            movepath[3] = new Curve(0.68f, 0.30f);
            movepath[4] = new Curve(0.74f, 0.10f);
        }
    }

    public boolean isFinish() {
        return progress >= 1;
    }

    public void move() {
        long currtime = System.currentTimeMillis();

        if (fromtime <= 0) {
            fromtime = currtime;
        }

        long timepast = currtime - fromtime;

        if (timepast < 0) {
            fromtime = currtime;
            timepast = 0;
        }

        progress = timepast / (float) duration;
        progress = Math.max(progress, 0);
        progress = Math.min(progress, 1);

    }

    DecimalFormat format = new DecimalFormat("0.00");

    public void draw(Canvas canvas, int screenWidth, int screenHeight) {
        float perio = 1.0f / (movepath.length - 1);

        int index1 = (int) (progress / perio);

        int index2 = index1 + 1;

        float radiox = 0, radioy = 0;

        if (index2 >= movepath.length) {
            Curve curve = movepath[index1];
            radiox = curve.radioX;
            radioy = curve.radioY;
        } else {
            Curve c1 = movepath[index1];
            Curve c2 = movepath[index2];

            float t1 = perio * index1;
            float t2 = perio * index2;

            radiox = c1.radioX + (c2.radioX - c1.radioX) * (progress - t1) / (t2 - t1);
            radioy = c1.radioY + (c2.radioY - c1.radioY) * (progress - t1) / (t2 - t1);
        }

        int centerX = (int) (screenWidth * radiox);
        int centerY = (int) (screenHeight * radioy);

        float scale = 1.0f;

        if (progress < 0.5f) {
            scale = progress + 0.5f;
        }

        float alpha = 1.0f;

        if (progress > 0.5f) {
            alpha = 1 - progress;
        }

        int intAlpha = (int) (255 * alpha);

        int imageWidth = (int) (scale * drawable.getIntrinsicWidth());
        int imageHeight = (int) (scale * drawable.getIntrinsicHeight());

        int left = centerX - imageWidth / 2;
        int right = left + imageWidth;

        int top = centerY - imageHeight / 2;
        int bottom = top + imageHeight;

        drawable.setBounds(left, top, right, bottom);
        drawable.setAlpha(intAlpha);
        drawable.draw(canvas);
    }

}
