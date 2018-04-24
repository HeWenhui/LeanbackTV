package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.SpeechAssessmentWebPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.StandSpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.SpeechStandLog;

import java.util.Map;

/**
 * Created by lingyuqiang on 2018/4/7.
 * 站立直播的语音答题
 */
public class LiveStandSpeechCreat implements BaseSpeechCreat {
    LiveBll liveBll;

    public LiveStandSpeechCreat(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String testId, String nonce, String content,
                                                  int time, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage) {
        SpeechStandLog.sno2(liveBll, testId);
        speechEvalAction = new LiveStandSpeechEvalActionImpl(speechEvalAction);
        StandSpeechAssAutoPager speechAssAutoPager =
                new StandSpeechAssAutoPager(context, liveid, testId, nonce,
                        content, (int) time, haveAnswer, speechEvalAction, getInfo.getStandLiveName(), getInfo.getHeadImgPath(), learning_stage);
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, String testId,
                                                    String nonce,
                                                    SpeechEvalAction speechEvalAction, String stuCouId) {
        SpeechAssessmentWebPager speechAssessmentPager = new SpeechAssessmentWebPager(context,
                liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                true, nonce, speechEvalAction, stuCouId, false);
        speechAssessmentPager.setStandingLive(true);
        return speechAssessmentPager;
    }

    @Override
    public void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
//        if (rightMargin != params.rightMargin) {
//            params.rightMargin = rightMargin;
//            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
//        }
    }

    class LiveStandSpeechEvalActionImpl implements LiveStandSpeechEvalAction {
        SpeechEvalAction action;

        public LiveStandSpeechEvalActionImpl(SpeechEvalAction action) {
            this.action = action;
        }

        @Override
        public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
            action.umsAgentDebugSys(eventId, mData);
        }

        @Override
        public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
            action.umsAgentDebugInter(eventId, mData);
        }

        @Override
        public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
            action.umsAgentDebugPv(eventId, mData);
        }

        @Override
        public void getSpeechEvalAnswerTeamStatus(String testId, AbstractBusinessDataCallBack callBack) {
            liveBll.getSpeechEvalAnswerTeamStatus(testId, callBack);
        }

        @Override
        public void getSpeechEval(String id, OnSpeechEval onSpeechEval) {
            action.getSpeechEval(id, onSpeechEval);
        }

        @Override
        public void stopSpeech(BaseSpeechAssessmentPager pager, String num) {
            action.stopSpeech(pager, num);
        }

        @Override
        public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, OnSpeechEval onSpeechEval) {
            action.sendSpeechEvalResult(id, stuAnswer, times, entranceTime, onSpeechEval);
        }

        @Override
        public void sendSpeechEvalResult2(String id, String stuAnswer, OnSpeechEval onSpeechEval) {
            action.sendSpeechEvalResult2(id, stuAnswer, onSpeechEval);
        }

        @Override
        public void onSpeechSuccess(String num) {
            action.onSpeechSuccess(num);
            liveBll.getStuGoldCount();
        }

        @Override
        public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
            action.speechIsAnswered(num, isAnswered);
        }
    }
}
