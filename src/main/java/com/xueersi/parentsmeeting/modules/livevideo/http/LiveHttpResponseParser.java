package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.parentsmeeting.http.HttpResponseParser;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.parentsmeeting.http.ResponseEntity;

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

    /** 解析getInfo */
    public LiveGetInfo parseLiveGetInfo(JSONObject data, LiveTopic liveTopic, int liveType, int from) {
        try {
            LiveGetInfo getInfo = new LiveGetInfo(liveTopic);
            getInfo.setId(data.getString("id"));
            getInfo.setName(data.getString("name"));
            getInfo.setInstructions(data.getString("instructions"));
            getInfo.setNotice(data.getString("notice"));
            getInfo.setLiveType(data.getInt("liveType"));
            getInfo.setLiveTime(data.getString("liveTime"));
            getInfo.setNowTime(data.getDouble("nowTime"));
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
                getInfo.setStudentLiveInfo(studentLiveInfoEntity);
                int mode = studentLiveInfo.optInt("mode", 0);
                liveTopic.setMode(mode == 0 ? LiveTopic.MODE_TRANING : LiveTopic.MODE_CLASS);
            }
            if (liveType == LiveBll.LIVE_TYPE_LIVE) {
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
            getInfo.setTestPaperUrl(data.optString("testPaperUrl", "http://live.xueersi.com/Live/getMultiTestPaper"));
            getInfo.setBlockChinese(data.optInt("blockChinese", 0) == 1);
            getInfo.setSubjectiveTestAnswerResult(data.optString("getSubjectiveTestResultUrl", "https://live.xueersi.com/Live/subjectiveTestAnswerResult/" + getInfo.getId()));
            LiveGetInfo.TotalOpeningLength totalOpeningLength = new LiveGetInfo.TotalOpeningLength();
            Object getTotalOpeningLengthObj = data.opt("getTotalOpeningLength");
            if (getTotalOpeningLengthObj instanceof JSONObject) {
                JSONObject getTotalOpeningLength = (JSONObject) getTotalOpeningLengthObj;
                totalOpeningLength.duration = getTotalOpeningLength.getDouble("duration");
                totalOpeningLength.speakingLen = getTotalOpeningLength.optString("speaking_len");
                totalOpeningLength.speakingNum = getTotalOpeningLength.optInt("speaking_num", 0);
            }
            getInfo.setTotalOpeningLength(totalOpeningLength);
            return getInfo;
        } catch (JSONException e) {
            Loger.e(TAG, "parseLiveGetInfo", e);
            MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", e.getMessage());
        }
        return null;
    }

    /** 解析直播服务器 */
    public PlayServerEntity parsePlayerServer(JSONObject object) {
        PlayServerEntity server = new PlayServerEntity();
        try {
            server.setAppname(object.getString("appname"));
            server.setCcode(object.optString("ccode"));
            server.setCode(object.optInt("code"));
            server.setIcode(object.optString("icode"));
            server.setPcode(object.optString("pcode"));
            server.setRtmpkey(object.optString("rtmpkey"));
            JSONArray playserverArray = object.getJSONArray("playserver");
            List<PlayserverEntity> playserver = new ArrayList<PlayserverEntity>();
            for (int i = 0; i < playserverArray.length(); i++) {
                PlayserverEntity entity = new PlayserverEntity();
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
                playserver.add(entity);
            }
            server.setPlayserver(playserver);
            return server;
        } catch (JSONException e) {
            MobAgent.httpResponseParserError(TAG, "parsePlayerServer", e.getMessage());
        }
        return null;
    }

    /** 解析直播h、缓存数据 */
    public LiveTopic parseLiveTopic(LiveTopic oldLiveTopic, JSONObject liveTopicJson, int type) throws JSONException {
        LiveTopic liveTopic = new LiveTopic();
        if (type != LiveBll.LIVE_TYPE_LIVE) {
            liveTopic.setMode(LiveTopic.MODE_CLASS);
        }
        if (type == LiveBll.LIVE_TYPE_LIVE && liveTopicJson.has("room_2")) {
            JSONObject status = liveTopicJson.getJSONObject("room_2");
            RoomStatusEntity coachStatusEntity = liveTopic.getCoachRoomstatus();
            coachStatusEntity.setMode(status.getString("mode"));
            coachStatusEntity.setOpenchat(status.getBoolean("openchat"));
            coachStatusEntity.setCalling(status.getBoolean("isCalling"));
            if (status.has("link_mic")) {
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
        }
        if (liveTopicJson.has("room_1")) {
            JSONObject status = liveTopicJson.getJSONObject("room_1");
            RoomStatusEntity mainStatusEntity = liveTopic.getMainRoomstatus();
            mainStatusEntity.setId(status.getInt("id"));
            mainStatusEntity.setClassbegin(status.getBoolean("classbegin"));
            mainStatusEntity.setOpenbarrage(status.getBoolean("openbarrage"));
            mainStatusEntity.setOpenchat(status.getBoolean("openchat"));
            if (status.has("exam")) {
                mainStatusEntity.setHaveExam(true);
                JSONObject jsonObject = status.getJSONObject("exam");
                mainStatusEntity.setExamStatus(jsonObject.optString("status", "off"));
                mainStatusEntity.setExamNum(jsonObject.optString("num", "0"));
            } else {
                mainStatusEntity.setHaveExam(false);
            }
//            if (status.has("vote")) {
//                JSONObject vote = status.getJSONObject("vote");
//                LiveTopic.VoteEntity voteEntity;
//                try {
//                    voteEntity = new LiveTopic.VoteEntity();
//                    voteEntity.setChoiceId(vote.getString("choiceId"));
//                    voteEntity.setChoiceType(vote.getInt("choiceType"));
//                    voteEntity.setChoiceNum(vote.getInt("choiceNum"));
//                    ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
//                    JSONArray result = vote.getJSONArray("result");
//                    for (int i = 0; i < result.length(); i++) {
//                        LiveTopic.VoteResult voteResult = new LiveTopic.VoteResult();
//                        voteResult.setPople(result.getInt(i));
//                        voteResults.add(voteResult);
//                    }
//                } catch (Exception e) {
//                    voteEntity = null;
//                }
//                mainStatusEntity.setVoteEntity(voteEntity);
//            }
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
            return null;
        }
        return speechEvalEntity;
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
            e.printStackTrace();
        }
        return starAndGoldEntity;
    }
}
