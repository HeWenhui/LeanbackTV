package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

/**
 * 中学激励系统事件
 */
public class EvenDriveEvent {
    //关闭互动题结果页,老连对使用
    public static final int CLOSE_H5 = 0;
    //自传互动题
    public static final int CLOSE_SELF_H5 = 1;
    //更新互动题的正确率
    public static final int UPDATE_EVEN_RIGHT = 2;

    public static final int CALL_BACK_UPDATE_EVEN_RIGHT = 3;
    private int status;
    private String testId;
    private int nowNum;
    private int highestNum;

    public int getNowNum() {
        return nowNum;
    }

    public void setNowNum(int nowNum) {
        this.nowNum = nowNum;
    }

    public int getHighestNum() {
        return highestNum;
    }

    public void setHighestNum(int highestNum) {
        this.highestNum = highestNum;
    }

    public EvenDriveEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public EvenDriveEvent setTestId(String testId) {
        this.testId = testId;
        return this;
    }

    public String getTestId() {
        return testId;
    }
}
