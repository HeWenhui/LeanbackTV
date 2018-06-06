package com.xueersi.parentsmeeting.modules.livevideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by lenovo on 2018/5/30.
 */

public class CustomUnScorllListView extends ListView {

    private boolean mIsUnScroll = false;//true 为不可滑动；false为可滑动，默认不可滑动

    public CustomUnScorllListView(Context context) {
        super(context, null);
    }

    public CustomUnScorllListView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CustomUnScorllListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUnScroll(boolean isUnScroll) {
        mIsUnScroll = isUnScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mIsUnScroll) {
                    //禁止滑动
                    Loger.i("RolePlayerDemoTest", "禁止listView滑动");
                    return false;
                } else {
                    return super.onTouchEvent(ev);
                }
        }
        return super.onTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mIsUnScroll) {
                    //禁止滑动
                    Loger.i("RolePlayerDemoTest", "listView不拦截滑动");
                    return false;
                } else {
                    return super.onInterceptTouchEvent(ev);
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
