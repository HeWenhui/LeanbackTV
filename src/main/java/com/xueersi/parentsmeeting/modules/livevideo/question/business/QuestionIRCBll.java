package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tal.speech.speechrecognizer.Constants;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechUtils;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayActionEnd;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineActionEnd;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
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
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * 互动题
 * Created by linyuqiang on 2018/7/5.
 */

public class QuestionIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, QuestionHttp {
    private QuestionBll mQuestionAction;
    private AnswerRankIRCBll mAnswerRankBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;
    //    private SpeechEvaluatorUtils mIse;
    private SpeechUtils mIse;
    /** 置空roleplay，防止QuestionBll里为空，外面不为空 */
    private RolePlayEnd rolePlayActionEnd = new RolePlayEnd();
    /** RolePlayer功能接口 */
    private RolePlayAction rolePlayAction;

    /** RolePlayer 人机功能接口 */
    private RolePlayMachineAction rolePlayMachineAction;

    private String Tag = "QuestionIRCBll";


    private List<String> questiongtype;

    private boolean change = false;

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
        String[] ptTypeFilters = {"4", "0", "1", "2", "8", "5", "6", "18", "19"};
        questiongtype = Arrays.asList(ptTypeFilters);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean isLand) {
        mQuestionAction.initView(bottomContent, isLand.get());
//        if (AppConfig.DEBUG) {
//            SpeechResultEntity speechResultEntity = new SpeechResultEntity();
//            speechResultEntity.score = 12;
//            speechResultEntity.enery = 2;
//            speechResultEntity.gold = 3;
//            speechResultEntity.praise = 10;
//            speechResultEntity.accuracy = 22;
//            speechResultEntity.fluency = 33;
//            ArrayList<SpeechResultMember> speechResultMembers = speechResultEntity.speechResultMembers;
//            for (int i = 0; i < 2; i++) {
//                SpeechResultMember speechResultMember = new SpeechResultMember();
//                speechResultMember.name = "测试" + i;
//                speechResultMember.score = i;
//                speechResultMembers.add(speechResultMember);
//            }
//            SpeechResultPager speechResultPager = new SpeechResultPager(activity, bottomContent, speechResultEntity);
//            bottomContent.addView(speechResultPager.getRootView());
//        }
//        if (com.xueersi.common.config.AppConfig.DEBUG) {
//            com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity answerResultEntity = new com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity();
//            answerResultEntity.isVoice = 1;
//            answerResultEntity.setEnergy(11);
//            answerResultEntity.setGold(2);
//            answerResultEntity.setIsRight(com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager.RESULT_TYPE_PART_CORRECT);
//            java.util.ArrayList<com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity.Answer> answerList = new java.util.ArrayList<>();
//            com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity.Answer answer = new com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity.Answer();
//            answer.setTestType(com.xueersi.parentsmeeting.modules.livevideo.entity.AnswerResultEntity.TEST_TYPE_2);
//            List<String> rightAnswers = new java.util.ArrayList<>();
//            rightAnswers.add("A");
//            answer.setRightAnswers(rightAnswers);
//            List<String> blankList = new java.util.ArrayList<>();
//            blankList.add("C");
//            answer.setBlankList(blankList);
//            List<String> choiceList = new java.util.ArrayList<>();
//            choiceList.add("C");
//            answer.setChoiceList(choiceList);
//            answerList.add(answer);
//            answerResultEntity.setAnswerList(answerList);
//            final android.view.ViewGroup group = bottomContent;
//            com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager artsPSEAnswerResultPager = new com.xueersi.parentsmeeting.modules.livevideo.question.page.ArtsPSEAnswerResultPager(activity, answerResultEntity,
//                    new com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerResultStateListener() {
//                        @Override
//                        public void onCompeletShow() {
//
//                        }
//
//                        @Override
//                        public void onAutoClose(com.xueersi.common.base.BasePager basePager) {
//                            group.removeView(basePager.getRootView());
//                        }
//
//                        @Override
//                        public void onCloseByUser() {
//
//                        }
//                    });
//            bottomContent.addView(artsPSEAnswerResultPager.getRootView());
//        }
    }

    public void onPause() {
        mQuestionAction.onPause();
    }

    @Override
    public void onLiveInited(LiveGetInfo data) {
        super.onLiveInited(data);
        mQuestionAction.setLiveGetInfo(data);
        LiveExamQuestionCreat liveExamQuestionCreat = new LiveExamQuestionCreat();
        int isArts = data.getIsArts();
        liveExamQuestionCreat.setisArts(isArts);
        mQuestionAction.setLiveVideoSAConfig(mLiveBll.getLiveVideoSAConfig());
        liveExamQuestionCreat.setLiveGetInfo(data);
        liveExamQuestionCreat.setQuestionBll(mQuestionAction);
        if (isArts != 1) {
            if (mAnswerRankBll != null) {
                liveExamQuestionCreat.setmAnswerRankBll(mAnswerRankBll);
            }
        }
        liveExamQuestionCreat.setQuestionHttp(this);
        mQuestionAction.setBaseExamQuestionCreat(liveExamQuestionCreat);
        LiveSubjectResultCreat baseSubjectResultCreat = new LiveSubjectResultCreat();
        baseSubjectResultCreat.setLiveGetInfo(data);
        mQuestionAction.setBaseSubjectResultCreat(baseSubjectResultCreat);
        mQuestionAction.setQuestionWebCreate(new LiveQuestionWebCreate());
        if (data.getPattern() == 2) {
            mQuestionAction.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mQuestionAction.new LiveQuestionSwitchImpl(), mQuestionAction, data));
            mQuestionAction.setBaseSpeechCreat(new LiveStandSpeechCreat(this, mLiveBll, mQuestionAction));
            StandSpeechTop3Bll standSpeechTop3Bll = new StandSpeechTop3Bll(activity, this, mLiveBll);
            standSpeechTop3Bll.initView(mRootView);
            mQuestionAction.setSpeechEndAction(standSpeechTop3Bll);
        } else {
            mQuestionAction.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(mQuestionAction.new LiveQuestionSwitchImpl(), mQuestionAction, data));
            mQuestionAction.setBaseSpeechCreat(new LiveSpeechCreat(mQuestionAction, data));
        }
        if (1 == data.getIsEnglish()) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
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
                            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                            mIse.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_CH);
