package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;

/**
 * Created by lingyuqiang on 2018/4/7.
 * 语音评测创建
 */
public interface BaseSpeechCreat {
    /** 创建原生语音评测 */
    BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String testId,
                                           String nonce, String content, int time, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, String userName, String headUrl, String learning_stage);

    /** 创建roleplay */
    BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, String testId,
                                             String nonce,
                                             SpeechEvalAction speechEvalAction, String stuCouId);

    void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin);
}
