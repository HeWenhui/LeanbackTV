package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload.utils;

import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

import java.io.File;

public class LogHelper {

    public static void sendUms(String eventId,
                               String logtype,
                               String preloadid,
                               String isuseip,
                               String loadurl,
                               String isresume,
                               String loadtime,
                               String sno,
                               String status,
                               String errorcode,
                               String resourcetype,
                               String failurl,
                               String liveid) {
        StableLogHashMap hashMap = new StableLogHashMap();
        hashMap.put("logtype", logtype);
        hashMap.put("preloadid", preloadid);
        hashMap.put("isuseip", isuseip);
        hashMap.put("loadurl", loadurl);
        hashMap.put("isresume", isresume);
        hashMap.put("loadtime", loadtime);
        hashMap.put("sno", sno);
        hashMap.put("status", status);
        hashMap.put("errorcode", errorcode);
        hashMap.put("resourcetype", resourcetype);
        hashMap.put("failurl", failurl);
        hashMap.put("liveid", liveid);
        hashMap.put("ip", IpAddressUtil.USER_IP);
        hashMap.put("freeSize", "" + CoursewarePreload.getFreeSize());
        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID, eventId, hashMap.getData());
    }


}
