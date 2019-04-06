package com.xueersi.parentsmeeting.modules.livevideo.chpk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by lenovo on 2019/1/31.
 */

public class AwardNumberView extends View {

    int[] numberImages = new int[]{
            R.drawable.livevideo_chpk_goldvalue0,
            R.drawable.livevideo_chpk_goldvalue1,
            R.drawable.livevideo_chpk_goldvalue2,
            R.drawable.livevideo_chpk_goldvalue3,
            R.drawable.livevideo_chpk_goldvalue4,
            R.drawable.livevideo_chpk_goldvalue5,
            R.drawable.livevideo_chpk_goldvalue6,
            R.drawable.livevideo_chpk_goldvalue7,
            R.drawable.livevideo_chpk_goldvalue8,
            R.drawable.livevideo_chpk_goldvalue9
    };

    private Drawable[] values;
    private int maxHeight;
    private int sumWidth;

    public AwardNumberView(Context context) {
        this(context, null);
    }

    public AwardNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwardNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGoldNumber(int number,int prefix,int after) {

        String lable = String.valueOf(number);
        int length = lable.length();
        values = new Drawable[length + 2];

//        values[0] = ContextCompat.getDrawable(getContext(), R.drawable.livevideo_chpk_goldvaluel);
        values[0] = ContextCompat.getDrawable(getContext(), prefix);

        for (int i = 0; i < length; i++) {
            String subString = lable.substring(i, i + 1);
            int index = Integer.parseInt(subString);

            int resid = numberImages[index];
            values[i + 1] = ContextCompat.getDrawable(getContext(), resid);
        }

//        values[length + 1] = ContextCompat.getDrawable(getContext(), R.drawable.livevideo_chpk_goldvaluer);
        values[length + 1] = ContextCompat.getDrawable(getContext(), after);

        maxHeight = 0;
        sumWidth = 0;
        for (Drawable drawable : values) {
            maxHeight = Math.max(maxHeight, drawable.getIntrinsicHeight());
            sumWidth = sumWidth + drawable.getIntrinsicWidth();
        }

        if (getMeasuredHeight() != 0) {
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (values == null || values.length <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int usedWidth = 0;
            int usedHeight = 0;

            if (heightMode == MeasureSpec.EXACTLY) {
                usedHeight = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                usedHeight = Math.min(heightSize, maxHeight);
            } else {
                usedHeight = maxHeight;
            }

            float scale = usedHeight / (float) maxHeight;

            if (widthMode == MeasureSpec.EXACTLY) {
                usedWidth = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                usedWidth = sumWidth * usedHeight / maxHeight;
                usedWidth = (int) (usedWidth * scale);
            } else {
                usedWidth = sumWidth * usedHeight / maxHeight;
            }

            setMeasuredDimension(usedWidth, usedHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.length <= 0) {
            return;
        }

        int thisWidth = getMeasuredWidth();
        int thisHeight = getMeasuredHeight();

        float scaled = thisHeight / (float) maxHeight;
        int drawWidth = (int) (sumWidth * scaled);
        int drawLeft = thisWidth / 2 - drawWidth / 2;

        for (Drawable logo : values) {
            int logoWidth = (int) (logo.getIntrinsicWidth() * scaled);
            int logoHeight = (int) (logo.getIntrinsicHeight() * scaled);
            int drawTop = thisHeight / 2 - logoHeight / 2;

            logo.setBounds(drawLeft, drawTop, drawLeft + logoWidth, drawTop + logoHeight);
            drawLeft = drawLeft + logoWidth;
            logo.draw(canvas);
        }
    }

}
