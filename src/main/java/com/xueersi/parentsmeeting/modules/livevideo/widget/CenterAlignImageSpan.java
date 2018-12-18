package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;

/**
*垂直文字对齐
*@author chekun
*created  at 2018/11/10 11:03
*/
public class CenterAlignImageSpan extends ImageSpan {

    public CenterAlignImageSpan(Drawable drawable) {
        super(drawable);
    }

    public CenterAlignImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        Drawable b = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;
        canvas.save();
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

}
