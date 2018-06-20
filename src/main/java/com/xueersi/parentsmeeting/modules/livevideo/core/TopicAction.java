package com.xueersi.parentsmeeting.modules.livevideo.core;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
*
*@author chekun
*created  at 2018/6/20 10:32
*/
public interface TopicAction {

    /**
     * topic 消息
     * @param data
     * @param modeChange  是否发生主/辅导 态切换
     */
      void onTopic(LiveTopic data,boolean modeChange);

}
