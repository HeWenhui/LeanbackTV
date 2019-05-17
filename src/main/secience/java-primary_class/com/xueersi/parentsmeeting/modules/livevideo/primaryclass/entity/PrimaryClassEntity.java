package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity;

import java.util.ArrayList;

public class PrimaryClassEntity {
    private String token;
    private String roomId;
    private TeamInfo teamInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    public void setTeamInfo(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
    }
}
