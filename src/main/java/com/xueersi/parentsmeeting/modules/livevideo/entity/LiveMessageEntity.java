package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 聊天信息和竖屏献花信息
 * Created by linyuqiang on 2016/8/2.
 */
public class LiveMessageEntity {
    /** 发送人 */
    private String sender;
    /** 消息内容 */
    private CharSequence text;
    /** 消息类型 */
    private int type;
    /** 献花用，花朵类型 */
    private int ftype;
    /** 献花用，是不是自己献花 */
    private boolean self;
    public static final int MESSAGE_MINE = 0;
    public static final int MESSAGE_TEACHER = 1;
    public static final int MESSAGE_CLASS = 2;
    public static final int MESSAGE_TIP = 3;
    public static final int MESSAGE_FLOWERS = 4;

    public LiveMessageEntity(String sender, int type, CharSequence text) {
        this.sender = sender;
        this.type = type;
        this.text = text;
    }

    public LiveMessageEntity(boolean self, String sender, int type, int ftype) {
        this.self = self;
        this.sender = sender;
        this.type = type;
        this.ftype = ftype;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public int getFtype() {
        return ftype;
    }

    public boolean isSelf() {
        return self;
    }
}
