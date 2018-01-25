package com.xueersi.parentsmeeting.modules.livevideo.business;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/1/5.
 * 直播和回放日志统计
 */
public interface LiveAndBackDebug {

    /**
     * 调试信息
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebug(String eventId, final Map<String, String> mData);

    /**
     * 交互日志
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebug2(String eventId, final Map<String, String> mData);

    /**
     * 展现日志
     *
     * @param eventId
     * @param mData
     */
    void umsAgentDebug3(String eventId, final Map<String, String> mData);

}
