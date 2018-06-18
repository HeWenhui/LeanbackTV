package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.speech.SpeechEvaluatorUtils;

/**
 * Created by linyuqiang on 2018/4/3.
 */
public abstract class BaseVoiceAnswerPager extends BasePager {

    public BaseVoiceAnswerPager(Context context) {
        super(context);
    }

    public abstract void setIse(SpeechEvaluatorUtils mIse);

    public abstract BaseVideoQuestionEntity getBaseVideoQuestionEntity();

    public abstract boolean isEnd();

    public abstract void setEnd();

    public abstract void stopPlayer();

    public abstract void setAudioRequest();

    public abstract void onNetWorkChange(int netWorkType);

    public abstract void examSubmitAll(String showQuestion, String s);

    public abstract void onUserBack();
}
