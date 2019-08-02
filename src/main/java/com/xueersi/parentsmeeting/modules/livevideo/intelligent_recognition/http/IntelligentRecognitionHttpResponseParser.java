package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechScoreEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class IntelligentRecognitionHttpResponseParser {

    private String TAG = "IntelligentRecognitionHttpResponseParser";
    Logger logger = LoggerFactory.getLogger(TAG);
//    private LiveHttpResponseParser liveHttpResponseParser;

    public IEResult parseIEResponse(ResponseEntity responseEntity) {
        IEResult ieResult = new IEResult();
        JSONObject json = (JSONObject) responseEntity.getJsonObject();
        ieResult.setAnswered(json.optString("answered"));
        ieResult.setLiveId(json.optString("liveId"));
        ieResult.setStuId(json.optString("stuId"));
        ieResult.setStuCouId(json.optString("stuCouId"));
        ieResult.setMaterialId(json.optString("materialId"));
        ieResult.setMaterialName(json.optString("materialName"));
        ieResult.setMateriaTypeId(json.optString("materiaTypeId"));
        ieResult.setContent(json.optString("content"));
        ieResult.setSetAnswerTime(json.optString("setAnswerTime"));
        ieResult.setAnswered(json.optString(""));
        ieResult.setAnswered(json.optString(""));
        JSONObject itemJson = json.optJSONObject("resource");
        if (itemJson != null) {
            JSONObject audioJSON = itemJson.optJSONObject("audio");
            HashMap<String, String> map = new HashMap<>();
            if (audioJSON != null) {
                Iterator iterator = audioJSON.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (key != null) {
                        key = key.toLowerCase();
                    }
                    String value = audioJSON.optString(key);
                    if (value != null) {
                        value = value.toLowerCase();
                    }
                    if (hasChar(key)) {
//                        logger.i("parseIEResponse word key:" + key + " value:" + value);
                        map.put(key, value);
                    } else {
//                        logger.i("parseIEResponse sentence key:" + key + " value:" + value);
                        ieResult.setSentence(value);
                    }
                }
                ieResult.setAudioHashMap(map);
            }
            String imgSrc = audioJSON.optString("img");
            ieResult.setImgSrc(imgSrc);
        }
        return ieResult;
    }


    private boolean hasDigit(String fileName) {
        for (int ii = 0; ii < fileName.length(); ii++) {
            char charA = fileName.charAt(ii);
            if (charA >= '0' && charA <= '9') {
                return true;
            }
        }
        return false;
    }

    private boolean hasChar(String fileName) {
        for (int ii = 0; ii < fileName.length(); ii++) {
            char charA = fileName.charAt(ii);
            if (charA == '.') {
                break;
            }
            if ((charA >= 'a' && charA <= 'z') || (charA >= 'A' && charA <= 'Z')) {
                return true;
            }
        }
        return false;
    }

    public SpeechScoreEntity parseSpeechScore(ResponseEntity responseEntity) {
        SpeechScoreEntity speechScoreEntity = new SpeechScoreEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if (jsonObject != null) {
            speechScoreEntity.setScore(jsonObject.optString("score"));
            speechScoreEntity.setGold(jsonObject.optString("gold"));
            speechScoreEntity.setStar(jsonObject.optString("star"));
        }
        return speechScoreEntity;
    }

    public GoldTeamStatus parseSpeechTeamRank(ResponseEntity responseEntity, String stuId) {
        GoldTeamStatus entity = new GoldTeamStatus();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray stuList = jsonObject.optJSONArray("stuList");
//            String avatar_path = mGetInfo.getHeadImgPath();
            String avatar_path;
            if (stuList != null) {
                for (int i = 0; i < stuList.length(); i++) {
                    try {
                        JSONObject stu = stuList.getJSONObject(i);
                        GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                        String stuId2 = stu.getString("stuId");
                        student.setMe(stuId != null && stuId.equals(stuId2));
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

    public void parseSubmitIntellectVoiceCorrect(ResponseEntity responseEntity) {

    }
}
