package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/1/12.
 * 接麦两个步骤
 */
public class VideoChatLog {
    private static String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;

    /** 接麦第2步，收到举手 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String teacher_type, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getStartLinkMic");
        logHashMap.put("teachertype", teacher_type);
//        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("2").addExY().addNonce(nonce).addStable("1");
//        }
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 接麦第3步，学生检查麦克风 */
    public static void sno3(LiveAndBackDebug liveAndBackDebug, String teacher_type, String nonce, boolean microphoneable) {
        StableLogHashMap logHashMap = new StableLogHashMap("MicrophoneCheck");
        logHashMap.put("teachertype", teacher_type);
        logHashMap.put("microphoneable", microphoneable ? "true" : "false");
//        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("3").addExY().addNonce(nonce).addStable("2");
//        }
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 接麦第四步，举手 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("raiseHand");
        logHashMap.put("clicktype", "clicked");
        logHashMap.put("status", "1");
//        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("4").addStable("2");
//        } else {
//            logHashMap.addSno("1").addStable("1");
//        }
        logHashMap.addExpect("1").addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 接麦第六步，学生收到上麦指令 */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String teacher_type, String is_selected, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getReadyLinkMic");
        logHashMap.put("teachertype", teacher_type);
        logHashMap.put("isselected", is_selected);
//        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("7").addStable("1");
//        }
        logHashMap.addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
    }

    /** 接麦第七步，加入房间 */
    public static void sno8(LiveAndBackDebug liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("joinChannelSuccess");
        logHashMap.put("channelname", room);
        logHashMap.put("status", (joinChannel == 0 ? "1" : "0"));
        logHashMap.addEx((joinChannel == 0 ? "Y" : "N"));
        if (!StringUtils.isEmpty(nonce)) {
            logHashMap.addNonce(nonce);
//            if (LiveVideoConfig.IS_SCIENCE) {
                logHashMap.addSno("8");
                logHashMap.addStable("2");
//            } else {
//                logHashMap.addSno("4");
//                logHashMap.addStable("1");
//            }
        }
        if (joinChannel != 0) {
            logHashMap.put("errcode", "" + joinChannel);
        }
        liveAndBackDebug.umsAgentDebugPv(eventId, logHashMap.getData());
    }
}
