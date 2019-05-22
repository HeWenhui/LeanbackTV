package com.xueersi.parentsmeeting.modules.livevideo.event;
/**
*nobook 实验 事件
*@author chekun
*created  at 2019/4/5 14:46
*/
public class ChsSpeakEvent {
    public static final int EVENT_TYPE_PAGE_CLOSE = 1;
    private int eventType;
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }
}
