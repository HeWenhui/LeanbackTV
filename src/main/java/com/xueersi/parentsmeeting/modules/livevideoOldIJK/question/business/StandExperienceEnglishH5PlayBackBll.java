package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

public class StandExperienceEnglishH5PlayBackBll extends EnglishH5PlayBackBll {

    public StandExperienceEnglishH5PlayBackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, final
    LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {

                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                if (!liveBackBll.getExperience() && mediaPlayerControl != null) {//体验课不能暂停
                    mediaPlayerControl.pause();
                }
                questionEntity.setAnswered(true);
                if (liveBackBll.getExperience()) {//体验课直接出题
                    BackMediaPlayerControl mediaPlayerControl1 = getInstance(BackMediaPlayerControl.class);
                    if (mediaPlayerControl1 != null) {
                        mediaPlayerControl1.start();
                    }
                    VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                            (questionEntity);

                    englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                    showQuestion.onShow(true, videoQuestionLiveEntity);
                }
            }
            break;
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                if (!liveBackBll.getExperience() && mediaPlayerControl != null) {
                    mediaPlayerControl.pause();
                }
                questionEntity.setAnswered(true);
                // 获取拼装一题多发的字段
                LiveVideoConfig.LIVEPLAYBACKINFOS = questionEntity.getUrl();
                LiveVideoConfig.LIVEPLAYBACKSTUID = mVideoEntity.getStuCoulId();
                LiveVideoConfig.LIVEPLAYBACKCLASSID = mVideoEntity.getClassId();
                LiveVideoConfig.LIVEPLAYBACKTEAMID = mVideoEntity.getTeamId();
                LiveVideoConfig.LIVEPLAYBACKSTAGE = mVideoEntity.getEdustage();
                LiveVideoConfig.LIVEPLAYBACKTYPE = questionEntity.getName();
                if (liveBackBll.getExperience()) {//站立直播体验课,不弹出弹窗
                    BackMediaPlayerControl mediaPlayerControl1 = getInstance(BackMediaPlayerControl.class);
                    if (mediaPlayerControl1 != null) {
                        mediaPlayerControl1.start();
                    }
                    VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                            (questionEntity);

                    videoQuestionLiveEntity.englishH5Entity.setNewEnglishH5(true);
                    englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                    showQuestion.onShow(true, videoQuestionLiveEntity);
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected EnglishH5CoursewareHttp getHttp() {
        return new ExperienceEnglishH5CoursewareImpl();
    }

    class ExperienceEnglishH5CoursewareImpl implements EnglishH5CoursewareHttp {

        @Override
        public void getStuGoldCount(String method) {
            //回放没有
        }

        @Override
        public void sendRankMessage(int rankStuReconnectMessage) {
            //回放没有
        }

        @Override
        public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, final
        AbstractBusinessDataCallBack callBack) {
            //回放没有
        }

        @Override
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID,
                                           String testAnswer, String courseware_type, String isSubmit, double
                                                   voiceTime, boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            String userMode = "1";
            HttpCallBack httpCallBack = new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer(responseEntity,
                            true);
                    entity.setVoice(true);
                    if (StringUtils.isSpace(entity.getTestId())) {
                        entity.setTestId(videoQuestionLiveEntity.id);
                    }
                    if (onAnswerReslut != null) {
                        onAnswerReslut.onAnswerReslut(videoQuestionLiveEntity, entity);
                    }
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    if (onAnswerReslut != null) {
                        onAnswerReslut.onAnswerFailure();
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    if (!responseEntity.isJsonError()) {
                        if (onAnswerReslut != null) {
                            onAnswerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                        }
                    }
                }
            };
            String isArts = String.valueOf(liveBackBll.getIsArts());
            getCourseHttpManager().submitExperienceCourseWareH5(
                    mVideoEntity.getSubmitCourseWareH5AnswerUseVoiceUrl(),
                    mVideoEntity.getLiveId(),
                    videoQuestionLiveEntity.id,
                    mVideoEntity.getChapterId(),
                    testAnswer,
                    voiceTime,
                    isRight,
                    isArts,
                    httpCallBack);
        }
    }

}
