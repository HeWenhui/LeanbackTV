package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

/**
 * Created by linyuqiang on 2018/1/26.
 */

public class LecAdvertLog {
    static String eventid = LiveVideoConfig.LEC_ADS;

    /**
     * 讲座广告日志第4步
     *
     * @param lecAdvertEntity
     * @param liveAndBackDebug
     */
    public static void sno4(LecAdvertEntity lecAdvertEntity, LiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
        logHashMap.put("adsid", "" + lecAdvertEntity.id);
        logHashMap.addSno("4").addStable("1").addExY();
        logHashMap.addNonce("" + lecAdvertEntity.nonce);
        liveAndBackDebug.umsAgentDebugPv(eventid, logHashMap.getData());
    }

    /**
     * 讲座广告日志第5步
     *
     * @param lecAdvertEntity
     * @param liveAndBackDebug
     */
    public static void sno5(LecAdvertEntity lecAdvertEntity, LiveAndBackDebug liveAndBackDebug) {
        StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
        logHashMap.put("adsid", "" + lecAdvertEntity.id);
        logHashMap.addSno("5").addStable("1").addExY();
        logHashMap.addNonce("" + lecAdvertEntity.nonce);
        liveAndBackDebug.umsAgentDebugInter(eventid, logHashMap.getData());
    }
}
