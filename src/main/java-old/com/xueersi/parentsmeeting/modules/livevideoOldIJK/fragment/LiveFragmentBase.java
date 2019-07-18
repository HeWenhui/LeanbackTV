package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.UserOnline;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.video.LiveVideoBll;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/5/7.
 * 直播的一些公共方法
 */
public abstract class LiveFragmentBase extends LiveVideoFragmentBase implements VideoAction {
    private String TAG = "LiveFragmentBase";
    /** 播放器同步 */
    protected static final Object mIjkLock = BasePlayerFragment.mIjkLock;
    protected WeakHandler mHandler = new WeakHandler(null);
    protected LiveBll2 mLiveBll;
    /** 直播类型 */
    protected int liveType;
    protected String mVSectionID;
    protected LiveGetInfo mGetInfo;
    protected int from = 0;
    public static final String ENTER_ROOM_FROM = "from";
    protected LiveVideoAction liveVideoAction;
    protected LogToFile mLogtf;
    protected LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
    protected long startTime = System.currentTimeMillis();
    protected LiveVideoBll mLiveVideoBll;
    /** 正在播放 */
    protected boolean isPlay = false;
    /** 上次播放统计开始时间 */
    protected long lastPlayTime;
    /** 是否播放成功 */
    protected boolean openSuccess = false;
    /** 播放时长定时任务 */
    private final long mPlayDurTime = 420000;

