package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseSpeechAssessmentPager;

/**
 * @author lingyuqiang
 * @date 2018/4/7
 * 语音评测创建
 */
public interface BaseSpeechCreat {
    /**
     * 收到roleplay互动题
     *
     * @param videoQuestionLiveEntity
     */
    void receiveRolePlay(final VideoQuestionLiveEntity videoQuestionLiveEntity);

    /** 创建原生语音评测 */
    BaseSpeechAssessmentPager createSpeech(Context context, String liveid,
                                           String nonce, VideoQuestionLiveEntity videoQuestionLiveEntity, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage);

    /** 创建roleplay */
    BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                             SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll);

    void setViewLayoutParams(BaseSpeechAssessmentPager baseSpeechAssessmentPager, int rightMargin);


    /** 文科新课件平台RolePlay*/
    BaseSpeechAssessmentPager createNewRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId, SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll);


}
