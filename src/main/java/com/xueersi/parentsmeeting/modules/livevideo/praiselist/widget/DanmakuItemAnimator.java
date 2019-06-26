package com.xueersi.parentsmeeting.modules.livevideo.praiselist.widget;

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
public class DanmakuItemAnimator extends BaseItemAnimator {
    private static final String Tag = "DanmakuItemAnimator";
    /**
     * 当前可见得item
     */
    private List<RecyclerView.ViewHolder> mVisibleItemList = new ArrayList<>();
    private static final float DEF_MIN_ALPHA = 0.25f;

    public DanmakuItemAnimator() {
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
        ViewCompat.setPivotX(holder.itemView, 0);
        ViewCompat.setPivotY(holder.itemView, holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
        // Log.e("MsgItemAnim", "=====>setAddAnimation called");
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
        ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
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
        ViewCompat.setPivotX(holder.itemView, holder.itemView.getWidth());
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

        if (!mVisibleItemList.contains(holder)) {
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

    /**
     * 释放资源
     */
    public void release() {
        if (mVisibleItemList != null) {
            mVisibleItemList.clear();
        }
    }
}
