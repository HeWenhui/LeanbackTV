package com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog;

import java.util.Map;

/**
 * 上报实体
 */
public class LiveBusiLogEntity {


    String businessAppId;
    int logType;  //-1:sys 0:pv  1：click  2:show  3:launch
    Map<String, String> mData;

}
