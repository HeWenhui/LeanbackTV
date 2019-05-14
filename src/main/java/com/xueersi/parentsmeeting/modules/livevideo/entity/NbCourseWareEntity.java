package com.xueersi.parentsmeeting.modules.livevideo.entity;
/**
*nobook 物理实验 信息model
*@author chekun
*created  at 2019/4/5 13:22
*/
public class NbCourseWareEntity {
   /**直播id**/
   private String liveId;
   /**页面加载地址**/
   private String url;
   /**是否是Nb 加 实验**/
   private boolean nbAddExperiment;

   /**试题类型 ：考试/自由实验**/
   private String experimentType;
   /**试题ld**/
   private String experimentId;
   /**实验 名称**/
   private String experimentName;

   /**是否已作答**/
   private boolean isAnswer ;

    /**
     * 是否是回放
     */
   private boolean playBack;

   /**nb 登录token**/
   private String nbToken ="";

   public NbCourseWareEntity(){}

    public NbCourseWareEntity(String liveId, String url, boolean nbAddExperiment) {
        this.liveId = liveId;
        this.url = url;
        this.nbAddExperiment = nbAddExperiment;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isNbExperiment() {
        return nbAddExperiment;
    }

    public void setNbAddExperiment(boolean addExperiment) {
        this.nbAddExperiment = addExperiment;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public String getNbToken() {
        return nbToken;
    }

    public void setNbToken(String nbToken) {
        this.nbToken = nbToken;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setPlayBack(boolean playBack) {
        this.playBack = playBack;
    }

    public boolean isPlayBack() {
        return playBack;
    }
}
