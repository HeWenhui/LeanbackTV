package com.xueersi.parentsmeeting.modules.livevideo.enteampk.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class EnTeamPkResponseParser extends HttpResponseParser {
    static String TAG = "EnTeamPkResponseParser";
    Logger logger = LiveLoggerFactory.getLogger(TAG);

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
            logger.e("parseTcpDispatch", e);
            MobAgent.httpResponseParserError(TAG, "parseTcpDispatch", e.getMessage());
        }
        return addresses;
    }

    public InteractiveTeam parseInteractiveTeam(String userId, JSONObject jsonObject) {
        InteractiveTeam interactiveTeam = new InteractiveTeam();
        try {
            interactiveTeam.setLive_id(jsonObject.getString("live_id"));
            interactiveTeam.setClass_id(jsonObject.getString("class_id"));
            interactiveTeam.setPk_team_id(jsonObject.getString("pk_team_id"));
            interactiveTeam.setTeam_type(jsonObject.getString("team_type"));
            interactiveTeam.setInteractive_team_id(jsonObject.getInt("interactive_team_id"));
            ArrayList<TeamMemberEntity> entities = parseGetStuActiveTeam(userId, jsonObject.getJSONArray("team_mate"));
            interactiveTeam.setEntities(entities);
            return interactiveTeam;
        } catch (JSONException e) {
            logger.e("parseInteractiveTeam", e);
            MobAgent.httpResponseParserError(TAG, "parseInteractiveTeam", e.getMessage());
        }
        return null;
    }

    public ArrayList<TeamMemberEntity> parseGetStuActiveTeam(String userId, JSONArray array) {
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        try {
            TeamMemberEntity myTeamMemberEntity = null;
            for (int i = 0; i < array.length(); i++) {
                JSONObject teamObj = array.getJSONObject(i);
                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                teamMemberEntity.id = teamObj.optInt("stu_id");
                teamMemberEntity.name = teamObj.optString("stu_name");
                teamMemberEntity.headurl = teamObj.optString("stu_head");
                if (("" + teamMemberEntity.id).equals(userId)) {
                    myTeamMemberEntity = teamMemberEntity;
                } else {
                    entities.add(teamMemberEntity);
                }
            }
            if (myTeamMemberEntity != null) {
                entities.add(myTeamMemberEntity);
            }
        } catch (JSONException e) {
            logger.e("parseGetStuActiveTeam", e);
            MobAgent.httpResponseParserError(TAG, "parseGetStuActiveTeam", e.getMessage());
        }
        return entities;
    }

}
