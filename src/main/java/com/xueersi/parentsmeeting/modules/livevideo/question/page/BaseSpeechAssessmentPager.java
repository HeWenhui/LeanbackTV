package com.xueersi.parentsmeeting.modules.livevideo.question.page;


import android.content.Context;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by lyqai on 2017/11/21.
 */

public abstract class BaseSpeechAssessmentPager extends LiveBasePager {
    /** 语音评测 */
    protected SpeechEvaluatorUtils mIse;

    public BaseSpeechAssessmentPager(Context context) {
        super(context);
    }

    public abstract void examSubmitAll();

    public abstract String getId();

    public abstract void jsExamSubmit();

    public abstract void stopPlayer();

    public void setIse(SpeechEvaluatorUtils mIse) {
        this.mIse = mIse;
    }
}
