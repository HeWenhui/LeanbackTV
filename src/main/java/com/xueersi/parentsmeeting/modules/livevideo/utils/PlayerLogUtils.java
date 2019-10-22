package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.content.Context;
import android.util.Log;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.LiveLogUtils;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoConfigEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerLogUtils {
    public static void changPlayLineLog(VideoConfigEntity videoConfigEntity, Context context, Exception e) {
        if (videoConfigEntity != null) {
            Map<String, String> map = new HashMap<>();
            map.put("changeLinePos", videoConfigEntity.getChangeLinePos() + "");
            map.put("protocol", videoConfigEntity.getProtocol() + "");
            map.put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
            map.put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.CHANGE_LINE_EXCEPTION);
            if (context != null) {
                UmsAgentManager.umsAgentDebug(context, LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map);
            }
        }
    }

    public static void playerError(VideoConfigEntity configEntity,
                                   boolean isPlayerCreated, boolean playerNotNull, Exception e,
                                   Context context) {
        if (configEntity != null) {
            StableLogHashMap map = new StableLogHashMap();
            map.put("userName", configEntity.getUserName()).
                    put("userId", configEntity.getUserId() + "").
                    put("streamId", configEntity.getStreamId()).
                    put("protocol", String.valueOf(configEntity.getProtocol())).
                    put("isPlayerCreated", String.valueOf(isPlayerCreated)).
//                put("initPlayer", String.valueOf(vPlayer.checkNotNull())).
        put("initPlayer", String.valueOf(playerNotNull)).
                    put(LiveLogUtils.PLAYER_OPERATING_KEY, LiveLogUtils.PLAY_EXCEPTION).
                    put(LiveLogUtils.EXCEPTION_MESSAGE, Log.getStackTraceString(e));
            if (context != null) {
                UmsAgentManager.umsAgentDebug(context, LiveLogUtils.VIDEO_PLAYER_LOG_EVENT, map.getData());
            }
        }
    }
}
