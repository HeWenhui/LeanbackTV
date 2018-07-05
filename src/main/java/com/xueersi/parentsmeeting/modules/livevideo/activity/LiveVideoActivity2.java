package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tal.speech.speechrecognizer.Constants;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.SimpleVPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.VPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoteBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.UserOnline;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.notice.LiveAutoNoticeIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.AnswerRankIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity2 extends LiveFragmentBase implements VideoAction, ActivityStatic, BaseLiveMessagePager.OnMsgUrlClick, BaseLiveMediaControllerBottom.MediaChildViewClick, AudioRequest, WebViewRequest, VideoChatEvent {

    private String TAG = "LiveVideoActivityLog";
    Logger logger = LoggerFactory.getLogger(TAG);

    {
        mLayoutVideo = R.layout.activity_video_live_new;
    }

    /**
     * 播放器同步
     */
    private static final Object mIjkLock = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    /** 上次播放统计开始时间 */
    long lastPlayTime;
    /** 是否播放成功 */
    boolean openSuccess = false;
    /** 播放时长定时任务 */
    private final long mPlayDurTime = 420000;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    /** 老师不在直播间 */
    private ImageView ivTeacherNotpresent;
    RelativeLayout bottomContent;
    RelativeLayout praiselistContent;
    /** 缓冲提示 */
    private ImageView ivLoading;
    private TextView tvLoadingHint;
    private LiveGetInfo mGetInfo;
    private LiveTopic mLiveTopic;
    private String vStuCourseID;
    private String courseId;
    private String mVSectionID;
    /** Activity暂停过，执行onStop */
    private boolean mHaveStop = false;
    /** Activity在onResume */
    private boolean mIsResume = false;

    private LiveVideoSAConfig liveVideoSAConfig;
    /** 是不是文理 */
    public boolean IS_SCIENCE = true;
    public static final String ENTER_ROOM_FROM = "from";
    /** 直播类型 */
    private int liveType;
    /** 连接老师加载-主讲 */
    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
    /** 连接老师加载-辅导 */
    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
    /** 正在播放 */
    private boolean isPlay = false;
    /** video缓存时间 */
    private long videoCachedDuration;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    LiveMediaControllerBottom liveMediaControllerBottom;
    boolean audioRequest = false;
    SpeechEvaluatorUtils mIse;
    long openStartTime;
    /**
     * 接麦已经连接老师
     */
    private AtomicBoolean startRemote = new AtomicBoolean(false);
    int from = 0;
    long startTime = System.currentTimeMillis();
    /**
     * onPause状态不暂停视频
     */
    boolean onPauseNotStopVideo = false;
    LiveTextureView liveTextureView;
    private LiveBll2 mLiveBll;
    private LiveIRCMessageBll liveIRCMessageBll;
    private RollCallBll rollCallBll;
    private LiveVideoBll liveVideoBll;
    private UserOnline userOnline;
    //LiveMessageBll liveMessageBll;
    private static String Tag = "LiveVideoActivity2";

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
        liveVideoBll = new LiveVideoBll(activity, mLiveBll, liveType);
        liveVideoBll.setVideoChatEvent(this);
        //先让播放器按照默认模式设置
        videoView = mContentView.findViewById(R.id.vv_course_video_video);
        logger.d("onVideoCreate:videoView=" + (videoView == null));
        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        setFirstParam(lp);
        // liveMessageBll.setVideoLayout(lp.width, lp.height);
        logger.d("onVideoCreate:time1=" + (System.currentTimeMillis() - startTime) + "," + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
        LiveGetInfo mGetInfo = LiveVideoEnter.getInfos.get(stuId + "-" + vStuCourseID + "-" + mVSectionID);
        initAllBll();
        logger.d("onVideoCreate:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();

        liveVideoBll.setHttpManager(mLiveBll.getHttpManager());
        liveVideoBll.setHttpResponseParser(mLiveBll.getHttpResponseParser());
        liveVideoBll.setVideoFragment(videoFragment);
        liveVideoBll.setVideoAction(this);

        addBusiness(activity, bottomContent);
        logger.d("onVideoCreate:time3=" + (System.currentTimeMillis() - before));
        return true;
    }

    @Override
    protected void onVideoCreateEnd() {
        mLiveBll.onCreate();
        mLiveBll.getInfo(mGetInfo);
        liveVideoBll.setvPlayer(vPlayer);
        liveIRCMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
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
                        setFirstParam(lp);
                        // liveMessageBll.setVideoLayout(lp.width, lp.height);
                        setMediaControllerBottomParam(lp);

                    }
                });
            }
        }, 10);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return mContentView;
    }

    /**
     * 添加 直播间内 所需的功能模块
     *
     * @param activity
     * @param bottomContent
     */
    private void addBusiness(Activity activity, RelativeLayout bottomContent) {

        TeamPkBll teamPkBll = new TeamPkBll(activity, mLiveBll, bottomContent);
        mLiveBll.addBusinessBll(teamPkBll);

        rollCallBll = new RollCallBll(activity, mLiveBll, bottomContent);
        mLiveBll.addBusinessBll(rollCallBll);

        mLiveBll.addBusinessBll(new TeacherPraiseBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new QuestionIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new LiveVoteBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new LiveAutoNoticeIRCBll(activity, mLiveBll, bottomContent));
        mLiveBll.addBusinessBll(new AnswerRankIRCBll(activity, mLiveBll, bottomContent));
        liveIRCMessageBll = new LiveIRCMessageBll(activity, mLiveBll, bottomContent);
        mLiveBll.addBusinessBll(liveIRCMessageBll);
        mLiveBll.setLiveIRCMessageBll(liveIRCMessageBll);
    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
    }

    @Override
    public void showLongMediaController() {
        super.showLongMediaController();
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) mContentView.findViewById(R.id.iv_course_video_teacher_notpresent);
        bottomContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);
        logger.e("========>:initView:" + bottomContent);
        praiselistContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_praiselist_content);
        praiselistContent.setVisibility(View.VISIBLE);
        ivLoading = (ImageView) mContentView.findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        mContentView.findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, mMediaController, videoFragment);
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, mMediaController, videoFragment);
        liveMediaControllerBottom.setVisibility(View.INVISIBLE);
        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
        //聊天
        long before = System.currentTimeMillis();
        //liveMessageBll.initViewLive(bottomContent);
        Loger.d(TAG, "initView:time1=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        Loger.d(TAG, "initView:time2=" + (System.currentTimeMillis() - before));
    }

    protected boolean initData() {
        Intent intent = activity.getIntent();
        courseId = intent.getStringExtra("courseId");
        vStuCourseID = intent.getStringExtra("vStuCourseID");
        mVSectionID = intent.getStringExtra("vSectionID");
        mVideoType = MobEnumUtil.VIDEO_LIVE;
        if (TextUtils.isEmpty(mVSectionID)) {
            Toast.makeText(activity, "直播场次不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
        XesMobAgent.enterLiveRoomFrom(from);
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {// 直播
            mLiveBll = new LiveBll2(activity, vStuCourseID, courseId, mVSectionID, from, null);
        } else if (liveType == LiveBll.LIVE_TYPE_LECTURE) {
            mLiveBll = new LiveBll2(activity, mVSectionID, liveType, from);
        } else if (liveType == LiveBll.LIVE_TYPE_TUTORIAL) {// 辅导
            mLiveBll = new LiveBll2(activity, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
        } else {
            Toast.makeText(activity, "直播类型不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
        ProxUtil.getProxUtil().put(activity, LiveBll2.class, mLiveBll);
        return true;
    }

    private void initAllBll() {
        mLiveBll.setVideoAction(this);
        mLiveBll.setLiveVideoBll(liveVideoBll);
        userOnline = new UserOnline(activity);
        android.util.Log.e("LiveVideoActivity", "====>initAllBll:" + bottomContent);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        setMediaControllerBottomParam(videoView.getLayoutParams());
    }

    /**
     * 控制栏下面距离视频底部的尺寸
     */
    public void setMediaControllerBottomParam(ViewGroup.LayoutParams lp) {
        //控制栏下面距离视频底部的尺寸
        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMediaControllerBottom;
        int topGap = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    /**
     * 设置蓝屏界面
     */
    private void setFirstParam(ViewGroup.LayoutParams lp) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        int rightMargin = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / LiveVideoConfig.VIDEO_WIDTH + (screenWidth - lp.width) / 2);
        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
            params.rightMargin = rightMargin;
            params.bottomMargin = params.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlFirstBackgroundView, params);
            LayoutParamsUtil.setViewLayoutParams(ivTeacherNotpresent, params);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLand) {
            mMediaController.setControllerBottom(liveMediaControllerBottom, false);
            setMediaControllerBottomParam(videoView.getLayoutParams());
        }
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
        protected void onPlayOpenSuccess() {
            TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
            if (tvFail != null) {
                tvFail.setVisibility(View.INVISIBLE);
            }
            setFirstBackgroundVisible(View.GONE);
            rollCallBll.onPlayOpenSuccess(videoView.getLayoutParams());
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
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    tvLoadingHint.setText("您的手机暂时不支持播放直播");
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onTitleShow(boolean show) {
//            liveMessageBll.onTitleShow(show);
//            if (rankBll != null) {
//                rankBll.onTitleShow(show);
//            }
        }

        protected VPlayerListener getWrapListener() {
            return liveVideoBll.getPlayListener();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResume = true;
        if (mHaveStop) {
            mHaveStop = false;
            if (startRemote.get()) {
                return;
            }
            if (!onPauseNotStopVideo) {
                setFirstBackgroundVisible(View.VISIBLE);
                new Thread() {
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
                }.start();
            }
            onPauseNotStopVideo = false;
        }
        if (mLiveBll != null) {
            mLiveBll.onResume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mIsResume = false;
        mHaveStop = true;
        if (startRemote.get()) {
            return;
        }
        if (!onPauseNotStopVideo) {
            new Thread() {
                @Override
                public void run() {
                    synchronized (mIjkLock) {
                        if (isInitialized()) {
                            if (openSuccess) {
                                liveVideoBll.stopPlayDuration();
                                Loger.d(TAG, "onPause:playTime=" + (System.currentTimeMillis() - lastPlayTime));
                            }
                            vPlayer.releaseSurface();
                            vPlayer.stop();
                        }
                        isPlay = false;
                    }
                }
            }.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLiveBll != null) {
            mLiveBll.onStop();
        }
    }

    @Override
    public boolean isResume() {
        return mIsResume;
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
    public void onTeacherNotPresent(final boolean isBefore) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                        setFirstBackgroundVisible(View.VISIBLE);
                        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                        ivTeacherNotpresent.setVisibility(View.GONE);
                        return;
                    }
                }
                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                } else {
                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
                    ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
                    mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用

    }

    int count = 0;

    @Override
    public void onLiveInit(LiveGetInfo getInfo) {
        mGetInfo = getInfo;
        liveVideoSAConfig = mLiveBll.getLiveVideoSAConfig();
        IS_SCIENCE = liveVideoSAConfig.IS_SCIENCE;
        liveMediaControllerBottom.setVisibility(View.VISIBLE);
        if ("1".equals(mGetInfo.getIsShowMarkPoint())) {
            liveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
        }
        long before = System.currentTimeMillis();
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
        }
        Loger.d(TAG, "onLiveInit:time=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        Loger.d(TAG, "onLiveInit:time2=" + (System.currentTimeMillis() - before));
        before = System.currentTimeMillis();
        if (1 == getInfo.getIsEnglish()) {
            mIse = new SpeechEvaluatorUtils(true);
            //记录当前正在走的模型，留给界面更新使用
            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                    RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
        } else {
            if (!IS_SCIENCE) {
                String[] subjectIds = getInfo.getSubjectIds();
                if (subjectIds != null) {
                    for (int i = 0; i < subjectIds.length; i++) {
                        String subjectId = subjectIds[i];
                        if (LiveVideoConfig.SubjectIds.SUBJECT_ID_CH.equals(subjectId)) {
                            mIse = new SpeechEvaluatorUtils(true, Constants.ASSESS_PARAM_LANGUAGE_CH);
                            //记录当前正在走的模型，留给界面更新使用
                            ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                                    RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
                            break;
                        }
                    }
                }
            }
        }
        mMediaController.setFileName(getInfo.getName());
        Loger.d(TAG, "onLiveInit:time3=" + (System.currentTimeMillis() - before));
    }

    @Override
    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
        final AtomicBoolean change = new AtomicBoolean(modechange);// 直播状态是不是变化
        mLiveTopic = cacheData;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (change.get()) {
                    setFirstBackgroundVisible(View.VISIBLE);
                }
                if (tvLoadingHint != null) {
                    if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                        tvLoadingHint.setText(mainTeacherLoad);
                    } else {
                        tvLoadingHint.setText(coachTeacherLoad);
                    }
                }
            }
        });
        liveVideoBll.onLiveStart(server, cacheData, modechange);
        rePlay(change.get());
    }

    @Override
    public void onModeChange(final String mode, final boolean isPresent) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (isInitialized()) {
                    liveVideoBll.stopPlayDuration();
                    vPlayer.releaseSurface();
                    vPlayer.stop();
                }
                isPlay = false;
                setFirstBackgroundVisible(View.VISIBLE);
                if (liveType == LiveBll.LIVE_TYPE_LIVE) {
                    if (mGetInfo.getStudentLiveInfo().isExpe() && LiveTopic.MODE_TRANING.equals(mode)) {
                        tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                        return;
                    }
                }
                if (isPresent) {
                    if (tvLoadingHint != null) {
                        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onClassTimoOut() {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "你来晚了，下课了，等着看回放吧";
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public void onLiveDontAllow(final String msg) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
        XESToastUtils.showToast(activity, "将在3秒内退出");
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                activity.setResult(ShareBusinessConfig.LIVE_USER_ERROR, intent);
                activity.finish();
            }
        }, 3000);
    }

    @Override
    public void onLiveError(final ResponseEntity responseEntity) {
        mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
        final String msg = "" + responseEntity.getErrorMsg();
        if (tvLoadingHint != null) {
            tvLoadingHint.setText(msg);
        }
    }

    @Override
    public AtomicBoolean getStartRemote() {
        return startRemote;
    }

    /**
     * 第一次播放，或者播放失败，重新播放
     *
     * @param modechange
     */
    @Override
    public void rePlay(boolean modechange) {
        if (mGetInfo == null) {//上次初始化尚未完成
            return;
        }
        if (startRemote.get()) {
            return;
        }
        liveVideoBll.onReplay();
        if (liveType == LiveBll.LIVE_TYPE_LIVE) {
            if (LiveTopic.MODE_TRANING.endsWith(mGetInfo.getLiveTopic().getMode()) && mGetInfo.getStudentLiveInfo().isExpe()) {
                tvLoadingHint.setText("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务");
                setFirstBackgroundVisible(View.VISIBLE);
                mContentView.findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.GONE);
                ivTeacherNotpresent.setVisibility(View.GONE);
                return;
            }
        }
        new Thread() {
            @Override
            public void run() {
                boolean isPresent = mLiveBll.isPresent();
                if (isPresent) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (tvLoadingHint != null) {
                                if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                                    tvLoadingHint.setText(mainTeacherLoad);
                                } else {
                                    tvLoadingHint.setText(coachTeacherLoad);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
        liveVideoBll.rePlay(modechange);
    }

    @Override
    public void stopPlay() {
        if (isInitialized()) {
            vPlayer.releaseSurface();
            vPlayer.stop();
        }
    }

    /**
     * 播放失败，或者完成时调用
     */
    private void onFail(int arg1, final int arg2) {
        liveVideoBll.onFail(arg1, arg2);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (tvLoadingHint != null) {
                    String errorMsg = null;
                    AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
                    if (error != null) {
                        errorMsg = error.getNum() + " (" + error.getTag() + ")";
                    }
                    TextView tvFail = (TextView) mContentView.findViewById(R.id.tv_course_video_loading_fail);
                    if (errorMsg != null) {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.VISIBLE);
                            tvFail.setText(errorMsg);
                        }
                    } else {
                        if (tvFail != null) {
                            tvFail.setVisibility(View.INVISIBLE);
                        }
                    }
                    if (mLiveBll.isPresent()) {
                        if (liveType != LiveBll.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic().getMode())) {
                            tvLoadingHint.setText(mainTeacherLoad);
                        } else {
                            tvLoadingHint.setText(coachTeacherLoad);
                        }
                    }
                    RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
                }
            }
        });
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        Loger.i(TAG, "onEvent:netWorkType=" + event.netWorkType);
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
        //liveMessageBll.onGetMyGoldDataEvent(event.goldNum);
    }

    @Override
    protected void onUserBackPressed() {
        super.onUserBackPressed();
    }

    @Override
    public void onDestroy() {
        isPlay = false;
        //liveMessageBll.onDestroy();
        new Thread() {
            @Override
            public void run() {
                if (mLiveBll != null) {
                    mLiveBll.onDestory();
                }
                ProxUtil.getProxUtil().clear();
            }
        }.start();
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, LiveVideoActivity2.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onMsgUrlClick(String url) {
//        onPauseNotStopVideo = true;
    }

    @Override
    public void onMediaViewClick(View child) {

    }


    @Override
    public void request(OnAudioRequest onAudioRequest) {
        audioRequest = true;
        if (onAudioRequest != null) {
            onAudioRequest.requestSuccess();
        }
    }

    @Override
    public void release() {
        audioRequest = false;
    }

    @Override
    public void requestWebView() {
    }

    @Override
    public void releaseWebView() {
    }

    @Override
    public void onWebViewEnd() {
    }

    public void updateIcon() {
        updateLoadingImage();
        updateRefreshImage();
    }

    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
            }
        }
    }

    @Override
    public boolean isFinishing() {
        return activity.isFinishing();
    }
}
