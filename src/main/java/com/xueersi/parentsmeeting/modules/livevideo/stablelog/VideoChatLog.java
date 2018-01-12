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
        StableLogHashMap stableLogHashMap = new StableLogHashMap("raiseHand");
        stableLogHashMap.put("clicktype", "clicked");
        stableLogHashMap.put("status", "1");
        stableLogHashMap.put("sno", "1");
        stableLogHashMap.put("expect", "1");
        stableLogHashMap.put("nonce", nonce);
        stableLogHashMap.put("stable", "1");
        liveAndBackDebug.umsAgentDebug2(eventId, stableLogHashMap.getData());
    }

    /** 接麦第四步，加入房间 */
    public static void sno4(LiveAndBackDebug liveAndBackDebug, String nonce, String room, int joinChannel) {
        StableLogHashMap stableLogHashMap = new StableLogHashMap("joinChannelSuccess");
        stableLogHashMap.put("channelname", room);
        stableLogHashMap.put("status", (joinChannel == 0 ? "1" : "0"));
        stableLogHashMap.put("ex", (joinChannel == 0 ? "Y" : "N"));
        if (!StringUtils.isEmpty(nonce)) {
            stableLogHashMap.put("nonce", nonce);
            stableLogHashMap.put("sno", "4");
            stableLogHashMap.put("stable", "1");
        }
        if (joinChannel != 0) {
            stableLogHashMap.put("errcode", "" + joinChannel);
        }
        liveAndBackDebug.umsAgentDebug3(eventId, stableLogHashMap.getData());
    }
}