//                            mIse = new SpeechEvaluatorUtils(true, Constants.ASSESS_PARAM_LANGUAGE_CH);
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
        if (mAnswerRankBll != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mQuestionAction.setAnswerRankBll(mAnswerRankBll.getAnswerRankBll());
                }
            });
        }
        if (mLiveAutoNoticeBll != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mQuestionAction.setLiveAutoNoticeBll(mLiveAutoNoticeBll.getLiveAutoNoticeBll());
                }
            });
        }

        if (mGetInfo.getIsArts() == 0) {
            QuestionWebCache webCache = new QuestionWebCache(activity);
            webCache.startCache();
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        /**新版文科课件平台 Topic**/
        Loger.e(Tag, "=======>onTopic:" + jsonObject);
        if (isNewArtsH5Courseware(jsonObject)) {
            try {
                if (change) {
                    LiveVideoConfig.isNewArts = false;
                }
                String onlineTechStatus = "";
                JSONObject onlineJobj = jsonObject.optJSONObject("coursewareOnlineTech");
                if (onlineJobj != null && "on".equals(onlineJobj.optString("status"))) {
                    JSONObject onlineTechObj = jsonObject.getJSONObject("coursewareOnlineTech");
                    if (!"{}".equals(onlineTechObj.toString())) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        LiveVideoConfig.isNewArts = true;
                        videoQuestionLiveEntity.setNewArtsCourseware(true);
                        String status = onlineTechObj.optString("status");
                        if ("on".equals(status)) {
                            videoQuestionLiveEntity.package_socurce = onlineTechObj.optInt("package_source");
                            videoQuestionLiveEntity.gold = onlineTechObj.optDouble("gold");
                            videoQuestionLiveEntity.time = onlineTechObj.optDouble("time");
                            videoQuestionLiveEntity.id = getIdStr(onlineTechObj.optJSONArray("id"));
                            videoQuestionLiveEntity.multiRolePlay = onlineTechObj.optString("multiRolePlay");
                            videoQuestionLiveEntity.roles = onlineTechObj.optString("roles");
                            videoQuestionLiveEntity.totalScore = onlineTechObj.optString("totalScore");
                            videoQuestionLiveEntity.answer = onlineTechObj.optString("answer");
                            videoQuestionLiveEntity.speechContent = onlineTechObj.optString("answer");
                            videoQuestionLiveEntity.type = onlineTechObj.optString("ptype");
                            videoQuestionLiveEntity.num = 1;
                            if ("5".equals(videoQuestionLiveEntity.type) || "6".equals(videoQuestionLiveEntity.type)) {
                                videoQuestionLiveEntity.setUrl(buildRolePlayUrl(getIdStr(onlineTechObj.optJSONArray("id")), videoQuestionLiveEntity.type));
                                videoQuestionLiveEntity.isAllow42 = "0";
                            } else {
                                videoQuestionLiveEntity.setUrl(buildCourseUrl(getIdStr(onlineTechObj.optJSONArray("id"))));
                                videoQuestionLiveEntity.isAllow42 = "1";
                            }
                            if ("1".equals(onlineTechObj.optString("isVoice"))) {
                                videoQuestionLiveEntity.assess_ref = onlineTechObj.optString("assess_ref");
                                videoQuestionLiveEntity.questiontype = onlineTechObj.optString("questiontype");
                                videoQuestionLiveEntity.setIsVoice(onlineTechObj.optString("isVoice"));
                            }
                            //解决，老师发题后，学生后进来，无法进入roleplay的问题
                            //人机的回调

                            enterLiveRplayAfterTeacherRead(videoQuestionLiveEntity);
//                            videoQuestionLiveEntity.setUrl(buildCourseUrl(getIdStr(onlineTechObj.getJSONArray("id"))));
                            logger.e("======> onTopic 1111:" + mQuestionAction);
                            if (mQuestionAction != null && (questiongtype.contains(videoQuestionLiveEntity.type))) {
                                logger.e("======> showQuestionType:" + videoQuestionLiveEntity.questiontype);
                                mQuestionAction.showQuestion(videoQuestionLiveEntity);
                                if (mAnswerRankBll != null) {
                                    mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                }
                                if (mLiveAutoNoticeBll != null) {
                                    mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                    mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.package_socurce + "");
                                }
                            }
                        }
                    }
                } else {
                    LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
                    if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
                        String num = mainRoomstatus.getExamNum();
                        if ("on".equals(mainRoomstatus.getExamStatus())) {
                            VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                            videoQuestionLiveEntity.id = num;
                            mQuestionAction.onExamStart(mLiveId, videoQuestionLiveEntity);
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(num);
                            }
                        } else {
                            mQuestionAction.onExamStop(num);
                        }
                    }


                    if (liveTopic.getVideoQuestionLiveEntity() != null) {
                        logger.e("======>QuestionIRCBlle:" + "走了错误的逻辑");
                        if (mQuestionAction != null) {

                            VideoQuestionLiveEntity videoQuestionLiveEntity = liveTopic.getVideoQuestionLiveEntity();

                            JSONObject topicObj = jsonObject.optJSONObject("topic");
                            videoQuestionLiveEntity.roles = topicObj.optString("roles");
                            videoQuestionLiveEntity.id = topicObj.optString("id");

                            //解决，老师发题后，学生后进来，无法进入roleplay的问题
                            //人机的回调

                            if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles)) {
                                if (rolePlayMachineAction == null) {
                                    RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, mRootView, mLiveBll, mGetInfo);
                                    rolePlayMachineAction = (RolePlayMachineAction) rolePlayerBll;
                                }

                                //多人的回调
                                if (rolePlayAction == null) {
                                    RolePlayerBll rolePlayerBll = new RolePlayerBll(activity, mRootView, mLiveBll, mGetInfo);
                                    rolePlayAction = rolePlayerBll;
                                }
                                mQuestionAction.setRolePlayMachineAction(rolePlayMachineAction, rolePlayActionEnd);
                                mQuestionAction.setRolePlayAction(rolePlayAction, rolePlayActionEnd);
                            }

                            mQuestionAction.showQuestion(videoQuestionLiveEntity);
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                            }
                            if (mLiveAutoNoticeBll != null) {
                                mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.srcType);
                            }
                        }
                    } else {
                        logger.e("======>QuestionIRCBlle:" + "正常的逻辑");
                        if (mQuestionAction != null) {
                            mQuestionAction.showQuestion(null);
                        }
                    }
