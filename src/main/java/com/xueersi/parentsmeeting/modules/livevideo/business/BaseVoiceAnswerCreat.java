package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2018/4/3.
 * 语音答题普通直播和站立直播
 */
public interface BaseVoiceAnswerCreat {
    BaseVoiceAnswerPager create(Activity activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                QuestionBll questionBll, RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse, LiveAndBackDebug liveAndBackDebug);

    void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin);

    /**
     * 回答结果显示
     *
     * @param context
     * @param questionBll
     * @param baseVideoQuestionEntity
     * @param entity
     * @return
     */
    boolean onAnswerReslut(Context context, QuestionBll questionBll, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity);
}
