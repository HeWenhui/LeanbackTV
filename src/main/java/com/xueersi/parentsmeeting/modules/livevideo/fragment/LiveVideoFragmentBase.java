package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlayerFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

/***
 * 视频播放主界面
 *
 * @author 林玉强
 */
public class LiveVideoFragmentBase extends Fragment {
    private String TAG = "LiveVideoFragmentBase";
    Logger logger = LoggerFactory.getLogger(TAG);
    protected BaseActivity activity;
    /** 布局默认资源 */
    protected int mLayoutVideo = R.layout.fragment_video_live;
    /** 播放器可刷新布局 */
    protected int mLayoutBackgroundRefresh = R.layout.layout_video_resfresh;
    /** 视频加载失败的标识码 */
    public static final int RESULT_FAILED = -7;
    /** 所在的Activity是否已经onCreated */
    private boolean mCreated = false;
    /** 当前界面是否横屏 */
    protected AtomicBoolean mIsLand = new AtomicBoolean(false);
    /** 播放器的屏幕高 */
    private int mPortVideoHeight = 0;
    /** 当前播放进度 */
    protected long mCurrentPosition;
    /** 视频总时长 */
    protected long mDuration;
    /** 播放器界面的模式 */
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    protected LivePlayerFragment videoFragment;
    /** 放播放器的 io.vov.vitamio.widget.CenterLayout */
    protected ViewGroup viewRoot;
    /** 播放器的VideoView com.xueersi.parentsmeeting.player.media.VideoView */
    protected VideoView videoView;
    /** 播放器父布局 */
    protected RelativeLayout rlContent;
    /** 播放器播放失败时的提供可刷新操作的背景 */
    protected View videoBackgroundRefresh;
    /** 重新刷新 */
    private Button btnVideoRefresh;
    /** 刷新页面的回退按钮 */
    private ImageView ivBack;
    /** 加载中动画的加载文字 */
    private TextView tvVideoLoadingText;
    /** 播放器的控制对象 */
    protected LiveMediaController mMediaController;
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 是否可以自动横竖屏转换 */
    protected boolean mIsAutoOrientation = true;
    /** 播放器统计时长 */
    private double mUMPlayVideoTime;
    /** 视频类型 */
    protected String mVideoType = MobEnumUtil.VIDEO_RECORDED;
    /** 是否可以播放当前视频 */
    protected boolean mIsEnalbePlayer = true;
    // region 解锁、屏幕点亮、耳麦拔插广播
    /** 是否完成了当前视频的播放 */
    private boolean mCloseComplete = false;
    String video;
    float leftVolume = VP.DEFAULT_STEREO_VOLUME, rightVolume = VP.DEFAULT_STEREO_VOLUME;

    // region 生命周期及系统调用
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.setLogMethod(false);
        activity = (BaseActivity) getActivity();
        sendPlayVideoHandler.sendEmptyMessageDelayed(1, 1000);
        mIsLand.set(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        if (!mIsLand.get()) {
            mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        }
//        mPortVideoHeight = (int) LiveVideoConfig.VIDEO_HEIGHT;
        logger.d("onCreate:mPortVideoHeight=" + mPortVideoHeight + ",IsLand=" + mIsLand.get());
        //showDialog(savedInstanceState);
    }

    private void onSelect(Bundle savedInstanceState, String video) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置在该页面音量控制键的音频流为媒体音量
        mCreated = true; // 界面onCreate完毕
        if (onVideoCreate(savedInstanceState)) {
            createPlayer();
            onVideoCreateEnd();
        } else {
            activity.finish();
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        videoFragment.setRequestedOrientation(requestedOrientation);
    }

    protected void onVideoCreateEnd() {

    }

    protected void createPlayer() {
        vPlayer = videoFragment.createPlayer();
    }

