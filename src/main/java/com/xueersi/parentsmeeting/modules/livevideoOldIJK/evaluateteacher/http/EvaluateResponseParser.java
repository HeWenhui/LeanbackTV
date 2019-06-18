package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.EvaluateContent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.entity.EvaluateOptionEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by：WangDe on 2018/12/3 16:27
 */
public class EvaluateResponseParser {

    public EvaluateOptionEntity parseEvaluateInfo(ResponseEntity responseEntity) {
        EvaluateOptionEntity evaluateOptionEntity = new EvaluateOptionEntity();

        JSONObject data = (JSONObject) responseEntity.getJsonObject();

        JSONArray evaScoreArray = data.optJSONArray("evaluateScore");
        if (evaScoreArray != null){
            Map<String, String> evaScoreMap = new HashMap<>();
            try {
                evaScoreMap.put("choose1", evaScoreArray.getString(0));
                evaScoreMap.put("choose2", evaScoreArray.getString(1));
                evaScoreMap.put("choose3", evaScoreArray.getString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            evaluateOptionEntity.setEvaluateScore(evaScoreMap);
        }

        JSONObject evaMainJson = data.optJSONObject("teacherEvaluOption");
        if (evaMainJson != null){
            Map<String, List<String>> evaMainMap = new HashMap<>();
            evaMainMap.put("choose1", parseOption(evaMainJson, "choose1"));
            evaMainMap.put("choose2", parseOption(evaMainJson, "choose2"));
            evaMainMap.put("choose3", parseOption(evaMainJson, "choose3"));
            evaluateOptionEntity.setTeacherEvaluOption(evaMainMap);
        }

        JSONObject evaTutorJson = data.optJSONObject("tutorEvaluOption");
        if (evaTutorJson != null){
            Map<String, List<String>> evaTutorMap = new HashMap<>();
            evaTutorMap.put("choose1", parseOption(evaTutorJson, "choose1"));
            evaTutorMap.put("choose2", parseOption(evaTutorJson, "choose2"));
            evaTutorMap.put("choose3", parseOption(evaTutorJson, "choose3"));
            evaluateOptionEntity.setTutorEvaluOption(evaTutorMap);
        }


        return evaluateOptionEntity;
    }

    private List<String> parseOption(JSONObject evaJson, String index) {
        List<String> evaList = new ArrayList<>();
        JSONArray evaMainArray1 = evaJson.optJSONArray(index);
        for (int i = 0; i < evaMainArray1.length(); i++) {
            try {
                evaList.add(evaMainArray1.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return evaList;
    }

    public FeedBackEntity parseFeedBackContent(ResponseEntity responseEntity) {
        FeedBackEntity feedBackEntity = null;
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        if(jsonObject !=null) {
            JSONObject mainJson =  jsonObject.optJSONObject("evluateConf");
            if (mainJson ==null || mainJson.optInt("evaluateIsOpen") !=1) {
                return null ;
            }
            feedBackEntity = new FeedBackEntity();
            feedBackEntity.setHaveTutor(mainJson.optInt("isHavecounselor")==1);
            feedBackEntity.setHaveInput(mainJson.optInt("isHaveInput")==1);
            feedBackEntity.setEvaluateTime(mainJson.optLong("evaluateTime"));
            feedBackEntity.setEvaluateTimePer(mainJson.optDouble("evaluateTimePer"));
            JSONObject contentjson = jsonObject.optJSONObject("evaluateContent");
            if(contentjson ==null) {
                return null;
            }
            JSONObject teacherJson = contentjson.optJSONObject("teacherEvaluOption");
            if (teacherJson == null) {
                return null;
            }
            parseFeedbackContent(teacherJson.optJSONArray("choose1"),false,feedBackEntity.getMainContentList());
            parseFeedbackContent(teacherJson.optJSONArray("choose2"),false,feedBackEntity.getMainContentList());
            parseFeedbackContent(teacherJson.optJSONArray("choose3"),false,feedBackEntity.getMainContentList());
            JSONObject tutorJson = contentjson.optJSONObject("tutorEvaluOption");
            if (tutorJson != null) {
                parseFeedbackContent(tutorJson.optJSONArray("choose1"),false,feedBackEntity.getTutorContentList());
                parseFeedbackContent(tutorJson.optJSONArray("choose2"),false,feedBackEntity.getTutorContentList());
                parseFeedbackContent(tutorJson.optJSONArray("choose3"),false,feedBackEntity.getTutorContentList());
            }

        }

        return feedBackEntity;
    }
    private void  parseFeedbackContent(JSONArray jsonArray, boolean isFirst,
                                       List<List<EvaluateContent>> contentList){

        if(jsonArray ==null || jsonArray.length()==0) {
            return ;
        }
        List<EvaluateContent> list = new ArrayList<>();
        EvaluateContent content = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            content = new EvaluateContent();
            content.setText(jsonArray.optString(i));
            if(isFirst && i==0) {
                content.setSelectFlag(true);
            }
            list.add(content);
        }
        contentList.add(list);
    }
}
