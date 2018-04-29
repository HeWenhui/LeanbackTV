package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author  chenkun
 * pk 对手
 */
public class TeamPkAdversaryEntity {

    private AdversaryInfo self;
    private AdversaryInfo opponent;

    public AdversaryInfo getSelf() {
        return self;
    }

    public void setSelf(AdversaryInfo self) {
        this.self = self;
    }

    public void setOpponent(AdversaryInfo opponent) {
        this.opponent = opponent;
    }

    public AdversaryInfo getOpponent() {
        return opponent;
    }

    public static class AdversaryInfo{
        private String teamName;
        private String teamMateName;
        private String slogon;
        private String backGroud;
        private String img;
        private String teacherName;
        private String teacherImg;
        private String teamId;
        private String classId;

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

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }
    }


}
