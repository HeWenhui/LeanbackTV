package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

/**
 * Created by lyqai on 2018/6/22.
 */

public class VideoOrientationEventListener extends OrientationEventListener {
    /** 当前界面方向-上方 */
    public static final int DIRECTION_UP = 0;
    /** 当前界面方向-手机左侧抬起 */
    public static final int DIRECTION_LEFT = 1;
    /** 当前界面方向-手机右侧抬起 */
    public static final int DIRECTION_RIGHT = 2;
    /** 当前界面方向-下方-暂时没有 */
    public static final int DIRECTION_DOWN = 3;

    /** 是否可以自动横竖屏转换 */
    protected boolean mIsAutoOrientation = true;
    /** 是否点击了横竖屏切换按钮 */
    private boolean mClick = false;
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    /** 点击进入横屏 */
    private boolean mClickLand = true;
    /** 点击进入竖屏 */
    private boolean mClickPort = true;
    /** 当前界面方向 */
    protected int mDirection = DIRECTION_UP;

    public VideoOrientationEventListener(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (((orientation >= 0) && (orientation <= 30)) || ((orientation <= 360) && (orientation >= 330))) {
            if (!mIsAutoOrientation) {
                // 不自动旋转屏幕时退出
                return;
            }
            if (mClick) {
                if (mIsLand && !mClickLand) {
                    return;
                } else {
                    mClickPort = true;
                    mClick = false;
                    mIsLand = false;
                }
            } else {
                if (mIsLand) {
                    mDirection = DIRECTION_UP;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mIsLand = false;
                    mClick = false;
                }
            }
        } else if (((orientation >= 230) && (orientation <= 310))) {
            if (!mIsAutoOrientation && mDirection == DIRECTION_UP) {
                // 不自动旋转屏幕,竖屏不能转横屏，但是横屏左右可切换
                return;
            }
            if (mClick) {
                if (!mIsLand && !mClickPort) {
                    return;
                } else {
                    mClickLand = true;
                    mClick = false;
                    mIsLand = true;
                }
            } else {
                if (mDirection != DIRECTION_RIGHT) {
                    mDirection = DIRECTION_RIGHT;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mIsLand = true;
                    mClick = false;
                }
            }
        } else if (((orientation >= 50) && (orientation <= 130))) {
            if (!mIsAutoOrientation && mDirection == DIRECTION_UP) {
                // 不自动旋转屏幕,竖屏不能转横屏，但是横屏左右可切换
                return;
            }
            if (mClick) {
                if (!mIsLand && !mClickPort) {
                    return;
                } else {
                    mClickLand = true;
                    mClick = false;
                    mIsLand = true;
                }
            } else {
                if (mDirection != DIRECTION_LEFT) {
                    mDirection = DIRECTION_LEFT;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    mIsLand = true;
                    mClick = false;
                }
            }
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mDirection = DIRECTION_UP;
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mDirection = DIRECTION_RIGHT;
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            mDirection = DIRECTION_LEFT;
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            mDirection = DIRECTION_DOWN;
        }
    }
}
