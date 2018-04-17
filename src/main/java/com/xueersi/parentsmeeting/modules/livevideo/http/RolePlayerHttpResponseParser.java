package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.http.HttpResponseParser;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * RolePlayer解析
 * Created by zouhao on 2018/4/13.
 */

public class RolePlayerHttpResponseParser extends HttpResponseParser {


    /**
     * 解析roleplay试题信息
     *
     * @param responseEntity
     * @return
     */
    public void parserRolePlayTestInfos(ResponseEntity responseEntity, RolePlayerEntity rolePlayerEntity) {
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONObject objContent = jsonObject.optJSONObject("content");
            int minute = jsonObject.optInt("rolePlayTime");
            rolePlayerEntity.setCountDownSecond(minute * 60);
            JSONArray arrSpeech = objContent.optJSONArray("speeches");
            for (int i = 0; i < arrSpeech.length(); i++) {
                JSONObject objMsg = arrSpeech.getJSONObject(i);
                String roleName = objMsg.optString("role");
                String msgContent = objMsg.optString("text");
                int maxTime = objMsg.optInt("time");
                RolePlayerEntity.RolePlayerHead head = rolePlayerEntity.getMapRoleHeadInfo().get(roleName);
                if (head == null) {
                    head = new RolePlayerEntity.RolePlayerHead();
                    head.setRoleName("角色");
                    head.setNickName("昵称");
                }
                if (head.isSelfRole()) {
                    rolePlayerEntity.setSelfLastIndex(i);
                }
                RolePlayerEntity.RolePlayerMessage msg = new RolePlayerEntity.RolePlayerMessage(head, msgContent, maxTime);
                //msg.setWebVoiceUrl(objMsg.optString("audio"));
                msg.setPosition(i);
                rolePlayerEntity.getLstRolePlayerMessage().add(msg);
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserRolePlayTestInfos", e.getMessage());
        }
    }

}
