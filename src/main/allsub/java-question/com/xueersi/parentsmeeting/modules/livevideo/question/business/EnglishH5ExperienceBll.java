package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.ExperCourseWareHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by：WangDe on 2018/8/30 11:22
 */
public class EnglishH5ExperienceBll extends LiveBackBaseBll {

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
        //语音答题
        WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, englishH5CoursewareBll.new
                LiveQuestionSwitchImpl());
        englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(wrapQuestionSwitch,
                englishH5CoursewareBll, liveGetInfo));

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
        return new int[]{LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE, LocalCourseConfig.CATEGORY_ENGLISH_MULH5COURSE_WARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
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
                VideoQuestionLiveEntity videoQuestionLiveEntity = getVideoQuestionLiveEntity(questionEntity, vCategory);
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
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                englishH5CoursewareBll.onH5Courseware("on", videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
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
        return videoQuestionLiveEntity;
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
                    getCourseWareHttpManager().getTestInfos(detailInfo.id, callBack);
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
                    getCourseWareHttpManager().submitMultiTest("" + testInfos, 1, isforce, callBack);
                } else if (TextUtils.equals(LiveQueConfig.EN_COURSE_TYPE_21, detailInfo.getArtType())) {
                    getCourseWareHttpManager().isSubmitH5Vote("" + testInfos, detailInfo.id, liveGetInfo.getStudentLiveInfo().getClassId(), liveGetInfo.getStuId(), 1, isforce, callBack);
                } else {
                    getCourseWareHttpManager().submitH5("" + testInfos, detailInfo.num, detailInfo.id, detailInfo.getArtType(), liveGetInfo.getStuId(), 1, isforce, callBack);
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
                                                   voiceTime, boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
            String stuId = LiveAppUserInfo.getInstance().getStuId();
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
