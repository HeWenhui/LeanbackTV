package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * @author 战队开宝箱结果
 */
public class ClassChestEntity {
    private long sumGold;
    /**
     * 自己是否是幸运之星
     */
    private boolean isMe;
    private List<SubChestEntity> subChestEntityList;

    public long getSumGold() {
        return sumGold;
    }

    public void setSumGold(long sumGold) {
        this.sumGold = sumGold;
    }

    public List<SubChestEntity> getSubChestEntityList() {
        return subChestEntityList;
    }

    public void setSubChestEntityList(List<SubChestEntity> subChestEntityList) {
        this.subChestEntityList = subChestEntityList;
    }


    public static class SubChestEntity {

        private long gold;
        private String stuName;
        private String avatarPath;
        private String stuId;


        public SubChestEntity(long gold, String stuName, String avatarPath, String stuId) {
            this.gold = gold;
            this.stuName = stuName;
            this.avatarPath = avatarPath;
            this.stuId = stuId;
        }

        public long getGold() {
            return gold;
        }

        public void setGold(long gold) {
            this.gold = gold;
        }

        public String getStuName() {
            return stuName;
        }

        public void setStuName(String stuName) {
            this.stuName = stuName;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

    public boolean isMe() {
        return isMe;
    }
}
