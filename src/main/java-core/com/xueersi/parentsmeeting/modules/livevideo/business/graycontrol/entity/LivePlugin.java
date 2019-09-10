package com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity;


import java.util.Map;

/**
 * 直播Plugin 定义
 *
 * @author shixiaoqiang
 */
public class LivePlugin {

    public int pluginId;

    public String pluginName;

    public boolean isAllowed = true;

    public int moduleId;

    public Map<String, String> properties;

    @Override
    public String toString() {
        return "LivePlugin={" +
                "pluginId=" + pluginId +
                ", pluginName='" + pluginName + '\'' +
                ", isAllowed=" + isAllowed +
                ", moduleId=" + moduleId +
                ", properties=" + properties +
                '}';
    }
}
