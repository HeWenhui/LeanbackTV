package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.entity.ReleaseedInfos;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.LiveExperienceEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoPointEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSpeedEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BigLivePlayBackEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.xueersi.parentsmeeting.modules.livevideo.dispatcher.DispatcherConfig.LIVE_PLAY_BACK_TUTOR_FLAGE;
import static com.xueersi.parentsmeeting.modules.livevideo.dispatcher.DispatcherConfig.SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE;
import static com.xueersi.parentsmeeting.modules.livevideo.dispatcher.DispatcherConfig.URL_DEFAULT_CHS_H5;

/**
 * Created by dqq on 2019/7/11.
 */
public class DispatcherHttpResponseParser extends HttpResponseParser {


    private String videoPaths;
    private String videopath;
    private String examPaperUrl;
    private String speechEvalUrl;
    private String hostPath;
    private String courseId;
    private String sectionId;
    private String sectionName;
    String[] ptTypeFilters = {"4", "0", "1", "2", "8", "5", "6"};
    private List<String> questiongtype = Arrays.asList(ptTypeFilters);


    public VideoResultEntity parseNewArtsEvent(String stucourseId, String id, VideoResultEntity entity,
                                               ResponseEntity responseEntity) {
        Map<String, VideoSectionEntity> mapSection = new HashMap<String, VideoSectionEntity>();
        if (entity != null && entity.getMapVideoSectionEntity() != null && entity.getMapVideoSectionEntity().size() > 0) {
            for (String key : entity.getMapVideoSectionEntity().keySet()) {
                if (!TextUtils.isEmpty(key) && key.endsWith("_t")) {
                    Map<String, VideoSectionEntity> map = new HashMap<>();
                    mapSection.put(key, entity.getMapVideoSectionEntity().get(key));
                }
            }
        }
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        UmsAgentTrayPreference.getInstance().put(SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE, jsonObject.optString("summerCourseWareSize"));
        VideoSectionEntity section = new VideoSectionEntity();
        List<VideoQuestionEntity> questionLst = new ArrayList<VideoQuestionEntity>();
        VideoQuestionEntity questionEntity = null;
        section.setVideoWebPath(videoPaths);
        section.setVideoPath(videopath);
        section.setHostPath(hostPath);
        section.setExamPaperUrl(examPaperUrl);
        section.setSpeechEvalUrl(speechEvalUrl);
        section.setvCoursseID(courseId);
        section.setvSectionID(sectionId);
        section.setvSectionName(sectionName);
        section.setStuCouId(stucourseId);
        JSONArray questionArray = jsonObject.optJSONArray("events");
        boolean isNewArtsPlatForm = false;
        if (questionArray != null) {
            for (int k = 0; k < questionArray.length(); k++) {
                try {
                    questionEntity = new VideoQuestionEntity();
                    JSONObject questionJson = questionArray.getJSONObject(k);
                    questionEntity.setvQuestionID(questionJson.optString("id"));
                    questionEntity.setvCategory(questionJson.optInt("category"));
                    questionEntity.setvQuestionInsretTime(questionJson.optInt("begintime"));
                    questionEntity.setAnswerDay(questionJson.optString("date"));
                    questionEntity.setvEndTime(questionJson.optInt("endtime"));
                    questionEntity.setUrl(questionJson.optString("url"));
                    questionEntity.setName(questionJson.optString("type"));
                    questionEntity.setvQuestionType(questionJson.optString("type"));


                    questionEntity.setSrcType(questionJson.optString("srcType"));
                    questionEntity.setQuestionNum(questionJson.optInt("num", 1));
                    JSONArray releasedArray = questionJson.optJSONArray("releaseInfos");
                    List<ReleaseedInfos> releaseLst = new ArrayList<ReleaseedInfos>();
                    ReleaseedInfos infos = null;
                    if (releasedArray != null) {
                        for (int i = 0; i < releasedArray.length(); i++) {
                            infos = new ReleaseedInfos();
                            JSONObject infoJson = releasedArray.getJSONObject(i);
                            infos.setId(infoJson.optString("id"));
                            infos.setType(infoJson.optString("type"));
                            infos.setRole(infoJson.optString("role"));
                            infos.setAnswer(infoJson.optString("answer"));
                            infos.setEstimatedTime(infoJson.optString("estimatedTime"));
                            infos.setAssess_ref(infoJson.optString("assess_ref"));
                            infos.setIsVoice(infoJson.optString("isVoice"));
                            infos.setTotalScore(infoJson.optString("totalScore"));
                            // 人为的划分H5Bll和QuestionBll: type为{"4", "0", "1", "2", "8", "5", "6"}的题型走QuestionBll,
                            // 将他们的category置为1001
                            if (questiongtype.contains(releasedArray.getJSONObject(0).optString("type"))) {
                                questionEntity.setvCategory(1001);
                            }
                            // 设置QuestionType,文科回放打点中使用
                            questionEntity.setvQuestionType(releasedArray.getJSONObject(0).optString("type"));
                            // 新增一个判断是否是新课件平台的字段
                            if (1000 == questionEntity.getvCategory() || 1001 == questionEntity.getvCategory()) {
                                isNewArtsPlatForm = true;
                            } else {
                                isNewArtsPlatForm = false;
                            }
                            releaseLst.add(infos);
                        }
                        questionEntity.setReleaseInfos(releaseLst);
                    }
                    questionLst.add(questionEntity);
                } catch (Exception e) {

                }

            }
            if (isNewArtsPlatForm) {
                section.setLstVideoQuestionEntity(questionLst);
                mapSection.put(id, section);
                entity.setMapVideoSectionEntity(mapSection);
            }

        }
        return entity;
    }

