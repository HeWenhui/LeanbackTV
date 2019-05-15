package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.PrimaryClassView;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.PrimaryClassViewSec;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoLoadingImgView;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ImageScale;

/**
 * 半身直播 UI 管理器
 *
 * @author chenkun
 * @version 1.0, 2018/10/22 下午5:39
 */

public class PrimaryClassLiveVideoAction extends LiveVideoAction {

    protected String mode;
    private static final String TAG = "PrimaryClassLiveVideoAction";
    //新加
    ImageView ivLivePrimaryClassKuangjiaImgNormal;
    RelativeLayout rlContent;
    private RelativeLayout rl_course_video_contentview;
    int isArts;
    PrimaryClassView primaryClassView;
    //
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
    private VideoLoadingImgView ivVodeoLoading;
    private final ImageView ivTecherState;


    public PrimaryClassLiveVideoAction(Activity activity, LiveBll2 mLiveBll, RelativeLayout mContentView, RelativeLayout rlContent, int isArts, String mode) {
        super(activity, mLiveBll, mContentView);
        this.mode = mode;
        this.isArts = isArts;
        this.rlContent = rlContent;
        flFirstBackgroundContent = mContentView.findViewById(R.id.fl_course_video_first_content);
        rlFirstBackgroundContent = mContentView.findViewById(R.id.rl_course_video_first_content);
        ll_course_video_loading = mContentView.findViewById(R.id.ll_course_video_loading);
        iv_course_video_loading_bg = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        ivVodeoLoading = mContentView.findViewById(R.id.rl_live_halfbody_video_loading);
        ivTecherState = mContentView.findViewById(R.id.iv_live_halfbody_teacher_state);
        rl_course_video_contentview = mContentView.findViewById(R.id.rl_course_video_contentview);
        ivLivePrimaryClassKuangjiaImgNormal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        primaryClassView = new PrimaryClassViewSec();
        setKuangjia();
    }

    private void setKuangjia() {
        ivLivePrimaryClassKuangjiaImgNormal.setImageResource(primaryClassView.getKuangjia());
        setMargin();
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
        showVedioLoading(View.INVISIBLE);
        ivTecherState.setVisibility(View.INVISIBLE);
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
        tvLoadingHint.setVisibility(View.VISIBLE);
        tvLoadingHint.setTextColor(Color.WHITE);
        rlFirstBackgroundView.setBackgroundColor(0xff000000);
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
        //logger.e("=======>showSupportTeacherUI:");
    }

