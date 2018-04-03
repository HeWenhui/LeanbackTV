package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2018/4/3.
 */
public interface BaseVoiceAnswerCreat {
    BaseVoiceAnswerPager create(Activity activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                QuestionSwitch questionSwitch, RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse, LiveAndBackDebug liveAndBackDebug);

    void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin);
}