    protected boolean onVideoCreate(Bundle savedInstanceState) {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置视频可播放
        videoFragment.setIsPlayerEnable(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 设置视频不可播放
        videoFragment.setIsPlayerEnable(false);
        if (!mCreated) {
            return;
        }
        if (isInitialized()) {
            if (vPlayer != null && vPlayer.isPlaying()) {
                // 暂停播放
                pausePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mCreated) {
            return;
        }
        XesMobAgent.userMarkVideoDestory(MobEnumUtil.MARK_VIDEO_ONSTOP);
        // 友盟统计
        umPlayVideoTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        boolean isRegistered = EventBus.getDefault().isRegistered(this);
        logger.d("onDestroy:isRegistered=" + isRegistered);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mIsLand.set(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        // 设置当前屏幕是否横屏
        loadLandOrPortView(); // 重新加载界面
        if (isInitialized()) {
            setVideoLayout(); // 设置播放器VideoView的布局样式
            if (mIsLand.get()) {
                if (mMediaController != null) {
                    mMediaController.showSystemUi(false);
                }
            }
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onConfigurationChanged(newConfig);
    }

    public final void onBackPressed() {
        // 这里需要写代码，如果是横屏则转换竖屏
        if (mIsLand.get()) {
            // 如果是横屏则切换为竖屏
            if (mIsAutoOrientation) {
                videoFragment.changeLOrP();
            } else {
                onUserBackPressed();
//                super.onBackPressed();
            }
        } else {
            onUserBackPressed();
//            super.onBackPressed();
        }
    }

    /**
     * 用户点击返回，判断是不是程序崩溃
     */
    protected void onUserBackPressed() {
//        activity.onBackPressed();
        activity.finish(LiveVideoConfig.VIDEO_CANCLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            // 据查阅资料是用来解决某个GOOGLE自带的BUG的
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            super.onSaveInstanceState(outState);
        }
    }

    // region 播放管理业务
    protected RelativeLayout mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        mContentView = (RelativeLayout) inflater.inflate(R.layout.frag_livevideo_content, container, false);
        logger.d("onCreateView");
        loadView(mLayoutVideo);
        return mContentView;
    }

    public ViewGroup getContentView() {
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoCreate.savedInstanceState = savedInstanceState;
    }

    LiveOnVideoCreate videoCreate = new LiveOnVideoCreate();

    class LiveOnVideoCreate implements LivePlayerFragment.OnVideoCreate {
        Bundle savedInstanceState;

        @Override
        public void onVideoCreate() {
            video = "ijk";
            logger.d("onActivityCreated:frag=" + ((ViewGroup) mContentView.findViewById(R.id.rl_live_video_frag)).getChildCount());
            onSelect(savedInstanceState, video);
        }
    }

    /** 加载界面 */
    protected void loadView(int id) {
        if (mContentView != null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        //getLayoutInflater();
        RelativeLayout view = (RelativeLayout) inflater.inflate(id, mContentView, false);
        mContentView = view;
        activity.getWindow().setBackgroundDrawable(null);
        viewRoot = (ViewGroup) mContentView.findViewById(R.id.cl_course_video_root);// 播放器所在的io.vov.vitamio.widget.CenterLayout
        videoView = (VideoView) mContentView.findViewById(R.id.vv_course_video_video); // 播放器的videoView
        rlContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_content);
        videoBackgroundRefresh = inflater.inflate(mLayoutBackgroundRefresh, rlContent, false); // 失败时播放器显示的背景
        videoBackgroundRefresh.setVisibility(View.GONE);
        rlContent.addView(videoBackgroundRefresh);
        tvVideoLoadingText = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_tip); // 加载进度文字框
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        LivePlayerFragment fragment = (LivePlayerFragment) getChildFragmentManager().findFragmentByTag("LivePlayerFragment");
        if (fragment == null) {
            fragment = getFragment();
            transaction.add(R.id.rl_live_video_frag, fragment, "LivePlayerFragment");
        } else {
            restoreFragment(fragment);
        }
        fragment.setOnVideoCreate(videoCreate);
        videoFragment = fragment;
        transaction.commit();
        logger.d("loadView:frag=" + ((ViewGroup) mContentView.findViewById(R.id.rl_live_video_frag)).getChildCount());
        btnVideoRefresh = (Button) videoBackgroundRefresh.findViewById(R.id.btn_layout_video_resfresh); // 刷新按钮
        btnVideoRefresh.setOnClickListener(btnRefreshListener); // 刷新事件
        ivBack = (ImageView) videoBackgroundRefresh.findViewById(R.id.iv_layout_video_resfresh_back);
        ivBack.setOnClickListener(ivRefreshBackListener); // 刷新页面的回退
        mMediaController = new LiveMediaController(activity, videoFragment);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ((ViewGroup) mContentView.findViewById(R.id.rl_course_video_live_controller_content)).addView(mMediaController, params);
        fragment.setMediaController(mMediaController);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 使屏幕保持长亮
        loadLandOrPortView();
    }

    protected LivePlayerFragment getFragment() {
        return new LivePlayerFragment();
    }

    protected void restoreFragment(LivePlayerFragment videoFragment) {

    }

    /** 加载旋转屏时相关布局 */
    protected void loadLandOrPortView() {
        LayoutParams lpr = rlContent.getLayoutParams();
        logger.d("loadLandOrPortView:mIsLand=" + mIsLand.get());
        if (lpr == null) {
            return;
        }
        if (mIsLand.get()) {
            lpr.height = LayoutParams.MATCH_PARENT;
        } else {
            if (mPortVideoHeight == 0) {
                mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
            }
            lpr.height = mPortVideoHeight;
            // lpr.height = VP.DEFAULT_PORT_HEIGHT;
        }
//        rlContent.setLayoutParams(lpr);
        videoFragment.loadLandOrPortView(mIsLand.get());
    }

    /** 设置播放器的界面布局 */
    protected void setVideoLayout() {
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, vPlayer.getVideoWidth(),
                vPlayer.getVideoHeight(), vPlayer.getVideoAspectRatio());
    }

    /** 加载视频异常时出现可重新刷新的背景界面 */
    @Deprecated
    protected void showRefresyLayout(int arg1, int arg2) {
        videoBackgroundRefresh.setVisibility(View.VISIBLE);
        updateRefreshImage();
        TextView errorInfo = (TextView) videoBackgroundRefresh.findViewById(R.id.tv_course_video_errorinfo);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errorInfo.setVisibility(View.VISIBLE);
            errorInfo.setText(error.getNum() + " (" + error.getTag() + ")");
        } else {
            errorInfo.setVisibility(View.GONE);
        }
        videoBackgroundRefresh.getLayoutParams().height = LayoutParams.MATCH_PARENT;
    }

