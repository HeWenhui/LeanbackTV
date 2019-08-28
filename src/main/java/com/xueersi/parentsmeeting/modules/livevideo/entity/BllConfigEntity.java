package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class BllConfigEntity {
    public String className;
    public String intent;
    public int moudleId;

    public BllConfigEntity(String className) {
        this.className = className;
    }

    public BllConfigEntity(String className, String intent) {
        this.className = className;
        this.intent = intent;
    }
    public BllConfigEntity(String className, int moudleId) {
        this.className = className;
        this.moudleId = moudleId;
    }
}
