package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.MediaControllerAction;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.learnfeedback.ExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.ExperienceLearnFeedbackPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5PlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackagePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveBackVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.mediacontroller.StandLiveVideoExperienceMediaController;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

//测试提交
public class StandLiveVideoExperienceFragment extends LiveBackVideoFragmentBase implements MediaControllerAction,
        ActivityChangeLand {
    {
        /** 布局默认资源 */
        mLayoutVideo = R.layout.frag_live_back_video;
    }

    private String TAG = getClass().getSimpleName();
    private RelativeLayout rl_course_video_live_controller_content;
    /** 互动题的布局下层（垂直方向的下层） */
    private RelativeLayout rlQuestionContentBottom;
    /** 互动题的布局上层（垂直方向的上层，防止被后面的view遮挡住） */
    private RelativeLayout rlQuestionContent;
    /** 更多课程广告的布局 */
    private RelativeLayout rlAdvanceContent;
    /** 初始进入播放器时的预加载界面 */
    private RelativeLayout rlFirstBackgroundView;
    /** 是不是播放失败 */
    boolean resultFailed = false;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    String beforeAttach;
    /** 是否显示移动网络提示 */
    private boolean mIsShowMobileAlert = true;
    /** 是否显示无网络提示 */
    private boolean mIsShowNoWifiAlert = true;
    /** 我的课程业务层 */
    LectureLivePlayBackBll lectureLivePlayBackBll;
    /** onPause状态不暂停视频 */
    PauseNotStopVideoIml pauseNotStopVideoIml;

    /** 播放路径名 */
    private String mWebPath;
    /** 节名称 */
    private String mSectionName;
    /** 加载视频提示 */
    private ImageView ivLoading;
    private TextView tvLoadingContent;
    /** 从哪个页面跳转 */
    String where;
    int isArts;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID_BACK;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE;
    /** 本地视频 */
    boolean islocal;
    static int times = -1;
    long createTime;
    private RelativeLayout bottom;
    private View mFloatView;
    private PopupWindow mPopupWindows;
    private Handler mHandler;
    private int progress = 0;
    protected LiveBackBll liveBackBll;
    protected LiveBackVideoBll liveBackVideoBll;
    /*** 全屏显示*/
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;
    /** 预加载 */
    LiveStandFrameAnim liveStandFrameAnim;
    /**
     * 存放
     */
//    private static final String experience = "experience";
//    /**
//     *
//     */
//    private Boolean isExperience;

    /**
     * 学习反馈窗口
     */
    public static StandLiveVideoExperienceFragment newInstance(boolean isExperience) {
//        Bundle arguments = new Bundle();
//        arguments.putBoolean(experience, isExperience);
//        StandLiveVideoExperienceFragment standLiveVideoExperienceFragment = ;
//        standLiveVideoExperienceFragment.setArguments(arguments);
        return new StandLiveVideoExperienceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
//        Bundle bundle = getArguments();
//        isExperience = bundle.getBoolean(experience, true);
        initView();
        return mContentView;
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        bottom = mContentView.findViewById(R.id.live_play_back_bottom);
        ivLoading = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingContent = mContentView.findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        ImageView ivBack = mContentView.findViewById(R.id.iv_course_video_back);

        if (ivBack != null) {
            mContentView.findViewById(R.id.iv_course_video_back).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
        rl_course_video_live_controller_content = mContentView.findViewById(R.id
                .rl_course_video_live_controller_content);
        // 加载横屏时互动题的列表布局
        rlQuestionContentBottom = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_question_bottom);
        rlQuestionContent = (RelativeLayout) mContentView.findViewById(R.id.rl_course_video_live_question_content);
        // 加载竖屏时显示更多课程广告的布局
        rlAdvanceContent = (RelativeLayout) mContentView.findViewById(R.id.rl_livevideo_playback);
    }

    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = ShareDataManager.getInstance().getCacheEntity(FooterIconEntity.class,
                false, ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(activity).load(loadingNoClickUrl).placeHolder(R.drawable
                        .livevideo_cy_moren_logo_normal).error(R.drawable.livevideo_cy_moren_logo_normal).into
                        (ivLoading);
            }
        }
    }

    /**
     * 在LiveBackVidoeFragmentBase中的内部类LiveOnVideoCreate的方法onVideoCreate中调用onSelect,然后调用这个方法onVideoCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onVideoCreate(Bundle savedInstanceState) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        super.onVideoCreate(savedInstanceState);
        times++;
        createTime = System.currentTimeMillis();
        AppBll.getInstance().registerAppEvent(this);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = activity.getIntent();
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);
        mHandler = new Handler();
        // 请求相应数据
        initData();
        preLoadView();
//        addOnGlobalLayoutListener();
    }

    /** 竖屏时填充视频列表布局 */
    protected void initData() {
        stuCourId = mVideoEntity.getStuCourseId();
        lectureLivePlayBackBll = new LectureLivePlayBackBll(activity, stuCourId);
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        Intent intent = activity.getIntent();
        where = intent.getStringExtra("where");
        isArts = intent.getIntExtra("isArts", 0);

        liveBackVideoBll = new LiveBackVideoBll(activity);
        liveBackVideoBll.setVideoEntity(mVideoEntity);
        liveBackVideoBll.setLiveBackPlayVideoFragment(liveBackPlayVideoFragment);
        liveBackVideoBll.setvPlayer(vPlayer);
        liveBackVideoBll.setSectionName(mSectionName);
        if (isArts == 1) {
            appID = UmsConstants.ARTS_APP_ID_BACK;
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            appID = UmsConstants.LIVE_APP_ID_BACK;
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        lectureLivePlayBackBll.setLiveVideoSAConfig(liveVideoSAConfig);
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 设置播放进度
        setmLastVideoPositionKey(mVideoEntity.getVideoCacheKey());
        // mCourseBll.getQuestionLivePlay(section);
        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 统计视频播放key
        mVisitTimeKey = mVideoEntity.getVisitTimeKey();
        // 播放器统计时长发送间隔
        if (isArts == 1) {
            setmSendPlayVideoTime(LiveVideoConfig.LIVE_HB_TIME);
        } else {
            setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());
        }
        // 播放视频
        mWebPath = mVideoEntity.getVideoPath();
        mDisplayName = mVideoEntity.getPlayVideoName();
        liveBackBll = new LiveBackBll(activity, mVideoEntity);
        liveBackBll.setStuCourId(stuCourId);
        liveBackBll.setvPlayer(vPlayer);

    }


    /**
     * 预加载动画
     */
    private void preLoadView() {
        liveStandFrameAnim = new LiveStandFrameAnim(activity);
        liveStandFrameAnim.check(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                View vsLiveStandUpdate = mContentView.findViewById(R.id.vs_live_stand_update);
                if (vsLiveStandUpdate != null) {
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                } else {
                    vsLiveStandUpdate = mContentView.findViewById(R.id.rl_live_stand_update);
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                }
                Map<String, String> mParams = new HashMap<>();
                mParams.put("logtype", "check_onDataSucess");
                mParams.put("isFinishing", "" + activity.isFinishing());
                Loger.d(activity, TAG, mParams, true);
                if (activity.isFinishing()) {
                    return;
                }
                initBll();
            }
        });
    }


    protected void initBll() {
        ProxUtil.getProxUtil().put(activity, MediaControllerAction.class, this);
        //发布题目的时候，如果点击取消题目，不会跳过这段做题目的时间
        ProxUtil.getProxUtil().put(activity, MediaPlayerControl.class, new MediaPlayerControl() {//zyy:
            @Override
            public void start() {
                liveBackPlayVideoFragment.start();
            }

            @Override
            public void pause() {
                liveBackPlayVideoFragment.pause();
            }

            @Override
            public void stop() {
                liveBackPlayVideoFragment.stop();
            }

            @Override
            public void seekTo(long pos) {

            }

            @Override
            public void setSpeed(float speed) {

            }

            @Override
            public float getSpeed() {
                return liveBackPlayVideoFragment.getSpeed();
            }

            @Override
            public void next() {
                liveBackPlayVideoFragment.next();
            }

            @Override
            public boolean isPlaying() {
                return liveBackPlayVideoFragment.isPlaying();
            }

            @Override
            public boolean isLandSpace() {
                return liveBackPlayVideoFragment.isLandSpace();
            }

            @Override
            public boolean isPlayInitialized() {
                return liveBackPlayVideoFragment.isPlayInitialized();
            }

            @Override
            public void changeLOrP() {
                liveBackPlayVideoFragment.changeLOrP();
            }

            @Override
            public long getCurrentPosition() {
                return liveBackPlayVideoFragment.getCurrentPosition();
            }

            @Override
            public long getDuration() {
                return liveBackPlayVideoFragment.getDuration();
            }

            @Override
            public int getBufferPercentage() {
                return liveBackPlayVideoFragment.getBufferPercentage();
            }

            @Override
            public void toggleVideoMode(int mode) {
                liveBackPlayVideoFragment.toggleVideoMode(mode);
            }

            @Override
            public void removeLoadingView() {
                liveBackPlayVideoFragment.removeLoadingView();
            }

            @Override
            public float scale(float scale) {
                return liveBackPlayVideoFragment.scale(scale);
            }

            @Override
            public int getVideoHeight() {
                return liveBackPlayVideoFragment.getVideoHeight();
            }

            @Override
            public void onShare() {
                liveBackPlayVideoFragment.onShare();
            }
        });
        ProxUtil.getProxUtil().put(activity, ActivityChangeLand.class, this);
        initBusiness();
        if (islocal) {
            // 互动题播放地址
            playNewVideo();
        } else {
            activity.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    activity.getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    if (AppBll.getInstance(activity).isNetWorkAlert()) {
                        // 互动题播放地址
                        AppBll.getInstance(activity.getApplication());
                        playNewVideo();
                    } else {
                        mIsShowNoWifiAlert = false;
                        AppBll.getInstance(activity.getApplication());
                    }
                    return false;
                }
            });
        }

    }


    protected void initBusiness() {
        liveBackBll.addBusinessShareParam("videoView", videoView);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity, onPauseNotStopVideo);
        addBusiness(activity);
        liveBackBll.onCreate();
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.initViewF(rlQuestionContentBottom, rlQuestionContent, mIsLand);
        }
    }

    //添加功能模块
    protected void addBusiness(Activity activity) {
        liveBackBll.addBusinessBll(new QuestionPlayBackBll(activity, liveBackBll));
        RedPackagePlayBackBll redPackagePlayBackBll = new RedPackagePlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(redPackagePlayBackBll);
        liveBackBll.addBusinessBll(new EnglishH5PlayBackBll(activity, liveBackBll));
        //站立直播体验课聊天区的添加
        liveBackBll.addBusinessBll(new StandLiveVideoExperienceBll(activity, liveBackBll));
        //播放完成后的反馈弹窗
        liveBackBll.addBusinessBll(new ExperienceLearnFeedbackBll(activity, liveBackBll));
    }

    @Override
    public void attachMediaController() {
        Loger.d(TAG, "attachMediaController:beforeAttach=" + beforeAttach);
        if (resultFailed) {
            Loger.d(TAG, "attachMediaController:resultFailed");
            return;
        }
        rlQuestionContentBottom.setVisibility(View.VISIBLE);
        rlQuestionContent.setVisibility(View.VISIBLE);
        if (mMediaController != null) {
//            mMediaController.setWindowLayoutType();
            mMediaController.release();
        }
//        StandLiveVideoExperienceMediaController mPlayBackMediaController = createLivePlaybackMediaController();
//        mPlayBackMediaController.setOnPointClick(liveBackBll);
        this.mMediaController = createLivePlaybackMediaController();
        liveBackPlayVideoFragment.setMediaController(mMediaController);
        rl_course_video_live_controller_content.removeAllViews();
        rl_course_video_live_controller_content.addView(mMediaController, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置播放器横竖屏切换按钮不显示
        mMediaController.setAutoOrientation(false);
        // 播放下一个按钮不显示
        mMediaController.setPlayNextVisable(false);
        // 设置速度按钮显示
//        mMediaController.setSetSpeedVisable(true);

        if (mIsLand.get()) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_course_video_live_controller_content
                    .getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
            lp.bottomMargin = 0;
            rl_course_video_live_controller_content.setLayoutParams(lp);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_course_video_live_controller_content
                    .getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rl_course_video_content);
            lp.bottomMargin = 0;
            rl_course_video_live_controller_content.setLayoutParams(lp);
        }
        // 设置视频显示名称
        mMediaController.setFileName(mDisplayName);
        if (liveBackBll.isShowQuestion()) {
            mMediaController.release();
            Loger.d(TAG, "attachMediaController:release:isShowQuestion");
        } else {
            showLongMediaController();
        }
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
//        if (mVideoEntity.getIsAllowMarkpoint() != 1) {
//            mPlayBackMediaController.setVideoQuestions("playback" + mVideoEntity.getvLivePlayBackType() + "-",
//                    lstVideoQuestion, vPlayer.getDuration());
//        }
    }

    protected StandLiveVideoExperienceMediaController createLivePlaybackMediaController() {
        return new StandLiveVideoExperienceMediaController(
                activity,
                liveBackPlayVideoFragment,
                mIsLand.get());
    }

    @Override
    protected void playNewVideo() {
        liveBackVideoBll.playNewVideo();
    }

    @Override
    public void release() {
        if (mMediaController != null) {
            mMediaController.release();
        }
    }

    @Override
    public void setAutoOrientation(boolean isAutoOrientation) {
        super.setAutoOrientation(isAutoOrientation);
    }

    @Override
    public void changeLOrP() {
        liveBackPlayVideoFragment.changeLOrP();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.onConfigurationChanged(newConfig);
        }
    }

    /** 加载旋转屏时相关布局 */
    @Override
    protected void loadLandOrPortView() {
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        super.loadLandOrPortView();
    }

    /** 加载视频异常时出现可重新刷新的背景界面 */
    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);
        TextView errorInfo = videoBackgroundRefresh.findViewById(com.xueersi.parentsmeeting.base.R.id
                .tv_course_video_errorinfo);
        videoBackgroundRefresh.findViewById(R.id.tv_course_video_errortip).setVisibility(View.GONE);
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errorInfo.setVisibility(View.VISIBLE);
            if (error == AvformatOpenInputError.HTTP_NOT_FOUND) {
                errorInfo.setText("回放视频未生成，请重试[" + mVideoEntity.getLiveId() + "]");
            } else {
                PlayErrorCode playErrorCode = PlayErrorCode.getError(arg2);
                errorInfo.setText("视频播放失败 [" + playErrorCode.getCode() + "]");
            }
        }
