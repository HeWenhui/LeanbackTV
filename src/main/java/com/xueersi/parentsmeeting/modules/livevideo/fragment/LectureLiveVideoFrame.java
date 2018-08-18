package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.*;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.leclearnreport.business.LecLearnReportIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5CoursewareIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import java.util.List;

/**
 * Created by linyuqiang on 2018/7/18.
 * 讲座布局
 */
public class LectureLiveVideoFrame extends LiveFragmentBase {
    private String TAG = "LectureLiveVideoFrameLog";
    protected LiveIRCMessageBll liveIRCMessageBll;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    protected BaseLiveMediaControllerBottom liveMediaControllerBottom;
    RelativeLayout bottomContent;
    private PopupWindow mPopupWindows;
    LecLiveVideoAction lecLiveVideoAction;
    /** onPause状态不暂停视频 */
    PauseNotStopVideoIml pauseNotStopVideoIml;

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
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam();
        videoFragment.setIsAutoOrientation(true);
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
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
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
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoLoadActivity.getInfos.get(liveType + "-" + stuId + "-" + mVSectionID);
        mLiveBll = new LiveBll2(activity, mVSectionID, liveType, from, mGetInfo);
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    @Override
    public void rePlay(boolean modechange) {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        liveVideoAction.rePlay(modechange);
        mLiveVideoBll.rePlay(modechange);
    }

    protected void addBusiness(Activity activity) {
        liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll);
        liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
        mLiveBll.addBusinessBll(liveIRCMessageBll);
        mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll));
        mLiveBll.addBusinessBll(new NBH5CoursewareIRCBll(activity, mLiveBll));
        mLiveBll.addBusinessBll(new RedPackageIRCBll(activity, mLiveBll));
        mLiveBll.addBusinessBll(new LecAdvertIRCBll(activity, mLiveBll));
        mLiveBll.addBusinessBll(new LecLearnReportIRCBll(activity, mLiveBll));
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
    }

    @Override
    protected void initView() {
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        logger.e("========>:initView:" + bottomContent);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
        createMediaControllerBottom();
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
        baseLiveMediaControllerTop.setAutoOrientation(true);
    }

    protected void createMediaControllerBottom() {
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
    }

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
                                    rePlay(false);
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
                                Loger.d(TAG, "onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
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
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        super.onLiveInit(getInfo);
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        long before = System.currentTimeMillis();
        mMediaController.setFileName(getInfo.getName());
        Loger.d(TAG, "onLiveInit:time3=" + (System.currentTimeMillis() - before));
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
        if (mIsLand.get()) {
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
                mMediaController.setControllerBottom(liveMediaControllerBottom, true);
                BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
                mMediaController.setControllerTop(baseLiveMediaControllerTop);
                controllerContent.addView(baseLiveMediaControllerTop);
                mMediaController.setAutoOrientation(true);
                liveMediaControllerBottom.setController(mMediaController);
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
                    businessBll.initViewF(bottomContent, mIsLand);
                }
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
                mMediaController.setControllerBottom(liveMediaControllerBottom, true);
                BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
                mMediaController.setControllerTop(baseLiveMediaControllerTop);
                controllerContent.addView(baseLiveMediaControllerTop);
                mMediaController.setAutoOrientation(true);
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
                    businessBll.initViewF(bottomContent, mIsLand);
                }
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
//        Loger.e(TAG, "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
        lecLiveVideoAction.setFirstParam(LiveVideoPoint.getInstance());
    }

    private void setFirstParamPort() {
        lecLiveVideoAction.setFirstParamPort();
    }
}
