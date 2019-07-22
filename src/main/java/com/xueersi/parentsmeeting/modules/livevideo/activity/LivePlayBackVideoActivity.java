//package com.xueersi.parentsmeeting.modules.livevideo.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup.LayoutParams;
//import android.view.ViewTreeObserver;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.entity.FooterIconEntity;
//import com.xueersi.common.event.AppEvent;
//import com.xueersi.common.logerhelper.MobEnumUtil;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
//import com.xueersi.lib.analytics.umsagent.UmsConstants;
//import com.xueersi.lib.framework.utils.NetWorkHelper;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
//import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
//import com.xueersi.parentsmeeting.module.videoplayer.media.VideoViewActivity;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.fragment.MediaControllerAction;
//import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
//import com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business.LecAdvertPlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NBH5PlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5PlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionPlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackagePlayBackBll;
//import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
//import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.util.List;
//
////import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
//
///**
// * 直播回放播放页
// *
// * @author Hua
// */
//@SuppressLint("HandlerLeak")
//@SuppressWarnings("unchecked")
//public class LivePlayBackVideoActivity extends VideoViewActivity implements LivePlaybackMediaController.OnPointClick, ActivityChangeLand, MediaControllerAction {
//    String TAG = "LivePlayBackVideoActivityLog";
//
//    {
//        /** 布局默认资源 */
//        mLayoutVideo = R.layout.activity_live_back_video;
//    }
//
//    private RelativeLayout rl_course_video_live_controller_content;
//    LivePlaybackMediaController mPlayBackMediaController;
//    /** 互动题的布局 */
//    private RelativeLayout rlQuestionContent;
//    /** 更多课程广告的布局 */
//    private RelativeLayout rlAdvanceContent;
//    /** 初始进入播放器时的预加载界面 */
//    private RelativeLayout rlFirstBackgroundView;
//    /** 是不是播放失败 */
//    boolean resultFailed = false;
//    /** 当前是否正在显示互动题 */
//    private boolean mIsShowQuestion = false;
//    /** 当前是否正在显示红包 */
//    private boolean mIsShowRedpacket = false;
//    /** 当前是否正在显示对话框 */
//    private boolean mIsShowDialog = false;
//    /** 是不是点击返回键或者点周围,取消互动题,而没有使用getPopupWindow */
//    boolean mIsBackDismiss = true;
//    /** 视频节对象 */
//    VideoLivePlayBackEntity mVideoEntity;
//    String beforeAttach;
//    /** 是否显示移动网络提示 */
//    private boolean mIsShowMobileAlert = true;
//    /** 是否显示无网络提示 */
//    private boolean mIsShowNoWifiAlert = true;
//    /** 我的课程业务层 */
//    LectureLivePlayBackBll lectureLivePlayBackBll;
//    /** 互动题 */
//    private VideoQuestionEntity mQuestionEntity;
//    /** 讲座购课广告的页面 */
//    private LecAdvertPager lecAdvertPager;
//    /** onPause状态不暂停视频 */
//    PauseNotStopVideoIml pauseNotStopVideoIml;
//    /** 播放时长定时任务(心跳) */
//    private final long mPlayTime = 60000;
//
//    /** 播放路径名 */
//    private String mWebPath;
//    /** 节名称 */
//    private String mSectionName;
//    /** 显示互动题 */
//    private static final int SHOW_QUESTION = 0;
//    /** 没有互动题 */
//    private static final int NO_QUESTION = 1;
//    boolean pausePlay = false;
//    /** 加载视频提示 */
//    private ImageView ivLoading;
//    private TextView tvLoadingContent;
//    /** 从哪个页面跳转 */
//    String where;
//    int isArts;
//    /** 区分文理appid */
//    String appID = UmsConstants.LIVE_APP_ID_BACK;
//    private LiveVideoSAConfig liveVideoSAConfig;
//    boolean IS_SCIENCE;
//    /** 本地视频 */
//    boolean islocal;
//    static int times = -1;
//    long createTime;
//    private LiveRemarkBll mLiveRemarkBll;
//    private RelativeLayout bottom;
//    private View mFloatView;
//    private PopupWindow mPopupWindows;
//    private Handler mHandler;
//    private int progress = 0;
//    LiveBackBll liveBackBll;
//
//    @Override
//    protected void onVideoCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onVideoCreate(savedInstanceState);
//        times++;
//        createTime = System.currentTimeMillis();
//        AppBll.getInstance().registerAppEvent(this);
//        // 设置不可自动横竖屏
//        setAutoOrientation(false);
//        Intent intent = getIntent();
//        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
//        islocal = intent.getBooleanExtra("islocal", false);
//        mHandler = new Handler();
//        // 加载互动题和视频列表
//        initView();
//        // 请求相应数据
//        initData();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
//        for (LiveBackBaseBll businessBll : businessBlls) {
//            businessBll.onConfigurationChanged(newConfig);
//        }
//    }
//
//    /** 初始化互动题和竖屏时下方的列表布局 */
//    @Override
//    public void attachMediaController() {
//        logger.d("attachMediaController:beforeAttach=" + beforeAttach);
//        if (resultFailed) {
//            logger.d("attachMediaController:resultFailed");
//            return;
//        }
//        if (mMediaController != null) {
////            mMediaController.setWindowLayoutType();
//            mMediaController.release();
//        }
//        if (mLiveRemarkBll != null) {
//            mLiveRemarkBll.hideMarkPoints();
//        }
//
//        LivePlaybackMediaController mPlayBackMediaController = new LivePlaybackMediaController(this, this, mIsLand.get());
//        this.mMediaController = mPlayBackMediaController;
//        rl_course_video_live_controller_content.removeAllViews();
//        rl_course_video_live_controller_content.addView(mMediaController, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        if (mLiveRemarkBll == null || mVideoEntity.getIsAllowMarkpoint() != 1) {
//            mMediaController.getTitleRightBtn().setVisibility(View.GONE);
//        } else {
//            mMediaController.getTitleRightBtn().setVisibility(View.VISIBLE);
//            mMediaController.getTitleRightBtn().setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mLiveRemarkBll.setController(mMediaController);
//                    mLiveRemarkBll.showMarkPoints();
//                }
//            });
//        }
//        // 设置播放器横竖屏切换按钮不显示
//        mMediaController.setAutoOrientation(false);
//        // 播放下一个按钮不显示
//        mMediaController.setPlayNextVisable(false);
//        // 设置速度按钮显示
//        mMediaController.setSetSpeedVisable(true);
//
//        // 设置当前是否为横屏
//        if (mPlayBackMediaController == null) {
//
//        } else {
////            mPlayBackMediaController.onAttach(mIsLand.get());
////            rl_course_video_live_controller_content.removeAllViews();
////            rl_course_video_live_controller_content.addView(mMediaController, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        }
//        if (mIsLand.get()) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_course_video_live_controller_content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
//            rl_course_video_live_controller_content.setLayoutParams(lp);
//        } else {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_course_video_live_controller_content.getLayoutParams();
//            lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rl_course_video_content);
//            rl_course_video_live_controller_content.setLayoutParams(lp);
//        }
//        setFileName(); // 设置视频显示名称
//        if (liveBackBll.isShowQuestion()) {
//            mMediaController.release();
//            logger.d("attachMediaController:release:mIsShowQuestion=" + mIsShowQuestion + "," + mIsShowRedpacket
//                    + "," + mIsShowDialog);
//        } else {
//            showLongMediaController();
//        }
//        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
//            return;
//        }
//        if (mVideoEntity.getIsAllowMarkpoint() != 1) {
//            mPlayBackMediaController.setVideoQuestions("playback" + mVideoEntity.getvLivePlayBackType() + "-", lstVideoQuestion,
//                    vPlayer.getDuration());
//        }
//    }
//
//    @Override
//    public void release() {
//        if (mMediaController != null) {
//            mMediaController.release();
//        }
//    }
//
//    @Override
//    protected void showRefresyLayout(int arg1, int arg2) {
//        super.showRefresyLayout(arg1, arg2);
//        TextView errorInfo = (TextView) videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id.tv_course_video_errorinfo);
//        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
//        if (error != null) {
//            errorInfo.setVisibility(View.VISIBLE);
//            String videoKey = getVideoKey();
//            if (error == AvformatOpenInputError.HTTP_NOT_FOUND) {
//                errorInfo.setText("(" + videoKey + ")" + " 回放未生成");
//            }
//        }
//        if (rlQuestionContent != null) {
//            if (lecAdvertPager == null) {
//                rlQuestionContent.setVisibility(View.GONE);
//            }
////            if (subjectResultPager != null) {
////                for (int i = 0; i < rlQuestionContent.getChildCount(); i++) {
////                    View child = rlQuestionContent.getChildAt(0);
////                    if (child != subjectResultPager.getRootView()) {
////                        rlQuestionContent.removeViewAt(i);
////                        i--;
////                    }
////                }
////            } else {
////                rlQuestionContent.removeAllViews();
////            }
//        }
//    }
//
//    /** 加载旋转屏时相关布局 */
//    @Override
//    protected void loadLandOrPortView() {
//        mPortVideoHeight = VideoBll.getVideoDefaultHeight(this);
//        super.loadLandOrPortView();
//    }
//
//    private void initView() {
//        // 预加载布局
//        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
//        bottom = (RelativeLayout) findViewById(R.id.live_play_back_bottom);
//        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
//        updateLoadingImage();
//        tvLoadingContent = (TextView) findViewById(R.id.tv_course_video_loading_content);
//        // 预加载布局中退出事件
//        ImageView ivBack = (ImageView) findViewById(R.id.iv_course_video_back);
//
//        if (ivBack != null) {
//            findViewById(R.id.iv_course_video_back).setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    LivePlayBackVideoActivity.this.onBackPressed();
//                }
//            });
//        }
//        rl_course_video_live_controller_content = findViewById(R.id.rl_course_video_live_controller_content);
//        // 加载横屏时互动题的列表布局
//        rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
//        // 加载竖屏时显示更多课程广告的布局
//        rlAdvanceContent = (RelativeLayout) findViewById(R.id.rl_livevideo_playback);
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        rlQuestionContent.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
////            @Override
////            public void onChildViewAdded(View parent, View child) {
////                logger.d( "onChildViewAdded");
////            }
////
////            @Override
////            public void onChildViewRemoved(View parent, View child) {
////                logger.d( "onChildViewRemoved");
////            }
////        });
//    }
//
//    /** 竖屏时填充视频列表布局 */
//    protected void initData() {
//        stuCourId = mVideoEntity.getStuCourseId();
//        lectureLivePlayBackBll = new LectureLivePlayBackBll(LivePlayBackVideoActivity.this, stuCourId);
//        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
//        where = getIntent().getStringExtra("where");
//        isArts = getIntent().getIntExtra("isArts", 0);
//        if (isArts == 1) {
//            appID = UmsConstants.ARTS_APP_ID_BACK;
//            IS_SCIENCE = false;
//            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
//        } else if (isArts == 2) {
//            appID = UmsConstants.ARTS_APP_ID_BACK;
//            IS_SCIENCE = false;
//            liveVideoSAConfig = new LiveVideoSAConfig(LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST);
//        } else {
//            appID = UmsConstants.LIVE_APP_ID_BACK;
//            IS_SCIENCE = true;
//            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
//        }
//        lectureLivePlayBackBll.setLiveVideoSAConfig(liveVideoSAConfig);
//        // 如果加载不出来
//        if (tvLoadingContent != null) {
//            tvLoadingContent.setText("正在获取视频资源，请稍候");
//        }
//        // 设置播放进度
//        setmLastVideoPositionKey(mVideoEntity.getVideoCacheKey());
//        // mCourseBll.getQuestionLivePlay(section);
//        // 视频名
//        mSectionName = mVideoEntity.getPlayVideoName();
//        // 统计视频播放key
//        mVisitTimeKey = mVideoEntity.getVisitTimeKey();
//        // 播放器统计时长发送间隔
//        if (isArts == 1) {
//            setmSendPlayVideoTime(LiveVideoConfig.LIVE_HB_TIME);
//        } else {
//            setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());
//        }
//        // 播放视频
//        mWebPath = mVideoEntity.getVideoPath();
//        ProxUtil.getProxUtil().put(this, MediaControllerAction.class, this);
//        liveBackBll = new LiveBackBll(this, mVideoEntity);
//        liveBackBll.setStuCourId(stuCourId);
//        liveBackBll.setvPlayer(vPlayer);
////        if (CourseInfoLiveActivity.isTest) {
////            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
////        }
////        if (AppConfig.DEBUG) {
////            mWebPath = "http://r01.xesimg.com/stream/tmp/2016/11/30/1480481513276687694567.mp4";
////        }
//        if (mVideoEntity != null && mVideoEntity.getIsAllowMarkpoint() == 1) {
//            LiveRemarkBll liveRemarkBll = new LiveRemarkBll(this, vPlayer);
//            liveRemarkBll.setBottom(bottom);
//            liveRemarkBll.setHttpManager(new LiveHttpManager(mContext));
//            liveRemarkBll.setList(mVideoEntity.getLstPoint());
//            liveRemarkBll.setLiveId(mVideoEntity.getLiveId());
//            //mLiveRemarkBll.showBtMark();
//            liveRemarkBll.getMarkPoints(mVideoEntity.getLiveId(), new AbstractBusinessDataCallBack() {
//                @Override
//                public void onDataSucess(Object... objData) {
//                    if (mMediaController != null) {
//                        mMediaController.getTitleRightBtn().setVisibility(View.VISIBLE);
//                        mMediaController.getTitleRightBtn().setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mLiveRemarkBll.setController(mMediaController);
//                                mLiveRemarkBll.showMarkPoints();
//                            }
//                        });
//                    }
//                }
//            });
//            mLiveRemarkBll = liveRemarkBll;
//            liveRemarkBll.setCallBack(new AbstractBusinessDataCallBack() {
//                @Override
//                public void onDataSucess(Object... objData) {
//                    attachMediaController();
//                }
//            });
//        }
//        ProxUtil.getProxUtil().put(this, BackMediaPlayerControl.class, this);
//        ProxUtil.getProxUtil().put(this, ActivityChangeLand.class, this);
//        liveBackBll.addBusinessShareParam("videoView", videoView);
//        pauseNotStopVideoIml = new PauseNotStopVideoIml(this, onPauseNotStopVideo);
//        addBusiness(this);
//        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
//        for (LiveBackBaseBll businessBll : businessBlls) {
//            businessBll.initViewF(null, rlQuestionContent, mIsLand);
//        }
//        if (islocal) {
//            // 互动题播放地址
//            playNewVideo(Uri.parse(mWebPath), mSectionName);
//        } else {
//            getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
//                    .OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
//                    if (AppBll.getInstance(LivePlayBackVideoActivity.this).isNetWorkAlert()) {
//                        // 互动题播放地址
//                        AppBll.getInstance(mBaseApplication);
//                        playNewVideo(Uri.parse(mWebPath), mSectionName);
//                    } else {
//                        mIsShowNoWifiAlert = false;
//                        AppBll.getInstance(mBaseApplication);
//                    }
//                    return false;
//                }
//            });
////            if (AppConfig.DEBUG) {
////                List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
////                VideoQuestionEntity videoQuestionEntity = new VideoQuestionEntity();
////                videoQuestionEntity.setvQuestionType("39804");
////                videoQuestionEntity.setvCategory(LocalCourseConfig.CATEGORY_LEC_ADVERT);
////                videoQuestionEntity.setvQuestionInsretTime(600);
////                videoQuestionEntity.setvEndTime(1600);
////                lstVideoQuestion.add(videoQuestionEntity);
////            }
//            //测试红包自动关闭
////            rlFirstBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener
//// () {
////                @Override
////                public boolean onPreDraw() {
////                    rlFirstBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
////                    initRedPacketResult(10);
////                    return false;
////                }
////            });
//            //测试试卷
////            mQuestionEntity = new VideoQuestionEntity();
////            mQuestionEntity.setvQuestionID("2");
////            mQuestionEntity.setvEndTime(120);
////            showExam();
//        }
//
//        ProxUtil.getProxUtil().put(this, ActivityChangeLand.class, this);
//    }
//
//    private void addBusiness(Activity activity) {
//        liveBackBll.addBusinessBll(new QuestionPlayBackBll(activity, liveBackBll));
//        RedPackagePlayBackBll redPackagePlayBackBll = new RedPackagePlayBackBll(activity, liveBackBll);
//        liveBackBll.addBusinessBll(redPackagePlayBackBll);
//        liveBackBll.addBusinessBll(new EnglishH5PlayBackBll(activity, liveBackBll));
//        liveBackBll.addBusinessBll(new NBH5PlayBackBll(activity, liveBackBll));
//        liveBackBll.addBusinessBll(new LecAdvertPlayBackBll(activity, liveBackBll));
//        liveBackBll.onCreate();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isInitialized() && pausePlay) {
//            vPlayer.pause();
//        }
//    }
//
//    @Override
//    public void pause() {
//        super.pause();
//        pausePlay = true;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected boolean shouldSendPlayVideo() {
//        if (mIsShowQuestion) {
//            return true;
//        }
//        return super.shouldSendPlayVideo();
//    }
//
//    @Override
//    protected void onPlayOpenStart() {
//        if (rlFirstBackgroundView != null) {
//            rlFirstBackgroundView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onPlayOpenSuccess() {
//        if (rlFirstBackgroundView != null) {
//            rlFirstBackgroundView.setVisibility(View.GONE);
//        }
//        if (mQuestionEntity != null) {
//            logger.d("onPlayOpenSuccess:showQuestion:isAnswered=" + mQuestionEntity.isAnswered() + "," +
//                    "mIsShowQuestion=" + mIsShowQuestion);
////            showQuestion(mQuestionEntity);
//        }
//    }
//
//    @Override
//    public void setSpeed(float speed) {
//        super.setSpeed(speed);
//        String key = "null";
//        if (mVideoEntity != null) {
//            if ("LivePlayBackActivity".equals(where)) {//直播辅导
//                key = where + ":playback2," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity.getCourseId()
//                        + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
//            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
//                key = where + ":playback3," + mVideoEntity.getLiveId();
//            } else {
//                if (islocal) {
//                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
//                        key = where + ":playback4," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
//                    } else {//直播课下载
//                        key = where + ":playback5," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
//                    }
//                } else {
//                    key = where + ":playback6," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity
//                            .getCourseId() + "-" + mVideoEntity.getLiveId();
//                }
//            }
//        }
//        UmsAgentManager.umsAgentStatistics(mContext, LiveVideoConfig.LIVE_VIDEO_PLAYBACK_SPEED,
//                "times=" + times + ",time=" + (System.currentTimeMillis() - createTime) + ",speed=" + speed + ",key="
//                        + key);
//    }
//
//    @Override
//    protected void resultFailed(int arg1, int arg2) {
//        super.resultFailed(arg1, arg2);
//        resultFailed = true;
//        mIsShowQuestion = mIsShowRedpacket = false;
//        logger.d("resultFailed:arg2=" + arg2);
//        if (arg2 != 0 && mVideoEntity != null) {
//            if ("LivePlayBackActivity".equals(where)) {//直播辅导
//                XesMobAgent.onOpenFail(where + ":playback2", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
//                        mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId
//                        (), mWebPath, arg2);
//            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
//                XesMobAgent.onOpenFail(where + ":playback3", mVideoEntity.getLiveId(), mWebPath, arg2);
//            } else {
//                if (islocal) {
//                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
//                        XesMobAgent.onOpenFail(where + ":playback4", mVideoEntity.getCourseId() + "-" + mVideoEntity
//                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
//                    } else {//直播课下载
//                        XesMobAgent.onOpenFail(where + ":playback5", mVideoEntity.getCourseId() + "-" + mVideoEntity
//                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
//                    }
//                } else {
//                    XesMobAgent.onOpenFail(where + ":playback6", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
//                            mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId(), mWebPath, arg2);
//                }
//            }
//        }
//    }
//
//    @Override
//    protected String getVideoKey() {
//        if (!islocal && mVideoEntity != null) {
//            if ("LivePlayBackActivity".equals(where)) {
//                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
//            } else if ("PublicLiveDetailActivity".equals(where)) {
//                return mVideoEntity.getLiveId();
//            } else {
//                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
//            }
//        }
//        return super.getVideoKey();
//    }
//
//    @Override
//    protected void sendPlayVideo() {
//        if (isArts == 1) {
//            // 如果观看视频时间等于或大于统计数则发送
//            if (mPlayVideoTime >= mSendPlayVideoTime) {
//                String liveId = mVideoEntity.getLiveId();
//                // 发送观看视频时间
//                lectureLivePlayBackBll.sendLiveCourseVisitTime(stuCourId, liveId, mSendPlayVideoTime, sendPlayVideoHandler, 1000);
//                // 时长初始化
//                mPlayVideoTime = 0;
//            }
//        } else {
//            super.sendPlayVideo();
//        }
//    }
//
//    /** 视频播放进度实时获取 */
//    @Override
//    protected void playingPosition(long currentPosition, long duration) {
//        super.playingPosition(currentPosition, duration);
//        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
//            return;
//        }
//        scanQuestion(currentPosition); // 扫描互动题
//    }
//
//    @Override
//    public void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position) {
//        liveBackBll.onOnPointClick(videoQuestionEntity, position);
//    }
//
//    /** 扫描是否有需要弹出的互动题 */
//    public void scanQuestion(long position) {
//        if (!mIsLand.get() || vPlayer == null || !vPlayer.isPlaying()) {
//            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
//            return;
//        }
//        liveBackBll.scanQuestion(position);
//    }
//
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onEvent(AppEvent event) {
//        if (islocal) {
//            return;
//        }
//        if (event.netWorkType == NetWorkHelper.MOBILE_STATE) {
//            if (AppBll.getInstance().getAppInfoEntity().isNotificationOnlyWIFI()) {
//                EventBus.getDefault().post(new AppEvent.OnlyWIFIEvent());
//            } else if (AppBll.getInstance().getAppInfoEntity().isNotificationMobileAlert()) {
//                EventBus.getDefault().post(new AppEvent.NowMobileEvent());
//            }
//        } else if (event.netWorkType == NetWorkHelper.WIFI_STATE) {
//            if (!mIsShowNoWifiAlert) {
//                mIsShowNoWifiAlert = true;
//                playNewVideo(Uri.parse(mWebPath), mSectionName);
//            }
//        }
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
//        stopShowRefresyLayout();
//    }
//
//    /**
//     * 开启了3G/4G提醒
//     *
//     * @param event
//     * @author zouhao
//     * @Create at: 2015-10-12 下午1:49:22
//     */
//    @Subscribe(threadMode = ThreadMode.POSTING)
//    public void onNowMobileEvent(AppEvent.NowMobileEvent event) {
//        if (mIsShowMobileAlert) {
//            mIsShowMobileAlert = false;
//            boolean pause = false;
//            final boolean initialized = isInitialized();
//            if (initialized) {
//                if (vPlayer.isPlaying()) {
//                    vPlayer.pause();
//                    pause = true;
//                }
//            }
//            final boolean finalPause = pause;
//            logger.i("onNowMobileEvent:initialized=" + initialized + ",pause=" + pause);
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(LivePlayBackVideoActivity
//                            .this, mBaseApplication, false,
//                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
//                    cancelDialog.setCancelBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            onUserBackPressed();
//                        }
//                    });
//                    cancelDialog.setVerifyBtnListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            logger.i("onNowMobileEvent:onClick:initialized=" + initialized + ",finalPause=" +
//                                    finalPause);
//                            if (initialized) {
//                                if (finalPause) {
//                                    if (vPlayer != null) {
//                                        vPlayer.start();
//                                    }
//                                }
//                            } else {
//                                if (StringUtils.isSpace(mWebPath)) {
//                                    XESToastUtils.showToast(LivePlayBackVideoActivity.this, "视频资源错误，请您尝试重新播放课程");
//                                    onUserBackPressed();
//                                } else {
//                                    playNewVideo(Uri.parse(mWebPath), mSectionName);
//                                }
//                            }
//                        }
//                    });
//                    cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo
//                            ("您当前使用的是3G/4G网络，是否继续观看？",
//                                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onUserBackPressed() {
//        boolean userBackPressed = liveBackBll.onUserBackPressed();
//        if (!userBackPressed) {
//            super.onUserBackPressed();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        AppBll.getInstance().unRegisterAppEvent(this);
//        super.onDestroy();
//        liveBackBll.onDestroy();
//        ProxUtil.getProxUtil().clear(this);
//    }
//
//    @Override
//    protected void resultComplete() {
//        // 没有广告，播放完毕直接退出
//        if (lecAdvertPager == null) {
//            onUserBackPressed();
//        }
//    }
//
//    @Override
//    protected void onRefresh() {
//        resultFailed = false;
//        if (AppBll.getInstance(this).isNetWorkAlert()) {
//            videoBackgroundRefresh.setVisibility(View.GONE);
//            logger.d("onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
//            if (rlQuestionContent.getChildCount() > 0) {
//                rlQuestionContent.setVisibility(View.VISIBLE);
//            }
//            playNewVideo(Uri.parse(mWebPath), mSectionName);
//        }
////        if (AppBll.getInstance(this).isNetWorkAlert()) {
////            loadView(mLayoutVideo);
////            initView();
////            initData();
////        }
//        AppBll.getInstance(mBaseApplication);
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     */
//    public static void intentTo(Activity context, Bundle bundle, String where) {
//        intentTo(context, bundle, where, VIDEO_REQUEST);
//    }
//
//    /**
//     * 跳转到播放器
//     *
//     * @param context
//     * @param bundle
//     * @param requestCode
//     */
//    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
//        Intent intent = new Intent(context, LivePlayBackVideoActivity.class);
//        intent.putExtras(bundle);
//        intent.putExtra("where", where);
//        context.startActivityForResult(intent, requestCode);
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
//            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl))
//                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
//        }
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        liveBackBll.onNewIntent(intent);
//    }
//
//    /** 重新打开播放器的监听 */
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        liveBackBll.onRestart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        liveBackBll.onStop();
//    }
//
//    @Override
//    public void setRequestedOrientation(int requestedOrientation) {
//        logger.d("setRequestedOrientation:requestedOrientation=" + requestedOrientation);
//        super.setRequestedOrientation(requestedOrientation);
//    }
//
//    @Override
//    public void setAutoOrientation(boolean isAutoOrientation) {
//        super.setAutoOrientation(isAutoOrientation);
//    }
//}
