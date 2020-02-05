package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组信息
 */
public class SubGroupEntity {
    /** 分组ID */
    private int groupId;
    /** 声网token */
    private String token;
    /** 机器人信息 */
    SubMemberEntity virStuInfo;

    /** 分组信息 */
    private List<SubMemberEntity> groupList = new ArrayList<>();

    /** 假流视频 */
    private JSONObject videoJson;
    /** 对接云平台假流视频地址 */
    private JSONObject videoPathJson;
    private JSONObject dataJson;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<SubMemberEntity> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<SubMemberEntity> groupList) {
        this.groupList = groupList;
    }

    public SubMemberEntity getVirStuInfo() {
        return virStuInfo;
    }

    public void setVirStuInfo(SubMemberEntity virStuInfo) {
        this.virStuInfo = virStuInfo;
    }

    public JSONObject getDataJson() {
        return dataJson;
    }

    public void setDataJson(JSONObject dataJson) {
        this.dataJson = dataJson;
    }

    public JSONObject getVideoPathJson() {
        return videoPathJson;
    }

    public void setVideoPathJson(JSONObject videoPathJson) {
        this.videoPathJson = videoPathJson;
    }

    public JSONObject getVideoJson() {
        return videoJson;
    }

    public void setVideoJson(JSONObject videoJson) {
        this.videoJson = videoJson;
    }
}
