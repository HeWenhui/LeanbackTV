package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by linyuqiang on 2018/8/23.
 */
public class ContextLiveAndBackDebug implements LiveAndBackDebug {
    LiveAndBackDebug liveAndBackDebug;
    WeakHashMap<String, Context> contextWeakHashMap = new WeakHashMap<>();

    public ContextLiveAndBackDebug(Context context) {
        contextWeakHashMap.put("context", context);
    }

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            liveAndBackDebug.umsAgentDebugInter(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            liveAndBackDebug.umsAgentDebugPv(eventId, mData);
        }
    }
}
