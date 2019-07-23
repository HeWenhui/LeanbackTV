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
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by：WangDe on 2018/8/28 11:44
 * 体验课互动题
 */
public class QuestionExperienceBll extends LiveBackBaseBll implements QuestionHttp {

    QuestionBll questionBll;

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
        questionBll.setQuestionHttp(this);
        //语音答题
        WrapQuestionSwitch wrapQuestionSwitch = new WrapQuestionSwitch(activity, questionBll.new
                LiveQuestionSwitchImpl());
        questionBll.setBaseVoiceAnswerCreat(new LiveBackVoiceAnswerCreat(wrapQuestionSwitch, questionBll, liveGetInfo));
        //语音评测
        LiveBackSpeechCreat liveBackSpeechCreat = new LiveBackSpeechCreat(questionBll);
        liveBackSpeechCreat.setSpeechEvalAction(new WrapSpeechEvalAction(activity));
        liveBackSpeechCreat.setIsExperience(liveBackBll.getExperience());
        questionBll.setBaseSpeechCreat(liveBackSpeechCreat);
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
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_QUESTION, LocalCourseConfig.CATEGORY_EXAM};
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
            default:
                break;
        }
    }


    @Override
    public void getStuGoldCount(String method) {

    }

    @Override
    public void sendRankMessage(int rankStuReconnectMessage) {

    }

    @Override
    public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity1, AbstractBusinessDataCallBack
            abstractBusinessDataCallBack) {

    }

    @Override
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
    public void sendSpeechEvalResult2(boolean isNewArt, String id, String stuAnswer, String isSubmit, final AbstractBusinessDataCallBack callBack) {
        String liveid = mVideoEntity.getLiveId();
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        String termId = mVideoEntity.getChapterId();
        String isArts = questionBll.IS_SCIENCE == false ? "1" : "0";
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

    @Override
    public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity1,
                                        AbstractBusinessDataCallBack callBack) {

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
}
