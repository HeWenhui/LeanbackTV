package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;

import java.util.HashMap;
import java.util.Map;

/**
* 热词 埋点日志
*@author chekun
*created  at 2019/5/16 13:10
*/
public class HotWordLog {
    private static final String eventId = "hot_word";

    /**半身直播-幼教**/
    public static final int LIVETYPE_PRESCHOOL = 1;
    /**半身直播-非幼教**/
    public static final int LIVETYPE_NOT_PRESHCOOL = 2;

    /**
     * @param liveAndBackDebug
     * @param hw_type 热词标识
     * @param type   直播类型  1 (幼教)， 2（站立直播非幼教）
     * @param classid
     * @param teamid
     * @param courseid
     */
    public static void hotWordSend(LiveAndBackDebug liveAndBackDebug, String hw_type, int type,
                                   String classid, String teamid, String courseid){
        Map<String,String> logHashMap = new HashMap<String, String>();
        logHashMap.put("hw_type", hw_type);
        logHashMap.put("type",  type+"");
        logHashMap.put("classid",  classid);
        logHashMap.put("teamid", teamid);
        logHashMap.put("courseid", courseid);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap);

    }
}
