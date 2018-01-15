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

    /** 接麦第一步，举手 */
    public static void sno1(LiveAndBackDebug liveAndBackDebug, String nonce) {
        StableLogHashMap logHashMap = new StableLogHashMap("raiseHand");
        logHashMap.put("clicktype", "clicked");
        logHashMap.put("status", "1");
        logHashMap.addSno("1").addExpect("1").addNonce(nonce).addStable("1");
        liveAndBackDebug.umsAgentDebug2(eventId, logHashMap.getData());
    }

    /** 接麦第四步，加入房间 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap logHashMap = new StableLogHashMap("joinChannelSuccess");
        logHashMap.put("channelname", room);
        logHashMap.put("status", (joinChannel == 0 ? "1" : "0"));
        logHashMap.addEx((joinChannel == 0 ? "Y" : "N"));
        if (!StringUtils.isEmpty(nonce)) {
            logHashMap.addNonce(nonce).addSno("4").addStable("1");
        }
        if (joinChannel != 0) {
            logHashMap.put("errcode", "" + joinChannel);
        }
        liveAndBackDebug.umsAgentDebug3(eventId, logHashMap.getData());
    }
}
