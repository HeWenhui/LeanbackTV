package com.xueersi.parentsmeeting.modules.livevideoOldIJK.core;

import org.json.JSONObject;

/**
*
*@author chekun
*created  at 2018/6/20 10:31
*/
public interface NoticeAction {

    /**
     * notice消息
     * @param sourceNick
     * @param target
     * @param data   notice 数据
     * @param type  消息类型
     */
   void onNotice(String sourceNick, String target, JSONObject data, int type);

    /**
     * notice 消息过滤
     * @return
     */
   int[] getNoticeFilter();

}
