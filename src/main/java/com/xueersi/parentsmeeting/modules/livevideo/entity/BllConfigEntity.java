package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class BllConfigEntity {
    public String className;
    public String intent;
    public int pluginId;

    public BllConfigEntity(String className) {
        this.className = className;
    }

    public BllConfigEntity(String className, String intent) {
        this.className = className;
        this.intent = intent;
    }
    public BllConfigEntity(String className, int pluginId) {
        this.className = className;
        this.pluginId = pluginId;
    }
}
