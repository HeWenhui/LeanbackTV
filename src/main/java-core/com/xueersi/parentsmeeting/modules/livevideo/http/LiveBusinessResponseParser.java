package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.util.Log;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 大班整合-数据解析
 *
 * @author chenkun
 * @version 1.0, 2019-08-19 16:25
 */

public class LiveBusinessResponseParser extends HttpResponseParser {

    /**
     * 大班整合 直播入口数据解析
     *
     * @param data
     * @param topic
     * @param liveType
     * @param from
     * @return
     */
    public LiveGetInfo parseLiveEnter(JSONObject data, LiveTopic topic, int liveType, int from) {

        LiveGetInfo liveGetInfo = null;
        try {
            liveGetInfo =  new LiveGetInfo(topic);
            liveGetInfo.setNowTime(data.optLong("nowTime"));
            //设置标志，大班直播间
            liveGetInfo.setBigLive(true);
            //解析学生-基础信息
            if (data.has("stuInfo")) {
                JSONObject stuInfoJsonObj = data.getJSONObject("stuInfo");
                liveGetInfo.setUname(stuInfoJsonObj.optString("userName", ""));
                liveGetInfo.setNickname(stuInfoJsonObj.optString("nickName", ""));
                liveGetInfo.setStuName(stuInfoJsonObj.optString("realName"));
                liveGetInfo.setEn_name(stuInfoJsonObj.optString("englishName", ""));
                liveGetInfo.setStuSex(stuInfoJsonObj.optString("sex"));
                liveGetInfo.setStuId(stuInfoJsonObj.optString("id"));
                liveGetInfo.setStuImg(stuInfoJsonObj.optString("avatar"));

                if (stuInfoJsonObj.has("psim")) {
                    JSONObject psImObject = stuInfoJsonObj.getJSONObject("psim");
                    liveGetInfo.setPsId(psImObject.optString("psId"));
                    liveGetInfo.setPsPwd(psImObject.optString("psPwd"));
                }
            }


            //解析小组成员id
            JSONArray teamStuIdArray = data.optJSONArray("teamStuIds");
            if (teamStuIdArray != null && teamStuIdArray.length() > 0) {
                ArrayList<String> teamStuIds = liveGetInfo.getTeamStuIds();
                for (int i = 0; i < teamStuIdArray.length(); i++) {
                    teamStuIds.add(teamStuIdArray.getString(i));
                }
            }

            // 解析直播场次信息
            if(data.has("planInfo")){
                JSONObject planInfoJsonObj = data.getJSONObject("planInfo");
                //直播id
                liveGetInfo.setId(planInfoJsonObj.optString("id"));
                //直播名称
                liveGetInfo.setName(planInfoJsonObj.optString("name"));
                //直播类型
                liveGetInfo.setLiveType(planInfoJsonObj.optInt("type"));

                //设置当前主/辅 讲模式
                int mode = planInfoJsonObj.optInt("mode",0);
                topic.setMode(mode == 0 ? LiveTopic.MODE_TRANING : LiveTopic.MODE_CLASS);
                liveGetInfo.setMode(topic.getMode());
                //设置直播模式
                liveGetInfo.setPattern(planInfoJsonObj.optInt("pattern"));

                //设置直播开始，结束时间
                liveGetInfo.setsTime(planInfoJsonObj.optLong("stime"));
                liveGetInfo.seteTime(planInfoJsonObj.optLong("etime"));

                //解析学科id
                if(planInfoJsonObj.has("subjectIds")){
                    String subjectIds = planInfoJsonObj.optString("subjectIds");
                    String[] arrSubjIds = subjectIds.split(",");
                    liveGetInfo.setSubjectIds(arrSubjIds);
                }

                // 解析直播课所属年级
                String gradsIdStr = planInfoJsonObj.optString("gradeIds");
                if(gradsIdStr != null && gradsIdStr.length() > 0){
                    String[]gradeIds = gradsIdStr.split(",");
                    if(gradeIds != null && gradeIds.length > 0){
                        try {
                            liveGetInfo.setGrade(Integer.parseInt(gradeIds[0]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


            // 解析学生场次信息
            if (data.has("stuLiveInfo")) {
                JSONObject stuLiveInfoObj = data.getJSONObject("stuLiveInfo");
                LiveGetInfo.StudentLiveInfoEntity studentLiveInfoEntity = new LiveGetInfo.StudentLiveInfoEntity();
                studentLiveInfoEntity.setClassId(stuLiveInfoObj.optString("classId"));
                studentLiveInfoEntity.setTeamId(stuLiveInfoObj.optString("teamId"));

                boolean isExpe = "-1".equals(studentLiveInfoEntity.getClassId());
                if(isExpe){
                    if (from != LiveVideoBusinessConfig.ENTER_FROM_1 && from != LiveVideoBusinessConfig.ENTER_FROM_2) {
                        studentLiveInfoEntity.setExpe(true);
                    }
                    XesMobAgent.liveExpe(from, liveGetInfo.getId());
                }
                liveGetInfo.setStudentLiveInfo(studentLiveInfoEntity);
            }



            //解析主讲老师信息
            if(data.has("teacherInfo")){
                JSONObject teacherInfoJsonObj = data.getJSONObject("teacherInfo");
                LiveGetInfo.MainTeacherInfo mainTeacherInfo = liveGetInfo.getMainTeacherInfo();
                mainTeacherInfo.setTeacherId(teacherInfoJsonObj.optString("id"));
                mainTeacherInfo.setTeacherName(teacherInfoJsonObj.optString("name"));
                mainTeacherInfo.setTeacherImg(teacherInfoJsonObj.optString("avatar"));
                liveGetInfo.setMainTeacherId(teacherInfoJsonObj.optString("id"));
            }


            // 解析辅导老师信息
            if(data.has("counselorInfo")){
                JSONObject counselorJosnObj = data.getJSONObject("counselorInfo");
                liveGetInfo.setTeacherId(counselorJosnObj.optString("id"));
                liveGetInfo.setTeacherName(counselorJosnObj.optString("name"));
                liveGetInfo.setTeacherIMG(counselorJosnObj.optString("avatar"));
            }

            //解析一级配置信息
            if(data.has("configs")){
               JSONObject cfgJsonObj = data.getJSONObject("configs");
                liveGetInfo.setMainTeacherVieo(cfgJsonObj.optString("mainTeacherVideo"));
                liveGetInfo.setCounselorTeacherVideo(cfgJsonObj.optString("counselorTeacherVideo"));
                liveGetInfo.setIrcNick(cfgJsonObj.optString("ircNick"));
                JSONArray ircRooms = cfgJsonObj.optJSONArray("ircRooms");
                //设置房间号
                if(ircRooms != null && ircRooms.length() > 0){
                     List<String> ircRoomList = new ArrayList<>();
                    for (int i = 0; i < ircRooms.length(); i++) {
                        ircRoomList.add(ircRooms.getString(i));
                    }
                    Log.e("ckTrac","=========>parseinfo_set irc rooms");
                    liveGetInfo.setIrcRoomList(ircRoomList);
                }

                liveGetInfo.setPsAppId(cfgJsonObj.optString("appId"));
                liveGetInfo.setPsAppKey(cfgJsonObj.optString("appKey"));

                if(cfgJsonObj.has("followType")){
                    JSONObject followTypeJsonObj = cfgJsonObj.optJSONObject("followType");
                    LiveGetInfo.FollowTypeEntity followTypeEntity = new LiveGetInfo.FollowTypeEntity();
                    followTypeEntity.setInt2(followTypeJsonObj.optInt("2"));
                    followTypeEntity.setInt3(followTypeJsonObj.optInt("3"));
                    followTypeEntity.setInt4(followTypeJsonObj.optInt("4"));
                    liveGetInfo.setFollowType(followTypeEntity);
                }

                if(cfgJsonObj.has("urls")){
                    JSONObject urlJsonObj = cfgJsonObj.getJSONObject("urls");
                    String url = urlJsonObj.optString("initModuleUrl");
                    HashMap<String,String> urlMaps = new HashMap<>();
                    urlMaps.put("initModuleUrl",url);
                    liveGetInfo.setUrlMap(urlMaps);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseLiveGetInfo", e.getMessage());
        }
        return liveGetInfo;
    }



}
