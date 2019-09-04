package com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog;

import java.util.Map;

/**
 * 上报实体
 */
public class LiveBusiLogEntity {


    public  String businessAppId;
    public int logType;  //-1:sys 0:pv  1：click  2:show  3:launch
    public Map<String, String> mData;

}
