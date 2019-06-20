package com.xueersi.parentsmeeting.modules.livevideo.teampk.http;

import android.content.Context;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamPKHttpResponseParser extends LiveHttpResponseParser {
    private String TAG = "LiveHttpResponseParser";
    private boolean fromLocal = false;

    public TeamPKHttpResponseParser(Context mContext) {
        super(mContext);
    }

    public void setFromLocal(boolean fromLocal) {
        this.fromLocal = fromLocal;
    }

    /** 解析小组战队pk可能为空 */
    public TeamPkTeamInfoEntity parseTeamInfoPrimary(ResponseEntity responseEntity) {
        TeamPkTeamInfoEntity teamInfoEntity = new TeamPkTeamInfoEntity();
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        try {
            if (data.has("students")) {
                JSONArray jsonArray = data.getJSONArray("students");
                TeamPkTeamInfoEntity.StudentEntity studentEntity;
                JSONObject jsonObject;
                List<TeamPkTeamInfoEntity.StudentEntity> teamMembers = new ArrayList<TeamPkTeamInfoEntity
                        .StudentEntity>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    studentEntity = new TeamPkTeamInfoEntity.StudentEntity();
                    studentEntity.setUserId(jsonObject.getString("userId"));
                    studentEntity.setUserName(jsonObject.getString("name"));
                    studentEntity.setImg(jsonObject.getString("img"));
                    teamMembers.add(studentEntity);
                }
                teamInfoEntity.setTeamMembers(teamMembers);
            }
            Object teamObj = data.opt("teamInfo");
            if (teamObj instanceof JSONObject) {
                JSONObject teamInfoObj = (JSONObject) data.get("teamInfo");


                if (teamInfoObj.has("imgs")) {
                    JSONObject jsonObject = (JSONObject) teamInfoObj.get("imgs");
                    if (jsonObject.has("key")) {
                        teamInfoEntity.setKey(jsonObject.getInt("key"));
                    }
                    if (jsonObject.has("imgs")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("imgs");
                        List<String> imgList = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            imgList.add(jsonArray.getString(i));
                        }
                        teamInfoEntity.setTeamLogoList(imgList);
                    }
                }

                TeamPkTeamInfoEntity.TeamInfoEntity teamInfo = new TeamPkTeamInfoEntity.TeamInfoEntity();
                teamInfo.setImg(teamInfoObj.getString("img"));
                teamInfo.setTeamId(teamInfoObj.getString("teamId"));
                teamInfo.setTeamName(teamInfoObj.getString("teamName"));
                teamInfo.setTeamMateName(teamInfoObj.getString("teamMateName"));
                teamInfo.setSlogon(teamInfoObj.getString("slogon"));
                teamInfo.setBackGroud(teamInfoObj.getString("backGroud"));
                teamInfo.setRoomid(data.getString("roomId"));
                teamInfo.setToken(data.getString("token"));
                teamInfo.setFromLocal(fromLocal);
                try {
                    JSONArray teamMembersArray = teamInfoObj.optJSONArray("teamMembers");
                    List<TeamPkTeamInfoEntity.StudentEntity> teamMembers = new ArrayList<>();
                    if (teamMembersArray != null) {
                        for (int i = 0; i < teamMembersArray.length(); i++) {
                            teamInfoObj = (JSONObject) teamMembersArray.get(i);
                            TeamMate teamMate = new TeamMate();
                            teamMate.setId(teamInfoObj.optString("stuId"));
                            teamMate.setName(teamInfoObj.optString("stuName"));
                            teamInfo.getResult().add(teamMate);
                            TeamPkTeamInfoEntity.StudentEntity studentEntity = new TeamPkTeamInfoEntity.StudentEntity();
                            studentEntity.setUserId(teamInfoObj.getString("stuId"));
                            studentEntity.setUserName(teamInfoObj.getString("stuName"));
                            studentEntity.setImg(teamInfoObj.optString("img"));
                            teamMembers.add(studentEntity);
                        }
                    }
                    teamInfoEntity.setTeamMembers(teamMembers);
                } catch (Exception e) {
                    MobAgent.httpResponseParserError(TAG, "parseTeamInfo.teamMembers", e.getMessage());
                }
                teamInfoEntity.setTeamInfo(teamInfo);
                return teamInfoEntity;
            } else {
                MobAgent.httpResponseParserError(TAG, "parseTeamInfo", "teamInfo=" + teamObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MobAgent.httpResponseParserError(TAG, "parseTeamInfo", e.getMessage());
        }
        return null;
    }

}
