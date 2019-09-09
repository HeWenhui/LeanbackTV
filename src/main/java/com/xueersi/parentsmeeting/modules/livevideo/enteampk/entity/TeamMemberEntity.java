package com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity;

public class TeamMemberEntity {
    public int id;
    public boolean isMy;
    public boolean isSelect;
    public String name;
    public String headurl = "";
    public int resId;
    public int gold;
    public int energy;
    public int praiseCount;
    public int thisPraiseCount;
    /** 聊天的昵称，irc返回,更准 */
    public String nickName = "";
    /** 聊天的昵称，接口返回 */
    private String nick_name = "";

    /** 段位 */
    private int segmentType;
    /** 星星 */
    private int star;

    public void copy(TeamMemberEntity other) {
        id = other.id;
        isMy = other.isMy;
        name = other.name;
        headurl = other.headurl;
        resId = other.resId;
        gold = other.gold;
        energy = other.getEnergy();
        praiseCount = other.praiseCount;
        thisPraiseCount = other.thisPraiseCount;
        nickName = other.nickName;
        nick_name = other.nick_name;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
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
        return "id=" + id + ",nickName=" + nickName;
    }
}
