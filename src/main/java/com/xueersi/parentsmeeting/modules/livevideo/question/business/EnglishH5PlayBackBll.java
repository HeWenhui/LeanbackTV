package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.os.Message;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/17.
 * 直播回放英语课件
 */
public class EnglishH5PlayBackBll extends LiveBackBaseBll {
    EnglishH5CoursewareBll englishH5CoursewareBll;
    private EnglishH5Cache englishH5Cache;

    public EnglishH5PlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
        englishH5CoursewareBll.setLiveType(mLiveType);
        englishH5CoursewareBll.setVSectionID(mVideoEntity.getLiveId());
        englishH5CoursewareBll.setLiveBll(getHttp());
        englishH5CoursewareBll.setGetInfo(liveGetInfo);
        if (liveBackBll.getPattern() == 2) {
            //语音答题
            LiveAndBackDebug liveAndBackDebug = getInstance(LiveAndBackDebug.class);
            LiveBackStandVoiceAnswerCreat liveStandVoiceAnswerCreat = new LiveBackStandVoiceAnswerCreat(activity,
                    englishH5CoursewareBll.new LiveStandQuestionSwitchImpl(), liveBackBll);
            liveStandVoiceAnswerCreat.setUserName(liveGetInfo.getStandLiveName());
            liveStandVoiceAnswerCreat.setHeadUrl(liveGetInfo.getHeadImgPath());
            liveStandVoiceAnswerCreat.setLivePagerBack(englishH5CoursewareBll);
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(liveStandVoiceAnswerCreat);
        } else {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, englishH5CoursewareBll.new
                    LiveQuestionSwitchImpl());
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(wrapQuestionSwitch,
                    englishH5CoursewareBll));
        }
        LiveBackBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new
                LiveBackBaseEnglishH5CoursewareCreat();
        int isArts = liveBackBll.getIsArts();
        liveBaseEnglishH5CoursewareCreat.setIS_SCIENCE(isArts != 1);
        liveBaseEnglishH5CoursewareCreat.setWrapOnH5ResultClose(new WrapOnH5ResultClose(activity));
        liveBaseEnglishH5CoursewareCreat.setLivePagerBack(englishH5CoursewareBll);
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            englishH5Cache = new EnglishH5Cache(activity, liveGetInfo.getId());
            englishH5Cache.setHttpManager(getmHttpManager());
            englishH5Cache.getCourseWareUrl();
        }
    }

    @Override
    public void initView() {
        englishH5CoursewareBll.initView(mRootView);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE, LocalCourseConfig
                .CATEGORY_ENGLISH_MULH5COURSE_WARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
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

                MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                if (!liveBackBll.getExperience() && mediaPlayerControl != null) {//体验课不能暂停
                    mediaPlayerControl.pause();
                }
                questionEntity.setAnswered(true);
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                        if (mediaPlayerControl != null) {
                            mediaPlayerControl.start();
                        }
                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                                (questionEntity);

                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                        showQuestion.onShow(true, videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                        showQuestion.onHide(questionEntity);
                    }
                });
                verifyCancelAlertDialog.showDialog();
//                }
            }
            break;
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
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
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                        if (mediaPlayerControl != null) {
                            mediaPlayerControl.start();
                        }
                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                                (questionEntity);
                        videoQuestionLiveEntity.englishH5Entity.setNewEnglishH5(true);
                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                        showQuestion.onShow(true, videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                        showQuestion.onHide(questionEntity);
                    }
                });
                verifyCancelAlertDialog.showDialog();
//                }
            }
            break;
            default:
                break;
        }
    }

    protected VideoQuestionLiveEntity getVideoQuestionLiveEntity(VideoQuestionEntity questionEntity) {
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
        return videoQuestionLiveEntity;
    }

    protected EnglishH5CoursewareHttp getHttp() {
        return new EnglishH5CoursewareImpl();
    }

    class EnglishH5CoursewareImpl implements EnglishH5CoursewareHttp {

        @Override
        public void getStuGoldCount() {
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
//            if (!liveBackBll.getExperience()) {
            getCourseHttpManager().sumitCourseWareH5(
                    enstuId,
                    videoQuestionLiveEntity.srcType,
                    videoQuestionLiveEntity.id,
                    testAnswer,
                    videoQuestionLiveEntity.getAnswerDay(),
                    mVideoEntity.getLiveId(),
                    videoQuestionLiveEntity.courseware_type,
                    isSubmit,
                    voiceTime,
                    isRight,
                    httpCallBack);
//            } else {
//                String isArts = String.valueOf(liveBackBll.getIsArts());
//                getCourseHttpManager().submitExperienceCourseWareH5(
//                        mVideoEntity.getSubmitCourseWareH5AnswerUseVoiceUrl(),
//                        mVideoEntity.getLiveId(),
//                        videoQuestionLiveEntity.id,
//                        mVideoEntity.getChapterId(),
//                        testAnswer,
//                        voiceTime,
//                        isRight,
//                        isArts,
//                        httpCallBack);
//            }
        }
    }


    @Override
    public void onDestory() {
        super.onDestory();
        if (englishH5Cache != null) {
            englishH5Cache.stop();
        }
    }
}