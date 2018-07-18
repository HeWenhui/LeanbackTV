package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
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
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RegMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.UserOnline;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/5/7.
 * 直播的一些公共方法
 */
public abstract class LiveFragmentBase extends LiveVideoFragmentBase implements VideoAction {
    private String TAG = "LiveVideoActivity2Log";
    /** 播放器同步 */
    protected static final Object mIjkLock = new Object();
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
        mLogtf = new LogToFile(mLiveBll, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
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
        createLiveVideoAction();
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
        mLiveBll.onCreate();
        mLiveVideoBll.setvPlayer(vPlayer);
        addOnGlobalLayoutListener();
        startGetInfo();
    }

    protected abstract void startGetInfo();

    protected void initView() {

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
                        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                        if (!isLand) {
                            return;
                        }
                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        boolean change = LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
                        long before = System.currentTimeMillis();
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
        setFirstParam();
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

    @Override
    protected VideoFragment getFragment() {
        return new LiveVideoPlayFragment();
    }

    protected class LiveVideoPlayFragment extends VideoFragment {

        @Override
        protected void onPlayOpenStart() {
            setFirstBackgroundVisible(View.VISIBLE);
            mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
        }

        @Override
        protected void resultFailed(final int arg1, final int arg2) {
            postDelayedIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    new Thread() {
                        @Override
                        public void run() {
                            synchronized (mIjkLock) {
                                onFail(arg1, arg2);
                            }
                        }
                    }.start();
                }
            }, 1200);
        }

        @Override
        protected void onPlayOpenSuccess() {
            TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
            if (tvFail != null) {
                tvFail.setVisibility(View.INVISIBLE);
            }
            setFirstBackgroundVisible(View.GONE);
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
            postDelayedIfNotFinish(new Runnable() {

                @Override
                public void run() {
                    new Thread() {
                        @Override
                        public void run() {
                            synchronized (mIjkLock) {
                                onFail(0, 0);
                            }
                        }
                    }.start();
                }
            }, 200);
        }

        @Override
        protected void onPlayError() {
            liveVideoAction.onPlayError();
        }

        @Override
        public void onTitleShow(boolean show) {
            for (LiveMediaController.MediaPlayerControl control : mediaPlayerControls) {
                control.onTitleShow(show);
            }
        }

        @Override
        protected PlayerService.VPlayerListener getWrapListener() {
            return mLiveVideoBll.getPlayListener();
        }

    }

    public void setFirstBackgroundVisible(int visible) {
        liveVideoAction.setFirstBackgroundVisible(visible);
    }

    protected abstract boolean initData();

    public abstract void rePlay(boolean modechange);

    @Override
    public void onTeacherNotPresent(final boolean isBefore) {
        liveVideoAction.onTeacherNotPresent(isBefore);
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
        liveVideoAction.onLiveInit(getInfo);
    }

    @Override
    public void onClassTimoOut() {
        liveVideoAction.onClassTimoOut();
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        liveVideoAction.onLiveStart(server, cacheData, modechange);
        mLiveVideoBll.onLiveStart(server, cacheData, modechange);
        AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        rePlay(change.get());
    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {

    }

    @Override
    public void onLiveDontAllow(final String msg) {
        liveVideoAction.onLiveDontAllow(msg);
    }

    @Override
    public void onLiveError(ResponseEntity responseEntity) {
        liveVideoAction.onLiveError(responseEntity);
    }

    @Override
    protected void resultFailed(final int arg1, final int arg2) {
        postDelayedIfNotFinish(new Runnable() {

            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (mIjkLock) {
                            onFail(arg1, arg2);
                        }
                    }
                }.start();
            }
        }, 1200);
    }

    /**
     * 播放失败，或者完成时调用
     */
    private void onFail(int arg1, final int arg2) {
        liveVideoAction.onFail(arg1, arg2);
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

    @Override
    public void onDestroy() {
        isPlay = false;
        userOnline.stop();
        if (mLiveBll != null) {
            mLiveBll.onDestory();
        }
        ProxUtil.getProxUtil().clear();
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }
}
