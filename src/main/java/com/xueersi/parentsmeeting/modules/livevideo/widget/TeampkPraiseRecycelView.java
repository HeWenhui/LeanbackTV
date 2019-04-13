package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 半身直播聊天面板
 *
 * @author chekun
 * created  at 2018/11/10 17:26
 */
public class TeampkPraiseRecycelView extends RecyclerView {

    private PkPraiseMsgAnimator mItemAnimator;

    public TeampkPraiseRecycelView(Context context) {
        this(context,null);
    }

    public TeampkPraiseRecycelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TeampkPraiseRecycelView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        initItemAnimator();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(this.getChildCount() > 0){
            this.scrollToPosition(0);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    private void initItemAnimator() {
        mItemAnimator = new PkPraiseMsgAnimator();
        mItemAnimator.setAddDuration(400);
        mItemAnimator.setMoveDuration(400);
        mItemAnimator.setRemoveDuration(300);
        this.setItemAnimator(mItemAnimator);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mItemAnimator != null){
            mItemAnimator.release();
        }
    }

    public static interface  ItemFadeAnimListener{
        /**
         * 所有item 淡出
         */
        void onAllItemFadeOut();
    }

}
