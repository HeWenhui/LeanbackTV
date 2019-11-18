package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.ExperCourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTIONS_SHOW;
import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTION_CLOSE;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionContract.INTELLIGENT_RECOGNITION_FILTER_ACTION;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionContract.INTELLIGENT_RECOGNITION_SIGN_KEY;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionContract.PROCESS_RECORD_SIGN;
import static com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig.EN_INTELLIGENT_EVALUTION;

/**
 * Created by：WangDe on 2018/8/30 11:22
 */
public class EnglishH5ExperienceBll extends LiveBackBaseBll {
    /**
     * ptType 过滤器
     */
    private List<String> ptTypeFilters = Arrays.asList(LiveQueConfig.ptTypeFilters);
    EnglishH5CoursewareBll englishH5CoursewareBll;
    private ExperCourseWareHttpManager courseWareHttpManager;
    int isArts;

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
//        englishH5CoursewareBll.setLiveBll(new EnglishH5CoursewareImpl());
        englishH5CoursewareBll.setLiveBll(new NewCourse());
        englishH5CoursewareBll.setGetInfo(liveGetInfo);
        WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, englishH5CoursewareBll.new
                LiveQuestionSwitchImpl());
        //语音答题
         if (liveBackBll.getPattern() == LiveVideoConfig.LIVE_PATTERN_2){
             LiveStandVoiceAnswerCreat liveStandVoiceAnswerCreat = new LiveStandVoiceAnswerCreat(activity, contextLiveAndBackDebug,
                     englishH5CoursewareBll.new LiveStandQuestionSwitchImpl(), liveGetInfo.getHeadImgPath(), liveGetInfo
                     .getStandLiveName());
             liveStandVoiceAnswerCreat.setLivePagerBack(englishH5CoursewareBll);
             englishH5CoursewareBll.setBaseVoiceAnswerCreat(liveStandVoiceAnswerCreat);
         }else {
             englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(wrapQuestionSwitch,
                     englishH5CoursewareBll, liveGetInfo).setExperience(true));
         }

        LiveBackBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new
                LiveBackBaseEnglishH5CoursewareCreat();
        liveBaseEnglishH5CoursewareCreat.setLiveGetInfo(liveGetInfo);
        isArts = liveBackBll.getIsArts();
        liveBaseEnglishH5CoursewareCreat.setArts(isArts);
        liveBaseEnglishH5CoursewareCreat.setWrapOnH5ResultClose(new WrapOnH5ResultClose(activity));
        liveBaseEnglishH5CoursewareCreat.setLivePagerBack(englishH5CoursewareBll);
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
    }

    @Override
    public void initView() {
        englishH5CoursewareBll.initView(getLiveViewAction());
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE, LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE,
                LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);//关闭答题
            }
            break;
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
                videoQuestionLiveEntity.setExper(true);
                EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                englishH5Entity.setNewEnglishH5(true);
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE: {
                if (EN_INTELLIGENT_EVALUTION.equals(questionEntity.getvQuestionType())) {
                    stopIntelligentActivity();
                    return;
                }
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
                Log.e("mqtt", "关闭上一题" + "CATEGORY_H5COURSE_NEWARTSWARE");
                EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTION_CLOSE, videoQuestionLiveEntity));
                englishH5CoursewareBll.onH5Courseware("off", videoQuestionLiveEntity);
            }
            break;
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
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_TUTOR_EVENT_35:
            case LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE: {
                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                questionEntity.setAnswered(true);
                // 获取拼装一题多发的字段
                LiveVideoConfig.LIVEPLAYBACKINFOS = questionEntity.getUrl();
                LiveVideoConfig.LIVEPLAYBACKSTUID = mVideoEntity.getStuCoulId();
                LiveVideoConfig.LIVEPLAYBACKCLASSID = mVideoEntity.getClassId();
                LiveVideoConfig.LIVEPLAYBACKTEAMID = mVideoEntity.getTeamId();
                LiveVideoConfig.LIVEPLAYBACKSTAGE = mVideoEntity.getEdustage();
                LiveVideoConfig.LIVEPLAYBACKTYPE = questionEntity.getName();
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
                EnglishH5Entity englishH5Entity =
                        videoQuestionLiveEntity.englishH5Entity;
                englishH5Entity.setNewEnglishH5(true);
//                liveGetInfo.setEducationStage(LiveVideoConfig.EDUCATION_STAGE_3);
                videoQuestionLiveEntity.setEducationstage(LiveVideoConfig.EDUCATION_STAGE_1);
                videoQuestionLiveEntity.setExper(true);
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
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE: {
                //LiveVideoConfig.isNewArts = true;
                questionEntity.setNewArtsCourseware(true);
                // mCurrentQuestionEntity.setNewArtsCourseware(true);
                questionEntity.setAnswered(true);
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntityNewCourse
                        (questionEntity, vCategory);
                if (ptTypeFilters.contains(videoQuestionLiveEntity.type) && !"1".equals(videoQuestionLiveEntity
                        .getIsVoice())) {
                    Loger.e("EnglishH5back", "====> return h5back");
                    return;
                }

                videoQuestionLiveEntity.setExper(true);
                videoQuestionLiveEntity.englishH5Entity.setArtsNewH5Courseware(true);
                EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTIONS_SHOW, videoQuestionLiveEntity));
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
                if (EN_INTELLIGENT_EVALUTION.equals(videoQuestionLiveEntity.type)) {
                    Bundle bundle = new Bundle();
                    IntelligentRecognitionRecord intelligentRecognitionRecord = new IntelligentRecognitionRecord();
//                intelligentRecognitionRecord.setAnswerTime(questionEntity.get);
                    intelligentRecognitionRecord.setStuId(liveGetInfo.getStuId());
                    intelligentRecognitionRecord.setStuCouId(liveGetInfo.getStuCouId());
                    intelligentRecognitionRecord.setLiveId(liveGetInfo.getId());
                    intelligentRecognitionRecord.setMaterialId(videoQuestionLiveEntity.id);
                    intelligentRecognitionRecord.setIsPlayBack("1");
                    bundle.putParcelable(PROCESS_RECORD_SIGN, intelligentRecognitionRecord);
                    XueErSiRouter.startModule(activity, "/aievaluation/intelligent_recognition", bundle);
                    return;
                }
            }
            break;
            default:
                break;
        }
    }

    private VideoQuestionLiveEntity getVideoQuestionLiveEntity(VideoQuestionEntity questionEntity, int vCategory) {

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
        if (vCategory == LocalCourseConfig.CATEGORY_TUTOR_EVENT_35) {
            videoQuestionLiveEntity.setTUtor(true);
        }
        if (liveGetInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY){
            videoQuestionLiveEntity.setLiveType(LiveVideoConfig.ExperiencLiveType.HALF_BODY);
            videoQuestionLiveEntity.englishH5Entity.setNewEnglishH5(true);
            String url = buildCourseUrl(videoQuestionLiveEntity, questionEntity);
            videoQuestionLiveEntity.englishH5Entity.setUrl(url);
        }
        return videoQuestionLiveEntity;
    }

    protected VideoQuestionLiveEntity getVideoQuestionLiveEntityNewCourse(VideoQuestionEntity questionEntity, int vCategory) {
        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
        if (vCategory == LocalCourseConfig.CATEGORY_TUTOR_EVENT_35) {
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
        if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_H5COURSE_NEWARTSWARE) {
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
            if (LiveQueConfig.EN_COURSE_TYPE_NEW_GAME.equals(type)) {
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

    private String buildCourseUrl(String testIds) {
        StringBuilder sb = new StringBuilder();
        String falseStr = Base64.encodeBytes("false".getBytes());
        sb.append(LiveHttpConfig.URL_ARTS_H5_URL).append("?liveId=").append(mVideoEntity.getLiveId())
                .append("&testIds=").append(testIds).append("&isPlayBack=").append("2")
                .append("&stuCouId=").append(mVideoEntity.getStuCoulId()).append("&stuId=").append(LiveAppUserInfo.getInstance().getStuId())
                .append("&stuClientPath=").append(falseStr)
                .append("&fontDir=").append(falseStr);
        return sb.toString();
    }

    /**
     * 构建体验课 新课件平台 试题加载地址
     **/
    private String buildCourseUrl(VideoQuestionLiveEntity videoQuestionLiveEntity, VideoQuestionEntity questionEntity) {
        if (questionEntity == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        String classTestId = "";
        String packageId = "";
        String packageSource = "";
        String packageAttr = "";
        String releasedPageInfos = "";
        if (!TextUtils.isEmpty(questionEntity.getCourseExtInfo())) {
            try {
                JSONObject jsonObject = new JSONObject(questionEntity.getCourseExtInfo());
                classTestId = jsonObject.optString("ctId");
                packageAttr = jsonObject.optString("pAttr");
                packageId = jsonObject.optString("pId");
                packageSource = jsonObject.optString("pSrc");
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(TAG, e);
            }
        }
        String url = TextUtils.isEmpty(mVideoEntity.getHalfBodyH5Url()) ? LiveHttpConfig.URL_HALFBODY_EXPERIENCE_LIVE_H5 : mVideoEntity.getHalfBodyH5Url();
        stringBuilder.append(url)
                .append("?stuId=").append(stuId)
                .append("&liveId=").append(mVideoEntity.getLiveId())
                .append("&packageSource=").append(packageSource)
                .append("&packageId=").append(packageId)
                .append("&termId=").append(mVideoEntity.getChapterId())
                .append("&releasedPageInfos=").append(questionEntity.getReleasedPageInfos());
        return stringBuilder.toString();
    }

    private String buildCourseH5Url(String testIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(LiveHttpConfig.URL_ARTS_COURSE_H5_URL).append("?stuId=").append(LiveAppUserInfo.getInstance().getStuId())
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

    public ExperCourseWareHttpManager getCourseWareHttpManager() {
        if (courseWareHttpManager == null) {
            courseWareHttpManager = new ExperCourseWareHttpManager(getmHttpManager());
        }
        return courseWareHttpManager;
    }

    class NewCourse extends EnglishH5CoursewareImpl implements EnglishH5CoursewareSecHttp {

        @Override
        public void getCourseWareTests(String url, String params, AbstractBusinessDataCallBack callBack) {
            logger.d("getCourseWareTests");
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
        public void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack) {
            logger.d("getCourseWareTests");
            if (isArts == LiveVideoSAConfig.ART_EN) {
                if (LiveQueConfig.isGroupGame(detailInfo.type)) {
                    getCourseWareHttpManager().getGroupGameTestInfos(detailInfo.id, liveGetInfo.getStuId(), detailInfo.type, callBack);
                } else {
                    getCourseWareHttpManager().getTestInfos(LiveAppUserInfo.getInstance().getStuId(), detailInfo.id, callBack);
                }
            } else {
                EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
                String classId = liveGetInfo.getStudentLiveInfo().getClassId();
                String[] res = getSrcType(englishH5Entity);
                getCourseWareHttpManager().getCourseWareTests(detailInfo, liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
                        englishH5Entity.getReleasedPageInfos(), 0, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), detailInfo.nonce, liveGetInfo.getIsAllowTeamPk(), callBack);
            }
        }

        @Override
        public void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, int isforce, String nonce, long entranceTime, String testInfos, AbstractBusinessDataCallBack callBack) {
            logger.d("submitCourseWareTests");
            if (isArts == LiveVideoSAConfig.ART_EN && !detailInfo.isTUtor()) {
//                if (LiveQueConfig.EN_COURSE_TYPE_VOICE_BLANK.equals(detailInfo.voiceType) || LiveQueConfig.EN_COURSE_TYPE_VOICE_CHOICE.equals(detailInfo.voiceType)) {
//                    getCourseWareHttpManager().submitH5("" + testInfos, detailInfo.num, detailInfo.id, detailInfo.voiceType, mGetInfo.getStuId(), 1, isforce, callBack);
//                } else {
//                    if (LiveQueConfig.getSubmitH5Types().contains(detailInfo.type)) {
//                        getCourseWareHttpManager().submitH5("" + testInfos, detailInfo.num, detailInfo.id, detailInfo.voiceType, mGetInfo.getStuId(), 1, isforce, callBack);
//                    } else {
//                        getCourseWareHttpManager().submitMultiTest("" + testInfos, 1, isforce, callBack);
//                    }
//                }
                if (LiveQueConfig.getSubmitMultiTestTypes().contains(detailInfo.getArtType())) {
                    getCourseWareHttpManager().submitMultiTest(detailInfo, "" + testInfos, 0, isforce, callBack);
                } else if (TextUtils.equals(LiveQueConfig.EN_COURSE_TYPE_21, detailInfo.getArtType())) {
                    getCourseWareHttpManager().isSubmitH5Vote("" + testInfos, detailInfo.id, liveGetInfo.getStudentLiveInfo().getClassId(), liveGetInfo.getStuId(), 0, isforce, callBack);
                } else {
                    getCourseWareHttpManager().submitH5(detailInfo, "" + testInfos, detailInfo.num, detailInfo.id, detailInfo.getArtType(), liveGetInfo.getStuId(), 0, isforce, callBack);
                }
            } else {
                EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
                String classId = liveGetInfo.getStudentLiveInfo().getClassId();
                String[] res = getSrcType(englishH5Entity);
                getCourseWareHttpManager().submitCourseWareTests(detailInfo, liveGetInfo.getStuId(), englishH5Entity.getPackageId(), englishH5Entity.getPackageSource(), englishH5Entity.getPackageAttr(),
                        englishH5Entity.getReleasedPageInfos(), 0, classId, englishH5Entity.getClassTestId(), res[0], res[1], liveGetInfo.getEducationStage(), nonce, testInfos, isforce, entranceTime, callBack);
            }
        }

        @Override
        public void submitGroupGame(VideoQuestionLiveEntity detailInfo, int gameMode, int voiceTime, int pkTeamId, int gameGroupId, int starNum, int energy, int gold, int videoLengthTime, int micLengthTime, int acceptVideoLengthTime, int acceptMicLengthTime, String answerData, AbstractBusinessDataCallBack callBack) {
            logger.d("submitGroupGame");
        }

        @Override
        public String getResultUrl(VideoQuestionLiveEntity detailInfo, int isforce, String nonce) {
            logger.d("getResultUrl");
            return null;
        }

        @Override
        public void getStuTestResult(VideoQuestionLiveEntity detailInfo, int isPlayBack, AbstractBusinessDataCallBack callBack) {
            logger.d("getStuTestResult");
            EnglishH5Entity englishH5Entity = detailInfo.englishH5Entity;
            String[] res = getSrcType(englishH5Entity);
            if ((LiveVideoConfig.EDUCATION_STAGE_3.equals(detailInfo.getEducationstage()) || LiveVideoConfig.EDUCATION_STAGE_4.equals(detailInfo.getEducationstage()))
                    && LiveQueConfig.CHI_COURESWARE_TYPE_AISUBJECTIVE.equals(englishH5Entity.getPackageAttr())) {
                getCourseWareHttpManager().getStuChiAITestResult(liveGetInfo.getId(), liveGetInfo.getStuId(), res[0], res[1], englishH5Entity.getClassTestId(), englishH5Entity.getPackageId(),
                        englishH5Entity.getPackageAttr(), isPlayBack, liveGetInfo.getStudentLiveInfo().getClassId(), callBack);
            } else {
                getCourseWareHttpManager().getStuTestResult(liveGetInfo.getId(), liveGetInfo.getStuId(), res[0], res[1], englishH5Entity.getClassTestId(), englishH5Entity.getPackageId(),
                        englishH5Entity.getPackageAttr(), englishH5Entity.getPackageSource(), isPlayBack, callBack, detailInfo.isTUtor());
            }
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
                                                   voiceTime, final boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
            String stuId = LiveAppUserInfo.getInstance().getStuId();
            String userMode = "1";
            String isArts = String.valueOf(liveBackBll.getIsArts());
            HttpCallBack callBack = new HttpCallBack() {

                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer
                            (responseEntity,
                                    true);
                    entity.setVoice(true);
                    entity.setExperience(true);
                    if (StringUtils.isSpace(entity.getTestId())) {
                        entity.setTestId(videoQuestionLiveEntity.id);
                    }
                    if (videoQuestionLiveEntity.isNewArtsH5Courseware()) {
                        entity.setResultType(isRight ? 2 : 0);
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
            if (videoQuestionLiveEntity.isNewArtsH5Courseware()) {
                getCourseWareHttpManager().sumitCourseWareH5(videoQuestionLiveEntity,
                        videoQuestionLiveEntity.id,
                        testAnswer,
                        videoQuestionLiveEntity.getAnswerDay(),
                        mVideoEntity.getLiveId(),
                        videoQuestionLiveEntity.courseware_type,
                        isSubmit,
                        voiceTime,
                        isRight,
                        callBack);
            } else {
                getCourseHttpManager().submitExperienceCourseWareH5(
                        mVideoEntity.getSubmitCourseWareH5AnswerUseVoiceUrl()
                        , mVideoEntity.getLiveId(),
                        videoQuestionLiveEntity.id,
                        mVideoEntity.getChapterId(),
                        testAnswer, voiceTime,
                        isRight, isArts,
                        callBack);
            }
        }
    }

    /**
     * 终止智能测评的Activity
     */
    private void stopIntelligentActivity() {
        setPauseNotStop(false);
        Intent intent = new Intent(INTELLIGENT_RECOGNITION_FILTER_ACTION);
        intent.putExtra(INTELLIGENT_RECOGNITION_SIGN_KEY, new JSONObject().toString());
        activity.sendBroadcast(intent);
    }

    PauseNotStopVideoInter pauseNotStopVideoIml;

    /** onPause状态不暂停视频 */
//    AtomicBoolean onPauseNotStopVideo = new AtomicBoolean(false);
    private void setPauseNotStop(boolean pauseNotStop) {
//        onPauseNotStopVideo.set(pauseNotStop);
        pauseNotStopVideoIml = ProxUtil.getProxUtil().get(activity, PauseNotStopVideoInter.class);
//        if (pauseNotStopVideoIml == null) {
//            pauseNotStopVideoIml = new PauseNotStopVideoIml(activity);
//        } else {
        if (pauseNotStopVideoIml != null) {
            pauseNotStopVideoIml.setPause(pauseNotStop);
        } else {
//            Map<String, String> map = new HashMap<>();
//            map.put("ProxUtil_getProxUtil", "PauseNotStopVideoInter.class is null");
//            UmsAgentManager.umsAgentDebug();
        }
        //声音设置为0
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(0, 0);
        }
//        }
    }

}
