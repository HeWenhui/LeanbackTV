package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 战队pk  战队信息
 */
public class TeamPkTeamInfoEntity {

    private List<StudentEntity> teamMembers; //队员信息
    private List<String> teamLogoList;             //队徽列表
    private TeamInfoEntity teamInfo;

    private int key;  // 自己队伍所在列表中的位置

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setTeamInfo(TeamInfoEntity teamInfo) {
        this.teamInfo = teamInfo;
    }

    public TeamInfoEntity getTeamInfo() {
        return teamInfo;
    }

    public void setTeamMembers(List<StudentEntity> members) {
        this.teamMembers = members;
    }

    public List<StudentEntity> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamLogoList(List<String> teamLogoList) {
        this.teamLogoList = teamLogoList;
    }

    public List<String> getTeamLogoList() {
        return teamLogoList;
    }


    public static class StudentEntity {
        String userId;
        String userName;
        String img;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class TeamInfoEntity {
        String teamName;
        String teamMateName;
        String slogon;
        String backGroud;
        String img;
        String teamId;
        String roomid;
        String token;
        List<TeamMate> result = new ArrayList<>();

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String temaNama) {
            this.teamName = temaNama;
        }

        public String getTeamMateName() {
            return teamMateName;
        }

        public void setTeamMateName(String teamMateName) {
            this.teamMateName = teamMateName;
        }

        public String getSlogon() {
            return slogon;
        }

        public void setSlogon(String slogon) {
            this.slogon = slogon;
        }

        public String getBackGroud() {
            return backGroud;
        }

        public void setBackGroud(String backGroud) {
            this.backGroud = backGroud;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public List<TeamMate> getResult() {
            return result;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getRoomid() {
            return roomid;
        }

        public void setRoomid(String roomid) {
            this.roomid = roomid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
