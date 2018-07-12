package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.StarInteractAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by lyqai on 2018/7/5.
 */

public class QuestionIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, QuestionHttp {
    private QuestionBll mQuestionAction;
    private AnswerRankIRCBll mAnswerRankBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;

    public QuestionIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
        mQuestionAction = new QuestionBll(context, liveBll.getStuCouId());
        putInstance(QuestionIRCBll.class, this);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mQuestionAction.setLiveBll(this);
        mQuestionAction.setLiveAndBackDebug(mLiveBll);
        mQuestionAction.initView(mRootView, true);
        mAnswerRankBll = getInstance(AnswerRankIRCBll.class);
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
        mLogtf.d("onCreate:mAnswerRankBll=" + mAnswerRankBll + "," + mLiveAutoNoticeBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo data) {
        super.onLiveInited(data);
        if (data.getPattern() == 2) {

        } else {
            mQuestionAction.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mQuestionAction.new LiveQuestionSwitchImpl()));
            mQuestionAction.setBaseSpeechCreat(new LiveSpeechCreat());
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
            if ("on".equals(mainRoomstatus.getExamStatus())) {
                String num = mainRoomstatus.getExamNum();
                mQuestionAction.onExamStart(mLiveId, num, "");
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.setTestId(num);
                }
            } else {
                mQuestionAction.onExamStop();
            }
        }
        if (liveTopic.getVideoQuestionLiveEntity() != null) {
            if (mQuestionAction != null) {
                mQuestionAction.showQuestion(liveTopic.getVideoQuestionLiveEntity());
                if (mAnswerRankBll != null) {
                    mAnswerRankBll.setTestId(liveTopic.getVideoQuestionLiveEntity().getvQuestionID());
                }
                if (mLiveAutoNoticeBll != null) {
                    mLiveAutoNoticeBll.setTestId(liveTopic.getVideoQuestionLiveEntity().getvQuestionID());
                    mLiveAutoNoticeBll.setSrcType(liveTopic.getVideoQuestionLiveEntity().srcType);
                }
            }
        } else {
            if (mQuestionAction != null) {
                mQuestionAction.showQuestion(null);
            }
        }
    }

    @Override
    public void onNotice(JSONObject object, int type) {
        switch (type) {
            case XESCODE.SENDQUESTION:
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.type = object.optString("ptype");
                videoQuestionLiveEntity.id = object.optString("id");
                videoQuestionLiveEntity.time = object.optDouble("time");
                videoQuestionLiveEntity.num = object.optInt("num");
                videoQuestionLiveEntity.gold = object.optDouble("gold");
                videoQuestionLiveEntity.srcType = object.optString("srcType");
                videoQuestionLiveEntity.choiceType = object.optString("choiceType", "1");
                videoQuestionLiveEntity.isTestUseH5 = object.optInt("isTestUseH5", -1) == 1;
                videoQuestionLiveEntity.nonce = object.optString("nonce", "");
                videoQuestionLiveEntity.isAllow42 = object.optString("isAllow42", "");
                videoQuestionLiveEntity.speechContent = object.optString("answer", "");
                videoQuestionLiveEntity.multiRolePlay = object.optString("multiRolePlay", "0");
                videoQuestionLiveEntity.roles = object.optString("roles", "");
//                        if (BuildConfig.DEBUG) {onget
//                            videoQuestionLiveEntity.isTestUseH5 = true;
//                        }
                String isVoice = object.optString("isVoice");
                videoQuestionLiveEntity.setIsVoice(isVoice);
                if ("1".equals(isVoice)) {
                    videoQuestionLiveEntity.questiontype = object.optString("questiontype");
                    videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                }
                if (mQuestionAction != null) {
//                            mGetInfo.getLiveTopic().setTopic(getTopicFromQuestion(videoQuestionLiveEntity));
                    mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(videoQuestionLiveEntity);
                    mQuestionAction.showQuestion(videoQuestionLiveEntity);
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                    }
                    if (mLiveAutoNoticeBll != null) {
                        mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.srcType);
                    }

                    if (mQuestionAction instanceof QuestionBll) {
                        ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(false);
                        Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: " +
                                "SENDQUESTION");
                    }
                }
                break;
            case XESCODE.STOPQUESTION:
                mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(null);
                if (mQuestionAction != null) {
                    try {
                        mQuestionAction.onStopQuestion(object.getString("ptype"), object.optString("ptype"));
                        if (mQuestionAction instanceof QuestionBll) {
                            ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                            Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: " +
                                    "STOPQUESTION");
                        }
                    } catch (Exception e) {

                    }
                }
                break;
            case XESCODE.EXAM_START:
                if (mQuestionAction != null) {
                    String num = object.optString("num", "0");
                    String nonce = object.optString("nonce");
                    mQuestionAction.onExamStart(mLiveId, num, nonce);
                    if (mQuestionAction instanceof QuestionBll) {
                        ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(false);
                        Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: EXAM_START");
                    }
                }
                break;
            case XESCODE.EXAM_STOP: {
                if (mQuestionAction != null) {
                    mQuestionAction.onExamStop();
                    if (mQuestionAction instanceof QuestionBll) {
                        ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                        Loger.e("webViewCloseByTeacher", "======>LiveBll setWebViewCloseByTeacher: EXAM_STOP");
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.SENDQUESTION, XESCODE.STOPQUESTION, XESCODE.EXAM_START, XESCODE.EXAM_STOP};
    }

    @Override
    public void getStuGoldCount() {
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                String liveid = mGetInfo.getId();
                String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
                getHttpManager().getStuGoldCount(enstuId, liveid, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        Loger.i(TAG, "getStuGoldCount:onPmSuccess=" + responseEntity.getJsonObject());
                        StarInteractAction starAction = ProxUtil.getProxUtil().get(mContext, StarInteractAction.class);
                        if (starAction != null) {
                            StarAndGoldEntity starAndGoldEntity = getHttpResponseParser().parseStuGoldCount(responseEntity);
                            mGetInfo.setGoldCount(starAndGoldEntity.getGoldCount());
                            mGetInfo.setStarCount(starAndGoldEntity.getStarCount());
                            starAction.onGetStar(starAndGoldEntity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        Loger.i(TAG, "getStuGoldCount:onPmFailure=" + msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        Loger.i(TAG, "getStuGoldCount:onPmError=" + responseEntity.getErrorMsg());
                    }
                });
            }
        }, 500);
    }

    @Override
    public void understand(boolean understand, String nonce) {
        if (mLiveBll.getMainTeacherStr() != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.UNDERSTANDS);
                jsonObject.put("understand", understand);
                jsonObject.put("nonce", nonce);
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
                mLogtf.d("understand ok");
            } catch (Exception e) {
                // Loger.e(TAG, "understand", e);
                mLogtf.e("understand", e);
            }
        } else {
            mLogtf.d("understand mMainTeacherStr=null");
        }
    }

    @Override
    public void sendRankMessage(int rankStuReconnectMessage) {
        if (mLiveBll.getLiveTopic().isDisable()) {
            return;
        }
        if (mLiveBll.getMainTeacherStr() != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", rankStuReconnectMessage + "");
                jsonObject.put("classId", mGetInfo.getStudentLiveInfo().getClassId());
                jsonObject.put("teamId", mGetInfo.getStudentLiveInfo().getTeamId());
                mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getQuestion(VideoQuestionLiveEntity videoQuestionLiveEntity, final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().getQuestion(enstuId, mGetInfo.getId(), videoQuestionLiveEntity.getvQuestionID(), new
                HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        Loger.d(TAG, "getQuestion:onPmSuccess" + responseEntity.getJsonObject());
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Loger.e(TAG, "getQuestion:onFailure", e);
                        super.onFailure(call, e);
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        Loger.d(TAG, "getQuestion:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                        callBack.onDataSucess();
                    }
                });
    }

    @Override
    public void liveSubmitTestAnswer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, final boolean isVoice, boolean isRight, final QuestionSwitch.OnAnswerReslut answerReslut) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("liveSubmitTestAnswer:enstuId=" + enstuId + "," + videoQuestionLiveEntity.srcType + ",testId=" +
                videoQuestionLiveEntity.id + ",liveId=" + mVSectionID + ",testAnswer="
                + testAnswer);
        String userMode = "1";
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (mGetInfo.getStudentLiveInfo().isExpe()) {
                userMode = "0";
            }
        }
        getHttpManager().liveSubmitTestAnswer(mLiveType, enstuId, videoQuestionLiveEntity.srcType,
                videoQuestionLiveEntity.id, mLiveId, testAnswer, userMode, isVoice, isRight, new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        mLogtf.d("liveSubmitTestAnswer:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                                videoQuestionLiveEntity);
                        VideoResultEntity entity = getHttpResponseParser().parseQuestionAnswer(responseEntity, isVoice);
                        entity.setVoice(isVoice);
                        if (StringUtils.isSpace(entity.getTestId())) {
                            entity.setTestId(videoQuestionLiveEntity.id);
                        }
                        if (answerReslut != null) {
                            answerReslut.onAnswerReslut(videoQuestionLiveEntity, entity);
                        }
                        if (mQuestionAction != null) {
                            mQuestionAction.onAnswerReslut(videoQuestionLiveEntity, entity);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        mLogtf.d("liveSubmitTestAnswer:onPmFailure=" + msg + ",testId=" + videoQuestionLiveEntity.id);
                        if (mQuestionAction != null) {
                            mQuestionAction.onAnswerFailure();
                        }
                        if (answerReslut != null) {
                            answerReslut.onAnswerFailure();
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        mLogtf.d("liveSubmitTestAnswer:onPmError=" + responseEntity.getErrorMsg() + ",testId=" +
                                videoQuestionLiveEntity.id);
                        if (!responseEntity.isJsonError()) {
                            if (mQuestionAction != null) {
                                mQuestionAction.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                        }
                    }
                });
    }

    @Override
    public void getSpeechEval(String id, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().getSpeechEval(enstuId, liveid, id, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                SpeechEvalEntity speechEvalEntity = getHttpResponseParser().parseSpeechEval(responseEntity);
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
        });
    }

    @Override
    public void sendSpeechEvalResult(String id, String stuAnswer, String times, int entranceTime, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().sendSpeechEvalResult(enstuId, liveid, id, stuAnswer, times, entranceTime, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult:onPmSuccess=" + responseEntity.getJsonObject());
                onSpeechEval.onSpeechEval(null);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("sendSpeechEvalResult:onPmFailure=" + msg);
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void sendSpeechEvalResult2(String id, String stuAnswer, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().sendSpeechEvalResult2(enstuId, liveid, id, stuAnswer, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult2:onPmSuccess=" + responseEntity.getJsonObject());
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
                mLogtf.i("sendSpeechEvalResult2:onPmFailure=" + msg);
                onSpeechEval.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("sendSpeechEvalResult2:onPmError=" + responseEntity.getErrorMsg());
                onSpeechEval.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, final AbstractBusinessDataCallBack callBack) {
        getHttpManager().getTestAnswerTeamStatus(videoQuestionLiveEntity.id, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                GoldTeamStatus entity = getHttpResponseParser().testAnswerTeamStatus(responseEntity, mGetInfo.getStuId(),
                        mGetInfo.getHeadImgPath());
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
//                if (AppConfig.DEBUG) {
//                    GoldTeamStatus entity = new GoldTeamStatus();
//                    for (int i = 0; i < 3; i++) {
//                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                        student.setNickname("测试" + i);
//                        student.setGold("90");
//                        student.setAvatar_path(mGetInfo.getHeadImgPath());
//                        student.setRight(i % 2 == 0);
//                        entity.getStudents().add(student);
//                    }
//                    callBack.onDataSucess(entity);
//                } else {
//                    callBack.onDataFail(1, responseEntity.getErrorMsg());
//                }
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void speechEval42IsAnswered(String mVSectionID, String num, final SpeechEvalAction.SpeechIsAnswered isAnswered) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().speechEval42IsAnswered(enstuId, mVSectionID, num, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                mLogtf.i("speechEval42IsAnswered:onPmSuccess=" + jsonObject);
                boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                isAnswered.isAnswer(isAnswer);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("speechEval42IsAnswered:onPmFailure=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("speechEval42IsAnswered:onPmError=" + responseEntity.getErrorMsg());
            }
        });
    }

}
