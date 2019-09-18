package com.xueersi.parentsmeeting.modules.livevideo.business.graycontrol.entity;


import java.util.List;

/**
 * 直播Plugin 配置接口定义
 *
 * @author shixiaoqiang
 */
public class LiveModuleConfigInfo {

    //插件列表
    public List<LivePlugin> plugins;

    @Override
    public String toString() {
        return "LiveModuleConfigInfo={" +
                "pluginDatas=" + plugins +
                '}';
    }
}


