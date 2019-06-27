package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import com.xueersi.common.http.ResponseEntity;

import org.json.JSONObject;

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
        ieResult.setAnswered(json.optString(""));
        ieResult.setAnswered(json.optString(""));
        ieResult.setAnswered(json.optString(""));
        JSONObject itemJson = json.optJSONObject("resource");
        IEResult.ResourceBean resourceBean = new IEResult.ResourceBean();
        if (itemJson != null) {
//            resourceBean.setBall(itemJson.optString());

        }
        ieResult.setResource(resourceBean);
        return ieResult;
    }
}
