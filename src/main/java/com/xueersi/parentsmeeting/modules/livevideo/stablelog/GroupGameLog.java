package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;

public class GroupGameLog {
    private String eventId;

    /**
     * 试题类型
     *
     * @param type
     */
    public GroupGameLog(String type) {
        this.eventId = getIdFromType(type);
    }
    /**
     * 根据试题类型 获取埋点日志id
     *
     * @param type
     * @return
     */
    private String getIdFromType(String type) {
        String eventId = "";
        if (LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(type)) {
            eventId = "treasurehunt";
        } else if (LiveQueConfig.EN_COURSE_TYPE_WHAT_IS_MISSING.equals(type)) {
            eventId = "what'smissing";
        } else if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(type)) {
            eventId = "voicecannon";
        } else if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(type)) {
            eventId = "hotairballoon";
        } else if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(type)) {
            eventId = "cleaningup";
        }
        return eventId;
    }

    /**
     * 学生收到题目信息（SNO 2）   ---交互日志
     */
    public void sno2(LiveAndBackDebug liveAndBackDebug, String testid, int signal) {
        StableLogHashMap logHashMap = new StableLogHashMap(eventId+"Receive");
        logHashMap.put("testid", testid);
        logHashMap.put("signal", "" + signal);
        logHashMap.put("sno", "2");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * 开始作答时间---展示日志
     */
    public void sno3(LiveAndBackDebug liveAndBackDebug, String testid, int signal) {
        StableLogHashMap logHashMap = new StableLogHashMap(eventId+"Answer");
        logHashMap.put("testid", testid);
        logHashMap.put("signal", "" + signal);
        logHashMap.put("sno", "3");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

    /**
     * 翻页时间---交互
     */
    public void sno4(LiveAndBackDebug liveAndBackDebug, String testid, String subtestid, int signal) {
        StableLogHashMap logHashMap = new StableLogHashMap(eventId+"NextPage");
        logHashMap.put("testid", testid);
        logHashMap.put("subtestid", subtestid);
        logHashMap.put("signal", "" + signal);
        logHashMap.put("sno", "4");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * 游戏结束时间（SNO 5）---交互日志
     */
    public void sno5(LiveAndBackDebug liveAndBackDebug, String testid, String state,
                     String hasvoicetime, int signal) {
        StableLogHashMap logHashMap = new StableLogHashMap(eventId+"Submit");
        logHashMap.put("testid", testid);
        logHashMap.put("state", state);
        logHashMap.put("hasvoicetime", hasvoicetime);
        logHashMap.put("signal", "" + signal);
        logHashMap.put("sno", "5");
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /**
     * sno6显示结果页
     */
    public void sno6(LiveAndBackDebug liveAndBackDebug, String testid, String mvpnum, int signal) {
        StableLogHashMap logHashMap = new StableLogHashMap(eventId+"ShowMvp");
        logHashMap.put("testid", testid);
        logHashMap.put("mvpnum", "" + mvpnum);
        logHashMap.put("signal", "" + signal);
        logHashMap.put("sno", "6");
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }

}
