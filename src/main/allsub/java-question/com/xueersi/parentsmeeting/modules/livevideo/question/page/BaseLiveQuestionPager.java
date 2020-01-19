package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.PutQuestion;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by linyuqiang on 2016/12/19.
 */
public abstract class BaseLiveQuestionPager extends LiveBasePager {
    protected PutQuestion putQuestion;

    protected boolean isPostEvent;

    public BaseLiveQuestionPager(Context context) {
        super(context);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (!isPostEvent){
            String testId = baseVideoQuestionEntity.getvQuestionID();
            LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(-1, -1, LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
            event.setBasePager(this);
            event.setCloseByTeahcer(true);
            EventBus.getDefault().post(event);
        }
    }

    public void onSubSuccess(View popupWindow_view, final String testId, final VideoResultEntity entity) {
        if (entity != null) {
            logger.d("onSubSuccess:gold=" + entity.getGoldNum() + ",energy=" + entity.getEnergy());
            View view = popupWindow_view;
            if (popupWindow_view == null) {
                view = getRootView();
            }
            if (view != null) {
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View view) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View view) {
                        LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(entity.getGoldNum(), entity.getEnergy(), LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
                        EventBus.getDefault().post(event);
                        isPostEvent = true;
                    }
                });
            }
        } else {
            logger.d("onSubSuccess:entity=null");
        }
        onSubSuccess();
    }

    public void onSubSuccess() {
    }

    public void onSubFailure(View popupWindow_view, final String testId, final VideoResultEntity entity) {
        if (entity != null) {
            logger.d("onSubFailure:gold=" + entity.getGoldNum() + ",energy=" + entity.getEnergy());
            View view = popupWindow_view;
            if (popupWindow_view == null) {
                view = getRootView();
            }
            if (view != null) {
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View view) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View view) {
                        LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(entity.getGoldNum(), entity.getEnergy(), LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
                        EventBus.getDefault().post(event);
                        isPostEvent = true;
                    }
                });
            }
        } else {
            logger.d("onSubFailure:entity=null");
        }
        onSubFailure();
    }

    public void onSubFailure() {
    }

    public void setPutQuestion(PutQuestion putQuestion) {
        this.putQuestion = putQuestion;
    }

    public void hideInputMode() {
    }

    public void onKeyboardShowing(boolean isShowing) {

    }

    public void onKeyboardShowing(boolean isShowing, int height) {

    }
}