    /**
     * 初始化 loading 资源
     */
    private void initLoadingView() {
        if (ivVodeoLoading != null) {
            if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
                //语文loading 居中显示
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivVodeoLoading.getLayoutParams();
                params.topMargin = 0;
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                LayoutParamsUtil.setViewLayoutParams(ivVodeoLoading, params);
                ivVodeoLoading.setImageResource(R.drawable.anim_live_video_loading_arts);
            } else {
                ivVodeoLoading.setImageResource(R.drawable.anim_live_video_loading);
            }
        }
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        initLoadingView();
    }

    /**
     * 展示 主讲老师UI页面
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
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
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
            ivTecherState.setVisibility(View.INVISIBLE);
            //showVedioLoading(visible);
            if (ivVodeoLoading != null) {
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setMargin();
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
                    logger.e("=======>onModeChange:");
                }
                setFirstParam(LiveVideoPoint.getInstance());
            }
        });

    }

    /**
     * 设置老师不在直播间的相关UI
     */
    private void setTeacherNotpresent(View view) {

        if (LiveTopic.MODE_CLASS.equals(mode)) {
            long now = System.currentTimeMillis() / 1000;
            // loading 视图
            if (view == rlFirstBackgroundView) {
                Drawable dwTeacherNotPresent = ResourcesCompat.getDrawable(activity.getResources(), getLoadingBg(),
                        null);
                view.setBackground(dwTeacherNotPresent);
            } else {
                if (mGetInfo == null) {
                    // 设置 老师不在直播间 背景图
                    ivTecherState.setVisibility(View.VISIBLE);
                    ivTecherState.setImageResource(getTeachNotpresentStateImg());
                    view.setBackground(ResourcesCompat.getDrawable(activity.getResources(), getNoTeacherBg(), null));
                } else {
                    if (!videoLoadingShowing()) {
                        if (now < mGetInfo.getsTime()) {
                            // 设置马上开始上课背景图
                            ivTecherState.setVisibility(View.VISIBLE);
                            ivTecherState.setImageResource(getClassBeforStateImg());
                            view.setBackground(activity.getResources().getDrawable(getClassBeforeBg()));
                        } else {
                            // 设置老师不在直播间背景图
                            ivTecherState.setVisibility(View.VISIBLE);
                            ivTecherState.setImageResource(getTeachNotpresentStateImg());
                            view.setBackground(activity.getResources().getDrawable(getNoTeacherBg()));
                        }
                    }
                }
            }
        } else {
            logger.d("setTeacherNotpresent:mode=training");
            if (view == rlFirstBackgroundView) {
                rlFirstBackgroundView.setBackgroundColor(0xff000000);
                return;
            }
            Drawable dwTeacherNotpresen = null;
            if (LiveVideoConfig.isSmallChinese) {

                dwTeacherNotpresen = activity.getResources().getDrawable(R.drawable.
                        livevideo_small_chinese_zw_dengdaida_bg_psnormal);

            } else if (LiveVideoConfig.isPrimary) {
                dwTeacherNotpresen = ResourcesCompat.getDrawable(activity.getResources(), R.drawable
                        .livevideo_zw_dengdaida_bg_psnormal, null);
            } else {
                dwTeacherNotpresen = ResourcesCompat.getDrawable(activity.getResources(), R.drawable
                        .livevideo_zw_dengdaida_bg_normal, null);
            }
            view.setBackground(dwTeacherNotpresen);
        }
    }

    private void setMargin() {
        logger.d("setMargin:mode=" + mode);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            final Bitmap bitmap = ((BitmapDrawable) ivLivePrimaryClassKuangjiaImgNormal.getDrawable()).getBitmap();
            float scale = (float) bitmap.getWidth() / 1328f;
            lp.leftMargin = (int) (13 * scale);
            lp.bottomMargin = (int) (13 * scale);
            lp.rightMargin = (int) (219 * scale);
            lp.topMargin = (int) (96 * scale);
            lp.addRule(RelativeLayout.ALIGN_LEFT, ivLivePrimaryClassKuangjiaImgNormal.getId());
            lp.addRule(RelativeLayout.ALIGN_TOP, ivLivePrimaryClassKuangjiaImgNormal.getId());
            lp.addRule(RelativeLayout.ALIGN_RIGHT, ivLivePrimaryClassKuangjiaImgNormal.getId());
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, ivLivePrimaryClassKuangjiaImgNormal.getId());
            ivLivePrimaryClassKuangjiaImgNormal.setVisibility(View.VISIBLE);
            ImageScale.setImageViewWidth(ivLivePrimaryClassKuangjiaImgNormal);
            rl_course_video_contentview.setBackgroundResource(primaryClassView.getBackImg());
        } else {
            lp.leftMargin = 0;
            lp.bottomMargin = 0;
            lp.rightMargin = 0;
            lp.topMargin = 0;
            lp.addRule(RelativeLayout.ALIGN_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_TOP, 0);
            lp.addRule(RelativeLayout.ALIGN_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
            ivLivePrimaryClassKuangjiaImgNormal.setVisibility(View.GONE);
            rl_course_video_contentview.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }
        rlContent.setLayoutParams(lp);
    }

    private int getClassBeforStateImg() {

        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.drawable.live_halfbody_class_before_state_arts;

        } else {
            return R.drawable.live_halfbody_class_before_state;

        }

    }

    private int getTeachNotpresentStateImg() {

        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.drawable.live_halfbody_teacher_notpresent_state_arts;
        } else {
            return R.drawable.live_halfbody_teacher_notpresent_state;

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
        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.drawable.live_halfbody_bg_arts;
        } else {
            return R.drawable.bszb_kejian_bg_img_normal;
        }
    }


    /**
     * 获取老师不在直播间 背景图片
     *
     * @return
     */
    private int getNoTeacherBg() {
        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.drawable.live_halfbody_bg_arts;
        } else {
            return R.drawable.live_halfbody_bg;
        }
    }

    /**
     * 获取开始上课前  背景
     *
     * @return
     */
    private int getClassBeforeBg() {

        if (mGetInfo != null && mGetInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.drawable.live_halfbody_bg_arts;
        } else {
            return R.drawable.live_halfbody_bg;
        }
    }


    @Override
    public void onDestory() {
        super.onDestory();
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
                    if (bufferView == null) {
                        bufferView = mContentView.findViewById(R.id.probar_course_video_loading_tip_progress);
                    }
                    //避免和buffer 的loading动画 冲突
                    if (bufferView != null && bufferView.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    ivVodeoLoading.setVisibility(visible);
                    if (View.VISIBLE == visible) {
                        ivTecherState.setVisibility(View.INVISIBLE);
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
}
