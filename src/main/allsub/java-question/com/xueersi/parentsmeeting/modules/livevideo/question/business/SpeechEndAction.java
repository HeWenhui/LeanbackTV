package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;

/**
 * Created by linyuqiang on 2018/4/10.
 * 语音评测结束事件
 */
public interface SpeechEndAction {

    void examSubmitAll(BaseSpeechAssessmentPager speechAssessmentPager, String num);

    void initView(LiveViewAction liveViewAction);

    void onStopSpeech(BaseSpeechAssessmentPager speechAssessmentPager, String num, OnTop3End top3End);

    interface OnTop3End {
        void onShowEnd();
    }
}
