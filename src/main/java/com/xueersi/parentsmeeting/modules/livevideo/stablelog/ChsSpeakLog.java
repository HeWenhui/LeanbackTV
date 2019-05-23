package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
*语文开讲吧 埋点日志
*@author chekun
*created  at 2019/5/23 13:54
*/
public class ChsSpeakLog {
    private static final String EVENT_ID = "live_chs_speak";

    /**
     * 统计学生作答方式
     * @param liveAndBackDebug
     * @param testid   试题id
     * @param mode     0/1 语音/手动
     * @param loadurl  试题加载地址
     * @param isplayback  0/1  是否是直播   直播/回放
     */
    public static void  anserMode(LiveAndBackDebug liveAndBackDebug, String testid,String mode,String loadurl,boolean isplayback){
        StableLogHashMap logHashMap = new StableLogHashMap("answerMode");
        logHashMap.put("testid", testid);
        logHashMap.put("mode", mode);
        logHashMap.put("loadurl", loadurl);
        logHashMap.put("isplayback", isplayback?"1":"0");
        liveAndBackDebug.umsAgentDebugInter(EVENT_ID, logHashMap.getData());
    }


}
