package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2017/2/27.
 */

public class SpeechEvalEntity {

    /**
     * stuId : 123
     * content : we are xue er si
     * speechEvalTime : 0
     * nowTime : 1488253336
     * speechEvalReleaseTime : 1487811992
     * endTime : 1487811992
     * answered : true
     */

    private String stuId;
    private String content;
    private String answer;
    private long speechEvalTime;
    private long nowTime;
    private long speechEvalReleaseTime;
    private long endTime;
    private int answered;
    private String testtype;

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getSpeechEvalTime() {
        return speechEvalTime;
    }

    public void setSpeechEvalTime(long speechEvalTime) {
        this.speechEvalTime = speechEvalTime;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public long getSpeechEvalReleaseTime() {
        return speechEvalReleaseTime;
    }

    public void setSpeechEvalReleaseTime(long speechEvalReleaseTime) {
        this.speechEvalReleaseTime = speechEvalReleaseTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int answered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public String getTesttype() {
        return testtype;
    }

    public void setTesttype(String testtype) {
        this.testtype = testtype;
    }
}
