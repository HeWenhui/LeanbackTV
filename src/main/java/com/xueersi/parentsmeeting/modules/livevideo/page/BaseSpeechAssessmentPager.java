package com.xueersi.parentsmeeting.modules.livevideo.page;


import android.content.Context;

import com.xueersi.parentsmeeting.base.BasePager;

/**
 * Created by lyqai on 2017/11/21.
 */

public abstract class BaseSpeechAssessmentPager extends BasePager {
    public BaseSpeechAssessmentPager(Context context) {
        super(context);
    }

    public abstract void examSubmitAll();

    public abstract String getId();

    public abstract void jsExamSubmit();

    public abstract void stopPlayer();
}
