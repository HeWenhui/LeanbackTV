package com.xueersi.parentsmeeting.modules.livevideo.question.page;


import android.content.Context;

import com.xueersi.common.speech.SpeechUtils;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by lyqai on 2017/11/21.
 */

public abstract class BaseSpeechAssessmentPager extends LiveBasePager {
    /** 语音评测 */
    protected SpeechUtils mIse;

    public BaseSpeechAssessmentPager(Context context) {
        super(context);
    }

    public abstract void examSubmitAll();

    public abstract String getId();

    public abstract void jsExamSubmit();

    public abstract void stopPlayer();

    public void setIse(SpeechUtils mIse) {
        this.mIse = mIse;
    }
}
