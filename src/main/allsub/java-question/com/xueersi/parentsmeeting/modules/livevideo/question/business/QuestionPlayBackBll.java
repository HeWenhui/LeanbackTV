package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.create.LiveBackBigQueCreate;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.CourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTIONS_SHOW;
import static com.xueersi.parentsmeeting.modules.livevideo.event.LiveBackQuestionEvent.QUSTION_CLOSE;

/**
 * Created by linyuqiang on 2018/7/17.
 * 互动题回放
 */
public class QuestionPlayBackBll extends LiveBackBaseBll implements QuestionHttp, QuestionSecHttp, EnglishH5CoursewareSecHttp {
    QuestionBll questionBll;
    String[] ptTypeFilters = {"4", "0", "1", "2", "8", "5", "6"};
    private List<String> questiongtype = Arrays.asList(ptTypeFilters);
    private CourseWareHttpManager courseWareHttpManager;

    public QuestionPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
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
        questionBll.setQuestionHttp(this);
        //站立直播
        if (liveBackBll.getPattern() == 2) {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                    LiveQuestionSwitchImpl());
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
            //语音评测
            LiveBackStandSpeechCreat liveBackStandSpeechCreat = new LiveBackStandSpeechCreat(liveBackBll,
                    questionBll);
            liveBackStandSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackStandSpeechCreat);
        } else {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                    LiveQuestionSwitchImpl());
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
            //语音评测
            LiveBackSpeechCreat liveBackSpeechCreat = new LiveBackSpeechCreat(questionBll);
            liveBackSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
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
        if (isArts == LiveVideoSAConfig.ART_SEC) {
            QuestionWebCache webCache = new QuestionWebCache(activity);
            webCache.startCache();
            questionBll.setBigQueCreate(new LiveBackBigQueCreate(activity, this));
        }
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_QUESTION, LocalCourseConfig.CATEGORY_EXAM, LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE, LocalCourseConfig.CATEGORY_BIG_TEST};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        logger.i("onQuestionEnd:vCategory=" + vCategory);
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_QUESTION: {
                questionBll.onStopQuestion("PlayBack:onQuestionEnd1", questionEntity.getvQuestionType(), "");
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
//            case LocalCourseConfig.CATEGORY_BIG_TEST: {
//                try {
//                    final VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//                    String url = questionEntity.getUrl();
//                    JSONObject jsonObject = new JSONObject(url);
//                    videoQuestionLiveEntity.id = jsonObject.getString("testId");
//                    videoQuestionLiveEntity.setDotId(jsonObject.getString("dotId"));
//                    videoQuestionLiveEntity.setSrcType(jsonObject.getString("srcType"));
//                    videoQuestionLiveEntity.setDotType(jsonObject.getInt("choiceType"));
//                    videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
//                    questionBll.showBigQuestion(videoQuestionLiveEntity, false);
//                } catch (Exception e) {
//                    LiveCrashReport.postCatchedException(e);
//                }
//                break;
//            }
            default:
                break;
        }
    }

    @Override
    public void initView() {
        questionBll.initView(getLiveViewAction(), mIsLand.get());
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, final
    LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        logger.i("showQuestion:vCategory=" + vCategory);
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_QUESTION: {
                // LiveVideoConfig.isNewArts = false;
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
                videoQuestionLiveEntity.type = questionEntity.getvQuestionType();
                videoQuestionLiveEntity.choiceType = questionEntity.getChoiceType();
                videoQuestionLiveEntity.isAllow42 = questionEntity.getIsAllow42();
                videoQuestionLiveEntity.setIsVoice(questionEntity.getIsVoice());
                videoQuestionLiveEntity.speechContent = questionEntity.getSpeechContent();
                videoQuestionLiveEntity.time = questionEntity.getEstimatedTime();
                videoQuestionLiveEntity.num = questionEntity.getQuestionNum();
                //讲座的答题数量。
                if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                    try {
                        List<AnswerEntity> answerEntityLst = questionEntity.getAnswerEntityLst();
                        for (int i = 0; i < answerEntityLst.size(); i++) {
                            videoQuestionLiveEntity.addAnswerEntity(answerEntityLst.get(i));
                        }
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(TAG, e);
                    }
                }
                videoQuestionLiveEntity.examSubmit = questionEntity.getvEndTime() - questionEntity
                        .getvQuestionInsretTime();
                videoQuestionLiveEntity.srcType = questionEntity.getSrcType();
                videoQuestionLiveEntity.setAnswerDay(questionEntity.getAnswerDay());
                videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                videoQuestionLiveEntity.assess_ref = questionEntity.getAssess_ref();
                videoQuestionLiveEntity.roles = questionEntity.getRoles();
//                int isArts = liveBackBll.getIsArts();
//                if (isArts == 0 && mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
//                    String[] ss = videoQuestionLiveEntity.id.split("-");
//                    if (ss.length > 1) {
//                        if ("0".equals(ss[1])) {
//                            videoQuestionLiveEntity.isTestUseH5 = true;
//                        }
//                    }
//                }

                if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles) && !"1".equals(videoQuestionLiveEntity.multiRolePlay)) {
                    logger.i("走人机start,拉取试题");
                    RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, getLiveViewAction(), liveBackBll, liveGetInfo, false);
                    questionBll.setRolePlayMachineAction(rolePlayerBll, null);

                }

                questionBll.showQuestion(videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
                //  LiveVideoConfig.isNewArts = false;
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
                        videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                        videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                        questionBll.onExamStart(mVideoEntity.getLiveId(), videoQuestionLiveEntity);
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
            case LocalCourseConfig.CATEGORY_QUESTIONBLL_NEWARTSWARE: {
                logger.i("showQuestion :" + vCategory + ":" + questionEntity.getvQuestionType() + ":" + questionEntity.getType() + ":" + questionEntity.toString());
                //LiveVideoConfig.isNewArts = true;
                questionEntity.setNewArtsCourseware(true);
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
                BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                if (!liveBackBll.getExperience() && mediaPlayerControl != null) {//体验课不能暂停
                    mediaPlayerControl.pause();
                }
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.start();
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
                            RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, getLiveViewAction(), liveBackBll, liveGetInfo, false);
                            questionBll.setRolePlayMachineAction(rolePlayerBll, null);
                        }
                        videoQuestionLiveEntity.setNewArtsCourseware(true);
                        videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                        videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                        videoQuestionLiveEntity.setLive(false);
                        if (questiongtype.contains(videoQuestionLiveEntity.type)) {
                            EventBus.getDefault().post(new LiveBackQuestionEvent(QUSTIONS_SHOW, videoQuestionLiveEntity));
                            questionBll.showQuestion(videoQuestionLiveEntity);
                            showQuestion.onShow(true, videoQuestionLiveEntity);
                        }
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
            case LocalCourseConfig.CATEGORY_BIG_TEST: {
                try {
                    final VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    String url = questionEntity.getUrl();
                    JSONObject jsonObject = new JSONObject(url);
                    videoQuestionLiveEntity.id = jsonObject.getString("testId");
                    videoQuestionLiveEntity.setDotId(jsonObject.getString("dotId"));
                    videoQuestionLiveEntity.setSrcType(jsonObject.getString("srcType"));
                    videoQuestionLiveEntity.setDotType(jsonObject.getInt("choiceType"));
                    if (videoQuestionLiveEntity.getDotType() == LiveQueConfig.DOTTYPE_FILL) {
                        videoQuestionLiveEntity.num = jsonObject.getInt("item_num");
                    } else {
                        videoQuestionLiveEntity.num = LiveQueConfig.DOTTYPE_SELE_NUM;
                    }
                    videoQuestionLiveEntity.setLive(false);
                    questionBll.showBigQuestion(videoQuestionLiveEntity, true);
                    videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                    long dealy = questionEntity.getvEndTime() - questionEntity.getvQuestionInsretTime();
                    if (dealy < 0) {
                        dealy = 180;
                    }
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            questionBll.showBigQuestion(videoQuestionLiveEntity, false);
                        }
                    }, dealy * 1000);
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void getStuGoldCount(String method) {
//回放没有
    }

    @Override
    public void sendRankMessage(int rankStuReconnectMessage) {
//回放没有
    }

    @Override
    public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack
            abstractBusinessDataCallBack) {
//回放没有
    }

    @Override
    public void liveSubmitTestAnswer(final LiveBasePager liveBasePager, final VideoQuestionLiveEntity
            videoQuestionLiveEntity1, String mVSectionID, String testAnswer, final boolean isVoice, boolean isRight,
                                     final QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit) {
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        HttpCallBack httpCallBack = new HttpCallBack(loadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("saveQuestionResult:onPmSuccess:responseEntity=" + responseEntity
                        .getJsonObject());
                VideoResultEntity entity = getCourseHttpResponseParser().parseQuestionAnswer(responseEntity,
                        isVoice);
                entity.setVoice(isVoice);
                if (answerReslut != null) {
                    answerReslut.onAnswerReslut(videoQuestionLiveEntity1, entity);
                }
                if (questionBll != null) {
                    questionBll.onAnswerReslut(liveBasePager, videoQuestionLiveEntity1, entity);
                }
                if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(videoQuestionLiveEntity1.type)) {
                    if (liveBackBll.getvPlayer() != null) {
                        liveBackBll.getvPlayer().pause();
                    }
                } else {
                    LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(activity, LiveBackBll
                            .ShowQuestion.class);
                    showQuestion.onHide(videoQuestionLiveEntity1);
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
        };
//        if (!videoQuestionLiveEntity1.type.equals("8")) {
        getCourseHttpManager().saveTestRecord(videoQuestionLiveEntity1.isNewArtsH5Courseware(),
                videoQuestionLiveEntity1.srcType,
                videoQuestionLiveEntity1.id,
                testAnswer,
                videoQuestionLiveEntity1.getAnswerDay(),
                mVSectionID,
                mVideoEntity.getvLivePlayBackType(),
                isVoice,
                isRight,
                httpCallBack);
//        } else {
//            getCourseHttpManager().saveTestRecords(
//                    LiveAppUserInfo.getInstance().getEnstuId(),
//                    videoQuestionLiveEntity1.srcType,
//                    videoQuestionLiveEntity1.id,
//                    testAnswer,
//                    mVideoEntity.getLiveId(),
//                    mVideoEntity.getChapterId(),
//                    mVideoEntity.getvLivePlayBackType(),
//                    isVoice,
//                    isRight,
//                    questionBll.IS_SCIENCE == false ? "1" : "0",
//                    videoQuestionLiveEntity1.type,
//                    mVideoEntity.getSubjectiveSubmitUrl(),
//                    httpCallBack);
//            getCourseHttpManager().saveExperienceTestRecord(
//                    mVideoEntity.getSubjectiveSubmitUrl(),
//                    enstuId,
//                    videoQuestionLiveEntity1.srcType,
//                    videoQuestionLiveEntity1.id,
//                    testAnswer,
//                    videoQuestionLiveEntity1.getAnswerDay(),
//                    mVSectionID,
//                    mVideoEntity.getvLivePlayBackType(),
//                    isVoice,
//                    isRight,
//                    httpCallBack);
//        }
    }

    /**
     * 发送语音评测
     *
     * @param id
     * @param stuAnswer
     * @param isSubmit
     * @param callBack
     */
    @Override
    public void sendSpeechEvalResult2(boolean isNewArt, String id, String stuAnswer, String isSubmit, final AbstractBusinessDataCallBack callBack) {
        String liveid = mVideoEntity.getLiveId();
        HttpCallBack httpCallBack = new HttpCallBack(false) {

            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
//                Handler handler=new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                        onSpeechEval.onSpeechEval(jsonObject);
//                    }
//                },2000);
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
        };

//        if (!liveBackBll.getExperience()) {
//            getCourseHttpManager().sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime,
//                    httpCallBack);
        getCourseHttpManager().sendSpeechEvalResult2(isNewArt, liveid, id, stuAnswer, httpCallBack, isSubmit);
//    } else

//    {
//        getCourseHttpManager().sendExpSpeechEvalResult(
//                mVideoEntity.getSpeechEvalSubmitUrl(),
//                liveid,
//                id,
//                mVideoEntity.getChapterId(),
//                questionBll.IS_SCIENCE == false ? "1" : "0",
//                stuAnswer,
//                httpCallBack);
//    }

    }

    @Override
    public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, final
    AbstractBusinessDataCallBack callBack) {
        //回放没有
    }

    @Override
    public void liveSubmitTestH5Answer(VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, String courseware_type, String isSubmit, double voiceTime, boolean isRight, QuestionSwitch.OnAnswerReslut onAnswerReslut) {

    }

    @Override
    public void speechEval42IsAnswered(boolean isNewArt, String mVSectionID, String num, final AbstractBusinessDataCallBack callBack) {
        getCourseHttpManager().speechEval42IsAnswered(mVSectionID, num, new HttpCallBack(false) {
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
                .append("&stuCouId=").append(mVideoEntity.getStuCoulId()).append("&stuId=").append(LiveAppUserInfo.getInstance().getStuId())
                .append("&xesrfh=").append(LiveAppUserInfo.getInstance().getUserRfh())
                .append("&cookie=").append(LiveAppUserInfo.getInstance().getUserToken());
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
                .append("&xesrfh=").append(LiveAppUserInfo.getInstance().getUserRfh())
                .append("&cookie=").append(LiveAppUserInfo.getInstance().getUserToken())
                .append("&stuClientPath=").append(falseStr)
                .append("&fontDir=").append(falseStr);
        return sb.toString();
    }

    public CourseWareHttpManager getCourseWareHttpManager() {
        if (courseWareHttpManager == null) {
            courseWareHttpManager = new CourseWareHttpManager(getmHttpManager());
        }
        return courseWareHttpManager;
    }

    @Override
    public void submitBigTestInteraction(VideoQuestionLiveEntity videoQuestionLiveEntity, JSONArray userAnswer, long startTime, int isForce, AbstractBusinessDataCallBack callBack) {
        getCourseWareHttpManager().submitBigTestInteraction(LiveAppUserInfo.getInstance().getStuId(), videoQuestionLiveEntity.id, videoQuestionLiveEntity.getDotId(), userAnswer, startTime, isForce, 1, videoQuestionLiveEntity.getSrcType(), callBack);
    }

    @Override
    public void getStuInteractionResult(VideoQuestionLiveEntity videoQuestionLiveEntity, AbstractBusinessDataCallBack callBack) {
        getCourseWareHttpManager().getStuInteractionResult(LiveAppUserInfo.getInstance().getStuId(), videoQuestionLiveEntity.id, videoQuestionLiveEntity.getSrcType(), videoQuestionLiveEntity.getDotId(), 1, callBack);
    }

    @Override
    public void getCourseWareTests(String url, String params, AbstractBusinessDataCallBack callBack) {

    }

    @Override
    public void getCourseWareTests(VideoQuestionLiveEntity detailInfo, AbstractBusinessDataCallBack callBack) {
        getCourseWareHttpManager().getTestInfos(detailInfo.id, callBack);
    }

    @Override
    public void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, int isforce, String nonce, long entranceTime, String testInfos, AbstractBusinessDataCallBack callBack) {
        getCourseWareHttpManager().submitMultiTest("" + testInfos, 2, isforce, callBack);
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
}
