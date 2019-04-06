package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

public class BllConfigEntity {
    public String className;
    public String intent;

    public BllConfigEntity(String className) {
        this.className = className;
    }

    public BllConfigEntity(String className, String intent) {
        this.className = className;
        this.intent = intent;
    }
}
