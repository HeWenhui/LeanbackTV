package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
/**
*半身直播 聊天消息布局管理器
*@author chekun
*created  at 2018/11/12 9:42
*/
public class LiveMsgLayoutManager extends LinearLayoutManager {

    private boolean mVScrollAble;

    public LiveMsgLayoutManager(Context context) {
        super(context);
    }

    public LiveMsgLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LiveMsgLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * 设置是否可 上下滑动
     * @param scrollAble
     */
    public void setVScrollAble(boolean scrollAble){
        mVScrollAble = scrollAble;
    }


    @Override
    public boolean canScrollVertically() {
        return  mVScrollAble && super.canScrollVertically();
    }
}
