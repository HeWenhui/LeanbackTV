package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

public class ShadowTextView extends FangZhengCuYuanTextView {
    int color;
    float radius;
    float dx;
    float dy;

    public ShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowTextView);
        color = typedArray.getColor(R.styleable.ShadowTextView_shadow_shadowColor, 0x00000000);
        dx = typedArray.getFloat(R.styleable.ShadowTextView_shadow_shadowDx, 0);
        dy = typedArray.getFloat(R.styleable.ShadowTextView_shadow_shadowDy, 0);
        radius = typedArray.getFloat(R.styleable.ShadowTextView_shadow_shadowRadius, 0);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextPaint textPaint = getPaint();
//        textPaint.setColor(color);
        textPaint.setShadowLayer(radius, dx, -dy, color);
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        //int baseLine = (height - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
        int baseLine = (getHeight() - (fontMetricsInt.descent - fontMetricsInt.ascent)) / 2 - fontMetricsInt.ascent;
//        float x = (getWidth() - textWidth) / 2;
        float x = getPaddingLeft();
        canvas.drawText("" + getText(), x, baseLine, textPaint);
        textPaint.setShadowLayer(radius, -dx, -dy, color);
        canvas.drawText("" + getText(), x, baseLine, textPaint);
        textPaint.setShadowLayer(radius, -dx, dy, color);
        canvas.drawText("" + getText(), x, baseLine, textPaint);
        textPaint.setShadowLayer(radius, dx, dy, color);
        canvas.drawText("" + getText(), x, baseLine, textPaint);
        textPaint.setShadowLayer(radius, dx, dy, color);
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
