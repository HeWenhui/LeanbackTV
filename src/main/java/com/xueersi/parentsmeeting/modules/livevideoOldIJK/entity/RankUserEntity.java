package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import android.text.TextUtils;

/**
 * Created by Tang on 2018/1/3.
 */

public class RankUserEntity {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if(TextUtils.isEmpty(name)){
            name="网校学员";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
