package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;

/**
 * Created by lingyuqiang on 2018/4/7.
 * 语音评测创建
 */
public interface BaseSpeechCreat {
    void receiveRolePlay(final VideoQuestionLiveEntity videoQuestionLiveEntity);

    /** 创建原生语音评测 */
    BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String testId,
                                           String nonce, String content, int time, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage);

    /** 创建roleplay */
    BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                             SpeechEvalAction speechEvalAction, String stuCouId);

    void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin);
}
