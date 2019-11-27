package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @Date on 2019/11/13 14:09
 * @Author zhangyuansun
 * @Description 英语1v2 小组课 答题结果
 */
public class GroupClassAnswerResultEntity {
    public static final int TYPE_GOLD = 100;
    public static final int TYPE_GOLD_SCORE = 200;
    public static final int TYPE_ONE_HEAD = 300;
    public static final int TYPE_TWO_HEAD = 400;

    private int type;
    private int interactType;

    private Answer myAnswer;
    private Answer teamAnswer;

    public static class Answer {
        int rankNum = 0;
        int gold = 0;
        int score = 0;
        String name;
        String headPath;
        /** 完成率 **/
        int completePercentage;
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

        public int getCompletePercentage() {
            return completePercentage;
        }

        public void setCompletePercentage(int completePercentage) {
            this.completePercentage = completePercentage;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getInteractType() {
        return interactType;
    }

    public void setInteractType(int interactType) {
        this.interactType = interactType;
    }

    public Answer getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(Answer myAnswer) {
        this.myAnswer = myAnswer;
    }

    public Answer getTeamAnswer() {
        return teamAnswer;
    }

    public void setTeamAnswer(Answer teamAnswer) {
        this.teamAnswer = teamAnswer;
    }

}