//                    JSONObject coursewareH5 = jsonObject.getJSONObject("coursewareH5");
//                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
//                    videoQuestionLiveEntity.setNewArtsCourseware(true);
//                    videoQuestionLiveEntity.gold = coursewareH5.optDouble("gold");
//                    videoQuestionLiveEntity.package_socurce = coursewareH5.optInt("package_socurce");
//                    videoQuestionLiveEntity.time = coursewareH5.optDouble("time");
//                    videoQuestionLiveEntity.setIsVoice(coursewareH5.optString("isVoice"));
//                    videoQuestionLiveEntity.type = coursewareH5.optString("ptype");
//                    String status = coursewareH5.optString("status", "off");
//                    if ("on".equals(status)) {
//                        videoQuestionLiveEntity.id = coursewareH5.optString("id");
//                        JSONArray idObject = coursewareH5.getJSONArray("id");
//                        String idStr = getIdStr(idObject);
//                        videoQuestionLiveEntity.setUrl(buildCourseUrl(idStr));
//                        if ("1".equals(videoQuestionLiveEntity.getIsVoice())) {
//                            videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = coursewareH5
//                                    .optString("questiontype");
//                            videoQuestionLiveEntity.assess_ref = coursewareH5.optString("assess_ref");
//                        }
//                        Loger.e("QuestionIRCBll", "======> onTopic 1111:" + mQuestionAction);
//                        if (mQuestionAction != null) {
//                            mQuestionAction.showQuestion(videoQuestionLiveEntity);
//                            if (mAnswerRankBll != null) {
//                                mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
//                            }
//                            if (mLiveAutoNoticeBll != null) {
//                                mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
//                                mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.package_socurce + "");
//                            }
//                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.e("======>QuestionIRCBlle:" + e.toString());
            }
        } else {
            LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
            if (mainRoomstatus.isHaveExam() && mQuestionAction != null) {
                String num = mainRoomstatus.getExamNum();
                if ("on".equals(mainRoomstatus.getExamStatus())) {
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    videoQuestionLiveEntity.id = num;
                    mQuestionAction.onExamStart(mLiveId, videoQuestionLiveEntity);
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(num);
                    }
                } else {
                    mQuestionAction.onExamStop(num);
                }
            }


            if (liveTopic.getVideoQuestionLiveEntity() != null) {
                logger.e("======>QuestionIRCBlle:" + "走了错误的逻辑");
                if (mQuestionAction != null) {

                    VideoQuestionLiveEntity videoQuestionLiveEntity = liveTopic.getVideoQuestionLiveEntity();

                    JSONObject topicObj = jsonObject.optJSONObject("topic");
                    videoQuestionLiveEntity.roles = topicObj.optString("roles");
                    videoQuestionLiveEntity.id = topicObj.optString("id");

                    //解决，老师发题后，学生后进来，无法进入roleplay的问题
                    //人机的回调

                    enterLiveRplayAfterTeacherRead(videoQuestionLiveEntity);

                    mQuestionAction.showQuestion(videoQuestionLiveEntity);
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                    }
                    if (mLiveAutoNoticeBll != null) {
                        mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.srcType);
                    }
                }
            } else {
                logger.e("======>QuestionIRCBlle:" + "正常的逻辑");
                if (mQuestionAction != null) {
                    mQuestionAction.showQuestion(null);
                }
            }
        }
        Loger.e(Tag, "=======>onTopic:" + "isNewArts:" + LiveVideoConfig.isNewArts);
    }

    private void enterLiveRplayAfterTeacherRead(VideoQuestionLiveEntity videoQuestionLiveEntity) {
        if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles) || "5".equals(videoQuestionLiveEntity.type)) {
            if (rolePlayMachineAction == null) {
                RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, mRootView, mLiveBll, mGetInfo);
                rolePlayMachineAction = (RolePlayMachineAction) rolePlayerBll;
            }

            //多人的回调
            if (rolePlayAction == null) {
                RolePlayerBll rolePlayerBll = new RolePlayerBll(activity, mRootView, mLiveBll, mGetInfo);
                rolePlayAction = rolePlayerBll;
            }
            mQuestionAction.setRolePlayMachineAction(rolePlayMachineAction, rolePlayActionEnd);
            mQuestionAction.setRolePlayAction(rolePlayAction, rolePlayActionEnd);
        }
    }


    /**
     * 是否是新版文科 课件 topic消息
     *
     * @param jsonObject
     * @return
     */
    private boolean isNewArtsH5Courseware(JSONObject jsonObject) {
        return (jsonObject.has("coursewareH5") || jsonObject.has("coursewareOnlineTech"));
    }


    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        logger.e("======>onNotice:" + type + ":" + object);
        switch (type) {
            case XESCODE.SENDQUESTION: {
                logger.i("onNotice SENDQUESTION ");
                change = true;
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

//
//                Loger.e("yzl_roleplay","走人机 end");
                String isVoice = object.optString("isVoice");
                videoQuestionLiveEntity.setIsVoice(isVoice);
                if ("1".equals(isVoice)) {
                    videoQuestionLiveEntity.questiontype = object.optString("questiontype");
                    videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                }
                if (mQuestionAction != null) {
//                            mGetInfo.getLiveTopic().setTopic(getTopicFromQuestion(videoQuestionLiveEntity));

                    //设置action的方法要在showQuestion之前
                    if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles) && !videoQuestionLiveEntity.multiRolePlay.equals("1")) {
                        logger.i("走人机start,拉取试题");
                        RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, mRootView, mLiveBll, mGetInfo);
                        mQuestionAction.setRolePlayMachineAction(rolePlayerBll, rolePlayActionEnd);
                        rolePlayMachineAction = rolePlayerBll;

                    }
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
                        logger.e("======>LiveBll setWebViewCloseByTeacher: " +
                                "SENDQUESTION");
                    }
                }
            }
            break;
            case XESCODE.ARTS_SEND_QUESTION: {
                logger.i("onNotice ARTS_SEND_QUESTION");
                change = false;
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.gold = object.optDouble("gold");
                videoQuestionLiveEntity.id = getIdStr(object.optJSONArray("id"));
                videoQuestionLiveEntity.nonce = object.optString("nonce");
                videoQuestionLiveEntity.roles = object.optString("roles");
                videoQuestionLiveEntity.type = object.optString("ptype");
                videoQuestionLiveEntity.package_socurce = object.optInt("package_source");
                videoQuestionLiveEntity.time = object.optDouble("time");
                videoQuestionLiveEntity.multiRolePlay = object.optString("multiRolePlay");
                videoQuestionLiveEntity.speechContent = object.optString("answer");
                videoQuestionLiveEntity.num = 1;
                videoQuestionLiveEntity.setNewArtsCourseware(true);
                String isVoice = object.optString("isVoice");
                videoQuestionLiveEntity.setIsVoice(isVoice);
                //构建 H5 url
                if ("5".equals(videoQuestionLiveEntity.type) || "6".equals(videoQuestionLiveEntity.type)) {
                    videoQuestionLiveEntity.setUrl(buildRolePlayUrl(getIdStr(object.optJSONArray("id")), videoQuestionLiveEntity.type));
                    videoQuestionLiveEntity.isAllow42 = "0";
                } else {
                    videoQuestionLiveEntity.setUrl(buildCourseUrl(getIdStr(object.optJSONArray("id"))));
                    videoQuestionLiveEntity.isAllow42 = "1";
                }
                if ("1".equals(isVoice)) {
                    videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                    videoQuestionLiveEntity.questiontype = object.optString("questionType");
                    videoQuestionLiveEntity.setIsVoice(isVoice);
                }
                if (mQuestionAction != null) {
                    //设置action的方法要在showQuestion之前
                    if (!TextUtils.isEmpty(videoQuestionLiveEntity.roles) && !videoQuestionLiveEntity.multiRolePlay.equals("1")) {
                        logger.i("onNotice 新课件平台，走人机start,拉取试题");
                        RolePlayMachineBll rolePlayerBll = new RolePlayMachineBll(activity, mRootView, mLiveBll, mGetInfo);
                        mQuestionAction.setRolePlayMachineAction(rolePlayerBll, rolePlayActionEnd);
                        rolePlayMachineAction = rolePlayerBll;

                    }
                    mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(videoQuestionLiveEntity);
                    mQuestionAction.showQuestion(videoQuestionLiveEntity);
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                    }
                    if (mLiveAutoNoticeBll != null) {
                        mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.srcType);
                    }
                }


                break;
            }
            case XESCODE.STOPQUESTION:
                logger.i("onNotice STOPQUESTION");
                mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(null);
                if (mQuestionAction != null) {
                    try {
                        mQuestionAction.onStopQuestion("STOPQUESTION", object.getString("ptype"), object.optString("nonce"));
                        //解决多人的时候，除了初次的多人正常进对话，其他的都进不去
                        rolePlayAction = null;
                        rolePlayMachineAction = null;
                        if (mQuestionAction instanceof QuestionBll) {
                            ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                            logger.e("======>LiveBll setWebViewCloseByTeacher: " +
                                    "STOPQUESTION");
                        }
                    } catch (Exception e) {

                    }
                }
                break;

            case XESCODE.ARTS_STOP_QUESTION: {
                logger.i("onNotice ARTS_STOP_QUESTION");
                mGetInfo.getLiveTopic().setVideoQuestionLiveEntity(null);
                String ptype = object.optString("ptype");

                String package_socurce = object.optString("package_socurce");
                if (mQuestionAction != null) {
                    try {
                        mQuestionAction.onStopQuestion("ARTS_STOP_QUESTION", object.getString("ptype"), object.optString("nonce"));
                        //解决多人的时候，除了初次的多人正常进对话，其他的都进不去
                        rolePlayAction = null;
                        rolePlayMachineAction = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case XESCODE.EXAM_START:
                logger.i("onNotice EXAM_START ");
                if (mQuestionAction != null) {
                    String num = object.optString("num", "0");
                    String nonce = object.optString("nonce");
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    videoQuestionLiveEntity.id = num;
                    videoQuestionLiveEntity.nonce = nonce;
                    mQuestionAction.onExamStart(mLiveId, videoQuestionLiveEntity);
                    if (mQuestionAction instanceof QuestionBll) {
                        ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(false);
                        logger.e("======>LiveBll setWebViewCloseByTeacher: EXAM_START");
                    }
                }
                break;
            case XESCODE.EXAM_STOP: {
                logger.i("onNotice EXAM_STOP ");
                if (mQuestionAction != null) {
                    String num = object.optString("num", "-1");
                    mQuestionAction.onExamStop(num);
                    if (mQuestionAction instanceof QuestionBll) {
                        ((QuestionBll) mQuestionAction).setWebViewCloseByTeacher(true);
                        logger.e("======>LiveBll setWebViewCloseByTeacher: EXAM_STOP");
                    }
                }
                break;
            }
            case XESCODE.XCR_ROOM_ROLE_READ: {
                logger.i("onNotice XCR_ROOM_ROLE_READ ");
                if (rolePlayAction == null) {
                    RolePlayerBll rolePlayerBll = new RolePlayerBll(activity, mRootView, mLiveBll, mGetInfo);
                    mQuestionAction.setRolePlayAction(rolePlayerBll, rolePlayActionEnd);
                    rolePlayAction = rolePlayerBll;
                }

                //在多人的时候，同时设置人机的roleplayaction
                if (rolePlayMachineAction == null) {
                    RolePlayMachineBll rolePlayerMachineBll = new RolePlayMachineBll(activity, mRootView, mLiveBll, mGetInfo);
                    mQuestionAction.setRolePlayMachineAction(rolePlayerMachineBll, rolePlayActionEnd);
                    rolePlayMachineAction = (RolePlayMachineAction) rolePlayerMachineBll;

                }

                String nonce = object.optString("nonce");
                rolePlayAction.teacherRead(mLiveId, mLiveBll.getStuCouId(), nonce);
                break;
            }
            default:
                break;
        }
    }


    private String getIdStr(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (jsonArray != null) {
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    if (i < (len - 1)) {
                        stringBuilder.append(jsonArray.getString(i)).append(",");
                    } else {
                        stringBuilder.append(jsonArray.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String buildCourseUrl(String testIds) {
        String isPlayback = "1";
        if (LiveVideoConfig.LIVE_TYPE_TUTORIAL == mLiveBll.getLiveType()) {
            isPlayback = "2";
        }
        StringBuilder sb = new StringBuilder();
        String falseStr = Base64.encodeBytes("false".getBytes());
        sb.append(mLiveBll.getLiveVideoSAConfig().inner.URL_ARTS_H5_URL).append("?liveId=").append(mLiveId)
                .append("&testIds=").append(testIds).append("&isPlayBack=").append(isPlayback)
                .append("&stuCouId=").append(mLiveBll.getStuCouId()).append("&stuId=").append(mGetInfo
                .getStuId())
                .append("&xesrfh=").append(AppBll.getInstance().getUserRfh())
                .append("&cookie=").append(AppBll.getInstance().getUserToken())
                .append("&stuClientPath=").append(falseStr)
                .append("&fontDir=").append(falseStr);
        return sb.toString();
    }

    private String buildRolePlayUrl(String id, String type) {
        String isPlayback = "0";
        StringBuilder sb = new StringBuilder();
        String url;
        if ("5".equals(type)) {
            if (mGetInfo.getPattern() == 2) {
                url = mLiveBll.getLiveVideoSAConfig().inner.URL_NEWARTS_STANDROALPLAY_URL;
            } else {
                url = mLiveBll.getLiveVideoSAConfig().inner.URL_NEWARTS_ROALPLAY_URL;
            }
        } else {
            url = mLiveBll.getLiveVideoSAConfig().inner.URL_NEWARTS_CHINESEREADING_URL;
        }
        sb.append(url).append("?liveId=").append(mLiveId)
                .append("&testId=").append(id).append("&isPlayBack=").append(isPlayback)
                .append("&stuCouId=").append(mLiveBll.getStuCouId()).append("&stuId=").append(mGetInfo
                .getStuId())
                .append("&xesrfh=").append(AppBll.getInstance().getUserRfh())
                .append("&cookie=").append(AppBll.getInstance().getUserToken());
        return sb.toString();
    }


    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.SENDQUESTION,
                XESCODE.STOPQUESTION,
                XESCODE.EXAM_START,
                XESCODE.EXAM_STOP,
                XESCODE.XCR_ROOM_ROLE_READ,
                XESCODE.ARTS_SEND_QUESTION,
                XESCODE.ARTS_STOP_QUESTION
        };
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        mQuestionAction.setVideoLayout(liveVideoPoint);
    }

    @Override
    public void getStuGoldCount(String method) {
        UpdateAchievement updateAchievement = getInstance(UpdateAchievement.class);
        if (updateAchievement != null) {
            updateAchievement.getStuGoldCount("getStuGoldCount:" + method, UpdateAchievement.GET_TYPE_QUE);
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
                        logger.d("getQuestion:onPmSuccess" + responseEntity.getJsonObject());
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.e("getQuestion:onFailure", e);
                        super.onFailure(call, e);
                        callBack.onDataSucess();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.d("getQuestion:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                        callBack.onDataSucess();
                    }
                });
    }

    @Override
    public void liveSubmitTestAnswer(final LiveBasePager liveBasePager, final VideoQuestionLiveEntity videoQuestionLiveEntity,
                                     String mVSectionID, String testAnswer, final boolean isVoice, boolean isRight, final QuestionSwitch.OnAnswerReslut answerReslut, String isSubmit) {
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
        if (LiveVideoConfig.isNewArts) {
            logger.e("======> liveSubmitTestAnswer:" + videoQuestionLiveEntity.isNewArtsH5Courseware());
            getHttpManager().liveNewArtsSubmitTestAnswer(
                    videoQuestionLiveEntity.id, mLiveId, testAnswer, isSubmit, new HttpCallBack() {

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
                                mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, entity);
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
//                        if (!responseEntity.isJsonError()) {
//
//                        }
                            if (mQuestionAction != null) {
                                mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                        }
                    });
        } else {
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
                                mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, entity);
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
//                        if (!responseEntity.isJsonError()) {
//
//                        }
                            if (mQuestionAction != null) {
                                mQuestionAction.onAnswerReslut(liveBasePager, videoQuestionLiveEntity, null);
                            }
                            if (answerReslut != null) {
                                answerReslut.onAnswerReslut(videoQuestionLiveEntity, null);
                            }
                        }
                    });
        }

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
    public void sendSpeechEvalResult2(String id, String stuAnswer, String isSubmit, final OnSpeechEval onSpeechEval) {
        String liveid = mGetInfo.getId();
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        if (LiveVideoConfig.isNewArts) {
            getHttpManager().sendSpeechEvalResultNewArts(enstuId, liveid, id, stuAnswer, isSubmit, new HttpCallBack(false) {

                @Override
                public void onPmSuccess(final ResponseEntity responseEntity) {
                    mLogtf.i("sendSpeechEvalResult2:onPmSuccess=" + responseEntity.getJsonObject());
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
        } else {
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
        if (LiveVideoConfig.isNewArts) {
            getHttpManager().speechNewArtEvaluateIsAnswered(enstuId, mVSectionID, num, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(final ResponseEntity responseEntity) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    JSONObject detail = jsonObject.optJSONObject("data");
                    if (detail != null) {
                        mLogtf.i("speechEvaluatenewArtsIsAnswered:onPmSuccess=" + jsonObject);
                        boolean isAnswer = detail.optInt("isAnswer") == 1;
                        isAnswered.isAnswer(isAnswer);
                    } else {
                        boolean isAnswer = jsonObject.optInt("isAnswer") == 1;
                        isAnswered.isAnswer(isAnswer);
                    }
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    mLogtf.i("speechEvaluatenewArtsIsAnswered:onPmFailure=" + msg);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    mLogtf.i("speechEvaluatenewArtsIsAnswered:onPmError=" + responseEntity.getErrorMsg());
                }
            });
        } else {
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
//                if (AppConfig.DEBUG) {
//                    GoldTeamStatus entity = new GoldTeamStatus();
//                    for (int i = 0; i < 3; i++) {
//                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                        student.setNickname("测试" + (test1++));
//                        student.createShowName();
//                        student.setScore("90");
//                        student.setAvatar_path(mGetInfo.getHeadImgPath());
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
                logger.d("getRolePlayAnswerTeamRank:msg=" + msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("getRolePlayAnswerTeamRank:onPmError=" + responseEntity.getErrorMsg());
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


    @Override
    public void onDestory() {
        super.onDestory();
        if (mQuestionAction != null) {
            mQuestionAction.onDestroy();
        }
    }

    private class RolePlayEnd implements RolePlayActionEnd, RolePlayMachineActionEnd {

        @Override
        public void endRolePlayAction(String method, RolePlayAction action) {
            mLogtf.d("endRolePlayAction:method=" + method + ",same=" + (action == rolePlayAction));
            rolePlayAction = null;
        }

        @Override
        public void endRolePlayMachineAction(String method, RolePlayAction action) {
            mLogtf.d("endRolePlayMachineAction:method=" + method + ",same=" + (action == rolePlayMachineAction));
            rolePlayMachineAction = null;
        }
    }

}






