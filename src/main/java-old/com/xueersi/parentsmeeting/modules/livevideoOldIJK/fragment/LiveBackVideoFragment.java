package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.business.SpeechBulletScreenPalyBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.EvaluateTeacherPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedbackTeacherLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business.NBH5PlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.ArtsAnswerResultPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5PlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.QuestionPlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.business.RedPackagePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.remark.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.PlayErrorCodeLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.video.LiveBackVideoBll;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
import com.xueersi.ui.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * Created by linyuqiang on 2018/7/23.
 */
public class LiveBackVideoFragment extends LiveBackVideoFragmentBase implements ActivityChangeLand,
        MediaControllerAction {
    String TAG = "LiveBackVideoFragment";

    {
        /** 布局默认资源 */
        mLayoutVideo = R.layout.frag_live_back_video;
    }

    private RelativeLayout rl_course_video_live_controller_content;
    /** 互动题的布局 */
    private RelativeLayout rlQuestionContentBottom;
    /** 互动题的布局 */
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
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE;
    /** 本地视频 */
    boolean islocal;
    static int times = -1;
    long createTime;
    private LiveRemarkBll mLiveRemarkBll;
    private RelativeLayout bottom;
    private View mFloatView;
    private PopupWindow mPopupWindows;
    private int progress = 0;
    protected LiveBackBll liveBackBll;
    protected LiveBackVideoBll liveBackVideoBll;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoMainEntity;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoTutorEntity;
    /** 播放视频类型 主讲，辅导 */
    int videoPlayStatus = MediaPlayer.VIDEO_TEACHER_ONLY_MAIN;

    /**
     * 全屏显示
     */
    protected int mVideoMode = VideoView.VIDEO_LAYOUT_SCALE;

    /** 全身直播 头像*/
    LinearLayout llUserHeadImage;
    /** 全身直播 头像*/
    CircleImageView civUserHeadImage;
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

        mVideoMainEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        mVideoTutorEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoTutorEntity");
        if (mVideoTutorEntity!=null) {
            mVideoTutorEntity.setIsAllowMarkpoint(0);
        }
        videoPlayStatus = intent.getIntExtra("teacherVideoStatus",0);

        islocal = intent.getBooleanExtra("islocal", false);
        startNewVideo();

        addOnGlobalLayoutListener();
    }

    private void startNewVideo() {
        if (mVideoEntity!=null) {
            savePosition(mCurrentPosition);
        }
        if (videoPlayStatus == MediaPlayer.VIDEO_TEACHER_TUTOR || videoPlayStatus == MediaPlayer.VIDEO_TEACHER_ONLY_TUTOR) {
            mVideoEntity = mVideoTutorEntity;

        } else {
            mVideoEntity = mVideoMainEntity;
        }


        if (mVideoEntity == null) {
            CrashReport.postCatchedException(new Exception("" + activity.getIntent().getExtras()));
        }
        // 请求相应数据
        initData();
        initBll();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return mContentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (liveBackBll != null) {
            List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
            for (LiveBackBaseBll businessBll : businessBlls) {
                businessBll.onConfigurationChanged(newConfig);
            }
        }
    }

    /** 初始化互动题和竖屏时下方的列表布局 */
    @Override
    public void attachMediaController() {
        logger.d("attachMediaController:beforeAttach=" + beforeAttach);
        if (resultFailed) {
            logger.d("attachMediaController:resultFailed");
            return;
        }
        rlQuestionContentBottom.setVisibility(View.VISIBLE);
        rlQuestionContent.setVisibility(View.VISIBLE);
        if (mMediaController != null) {
//            mMediaController.setWindowLayoutType();
            mMediaController.release();
        }
        if (mLiveRemarkBll != null) {
            mLiveRemarkBll.hideMarkPoints();
        }
        LivePlaybackMediaController mPlayBackMediaController = createLivePlaybackMediaController();
        mPlayBackMediaController.setOnPointClick(liveBackBll);
        this.mMediaController = mPlayBackMediaController;
        liveBackPlayVideoFragment.setMediaController(mMediaController);
        rl_course_video_live_controller_content.removeAllViews();
        rl_course_video_live_controller_content.addView(mMediaController, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mLiveRemarkBll == null || mVideoEntity.getIsAllowMarkpoint() != 1) {
            mMediaController.getTitleRightBtn().setVisibility(View.GONE);
        } else {
            mMediaController.getTitleRightBtn().setVisibility(View.VISIBLE);
            mMediaController.getTitleRightBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLiveRemarkBll.setController(mMediaController);
                    mLiveRemarkBll.showMarkPoints();
                }
            });
        }
        // 设置播放器横竖屏切换按钮不显示
        mMediaController.setAutoOrientation(false);
        // 播放下一个按钮不显示
        mMediaController.setPlayNextVisable(false);
        // 设置速度按钮显示
        mMediaController.setSetSpeedVisable(true);

        mMediaController.setVideoStatus(MediaPlayer.VIDEO_BOTTOM_CONTROL_CODE_TEACHER, videoPlayStatus, "");


        // 设置当前是否为横屏
        if (mPlayBackMediaController == null) {

        } else {
//            mPlayBackMediaController.onAttach(mIsLand.get());
//            rl_course_video_live_controller_content.removeAllViews();
//            rl_course_video_live_controller_content.addView(mMediaController, new LayoutParams(LayoutParams
// .MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
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
            logger.d("attachMediaController:release:isShowQuestion");
        } else {
            showLongMediaController();
        }
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        if (mVideoEntity.getIsAllowMarkpoint() != 1) {
            mPlayBackMediaController.setVideoQuestions("playback" + mVideoEntity.getvLivePlayBackType() + "-",
                    lstVideoQuestion,
                    vPlayer.getDuration());
        }
    }

    protected LivePlaybackMediaController createLivePlaybackMediaController() {
        LivePlaybackMediaController mPlayBackMediaController = new LivePlaybackMediaController(activity,
                liveBackPlayVideoFragment, mIsLand.get());
        return mPlayBackMediaController;
    }

    @Override
    public void release() {
        if (mMediaController != null) {
            mMediaController.release();
        }
    }

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
                //统计日志
                PlayErrorCodeLog.livePlayError(liveBackBll, playErrorCode);
            }
        }
        rlQuestionContent.setVisibility(View.GONE);
        rlQuestionContentBottom.setVisibility(View.GONE);
    }

    /** 加载旋转屏时相关布局 */
    @Override
    protected void loadLandOrPortView() {
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        super.loadLandOrPortView();
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
        llUserHeadImage = mContentView.findViewById(R.id.ll_livevideo_en_stand_achive_user_head_imge);
        civUserHeadImage = mContentView.findViewById(R.id.iv_livevideo_en_stand_achive_user_head_imge);

    }


    /** 竖屏时填充视频列表布局 */
    protected void initData() {
        stuCourId = mVideoEntity.getStuCourseId();
        lectureLivePlayBackBll = new LectureLivePlayBackBll(activity, stuCourId);
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        Intent intent = activity.getIntent();
        where = intent.getStringExtra("where");
        isArts = intent.getIntExtra("isArts", 0);

        liveBackVideoBll = new LiveBackVideoBll(activity, islocal);
        liveBackVideoBll.setVideoEntity(mVideoEntity);
        liveBackVideoBll.setLiveBackPlayVideoFragment(liveBackPlayVideoFragment);
        liveBackVideoBll.setvPlayer(vPlayer);
        liveBackVideoBll.setSectionName(mSectionName);
        if (isArts == 1) {
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else if (isArts == 2) {
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(LiveVideoConfig.HTTP_PRIMARY_CHINESE_HOST);
        } else {
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        if (mVideoEntity.isMul()) {
            AppConfig.isMulLiveBack = true;
        } else {
            AppConfig.isMulLiveBack = false;
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
        liveBackBll = null;
        liveBackBll = new LiveBackBll(activity, mVideoEntity);
        liveBackBll.setStuCourId(stuCourId);
        liveBackBll.setvPlayer(vPlayer);
    }

    protected void initBll() {
        ProxUtil.getProxUtil().put(activity, MediaControllerAction.class, this);
        ProxUtil.getProxUtil().put(activity, BackMediaPlayerControl.class, liveBackPlayVideoFragment);
        ProxUtil.getProxUtil().put(activity, ActivityChangeLand.class, this);
        ProxUtil.getProxUtil().put(activity, BasePlayerFragment.class, liveBackPlayVideoFragment);
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

    @Override
    protected void playNewVideo() {
        liveBackVideoBll.playNewVideo();
    }

    protected void initBusiness() {
        liveBackBll.addBusinessShareParam("videoView", videoView);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity, onPauseNotStopVideo);
        addBusiness(activity);
        liveBackBll.onCreate();
        long before = System.currentTimeMillis();
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.initViewF(rlQuestionContentBottom, rlQuestionContent, mIsLand);
        }
        logger.d("initBusiness:initViewF:time=" + (System.currentTimeMillis() - before));
    }

    protected void initLiveRemarkBll() {
        // TODO: 2018/12/5  
        if (isArts == 1 || "PublicLiveDetailActivity".equals(where)) {
            return;
        }
        if (mVideoEntity != null && mVideoEntity.getIsAllowMarkpoint() == 1) {
            LiveRemarkBll liveRemarkBll = new LiveRemarkBll(activity, vPlayer);
            liveRemarkBll.setBottom(bottom);
            liveRemarkBll.setHttpManager(new LiveHttpManager(activity));
            liveRemarkBll.setList(mVideoEntity.getLstPoint());
            liveRemarkBll.setLiveId(mVideoEntity.getLiveId());
            //mLiveRemarkBll.showBtMark();
            liveRemarkBll.getMarkPoints(mVideoEntity.getLiveId(), new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    if (mMediaController != null) {
                        mMediaController.getTitleRightBtn().setVisibility(View.VISIBLE);
                        mMediaController.getTitleRightBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLiveRemarkBll.setController(mMediaController);
                                mLiveRemarkBll.showMarkPoints();
                            }
                        });
                    }
                }
            });
            mLiveRemarkBll = liveRemarkBll;
            liveRemarkBll.setCallBack(new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    attachMediaController();
                }
            });
            liveRemarkBll.setOnItemClick(liveBackBll.getOnItemClick());
        }
    }

    //添加功能模块
    protected void addBusiness(Activity activity) {
        liveBackBll.addBusinessBll(new QuestionPlayBackBll(activity, liveBackBll));
        RedPackagePlayBackBll redPackagePlayBackBll = new RedPackagePlayBackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(redPackagePlayBackBll);
        liveBackBll.addBusinessBll(new EnglishH5PlayBackBll(activity, liveBackBll));
        liveBackBll.addBusinessBll(new NBH5PlayBackBll(activity, liveBackBll));
        liveBackBll.addBusinessBll(new SpeechBulletScreenPalyBackBll(activity, liveBackBll));
        //直播
        if (liveBackBll.getLiveType() == LiveVideoConfig.LIVE_TYPE_LIVE) {
            //理科
            if (liveBackBll.getIsArts() == 0) {
                initLiveRemarkBll();
            } else {
                Log.e("LiveBackVideoFragment", "====> initAnswerResultBll");
                liveBackBll.addBusinessBll(new ArtsAnswerResultPlayBackBll(activity, liveBackBll));
                if (liveBackBll.getPattern() != 2) {
                    //回放聊天区加上MMD的皮肤
                    liveBackBll.addBusinessBll(new LiveMessageBackBll(activity, liveBackBll));
                }
            }
            if (!islocal) {
                EvaluateTeacherPlayBackBll evaluateTeacherPlayBackBll = new EvaluateTeacherPlayBackBll(activity,
                        liveBackBll);
                evaluateTeacherPlayBackBll.setLiveFragmentBase(liveBackPlayVideoFragment);
                liveBackBll.addBusinessBll(evaluateTeacherPlayBackBll);


                FeedbackTeacherLiveBackBll feedbackTeacherLiveBackBll = new FeedbackTeacherLiveBackBll(activity,liveBackBll);
                feedbackTeacherLiveBackBll.setLiveFragment(liveBackPlayVideoFragment);
                liveBackBll.addBusinessBll(feedbackTeacherLiveBackBll);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInitialized() && pausePlay) {
            vPlayer.pause();
        }
        if (liveBackVideoBll != null) {
            liveBackVideoBll.onResume();
        }

    }

    @Override
    public void onPause() {
        if (isInitialized()) {
            if (!onPauseNotStopVideo.get()) {
                if (liveBackVideoBll != null) {
                    liveBackVideoBll.onPause(0);
                }
            }
        }
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

    @Override
    protected void seekTo(long pos) {
        super.seekTo(pos);
        liveBackVideoBll.seekTo(pos);
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        String key = "null";
        if (mVideoEntity != null) {
            if ("LivePlayBackActivity".equals(where)) {//直播辅导
                key = where + ":playback2," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity.getCourseId()
                        + "-" + mVideoEntity.getSectionId() + "-" + mVideoEntity.getLiveId();
            } else if ("PublicLiveDetailActivity".equals(where)) {//公开直播
                key = where + ":playback3," + mVideoEntity.getLiveId();
            } else {
                if (islocal) {
                    if (mVideoEntity.getvLivePlayBackType() == LocalCourseConfig.LIVE_PLAY_RECORD) {//直播辅导下载
                        key = where + ":playback4," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
                    } else {//直播课下载
                        key = where + ":playback5," + mVideoEntity.getCourseId() + "-" + mVideoEntity.getLiveId();
                    }
                } else {
                    key = where + ":playback6," + LocalCourseConfig.LIVEPLAYBACK_COURSE + "" + mVideoEntity
                            .getCourseId() + "-" + mVideoEntity.getLiveId();
                }
            }
        }
        UmsAgentManager.umsAgentStatistics(activity, LiveVideoConfig.LIVE_VIDEO_PLAYBACK_SPEED,
                "times=" + times + ",time=" + (System.currentTimeMillis() - createTime) + ",speed=" + speed + ",key="
                        + key);
        liveBackBll.setSpeed(speed);
    }

    @Override
    protected int onVideoStatusChange(int code, int status) {
        UmsAgentManager.umsAgentDebug(activity, "" + code, "status");
        if (code == MediaPlayer.VIDEO_BOTTOM_CONTROL_CODE_TEACHER) {
            videoPlayStatus = status;
            umsTeacherChange();
        }
        startNewVideo();
        return code;
    }

    /**
     * 老师统计
     */
    private void umsTeacherChange(){
        if(videoPlayStatus == MediaPlayer.VIDEO_TEACHER_MAIN && liveBackBll!=null) {
            StableLogHashMap logHashMap = new StableLogHashMap("backup_teacher");
            liveBackBll.umsAgentDebugInter(LogConfig.LIVE_H5PLAT,logHashMap);
        } else if(videoPlayStatus == MediaPlayer.VIDEO_TEACHER_TUTOR && liveBackBll!=null){
            StableLogHashMap logHashMap = new StableLogHashMap("backup_coach");
            liveBackBll.umsAgentDebugInter(LogConfig.LIVE_H5PLAT,logHashMap);
        }

    }

    @Override
    protected VPlayerCallBack.VPlayerListener getWrapListener() {
        return liveBackVideoBll.getPlayListener();
    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        super.resultFailed(arg1, arg2);
        resultFailed = true;
        logger.d("resultFailed:arg2=" + arg2);
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
        if (isArts == 1 || isArts == 2) {
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

    /** 扫描是否有需要弹出的互动题 */
    public void scanQuestion(long position) {
        if (!mIsLand.get() || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }
        liveBackBll.scanQuestion(position);
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

    /** 是否允许移动数据播放 */
    private boolean allowMobilePlayVideo = false;

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
            logger.i("onNowMobileEvent:initialized=" + initialized + ",pause=" + pause);
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
                            logger.i("onNowMobileEvent:onClick:initialized=" + initialized + ",finalPause=" +
                                    finalPause);
                            allowMobilePlayVideo = true;
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

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        if (liveBackBll != null) {
            liveBackBll.onDestroy();
        }
        if (liveBackVideoBll != null) {
            liveBackVideoBll.onDestroy();
        }
        LiveVideoConfig.isNewArts = false;
        ProxUtil.getProxUtil().clear(activity);
    }

    @Override
    protected void resultComplete() {
        if (mVideoTutorEntity != null && videoPlayStatus == MediaPlayer.VIDEO_TEACHER_MAIN) {
            videoPlayStatus = MediaPlayer.VIDEO_TEACHER_TUTOR;
            mMediaController.setVideoStatus(MediaPlayer.VIDEO_BOTTOM_CONTROL_CODE_TEACHER,
                    MediaPlayer.VIDEO_TEACHER_TUTOR, "");

            startNewVideo();
            return;
        }
        onUserBackPressed();
    }

    @Override
    protected void onRefresh() {
        resultFailed = false;
        if (AppBll.getInstance(activity).isNetWorkAlert()) {
            videoBackgroundRefresh.setVisibility(View.GONE);
            logger.d("onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
            playNewVideo();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            int netWorkType = NetWorkHelper.getNetWorkState(activity, stringBuilder);
            if (netWorkType == NetWorkHelper.MOBILE_STATE && allowMobilePlayVideo) {
                videoBackgroundRefresh.setVisibility(View.GONE);
                logger.d("mobile status : onRefresh:ChildCount=" + rlQuestionContent.getChildCount());
                playNewVideo();
            } else {
                logger.i("not mobile status,or not allowMobilePlayVideo");
            }
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

    /** 重新打开播放器的监听 */
    public void onRestart() {
        if (liveBackBll != null) {
            liveBackBll.onRestart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (liveBackBll != null) {
            liveBackBll.onStop();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        logger.d("setRequestedOrientation:requestedOrientation=" + requestedOrientation);
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public void changeLOrP() {
        liveBackPlayVideoFragment.changeLOrP();
    }

    @Override
    public void setAutoOrientation(boolean isAutoOrientation) {
        super.setAutoOrientation(isAutoOrientation);
    }

    /**
     * 监听虚拟键盘弹起，收起小英MMD皮肤
     */
    private void addOnGlobalLayoutListener() {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                        .OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (videoView.getWidth() <= 0) {
                            return;
                        }
                        boolean isLand = activity.getResources().getConfiguration().orientation == Configuration
                                .ORIENTATION_LANDSCAPE;
                        if (!isLand) {
                            return;
                        }
                        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH,
                                (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        boolean change = LiveVideoPoint.initLiveVideoPoint(activity, liveVideoPoint, lp);
                        long before = System.currentTimeMillis();
//                        setFirstParam();
                        if (change) {
                            onGlobalLayoutListener();
                        }
                        logger.d("onGlobalLayout:change=" + change + ",time=" + (System.currentTimeMillis() - before));
                    }
                });
            }
        }, 10);
    }

    /**
     * 设置蓝屏界面
     */
//    protected void setFirstParam() {
//        if (liveVideoAction != null) {
//            liveVideoAction.setFirstParam(liveVideoPoint);
//        }
//    }
    protected void onGlobalLayoutListener() {
        ArrayList<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.setVideoLayoutF(liveVideoPoint);
        }
    }
}
