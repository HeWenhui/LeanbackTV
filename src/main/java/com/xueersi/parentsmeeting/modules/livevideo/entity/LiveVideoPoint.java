package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

/**
 * Created by lyqai on 2018/7/10.
 * 直播的10个点
 */
public class LiveVideoPoint {
    Logger logger = LoggerFactory.getLogger("LiveVideoPoint");
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
        String info = "x=" + x2 + "," + x3 + "," + x4 + ",5=" + screenWidth + ",vw=" + videoWidth + ",pw=" + pptWidth + ",hw=" + headWidth
                + ",y=" + y2 + "," + y3 + "," + y4 + ",5=" + screenHeight + ",vh=" + videoHeight + ",hh=" + headHeight + ",mh=" + msgHeight;
        logger.d("initLiveVideoPoint:info=" + info);
        return info;
    }

    public static boolean initLiveVideoPoint(Activity activity, LiveVideoPoint liveVideoPoint, ViewGroup.LayoutParams lp) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int screenHeight = ScreenUtils.getScreenHeight();
        if (liveVideoPoint.screenWidth == screenWidth && liveVideoPoint.videoWidth == lp.width && liveVideoPoint.videoHeight == lp.height) {
            return false;
        }
        //计算x的几个点
        liveVideoPoint.x2 = (screenWidth - lp.width) / 2;
        //头像的宽度
        int headWidth = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / LiveVideoConfig.VIDEO_WIDTH);
        liveVideoPoint.videoWidth = lp.width;
        liveVideoPoint.headWidth = headWidth;
        liveVideoPoint.pptWidth = liveVideoPoint.videoWidth - headWidth;

        liveVideoPoint.x3 = liveVideoPoint.x2 + liveVideoPoint.pptWidth;
        liveVideoPoint.x4 = liveVideoPoint.x2 + lp.width;
        liveVideoPoint.screenWidth = screenWidth;
        //计算y的几个点
        liveVideoPoint.y2 = (screenHeight - lp.height) / 2;
        //头像的高度
        int headHeight = (int) ((LiveVideoConfig.VIDEO_HEAD_HEIGHT) * lp.height / LiveVideoConfig.VIDEO_HEIGHT);
        liveVideoPoint.videoHeight = lp.height;
        liveVideoPoint.headHeight = headHeight;
        liveVideoPoint.msgHeight = lp.height - headHeight;
        liveVideoPoint.y3 = liveVideoPoint.y2 + headHeight;
        liveVideoPoint.y4 = liveVideoPoint.y2 + lp.height;
        liveVideoPoint.screenHeight = screenHeight;
        liveVideoPoint.toString();
        return true;
    }
}