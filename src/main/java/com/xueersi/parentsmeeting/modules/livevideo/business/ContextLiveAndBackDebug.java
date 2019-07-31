package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by linyuqiang on 2018/8/23.
 */
public class ContextLiveAndBackDebug implements LiveAndBackDebug {
    private LiveAndBackDebug liveAndBackDebug;
    private WeakHashMap<String, Context> contextWeakHashMap = new WeakHashMap<>();
    private StableLogHashMap commonStableLogHashMap;

    public ContextLiveAndBackDebug(Context context) {
        contextWeakHashMap.put("context", context);
    }

    /**
     * 一些共有参数
     *
     * @param key
     * @param value
     */
    public void addCommonData(String key, String value) {
        if (commonStableLogHashMap == null) {
            commonStableLogHashMap = new StableLogHashMap();
        }
        commonStableLogHashMap.put(key, value);
    }

    /**
     * 一些共有参数
     *
     * @param key
     * @param value
     */
    public void addCommonAnal(String key, String value) {
        if (commonStableLogHashMap == null) {
            commonStableLogHashMap = new StableLogHashMap();
        }
        commonStableLogHashMap.putAnal(key, value);
    }

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                Map<String, String> mData2 = commonStableLogHashMap.getData();
                mData.putAll(mData2);
            }
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                Map<String, String> mData2 = commonStableLogHashMap.getData();
                mData.putAll(mData2);
            }
            liveAndBackDebug.umsAgentDebugInter(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugPv(String eventId, Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                Map<String, String> mData2 = commonStableLogHashMap.getData();
                mData.putAll(mData2);
            }
            liveAndBackDebug.umsAgentDebugPv(eventId, mData);
        }
    }

    @Override
    public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                stableLogHashMap.putAll(commonStableLogHashMap);
            }
            liveAndBackDebug.umsAgentDebugSys(eventId, stableLogHashMap);
        }
    }

    @Override
    public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                stableLogHashMap.putAll(commonStableLogHashMap);
            }
            liveAndBackDebug.umsAgentDebugInter(eventId, stableLogHashMap);
        }
    }

    @Override
    public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(contextWeakHashMap.get("context"), LiveAndBackDebug.class);
        }
        if (liveAndBackDebug != null) {
            if (commonStableLogHashMap != null) {
                stableLogHashMap.putAll(commonStableLogHashMap);
            }
            liveAndBackDebug.umsAgentDebugPv(eventId, stableLogHashMap);
        }
    }
}