//        rlQuestionContent.setVisibility(View.GONE);
//        rlQuestionContentBottom.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isInitialized() && pausePlay) {
            vPlayer.pause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onPlayOpenStart() {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPlayOpenSuccess() {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.GONE);
        }
        attachMediaController();
    }

    @Override
    protected void savePosition(long fromStart) {
        liveBackVideoBll.savePosition(fromStart);
    }

    @Override
    protected long getStartPosition() {
        if (mFromStart) {
            return 0;
        }
        return liveBackVideoBll.getStartPosition();
    }

//    @Override
//    public void setSpeed(float speed) {
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
//        UmsAgentManager.umsAgentStatistics(activity, LiveVideoConfig.LIVE_VIDEO_PLAYBACK_SPEED,
//                "times=" + times + ",time=" + (System.currentTimeMillis() - createTime) + ",speed=" + speed + ",key="
//                        + key);
//        liveBackBll.setSpeed(speed);
//    }

    @Override
    protected PlayerService.VPlayerListener getWrapListener() {
        return liveBackVideoBll.getPlayListener();
    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        super.resultFailed(arg1, arg2);
        resultFailed = true;
        Loger.d(TAG, "resultFailed:arg2=" + arg2);
        if (arg2 != 0 && mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {//直播辅导
                XesMobAgent.onOpenFail(where + ":playback2", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
                        mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId
                        (), mWebPath, arg2);
            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
                XesMobAgent.onOpenFail(where + ":playback3", mVideoEntity.getLiveId(), mWebPath, arg2);
            } else {
                if (islocal) {
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
                        XesMobAgent.onOpenFail(where + ":playback4", mVideoEntity.getCourseId() + "-" + mVideoEntity
                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
                    } else {//直播课下载
                        XesMobAgent.onOpenFail(where + ":playback5", mVideoEntity.getCourseId() + "-" + mVideoEntity
                                .getLiveId(), mWebPath + "," + new File(mWebPath).length(), arg2);
                    }
                } else {
                    XesMobAgent.onOpenFail(where + ":playback6", LocalCourseConfig.LIVEPLAYBACK_COURSE + "" +
                            mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId(), mWebPath, arg2);
                }
            }
        }
    }

    @Override
    protected String getVideoKey() {
        if (!islocal && mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {
                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
            } else if ("PublicLiveDetailActivity".equals(where)) {
                return mVideoEntity.getLiveId();
            } else {
                return mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
            }
        }
        return super.getVideoKey();
    }

    @Override
    protected void sendPlayVideo() {
        if (isArts == 1) {
            // 如果观看视频时间等于或大于统计数则发送
            if (mPlayVideoTime >= mSendPlayVideoTime) {
                String liveId = mVideoEntity.getLiveId();
                // 发送观看视频时间
                lectureLivePlayBackBll.sendLiveCourseVisitTime(stuCourId, liveId, mSendPlayVideoTime,
                        sendPlayVideoHandler, 1000);
                // 时长初始化
                mPlayVideoTime = 0;
            }
        } else {
            super.sendPlayVideo();
        }
    }

    /** 视频播放进度实时获取 */
    @Override
    protected void playingPosition(long currentPosition, long duration) {
        super.playingPosition(currentPosition, duration);
        if (NetWorkHelper.getNetWorkState(activity) == NetWorkHelper.NO_NETWORK) {
            return;
        }
        scanQuestion(currentPosition); // 扫描互动题
    }

    /** 扫描是否有需要弹出的互动题 */
    public void scanQuestion(long position) {
        if (!mIsLand.get() || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }
        liveBackBll.scanQuestion(position);
    }

    @Override
    protected void onPausePlayer() {
        super.onPausePlayer();
        liveBackBll.onPausePlayer();
    }

    @Override
    protected void onStartPlayer() {
        super.onStartPlayer();
        liveBackBll.onStartPlayer();
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (event.getClass() == AppEvent.class) {
            if (islocal) {
                return;
            }
            if (event.netWorkType == NetWorkHelper.MOBILE_STATE) {
                if (AppBll.getInstance().getAppInfoEntity().isNotificationOnlyWIFI()) {
                    EventBus.getDefault().post(new AppEvent.OnlyWIFIEvent());
                } else if (AppBll.getInstance().getAppInfoEntity().isNotificationMobileAlert()) {
                    EventBus.getDefault().post(new AppEvent.NowMobileEvent());
                }
            } else if (event.netWorkType == NetWorkHelper.WIFI_STATE) {
                if (!mIsShowNoWifiAlert) {
                    mIsShowNoWifiAlert = true;
                    playNewVideo();
                }
            } else {
                liveBackVideoBll.onNetWorkChange(event.netWorkType);
            }
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
        stopShowRefresyLayout();
    }

    /**
     * 开启了3G/4G提醒
     *
     * @param event
     * @author zouhao
     * @Create at: 2015-10-12 下午1:49:22
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onNowMobileEvent(AppEvent.NowMobileEvent event) {
        if (mIsShowMobileAlert) {
            mIsShowMobileAlert = false;
            boolean pause = false;
            final boolean initialized = isInitialized();
            if (initialized) {
                if (vPlayer.isPlaying()) {
                    vPlayer.pause();
                    pause = true;
                }
            }
            final boolean finalPause = pause;
            Loger.i(TAG, "onNowMobileEvent:initialized=" + initialized + ",pause=" + pause);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, activity
                            .getApplication(), false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onUserBackPressed();
                        }
                    });
                    cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Loger.i(TAG, "onNowMobileEvent:onClick:initialized=" + initialized + ",finalPause=" +
                                    finalPause);
                            if (initialized) {
                                if (finalPause) {
                                    if (vPlayer != null) {
                                        vPlayer.start();
                                    }
                                }
                            } else {
                                if (StringUtils.isSpace(mWebPath)) {
                                    XESToastUtils.showToast(activity, "视频资源错误，请您尝试重新播放课程");
                                    onUserBackPressed();
                                } else {
                                    playNewVideo();
                                }
                            }
                        }
                    });
                    cancelDialog.setCancelShowText("返回课程列表").setVerifyShowText("继续观看").initInfo
                            ("您当前使用的是3G/4G网络，是否继续观看？",
                                    VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
                }
            });
        }
    }


    @Override
    protected void onUserBackPressed() {
        boolean userBackPressed = liveBackBll.onUserBackPressed();
        if (!userBackPressed) {
            super.onUserBackPressed();
        }
    }

    /** 重新打开播放器的监听 */
    public void onRestart() {
        liveBackBll.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();
        liveBackBll.onStop();
    }


    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        liveBackBll.onDestory();
        ProxUtil.getProxUtil().clear(activity);
    }

    /**
     * 视频回放结束
     */
    @Override
    protected void resultComplete() {
        liveBackBll.onLiveBackBaseBllUserBackPressed();
//        onUserBackPressed();
    }

    @Override
    protected void onRefresh() {
        resultFailed = false;
        if (AppBll.getInstance(activity).isNetWorkAlert()) {
            videoBackgroundRefresh.setVisibility(View.GONE);
            Loger.d(TAG, "onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
            playNewVideo();
        }
//        if (AppBll.getInstance(this).isNetWorkAlert()) {
//            loadView(mLayoutVideo);
//            initView();
//            initData();
//        }
        AppBll.getInstance(activity.getApplication());
    }

    protected void updateIcon() {
        updateLoadingImage();
        updateRefreshImage();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        Loger.d(TAG, "setRequestedOrientation:requestedOrientation=" + requestedOrientation);
        super.setRequestedOrientation(requestedOrientation);
    }


}
