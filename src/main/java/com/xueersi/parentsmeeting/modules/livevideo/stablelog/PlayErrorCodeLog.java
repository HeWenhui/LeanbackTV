package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;

/**
 * Created by linyuqiang on 2018/9/8.
 * 播放失败的统计
 */
public class PlayErrorCodeLog {

    public static void livePlayError(LiveAndBackDebug liveAndBackDebug, PlayErrorCode playErrorCode) {
        try {
            StableLogHashMap stableLogHashMap = new StableLogHashMap();
            stableLogHashMap.put("code", "" + playErrorCode.getCode());
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.LIVE_PLAY_ERROR, stableLogHashMap.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}