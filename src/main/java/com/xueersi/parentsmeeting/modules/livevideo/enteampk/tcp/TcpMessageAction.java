package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import org.json.JSONObject;

/**
 * @author chekun
 * created  at 2018/6/20 10:31
 */
public interface TcpMessageAction {

    void onMessage(short type, int operation, String msg);

    /**
     * notice 消息过滤
     *
     * @return
     */
    short[] getMessageFilter();

}
