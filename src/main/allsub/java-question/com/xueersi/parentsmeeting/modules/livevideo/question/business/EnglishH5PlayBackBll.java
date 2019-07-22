package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ChsSpeakEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.CourseWareHttpManager;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTIONS_SHOW;
import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTION_CLOSE;

/**
 * Created by linyuqiang on 2018/7/17.
 * 直播回放英语课件
 */
public class EnglishH5PlayBackBll extends LiveBackBaseBll {
    EnglishH5CoursewareBll englishH5CoursewareBll;
    private EnglishH5Cache englishH5Cache;
    String[] filters = {"4", "0", "1", "2", "8", "5", "6"};
    /**
     * ptType 过滤器
     */
    private List<String> ptTypeFilters = Arrays.asList(filters);
    private CourseWareHttpManager courseWareHttpManager;
    private VideoQuestionEntity mCurrentQuestionEntity;

    public EnglishH5PlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
        englishH5CoursewareBll.setIsplayback(1);
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
                    englishH5CoursewareBll, liveGetInfo));
        }
        LiveBackBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new
                LiveBackBaseEnglishH5CoursewareCreat();
        liveBaseEnglishH5CoursewareCreat.setLiveGetInfo(liveGetInfo);
        int isArts = liveBackBll.getIsArts();
        liveBaseEnglishH5CoursewareCreat.setArts(isArts);
        liveBaseEnglishH5CoursewareCreat.setWrapOnH5ResultClose(new WrapOnH5ResultClose(activity));
        liveBaseEnglishH5CoursewareCreat.setLivePagerBack(englishH5CoursewareBll);
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            englishH5Cache = new EnglishH5Cache(activity, liveGetInfo);
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
//        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE, LocalCourseConfig
//                .CATEGORY_ENGLISH_MULH5COURSE_WARE};
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE, LocalCourseConfig
                .CATEGORY_ENGLISH_MULH5COURSE_WARE, LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE
                , LocalCourseConfig.CATEGORY_TUTOR_EVENT_35};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                 // 语文开讲吧回放试题，不再出来扫描到的 结束答题时间点
                if(questionEntity != null && isChsSpeaking(questionEntity.getEnglishH5Entity())){
                    Log.e("chs_speak","====>H5playBackBll_onQuestionEnd");
                 }else{
                    VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                    englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
                }
            }
            break;
            case LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity,vCategory);
                Log.e("mqtt", "关闭上一题" + "CATEGORY_H5COURSE_NEWARTSWARE");
                EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTION_CLOSE, videoQuestionLiveEntity));
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, final
    LiveBackBll.ShowQuestion showQuestion) {
        mCurrentQuestionEntity = questionEntity;
        mRootView.setVisibility(View.VISIBLE);
        final int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                LiveVideoConfig.isNewArts = false;
                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
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
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        if (mediaPlayerControl != null) {
                            mediaPlayerControl.start();
                        }
                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                                (questionEntity,vCategory);

                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                        showQuestion.onShow(true, videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                        showQuestion.onHide(questionEntity);
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            case LocalCourseConfig.CATEGORY_TUTOR_EVENT_35:
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                LiveVideoConfig.isNewArts = false;
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
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                                (questionEntity,vCategory);
                        EnglishH5Entity englishH5Entity =
                                videoQuestionLiveEntity.englishH5Entity;
                        englishH5Entity.setNewEnglishH5(true);
                        try {
                            JSONObject jsonObject = new JSONObject(questionEntity.getName());
                            String classTestId = jsonObject.optString("ctId");
                            String packageAttr = jsonObject.optString("pAttr");
                            String packageId = jsonObject.optString("pId");
                            String packageSource = jsonObject.optString("pSrc");
                            englishH5Entity.setReleasedPageInfos(questionEntity.getUrl());
                            englishH5Entity.setClassTestId(classTestId);
                            englishH5Entity.setPackageAttr(packageAttr);
                            englishH5Entity.setPackageId(packageId);
                            englishH5Entity.setPackageSource(packageSource);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                        //语文开讲吧 出现试题不在恢复视频播放
                        if(!isChsSpeaking(englishH5Entity)){
                            BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                            if (mediaPlayerControl != null) {
                                mediaPlayerControl.start();
                            }
                        }
                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                        showQuestion.onShow(true, videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                        showQuestion.onHide(questionEntity);
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            case LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE: {
                LiveVideoConfig.isNewArts = true;
                Log.e("Duncan", "mqtt+文科新课件平台");
                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
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
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        if (mediaPlayerControl != null) {
                            mediaPlayerControl.start();
                        }
                        VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity
                                (questionEntity,vCategory);
                        if (ptTypeFilters.contains(videoQuestionLiveEntity.type) && !"1".equals(videoQuestionLiveEntity
                                .getIsVoice())) {
                            Loger.e("EnglishH5back", "====> return h5back");
                            return;
                        }
                        videoQuestionLiveEntity.englishH5Entity.setArtsNewH5Courseware(true);
                        EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTIONS_SHOW, videoQuestionLiveEntity));
                        englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                        showQuestion.onShow(true, videoQuestionLiveEntity);
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                        showQuestion.onHide(questionEntity);
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            default:
                break;
        }
    }

    /**
     * 是否是语文开讲吧的相关试题
     * @param englishH5Entity
     * @return
     */
    private boolean isChsSpeaking(EnglishH5Entity englishH5Entity) {
        return englishH5Entity!= null && LiveQueConfig.CHI_COURESWARE_TYPE_SPEAKING_CHINESE.equals(englishH5Entity.getPackageAttr());
    }

    protected VideoQuestionLiveEntity getVideoQuestionLiveEntity(VideoQuestionEntity questionEntity,int vCategory) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        if (vCategory ==  LocalCourseConfig.CATEGORY_TUTOR_EVENT_35) {
            videoQuestionLiveEntity.setTUtor(true);
        }
        videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
        videoQuestionLiveEntity.englishH5Entity = questionEntity.getEnglishH5Entity();
        String isVoice = questionEntity.getIsVoice();
        videoQuestionLiveEntity.setIsVoice(isVoice);
        if ("1".equals(isVoice)) {
            videoQuestionLiveEntity.type = questionEntity.getVoiceQuestiontype();
        }
        videoQuestionLiveEntity.setArtType(questionEntity.getVoiceQuestiontype());
        videoQuestionLiveEntity.assess_ref = questionEntity.getAssess_ref();
        if (questionEntity.getvCategory() == 1000) {
            List<String> testIds = new ArrayList<>();
            if (testIds.size() > 0) {
                testIds.clear();
            }
            String type = "";
            String isVoices = "";
            String assess = "";
            for (int i = 0; i < questionEntity.getReleaseInfos().size(); i++) {
                testIds.add(questionEntity.getReleaseInfos().get(i).getId());
                type = questionEntity.getReleaseInfos().get(0).getType();
                isVoices = questionEntity.getReleaseInfos().get(0).getIsVoice();
                assess = questionEntity.getReleaseInfos().get(0).getAssess_ref();
                videoQuestionLiveEntity.id = questionEntity.getReleaseInfos().get(0).getId();
            }
            if ("17".equals(type)) {
                videoQuestionLiveEntity.setUrl(buildCourseH5Url(getTestIdS(testIds)));
            } else {
                videoQuestionLiveEntity.setUrl(buildCourseUrl(getTestIdS(testIds)));
            }
            videoQuestionLiveEntity.setIsVoice(isVoices);
            videoQuestionLiveEntity.assess_ref = assess;
            videoQuestionLiveEntity.type = type;
            videoQuestionLiveEntity.courseware_type = type;
        } else {
            videoQuestionLiveEntity.setUrl(questionEntity.getEnglishH5Play_url());
            videoQuestionLiveEntity.courseware_type = questionEntity.getvQuestionType();
        }
        videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
        videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
        videoQuestionLiveEntity.setAnswerDay(questionEntity.getAnswerDay());
        return videoQuestionLiveEntity;
    }

    protected EnglishH5CoursewareHttp getHttp() {
        int isArts = liveBackBll.getIsArts();
        //if (isArts == LiveVideoSAConfig.ART_EN || (isArts == LiveVideoSAConfig.ART_SEC && liveBackBll.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY)) {
            return new EnglishH5CoursewareSecImpl();
       // }
        //return new EnglishH5CoursewareImpl();
    }

    public CourseWareHttpManager getCourseWareHttpManager() {
        if (courseWareHttpManager == null) {
            courseWareHttpManager = new CourseWareHttpManager(getmHttpManager());
        }
        return courseWareHttpManager;
    }

    class EnglishH5CoursewareSecImpl extends EnglishH5CoursewareImpl implements EnglishH5CoursewareSecHttp {

        @Override
        public String getResultUrl(VideoQuestionLiveEntity detailInfo, int isforce, String nonce) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
            EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
            String classId = studentLiveInfo.getClassId();
            String teamId = studentLiveInfo.getTeamId();
            String educationStage = liveGetInfo.getEducationStage();
            StringBuilder stringBuilder;
            if (detailInfo.isTUtor()) {
                stringBuilder = new StringBuilder(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_RESULT_TUTOR_FILE);

            } else {
                stringBuilder = new StringBuilder(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_RESULT_FILE);

            }
            stringBuilder.append("?stuId=").append(liveGetInfo.getStuId());
            stringBuilder.append("&liveId=").append(liveGetInfo.getId());
            stringBuilder.append("&stuCouId=").append(liveBackBll.getStuCourId());
            stringBuilder.append("&classId=").append(classId);
            stringBuilder.append("&teamId=").append(teamId);
            stringBuilder.append("&packageId=").append(englishH5Entity.getPackageId());
            stringBuilder.append("&packageSource=").append(englishH5Entity.getPackageSource());
            stringBuilder.append("&packageAttr=").append(englishH5Entity.getPackageAttr());
            stringBuilder.append("&classTestId=").append(englishH5Entity.getClassTestId());
            stringBuilder.append("&isPlayBack=1");
            stringBuilder.append("&educationStage=").append(educationStage);
            stringBuilder.append("&isShowTeamPk=").append(0);
            stringBuilder.append("&nonce=").append(nonce);
            stringBuilder.append("&isforce=").append(isforce);
            stringBuilder.append("&releasedPageInfos=").append(englishH5Entity.getReleasedPageInfos());
            String resUrl = stringBuilder.toString();
            return resUrl;
        }

        /**
         * 学生作答情况列表
         */
        @Override
        public void getStuTestResult(VideoQuestionLiveEntity detailInfo, int isPlayBack, AbstractBusinessDataCallBack callBack) {
            EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
            String[] res = getSrcType(englishH5Entity);
            getCourseWareHttpManager().getStuTestResult(liveGetInfo.getId(), liveGetInfo.getStuId(), res[0], res[1], englishH5Entity.getClassTestId(), englishH5Entity.getPackageId(),
                    englishH5Entity.getPackageAttr(), isPlayBack, callBack,detailInfo.isTUtor());
        }

        @Override
        public void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, int isforce, String nonce, long entranceTime, String testInfos, AbstractBusinessDataCallBack callBack) {
            EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
            String classId = liveGetInfo.getStudentLiveInfo().getClassId();
            String[] res = getSrcType(englishH5Entity);
            getCourseWareHttpManager().submitCourseWareTests(detailInfo,liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
                    englishH5Entity.getReleasedPageInfos(), 1, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), nonce, testInfos, isforce, entranceTime, callBack);
        }

        @Override
        public void submitGroupGame(VideoQuestionLiveEntity detailInfo, int gameMode, int voiceTime, int pkTeamId, int gameGroupId, int starNum, int energy, int gold, int videoLengthTime, int micLengthTime, int acceptVideoLengthTime, int acceptMicLengthTime, String answerData, AbstractBusinessDataCallBack callBack) {
            String classId = liveGetInfo.getStudentLiveInfo().getClassId();
            getCourseWareHttpManager().submitGroupGame(classId, detailInfo.id, detailInfo.type, gameMode, voiceTime, 2, pkTeamId, gameGroupId, starNum, energy, gold, videoLengthTime, micLengthTime, acceptVideoLengthTime, acceptMicLengthTime, answerData, callBack);
        }

        @Override
        public void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack) {
            if (liveBackBll.getIsArts() == LiveVideoSAConfig.ART_EN) {
                if (LiveQueConfig.isGroupGame(detailInfo.type)) {
                    getCourseWareHttpManager().getGroupGameTestInfos(detailInfo.id, liveGetInfo.getStuId(), detailInfo.type, callBack);
                } else {
                    EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
                    String classId = liveGetInfo.getStudentLiveInfo().getClassId();
                    String[] res = getSrcType(englishH5Entity);
                    getCourseWareHttpManager().getCourseWareTests(detailInfo,liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
                            englishH5Entity.getReleasedPageInfos(), 0, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), detailInfo.nonce, "0", callBack);
                }
            }
