package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.BusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.miracast.MiracastBll;
import com.xueersi.parentsmeeting.modules.livevideo.miracast.MiracastLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.utils.BuryUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.share.business.advert.AdvertSourceUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linyuqiang on 2018/7/18.
 * 讲座布局
 */
public class LectureLiveVideoFragment extends LiveFragmentBase implements ActivityChangeLand, MiracastLiveMediaControllerTop.TvBtnClicklistener, MiracastBll.MiracastLivebackBllCallback {
    private static final int REQUEST_MUST_PERMISSION = 1;
    private String TAG = "LectureLiveVideoFrameLog";
    MiracastLiveMediaControllerTop miracastLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;
    RelativeLayout bottomContent;
    protected LiveViewAction liveViewAction;
    private PopupWindow mPopupWindows;
    LecLiveVideoAction lecLiveVideoAction;

    private MiracastBll miracastBll;
    /**
     * onPause状态不暂停视频
     */
    PauseNotStopVideoIml pauseNotStopVideoIml;
    boolean firstInitView = false;

    //横屏 投屏的逻辑
    public boolean shouldShowLetouPage = false;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        boolean onVideoCreate = super.onVideoCreate(savedInstanceState);
        if (onVideoCreate) {
            long before = System.currentTimeMillis();
            createLiveVideoAction();
            liveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
            initAllBll();
            logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
            before = System.currentTimeMillis();
            addBusiness(activity);
            logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
        }
        return onVideoCreate;
    }

    private void initAllBll() {
        logger.d("====>initAllBll:" + bottomContent);
        ProxUtil.getProxUtil().put(activity, ActivityChangeLand.class, this);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(miracastLiveMediaControllerTop);

        setMediaControllerBottomParam();
        videoFragment.setIsAutoOrientation(false);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity);
        mLiveBll.addBusinessShareParam("videoView", videoView);
    }

    @Override
    protected void onBusinessCreate() {
        super.onBusinessCreate();
        changeLandAndPort();
    }

    @Override
    protected void startGetInfo() {
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(liveType + "-" + stuId + "-" + mVSectionID);
        mLiveBll.getInfo(mGetInfo);


    }

    @Override
    protected void onGlobalLayoutListener() {
        super.onGlobalLayoutListener();
        setMediaControllerBottomParam();
    }

    @Override
    protected void createLiveVideoAction() {
//        super.createLiveVideoAction();
        liveVideoAction = lecLiveVideoAction = new LecLiveVideoAction(activity, mLiveBll, mContentView);
        lecLiveVideoAction.setmIsLand(mIsLand);
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam() {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = liveVideoPoint.y2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            logger.d("setMediaControllerBottomParam:paddingBottom=" + paddingBottom);
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    public boolean initData() {
        Intent intent = activity.getIntent();
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        mVSectionID = intent.getStringExtra("vSectionID");
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(liveType + "-" + stuId + "-" + mVSectionID);
        mLiveBll = new LiveBll2(activity, mVSectionID, liveType, from, mGetInfo);
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    @Override
    public void rePlay(boolean modechange) {

    }

    /**
     * @param modeChange
     */
    @Override
    public void psRePlay(boolean modeChange) {
        if (mGetInfo == null || liveVideoAction == null) {//上次初始化尚未完成
            return;
        }
        liveVideoAction.rePlay(modeChange);
        mLiveVideoBll.psRePlay(modeChange);

    }

    @Override
    public void changeLine(int pos) {
        if (mLiveVideoBll != null) {
            mLiveVideoBll.changeLine(pos);
        }
    }

//    @Override
//    public void changeNextLine() {
//        if (mLiveVideoBll != null) {
//            mLiveVideoBll.changeNextLine();
//        }
//    }

    @Override
    public void changeNowLine() {
        if (mLiveVideoBll != null) {
            mLiveVideoBll.changeNowLine();
        }
    }

    protected void addBusiness(Activity activity) {
        ProxUtil.getProxUtil().put(activity, BaseLiveMediaControllerTop.class, miracastLiveMediaControllerTop);
        ProxUtil.getProxUtil().put(activity, BaseLiveMediaControllerBottom.class, liveMediaControllerBottom);
        ArrayList<BllConfigEntity> bllConfigEntities = AllBllConfig.getLiveBusinessLec();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            String className = "";
            try {
                BllConfigEntity bllConfigEntity = bllConfigEntities.get(i);
                className = bllConfigEntity.className;
                Class<?> c = Class.forName(className);
                Class<? extends LiveBaseBll> clazz;
                if (BusinessCreat.class.isAssignableFrom(c)) {
                    Class<? extends BusinessCreat> creatClazz = (Class<? extends BusinessCreat>) c;
                    BusinessCreat businessCreat = creatClazz.newInstance();
                    clazz = businessCreat.getClassName(activity.getIntent());
                    if (clazz == null) {
                        continue;
                    }
                } else if (LiveBaseBll.class.isAssignableFrom(c)) {
                    clazz = (Class<? extends LiveBaseBll>) c;
                } else {
                    continue;
                }
                Constructor<? extends LiveBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBll2.class});
                LiveBaseBll liveBaseBll = constructor.newInstance(activity, mLiveBll);
                if (liveBaseBll instanceof MiracastBll) {
                    miracastBll = (MiracastBll) liveBaseBll;
                    miracastBll.setMiracastLivebackBllCallback(this);
                }
                mLiveBll.addBusinessBll(liveBaseBll);
                logger.d("addBusiness:business=" + className);
            } catch (Exception e) {
                logger.d("addBusiness:business=" + className, e);
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    @Override
    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        liveViewAction = new LiveViewActionIml(activity, mContentView, bottomContent);
        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        miracastLiveMediaControllerTop = new MiracastLiveMediaControllerTop(activity, mMediaController, videoFragment);
        miracastLiveMediaControllerTop.setTvPlayClickListener(this);
        createMediaControllerBottom();
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, miracastLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom);
//        miracastLiveMediaControllerTop.setAutoOrientation(true);
    }

    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

    // TODO: 2019/2/27 为什么onResume
    @Override
    public void onResume() {
        super.onResume();
        if (mHaveStop) {
            mHaveStop = false;
            if (!pauseNotStopVideoIml.getPause()) {
                setFirstBackgroundVisible(View.VISIBLE);
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!MediaPlayer.getIsNewIJK()) {
                                        rePlay(false);
                                    } else {
                                        psRePlay(false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            pauseNotStopVideoIml.setPause(false);
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }

        XrsBury.pageStartBury(getResources().getString(R.string.pv_02_84), mGetInfo != null ? mGetInfo.getId() : "",
                vPlayer != null ? vPlayer.getCurrentPosition() : "",
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
        if (!pauseNotStopVideoIml.getPause()) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            if (openSuccess) {
                                mLiveVideoBll.stopPlayDuration();
                                logger.d("onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                            }
                            vPlayer.releaseSurface();
                            vPlayer.stop();
                        }
                        isPlay = false;
                    }
                }
            });
        }
        if (mLiveBll != null) {
            mLiveBll.onPause();
        }
        XrsBury.pageEndBury(getResources().getString(R.string.pv_02_84), mGetInfo != null ? mGetInfo.getId() : "",
                vPlayer != null ? vPlayer.getCurrentPosition() : "",
                AdvertSourceUtils.getInstance().getSourceid(), AdvertSourceUtils.getInstance().getAdvertid());
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        logger.d("onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void setAutoOrientation(boolean isAutoOrientation) {
        super.setAutoOrientation(isAutoOrientation);
        if (videoFragment != null) {
            videoFragment.setIsAutoOrientation(isAutoOrientation);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        logger.d("onConfigurationChanged:videoView=" + lp.width + "," + lp.height);
        if (mIsLand.get()) {
            videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                    (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
            LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
            logger.d("onConfigurationChanged:videoView2=" + lp.width + "," + lp.height);
        }
        changeLandAndPort();
        liveViewAction.removeView(liveMediaControllerBottom);
        if (mIsLand.get()) {
            liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom
                    , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

//        if (lecLiveVideoAction != null) {
//            lecLiveVideoAction.onConfigurationChanged();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 切换试题区位置
     */
    private void changeLandAndPort() {
        ViewGroup group = (ViewGroup) bottomContent.getParent();
        long before = System.currentTimeMillis();
        if (mIsLand.get()) {//横屏
            if (group != rlContent) {
                //设置控制
                ViewGroup controllerContent = (ViewGroup) mContentView.findViewById(R.id.rl_course_video_live_controller_content);
                controllerContent.removeAllViews();
                if (mMediaController != null) {
                    mMediaController.setControllerBottom(null, false);
                }
                mMediaController = new LiveMediaController(activity, videoFragment);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                controllerContent.addView(mMediaController, params);
                mMediaController.setControllerBottom(liveMediaControllerBottom, false);
                MiracastLiveMediaControllerTop miracastLiveMediaControllerTop = new MiracastLiveMediaControllerTop(activity, mMediaController, videoFragment);
                miracastLiveMediaControllerTop.setTvPlayClickListener(this);
                miracastLiveMediaControllerTop.hildAllView(true);
                mMediaController.setControllerTop(miracastLiveMediaControllerTop);
                controllerContent.addView(miracastLiveMediaControllerTop);
//                mMediaController.setAutoOrientation(true);
//                liveMediaControllerBottom.setController(mMediaController);
                if (mGetInfo != null) {
                    mMediaController.setFileName(mGetInfo.getName());
                }
                setMediaControllerBottomParam();
                // 换位置
                group.removeView(bottomContent);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                bottomContent.removeAllViews();
                rlContent.addView(bottomContent, lp);
                logger.d("changeLandAndPort:time1=" + (System.currentTimeMillis() - before));
                before = System.currentTimeMillis();
                List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
                for (LiveBaseBll businessBll : businessBlls) {
                    businessBll.initViewF(liveViewAction, bottomContent, mIsLand, mContentView);
                }
                firstInitView = true;
                logger.d("changeLandAndPort:time2=" + (System.currentTimeMillis() - before));
//                liveMessageBll.initView(questionContent, mIsLand);
//                //点名
//                rollCallBll.initView(questionContent);
//                //互动题和懂了吗
//                questionBll.initView(questionContent, mIsLand);
//                //红包
//                //redPackageBll.initView(questionContent);
//                //学习报告
//                learnReportBll.initView(questionContent);
//                h5CoursewareBll.initView(questionContent);
//                lecAdvertAction.initView(questionContent, mIsLand);
                mMediaController.show();
            }
            setFirstParamLand();
        } else {
            ViewGroup content = (ViewGroup) mContentView.findViewById(R.id.rl_course_video_contentview);
            if (group != content) {
                //设置控制
                ViewGroup controllerContent = (ViewGroup) mContentView.findViewById(R.id.rl_course_video_live_controller_content);
                controllerContent.removeAllViews();
                if (mMediaController != null) {
                    mMediaController.setControllerBottom(null, false);
                }
                mMediaController = new LiveMediaController(activity, videoFragment);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                controllerContent.addView(mMediaController, params);
                mMediaController.setControllerBottom(liveMediaControllerBottom, false);
                MiracastLiveMediaControllerTop miracastLiveMediaControllerTop = new MiracastLiveMediaControllerTop(activity, mMediaController, videoFragment);
                miracastLiveMediaControllerTop.hildAllView(false);
                miracastLiveMediaControllerTop.setTvPlayClickListener(this);
                mMediaController.setControllerTop(miracastLiveMediaControllerTop);
                controllerContent.addView(miracastLiveMediaControllerTop);
//                mMediaController.setAutoOrientation(true);
                liveMediaControllerBottom.setController(mMediaController);
                if (mGetInfo != null) {
                    mMediaController.setFileName(mGetInfo.getName());
                }
                setMediaControllerBottomParam();
                // 换位置
                group.removeView(bottomContent);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
                bottomContent.removeAllViews();
                content.addView(bottomContent, lp);
                logger.d("changeLandAndPort:time3=" + (System.currentTimeMillis() - before));
                before = System.currentTimeMillis();
                List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
                for (LiveBaseBll businessBll : businessBlls) {
                    businessBll.initViewF(liveViewAction, bottomContent, mIsLand, mContentView);
                }
                firstInitView = true;
                logger.d("changeLandAndPort:time4=" + (System.currentTimeMillis() - before));
//                liveMessageBll.initView(questionContent, mIsLand);
//                //点名
//                rollCallBll.initView(questionContent);
//                //互动题和懂了吗
//                questionBll.initView(questionContent, mIsLand);
//                //红包
//                //redPackageBll.initView(questionContent);
//                //学习报告
//                learnReportBll.initView(questionContent);
//                h5CoursewareBll.initView(questionContent);
//                lecAdvertAction.initView(questionContent, mIsLand);
                mMediaController.show();
            }
            setFirstParamPort();
            if (mPopupWindows != null && mPopupWindows.isShowing()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPopupWindows.dismiss();
                    }
                });
            }

            //
            if (shouldShowLetouPage) {
                if (miracastBll != null) {
                    shouldShowLetouPage = false;
                    miracastBll.showPager(getContentView());
                }
            }
        }
        //在后台被回收，再启动。会没有初始化view
        if (!firstInitView) {
            logger.d("changeLandAndPort:firstInitView=false");
            firstInitView = true;
            List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
            for (LiveBaseBll businessBll : businessBlls) {
                businessBll.initViewF(liveViewAction, bottomContent, mIsLand, mContentView);
            }
        }
    }

    public void onNewIntent(Intent intent) {
        ViewGroup parents = (ViewGroup) videoView.getParent();
//        if (parents != null) {
//            parents.removeView(videoView);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            mParent.addView(videoView, params);
//        }
    }

    @Override
    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
        if (liveVideoAction != null) {
            liveVideoAction.onPlayError(errorCode, playErrorCode);
        }
    }

    /**
     * 设置蓝屏界面
     */
    private void setFirstParamLand() {
//        final View contentView = findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
//        int rightMargin = (int) (com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
//        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
//            params.rightMargin = rightMargin;
//            params.bottomMargin = params.topMargin = topMargin;
//            rlFirstBackgroundView.setLayoutParams(params);
//            ivTeacherNotpresent.setLayoutParams(params);
//            ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//        }
//        logger.e( "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
        lecLiveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
    }

    private void setFirstParamPort() {
        lecLiveVideoAction.setFirstParamPort();
    }

    @Override
    public void changeLOrP() {
        if (videoFragment != null) {
            videoFragment.changeLOrP();
        }
    }

    /**
     * 电视投屏按钮 点击事件
     *
     * @param view
     */
    @Override
    public void onTvPlayClick(View view) {
        BuryUtil.click(R.string.click_03_63_044, mGetInfo!=null?mGetInfo.getId():"");
        if (mIsLand.get()) {
            shouldShowLetouPage = true;
            changeLOrP();
        } else {
            if (miracastBll != null) {
                miracastBll.showPager(getContentView());
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestReadPhoneStatesPermissions() {
        if (isAdded()){
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_MUST_PERMISSION);
        }

    }

    @Override
    public void onSearchRequestPromession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestReadPhoneStatesPermissions();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (miracastBll != null) {
            if (requestCode == 1) {
                if (grantResults!=null&&grantResults.length>=1&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    miracastBll.startSearch();
                }
            }
        }

    }
}
