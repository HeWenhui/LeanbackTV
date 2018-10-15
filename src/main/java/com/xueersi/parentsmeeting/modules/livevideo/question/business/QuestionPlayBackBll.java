package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/17.
 * 互动题回放
 */
public class QuestionPlayBackBll extends LiveBackBaseBll implements QuestionHttp {
    QuestionBll questionBll;


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
        questionBll.setLiveBll(this);
        //站立直播
        if (liveBackBll.getPattern() == 2) {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                    LiveQuestionSwitchImpl());
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll));
            //语音评测
            LiveBackStandSpeechCreat liveBackStandSpeechCreat = new LiveBackStandSpeechCreat(this, liveBackBll,
                    questionBll);
            liveBackStandSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackStandSpeechCreat);
        } else {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                    LiveQuestionSwitchImpl());
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll));
            //语音评测
            LiveBackSpeechCreat liveBackSpeechCreat = new LiveBackSpeechCreat(questionBll);
            liveBackSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackSpeechCreat);
        }
        //测试卷
        LiveBackExamQuestionCreat liveBackExamQuestionCreat = new LiveBackExamQuestionCreat();
        liveBackExamQuestionCreat.setLiveGetInfo(liveGetInfo);
        int isArts = liveBackBll.getIsArts();
        liveBackExamQuestionCreat.setIS_SCIENCE(isArts != 1);
        liveBackExamQuestionCreat.setLivePagerBack(questionBll);
        liveBackExamQuestionCreat.setExamStop(new LiveBackExamStop(activity, questionBll));
        questionBll.setBaseExamQuestionCreat(liveBackExamQuestionCreat);
        //主观题结果页
        LiveBackSubjectResultCreat liveBackSubjectResultCreat = new LiveBackSubjectResultCreat();
        liveBackSubjectResultCreat.setLiveGetInfo(liveGetInfo);
        WrapQuestionWebStop wrapQuestionWebStop = new WrapQuestionWebStop(activity);
        wrapQuestionWebStop.setStopWebQuestion(questionBll);
        liveBackSubjectResultCreat.setWrapQuestionWebStop(wrapQuestionWebStop);
        questionBll.setBaseSubjectResultCreat(liveBackSubjectResultCreat);
        QuestionWebCache webCache = new QuestionWebCache(activity);
        webCache.startCache();
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_QUESTION, LocalCourseConfig.CATEGORY_EXAM};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_QUESTION: {
                questionBll.onStopQuestion(questionEntity.getvQuestionType(), "");
//                if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(questionEntity.getvQuestionType())) {
//                    MediaPlayerControl mediaPlayerControl = getInstance(MediaPlayerControl.class);
//                    mediaPlayerControl.pause();
//                }
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
                questionBll.onExamStop(questionEntity.getvQuestionID());
            }
            break;
        }
    }

    @Override
    public void initView() {
        questionBll.initView(mRootView, mIsLand.get());
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
                int isArts = liveBackBll.getIsArts();
                if (isArts == 0 && mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                    String[] ss = videoQuestionLiveEntity.id.split("-");
                    if (ss.length > 1) {
                        if ("0".equals(ss[1])) {
                            videoQuestionLiveEntity.isTestUseH5 = true;
                        }
                    }
                }

                //todo:
                videoQuestionLiveEntity.roles = "test_role_name";
                if(!TextUtils.isEmpty(videoQuestionLiveEntity.roles) && ! "1".equals(videoQuestionLiveEntity.multiRolePlay) ){
                    logger.i("走人机start,拉取试题");
                        RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, mRootView, liveBackBll, liveGetInfo);
                    questionBll.setRolePlayMachineAction(rolePlayerBll);

                }

                questionBll.showQuestion(videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
//                if (liveBackBll.getExperience()) {//如果是全身直播体验课
//                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//                    videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
//                    videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
//                    videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
//                    questionBll.onExamStart(mVideoEntity.getLiveId(), videoQuestionLiveEntity);
//                    showQuestion.onShow(true, videoQuestionLiveEntity);
//                } else {

                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity
                        .getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (vPlayer != null) {
//                            vPlayer.start();
//                        }
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

    @Override
    public void getStuGoldCount() {
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
                                     final QuestionSwitch.OnAnswerReslut answerReslut) {
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
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
        getCourseHttpManager().saveTestRecord(
                enstuId,
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
//                    UserBll.getInstance().getMyUserInfoEntity().getEnstuId(),
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

    @Override
    public void getSpeechEval(String id, final OnSpeechEval onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        HttpCallBack httpCallBack = new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                SpeechEvalEntity speechEvalEntity = getCourseHttpResponseParser().parseSpeechEval(responseEntity);
                if (speechEvalEntity != null) {
                    onSpeechEval.onSpeechEval(speechEvalEntity);
                } else {
                    responseEntity = new ResponseEntity();
                    responseEntity.setStatus(false);
                    responseEntity.setErrorMsg("出了点意外，请稍后试试");
                    responseEntity.setJsonError(true);
                    onSpeechEval.onPmError(responseEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
            }
        };
//        if (!liveBackBll.getExperience()) {
        getCourseHttpManager().getSpeechEval(enstuId, liveid, id, httpCallBack);
//        } else {
//            getCourseHttpManager().getExpeSpeechEval(mVideoEntity.getSpeechEvalUrl(), enstuId,
//                    liveid, id, httpCallBack);
//        }
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, final OnSpeechEval
            onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        HttpCallBack httpCallBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                onSpeechEval.onSpeechEval(null);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
            }
        };
//        if (!liveBackBll.getExperience()) {
        getCourseHttpManager().sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime,
                httpCallBack);
//        } else {
//            getCourseHttpManager().sendExpSpeechEvalResult(
//                    mVideoEntity.getSpeechEvalSubmitUrl(),
//                    liveid,
//                    id,
//                    mVideoEntity.getChapterId(),
//                    questionBll.IS_SCIENCE == false ? "1" : "0",
//                    stuAnswer,
//                    httpCallBack);
//        }
    }

    /**
     * 发送语音评测
     *
     * @param id
     * @param stuAnswer
     * @param onSpeechEval
     */
    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, final OnSpeechEval onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
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
                onSpeechEval.onSpeechEval(jsonObject);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
            }
        };

//        if (!liveBackBll.getExperience()) {
//            getCourseHttpManager().sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime,
//                    httpCallBack);
        getCourseHttpManager().sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer, httpCallBack);
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
    public void speechEval42IsAnswered(String mVSectionID, String num, final SpeechEvalAction.SpeechIsAnswered
            isAnswered) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getCourseHttpManager().speechEval42IsAnswered(enstuId, mVSectionID, num, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                isAnswered.isAnswer(isAnswer);
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