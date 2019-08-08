package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

/**
 * Created by linyuqiang on 2018/1/24.
 * 直播的相对布局
 */
public class LiveRelativeLayout extends RelativeLayout {
    String TAG = "LiveRelativeLayout";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

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
        logger.d( "setLayoutParams:params=" + params.width + "," + params.height);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        logger.d( "addView:id=" + child.getId());
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        logger.d( "addView:id=" + child.getId() + ",params=" + params.width + "," + params.height);
    }

    @Override
    public void removeView(View view) {
        logger.d( "removeView:id=" + view.getId());
        super.removeView(view);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        logger.d( "setVisibility:visibility=" + visibility);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        logger.d( "removeAllViews");
    }

}
