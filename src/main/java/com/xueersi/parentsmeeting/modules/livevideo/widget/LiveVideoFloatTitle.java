package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by linyuqiang on 2016/9/26.
 * 直播弹窗
 */
public class LiveVideoFloatTitle extends RelativeLayout {
    OnCancleClick onCancleClick;

    public LiveVideoFloatTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_livefloat_title, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LiveVideoFloatTitle,
                0, 0);
        String title = typedArray.getString(R.styleable.LiveVideoFloatTitle_floattitle);
        //显示退出，默认显示
        boolean showcancle = typedArray.getBoolean(R.styleable.LiveVideoFloatTitle_showcancle, true);
        //显示背景，默认显示
        boolean showback = typedArray.getBoolean(R.styleable.LiveVideoFloatTitle_showback, true);
        typedArray.recycle();
        ((TextView) findViewById(R.id.tv_livevideo_float_title)).setText(title);
        if (showcancle) {
            findViewById(R.id.im_livevideo_float_cancle).setVisibility(VISIBLE);
            findViewById(R.id.im_livevideo_float_cancle).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCancleClick != null) {
                        onCancleClick.onCancleClick();
                    }
                }
            });
        } else {
            findViewById(R.id.im_livevideo_float_cancle).setVisibility(GONE);
        }
        if (showback) {
            setBackgroundResource(R.drawable.shape_livevideo_floattitle_bg);
        }
    }

    public void setOnCancleClick(OnCancleClick onCancleClick) {
        this.onCancleClick = onCancleClick;
    }

    public interface OnCancleClick {
        void onCancleClick();
    }
}
