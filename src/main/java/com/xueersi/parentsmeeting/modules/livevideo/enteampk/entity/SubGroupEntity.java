package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;

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

    LiveAppUserInfo virStuInfo;

    /** 分组信息 */
    private List<SubMemberEntity> groupList = new ArrayList<>();

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
}
