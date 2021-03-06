package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayStandMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.StandSpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.SpeechStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * Created by lingyuqiang on 2018/4/7.
 * 站立直播的语音答题
 */
public class LiveStandSpeechCreat implements BaseSpeechCreat {
    Context context;
    SpeechEvalHttp questionIRCBll;
    LiveAndBackDebug liveAndBackDebug;
    LivePagerBack livePagerBack;

    public LiveStandSpeechCreat(Context context, SpeechEvalHttp questionIRCBll, LiveAndBackDebug liveAndBackDebug, LivePagerBack livePagerBack) {
        this.context = context;
        this.questionIRCBll = questionIRCBll;
        this.liveAndBackDebug = liveAndBackDebug;
        this.livePagerBack = livePagerBack;
    }

    @Override
    public void receiveRolePlay(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        RolePlayStandLog.sno2(liveAndBackDebug, videoQuestionLiveEntity.id);
    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String nonce, VideoQuestionLiveEntity videoQuestionLiveEntity,
                                                  boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage) {
        SpeechStandLog.sno2(liveAndBackDebug, videoQuestionLiveEntity.id, nonce);
        speechEvalAction = new LiveStandSpeechEvalActionImpl(speechEvalAction);
        StandSpeechAssAutoPager speechAssAutoPager =
                new StandSpeechAssAutoPager(context, videoQuestionLiveEntity, liveid, videoQuestionLiveEntity.id, nonce,
                        videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time, haveAnswer, speechEvalAction, getInfo.getStandLiveName(), getInfo.getHeadImgPath(), learning_stage, livePagerBack);
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                                    SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll) {

        RolePlayStandMachinePager rolePlayerPager = new RolePlayStandMachinePager(context,
                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
        return rolePlayerPager;
    }


    @Override
    public void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
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
                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
        return rolePlayerPager;

    }

    class LiveStandSpeechEvalActionImpl implements LiveStandSpeechEvalAction {
        SpeechEvalAction action;

        public LiveStandSpeechEvalActionImpl(SpeechEvalAction action) {
            this.action = action;
        }

        @Override
        public long getRequestTime() {
            try {
                String requestTime = questionIRCBll.getRequestTime();
                long time = Long.parseLong(requestTime);
                return time * 1000;
            } catch (Exception e) {

            }
            return 3000;
        }

        @Override
        public void getSpeechEvalAnswerTeamStatus(boolean isNewArt,String testId, AbstractBusinessDataCallBack callBack) {
            String requestTime = questionIRCBll.getRequestTime();
            if (!"-1".equals(requestTime)) {
                questionIRCBll.getSpeechEvalAnswerTeamStatus(isNewArt,testId, callBack);
            }
        }

//        @Override
//        public void getSpeechEval(String id, AbstractBusinessDataCallBack callBack) {
//            action.getSpeechEval(id, callBack);
//        }

        @Override
        public void stopSpeech(BaseSpeechAssessmentPager pager, BaseVideoQuestionEntity baseVideoQuestionEntity, String num) {
            action.stopSpeech(pager, baseVideoQuestionEntity, num);
        }

        @Override
        public void sendSpeechEvalResult2(String id, VideoQuestionLiveEntity videoQuestionLiveEntity, String stuAnswer, String isSubmit, AbstractBusinessDataCallBack callBack) {
            action.sendSpeechEvalResult2(id, videoQuestionLiveEntity, stuAnswer, isSubmit, callBack);
        }

        @Override
        public void onSpeechSuccess(String num) {
            action.onSpeechSuccess(num);
            UpdateAchievement updateAchievement = ProxUtil.getProvide(context, UpdateAchievement.class);
            if (updateAchievement != null) {
                updateAchievement.getStuGoldCount("onSpeechSuccess:num=" + num, UpdateAchievement.GET_TYPE_QUE);
            }
        }

        @Override
        public void speechIsAnswered(boolean isNewArt,String num, AbstractBusinessDataCallBack callBack) {
            action.speechIsAnswered(isNewArt,num, callBack);
        }

    }
}
