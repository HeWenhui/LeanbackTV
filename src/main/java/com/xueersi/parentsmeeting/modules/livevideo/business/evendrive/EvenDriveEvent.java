package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

/**
 * 中学激励系统事件
 */
public class EvenDriveEvent {
    public static final int CLOSE_H5 = 0;
    private int status;

    public EvenDriveEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
