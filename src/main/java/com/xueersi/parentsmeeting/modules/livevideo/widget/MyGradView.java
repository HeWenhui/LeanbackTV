package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by David on 2018/9/8.
 */
public class MyGradView extends GridView {
    public MyGradView(Context context) {
        super(context);
    }

    public MyGradView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGradView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
