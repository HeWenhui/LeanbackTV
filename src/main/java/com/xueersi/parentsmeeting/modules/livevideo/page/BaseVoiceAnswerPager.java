package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;

import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.entity.BaseVideoQuestionEntity;


/**
 * Created by linyuqiang on 2018/4/3.
 */
public abstract class BaseVoiceAnswerPager extends LiveBasePager {

    public BaseVoiceAnswerPager(Context context) {
        super(context);
    }

    public abstract void setIse(SpeechUtils mIse);

    public abstract BaseVideoQuestionEntity getBaseVideoQuestionEntity();

    public abstract boolean isEnd();

    public abstract void setEnd();

    public abstract void stopPlayer();

    public abstract void setAudioRequest();

    public abstract void onNetWorkChange(int netWorkType);

    public abstract void examSubmitAll(String showQuestion, String s);

    public abstract void onUserBack();
}
