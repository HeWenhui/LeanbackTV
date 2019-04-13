package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

import java.util.ArrayList;
import java.util.List;

/**
 * 中学连对激励系统使用Entity
 */
public class EvenDriveEntity {
    /** 其他学生的排名 */
    private List<OtherEntity> otherEntities;
    /** 用户自己的排名信息 */
    private MyEntity myEntity;

    public EvenDriveEntity() {
        this.otherEntities = new ArrayList<>();
    }

    public static class OtherEntity {

        /** 学生ID */
        private String stuId;
        /** 学生排名 */
        private int ranking;
        /** 连对数量 */
        private int evenPairNum;
        /** 是否可以点赞 */
        private int isThumbsUp;
        /** 学生名称 */
        private String name;
        /** 点赞数量 */
        private int thumbsUpNum;

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public int getEvenPairNum() {
            return evenPairNum;
        }

        public void setEvenPairNum(int evenPairNum) {
            this.evenPairNum = evenPairNum;
        }

        public int getIsThumbsUp() {
            return isThumbsUp;
        }

        public void setIsThumbsUp(int isThumbsUp) {
            this.isThumbsUp = isThumbsUp;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getThumbsUpNum() {
            return thumbsUpNum;
        }

        public void setThumbsUpNum(int thumbsUpNum) {
            this.thumbsUpNum = thumbsUpNum;
        }
    }

    //    我的排名信息
    public static class MyEntity {
        //    学生展示
        private String name;

        //    学生排名
        private int rank;

        //    当前连对数量
        private int evenPairNum;

        //    当前学生的最高连对
        private String highestRightNum;
        //学生ID
        private String stuId;


        /** 是否可以点赞 */
        private int isThumbsUp;

        /** 点赞数量 */
        private int thumbsUpNum;

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

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getEvenPairNum() {
            return evenPairNum;
        }

        public void setEvenPairNum(int evenPairNum) {
            this.evenPairNum = evenPairNum;
        }

        public String getHighestRightNum() {
            return highestRightNum;
        }

        public void setHighestRightNum(String highestRightNum) {
            this.highestRightNum = highestRightNum;
        }
    }

    public List<OtherEntity> getOtherEntities() {
        return otherEntities;
    }

    public void setOtherEntities(List<OtherEntity> otherEntities) {
        this.otherEntities = otherEntities;
    }

    public MyEntity getMyEntity() {
        return myEntity;
    }

    public void setMyEntity(MyEntity myEntity) {
        this.myEntity = myEntity;
    }
}
