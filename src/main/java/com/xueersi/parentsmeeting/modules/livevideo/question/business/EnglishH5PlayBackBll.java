package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoPlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/17.
 * 直播回放英语课件
 */
public class EnglishH5PlayBackBll extends LiveBackBaseBll {
    EnglishH5CoursewareBll englishH5CoursewareBll;

    public EnglishH5PlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
        englishH5CoursewareBll.setLiveType(mLiveType);
        englishH5CoursewareBll.setVSectionID(mVideoEntity.getLiveId());
        englishH5CoursewareBll.setLiveBll(new EnglishH5CoursewareImpl());
        if (liveBackBll.getPattern() == 2) {
            LiveAndBackDebug liveAndBackDebug = getInstance(LiveAndBackDebug.class);
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveStandVoiceAnswerCreat(activity, liveAndBackDebug, englishH5CoursewareBll.new LiveStandQuestionSwitchImpl(), liveGetInfo.getHeadImgPath(), liveGetInfo.getStandLiveName()) {
                @Override
                public boolean onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                    MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                    if (videoPlayAction != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
                        videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                        videoPlayAction.start();
                    }
                    return super.onAnswerReslut(context, questionBll, baseVoiceAnswerPager, baseVideoQuestionEntity, entity);
                }
            });
        } else {
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(englishH5CoursewareBll.new LiveQuestionSwitchImpl()) {
                @Override
                public boolean onAnswerReslut(Context context, AnswerRightResultVoice questionBll, BaseVoiceAnswerPager baseVoiceAnswerPager, BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                    MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                    if (videoPlayAction != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
                        videoPlayAction.seekTo(videoQuestionLiveEntity.getvEndTime() * 1000);
                        videoPlayAction.start();
                    }
                    return super.onAnswerReslut(context, questionBll, baseVoiceAnswerPager, baseVideoQuestionEntity, entity);
                }
            });
        }
        LiveBackBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new LiveBackBaseEnglishH5CoursewareCreat();
        int isArts = (int) liveBackBll.getBusinessShareParam("isArts");
        liveBaseEnglishH5CoursewareCreat.setIS_SCIENCE(isArts != 1);
        liveBaseEnglishH5CoursewareCreat.setWrapOnH5ResultClose(new WrapOnH5ResultClose() {
            @Override
            public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager) {
                super.onH5ResultClose(baseEnglishH5CoursewarePager);
                MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                if (videoPlayAction != null) {
                    videoPlayAction.seekTo(videoQuestionH5Entity.getvEndTime() * 1000);
                    videoPlayAction.start();
                }
            }
        });
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
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
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
            }
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                if (videoPlayAction != null) {
                    videoPlayAction.pause();
                }
                questionEntity.setAnswered(true);
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                        if (videoPlayAction != null) {
                            videoPlayAction.start();
                        }
                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity);
                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaPlayerControl videoPlayAction = getInstance(MediaPlayerControl.class);
                        videoPlayAction.seekTo(questionEntity.getvEndTime() * 1000);
                        videoPlayAction.start();
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            default:
                break;
        }
    }

    private VideoQuestionLiveEntity getVideoQuestionLiveEntity(VideoQuestionEntity questionEntity) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        videoQuestionLiveEntity.id = videoQuestionLiveEntity.id;
        videoQuestionLiveEntity.englishH5Entity = questionEntity.getEnglishH5Entity();
        videoQuestionLiveEntity.setIsVoice(questionEntity.getIsVoice());
        videoQuestionLiveEntity.setUrl(questionEntity.getEnglishH5Play_url());
        videoQuestionLiveEntity.courseware_type = questionEntity.getvQuestionType();
        videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
        videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
        return videoQuestionLiveEntity;
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
        public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, final AbstractBusinessDataCallBack callBack) {
            //回放没有
        }

        @Override
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, String courseware_type, String isSubmit, double voiceTime, boolean isRight, final QuestionSwitch.OnAnswerReslut onAnswerReslut) {
            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            String userMode = "1";
            getCourseHttpManager().sumitCourseWareH5(enstuId, videoQuestionLiveEntity.srcType,
                    videoQuestionLiveEntity.id, mVideoEntity.getLiveId(), testAnswer, courseware_type, userMode, isSubmit, voiceTime, isRight, new
                            HttpCallBack() {

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
                            });
        }
    }

}