    /** 播放一个新的视频 */
    protected void playNewVideo(Uri uri, String displayName) {
        videoFragment.playNewVideo(uri, displayName);
    }

    public void setVolume(float left, float right) {
        videoFragment.setVolume(left, right);
    }

    /**
     * 播放一个新的视频
     *
     * @param uri
     * @param displayName
     * @param shareKey    用于标识当前视频的唯一值
     * @author zouhao
     * @Create at: 2015-9-23 下午7:45:41
     */
//    protected void playNewVideo(Uri uri, String displayName, String shareKey) {
//        videoFragment.playNewVideo(uri, displayName, shareKey);
//    }

    /** 播放下一个视频 */
    protected void startPlayNextVideo() {

    }

    /** 暂停播放 */
    protected void pausePlayer() {
        if (isInitialized()) {
            vPlayer.pause();
        }
    }

    /** 停止播放 */
    public void stopPlayer() {
        if (isInitialized()) {
            vPlayer.releaseSurface();
            vPlayer.stop();
        }
    }

    /** seek完成 */
    protected void onSeekComplete() {

    }

    /***
     * 正在播放中的实时进度回调
     *
     * @param currentPosition 当前播放的进度
     * @param duration        视频总时长(毫秒)
     * @author zouhao
     * @Create at: 2015-6-5 上午11:33:26
     */
    protected void playingPosition(long currentPosition, long duration) {
        this.mCurrentPosition = currentPosition;
        this.mDuration = duration;
    }

    /** 刷新界面重新加载视频 */
    protected void onRefresh() {
        if (mIsEnalbePlayer) {
            loadView(mLayoutVideo);
        }
    }

    /**
     * 是否允许视频界面自动横竖屏
     *
     * @param isAutoOrientation
     */
    public void setAutoOrientation(boolean isAutoOrientation) {
        mIsAutoOrientation = isAutoOrientation;
    }

    /** 视频非正常播放完毕，有可能是断网了，也有可能一开始打开失败了 */
    @Deprecated
    protected void resultFailed(int arg1, int arg2) {
        showRefresyLayout(arg1, arg2);
    }

    /** 判断当前为竖屏并且处于播放状态时，显示控制栏 */
    public void showLongMediaController() {
        if (!mIsLand.get()) {
            // 竖屏时长时间显示
            mMediaController.showLong();
        } else {
            // 横屏时短时间显示
            mMediaController.show();
        }
    }

    /** 播放器是否已经成功初始化完毕处于可以加载资源随时播放的状态 */
    protected boolean isInitialized() {
        return (mCreated && vPlayer != null && vPlayer.isInitialized());
    }


    /** 播放异常刷新界面 */
    OnClickListener btnRefreshListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    /** 刷新界面上返回按钮 */
    OnClickListener ivRefreshBackListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /** 统计观看视频时长 */
    @SuppressLint("HandlerLeak")
    private Handler sendPlayVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 视频初始化完成，视频正在播放,统计观看时长
            if (isInitialized() && vPlayer != null && vPlayer.isPlaying()) {
                mUMPlayVideoTime++;
            }
            if (!activity.isFinishing()) {
                sendPlayVideoHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    private void umPlayVideoTime() {
        double videoPlayTime = 1;
        if (mUMPlayVideoTime > 10) {
            if (MobEnumUtil.VIDEO_RECORDED.equals(mVideoType)) {
                videoPlayTime = Math.floor(mUMPlayVideoTime / 10);
            } else {
                videoPlayTime = Math.floor(mUMPlayVideoTime / 60);
            }
        }
        mUMPlayVideoTime = 0;
        String timeKey = (int) videoPlayTime + "～" + (int) (videoPlayTime + 1);
        if (MobEnumUtil.VIDEO_RECORDED.equals(mVideoType)) {
            timeKey = timeKey + "(10秒)";
        } else {
            timeKey = timeKey + "(分)";
        }
        XesMobAgent.userPlayVideoTime(mVideoType, timeKey);
    }

    protected void updateRefreshImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        ImageView ivRefresh = (ImageView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id.iv_course_video_refresh_bg);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivRefresh);
            }
        }
    }
}
