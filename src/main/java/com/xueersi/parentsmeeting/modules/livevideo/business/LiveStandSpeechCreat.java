package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.StandSpeechAssAutoPager;

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
    public BaseSpeechAssessmentPager create(Context context, String liveid, String testId, String nonce, String content,
                                            int time, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, String userName, String headUrl, String learning_stage) {
        speechEvalAction = new LiveStandSpeechEvalActionImpl(speechEvalAction);
        StandSpeechAssAutoPager speechAssAutoPager =
                new StandSpeechAssAutoPager(context, liveid, testId, nonce,
                        content, (int) time, haveAnswer, speechEvalAction, userName, headUrl);
        return speechAssAutoPager;
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
        public void umsAgentDebug(String eventId, Map<String, String> mData) {
            action.umsAgentDebug(eventId, mData);
        }

        @Override
        public void umsAgentDebug2(String eventId, Map<String, String> mData) {
            action.umsAgentDebug2(eventId, mData);
        }

        @Override
        public void umsAgentDebug3(String eventId, Map<String, String> mData) {
            action.umsAgentDebug3(eventId, mData);
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
        }

        @Override
        public void speechIsAnswered(String num, SpeechIsAnswered isAnswered) {
            action.speechIsAnswered(num, isAnswered);
        }
    }
}
