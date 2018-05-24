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
            rolePlayerEntity.setPullDZCount(0);//每次试题返回的时候，将点赞置为0
            rolePlayerEntity.getLstRolePlayerMessage().clear();//在试题信息返回的时候先清空数据集合，防止当服务器返回重复数据的时候，本地也出现重复
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONObject objContent = jsonObject.optJSONObject("content");
            int minute = jsonObject.optInt("rolePlayTime");
            //对话信息的数据实体中需要存入试题id
            String test_id = jsonObject.optString("test_id");
            rolePlayerEntity.setCountDownSecond(minute * 60);
            JSONArray arrSpeech = objContent.optJSONArray("speeches");
            for (int i = 0; i < arrSpeech.length(); i++) {
                JSONObject objMsg = arrSpeech.getJSONObject(i);
                String roleName = objMsg.optString("role");
                String msgContent = objMsg.optString("text");
                int maxTime = objMsg.optInt("time");

                //不满三秒，也要保证至少三秒，统一由服务器计算好返回
               /* if (maxTime < 3) {
                    maxTime = (msgContent.length()/5) +3;
                }*/

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
                msg.setTestId(test_id);//只是记录点击对话的日志用
                //msg.setWebVoiceUrl(objMsg.optString("audio"));
                msg.setPosition(i);
                rolePlayerEntity.getLstRolePlayerMessage().add(msg);
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserRolePlayTestInfos", e.getMessage());
        }
    }

}