    /**
     * 解析直播回放互动题扣除金币
     *
     * @param stuCouId
     * @param responseEntity
     * @return
     */
    public VideoResultEntity deductStuGoldParser(String id, String stuCouId, ResponseEntity responseEntity) {
        VideoResultEntity entity = new VideoResultEntity();
        try {
            MyUserInfoEntity myUserInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
            Map<String, VideoSectionEntity> mapSection = new HashMap<String, VideoSectionEntity>();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//            MediaPlayer.isPSIJK = "1".equals(jsonObject.optString("isNewSDK")) &&
//                    "1".equals(jsonObject.optString("isNewIRC"));
//            ;

            entity.setResultType(jsonObject.optInt("tip"));
            entity.setMsg(jsonObject.optString("msg"));
            JSONArray arrData = jsonObject.optJSONArray("data");
            VideoSectionEntity section = null;
            if (arrData != null) {
                if (arrData.length() > 0) {
                    JSONObject json0 = arrData.getJSONObject(0);
                    MediaPlayer.setIsNewIJK(MediaPlayer.IS_NEW_IJK.equals(json0.optString("isNewSDK")) &&
                            MediaPlayer.IS_NEW_IJK.equals(json0.optString("isNewIRC")));
                }
                for (int i = 0; i < arrData.length(); i++) {
                    JSONObject sectionJson = arrData.getJSONObject(i);
                    int isArts = sectionJson.optInt("isArts");
                    entity.setIsArts(isArts);
                    section = new VideoSectionEntity();
                    JSONObject liveInfo = arrData.optJSONObject(i).optJSONObject("liveInfo");
                    if (liveInfo != null) {
                        if (isArts == 0 || isArts == 2) {//理科多一层data
                            JSONObject infoData = liveInfo.optJSONObject("data");
                            if (infoData != null) {
                                liveInfo = infoData;
                            }
                        }

                        setLiveInfo(liveInfo, entity, section, isArts, stuCouId, sectionJson);
                    }
                    section.setvCoursseID(sectionJson.optString("courseId"));
                    courseId = sectionJson.optString("courseId");
                    section.setvSectionID(sectionJson.optString("id"));
                    sectionId = sectionJson.optString("id");
                    section.setvSectionName(sectionJson.optString("planName"));
                    sectionName = sectionJson.optString("planName");
                    String videoPath = sectionJson.optString("videoPath");
                    JSONArray pathArray = sectionJson.optJSONArray("hostPath");
                    String url = "";
                    if (pathArray != null) {
                        url = pathArray.get(0).toString();
                        section.setVideoWebPath(url + videoPath);
                        videoPaths = url + videoPath;
                    }
                    section.setHostPath(pathArray.toString());
                    hostPath = pathArray.toString();
                    section.setVideoPath(videoPath);
                    videopath = videoPath;
                    section.setExamPaperUrl(sectionJson.optString("examPaperUrl"));
                    examPaperUrl = sectionJson.optString("examPaperUrl");
                    section.setSpeechEvalUrl(sectionJson.optString("speechEvalUrl"));
                    speechEvalUrl = sectionJson.optString("speechEvalUrl");
                    List<VideoQuestionEntity> questionLst = parseEvent(sectionJson, isArts, stuCouId,
                            sectionId, myUserInfoEntity.getStuId(), entity);
                    section.setLstVideoQuestionEntity(questionLst);
                    mapSection.put(sectionJson.optString("id"), section);


                    //解析辅导老师信息
                    VideoSectionEntity tutorEntity = parseTutorSetionEntity(entity, id, jsonObject, url, isArts,
                            stuCouId, myUserInfoEntity.getStuId()
                            , mapSection, liveInfo, sectionJson);
                    if (tutorEntity != null) {
                        tutorEntity.setvStuCourseID(sectionJson.optString("courseId"));
                        tutorEntity.setExamPaperUrl(sectionJson.optString("examPaperUrl"));
                        tutorEntity.setSpeechEvalUrl(sectionJson.optString("speechEvalUrl"));
                        mapSection.put(tutorEntity.getvSectionID(), tutorEntity);
                    }


                }
            }
            entity.setMapVideoSectionEntity(mapSection);
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "deductStuGoldParser:id=" + id, e.getMessage());
            return entity;
        }
        logger.d("deductStuGoldParser" + JSON.toJSONString(entity));
        return entity;
    }

    private void setLiveInfo(JSONObject liveInfo, VideoResultEntity entity, VideoSectionEntity section, int isArts,
                             String stuCouId, JSONObject sectionJson) throws Exception {
        if (liveInfo != null) {
            entity.setIsAllowMarkpoint(liveInfo.optInt("isAllowMarkpoint"));
            //entity.setIsAllowMarkpoint(0);
            entity.setLearning_stage(liveInfo.optString("learning_stage", ""));
            section.setClassId(liveInfo.optString("class_id"));
            section.setStuCouId(stuCouId);
            section.setTeamId(liveInfo.optString("team_id"));
            section.setEducationStage(liveInfo.optString("educationStage"));
            entity.setPattern(liveInfo.optInt("pattern", 1));
            if (isArts == 0) {
                liveInfo.put("getCourseWareHtml", sectionJson.opt("getCourseWareHtml"));
                liveInfo.put("getCourseWareHtmlZhongXueUrl", sectionJson.opt
                        ("getCourseWareHtmlZhongXueUrl"));
            }
            entity.setGetInfoStr(liveInfo.toString());
            section.setEvaluateIsOpen(liveInfo.optInt("evaluateIsOpen"));
            section.setEvaluateTimePer(liveInfo.optDouble("evaluateTimePer"));
            if (liveInfo.has("teacherInfo")) {
                JSONObject mainObject = liveInfo.optJSONObject("teacherInfo");
                if (mainObject != null) {
                    section.setMainTeacherId(mainObject.optString("teacherId"));
                    section.setMainTeacherName(mainObject.optString("teacherName"));
                    section.setMainTeacherImg(mainObject.optString("teacherImg"));
                }
            }
            if (liveInfo.has("counselorInfo")) {
                JSONObject tutorObject = liveInfo.optJSONObject("counselorInfo");
                if (tutorObject != null) {
                    section.setTutorTeacherId(tutorObject.optString("teacherId"));
                    section.setTutorTeacherName(tutorObject.optString("teacherName"));
                    section.setTutorTeacherImg(tutorObject.optString("teacherImg"));
                }
            }
        }
    }

    private List<VideoQuestionEntity> parseEvent(JSONObject sectionJson, int isArts, String stuCouId,
                                                 String sectionId, String stuId, VideoResultEntity entity) {
        List<VideoQuestionEntity> questionLst = new ArrayList<VideoQuestionEntity>();
        JSONArray questionArray = sectionJson.optJSONArray("event");
        VideoQuestionEntity questionEntity = null;

        if (questionArray != null) {
            for (int k = 0; k < questionArray.length(); k++) {
                try {
                    questionEntity = new VideoQuestionEntity();
                    JSONObject questionJson = questionArray.getJSONObject(k);
                    questionEntity.setvQuestionID(questionJson.optString("id"));
                    questionEntity.setvCategory(questionJson.optInt("category"));
                    questionEntity.setvQuestionInsretTime(questionJson.optInt("begintime"));
                    questionEntity.setAnswerDay(questionJson.optString("date"));
                    questionEntity.setvEndTime(questionJson.optInt("endtime"));
                    questionEntity.setUrl(questionJson.optString("url"));
                    questionEntity.setName(questionJson.optString("type"));
                    questionEntity.setvQuestionType(questionJson.optString("type"));
                    questionEntity.setSrcType(questionJson.optString("srcType"));
                    //旧讲义，回放，rolepaly题型，需要解析rols字段
                    questionEntity.setRoles(questionJson.optString("roles", ""));
                    String choiceType = questionJson.optString("choiceType", "1");
                    if ("".equals(choiceType)) {
                        choiceType = "1";
                    }
                    int category = questionJson.optInt("category");
                    if (LocalCourseConfig
                            .CATEGORY_ENGLISH_MULH5COURSE_WARE == category || LocalCourseConfig.CATEGORY_TUTOR_EVENT_35 == category) {
                        questionEntity.setvQuestionType(questionJson.optString("pAttr"));
                        entity.setIsMul(1);
                        AppConfig.isMulLiveBack = true;
                    }
                    questionEntity.setChoiceType(choiceType);
                    VideoPointEntity videoPointEntity = new VideoPointEntity();
                    videoPointEntity.setRelativeTime(questionJson.optInt("begintime"));
                    videoPointEntity.setType(questionJson.optInt("category"));
                    videoPointEntity.setNewType(questionJson.optString("pAttr"));
                    entity.getLstPoint().add(videoPointEntity);
                    questionEntity.setQuestionNum(questionJson.optInt("num", 1));
                    if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE) {
                        String host = isArts == 0 ? ShareBusinessConfig.LIVE_SCIENCE :
                                ShareBusinessConfig.LIVE_LIBARTS;
                        String coursewareH5 = "https://live.xueersi.com/" +
                                host + "/Live/coursewareH5/";

                        if (isArts == 2) {
                            coursewareH5 = URL_DEFAULT_CHS_H5;
                        }
                        questionEntity.setEnglishH5Play_url(coursewareH5 + sectionId + "/"
                                + stuCouId + "/" + questionEntity.getvQuestionID()
                                + "/" + questionEntity.getvQuestionType() + "/" + stuId);
                        String isVoice = questionJson.optString("isVoice", "0");
                        questionEntity.setIsVoice(isVoice);
                        if ("1".equals(isVoice)) {
                            try {
                                JSONObject test_info = questionJson.getJSONObject("test_info");
                                String voiceQuestiontype = test_info.getString("type");
                                questionEntity.setVoiceQuestiontype(voiceQuestiontype);
                                questionEntity.setAssess_ref(questionJson.optString("assess_ref"));
                            } catch (Exception e) {
                                questionEntity.setIsVoice("0");
                            }
                        }
                    } else if (questionEntity.getvCategory() == LocalCourseConfig
                            .CATEGORY_ENGLISH_MULH5COURSE_WARE || LocalCourseConfig.CATEGORY_TUTOR_EVENT_35 == category) {

                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_SUPER_SPEAKER) {
                        questionEntity.setIsupload(questionJson.optInt("isupload"));
                        questionEntity.setAnswerTime(questionJson.optInt("answerTime"));
                        questionEntity.setRecordTime(questionJson.optInt("recordTime"));
                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_QUESTION) {
                        // 填空题
                        if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity.getvQuestionType())
                                || LocalCourseConfig.QUESTION_TYPE_SELECT.equals(questionEntity
                                .getvQuestionType())) {
                            if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity
                                    .getvQuestionType())) {
                                int num = questionJson.optInt("num");
                                Object object = questionJson.get("answer");
                                List<AnswerEntity> anserEntityLst = new ArrayList<>();
                                if (object instanceof JSONArray) {
                                    JSONArray answerArray = (JSONArray) object;
                                    questionEntity.setvBlankSize(answerArray.length());
                                    AnswerEntity answerEntity;
                                    for (int j = 0; j < answerArray.length(); j++) {
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(String.valueOf(j));
                                        answerEntity.setRightAnswer(answerArray.getString(j));
                                        anserEntityLst.add(answerEntity);
                                    }
                                } else if (object instanceof JSONObject) {
                                    JSONObject jsonObject1 = (JSONObject) object;
                                    Iterator<String> keys = jsonObject1.keys();
                                    AnswerEntity answerEntity;
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(key);
                                        answerEntity.setRightAnswer(jsonObject1.getString(key));
                                        anserEntityLst.add(answerEntity);
                                    }
                                } else {
                                    AnswerEntity answerEntity;
                                    for (int j = 0; j < num; j++) {
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(String.valueOf(j));
                                        answerEntity.setRightAnswer("");
                                        anserEntityLst.add(answerEntity);
                                    }
                                }
                                questionEntity.setAnswerEntityLst(anserEntityLst);
                                if (num > 0) {
                                    questionEntity.setvBlankSize(num);
                                } else {
                                    questionEntity.setvBlankSize(anserEntityLst.size());
                                }
                                // 选择题
                            } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(questionEntity
                                    .getvQuestionType())) {
                                questionEntity.setvQuestionAnswer(questionJson.optString("answer"));
                            }
                            String isVoice = questionJson.optString("isVoice", "0");
                            questionEntity.setIsVoice(isVoice);
                            if ("1".equals(isVoice)) {
                                questionEntity.setVoiceQuestiontype(questionEntity.getvQuestionType());
                                questionEntity.setAssess_ref(questionJson.optString("assess_ref"));
                            }
                        } else {
                            if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(questionEntity
                                    .getvQuestionType())) {
                                questionEntity.setIsAllow42(questionJson.optString("isAllow42", "0"));
                                if ("1".equals(questionEntity.getIsAllow42())) {
                                    questionEntity.setSpeechContent(questionJson.optString("answer", ""));
                                    questionEntity.setEstimatedTime(questionJson.optInt("estimatedTime",
                                            0));
                                }
                            }
                        }
                        // 如果互动题结束时间为0，加上默认时间
                        if (questionEntity.getvEndTime() == 0 && questionEntity.getvQuestionInsretTime()
                                != 0) {
                            questionEntity.setvEndTime(questionEntity.getvQuestionInsretTime()
                                    + questionJson.optInt("timer", 0));
                        }
                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_H5COURSE_WARE) {
                        questionEntity.setH5Play_url(questionJson.optString("play_url"));
                    } else if(questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_SCIENCE_VOTE){
                        try {
                            questionEntity.setOrgDataStr(questionJson.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    questionLst.add(questionEntity);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "deductStuGoldParser:id=" + sectionId, e
                            .getMessage());
                }
            }
        }

        if (!questionLst.isEmpty()) {
            logger.i("deductStuGoldParser");
        }
        return questionLst;
    }

    private VideoSectionEntity parseTutorSetionEntity(VideoResultEntity entity, String id, JSONObject jsonObject,
                                                      String url,
                                                      int isArts, String stuCouId,
                                                      String stuId, Map<String, VideoSectionEntity> mapSection,
                                                      JSONObject liveInfo, JSONObject mainJson) throws Exception {
        JSONObject tutorJson = jsonObject.optJSONObject("tutorshipInfo");
        VideoSectionEntity section = null;
        if (tutorJson != null) {
            String questionHost = tutorJson.optString("getCourseWareHtmlCoach");
            UmsAgentTrayPreference.getInstance().put(AppConfig.XES_LIVE_VIDEO_TUTOR_RESULT_HTML, questionHost);
            List<VideoQuestionEntity> questionLst;
            JSONObject json = tutorJson.optJSONObject(id);
            if (json != null) {
                section = new VideoSectionEntity();
                section.setvSectionID(id + LIVE_PLAY_BACK_TUTOR_FLAGE);
                section.setVideoWebPath(url + json.optString("videoPath"));
                questionLst = parseEvent(json, isArts, stuCouId, section.getvSectionID(), stuId, entity);
                setLiveInfo(liveInfo, entity, section, isArts, stuCouId, mainJson);

                section.setLstVideoQuestionEntity(questionLst);
            }
        }
        return section;
    }

    public LiveExperienceEntity deductStuGoldParsers(ResponseEntity responseEntity) {
        //      LiveExperienceEntity resultEntity = JsonUtil.getEntityFromJson(responseEntity.getJsonObject()
        // .toString(),
        // LiveExperienceEntity.class);
        LiveExperienceEntity resultEntity = new LiveExperienceEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();

        MediaPlayer.setIsNewIJK((MediaPlayer.IS_NEW_IJK.equals(jsonObject.optString("isNewSDK")) &&
                MediaPlayer.IS_NEW_IJK.equals(jsonObject.optString("isNewIRC"))));
//        MediaPlayer.isPSIJK = ("1".equals(jsonObject.optString("isNewSDK")) && "1".equals(jsonObject.optString("isNewIRC")));
        resultEntity.setExpLiveType(jsonObject.optInt("expLiveType", 1));
        resultEntity.setHbTime(jsonObject.optInt("hbTime", 60));
        resultEntity.setVisitTimeUrl(jsonObject.optString("visitTimeUrl", ""));

        resultEntity.setPattern(jsonObject.optInt("pattern", 1));
        resultEntity.setExpSciAi(jsonObject.optBoolean("isExpSciAi"));
        resultEntity.setLiveType(jsonObject.optInt("liveType"));
        int isArts = jsonObject.optInt("isArts");
        resultEntity.setIsArts(isArts);
        resultEntity.setClassId(jsonObject.optString("classId"));
        String videoPath = jsonObject.optString("videoPath");
        JSONArray pathArray = jsonObject.optJSONArray("hostPath");
        ArrayList<String> videoPaths = new ArrayList<>();
        if (pathArray != null) {
            String url = null;
            try {
                url = pathArray.get(0).toString();
                resultEntity.setVideoPath(url + videoPath);
                for (int i = 0; i < pathArray.length(); i++) {
                    String itemUrl = pathArray.get(i).toString();
                    videoPaths.add(itemUrl + videoPath);
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        resultEntity.setVideoPaths(videoPaths);
        resultEntity.setExamPaperUrl(jsonObject.optString("examPaperUrl"));
        resultEntity.setTestPaperUrl(jsonObject.optString("testPaperUrl"));
        resultEntity.setSpeechEvalUrl(jsonObject.optString("speechEvalUrl"));

        resultEntity.setSpeechEvalSubmitUrl(jsonObject.optString("speechEvalSubmitUrl"));
        resultEntity.setSubmitCourseWareH5AnswerUseVoiceUrl(jsonObject.optString
                ("submitCourseWareH5AnswerUseVoiceUrl"));
        resultEntity.setInteractUrl(jsonObject.optString("interactUrl"));
        resultEntity.setSubjectiveSubmitUrl(jsonObject.optString("subjectiveSubmitUrl"));
        resultEntity.setCoursewareH5Url(jsonObject.optString("coursewareH5Url"));
        //半身直播 h5页面域名
        resultEntity.setHalfBodyH5Url(jsonObject.optString("halfBodyH5Url"));

        resultEntity.setPaidBannerInfoUrl(jsonObject.optString("getBuyInfoUrl",
                "https://laoshi.xueersi.com/science/AutoLive/buydInfo"));
        resultEntity.setRecommendClassUrl(jsonObject.optString("recommendCourseUrl",
                "https://laoshi.xueersi.com/science/AutoLive/recommendCourse"));
        resultEntity.setExamUrl(jsonObject.optString("examUrl"));
        resultEntity.setSubmitUnderStandUrl(jsonObject.optString("getEnglishInvestigateUrl"));
        resultEntity.setPreK(jsonObject.optBoolean("isPreK", false));
        //体验课新手引导
        resultEntity.setNoviceGuide(jsonObject.optBoolean("noviceGuide", true));
        //学习反馈
        JSONArray jsonArray = jsonObject.optJSONArray("feedbackInfo");
        if (jsonArray != null) {
            ArrayList<LiveExperienceEntity.LearnFeedBack> learnFeedBacks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject feedbackJson = jsonArray.getJSONObject(i);
                    LiveExperienceEntity.LearnFeedBack learnFeedBack = new LiveExperienceEntity.LearnFeedBack();
                    learnFeedBack.setId(feedbackJson.optString("id"));
                    learnFeedBack.setDefaultOption(feedbackJson.optString("default_option"));
                    learnFeedBack.setTitle(feedbackJson.optString("title"));
                    JSONObject optionJSON = feedbackJson.optJSONObject("option");

                    if (optionJSON != null) {
                        HashMap<String, String> mapOption = new LinkedHashMap<>();
                        Iterator<String> iterator = optionJSON.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            String value = optionJSON.getString(key);
                            mapOption.put(key, value);
                        }
                        learnFeedBack.setOptions(mapOption);
                    }
                    learnFeedBacks.add(learnFeedBack);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
            resultEntity.setLearnFeedback(learnFeedBacks);
        }
        resultEntity.setGetSubjectiveTestResultUrl(jsonObject.optString("getSubjectiveTestResultUrl"));
        resultEntity.setId(jsonObject.optString("id"));

        JSONObject autolive = jsonObject.optJSONObject("autoLive");
        LiveExperienceEntity.AutoLive autobean = new LiveExperienceEntity.AutoLive();
        autobean.setStartTime(autolive.optLong("startTime"));
        autobean.setEndTime(autolive.optLong("endTime"));
        autobean.setNowTime(autolive.optLong("nowTime"));
        autobean.setGradId(autolive.optString("grade_id"));
        autobean.setSubjectId(autolive.optString("subject_id"));
        autobean.setTermId(autolive.optString("termId"));

        resultEntity.setAutoLive(autobean);

        JSONObject liveinfo = jsonObject.optJSONObject("liveInfo");
        LiveExperienceEntity.LiveInfo liveBean = new LiveExperienceEntity.LiveInfo();
        liveBean.setStime(liveinfo.optLong("stime"));
        liveBean.setEtime(liveinfo.optLong("etime"));
        liveBean.setStart_time(liveinfo.optString("start_time"));
        liveBean.setEnd_time(liveinfo.optString("end_time"));
        liveBean.setTeacherId(liveinfo.optString("teacher_id"));
        resultEntity.setLiveInfo(liveBean);
        if (jsonObject.has("sciAiEvent")) {
            JSONObject sAiEvent = jsonObject.optJSONObject("sciAiEvent");
            VideoSpeedEntity AiEventBean = new VideoSpeedEntity();
            JSONObject leading = sAiEvent.optJSONObject("leadingStage");
            VideoSpeedEntity.LeadingStage leadingBean = new VideoSpeedEntity.LeadingStage();
            leadingBean.setExistRetell(leading.optBoolean("existRetell"));
            leadingBean.setBeginTime(leading.optInt("beginTime"));
            leadingBean.setEndTime(leading.optInt("endTime"));
            leadingBean.setValidTime(leading.optInt("validTime"));
            AiEventBean.setLeadingStage(leadingBean);
            JSONArray exercises = sAiEvent.optJSONArray("exercises");
            if (exercises != null) {
                ArrayList<VideoSpeedEntity.Exercise> exerciseses = new ArrayList<>();
                for (int i = 0; i < exercises.length(); i++) {
                    try {
                        JSONObject exerciseJson = exercises.getJSONObject(i);
                        VideoSpeedEntity.Exercise exerciseBean = new VideoSpeedEntity.Exercise();
                        exerciseBean.setShare(exerciseJson.optBoolean("isShare"));
                        exerciseBean.setAnswerResult(exerciseJson.optBoolean("answerResult"));
                        exerciseBean.setValidTimeForHard(exerciseJson.optInt("validTimeForHard"));
                        exerciseBean.setValidTimeForEasy(exerciseJson.optInt("validTimeForEasy"));
                        VideoSpeedEntity.Exercise.KnowledgePoint pointBean =
                                new VideoSpeedEntity.Exercise.KnowledgePoint();
                        JSONObject pointJson = exerciseJson.optJSONObject("knowledgePoints");
                        pointBean.setExistRetell(pointJson.optBoolean("existRetell"));
                        pointBean.setBeginTime(pointJson.optInt("beginTime"));
                        pointBean.setEndTime(pointJson.optInt("endTime"));
                        pointBean.setValidTime(pointJson.optInt("validTime"));
                        exerciseBean.setKnowledgePoints(pointBean);
                        JSONArray examplesArray = exerciseJson.optJSONArray("example");
                        if (examplesArray != null) {
                            ArrayList<VideoSpeedEntity.Exercise.Example> examples = new ArrayList<>();
                            for (int j = 0; j < examplesArray.length(); j++) {
                                JSONObject exampleJson = examplesArray.getJSONObject(j);
                                VideoSpeedEntity.Exercise.Example exampleBean = new VideoSpeedEntity.Exercise.Example();
                                exampleBean.setExampleId(exampleJson.optString("exampleId"));
                                JSONObject introduceJson = exampleJson.optJSONObject("introduce");
                                VideoSpeedEntity.Exercise.Example.Introduce introduceBean =
                                        new VideoSpeedEntity.Exercise.Example.Introduce();
                                introduceBean.setExistRetell(introduceJson.optBoolean("existRetell"));
                                introduceBean.setBeginTime(introduceJson.optInt("beginTime"));
                                introduceBean.setEndTime(introduceJson.optInt("endTime"));
                                introduceBean.setValidTime(introduceJson.optInt("validTime"));
                                exampleBean.setIntroduce(introduceBean);
                                JSONObject publishJson = exampleJson.optJSONObject("publish");
                                VideoSpeedEntity.Exercise.Example.Publish publishBean = new VideoSpeedEntity.Exercise.Example.Publish();
                                publishBean.setExistRetell(publishJson.optBoolean("existRetell"));
                                publishBean.setBeginTime(publishJson.optInt("beginTime"));
                                publishBean.setEndTime(publishJson.optInt("endTime"));
                                publishBean.setValidTime(publishJson.optInt("validTime"));
                                exampleBean.setPublish(publishBean);
                                JSONObject interpretJson = exampleJson.optJSONObject("interpret");
                                VideoSpeedEntity.Exercise.Example.Interpret InterpretBean = new VideoSpeedEntity.Exercise.Example.Interpret();
                                InterpretBean.setExistRetell(interpretJson.optBoolean("existRetell"));
                                InterpretBean.setBeginTime(interpretJson.optInt("beginTime"));
                                InterpretBean.setEndTime(interpretJson.optInt("endTime"));
                                InterpretBean.setValidTime(interpretJson.optInt("validTime"));
                                exampleBean.setInterpret(InterpretBean);
                                examples.add(exampleBean);
                            }
                            exerciseBean.setExample(examples);
                        }
                        exerciseses.add(exerciseBean);
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }
                AiEventBean.setExercises(exerciseses);
            }
            JSONObject endingJson = sAiEvent.optJSONObject("endingStage");
            VideoSpeedEntity.EndingStage endingBean = new VideoSpeedEntity.EndingStage();
            endingBean.setExistRetell(endingJson.optBoolean("existRetell"));
            endingBean.setBeginTime(endingJson.optInt("beginTime"));
            endingBean.setEndTime(endingJson.optInt("endTime"));
            endingBean.setValidTime(endingJson.optInt("validTime"));
            AiEventBean.setEndingStage(endingBean);
            resultEntity.setSciAiEvent(AiEventBean);

        }

        //IRC连接地址
        if (jsonObject.has("httpsLiveChatDispatchUrl")) {
            //  获取 连接IRC服务  参数获取接口列表
            ArrayList<String> roomChatCfgServerList = new ArrayList<String>();
            JSONArray chatUrlArray = jsonObject.optJSONArray("httpsLiveChatDispatchUrl");
            for (int index = 0; index < chatUrlArray.length(); index++) {
                String url = chatUrlArray.optString(index, "");
                roomChatCfgServerList.add(url);
            }
            resultEntity.setRoomChatCfgServerList(roomChatCfgServerList);
        }

        if (jsonObject.has("expChatId")) {
            String expChatId = jsonObject.optString("expChatId", "");
            resultEntity.setExpChatId(expChatId);
        }


        if (jsonObject.has("stuInfo")) {
            try {
                JSONObject stuInfoObj = jsonObject.getJSONObject("stuInfo");
                String sex = stuInfoObj.optString("sex");
                resultEntity.setSex(sex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<VideoQuestionEntity> questionEntitys = new ArrayList<>();
        JSONArray questionArray = jsonObject.optJSONArray("event");
        if (questionArray != null) {
            for (int k = 0; k < questionArray.length(); k++) {
                try {
                    if (null == questionArray.getJSONObject(k)) {
                        continue;
                    }
                    JSONObject questionJson = questionArray.getJSONObject(k);
                    VideoQuestionEntity questionEntity = new VideoQuestionEntity();
                    questionEntity.setvQuestionID(questionJson.optString("id"));
                    questionEntity.setvCategory(questionJson.optInt("category"));
                    questionEntity.setvQuestionInsretTime(questionJson.optInt("begintime"));
                    questionEntity.setAnswerDay(questionJson.optString("date"));
                    questionEntity.setvEndTime(questionJson.optInt("endtime"));
                    questionEntity.setvQuestionType(questionJson.optString("type"));
                    questionEntity.setCourseExtInfo(questionJson.optString("type"));
                    questionEntity.setReleasedPageInfos(questionJson.optString("url"));
                    questionEntity.setSrcType(questionJson.optString("srcType"));
                    questionEntity.setDate(questionJson.optString("date"));
                    String choiceType = questionJson.optString("choiceType", "1");
                    questionEntity.setChoiceType(choiceType);

                    if ("".equals(choiceType)) {
                        choiceType = "1";
                    }
                    if (24 == questionJson.optInt("category")) {
                        questionEntity.setvQuestionType(questionJson.optString("pAttr"));
                        AppConfig.isMulLiveBack = true;
                    }
                    questionEntity.setChoiceType(choiceType);

                    questionEntity.setQuestionNum(questionJson.optInt("num", 1));
                    if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE) {

                        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                        String liveId = jsonObject.optString("id");
                        String termId = autolive.optString("termId");
                        String courseWareUrl = questionJson.optString("url");
                        String type = questionJson.optString("type");
                        String testId = questionJson.optString("id");

                        String coursewareH5 = resultEntity.getCoursewareH5Url() + "?stuId=" + stuId + "&liveId=" +
                                liveId + "&type=" + type + "&termId=" + termId + "&courseWareUrl=" + courseWareUrl +
                                "&testId=" + testId + "&";
                        //                        String coursewareH5 = "http://student.xueersi
                        // .com/science/AutoLive/coursewareH5" +
                        // "?stuId=" + stuId + "&liveId=" +
                        //                                liveId + "&type=" + type + "&termId=" + termId +
                        // "&courseWareUrl=" + courseWareUrl +
                        //                                "&testId=" + testId + "&";
                        questionEntity.setEnglishH5Play_url(coursewareH5);

                        String isVoice = questionJson.optString("isVoice", "0");
                        questionEntity.setIsVoice(isVoice);

                        if ("1".equals(isVoice)) {
                            try {
                                JSONObject test_info = questionJson.getJSONObject("test_info");
                                String voiceQuestiontype = test_info.getString("type");
                                questionEntity.setVoiceQuestiontype(voiceQuestiontype);
                                questionEntity.setAssess_ref(questionJson.optString("assess_ref"));
                                questionEntity.setUrl(questionJson.optString("url"));
                            } catch (Exception e) {
                                questionEntity.setIsVoice("0");
                            }
                        }

                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_QUESTION) {
                        // 填空题
                        if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity.getvQuestionType())
                                || LocalCourseConfig.QUESTION_TYPE_SELECT.equals(questionEntity.getvQuestionType())) {
                            if (LocalCourseConfig.QUESTION_TYPE_BLANK.equals(questionEntity.getvQuestionType())) {
                                int num = questionJson.optInt("num");
                                Object object = questionJson.get("answer");
                                List<AnswerEntity> anserEntityLst = new ArrayList<>();
                                if (object instanceof JSONArray) {
                                    JSONArray answerArray = (JSONArray) object;
                                    questionEntity.setvBlankSize(answerArray.length());
                                    AnswerEntity answerEntity;
                                    for (int j = 0; j < answerArray.length(); j++) {
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(String.valueOf(j));
                                        answerEntity.setRightAnswer(answerArray.getString(j));
                                        anserEntityLst.add(answerEntity);
                                    }
                                } else if (object instanceof JSONObject) {
                                    JSONObject jsonObject1 = (JSONObject) object;
                                    Iterator<String> keys = jsonObject1.keys();
                                    AnswerEntity answerEntity;
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(key);
                                        answerEntity.setRightAnswer(jsonObject1.getString(key));
                                        anserEntityLst.add(answerEntity);
                                    }
                                } else {
                                    AnswerEntity answerEntity;
                                    for (int j = 0; j < num; j++) {
                                        answerEntity = new AnswerEntity();
                                        answerEntity.setQuestionId(questionJson.optString("id"));
                                        answerEntity.setAnswerId(String.valueOf(j));
                                        answerEntity.setRightAnswer("");
                                        anserEntityLst.add(answerEntity);
                                    }
                                }
                                questionEntity.setAnswerEntityLst(anserEntityLst);
                                if (num > 0) {
                                    questionEntity.setvBlankSize(num);
                                } else {
                                    questionEntity.setvBlankSize(anserEntityLst.size());
                                }
                                // 选择题
                            } else if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(questionEntity.getvQuestionType
                                    ())) {
                                questionEntity.setQuestionNum(questionJson.optInt("num", 1));
                                questionEntity.setvQuestionAnswer(questionJson.optString("answer"));
                            }
                            String isVoice = questionJson.optString("isVoice", "0");
                            questionEntity.setIsVoice(isVoice);
                            if ("1".equals(isVoice)) {
                                questionEntity.setVoiceQuestiontype(questionEntity.getvQuestionType());
                                questionEntity.setAssess_ref(questionJson.optString("assess_ref"));
                            }
                        } else if (LocalCourseConfig.QUESTION_TYPE_SUBJECT.equals(questionEntity.getvQuestionType())) {
                            String answer = questionJson.optString("answer");
                            int num = questionJson.optInt("num");
                            List<AnswerEntity> anserEntityLst = new ArrayList<>();
                            AnswerEntity answerEntity = new AnswerEntity();
                            answerEntity.setQuestionId(questionJson.optString("id"));
                            answerEntity.setAnswerId("0");
                            answerEntity.setRightAnswer(answer);
                            anserEntityLst.add(answerEntity);
                            questionEntity.setAnswerEntityLst(anserEntityLst);
                            if (num > 0) {
                                questionEntity.setvBlankSize(num);
                            } else {
                                questionEntity.setvBlankSize(anserEntityLst.size());
                            }
                        } else {
                            if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(questionEntity.getvQuestionType())) {
                                questionEntity.setIsAllow42(questionJson.optString("isAllow42", "0"));
                                if ("1".equals(questionEntity.getIsAllow42())) {
                                    questionEntity.setSpeechContent(questionJson.optString("answer", ""));
                                    questionEntity.setEstimatedTime(questionJson.optInt("estimatedTime", 0));
                                }
                            }
                        }
                        // 如果互动题结束时间为0，加上默认时间
                        if (questionEntity.getvEndTime() == 0 && questionEntity.getvQuestionInsretTime() != 0) {
                            questionEntity.setvEndTime(questionEntity.getvQuestionInsretTime()
                                    + questionJson.optInt("timer", 0));
                        }
                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_UNDERSTAND) {
                        /**课中难度调查*/
                        //                        if (questionJson.has("englishInvestigate")) {
                        //                            JSONObject hardJSON = questionJson.optJSONObject
                        // ("englishInvestigate");
                        //                            HashMap<String, String> map = new LinkedHashMap<>();
                        //                            JSONObject optionJSON = hardJSON.optJSONObject("option");
                        //                            if (optionJSON != null) {
                        //                                Iterator<String> iterator = optionJSON.keys();
                        //                                while (iterator.hasNext()) {
                        //                                    String key = iterator.next();
                        //                                    String value = optionJSON.getString(key);
                        //                                    map.put(key, value);
                        //                                }
                        //                            }
                        //                            questionEntity.setUnderStandDifficultyTitle(hardJSON.optString
                        // ("title"));
                        //                            questionEntity.setUnderStandDifficulty(map);
                        //                        }
                    } else if (questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_H5COURSE_WARE) {
                        questionEntity.setH5Play_url(questionJson.optString("play_url"));
                    }
                    questionEntitys.add(questionEntity);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "deductStuGoldParser:id=" + ",i=", e.getMessage());
                }
            }

        }
        resultEntity.setEvent(questionEntitys);
        return resultEntity;
    }

    public static ExpLiveInfo parserExliveInfo(ResponseEntity responseEntity) {

        ExpLiveInfo result = null;

        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONObject data = jsonObject.getJSONObject("expLiveInfo");
            int coachTeacherId = data.getInt("coachTeacherId");
            int expLiveId = data.getInt("expLiveId");
            int expLiveQueryInterval = data.getInt("expLiveQueryInterval");
            String getLiveStatus = data.getString("getLiveStatus");
            int isSignIn = data.getInt("isSignIn");
            int mode = data.getInt("mode");
            String signInUrl = data.getString("signInUrl");
            int videoCutDownTime = data.getInt("videoCutDownTime");
            result = new ExpLiveInfo(coachTeacherId, expLiveId, expLiveQueryInterval, getLiveStatus, isSignIn, mode, signInUrl, videoCutDownTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public PublicEntity publicLiveCourseQuestionParser(ResponseEntity responseEntity) {
        PublicEntity publicLiveCourseEntity = new PublicEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            VideoQuestionEntity questionEntity = null;
            if (jsonObject != null) {
                MediaPlayer.setIsNewIJK("1".equals(jsonObject.optString("isNewSDK")) && "1".equals(jsonObject.optString("isNewIRC")));
                publicLiveCourseEntity.setOnlineNums(jsonObject.optString("onlineNums", "[]"));
                List<VideoQuestionEntity> questionLst = new ArrayList();
                String videoPath = jsonObject.optString("videoPath");
                JSONArray pathArray = jsonObject.optJSONArray("hostPath");
                if (pathArray != null) {
                    String url = pathArray.get(0).toString();
                    publicLiveCourseEntity.setPlayBackUrl(url + videoPath);
                }

                int playVideoTime = 180;
                if (!jsonObject.isNull("hbTime")) {
                    playVideoTime = jsonObject.getInt("hbTime");
                }

                publicLiveCourseEntity.setSendPlayVideoTime(playVideoTime);
                publicLiveCourseEntity.setRadioType(jsonObject.optString("radioType", "600P"));
                JSONArray questionArray = jsonObject.optJSONArray("event");
                if (questionArray != null) {
                    for (int k = 0; k < questionArray.length(); ++k) {
                        questionEntity = new VideoQuestionEntity();
                        JSONObject questionJson = questionArray.getJSONObject(k);
                        questionEntity.setvQuestionID(questionJson.optString("id"));
                        int vCategory = questionJson.optInt("category");
                        questionEntity.setvCategory(vCategory);
                        questionEntity.setvQuestionInsretTime(questionJson.optInt("begintime"));
                        questionEntity.setAnswerDay(questionJson.optString("date"));
                        questionEntity.setvEndTime(questionJson.optInt("endtime"));
                        questionEntity.setvQuestionType(questionJson.optString("type"));
                        questionEntity.setSrcType(questionJson.optString("srcType"));
                        String choiceType = questionJson.optString("choiceType", "1");
                        if ("".equals(choiceType)) {
                            choiceType = "1";
                        }

                        questionEntity.setChoiceType(choiceType);
                        questionEntity.setQuestionNum(questionJson.optInt("num", 1));
                        if ("2".equals(questionEntity.getvQuestionType())) {
                            questionEntity.setvBlankSize(questionJson.optJSONArray("answer").length());
                            List<AnswerEntity> anserEntityLst = new ArrayList();
                            AnswerEntity answerEntity = null;
                            JSONArray answerArray = questionJson.optJSONArray("answer");

                            for (int j = 0; j < answerArray.length(); ++j) {
                                answerEntity = new AnswerEntity();
                                answerEntity.setQuestionId(questionJson.optString("id"));
                                answerEntity.setAnswerId(String.valueOf(j));
                                answerEntity.setRightAnswer(answerArray.getString(j));
                                anserEntityLst.add(answerEntity);
                            }

                            questionEntity.setAnswerEntityLst(anserEntityLst);
                        } else if ("1".equals(questionEntity.getvQuestionType())) {
                            questionEntity.setvQuestionAnswer(questionJson.optString("answer"));
                        }

                        if (questionJson.optInt("category") == 1 && questionEntity.getvEndTime() == 0 && questionEntity.getvQuestionInsretTime() != 0) {
                            questionEntity.setvEndTime(questionEntity.getvQuestionInsretTime() + questionJson.optInt("timer", 0));
                        }

                        if (questionEntity.getvCategory() == 8) {
                            questionEntity.setH5Play_url(questionJson.optString("play_url"));
                        }

                        questionEntity.setvQuestionID(questionJson.optString("id"));
                        questionLst.add(questionEntity);
                    }

                    publicLiveCourseEntity.setLstVideoQuestion(questionLst);
                }

                publicLiveCourseEntity.setStreamTimes(jsonObject.optString("streamTimes", "[]"));
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "publicLiveCourseQuestionParser", e.getMessage());
        }

        return publicLiveCourseEntity;
    }



    /**
     * 解析大班整合回放
     *
     * @param responseEntity
     * @param publicLiveCourseEntity
     */
    public BigLivePlayBackEntity praseBigLiveEnterPlayBack(ResponseEntity responseEntity) {
        BigLivePlayBackEntity playBackEntity = null;
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            playBackEntity = new BigLivePlayBackEntity();
            playBackEntity.setNowTime(data.optLong("nowTime"));
            //解析学生基础信息
            if(data.has("stuInfo")){
                JSONObject stuInfoJsonObj = data.optJSONObject("stuInfo");
                BigLivePlayBackEntity.StuInfo stuInfo = new BigLivePlayBackEntity.StuInfo();
                stuInfo.setId(stuInfoJsonObj.optString("id"));
                stuInfo.setUserName(stuInfoJsonObj.optString("userName"));
                stuInfo.setNickName(stuInfoJsonObj.optString("nickName"));
                stuInfo.setRealName(stuInfoJsonObj.optString("realName"));
                stuInfo.setEnglishName(stuInfoJsonObj.optString("englishName"));
                stuInfo.setSex(stuInfoJsonObj.optInt("sex"));
                stuInfo.setGradeName(stuInfoJsonObj.optString("gradeName"));
                stuInfo.setGradeId(stuInfoJsonObj.optInt("gradeId"));
                stuInfo.setAvatar(stuInfoJsonObj.optString("avatar"));
                stuInfo.setGoldNum(stuInfoJsonObj.optLong("goldNum"));
                if(stuInfoJsonObj.has("psim")){
                    JSONObject psImJsonObj = stuInfoJsonObj.optJSONObject("psim");
                    stuInfo.setPsImId(psImJsonObj.optString("psId"));
                    stuInfo.setPsImPwd(psImJsonObj.optString("psPwd"));
                }
                playBackEntity.setStuInfo(stuInfo);
            }

            //解析学生场次信息
            if(data.has("stuLiveInfo")){

                JSONObject stuLiveInfoJsonObj = data.getJSONObject("stuLiveInfo");
                BigLivePlayBackEntity.StuLiveInfo stuLiveInfo = new BigLivePlayBackEntity.StuLiveInfo();
                stuLiveInfo.setClassId(stuLiveInfoJsonObj.optString("classId"));
                stuLiveInfo.setTeamId(stuLiveInfoJsonObj.optString("teamId"));
                playBackEntity.setStuLiveInfo(stuLiveInfo);
            }

            if(data.has("teamStuIds")){
                JSONArray teamStudIdsJsonArray = data.getJSONArray("teamStuIds");
                if(teamStudIdsJsonArray.length() > 0){
                    List<String> teamStuIdList = new ArrayList<>();
                    for (int i = 0; i < teamStudIdsJsonArray.length(); i++) {
                        teamStuIdList.add(teamStudIdsJsonArray.getString(i));
                    }
                    if(playBackEntity.getStuLiveInfo() != null){
                        playBackEntity.getStuLiveInfo().setTeamStudIds(teamStuIdList);
                    }else{
                        BigLivePlayBackEntity.StuLiveInfo stuLiveInfo = new BigLivePlayBackEntity.StuLiveInfo();
                        stuLiveInfo.setTeamStudIds(teamStuIdList);
                        playBackEntity.setStuLiveInfo(stuLiveInfo);
                    }
                }
            }

            //解析课程场次信息
            if(data.has("planInfo")){
                JSONObject planInfoJsonObj = data.getJSONObject("planInfo");
                BigLivePlayBackEntity.PlanInfo planInfo = new BigLivePlayBackEntity.PlanInfo();
                planInfo.setId(planInfoJsonObj.optString("id"));
                planInfo.setName(planInfoJsonObj.optString("name"));
                planInfo.setType(planInfoJsonObj.optString("type"));
                planInfo.setMode(planInfoJsonObj.optString("mode"));
                planInfo.setPattern(planInfoJsonObj.optString("pattern"));
                planInfo.setsTime(planInfoJsonObj.optLong("stime"));
                planInfo.seteTIme(planInfoJsonObj.optLong("etime"));
                String subjectIdsStr = planInfoJsonObj.optString("subjectIds");
                if(!StringUtils.isEmpty(subjectIdsStr)){
                    String[]ids = subjectIdsStr.split(",");
                    if(ids.length > 0){
                        planInfo.setSubjectIds(Arrays.asList(ids));
                    }
                }

                String gradeIdsStr = planInfoJsonObj.optString("gradeIds");
                if(!StringUtils.isEmpty(gradeIdsStr)){
                    String []ids = gradeIdsStr.split(",");
                    if(ids.length > 0){
                        planInfo.setGradeIds(Arrays.asList(ids));
                    }
                }
                playBackEntity.setPlanInfo(planInfo);
            }


            //解析主讲老师信息
            if(data.has("teacherInfo")){
                JSONObject teacherInfoJsonObj = data.getJSONObject("teacherInfo");
                BigLivePlayBackEntity.TeacherInfo teacherInfo = new BigLivePlayBackEntity.TeacherInfo();
                teacherInfo.setId(teacherInfoJsonObj.optString("id"));
                teacherInfo.setName(teacherInfoJsonObj.optString("name"));
                teacherInfo.setType(teacherInfoJsonObj.optString("type"));
                teacherInfo.setNickName(teacherInfoJsonObj.optString("nickName"));
                teacherInfo.setSex(teacherInfoJsonObj.optString("sex"));
                teacherInfo.setAvatar(teacherInfoJsonObj.optString("avatar"));
                teacherInfo.setAreaName(teacherInfoJsonObj.optString("areaName"));
                teacherInfo.setBranchName(teacherInfoJsonObj.optString("branchName"));

                playBackEntity.setMainTeacher(teacherInfo);
            }

            // 解析辅导老师信息
            if(data.has("counselorInfo")){
                JSONObject counselorInfoJsonObj = data.getJSONObject("counselorInfo");
                BigLivePlayBackEntity.TeacherInfo teacherInfo = new BigLivePlayBackEntity.TeacherInfo();

                teacherInfo.setId(counselorInfoJsonObj.optString("id"));
                teacherInfo.setName(counselorInfoJsonObj.optString("name"));
                teacherInfo.setType(counselorInfoJsonObj.optString("type"));
                teacherInfo.setNickName(counselorInfoJsonObj.optString("nickName"));
                teacherInfo.setSex(counselorInfoJsonObj.optString("sex"));
                teacherInfo.setAvatar(counselorInfoJsonObj.optString("avatar"));
                teacherInfo.setAreaName(counselorInfoJsonObj.optString("areaName"));
                teacherInfo.setBranchName(counselorInfoJsonObj.optString("branchName"));

                playBackEntity.setCounselorTeacher(teacherInfo);
            }

            // 解析配置信息
            if(data.has("configs")){
                JSONObject configsJsonObj = data.getJSONObject("configs");
                BigLivePlayBackEntity.Configs configs = new BigLivePlayBackEntity.Configs();
                configs.setAppId(configsJsonObj.optString("appId"));
                configs.setAppKey(configsJsonObj.optString("appKey"));
                configs.setVideoFile(configsJsonObj.optString("videoFile"));
                JSONObject urlsJsonObj = configsJsonObj.optJSONObject("urls");
                //解析回放 相关接口信息
                if(urlsJsonObj != null){
                    configs.setGetChatRecordUrl(urlsJsonObj.optString("getChatRecordUrl"));
                    configs.setGetMetadataUrl(urlsJsonObj.optString("getMetadataUrl"));
                    configs.setInitModuleUrl(urlsJsonObj.optString("initModuleUrl"));
                }
                configs.setIrcRoomsJson(configsJsonObj.optJSONArray("ircRooms").toString());
                playBackEntity.setConfigs(configs);
            }


        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "praseBigLiveEnterPlayBack", e.getMessage());
        }
        return playBackEntity;
    }

    /**
     * 大班整合-讲座灰度场次确认
     * @param responseEntity
     * @return
     */
    public int parserPublicResult(ResponseEntity responseEntity) {
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if (jsonObject != null) {
            try {
                return jsonObject.optInt("status");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 大班整合-直播 灰度场次确认
     * @param responseEntity
     * @return
     */
    public int parseBigLivePlanVersion(ResponseEntity responseEntity){
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if (jsonObject != null) {
            try {
                return jsonObject.optInt("planVersion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }



}
