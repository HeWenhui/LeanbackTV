package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/4/4.
 */

public class MoreChoice implements Serializable {
    private List<Choice> cases;
    public static class Choice implements Serializable{
        private String saleName;
        private int limit;
        private String signUpUrl;
        private int isLearn;
        private String courseId;
        private String adId;
        private String classId;

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getClassId() {
            return classId;
        }

        public void setClassId(String classId) {
            this.classId = classId;
        }

        public String getSaleName() {
            return saleName;
        }

        public void setSaleName(String saleName) {
            this.saleName = saleName;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getSignUpUrl() {
            return signUpUrl;
        }

        public void setSignUpUrl(String signUpUrl) {
            this.signUpUrl = signUpUrl;
        }

        public int getIsLearn() {
            return isLearn;
        }

        public void setIsLearn(int isLearn) {
            this.isLearn = isLearn;
        }
    }
    private String rows;

    public List<Choice> getCases() {
        return cases;
    }

    public void setCases(List<Choice> cases) {
        this.cases = cases;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }
}
