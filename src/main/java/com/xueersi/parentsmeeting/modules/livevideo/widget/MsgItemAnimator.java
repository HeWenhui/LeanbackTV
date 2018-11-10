package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.Animator;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
* 半身直播 聊天消息通
*@author chekun
*created  at 2018/11/9 10:06
*/
public class MsgItemAnimator extends BaseItemAnimator {
    private static final String Tag = "MsgItemAnimator";

    /**当前可见得item*/
    private List<RecyclerView.ViewHolder> mVisibleItemList = new ArrayList<>();

    private static final float DEF_MIN_ALPHA = 0.25f;

    @Override
    public void setRemoveAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        Log.e(Tag,"======>setRemoveAnimation called");
        animator.alpha(0);
    }

    @Override
    public void removeAnimationEnd(RecyclerView.ViewHolder holder) {
        Log.e(Tag,"======>removeAnimationEnd called");
        ViewCompat.setAlpha(holder.itemView,1);
    }

    @Override
    public void addAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView,0);
        ViewCompat.setScaleY(holder.itemView,0);
        ViewCompat.setAlpha(holder.itemView,1f);
    }

    @Override
    public void setAddAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        ViewCompat.setPivotX(holder.itemView,holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView,holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
    }

    @Override
    public void addAnimationCancel(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView,1);
        ViewCompat.setScaleY(holder.itemView,1);
    }

    @Override
    public void setOldChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        Log.e(Tag,"======>setOldChangeAnimation called");
        ViewCompat.setPivotX(holder.itemView,holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView,holder.itemView.getHeight());
        animator.scaleX(0).scaleY(0);
    }

    @Override
    public void oldChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        Log.e(Tag,"======>oldChangeAnimationEnd called");
        ViewCompat.setScaleX(holder.itemView,1);
        ViewCompat.setScaleY(holder.itemView,1);
    }

    @Override
    public void newChangeAnimationInit(RecyclerView.ViewHolder holder) {
        Log.e(Tag,"======>newChangeAnimationInit called");
        ViewCompat.setScaleX(holder.itemView,0);
        ViewCompat.setScaleY(holder.itemView,0);
    }

    @Override
    public void setNewChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        Log.e(Tag,"======>setNewChangeAnimation called");
        ViewCompat.setPivotX(holder.itemView,holder.itemView.getWidth());
        ViewCompat.setPivotY(holder.itemView,holder.itemView.getHeight());
        animator.scaleX(1).scaleY(1);
    }

    @Override
    public void newChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        Log.e(Tag,"======>newChangeAnimationEnd called");
        ViewCompat.setScaleX(holder.itemView,1);
        ViewCompat.setScaleY(holder.itemView,1);
    }

    @Override
    public void itemMoveAnimationStart(ViewPropertyAnimatorCompat animator,
                                       RecyclerView.ViewHolder holder, int startX, int startY, int toX, int toY) {
        Log.e(Tag,"======>itemMoveAnimationStart called:"+startX+":"+startY+":"+toX+":"+toY +":"+holder);
       // ViewCompat.setAlpha(holder.itemView,getItemAlpha(holder,toY));
        animator.alpha(getItemAlpha(holder,toY));
    }

    private float getItemAlpha(RecyclerView.ViewHolder holder, int toY) {
        float alphaValue  = 1.0f;
        if(toY < 0){
            alphaValue = 0.0f;
        }else{
            float itemCenterY = toY - holder.itemView.getHeight()/2;
            alphaValue = itemCenterY*1.0f/(float) ((ViewGroup)holder.itemView.getParent()).getMeasuredHeight();
            alphaValue = alphaValue <= DEF_MIN_ALPHA ?DEF_MIN_ALPHA:alphaValue;
        }
        Log.e(Tag,"======>getItemAlpha:"+alphaValue);
        return alphaValue;
    }

    @Override
    public void itemMoveAnimationEnd(RecyclerView.ViewHolder holder) {
        super.itemMoveAnimationEnd(holder);
        Log.e(Tag,"======>itemMoveAnimationEnd called");
    }

}
