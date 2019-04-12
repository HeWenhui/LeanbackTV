package com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog;

import android.text.TextUtils;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
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

    /**
     * 直播体验课视频 播放失败 上传日志
     *
     * @param liveAndBackDebug
     * @param playErrorCode
     */
    public static void standExperienceLivePlayError(LiveAndBackDebug liveAndBackDebug, PlayErrorCode playErrorCode) {
        try {
            StableLogHashMap stableLogHashMap = new StableLogHashMap();
            stableLogHashMap.put("errcode", "" + playErrorCode.getCode());
            String errMsg = "视屏播放失败";
            if (!TextUtils.isEmpty(playErrorCode.getTip())) {
                errMsg = playErrorCode.getTip();
            }
            stableLogHashMap.put("errmsg", errMsg);
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.STAND_EXPERIENCE_LIVE_PLAY_ERROR, stableLogHashMap
                    .getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直播体验课视频 播放失败 上传日志
     *
     * @param liveAndBackDebug
     */
    public static void standExperienceLivePlayError(LiveAndBackDebug liveAndBackDebug, String url, String errMsg) {
        try {
            StableLogHashMap stableLogHashMap = new StableLogHashMap();

//            String errMsg = "";
//            if (!TextUtils.isEmpty(playErrorCode.getTip())) {
//                errMsg = playErrorCode.getTip();
//            }
//            stableLogHashMap.put("errmsg","");
            stableLogHashMap.put("playurl", url);
            stableLogHashMap.put("errmsg", errMsg);
            liveAndBackDebug.umsAgentDebugSys(LiveVideoConfig.STAND_EXPERIENCE_LIVE_PLAY_ERROR, stableLogHashMap
                    .getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
