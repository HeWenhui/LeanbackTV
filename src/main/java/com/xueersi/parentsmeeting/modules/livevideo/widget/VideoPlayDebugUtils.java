package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.View;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 跟直播播放器相关的日志上报类，辅助排查播放器相关问题
 */
public class VideoPlayDebugUtils {
    //视频播放View相关属性上报
    static String VIDEO_VIEW_DEBUG = "video_view_debug";
    static String LIVEVIDEO_VIDEO_VIEW = "livevideo_videoview";
    static String VIDEO_VIEW_CONTENT = "livevideo videoview is not visible";
    static String SET_VIDEO_VIEW_GONE = "set videoview gone";
    static String SET_VIDEO_VIEW_VISIBLE = "set videoview visible";

    /**
     * 如果videoview处于not visible状态，上报日志
     *
     * @param context
     * @param view
     */
    public static void umsIfVideoViewIsNotVisible(Context context, View view) {
        if (context != null && view != null && view.getVisibility() != View.VISIBLE) {
            Map map = new HashMap();
            map.put(LIVEVIDEO_VIDEO_VIEW, VIDEO_VIEW_CONTENT);
            UmsAgentManager.umsAgentDebug(context, VIDEO_VIEW_DEBUG, map);
        }
    }

    public static void umsVideoViewGone(Context context, View view) {
        if (context != null && view != null && view.getVisibility() != View.VISIBLE) {
//            livevideo_videoview
            Map map = new HashMap();
            map.put(LIVEVIDEO_VIDEO_VIEW, SET_VIDEO_VIEW_GONE);
            UmsAgentManager.umsAgentDebug(context, VIDEO_VIEW_DEBUG, map);
        }
    }


    public static void umsVideoViewVisible(Context context, View view) {
        if (context != null && view != null && view.getVisibility() != View.VISIBLE) {
//            livevideo_videoview
            Map map = new HashMap();
            map.put(LIVEVIDEO_VIDEO_VIEW, SET_VIDEO_VIEW_VISIBLE);
            UmsAgentManager.umsAgentDebug(context, VIDEO_VIEW_DEBUG, map);
        }
    }
}
