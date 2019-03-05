package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.entity.EvaluateOptionEntity;

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
}
