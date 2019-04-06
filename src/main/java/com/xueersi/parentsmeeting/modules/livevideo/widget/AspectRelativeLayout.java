package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by lenovo on 2019/2/18.
 */

public class AspectRelativeLayout extends RelativeLayout {

    private float aspect=0;

    public AspectRelativeLayout(Context context) {
        this(context,null);
    }

    public AspectRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AspectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRelativeLayout);
        aspect = a.getFloat(R.styleable.AspectRelativeLayout_content_aspect,0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (aspect>0){
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = (int) (widthSize / aspect);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
