package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity2;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;

/**
 * Created by lyqai on 2018/7/13.
 */

public class StandLiveVideoActivity2 extends LiveVideoActivity2 {
    private String TAG = "StandLiveVideoActivity2Log";
    Logger logger = LoggerFactory.getLogger(TAG);
    LiveStandFrameAnim liveStandFrameAnim;
    boolean startGetInfo = false;
    LiveStandMediaControllerBottom standMediaControllerBottom;
    private RelativeLayout rlFirstBackgroundContent;
    private FrameLayout flFirstBackgroundContent;
    boolean isSetFirstParam = true;

    public StandLiveVideoActivity2() {
        mLayoutVideo = R.layout.activity_video_live_stand_new;
    }

    @Override
    protected void initView() {
        super.initView();
        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
    }

    @Override
    protected void setFirstParam(ViewGroup.LayoutParams lp) {
//        super.setFirstParam(lp);
        setFirstParam(lp, isSetFirstParam);
        isSetFirstParam = false;
    }

    /**
     * 设置蓝屏界面
     */
    private void setFirstParam(ViewGroup.LayoutParams lp, boolean first) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        Loger.d(TAG, "setFirstParam:mode=" + mode);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (first) {
                //主讲模式去掉外层的RelativeLayout换回FrameLayout
                ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
                if (group != flFirstBackgroundContent) {
                    while (group.getChildCount() > 0) {
                        View childView = group.getChildAt(0);
                        group.removeViewAt(0);
                        flFirstBackgroundContent.addView(childView);
                    }
                }
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
//           主讲模式铺满屏幕，等比例缩放
            int screenHeight = ScreenUtils.getScreenHeight();
            float density = ScreenUtils.getScreenDensity();
            int bitmapW = (int) (density * 1280);
            int bitmapH = (int) (density * 720);
            float screenRatio = (float) screenWidth / (float) screenHeight;
            int newWidth = screenWidth;
            int newHeight = screenHeight;
            if (screenRatio > (float) 16 / (float) 9) {
                newHeight = (int) ((float) screenWidth * (float) bitmapH / (float) bitmapW);
            } else if (screenRatio < (float) 16 / (float) 9) {
                newWidth = (int) ((float) screenHeight * (float) bitmapW / (float) bitmapH);
            }
            if (params.width != newWidth || params.height != newHeight) {
                params.width = newWidth;
                params.height = newHeight;
                params.rightMargin = 0;
                LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
                LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
            }
        } else {
            if (first) {
                //辅导模式去掉外层的FrameLayout
                ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
                if (group != rlFirstBackgroundContent) {
                    while (group.getChildCount() > 0) {
                        View childView = group.getChildAt(0);
                        group.removeViewAt(0);
                        rlFirstBackgroundContent.addView(childView);
                    }
                }
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
            // 辅导模式保留三分屏
            if (params.width != ScreenUtils.getScreenWidth() || params.height != ScreenUtils.getScreenHeight()) {
                params.width = ScreenUtils.getScreenWidth();
                params.height = ScreenUtils.getScreenHeight();
                LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
                LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
                Loger.d(TAG, "setFirstParam:width=" + params.width);
            }

            int rightMargin = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / LiveVideoConfig.VIDEO_WIDTH + (screenWidth - lp.width) / 2);
            int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
            if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
                params.rightMargin = rightMargin;
                params.bottomMargin = params.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
                LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
                Loger.d(TAG, "setFirstParam:rightMargin=" + rightMargin);
            }
        }
        //Loger.e(TAG, "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
    }

    @Override
    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = standMediaControllerBottom = new LiveStandMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        standMediaControllerBottom.onModeChange(getInfo.getMode(), getInfo);
    }

    @Override
    public void onModeChange(final String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                standMediaControllerBottom.onModeChange(mode, mGetInfo);
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    //主讲模式去掉外层的RelativeLayout换回FrameLayout
                    ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
                    if (group != flFirstBackgroundContent) {
                        while (group.getChildCount() > 0) {
                            View childView = group.getChildAt(0);
                            group.removeViewAt(0);
                            flFirstBackgroundContent.addView(childView);
                        }
                    }
                } else {
                    //辅导模式去掉外层的FrameLayout
                    ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
                    if (group != rlFirstBackgroundContent) {
                        while (group.getChildCount() > 0) {
                            View childView = group.getChildAt(0);
                            group.removeViewAt(0);
                            rlFirstBackgroundContent.addView(childView);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onVideoCreateEnd() {
        startGetInfo = false;
        super.onVideoCreateEnd();
        liveStandFrameAnim = new LiveStandFrameAnim(activity);
        liveStandFrameAnim.check(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                startGetInfo = true;
                View vsLiveStandUpdate = activity.findViewById(R.id.vs_live_stand_update);
                if (vsLiveStandUpdate != null) {
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                } else {
                    vsLiveStandUpdate = activity.findViewById(R.id.rl_live_stand_update);
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                }
                if (activity.isFinishing()) {
                    return;
                }
                startGetInfo();
            }
        });
    }

    @Override
    protected void startGetInfo() {
        if (startGetInfo) {
            super.startGetInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestory();
        }
    }
}
