package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/1/5.
 * 直播和回放日志统计
 */
public interface BaseLiveAndBackDebug extends LiveProvide {

    /**
     * 调试信息
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebugSys(String eventId, final Map<String, String> mData);

    /**
     * 交互日志
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebugInter(String eventId, final Map<String, String> mData);

    /**
     * 展现日志
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebugPv(String eventId, final Map<String, String> mData);

    /**
     * 调试信息
     *
     * @param stableLogHashMap
     */
    void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap);

    /**
     * 交互日志
     *
     * @param stableLogHashMap
     */
    void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap);

    /**
     * 展现日志
     *
     * @param stableLogHashMap
     */
    void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap);
}
