package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

/**
*文科新课件平台 在线教研 数据modle
*@author chekun
*created  at 2018/8/23 14:04
*/
public class H5OnlineTechEntity {

     /**页面包来源：1-设计部H5，2-在线教研，3-模板改编创建**/
     private int package_source;
     /**奖励金币**/
     private String gold;
     /**时间，单位：分钟**/
     private String time;
     /**试题id的数组**/
     private String id;
    /**
     * 0-多发，1-在线教研填空，2-在线教研选择，4-在线教研语音测评，5-在线教研roleplay，
       6-在线教研语文跟读，7-本地上传普通，8-在线教研语文主观题，9-本地上传课前测，10-本地上传课中测，
     11-本地上传出门考，12-本地上传游戏，13-本地上传互动题，14-本地上传语音测评,15-本地上传-语音答题填空,16-本地上传-语音答题选择
     */
    private String ptype;
    /**
     * 角色扮演题独有 。"1"-多人RolePlay “0”-单人RolePlay
     */
    private String multiRolePlay;
    /**角色名字**/
    private String roles;
    /**总分数**/
    private String totalScore;
    /**语音评测答案**/
    private String answer;

    /**当前状态**/
    private String status;



    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getPackage_source() {
        return package_source;
    }

    public void setPackage_source(int package_source) {
        this.package_source = package_source;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getMultiRolePlay() {
        return multiRolePlay;
    }

    public void setMultiRolePlay(String multiRolePlay) {
        this.multiRolePlay = multiRolePlay;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
