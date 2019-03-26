package com.xueersi.parentsmeeting.modules.livevideo.enteampk.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class EnTeamPkResponseParser extends HttpResponseParser {
    static String TAG = "EnTeamPkResponseParser";

    public EnTeamPkResponseParser() {
    }

    public ArrayList<InetSocketAddress> parseTcpDispatch(ResponseEntity responseEntity) {
        ArrayList<InetSocketAddress> addresses = new ArrayList<>();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray array = jsonObject.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                String hostAndIpStr = array.getString(i);
                String[] hostAndIp = hostAndIpStr.split(":");
                InetSocketAddress inetSocketAddress = new InetSocketAddress(hostAndIp[0], Integer.parseInt(hostAndIp[1]));
                addresses.add(inetSocketAddress);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public InteractiveTeam parseInteractiveTeam(JSONObject jsonObject) {
        InteractiveTeam interactiveTeam = new InteractiveTeam();
        try {
            interactiveTeam.setLive_id(jsonObject.getString("live_id"));
            interactiveTeam.setClass_id(jsonObject.getString("class_id"));
            interactiveTeam.setPk_team_id(jsonObject.getString("pk_team_id"));
            interactiveTeam.setTeam_type(jsonObject.getString("team_type"));
            interactiveTeam.setInteractive_team_id(jsonObject.getString("interactive_team_id"));
            ArrayList<TeamMemberEntity> entities = parseGetStuActiveTeam(jsonObject.getJSONArray(""));
            interactiveTeam.setEntities(entities);
            return interactiveTeam;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<TeamMemberEntity> parseGetStuActiveTeam(JSONArray array) {
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject teamObj = array.getJSONObject(i);
                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                teamMemberEntity.id = teamObj.optInt("stu_id");
                teamMemberEntity.name = teamObj.optString("stu_name");
                teamMemberEntity.headurl = teamObj.optString("stu_head");
                entities.add(teamMemberEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public ArrayList<TeamMemberEntity> parseGetStuActiveTeam(ResponseEntity responseEntity) {
        JSONArray array = (JSONArray) responseEntity.getJsonObject();
        return parseGetStuActiveTeam(array);
    }
}
