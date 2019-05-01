package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

import java.util.HashMap;

/**
 * Created by：WangDe on 2018/8/30 11:22
 */
public class EnglishH5ExperienceBll extends LiveBackBaseBll {

    EnglishH5CoursewareBll englishH5CoursewareBll;

    public EnglishH5ExperienceBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
        englishH5CoursewareBll.setLiveType(mLiveType);
        englishH5CoursewareBll.setVSectionID(mVideoEntity.getLiveId());
        englishH5CoursewareBll.setLiveBll(new EnglishH5CoursewareImpl());
        englishH5CoursewareBll.setGetInfo(liveGetInfo);
        //语音答题
        WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, englishH5CoursewareBll.new
                LiveQuestionSwitchImpl());
        englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(wrapQuestionSwitch,
                englishH5CoursewareBll, liveGetInfo));

        LiveBackBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new
                LiveBackBaseEnglishH5CoursewareCreat();
        liveBaseEnglishH5CoursewareCreat.setLiveGetInfo(liveGetInfo);
        int isArts = liveBackBll.getIsArts();
        liveBaseEnglishH5CoursewareCreat.setArts(isArts);
        liveBaseEnglishH5CoursewareCreat.setWrapOnH5ResultClose(new WrapOnH5ResultClose(activity));
        liveBaseEnglishH5CoursewareCreat.setLivePagerBack(englishH5CoursewareBll);
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
    }

    @Override
    public void initView() {
        englishH5CoursewareBll.initView(mRootView);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);//关闭答题
            }
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, final
    LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                questionEntity.setAnswered(true);
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_TUTOR_EVENT_35:
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {

                MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                questionEntity.setAnswered(true);
                // 获取拼装一题多发的字段
                LiveVideoConfig.LIVEPLAYBACKINFOS = questionEntity.getUrl();
                LiveVideoConfig.LIVEPLAYBACKSTUID = mVideoEntity.getStuCoulId();
                LiveVideoConfig.LIVEPLAYBACKCLASSID = mVideoEntity.getClassId();
                LiveVideoConfig.LIVEPLAYBACKTEAMID = mVideoEntity.getTeamId();
                LiveVideoConfig.LIVEPLAYBACKSTAGE = mVideoEntity.getEdustage();
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            default:
                break;
        }
    }

    private VideoQuestionLiveEntity getVideoQuestionLiveEntity(VideoQuestionEntity questionEntity,int vCategory) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
        videoQuestionLiveEntity.englishH5Entity = questionEntity.getEnglishH5Entity();
        String isVoice = questionEntity.getIsVoice();
        videoQuestionLiveEntity.setIsVoice(isVoice);
        if ("1".equals(isVoice)) {
            videoQuestionLiveEntity.type = questionEntity.getVoiceQuestiontype();
        }
        videoQuestionLiveEntity.assess_ref = questionEntity.getAssess_ref();
        videoQuestionLiveEntity.setUrl(questionEntity.getEnglishH5Play_url());
        videoQuestionLiveEntity.courseware_type = questionEntity.getvQuestionType();
        videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
        videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
        videoQuestionLiveEntity.setAnswerDay(questionEntity.getAnswerDay());
        if (vCategory ==  LocalCourseConfig.CATEGORY_TUTOR_EVENT_35) {
            videoQuestionLiveEntity.setTUtor(true);
        }
        return videoQuestionLiveEntity;
    }

    class EnglishH5CoursewareImpl implements EnglishH5CoursewareHttp {

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

        /**
         * 提交h5语音答题
         *
         * @param videoQuestionLiveEntity
         * @param mVSectionID
         * @param testAnswer
         * @param courseware_type
         * @param isSubmit
         * @param voiceTime
         * @param isRight
         * @param onAnswerReslut
         */
        @Override
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID,
                                           String testAnswer, String courseware_type, String isSubmit, double
                                                   voiceTime, boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
            String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
            String userMode = "1";
            String isArts = String.valueOf(liveBackBll.getIsArts());
            getCourseHttpManager().submitExperienceCourseWareH5(
                    mVideoEntity.getSubmitCourseWareH5AnswerUseVoiceUrl()
                    , mVideoEntity.getLiveId(),
                    videoQuestionLiveEntity.id,
                    mVideoEntity.getChapterId(),
                    testAnswer, voiceTime,
                    isRight, isArts,
                    new HttpCallBack() {

                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) {
                            VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer
                                    (responseEntity,
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
                    });
        }
    }
}
