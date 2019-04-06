package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by lyqai on 2017/7/21.
 * 旁听学生数据
 */
public class StudyInfo {
    private String signTime;// "13:23",
    private String onlineTime;// 0,
    private String myRank;// "-1\/0",
    private String testRate;// "0%"
    private String mode;

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getMyRank() {
        return myRank;
    }

    public void setMyRank(String myRank) {
        this.myRank = myRank;
    }

    public String getTestRate() {
        return testRate;
    }

    public void setTestRate(String testRate) {
        this.testRate = testRate;
    }


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
