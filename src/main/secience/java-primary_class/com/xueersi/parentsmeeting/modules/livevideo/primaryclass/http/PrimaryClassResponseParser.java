package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.PrimaryClassEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrimaryClassResponseParser {
    public PrimaryClassEntity parsePrimaryClassEntity(ResponseEntity responseEntity) {
        PrimaryClassEntity primaryClassEntity = new PrimaryClassEntity();
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        try {
            primaryClassEntity.setRoomId(jsonObject.getString("roomId"));
            primaryClassEntity.setToken(jsonObject.getString("token"));
            JSONObject teamInfoObj = jsonObject.getJSONObject("teamInfo");
            TeamInfo teamInfo = new TeamInfo();
            teamInfo.setTeamId(teamInfoObj.getString("teamId"));
            teamInfo.setTeamName(teamInfoObj.getString("teamName"));
            teamInfo.setTeamImg(teamInfoObj.getString("teamImg"));
            JSONArray teamMembersObj = teamInfoObj.getJSONArray("teamMembers");
            ArrayList<TeamMember> teamMembers = teamInfo.getTeamMembers();
            for (int i = 0; i < teamMembersObj.length(); i++) {
                JSONObject teamMemberObj = teamMembersObj.getJSONObject(i);
                TeamMember teamMember = new TeamMember();
                teamMember.setStuId(teamMemberObj.getInt("stuId"));
                teamMember.setStuName(teamMemberObj.getString("stuName"));
                teamMembers.add(teamMember);
            }
            primaryClassEntity.setTeamInfo(teamInfo);
            return primaryClassEntity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
