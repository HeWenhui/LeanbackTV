package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;


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
        boolean useDefualtFont = a.getBoolean(R.styleable.ArtsTextView_useDefultTextFont,true);
        a.recycle();

        if (suffixResId > 0) {
            icon = BitmapFactory.decodeResource(context.getResources(), suffixResId);
            //Loger.e("ArtsAnswerTextView","======> padding:"+icon.getWidth()+":"+drawableLeftMargin);
            int rightPadding = icon.getWidth() + drawableLeftMargin;
            this.setPadding(0, 0, rightPadding > 0?rightPadding:0, drawableTopMargin);
            iconPaint = new Paint();
            iconPaint.setFilterBitmap(true);
            iconPaint.setAntiAlias(true);
        }
        // 加载字体
        if(!useDefualtFont){
            Typeface fontFace = FontCache.getTypeface(getContext(), "fangzhengcuyuan.ttf");
            if(fontFace != null){
                setTypeface(fontFace);
            }
        }
    }
    /**
     *设置显示内容
     * @param textStr
     */
    public final void setTextWithIcon(CharSequence textStr){
        setText(textStr);
    }


    public void setIconResId(int resId) {
        icon = BitmapFactory.decodeResource(getContext().getResources(), resId);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIcon(canvas);
        //Loger.e("ArtsAnswerTextView","========> padding:"+this.getPaddingRight());
    }


    private void drawIcon(Canvas canvas) {

        if (icon != null) {
            int lindeIndex = getLayout().getLineCount() - 1;
            lindeIndex = lindeIndex < 0 ? 0 : lindeIndex;
            if (lastLineBound == null) {
                lastLineBound = new Rect();
            }
            getLayout().getLineBounds(lindeIndex, lastLineBound);
            //getLayout.getl

            int characterIndex = getText().length();
            int top = lastLineBound.top + drawableTopMargin;
            int left = 0;
            if(characterIndex > 0){
                left = (int) getLayout().getSecondaryHorizontal(characterIndex) + drawableLeftMargin;
            }
            canvas.drawBitmap(icon, left, top, iconPaint);
        }
    }
}
