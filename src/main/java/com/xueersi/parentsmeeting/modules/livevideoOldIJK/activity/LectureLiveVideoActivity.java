package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.entity.FooterIconEntity;
//import com.xueersi.common.event.AppEvent;
//import com.xueersi.common.event.MiniEvent;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.logerhelper.MobEnumUtil;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.common.permission.XesPermission;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.lib.framework.utils.ScreenUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
//import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.SimpleVPlayerListener;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack.VPlayerListener;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
//import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
//import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
//import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
//import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.H5CoursewareBll;
//import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertBll;
//import com.xueersi.parentsmeeting.modules.livevideo.learnreport.business.LecLearnReportBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
//import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
//import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.FloatWindowManager;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import tv.danmaku.ijk.media.player.AvformatOpenInputError;
//
///**
// * 直播
// *
// * @author linyuqiang
// */
//public class LectureLiveVideoActivity extends LiveVideoActivityBase implements VideoAction, ActivityStatic, BaseLiveMessagePager.OnMsgUrlClick, ActivityChangeLand {
//
//    private String TAG = "LecLiveVideoActivityLog";
//    /**
//     * 播放器同步
//     */
//    private static final Object mIjkLock = new Object();
//    private WeakHandler mHandler = new WeakHandler(null);
//    /** 缓冲超时 */
//    private final long mBufferTimeout = 5000;
//    /** 打开超时 */
//    private final long mOpenTimeOut = 15000;
//    /** 播放时长定时任务 */
//    private final long mPlayDurTime = 420000;
//    private LiveBll mLiveBll;
//    /**
//     * 直播缓存打开统计
//     */
//    private VPlayerListener mPlayStatistics;
//    /**
//     * 初始进入播放器时的预加载界面
//     */
//    private RelativeLayout rlFirstBackgroundView;
//    /**
//     * 老师不在直播间
//     */
//    private ImageView ivTeacherNotpresent;
//    /**
//     * 缓冲提示
//     */
//    private ImageView ivLoading;
//    private TextView tvLoadingHint;
//    private LiveGetInfo mGetInfo;
//    /** 直播服务器 */
//    private PlayServerEntity mServer;
//    private ArrayList<PlayserverEntity> failPlayserverEntity = new ArrayList<>();
//    private ArrayList<PlayserverEntity> failFlvPlayserverEntity = new ArrayList<>();
//    /**
//     * 直播服务器选择
//     */
//    private PlayserverEntity lastPlayserverEntity;
//    private int lastIndex;
//    private LiveTopic mLiveTopic;
//    private String mVSectionID;
//    /**
//     * Activity暂停过，执行onStop
//     */
//    private boolean mHaveStop = false;
//    /**
//     * Activity在onResume
//     */
//    private boolean mIsResume = false;
//    private LogToFile mLogtf;
//    /**
//     * 一些用户错误
//     */
//    public static final int LIVE_USER_ERROR = 102;
//
//    public static final String ENTER_ROOM_FROM = "from";
//    RelativeLayout questionContent;
//    /**
//     * 直播类型
//     */
//    private int liveType;
//    /**
//     * 连接老师加载-主讲
//     */
//    private final String mainTeacherLoad = "正在连接主讲老师，请耐心等候";
//    /** 连接老师加载-辅导 */
//    private final String coachTeacherLoad = "正在连接辅导老师，请耐心等候";
//    /** 正在播放 */
//    private boolean isPlay = false;
//    /** video缓存时间 */
//    private long videoCachedDuration;
//    LiveMessageBll liveMessageBll;
//    QuestionBll questionBll;
//    RollCallBll rollCallBll;
//    //RedPackageBll redPackageBll;
//    LecLearnReportBll learnReportBll;
//    H5CoursewareBll h5CoursewareBll;
//    LecAdvertBll lecAdvertAction;
//    //    StarInteractBll starBll;
//    private Boolean picinpic = false;
//    /**
//     * 视频宽度
//     */
//    public static final float VIDEO_WIDTH = 1280f;
//    /**
//     * 视频高度
//     */
//    public static final float VIDEO_HEIGHT = 720f;
//    /**
//     * 视频宽高比
//     */
//    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;
//    long openStartTime;
//    /** onPause状态不暂停视频 */
//    boolean onPauseNotStopVideo = false;
//    private View mFloatView;
//    private PopupWindow mPopupWindows;
//    private View mInflate;
//    private static WindowManager mWindowManager;
//    private static WindowManager.LayoutParams wmParams;
//    private ViewGroup mParent;
//    private LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//
//    @Override
//    protected boolean onVideoCreate(Bundle savedInstanceState) {
//        mLogtf = new LogToFile(TAG);
//        mLogtf.clear();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        liveType = getIntent().getIntExtra("type", 0);
////        LectureMediaControllerBottom liveMediaControllerBottom = new LectureMediaControllerBottom(this,
//// mMediaController, this);
//        LiveMediaControllerBottom liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController,
//                this);
//        liveMessageBll = new LiveMessageBll(this, liveType);
//        liveMessageBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
//        questionBll = new QuestionBll(this, "");
//        liveMessageBll.setQuestionBll(questionBll);
//        rollCallBll = new RollCallBll(this);
//        // redPackageBll = new RedPackageBll(this);
//        learnReportBll = new LecLearnReportBll(this);
//        h5CoursewareBll = new H5CoursewareBll(this);
//        lecAdvertAction = new LecAdvertBll(this);
//        questionBll.setShareDataManager(mShareDataManager);
//        AppBll.getInstance().registerAppEvent(this);
//        boolean init = initData();
//        if (!init) {
//            onUserBackPressed();
//            return false;
//        }
//        liveMessageBll.setLiveBll(mLiveBll);
//        //rollCallBll.setLiveBll(mLiveBll);
//        //redPackageBll.setLiveBll(mLiveBll);
//        learnReportBll.setLiveId(mVSectionID);
//        learnReportBll.setLiveBll(mLiveBll);
//        learnReportBll.setmShareDataManager(mShareDataManager);
//        questionBll.setLiveBll(mLiveBll);
//        lecAdvertAction.setLecAdvertHttp(mLiveBll);
//        lecAdvertAction.setLiveid(mVSectionID);
//        questionBll.setVSectionID(mVSectionID);
//        //redPackageBll.setVSectionID(mVSectionID);
//        questionBll.setLiveType(liveType);
//        questionBll.initData();
//        initView();
//        changeLandAndPort();
//        return true;
//    }
//
//    @Override
//    public void showLongMediaController() {
//        mMediaController.show();
//    }
//
//    @Override
//    protected void showRefresyLayout(int arg1, int arg2) {
//        super.showRefresyLayout(arg1, arg2);
//    }
//
//    private void initView() {
//        // 预加载布局
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
//        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
//        questionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
//        questionContent.setVisibility(View.VISIBLE);
//        //聊天
//        liveMessageBll.initView(questionContent, mIsLand);
//        //聊天
//        //if (liveType != LiveBll.LIVE_TYPE_LECTURE) {
//        //}
//        //点名
//        rollCallBll.initView(questionContent);
//        //互动题和懂了吗
//        questionBll.initView(questionContent, mIsLand);
//        //红包
//        // redPackageBll.initView(questionContent);
//        //学习报告
//        learnReportBll.initView(questionContent);
//        h5CoursewareBll.initView(questionContent);
//        lecAdvertAction.initView(questionContent, mIsLand);
//        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
//        updateLoadingImage();
//        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
//        // 预加载布局中退出事件
//        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
//        tvLoadingHint.setText("获取课程信息");
//        //先让播放器按照默认模式设置
//        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
//                (int) VIDEO_HEIGHT, VIDEO_RATIO);
//        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//        liveMessageBll.setVideoLayout(lp.width, lp.height);
//        LiveVideoPoint.initLiveVideoPoint(this, liveVideoPoint, lp);
//        final View contentView = findViewById(android.R.id.content);
//        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (videoView.getWidth() <= 0) {
//                    return;
//                }
//                boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
//                //logger.i( "setVideoWidthAndHeight:isLand=" + isLand);
//                if (!isLand) {
//                    return;
//                }
//                videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH,
//                        (int) VIDEO_HEIGHT, VIDEO_RATIO);
//                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//                setFirstParamLand(lp);
//                LiveVideoPoint.initLiveVideoPoint(LectureLiveVideoActivity.this, liveVideoPoint, lp);
//                liveMessageBll.setVideoLayout(lp.width, lp.height);
//                setMediaControllerBottomParam(lp);
////                if (starBll != null) {
////                    starBll.setLayoutParams(lp);
////                }
//            }
//        });
//
//    }
//
//    protected boolean initData() {
//        Intent intent = getIntent();
//        mVSectionID = intent.getStringExtra("vSectionID");
//        mVideoType = MobEnumUtil.VIDEO_LIVE;
//        if (TextUtils.isEmpty(mVSectionID)) {
//            Toast.makeText(this, "直播场次不存在", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        int from = intent.getIntExtra(ENTER_ROOM_FROM, 0);
//        XesMobAgent.enterLiveRoomFrom(from);
//        if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE || liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {// 直播
//            mLiveBll = new LiveBll(this, mVSectionID, liveType, from);
//        } else if (liveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {// 辅导
//            mLiveBll = new LiveBll(this, mVSectionID, intent.getStringExtra("currentDutyId"), liveType, from);
//        } else {
//            Toast.makeText(this, "直播类型不支持", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        LogToFile.liveBll = mLiveBll;
//        mPlayStatistics = mLiveBll.getVideoListener();
//        mLiveBll.setQuestionAction(questionBll);
//        mLiveBll.setRollCallAction(rollCallBll);
//        //mLiveBll.setReadPackageBll(redPackageBll);
//        mLiveBll.setLecLearnReportAction(learnReportBll);
//        mLiveBll.setVideoAction(this);
//        mLiveBll.setRoomAction(liveMessageBll);
//        mLiveBll.setH5CoursewareAction(h5CoursewareBll);
//        mLiveBll.setLecAdvertAction(lecAdvertAction);
//        mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), true);
//        setMediaControllerBottomParam(videoView.getLayoutParams());
//        ProxUtil.getProxUtil().put(this, ActivityChangeLand.class, this);
//        return true;
//    }
//
//    @Override
//    protected void onVideoCreateEnd() {
//        mLiveBll.setLivePlayLog(livePlayLog);
//        mLiveBll.getInfo(null);
//    }
//
//    /**
//     * 控制栏下面距离视频底部的尺寸
//     */
//    private void setMediaControllerBottomParam(ViewGroup.LayoutParams lp) {
//        //控制栏下面距离视频底部的尺寸
//        BaseLiveMediaControllerBottom baseLiveMediaControllerBottom = liveMessageBll.getLiveMediaControllerBottom();
//        int topGap = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());
//        //logger.e( "setMediaControllerBottomParam:paddingBottom=" + paddingBottom + "," +
//        // liveMediaControllerBottom.getPaddingBottom());
//        if (baseLiveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
//            baseLiveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        changeLandAndPort();
////        if (starBll != null) {
////            starBll.initView(questionContent);
////            starBll.onConfigurationChanged(mIsLand);
////        }
//        if (mIsLand) {
//            if (mPopupWindows != null) {
//                mPopupWindows = null;
//            }
//            if (LiveVideoConfig.MORE_COURSE > 0) {
//                showPopupwindow();
//            }
//        } else {
//            if (mPopupWindows != null && mPopupWindows.isShowing()) {
//                mPopupWindows.dismiss();
//            }
//
//        }
//
//    }
//
//    /**
//     * 切换试题区位置
//     */
//    private void changeLandAndPort() {
//        ViewGroup group = (ViewGroup) questionContent.getParent();
//        if (mIsLand) {
//            if (group != rlContent) {
//                //设置控制
//                ViewGroup controllerContent = (ViewGroup) findViewById(R.id.rl_course_video_live_controller_content);
//                controllerContent.removeAllViews();
//                if (mMediaController != null) {
//                    mMediaController.setControllerBottom(null, false);
//                }
//                mMediaController = new LiveMediaController(LectureLiveVideoActivity.this, LectureLiveVideoActivity
//                        .this);
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                controllerContent.addView(mMediaController, params);
//                mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), true);
//                BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
//                mMediaController.setControllerTop(baseLiveMediaControllerTop);
//                controllerContent.addView(baseLiveMediaControllerTop);
//                mMediaController.setAutoOrientation(true);
//                liveMessageBll.getLiveMediaControllerBottom().setController(mMediaController);
//                if (mGetInfo != null) {
//                    mMediaController.setFileName(mGetInfo.getName());
//                }
//                setMediaControllerBottomParam(videoView.getLayoutParams());
//                // 换位置
//                group.removeView(questionContent);
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
//                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                rlContent.addView(questionContent, lp);
//                questionContent.removeAllViews();
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
//                mMediaController.show();
//            }
//            group.post(new Runnable() {
//                @Override
//                public void run() {
//                    ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//                    setFirstParamLand(lp);
//                }
//            });
//        } else {
//            ViewGroup content = (ViewGroup) findViewById(R.id.rl_course_video_contentview);
//            if (group != content) {
//                //设置控制
//                ViewGroup controllerContent = (ViewGroup) findViewById(R.id.rl_course_video_live_controller_content);
//                controllerContent.removeAllViews();
//                if (mMediaController != null) {
//                    mMediaController.setControllerBottom(null, false);
//                }
//                mMediaController = new LiveMediaController(LectureLiveVideoActivity.this, LectureLiveVideoActivity
//                        .this);
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                controllerContent.addView(mMediaController, params);
//                mMediaController.setControllerBottom(liveMessageBll.getLiveMediaControllerBottom(), true);
//                BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
//                mMediaController.setControllerTop(baseLiveMediaControllerTop);
//                controllerContent.addView(baseLiveMediaControllerTop);
//                mMediaController.setAutoOrientation(true);
//                liveMessageBll.getLiveMediaControllerBottom().setController(mMediaController);
//                if (mGetInfo != null) {
//                    mMediaController.setFileName(mGetInfo.getName());
//                }
//                setMediaControllerBottomParam(videoView.getLayoutParams());
//                // 换位置
//                group.removeView(questionContent);
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
//                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
//                content.addView(questionContent, lp);
//                questionContent.removeAllViews();
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
//                mMediaController.show();
//            }
//            group.post(new Runnable() {
//                @Override
//                public void run() {
//                    setFirstParamPort();
//                }
//            });
//            if (mPopupWindows != null && mPopupWindows.isShowing()) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPopupWindows.dismiss();
//                    }
//                });
//
//            }
//
//
//        }
//    }
//
//    /**
//     * 设置蓝屏界面
//     */
//    private void setFirstParamLand(ViewGroup.LayoutParams lp) {
//        final View contentView = findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
//        int rightMargin = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (screenWidth - lp.width) / 2);
//        int topMargin = (ScreenUtils.getScreenHeight() - lp.height) / 2;
//        if (params.rightMargin != rightMargin || params.bottomMargin != topMargin) {
//            params.rightMargin = rightMargin;
//            params.bottomMargin = params.topMargin = topMargin;
//            rlFirstBackgroundView.setLayoutParams(params);
//            ivTeacherNotpresent.setLayoutParams(params);
//            ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//        }
//        //logger.e( "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
//    }
//
//
//    private void setFirstParamPort() {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
//        if (params.rightMargin != RelativeLayout.LayoutParams.MATCH_PARENT || params.bottomMargin != RelativeLayout.LayoutParams.MATCH_PARENT) {
//            params.rightMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
//            params.bottomMargin = RelativeLayout.LayoutParams.MATCH_PARENT;
//            rlFirstBackgroundView.setLayoutParams(params);
//            ivTeacherNotpresent.setLayoutParams(params);
//            ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//        }
//        //logger.e( "setFirstParamLand:screenWidth=" + screenWidth + ",width=" + lp.width + "," + lp.height + "," + rightMargin);
//    }
//
//    @Override
//    protected void onPlayOpenStart() {
//        setFirstBackgroundVisible(View.VISIBLE);
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
//        if (mIsLand && LiveVideoConfig.MORE_COURSE > 0) {
//            showPopupwindow();
//        }
//    }
//
//    @Override
//    protected void onPlayOpenSuccess() {
//        TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
//        if (tvFail != null) {
//            tvFail.setVisibility(View.INVISIBLE);
//        }
//        setFirstBackgroundVisible(View.GONE);
//        rollCallBll.onPlayOpenSuccess(videoView.getLayoutParams());
//    }
//
//    private void showPopupwindow() {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mFloatView = inflater.inflate(R.layout.livemessage_jumpboard, null);
//        mPopupWindows = new PopupWindow(mFloatView, 360, 90, false);
//        mPopupWindows.setOutsideTouchable(false);
//        mPopupWindows.showAtLocation(mFloatView, Gravity.BOTTOM | Gravity.LEFT, ScreenUtils.getScreenWidth() - 420, 160);
//        // 03.29 横竖屏的切换
//        mFloatView.findViewById(R.id.switch_orientation).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //判断当前屏幕方向
//                changeLOrP();
//                LiveVideoConfig.isloading = true;
//            }
//        });
//        TextView totalnum = (TextView) mFloatView.findViewById(R.id.tv_apply_totalnum);
//        totalnum.setText(LiveVideoConfig.MORE_COURSE + "");
//
//    }
//
//    private void createRealVideo(String courseId, String classId) {
//        boolean isPermission = XesPermission.applyFloatWindow(this);
//        //有对应权限或者系统版本小于7.0
//        if (isPermission || Build.VERSION.SDK_INT < 24) {
//            mParent = (ViewGroup) videoView.getParent();
//            if (mParent != null) {
//                mParent.removeView(videoView);
//            }
//            //开启悬浮窗
//            OtherModulesEnter.intentToOrderConfirmActivity(this, courseId + "-" + classId, 100, "LectureLiveVideoActivity");
//            FloatWindowManager.addView(this, videoView, 1);
//            picinpic = true;
//        }
//
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mIsResume = true;
//        if (mHaveStop) {
//            mHaveStop = false;
//            if (!onPauseNotStopVideo) {
//                setFirstBackgroundVisible(View.VISIBLE);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    rePlay();
//                                }
//                            });
//                        }
//                    }
//                }.start();
//            }
//            onPauseNotStopVideo = false;
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mIsResume = false;
//        mHaveStop = true;
//        if (!onPauseNotStopVideo) {
//            new Thread() {
//                @Override
//                public void run() {
//                    synchronized (mIjkLock) {
//                        if (isInitialized()) {
//                            mHandler.removeCallbacks(mPlayDuration);
//                            vPlayer.releaseSurface();
//                            vPlayer.stop();
//                        }
//                        isPlay = false;
//                    }
//                }
//            }.start();
//        }
//    }
//
//    @Override
//    public boolean isResume() {
//        return mIsResume;
//    }
//
//    @Override
//    protected void resultFailed(final int arg1, final int arg2) {
//        postDelayedIfNotFinish(new Runnable() {
//
//            @Override
//            public void run() {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            onFail(arg1, arg2);
//                        }
//                    }
//                }.start();
//            }
//        }, 1200);
//    }
//
//    @Override
//    protected void playComplete() {
//        postDelayedIfNotFinish(new Runnable() {
//
//            @Override
//            public void run() {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        synchronized (mIjkLock) {
//                            onFail(0, 0);
//                        }
//                    }
//                }.start();
//            }
//        }, 200);
//    }
//
//    @Override
//    protected void onPlayError() {
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                tvLoadingHint.setText("您的手机暂时不支持播放直播");
//                findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//
//    @Override
//    public void onTitleShow(boolean show) {
//        liveMessageBll.onTitleShow(show);
//    }
//
//    protected VPlayerListener getWrapListener() {
//        return mPlayListener;
//    }
//
//    private VPlayerListener mPlayListener = new SimpleVPlayerListener() {
//
//        @Override
//        public void onPlaybackComplete() {
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mHandler.removeCallbacks(mPlayDuration);
//            mPlayStatistics.onPlaybackComplete();
//            mLogtf.d("onPlaybackComplete");
//        }
//
//        @Override
//        public void onPlayError() {
//            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mHandler.removeCallbacks(mPlayDuration);
//            mPlayStatistics.onPlayError();
//        }
//
//        @Override
//        public void onOpenSuccess() {
//            isPlay = true;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mPlayStatistics.onOpenSuccess();
//            mHandler.removeCallbacks(mPlayDuration);
//            mHandler.postDelayed(mPlayDuration, mPlayDurTime);
//            mHandler.removeCallbacks(getVideoCachedDurationRun);
//            mHandler.postDelayed(getVideoCachedDurationRun, 10000);
//        }
//
//        @Override
//        public void onOpenStart() {
//            mLogtf.d("onOpenStart");
//            openStartTime = System.currentTimeMillis();
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            postDelayedIfNotFinish(mOpenTimeOutRun, mOpenTimeOut);
//            mPlayStatistics.onOpenStart();
//        }
//
//        @Override
//        public void onOpenFailed(int arg1, int arg2) {
//            isPlay = false;
//            mHandler.removeCallbacks(mOpenTimeOutRun);
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mPlayStatistics.onOpenFailed(arg1, arg2);
//            mLogtf.d("onOpenFailed");
//            if (lastPlayserverEntity != null) {
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "fail reconnect");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//        }
//
//        @Override
//        public void onBufferStart() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            postDelayedIfNotFinish(mBufferTimeOutRun, mBufferTimeout);
//            mPlayStatistics.onBufferStart();
//            mLogtf.d("onBufferStart");
//        }
//
//        @Override
//        public void onBufferComplete() {
//            mHandler.removeCallbacks(mBufferTimeOutRun);
//            mPlayStatistics.onBufferComplete();
//            mLogtf.d("onBufferComplete");
//        }
//    };
//
//
//    /**
//     * 得到Video缓存时间
//     */
//    private Runnable getVideoCachedDurationRun = new Runnable() {
//        @Override
//        public void run() {
//            mHandler.removeCallbacks(this);
//            if (isPlay && !isFinishing()) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        videoCachedDuration = vPlayer.getVideoCachedDuration();
//                        questionBll.setVideoCachedDuration(videoCachedDuration);
//                        mHandler.postDelayed(getVideoCachedDurationRun, 30000);
//                        mLiveBll.getOnloadLogs(TAG, "videoCachedDuration=" + videoCachedDuration);
//                        if (videoCachedDuration > 10000) {
//                            mLiveBll.streamReport(LiveBll.MegId.MEGID_12130, mGetInfo.getChannelname(), -1);
//                            if (lastPlayserverEntity != null) {
//                                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "play delay reconnect");
//                                reportPlayStarTime = System.currentTimeMillis();
//                            }
//                        }
//                    }
//                }.start();
//                //logger.i( "onOpenSuccess:videoCachedDuration=" + videoCachedDuration);
//            }
//        }
//    };
//
//    /**
//     * 缓冲超时
//     */
//    private Runnable mBufferTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            mLogtf.d("bufferTimeOut:progress=" + vPlayer.getBufferProgress());
//            long openTime = System.currentTimeMillis() - openStartTime;
//            if (openTime > 40000) {
//                mLiveBll.streamReport(LiveBll.MegId.MEGID_12107, mGetInfo.getChannelname(), openTime);
//            } else {
//                mLiveBll.streamReport(LiveBll.MegId.MEGID_12137, mGetInfo.getChannelname(), openTime);
//            }
//            if (lastPlayserverEntity != null) {
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "buffer empty reconnect");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//            mLiveBll.repair(true);
//            mLiveBll.liveGetPlayServer(false);
//        }
//    };
//
//    /** 播放时长，7分钟统计 */
//    private Runnable mPlayDuration = new Runnable() {
//        @Override
//        public void run() {
//            if (lastPlayserverEntity != null) {
//                mLiveBll.live_report_play_duration(mGetInfo.getChannelname(), System.currentTimeMillis() - reportPlayStarTime, lastPlayserverEntity, "normal");
//                reportPlayStarTime = System.currentTimeMillis();
//            }
//            if (isPlay && !isFinishing()) {
//                mHandler.postDelayed(this, mPlayDurTime);
//            }
//        }
//    };
//
//    /**
//     * 打开超时
//     */
//    private Runnable mOpenTimeOutRun = new Runnable() {
//
//        @Override
//        public void run() {
//            long openTimeOut = System.currentTimeMillis() - openStartTime;
//            mLogtf.d("openTimeOut:progress=" + vPlayer.getBufferProgress() + ",openTimeOut=" + openTimeOut);
//            mLiveBll.repair(false);
//            mLiveBll.liveGetPlayServer(false);
//        }
//    };
//
//    @Override
//    public void onTeacherNotPresent(final boolean isBefore) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
////                setFirstBackgroundVisible(View.VISIBLE);
////                String text;
////                if (isBefore) {
////                    text = "老师还未进入直播间，请稍后再来";
////                } else {
////                    text = "你来晚了，下课了，等着看回放吧";
////                }
////                final String msg = text;
////                if (tvLoadingHint != null) {
////                    tvLoadingHint.setText(msg);
////                }
//                int visibility = rlFirstBackgroundView.getVisibility();
//                mLogtf.d("onTeacherNotPresent:First=" + visibility);
//                if (rlFirstBackgroundView.getVisibility() == View.GONE) {
//                    ivTeacherNotpresent.setVisibility(View.GONE);
//                } else {
//                    ivTeacherNotpresent.setVisibility(View.VISIBLE);
//                    if (isLandSpace()) {
//                        ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//                    } else {
//                        ivTeacherNotpresent.setBackgroundResource(R.drawable.livevideo_zw_dengdaida_bg_normal);
//                    }
//                    findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onTeacherQuit(final boolean isQuit) {//老师离线，暂时不用
//
//    }
//
//    @Override
//    public void onLiveInit(LiveGetInfo getInfo) {
//        mGetInfo = getInfo;
//        mMediaController.setFileName(getInfo.getName());
//        if (getInfo.isCloseChat()) {
//            liveMessageBll.closeChat(true);
//        }
//        liveMessageBll.setLiveGetInfo(getInfo);
//        //rollCallBll.onLiveInit(liveType, getInfo);
//        questionBll.setLiveGetInfo(getInfo);
////        if (AppConfig.DEBUG) {
////            LecAdvertEntity lecAdvertEntity = new LecAdvertEntity();
////            lecAdvertAction.start(lecAdvertEntity);
////        }
//    }
//
//    @Override
//    public void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange) {
//        mServer = server;
//        mLiveTopic = cacheData;
//        questionBll.setLiveTopic(cacheData);
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                if (tvLoadingHint != null) {
//                    if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic()
//                            .getMode())) {
//                        tvLoadingHint.setText(mainTeacherLoad);
//                    } else {
//                        tvLoadingHint.setText(coachTeacherLoad);
//                    }
//                }
//            }
//        });
//        rePlay();
//    }
//
//    @Override
//    public void onModeChange(final String mode, final boolean isPresent) {
//        mLogtf.i("onModeChange:mode=" + mode);
//        try {
//            liveMessageBll.onModeChange(mode, isPresent);
//            rollCallBll.onModeChange(mode, isPresent);
//        } catch (Exception e) {
//            mLogtf.e("onModeChange:mode=" + mode, e);
//        }
//        mLogtf.i("onModeChange:mode=" + mode);
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                mLogtf.d("onModeChange:isInitialized=" + isInitialized());
//                if (isInitialized()) {
//                    mHandler.removeCallbacks(mPlayDuration);
//                    vPlayer.releaseSurface();
//                    vPlayer.stop();
//                }
//                isPlay = false;
//                setFirstBackgroundVisible(View.VISIBLE);
//                if (isPresent) {
//                    if (tvLoadingHint != null) {
//                        if (LiveTopic.MODE_CLASS.endsWith(mode)) {
//                            tvLoadingHint.setText(mainTeacherLoad);
//                        } else {
//                            tvLoadingHint.setText(coachTeacherLoad);
//                        }
//                    }
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void changeLOrP() {
//        if (mIsAutoOrientation) {
//            super.changeLOrP();
//        }
//    }
//
//    @Override
//    public void onClassTimoOut() {
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "你来晚了，下课了，等着看回放吧";
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
//    @Override
//    public void onLiveDontAllow(final String msg) {
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//        XESToastUtils.showToast(this, "将在3秒内退出");
//        postDelayedIfNotFinish(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent();
//                intent.putExtra("msg", msg);
//                setResult(LIVE_USER_ERROR, intent);
//                finish();
//            }
//        }, 3000);
//    }
//
//    @Override
//    public void onLiveError(final ResponseEntity responseEntity) {
//        mLogtf.d("onLiveError");
//        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.INVISIBLE);
//        final String msg = "" + responseEntity.getErrorMsg();
//        if (tvLoadingHint != null) {
//            tvLoadingHint.setText(msg);
//        }
//    }
//
//    /**
//     * 第一次播放，或者播放失败，重新播放
//     */
//    private void rePlay() {
//        if (mGetInfo == null) {//上次初始化尚未完成
//            return;
//        }
//        livePlayLog.onReplay();
//        new Thread() {
//            @Override
//            public void run() {
//                boolean isPresent = mLiveBll.isPresent();
//                if (isPresent) {
//                    mHandler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (tvLoadingHint != null) {
//                                if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo
//                                        .getLiveTopic().getMode())) {
//                                    tvLoadingHint.setText(mainTeacherLoad);
//                                } else {
//                                    tvLoadingHint.setText(coachTeacherLoad);
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        }.start();
//        String url;
//        String msg = "rePlay:";
//        if (mServer == null) {
//            String rtmpUrl = null;
//            String[] rtmpUrls = mGetInfo.getRtmpUrls();
//            if (rtmpUrls != null) {
//                rtmpUrl = rtmpUrls[(lastIndex++) % rtmpUrls.length];
//            }
//            if (rtmpUrl == null) {
//                rtmpUrl = mGetInfo.getRtmpUrl();
//            }
//            url = rtmpUrl + "/" + mGetInfo.getChannelname();
//            msg += "mServer=null";
//            mLiveBll.setPlayserverEntity(null);
//            livePlayLog.setLastPlayserverEntity(null);
//        } else {
//            List<PlayserverEntity> playservers = mServer.getPlayserver();
//            msg += "playservers=" + playservers.size();
//            PlayserverEntity entity = null;
//            boolean useFlv = false;
//            if (lastPlayserverEntity == null) {
//                msg += ",lastPlayserverEntity=null";
//                entity = playservers.get(0);
//            } else {
//                msg += ",failPlayserverEntity=" + failPlayserverEntity.size();
//                if (!failPlayserverEntity.isEmpty()) {
//                    boolean allRtmpFail = true;
//                    boolean allFlvFail = true;
//                    List<PlayserverEntity> flvPlayservers = new ArrayList<>();
//                    for (int i = 0; i < playservers.size(); i++) {
//                        PlayserverEntity playserverEntity = playservers.get(i);
//                        if (!StringUtils.isEmpty(playserverEntity.getFlvpostfix())) {
//                            flvPlayservers.add(playserverEntity);
//                            if (!failFlvPlayserverEntity.contains(playserverEntity)) {
//                                allFlvFail = false;
//                            }
//                        }
//                        if (!failPlayserverEntity.contains(playserverEntity)) {
//                            allRtmpFail = false;
//                        }
//                    }
//                    if (allFlvFail) {
//                        msg += ",allFlvFail";
//                        failPlayserverEntity.clear();
//                        failFlvPlayserverEntity.clear();
//                    } else {
//                        if (allRtmpFail) {
//                            if (flvPlayservers.isEmpty()) {
//                                failPlayserverEntity.clear();
//                            } else {
//                                if (!lastPlayserverEntity.isUseFlv()) {
//                                    entity = flvPlayservers.get(0);
//                                    entity.setUseFlv(true);
//                                    useFlv = true;
//                                    msg += ",setUseFlv1";
//                                } else {
//                                    for (int i = 0; i < flvPlayservers.size(); i++) {
//                                        PlayserverEntity playserverEntity = flvPlayservers.get(i);
//                                        if (lastPlayserverEntity.getAddress().equals(playserverEntity.getAddress())) {
//                                            entity = flvPlayservers.get((i + 1) % flvPlayservers.size());
//                                            entity.setUseFlv(true);
//                                            useFlv = true;
//                                            msg += ",setUseFlv2";
//                                            break;
//                                        }
//                                    }
//                                    if (entity == null) {
//                                        msg += ",entity=null1";
//                                        entity = flvPlayservers.get(0);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                if (entity == null) {
//                    for (int i = 0; i < playservers.size(); i++) {
//                        PlayserverEntity playserverEntity = playservers.get(i);
//                        if (lastPlayserverEntity.equals(playserverEntity)) {
//                            entity = playservers.get((i + 1) % playservers.size());
//                            msg += ",entity=null2";
//                            break;
//                        }
//                    }
//                }
//                if (entity == null) {
//                    msg += ",entity=null3";
//                    entity = playservers.get(0);
//                }
//            }
//            lastPlayserverEntity = entity;
//            mLiveBll.setPlayserverEntity(entity);
//            livePlayLog.setLastPlayserverEntity(entity);
//            if (useFlv) {
//                url = "http://" + entity.getAddress() + ":" + entity.getHttpport() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname() + entity.getFlvpostfix();
//            } else {
//                if (StringUtils.isEmpty(entity.getIp_gslb_addr())) {
//                    url = "rtmp://" + entity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
//                } else {
//                    final PlayserverEntity finalEntity = entity;
//                    mLiveBll.dns_resolve_stream(entity, mServer, mGetInfo.getChannelname(), new AbstractBusinessDataCallBack() {
//                        @Override
//                        public void onDataSucess(Object... objData) {
//                            if (finalEntity != lastPlayserverEntity) {
//                                return;
//                            }
//                            String provide = (String) objData[0];
//                            String url;
//                            if ("wangsu".equals(provide)) {
//                                url = objData[1] + "&username=" + mGetInfo.getUname() + "&cfrom=android";
//                                playNewVideo(Uri.parse(url), mGetInfo.getName());
//                            } else if ("ali".equals(provide)) {
//                                url = (String) objData[1];
//                                StringBuilder stringBuilder = new StringBuilder(url);
//                                addBody("Sucess", stringBuilder);
//                                url = stringBuilder + "&username=" + mGetInfo.getUname();
//                                playNewVideo(Uri.parse(url), mGetInfo.getName());
//                            } else {
//                                return;
//                            }
//                            StableLogHashMap stableLogHashMap = new StableLogHashMap("glsb3rdDnsReply");
//                            stableLogHashMap.put("message", "" + url);
//                            stableLogHashMap.put("activity", mContext.getClass().getSimpleName());
//                            Loger.e(mContext, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData(), true);
//                        }
//
//                        @Override
//                        public void onDataFail(int errStatus, String failMsg) {
//                            if (finalEntity != lastPlayserverEntity) {
//                                return;
//                            }
//                            String url = "rtmp://" + finalEntity.getAddress() + "/" + mServer.getAppname() + "/" + mGetInfo.getChannelname();
//                            StringBuilder stringBuilder = new StringBuilder(url);
//                            addBody("Fail", stringBuilder);
//                            playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
//                        }
//                    });
//                    return;
//                }
//            }
//            msg += ",entity=" + entity.getIcode();
//        }
//        StringBuilder stringBuilder = new StringBuilder(url);
//        msg += addBody("rePlay", stringBuilder);
//        msg += ",url=" + stringBuilder;
//        mLogtf.d(msg);
//        playNewVideo(Uri.parse(stringBuilder.toString()), mGetInfo.getName());
//    }
//
//    /**
//     * 直播地址的一些通用参数
//     *
//     * @param method
//     * @param url
//     * @return
//     */
//    protected String addBody(String method, StringBuilder url) {
//        String msg = "";
//        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode())) {
//            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
//                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
//                msg += ",t1";
//            } else {
//                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayT())) {
//                    url.append("?" + mGetInfo.getSkeyPlayT() + "&cfrom=android");
//                    msg += ",t2";
//                } else {
//                    url.append("?cfrom=android");
//                    msg += ",t3";
//                }
//            }
//        } else {
//            if (lastPlayserverEntity != null && !StringUtils.isSpace(lastPlayserverEntity.getRtmpkey())) {
//                url.append("?" + lastPlayserverEntity.getRtmpkey() + "&cfrom=android");
//                msg += ",f1";
//            } else {
//                if (!StringUtils.isSpace(mGetInfo.getSkeyPlayF())) {
//                    url.append("?" + mGetInfo.getSkeyPlayF() + "&cfrom=android");
//                    msg += ",f2";
//                } else {
//                    url.append("?cfrom=android");
//                    msg += ",f3";
//                }
//            }
//        }
//        logger.d( "addBody:method=" + method + ",url=" + url);
//        return msg;
//    }
//
//    @Override
//    public void onPlayError(int errorCode, PlayErrorCode playErrorCode) {
//
//    }
//
//    /**
//     * 播放失败，或者完成时调用
//     */
//    private void onFail(int arg1, final int arg2) {
//        if (lastPlayserverEntity != null) {
//            if (lastPlayserverEntity.isUseFlv()) {
//                if (!failFlvPlayserverEntity.contains(lastPlayserverEntity)) {
//                    failFlvPlayserverEntity.add(lastPlayserverEntity);
//                }
//            } else {
//                if (!failPlayserverEntity.contains(lastPlayserverEntity)) {
//                    failPlayserverEntity.add(lastPlayserverEntity);
//                }
//            }
//        }
//        mHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                if (tvLoadingHint != null) {
//                    String errorMsg = null;
//                    AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
//                    if (error != null) {
//                        errorMsg = error.getNum() + " (" + error.getTag() + ")";
//                    }
//                    TextView tvFail = (TextView) findViewById(R.id.tv_course_video_loading_fail);
//                    if (errorMsg != null) {
//                        if (tvFail != null) {
//                            tvFail.setVisibility(View.VISIBLE);
//                            tvFail.setText(errorMsg);
//                        }
//                    } else {
//                        if (tvFail != null) {
//                            tvFail.setVisibility(View.INVISIBLE);
//                        }
//                    }
//                    mLogtf.d("onFail:arg2=" + arg2 + ",errorMsg=" + errorMsg + ",isPresent=" + mLiveBll.isPresent());
//                    if (mLiveBll.isPresent()) {
//                        if (liveType != LiveVideoConfig.LIVE_TYPE_LIVE || LiveTopic.MODE_CLASS.endsWith(mGetInfo.getLiveTopic
//                                ().getMode())) {
//                            tvLoadingHint.setText(mainTeacherLoad);
//                        } else {
//                            tvLoadingHint.setText(coachTeacherLoad);
//                        }
//                    }
//                    RoomStatusEntity status = mGetInfo.getLiveTopic().getMainRoomstatus();
//                    if (status != null) {
//                        mLogtf.d("onFail:classbegin=" + status.isClassbegin());
//                    }
//                }
//            }
//        });
//        mLiveBll.liveGetPlayServer(false);
//    }
//
//    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
//        if (isFinishing()) {
//            return;
//        }
//        mHandler.postDelayed(r, delayMillis);
//    }
//
//    public void setFirstBackgroundVisible(int visible) {
//        rlFirstBackgroundView.setVisibility(visible);
//        if (visible == View.GONE) {
//            ivTeacherNotpresent.setVisibility(View.GONE);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent event) {
//        logger.i( "onEvent:netWorkType=" + event.netWorkType);
//        mLiveBll.onNetWorkChange(event.netWorkType);
//    }
//
//    /**
//     * 只在WIFI下使用激活
//     *
//     * @param onlyWIFIEvent
//     * @author zouhao
//     * @Create at: 2015-9-24 下午1:57:04
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {
//        Toast.makeText(this, "没有wifi", Toast.LENGTH_SHORT).show();
//        onUserBackPressed();
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(MiniEvent event) {
//        if ("Order".equals(event.getMin())) {
//            final String courseId = event.getCourseId();
//            final String classId = event.getClassId();
//            if (mIsLand) {
//                changeLOrP();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        createRealVideo(courseId, classId);
//                    }
//                }, 500);
//            } else {
//                createRealVideo(event.getCourseId(), event.getClassId());
//            }
//            onPauseNotStopVideo = true;
//            // 添加点击立即报名的日志
//            StableLogHashMap logHashMap = new StableLogHashMap("clickEnroll");
//            logHashMap.put("adsid", "" + event.getAdId());
//            logHashMap.addSno("5").addStable("2");
//            logHashMap.put("extra", "点击了立即报名");
//            mLiveBll.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
//            LiveVideoConfig.LECTUREADID = event.getAdId();
//        }
//        if ("Invisible".equals(event.getMin())) {
//            if (mPopupWindows != null && mPopupWindows.isShowing()) {
//                mPopupWindows.dismiss();
//            }
//        }
//        if ("ConfirmClick".equals(event.getMin())) {
//            // 添加用户点击提交订单日志
//            StableLogHashMap logHashMap = new StableLogHashMap("clickSubmitOrder");
//            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
//            logHashMap.addSno("6").addStable("2");
//            logHashMap.put("extra", "点击了立即支付");
//            mLiveBll.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
//        }
//        if ("OrderPaySuccess".equals(event.getMin())) {
//            // 添加用户购买成功的日志
//            StableLogHashMap logHashMap = new StableLogHashMap("purchaseSucceed");
//            logHashMap.put("adsid", "" + LiveVideoConfig.LECTUREADID);
//            logHashMap.addSno("7").addStable("2");
//            logHashMap.put("orderid", event.getCourseId());
//            logHashMap.put("extra", "用户支付成功");
//            mLiveBll.umsAgentDebugSys(LiveVideoConfig.LEC_ADS, logHashMap.getData());
//        }
//        if ("Advertisement".equals(event.getMin())) {
//            // 收到广告指令就弹出面板抽屉
//            if (mIsLand && LiveVideoConfig.MORE_COURSE > 0) {
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mPopupWindows != null) {
//                            mPopupWindows.dismiss();
//                            mPopupWindows = null;
//                        }
//                        showPopupwindow();
//                    }
//                }, 1000);
//            }
//        }
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ViewGroup parents = (ViewGroup) videoView.getParent();
//        if (parents != null) {
//            parents.removeView(videoView);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            mParent.addView(videoView, params);
//        }
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (picinpic) {
//            ViewGroup parents = (ViewGroup) videoView.getParent();
//            if (parents != null) {
//                parents.removeView(videoView);
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                mParent.addView(videoView, params);
//            }
//            picinpic = !picinpic;
//        }
//
//    }
//
//
//    /**
//     * 是否显示移动网络提示
//     */
//    private boolean mIsShowMobileAlert = true;
//
//    /**
//     * 开启了3G/4G提醒
//     *
//     * @param event
//     * @author zouhao
//     * @Create at: 2015-10-12 下午1:49:22
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.NowMobileEvent event) {
//        if (mIsShowMobileAlert) {
//            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(this, mBaseApplication, false,
//                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
//            cancelDialog.setCancelBtnListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onUserBackPressed();
//                }
//            });
//            cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？")
//                    .showDialog();
//            mIsShowMobileAlert = false;
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent.OnGetGoldUpdateEvent event) {
//        liveMessageBll.onGetMyGoldDataEvent(event.goldNum);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (null != this.getCurrentFocus()) {
//            /** 点击空白位置 隐藏软键盘 */
//            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onUserBackPressed() {
//        if (questionBll.onBack()) {
//
//        } else {
//            if (liveMessageBll.onBack()) {
//
//            } else if (h5CoursewareBll.onBack()) {
//
//            } else {
//                super.onUserBackPressed();
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        isPlay = false;
//        if (mLogtf != null) {
//            mLogtf.d("onDestroy");
//        }
//        liveMessageBll.onDestroy();
//        new Thread() {
//            @Override
//            public void run() {
//                if (mLiveBll != null) {
//                    mLiveBll.onDestroy();
//                }
//                ProxUtil.getProxUtil().clear(LectureLiveVideoActivity.this);
//            }
//        }.start();
//        AppBll.getInstance().unRegisterAppEvent(this);
//        super.onDestroy();
//        //关闭悬浮窗
//        FloatWindowManager.hide();
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     * @param requestCode
//     */
//    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
//        Intent intent = new Intent(context, LectureLiveVideoActivity.class);
//        intent.putExtras(bundle);
//        context.startActivityForResult(intent, requestCode);
//    }
//
//    @Override
//    public void onMsgUrlClick(String url) {
////        onPauseNotStopVideo = true;
//    }
//
//    @Override
//    protected void updateIcon() {
//        updateLoadingImage();
//        updateRefreshImage();
//    }
//
//    protected void updateLoadingImage() {
//        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
//        if (footerIconEntity != null) {
//            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
//            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
//                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
//            }
//        }
//    }
//}
