package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * RolePlayer解析
 * Created by zouhao on 2018/4/13.
 */

public class RolePlayerHttpResponseParser extends HttpResponseParser {


    /**
     * 解析文科旧讲义 多人试题信息
     *
     * @param responseEntity
     * @return
     */
    public void parserMutRolePlayTestInfos(ResponseEntity responseEntity, RolePlayerEntity rolePlayerEntity) {
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
            MobAgent.httpResponseParserError(TAG, "parserMutRolePlayTestInfos", e.getMessage());
        }
    }


    /**
     * 解析文科旧讲义roleplay分组和试题信息
     *
     * @param responseEntity
     * @return
     */
    public RolePlayerEntity parserRolePlayGroupAndTestInfos(ResponseEntity responseEntity) {
        try {
            RolePlayerEntity rolePlayerEntity = new RolePlayerEntity();
            rolePlayerEntity.setPullDZCount(0);//每次试题返回的时候，将点赞置为0
            rolePlayerEntity.getLstRolePlayerMessage().clear();//在试题信息返回的时候先清空数据集合，防止当服务器返回重复数据的时候，本地也出现重复
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();

            JSONObject objContent = jsonObject.optJSONObject("content");
            int minute = jsonObject.optInt("rolePlayTime");

            //自己的角色名字
            String releaseRole = jsonObject.optString("releaseRole");

            //对话信息的数据实体中需要存入试题id
            String test_id = jsonObject.optString("test_id");
            rolePlayerEntity.setTestId(test_id);
            rolePlayerEntity.setCountDownSecond(minute * 60);

            //对话信息
            JSONArray arrSpeech = objContent.optJSONArray("speeches");

            //角色信息
            JSONArray arrRoles = objContent.optJSONArray("roles");

            //所有对话的音频地址
            JSONObject objAudio =  jsonObject.optJSONObject("audio");
            JSONObject objStems = objAudio.optJSONObject("stem");
            for(int i = 0; i<arrRoles.length();i++){
                JSONObject objRole = arrRoles.getJSONObject(i);
                String roleName = objRole.optString("role");
                String nickName = objRole.optString("name");
                String img = objRole.optString("img");
                RolePlayerEntity.RolePlayerHead rolePlayerHead = new RolePlayerEntity.RolePlayerHead();
                rolePlayerHead.setRoleName(roleName);
                rolePlayerHead.setNickName(nickName);
                rolePlayerHead.setHeadImg(img);
                //通过“releaseRole”字段来判断“我”的角色
                if(roleName.equals(releaseRole)){
                    rolePlayerHead.setSelfRole(true);
                }
                //将分组信息放到map，list中
                rolePlayerEntity.getMapRoleHeadInfo().put(roleName,rolePlayerHead);
                rolePlayerEntity.getLstRoleInfo().add(rolePlayerHead);
            }

            for (int i = 0; i < arrSpeech.length(); i++) {
                JSONObject objMsg = arrSpeech.getJSONObject(i);
                String roleName = objMsg.optString("role");
                String msgContent = objMsg.optString("text");
                int maxTime = objMsg.optInt("time");
                //“9.mp3”
                String audio = objMsg.optString("audio");
                //http://resource.xxyys.com/test_library/audio/2017/11/07/t_20125_9.mp3?1510050714

                String realAudio = null;
                if(objAudio != null){
                    if(objStems != null){
                        realAudio = objStems.optString(audio);
                    }
                }
                //不满三秒，也要保证至少三秒，统一由服务器计算好返回
               /* if (maxTime < 3) {
                    maxTime = (msgContent.length()/5) +3;
                }*/

                //解析message
                RolePlayerEntity.RolePlayerHead head = rolePlayerEntity.getMapRoleHeadInfo().get(roleName);
                if (head == null) {
                    head = new RolePlayerEntity.RolePlayerHead();
                    head.setRoleName("角色");
                    head.setNickName("昵称");
                }
                if (head.isSelfRole()) {
                    rolePlayerEntity.setSelfLastIndex(i);
                }
                RolePlayerEntity.RolePlayerMessage msg = new RolePlayerEntity.RolePlayerMessage(head, msgContent, maxTime,realAudio);
                if(!head.isSelfRole()){
                    msg.setWebVoiceUrl(realAudio);//只记录别人的网络音频地址（非阿里云地址）
                }

                msg.setTestId(test_id);//只是记录点击对话的日志用
                //msg.setWebVoiceUrl(objMsg.optString("audio"));
                msg.setPosition(i);
                rolePlayerEntity.getLstRolePlayerMessage().add(msg);
            }
            return rolePlayerEntity;
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserRolePlayGroupAndTestInfos", e.getMessage());
            logger.i(" e.getMessage():"+ e.getMessage());
            return null;
        }


    }

    /**
     * 解析文科新课件平台多人试题信息
     *
     * @param responseEntity
     * @return
     */
    public RolePlayerEntity parserNewArtsMutRolePlayTestInfos(ResponseEntity responseEntity, RolePlayerEntity rolePlayerEntity) {
        try {
            rolePlayerEntity.setPullDZCount(0);//每次试题返回的时候，将点赞置为0
            rolePlayerEntity.getLstRolePlayerMessage().clear();//在试题信息返回的时候先清空数据集合，防止当服务器返回重复数据的时候，本地也出现重复
            rolePlayerEntity.setNewArts(true); // 判断是否是文科新课件平台的标识
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONObject objContent = jsonObject.optJSONObject("content");
            int minute = jsonObject.optInt("rolePlayTime");
            //对话信息的数据实体中需要存入试题id
            String test_id = jsonObject.optString("testId");
            rolePlayerEntity.setTestId(test_id);
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
            MobAgent.httpResponseParserError(TAG, "parserNewArtsMutRolePlayTestInfos", e.getMessage());
        }
        return rolePlayerEntity;
    }
    /**
     * 解析文科新课件平台roleplay分组和试题信息
     *
     * @param responseEntity
     * @return
     */
    public RolePlayerEntity parserNewRolePlayGroupAndTestInfos(ResponseEntity responseEntity) {
        try {
            RolePlayerEntity rolePlayerEntity = new RolePlayerEntity();
            rolePlayerEntity.setPullDZCount(0);//每次试题返回的时候，将点赞置为0
            rolePlayerEntity.getLstRolePlayerMessage().clear();//在试题信息返回的时候先清空数据集合，防止当服务器返回重复数据的时候，本地也出现重复
            rolePlayerEntity.setNewArts(true); // 判断是否是文科新课件平台的标识
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            logger.i(" rolePlay jsonObject:"+ jsonObject.toString());
            JSONObject objContent = jsonObject.optJSONObject("content");
            int minute = jsonObject.optInt("rolePlayTime");

            //自己的角色名字
            String releaseRole = jsonObject.optString("releaseRole");
            releaseRole = releaseRole.replaceAll("\n","");

            //发题时间，暂时体验课用
            rolePlayerEntity.setRolePlayReleaseTime(jsonObject.optLong("rolePlayReleaseTime"));
            //对话信息的数据实体中需要存入试题id
            String test_id = jsonObject.optString("testId");
            rolePlayerEntity.setTestId(test_id);
            rolePlayerEntity.setCountDownSecond(minute * 60);

            //对话信息
            JSONArray arrSpeech = objContent.optJSONArray("speeches");

            //角色信息
            JSONArray arrRoles = objContent.optJSONArray("roles");

            //所有对话的音频地址
            JSONObject objAudio =  jsonObject.optJSONObject("audio");
           if(objAudio == null){
                logger.i("audio is empty");
                MobAgent.httpResponseParserError(TAG, "parserNewRolePlayGroupAndTestInfos", "audio is empty");
                //return null;
            }
            JSONObject objStems = objAudio.optJSONObject("stem");
            for(int i = 0; i<arrRoles.length();i++){
                JSONObject objRole = arrRoles.getJSONObject(i);
                String roleName = objRole.optString("role");
                String nickName = objRole.optString("name");
                String img = objRole.optString("img");
                RolePlayerEntity.RolePlayerHead rolePlayerHead = new RolePlayerEntity.RolePlayerHead();
                if(roleName != null){
                    roleName = roleName.replaceAll("\n","");
                }
                rolePlayerHead.setRoleName(roleName);
                rolePlayerHead.setNickName(nickName);
                rolePlayerHead.setHeadImg(img);
                //通过“releaseRole”字段来判断“我”的角色
                if(roleName.equals(releaseRole)){
                    rolePlayerHead.setSelfRole(true);
                }
                logger.i(roleName+":"+releaseRole);
                //将分组信息放到map，list中
                rolePlayerEntity.getMapRoleHeadInfo().put(roleName,rolePlayerHead);
                rolePlayerEntity.getLstRoleInfo().add(rolePlayerHead);
            }
            for (int i = 0; i < arrSpeech.length(); i++) {
                JSONObject objMsg = arrSpeech.getJSONObject(i);
                String roleName = objMsg.optString("role");
                String msgContent = objMsg.optString("text");
                int maxTime = objMsg.optInt("time");
                //“9.mp3”
                String audio = objMsg.optString("audio");
                //http://resource.xxyys.com/test_library/audio/2017/11/07/t_20125_9.mp3?1510050714
                String realAudio = null;
                if(objAudio != null){
                    if(objStems != null){
                        realAudio = objStems.optString(audio);
                    }
                }



                //不满三秒，也要保证至少三秒，统一由服务器计算好返回
               /* if (maxTime < 3) {
                    maxTime = (msgContent.length()/5) +3;
                }*/
                //解析message
                roleName = roleName.replaceAll("\n","");
                HashMap <String,RolePlayerEntity.RolePlayerHead> roleHeadsMap = (HashMap<String, RolePlayerEntity.RolePlayerHead>) rolePlayerEntity.getMapRoleHeadInfo();
                RolePlayerEntity.RolePlayerHead head =roleHeadsMap.get(roleName);
                logger.i(roleName+" : "+roleHeadsMap.containsKey(roleName)+":"+head);
                if (head == null) {
                    head = new RolePlayerEntity.RolePlayerHead();
                    head.setRoleName("角色");
                    head.setNickName("昵称");
                }
                logger.i(roleName+" : "+roleHeadsMap.containsKey(roleName)+":"+head.isSelfRole());
                if (head.isSelfRole()) {
                    rolePlayerEntity.setSelfLastIndex(i);
                }
                RolePlayerEntity.RolePlayerMessage msg = new RolePlayerEntity.RolePlayerMessage(head, msgContent, maxTime,realAudio);
                if(!head.isSelfRole()){
                    msg.setWebVoiceUrl(realAudio);//只记录别人的网络音频地址（非阿里云地址）
                }

                msg.setTestId(test_id);//只是记录点击对话的日志用
                //msg.setWebVoiceUrl(objMsg.optString("audio"));
                msg.setPosition(i);
                rolePlayerEntity.getLstRolePlayerMessage().add(msg);
            }
            return rolePlayerEntity;
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parserNewRolePlayGroupAndTestInfos", e.getMessage());
            logger.i(" e.getMessage():"+ e.getMessage());
            return null;
        }


    }
}