//            else if (LiveVideoSAConfig.ART_SEC == liveBackBll.getIsArts()){
//                if (liveBackBll.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY && LiveQueConfig.CHI_COURESWARE_TYPE_SPEAKING_CHINESE.equals(detailInfo.englishH5Entity.getPackageAttr())){
//                    EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
//                    String classId = liveGetInfo.getStudentLiveInfo().getClassId();
//                    String[] res = getSrcType(englishH5Entity);
//                    getCourseWareHttpManager().getCourseWareTests(detailInfo,liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
//                            englishH5Entity.getReleasedPageInfos(), 1, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), detailInfo.nonce, "0", callBack);
//                }
//            }
            else {

                EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
                String classId = liveGetInfo.getStudentLiveInfo().getClassId();
                String[] res = getSrcType(englishH5Entity);
                getCourseWareHttpManager().getCourseWareTests(detailInfo,liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
                        englishH5Entity.getReleasedPageInfos(), 1, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), detailInfo.nonce, "0", callBack);
            }
        }

        private String[] getSrcType(EnglishH5Entity englishH5Entity) {
            String[] res = new String[2];
            String srcTypes = "";
            String testIds = "";
            try {
                JSONArray array = new JSONArray(englishH5Entity.getReleasedPageInfos());
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONArray value = jsonObject.getJSONArray(key);
                        srcTypes += value.getString(0);
                        testIds += value.getString(1);
                        if (i != length - 1) {
                            srcTypes += ",";
                            testIds += ",";
                        }
                    }
                }
            } catch (JSONException e) {
                logger.e("getCourseWareTests", e);
            }
            res[0] = srcTypes;
            res[1] = testIds;
            return res;
        }

        @Override
        public void getCourseWareTests(String url, String params, final AbstractBusinessDataCallBack callBack) {
            HttpRequestParams httpRequestParams = creatHttpRequestParams(params);
            getmHttpManager().sendPostNoBusiness(url, httpRequestParams, new Callback() {

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String r = response.body().string();
                    logger.d("getCourseWareTests:onResponse=" + r);
                    callBack.onDataSucess(r);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    logger.e("onFailure", e);
                    if (e instanceof UnknownHostException) {
                        callBack.onDataFail(0, "UnknownHostException");
                    } else {
                        callBack.onDataFail(0, Log.getStackTraceString(e));
                    }
                }
            });
        }

        private HttpRequestParams creatHttpRequestParams(String params) {
            HttpRequestParams httpRequestParams = new HttpRequestParams();
            try {
                JSONObject jsonObject = new JSONObject(params);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonObject.getString(key);
                    httpRequestParams.addBodyParam(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return httpRequestParams;
        }
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

        @Override
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID,
                                           String testAnswer, String courseware_type, String isSubmit, double
                                                   voiceTime, boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
            final Boolean isRights = isRight;
            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            String userMode = "1";
            HttpCallBack httpCallBack = new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer(responseEntity,
                            true);
                    entity.setVoice(true);
                    if (LiveVideoConfig.isNewArts) {
                        entity.setResultType(isRights ? 2 : 0);
                    }
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

    private String buildCourseUrl(String testIds) {
        StringBuilder sb = new StringBuilder();
        String falseStr = Base64.encodeBytes("false".getBytes());
        sb.append(new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false).inner.URL_ARTS_H5_URL).append("?liveId=").append(mVideoEntity.getLiveId())
                .append("&testIds=").append(testIds).append("&isPlayBack=").append("2")
                .append("&stuCouId=").append(mVideoEntity.getStuCoulId()).append("&stuId=").append(UserBll.getInstance().getMyUserInfoEntity().getStuId())
                .append("&xesrfh=").append(AppBll.getInstance().getUserRfh())
                .append("&cookie=").append(AppBll.getInstance().getUserToken())
                .append("&stuClientPath=").append(falseStr)
                .append("&fontDir=").append(falseStr);
        return sb.toString();
    }

    private String buildCourseH5Url(String testIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false).inner.URL_ARTS_COURSE_H5_URL).append("?stuId=").append(UserBll.getInstance().getMyUserInfoEntity().getStuId())
                .append("&stuCouId=").append(mVideoEntity.getStuCoulId()).append("&liveId=").append(mVideoEntity.getLiveId())
                .append("&testId=").append(testIds).append("&type=").append(17).append("&isPlayBack=1");
        return sb.toString();
    }

    private String getTestIdS(List<String> testIds) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (testIds != null) {
                for (int i = 0; i < testIds.size(); i++) {
                    if (i < (testIds.size() - 1)) {
                        stringBuilder.append(testIds.get(i)).append(",");
                    } else {
                        stringBuilder.append(testIds.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChsSpeakPageClose(ChsSpeakEvent event){
        if(event.getEventType() == ChsSpeakEvent.EVENT_TYPE_PAGE_CLOSE){
            BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
            // 语文开讲吧 关闭页面 调转到试题结束 时间点
            //Log.e("nbTrace","========>onChsSpeakPageClose endTime:"+mediaPlayerControl.isPlaying());
            if(mediaPlayerControl != null && !mediaPlayerControl.isPlaying()){
                if(mCurrentQuestionEntity != null){
                   // Log.e("nbTrace","========>onChsSpeakPageClose endTime:"+mCurrentQuestionEntity.getvEndTime());
                    mediaPlayerControl.seekTo(mCurrentQuestionEntity.getvEndTime() * 1000);
                    mediaPlayerControl.start();
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (englishH5Cache != null) {
            englishH5Cache.stop();
        }
        EventBus.getDefault().unregister(this);
    }
}
