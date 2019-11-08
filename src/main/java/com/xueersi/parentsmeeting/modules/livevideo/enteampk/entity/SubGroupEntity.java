package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;

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
    private JSONObject videoList;

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

    public JSONObject getVideoList() {
        return videoList;
    }

    public void setVideoList(JSONObject videoList) {
        this.videoList = videoList;
    }
}
