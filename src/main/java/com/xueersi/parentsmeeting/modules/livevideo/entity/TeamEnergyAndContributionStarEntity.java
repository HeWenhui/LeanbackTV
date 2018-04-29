package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * @author chenkun
 * 每题战队能量和贡献之星
 */
public class TeamEnergyAndContributionStarEntity {

    private List<ContributionStar> contributionStarList;
    private TeamEnergyInfo myTeamEngerInfo;
    private TeamEnergyInfo competitorEngerInfo;

    public TeamEnergyAndContributionStarEntity(){}

    public TeamEnergyAndContributionStarEntity(List<ContributionStar> contributionStarList,
                                               TeamEnergyInfo myTeamEngerInfo, TeamEnergyInfo
                                                       competitorEngerInfo) {
        this.contributionStarList = contributionStarList;
        this.myTeamEngerInfo = myTeamEngerInfo;
        this.competitorEngerInfo = competitorEngerInfo;
    }

    public List<ContributionStar> getContributionStarList() {
        return contributionStarList;
    }

    public void setContributionStarList(List<ContributionStar> contributionStarList) {
        this.contributionStarList = contributionStarList;
    }

    public TeamEnergyInfo getMyTeamEngerInfo() {
        return myTeamEngerInfo;
    }

    public void setMyTeamEngerInfo(TeamEnergyInfo myTeamEngerInfo) {
        this.myTeamEngerInfo = myTeamEngerInfo;
    }

    public TeamEnergyInfo getCompetitorEngerInfo() {
        return competitorEngerInfo;
    }

    public void setCompetitorEngerInfo(TeamEnergyInfo competitorEngerInfo) {
        this.competitorEngerInfo = competitorEngerInfo;
    }


    public static class ContributionStar {
        private String stuId;
        private long energy;
        private String name;
        private String realname;
        private String nickname;
        private String avaterPath;
        public ContributionStar(){

        }
        public ContributionStar(String stuId, long energy, String realname, String nickname, String avaterPath,String name) {
            this.stuId = stuId;
            this.energy = energy;
            this.realname = realname;
            this.nickname = nickname;
            this.avaterPath = avaterPath;
            this.name = name;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public long getEnergy() {
            return energy;
        }

        public void setEnergy(long energy) {
            this.energy = energy;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvaterPath() {
            return avaterPath;
        }

        public void setAvaterPath(String avaterPath) {
            this.avaterPath = avaterPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class TeamEnergyInfo {
        private long addEnergy;
        private long totalEnergy;
        private String teamName;
        private String teamMateName;
        private String slogon;
        private String backGroud;
        private String img;
        private String teacherName;
        private String teacherImg;

        public TeamEnergyInfo(){

        }
        public TeamEnergyInfo(long addEnergy, long totalEnergy, String teamName, String teamMateName,
                              String slogon, String backGroud, String img,
                              String teacherName, String teacherImg) {
            this.addEnergy = addEnergy;
            this.totalEnergy = totalEnergy;
            this.teamName = teamName;
            this.teamMateName = teamMateName;
            this.slogon = slogon;
            this.backGroud = backGroud;
            this.img = img;
            this.teacherName = teacherName;
            this.teacherImg = teacherImg;
        }

        public long getAddEnergy() {
            return addEnergy;
        }

        public void setAddEnergy(long addEnergy) {
            this.addEnergy = addEnergy;
        }

        public long getTotalEnergy() {
            return totalEnergy;
        }

        public void setTotalEnergy(long totalEnergy) {
            this.totalEnergy = totalEnergy;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
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

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getTeacherImg() {
            return teacherImg;
        }

        public void setTeacherImg(String teacherImg) {
            this.teacherImg = teacherImg;
        }
    }

}
