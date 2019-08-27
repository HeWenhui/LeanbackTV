package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class BllConfigEntity {
    public String className;
    public String intent;
    public int businessId;

    public BllConfigEntity(String className) {
        this.className = className;
    }

    public BllConfigEntity(String className, String intent) {
        this.className = className;
        this.intent = intent;
    }
    public BllConfigEntity(String className, int businessId) {
        this.className = className;
        this.businessId = businessId;
    }
}
