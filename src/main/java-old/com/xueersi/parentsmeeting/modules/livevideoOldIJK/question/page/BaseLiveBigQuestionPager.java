package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionSecHttp;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.create.BigQueCreate;

/**
 * Created by linyuqiang on 2016/12/19.
 */
public abstract class BaseLiveBigQuestionPager extends LiveBasePager {
    protected QuestionSecHttp questionSecHttp;
    protected VideoQuestionLiveEntity videoQuestionLiveEntity;
    protected RelativeLayout rlQuestionResContent;
    protected BigQueCreate.OnSubmit onSubmit;
    protected boolean isPlayback = false;

    public BaseLiveBigQuestionPager(Context context) {
        super(context);
    }

    public void setRlQuestionResContent(RelativeLayout rlQuestionResContent) {
        this.rlQuestionResContent = rlQuestionResContent;
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
