package com.xueersi.parentsmeeting.modules.livevideoOldIJK.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseItemAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * 半身直播 聊天消息通
 * 当前存在问题： 恰好在 data.clear 时来新消息 ，此时该消息展示不出来
 *
 * @author chekun
 * created  at 2018/11/9 10:06
 */
public class PkPraiseMsgAnimator extends BaseItemAnimator {
    private static final String Tag = "MsgItemAnimator";

    /**
     * 当前可见得item
     */
    private List<RecyclerView.ViewHolder> mVisibleItemList = new ArrayList<>();

    private static final float DEF_MIN_ALPHA = 0.25f;
    /**
     * 每个item 消失动画持续时间
     */
    private static final long ITEM_FADE_OUT_DURATION = 6000L;
    /**
     * item 可见 持续时间
     */
    private static final long ITEM_VISIBLE_DURATION = 6000L;
    private Handler mHandler;
    private static final float MARQUEE_ANIM_DISPATCH_FRACTION = 0f;
    private ItemFadeOutListener itemFadeOutListener;


    public PkPraiseMsgAnimator() {
    }

    @Override
    public void setRemoveAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        //animator.alpha(0);
    }

    @Override
    public void removeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setAlpha(holder.itemView, 1);
        mVisibleItemList.remove(holder);
    }

    @Override
    public void addAnimationInit(RecyclerView.ViewHolder holder) {
       // Log.e("MsgItemAnim", "=====>addAnimationInit called");
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
        ViewCompat.setAlpha(holder.itemView, 1f);
        mVisibleItemList.add(holder);
    }

    @Override
    public void setAddAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
       // cancleFadeOut();
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
        ViewCompat.setPivotX(holder.itemView, 0);
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
       // Log.e("MsgItemAnim", "=====>setAddAnimation called");
        //2秒内无新消息自动消失
    }

    @Override
    public void addAnimationCancel(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
        ViewCompat.setAlpha(holder.itemView, 1);
        //Log.e("MsgItemAnim", "=====>addAnimationCancel called");
    }

    @Override
    public void setOldChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        ViewCompat.setPivotX(holder.itemView, 0);
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(0).scaleY(0);
        ViewCompat.setAlpha(holder.itemView, 1);
    }

    @Override
    public void oldChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
        ViewCompat.setAlpha(holder.itemView, 1);

    }

    @Override
    public void newChangeAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
        ViewCompat.setAlpha(holder.itemView, 1);
    }

    @Override
    public void setNewChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        ViewCompat.setPivotX(holder.itemView,0);
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
        ViewCompat.setAlpha(holder.itemView, 1);
    }

    @Override
    public void newChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 1);
        ViewCompat.setScaleY(holder.itemView, 1);
        ViewCompat.setAlpha(holder.itemView, 1);
    }


    @Override
    public void itemMoveAnimationStart(ViewPropertyAnimatorCompat animator,
                                       RecyclerView.ViewHolder holder, int startX, int startY, int toX, int toY) {
        float alphaValue = getItemAlpha(holder, toY);
        animator.alpha(alphaValue);

        if(!mVisibleItemList.contains(holder)){
            mVisibleItemList.add(holder);
        }
    }

    private float getItemAlpha(RecyclerView.ViewHolder holder, int toY) {
        float alphaValue = 1.0f;
        if (toY < 0) {
            alphaValue = 0.0f;
        } else {
            if ((toY - holder.itemView.getHeight()) > 0) {
                float offsetY = toY + holder.itemView.getHeight();
                alphaValue = offsetY * 1.0f / (float) ((ViewGroup) holder.itemView.getParent()).getMeasuredHeight();
            } else {
                float offsetY = toY + holder.itemView.getHeight() / 2;
                alphaValue = offsetY * 1.0f / (float) ((ViewGroup) holder.itemView.getParent()).getMeasuredHeight();
            }
            alphaValue = alphaValue <= DEF_MIN_ALPHA ? DEF_MIN_ALPHA : alphaValue;
        }
        return alphaValue;
    }

    @Override
    public void itemMoveAnimationEnd(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getAlpha() <= 0) {
            mVisibleItemList.remove(holder);
        }
    }


    private boolean fadeOutCancle;
    private int mCurrentItemIndex;

    /**
     * item 价格逐渐消失
     */
    private void fadeOut() {
        //Log.e("MsgItemAnim", "=====>fadeOut called:visibleItemSize=" + mVisibleItemList.size()+":"+mCurrentItemIndex);
        if (mCurrentItemIndex < mVisibleItemList.size()) {
            RecyclerView.ViewHolder holder = mVisibleItemList.get(mCurrentItemIndex);
            itemFadeOut(holder, mCurrentItemIndex);
        }
    }


    /*private void cancleFadeOut() {
        fadeOutCancle = true;
        if (animatorList.size() > 0) {
            for (int i = 0; i < animatorList.size(); i++) {
                if (animatorList.get(i).isRunning()) {
                    animatorList.get(i).cancelAnim();
                }
            }
        }
    }*/


    private List<FadeOutAnimator> animatorList = new ArrayList<>();

    private void itemFadeOut(RecyclerView.ViewHolder holder, int itemIndex) {
        FadeOutAnimator animator = null;
        //Log.e("MsgItemAnim", "=====>itemFadeOut ListSize:" + animatorList.size());
        //从复用池中寻找
        if (animatorList != null && animatorList.size() > 0) {
            for (int i = 0; i < animatorList.size(); i++) {
                if (!animatorList.get(i).isRunning()) {
                    animator = animatorList.get(i);
                }
            }
        }
        if (animator == null) {
            animator = new FadeOutAnimator();
            //添加到复用池中
            animatorList.add(animator);
        }
        animator.bind2View(holder.itemView, itemIndex);
        animator.startAnim();

    }


    class FadeOutAnimator implements ValueAnimator.AnimatorUpdateListener {
        ObjectAnimator fadeOutAnim;
        private int mItemIndex;
        private View mTargetView;
        private float mStartAlpha;
        private boolean animDispatched = false;

        public FadeOutAnimator() {
        }

        public void bind2View(View targetView, int itemIndex) {
            if (fadeOutAnim != null) {
                fadeOutAnim.cancel();
                fadeOutAnim.removeAllUpdateListeners();
                fadeOutAnim.removeAllListeners();
            }
            animDispatched = false;
            mItemIndex = itemIndex;
            mTargetView = targetView;
            mStartAlpha = targetView.getAlpha();
            fadeOutAnim = ObjectAnimator.ofFloat(targetView, "alpha", mStartAlpha, 0);
            fadeOutAnim.setDuration((long) (ITEM_FADE_OUT_DURATION*mStartAlpha)).addUpdateListener(this);
        }


        public boolean isRunning() {
            return fadeOutAnim.isRunning();
        }

        public void startAnim() {
            fadeOutAnim.start();
        }

        /**
         * 取消当前动画，并把View 恢复成初始状态
         */
        public void cancelAnim() {
            fadeOutAnim.cancel();
            fadeOutAnim.removeAllUpdateListeners();
            fadeOutAnim.removeAllListeners();
           /* if (mTargetView.getAlpha() <= DEF_MIN_ALPHA) {
                mTargetView.setAlpha(DEF_MIN_ALPHA);
            }*/
        }


        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (!animDispatched && !fadeOutCancle && animation.getAnimatedFraction() > MARQUEE_ANIM_DISPATCH_FRACTION) {
                animDispatched = true;
                mCurrentItemIndex++;
                fadeOut();
            }
            //最后一个item 动画执行结束
            if (animation.getAnimatedFraction() >= 1 && mItemIndex == (mVisibleItemList.size() - 1) && !fadeOutCancle) {
                if (itemFadeOutListener != null) {
                    itemFadeOutListener.onAnimEnd();
                    mVisibleItemList.clear();
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
       // cancleFadeOut();
        mHandler = null;
        if (mVisibleItemList != null) {
            mVisibleItemList.clear();
        }
        itemFadeOutListener = null;

        if (animatorList != null) {
            animatorList.clear();
        }
    }

    public void addFadeOutAnimListener(ItemFadeOutListener listener) {
        itemFadeOutListener = listener;
    }

    /**
     * item 淡出动画监听
     */
    public static interface ItemFadeOutListener {
        /**
         * 动画结束
         */
        void onAnimEnd();
    }

}
