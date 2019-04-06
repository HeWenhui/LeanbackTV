package com.xueersi.parentsmeeting.modules.livevideoOldIJK.event;


import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.VideoQuestionLiveEntity;

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
    private VideoQuestionLiveEntity mData;

    /**
     * @param envetnType  当前事件 类型 : 1 开；2：关闭
     * @param questionLiveEntity    当前试题数据信息
     */
    public LiveBackQuestionEvent(int envetnType, VideoQuestionLiveEntity questionLiveEntity) {
        this.mEnvetnType = envetnType;
        this.mData = questionLiveEntity;
    }

    public int getEnvetnType() {
        return mEnvetnType;
    }

    public VideoQuestionLiveEntity getData() {
        return mData;
    }
}

