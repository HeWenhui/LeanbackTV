package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

/**
 * Created by linyuqiang on 2017/9/26.
 */
public class SpeechStrokeTextView extends TextView {
    private TextView borderText = null;///用于描边的TextView

    public SpeechStrokeTextView(Context context) {
        super(context);
        borderText = new TextView(context);
        init();
    }

    public SpeechStrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context, attrs);
        init();
    }

    public SpeechStrokeTextView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        borderText = new TextView(context, attrs, defStyle);
        init();
    }

    public void init() {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(3 * ScreenUtils.getScreenDensity() / 2);                                  //设置描边宽度
        tp1.setStyle(Paint.Style.STROKE);                             //对文字只描边
        borderText.setTextColor(0x6f86511b);  //设置描边颜色
        borderText.setGravity(getGravity());
    }

    public TextView getBorderText() {
        return borderText;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
//        borderText.setLayoutParams(params);
        LayoutParamsUtil.setViewLayoutParams(borderText, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        borderText.draw(canvas);
    }
}