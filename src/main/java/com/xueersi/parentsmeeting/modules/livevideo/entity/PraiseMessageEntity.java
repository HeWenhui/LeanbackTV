package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class PraiseMessageEntity {
    //特效礼物消息
    public final static int TYPE_SPECIAL_GIFT = 1;

    //点赞消息
    public final static int TYPE_PRAISE = 2;

    //班级
    public final static int TYPE_CLASS = 3;


    //数学
    public static final int SPECIAL_GIFT_TYPE_MATH = 0;

    //物理
    public static final int SPECIAL_GIFT_TYPE_PHYSICAL = 1;

    //化学
    public static final int SPECIAL_GIFT_TYPE_CHEMISTRY = 2;

    //消息类型
    private int messageType;

    //特效礼物类型
    private int giftType;

    //消息内容
    private String messageContent;

    //点赞的具体数值
    private long praiseNum;

    private String userId;

    //用户名称
    private String userName;

    //来自主讲或者辅导
    private String from;

    public long getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(long praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PraiseMessageEntity) {
            PraiseMessageEntity other = (PraiseMessageEntity) obj;
            if (this.userId != null && this.userId.equals(other.userId)) {
                return true;
            }
        }
        return false;
    }
}
