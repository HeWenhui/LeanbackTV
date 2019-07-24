package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;
import android.text.TextUtils;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.EnglishPk;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AddPersonAndTeamEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.DeviceDetectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.EvaluateContent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HalfBodyLiveStudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.FollowTypeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.TestInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.TopicEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MyRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkStar;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkStuProgress;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListStudentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListTeamEntity;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LiveHttpResponseParser extends HttpResponseParser {
    static String TAG = "LiveHttpResponseParser";
    Context mContext;

    public LiveHttpResponseParser(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 解析getInfo 理科
     *
     * @param data
     * @param liveTopic
     * @param getInfo
     */
    public void parseLiveGetInfoScience(JSONObject data, LiveTopic liveTopic, LiveGetInfo getInfo) {

        getInfo.setEducationStage(data.optString("educationStage", "0"));
        getInfo.setGrade(Integer.parseInt(data.optString("gradeIds").split(",")[0]));
        int isPrimarySchool = data.optInt("isPrimarySchool");
        if (1 == isPrimarySchool) {
            LiveVideoConfig.isPrimary = true;
        } else {
            LiveVideoConfig.isPrimary = false;
        }
        //小英萌萌哒皮肤专用
        if (data.has("useSkin")) {
            getInfo.setSmallEnglish((String.valueOf(data.optString("useSkin"))).equals("1"));
            getInfo.setUseSkin(data.optInt("useSkin", 0));
            LiveVideoConfig.isSmallChinese = String.valueOf(data.optString("useSkin")).equals("2");
        } else {
            getInfo.setSmallEnglish(false);
            LiveVideoConfig.isSmallChinese = false;
        }
        getInfo.setIsPrimarySchool(isPrimarySchool);
        getInfo.setIsYouJiao(data.optInt("isYouJiao"));
        LiveVideoConfig.isScience = true;
        getInfo.setAllowSnapshot(data.optInt("allowSnapshot"));
        LiveVideoConfig.educationstage = getInfo.getEducationStage();
        LiveVideoConfig.LIVEMULPRELOAD = data.optString("courseWarePreLoadUrl");
        LiveVideoConfig.LIVEMULH5URL = data.optString("getCourseWareHtml");
        getInfo.setStuPutUpHandsNum(data.optInt("stuPutUpHandsNum"));
        getInfo.setAllowLinkMicNew(data.optInt("allowLinkMicNew"));
        getInfo.setGetCourseWareHtmlNew(data.optString("getCourseWareHtmlNew"));
        getInfo.setGetCourseWareHtmlZhongXueUrl(data.optString("getCourseWareHtmlZhongXueUrl"));
        // TODO 理科小学
//        getInfo.setScienceStaticConfig(parseScienceStaticConfig(data));
        if (getInfo.getAllowLinkMicNew() == 1) {
            getInfo.setAllowLinkMic(false);
        }
        if (data.has("ePlanInfo")) {
            try {
                JSONObject ePlanInfo = data.getJSONObject("ePlanInfo");
                getInfo.ePlanInfo = new LiveGetInfo.EPlanInfoBean();
                getInfo.ePlanInfo.ePlanId = ePlanInfo.optString("ePlanId");
                getInfo.ePlanInfo.eTeacherId = ePlanInfo.optString("eTeacherId");
                getInfo.ePlanInfo.eClassId = ePlanInfo.optString("eClassId");
                if (ePlanInfo.has("fakePlanId")) {
                    getInfo.ePlanInfo.fakePlanId = ePlanInfo.optString("fakePlanId");
                }
            } catch (JSONException e) {
                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo.ePlanInfo", e.getMessage());
            }
        }
    }

    @Deprecated
//    public static ScienceStaticConfig parseScienceStaticConfig(JSONObject data) {
//        ScienceStaticConfig scienceStaticConfig = null;
//        if (data.has("science_static_config")) {
//            JSONObject science_static_config = data.optJSONObject("science_static_config");
//            if (science_static_config != null) {
//                scienceStaticConfig = new ScienceStaticConfig();
//                HashMap<String, ScienceStaticConfig.Version> stringVersionHashMap = scienceStaticConfig.stringVersionHashMap;
//                Iterator<String> keys = science_static_config.keys();
//                if (keys.hasNext()) {
//                    String key = keys.next();
//                    ScienceStaticConfig.Version version = new ScienceStaticConfig.Version();
//                    version.version = key;
//                    try {
//                        JSONObject versionObj = science_static_config.getJSONObject(key);
//                        int canUseLocal = versionObj.getInt("canUseLocal");
//                        if (canUseLocal == 1) {
//                            version.url = versionObj.getString("url");
//                            version.templateURL = versionObj.getString("templateURL");
//                            version.tarballURL = versionObj.getString("tarballURL");
//                            version.assetsHash = versionObj.getString("assetsHash");
//                            version.templateForLocalURL = versionObj.getString("templateForLocalURL");
//                            stringVersionHashMap.put(key, version);
//                        }
//                    } catch (JSONException e) {
//                        MobAgent.httpResponseParserError(TAG, "parseScienceStaticConfig", e.getMessage());
//                    }
//                }
//            }
//        }
//        return scienceStaticConfig;
//    }

    /**
     * 解析getInfo 文科
     *
     * @param data
     * @param liveTopic
     * @param getInfo
     */
    public void parseLiveGetInfoChinese(JSONObject data, LiveTopic liveTopic, LiveGetInfo getInfo) {
        getInfo.setEducationStage(data.optString("educationStage", "0"));
        try {
            getInfo.setGrade(Integer.parseInt(data.optString("gradeIds").split(",")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data.has("isAllowTeamPk")) {
            getInfo.setIsAllowTeamPk(data.optString("isAllowTeamPkNew", "0"));
        }

//            LiveVideoConfig.isPrimary = true;
//        } else {
        LiveVideoConfig.isPrimary = false;
//        }
        LiveVideoConfig.isScience = false;
        //小英萌萌哒皮肤专用
        if (data.has("useSkin")) {
            getInfo.setSmallEnglish((String.valueOf(data.optString("useSkin"))).equals("1"));
            getInfo.setUseSkin(data.optInt("useSkin", 0));
            LiveVideoConfig.isSmallChinese = String.valueOf(data.optString("useSkin")).equals("2");
        } else {
            getInfo.setSmallEnglish(false);
            LiveVideoConfig.isSmallChinese = false;
        }
        getInfo.setAllowSnapshot(data.optInt("allowSnapshot"));
        LiveVideoConfig.educationstage = getInfo.getEducationStage();
        LiveVideoConfig.LIVEMULPRELOADCHS = data.optString("courseWarePreLoadUrl");
        LiveVideoConfig.LIVEMULH5URLCHS = data.optString("getCourseWareHtml");
        getInfo.setGetCourseWareHtmlNew(data.optString("getCourseWareWeb"));
//        LiveVideoConfig.LIVEMULPRELOAD = data.optString("courseWarePreLoadUrl");
//        LiveVideoConfig.LIVEMULH5URL = data.optString("getCourseWareHtml");
//        getInfo.setStuPutUpHandsNum(data.optInt("stuPutUpHandsNum"));
//        getInfo.setAllowLinkMicNew(data.optInt("allowLinkMicNew"));
//        if (getInfo.getAllowLinkMicNew() == 1) {
//            getInfo.setAllowLinkMic(false);
//        }
    }

    /**
     * 解析getInfo 文科
     *
     * @param data
     * @param liveTopic
     * @param getInfo
     */
    public void parseLiveGetInfoLibarts(JSONObject data, LiveTopic liveTopic, LiveGetInfo getInfo) {
        getInfo.setAppid(data.optString("appId"));
        // 文科表扬榜
        if (data.has("liveRank")) {
            JSONObject jsonObject = data.optJSONObject("liveRank");
            if (jsonObject != null) {
                int showRank = jsonObject.optInt("showRankNum");
                getInfo.setShowArtsPraise(showRank);
            }
        }
        //小英萌萌哒皮肤专用
        if (data.has("useSkin")) {
            getInfo.setSmallEnglish((String.valueOf(data.optString("useSkin"))).equals("1"));
            getInfo.setUseSkin(data.optInt("useSkin", 0));
            LiveVideoConfig.isSmallChinese = String.valueOf(data.optString("useSkin")).equals("2");
        } else {
            getInfo.setSmallEnglish(false);
            LiveVideoConfig.isSmallChinese = false;
        }
        //英语小目标
        LiveGetInfo.BetterMe betterMe = getInfo.getBetterMe();
        betterMe.setArriveLate("1".equals(data.optString("isArriveLate")));
        betterMe.setUseBetterMe("1".equals(data.optString("isUseBetterMe")));

        JSONObject englishPkObj = data.optJSONObject("englishPk");
        if (englishPkObj != null) {
            LiveGetInfo.EnglishPk englishPk = getInfo.getEnglishPk();
            englishPk.canUsePK = englishPkObj.optInt("canUsePK");
            englishPk.historyScore = englishPkObj.optInt("historyScore");
            englishPk.isTwoLose = englishPkObj.optInt("isTwoLose");
            int hasGroup = englishPkObj.optInt("hasGroup");
            if (EnglishPk.HAS_GROUP_MAIN == hasGroup) {
                if (LiveTopic.MODE_CLASS.equals(getInfo.getMode())) {
                    englishPk.hasGroup = hasGroup;
                } else {
                    englishPk.hasGroup = EnglishPk.HAS_GROUP_TRAN;
                }
            }
        }
        JSONObject pkEnergyObj = data.optJSONObject("pkEnergy");
        if (pkEnergyObj != null) {
            LiveGetInfo.EnPkEnergy enpkEnergy = getInfo.getEnpkEnergy();
            enpkEnergy.me = pkEnergyObj.optInt("me");
            enpkEnergy.myTeam = pkEnergyObj.optInt("myTeam");
            enpkEnergy.opTeam = pkEnergyObj.optInt("opTeam");
        }
    }

    /**
     * 解析getInfo之前，先把之前用来判断状态的静态变量置空，以免上一次的状态影响这一次
     */
    private void setStaticStatusNull() {
        //小学语文MMD皮肤
        LiveVideoConfig.isSmallChinese = false;
        LiveVideoConfig.isPrimary = false;
        LiveVideoConfig.isScience = false;
        LiveVideoConfig.isMulLiveBack = false;
    }

    /**
     * 解析getInfo
     */
    public LiveGetInfo parseLiveGetInfo(JSONObject data, LiveTopic liveTopic, int liveType, int from) {
        try {
            LiveGetInfo getInfo = new LiveGetInfo(liveTopic);

            VideoConfigEntity videoConfigEntity = new VideoConfigEntity();

            videoConfigEntity.setDuration(data.optLong("duration"));
            videoConfigEntity.setWaterMark(data.optLong("waterMark"));
            getInfo.setVideoConfigEntity(videoConfigEntity);
//            MediaPlayer.getIsNewIJK() = "1".equals(data.optString("isNewSDK")) && "1".equals(data.optString("isNewIRC"));
//            MediaPlayer.getIsNewIJK() = true;
            MediaPlayer.setIsNewIJK(true);
            //解析getInfo之前，先把之前用来判断状态的静态变量置空
            setStaticStatusNull();
            getInfo.setId(data.getString("id"));
            getInfo.setIs_show_ranks(data.optString("is_show_ranks"));
            //getInfo.setIs_show_ranks("1");
            getInfo.setName(data.getString("name"));
            getInfo.setEn_name(data.optString("en_name"));
            getInfo.setNickname(data.optString("nickname"));
            getInfo.setInstructions(data.getString("instructions"));
            getInfo.setNotice(data.getString("notice"));
            getInfo.setLiveType(data.getInt("liveType"));
            getInfo.setLiveTime(data.getString("liveTime"));
            getInfo.setsTime(data.optLong("stime"));
            getInfo.seteTime(data.optLong("etime"));
            getInfo.setNowTime(data.getDouble("nowTime"));
            //getInfo.setIsShowMarkPoint(data.optString("isAllowMarkpoint"));\
            if (data.has("isAllowTeamPk")) {
                getInfo.setIsAllowTeamPk(data.getString("isAllowTeamPk"));
            }
            // 解析幼教字段
            getInfo.setPreschool(data.optInt("isYouJiao") == 1);

            getInfo.setIsShowMarkPoint(data.optString("isAllowMarkpoint"));
            getInfo.setIsAIPartner(data.optInt("isAIPartner"));

            //连对激励
            getInfo.setIsOpenNewCourseWare(data.optInt("isOpenNewCourseWare"));
//            getInfo.setIsOpenNewCourseWare(1);
            getInfo.setGetJournalUrl(data.optString("getJournalUrl", "https://live.xueersi.com/science/Stimulation/getJournal"));
            getInfo.setGetEvenPairListUrl(data.optString("getEvenPairListUrl", "https://live.xueersi.com/science/Stimulation/evenPairList"));
            getInfo.setGetThumbsUpUrl(data.optString("getThumbsUpUrl", "https://live.xueersi.com/science/Stimulation/thumbsUp"));
            //getInfo.setIsShowMarkPoint("0");
            getInfo.setIsShowCounselorWhisper(data.optString("counselor_whisper"));
            getInfo.setIsSeniorOfHighSchool(data.optInt("isSeniorOfHighSchool"));
            getInfo.setIsVoiceInteraction(data.optInt("isVoiceInteraction"));

            //幼升小金话筒

            //getInfo.setIsShowCounselorWhisper("1");
            if (data.has("followType")) {
                JSONObject followType = data.getJSONObject("followType");
                FollowTypeEntity followTypeEntity = new FollowTypeEntity();
                followTypeEntity.setInt2(followType.getInt("2"));
                followTypeEntity.setInt3(followType.getInt("3"));
                followTypeEntity.setInt4(followType.getInt("4"));
            }

            if (data.has("highFollowType")) {
                JSONArray jsonArray = data.optJSONArray("highFollowType");
                for (int i = 0; i < jsonArray.length(); i++) {
                    getInfo.getPraiseGift().add(jsonArray.optInt(i));
                }
            }
            if (data.has("highLiveFlowerRate")) {
                JSONArray jsonArray = data.optJSONArray("highLiveFlowerRate");
                for (int i = 0; i < jsonArray.length(); i++) {
                    getInfo.getPraiseGiftRate().add(jsonArray.optDouble(i));
                }
            }
            getInfo.setPraiseAutoBarrageTime(data.optInt("praiseAutoBarrageTime", 1));
            getInfo.setPraiseAutoCutTime(data.optInt("praiseAutoCutTime", 5));

            getInfo.setTeacherId(data.getString("teacherId"));
            getInfo.setTeacherName(data.getString("teacherName"));
            getInfo.setTeacherIMG(data.optString("teacherImg"));
            getInfo.setMainTeacherId(data.optString("mainTeacherId"));
            if (data.has("mainTeacherInfos")) {
                try {
                    JSONObject mainTeacherInfos = data.getJSONObject("mainTeacherInfos");
                    LiveGetInfo.MainTeacherInfo mainTeacherInfo = getInfo.getMainTeacherInfo();
                    mainTeacherInfo.setTeacherId(mainTeacherInfos.optString("teacherId"));
                    mainTeacherInfo.setTeacherImg(mainTeacherInfos.optString("teacherImg"));
                    mainTeacherInfo.setTeacherName(mainTeacherInfos.optString("teacherName"));
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo.mainTeacherInfos", e.getMessage());
                }
            }
            if (data.has("testInfo")) {
                JSONArray testInfoArray = data.getJSONArray("testInfo");
                for (int i = 0; i < testInfoArray.length(); i++) {
                    JSONObject testInfoJobj = testInfoArray.getJSONObject(i);
                    TestInfoEntity info = new TestInfoEntity();
                    info.id = testInfoJobj.getString("id");
                    info.type = testInfoJobj.getString("type");
                    info.content = testInfoJobj.getString("content");
                    info.audio = testInfoJobj.getString("audio");
                    info.num = testInfoJobj.getInt("num");
                    getInfo.getTestInfo().add(info);
                }
            }
            getInfo.setStuId(data.getString("stuId"));
            getInfo.setUname(data.getString("uname"));
            getInfo.setStuName(data.getString("stuName"));
            getInfo.setStuSex(data.getString("stuSex"));
            getInfo.setStuImg(data.optString("stuImg"));
            if (data.has("studentLiveInfo")) {
                StudentLiveInfoEntity studentLiveInfoEntity = new StudentLiveInfoEntity();
                JSONObject studentLiveInfo = data.getJSONObject("studentLiveInfo");
                studentLiveInfoEntity.setCourseId(studentLiveInfo.optString("courseId"));
                studentLiveInfoEntity.setGroupId(studentLiveInfo.getString("groupId"));
                studentLiveInfoEntity.setClassId(studentLiveInfo.getString("classId"));
                boolean isExpe = "-1".equals(studentLiveInfoEntity.getClassId());
                if (isExpe) {
                    if (from != LiveVideoBusinessConfig.ENTER_FROM_1 && from != LiveVideoBusinessConfig.ENTER_FROM_2) {
                        studentLiveInfoEntity.setExpe(true);
                    }
                    XesMobAgent.liveExpe(from, getInfo.getId());
                }
                studentLiveInfoEntity.setGoldNum(studentLiveInfo.optInt("goldNum"));
                studentLiveInfoEntity.setShutupStatus(studentLiveInfo.getString("shutupStatus"));
                studentLiveInfoEntity.setEvaluateStatus(studentLiveInfo.optInt("evaluateStatus", 0));
                studentLiveInfoEntity.setSignStatus(studentLiveInfo.optInt("signStatus", 0));
                studentLiveInfoEntity.setTeamId(studentLiveInfo.optString("teamId", ""));
                studentLiveInfoEntity.setBuyCourseUrl(studentLiveInfo.optString("buyCourseUrl"));
                studentLiveInfoEntity.setUserModeTotalTime(studentLiveInfo.optLong("userModeTotalTime", 1800));
                studentLiveInfoEntity.setUserModeTime(studentLiveInfo.optLong("userModeTime", 1800));
                studentLiveInfoEntity.setLearning_stage(studentLiveInfo.optString("learning_stage", "-1"));
                getInfo.setStudentLiveInfo(studentLiveInfoEntity);
                int mode = studentLiveInfo.optInt("mode", 0);
                liveTopic.setMode(mode == 0 ? LiveTopic.MODE_TRANING : LiveTopic.MODE_CLASS);
                getInfo.setMode(liveTopic.getMode());
            }
            if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                JSONArray teamStuIdArray = data.optJSONArray("teamStuIds");
                if (teamStuIdArray != null) {
                    try {
                        ArrayList<String> teamStuIds = getInfo.getTeamStuIds();
                        for (int i = 0; i < teamStuIdArray.length(); i++) {
                            teamStuIds.add(teamStuIdArray.getString(i));
                        }
                        logger.d("parseLiveGetInfo:teamStuIds=" + teamStuIds.size());
                    } catch (Exception e) {
                        MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo.teamStuIds", e.getMessage());
                    }
                }

                getInfo.setIsArts(data.optInt("isArts", 0));//文科可以星星互动
                getInfo.setIsEnglish(data.optInt("isEnglish", 0));
                getInfo.setIsAllowStar(data.optInt("isAllowStar", 0));//是不是可以星星互动
                if (getInfo.getIsAllowStar() == 1) {
                    if (data.has("stuStarInfo")) {
                        JSONObject starCountObj = data.getJSONObject("stuStarInfo");
                        getInfo.setStarCount(starCountObj.optInt("stuStarAmount", 0));
                    }
                    if (data.has("stuGoldInfo")) {
                        JSONObject goldCountObj = data.getJSONObject("stuGoldInfo");
                        getInfo.setGoldCount(goldCountObj.optInt("goldAmount", 0));
                    }
                }
                LiveGetInfo.EvaluateTeacherEntity evaluateTeacherEntity = new LiveGetInfo.EvaluateTeacherEntity();
                evaluateTeacherEntity.setEvaluateIsOpen(data.optInt("evaluateIsOpen", 0) == 1 ? true : false);
                evaluateTeacherEntity.setEvaluateTime(data.optLong("evaluateTime", 0));
                getInfo.setEvaluateTeacherEntity(evaluateTeacherEntity);
            }
            getInfo.setStat(data.getInt("stat"));
            getInfo.setRtmpUrl(data.getString("rtmpUrl"));
            JSONArray rtmpUrlArray = data.optJSONArray("rtmpUrls");
            if (rtmpUrlArray != null) {
                String[] rtmpUrls = new String[rtmpUrlArray.length()];
                for (int i = 0; i < rtmpUrlArray.length(); i++) {
                    rtmpUrls[i] = rtmpUrlArray.getString(i);
                }
                getInfo.setRtmpUrls(rtmpUrls);
            }
            getInfo.setRoomId(data.optString("roomId"));
            ArrayList<TalkConfHost> newTalkConfHosts = new ArrayList<>();
            if (data.has("httpsLiveChatDispatchUrl")) {
                JSONArray array = data.optJSONArray("httpsLiveChatDispatchUrl");
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        String host = array.getString(i);
                        TalkConfHost talkConfHost = new TalkConfHost();
                        talkConfHost.setHost(host);
                        newTalkConfHosts.add(talkConfHost);
                    }
                }
            }
            getInfo.setNewTalkConfHosts(newTalkConfHosts);
            getInfo.setHbTime(data.getInt("hbTime"));
            getInfo.setClientLog(data.optString("clientLog", LiveVideoConfig.URL_LIVE_ON_LOAD_LOGS));
            getInfo.setGslbServerUrl(data.getString("gslbServerUrl"));
            getInfo.setLogServerUrl(data.optString("logServerUrl"));
            List<String> headImgUrl = new ArrayList<String>();
            if (data.has("headImgUrl")) {
                Object result = data.get("headImgUrl");
                if (result instanceof JSONArray) {
                    JSONArray headImgUrlArray = (JSONArray) result;
                    for (int i = 0; i < headImgUrlArray.length(); i++) {
                        headImgUrl.add(headImgUrlArray.getString(i));
                    }
                }
            }
            logger.i("parseLiveGetInfo:headImgUrl=" + headImgUrl.size());
            getInfo.setHeadImgUrl(headImgUrl);
            try {
                getInfo.setHeadImgPath(data.optString("headImgPath"));
                getInfo.setImgSizeType(data.optString("imgSizeType"));
                getInfo.setHeadImgVersion(data.optString("headImgVersion"));
            } catch (Exception e) {
                logger.e("parseLiveGetInfo.Head", e);
                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo.Head", e.getMessage());
            }
            getInfo.setCloseChat(data.optInt("isCloseChat", 0) == 1);
            if (data.has("skeyPlayT")) {
                getInfo.setSkeyPlayT(data.getString("skeyPlayT"));
            } else {
//                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", "skeyPlayT=null");
            }
            if (data.has("skeyPlayF")) {
                getInfo.setSkeyPlayF(data.getString("skeyPlayF"));
            } else {
//                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", "skeyPlayF=null");
            }
            getInfo.setSpeechEvalUrl(data.optString("speechEvalUrl", "https://live.xueersi.com/Live/speechEval/"));
            getInfo.setUrlClick(data.optInt("urlClick", 0));
            getInfo.setAllowLinkMic(data.optInt("allowLinkMic", 1) == 1);
            getInfo.setStuLinkMicNum(data.optInt("stuLinkMicNum", 0));
            getInfo.setTestPaperUrl(data.optString("testPaperUrl", LiveVideoConfig.URL_LIVE_MULTI_TEST));
            getInfo.setBlockChinese(data.optInt("blockChinese", 0) == 1);
            getInfo.setSubjectiveTestAnswerResult(data.optString("getSubjectiveTestResultUrl", "https://live.xueersi" +
                    ".com/Live/subjectiveTestAnswerResult/" + getInfo.getId()));
            LiveGetInfo.TotalOpeningLength totalOpeningLength = new LiveGetInfo.TotalOpeningLength();
            Object getTotalOpeningLengthObj = data.opt("getTotalOpeningLength");
            if (getTotalOpeningLengthObj instanceof JSONObject) {
                JSONObject getTotalOpeningLength = (JSONObject) getTotalOpeningLengthObj;
                totalOpeningLength.duration = getTotalOpeningLength.optDouble("duration", 0);
                totalOpeningLength.speakingLen = getTotalOpeningLength.optString("speaking_len");
                totalOpeningLength.speakingNum = getTotalOpeningLength.optInt("speaking_num", 0);
            }
            getInfo.setTotalOpeningLength(totalOpeningLength);
            getInfo.setPattern(data.optInt("pattern", 1));
            getInfo.setRequestTime(data.optString("requestTime"));
            //解析学科id
            if (data.has("subject_ids")) {
                String strSubjIds = data.getString("subject_ids");
                String[] arrSubjIds = strSubjIds.split(",");
                getInfo.setSubjectIds(arrSubjIds);
            }
            //金话筒
            getInfo.setUseGoldMicroPhone(data.optInt("isGoldMicrophone"));
            //超级演讲秀  注意空格
            int isSuperSpeechShow = data.optInt("isSuperSpeechShow");
            int _isSuperSpeechShow = data.optInt("isSuperSpeechShow ");
            getInfo.setUseSuperSpeakerShow(isSuperSpeechShow == 0 ? _isSuperSpeechShow : isSuperSpeechShow);
            getInfo.setSubject_digits(data.optString("subject_digits"));
            if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                getInfo.setIsNewProject(data.optInt("isNewProject", 0));
                if (getInfo.getIsArts() == LiveVideoSAConfig.ART_EN) {
                    parseLiveGetInfoLibarts(data, liveTopic, getInfo);
                } else if (getInfo.getIsArts() == LiveVideoSAConfig.ART_CH) {
                    parseLiveGetInfoChinese(data, liveTopic, getInfo);
                } else {
                    parseLiveGetInfoScience(data, liveTopic, getInfo);
                }
            }
            getInfo.setSubjectiveItem2AIUrl(data.optString("subjectiveItem2AIUrl"));
            return getInfo;
        } catch (JSONException e) {
            logger.e("parseLiveGetInfo", e);
            MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", e.getMessage());
        }
        return null;
    }

    /**
     * 解析直播服务器
     */
    public PlayServerEntity parsePlayerServer(JSONObject object) {
        PlayServerEntity server = new PlayServerEntity();
        try {
            server.setCode(object.optInt("code"));
            if (server.getCode() == 200) {
                server.setAppname(object.getString("appname"));
                server.setCcode(object.optString("ccode"));
                server.setIcode(object.optString("icode"));
                server.setPcode(object.optString("pcode"));
                server.setRtmpkey(object.optString("rtmpkey"));
                server.setCipdispatch(object.optString("clientip"));
                JSONArray playserverArray = object.getJSONArray("playserver");
                List<PlayserverEntity> playserver = new ArrayList<PlayserverEntity>();
                for (int i = 0; i < playserverArray.length(); i++) {
                    PlayserverEntity entity = new PlayserverEntity();
                    entity.setServer(server);
                    object = playserverArray.getJSONObject(i);
                    entity.setAcode(object.getString("acode"));
                    entity.setAddress(object.getString("address"));
                    entity.setCcode(object.optString("ccode"));
                    entity.setGroup(object.optString("group"));
                    entity.setIcode(object.optString("icode"));
                    entity.setPcode(object.optString("pcode"));
                    entity.setPriority(object.optInt("priority"));
                    entity.setProvide(object.optString("provide"));
                    entity.setRtmpkey(object.optString("rtmpkey"));
                    entity.setHttpport(object.optString("httpport"));
                    entity.setFlvpostfix(object.optString("flvpostfix"));
                    entity.setIp_gslb_addr(object.optString("ip_gslb_addr"));
//                if (AppConfig.DEBUG && StringUtils.isEmpty(entity.getIp_gslb_addr())) {
//                    continue;
//                }
                    playserver.add(entity);
                }
                server.setPlayserver(playserver);
            }
            return server;
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parsePlayerServer", e.getMessage());
        }
        return null;
    }

    /**
     * 解析直播topic数据
     */
    public LiveTopic parseLiveTopic(LiveTopic oldLiveTopic, JSONObject liveTopicJson, int type) throws JSONException {
        LiveTopic liveTopic = new LiveTopic();
        if (type != LiveVideoConfig.LIVE_TYPE_LIVE) {
            liveTopic.setMode(LiveTopic.MODE_CLASS);
        }

        LiveTopic.TeamPkEntity teamPkEntity = new LiveTopic.TeamPkEntity();
        if (type == LiveVideoConfig.LIVE_TYPE_LIVE && liveTopicJson.has("room_2")) {
            JSONObject status = liveTopicJson.getJSONObject("room_2");
            RoomStatusEntity coachStatusEntity = liveTopic.getCoachRoomstatus();
            coachStatusEntity.setMode(status.getString("mode"));
            coachStatusEntity.setOpenchat(status.getBoolean("openchat"));
            coachStatusEntity.setCalling(status.getBoolean("isCalling"));
            coachStatusEntity.setListStatus(status.optInt("billList"));
            coachStatusEntity.setOpenlike(status.optBoolean("openlike"));

            if (status.has("openbarrage")) {
                logger.i("room2中有openbarrage字段 理科 status.getBoolean(\"openbarrage\") = " + status.getBoolean
                        ("openbarrage") + " " + status.toString());
                //新增字段，辅导老师开启礼物与否 true开启
                coachStatusEntity.setFDLKOpenbarrage(status.getBoolean("openbarrage"));

            } else {
                logger.i("room2中没有openbarrage字段 文科" + status.toString());
            }

            // 解析辅讲老师信息
            LiveTopic.TeamPkEntity.RoomInfo roomInfo2 = new LiveTopic.TeamPkEntity.RoomInfo();
            roomInfo2.setAlloteam(status.optInt("alloteam"));
            roomInfo2.setOpenbox(status.optInt("openbox"));
            roomInfo2.setAllotpkman(status.optInt("allotpkman"));
            roomInfo2.setPKStep(status.optInt("PKStep"));
            teamPkEntity.setRoomInfo2(roomInfo2);

            if (status.has("link_mic")) {
                logger.i("辅导老师 parseLiveTopic status = " + status.toString());
                JSONObject link_mic = status.getJSONObject("link_mic");
                coachStatusEntity.setOnmic(link_mic.optString("onmic", "off"));
                coachStatusEntity.setOpenhands(link_mic.optString("openhands", "off"));
                coachStatusEntity.setRoom(link_mic.optString("room"));

                ArrayList<ClassmateEntity> classmateEntities = coachStatusEntity.getClassmateEntities();
                classmateEntities.clear();
                JSONArray students = link_mic.optJSONArray("students");
                boolean classmateChange;
                if (oldLiveTopic.getCoachRoomstatus().getStudents() == null) {
                    classmateChange = true;
                } else {
                    if (students != null) {
                        classmateChange = !oldLiveTopic.getCoachRoomstatus().getStudents().equals(students);
                    } else {
                        classmateChange = true;
                    }
                }
                if (students != null) {
                    for (int i = 0; i < students.length(); i++) {
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        classmateEntity.setId(students.getString(i));
                        classmateEntities.add(classmateEntity);
                    }
                }
                coachStatusEntity.setStudents(students);
                coachStatusEntity.setClassmateChange(classmateChange);
            } else {
                coachStatusEntity.setOnmic("off");
                coachStatusEntity.setOpenhands("off");
                coachStatusEntity.getClassmateEntities().clear();
            }
            // 文科表扬榜 topic中相关信息
            try {
                if (status.has("openPraiseList")) {
                    JSONObject jsonObject = status.getJSONObject("openPraiseList");
                    LiveTopic.ArtsPraiseTopicEntity artsPraiseTopicEntity = new LiveTopic.ArtsPraiseTopicEntity();
                    artsPraiseTopicEntity.setId(jsonObject.optString("id", ""));
                    artsPraiseTopicEntity.setStastus(jsonObject.optBoolean("stastus", false));
                    artsPraiseTopicEntity.setRankType(jsonObject.optInt("rankType"));
                    liveTopic.setArtsPraiseTopicEntity(artsPraiseTopicEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (status.has("groupSpeech")) {
                JSONObject jsonObject = status.optJSONObject("groupSpeech");
                coachStatusEntity.setGroupSpeechRoom(jsonObject.optString("voiceId"));
                coachStatusEntity.setOnGroupSpeech(jsonObject.optString("onGroupSpeech"));
            }
        }
        if (liveTopicJson.has("room_1")) {
            logger.i("主讲老师 parseLiveTopic liveTopicJson = " + liveTopicJson.toString());
            JSONObject status = liveTopicJson.getJSONObject("room_1");
            RoomStatusEntity mainStatusEntity = liveTopic.getMainRoomstatus();
            mainStatusEntity.setOnbreak(status.optBoolean("isOnBreak"));
            mainStatusEntity.setId(status.getInt("id"));
            mainStatusEntity.setClassbegin(status.getBoolean("classbegin"));
            mainStatusEntity.setOpenbarrage(status.getBoolean("openbarrage"));
            liveTopic.getCoachRoomstatus().setZJLKOpenbarrage(status.getBoolean("openbarrage"));
            //一定不要忘记在topic返回的时候，room1里openbarrage字段的值设置到理科主讲实体中
            mainStatusEntity.setOpenchat(status.getBoolean("openchat"));
            mainStatusEntity.setOpenFeedback(status.optBoolean("isOpenFeedback"));
            mainStatusEntity.setOpenlike(status.optBoolean("openlike"));

            // 解析主讲老师信息
            LiveTopic.TeamPkEntity.RoomInfo roomInfo1 = new LiveTopic.TeamPkEntity.RoomInfo();
            roomInfo1.setAlloteam(status.optInt("alloteam"));
            roomInfo1.setOpenbox(status.optInt("openbox"));
            roomInfo1.setAllotpkman(status.optInt("allotpkman"));
            roomInfo1.setPKStep(status.optInt("PKStep"));

            teamPkEntity.setRoomInfo1(roomInfo1);


            if (status.has("exam")) {
                mainStatusEntity.setHaveExam(true);
                JSONObject jsonObject = status.getJSONObject("exam");
                mainStatusEntity.setExamStatus(jsonObject.optString("status", "off"));
                mainStatusEntity.setExamNum(jsonObject.optString("num", "-1"));
            } else {
                mainStatusEntity.setHaveExam(false);
            }
            if (status.has("groupSpeech")) {
                JSONObject jsonObject = status.optJSONObject("groupSpeech");
                mainStatusEntity.setGroupSpeechRoom(jsonObject.optString("voiceId"));
                mainStatusEntity.setOnGroupSpeech(jsonObject.optString("onGroupSpeech"));
            }

            if (status.has("vioceChat")) {
                JSONObject jsonObject = status.getJSONObject("vioceChat");
                mainStatusEntity.setAgoraVoiceChatRoom(jsonObject.optString("agoraVioceChatRoom"));
                mainStatusEntity.setOnVideoChat(jsonObject.optString("onVioceChat"));
            }
            if (status.has("link_mic")) {
                JSONObject link_mic = status.getJSONObject("link_mic");
                mainStatusEntity.setOnmic(link_mic.optString("onmic", "off"));
                mainStatusEntity.setOpenhands(link_mic.optString("openhands", "off"));
                mainStatusEntity.setRoom(link_mic.optString("room"));
                ArrayList<ClassmateEntity> classmateEntities = mainStatusEntity.getClassmateEntities();
                classmateEntities.clear();
                JSONArray students = link_mic.optJSONArray("students");
                boolean classmateChange;
                if (oldLiveTopic.getMainRoomstatus().getStudents() == null) {
                    classmateChange = true;
                } else {
                    if (students != null) {
                        classmateChange = !oldLiveTopic.getMainRoomstatus().getStudents().equals(students);
                    } else {
                        classmateChange = true;
                    }
                }
                if (students != null) {
                    for (int i = 0; i < students.length(); i++) {
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        classmateEntity.setId(students.getString(i));
                        classmateEntities.add(classmateEntity);
                    }
                }
                mainStatusEntity.setStudents(students);
                mainStatusEntity.setClassmateChange(classmateChange);
            } else {
                mainStatusEntity.setOnmic("off");
                mainStatusEntity.setOpenhands("off");
                mainStatusEntity.getClassmateEntities().clear();
            }
            mainStatusEntity.setOpenDbEnergy(status.optBoolean("openDbEnergy", false));
            mainStatusEntity.setOpenVoiceBarrage(status.optBoolean("openVoiceBarrage", false));
            mainStatusEntity.setVoiceBarrageCount(status.optInt("voiceBarrageCount", 0));
        }
        liveTopic.setTeamPkEntity(teamPkEntity);
//        topic":{"gold_count":3,"id":"161870","num":1,"time":3,"type":"2"}}
        if (liveTopicJson.has("topic")) {
            JSONObject topic = liveTopicJson.getJSONObject("topic");
            try {
                if (topic.has("id")) {
                    VideoQuestionLiveEntity videoQuestionLiveEntity = new VideoQuestionLiveEntity();
                    TopicEntity topicEntity = new TopicEntity();
                    topicEntity.setId(topic.getString("id"));
                    videoQuestionLiveEntity.id = topic.getString("id");
                    topicEntity.setAnswer(topic.optString("answer"));
                    videoQuestionLiveEntity.setStuAnswer(topic.optString("answer"));
                    topicEntity.setGold_count(topic.optInt("gold_count", 0));
                    videoQuestionLiveEntity.gold = topic.optInt("gold_count", 0);
                    topicEntity.setNum(topic.optInt("num"));
                    videoQuestionLiveEntity.num = topic.optInt("num");
                    topicEntity.setTime(topic.optInt("time"));
                    videoQuestionLiveEntity.time = topic.optInt("time");
                    topicEntity.setType(topic.optString("type"));
                    videoQuestionLiveEntity.type = topic.optString("type");
                    String choiceType = topic.optString("choiceType", "1");
                    if ("".equals(choiceType)) {
                        choiceType = "1";
                    }
                    topicEntity.setChoiceType(choiceType);
                    videoQuestionLiveEntity.choiceType = choiceType;
                    topicEntity.setSrcType(topic.optString("srcType"));
                    videoQuestionLiveEntity.srcType = topic.optString("srcType");
                    topicEntity.setTestUseH5(topic.optInt("isTestUseH5", -1) == 1);
                    videoQuestionLiveEntity.isTestUseH5 = topic.optInt("isTestUseH5", -1) == 1;
                    topicEntity.setIsAllow42(topic.optString("isAllow42", "0"));
                    videoQuestionLiveEntity.isAllow42 = topic.optString("isAllow42", "0");
                    topicEntity.setSpeechContent(topic.optString("answer"));
                    videoQuestionLiveEntity.speechContent = topic.optString("answer");
                    videoQuestionLiveEntity.setIsVoice(topic.optString("isVoice", "0"));
//                    liveTopic.setTopic(topicEntity);
                    if ("1".equals(videoQuestionLiveEntity.getIsVoice())) {
                        videoQuestionLiveEntity.questiontype = topic.optString("questiontype");
                        videoQuestionLiveEntity.assess_ref = topic.optString("assess_ref");
                    }
                    liveTopic.setVideoQuestionLiveEntity(videoQuestionLiveEntity);
                }
            } catch (JSONException e) {
                logger.e("parseLiveTopic", e);
            }
        }
        try {
            JSONArray disableSpeakingArray = liveTopicJson.getJSONArray("disable_speaking");
            List<String> disableSpeaking = new ArrayList<>();
            for (int i = 0; i < disableSpeakingArray.length(); i++) {
                JSONObject object = disableSpeakingArray.getJSONObject(i);
                disableSpeaking.add(object.getString("id"));
            }
            liveTopic.setDisableSpeaking(disableSpeaking);
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parseLiveTopic", e.getMessage());
            logger.e("parseLiveTopic", e);
        }
        return liveTopic;
    }

    /**
     * 解析直播回放互动题获取红包
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity redPacketParseParser(ResponseEntity responseEntity) {
        VideoResultEntity entity = new VideoResultEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setGoldNum(jsonObject.getInt("gold"));
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "redPacketParseParser", e.getMessage());
        }
        return entity;
    }

    private int testid = 1;
    private boolean lyqTest = true;

    public GoldTeamStatus redGoldTeamStatus(ResponseEntity responseEntity, String stuid, String headUrl) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.getJSONArray("stuList");
            for (int i = 0; i < stuList.length(); i++) {
                try {
                    JSONObject stu = stuList.getJSONObject(i);
                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                    String stuId2 = stu.getString("stuId");
                    student.setMe(stuid.equals(stuId2));
                    student.setStuId(stuId2);
                    student.setName(stu.optString("name"));
                    student.setRealname(stu.optString("realname"));
                    student.setNickname(stu.getString("nickname"));
                    student.setEn_name(stu.getString("en_name"));
                    student.createShowName();
                    student.setGold(stu.optString("gold"));
                    String avatar_path = stu.getString("avatar_path");
                    student.setAvatar_path(avatar_path);
                    entity.getStudents().add(student);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "redGoldTeamStatus:i=" + i, e.getMessage());
                }
            }
//            if (AppConfig.DEBUG && lyqTest) {
//                for (int i = 0; i < 11; i++) {
//                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                    student.setStuId("12345" + testid++);
//                    student.setName(student.getStuId());
//                    student.setNickname("测试测试" + testid++);
//                    if (i % 2 == 0) {
//                        student.setEn_name("rrrrrrrr...");
//                    }
//                    student.createShowName();
//                    student.setGold("1" + i);
//                    student.setAvatar_path(headUrl);
//                    entity.getStudents().add(student);
//                }
//            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "redGoldTeamStatus", e.getMessage());
        }
//        if (AppConfig.DEBUG && lyqTest) {
//            {
//                GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                student.setStuId(stuid);
//                student.setMe(true);
//                student.setName(student.getStuId());
//                student.setNickname("测试" + testid++);
//                student.createShowName();
//                student.setGold("99");
//                student.setAvatar_path(headUrl);
//                entity.getStudents().add(student);
//            }
//            for (int i = 0; i < 11; i++) {
//                GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                student.setStuId("12345" + testid++);
//                student.setName(student.getStuId());
//                student.setNickname("测试" + testid++);
//                student.createShowName();
//                student.setGold("1" + i);
//                student.setAvatar_path(headUrl);
//                entity.getStudents().add(student);
//            }
//        }
        return entity;
    }

    public GoldTeamStatus testAnswerTeamStatus(ResponseEntity responseEntity, String stuid, String headUrl) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.getJSONArray("stuList");
            for (int i = 0; i < stuList.length(); i++) {
                try {
                    JSONObject stu = stuList.getJSONObject(i);
                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                    String stuId2 = stu.getString("stuId");
                    student.setMe(stuid.equals(stuId2));
                    student.setStuId(stuId2);
                    student.setName(stu.optString("name"));
                    student.setRealname(stu.optString("realname"));
                    student.setNickname(stu.getString("nickname"));
                    student.setEn_name(stu.getString("en_name"));
                    student.createShowName();
                    student.setRight(stu.optInt("isRight") == 1 || stu.optInt("isRight") == 2);
                    String avatar_path = stu.getString("avatar_path");
                    student.setAvatar_path(avatar_path);
                    entity.getStudents().add(student);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "redGoldTeamStatus:i=" + i, e.getMessage());
                }
            }
//            if (AppConfig.DEBUG && lyqTest) {
//                for (int i = 0; i < 8; i++) {
//                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                    student.setStuId("12345" + testid++);
//                    student.setName(student.getStuId());
//                    student.setNickname("测试" + testid++);
//                    student.setRight(i % 2 == 0);
//                    student.setAvatar_path(headUrl);
//                    entity.getStudents().add(student);
//                }
//            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "testAnswerTeamStatus", e.getMessage());
        }
//        if (AppConfig.DEBUG && lyqTest) {
//            for (int i = 0; i < 8; i++) {
//                GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                student.setStuId("12345" + testid++);
//                student.setName(student.getStuId());
//                student.setNickname("测试" + testid++);
//                student.setRight(i % 2 == 0);
//                student.setAvatar_path(headUrl);
//                entity.getStudents().add(student);
//            }
//        }
        return entity;
    }

    public GoldTeamStatus getSpeechEvalAnswerTeamStatus(ResponseEntity responseEntity, String stuid) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.getJSONArray("stuList");
            String avatar_path = "";
            for (int i = 0; i < stuList.length(); i++) {
                try {
                    JSONObject stu = stuList.getJSONObject(i);
                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                    String stuId2 = stu.getString("stuId");
                    student.setMe(stuid.equals(stuId2));
                    if (student.isMe()) {
                        continue;
                    }
                    student.setStuId(stuId2);
                    student.setName(stu.optString("name"));
                    student.setRealname(stu.optString("realname"));
                    student.setNickname(stu.getString("nickname"));
                    student.setEn_name(stu.getString("en_name"));
                    student.createShowName();
                    student.setScore(stu.optString("score", "0"));
                    avatar_path = stu.getString("avatar_path");
                    student.setAvatar_path(avatar_path);
                    entity.getStudents().add(student);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "redGoldTeamStatus:i=" + i, e.getMessage());
                }
            }
//            if (AppConfig.DEBUG && lyqTest) {
//                for (int i = 0; i < 3; i++) {
//                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                    student.setStuId("12345" + testid++);
//                    student.setName(student.getStuId());
//                    student.setNickname("测试" + testid++);
//                    student.createShowName();
//                    student.setScore("" + (10 + i));
//                    student.setAvatar_path(avatar_path);
//                    entity.getStudents().add(student);
//                }
//            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "redGoldTeamStatus", e.getMessage());
        }
        return entity;
    }

    /**
     * 解析互动题
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity parseQuestionAnswer(ResponseEntity responseEntity, boolean isVoice) {
        VideoResultEntity entity = new VideoResultEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setTestId(jsonObject.optString("testId"));
            entity.setResultType(jsonObject.optInt("tip"));
            entity.setGoldNum(jsonObject.optInt("gold"));
            entity.setEnergy(jsonObject.optInt("energy"));
            entity.setMsg(jsonObject.optString("msg"));
            entity.setRightNum(jsonObject.optInt("rightnum"));
//            entity.setIsAnswer(jsonObject.optInt("isAnswer", 0));
            if (isVoice) {
                entity.setStandardAnswer(jsonObject.optString("standardAnswer"));
                entity.setYourAnswer(jsonObject.optString("yourAnswer"));
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parseQuestionAnswer", e.getMessage());
        }
        return entity;
    }

    /**
     * 解析文科新课件平台互动题
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity parseNewArtsPlatformQuestionAnswer(ResponseEntity responseEntity, boolean isVoice) {
        VideoResultEntity entity = new VideoResultEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        JSONObject total = jsonObject.optJSONObject("total");
        entity.setGoldNum(Integer.parseInt(total.optString("gold")));
        entity.setRightNum(Integer.parseInt(total.optString("isRight")));
        entity.setEnergy(total.optInt("energy"));
        JSONArray split = jsonObject.optJSONArray("split");
        for (int i = 0; i < split.length(); i++) {
            JSONObject obj = split.optJSONObject(i);
            entity.setTestId(obj.optString("testId"));
            entity.setResultType(Integer.parseInt(obj.optString("isRight")));
            if (isVoice) {
                JSONArray standeranswer = obj.optJSONArray("rightAnswer");
                JSONArray youranswer = obj.optJSONArray("choice");
                entity.setStandardAnswer(standeranswer.optString(0));
                entity.setYourAnswer(youranswer.optString(0));
            }
        }
        return entity;
    }

    /**
     * 解析文科新课件H5语音答题
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity parseNewArtsH5PlatformQuestionAnswer(ResponseEntity responseEntity, boolean isVoice) {
        VideoResultEntity entity = new VideoResultEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        entity.setGoldNum(Integer.parseInt(jsonObject.optString("gold")));
        entity.setEnergy(jsonObject.optInt("energy"));
        entity.setTestId(jsonObject.optString("testId"));

        return entity;
    }

    /**
     * 解析学习报告
     *
     * @param responseEntity
     * @return
     */
    public LearnReportEntity parseLearnReport(ResponseEntity responseEntity) {
        try {
            JSONObject dataObject = (JSONObject) responseEntity.getJsonObject();
            LearnReportEntity learnReportEntity = new LearnReportEntity();
            LearnReportEntity.ReportEntity stu = new LearnReportEntity.ReportEntity();
            learnReportEntity.setStu(stu);
            JSONObject stuObject = dataObject.getJSONObject("stu");
            stu.setStuId(stuObject.getInt("stu_id"));
            //stu.setGold(stuObject.getInt("group_id"));
            stu.setRate(stuObject.getString("rate"));
            stu.setAverageRate(stuObject.getString("average_rate"));
            stu.setRank(stuObject.getInt("rank"));
            stu.setLastRank(stuObject.optInt("last_rank", 0));
            stu.setTime(stuObject.getInt("time"));
            //stu.setStuName(stuObject.getString("stuName"));
            return learnReportEntity;
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseLearnReport", e.getMessage());
        }
        return null;
    }

    /**
     * 解析学习报告
     *
     * @param responseEntity
     * @return
     */
    public LearnReportEntity parseLecLearnReport(ResponseEntity responseEntity) {
        try {
            JSONObject dataObject = (JSONObject) responseEntity.getJsonObject();
            LearnReportEntity learnReportEntity = new LearnReportEntity();
            LearnReportEntity.ReportEntity stu = new LearnReportEntity.ReportEntity();
            learnReportEntity.setStu(stu);
            stu.setStuId(dataObject.getInt("stu_id"));
            //stu.setGold(stuObject.getInt("group_id"));
            stu.setRate(dataObject.getString("rate"));
            stu.setAverageRate(dataObject.getString("averageRate"));
            stu.setRankStr(dataObject.optString("rank"));
            stu.setLastRankStr(dataObject.optString("lastRank"));
            stu.setTime(dataObject.optInt("time"));
            //stu.setStuName(stuObject.getString("stuName"));
            return learnReportEntity;
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseLearnReport", e.getMessage());
        }
        return null;
    }

    public SpeechEvalEntity parseSpeechEval(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        SpeechEvalEntity speechEvalEntity = new SpeechEvalEntity();
        try {
            speechEvalEntity.setContent(data.getString("content"));
            speechEvalEntity.setAnswer(data.optString("answer", speechEvalEntity.getContent()));
            speechEvalEntity.setSpeechEvalTime(data.getLong("speechEvalTime"));
            speechEvalEntity.setNowTime(data.getLong("nowTime"));
            speechEvalEntity.setSpeechEvalReleaseTime(data.getLong("speechEvalReleaseTime"));
            speechEvalEntity.setEndTime(data.getLong("endTime"));
            speechEvalEntity.setAnswered(data.getInt("answered"));
            speechEvalEntity.setTesttype(data.getString("testtype"));
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parseSpeechEval", e.getMessage());
            return null;
        }
        return speechEvalEntity;
    }

    public GoldTeamStatus parseSpeechTeamRank(ResponseEntity responseEntity, LiveGetInfo mGetInfo) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.optJSONArray("stuList");
            String avatar_path = mGetInfo.getHeadImgPath();
            if (stuList != null) {
                for (int i = 0; i < stuList.length(); i++) {
                    try {
                        JSONObject stu = stuList.getJSONObject(i);
                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                        String stuId2 = stu.getString("stuId");
                        student.setMe(mGetInfo.getStuId().equals(stuId2));
                        student.setStuId(stuId2);
                        student.setName(stu.optString("name"));
                        student.setRealname(stu.optString("realname"));
                        student.setNickname(stu.getString("nickname"));
                        student.setEn_name(stu.getString("en_name"));
                        student.createShowName();
                        student.setScore(stu.optString("score", "0"));
                        avatar_path = stu.getString("avatar_path");
                        student.setAvatar_path(avatar_path);
                        entity.getStudents().add(student);
                    } catch (Exception e) {
                        MobAgent.httpResponseParserError(TAG, "parseSpeechTeamRank:i=" + i, e.getMessage());
                    }
                }
            }
//            if (AppConfig.DEBUG && lyqTest) {
//                for (int i = 0; i < 3; i++) {
//                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                    student.setStuId("12345" + testid++);
//                    student.setName(student.getStuId());
//                    student.setNickname("测试" + testid++);
//                    student.setScore("" + (10 + i));
//                    student.setGold("" + (10 + i));
//                    student.setAvatar_path(avatar_path);
//                    entity.getStudents().add(student);
//                }
//            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parseSpeechTeamRank", e.getMessage());
        }
        return entity;
    }

    public GoldTeamStatus parseRolePlayTeamRank(ResponseEntity responseEntity, LiveGetInfo mGetInfo) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.optJSONArray("stuList");
            String avatar_path = mGetInfo.getHeadImgPath();
            if (stuList != null) {
                for (int i = 0; i < stuList.length(); i++) {
                    try {
                        JSONObject stu = stuList.getJSONObject(i);
                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                        String stuId2 = stu.getString("stuId");
                        student.setMe(mGetInfo.getStuId().equals(stuId2));
                        student.setStuId(stuId2);
                        student.setName(stu.optString("name"));
                        student.setRealname(stu.optString("realname"));
                        student.setNickname(stu.getString("nickname"));
                        student.setEn_name(stu.getString("en_name"));
                        student.createShowName();
                        student.setScore(stu.optString("score", "0"));
                        avatar_path = stu.getString("avatar_path");
                        student.setAvatar_path(avatar_path);
                        entity.getStudents().add(student);
                    } catch (Exception e) {
                        MobAgent.httpResponseParserError(TAG, "parseSpeechTeamRank:i=" + i, e.getMessage());
                    }
                }
            }
//            if (AppConfig.DEBUG && lyqTest) {
//                for (int i = 0; i < 3; i++) {
//                    GoldTeamStatus.Student student = new GoldTeamStatus.Student();
//                    student.setStuId("12345" + testid++);
//                    student.setName(student.getStuId());
//                    student.setNickname("测试" + testid++);
//                    student.setScore("" + (10 + i));
//                    student.setGold("" + (10 + i));
//                    student.setAvatar_path(avatar_path);
//                    entity.getStudents().add(student);
//                }
//            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parseSpeechTeamRank", e.getMessage());
        }
        return entity;
    }

    public StudyInfo parseStudyInfo(ResponseEntity responseEntity, String oldMode) {
        StudyInfo studyInfo = new StudyInfo();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        studyInfo.setSignTime(data.optString("signTime"));
        studyInfo.setOnlineTime(data.optString("onlineTime"));
        studyInfo.setTestRate(data.optString("testRate"));
        studyInfo.setMyRank(data.optString("myRank"));
        studyInfo.setMode(data.optString("mode", oldMode));
        return studyInfo;
    }

    /**
     * 解析文理半身直播 家长旁听数据
     *
     * @param responseEntity
     * @param oldMode
     * @return
     */
    public HalfBodyLiveStudyInfo parseStuHalfbodyLiveInfo(ResponseEntity responseEntity, String oldMode) {
        HalfBodyLiveStudyInfo result = new HalfBodyLiveStudyInfo();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            result.setSignTime(data.optString("signTime", ""));
            result.setOnlineTime(data.optString("onlineTime"));
            result.setMode(data.optString("mode", oldMode));
            JSONObject teamInfo = data.optJSONObject("teamInfo");
            if (teamInfo != null) {
                result.setMyRank(teamInfo.optString("myRank"));
                result.setOurTeamEnergy(teamInfo.optLong("ourTeamEnergy"));
                result.setHostileTeamEnergy(teamInfo.optLong("hostileTeamEnergy"));
            }

            JSONObject testInfo = data.optJSONObject("testInfo");
            if (testInfo != null) {
                result.setTestRate(testInfo.optString("stuAvgRate"));
                result.setStuAvgRate(testInfo.optString("stuAvgRate"));
                JSONArray testListArray = testInfo.optJSONArray("testList");
                if (testListArray != null && testListArray.length() > 0) {
                    List<HalfBodyLiveStudyInfo.TestInfo> testInfoList = new ArrayList<>();
                    HalfBodyLiveStudyInfo.TestInfo info = null;
                    JSONObject testJsonObj = null;
                    for (int i = 0; i < testListArray.length(); i++) {
                        testJsonObj = testListArray.getJSONObject(i);
                        info = new HalfBodyLiveStudyInfo.TestInfo();
                        info.setAnsweredStatus(testJsonObj.optInt("answeredStatus"));
                        info.setOrderNum(testJsonObj.optInt("orderNum"));
                        info.setPlanAvgRightRate(testJsonObj.optString("planAvgRightRate"));
                        testInfoList.add(info);
                    }
                    result.setTestList(testInfoList);
                }
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parseStuHalfbodyLiveInfo", e.getMessage());
        }
        return result;
    }


    public AllRankEntity parseAllRank(ResponseEntity responseEntity) {
        AllRankEntity allRankEntity = new AllRankEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        try {
            {
                //我的班级排名
                JSONObject classRankingObj = jsonObject.getJSONObject("classRanking");
                JSONArray classRankingArray = classRankingObj.getJSONArray("list");
                MyRankEntity myRankEntityClass = allRankEntity.getMyRankEntityClass();
                JSONObject classInfoObj = classRankingObj.getJSONObject("classInfo");
                myRankEntityClass.setMyId(classInfoObj.getString("classId"));
                ArrayList<RankEntity> rankEntities = myRankEntityClass.getRankEntities();
                for (int i = 0; i < classRankingArray.length(); i++) {
                    JSONObject classRanking = classRankingArray.getJSONObject(i);
                    RankEntity rankEntity = new RankEntity();
                    rankEntity.setId(classRanking.getString("classId"));
                    rankEntity.setRank(classRanking.getString("rank"));
                    rankEntity.setName(classRanking.getString("className"));
                    rankEntity.setRate(classRanking.getString("rate"));
                    if (rankEntity.getId().equals(myRankEntityClass.getMyId())) {
                        rankEntity.setMe(true);
                    }
                    rankEntities.add(rankEntity);
                }
            }
            JSONObject teamRankingObj = jsonObject.getJSONObject("teamRanking");
            {
                //我的组内排名
                MyRankEntity myRankEntityMyTeam = allRankEntity.getMyRankEntityMyTeam();
                JSONObject myRankObj = teamRankingObj.getJSONObject("myRank");
                myRankEntityMyTeam.setMyId(myRankObj.getString("stuId"));
                JSONArray teamStuRankList = teamRankingObj.getJSONArray("teamStuRankList");
                ArrayList<RankEntity> rankEntities = myRankEntityMyTeam.getRankEntities();
                boolean contentMe = false;
                for (int i = 0; i < teamStuRankList.length(); i++) {
                    JSONObject teamRanking = teamStuRankList.getJSONObject(i);
                    RankEntity rankEntity = new RankEntity();
                    rankEntity.setId(teamRanking.getString("stuId"));
                    rankEntity.setRank(teamRanking.getString("rank"));
                    rankEntity.setName(teamRanking.getString("stuName"));
                    rankEntity.setRate(teamRanking.getString("rate"));
                    rankEntity.setThumbsUpNum(teamRanking.optInt("thumbsUpNum"));
                    rankEntity.setIsThumbsUp(teamRanking.optInt("isThumbsUp"));
                    if (rankEntity.getId().equals(myRankEntityMyTeam.getMyId())) {
                        rankEntity.setMe(true);
                        contentMe = true;
                    }
                    rankEntities.add(rankEntity);
                }
//                if (!contentMe) {
//                    RankEntity rankEntity = new RankEntity();
//                    rankEntity.setMe(true);
//                    rankEntity.setId(myRankObj.getString("stuId"));
//                    rankEntity.setRank(myRankObj.getString("disRank"));
//                    rankEntity.setName(myRankObj.getString("stuName"));
//                    rankEntity.setRate(myRankObj.getString("rate"));
//                    rankEntities.add(rankEntity);
//                }
            }
            {
                //我的小组排名
                MyRankEntity myRankEntityTeams = allRankEntity.getMyRankEntityTeams();
                JSONObject myTeamRankObj = teamRankingObj.getJSONObject("myTeamRank");
                myRankEntityTeams.setMyId(myTeamRankObj.getString("teamId"));
                JSONArray teamRankList = teamRankingObj.getJSONArray("teamRankList");
                ArrayList<RankEntity> rankEntities = myRankEntityTeams.getRankEntities();
                for (int i = 0; i < teamRankList.length(); i++) {
                    JSONObject teamRanking = teamRankList.getJSONObject(i);
                    RankEntity rankEntity = new RankEntity();
                    rankEntity.setId(teamRanking.getString("teamId"));
                    rankEntity.setRank(teamRanking.getString("rank"));
                    rankEntity.setName(teamRanking.getString("teamName"));
                    rankEntity.setRate(teamRanking.getString("rate"));
                    if (rankEntity.getId().equals(myRankEntityTeams.getMyId())) {
                        rankEntity.setMe(true);
                    }
                    rankEntities.add(rankEntity);
                }
            }
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parseAllRank", e.getMessage());
            e.printStackTrace();
        }
        return allRankEntity;
    }

    public StarAndGoldEntity parseStuGoldCount(ResponseEntity responseEntity) {
        StarAndGoldEntity starAndGoldEntity = new StarAndGoldEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        try {
            JSONObject starObj = jsonObject.getJSONObject("star");
            starAndGoldEntity.setStarCount(starObj.optInt("stuStarAmount", 0));
            JSONObject goldObj = jsonObject.getJSONObject("gold");
            starAndGoldEntity.setGoldCount(goldObj.optInt("goldAmount", 0));
            StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
            JSONObject pkEnergyObj = jsonObject.optJSONObject("pkEnergy");
            if (pkEnergyObj != null) {
                pkEnergy.me = pkEnergyObj.optInt("me");
                pkEnergy.myTeam = pkEnergyObj.optInt("myTeam");
                pkEnergy.opTeam = pkEnergyObj.optInt("opTeam");
            }
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parseStuGoldCount", e.getMessage());
            e.printStackTrace();
        }
        return starAndGoldEntity;
    }

    /**
     * 解析优秀榜
     *
     * @param responseEntity
     * @return
     */
    public ExcellentListEntity parseExcellentList(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        ExcellentListEntity entity = new ExcellentListEntity();
        try {
            JSONArray list = data.getJSONArray("list");
            if (list != null && list.length() > 0) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject teamObject = list.getJSONObject(i);
                    PraiseListTeamEntity teamEntity = new PraiseListTeamEntity();
                    teamEntity.setOnListNums(teamObject.getInt("onListNums"));
                    teamEntity.setTeamMemberNums(teamObject.getInt("teamMemberNums"));
                    teamEntity.setPkTeamId(teamObject.getString("pkTeamId"));
                    teamEntity.setIsMy(teamObject.optInt("isMy", -1));
                    if (teamEntity.getIsMy() == 1) {
                        entity.setIsMy(1);
                    }
                    JSONArray stuList = teamObject.getJSONArray("stuList");
                    for (int j = 0; j < stuList.length(); j++) {
                        JSONObject studentObject = stuList.getJSONObject(j);
                        PraiseListStudentEntity studentEntity = new PraiseListStudentEntity();
                        studentEntity.setExcellentNum(studentObject.getInt("excellentNum"));
                        studentEntity.setStuName(studentObject.getString("stuName"));
                        studentEntity.setIsMy(studentObject.optInt("isMy"));
                        teamEntity.getStudentList().add(studentEntity);
                        if (studentEntity.getIsMy() == 1) {
                            entity.setIsMy(1);
                        }
                    }
                    JSONObject pkTeamInfo = teamObject.getJSONObject("pkTeamInfo");
                    teamEntity.setHoverImg(pkTeamInfo.getString("hoverImg"));
                    teamEntity.setHoverSmallImg(pkTeamInfo.getString("hoverSmallImg"));
                    teamEntity.setNormalImg(pkTeamInfo.getString("normalImg"));
                    teamEntity.setNormalSmallImg(pkTeamInfo.getString("normalSmallImg"));
                    teamEntity.setPressImg(pkTeamInfo.getString("pressImg"));
                    teamEntity.setPressSmallImg(pkTeamInfo.getString("pressSmallImg"));
                    teamEntity.setTeamName(pkTeamInfo.getString("teamName"));
                    entity.getTeamList().add(teamEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseExcellentList", e.getMessage());
            return null;
        }
        return entity;
    }

    /**
     * 解析计算小超市榜
     *
     * @param responseEntity
     * @return
     */
    public MinimarketListEntity parseMiniMarketList(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        MinimarketListEntity entity = new MinimarketListEntity();
        try {
            entity.setIsRelease(data.getInt("isRelease"));
            entity.setTitle(data.getString("title"));
            entity.setTitleId(data.getInt("titleId"));
            entity.setTeamNum(data.getInt("teamNum"));

            JSONArray list = data.getJSONArray("list");
            if (list != null && list.length() > 0) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject teamObject = list.getJSONObject(i);
                    PraiseListTeamEntity teamEntity = new PraiseListTeamEntity();
                    teamEntity.setOnListNums(teamObject.getInt("stuSubmitNums"));
                    teamEntity.setTeamMemberNums(teamObject.getInt("teamMemberNums"));
                    teamEntity.setTeamRanking(teamObject.getInt("teamRanking"));
                    teamEntity.setPkTeamId(teamObject.getString("pkTeamId"));
                    teamEntity.setIsMy(teamObject.optInt("isMy", -1));
                    if (teamEntity.getIsMy() == 1) {
                        entity.setIsMy(1);
                    }
                    JSONArray stuList = teamObject.getJSONArray("stuList");
                    for (int j = 0; j < stuList.length(); j++) {
                        JSONObject studentObject = stuList.getJSONObject(j);
                        PraiseListStudentEntity studentEntity = new PraiseListStudentEntity();
                        studentEntity.setExcellentNum(studentObject.getInt("stuPunchNum"));
                        studentEntity.setStuName(studentObject.getString("stuName"));
                        studentEntity.setStuId(studentObject.getString("stuId"));
                        studentEntity.setIsMy(studentObject.optInt("isMy"));
                        teamEntity.getStudentList().add(studentEntity);
                        if (studentEntity.getIsMy() == 1) {
                            entity.setIsMy(1);
                        }
                    }
                    JSONObject pkTeamInfo = teamObject.getJSONObject("pkTeamInfo");
                    teamEntity.setHoverImg(pkTeamInfo.getString("hoverImg"));
                    teamEntity.setHoverSmallImg(pkTeamInfo.getString("hoverSmallImg"));
                    teamEntity.setNormalImg(pkTeamInfo.getString("normalImg"));
                    teamEntity.setNormalSmallImg(pkTeamInfo.getString("normalSmallImg"));
                    teamEntity.setPressImg(pkTeamInfo.getString("pressImg"));
                    teamEntity.setPressSmallImg(pkTeamInfo.getString("pressSmallImg"));
                    teamEntity.setTeamName(pkTeamInfo.getString("teamName"));
                    entity.getTeamList().add(teamEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseMinimarketList", e.getMessage());
            return null;
        }
        return entity;
    }

    /**
     * 解析点赞榜
     *
     * @param responseEntity
     * @return
     */
    public LikeListEntity parseLikeList(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        LikeListEntity entity = new LikeListEntity();
        try {
            JSONArray list = data.getJSONArray("list");
            if (list != null && list.length() > 0) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject teamObject = list.getJSONObject(i);
                    PraiseListTeamEntity teamEntity = new PraiseListTeamEntity();
                    teamEntity.setPraiseTotalNum(teamObject.getInt("praiseTotalNum"));
                    teamEntity.setPkTeamId(teamObject.getString("pkTeamId"));
                    teamEntity.setIsMy(teamObject.optInt("isMy", -1));
                    if (teamEntity.getIsMy() == 1) {
                        entity.setIsMy(1);
                    }
                    JSONArray stuList = teamObject.optJSONArray("stuList");
                    if (stuList != null) {
                        for (int j = 0; j < stuList.length(); j++) {
                            JSONObject studentObject = stuList.getJSONObject(j);
                            PraiseListStudentEntity studentEntity = new PraiseListStudentEntity();
                            studentEntity.setExcellentNum(studentObject.getInt("stuPraiseNum"));
                            studentEntity.setStuName(studentObject.getString("stuName"));
                            studentEntity.setStuId(studentObject.getString("stuId"));
                            studentEntity.setIsMy(studentObject.optInt("isMy"));
                            teamEntity.getStudentList().add(studentEntity);
                            if (studentEntity.getIsMy() == 1) {
                                entity.setIsMy(1);
                            }
                        }
                    }
                    JSONObject pkTeamInfo = teamObject.getJSONObject("pkTeamInfo");
                    teamEntity.setHoverImg(pkTeamInfo.getString("hoverImg"));
                    teamEntity.setHoverSmallImg(pkTeamInfo.getString("hoverSmallImg"));
                    teamEntity.setNormalImg(pkTeamInfo.getString("normalImg"));
                    teamEntity.setNormalSmallImg(pkTeamInfo.getString("normalSmallImg"));
                    teamEntity.setPressImg(pkTeamInfo.getString("pressImg"));
                    teamEntity.setPressSmallImg(pkTeamInfo.getString("pressSmallImg"));
                    teamEntity.setTeamName(pkTeamInfo.getString("teamName"));
                    entity.getTeamList().add(teamEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseLikeList", e.getMessage());
            return null;
        }
        return entity;
    }

    /**
     * 解析点赞概率
     *
     * @param responseEntity
     * @return
     */
    public LikeProbabilityEntity parseLikeProbability(ResponseEntity responseEntity) {
        LikeProbabilityEntity likeProbabilityEntity = new LikeProbabilityEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            likeProbabilityEntity.setStuId(data.getString("stuId"));
            likeProbabilityEntity.setProbability(data.getInt("probability"));

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseLikeProbability", e.getMessage());
        }
        return likeProbabilityEntity;
    }

    /**
     * 解析分队仪式信息
     */
    public TeamPkTeamInfoEntity parseTeamInfo(ResponseEntity responseEntity) {
        TeamPkTeamInfoEntity teamInfoEntity = new TeamPkTeamInfoEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            if (data.has("students")) {
                JSONArray jsonArray = data.getJSONArray("students");
                TeamPkTeamInfoEntity.StudentEntity studentEntity;
                JSONObject jsonObject;
                List<TeamPkTeamInfoEntity.StudentEntity> teamMembers = new ArrayList<TeamPkTeamInfoEntity
                        .StudentEntity>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    studentEntity = new TeamPkTeamInfoEntity.StudentEntity();
                    studentEntity.setUserId(jsonObject.getString("userId"));
                    studentEntity.setUserName(jsonObject.getString("name"));
                    studentEntity.setImg(jsonObject.getString("img"));
                    teamMembers.add(studentEntity);
                }
                teamInfoEntity.setTeamMembers(teamMembers);
            }


            if (data.has("imgs")) {
                JSONObject jsonObject = (JSONObject) data.get("imgs");
                if (jsonObject.has("key")) {
                    teamInfoEntity.setKey(jsonObject.getInt("key"));
                }
                if (jsonObject.has("imgs")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("imgs");
                    List<String> imgList = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imgList.add(jsonArray.getString(i));
                    }
                    teamInfoEntity.setTeamLogoList(imgList);
                }
            }

            if (data.has("teamInfo")) {
                JSONObject jsonObject = (JSONObject) data.get("teamInfo");
                TeamPkTeamInfoEntity.TeamInfoEntity teamInfo = new TeamPkTeamInfoEntity.TeamInfoEntity();
                teamInfo.setImg(jsonObject.getString("img"));
                teamInfo.setTeamName(jsonObject.getString("teamName"));
                teamInfo.setTeamMateName(jsonObject.getString("teamMateName"));
                teamInfo.setSlogon(jsonObject.getString("slogon"));
                teamInfo.setBackGroud(jsonObject.getString("backGroud"));
                teamInfoEntity.setTeamInfo(teamInfo);
                return teamInfoEntity;
            }

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseTeamInfo", e.getMessage());
        }

        return null;
    }

    /**
     * 解析pk 对手信息
     */
    public TeamPkAdversaryEntity parsePkAdversary(ResponseEntity responseEntity) {
        TeamPkAdversaryEntity pkAdversaryEntity = new TeamPkAdversaryEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("self")) {
                JSONObject jsonObject = (JSONObject) data.get("self");
                TeamPkAdversaryEntity.AdversaryInfo selfInfo = new TeamPkAdversaryEntity.AdversaryInfo();
                selfInfo.setClassId(jsonObject.getString("classId"));
                selfInfo.setTeamId(jsonObject.getString("teamId"));
                if (jsonObject.has("teamInfo")) {
                    JSONObject teamInfoObj = jsonObject.getJSONObject("teamInfo");
                    selfInfo.setTeamName(teamInfoObj.getString("teamName"));
                    selfInfo.setTeamMateName(teamInfoObj.getString("teamMateName"));
                    selfInfo.setSlogon(teamInfoObj.getString("slogon"));
                    selfInfo.setBackGroud(teamInfoObj.getString("backGroud"));
                    selfInfo.setImg(teamInfoObj.getString("img"));
                    selfInfo.setTeacherName(teamInfoObj.getString("teacherName"));
                    selfInfo.setTeacherImg(teamInfoObj.getString("teacherImg"));
                }
                pkAdversaryEntity.setSelf(selfInfo);
            }

            if (data.has("opponent")) {
                JSONObject jsonObject = (JSONObject) data.get("opponent");
                TeamPkAdversaryEntity.AdversaryInfo opponentInfo = new TeamPkAdversaryEntity.AdversaryInfo();
                opponentInfo.setClassId(jsonObject.getString("classId"));
                opponentInfo.setTeamId(jsonObject.getString("teamId"));
                if (jsonObject.has("teamInfo")) {
                    JSONObject teamInfoObj = jsonObject.getJSONObject("teamInfo");
                    opponentInfo.setTeamName(teamInfoObj.getString("teamName"));
                    opponentInfo.setTeamMateName(teamInfoObj.getString("teamMateName"));
                    opponentInfo.setSlogon(teamInfoObj.getString("slogon"));
                    opponentInfo.setBackGroud(teamInfoObj.getString("backGroud"));
                    opponentInfo.setImg(teamInfoObj.getString("img"));
                    opponentInfo.setTeacherName(teamInfoObj.getString("teacherName"));
                    opponentInfo.setTeacherImg(teamInfoObj.getString("teacherImg"));
                }
                pkAdversaryEntity.setOpponent(opponentInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkAdversaryEntity;
    }

    /**
     * 解析学生开宝箱
     *
     * @param responseEntity
     * @return
     */
    public StudentChestEntity parseStuChest(ResponseEntity responseEntity) {
        StudentChestEntity studentChestEntity = null;
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            int gold = data.optInt("gold");
            studentChestEntity = new StudentChestEntity();
            String isGet = data.getString("isGet");
            studentChestEntity.setIsGet(isGet);
            studentChestEntity.setGold(gold);

            if (data.has("chip")) {
                JSONObject chipObject = data.getJSONObject("chip");
                studentChestEntity.setAiPatner(true);
                studentChestEntity.setChipName(chipObject.optString("chipName", ""));
                studentChestEntity.setChipNum(chipObject.optInt("chipNum"));
                studentChestEntity.setChipType(chipObject.optInt("chipType"));
                studentChestEntity.setChipUrl(chipObject.optString("chipUrl"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentChestEntity;
    }


    /**
     * 解析班级开宝箱结果
     *
     * @param responseEntity
     * @return
     */
    public ClassChestEntity parseClassChest(ResponseEntity responseEntity) {
        ClassChestEntity classChestEntity = new ClassChestEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("sumGold")) {
                long sumGold = data.getLong("sumGold");
                long sumChip = data.optLong("sumChip");
                classChestEntity.setSumGold(sumGold);
                classChestEntity.setSumChip(sumChip);
            }
            if (data.has("stuList")) {
                List<ClassChestEntity.SubChestEntity> list = new ArrayList<ClassChestEntity.SubChestEntity>();
                JSONArray jsonArray = data.getJSONArray("stuList");
                ClassChestEntity.SubChestEntity subChestEntity = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    long gold = jsonObject.getLong("gold");
                    String stuName = jsonObject.getString("stuName");
                    String avatarPath = jsonObject.getString("avatarPath");
                    String stuId = jsonObject.getString("stuId");
                    subChestEntity = new ClassChestEntity.SubChestEntity(gold, stuName, avatarPath, stuId);
                    long chipNum = jsonObject.optLong("chipNum");
                    String chipName = jsonObject.optString("chipName");
                    subChestEntity.setChipName(chipName);
                    subChestEntity.setChipNum(chipNum);
                    subChestEntity.setChipType(jsonObject.optInt("chipType"));
                    subChestEntity.setChipUrl(jsonObject.optString("chipUrl"));
                    list.add(subChestEntity);
                }
                classChestEntity.setSubChestEntityList(list);
            }

            if (data.has("isMe")) {
                classChestEntity.setIsMe(data.optInt("isMe", 0) == 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classChestEntity;
    }

    /**
     * 解析 投票题 增加能量
     *
     * @param responseEntity
     * @return
     */
    public AddPersonAndTeamEnergyEntity parseAddPersonAndTeamEnergy(ResponseEntity responseEntity) {
        AddPersonAndTeamEnergyEntity energyEntity = null;
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            long teamEnergy = data.getLong("teamEnergy");
            energyEntity = new AddPersonAndTeamEnergyEntity(teamEnergy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return energyEntity;
    }

    /**
     * 解析 学生 总金币 及战队 能量
     *
     * @param responseEntity
     * @return
     */
    public StudentCoinAndTotalEnergyEntity parseStuCoinAndTotalEnergy(ResponseEntity responseEntity) {
        StudentCoinAndTotalEnergyEntity energyEntity = new StudentCoinAndTotalEnergyEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            energyEntity.setCompetitorEnergy(data.getLong("competitorEnergy"));
            energyEntity.setMyEnergy(data.getLong("myEnergy"));
            energyEntity.setStuLiveGold(data.getLong("stuLiveGold"));
            energyEntity.setStuEnergy(data.optInt("stuEnergy"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return energyEntity;
    }

    /**
     * 解析每题战队能量 和贡献之星
     *
     * @param responseEntity
     * @return
     */
    public TeamEnergyAndContributionStarEntity parseTeanEnergyAndContribution(ResponseEntity responseEntity) {
        TeamEnergyAndContributionStarEntity entity = new TeamEnergyAndContributionStarEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("starList")) {
                JSONArray jsonArray = data.optJSONArray("starList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonObject = null;
                    List<TeamEnergyAndContributionStarEntity.ContributionStar> contributionStarList
                            = new ArrayList<TeamEnergyAndContributionStarEntity.ContributionStar>();
                    TeamEnergyAndContributionStarEntity.ContributionStar star = null;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        star = new TeamEnergyAndContributionStarEntity.ContributionStar();
                        star.setStuId(jsonObject.getString("stuId"));
                        star.setEnergy(jsonObject.getLong("energy"));
                        star.setName(jsonObject.getString("name"));
                        star.setRealname(jsonObject.getString("realname"));
                        star.setNickname(jsonObject.getString("nickname"));
                        star.setAvaterPath(jsonObject.getString("avater_path"));
                        contributionStarList.add(star);
                    }
                    entity.setContributionStarList(contributionStarList);
                }
            }


            if (data.has("myTeam")) {
                JSONObject jsonObject = data.getJSONObject("myTeam");
                TeamEnergyAndContributionStarEntity.TeamEnergyInfo teamEnergyInfo
                        = new TeamEnergyAndContributionStarEntity.TeamEnergyInfo();
                teamEnergyInfo.setAddEnergy(jsonObject.getLong("addEnergy"));
                teamEnergyInfo.setTotalEnergy(jsonObject.getLong("totalEnergy"));
                if (jsonObject.has("teamInfo")) {
                    JSONObject teamInfoJsonObj = jsonObject.getJSONObject("teamInfo");
                    teamEnergyInfo.setTeamName(teamInfoJsonObj.getString("teamName"));
                    teamEnergyInfo.setTeamMateName(teamInfoJsonObj.getString("teamMateName"));
                    teamEnergyInfo.setSlogon(teamInfoJsonObj.getString("slogon"));
                    teamEnergyInfo.setBackGroud(teamInfoJsonObj.getString("backGroud"));
                    teamEnergyInfo.setImg(teamInfoJsonObj.getString("img"));
                    teamEnergyInfo.setTeacherName(teamInfoJsonObj.getString("teacherName"));
                    teamEnergyInfo.setTeacherImg(teamInfoJsonObj.getString("teacherImg"));
                }
                entity.setMyTeamEngerInfo(teamEnergyInfo);
            }

            if (data.has("competitor")) {
                JSONObject jsonObject = data.getJSONObject("competitor");
                TeamEnergyAndContributionStarEntity.TeamEnergyInfo teamEnergyInfo
                        = new TeamEnergyAndContributionStarEntity.TeamEnergyInfo();
                teamEnergyInfo.setAddEnergy(jsonObject.getLong("addEnergy"));
                teamEnergyInfo.setTotalEnergy(jsonObject.getLong("totalEnergy"));
                if (jsonObject.has("teamInfo")) {
                    JSONObject teamInfoJsonObj = jsonObject.getJSONObject("teamInfo");
                    teamEnergyInfo.setTeamName(teamInfoJsonObj.getString("teamName"));
                    teamEnergyInfo.setTeamMateName(teamInfoJsonObj.getString("teamMateName"));
                    teamEnergyInfo.setSlogon(teamInfoJsonObj.getString("slogon"));
                    teamEnergyInfo.setBackGroud(teamInfoJsonObj.getString("backGroud"));
                    teamEnergyInfo.setImg(teamInfoJsonObj.getString("img"));
                    teamEnergyInfo.setTeacherName(teamInfoJsonObj.getString("teacherName"));
                    teamEnergyInfo.setTeacherImg(teamInfoJsonObj.getString("teacherImg"));
                }
                entity.setCompetitorEngerInfo(teamEnergyInfo);
            }
            if (data.has("isMe")) {
                entity.setIsMe(data.optInt("isMe", 0) == 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseTeanEnergyAndContribution", e.getMessage());
        }
        return entity;
    }

    /**
     * 解析每题 pk 结果
     *
     * @param responseEntity
     * @return
     */
    public StudentPkResultEntity parseStuPkResult(ResponseEntity responseEntity) {

        StudentPkResultEntity entity = new StudentPkResultEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();

            if (data.has("my")) {
                JSONObject jsonObject = data.getJSONObject("my");
                StudentPkResultEntity.PkResultInfo resultInfo = new StudentPkResultEntity.PkResultInfo();
                resultInfo.setEnergy(jsonObject.getLong("energy"));
                if (jsonObject.has("teamInfo")) {
                    JSONObject teamInfoJsonObj = jsonObject.getJSONObject("teamInfo");
                    resultInfo.setTeamName(teamInfoJsonObj.getString("teamName"));
                    resultInfo.setTeamMateName(teamInfoJsonObj.getString("teamMateName"));
                    resultInfo.setSlogon(teamInfoJsonObj.getString("slogon"));
                    resultInfo.setBackGroud(teamInfoJsonObj.getString("backGroud"));
                    resultInfo.setImg(teamInfoJsonObj.getString("img"));
                }

                if (jsonObject.has("teacherInfo")) {
                    JSONObject teacherInfoJsonObj = jsonObject.getJSONObject("teacherInfo");
                    resultInfo.setTeacherName(teacherInfoJsonObj.getString("teacherName"));
                    resultInfo.setTeacherImg(teacherInfoJsonObj.getString("teacherImg"));
                }
                entity.setMyTeamResultInfo(resultInfo);
            }


            if (data.has("competitor")) {
                JSONObject jsonObject = data.getJSONObject("competitor");
                StudentPkResultEntity.PkResultInfo resultInfo = new StudentPkResultEntity.PkResultInfo();
                resultInfo.setEnergy(jsonObject.getLong("energy"));
                if (jsonObject.has("teamInfo")) {

                    JSONObject teamInfoJsonObj = jsonObject.getJSONObject("teamInfo");
                    resultInfo.setTeamName(teamInfoJsonObj.getString("teamName"));
                    resultInfo.setTeamMateName(teamInfoJsonObj.getString("teamMateName"));
                    resultInfo.setSlogon(teamInfoJsonObj.getString("slogon"));
                    resultInfo.setBackGroud(teamInfoJsonObj.getString("backGroud"));
                    resultInfo.setImg(teamInfoJsonObj.getString("img"));
                }

                if (jsonObject.has("teacherInfo")) {
                    JSONObject teacherInfoJsonObj = jsonObject.getJSONObject("teacherInfo");
                    resultInfo.setTeacherName(teacherInfoJsonObj.getString("teacherName"));
                    resultInfo.setTeacherImg(teacherInfoJsonObj.getString("teacherImg"));
                }
                entity.setCompetitorResultInfo(resultInfo);
            }
        } catch (Exception e) {
            logger.d("parseStuPkResult",e);
            MobAgent.httpResponseParserError(TAG, "parseStuPkResult", e.getMessage());
        }
        return entity;
    }

    /*
     * 解析更多课程的数据
     * */
    public MoreChoice parseMoreChoice(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        MoreChoice moreChoice = new MoreChoice();
        JSONArray casesjson = data.optJSONArray("cases");
        List<MoreChoice.Choice> choices = new ArrayList<>();
        for (int i = 0; i < casesjson.length(); i++) {
            JSONObject jsonObject = casesjson.optJSONObject(i);
            MoreChoice.Choice choice = new MoreChoice.Choice();
            choice.setSaleName(jsonObject.optString("saleName"));
            choice.setLimit(jsonObject.optInt("limit"));
            choice.setSignUpUrl(jsonObject.optString("signUpUrl"));
            choice.setIsLearn(jsonObject.optInt("isLearn"));
            choice.setCourseId(jsonObject.optString("courseId"));
            choice.setAdId(jsonObject.optString("adId"));
            choice.setClassId(jsonObject.optString("classId"));
            choices.add(choice);
        }
        moreChoice.setCases(choices);
        moreChoice.setRows(data.optString("rows"));
        return moreChoice;

    }

    /**
     * 解析低端设备检测信息
     */
    public DeviceDetectionEntity parseDeviceDetectionInfo(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        DeviceDetectionEntity entity = new DeviceDetectionEntity();
        entity.setUnMatchCount(data.optInt("unMatchCount"));
        entity.setUnMatchDesc(data.optString("unMatchDesc"));
        JSONArray array = data.optJSONArray("unMatchList");
        for (int i = 0; i < array.length(); i++) {
            // type = 1：系统版本    type = 3： 设备内存
            JSONObject jsonObject = array.optJSONObject(i);
            int type = jsonObject.optInt("type");
            if (type == 1) {
                entity.setVersionCurrent(jsonObject.optString("current"));
                entity.setVersionNotice(jsonObject.optString("notice"));
            } else if (type == 3) {
                entity.setMemoryCurrent(jsonObject.optString("current"));
                entity.setMemoryNotice(jsonObject.optString("notice"));
            }
        }
        return entity;
    }


    public ArtsExtLiveInfo parseArtsExtLiveInfo(ResponseEntity responseEntity) {
        ArtsExtLiveInfo info = new ArtsExtLiveInfo();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        info.setNewCourseWarePlatform(data.optString("newCourseWarePlatform"));
        info.setIsGroupGameCourseWare(data.optInt("isGroupGameCourseWare", -1));
        info.setSummerCourseWareSize(data.optString("summerCourseWareSize"));
        UmsAgentTrayPreference.getInstance().put(ShareDataConfig.SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE, info.getSummerCourseWareSize());
        return info;
    }

    public HashMap<String, ClassmateEntity> parseStuInfoByIds(String stuIds, ResponseEntity responseEntity) {
        HashMap<String, ClassmateEntity> classmateEntities = new HashMap<>();
        String[] ids = stuIds.split(",");
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            ClassmateEntity classmateEntity = new ClassmateEntity();
            try {
                JSONObject stuJSONObject = jsonObject.getJSONObject(id);
                String realname = stuJSONObject.optString("realname");
                if (!StringUtils.isEmpty(realname)) {
                    classmateEntity.setName(realname);
                } else {
                    String nickname = stuJSONObject.optString("nickname");
                    if (!StringUtils.isEmpty(nickname)) {
                        classmateEntity.setName(nickname);
                    } else {
                        classmateEntity.setName(stuJSONObject.optString("name"));
                    }
                }
                classmateEntity.setImg(stuJSONObject.optString("avatar_path"));
                classmateEntities.put(id, classmateEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return classmateEntities;
    }

    public PkTeamEntity parsegetSelfTeamInfo(ResponseEntity responseEntity, String stu_id) {
        try {
            PkTeamEntity pkTeamEntity = new PkTeamEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            {
                pkTeamEntity.setPkTeamId(jsonObject.getInt("pkTeamId"));
                pkTeamEntity.setaId(jsonObject.getInt("team_a_id"));
                ArrayList<TeamMemberEntity> aTeamMemberEntity = pkTeamEntity.getaTeamMemberEntity();
                JSONArray team_a_mate = jsonObject.getJSONArray("team_a_mate");
                for (int i = 0; i < team_a_mate.length(); i++) {
                    JSONObject jsonObject1 = team_a_mate.getJSONObject(i);
                    TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                    teamMemberEntity.id = jsonObject1.optInt("stu_id");
                    if (stu_id.equals("" + teamMemberEntity.id)) {
                        teamMemberEntity.isMy = true;
                        pkTeamEntity.setMyTeam(pkTeamEntity.getaId());
                    }
                    teamMemberEntity.name = jsonObject1.optString("stu_name");
                    teamMemberEntity.headurl = jsonObject1.optString("stu_head");
                    aTeamMemberEntity.add(teamMemberEntity);
                }
            }
            {
                pkTeamEntity.setbId(jsonObject.getInt("team_b_id"));
                ArrayList<TeamMemberEntity> bTeamMemberEntity = pkTeamEntity.getbTeamMemberEntity();
                JSONArray team_b_mate = jsonObject.getJSONArray("team_b_mate");
                for (int i = 0; i < team_b_mate.length(); i++) {
                    JSONObject jsonObject1 = team_b_mate.getJSONObject(i);
                    TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                    teamMemberEntity.id = jsonObject1.optInt("stu_id");
                    if (stu_id.equals("" + teamMemberEntity.id)) {
                        teamMemberEntity.isMy = true;
                        pkTeamEntity.setMyTeam(pkTeamEntity.getbId());
                    }
                    teamMemberEntity.name = jsonObject1.optString("stu_name");
                    teamMemberEntity.headurl = jsonObject1.optString("stu_head");
                    teamMemberEntity.setNick_name(jsonObject1.optString("nick_name"));
                    bTeamMemberEntity.add(teamMemberEntity);
                }
            }
            return pkTeamEntity;
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parsegetSelfTeamInfo", e.getMessage());
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    public EnTeamPkRankEntity parseUpdataEnglishPkByTestId(ResponseEntity responseEntity, String stu_id) {
        try {
            EnTeamPkRankEntity enTeamPkRankEntity = new EnTeamPkRankEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            enTeamPkRankEntity.setNoShow(jsonObject.optInt("noShow"));
            enTeamPkRankEntity.setMyTeamTotal(jsonObject.optInt("myTeamTotal"));
            enTeamPkRankEntity.setMyTeamCurrent(jsonObject.optInt("myTeamCurrent"));
            enTeamPkRankEntity.setApkTeamId(jsonObject.optInt("myPkHeadTeamId"));
            enTeamPkRankEntity.setIsWin(jsonObject.optInt("isWin"));

            enTeamPkRankEntity.setOpTeamTotal(jsonObject.optInt("opTeamTotal"));
            enTeamPkRankEntity.setOpTeamCurrent(jsonObject.optInt("opTeamCurrent"));
            enTeamPkRankEntity.setBpkTeamId(jsonObject.optInt("opPkHeadTeamId"));
            ArrayList<TeamMemberEntity> memberEntities = enTeamPkRankEntity.getMemberEntities();
            JSONArray jsonArray = jsonObject.optJSONArray("top3");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject top3Obj = jsonArray.getJSONObject(i);
                    TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                    teamMemberEntity.id = top3Obj.optInt("stuId");
                    if (stu_id.equals("" + teamMemberEntity.id)) {
                        teamMemberEntity.isMy = true;
                    }
                    teamMemberEntity.name = top3Obj.optString("name");
                    teamMemberEntity.headurl = top3Obj.optString("head");
                    teamMemberEntity.gold = top3Obj.optInt("gold");
                    teamMemberEntity.energy = top3Obj.optInt("energy");
                    memberEntities.add(teamMemberEntity);
                }
            }
            return enTeamPkRankEntity;
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseUpdataEnglishPkByTestId", e.getMessage());
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    /**
     * 解析战队pk 明星榜
     *
     * @param responseEntity
     * @return
     */
    public List<TeamPkStar> parseTeamPkStar(ResponseEntity responseEntity) {
        List<TeamPkStar> resultList = null;
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("students")) {
                resultList = new ArrayList<TeamPkStar>();
                JSONArray jsonArray = data.optJSONArray("students");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonObject = null;
                    TeamPkStar star = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        star = new TeamPkStar();
                        star.setAvatarPath(jsonObject.optString("avatarPath"));
                        star.setEnergy(jsonObject.optString("energy"));
                        star.setName(jsonObject.optString("name"));
                        star.setTeamName(jsonObject.optString("teamName"));
                        star.setStuId(jsonObject.optString("stuId"));
                        star.setSuper(jsonObject.optInt("isSuper", 0) == 1);
                        resultList.add(star);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    /**
     * 解析战队pk 进步榜
     *
     * @param responseEntity
     * @return
     */
    public List<TeamPkStuProgress> parseTeamPkProgressStu(ResponseEntity responseEntity) {
        List<TeamPkStuProgress> resultList = null;
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("students")) {
                resultList = new ArrayList<TeamPkStuProgress>();
                JSONArray jsonArray = data.optJSONArray("students");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonObject = null;
                    TeamPkStuProgress star = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        star = new TeamPkStuProgress();
                        star.setAvatarPath(jsonObject.optString("avatarPath"));
                        star.setProgressScope(jsonObject.optString("progressScope"));
                        star.setName(jsonObject.optString("name"));
                        star.setTeamName(jsonObject.optString("teamName"));
                        star.setStuId(jsonObject.optString("stuId"));
                        star.setSuper(jsonObject.optInt("isSuper", 0) == 1);
                        resultList.add(star);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    /**
     * 解析战队pk 战队成员信息
     *
     * @param responseEntity
     * @return
     */
    public List<TeamMate> parseTeamMates(ResponseEntity responseEntity) {
        List<TeamMate> result = null;
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            if (data.has("students")) {
                result = new ArrayList<TeamMate>();
                JSONArray jsonArray = data.optJSONArray("students");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonObject = null;
                    TeamMate teamMate = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        teamMate = new TeamMate();
                        teamMate.setId(jsonObject.optString("stuId"));
                        teamMate.setName(jsonObject.optString("name"));
                        result.add(teamMate);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public CoursewareInfoEntity parseCoursewareInfo(ResponseEntity responseEntity) {
        CoursewareInfoEntity coursewareInfoEntity = new CoursewareInfoEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        List<CoursewareInfoEntity.LiveCourseware> liveCoursewares = new ArrayList<>();
        if (data.has("list")) {
            try {
                JSONArray liveCoursewareArray = data.getJSONArray("list");
                for (int i = 0; i < liveCoursewareArray.length(); i++) {
                    try {
                        CoursewareInfoEntity.LiveCourseware liveCourseware = new CoursewareInfoEntity.LiveCourseware();
                        JSONObject liveJson = liveCoursewareArray.getJSONObject(i);
                        liveCourseware.setLiveId(liveJson.getString("liveId"));
                        liveCourseware.setStime(liveJson.optLong("stime", System.currentTimeMillis() / 1000));
                        if (liveJson.has("infos")) {
                            JSONArray coursewareArray = liveJson.getJSONArray("infos");
                            List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos = new ArrayList<>();
                            for (int j = 0; j < coursewareArray.length(); j++) {
                                JSONObject coursewareJson = coursewareArray.getJSONObject(j);
                                CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo = new CoursewareInfoEntity.ItemCoursewareInfo();
                                coursewareInfo.setSourceId(coursewareJson.optString("sourceId"));
                                coursewareInfo.setPackageId(coursewareJson.optString("packageId"));
                                coursewareInfo.setPackageSource(coursewareJson.optString("packageSource"));
                                coursewareInfo.setTemplate(coursewareJson.optInt("isTemplate") == 1);
                                coursewareInfo.setPageId(coursewareJson.optString("pageId"));
                                coursewareInfo.setResourceUrl(coursewareJson.optString("resourceUrl"));
                                coursewareInfo.setTemplateUrl(coursewareJson.optString("templateUrl"));
//                            coursewareInfo.setMd5(coursewareJson.optString("md5"));
                                coursewareInfo.setResourceMd5(coursewareJson.optString("resourceMd5"));
                                coursewareInfo.setTemplateMd5(coursewareJson.optString("templateMd5"));
                                coursewareInfos.add(coursewareInfo);
                            }
                            liveCourseware.setCoursewareInfos(coursewareInfos);
                        }
                        liveCoursewares.add(liveCourseware);
                    } catch (Exception e) {
                        MobAgent.httpResponseParserError(TAG, "parseCoursewareInfo", e.getMessage());
                    }
                }
                coursewareInfoEntity.setCoursewaresList(liveCoursewares);
                JSONObject hostJson = data.getJSONObject("host");
                if (hostJson.has("cdns")) {
                    JSONArray cdnsArray = hostJson.getJSONArray("cdns");
                    List<String> cdns = new ArrayList<>();
                    for (int i = 0; i < cdnsArray.length(); i++) {
                        cdns.add(cdnsArray.getString(i));
                    }
                    coursewareInfoEntity.setCdns(cdns);
                }
                if (hostJson.has("ips")) {
                    JSONArray cdnsArray = hostJson.getJSONArray("ips");
                    List<String> ips = new ArrayList<>();
                    for (int i = 0; i < cdnsArray.length(); i++) {
                        ips.add(cdnsArray.getString(i));
                    }
                    coursewareInfoEntity.setIps(ips);
                }
                if (data.has("resource")) {
                    JSONObject resourceArray = data.getJSONObject("resource");
//                    JSONArray resourceArray = data.getJSONArray("resource");
                    List<String> resources = new ArrayList<>();
//                    for (int i = 0; i < resourceArray.length(); i++) {
                    JSONArray formulasArray = resourceArray.optJSONArray("formulas");
                    if (formulasArray != null) {
                        for (int j = 0; j < formulasArray.length(); j++) {
                            resources.add(formulasArray.getString(j));
                        }
                    }
                    JSONArray fontsArray = resourceArray.optJSONArray("fonts");
                    if (fontsArray != null) {
                        for (int k = 0; k < fontsArray.length(); k++) {
                            resources.add(fontsArray.getString(k));
                        }
                    }

                    JSONObject nbResource = resourceArray.optJSONObject("NBResource");
                    if (nbResource != null) {
                        String resurseMd5 = nbResource.optString("resourceMd5");
                        String resurseUrl = nbResource.optString("resourceUrl");
                        if (!TextUtils.isEmpty(resurseMd5) && !TextUtils.isEmpty(resurseUrl)) {
                            CoursewareInfoEntity.NbCoursewareInfo nbCoursewareInfo = new CoursewareInfoEntity.NbCoursewareInfo();
                            nbCoursewareInfo.setResourceMd5(resurseMd5);
                            nbCoursewareInfo.setResourceUrl(resurseUrl);
                            coursewareInfoEntity.setNbCoursewareInfo(nbCoursewareInfo);
                            //缓存NB资源文件解压相对路径
                            ShareDataManager.getInstance().put(NbCourseWareConfig.LOCAL_RES_DIR, resurseMd5,
                                    ShareDataManager.SHAREDATA_NOT_CLEAR);
                        }
                    }

//                    }
                    coursewareInfoEntity.setResources(resources);
                }
//                if (data.has("loadpages")){
//                    JSONArray loadpageArray = data.getJSONArray("loadpages");
//                    List<String> loadpages = new ArrayList<>();
//                    for (int i = 0; i < loadpageArray.length(); i++) {
//                        loadpages.add(loadpageArray.getString(i));
//                    }
//                    coursewareInfoEntity.setLoadpages(loadpages);
//                }
//                if (data.has("staticSource")){
//                    JSONObject staticSourceJson = data.getJSONObject("staticSource");
//                    List<String> staticSources = new ArrayList<>();
//                    staticSources.add(staticSourceJson.optString("V1"));
//                    staticSources.add(staticSourceJson.optString("V2"));
//                    coursewareInfoEntity.setStaticSources(staticSources);
//                }
            } catch (JSONException e) {
                MobAgent.httpResponseParserError(TAG, "parseCoursewareInfo", e.getMessage());
                e.printStackTrace();
            }
        }
        return coursewareInfoEntity;
    }

    /**
     * 解析中学理科 连对激励的Entity
     *
     * @param responseEntity
     * @return
     */
    public EvenDriveEntity parseEvenEntity(ResponseEntity responseEntity) {
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        EvenDriveEntity evenDriveEntity = new EvenDriveEntity();
        EvenDriveEntity.MyEntity myEntity = new EvenDriveEntity.MyEntity();
        int myRank = 1;
        if (jsonObject.has("myInfo")) {
            try {
                JSONObject myJSON = jsonObject.getJSONObject("myInfo");
                myEntity.setEvenPairNum(myJSON.optInt("evenPairNum"));
                myEntity.setHighestRightNum(myJSON.optString("highestRightNum"));
                myEntity.setName(myJSON.optString("name"));
                myEntity.setStuId(myJSON.optString("stuId"));
                myEntity.setIsThumbsUp(myJSON.optInt("isThumbsUp"));
                myEntity.setThumbsUpNum(myJSON.optInt("thumbsUpNum"));
                myRank = myJSON.optInt("rank");
                myEntity.setRank(myRank);
                evenDriveEntity.setMyEntity(myEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("stuRanking")) {
            JSONArray jsonArray;
            try {
                jsonArray = jsonObject.getJSONArray("stuRanking");
                List<EvenDriveEntity.OtherEntity> list = new LinkedList<>();
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject itemJSON = jsonArray.getJSONObject(item);
                    EvenDriveEntity.OtherEntity otherEntity = new EvenDriveEntity.OtherEntity();
                    otherEntity.setEvenPairNum(itemJSON.optInt("evenPairNum"));
                    otherEntity.setIsThumbsUp(itemJSON.optInt("isThumbsUp"));
                    otherEntity.setName(itemJSON.optString("name"));
                    String stuId = itemJSON.optString("stuId");
                    otherEntity.setStuId(stuId);
                    otherEntity.setThumbsUpNum(itemJSON.optInt("thumbsUpNum"));
                    int ranking = itemJSON.optInt("ranking");
                    otherEntity.setRanking(ranking);
//                    if (myRank == ranking) {
//                    EvenDriveEntity.OtherEntity myListEntity = new EvenDriveEntity.OtherEntity();
//                    myListEntity.setRanking(myRank);
//                    myListEntity.setEvenPairNum(itemJSON.optInt("evenPairNum"));
//                    myListEntity.setIsThumbsUp(itemJSON.optInt("isThumbsUp"));
//                    myListEntity.setName(itemJSON.optString("name"));
//                        String stuId = itemJSON.optString("stuId");
//                    myListEntity.setStuId(stuId);
//                    myListEntity.setThumbsUpNum(itemJSON.optInt("thumbsUpNum"));
//                    list.add(0, myListEntity);
//                    }
                    list.add(otherEntity);
                }
                if (myRank != 0) {
                    EvenDriveEntity.OtherEntity myInOtherEntity = new EvenDriveEntity.OtherEntity();
                    myInOtherEntity.setRanking(myRank);
                    myInOtherEntity.setEvenPairNum(myEntity.getEvenPairNum());
                    myInOtherEntity.setIsThumbsUp(myEntity.getIsThumbsUp());
                    myInOtherEntity.setName(myEntity.getName());
//                        String stuId = itemJSON.optString("stuId");
                    myInOtherEntity.setStuId(myEntity.getStuId());
                    myInOtherEntity.setThumbsUpNum(myEntity.getThumbsUpNum());
                    list.add(0, myInOtherEntity);
                }
                evenDriveEntity.setOtherEntities(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return evenDriveEntity;
    }

    public SuperSpeakerRedPackageEntity parseSuperSpeakerSubmitEntity(ResponseEntity responseEntity) {
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        SuperSpeakerRedPackageEntity entity = new SuperSpeakerRedPackageEntity();
        entity.setMoney(jsonObject.optString("gold"));
        return entity;
    }

    /**
     * 解析 出门测表扬榜数据
     *
     * @param responseEntity
     * @return
     */
    public PraiseEntity parseTutorPraiseEntity(ResponseEntity responseEntity) throws Exception {
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        // JSONObject jsonObject = new JSONObject(data);
        List<PraiseContentEntity> contentEntityList = new ArrayList<>();
        PraiseContentEntity titleEntity = null;
        PraiseContentEntity contentEntity = null;
        PraiseEntity entity = new PraiseEntity();
        entity.setContentEntityList(contentEntityList);
        int gradle = jsonObject.optInt("grade");
        int position = 0;
//        int gradle = 2;
        if (gradle == PraiseConfig.GRADLE_SMALL) {
            entity.setPraiseStyle(jsonObject.optInt("bizId"));
            //entity.setPraiseStyle(1);
        } else {
            entity.setPraiseStyle(PraiseConfig.PRAISE_DARK);
        }
        entity.setPraiseName(jsonObject.optString("rankTitle"));
        entity.setPraiseType(jsonObject.optInt("category"));
        if (entity.getPraiseType() == PraiseConfig.PRAISE_TYPE_TALK) {
            titleEntity = new PraiseContentEntity();
            titleEntity.setViewType(PraiseConfig.VIEW_TYPE_TITLE);
            titleEntity.setPraiseStyle(entity.getPraiseStyle());
            titleEntity.setItemSpan(4);
            titleEntity.setName("题目描述");
            contentEntityList.add(titleEntity);
            position = 1;
            contentEntity = new PraiseContentEntity();
            contentEntity.setPraiseStyle(entity.getPraiseStyle());
            contentEntity.setOralQuestion(true);
            contentEntity.setItemSpan(4);
            contentEntity.setName(jsonObject.optString("desc"));
            contentEntityList.add(contentEntity);
        }
        entity.setResultType(jsonObject.optInt("isInList"));
        entity.setTeacherHeadImage(jsonObject.optString("counselorAvatar"));
        entity.setTeacherName(jsonObject.optString("counselorName"));
        entity.setEncouraging(jsonObject.optString("word"));
        JSONArray array = jsonObject.optJSONArray("list");
        if (array != null && array.length() > 0) {
            JSONObject userListObject = null;
            for (int i = 0; i < array.length(); i++) {
                userListObject = array.optJSONObject(i);
                String title = userListObject.optString("group");
                if (!TextUtils.isEmpty(title)) {
                    titleEntity = new PraiseContentEntity();
                    titleEntity.setViewType(PraiseConfig.VIEW_TYPE_TITLE);
                    titleEntity.setItemSpan(4);
                    titleEntity.setName(title);
                    titleEntity.setPraiseStyle(entity.getPraiseStyle());
                    contentEntityList.add(titleEntity);
                    position++;
                }
                parsePraiseContentEntity(entity, userListObject, contentEntityList, entity.getPraiseStyle(), position);
            }
        }
        return entity;
    }

    /**
     * 解析榜单内容
     *
     * @param jsonObject
     * @param contentEntityList
     */
    private void parsePraiseContentEntity(PraiseEntity entity, JSONObject jsonObject, List<PraiseContentEntity> contentEntityList, int style, int position) {
        JSONArray array = jsonObject.optJSONArray("stus");
        if (array != null && array.length() > 0) {
            JSONObject userListObject = null;
            PraiseContentEntity contentEntity = null;
            for (int i = 0; i < array.length(); i++) {
                userListObject = array.optJSONObject(i);
                contentEntity = new PraiseContentEntity();
                position++;
                contentEntity.setName(userListObject.optString("name"));
                if (userListObject.optInt("inList") == 1) {
                    entity.setPosition(position);
                }
                contentEntity.setStatus(userListObject.optInt("inList"));
                contentEntity.setPraiseStyle(style);
                contentEntityList.add(contentEntity);
            }
        } else {
            PraiseContentEntity contentEntity = new PraiseContentEntity();
            contentEntity.setName(jsonObject.optString("msg"));
            contentEntity.setItemSpan(4);
            contentEntity.setPraiseStyle(style);
            contentEntityList.add(contentEntity);
        }
    }

    /**
     * 英语小目标 - 解析学生段位信息
     *
     * @param responseEntity
     * @return
     */
    public StuSegmentEntity parseStuSegmentInfo(ResponseEntity responseEntity) {
        try {
            StuSegmentEntity stuSegmentEntity = new StuSegmentEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            stuSegmentEntity.setAimNumber(jsonObject.getString("aimNumber"));
            stuSegmentEntity.setSegment(jsonObject.getString("segment"));
            stuSegmentEntity.setStar(jsonObject.getString("star"));
            stuSegmentEntity.setSumCount(jsonObject.getString("sumCount"));
            return  stuSegmentEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 英语小目标 - 解析学生这节课小目标
     *
     * @param responseEntity
     * @return
     */
    public BetterMeEntity parseBetterMeInfo(ResponseEntity responseEntity) {
        try {
            BetterMeEntity betterMeEntity = new BetterMeEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            betterMeEntity.setAimType(jsonObject.getString("aimType"));
            betterMeEntity.setAimValue(jsonObject.getString("aimValue"));
            betterMeEntity.setFirstReceive("1".equals(jsonObject.optString("isFirstReceive","0")));
            return  betterMeEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 英语小目标 - 解析实时学生目标完成度
     *
     * @param responseEntity
     * @return
     */
    public AimRealTimeValEntity parseAimRealTimeValInfo(ResponseEntity responseEntity) {
        try {
            AimRealTimeValEntity aimRealTimeValEntity = new AimRealTimeValEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            aimRealTimeValEntity.setDoneAim("1".equals(jsonObject.getString("isDoneAim")));
            aimRealTimeValEntity.setType(jsonObject.getString("aimType"));
            aimRealTimeValEntity.setRealTimeVal(jsonObject.getString("realTimeVal"));
            aimRealTimeValEntity.setAimValue(jsonObject.getString("aimValue"));
            return  aimRealTimeValEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 英语小目标 - 解析小目标结果
     *
     * @param responseEntity
     * @return
     */
    public StuAimResultEntity parseStuAimResultInfo(ResponseEntity responseEntity) {
        try {
            StuAimResultEntity stuAimResultEntity = new StuAimResultEntity();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            stuAimResultEntity.setIsUpGrade(jsonObject.getString("isUpGrade"));
            stuAimResultEntity.setSegment(jsonObject.getString("segment"));
            stuAimResultEntity.setAimNumber(jsonObject.getString("aimNumber"));
            stuAimResultEntity.setStar(jsonObject.getString("star"));
            stuAimResultEntity.setIsDoneAim(jsonObject.getString("isDoneAim"));
            stuAimResultEntity.setAimType(jsonObject.getString("aimType"));
            stuAimResultEntity.setRealTimeVal(jsonObject.getString("realTimeVal"));
            stuAimResultEntity.setAimValue(jsonObject.getString("aimValue"));
            return  stuAimResultEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
