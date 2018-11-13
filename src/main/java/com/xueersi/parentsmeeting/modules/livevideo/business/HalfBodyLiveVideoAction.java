package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;

/**
 * 半身直播 UI 管理器
 *
 * @author chenkun
 * @version 1.0, 2018/10/22 下午5:39
 */

public class HalfBodyLiveVideoAction extends LiveVideoAction {

    protected String mode = LiveTopic.MODE_TRANING;
    private static final String TAG = "HalfBodyLiveVideoAction";

    private RelativeLayout rlFirstBackgroundContent;
    private FrameLayout flFirstBackgroundContent;

    /**
     * 视频正在加载中根布局
     */
    private LinearLayout ll_course_video_loading;

    /**
     * 视频加载中 图片
     */
    private ImageView iv_course_video_loading_bg;

    /**
     * 是否已完成首次初始化
     */
    private boolean mInited;

    private String strVideoLoading = "正在获取视频资源，请稍后";
    /**
     * 全屏模式下 视频加载中UI
     */
    private final RelativeLayout rlMainTeacherLoading;


    public HalfBodyLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView, String mode) {
        super(activity, mLiveBll, mContentView);
        this.mode = mode;

        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        rlMainTeacherLoading = mContentView.findViewById(R.id.rl_live_halfbody_video_loading);

    }

    @Override
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        logger.e("==========>setFirstParam called");
        switchUI(liveVideoPoint);
        mInited = true;
    }

    /**
     * 切换不同UI
     *
     * @param liveVideoPoint: 视频锚点信息，用于计算UI 布局 信息
     */
    private void switchUI(LiveVideoPoint liveVideoPoint) {
        logger.e("setFirstParam:mode=" + mode);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            showMainTeacherUI();
        } else {
            showSupportTeacherUI(liveVideoPoint);
        }
    }

    /**
     * 展示辅导老师UI 页面
     *
     * @param liveVideoPoint
     */
    private void showSupportTeacherUI(LiveVideoPoint liveVideoPoint) {
        rlMainTeacherLoading.setVisibility(View.INVISIBLE);
        if (!mInited) {
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
            logger.d("setFirstParam:width=" + params.width);
        }

        int rightMargin = liveVideoPoint.getRightMargin();
        int topMargin = liveVideoPoint.y2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
            logger.d("setFirstParam:rightMargin=" + rightMargin);
        }

        // 视频加载中UI
        ll_course_video_loading.setVisibility(View.VISIBLE);
        iv_course_video_loading_bg.setVisibility(View.VISIBLE);
        tvLoadingHint.setTextColor(Color.WHITE);
        rlFirstBackgroundView.setBackgroundColor(0xff000000);
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
        Log.e("HalfBodyLiveAction","=======>showSupportTeacherUI:");

    }


    /**
     * 展示 主讲老师UI页面
     *
     * @param screenWidth
     */
    private void showMainTeacherUI() {
        View contentView = activity.findViewById(android.R.id.content);
        View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);

        logger.e("=====>showMainTeacherUI called:" + mInited);
        if (!mInited) {
            //主讲模式去掉外层的RelativeLayout换回FrameLayout
            ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
            if (group != flFirstBackgroundContent) {
                logger.e("=====>showMainTeacherUI called: 222222");
                while (group.getChildCount() > 0) {
                    logger.e("=====>showMainTeacherUI called: 333333");
                    View childView = group.getChildAt(0);
                    group.removeViewAt(0);
                    flFirstBackgroundContent.addView(childView);
                }
            }
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
        //主讲模式铺满屏幕，等比例缩放
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

        Drawable dwTeacherNotPresent = activity.getResources().getDrawable(R.drawable.live_halfbody_bg);
        rlFirstBackgroundView.setBackground(dwTeacherNotPresent);
        tvLoadingHint.setTextColor(Color.parseColor("#3B9699"));
        ll_course_video_loading.setVisibility(View.VISIBLE);
        iv_course_video_loading_bg.setVisibility(View.INVISIBLE);
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
        Log.e("HalfBodyLiveAction","=======>showMainTeacherUI:");
    }

    @Override
    public void setFirstBackgroundVisible(int visible) {
        showMainTeachLoading(visible);
        if (rlFirstBackgroundView == null) {
            return;
        }
        rlMainTeacherLoading.setVisibility(visible);
        if (visible == View.VISIBLE) {
            setTeacherNotpresent(rlFirstBackgroundView);
        }
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
            rlFirstBackgroundView.setVisibility(View.GONE);
        } else {
            if (ivTeacherNotpresent.getVisibility() == View.VISIBLE) {
                setTeacherNotpresent(ivTeacherNotpresent);
            }
        }
    }


    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int visibility = rlFirstBackgroundView.getVisibility();
                mLogtf.d("onTeacherNotPresent:First=" + visibility);
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    setTeacherNotpresent(ivTeacherNotpresent);
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View
                            .INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        super.onModeChange(mode, isPresent);
        this.mode = mode;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
                    iv_course_video_loading_bg.setVisibility(View.GONE);
                    ll_course_video_loading.setVisibility(View.GONE);
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
                    iv_course_video_loading_bg.setVisibility(View.VISIBLE);
                    ll_course_video_loading.setVisibility(View.VISIBLE);
                    rlFirstBackgroundView.setBackgroundColor(0xff000000);
                    Log.e("HalfBodyLiveAction","=======>onModeChange:");
                }
                setFirstParam(LiveVideoPoint.getInstance());
            }
        });

    }

    /**
     * 设置老师不在直播间的相关UI
     *
     * @param rlFirstBackgroundView
     */
    private void setTeacherNotpresent(View view) {

        if (LiveTopic.MODE_CLASS.equals(mode)) {
            Drawable dwTeacherNotPresent = activity.getResources().getDrawable(R.drawable.live_halfbody_bg);
            view.setBackground(dwTeacherNotPresent);
        } else {
            logger.d("setTeacherNotpresent:mode=training");
            if (view == rlFirstBackgroundView) {
                rlFirstBackgroundView.setBackgroundColor(0xff000000);
                return;
            }
            Drawable dwTeacherNotpresen = null;
            if (LiveVideoConfig.isPrimary) {
                dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_psnormal);
            } else {
                dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_normal);
            }
            view.setBackgroundDrawable(dwTeacherNotpresen);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        super.onLiveStart(server, cacheData, modechange);
        showMainTeachLoading(View.VISIBLE);
    }

    private void showMainTeachLoading(final int visible) {
        if(LiveTopic.MODE_CLASS.equals(mode)){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(View.VISIBLE == visible){
                        View bufferView  = activity.getWindow().getDecorView().findViewById(R.id.rl_course_video_loading);
                        if(bufferView != null && bufferView.getVisibility() == View.VISIBLE){
                            return;
                        }
                        if(rlMainTeacherLoading.getParent() != null){
                            rlMainTeacherLoading.setVisibility(visible);
                        }else{
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            rlFirstBackgroundContent.addView(rlMainTeacherLoading,params);
                        }
                    }else{
                        if(rlMainTeacherLoading != null && rlMainTeacherLoading.getParent() != null){
                            ((ViewGroup)rlMainTeacherLoading.getParent()).removeView(rlMainTeacherLoading);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onLiveDontAllow(String msg) {
        super.onLiveDontAllow(msg);
        showMainTeachLoading(View.INVISIBLE);
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        showMainTeachLoading(View.INVISIBLE);
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        super.onPlayError(errorCode, playErrorCode);
        showMainTeachLoading(View.INVISIBLE);
    }

    @Override
    public void playComplete() {
        super.playComplete();
        showMainTeachLoading(View.INVISIBLE);
    }

    @Override
    public void onFail(int arg1, int arg2) {
        super.onFail(arg1, arg2);
        showMainTeachLoading(View.INVISIBLE);
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        super.onLiveError(responseEntity);
        showMainTeachLoading(View.INVISIBLE);
    }


    @Override
    public void rePlay(boolean modechange) {
        Log.e("HalfBodyLiveAction","=======>rePlay:");
        super.rePlay(modechange);
    }

    @Override
    public void onClassTimoOut() {
        rlMainTeacherLoading.setVisibility(View.INVISIBLE);
        super.onClassTimoOut();
    }
}
