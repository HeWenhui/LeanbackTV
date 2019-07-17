package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity;

public class SpeechStopEntity {
    private int type;
    private int goldNum;
    private int speechStatus;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGoldNum() {
        return goldNum;
    }

    public void setGoldNum(int goldNum) {
        this.goldNum = goldNum;
    }

    public int getSpeechStatus() {
        return speechStatus;
    }

    public void setSpeechStatus(int speechStatus) {
        this.speechStatus = speechStatus;
    }
}
