package com.xueersi.parentsmeeting.modules.livevideo.event;
/**
 * 直播间内  h5 关闭事件
 *@author  chenkun
 * */
public class LiveRoomH5CloseEvent {
    private int mGoldNum; //h5 返回的 金币
    private int mEnergyNum; // h5返回的 能量
    private int h5Type;
    public  static  final int H5_TYPE_INTERACTION = 1; //互动题
    public  static  final int H5_TYPE_EXAM = 2; //测试卷
    public  static  final int H5_TYPE_COURSE= 3; //课件
    private String id; // testId/ testPlan
    private boolean closeByTeacher;// 是否是老师发起的关闭

    public void setCloseByTeahcer(boolean closeByTeacher) {
        this.closeByTeacher = closeByTeacher;
    }

    public boolean isCloseByTeacher() {
        return closeByTeacher;
    }

    public LiveRoomH5CloseEvent(){
    }

    public LiveRoomH5CloseEvent(int mGoldNum, int mEnergyNum,int h5Type,String id) {
        this.mGoldNum = mGoldNum;
        this.mEnergyNum = mEnergyNum;
        this.h5Type = h5Type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setH5Type(int h5Type) {
        this.h5Type = h5Type;
    }

    public int getH5Type() {
        return h5Type;
    }

    public int getmGoldNum() {
        return mGoldNum;
    }

    public void setmGoldNum(int mGoldNum) {
        this.mGoldNum = mGoldNum;
    }

    public int getmEnergyNum() {
        return mEnergyNum;
    }

    public void setmEnergyNum(int mEnergyNum) {
        this.mEnergyNum = mEnergyNum;
    }
}
