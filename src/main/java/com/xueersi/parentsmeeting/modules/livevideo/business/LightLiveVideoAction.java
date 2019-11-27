package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoLoadingImgView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business
 * @ClassName: LightLiveVideoAction
 * @Description: 轻直播加载页
 * @Author: WangDe
 * @CreateDate: 2019/11/22 15:29
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/22 15:29
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveVideoAction extends LiveVideoAction {

    protected String mode = LiveTopic.MODE_TRANING;
    private static final String TAG = "HalfBodyLiveVideoAction";

//    private RelativeLayout rlFirstBackgroundContent;
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
    private VideoLoadingImgView ivVodeoLoading;
    protected AtomicBoolean mIsLand = new AtomicBoolean(false);
    private final LinearLayout llLoding;

    public LightLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView, String mode) {
        super(activity, mLiveBll, mContentView);
        this.mode = mode;
        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
//        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        ivVodeoLoading = mContentView.findViewById(R.id.vl_lightlive_video_loading);
        llLoding = mContentView.findViewById(R.id.ll_lightlive_video_loading_container);
    }

    public void setmIsLand(AtomicBoolean mIsLand) {
        this.mIsLand = mIsLand;
    }

    @Override
    public void setFirstParam(LiveVideoPoint liveVideoPoint) {
        logger.e("==========>setFirstParam called");
//        switchUI(liveVideoPoint);
        if (mIsLand.get()) {
            showMainTeacherUI();
        } else {
            setFirstParamPort();
        }
        mInited = true;
    }

    /**
     * 初始化 loading 资源
     */
