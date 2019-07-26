package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.CreateAnswerReslutEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.VoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import org.json.JSONObject;

import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE1;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE2;
import static com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity.QUE_RES_TYPE4;

/**
 * Created by linyuqiang on 2018/4/3.
 * 直播创建语音答题
 */
public class LiveVoiceAnswerCreat implements BaseVoiceAnswerCreat {
    private QuestionSwitch questionSwitch;
    Logger logger = LoggerFactory.getLogger("LiveVoiceAnswerCreat");
    LivePagerBack livePagerBack;
    LiveGetInfo getInfo;

    public LiveVoiceAnswerCreat(QuestionSwitch questionSwitch, LivePagerBack livePagerBack, LiveGetInfo getInfo) {
        this.questionSwitch = questionSwitch;
        this.livePagerBack = livePagerBack;
        this.getInfo = getInfo;
    }

    @Override
    public BaseVoiceAnswerPager create(Context activity, VideoQuestionLiveEntity videoQuestionLiveEntity, JSONObject assess_ref, String type,
                                       RelativeLayout rlQuestionContent, SpeechUtils mIse) {
        if (questionSwitch instanceof WrapQuestionSwitch) {
            WrapQuestionSwitch wrapQuestionSwitch = (WrapQuestionSwitch) questionSwitch;
            wrapQuestionSwitch.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
        }
        VoiceAnswerPager voiceAnswerPager2 = new VoiceAnswerPager(activity, videoQuestionLiveEntity, assess_ref, videoQuestionLiveEntity.type, questionSwitch);
        voiceAnswerPager2.setIse(mIse);
        voiceAnswerPager2.setLivePagerBack(livePagerBack);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        if (liveVideoPoint != null) {
            params.rightMargin = liveVideoPoint.getRightMargin();
            logger.d("create:rightMargin=" + params.rightMargin);
        } else {
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
            params.rightMargin = wradio;
        }
        rlQuestionContent.addView(voiceAnswerPager2.getRootView(), params);
        String sourcetype = questionSwitch.getsourcetype(videoQuestionLiveEntity);
        VoiceAnswerLog.sno2(voiceAnswerPager2, videoQuestionLiveEntity.type, videoQuestionLiveEntity.id, videoQuestionLiveEntity.nonce, sourcetype);
        return voiceAnswerPager2;
    }

    @Override
    public void setViewLayoutParams(BaseVoiceAnswerPager baseVoiceAnswerPager, int rightMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseVoiceAnswerPager.getRootView().getLayoutParams();
        if (rightMargin != params.rightMargin) {
            params.rightMargin = rightMargin;
            logger.d("setViewLayoutParams:rightMargin=" + params.rightMargin);
            LayoutParamsUtil.setViewLayoutParams(baseVoiceAnswerPager.getRootView(), params);
        }
    }

    @Override
    public CreateAnswerReslutEntity onAnswerReslut(Context context, AnswerRightResultVoice answerRightResultVoice, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
        CreateAnswerReslutEntity createAnswerReslutEntity = new CreateAnswerReslutEntity();
        boolean isSuccess = false;
        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        if (videoQuestionLiveEntity.isNewArtsH5Courseware()) {
            entity.setNewArt(true);
            boolean smallEnglish = false;
            if (answerRightResultVoice instanceof NewArtsAnswerRightResultVoice) {
                NewArtsAnswerRightResultVoice artsAnswerRightResultVoice = (NewArtsAnswerRightResultVoice) answerRightResultVoice;
                if (getInfo != null) {
                    smallEnglish = getInfo.getSmallEnglish();
                }
                if (smallEnglish) {
                    AnswerResultEntity answerResultEntity = AnswerResultEntity.getAnswerResultEntity(videoQuestionLiveEntity, entity);
                    View view = artsAnswerRightResultVoice.initArtsAnswerRightResultVoice(answerResultEntity);
                    createAnswerReslutEntity.resultView = view;
                    isSuccess = answerResultEntity.getIsRight() == 2;
                }
            }
            if (!smallEnglish) {
                if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE2) {
                    if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                        answerRightResultVoice.initSelectAnswerRightResultVoice(entity);
                    } else {
                        answerRightResultVoice.initFillinAnswerRightResultVoice(entity);
                    }
                    isSuccess = true;
                    // 回答错误提示
                } else if (entity.getResultType() == 0) {
                    if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                        answerRightResultVoice.initSelectAnswerWrongResultVoice(entity);
                    } else {
                        answerRightResultVoice.initFillAnswerWrongResultVoice(entity);
                    }
                    // 填空题部分正确提示
                }
            }
        } else {
            entity.setNewArt(false);
            if (entity.getResultType() == QUE_RES_TYPE1 || entity.getResultType() == QUE_RES_TYPE4) {
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                    answerRightResultVoice.initSelectAnswerRightResultVoice(entity);
                } else {
                    answerRightResultVoice.initFillinAnswerRightResultVoice(entity);
                }
                isSuccess = true;
                // 回答错误提示
            } else if (entity.getResultType() == QUE_RES_TYPE2) {
                if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionLiveEntity.type)) {
                    answerRightResultVoice.initSelectAnswerWrongResultVoice(entity);
                } else {
                    answerRightResultVoice.initFillAnswerWrongResultVoice(entity);
                }
                // 填空题部分正确提示
            }
        }
        createAnswerReslutEntity.isSuccess = isSuccess;
        return createAnswerReslutEntity;
    }
}
