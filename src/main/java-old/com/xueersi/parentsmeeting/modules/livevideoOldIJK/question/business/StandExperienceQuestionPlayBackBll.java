package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.json.JSONObject;

import java.util.HashMap;

public class StandExperienceQuestionPlayBackBll extends QuestionPlayBackBll {

    private String TAG = getClass().getSimpleName();

    public StandExperienceQuestionPlayBackBll(Activity activity, StandExperienceLiveBackBll liveBackBll) {
        super(activity, liveBackBll);
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
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
            //语音评测
            LiveBackStandSpeechCreat liveBackStandSpeechCreat = new LiveBackStandSpeechCreat(this, liveBackBll,
                    questionBll);
            liveBackStandSpeechCreat.setIsExperience(((StandExperienceLiveBackBll) liveBackBll).getExperience());
            liveBackStandSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackStandSpeechCreat);
        } else {
            //语音答题
            WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                    LiveQuestionSwitchImpl());
            questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
            //语音评测
            LiveBackSpeechCreat liveBackSpeechCreat = new LiveBackSpeechCreat(questionBll);
            liveBackSpeechCreat.setIsExperience(((StandExperienceLiveBackBll) liveBackBll).getExperience());
            liveBackSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
            questionBll.setBaseSpeechCreat(liveBackSpeechCreat);
        }
        //测试卷
        LiveBackExamQuestionCreat liveBackExamQuestionCreat = new LiveBackExamQuestionCreat();
        liveBackExamQuestionCreat.setLiveGetInfo(liveGetInfo);
        int isArts = liveBackBll.getIsArts();
        liveBackExamQuestionCreat.setArts(isArts);
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
                questionBll.showQuestion(videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            case LocalCourseConfig.CATEGORY_EXAM: {
                //如果是全身直播体验课
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.id = questionEntity.getvQuestionID();
                videoQuestionLiveEntity.setvQuestionInsretTime(questionEntity.getvQuestionInsretTime());
                videoQuestionLiveEntity.setvEndTime(questionEntity.getvEndTime());
                questionBll.onExamStart(mVideoEntity.getLiveId(), videoQuestionLiveEntity);
                showQuestion.onShow(true, videoQuestionLiveEntity);
            }
            break;
            default:
                break;
        }
    }


    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, final OnSpeechEval
            onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
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
//            getCourseHttpManager().sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime,
//                    httpCallBack);
//        } else {
        getCourseHttpManager().sendExpSpeechEvalResult(
                mVideoEntity.getSpeechEvalSubmitUrl(),
                liveid,
                id,
                mVideoEntity.getChapterId(),
                questionBll.IS_SCIENCE == false ? "1" : "0",
                stuAnswer,
                httpCallBack);
//        }
    }

    @Override
    public void getSpeechEval(String id, final OnSpeechEval onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
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
//        getCourseHttpManager().getSpeechEval(enstuId, liveid, id, httpCallBack);
//        } else {
        getCourseHttpManager().getExpeSpeechEval(enstuId,
                liveid, id, mVideoEntity.getSpeechEvalUrl(), httpCallBack);
//        }
    }

    /**
     * 发送语音评测
     *  @param id
     * @param stuAnswer
     * @param isSubmit
     * @param onSpeechEval
     */
    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, String isSubmit, final OnSpeechEval onSpeechEval) {
        String liveid = mVideoEntity.getLiveId();
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
//        getCourseHttpManager().sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer,
        HttpCallBack httpCallBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                onSpeechEval.onSpeechEval(jsonObject);
                Log.e(TAG, "" + jsonObject.toString());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                onSpeechEval.onPmError(responseEntity);
//            }
            }
        };
        getCourseHttpManager().sendExpSpeechEvalResult(
                mVideoEntity.getSpeechEvalSubmitUrl(),
                liveid,
                id,
                mVideoEntity.getChapterId(),
                questionBll.IS_SCIENCE == false ? "1" : "0",
                stuAnswer,
                httpCallBack);
    }

    @Override
    public void liveSubmitTestAnswer(final LiveBasePager liveBasePager, final VideoQuestionLiveEntity
            videoQuestionLiveEntity1, String mVSectionID, String testAnswer, final boolean isVoice, boolean isRight,
                                     final QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit) {
        DataLoadEntity loadEntity = new DataLoadEntity(mContext);
        loadEntity.setLoadingTip(R.string.loading_tip_default);
        BaseBll.postDataLoadEvent(loadEntity.beginLoading());
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
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
//            getCourseHttpManager().saveTestRecord(
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
//        } else {
        getCourseHttpManager().saveTestRecords(
                LiveAppUserInfo.getInstance().getEnstuId(),
                videoQuestionLiveEntity1.srcType,
                videoQuestionLiveEntity1.id,
                testAnswer,
                mVideoEntity.getLiveId(),
                mVideoEntity.getChapterId(),
                mVideoEntity.getvLivePlayBackType(),
                isVoice,
                isRight,
                questionBll.IS_SCIENCE == false ? "1" : "0",
                videoQuestionLiveEntity1.type,
                mVideoEntity.getSubjectiveSubmitUrl(),
                httpCallBack);
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
}