//    private void initLoadingView() {
//        if (ivVodeoLoading != null) {
//            if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
//                //语文loading 居中显示
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivVodeoLoading.getLayoutParams();
//                params.topMargin = 0;
//                params.addRule(RelativeLayout.CENTER_IN_PARENT);
//                LayoutParamsUtil.setViewLayoutParams(ivVodeoLoading, params);
//                ivVodeoLoading.setImageResource(R.drawable.anim_live_video_loading_arts);
//            } else {
//                ivVodeoLoading.setImageResource(R.drawable.anim_live_video_loading);
//            }
//        }
//    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
//        initLoadingView();
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
        //ivTecherState.setVisibility(View.INVISIBLE);
        if (!mInited) {
            //主讲模式去掉外层的RelativeLayout换回FrameLayout
            ViewGroup group = (ViewGroup) rlFirstBackgroundView.getParent();
            if (group != flFirstBackgroundContent) {
                while (group.getChildCount() > 0) {
                    View childView = group.getChildAt(0);
                    group.removeViewAt(0);
                    flFirstBackgroundContent.addView(childView);
                }
            }
            showVedioLoading(View.VISIBLE);
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

        Drawable dwTeacherNotPresent = ResourcesCompat.getDrawable(activity.getResources(), getLoadingBg(), null);
        rlFirstBackgroundView.setBackground(dwTeacherNotPresent);
        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            tvLoadingHint.setTextColor(Color.WHITE);
        } else {
            tvLoadingHint.setTextColor(Color.parseColor("#3B9699"));
        }

        tvLoadingHint.setVisibility(View.INVISIBLE);
        ll_course_video_loading.setVisibility(View.VISIBLE);
        iv_course_video_loading_bg.setVisibility(View.INVISIBLE);

        // logger.e( "=======>showMainTeacherUI:");
    }

    @Override
    public void setFirstBackgroundVisible(int visible) {
        // Log.e("loadingView","========>setFirstBackgroundVisible:"+visible);
        if (rlFirstBackgroundView == null) {
            return;
        }
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.VISIBLE) {
            setTeacherNotpresent(rlFirstBackgroundView);
            ivTeacherNotpresent.setVisibility(View.VISIBLE);
            setTeacherNotpresent(ivTeacherNotpresent);
        }
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
            //showVedioLoading(visible);
            if (ivVodeoLoading != null) {
                llLoding.setVisibility(View.GONE);
                ivVodeoLoading.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public void onTeacherNotPresent(boolean isBefore) {
        // Log.e("loadingView", "=========>onTeacherNotPresent called:" + isBefore);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int visibility = rlFirstBackgroundView.getVisibility();
                mLogtf.d("onTeacherNotPresent:First=" + visibility);
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    if (ivVodeoLoading != null) {
                        llLoding.setVisibility(View.GONE);
                        ivVodeoLoading.setVisibility(View.INVISIBLE);
                    }
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
        this.mode = mode;
        super.onModeChange(mode, isPresent);
        logger.e("====>onModeChange:" + this.mode);
    }

    /**
     * 设置老师不在直播间的相关UI
     *
     * @param rlFirstBackgroundView
     */
    private void setTeacherNotpresent(View view) {
        long now = System.currentTimeMillis() / 1000;
        // loading 视图
        if (view == rlFirstBackgroundView) {
            Drawable dwTeacherNotPresent = ResourcesCompat.getDrawable(activity.getResources(), getLoadingBg(),
                    null);
            view.setBackground(dwTeacherNotPresent);
        } else {
            if (mGetInfo == null) {
                view.setBackground(ResourcesCompat.getDrawable(activity.getResources(), getNoTeacherBg(), null));
            } else {
                if (!videoLoadingShowing()) {
                    if (now < mGetInfo.getsTime()) {
                        // 设置马上开始上课背景图
                        view.setBackground(activity.getResources().getDrawable(R.drawable.livevideo_lightlive_not_start_bg));
                    } else {
                        // 设置老师不在直播间背景图
                        view.setBackground(activity.getResources().getDrawable(getNoTeacherBg()));
                    }
                }
            }
        }

    }


    private boolean videoLoadingShowing() {
        return ivVodeoLoading != null && ivVodeoLoading.getVisibility() == View.VISIBLE;
    }


    /**
     * 获取视频加载中 背景图片
     *
     * @return
     */
    private int getLoadingBg() {
        return R.drawable.livevideo_lightlive_not_start_bg;
    }

    /**
     * 获取老师不在直播间 背景图片
     *
     * @return
     */
    private int getNoTeacherBg() {
        return R.drawable.livevideo_lightlive_no_teacher;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        super.onLiveStart(server, cacheData, modechange);
        showVedioLoading(View.VISIBLE);
    }

    private View bufferView;

    private void showVedioLoading(final int visible) {

        if (LiveTopic.MODE_CLASS.equals(mode) && ivVodeoLoading != null && visible != ivVodeoLoading.getVisibility()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    llLoding.setVisibility(visible);
                    ivVodeoLoading.setVisibility(visible);
                    if (View.VISIBLE == visible) {
                        ivTeacherNotpresent.setBackground(ResourcesCompat.getDrawable(activity.getResources(),
                                getLoadingBg(), null));
                    } else {
                        setTeacherNotpresent(ivTeacherNotpresent);
                    }
                }
            });
        }
    }

    @Override
    public void onLiveDontAllow(String msg) {
        super.onLiveDontAllow(msg);
        showVedioLoading(View.INVISIBLE);
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        showVedioLoading(View.INVISIBLE);

    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        super.onPlayError(errorCode, playErrorCode);
        showVedioLoading(View.INVISIBLE);

    }

    @Override
    public void playComplete() {
        super.playComplete();
        showVedioLoading(View.INVISIBLE);

    }

    @Override
    public void onFail(int arg1, int arg2) {
        super.onFail(arg1, arg2);
        showVedioLoading(View.INVISIBLE);

    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        super.onLiveError(responseEntity);
        showVedioLoading(View.INVISIBLE);

    }


    @Override
    public void rePlay(boolean modechange) {
        super.rePlay(modechange);
    }

    @Override
    public void onClassTimoOut() {
        showVedioLoading(View.INVISIBLE);
        super.onClassTimoOut();
    }

    public void setFirstParamPort() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rlFirstBackgroundView.getLayoutParams();
        if (params.rightMargin != 0 || params.bottomMargin != 0 || params.topMargin != 0 || params.width != ViewGroup.LayoutParams.MATCH_PARENT || params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            params.rightMargin = 0;
            params.bottomMargin = 0;
            params.topMargin = 0;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rlFirstBackgroundView.setLayoutParams(params);
            ivTeacherNotpresent.setLayoutParams(params);
            if (dwTeacherNotpresen == null) {
                dwTeacherNotpresen = activity.getResources().getDrawable(getNoTeacherBg());
            }
            ivTeacherNotpresent.setBackgroundDrawable(dwTeacherNotpresen);
        }
    }
}
