package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by lyqai on 2018/7/5.
 */

public class EnglishH5CoursewareIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private EnglishH5CoursewareBll englishH5CoursewareAction;
    private AnswerRankIRCBll mAnswerRankBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;

    public EnglishH5CoursewareIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        EnglishH5CoursewareBll englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
        englishH5CoursewareBll.setLiveType(mLiveType);
        englishH5CoursewareBll.setVSectionID(mLiveId);
        englishH5CoursewareBll.initView(mRootView);
        englishH5CoursewareBll.setLiveBll(new EnglishH5CoursewareImpl());
        englishH5CoursewareBll.initData();
        englishH5CoursewareAction = englishH5CoursewareBll;
        mAnswerRankBll = getInstance(AnswerRankIRCBll.class);
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        int pattern = getInfo.getPattern();
        EnglishH5CoursewareBll englishH5CoursewareBll = (EnglishH5CoursewareBll) englishH5CoursewareAction;
        if (pattern == 2) {
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveStandVoiceAnswerCreat(activity, mLiveBll, englishH5CoursewareBll.new LiveStandQuestionSwitchImpl(), mGetInfo.getHeadImgPath(), mGetInfo.getStandLiveName()));
        } else {
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(englishH5CoursewareBll.new LiveQuestionSwitchImpl()));
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (englishH5CoursewareAction != null) {
            englishH5CoursewareAction.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            if (englishH5CoursewareAction != null && jsonObject.has("H5_Courseware")) {
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                JSONObject h5_Experiment = jsonObject.getJSONObject("H5_Courseware");
                String play_url = "";
                String status = h5_Experiment.optString("status", "off");
                String id = "";
                String courseware_type = "";
                if ("on".equals(status)) {
                    id = h5_Experiment.getString("id");
                    courseware_type = h5_Experiment.getString("courseware_type");
                    play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveBll.getStuCouId() + "/" + id +
                            "/" + courseware_type
                            + "/" + mGetInfo.getStuId();
                    videoQuestionLiveEntity.id = id;
                    videoQuestionLiveEntity.courseware_type = courseware_type;
                    videoQuestionLiveEntity.setUrl(play_url);
                    videoQuestionLiveEntity.nonce = "";
                    String isVoice = h5_Experiment.optString("isVoice");
                    videoQuestionLiveEntity.setIsVoice(isVoice);
                    if ("1".equals(isVoice)) {
                        videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = h5_Experiment
                                .optString("questiontype");
                        videoQuestionLiveEntity.assess_ref = h5_Experiment.optString("assess_ref");
                    }
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                    }
                    if (mLiveAutoNoticeBll != null) {
                        mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                        mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                    }
                }
                englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onNotice(JSONObject object, int type) {
        switch (type) {
            case XESCODE.ENGLISH_H5_COURSEWARE:
                try {
                    if (englishH5CoursewareAction != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        String play_url = "";
                        String status = object.optString("status", "off");
                        String nonce = object.optString("nonce");
                        String id = "";
                        String courseware_type = "";
                        if ("on".equals(status)) {
                            id = object.getString("id");
                            courseware_type = object.getString("courseware_type");
                            play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveBll.getStuCouId() + "/"
                                    + id + "/" + courseware_type
                                    + "/" + mGetInfo.getStuId();
                            videoQuestionLiveEntity.id = id;
                            videoQuestionLiveEntity.courseware_type = courseware_type;
                            videoQuestionLiveEntity.setUrl(play_url);
                            videoQuestionLiveEntity.nonce = nonce;
                            String isVoice = object.optString("isVoice");
                            videoQuestionLiveEntity.setIsVoice(isVoice);
                            if ("1".equals(isVoice)) {
                                videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = object
                                        .optString("questiontype");
                                videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                            }
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mAnswerRankBll.setType(videoQuestionLiveEntity.courseware_type);
                            }
                            if (mLiveAutoNoticeBll != null) {
                                mLiveAutoNoticeBll.setTestId(videoQuestionLiveEntity.getvQuestionID());
                                mLiveAutoNoticeBll.setSrcType(videoQuestionLiveEntity.courseware_type);
                            }
                            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher
                                        (false);
                            }
                        } else {
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setNonce(object.optString("nonce"));
                            }
                            if (englishH5CoursewareAction instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareAction).setWebViewCloseByTeacher(true);
                            }
                        }
                        englishH5CoursewareAction.onH5Courseware(status, videoQuestionLiveEntity);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.ENGLISH_H5_COURSEWARE};
    }

    class EnglishH5CoursewareImpl implements EnglishH5CoursewareHttp {

        @Override
        public void getStuGoldCount() {
            QuestionIRCBll questionIRCBll = getInstance(QuestionIRCBll.class);
            if (questionIRCBll != null) {
                questionIRCBll.getStuGoldCount();
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
                    sendNotice(jsonObject, mLiveBll.getMainTeacherStr());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID, String testAnswer, String courseware_type, String isSubmit, double voiceTime, boolean isRight, final QuestionSwitch.OnAnswerReslut onAnswerReslut) {
            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            mLogtf.d("liveSubmitTestH5Answer:enstuId=" + enstuId + "," + videoQuestionLiveEntity.srcType + ",testId=" +
                    videoQuestionLiveEntity.id + ",liveId=" + mLiveId + ",testAnswer="
                    + testAnswer);
            String userMode = "1";
            if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                if (mGetInfo.getStudentLiveInfo().isExpe()) {
                    userMode = "0";
                }
            }
            getHttpManager().liveSubmitTestH5Answer(enstuId, videoQuestionLiveEntity.srcType,
                    videoQuestionLiveEntity.id, mLiveId, testAnswer, courseware_type, userMode, isSubmit, voiceTime, isRight, new
                            HttpCallBack() {

                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) {
                                    mLogtf.d("liveSubmitTestH5Answer:onPmSuccess=" + responseEntity.getJsonObject()
                                            .toString() +
                                            "," + videoQuestionLiveEntity);
                                    VideoResultEntity entity = getHttpResponseParser().parseQuestionAnswer(responseEntity,
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
                                    mLogtf.d("liveSubmitTestH5Answer:onPmFailure=" + msg + ",testId=" +
                                            videoQuestionLiveEntity.id);
                                    if (onAnswerReslut != null) {
                                        onAnswerReslut.onAnswerFailure();
                                    }
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    mLogtf.d("liveSubmitTestH5Answer:onPmError=" + responseEntity.getErrorMsg() + "," +
                                            "testId=" +
                                            videoQuestionLiveEntity.id);
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
