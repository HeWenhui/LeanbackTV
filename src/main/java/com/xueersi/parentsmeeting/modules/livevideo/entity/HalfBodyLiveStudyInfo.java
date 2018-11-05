package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * 半身直播 旁听 数据 mode
 *
 * @author chekun
 * created  at 2018/10/31 16:35
 */
public class HalfBodyLiveStudyInfo extends StudyInfo {
    /**答错*/
    public static final int ANSWER_STATE_ERROR = 0;
    /**答队*/
    public static final int ANSWER_STATE_RIGHT = 1;
    /**部分正确*/
    public static final int ANSWER_STATE_PART_RIGHT = 2;
    /**未作答*/
    public static final int ANSWER_STATE_NO_ANSWER = 3;
    /**
     * 本队战队能量
     */
    private long ourTeamEnergy;
    /**
     * 对手战队能量
     */
    private long hostileTeamEnergy;

    /**
     * 平均正确率
     */
    private String stuAvgRate;

    /**答题信息*/
    private List<TestInfo>  testList;

    public long getOurTeamEnergy() {
        return ourTeamEnergy;
    }

    public void setOurTeamEnergy(long ourTeamEnergy) {
        this.ourTeamEnergy = ourTeamEnergy;
    }

   public static class TestInfo{
        /**题号*/
        private int orderNum;
        /**答题状态 0错 1对 2半对 3未作答*/
        private int answeredStatus;
        /**本题全场正确率*/
        private String planAvgRightRate;

       public int getOrderNum() {
           return orderNum;
       }

       public void setOrderNum(int orderNum) {
           this.orderNum = orderNum;
       }

       public int getAnsweredStatus() {
           return answeredStatus;
       }

       public void setAnsweredStatus(int answeredStatus) {
           this.answeredStatus = answeredStatus;
       }

       public String getPlanAvgRightRate() {
           return planAvgRightRate;
       }

       public void setPlanAvgRightRate(String planAvgRightRate) {
           this.planAvgRightRate = planAvgRightRate;
       }
   }

    public long getHostileTeamEnergy() {
        return hostileTeamEnergy;
    }

    public void setHostileTeamEnergy(long hostileTeamEnergy) {
        this.hostileTeamEnergy = hostileTeamEnergy;
    }

    public String getStuAvgRate() {
        return stuAvgRate;
    }

    public void setStuAvgRate(String stuAvgRate) {
        this.stuAvgRate = stuAvgRate;
    }

    public List<TestInfo> getTestList() {
        return testList;
    }

    public void setTestList(List<TestInfo> testList) {
        this.testList = testList;
    }
}
