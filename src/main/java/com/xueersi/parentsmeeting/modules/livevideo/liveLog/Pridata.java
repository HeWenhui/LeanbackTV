package com.xueersi.parentsmeeting.modules.livevideo.liveLog;


import com.xueersi.common.logerhelper.network.PingInfo;

import java.util.Map;

/**
 * 直播 日志参数
 */
public class Pridata {


    public NetSpeed netspeed; //网速

    public Map<String, String> dnsinfo; //网络DNS;

    public Map<String, PingInfo> ping;  //域名ping 信息

    public String sig="";

}


