package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbLoginEntity;

import org.json.JSONObject;

/**
 * NB 实验 接口解析
 *
 * @author chekun
 * created  at 2019/4/3 16:59
 */
public class NbHttpResponseParser extends HttpResponseParser {

    /**
     * 解析Nb 登录
     *
     * @param responseEntity
     * @return
     */
    public static NbLoginEntity parseNbLogin(ResponseEntity responseEntity) {
        NbLoginEntity entity = new NbLoginEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setToken(jsonObject.optString("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }


    /**
     * 解析Nb 试题信息
     *
     * @param responseEntity
     * @return
     */
    public static NbCourseWareEntity parseNbTestInfo(ResponseEntity responseEntity) {
        NbCourseWareEntity entity = new NbCourseWareEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setExperimentId(jsonObject.optString("experimentId"));
            entity.setExperimentName(jsonObject.optString("name", ""));
            entity.setAnswer(jsonObject.optString("isAnswer", "").equals("1"));
            entity.setUrl(jsonObject.optString("play_url", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
