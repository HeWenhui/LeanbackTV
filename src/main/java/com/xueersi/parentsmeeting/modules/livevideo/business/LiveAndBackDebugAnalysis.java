package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.util.Map;

public interface LiveAndBackDebugAnalysis extends LiveAndBackDebug {
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
