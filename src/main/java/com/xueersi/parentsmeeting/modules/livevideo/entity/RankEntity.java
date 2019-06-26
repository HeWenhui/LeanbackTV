package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by lingyuqiang on 2017/9/21.
 * 排名统一字段
 */
public class RankEntity {
    String id;
    String rank;
    String name;
    String rate;
    boolean isMe;
    /** 点赞数量 */
    private int thumbsUpNum;
    /** 是否可以点赞 */
    private int isThumbsUp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public int getThumbsUpNum() {
        return thumbsUpNum;
    }

    public void setThumbsUpNum(int thumbsUpNum) {
        this.thumbsUpNum = thumbsUpNum;
    }

    public int getIsThumbsUp() {
        return isThumbsUp;
    }

    public void setIsThumbsUp(int isThumbsUp) {
        this.isThumbsUp = isThumbsUp;
    }
}
