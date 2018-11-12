package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 半身直播聊天面板
 *
 * @author chekun
 * created  at 2018/11/10 17:26
 */
public class HalfBodyLiveMsgRecycelView extends RecyclerView implements MsgItemAnimator.ItemFadeOutListener {

    private MsgItemAnimator mItemAnimator;
    private ItemFadeAnimListener mItemFadeAnimListener;

    public HalfBodyLiveMsgRecycelView(Context context) {
        this(context,null);
    }

    public HalfBodyLiveMsgRecycelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HalfBodyLiveMsgRecycelView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        initItemAnimator();
    }

    private void initItemAnimator() {
        mItemAnimator = new MsgItemAnimator();
        mItemAnimator.setAddDuration(400);
        mItemAnimator.setMoveDuration(300);
        mItemAnimator.setRemoveDuration(300);
        this.setItemAnimator(mItemAnimator);
        mItemAnimator.addFadeOutAnimListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mItemAnimator != null){
            mItemAnimator.release();
        }
        mItemFadeAnimListener = null;
    }

    @Override
    public void onAnimEnd() {
     if(mItemFadeAnimListener != null){
         mItemFadeAnimListener.onAllItemFadeOut();
     }
    }

    public void setItemFadeAnimListener(ItemFadeAnimListener listener){
        mItemFadeAnimListener = listener;
    }

    public static interface  ItemFadeAnimListener{
        /**
         * 所有item 淡出
         */
        void onAllItemFadeOut();
    }

}
