package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.Constants;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.KeyboardShowingReg;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * Created by lyqai on 2018/7/5.
 */

public class QuestionIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, QuestionHttp {
    private QuestionBll mQuestionAction;
    private AnswerRankIRCBll mAnswerRankBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;
    private SpeechEvaluatorUtils mIse;
    /** RolePlayer功能接口 */
    private RolePlayAction rolePlayAction;

    public QuestionIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mQuestionAction = new QuestionBll(context, liveBll.getStuCouId());
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mQuestionAction.setLiveBll(this);
        mQuestionAction.setVSectionID(mLiveId);
        mQuestionAction.setShareDataManager(mShareDataManager);
        mAnswerRankBll = getInstance(AnswerRankIRCBll.class);
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
        mLogtf.d("onCreate:mAnswerRankBll=" + mAnswerRankBll + "," + mLiveAutoNoticeBll);
        KeyboardShowingReg keyboardShowingReg = getInstance(KeyboardShowingReg.class);
        if (keyboardShowingReg != null) {
            keyboardShowingReg.addKeyboardShowing(mQuestionAction);
        }
        mQuestionAction.setLiveType(mLiveType);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean isLand) {
        mQuestionAction.initView(bottomContent, isLand.get());
    }

    @Override
    public void onLiveInited(LiveGetInfo data) {
        super.onLiveInited(data);
        mQuestionAction.setLiveGetInfo(data);
        LiveExamQuestionCreat liveExamQuestionCreat = new LiveExamQuestionCreat();
        int isArts = (int) mLiveBll.getBusinessShareParam("isArts");
        liveExamQuestionCreat.setIS_SCIENCE(isArts != 1);
        mQuestionAction.setLiveVideoSAConfig(mLiveBll.getLiveVideoSAConfig());
        liveExamQuestionCreat.setLiveGetInfo(data);
        liveExamQuestionCreat.setQuestionBll(mQuestionAction);
        if (isArts != 1) {
            if (mAnswerRankBll != null) {
                liveExamQuestionCreat.setmAnswerRankBll(mAnswerRankBll.getAnswerRankBll());
            }
        }
        liveExamQuestionCreat.setQuestionHttp(this);
        mQuestionAction.setBaseExamQuestionCreat(liveExamQuestionCreat);
        LiveSubjectResultCreat baseSubjectResultCreat = new LiveSubjectResultCreat();
        baseSubjectResultCreat.setLiveGetInfo(data);
        mQuestionAction.setBaseSubjectResultCreat(baseSubjectResultCreat);
        if (data.getPattern() == 2) {
            mQuestionAction.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mQuestionAction.new LiveQuestionSwitchImpl(), mQuestionAction));
            mQuestionAction.setBaseSpeechCreat(new LiveStandSpeechCreat(this, mLiveBll, mQuestionAction));
            StandSpeechTop3Bll standSpeechTop3Bll = new StandSpeechTop3Bll(this, mLiveBll);
            standSpeechTop3Bll.initView(mRootView);
            mQuestionAction.setSpeechEndAction(standSpeechTop3Bll);
        } else {
            mQuestionAction.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mQuestionAction.new LiveQuestionSwitchImpl(), mQuestionAction));
            mQuestionAction.setBaseSpeechCreat(new LiveSpeechCreat(mQuestionAction));
        }
        if (1 == data.getIsEnglish()) {
            mIse = new SpeechEvaluatorUtils(true);
            //记录当前正在走的模型，留给界面更新使用
            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                    RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
        } else {
            if (data.getIsArts() == 1) {
                String[] subjectIds = data.getSubjectIds();
                if (subjectIds != null) {
                    for (int i = 0; i < subjectIds.length; i++) {
                        String subjectId = subjectIds[i];
                        if (LiveVideoConfig.SubjectIds.SUBJECT_ID_CH.equals(subjectId)) {
                            mIse = new SpeechEvaluatorUtils(true, Constants.ASSESS_PARAM_LANGUAGE_CH);
                            //记录当前正在走的模型，留给界面更新使用
                            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                                    RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
                            break;
                        }
                    }
                }
            }
        }
        mQuestionAction.setIse(mIse);
        EnglishH5CoursewareIRCBll englishH5CoursewareIRCBll = getInstance(EnglishH5CoursewareIRCBll.class);
        if (englishH5CoursewareIRCBll != null) {
            englishH5CoursewareIRCBll.setIse(mIse);
        }
        mQuestionAction.initData();
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
        if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
            if ("on".equals(mainRoomstatus.getExamStatus())) {
                String num = mainRoomstatus.getExamNum();
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.id = num;
                mQuestionAction.onExamStart(mLiveId, videoQuestionLiveEntity);
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
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        switch (type) {
            case XESCODE.SENDQUESTION: {
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
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    videoQuestionLiveEntity.id = num;
                    videoQuestionLiveEntity.nonce = nonce;
                    mQuestionAction.onExamStart(mLiveId, videoQuestionLiveEntity);
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
            case XESCODE.XCR_ROOM_ROLE_READ: {
                if (rolePlayAction == null) {
                    RolePlayerBll rolePlayerBll = new RolePlayerBll(activity, mRootView, mLiveBll, mGetInfo);
                    mQuestionAction.setRolePlayAction(rolePlayerBll);
                    rolePlayAction = rolePlayerBll;
                }
                String nonce = object.optString("nonce");
                rolePlayAction.teacherRead(mLiveId, mLiveBll.getStuCouId(), nonce);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.SENDQUESTION, XESCODE.STOPQUESTION, XESCODE.EXAM_START, XESCODE.EXAM_STOP, XESCODE.XCR_ROOM_ROLE_READ};
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        mQuestionAction.setVideoLayout(liveVideoPoint);
    }

    @Override
    public void getStuGoldCount() {
        UpdateAchievement updateAchievement = getInstance(UpdateAchievement.class);
        if (updateAchievement != null) {
            updateAchievement.getStuGoldCount();
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

    private int test1 = 0;

    public void getSpeechEvalAnswerTeamStatus(String testId, final AbstractBusinessDataCallBack callBack) {
        getHttpManager().getSpeechEvalAnswerTeamStatus(testId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                GoldTeamStatus entity = getHttpResponseParser().getSpeechEvalAnswerTeamStatus(responseEntity, mGetInfo
                        .getStuId());
                callBack.onDataSucess(entity);
//                if (AppConfig.DEBUG) {
//                    GoldTeamStatus entity = new GoldTeamStatus();
//                    Random random = new Random();
//                    for (int i = 0; i < 5; i++) {
//                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                        student.setNickname("测试" + (test1++));
//                        student.createShowName();
//                        student.setScore("" + random.nextInt(101));
//                        student.setAvatar_path(mGetInfo.getHeadImgPath());
//                        entity.getStudents().add(student);
//                    }
//                    callBack.onDataSucess(entity);
//                } else {
//                    callBack.onDataFail(1, responseEntity.getErrorMsg());
//                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if (AppConfig.DEBUG) {
                    GoldTeamStatus entity = new GoldTeamStatus();
                    for (int i = 0; i < 3; i++) {
                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                        student.setNickname("测试" + (test1++));
                        student.createShowName();
                        student.setScore("90");
                        student.setAvatar_path(mGetInfo.getHeadImgPath());
                        entity.getStudents().add(student);
                    }
                    callBack.onDataSucess(entity);
                } else {
                    callBack.onDataFail(1, responseEntity.getErrorMsg());
                }
            }
        });
    }

    public String getRequestTime() {
        return mGetInfo.getRequestTime();
    }

    public void getRolePlayAnswerTeamRank(String testId, final AbstractBusinessDataCallBack callBack) {
        getHttpManager().getRolePlayAnswerTeamRank(testId, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mLogtf.d("getRolePlayAnswerTeamRank:responseEntity=" + responseEntity.getJsonObject());
                GoldTeamStatus entity = getHttpResponseParser().parseRolePlayTeamRank(responseEntity, mGetInfo);
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.d(TAG, "getRolePlayAnswerTeamRank:msg=" + msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.d(TAG, "getRolePlayAnswerTeamRank:onPmError=" + responseEntity.getErrorMsg());
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }

        });
    }

    public void getSpeechEvalAnswerTeamRank(String id, final AbstractBusinessDataCallBack callBack) {
        getHttpManager().getSpeechEvalAnswerTeamRank(id, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(final ResponseEntity responseEntity) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmSuccess=" + responseEntity.getJsonObject());
                GoldTeamStatus entity = getHttpResponseParser().parseSpeechTeamRank(responseEntity, mGetInfo);
                callBack.onDataSucess(entity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmFailure=" + msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.i("getSpeechEvalAnswerTeamRank:onPmError=" + responseEntity.getErrorMsg());
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

}
