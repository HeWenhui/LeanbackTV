package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @Date on 2019/11/13 14:09
 * @Author zhangyuansun
 * @Description 英语1v2 小组课 答题结果
 */
public class GroupClassAnswerResultEntity {
    public static final int TYPE_GOLD = 100;
    public static final int TYPE_GOLD_SCORE = 200;
    public static final int TYPE_HEAD_NAME_GOLD_MULTI = 300;
    public static final int TYPE_HEAD_NAME_GOLD_SINGLE = 400;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int rankNum;
    int gold;
    int score;
    String name;
    String headPath;

    int teamRankNum;
    int teamGold;
    int teamScore;
    String teamName;
    String teamHeadPath;

    public int getRankNum() {
        return rankNum;
    }

    public void setRankNum(int rankNum) {
        this.rankNum = rankNum;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public int getTeamRankNum() {
        return teamRankNum;
    }

    public void setTeamRankNum(int teamRankNum) {
        this.teamRankNum = teamRankNum;
    }

    public int getTeamGold() {
        return teamGold;
    }

    public void setTeamGold(int teamGold) {
        this.teamGold = teamGold;
    }

    public int getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(int teamScore) {
        this.teamScore = teamScore;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamHeadPath() {
        return teamHeadPath;
    }

    public void setTeamHeadPath(String teamHeadPath) {
        this.teamHeadPath = teamHeadPath;
    }
}
