package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.xesalib.utils.string.StringUtils;

/**
 * Created by linyuqiang on 2018/1/12.
 * 接麦两个步骤
 */
public class VideoChatLog {
    private static String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;

    /** 接麦第2步，收到举手 */
    public static void sno2(LiveAndBackDebug liveAndBackDebug, String teacher_type, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getStartLinkMic");
        logHashMap.put("teacher_type", teacher_type);
        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("2").addExY().addNonce(nonce).addStable("1");
        }
        liveAndBackDebug.umsAgentDebug2(eventId, logHashMap.getData());
    }

    /** 接麦第四步，举手 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("raiseHand");
        logHashMap.put("clicktype", "clicked");
        logHashMap.put("status", "1");
        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("4").addStable("2");
        } else {
            logHashMap.addSno("1").addStable("1");
        }
        logHashMap.addExpect("1").addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebug2(eventId, logHashMap.getData());
    }

    /** 接麦第六步，学生收到上麦指令 */
    public static void sno6(LiveAndBackDebug liveAndBackDebug, String teacher_type, String is_selected, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("getReadyLinkMic");
        logHashMap.put("teacher_type", teacher_type);
        logHashMap.put("is_selected", is_selected);
        if (LiveVideoConfig.IS_SCIENCE) {
            logHashMap.addSno("6").addStable("1");
        }
        logHashMap.addExY().addNonce(nonce);
        liveAndBackDebug.umsAgentDebug2(eventId, logHashMap.getData());
    }

    /** 接麦第七步，加入房间 */
    public static void sno7(LiveAndBackDebug liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("joinChannelSuccess");
        logHashMap.put("channelname", room);
        logHashMap.put("status", (joinChannel == 0 ? "1" : "0"));
        logHashMap.addEx((joinChannel == 0 ? "Y" : "N"));
        if (!StringUtils.isEmpty(nonce)) {
            logHashMap.addNonce(nonce);
            if (LiveVideoConfig.IS_SCIENCE) {
                logHashMap.addSno("7");
                logHashMap.addStable("2");
            } else {
                logHashMap.addSno("4");
                logHashMap.addStable("1");
            }
        }
        if (joinChannel != 0) {
            logHashMap.put("errcode", "" + joinChannel);
        }
        liveAndBackDebug.umsAgentDebug3(eventId, logHashMap.getData());
    }
}
