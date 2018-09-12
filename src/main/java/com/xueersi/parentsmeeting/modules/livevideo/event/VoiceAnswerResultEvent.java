package com.xueersi.parentsmeeting.modules.livevideo.event;

/**
*语音题 答题结果 时间
*@author chekun
*created  at 2018/9/7 10:23
*/
public class VoiceAnswerResultEvent {
  /**试题id*/
  private String testId;
  /**所得分数*/
  private int  score;
    public VoiceAnswerResultEvent(String testId, int score) {
        this.testId = testId;
        this.score = score;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if( obj != null && obj instanceof  VoiceAnswerResultEvent){
            VoiceAnswerResultEvent targetObj = (VoiceAnswerResultEvent)obj;
            if(targetObj.testId != null && targetObj.testId.equals(this.testId) && targetObj.score == this.score){
                result = true;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "VoiceAnswerResultEvent{" +
                "testId='" + testId + '\'' +
                ", score=" + score +
                '}';
    }
}
