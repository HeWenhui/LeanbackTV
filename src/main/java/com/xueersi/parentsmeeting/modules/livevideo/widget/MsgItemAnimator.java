package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * 半身直播 聊天消息通
 *
 * @author chekun
 * created  at 2018/11/9 10:06
 */
public class MsgItemAnimator extends BaseItemAnimator {
    private static final String Tag = "MsgItemAnimator";

    /**
     * 当前可见得item
     */
    private List<RecyclerView.ViewHolder> mVisibleItemList = new ArrayList<>();

    private static final float DEF_MIN_ALPHA = 0.25f;
    /**
     * 没个item 消失动画持续时间
     */
    private static final long ITEM_FADE_OUT_DURATION = 600L;
    /**
     * item 消失  间隔时间
     */
    private static final long ITEM_FADE_OUT_DELAY = 800L;
    /**
     * item 可见 持续时间
     */
    private static final long ITEM_VISIBLE_DURATION = 2000L;
    private Handler mHandler;
    private static final float MARQUEE_ANIM_DISPATCH_FRACTION = 0.3f;
    private ItemFadeOutListener itemFadeOutListener;


    @Override
    public void setRemoveAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        animator.alpha(0);
    }

    @Override
    public void removeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setAlpha(holder.itemView, 1);
        mVisibleItemList.remove(holder);
    }

    @Override
    public void addAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
        ViewCompat.setAlpha(holder.itemView, 1f);

    }

    @Override
    public void setAddAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
        ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
        mVisibleItemList.add(holder);
        cancleFadeOut();
        //2秒内无新消息自动消失
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOut();
            }
        }, ITEM_VISIBLE_DURATION );
    }

    @Override
    public void addAnimationCancel(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
    }

    @Override
    public void setOldChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(0).scaleY(0);
    }

    @Override
    public void oldChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
    }

    @Override
    public void newChangeAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
    }

    @Override
    public void setNewChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
    }

    @Override
    public void newChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
    }

    @Override
    public void itemMoveAnimationStart(ViewPropertyAnimatorCompat animator,
                                       RecyclerView.ViewHolder holder, int startX, int startY, int toX, int toY) {
        float alphaValue = getItemAlpha(holder, toY);
        animator.alpha(alphaValue);
        if (alphaValue == 0) {
            mVisibleItemList.remove(holder);
        }
    }

    private float getItemAlpha(RecyclerView.ViewHolder holder, int toY) {
        float alphaValue = 1.0f;
        if (toY < 0) {
            alphaValue = 0.0f;
        } else {
            float itemCenterY = toY - holder.itemView.getHeight() / 2;
            alphaValue = itemCenterY * 1.0f / (float) ((ViewGroup) holder.itemView.getParent()).getMeasuredHeight();
            alphaValue = alphaValue <= DEF_MIN_ALPHA ? DEF_MIN_ALPHA : alphaValue;
        }
        return alphaValue;
    }

    @Override
    public void itemMoveAnimationEnd(RecyclerView.ViewHolder holder) {
        super.itemMoveAnimationEnd(holder);
    }


    int itemIndex = 0;
    private boolean fadeOutCancle;

    /**
     * item 价格逐渐消失
     */
    private void fadeOut() {
        fadeOutCancle = false;
        if (itemIndex < mVisibleItemList.size()) {
            RecyclerView.ViewHolder holder = mVisibleItemList.get(itemIndex);
            itemFadeOut(holder);
        }
    }


    private void cancleFadeOut() {
        itemIndex = 0;
        fadeOutCancle = true;
    }

    private void itemFadeOut(RecyclerView.ViewHolder holder) {
        ObjectAnimator fadeOutAnim = ObjectAnimator.ofFloat(holder.itemView, "alpha",
                holder.itemView.getAlpha(), 0);
        fadeOutAnim.setDuration(ITEM_FADE_OUT_DURATION).addUpdateListener(new ItemAnimUpdateListener(itemIndex));
        fadeOutAnim.start();
    }

    class ItemAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private boolean animDispatched = false;
        private int mItemIndex;
        public ItemAnimUpdateListener(int index){
            mItemIndex = index;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (!animDispatched && !fadeOutCancle && animation.getAnimatedFraction() > MARQUEE_ANIM_DISPATCH_FRACTION) {
                animDispatched = true;
                itemIndex++;
                fadeOut();
            }
            //最后一个item 动画执行结束
            if(animation.getAnimatedFraction() >=1 && mItemIndex == (mVisibleItemList.size()-1)){
                if(itemFadeOutListener != null){
                    itemFadeOutListener.onAnimEnd();
                    mVisibleItemList.clear();
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        cancleFadeOut();
        mHandler = null;
        if(mVisibleItemList != null){
            mVisibleItemList.clear();
        }
        itemFadeOutListener = null;
    }

    public void addFadeOutAnimListener(ItemFadeOutListener listener){
        itemFadeOutListener = listener;
    }

    /**
     * item 淡出动画监听
     */
    public static interface ItemFadeOutListener{
        /**
         * 动画结束
         */
       void onAnimEnd();
    }

}
