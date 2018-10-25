package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

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
    private LinearLayout ll_course_video_loading;
    private ImageView iv_course_video_loading_bg;
    /**
     * 是否已完成首次初始化
     */
    private boolean mInited;


    public HalfBodyLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView, String mode) {
        super(activity, mLiveBll, mContentView);
        this.mode = mode;

        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
    }

    @Override
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        Log.e(TAG, "==========>setFirstParam called");
        switchUI(liveVideoPoint);
        mInited = true;
    }

    /**
     * 切换不同UI
     *
     * @param liveVideoPoint: 视频锚点信息，用于计算UI 布局 信息
     */
    private void switchUI(LiveVideoPoint liveVideoPoint) {
        Log.e(TAG, "setFirstParam:mode=" + mode);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            showMainTeacherUI();
        } else {
            showSupportTeacherUI(liveVideoPoint);
        }
    }

    /**
     * 展示辅导老师UI 页面
     * @param liveVideoPoint
     */
    private void showSupportTeacherUI(LiveVideoPoint liveVideoPoint) {
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
        ll_course_video_loading.setVisibility(View.VISIBLE);
        iv_course_video_loading_bg.setVisibility(View.VISIBLE);
        rlFirstBackgroundView.setBackgroundColor(0xff000000);
    }


    /**
     * 展示 主讲老师UI页面
     * @param screenWidth
     */
    private void showMainTeacherUI() {
        View contentView = activity.findViewById(android.R.id.content);
        View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);

        Log.e(TAG,"=====>showMainTeacherUI called:"+mInited);
        if (!mInited) {
            //主讲模式去掉外层的RelativeLayout换回FrameLayout
            ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
            if (group != flFirstBackgroundContent) {
                Log.e(TAG,"=====>showMainTeacherUI called: 222222");
                while (group.getChildCount() > 0) {
                    Log.e(TAG,"=====>showMainTeacherUI called: 333333");
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
        ll_course_video_loading.setVisibility(View.GONE);
        iv_course_video_loading_bg.setVisibility(View.GONE);
    }


    @Override
    public void setFirstBackgroundVisible(int visible) {
        Log.e(TAG,"=========>setFirstBackgroundVisible called:");


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


    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        //super.onTeacherNotPresent(isBefore);

        Log.e(TAG,"=======>onTeacherNotPresent called");

    }


    @Override
    public void onModeChange(String mode, boolean isPresent) {
       //super.onModeChange(mode, isPresent);
        this.mode = mode;
        Log.e(TAG,"=======>onModeChange called");
    }

    /**
     * 设置老师不在直播间的相关UI
     * @param rlFirstBackgroundView
     */
    private void setTeacherNotpresent(View view) {



    }

    @Override
    public void onDestory() {
        super.onDestory();
        // TODO: 2018/10/22  释放相关UI 资源
    }
}