    private UserOnline userOnline;
    /** Activity暂停过，执行onStop */
    protected boolean mHaveStop = false;
    protected ArrayList<LiveMediaController.MediaPlayerControl> mediaPlayerControls = new ArrayList<>();
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        logger.d("==========>onVideoCreate:");
        long before = System.currentTimeMillis();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        liveType = activity.getIntent().getIntExtra("type", 0);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        AppBll.getInstance().registerAppEvent(this);
        boolean init = initData();
        if (!init) {
            onUserBackPressed();
            return false;
        }
        mLogtf = new LogToFile(activity, TAG);
        userOnline = new UserOnline(activity, liveType, mVSectionID);
        userOnline.setHttpManager(mLiveBll.getHttpManager());
        //先让播放器按照默认模式设置
        videoView = mContentView.findViewById(R.id.vv_course_video_video);
        logger.d("onVideoCreate:videoView=" + (videoView == null));
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
        setFirstParam();
        createLiveVideoBll();
        mLiveBll.setmIsLand(mIsLand);
        mLiveBll.setVideoAction(this);
        ProxUtil.getProxUtil().put(activity, RegMediaPlayerControl.class, new RegMediaPlayerControl() {

            @Override
            public void addMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl) {
                mediaPlayerControls.add(mediaPlayerControl);
            }

            @Override
            public void removeMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl) {
                mediaPlayerControls.remove(mediaPlayerControl);
            }
        });
        ProxUtil.getProxUtil().put(activity, BasePlayerFragment.class, videoFragment);
        return true;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return mContentView;
    }

    @Override
    protected void onVideoCreateEnd() {
        mLiveBll.addBusinessShareParam("vPlayer", vPlayer);
        mLiveVideoBll.setvPlayer(vPlayer);
        addOnGlobalLayoutListener();
        onBusinessCreate();
        startGetInfo();
    }

    protected void onBusinessCreate() {
        mLiveBll.onCreate();
    }

    protected abstract void startGetInfo();

    protected void initView() {

    }

    @Override
    public void stopPlayer() {
        super.stopPlayer();
        mLiveVideoBll.stopPlay();
    }

    @Override
    protected void onUserBackPressed() {
        if (mLiveBll == null) {
            super.onUserBackPressed();
        }
        boolean userBackPressed = mLiveBll.onUserBackPressed();
        if (!userBackPressed) {
            super.onUserBackPressed();
        }
    }

    private void addOnGlobalLayoutListener() {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (videoView.getWidth() <= 0) {
                            return;
                        }
                        boolean isLand = activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                        if (!isLand) {
                            return;
                        }
                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        boolean change = LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
                        long before = System.currentTimeMillis();
                        setFirstParam();
                        if (change) {
                            onGlobalLayoutListener();
                        }
                        logger.d("onGlobalLayout:change=" + change + ",time=" + (System.currentTimeMillis() - before));
                    }
                });
            }
        }, 10);
    }

    protected void onGlobalLayoutListener() {
        List<LiveBaseBll> businessBlls = mLiveBll.getBusinessBlls();
        for (LiveBaseBll businessBll : businessBlls) {
            businessBll.setVideoLayoutF(liveVideoPoint);
        }
    }

    /**
     * 设置蓝屏界面
     */
    protected void setFirstParam() {
        if (liveVideoAction != null) {
            liveVideoAction.setFirstParam(liveVideoPoint);
        }
    }

    protected void createLiveVideoAction() {
        liveVideoAction = new LiveVideoAction(activity, mLiveBll, mContentView);
    }

    protected LiveVideoBll createLiveVideoBll() {
        LiveVideoBll liveVideoBll = new LiveVideoBll(activity, mLiveBll, liveType);
        liveVideoBll.setHttpManager(mLiveBll.getHttpManager());
        liveVideoBll.setHttpResponseParser(mLiveBll.getHttpResponseParser());
        liveVideoBll.setVideoFragment(videoFragment);
        liveVideoBll.setVideoAction(this);
        mLiveVideoBll = liveVideoBll;
        mLiveBll.setLiveVideoBll(liveVideoBll);
        return liveVideoBll;
    }

    /**
     * 三分屏重写这个方法，使用新的的Loading
     * 普通的还是走这里默认的
     */
    @Override
    protected LivePlayerFragment getFragment() {
        LiveLivePlayerPlayFragment liveVideoPlayFragment = new LiveLivePlayerPlayFragment();
        liveVideoPlayFragment.liveFragmentBase = this;
        return liveVideoPlayFragment;
    }

    @Override
    protected void restoreFragment(LivePlayerFragment videoFragment) {
        LiveLivePlayerPlayFragment liveVideoPlayFragment = (LiveLivePlayerPlayFragment) videoFragment;
        liveVideoPlayFragment.liveFragmentBase = this;
    }

    protected void onPlayOpenSuccess() {

    }

    public static class LiveLivePlayerPlayFragment extends LivePlayerFragment {
        private final String TAG = "LiveLivePlayerPlayFragment";
        LiveFragmentBase liveFragmentBase;

        public LiveLivePlayerPlayFragment() {
            logger.d("LiveLivePlayerPlayFragment");
        }

        public void setLiveFragmentBase(LiveFragmentBase liveFragmentBase) {
            this.liveFragmentBase = liveFragmentBase;
        }

        @Override
        protected void onPlayOpenStart() {
            liveFragmentBase.setFirstBackgroundVisible(View.VISIBLE);
            liveFragmentBase.mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
            liveFragmentBase.openSuccess = false;
        }

        @Override
        public void resultFailed(final int arg1, final int arg2) {
            liveFragmentBase.postDelayedIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (mIjkLock) {
                                liveFragmentBase.onFail(arg1, arg2);
                            }
                        }
                    });
                }
            }, 1200);
            liveFragmentBase.openSuccess = false;
        }

        @Override
        protected void onPlayOpenSuccess() {
            TextView tvFail = (TextView) liveFragmentBase.mContentView.findViewById(R.id.tv_course_video_loading_fail);
            if (tvFail != null) {
                tvFail.setVisibility(View.INVISIBLE);
            }
            liveFragmentBase.openSuccess = true;
            liveFragmentBase.setFirstBackgroundVisible(View.GONE);
            liveFragmentBase.onPlayOpenSuccess();
//            if (mGetInfo != null && mGetInfo.getIsShowMarkPoint().equals("1")) {
//                if (liveRemarkBll == null) {
//                    liveRemarkBll = new LiveRemarkBll(activity, vPlayer);
//                    if (videoChatBll != null) {
//                        videoChatBll.setLiveRemarkBll(liveRemarkBll);
//                    }
//                    if (mLiveBll != null && liveMediaControllerBottom != null) {
//                        if (liveTextureView == null) {
//                            ViewStub viewStub = (ViewStub) mContentView.findViewById(R.id.vs_course_video_video_texture);
//                            liveTextureView = (LiveTextureView) viewStub.inflate();
//                            liveTextureView.vPlayer = vPlayer;
//                            liveTextureView.setLayoutParams(videoView.getLayoutParams());
//                        }
//                        liveRemarkBll.showBtMark();
//                        liveRemarkBll.setTextureView(liveTextureView);
//                        liveRemarkBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//                        liveRemarkBll.setVideoView(videoView);
//                        mLiveBll.setLiveRemarkBll(liveRemarkBll);
//                        liveRemarkBll.setLiveAndBackDebug(mLiveBll);
//                    }
//                } else {
//                    liveRemarkBll.initData();
//                }
//            }
        }

        @Override
        protected void playComplete() {
            liveFragmentBase.setFirstBackgroundVisible(View.VISIBLE);
            liveFragmentBase.postDelayedIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (mIjkLock) {
                                liveFragmentBase.playComplete();
                            }
                        }
                    });
                }
            }, 200);
            liveFragmentBase.openSuccess = false;
        }

        @Override
        protected void onPlayError() {
            if (liveFragmentBase.liveVideoAction != null) {
                liveFragmentBase.liveVideoAction.onPlayError();
            }
            liveFragmentBase.openSuccess = false;
        }

        @Override
        public void onTitleShow(boolean show) {
            for (LiveMediaController.MediaPlayerControl control : liveFragmentBase.mediaPlayerControls) {
                control.onTitleShow(show);
            }
        }

        @Override
        protected VPlayerCallBack.VPlayerListener getWrapListener() {
            return liveFragmentBase.mLiveVideoBll.getPlayListener();
        }

    }

    public void setFirstBackgroundVisible(int visible) {
        if (liveVideoAction != null) {
            liveVideoAction.setFirstBackgroundVisible(visible);
        }
    }

    protected abstract boolean initData();

    public abstract void rePlay(boolean modechange);

    @Override
    public void onTeacherNotPresent(final boolean isBefore) {
        if (liveVideoAction != null) {
            liveVideoAction.onTeacherNotPresent(isBefore);
        }
    }

    @Override
    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用

    }

    @Override
    public void onPause() {
        super.onPause();
        mHaveStop = true;
    }

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        userOnline.setGetInfo(getInfo);
        userOnline.start();
        mGetInfo = getInfo;
        if (liveVideoAction != null) {
            liveVideoAction.onLiveInit(getInfo);
        }
    }

    @Override
    public void onClassTimoOut() {
        if (liveVideoAction != null) {
            liveVideoAction.onClassTimoOut();
        }
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        if (liveVideoAction == null) {
            return;
        }
        mLogtf.d("onLiveStart:mHaveStop=" + mHaveStop);
        liveVideoAction.onLiveStart(server, cacheData, modechange);
        mLiveVideoBll.onLiveStart(server, cacheData, modechange);
        AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        rePlay(change.get());
    }

    @Override
    public void onLiveTimeOut() {
        mLogtf.d("onLiveStart:onLiveTimeOut");
        if (liveVideoAction == null) {
            return;
        }
        liveVideoAction.onLiveTimeOut();
    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {

    }

    @Override
    public void onLiveDontAllow(final String msg) {
        if (liveVideoAction != null) {
            liveVideoAction.onLiveDontAllow(msg);
        }
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        if (liveVideoAction != null) {
            liveVideoAction.onLiveError(responseEntity);
        }
    }

    @Override
    protected void resultFailed(final int arg1, final int arg2) {
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(arg1, arg2);
                        }
                    }
                });
            }
        }, 1200);
    }

    /**
     * 播放完成时调用
     */
    private void playComplete() {
        if (liveVideoAction != null) {
            liveVideoAction.playComplete();
        }
    }

    /**
     * 播放失败，或者完成时调用
     */
    protected void onFail(int arg1, final int arg2) {
        if (liveVideoAction != null) {
            liveVideoAction.onFail(arg1, arg2);
        }
    }

    /**
     * 只在WIFI下使用激活
     *
     * @param onlyWIFIEvent
     * @author zouhao
     * @Create at: 2015-9-24 下午1:57:04
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {
        Toast.makeText(activity, "没有wifi", Toast.LENGTH_SHORT).show();
        onUserBackPressed();
    }

    /**
     * 是否显示移动网络提示
     */
    private boolean mIsShowMobileAlert = true;

    /**
     * 开启了3G/4G提醒
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-10-12 下午1:49:22
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.NowMobileEvent event) {
        if (mIsShowMobileAlert) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserBackPressed();
                }
            });
            cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？")
                    .showDialog();
            mIsShowMobileAlert = false;
        }
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void updateIcon() {
        if (liveVideoAction != null) {
            liveVideoAction.updateLoadingImage();
        }
        updateRefreshImage();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (event.getClass() == AppEvent.class) {
            logger.i("onEvent:netWorkType=" + event.netWorkType);
            mLiveVideoBll.onNetWorkChange(event.netWorkType);
        }
    }

    @Override
    public void onDestroy() {
        isPlay = false;
        if (mLiveVideoBll != null) {
            mLiveVideoBll.onDestroy();
        }
        if (userOnline != null) {
            userOnline.stop();
        }
        if (mLiveBll != null) {
            mLiveBll.onDestroy();
        }
        liveVideoAction.onDestroy();
        liveVideoAction = null;
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ProxUtil.getProxUtil().clear(activity);
                LiveThreadPoolExecutor.destory();
            }
        });
        LiveVideoConfig.isSmallChinese = false;
    }

    /** 测试notice */
    public void testNotice(String notice) {
        if (mLiveBll != null) {
            mLiveBll.testNotice(notice);
        }
    }
}
