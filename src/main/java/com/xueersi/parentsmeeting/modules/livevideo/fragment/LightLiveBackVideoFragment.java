package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.business.VideoBll;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.BackBusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.PauseNotStopVideoIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.liveback.SuperSpeakerBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllBackBllConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.FeedbackTeacherLiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveBackVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LightLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LightlivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoLoadingImgView;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.fragment
 * @ClassName: LightLiveBackVideoFragment
 * @Description: 轻直播直播回放
 * @Author: WangDe
 * @CreateDate: 2019/12/24 14:18
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/24 14:18
 * @UpdateRemark: 更新说明 1.0 支持横竖屏切换
 * @Version: 1.0
 */
public class LightLiveBackVideoFragment extends LiveBackVideoFragmentBase implements ActivityChangeLand,
        MediaControllerAction {
    public String TAG = "LightLiveBackVideoFragment";

    /**
     * 视频节对象
     */
    private VideoLivePlayBackEntity mVideoEntity;
    private VideoLivePlayBackEntity mVideoMainEntity;

    private long createTime;
    private int videoPlayStatus;
    private String mSectionName;
    private String where;
    private ImageView ivLoading;
    private RelativeLayout rlFirstBackgroundView;
    private ImageView ivBack;
    private String mWebPath;
    private RelativeLayout contentLayout;
    private LinearLayout otherContent;

    {
        /** 布局默认资源 */
        mLayoutVideo = R.layout.light_live_back_video;
    }

    private RelativeLayout rl_course_video_live_controller_content;
    /**
     * 互动题的布局
     */
    private RelativeLayout rlQuestionContentBottom;
    /**
     * 互动题的布局
     */
    private RelativeLayout rlQuestionContent;
    private LiveViewAction liveViewAction;

    protected LiveBackBll liveBackBll;
    protected LiveBackVideoBll liveBackVideoBll;

    private TextView tvLoadingContent;
    /**
     * onPause状态不暂停视频
     */
    PauseNotStopVideoIml pauseNotStopVideoIml;
    boolean isNetWorkEnable = false;
    /** 是否显示移动网络提示 */
    private boolean mIsShowMobileAlert = true;
    /** 是否显示无网络提示 */
    private boolean mIsShowNoWifiAlert = true;
    /** 是不是播放失败 */
    boolean resultFailed = false;
    boolean firstInitView = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = (RelativeLayout) super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return mContentView;
    }

    private void initView() {
        // 预加载布局
        rlFirstBackgroundView = mContentView.findViewById(R.id.rl_course_video_first_backgroud);
        ivLoading = mContentView.findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingContent = mContentView.findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        ivBack = mContentView.findViewById(R.id.iv_course_video_back);

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
        otherContent = mContentView.findViewById(R.id.ll_course_video_live_other_content);
        otherContent.setVisibility(View.VISIBLE);
        contentLayout = mContentView.findViewById(R.id.rl_course_video_live_content);
        liveViewAction = new LiveViewActionIml(activity, mContentView, rlQuestionContent);
        updateLoadingImage();
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
        if (liveBackBll != null) {
            liveBackBll.onReusme();
        }
        //还原声音
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(activity, BasePlayerFragment.class);
        if (videoFragment != null && !videoFragment.isMuteMode()) {//静音模式下不要次操作
            videoFragment.setVolume(1f, 1f);
        }
    }

    @Override
    public void onPause() {
        if (liveBackBll != null) {
            liveBackBll.onPause();
        }

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
    protected void onVideoCreate(Bundle savedInstanceState) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        super.onVideoCreate(savedInstanceState);
        createTime = System.currentTimeMillis();
        LiveAppBll.getInstance().registerAppEvent(this);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = activity.getIntent();

        mVideoMainEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        //测试代码
//        mVideoMainEntity.setVideoPath("https://replayqn.wangxiao.eaydu.com/ll/12077/9da63786302394b95c62f146a7087aa1.flv.mp4");
        videoPlayStatus = intent.getIntExtra("teacherVideoStatus", 0);
        changeLandAndPort();
        startNewVideo();
    }

    private void startNewVideo() {
        if (mVideoEntity != null) {
            savePosition(mCurrentPosition);
        }
        mVideoEntity = mVideoMainEntity;

        if (mVideoEntity == null) {
            LiveCrashReport.postCatchedException(new Exception("" + activity.getIntent().getExtras()));
        }
        // 请求相应数据
        initData();
        initBll();
    }

    protected void initData() {
        stuCourId = mVideoEntity.getStuCourseId();
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        Intent intent = activity.getIntent();
        where = intent.getStringExtra("where");

        liveBackVideoBll = new LiveBackVideoBll(activity, false);
        liveBackVideoBll.setVideoEntity(mVideoEntity);
        liveBackVideoBll.setLiveBackPlayVideoFragment(liveBackPlayVideoFragment);
        liveBackVideoBll.setvPlayer(vPlayer);
        liveBackVideoBll.setSectionName(mSectionName);

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

        setmSendPlayVideoTime(mVideoEntity.getvCourseSendPlayVideoTime());

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
        activity.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                activity.getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                if (LiveAppBll.getInstance().isNetWorkAlert() || isNetWorkEnable) {
                    // 互动题播放地址
                    playNewVideo();
                } else {
                    mIsShowNoWifiAlert = false;
                }
                return false;
            }
        });


    }

    @Override
    protected void playNewVideo() {
        liveBackVideoBll.playNewVideo();
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
        setAutoOrientation(true);
        attachMediaController();
        ArrayList list = liveBackBll.getLiveBackBaseBlls();

    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        super.resultFailed(arg1, arg2);
        resultFailed = true;
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
//        super.seekTo(pos);
        liveBackVideoBll.seekTo(pos);
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        String key = "null";
        liveBackBll.setSpeed(speed);
    }

    @Override
    protected String getVideoKey() {
        if (mVideoEntity != null) {
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
        super.sendPlayVideo();
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
        if (vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }
        liveBackBll.scanQuestion(position);
    }

    @Override
    protected int onVideoStatusChange(int code, int status) {
        UmsAgentManager.umsAgentDebug(activity, "" + code, "status");
        if (code == MediaPlayer.VIDEO_BOTTOM_CONTROL_CODE_TEACHER) {
            videoPlayStatus = status;
        }
        startNewVideo();
        return code;
    }

    protected void initBusiness() {
        liveBackBll.addBusinessShareParam("videoView", videoView);
        pauseNotStopVideoIml = new PauseNotStopVideoIml(activity, onPauseNotStopVideo);
        addBusiness(activity);
        liveBackBll.onCreate();
        long before = System.currentTimeMillis();
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.initViewF(liveViewAction, rlQuestionContentBottom, rlQuestionContent, mIsLand);
        }


        logger.d("initBusiness:initViewF:time=" + (System.currentTimeMillis() - before));
    }

    //添加功能模块
    protected void addBusiness(Activity activity) {
        ArrayList<BllConfigEntity> bllConfigEntities = AllBackBllConfig.getLightliveBackBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }
    }

    protected LiveBackBaseBll creatBll(BllConfigEntity bllConfigEntity) {
        String className = "";
        try {
            className = bllConfigEntity.className;
            Class<?> c = Class.forName(className);
            Class<? extends LiveBackBaseBll> clazz;
            if (BackBusinessCreat.class.isAssignableFrom(c)) {
                Class<? extends BackBusinessCreat> creatClazz = (Class<? extends BackBusinessCreat>) c;
                BackBusinessCreat businessCreat = creatClazz.newInstance();
                clazz = businessCreat.getClassName(activity.getIntent());
                if (clazz == null) {
                    return null;
                }
            } else if (LiveBackBaseBll.class.isAssignableFrom(c)) {
                clazz = (Class<? extends LiveBackBaseBll>) c;
            } else {
                return null;
            }
            Constructor<? extends LiveBackBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBackBll.class});
            LiveBackBaseBll liveBaseBll = constructor.newInstance(activity, liveBackBll);
            logger.d("creatBll:business=" + className);
            return liveBaseBll;
        } catch (Exception e) {
            logger.d("creatBll:business=" + className, e);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    // 处理横竖屏切换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (liveBackBll != null) {
            List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
            for (LiveBackBaseBll businessBll : businessBlls) {
                businessBll.onConfigurationChanged(newConfig);
            }
        }
        changeLandAndPort();
    }

    @Override
    protected void loadLandOrPortView() {
        mPortVideoHeight = VideoBll.getVideoDefaultHeight(activity);
        super.loadLandOrPortView();

    }

    @Override
    public void attachMediaController() {
        {
            logger.d("attachMediaController:beforeAttach=" );
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
            LightlivePlaybackMediaController mPlayBackMediaController = createLivePlaybackMediaController();
            mPlayBackMediaController.setOnPointClick(liveBackBll);
            this.mMediaController = mPlayBackMediaController;
            liveBackPlayVideoFragment.setMediaController(mMediaController);
            rl_course_video_live_controller_content.removeAllViews();

            rl_course_video_live_controller_content.addView(mPlayBackMediaController, new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            mMediaController.setSetSpeedVisable(mIsLand.get());
            ((LightlivePlaybackMediaController)mMediaController).onAttach(mIsLand.get());
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
    }

    protected LightlivePlaybackMediaController createLivePlaybackMediaController() {
        LightlivePlaybackMediaController mPlayBackMediaController = new LightlivePlaybackMediaController(activity,
                liveBackPlayVideoFragment, mIsLand.get());
        return mPlayBackMediaController;
    }

    @Override
    public void release() {

    }

    @Override
    protected void showRefresyLayout(int arg1, int arg2) {
        super.showRefresyLayout(arg1, arg2);

        setLoaidngUlVisibility(View.GONE);
        setLoadResultUlVisibility(View.VISIBLE);
        TextView errorInfo = videoBackgroundRefresh.findViewById(R.id.tv_course_video_errorinfo);
        videoBackgroundRefresh.findViewById(R.id.tv_course_video_errortip).setVisibility(View.GONE);
        MediaErrorInfo mediaErrorInfo = liveBackPlayVideoFragment.getMediaErrorInfo();
        errorInfo.setVisibility(View.VISIBLE);

        if (mediaErrorInfo != null) {
            switch (mediaErrorInfo.mErrorCode) {
                case MediaErrorInfo.PSPlayerError: {
                    AvformatOpenInputError error = AvformatOpenInputError.getError(mediaErrorInfo.mPlayerErrorCode);
                    if (error == AvformatOpenInputError.HTTP_NOT_FOUND) {
                        errorInfo.setText("回放视频未生成，请重试[" + mVideoEntity.getLiveId() + "]");
                    } else {
                        errorInfo.setText("视频播放失败[" + mediaErrorInfo.mPlayerErrorCode + " " + "],请重试");
                    }
                    break;
                }
                case MediaErrorInfo.PSDispatchFailed: {
                    errorInfo.setText("视频播放失败[" + MediaErrorInfo.PSDispatchFailed + "],请点击重试");
                    break;
                }
                case MediaErrorInfo.PSChannelNotExist: {
                    errorInfo.setText("视频播放失败[" + MediaErrorInfo.PSChannelNotExist + "],请点击重试");
                    break;
                }
                case MediaErrorInfo.PSServer403: {
                    errorInfo.setText("鉴权失败[" + MediaErrorInfo.PSServer403 + "],请点击重试");
                    break;
                }
                default: {
                    errorInfo.setText("视频播放失败 [" + arg2 + "]");
                    break;
                }
            }
        }
    }

    private void setLoaidngUlVisibility(int visibility) {
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(visibility);
            ivLoading.setVisibility(visibility);
        }

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

    private void setLoadResultUlVisibility(int visibility) {
        if (videoBackgroundRefresh != null) {
            videoBackgroundRefresh.setVisibility(visibility);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (event.getClass() == AppEvent.class) {
            if (event.netWorkType == NetWorkHelper.MOBILE_STATE) {
                if (LiveAppBll.getInstance().isNotificationOnlyWIFI()) {
                    EventBus.getDefault().post(new AppEvent.OnlyWIFIEvent());
                } else if (LiveAppBll.getInstance().isNotificationMobileAlert()) {
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

    /** 是否允许移动数据播放 */
    private boolean allowMobilePlayVideo = false;

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
            LiveMainHandler.post(new Runnable() {
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
        LiveAppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
        if (liveBackBll != null) {
            liveBackBll.onDestroy();
        }
        if (liveBackVideoBll != null) {
            liveBackVideoBll.onDestroy();
        }
        // LiveVideoConfig.isNewArts = false;
        ProxUtil.getProxUtil().clear(activity);
    }

    /**
     * 视频播放结束退出
     */
    @Override
    protected void resultComplete() {
        mIsEnd = true;
        onUserBackPressed();
    }

    @Override
    protected void onRefresh() {
        resultFailed = false;
        setLoaidngUlVisibility(View.VISIBLE);
        if (LiveAppBll.getInstance().isNetWorkAlert()) {
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
     * 切换试题区位置
     */
    private void changeLandAndPort() {
        ViewGroup group = (ViewGroup) rlQuestionContent.getParent();
        long before = System.currentTimeMillis();
        if (mIsLand.get()) {
            if (group != rlContent) {
                group.removeView(rlQuestionContent);
                // 换位置
                contentLayout.removeAllViews();
                contentLayout.setBackground(activity.getResources().getDrawable(R.color.COLOR_00000000));
                ((ViewGroup)otherContent.getParent()).removeView(otherContent);
                otherContent.setVisibility(View.INVISIBLE);


                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlQuestionContentBottom.removeAllViews();
                otherContent.removeAllViews();
                rlQuestionContent.removeAllViews();

                rlContent.addView(rlQuestionContent,lp);

                logger.d("changeLandAndPort:time1=" + (System.currentTimeMillis() - before));
                before = System.currentTimeMillis();
                if (liveBackBll != null && !liveBackBll.getLiveBackBaseBlls().isEmpty()){
                    List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
                    for (LiveBackBaseBll businessBll : businessBlls) {
                        businessBll.initViewF(liveViewAction, rlQuestionContentBottom, rlQuestionContent, mIsLand);
                    }
                }
                firstInitView = true;
                logger.d("changeLandAndPort:time2=" + (System.currentTimeMillis() - before));
            }
        } else {
            ViewGroup content = (ViewGroup) mContentView.findViewById(R.id.rl_course_video_contentview);
            if (group != content) {
//                setMediaControllerBottomParam();
                // 换位置
                group.removeView(rlQuestionContent);
                group.removeView(otherContent);
                group.removeView(contentLayout);
                rlQuestionContentBottom.removeAllViews();
                rlQuestionContent.removeAllViews();
                otherContent.removeAllViews();
                contentLayout.removeAllViews();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, R.id.rl_course_video_content);
                content.addView(otherContent, lp);
                otherContent.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp2.addRule(RelativeLayout.BELOW, R.id.ll_course_video_live_other_content);
                lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                content.addView(rlQuestionContent,lp2);

                content.removeView(contentLayout);
                RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                contentLayout.setBackground(activity.getResources().getDrawable(R.color.COLOR_00000000));
                content.addView(contentLayout,lp3);

                logger.d("changeLandAndPort:time3=" + (System.currentTimeMillis() - before));
                before = System.currentTimeMillis();
                if (liveBackBll != null && !liveBackBll.getLiveBackBaseBlls().isEmpty()){
                    List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
                    for (LiveBackBaseBll businessBll : businessBlls) {
                        businessBll.initViewF(liveViewAction, rlQuestionContentBottom, rlQuestionContent, mIsLand);
                    }
                }
                firstInitView = true;
                logger.d("changeLandAndPort:time4=" + (System.currentTimeMillis() - before));
//                mMediaController.show();
            }
        }
        if (mMediaController != null){
            ((LightlivePlaybackMediaController)mMediaController).onAttach(mIsLand.get());
            mMediaController.show();
        }
        //在后台被回收，再启动。会没有初始化view
        if (!firstInitView) {
            logger.d("changeLandAndPort:firstInitView=false");
            firstInitView = true;
            if (liveBackBll != null && !liveBackBll.getLiveBackBaseBlls().isEmpty()){
                List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
                for (LiveBackBaseBll businessBll : businessBlls) {
                    businessBll.initViewF(liveViewAction, rlQuestionContentBottom, rlQuestionContent, mIsLand);
                }
            }
        }
    }

}
