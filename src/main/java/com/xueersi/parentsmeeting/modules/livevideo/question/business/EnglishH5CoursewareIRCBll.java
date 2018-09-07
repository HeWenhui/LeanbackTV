package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.string.Base64;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.H5OnlineTechEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultCplShowEvent;
import com.xueersi.parentsmeeting.modules.livevideo.notice.business.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/5.
 */

public class EnglishH5CoursewareIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private EnglishH5CoursewareBll englishH5CoursewareBll;
    private AnswerRankIRCBll mAnswerRankBll;
    private TeamPkBll mTeamPKBll;
    private LiveAutoNoticeIRCBll mLiveAutoNoticeBll;
    private EnglishH5Cache englishH5Cache;
    private String Tag = "EnglishH5CoursewareIRCBll";

    public EnglishH5CoursewareIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(EnglishH5CoursewareIRCBll.class, this);
        englishH5CoursewareBll = new EnglishH5CoursewareBll(activity);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        englishH5CoursewareBll.setShareDataManager(mShareDataManager);
        englishH5CoursewareBll.setLiveType(mLiveType);
        englishH5CoursewareBll.setVSectionID(mLiveId);
        englishH5CoursewareBll.setLiveBll(new EnglishH5CoursewareImpl());
        englishH5CoursewareBll.initData();
        mAnswerRankBll = getInstance(AnswerRankIRCBll.class);
        mLiveAutoNoticeBll = getInstance(LiveAutoNoticeIRCBll.class);
        mTeamPKBll = getInstance(TeamPkBll.class);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        englishH5CoursewareBll.initView(mRootView);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        int pattern = getInfo.getPattern();
        if (pattern == 2) {
            LiveStandVoiceAnswerCreat liveStandVoiceAnswerCreat = new LiveStandVoiceAnswerCreat(activity, mLiveBll,
                    englishH5CoursewareBll.new LiveStandQuestionSwitchImpl(), mGetInfo.getHeadImgPath(), mGetInfo
                    .getStandLiveName());
            liveStandVoiceAnswerCreat.setLivePagerBack(englishH5CoursewareBll);
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(liveStandVoiceAnswerCreat);
        } else {
            englishH5CoursewareBll.setBaseVoiceAnswerCreat(new LiveVoiceAnswerCreat(englishH5CoursewareBll.new
                    LiveQuestionSwitchImpl(), englishH5CoursewareBll));
        }
        LiveBaseEnglishH5CoursewareCreat liveBaseEnglishH5CoursewareCreat = new LiveBaseEnglishH5CoursewareCreat();
        int isArts = (int) mLiveBll.getBusinessShareParam("isArts");
        liveBaseEnglishH5CoursewareCreat.setIS_SCIENCE(isArts != 1);
        if (isArts != 1) {
            if (mAnswerRankBll != null) {
                liveBaseEnglishH5CoursewareCreat.setmAnswerRankBll(mAnswerRankBll);
            }
        }
        liveBaseEnglishH5CoursewareCreat.setAllowTeamPk(getInfo != null && "1".equals(getInfo.getIsAllowTeamPk()));
        liveBaseEnglishH5CoursewareCreat.setLivePagerBack(englishH5CoursewareBll);
        englishH5CoursewareBll.setGetInfo(getInfo);
        englishH5CoursewareBll.setBaseEnglishH5CoursewareCreat(liveBaseEnglishH5CoursewareCreat);
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            englishH5Cache = new EnglishH5Cache(activity, mLiveId);
            englishH5Cache.setHttpManager(mLiveBll.getHttpManager());
            englishH5Cache.getCourseWareUrl();
        }
        if (mAnswerRankBll != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    englishH5CoursewareBll.setAnswerRankBll(mAnswerRankBll.getAnswerRankBll());
                }
            });
        }
        if (mLiveAutoNoticeBll != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    englishH5CoursewareBll.setLiveAutoNoticeBll(mLiveAutoNoticeBll.getLiveAutoNoticeBll());
                }
            });
        }

        EventBus.getDefault().register(this);
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        englishH5CoursewareBll.setIse(ise);
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (englishH5CoursewareBll != null) {
            englishH5CoursewareBll.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onPause() {
        if (englishH5CoursewareBll != null) {
            englishH5CoursewareBll.onPause();
        }
    }

    @Override
    public void onResume() {
        if (englishH5CoursewareBll != null) {
            englishH5CoursewareBll.onResume();
        }
    }


    String[] filters = {"4", "0", "1", "2", "8", "5", "6"};
    /**
     * ptType 过滤器
     */
    private List<String> ptTypeFilters = Arrays.asList(filters);

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            //文科新课件平台  topic
            if (isNewArtsH5Courseware(jsonObject)) {
                LiveVideoConfig.isNewArts = true;
                boolean isCourseware = jsonObject.optBoolean("isCourseware");
                JSONObject coursewareH5 = jsonObject.getJSONObject("coursewareH5");
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.setNewArtsCourseware(true);
                videoQuestionLiveEntity.gold = coursewareH5.optDouble("gold");
                videoQuestionLiveEntity.package_socurce = coursewareH5.optInt("package_socurce");
                videoQuestionLiveEntity.time = coursewareH5.optDouble("time");
                videoQuestionLiveEntity.setIsVoice(coursewareH5.optString("isVoice"));
                videoQuestionLiveEntity.type = coursewareH5.optString("ptype");
                String status = coursewareH5.optString("status", "off");
                if ("on".equals(status)) {
                    videoQuestionLiveEntity.id = coursewareH5.optString("id");
                    JSONArray idObject = coursewareH5.getJSONArray("id");
                    String idStr = getIdStr(idObject);
                    videoQuestionLiveEntity.setUrl(buildCourseUrl(idStr));
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice())) {
                        videoQuestionLiveEntity.type = videoQuestionLiveEntity.questiontype = coursewareH5
                                .optString("questiontype");
                        videoQuestionLiveEntity.assess_ref = coursewareH5.optString("assess_ref");
                    }
                }

                if (jsonObject.has("coursewareOnlineTech")) {
                    JSONObject onlineTechObj = jsonObject.getJSONObject("coursewareOnlineTech");
                    if (!"{}".equals(onlineTechObj.toString())) {
                        H5OnlineTechEntity h5OnlineTechEntity = new H5OnlineTechEntity();
                        h5OnlineTechEntity.setStatus(onlineTechObj.optString("status"));
                        status = onlineTechObj.optString("status");
                        h5OnlineTechEntity.setPackage_source(onlineTechObj.optInt("package_source"));
                        h5OnlineTechEntity.setGold(onlineTechObj.optString("gold"));
                        h5OnlineTechEntity.setTime(onlineTechObj.optString("time"));
                        h5OnlineTechEntity.setId(onlineTechObj.optString("id"));
                        h5OnlineTechEntity.setPtype(onlineTechObj.optString("ptype"));
                        if (ptTypeFilters.contains(onlineTechObj.optString("ptype"))) {
                            Loger.e("EnglishH5IRC","====> return 0099999999");
                            return;
                        }
                        videoQuestionLiveEntity.setIsVoice(onlineTechObj.optString("isVoice"));
                        h5OnlineTechEntity.setMultiRolePlay(onlineTechObj.optString("multiRolePlay"));
                        h5OnlineTechEntity.setRoles(onlineTechObj.optString("roles"));
                        h5OnlineTechEntity.setTotalScore(onlineTechObj.optString("totalScore"));
                        h5OnlineTechEntity.setAnswer(onlineTechObj.optString("answer"));
                        videoQuestionLiveEntity.assess_ref = onlineTechObj.optString("assess_ref");
                        // 09.03 拼接和前端交互的URL
                        JSONArray jsonArray = onlineTechObj.optJSONArray("id");
                        String testIds = getIdStr(jsonArray);
                        videoQuestionLiveEntity.setUrl(buildCourseUrl(testIds));
//                        videoQuestionLiveEntity.setUrl("123");
                        JSONArray ids = onlineTechObj.getJSONArray("id");
                        String detail = ids.getString(0);
                        videoQuestionLiveEntity.id = detail;
                        videoQuestionLiveEntity.type = onlineTechObj.optString("questiontype");
                        videoQuestionLiveEntity.setOnlineTechEntity(h5OnlineTechEntity);
                    }
                }
                englishH5CoursewareBll.onH5Courseware(status, videoQuestionLiveEntity);
                Loger.e("Duncan", "======>EnglishH5CoursewareIRCBll:" + "H5语音答题");
            } else {
                LiveVideoConfig.isNewArts = false;
                if (englishH5CoursewareBll != null && jsonObject.has("H5_Courseware")) {
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                    JSONObject h5_Experiment = jsonObject.getJSONObject("H5_Courseware");
                    String play_url = "";
                    String status = h5_Experiment.optString("status", "off");
                    String id = "";
                    String courseware_type = "";
                    if ("on".equals(status)) {
                        englishH5Entity.setNewEnglishH5(false);
                        LiveVideoConfig.isNewEnglishH5 = false;
                        LiveVideoConfig.isSend = false;
                        id = h5_Experiment.getString("id");
                        courseware_type = h5_Experiment.getString("courseware_type");
                        play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveBll
                                .getStuCouId() + "/" + id +
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
                    } else {
                        englishH5Entity.setNewEnglishH5(false);
                        LiveVideoConfig.isNewEnglishH5 = false;
                        if (englishH5CoursewareBll != null) {
                            JSONObject object = jsonObject.optJSONObject("platformTest");
                            if (object != null && !object.toString().equals("{}")) {
                                englishH5Entity.setNewEnglishH5(true);
                                LiveVideoConfig.isNewEnglishH5 = true;
                                LiveVideoConfig.isSend = true;
                                status = LiveVideoConfig.isSend ? "on" : "off";
                                String nonce = object.optString("nonce");
                                LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                                String teamId = studentLiveInfo.getTeamId();
                                String classId = studentLiveInfo.getClassId();
//                            EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                                englishH5Entity.setNewEnglishH5(true);
                                try {
                                    JSONObject objects = new JSONObject();
                                    objects.put("packageId", object.getString("pId"));
                                    englishH5Entity.setPackageId(object.getString("pId"));
                                    objects.put("packageSource", object.getString("pSrc"));
                                    englishH5Entity.setPackageSource(object.getString("pSrc"));
                                    objects.put("packageAttr", object.getString("pAttr"));
                                    englishH5Entity.setPackageAttr(object.getString("pAttr"));
                                    objects.put("releasedPageInfos", object.getString("tests"));
                                    englishH5Entity.setReleasedPageInfos(object.getString("tests"));
                                    objects.put("teamId", teamId);
                                    englishH5Entity.setTeamId(teamId);
                                    objects.put("stuCouId", mLiveBll.getStuCouId());
                                    englishH5Entity.setStuCouId(mLiveBll.getStuCouId());
                                    objects.put("stuId", mGetInfo.getStuId());
                                    englishH5Entity.setStuId(mGetInfo.getStuId());
                                    objects.put("classId", classId);
                                    englishH5Entity.setClassId(classId);
                                    objects.put("classTestId", object.getString("ctId"));
                                    mShareDataManager.put(LiveVideoConfig.newEnglishH5, objects.toString(),
                                            ShareDataManager.SHAREDATA_USER);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
//                            englishH5Entity.setNewEnglishH5(true);
//                            LiveVideoConfig.isNewEnglishH5 = true;
                                LiveVideoConfig.isSend = false;
                            }
                        }
                    }
                    englishH5CoursewareBll.onH5Courseware(status, videoQuestionLiveEntity);
                }
            }
        } catch (Exception e) {
            Loger.e("Duncan", "======>EnglishH5CoursewareIRCBlle:" + e.toString());
        }
    }

    /**
     * 是否是新文科 课件平台
     *
     * @param jsonObject
     * @return
     */
    private boolean isNewArtsH5Courseware(JSONObject jsonObject) {
        return jsonObject.has("coursewareH5") || jsonObject.has("coursewareOnlineTech");
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


    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        Loger.e(Tag, "=======>onNotice:" + type + ":" + object);
        switch (type) {
            case XESCODE.ENGLISH_H5_COURSEWARE:
                LiveVideoConfig.isNewEnglishH5 = false;
                try {
                    if (englishH5CoursewareBll != null) {
                        VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                        EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                        englishH5Entity.setNewEnglishH5(false);
                        String play_url = "";
                        String status = object.optString("status", "off");
                        String nonce = object.optString("nonce");
                        String id = "";
                        String courseware_type = "";
                        if ("on".equals(status)) {
                            id = object.getString("id");
                            courseware_type = object.getString("courseware_type");
                            play_url = mLiveBll.getLiveVideoSAConfig().inner.coursewareH5 + mLiveId + "/" + mLiveBll
                                    .getStuCouId() + "/"
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
                            if (englishH5CoursewareBll instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareBll).setWebViewCloseByTeacher
                                        (false);
                            }
                        } else {
                            if (mAnswerRankBll != null) {
                                mAnswerRankBll.setNonce(object.optString("nonce"));
                            }
                            if (englishH5CoursewareBll instanceof EnglishH5CoursewareBll) {
                                ((EnglishH5CoursewareBll) englishH5CoursewareBll).setWebViewCloseByTeacher(true);
                            }
                        }
                        englishH5CoursewareBll.onH5Courseware(status, videoQuestionLiveEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case XESCODE.MULTIPLE_H5_COURSEWARE:
                LiveVideoConfig.isNewEnglishH5 = true;
                if (englishH5CoursewareBll != null) {
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    LiveVideoConfig.isSend = object.optBoolean("open");
//                            String status = "";
                    String status = object.optString("status", "off");
                    String nonce = object.optString("nonce");
                    LiveVideoConfig.nonce = nonce;
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                    String teamId = studentLiveInfo.getTeamId();
                    String classId = studentLiveInfo.getClassId();
                    EnglishH5Entity englishH5Entity = videoQuestionLiveEntity.englishH5Entity;
                    englishH5Entity.setNewEnglishH5(true);
                    try {
                        JSONObject objects = new JSONObject();
                        objects.put("packageId", object.getString("pId"));
                        englishH5Entity.setPackageId(object.getString("pId"));
                        objects.put("packageSource", object.getString("pSrc"));
                        LiveVideoConfig.pSrc = object.getString("pSrc");
                        objects.put("packageAttr", object.getString("pAttr"));
                        objects.put("releasedPageInfos", object.getString("tests"));
                        LiveVideoConfig.tests = object.getString("tests");
                        englishH5Entity.setReleasedPageInfos(object.getString("tests"));
                        objects.put("teamId", teamId);
                        objects.put("stuCouId", mLiveBll.getStuCouId());
                        objects.put("stuId", mGetInfo.getStuId());
                        objects.put("classId", classId);
                        objects.put("classTestId", object.getString("ctId"));
                        LiveVideoConfig.ctId = object.getString("ctId");
                        mShareDataManager.put(LiveVideoConfig.newEnglishH5, objects.toString(),
                                ShareDataManager.SHAREDATA_USER);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Loger.e("TeamPkBll", "======>888888" + "LiveVideoConfig.isSend:" + LiveVideoConfig.isSend + ":" +
                            mTeamPKBll);
                    // 08.07  课件之前的功能添加
                    if (!LiveVideoConfig.isSend) {
                        if (englishH5CoursewareBll instanceof EnglishH5CoursewareBll) {
                            ((EnglishH5CoursewareBll) englishH5CoursewareBll).setWebViewCloseByTeacher(true);
                        }
                    }
                    englishH5CoursewareBll.onH5Courseware(status, videoQuestionLiveEntity);
                }
                break;

            case XESCODE.ARTS_H5_COURSEWARE: {
                Loger.e(Tag, "===========>ARTS_H5_COURSEWARE");
                VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                videoQuestionLiveEntity.setNewArtsCourseware(true);
                String status = object.optString("status", "off");
                if ("on".equals(status)) {
                    videoQuestionLiveEntity.gold = object.optDouble("gold");
                    videoQuestionLiveEntity.type = object.optString("ptype");
                    videoQuestionLiveEntity.package_socurce = object.optInt("package_socurce");
                    String id = object.optString("id", "");
                    videoQuestionLiveEntity.id = id;
                    videoQuestionLiveEntity.nonce = object.optString("nonce");
                    JSONArray jsonArray = object.optJSONArray("id");
                    String testIds = getIdStr(jsonArray);
                    videoQuestionLiveEntity.setUrl(buildCourseUrl(testIds));
                    String isVoice = object.optString("isVoice");
                    videoQuestionLiveEntity.setIsVoice(isVoice);
                    videoQuestionLiveEntity.type = object.optString("questiontype");
                    if ("1".equals(isVoice)) {
                        videoQuestionLiveEntity.questiontype = object.optString("questiontype");
                        videoQuestionLiveEntity.assess_ref = object.optString("assess_ref");
                    }
                } else {
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.setNonce(object.optString("nonce"));
                    }
                }
                englishH5CoursewareBll.onH5Courseware(status, videoQuestionLiveEntity);
                break;
            }
            default:
                break;
        }
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
                .append("&cookie=").append(AppBll.getInstance().getUserToken())
                .append("&stuClientPath=").append(falseStr)
                .append("&fontDir=").append(falseStr);
        return sb.toString();
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.ENGLISH_H5_COURSEWARE,
                XESCODE.MULTIPLE_H5_COURSEWARE,
                XESCODE.ARTS_H5_COURSEWARE,
        };
    }

    class EnglishH5CoursewareImpl implements EnglishH5CoursewareHttp {

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
                    sendNotice(jsonObject, mLiveBll.getMainTeacherStr());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void getTestAnswerTeamStatus(VideoQuestionLiveEntity videoQuestionLiveEntity, final
        AbstractBusinessDataCallBack callBack) {
            getHttpManager().getTestAnswerTeamStatus(videoQuestionLiveEntity.id, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    GoldTeamStatus entity = getHttpResponseParser().testAnswerTeamStatus(responseEntity, mGetInfo
                                    .getStuId(),
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
        public void liveSubmitTestH5Answer(final VideoQuestionLiveEntity videoQuestionLiveEntity, String mVSectionID,
                                           String testAnswer, String courseware_type, String isSubmit, double
                                                   voiceTime, boolean isRight, final QuestionSwitch
                .OnAnswerReslut onAnswerReslut) {
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
            if (LiveVideoConfig.isNewArts) {
                Loger.d("Duncan", "onPutQuestionResultNewArts3");
                getHttpManager().liveSubmitNewArtsH5Answer(videoQuestionLiveEntity.srcType,
                        videoQuestionLiveEntity.id, mLiveId, testAnswer, courseware_type, userMode, isSubmit, voiceTime,
                        isRight, new HttpCallBack() {
                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) {
                                Loger.d("Duncan", "onPutQuestionResultNewArts4");
                                mLogtf.d("liveSubmitTestH5Answer:onPmSuccess=" + responseEntity.getJsonObject()
                                        .toString() +
                                        "," + videoQuestionLiveEntity);
                                VideoResultEntity entity = getHttpResponseParser().parseNewArtsPlatformQuestionAnswer
                                        (responseEntity,
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
            } else {
                getHttpManager().liveSubmitTestH5Answer(enstuId, videoQuestionLiveEntity.srcType,
                        videoQuestionLiveEntity.id, mLiveId, testAnswer, courseware_type, userMode, isSubmit, voiceTime,
                        isRight, new
                                HttpCallBack() {

                                    @Override
                                    public void onPmSuccess(ResponseEntity responseEntity) {
                                        mLogtf.d("liveSubmitTestH5Answer:onPmSuccess=" + responseEntity.getJsonObject()
                                                .toString() +
                                                "," + videoQuestionLiveEntity);
                                        VideoResultEntity entity = getHttpResponseParser().parseQuestionAnswer
                                                (responseEntity,
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
                                        mLogtf.d("liveSubmitTestH5Answer:onPmError=" + responseEntity.getErrorMsg() +
                                                "," +
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArtsResultCmplShow(AnswerResultCplShowEvent event) {
        if (englishH5CoursewareBll != null) {
            englishH5CoursewareBll.froceClose();
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (englishH5CoursewareBll != null) {
            englishH5CoursewareBll.destroy();
        }
        if (englishH5Cache != null) {
            englishH5Cache.stop();
        }
        EventBus.getDefault().unregister(this);
    }

}
