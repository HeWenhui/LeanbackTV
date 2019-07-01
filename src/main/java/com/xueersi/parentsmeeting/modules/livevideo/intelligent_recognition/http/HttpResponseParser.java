package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class HttpResponseParser {

    public IEResult parseResponse(ResponseEntity responseEntity) {
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
                    String value = audioJSON.optString("key");
                    map.put(key, value);
                }
                ieResult.setAudioHashMap(map);
            }
            String imgSrc = audioJSON.optString("img");
            ieResult.setImgSrc(imgSrc);
        }
        return ieResult;
    }
}
