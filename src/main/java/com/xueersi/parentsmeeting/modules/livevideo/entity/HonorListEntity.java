package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 光荣榜
 */

public class HonorListEntity {

    /** 是否点赞标志位 */
    private int praiseStatus;

    /** 1表示我在榜上，0表示我不在榜上 */
    private int isMy;


    ArrayList<HonorEntity> honorEntities = new ArrayList<>();

    public ArrayList<HonorEntity> getHonorEntities() {
        return honorEntities;
    }

    public void setHonorEntities(ArrayList<HonorEntity> honorEntities) {
        this.honorEntities = honorEntities;
    }

    public class HonorEntity {
        /** 学生总共上过优秀榜的次数 */
        private String excellentNum;
        /** 用户姓名 */
        private String stuName;

        /** 1表示我在榜上，0表示我不在榜上 */
        private int isMy;

        public String getExcellentNum() {
            return excellentNum;
        }

        public void setExcellentNum(String excellentNum) {
            this.excellentNum = excellentNum;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }

    }

    public int getPraiseStatus() {
        return praiseStatus;
    }

    public void setPraiseStatus(int praiseStatus) {
        this.praiseStatus = praiseStatus;
    }


    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

}
