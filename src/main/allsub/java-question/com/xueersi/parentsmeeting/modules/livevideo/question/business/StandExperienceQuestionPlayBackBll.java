package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.util.Log;
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
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
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
        liveBackExamQuestionCreat.setLivePagerBack(activity, questionBll);
        questionBll.setBaseExamQuestionCreat(liveBackExamQuestionCreat);
        //主观题结果页
        LiveBackSubjectResultCreat liveBackSubjectResultCreat = new LiveBackSubjectResultCreat();
        liveBackSubjectResultCreat.setLiveGetInfo(liveGetInfo);
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

    /**
     * 发送语音评测
     *
     * @param id
     * @param stuAnswer
     * @param isSubmit
     * @param callBack
     */
    @Override
    public void sendSpeechEvalResult2(boolean isNewArt,String id, String stuAnswer, String isSubmit, final AbstractBusinessDataCallBack callBack) {
        String liveid = mVideoEntity.getLiveId();
//        getCourseHttpManager().sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer,
        HttpCallBack httpCallBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                callBack.onDataSucess(jsonObject);
                Log.e(TAG, "" + jsonObject.toString());
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
