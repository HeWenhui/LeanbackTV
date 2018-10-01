package com.xueersi.parentsmeeting.modules.livevideo.event;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

/**
* 直播回放  试题消息
*@author chekun
*created  at 2018/10/1 15:44
*/
public class LiveBackQuestionEvent {

    /**试题开始展示事件*/
    public static final int QUSTIONS_SHOW = 1;
    /**试题结束展示事件*/
    public static final int QUSTION_CLOSE = 2;
    private int mEnvetnType;
    private VideoQuestionEntity mData;

    /**
     * @param envetnType  当前事件 类型 : 1 开；2：关闭
     * @param questionLiveEntity    当前试题数据信息
     */
    public LiveBackQuestionEvent(int envetnType, VideoQuestionEntity questionLiveEntity) {
        this.mEnvetnType = envetnType;
        this.mData = questionLiveEntity;
    }

    public int getEnvetnType() {
        return mEnvetnType;
    }

    public VideoQuestionEntity getData() {
        return mData;
    }
}

