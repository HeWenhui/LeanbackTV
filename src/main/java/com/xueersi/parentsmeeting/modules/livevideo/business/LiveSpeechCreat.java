package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssAutoPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.BaseSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechAssessmentWebX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.lib.framework.utils.ScreenUtils;

/**
 * Created by lyqai on 2018/4/7.
 */

public class LiveSpeechCreat implements BaseSpeechCreat {
    boolean isLive;

    public LiveSpeechCreat(boolean isLive) {
        this.isLive = isLive;
    }

    @Override
    public void receiveRolePlay(VideoQuestionLiveEntity videoQuestionLiveEntity) {

    }

    @Override
    public BaseSpeechAssessmentPager createSpeech(Context context, String liveid, String nonce, VideoQuestionLiveEntity videoQuestionLiveEntity, boolean haveAnswer, SpeechEvalAction speechEvalAction, RelativeLayout.LayoutParams lp, LiveGetInfo getInfo, String learning_stage) {
        SpeechAssAutoPager speechAssAutoPager;
        if (isLive) {
            speechAssAutoPager =
                    new SpeechAssAutoPager(context, liveid, videoQuestionLiveEntity.id, nonce,
                            videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time, haveAnswer, learning_stage, speechEvalAction);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
            lp.rightMargin = wradio;
        } else {
            speechAssAutoPager = new SpeechAssAutoPager(context,
                    liveid, videoQuestionLiveEntity.id,
                    "", videoQuestionLiveEntity.speechContent, (int) videoQuestionLiveEntity.time,
                    videoQuestionLiveEntity.getvEndTime() - videoQuestionLiveEntity.getvQuestionInsretTime(), learning_stage, speechEvalAction);
        }
        return speechAssAutoPager;
    }

    @Override
    public BaseSpeechAssessmentPager createRolePlay(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity videoQuestionLiveEntity, String testId,
                                                    SpeechEvalAction speechEvalAction, String stuCouId) {
        SpeechAssessmentWebX5Pager speechAssessmentPager = new SpeechAssessmentWebX5Pager(context,
                liveGetInfo.getId(), testId, liveGetInfo.getStuId(),
                true, videoQuestionLiveEntity.nonce, speechEvalAction, stuCouId, false);
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
