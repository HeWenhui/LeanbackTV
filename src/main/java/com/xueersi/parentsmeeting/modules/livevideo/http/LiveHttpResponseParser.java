package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AddPersonAndTeamEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.FollowTypeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.NewTalkConfEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.TestInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.TopicEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MyRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.common.http.ResponseEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LiveHttpResponseParser extends HttpResponseParser {
    String TAG = "LiveHttpResponseParser";
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
        try {
            getInfo.setGrade(Integer.parseInt(data.optString("gradeIds").split(",")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (1 == data.optInt("isPrimarySchool")) {
            LiveVideoConfig.isPrimary = true;
        } else {
            LiveVideoConfig.isPrimary = false;
        }
        LiveVideoConfig.isScience = true;
        LiveVideoConfig.educationstage = getInfo.getEducationStage();
        LiveVideoConfig.LIVEMULPRELOAD = data.optString("courseWarePreLoadUrl");
        LiveVideoConfig.LIVEMULH5URL = data.optString("getCourseWareHtml");
    }

    /**
     * 解析getInfo 文科
     *
     * @param data
     * @param liveTopic
     * @param getInfo
     */
    public void parseLiveGetInfoLibarts(JSONObject data, LiveTopic liveTopic, LiveGetInfo getInfo) {
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
        } else {
            getInfo.setSmallEnglish(false);
        }
    }

    /**
     * 解析getInfo
     */
    public LiveGetInfo parseLiveGetInfo(JSONObject data, LiveTopic liveTopic, int liveType, int from) {
        try {
            LiveGetInfo getInfo = new LiveGetInfo(liveTopic);
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
            getInfo.setIsShowMarkPoint(data.optString("isAllowMarkpoint"));
            getInfo.setIsAIPartner(data.optInt("isAIPartner"));

            //getInfo.setIsShowMarkPoint("0");
            getInfo.setIsShowCounselorWhisper(data.optString("counselor_whisper"));
            getInfo.setIsSeniorOfHighSchool(data.optInt("isSeniorOfHighSchool"));

            //getInfo.setIsShowCounselorWhisper("1");
            if (data.has("followType")) {
                JSONObject followType = data.getJSONObject("followType");
                FollowTypeEntity followTypeEntity = new FollowTypeEntity();
                followTypeEntity.setInt2(followType.getInt("2"));
                followTypeEntity.setInt3(followType.getInt("3"));
                followTypeEntity.setInt4(followType.getInt("4"));
            }

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
            getInfo.setStuImg(data.getString("stuImg"));
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
                        Loger.d(TAG, "parseLiveGetInfo:teamStuIds=" + teamStuIds.size());
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
            getInfo.setTalkHost(data.getString("talkHost"));
            getInfo.setTalkPort(data.getString("talkPort"));
            getInfo.setTalkPwd(data.optString("talkPwd"));
            getInfo.setRoomId(data.optString("roomId"));
            if (data.has("newTalkConf")) {
                List<NewTalkConfEntity> newTalkConf = new ArrayList<NewTalkConfEntity>();
                JSONArray newTalkarray = data.getJSONArray("newTalkConf");
                for (int i = 0; i < newTalkarray.length(); i++) {
                    NewTalkConfEntity talkConfEntity = new NewTalkConfEntity();
                    JSONObject talkConfobj = newTalkarray.getJSONObject(i);
                    talkConfEntity.setHost(talkConfobj.getString("host"));
                    talkConfEntity.setPort(talkConfobj.getString("port"));
                    talkConfEntity.setPwd(talkConfobj.optString("pwd"));
                    newTalkConf.add(talkConfEntity);
                }
                getInfo.setNewTalkConf(newTalkConf);
            }
            ArrayList<TalkConfHost> newTalkConfHosts = new ArrayList<>();
            if (data.has("liveChatDispatchUrl")) {
                JSONArray array = data.optJSONArray("liveChatDispatchUrl");
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
            Loger.i(TAG, "parseLiveGetInfo:headImgUrl=" + headImgUrl.size());
            getInfo.setHeadImgUrl(headImgUrl);
            try {
                getInfo.setHeadImgPath(data.optString("headImgPath"));
                getInfo.setImgSizeType(data.optString("imgSizeType"));
                getInfo.setHeadImgVersion(data.optString("headImgVersion"));
            } catch (Exception e) {
                Loger.e(TAG, "parseLiveGetInfo.Head", e);
                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo.Head", e.getMessage());
            }
            getInfo.setCloseChat(data.optInt("isCloseChat", 0) == 1);
            if (data.has("skeyPlayT")) {
                getInfo.setSkeyPlayT(data.getString("skeyPlayT"));
            } else {
                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", "skeyPlayT=null");
            }
            if (data.has("skeyPlayF")) {
                getInfo.setSkeyPlayF(data.getString("skeyPlayF"));
            } else {
                MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", "skeyPlayF=null");
            }
            getInfo.setSpeechEvalUrl(data.optString("speechEvalUrl", "https://live.xueersi.com/Live/speechEval/"));
            getInfo.setUrlClick(data.optInt("urlClick", 0));
            getInfo.setAllowLinkMic(data.optInt("allowLinkMic", 1) == 1);
            getInfo.setStuLinkMicNum(data.optInt("stuLinkMicNum", 0));
            getInfo.setTestPaperUrl(data.optString("testPaperUrl", "https://live.xueersi.com/Live/getMultiTestPaper"));
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
            LiveVideoConfig.isPrimary = false;
            LiveVideoConfig.isScience = false;
            if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                if (getInfo.getIsArts() == 1) {
                    parseLiveGetInfoLibarts(data, liveTopic, getInfo);
                } else {
                    parseLiveGetInfoScience(data, liveTopic, getInfo);
                }
            }
            return getInfo;
        } catch (JSONException e) {
            Loger.e(TAG, "parseLiveGetInfo", e);
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
            server.setAppname(object.getString("appname"));
            server.setCcode(object.optString("ccode"));
            server.setCode(object.optInt("code"));
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
            coachStatusEntity.setListStatus(status.optInt("listStatus"));

            if (status.has("openbarrage")) {
                Loger.i("yzl_fd", "room2中有openbarrage字段 理科 status.getBoolean(\"openbarrage\") = " + status.getBoolean("openbarrage") + " " + status.toString());
                //新增字段，辅导老师开启礼物与否 true开启
                coachStatusEntity.setFDLKOpenbarrage(status.getBoolean("openbarrage"));

            } else {
                Loger.i("yzl_fd", "room2中没有openbarrage字段 文科" + status.toString());
            }

            // 解析辅讲老师信息
            LiveTopic.TeamPkEntity.RoomInfo roomInfo2 = new LiveTopic.TeamPkEntity.RoomInfo();
            roomInfo2.setAlloteam(status.optInt("alloteam"));
            roomInfo2.setOpenbox(status.optInt("openbox"));
            roomInfo2.setAllotpkman(status.optInt("allotpkman"));
            teamPkEntity.setRoomInfo2(roomInfo2);

            if (status.has("link_mic")) {
                Loger.i("yzl_fd", "辅导老师 parseLiveTopic status = " + status.toString());
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
        }
        if (liveTopicJson.has("room_1")) {
            Loger.i("yzl_fd", "主讲老师 parseLiveTopic liveTopicJson = " + liveTopicJson.toString());
            JSONObject status = liveTopicJson.getJSONObject("room_1");
            RoomStatusEntity mainStatusEntity = liveTopic.getMainRoomstatus();
            mainStatusEntity.setOnbreak(status.optBoolean("isOnBreak"));
            mainStatusEntity.setId(status.getInt("id"));
            mainStatusEntity.setClassbegin(status.getBoolean("classbegin"));
            mainStatusEntity.setOpenbarrage(status.getBoolean("openbarrage"));
            liveTopic.getCoachRoomstatus().setZJLKOpenbarrage(status.getBoolean("openbarrage"));//一定不要忘记在topic返回的时候，room1里openbarrage字段的值设置到理科主讲实体中
            mainStatusEntity.setOpenchat(status.getBoolean("openchat"));
            mainStatusEntity.setOpenFeedback(status.optBoolean("isOpenFeedback"));

            // 解析主讲老师信息
            LiveTopic.TeamPkEntity.RoomInfo roomInfo1 = new LiveTopic.TeamPkEntity.RoomInfo();
            roomInfo1.setAlloteam(status.optInt("alloteam"));
            roomInfo1.setOpenbox(status.optInt("openbox"));
            roomInfo1.setAllotpkman(status.optInt("allotpkman"));
            teamPkEntity.setRoomInfo1(roomInfo1);


            if (status.has("exam")) {
                mainStatusEntity.setHaveExam(true);
                JSONObject jsonObject = status.getJSONObject("exam");
                mainStatusEntity.setExamStatus(jsonObject.optString("status", "off"));
                mainStatusEntity.setExamNum(jsonObject.optString("num", "-1"));
            } else {
                mainStatusEntity.setHaveExam(false);
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
                Loger.e(TAG, "parseLiveTopic", e);
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
            Loger.e(TAG, "parseLiveTopic", e);
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
                    student.setRight(stu.optInt("isRight") == 1);
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
        JSONArray split = jsonObject.optJSONArray("split");
        for(int i = 0 ; i < split.length() ; i++){
            JSONObject obj = split.optJSONObject(i);
            entity.setTestId(obj.optString("testId"));
            entity.setResultType(Integer.parseInt(obj.optString("isRight")));
            if (isVoice) {
                JSONArray standeranswer = obj.optJSONArray("rightAnswer");
                JSONArray youranswer = obj.optJSONArray("choice");
                entity.setStandardAnswer( standeranswer.optString(0));
                entity.setYourAnswer(youranswer.optString(0));
            }
        }
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
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parseStuGoldCount", e.getMessage());
            e.printStackTrace();
        }
        return starAndGoldEntity;
    }

    /**
     * 解析光荣榜
     *
     * @param responseEntity
     * @return
     */
    public HonorListEntity parseHonorList(ResponseEntity responseEntity) {

//        HonorListEntity honorListEntity = new HonorListEntity();
//        honorListEntity.setPraiseStatus(1);
//        honorListEntity.setIsMy(1);
//
//        for (int i = 0; i < 10; i++) {
//            HonorListEntity.HonorEntity honorEntity = honorListEntity.new HonorEntity();
//            honorEntity.setExcellentNum(String.valueOf(i + 1));
//            honorEntity.setStuName("学生" + i);
//            if (honorEntity.getIsMy() == 1) {
//                honorListEntity.setIsMy(1);
//            } else {
//                honorListEntity.getHonorEntities().add(honorEntity);
//            }
//        }

        Loger.i(TAG, "parseHonorList: " + responseEntity.getJsonObject());
        HonorListEntity honorListEntity = new HonorListEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            honorListEntity.setPraiseStatus(data.getInt("praiseStatus"));
            JSONArray array = data.getJSONArray("list");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                HonorListEntity.HonorEntity honorEntity = honorListEntity.new HonorEntity();
                honorEntity.setIsMy(jsonObject.getInt("isMy"));
                honorEntity.setExcellentNum(jsonObject.getString("excellent_num"));
                honorEntity.setStuName(jsonObject.getString("stu_name"));
                if (honorEntity.getIsMy() == 1) {
                    honorListEntity.setIsMy(1);
                }
                honorListEntity.getHonorEntities().add(honorEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseHonorList", e.getMessage());
        }
        return honorListEntity;
    }

    /**
     * 解析点赞榜
     *
     * @param responseEntity
     * @return
     */
    public ThumbsUpListEntity parseThumbsUpList(ResponseEntity responseEntity) {

//        ThumbsUpListEntity thumbsUpListEntity = new ThumbsUpListEntity();
//        for (int i = 0; i < 40; i++) {
//            ThumbsUpListEntity.ThumbsUpEntity likeEntity = thumbsUpListEntity.new ThumbsUpEntity();
//            likeEntity.setStuPraiseNum(i + 10);
//            if (i % 2 == 0) {
//                likeEntity.setStuName("学生" + i);
//            } else {
//                likeEntity.setStuName("学生地方撒" + i);
//            }
//            likeEntity.setIsMy(1);
//            if (likeEntity.getIsMy() == 1) {
//                thumbsUpListEntity.setIsMy(1);
//            }
//            thumbsUpListEntity.getThumbsUpEntities().add(likeEntity);
//        }

        Loger.i(TAG, "parseThumbsUpList: " + responseEntity.getJsonObject());
        ThumbsUpListEntity thumbsUpListEntity = new ThumbsUpListEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            JSONArray array = data.getJSONArray("list");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                ThumbsUpListEntity.ThumbsUpEntity likeEntity = thumbsUpListEntity.new ThumbsUpEntity();
                likeEntity.setIsMy(jsonObject.getInt("isMy"));
                likeEntity.setStuPraiseNum(jsonObject.getInt("stu_praise_num"));
                likeEntity.setStuName(jsonObject.getString("stu_name"));
                if (likeEntity.getIsMy() == 1) {
                    thumbsUpListEntity.setIsMy(1);
                }
                thumbsUpListEntity.getThumbsUpEntities().add(likeEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseThumbsUpList", e.getMessage());
        }
        return thumbsUpListEntity;
    }

    /**
     * 解析进步榜
     *
     * @param responseEntity
     * @return
     */
    public ProgressListEntity parseProgressList(ResponseEntity responseEntity) {
//        ProgressListEntity progressListEntity = new ProgressListEntity();
//        for (int i = 0; i < 10; i++) {
//            ProgressListEntity.ProgressEntity progressEntity = progressListEntity.new ProgressEntity();
//            progressEntity.setStuId("11" + i);
//            if (i % 2 == 0) {
//                progressEntity.setStuName("学生大是大非" + i);
//            } else {
//                progressEntity.setStuName("学" + i);
//            }
//
//            progressEntity.setIsMy(1);
//            progressEntity.setProgressScore(String.valueOf(91 + i));
//            if (progressEntity.getIsMy() == 1) {
//                progressListEntity.setIsMy(1);
//            }
//            progressListEntity.getProgressEntities().add(progressEntity);
//        }
        Loger.i(TAG, "parseProgressList: " + responseEntity.getJsonObject());
        ProgressListEntity progressListEntity = new ProgressListEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            progressListEntity.setPraiseStatus(data.getInt("praiseStatus"));
            JSONArray array = data.getJSONArray("list");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                ProgressListEntity.ProgressEntity progressEntity = progressListEntity.new ProgressEntity();
                progressEntity.setStuId(jsonObject.getString("stu_id"));
                progressEntity.setStuName(jsonObject.getString("stu_name"));
                progressEntity.setIsMy(jsonObject.getInt("isMy"));
                progressEntity.setProgressScore(jsonObject.getString("progress_score"));
                if (progressEntity.getIsMy() == 1) {
                    progressListEntity.setIsMy(1);
                }
                progressListEntity.getProgressEntities().add(progressEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseProgressList", e.getMessage());
        }
        return progressListEntity;
    }
    /**
     * 解析点赞概率
     *
     * @param responseEntity
     * @return
     */
    public ThumbsUpProbabilityEntity parseThumbsUpProbability(ResponseEntity responseEntity) {
        Loger.i(TAG, "parseThumbsUpProbability: " + responseEntity.getJsonObject());
        ThumbsUpProbabilityEntity thumbsUpProbabilityEntity = new ThumbsUpProbabilityEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            thumbsUpProbabilityEntity.setStuId(data.getString("stuId"));
            thumbsUpProbabilityEntity.setProbability(data.getInt("probability"));

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseThumbsUpProbability", e.getMessage());
        }
        return thumbsUpProbabilityEntity;
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teamInfoEntity;
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
                JSONArray jsonArray = data.getJSONArray("starList");
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
            e.printStackTrace();
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


    public ArtsExtLiveInfo parseArtsExtLiveInfo(ResponseEntity responseEntity){
        ArtsExtLiveInfo info = new ArtsExtLiveInfo();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        info.setNewCourseWarePlatform(data.optString("newCourseWarePlatform"));
        return info;
    }
}
