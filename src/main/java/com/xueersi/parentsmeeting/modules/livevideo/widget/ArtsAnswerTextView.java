package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;


/**
 * 答题统计面板 答案展示textView
 *
 * @author chenkun
 * @version 1.0, 2018/7/31 下午1:54
 */

public class ArtsAnswerTextView extends TextView {

    private Rect lastLineBound;
    private Bitmap icon;
    private int drawableLeftMargin;
    private int drawableTopMargin;
    private Paint iconPaint;

    public ArtsAnswerTextView(Context context) {
        this(context, null);
    }

    public ArtsAnswerTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArtsAnswerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArtsTextView,
                defStyleAttr, 0);
        int suffixResId = a.getResourceId(R.styleable.ArtsTextView_suffixIcon, -1);
        drawableLeftMargin = (int) a.getDimension(R.styleable.ArtsTextView_drawableLeftMargin, 0f);
        drawableTopMargin = (int) a.getDimension(R.styleable.ArtsTextView_drawableTopMargin, 0f);
        a.recycle();

        if (suffixResId > 0) {
            icon = BitmapFactory.decodeResource(context.getResources(), suffixResId);
            Loger.e("ArtsAnswerTextView","======> padding:"+icon.getWidth()+":"+drawableLeftMargin);
            int rightPadding = icon.getWidth() + drawableLeftMargin;
            this.setPadding(0, 0, rightPadding > 0?rightPadding:0, 0);
            iconPaint = new Paint();
            iconPaint.setFilterBitmap(true);
            iconPaint.setAntiAlias(true);
        }

    }


    public void setIconResId(int resId) {
        icon = BitmapFactory.decodeResource(getContext().getResources(), resId);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIcon(canvas);

        Loger.e("ArtsAnswerTextView","========> padding:"+this.getPaddingRight());

    }

    private void drawIcon(Canvas canvas) {

        if (icon != null) {
            int lindeIndex = getLayout().getLineCount() - 1;
            lindeIndex = lindeIndex < 0 ? 0 : lindeIndex;
            if (lastLineBound == null) {
                lastLineBound = new Rect();
            }
            getLayout().getLineBounds(lindeIndex, lastLineBound);
            int characterIndex = getText().length();
            int top = lastLineBound.top + drawableTopMargin;
            int left = (int) getLayout().getSecondaryHorizontal(characterIndex) + drawableLeftMargin;
            canvas.drawBitmap(icon, left, top, iconPaint);
        }
    }
}
