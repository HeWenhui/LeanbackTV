package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;

/**
 * Created by linyuqiang on 2018/7/18.
 * 站立直播的加载页
 */
public class StandLiveVideoAction extends LiveVideoAction {
    private static final String TAG = "StandLiveVideoAction";
    private RelativeLayout rlFirstBackgroundContent;
    private FrameLayout flFirstBackgroundContent;
    private LinearLayout ll_course_video_loading;
    private ImageView iv_course_video_loading_bg;
    boolean isSetFirstParam = true;
    protected String mode = LiveTopic.MODE_TRANING;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenBefore;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenAfter;
    /** 老师不在直播间背景图 */
    protected Drawable dwTeacherNotpresenDoing;

    public StandLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView, String mode) {
        super(activity, mLiveBll, mContentView);
        this.mode = mode;
        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
    }

    @Override
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        setFirstParam(liveVideoPoint, isSetFirstParam);
        isSetFirstParam = false;
    }

    /**
     * 设置蓝屏界面
     */
    private void setFirstParam(LiveVideoPoint liveVideoPoint, boolean first) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        logger.d( "setFirstParam:mode=" + mode);
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
            ll_course_video_loading.setVisibility(View.GONE);
            iv_course_video_loading_bg.setVisibility(View.GONE);
            if (dwTeacherNotpresenBefore == null) {
                dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
            }
            rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
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
                logger.d( "setFirstParam:width=" + params.width);
            }

            int rightMargin = liveVideoPoint.getRightMargin();
            int topMargin = liveVideoPoint.y2;
            if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
                params.rightMargin = rightMargin;
                params.bottomMargin = params.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
                LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
                logger.d( "setFirstParam:rightMargin=" + rightMargin);
            }
            ll_course_video_loading.setVisibility(View.VISIBLE);
            iv_course_video_loading_bg.setVisibility(View.VISIBLE);
            rlFirstBackgroundView.setBackgroundColor(0xff000000);
        }
        //logger.e( "setFirstParam:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        StandLiveVideoAction.this.mode = mode;
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
//                    if (dwTeacherNotpresenBefore == null) {
//                        dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
//                    }
//                    rlFirstBackgroundView.setBackgroundDrawable(dwTeacherNotpresenBefore);
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
                }
                setFirstParam(LiveVideoPoint.getInstance());
                StandLiveVideoAction.super.onModeChange(mode, isPresent);
            }
        });
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
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void setFirstBackgroundVisible(int visible) {
        if (rlFirstBackgroundView == null) {
            return;
        }
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.VISIBLE) {
            setTeacherNotpresent(rlFirstBackgroundView);
        }
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        } else {
            if (ivTeacherNotpresent.getVisibility() == View.VISIBLE) {
                setTeacherNotpresent(ivTeacherNotpresent);
            }
        }
    }

    /**
     * 设置老师不在直播间背景
     *
     * @param view
     */
    private void setTeacherNotpresent(View view) {
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            long now = System.currentTimeMillis() / 1000;
            if (mGetInfo == null) {
                if (dwTeacherNotpresenDoing == null) {
                    dwTeacherNotpresenDoing = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before_doing);
                }
                view.setBackgroundDrawable(dwTeacherNotpresenDoing);
            } else {
                if (now < mGetInfo.getsTime()) {
                    if (dwTeacherNotpresenBefore == null) {
                        dwTeacherNotpresenBefore = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before);
                    }
                    view.setBackgroundDrawable(dwTeacherNotpresenBefore);
                    logger.d( "setTeacherNotpresent:before");
                } else if (now > mGetInfo.geteTime()) {
                    if (dwTeacherNotpresenAfter == null) {
                        dwTeacherNotpresenAfter = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_after);
                    }
                    view.setBackgroundDrawable(dwTeacherNotpresenAfter);
                    logger.d( "setTeacherNotpresent:after");
                } else {
                    if (dwTeacherNotpresenDoing == null) {
                        dwTeacherNotpresenDoing = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_before_doing);
                    }
                    view.setBackgroundDrawable(dwTeacherNotpresenDoing);
                    logger.d( "setTeacherNotpresent:doing");
                }
            }
        } else {
            logger.d( "setTeacherNotpresent:mode=training");
            if (view == rlFirstBackgroundView) {
                rlFirstBackgroundView.setBackgroundColor(0xff000000);
                return;
            }
            if (dwTeacherNotpresen == null) {
                dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.livevideo_zw_dengdaida_bg_normal);
            }
            view.setBackgroundDrawable(dwTeacherNotpresen);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        dwTeacherNotpresenBefore = null;
        dwTeacherNotpresenAfter = null;
        dwTeacherNotpresenDoing = null;
    }
}
