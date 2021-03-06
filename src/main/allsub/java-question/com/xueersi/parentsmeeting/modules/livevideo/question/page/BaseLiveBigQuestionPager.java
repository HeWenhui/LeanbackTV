package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.PutQuestion;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.create.BigQueCreate;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by linyuqiang on 2016/12/19.
 */
public abstract class BaseLiveBigQuestionPager extends LiveBasePager {
    protected QuestionSecHttp questionSecHttp;
    protected VideoQuestionLiveEntity videoQuestionLiveEntity;
    protected LiveViewAction liveViewAction;
    protected BigQueCreate.OnSubmit onSubmit;
    protected boolean isPlayback = false;

    public BaseLiveBigQuestionPager(Context context) {
        super(context);
    }

    public void setRlQuestionResContent(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
    }

    public void setOnSubmit(BigQueCreate.OnSubmit onSubmit) {
        this.onSubmit = onSubmit;
    }

    public void setPlayback(boolean playback) {
        isPlayback = playback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onSubSuccess() {
    }

    public void onSubFailure() {
    }

    public void setQuestionSecHttp(QuestionSecHttp questionSecHttp) {
        this.questionSecHttp = questionSecHttp;
    }

    public void hideInputMode() {
    }

    public void onKeyboardShowing(boolean isShowing, int height) {

    }

    public void submitData() {

    }
}
