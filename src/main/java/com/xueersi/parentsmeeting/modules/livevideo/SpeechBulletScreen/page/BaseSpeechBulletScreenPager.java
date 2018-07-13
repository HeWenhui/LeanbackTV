package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;

/**
 * Created by Zhang Yuansun on 2018/7/11.
 */

public abstract class BaseSpeechBulletScreenPager extends BasePager {
    public BaseSpeechBulletScreenPager(Context context) {
        super(context);
    }

    public abstract void setSpeechEvaluatorUtils(SpeechEvaluatorUtils speechEvaluatorUtils);

}
