package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author  chenkun
 * 学生获取宝箱
 */
public class StudentChestEntity {
    private  String gold;
    private  String  isGet;


    public StudentChestEntity(String gold, String isGet) {
        this.gold = gold;
        this.isGet = isGet;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getIsGet() {
        return isGet;
    }

    public void setIsGet(String isGet) {
        this.isGet = isGet;
    }
}
