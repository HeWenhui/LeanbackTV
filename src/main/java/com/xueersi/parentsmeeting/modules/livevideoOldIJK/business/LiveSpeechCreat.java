package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.content.Context;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.BaseSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SpeechAssessmentWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;

/**
 * Created by linyuqiang on 2018/4/7.
 * 直播的语音评测创建
 */
public class LiveSpeechCreat implements BaseSpeechCreat {
    private LivePagerBack livePagerBack;
    private LiveGetInfo getInfo;

    public LiveSpeechCreat(LivePagerBack livePagerBack, LiveGetInfo getInfo) {
        this.livePagerBack = livePagerBack;
        this.getInfo = getInfo;
    }

    @Override
    public void receiveRolePlay(VideoQuestionLiveEntity videoQuestionLiveEntity) {

    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String nonce, VideoQuestionLiveEntity videoQuestionLiveEntity, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage) {
        SpeechAssAutoPager speechAssAutoPager =
                new SpeechAssAutoPager(context, videoQuestionLiveEntity, liveid, videoQuestionLiveEntity.id, getInfo, nonce,
                        videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time, haveAnswer, learning_stage, speechEvalAction, livePagerBack);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        lp.rightMargin = liveVideoPoint.getRightMargin();
        if (getInfo.getSmallEnglish()) {
            speechAssAutoPager.setSmallEnglish(1);
        }
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                                    SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll) {

        //老讲义人机走原生
        if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles)) {
            RolePlayMachinePager rolePlayerPager = new RolePlayMachinePager(context,
                    videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                    true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
            return rolePlayerPager;
        }
        SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack);
        return speechAssessmentPager;
    }


    @Override
    public BaseSpeechAssessmentPager createNewRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                                       SpeechEvalAction speechEvalAction, String stuCouId, RolePlayMachineBll rolePlayMachineBll) {
        //新课件平台， roleplay也走原生
        if (liveGetInfo.getLiveType() != 2 && "5".equals(videoQuestionLiveEntity.type)) {
            RolePlayMachinePager rolePlayerPager = new RolePlayMachinePager(context,
                    videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                    true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack, rolePlayMachineBll, liveGetInfo);
            return rolePlayerPager;
        }
        SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
                videoQuestionLiveEntity, liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false, livePagerBack);
        return speechAssessmentPager;
    }


    @Override
    public void setViewLayoutParams(BaseSpeechAssessmentPager baseVoiceAnswerPager, int rightMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
        if (rightMargin != params.rightMargin) {
            params.rightMargin = rightMargin;
            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
        }
    }

}
