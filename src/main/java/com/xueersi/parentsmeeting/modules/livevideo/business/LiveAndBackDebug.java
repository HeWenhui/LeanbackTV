package com.xueersi.parentsmeeting.modules.livevideo.business;

import java.util.Map;

/**
 * Created by linyuqiang on 2018/1/5.
 * 直播和回放日志统计
 */
public interface LiveAndBackDebug {

    void umsAgentDebug(String eventId, final Map<String, String> mData);

    void umsAgentDebug2(String eventId, final Map<String, String> mData);

    void umsAgentDebug3(String eventId, final Map<String, String> mData);

}
