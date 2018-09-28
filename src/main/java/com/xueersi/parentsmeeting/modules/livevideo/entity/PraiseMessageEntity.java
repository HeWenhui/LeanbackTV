package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class PraiseMessageEntity {
    //特效礼物礼物
    public final static int TYPE_SPECIAL_GIFT = 1;

    //班级点赞消息
    public final static int TYPE_CLASS = 2;

    private int messageType;
    private int giftType;
    private String messageContent;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
