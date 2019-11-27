package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.ExperCourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTIONS_SHOW;
import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTION_CLOSE;

/**
 * Created by：WangDe on 2018/8/28 11:44
 * 体验课互动题
 */
public class QuestionExperienceBll extends LiveBackBaseBll {
    QuestionBll questionBll;
    private List<String> questiongtype = Arrays.asList(LiveQueConfig.ptTypeFilters);
    private ExperCourseWareHttpManager courseWareHttpManager;

    public QuestionExperienceBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        questionBll = new QuestionBll(activity, liveBackBll.getStuCourId());
        questionBll.setLiveVideoSAConfig(liveBackBll.getLiveVideoSAConfig());
        questionBll.setLiveType(liveBackBll.getLiveType());
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        questionBll.setVSectionID(mVideoEntity.getLiveId());
        questionBll.setShareDataManager(mShareDataManager);
        questionBll.setLiveGetInfo(liveGetInfo);
//        questionBll.setQuestionHttp(new ExperQuestionHttp());
        questionBll.setQuestionHttp(new ExperCourse());
        //语音答题
        WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                LiveQuestionSwitchImpl());
        questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
        if (liveGetInfo.getPattern() == 2){
            //语音评测
            LiveBackStandSpeechCreat liveBackStandSpeechCreat = new LiveBackStandSpeechCreat(liveBackBll,
                    questionBll);
            liveBackStandSpeechCreat.setIsExperience(liveBackBll.getExperience());
            liveBackStandSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackStandSpeechCreat);
        } else {
            //语音评测
            LiveBackSpeechCreat liveBackSpeechCreat = new LiveBackSpeechCreat(questionBll);
            liveBackSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            liveBackSpeechCreat.setIsExperience(liveBackBll.getExperience());
            questionBll.setBaseSpeechCreat(liveBackSpeechCreat);
        }
        //测试卷
        LiveBackExamQuestionCreat liveBackExamQuestionCreat = new LiveBackExamQuestionCreat();
        liveBackExamQuestionCreat.setLiveGetInfo(liveGetInfo);
        int isArts = liveBackBll.getIsArts();
        liveBackExamQuestionCreat.setArts(isArts);
        liveBackExamQuestionCreat.setLivePagerBack(activity, questionBll);
        questionBll.setBaseExamQuestionCreat(liveBackExamQuestionCreat);
        //主观题结果页
        LiveBackSubjectResultCreat liveBackSubjectResultCreat = new LiveBackSubjectResultCreat();
        liveBackSubjectResultCreat.setLiveGetInfo(liveGetInfo);
        questionBll.setBaseSubjectResultCreat(liveBackSubjectResultCreat);
    }

    class ExperQuestionHttp implements QuestionHttp {

        @Override
        public void getStuGoldCount(String method) {

        }

        @Override
        public void sendRankMessage(int rankStuReconnectMessage) {

        }

        @Override
        public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack abstractBusinessDataCallBack) {

        }

        public void liveSubmitTestAnswer(final LiveBasePager liveBasePager, final VideoQuestionLiveEntity
                videoQuestionLiveEntity1,
                                         String mVSectionID, String testAnswer, final boolean isVoice, boolean isRight,
                                         final QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit) {
            DataLoadEntity loadEntity = new DataLoadEntity(mContext);
            loadEntity.setLoadingTip(R.string.loading_tip_default);
            BaseBll.postDataLoadEvent(loadEntity.beginLoading());
            String isArts = questionBll.IS_SCIENCE == false ? "1" : "0";
            getCourseHttpManager().saveTestRecords(
                    videoQuestionLiveEntity1.srcType,
                    videoQuestionLiveEntity1.id,
                    testAnswer,
                    mVideoEntity.getLiveId(),
                    mVideoEntity.getChapterId(),
                    mVideoEntity.getvLivePlayBackType(),
                    isVoice,
                    isRight,
                    isArts,
                    videoQuestionLiveEntity1.type,
                    mVideoEntity.getSubjectiveSubmitUrl(), new HttpCallBack(loadEntity) {

                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) {
                            logger.d("saveQuestionResult:onPmSuccess:responseEntity=" + responseEntity
                                    .getJsonObject());
                            VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer
                                    (responseEntity,
                                            isVoice);
                            entity.setVoice(isVoice);
                            if (!LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity1.type)) {
                                if (answerReslut != null) {
                                    answerReslut.onAnswerReslut(videoQuestionLiveEntity1, entity);
                                }
                                if (questionBll != null) {
                                    questionBll.onAnswerReslut(liveBasePager, videoQuestionLiveEntity1, entity);
                                }
                                LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(activity,
                                        LiveBackBll.ShowQuestion.class);
                                showQuestion.onHide(videoQuestionLiveEntity1);

                            } else {
                                //语文主观题答完直接隐藏答题框
                                questionBll.questionViewGone(true);
                            }
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            XESToastUtils.showToast(mContext, msg);
                            if (questionBll != null) {
                                questionBll.onAnswerFailure();
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerFailure();
                            }
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
//                        if (!responseEntity.isJsonError()) {

//                        }
                            if (questionBll != null) {
                                questionBll.onAnswerReslut(liveBasePager, videoQuestionLiveEntity1, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity1, null);
                            }
                        }
                    });
        }

        @Override
        public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack callBack) {

        }

        @Override
        public void sendSpeechEvalResult2(boolean isNewArt, String id, String stuAnswer, String isSubmit, final AbstractBusinessDataCallBack callBack) {
            String liveid = mVideoEntity.getLiveId();
            String termId = mVideoEntity.getChapterId();
            if (isNewArt) {
                getCourseWareHttpManager().sendSpeechEvalResultNewArts(liveid, id, stuAnswer, isSubmit, new HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(final ResponseEntity responseEntity) {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        callBack.onDataSucess(jsonObject);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
                    }
                });
            } else {
                String isArts = questionBll.IS_SCIENCE ? "0" : "1";
                getCourseHttpManager().sendExpSpeechEvalResult(mVideoEntity
                        .getSpeechEvalSubmitUrl(), liveid, id, termId, isArts, stuAnswer, new HttpCallBack(false) {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                        callBack.onDataSucess(jsonObject);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
                    }
                });
            }
        }


        @Override
        public void speechEval42IsAnswered(boolean isNewArt, String mVSectionID, String num, final AbstractBusinessDataCallBack callBack) {
            getCourseWareHttpManager().speechEval42IsAnswered(mVSectionID, num, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(final ResponseEntity responseEntity) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                    callBack.onDataSucess(isAnswer);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                }
            });
        }
    }


    public ExperCourseWareHttpManager getCourseWareHttpManager() {
        if (courseWareHttpManager == null) {
            courseWareHttpManager = new ExperCourseWareHttpManager(getmHttpManager());
        }
        return courseWareHttpManager;
    }

    class ExperCourse extends ExperQuestionHttp implements EnglishH5CoursewareSecHttp {

        @Override
        public void getCourseWareTests(String url, String params, AbstractBusinessDataCallBack callBack) {

        }

        @Override
        public void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack) {
            getCourseWareHttpManager().getTestInfos(LiveAppUserInfo.getInstance().getStuId(), detailInfo.id, callBack);
        }

        @Override
        public void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, int isforce, String nonce, long entranceTime, String testInfos, AbstractBusinessDataCallBack callBack) {
            getCourseWareHttpManager().submitMultiTest(detailInfo, "" + testInfos, 0, isforce, callBack);
        }

        @Override
        public void submitGroupGame(VideoQuestionLiveEntity detailInfo, int gameMode, int voiceTime, int pkTeamId, int gameGroupId, int starNum, int energy, int gold, int videoLengthTime, int micLengthTime, int acceptVideoLengthTime, int acceptMicLengthTime, String answerData, AbstractBusinessDataCallBack callBack) {

        }

        @Override
        public String getResultUrl(VideoQuestionLiveEntity detailInfo, int isforce, String nonce) {
            return null;
        }

        @Override
        public void getStuTestResult(VideoQuestionLiveEntity detailInfo, int isPlayBack, AbstractBusinessDataCallBack callBack) {

        }

        @Override
        public void liveSubmitTestH5Answer(VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, String courseware_type, String isSubmit, double voiceTime, boolean isRight, QuestionSwitch.OnAnswerReslut onAnswerReslut) {

        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_QUESTION, LocalCourseConfig.CATEGORY_EXAM, LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_QUESTION: {
                questionBll.onStopQuestion("Experience:onQuestionEnd", questionEntity.getvQuestionType(), "");
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
                questionBll.onExamStop(questionEntity.getvQuestionID());
            }
            break;
            case LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTION_CLOSE, videoQuestionLiveEntity));
                questionBll.onStopQuestion("PlayBack:onQuestionEnd3", questionEntity.getvQuestionType(), "");
            }
            break;
        }
    }

    @Override
    public void initView() {
        questionBll.initView(getLiveViewAction(), mIsLand);
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, final
    LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_QUESTION: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
                videoQuestionLiveEntity.type = questionEntity.getvQuestionType();
                videoQuestionLiveEntity.choiceType = questionEntity.getChoiceType();
                videoQuestionLiveEntity.isAllow42 = questionEntity.getIsAllow42();
                videoQuestionLiveEntity.setIsVoice(questionEntity.getIsVoice());
                videoQuestionLiveEntity.speechContent = questionEntity.getSpeechContent();
                videoQuestionLiveEntity.time = questionEntity.getEstimatedTime();
                videoQuestionLiveEntity.num = questionEntity.getQuestionNum();
                videoQuestionLiveEntity.examSubmit = questionEntity.getvEndTime() - questionEntity
                        .getvQuestionInsretTime();
                videoQuestionLiveEntity.srcType = questionEntity.getSrcType();
                videoQuestionLiveEntity.setAnswerDay(questionEntity.getAnswerDay());
                videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                videoQuestionLiveEntity.assess_ref = questionEntity.getAssess_ref();
                videoQuestionLiveEntity.setTermId(mVideoEntity.getChapterId());
                if (!questionEntity.getAnswerEntityLst().isEmpty()) {
                    for (AnswerEntity answerEntity : questionEntity.getAnswerEntityLst()) {
                        videoQuestionLiveEntity.addAnswerEntity(answerEntity);
                    }
                }
                questionBll.showQuestion(videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
                videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                questionBll.onExamStart(mVideoEntity.getLiveId(), videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE: {
                logger.i("showQuestion :" + vCategory + ":" + questionEntity.getvQuestionType() + ":" + questionEntity.getType() + ":" + questionEntity.toString());
                //LiveVideoConfig.isNewArts = true;
                questionEntity.setNewArtsCourseware(true);

                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                if (questionEntity.getvCategory() == 1001) {
                    List<String> testIds = new ArrayList<>();
                    if (testIds.size() > 0) {
                        testIds.clear();
                    }
                    String type = "";
                    String isVoices = "";
                    String assess = "";
                    String answers = "";
                    for (int i = 0; i < questionEntity.getReleaseInfos().size(); i++) {
                        testIds.add(questionEntity.getReleaseInfos().get(i).getId());
                        type = questionEntity.getReleaseInfos().get(0).getType();
                        isVoices = questionEntity.getReleaseInfos().get(0).getIsVoice();
                        assess = questionEntity.getReleaseInfos().get(0).getAssess_ref();
                        answers = questionEntity.getReleaseInfos().get(0).getAnswer();
                    }
                    if ("5".equals(type) || "6".equals(type)) {
                        videoQuestionLiveEntity.setUrl(buildRoleplayUrl(getTestIdS(testIds), type));
                        videoQuestionLiveEntity.isAllow42 = "0";
                    } else {
                        videoQuestionLiveEntity.setUrl(buildCourseUrl(getTestIdS(testIds)));
                        videoQuestionLiveEntity.isAllow42 = "1";
                    }
                    videoQuestionLiveEntity.id = getTestIdS(testIds);
                    videoQuestionLiveEntity.type = type;
                    videoQuestionLiveEntity.setArtType(type);
                    videoQuestionLiveEntity.num = 1;
                    videoQuestionLiveEntity.setIsVoice(isVoices);
                    videoQuestionLiveEntity.assess_ref = assess;
                    videoQuestionLiveEntity.speechContent = answers;
                }
                //非常重要，不然新课件平台，roleplay无法进入
                logger.i("yzl_showQuestion type = " + videoQuestionLiveEntity.type);
                if ("5".equals(videoQuestionLiveEntity.type)) {
                    logger.i("yzl_init new rolePlay bll");
                    RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, getLiveViewAction(), liveBackBll, liveGetInfo, false, getLiveHttpAction());
                    questionBll.setRolePlayMachineAction(rolePlayerBll, null);
                }
                videoQuestionLiveEntity.setNewArtsCourseware(true);
                videoQuestionLiveEntity.setExper(true);
                videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                videoQuestionLiveEntity.setLive(false);
                videoQuestionLiveEntity.setAnswerDay(questionEntity.getAnswerDay());
                videoQuestionLiveEntity.setTermId(mVideoEntity.getChapterId());
                if (questiongtype.contains(videoQuestionLiveEntity.type)) {
                    EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTIONS_SHOW, videoQuestionLiveEntity));
                    questionBll.showQuestion(videoQuestionLiveEntity);
                    showQuestion.onShow(true, videoQuestionLiveEntity);
                }
                break;
            }
            default:
                break;
        }
    }

    private String buildRoleplayUrl(String id, String type) {
        String isPlayback = "1";
        StringBuilder sb = new StringBuilder();
        String url;
        if ("5".equals(type)) {
            if (mVideoEntity.getPattern() == 2) {
                url = LiveHttpConfig.URL_NEWARTS_STANDROALPLAY_URL;
            } else {
                url = LiveHttpConfig.URL_NEWARTS_ROALPLAY_URL;
            }
        } else {
            url = LiveHttpConfig.URL_NEWARTS_CHINESEREADING_URL;
        }
        sb.append(url).append("?liveId=").append(mVideoEntity.getLiveId())
                .append("&testId=").append(id).append("&isPlayBack=").append(isPlayback)
                .append("&stuCouId=").append(mVideoEntity.getStuCoulId()).append("&stuId=").append(LiveAppUserInfo.getInstance().getStuId());
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

}
