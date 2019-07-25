package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.AnswerEntity;
import com.xueersi.common.entity.ReleaseedInfos;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoPointEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LiveTransferHttpResponseParser extends HttpResponseParser {

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
                            sectionId, LiveAppUserInfo.getInstance().getStuId(), entity);
                    section.setLstVideoQuestionEntity(questionLst);
                    mapSection.put(sectionJson.optString("id"), section);


                    //解析辅导老师信息
                    VideoSectionEntity tutorEntity = parseTutorSetionEntity(entity, id, jsonObject, url, isArts,
                            stuCouId, LiveAppUserInfo.getInstance().getStuId()
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
                section.setvSectionID(id + LiveVideoConfig.LIVE_PLAY_BACK_TUTOR_FLAGE);
                section.setVideoWebPath(url + json.optString("videoPath"));
                questionLst = parseEvent(json, isArts, stuCouId, section.getvSectionID(), stuId, entity);
                setLiveInfo(liveInfo, entity, section, isArts, stuCouId, mainJson);

                section.setLstVideoQuestionEntity(questionLst);
            }
        }
        return section;
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
                        String coursewareH5 = LiveHttpConfig.LIVE_HOST + "/" + host + "/Live/coursewareH5/";

                        if (isArts == 2) {
                            coursewareH5 = LiveVideoConfig.URL_DEFAULT_CHS_H5;
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
        UmsAgentTrayPreference.getInstance().put(LiveVideoConfig.SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE, jsonObject.optString("summerCourseWareSize"));
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

}
