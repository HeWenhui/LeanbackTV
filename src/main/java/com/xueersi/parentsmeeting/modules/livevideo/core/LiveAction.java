package com.xueersi.parentsmeeting.modules.livevideo.core;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;

import org.json.JSONObject;

/**
*
*@author chekun
*created  at 2018/6/20 12:27
*/
public interface LiveAction extends LiveAndBackDebug{

    /**
     * 发送 notice 消息
     * @param data
     * @param targetName  消息接受方姓名  当targetName 为Null时 广播发送消息
     * @return    是否发送成功
     */
    boolean sendNotice(String targetName,JSONObject data);

    /**
     * 发送 msg 消息
     * @param data
     * @return 是否发送成功
     */
    boolean sendMessage(JSONObject data);

}
