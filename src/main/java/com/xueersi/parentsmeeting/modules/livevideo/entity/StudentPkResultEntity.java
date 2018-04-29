package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author  chenkun
 * 学生每题 pk 结果
 */
public class StudentPkResultEntity {

    private PkResultInfo myTeamResultInfo;
    private PkResultInfo competitorResultInfo;

    public  StudentPkResultEntity(){

    }

    public StudentPkResultEntity(PkResultInfo myTeamResultInfo, PkResultInfo competitorResultInfo) {
        this.myTeamResultInfo = myTeamResultInfo;
        this.competitorResultInfo = competitorResultInfo;
    }

    public PkResultInfo getMyTeamResultInfo() {
        return myTeamResultInfo;
    }

    public void setMyTeamResultInfo(PkResultInfo myTeamResultInfo) {
        this.myTeamResultInfo = myTeamResultInfo;
    }

    public PkResultInfo getCompetitorResultInfo() {
        return competitorResultInfo;
    }

    public void setCompetitorResultInfo(PkResultInfo competitorResultInfo) {
        this.competitorResultInfo = competitorResultInfo;
    }


    public static  class  PkResultInfo{
        private  long energy;
        private  String teamName;
        private String teamMateName;
        private String slogon;
        private String backGroud;
        private String img;
        private String teacherName;
        private String teacherImg;

        public PkResultInfo(){

        }
        public PkResultInfo(long energy, String teamName, String teamMateName, String slogon,
                            String backGroud, String img, String teacherName, String teacherImg) {
            this.energy = energy;
            this.teamName = teamName;
            this.teamMateName = teamMateName;
            this.slogon = slogon;
            this.backGroud = backGroud;
            this.img = img;
            this.teacherName = teacherName;
            this.teacherImg = teacherImg;
        }

        public long getEnergy() {
            return energy;
        }

        public void setEnergy(long energy) {
            this.energy = energy;
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
