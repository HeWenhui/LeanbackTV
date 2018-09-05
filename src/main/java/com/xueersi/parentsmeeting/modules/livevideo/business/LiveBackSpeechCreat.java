package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.BaseSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.WrapSpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssessmentWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

/**
 * Created by linyuqiang on 2018/4/7.
 * 直播回放的语音评测创建
 */
public class LiveBackSpeechCreat implements BaseSpeechCreat {
    WrapSpeechEvalAction wrapSpeechEvalAction;
    LivePagerBack livePagerBack;

    public LiveBackSpeechCreat(LivePagerBack livePagerBack) {
        this.livePagerBack = livePagerBack;
    }

    @Override
    public void receiveRolePlay(VideoQuestionLiveEntity videoQuestionLiveEntity) {

    }

    public void setSpeechEvalAction(WrapSpeechEvalAction speechEvalAction) {
        this.wrapSpeechEvalAction = speechEvalAction;
    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String nonce, VideoQuestionLiveEntity videoQuestionLiveEntity, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage) {
        wrapSpeechEvalAction.setSpeechEvalAction(speechEvalAction);
        wrapSpeechEvalAction.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        SpeechAssAutoPager speechAssAutoPager = new SpeechAssAutoPager(context,
                videoQuestionLiveEntity, liveid, videoQuestionLiveEntity.id,
                "", videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time,
                videoQuestionLiveEntity.getvEndTime() - videoQuestionLiveEntity.getvQuestionInsretTime(), learning_stage, wrapSpeechEvalAction, livePagerBack);
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                                    SpeechEvalAction speechEvalAction, String stuCouId) {
        wrapSpeechEvalAction.setSpeechEvalAction(speechEvalAction);
        wrapSpeechEvalAction.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                false, videoQuestionLiveEntity.nonce, wrapSpeechEvalAction, stuCouId, false, livePagerBack);
        return speechAssessmentPager;
    }

    @Override
    public void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
        if (rightMargin != params.rightMargin) {
            params.rightMargin = rightMargin;
            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
        }
    }

}
