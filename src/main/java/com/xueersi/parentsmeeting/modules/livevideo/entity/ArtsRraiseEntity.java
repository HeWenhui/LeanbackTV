package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * 文科表扬榜数据模型
 *
 * @author chenkun
 * @version 1.0, 2018/7/18 上午10:32
 */

public class ArtsRraiseEntity {

    /**
     * 榜单名称
     */
    private String rankTitle;
    /**
     * 榜单类型
     */
    private int rankType;
    /**
     * 鼓励语
     */
    private String word;
    /**
     * 辅导老师昵称
     */
    private String counselorName;
    /**
     * 辅导老师头像
     */
    private String counselorAvatar;

    private List<RankEntity> rankEntities;

    public String getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(String rankTitle) {
        this.rankTitle = rankTitle;
    }

    public int getRankType() {
        return rankType;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName;
    }

    public String getCounselorAvatar() {
        return counselorAvatar;
    }

    public void setCounselorAvatar(String counselorAvatar) {
        this.counselorAvatar = counselorAvatar;
    }

    public List<RankEntity> getRankEntities() {
        return rankEntities;
    }

    public void setRankEntities(List<RankEntity> rankEntities) {
        this.rankEntities = rankEntities;
    }


    public static class RankEntity {

        private String stuId;
        private String realName;
        private int number;
        private int inList;

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getInList() {
            return inList;
        }

        public void setInList(int inList) {
            this.inList = inList;
        }
    }


}
