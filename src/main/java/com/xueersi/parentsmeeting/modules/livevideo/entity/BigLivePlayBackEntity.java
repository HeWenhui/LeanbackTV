package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.common.base.BaseEntity;

import java.util.List;

/**
 * 大班整合-进入回放
 *
 * @author chenkun
 * @version 1.0, 2019-08-21 09:38
 */

public class BigLivePlayBackEntity extends BaseEntity {

    /**server端当前时间**/
    private long nowTime;

    /**学生基础信息**/
    private StuInfo stuInfo;

    /**学生场次信息**/
    private StuLiveInfo stuLiveInfo;

    /**场次信息**/
    private PlanInfo planInfo;

    /**主讲老师信息**/
    private TeacherInfo mainTeacher;
    /**辅导老师信息**/
    private TeacherInfo counselorTeacher;

    /**配置信息**/
    private Configs configs;


    /** 视频播放统计轮寻时间 */
    private int vCourseSendPlayVideoTime;

    /** 统计视频播放key */
    private String visitTimeKey;


    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public StuInfo getStuInfo() {
        return stuInfo;
    }

    public void setStuInfo(StuInfo stuInfo) {
        this.stuInfo = stuInfo;
    }

    public StuLiveInfo getStuLiveInfo() {
        return stuLiveInfo;
    }

    public void setStuLiveInfo(StuLiveInfo stuLiveInfo) {
        this.stuLiveInfo = stuLiveInfo;
    }

    public PlanInfo getPlanInfo() {
        return planInfo;
    }

    public void setPlanInfo(PlanInfo planInfo) {
        this.planInfo = planInfo;
    }

    public TeacherInfo getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(TeacherInfo mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public TeacherInfo getCounselorTeacher() {
        return counselorTeacher;
    }

    public void setCounselorTeacher(TeacherInfo counselorTeacher) {
        this.counselorTeacher = counselorTeacher;
    }

    public Configs getConfigs() {
        return configs;
    }

    public void setConfigs(Configs configs) {
        this.configs = configs;
    }

    public int getvCourseSendPlayVideoTime() {
        return vCourseSendPlayVideoTime;
    }

    public void setvCourseSendPlayVideoTime(int vCourseSendPlayVideoTime) {
        this.vCourseSendPlayVideoTime = vCourseSendPlayVideoTime;
    }

    public String getVisitTimeKey() {
        return visitTimeKey;
    }

    public void setVisitTimeKey(String visitTimeKey) {
        this.visitTimeKey = visitTimeKey;
    }


    public static class StuInfo{

      private String id;
      private String userName;
      private String nickName;
      private String realName;
      private String englishName;
      private int    sex;
      private String gradeName;
      private int    gradeId;
      private String avatar;
      private long   goldNum;
      private String psImId;
      private String psImPwd;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getGradeName() {
            return gradeName;
        }

        public void setGradeName(String gradeName) {
            this.gradeName = gradeName;
        }

        public int getGradeId() {
            return gradeId;
        }

        public void setGradeId(int gradeId) {
            this.gradeId = gradeId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public long getGoldNum() {
            return goldNum;
        }

        public void setGoldNum(long goldNum) {
            this.goldNum = goldNum;
        }

        public String getPsImId() {
            return psImId;
        }

        public void setPsImId(String psImId) {
            this.psImId = psImId;
        }

        public String getPsImPwd() {
            return psImPwd;
        }

        public void setPsImPwd(String psImPwd) {
            this.psImPwd = psImPwd;
        }

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }
    }


    public static class StuLiveInfo{

        private String classId;
        private String teamId;
        /**本队成员**/
        private List<String> teamStudIds;


        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }


        public List<String> getTeamStudIds() {
            return teamStudIds;
        }

        public void setTeamStudIds(List<String> teamStudIds) {
            this.teamStudIds = teamStudIds;
        }
    }



    public static class PlanInfo{

        private String id;
        private String name;
        private String type;
        private String mode;
        private String pattern;
        private String sTime;
        private String eTIme;
        private List<String> subjectIds;
        private List<String> gradeIds;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getsTime() {
            return sTime;
        }

        public void setsTime(String sTime) {
            this.sTime = sTime;
        }

        public String geteTIme() {
            return eTIme;
        }

        public void seteTIme(String eTIme) {
            this.eTIme = eTIme;
        }

        public List<String> getSubjectIds() {
            return subjectIds;
        }

        public void setSubjectIds(List<String> subjectIds) {
            this.subjectIds = subjectIds;
        }

        public List<String> getGradeIds() {
            return gradeIds;
        }

        public void setGradeIds(List<String> gradeIds) {
            this.gradeIds = gradeIds;
        }
    }




    public static class TeacherInfo{

        private String id;
        private String name;
        private String type;
        private String nickName;
        private String sex;
        private String avatar;
        private String areaName;
        private String branchName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }
    }


    public static class Configs{
      private String appId;
      private String appKey;
      private String videoFile;
      private String videoPath;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getVideoFile() {
            return videoFile;
        }

        public void setVideoFile(String videoFile) {
            this.videoFile = videoFile;
        }

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }
    }



}
