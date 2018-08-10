package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @author  chenkun
 * 学生获取宝箱
 */
public class StudentChestEntity {
    private  int gold;
    /**ai 碎片奖励 数*/
    private  int chipNum;
    /**碎片名称*/
    private  String chipName;
    private  String  isGet;

    private boolean isAiPatner;

    public StudentChestEntity(){

    }

    public StudentChestEntity(int gold, int chipNum,String chipName ,String isGet) {
        this.gold = gold;
        this.isGet = isGet;
        this.chipNum = chipNum;
        this.chipName = chipName;
    }

    public int getGold() {
        return gold;
    }

    public int getChipNum() {
        return chipNum;
    }

    public void setChipNum(int chipNum) {
        this.chipNum = chipNum;
    }

    public String getChipName() {
        return chipName;
    }

    public void setChipName(String chipName) {
        this.chipName = chipName;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getIsGet() {
        return isGet;
    }

    public void setIsGet(String isGet) {
        this.isGet = isGet;
    }


    public boolean isAiPatner() {
        return isAiPatner;
    }

    public void setAiPatner(boolean isAiPatner){
        this.isAiPatner = isAiPatner;
    }
}
