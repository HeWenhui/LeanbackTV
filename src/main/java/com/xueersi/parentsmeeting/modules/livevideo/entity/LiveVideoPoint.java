package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by lyqai on 2018/7/10.
 * 直播的10个点
 */
public class LiveVideoPoint {
    private static LiveVideoPoint instance;
    public final int x1 = 0;
    /** 视频左边的位置 */
    public int x2;
    /** 视频ppt右侧的位置 */
    public int x3;
    /** 视频右边的位置 */
    public int x4;
    public int screenWidth;
    public int videoWidth;
    public int pptWidth;
    public int headWidth;
    public final int y1 = 0;
    /** 视频上边的位置 */
    public int y2;
    /** 视频头像下边的位置 */
    public int y3;
    /** 视频下边的位置 */
    public int y4;
    public int screenHeight;
    public int videoHeight;
    public int headHeight;
    public int msgHeight;

    private LiveVideoPoint() {

    }

    public static LiveVideoPoint getInstance() {
        if (instance == null) {
            return instance = new LiveVideoPoint();
        }
        return instance;
    }

    public int getRightMargin() {
        return screenWidth - x3;
    }

    @Override
    public String toString() {
        return "x=" + x2 + "," + x3 + "," + x4 + ",5=" + screenWidth + ",vw=" + videoWidth + ",pw=" + pptWidth + ",hw=" + headWidth
                + ",y=" + y2 + "," + y3 + "," + y4 + ",5=" + screenHeight + ",vh=" + videoHeight + ",hh=" + headHeight + ",mh=" + msgHeight;
    }
}
