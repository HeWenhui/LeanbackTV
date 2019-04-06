package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import android.support.annotation.NonNull;

public class PraiseMessageEntity implements Comparable {
    //特效礼物消息
    public final static int TYPE_SPECIAL_GIFT = 1;

    //点赞消息
    public final static int TYPE_PRAISE = 2;

    //班级
    public final static int TYPE_CLASS = 3;

    //我点赞的总数量
    public final static int SORT_KEY_MY_PRAISE = 1;

    //我送出的礼物特效
    public final static int SORT_KEY_MY_GIFT = 2;

    //同班同学送出的礼物特效
    public final static int SORT_KEY_OTHER_GIFT = 3;

    //班级点赞的数量
    public final static int SORT_KEY_CLASS_PRAISE = 4;

    //同班同学点赞的数量
    public final static int SORT_KEY_STUDENT_PRAISE = 5;


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

    //排序关键字
    private int sortKey;

    public int getSortKey() {
        return sortKey;
    }

    public void setSortKey(int sortKey) {
        this.sortKey = sortKey;
    }

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

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof PraiseMessageEntity) {
            PraiseMessageEntity praiseMessageEntity = (PraiseMessageEntity) o;
            int sort = this.sortKey - praiseMessageEntity.getSortKey();
            if (sort == 0) {
                return 1;
            }
            return sort;
        }
        return 0;
    }
}
