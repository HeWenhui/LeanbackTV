package com.xueersi.parentsmeeting.modules.livevideo.entity;

import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveHeadView;

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
    private boolean playAnimation;
    private StandLiveHeadView standLiveHeadView;
    private String headUrl;
    private int segmentType;
    private int star;

    private String evenNum;
    //自己
    public static final int MESSAGE_MINE = 0;
    //教师
    public static final int MESSAGE_TEACHER = 1;
    //同班同学
    public static final int MESSAGE_CLASS = 2;
    //系统提示
    public static final int MESSAGE_TIP = 3;
    //献花
    public static final int MESSAGE_FLOWERS = 4;
    //连对激励系统的点赞
    public static final int EVEN_DRIVE_LIKE = 5;
    //连对激励系统学报
    public static final int EVEN_DRIVE_REPORT = 6;

    public LiveMessageEntity(String sender, int type, CharSequence text) {
        this.sender = sender;
        this.type = type;
        this.text = text;
    }

    public LiveMessageEntity(String sender, int type, CharSequence text, String headUrl) {
        this.sender = sender;
        this.type = type;
        this.text = text;
        this.headUrl = headUrl;
    }

    public LiveMessageEntity(String sender, int type, CharSequence text, String headUrl,int segmentType,int star) {
        this.sender = sender;
        this.type = type;
        this.text = text;
        this.headUrl = headUrl;
        this.segmentType = segmentType;
        this.star = star;
    }

    public LiveMessageEntity(boolean self, String sender, int type, int ftype) {
        this.self = self;
        this.sender = sender;
        this.type = type;
        this.ftype = ftype;
    }

    public String getEvenNum() {
        return evenNum;
    }

    public void setEvenNum(String evenNum) {
        this.evenNum = evenNum;
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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isPlayAnimation() {
        return playAnimation;
    }

    public void setPlayAnimation(boolean playAnimation) {
        this.playAnimation = playAnimation;
    }

    public StandLiveHeadView getStandLiveHeadView() {
        return standLiveHeadView;
    }

    public void setStandLiveHeadView(StandLiveHeadView standLiveHeadView) {
        this.standLiveHeadView = standLiveHeadView;
    }

    public int getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(int segmentType) {
        this.segmentType = segmentType;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "type=" + type + ",text=" + text;
    }
}
