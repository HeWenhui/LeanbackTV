package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpAutoLive;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.IPlayBackMediaCtr;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.BackBusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperLiveAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperModeChange;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperienceIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.IrcAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoPlayState;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllExperienceConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ExperConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.ExpFeedbackDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.StudyResultDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness.ExperienceQuitFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.learnfeedback.business.HalfBodyExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;
import com.xueersi.parentsmeeting.modules.livevideo.weight.ExperMediaCtrl;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveBackPlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ExperienceRecordFragmentBase extends LiveBackVideoFragmentBase implements ViewTreeObserver.OnGlobalLayoutListener {

    private String TAG = "ExperienceRecordFragmentBase";

    {
        /** 布局默认资源 */
        mLayoutVideo = R.layout.frag_exper_live_back_video;
    }

    private final Handler getHandler = LiveMainHandler.getMainHandler();

    // 刷新状态任务
    private final Runnable liveModeTask = new Runnable() {
        @Override
        public void run() {
            freshLiveMode();
        }
    };

    private final Runnable liveHeartTask = new Runnable() {
        @Override
        public void run() {
            freshVisitTime();
        }
    };

    private final Runnable playDelayTask = new Runnable() {
        @Override
        public void run() {
            if (videoPlayState.isPlaying)
                liveBackPlayVideoFragment.playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
        }
    };

    // Player 接口监听
    private final VPlayerCallBack.VPlayerListener mPlayerListener = new VPlayerCallBack.SimpleVPlayerListener() {
        @Override
        public void onBufferStart() {
            if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
                sendLogMessage("playFileNotFluent",
                        "videopath", getBackVideo(),
                        "status", "failed",
                        "loglevel", "1",
                        "functype", "6");
            } else if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_1 || expLiveInfo.getMode() == ExperConfig.COURSE_STATE_3) {
                sendLogMessage("playStreamNotFluent",
                        "stream", getLiveVideo(),
                        "status", "failed",
                        "loglevel", "1",
                        "functype", "6");
            }

        }
    };

    protected VideoLivePlayBackEntity playBackEntity;

    protected ExpLiveInfo expLiveInfo;

    protected ExpAutoLive expAutoLive;

    private List<String> chatCfgServerList;

    private int isArts;

    private String expChatId;

    private String sex;

    private int pattern;
    private LiveGetInfo mGetInfo;
    private String appID = UmsConstants.APP_ID;

    protected LiveBackBll liveBackBll;
    protected ExperienceIRCBll experienceIrcBll;
    HalfBodyExperienceLearnFeedbackBll learnFeedbackBll;
    ExperienceQuitFeedbackBll experienceQuitFeedbackBll;

    protected ExperienceBusiness expBusiness;

    protected RelativeLayout rl_course_video_live_controller_content;
    protected RelativeLayout bottomContent;
    protected RelativeLayout rlQuestionContent;
    protected LiveViewAction liveViewAction;

    protected ExperLiveAction experLiveAction;

    private ImageView ivLoading;

    private TextView tvLoadingHint;
    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    LiveMediaControllerBottom liveMediaControllerBottom;

    private StudyResultDialog studyResultDialog;

    private ExpFeedbackDialog expFeedbackDialog;

    private int savedWidth;
    private int savedHeight;

    private boolean isBackPressed;

    private boolean isStudyShow;

    /**
     * 播放器当前状态值
     */
    private VideoPlayState videoPlayState;

    private String teacherNick = null;

    protected VPlayerCallBack.VPlayerListener getWrapListener() {
        return mPlayerListener;
    }

    protected void sendLogMessage(String logtype, String... args) {
        if (args == null || args.length % 2 != 0) {
            return;
        }

        StableLogHashMap logHashMap = new StableLogHashMap(logtype);

        logHashMap.put("appid", appID);
        logHashMap.put("userid", LiveAppUserInfo.getInstance().getStuId() + "");
        logHashMap.put("usertype", "student");
        logHashMap.put("teacherid", expLiveInfo.getCoachTeacherId() + "");
        logHashMap.put("timestamp", System.currentTimeMillis() + "");
        logHashMap.put("liveid", playBackEntity.getLiveId());
        logHashMap.put("termid", playBackEntity.getChapterId());
        logHashMap.put("uip", IpAddressUtil.USER_IP);

        if (mGetInfo != null && mGetInfo.getStuName() != null) {
            logHashMap.put("uname", mGetInfo.getStuName());
        } else {
            logHashMap.put("uname", "");
        }

        int count = args.length / 2;
        for (int i = 0; i < count; i++) {
            String key = args[i * 2 + 0];
            String val = args[i * 2 + 1];
            logHashMap.put(key, val);
        }

        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadBehavior, logHashMap.getData());
    }

    @Override
    protected void onVideoCreate(Bundle savedInstanceState) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 设置不可自动横竖屏
        setAutoOrientation(false);

        initlizeData();

        initRoomInfo();

        initlizeView();

        initlizeBlls();

        onModeChanged();

        LiveAppBll.getInstance().registerAppEvent(this);

        getHandler.postDelayed(liveModeTask, getModeInterval());

        if (expLiveInfo.getMode() != ExperConfig.COURSE_STATE_4) {
            getHandler.postDelayed(liveHeartTask, getHeartInterval());
        }
        ProxUtil.getProxUtil().put(activity, ActivityChangeLand.class, new ActivityChangeLand() {
            @Override
            public void setAutoOrientation(boolean isAutoOrientation) {
                liveBackPlayVideoFragment.setIsAutoOrientation(isAutoOrientation);
            }

            @Override
            public void setRequestedOrientation(int requestedOrientation) {
                liveBackPlayVideoFragment.setRequestedOrientation(requestedOrientation);
            }

            @Override
            public void changeLOrP() {
                liveBackPlayVideoFragment.changeLOrP();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onUserBackPressed() {
        boolean userBackPressed = liveBackBll.onUserBackPressed();
        if (userBackPressed) {
            return;
        }
        if (!isStudyShow) {
            isBackPressed = true;
            initStudyResult();
        } else {
            activity.finish();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        liveBackBll.onStop();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        if (experienceIrcBll != null) {
            experienceIrcBll.onNetWorkChange(event);
        }
    }

    public void onGlobalLayout() {
        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (!isLand) {
            return;
        }
        View contentView = activity.findViewById(android.R.id.content);
        int viewWidth = contentView.getMeasuredWidth();
        int viewHeight = contentView.getMeasuredHeight();

        if (viewWidth <= 0 || viewHeight <= 0 || (savedWidth == viewWidth && savedHeight == viewHeight)) {
            return;
        }

        savedWidth = viewWidth;
        savedHeight = viewHeight;

        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH, (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        LiveVideoPoint.initLiveVideoPoint(activity, LiveVideoPoint.getInstance(), lp);

        int topGap = (savedHeight - videoView.getLayoutParams().height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());

        if (liveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            liveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }
    }

    @Override
    protected void onPlayOpenStart() {

    }

    @Override
    protected void onPlayOpenSuccess() {

        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_1 || expLiveInfo.getMode() == ExperConfig.COURSE_STATE_3) {
            sendLogMessage("videoStartPlay",
                    "streamid", getLiveVideo(),
                    "status", "success",
                    "loglevel", "1",
                    "functype", "6");
        } else if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            sendLogMessage("videoStartPlay",
                    "videopath", getBackVideo(),
                    "status", "success",
                    "loglevel", "1",
                    "functype", "6");
        }

        if (videoPlayState.isPlaying) {
            seekToCurrent();
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                experLiveAction.onPlayOpenSuccess();
            }
        };

        getHandler.postDelayed(action, 1500);

    }

    @Override
    protected void resultComplete() {
        super.resultComplete();

        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            sendLogMessage("playVideoFileFinished",
                    "videopath", getBackVideo(),
                    "status", "success",
                    "loglevel", "1",
                    "functype", "6");
        }
    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        Log.i("expTess", "resultFailed  error=" + arg2);
        experLiveAction.resultFailed(videoPlayState, arg1, arg2);
        if (arg2 == MediaErrorInfo.PLAY_COMPLETE) {
            if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_1 || expLiveInfo.getMode() == ExperConfig.COURSE_STATE_3) {
                getHandler.postDelayed(playDelayTask, 3 * 1000);
            }

        } else if (arg2 == MediaErrorInfo.PSChannelNotExist) {
            getHandler.postDelayed(playDelayTask, 3 * 1000);

        } else {
            if (videoPlayState.isPlaying) {
                changeNextLine();
//                playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
            }
        }

        if (arg2 == MediaErrorInfo.PLAY_COMPLETE) {
            return;
        }

        if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            sendLogMessage("playFileError",
                    "videopath", getBackVideo(),
                    "errCode", arg2 + "",
                    "errMsg", "",
                    "mode", expLiveInfo.getMode() + "",
                    "status", "failed",
                    "loglevel", "Error",
                    "functype", "6");
        } else if (expLiveInfo.getMode() == ExperConfig.COURSE_STATE_1 || expLiveInfo.getMode() == ExperConfig.COURSE_STATE_2) {
            sendLogMessage("playStreamError",
                    "stream", getLiveVideo(),
                    "errCode", arg2 + "",
                    "errMsg", "",
                    "mode", expLiveInfo.getMode() + "",
                    "status", "failed",
                    "loglevel", "Error",
                    "functype", "6");
        }

    }

    @Override
    protected LiveBackPlayerFragment getFragment() {
        ExPlayerFragment exPlayerFragment = new ExPlayerFragment();
        exPlayerFragment.setRecordFragmentBase(this);
        return exPlayerFragment;
    }

    @Override
    protected void restoreFragment(LiveBackPlayerFragment videoFragment) {
        ExPlayerFragment exPlayerFragment = (ExPlayerFragment) videoFragment;
        exPlayerFragment.setRecordFragmentBase(this);
    }

    public static class ExPlayerFragment extends LiveVideoFragmentBase {
        ExperienceRecordFragmentBase recordFragmentBase;

        public void setRecordFragmentBase(ExperienceRecordFragmentBase recordFragmentBase) {
            this.recordFragmentBase = recordFragmentBase;
            liveBackVideoFragment = recordFragmentBase;
        }

        @Override
        protected VPlayerCallBack.VPlayerListener getWrapListener() {
            return new VPlayerCallBack.SimpleVPlayerListener() {
                @Override
                public void onPlayError() {
                    recordFragmentBase.onPlayError();
                }
            };
        }
    }

    protected void onPlayError() {
        Log.i("expTess", "onPlayError");
        if (videoPlayState.isPlaying) {
            liveBackPlayVideoFragment.playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
        }
    }

    @Override
    protected void playingPosition(long currentPosition, long duration) {


        long t1 = TimeUtils.gennerSecond(currentPosition);
        long t2 = TimeUtils.gennerSecond(duration);

        if (videoPlayState.protocol == MediaPlayer.VIDEO_PROTOCOL_MP4) {
            scanQuestion(currentPosition);
        }


        if (!videoPlayState.reported && videoPlayState.protocol == MediaPlayer.VIDEO_PROTOCOL_MP4 && t2 - t1 < 3 * 60) {
            reportToTeacher(duration, currentPosition);
        }
    }

    /**
     * 初始化数据
     */
    protected void initlizeData() {
        Intent intent = activity.getIntent();
        Bundle extras = intent.getExtras();

        playBackEntity = (VideoLivePlayBackEntity) extras.getSerializable("videoliveplayback");
        isArts = extras.getInt("isArts");
        chatCfgServerList = extras.getStringArrayList("roomChatCfgServerList");
        expLiveInfo = (ExpLiveInfo) extras.getSerializable("expLiveInfo");
        expAutoLive = (ExpAutoLive) extras.getSerializable("expAutoLive");
        expChatId = extras.getString("expChatId");
        sex = extras.getString("sex");
        pattern = extras.getInt("pattern");
        lastMode = expLiveInfo.getMode();
        ProxUtil.getProxUtil().put(activity, ExpLiveInfo.class, expLiveInfo);
        videoPlayState = new VideoPlayState();
    }

    /**
     * 初始化房间
     */
    protected void initRoomInfo() {

        mGetInfo = new LiveGetInfo(new LiveTopic());
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfoEntity = new LiveGetInfo.StudentLiveInfoEntity();
        studentLiveInfoEntity.setClassId(playBackEntity.getClassId());
        studentLiveInfoEntity.setCourseId(playBackEntity.getCourseId());
        mGetInfo.setStudentLiveInfo(studentLiveInfoEntity);

        mGetInfo.setId(playBackEntity.getLiveId());
        mGetInfo.setLiveType(expLiveInfo.getLiveType());
        mGetInfo.setStuId(LiveAppUserInfo.getInstance().getStuId());
        mGetInfo.setStuSex(TextUtils.isEmpty(sex) ? "" : sex);

        String stuName = TextUtils.isEmpty(LiveAppUserInfo.getInstance().getRealName())
                ? LiveAppUserInfo.getInstance().getNickName() : LiveAppUserInfo.getInstance().getRealName();
        mGetInfo.setStuName(stuName);
        mGetInfo.setNickname(LiveAppUserInfo.getInstance().getNickName());
        mGetInfo.setHeadImgPath(LiveAppUserInfo.getInstance().getHeadImg());

        mGetInfo.getStudentLiveInfo().setSignStatus(expLiveInfo.getIsSignIn());
    }

    /**
     * 初始化业务
     */
    protected void initlizeBlls() {

        liveBackBll = new LiveBackBll(activity, playBackEntity);
        liveBackBll.setStuCourId(playBackEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);

        expBusiness = new ExperienceBusiness(activity);
        initlizeTalk();

        addBusiness(activity);
        liveBackBll.onCreate();
        initBllView();
    }

    protected void initBllView() {
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            experienceIrcBll.addBll(businessBll);
            businessBll.initViewF(liveViewAction, null, rlQuestionContent, mIsLand);
        }
    }

    protected void addBusiness(Activity activity) {
        ArrayList<BllConfigEntity> bllConfigEntities = AllExperienceConfig.getExperienceRecordBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }

        liveBackBll.addBusinessBll(new RedPackageExperienceBll(activity, liveBackBll, playBackEntity.getChapterId()));
        ExpRollCallBll expRollCallBll = new ExpRollCallBll(activity, liveBackBll, expLiveInfo, expAutoLive.getTermId());
        liveBackBll.addBusinessBll(expRollCallBll);

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

    /** 当前处于第几条线路 */
    private int nowPos = 0;
    /** 一共有几条线路 */
    private int totalRouteNum = 0;
    // region 播放器核心服务监听
    /** 播放器核心服务监听 */

    /**
     * 切换到下一条线路(回放和体验课专用,目前只支持mp4)
     */
    protected void changeNextLine() {
        this.nowPos++;
        //当前线路小于总线路数
        if (this.nowPos < totalRouteNum) {
            liveBackPlayVideoFragment.changePlayLive(this.nowPos, MediaPlayer.VIDEO_PROTOCOL_MP4);
        } else {
            if (totalRouteNum != 0) {
                this.nowPos = 0;
                liveBackPlayVideoFragment.changePlayLive(this.nowPos, MediaPlayer.VIDEO_PROTOCOL_MP4);
            } else {
                liveBackPlayVideoFragment.playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
            }
        }
    }

    @Override
    protected void attachMediaController() {
//        super.attachMediaController();
        if (mMediaController == null) {
            mMediaController = new ExperMediaCtrl(activity, liveBackPlayVideoFragment);
            mMediaController.setFileName(playBackEntity.getPlayVideoName());
        }
//        showLongMediaController();
    }

    /**
     * 初始化视图
     */
    protected void initlizeView() {
        rl_course_video_live_controller_content = mContentView.findViewById(R.id
                .rl_course_video_live_controller_content);
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        bottomContent = findViewById(R.id.rl_course_video_live_question_content);

        bottomContent.setVisibility(View.VISIBLE);

        ivLoading = findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingHint = findViewById(R.id.tv_course_video_loading_content);

        tvLoadingHint.setText("正在加载视频");
        //基础布局
        rlQuestionContent = findViewById(R.id.rl_course_video_record_question_content);
        liveViewAction = new LiveViewActionIml(activity, mContentView, rlQuestionContent);
        rlQuestionContent.setVisibility(View.VISIBLE);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        //设置标题，要在setControllerTop方法以后
        createMediaController();
        createLiveVideoAction();
    }

    protected void createLiveVideoAction() {
        experLiveAction = new ExperLiveAction(activity, mContentView, expLiveInfo);
    }

    protected void createMediaController() {
        mMediaController = creatLiveMediaCtr();
        ExperMediaCtrl experMediaCtrl = (ExperMediaCtrl) mMediaController;
        rl_course_video_live_controller_content.addView(experMediaCtrl, new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        baseLiveMediaControllerTop = createMediaControlerTop();
        createMediaControllerBottom();
        ProxUtil.getProxUtil().put(activity, BaseLiveMediaControllerBottom.class, liveMediaControllerBottom);
        experMediaCtrl.setControllerTop(baseLiveMediaControllerTop);
        experMediaCtrl.setControllerBottom(liveMediaControllerBottom, false);
        mMediaController.setFileName(playBackEntity.getPlayVideoName());
        mMediaController.show();
    }

    protected IPlayBackMediaCtr creatLiveMediaCtr() {
        ExperMediaCtrl experMediaCtrl = new ExperMediaCtrl(activity, liveBackPlayVideoFragment);
        return experMediaCtrl;
    }

    protected BaseLiveMediaControllerTop createMediaControlerTop() {
        ExperMediaCtrl experMediaCtrl = (ExperMediaCtrl) mMediaController;
        BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(activity, experMediaCtrl, liveBackPlayVideoFragment);
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return baseLiveMediaControllerTop;
    }

    protected void createMediaControllerBottom() {
        ExperMediaCtrl experMediaCtrl = (ExperMediaCtrl) mMediaController;
        liveMediaControllerBottom = new LiveMediaControllerBottom(activity, experMediaCtrl, liveBackPlayVideoFragment);
        liveMediaControllerBottom.experience();
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 初始化聊天
     */
    protected void initlizeTalk() {
        experienceIrcBll = new ExperienceIRCBll(activity, expChatId, mGetInfo);
        experienceIrcBll.addNotice(new NoticeAction() {
            @Override
            public void onNotice(String sourceNick, String target, JSONObject data, int type) {
                if (type == XESCODE.ExpLive.XEP_MODE_CHANGE) {
                    try {
                        int status = data.getInt("status");
                        setNoticeMode(status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public int[] getNoticeFilter() {
                return new int[]{XESCODE.ExpLive.XEP_MODE_CHANGE};
            }
        });
        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                experienceIrcBll.onCreate();
            }
        });
    }

    /**
     * 获取课程模式（未开始，课前，课中，课后，已结束)
     */
    protected void freshLiveMode() {

        String url = expLiveInfo.getLiveStatus();
        int expLiveId = expLiveInfo.getExpLiveId();

        expBusiness.getExpLiveStatus(url, expLiveId, new HttpCallBack() {
            @Override
            public void onPmFailure(Throwable error, String msg) {
                getHandler.postDelayed(liveModeTask, getModeInterval());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                getHandler.postDelayed(liveModeTask, getModeInterval());
            }

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                JSONObject json = (JSONObject) responseEntity.getJsonObject();
                int mode = json.getInt("mode");

                if (expLiveInfo.getMode() != mode) {
                    expLiveInfo.setMode(mode);
                    Log.i("expTess", "onModeChanged fresh mode=" + mode);
                    onModeChanged();
                }

                if (mode != ExperConfig.COURSE_STATE_4) {
                    getHandler.postDelayed(liveModeTask, getModeInterval());
                }

            }
        });

    }

    protected void freshVisitTime() {
        String url = playBackEntity.getVisitTimeUrl();
        String liveId = playBackEntity.getLiveId();
        String termId = playBackEntity.getChapterId();

        expBusiness.visitTimeHeart(url, liveId, termId, new HttpCallBack() {
            @Override
            public void onPmFailure(Throwable error, String msg) {
                if (expLiveInfo.getMode() != ExperConfig.COURSE_STATE_4) {
                    getHandler.postDelayed(liveHeartTask, getHeartInterval());
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                if (expLiveInfo.getMode() != ExperConfig.COURSE_STATE_4) {
                    getHandler.postDelayed(liveHeartTask, getHeartInterval());
                }
            }

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (expLiveInfo.getMode() != ExperConfig.COURSE_STATE_4) {
                    getHandler.postDelayed(liveHeartTask, getHeartInterval());
                }
            }
        });
    }

    protected void seekToCurrent() {
        if (videoPlayState.protocol == MediaPlayer.VIDEO_PROTOCOL_MP4) {
            // 如果是播放状态，并且是回放视频，seek到指定位置
            long seekSecond = expAutoLive.getSeekTime();
            if (seekSecond > 0) {
                seekTo(seekSecond * 1000);
            }
        }
    }

    protected void setNoticeMode(final int status) {

        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (expLiveInfo.getMode() != status) {
                    expLiveInfo.setMode(status);
                    Log.i("expTess", "onModeChanged notice mode=" + status);
                    onModeChanged();
                }
            }
        };

        getHandler.post(action);
    }

    int lastMode;

    /**
     * 课程模式切换（未开始，课前，课中，课后，已结束)
     */
    protected void onModeChanged() {

        getHandler.removeCallbacks(playDelayTask);

        int mode = expLiveInfo.getMode();
        logger.d("onModeChanged execute mode=" + mode);
        experLiveAction.onModeChanged(mode);
        if (lastMode != mode) {
            ArrayList<LiveBackBaseBll> liveBackBaseBlls = liveBackBll.getLiveBackBaseBlls();
            for (int i = 0; i < liveBackBaseBlls.size(); i++) {
                LiveBackBaseBll liveBackBaseBll = liveBackBaseBlls.get(i);
                if (liveBackBaseBll instanceof ExperModeChange) {
                    ExperModeChange experModeChange = (ExperModeChange) liveBackBaseBll;
                    experModeChange.onModeChange(lastMode, mode);
                }
            }
            lastMode = mode;
        }
        if (videoPlayState.isPlaying) {
            stopPlayer();
        }

        if (mode == ExperConfig.COURSE_STATE_1 || mode == ExperConfig.COURSE_STATE_3) {
            String videoPath = getLiveVideo();
            int protocol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
            videoPlayState.isPlaying = true;
            videoPlayState.videoPath = videoPath;
            videoPlayState.protocol = protocol;
            liveBackPlayVideoFragment.setmDisplayName(playBackEntity.getPlayVideoName());
            liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);

        } else if (mode == ExperConfig.COURSE_STATE_2) {

            sendLogMessage("playVideoFile",
                    "videopath", getBackVideo(),
                    "offset", expAutoLive.getSeekTime() + "",
                    "status", "none",
                    "loglevel", "1",
                    "functype", "6");
            String videoPath = getBackVideo();
            int protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
            videoPlayState.isPlaying = true;
            videoPlayState.videoPath = videoPath;
            videoPlayState.protocol = protocol;
            liveBackPlayVideoFragment.setmDisplayName(playBackEntity.getPlayVideoName());
            liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);
        }
        if (mode == ExperConfig.COURSE_STATE_4 && !isStudyShow) {
            initStudyResult();
        }
    }

    /**
     * 上报视频快播完了
     */
    protected void reportToTeacher(long duration, long curpos) {

        sendLogMessage("notifyVideoCutDown",
                "duration", TimeUtils.gennerSecond(duration) + "",
                "curpos", TimeUtils.gennerSecond(curpos) + "",
                "videoCutDownTime", "180",
                "status", "failed",
                "loglevel", "1",
                "functype", "6");

        videoPlayState.reported = true;

        if (teacherNick == null) {
            return;
        }

        JSONObject data = new JSONObject();

        try {
            data.put("type", XESCODE.ExpLive.XEP_BACK_FINISH);
            data.put("stuid", LiveAppUserInfo.getInstance().getStuId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 发送IRC指令，告诉辅导老师快播放完了
        IrcAction ircAction = ProxUtil.getProvide(activity, IrcAction.class);
        ircAction.sendNotice(teacherNick, data.toString());
    }

    /**
     * 初始化学习结果
     */
    protected void initStudyResult() {

        HttpCallBack callBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (isStudyShow) {
                    return;
                }

                ExperienceResult learn = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString(), ExperienceResult.class);

                if (learn != null) {
                    showStudyResult(learn);
                } else if (isBackPressed) {
                    experienceIrcBll = new ExperienceIRCBll(activity, expChatId, mGetInfo);
                    activity.finish();
                }

                isStudyShow = true;
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                isStudyShow = true;

                if (isBackPressed) {
                    activity.finish();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                isStudyShow = true;

                if (isBackPressed) {
                    activity.finish();
                }
            }

        };

        String planId = playBackEntity.getLiveId();
        String orderId = playBackEntity.getChapterId();
        String userId = LiveAppUserInfo.getInstance().getStuId();
        expBusiness.getExperienceResult(planId, orderId, userId, callBack);
    }

    /**
     * 显示学习结果
     *
     * @param result
     */
    protected void showStudyResult(final ExperienceResult result) {
        if (studyResultDialog == null) {
            studyResultDialog = new StudyResultDialog(activity);
            studyResultDialog.setCancelable(false);
        }
        studyResultDialog.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                if (which == StudyResultDialog.BUTTON_SHUT) {
                    showExpFeedBack();
                } else if (which == StudyResultDialog.BUTTON_CHAT) {
                    ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(result.getWechatNum());
                    XESToastUtils.showToast("您已复制老师微信号，快去添加吧!");
                    if (isBackPressed) {
                        activity.finish();
                    }
                } else if (which == StudyResultDialog.BUTTON_APPLY) {
                    if (result.getUrl() != null) {
                        BrowserActivity.openBrowser(activity, result.getUrl());
                    } else {
                        XESToastUtils.showToast("数据异常");
                    }

                    if (isBackPressed) {
                        activity.finish();
                    }
                }
            }
        });

        studyResultDialog.setStudyResult(result);
        studyResultDialog.show();

    }

    /**
     * 显示体验反馈
     */
    protected void showExpFeedBack() {

        if (expFeedbackDialog == null) {
            expFeedbackDialog = new ExpFeedbackDialog(activity);
            expFeedbackDialog.setCancelable(false);
        }

        expFeedbackDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (isBackPressed) {
                    activity.finish();
                }
            }
        });

        expFeedbackDialog.setClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                String closeType = "";

                if (which == ExpFeedbackDialog.BUTTON_CLOSE) {
                    closeType = "2";
                } else if (which == ExpFeedbackDialog.BUTTON_SUBMIT) {
                    closeType = "1";

                    JSONArray jsonArray = new JSONArray();
                    try {
                        JSONObject jsonOption = new JSONObject();
                        jsonOption.put("1", expFeedbackDialog.getDifficulty());
                        jsonArray.put(jsonOption);
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("2", expFeedbackDialog.getSatisficing());
                        jsonArray.put(jsonObject2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HttpCallBack callBack = new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                        }
                    };

                    expBusiness.sendExperienceFeedback(playBackEntity.getLiveId(), playBackEntity.getSubjectId(), playBackEntity.getGradId(), playBackEntity.getChapterId(), expFeedbackDialog.getSuggest(), jsonArray, callBack);
                }

            }
        });

        expFeedbackDialog.show();
    }

    /**
     * 互动题扫描
     *
     * @param position
     */
    protected void scanQuestion(long position) {
        if (!mIsLand.get() || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }

        liveBackBll.scanQuestion(position);
    }

    /**
     * 回放视屏地址
     *
     * @return
     */
    protected String getBackVideo() {
        String videoPath;
        String url = playBackEntity.getVideoPath();
        if (url.contains("http") || url.contains("https")) {
            videoPath = DoPSVideoHandle.getPSVideoPath(url);
        } else {
            videoPath = url;
        }

        return videoPath;
    }

    /**
     * 直播视频地址
     *
     * @return
     */
    protected String getLiveVideo() {
        return "x_" + expLiveInfo.getLiveType() + "_" + expLiveInfo.getExpLiveId() + "_" + expLiveInfo.getCoachTeacherId();
    }

    protected long getHeartInterval() {
        return playBackEntity.getHbTime() * 1000;
    }

    /**
     * mode刷新时间
     *
     * @return
     */
    protected long getModeInterval() {
        return expLiveInfo.getExpLiveQueryInterval() * 1000;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getHandler.removeCallbacks(liveModeTask);
        getHandler.removeCallbacks(liveHeartTask);
        getHandler.removeCallbacks(playDelayTask);

        LiveAppBll.getInstance().unRegisterAppEvent(this);

        if (videoPlayState.isPlaying) {
            stopPlayer();
        }

        liveBackBll.onDestroy();
        if (experienceIrcBll != null) {
            experienceIrcBll.onDestory();
            experienceIrcBll = null;
        }
        ProxUtil.getProxUtil().clear(activity);
    }

    @Override
    protected void seekTo(long pos) {

    }

    @Override
    protected long getStartPosition() {
        return 0;
    }

}
