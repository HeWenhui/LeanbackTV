package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;

import org.json.JSONObject;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2018/4/3.
 * 直播创建语音答题
 */
public class LiveBackVoiceAnswerCreat implements BaseVoiceAnswerCreat {
    private WrapQuestionSwitch questionSwitch;
    Logger logger = LoggerFactory.getLogger("LiveBackVoiceAnswerCreat");
    LivePagerBack livePagerBack;

    public LiveBackVoiceAnswerCreat(WrapQuestionSwitch questionSwitch, LivePagerBack livePagerBack) {
        this.questionSwitch = questionSwitch;
        this.livePagerBack = livePagerBack;
    }

    @Override
    public BaseVoiceAnswerPager create(Context activity, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type,
                                       RelativeLayout rlQuestionContent, SpeechEvaluatorUtils mIse) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        questionSwitch.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        VoiceAnswerPager voiceAnswerPager2 = new VoiceAnswerPager(activity, baseVideoQuestionEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch);
        voiceAnswerPager2.setIse(mIse);
        voiceAnswerPager2.setLivePagerBack(livePagerBack);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);
        String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
        VoiceAnswerLog.sno2(voiceAnswerPager2, videoQuestionLiveEntity.type, videoQuestionLiveEntity.id, videoQuestionLiveEntity.nonce, sourcetype);
        return voiceAnswerPager2;
    }

    @Override
    public void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin) {

    }

    @Override
    public boolean onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        boolean isSuccess = false;
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                questionBll.initSelectAnswerRightResultVoice(entity);
            } else {
                questionBll.initFillinAnswerRightResultVoice(entity);
            }
            isSuccess = true;
            // 回答错误提示
        } else if (entity.getResultType() == QUE_RES_TYPE2) {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                questionBll.initSelectAnswerWrongResultVoice(entity);
            } else {
                questionBll.initFillAnswerWrongResultVoice(entity);
            }
            // 填空题部分正确提示
        }
        return isSuccess;
    }
}