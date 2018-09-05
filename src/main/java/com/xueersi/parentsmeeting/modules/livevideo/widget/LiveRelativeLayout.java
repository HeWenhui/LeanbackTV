package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

/**
 * Created by linyuqiang on 2018/1/24.
 * 直播的相对布局
 */
public class LiveRelativeLayout extends RelativeLayout {
    String TAG = "LiveRelativeLayout";

    public LiveRelativeLayout(Context context) {
        super(context);
    }

    public LiveRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        Loger.d(TAG, "setLayoutParams:params=" + params.width + "," + params.height);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        Loger.d(TAG, "addView:id=" + child.getId());
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        Loger.d(TAG, "addView:id=" + child.getId() + ",params=" + params.width + "," + params.height);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        Loger.d(TAG, "removeView:id=" + view.getId());
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Loger.d(TAG, "setVisibility:visibility=" + visibility);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        Loger.d(TAG, "removeAllViews");
    }

}
