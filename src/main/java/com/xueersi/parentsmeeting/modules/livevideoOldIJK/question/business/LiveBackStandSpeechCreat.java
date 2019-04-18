package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayStandMachinePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SpeechAssessmentWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.StandSpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.RolePlayStandLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.SpeechStandLog;

/**
 * Created by lingyuqiang on 2018/4/7.
 * 站立直播的语音答题-回放
 */
public class LiveBackStandSpeechCreat implements BaseSpeechCreat {
    QuestionPlayBackBll questionIRCBll;
    LiveAndBackDebug liveAndBackDebug;
    LivePagerBack livePagerBack;
    WrapSpeechEvalAction wrapSpeechEvalAction;
    /**
     * 是否是体验课
     */
    private boolean isExperience;

    public void setIsExperience(boolean experience) {
        isExperience = experience;
    }

    public LiveBackStandSpeechCreat(QuestionPlayBackBll questionIRCBll, LiveAndBackDebug liveAndBackDebug,
                                    LivePagerBack livePagerBack) {
        this.questionIRCBll = questionIRCBll;
        this.liveAndBackDebug = liveAndBackDebug;
        this.livePagerBack = livePagerBack;
    }

    public void setSpeechEvalAction(WrapSpeechEvalAction wrapSpeechEvalAction) {
        this.wrapSpeechEvalAction = wrapSpeechEvalAction;
    }

    @Override
    public void receiveRolePlay(VideoQuestionLiveEntity videoQuestionLiveEntity) {

    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String nonce,
                                                  VideoQuestionLiveEntity videoQuestionLiveEntity,
                                                  boolean haveAnswer, SpeechEvalAction speechEvalAction,
                                                  RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String
                                                          learning_stage) {
        SpeechStandLog.sno2(liveAndBackDebug, videoQuestionLiveEntity.id, nonce);
        wrapSpeechEvalAction.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        wrapSpeechEvalAction.setSpeechEvalAction(speechEvalAction);
        speechEvalAction = new LiveStandSpeechEvalActionImpl(wrapSpeechEvalAction);
        StandSpeechAssAutoPager speechAssAutoPager = new StandSpeechAssAutoPager(context,
                videoQuestionLiveEntity, liveid, videoQuestionLiveEntity.id,
                "", videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time,
                videoQuestionLiveEntity.getvEndTime() - videoQuestionLiveEntity.getvQuestionInsretTime(),
                speechEvalAction, getInfo.getStandLiveName(), getInfo.getHeadImgPath(), learning_stage);
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity
            videoQuestionLiveEntity, String testId,
                                                    SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll) {
        if (!isExperience) {
            RolePlayStandMachinePager rolePlayerPager = new RolePlayStandMachinePager(context,
                    videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                    false, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
            return rolePlayerPager;
        } else {
            SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
                    videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                    false, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack);
            speechAssessmentPager.setStandingLive(true);
            speechAssessmentPager.setIsExperience(isExperience);
            RolePlayStandLog.sno3(liveAndBackDebug, testId);
            return speechAssessmentPager;
        }
    }

    @Override
    public void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView()
// .getLayoutParams();
//        if (rightMargin != params.rightMargin) {
//            params.rightMargin = rightMargin;
//            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
//        }
    }

    @Override
    public BaseSpeechAssessmentPager createNewRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId, SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll) {
//        SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
//                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
//                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack);
//        speechAssessmentPager.setStandingLive(true);
//        RolePlayStandLog.sno3(liveAndBackDebug, testId);
//        return speechAssessmentPager;
        RolePlayStandMachinePager rolePlayerPager = new RolePlayStandMachinePager(context,
                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                false, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
        return rolePlayerPager;
    }

    class LiveStandSpeechEvalActionImpl implements LiveStandSpeechEvalAction {
        SpeechEvalAction action;

        public LiveStandSpeechEvalActionImpl(SpeechEvalAction action) {
            this.action = action;
        }

        @Override
        public long getRequestTime() {
            return 3000;
        }

        @Override
        public void getSpeechEvalAnswerTeamStatus(String testId, AbstractBusinessDataCallBack callBack) {

        }

        @Override
        public void getSpeechEval(String id, OnSpeechEval onSpeechEval) {
            action.getSpeechEval(id, onSpeechEval);
        }

        @Override
        public void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity,
                               String num) {
            action.stopSpeech(pager, baseVideoQuestionEntity, num);
        }

        @Override
        public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval
                onSpeechEval) {
            action.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
        }

        @Override
        public void sendSpeechEvalResult2(String id, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuAnswer, String isSubmit, OnSpeechEval onSpeechEval) {
            action.sendSpeechEvalResult2(id, videoQuestionLiveEntity, stuAnswer, isSubmit, onSpeechEval);
        }

        @Override
        public void onSpeechSuccess(String num) {
            action.onSpeechSuccess(num);
        }

        @Override
        public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
            action.speechIsAnswered(num, isAnswered);
        }

    }
}