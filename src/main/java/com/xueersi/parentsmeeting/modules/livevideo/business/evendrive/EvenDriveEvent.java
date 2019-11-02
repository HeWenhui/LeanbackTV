package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

/**
 * 中学激励系统事件
 */
public class EvenDriveEvent {
    //关闭互动题结果页,老连对使用
    public static final int CLOSE_H5 = 0;
    //更新互动题的正确率
    public static final int UPDATE_EVEN_RIGHT = 1;
    private int status;

    public EvenDriveEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
