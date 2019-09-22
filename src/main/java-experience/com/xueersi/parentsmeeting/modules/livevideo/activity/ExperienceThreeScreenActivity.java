package com.xueersi.parentsmeeting.modules.livevideo.activity;

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
import android.widget.Toast;

import com.xueersi.parentsmeeting.modules.livevideo.business.ExperIRCMessBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperienceIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.IrcAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoPlayState;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpAutoLive;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.module.videoplayer.ps.MediaErrorInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BackBusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllExperienceConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
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
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 主要功能： 课前直播；课中回放；课后直播；互动题；学习报告;(打点数据;禁言；签到；踢人；聊天；学习反馈;数据埋点;)
 * <p>
 * Created by yuanwei2 on 2019/5/23.
 */

public class ExperienceThreeScreenActivity extends LiveVideoActivityBase implements BaseLiveMediaControllerBottom.MediaChildViewClick, ViewTreeObserver.OnGlobalLayoutListener {
    private String TAG = "ExperienceThreeScreenActivity";

    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, ExperienceThreeScreenActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 未开始
     */
    public static final int COURSE_STATE_0 = 0;

    /**
     * 课前状态
     */
    public static final int COURSE_STATE_1 = 1;

    /**
     * 课中状态
     */
    public static final int COURSE_STATE_2 = 2;

    /**
     * 课后状态
     */
    public static final int COURSE_STATE_3 = 3;

    /**
     * 已结束
     */
    public static final int COURSE_STATE_4 = 4;

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
                playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
        }
    };

    // Player 接口监听
    private final VPlayerCallBack.VPlayerListener mPlayerListener = new VPlayerCallBack.SimpleVPlayerListener() {
        @Override
        public void onBufferStart() {
            if (expLiveInfo.getMode() == COURSE_STATE_2) {
                sendLogMessage("playFileNotFluent",
                        "videopath", getBackVideo(),
                        "status", "failed",
                        "loglevel", "1",
                        "functype", "6");
            } else if (expLiveInfo.getMode() == COURSE_STATE_1 || expLiveInfo.getMode() == COURSE_STATE_3) {
                sendLogMessage("playStreamNotFluent",
                        "stream", getLiveVideo(),
                        "status", "failed",
                        "loglevel", "1",
                        "functype", "6");
            }

        }
    };

    private VideoLivePlayBackEntity playBackEntity;

    private ExpLiveInfo expLiveInfo;

    private ExpAutoLive expAutoLive;

    private List<String> chatCfgServerList;

    private List<VideoQuestionEntity> roomChatEvent;

    private int isArts;

    private String expChatId;

    private String sex;

    private int pattern;

    private int mNetWorkType;
    private LiveGetInfo mGetInfo;
    private String appID = UmsConstants.APP_ID;

    private LiveBackBll liveBackBll;
    private ExperienceIRCBll experienceIrcBll;

    /**
     * 签到业务
     */
    private ExpRollCallBll expRollCallBll;

    private LiveHttpManager mHttpManager;

    private ExperienceBusiness expBusiness;

    private LiveMessagePager mLiveMessagePager;
    private ExperIRCMessBll experIRCMessBll;
    private RelativeLayout bottomContent;

    private ViewGroup rootLayout;

    private RelativeLayout rlFirstBackgroundView;

    private RelativeLayout rlLiveMessageContent;

    private ImageView ivTeacherNotpresent;

    private ImageView ivLoading;

    private TextView tvLoadingHint;

    LiveMediaControllerBottom liveMediaControllerBottom;

    private StudyResultDialog studyResultDialog;

    private ExpFeedbackDialog expFeedbackDialog;

    private int savedWidth;
    private int savedHeight;

    private boolean isBackPressed;

    private boolean isStudyShow;

    private boolean isFirstTopic = true;

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

        UmsAgentManager.umsAgentOtherBusiness(ExperienceThreeScreenActivity.this, appID, UmsConstants.uploadBehavior, logHashMap.getData());
    }

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

        if (expLiveInfo.getMode() != COURSE_STATE_4) {
            getHandler.postDelayed(liveHeartTask, getHeartInterval());
        }

        return true;
    }

    @Override
    public void finish() {

        getHandler.removeCallbacks(liveModeTask);
        getHandler.removeCallbacks(liveHeartTask);
        getHandler.removeCallbacks(playDelayTask);

        LiveAppBll.getInstance().unRegisterAppEvent(this);

        if (videoPlayState.isPlaying) {
            stopPlayer();
        }

        liveBackBll.onDestroy();
        mLiveMessagePager = null;
        super.finish();
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
    public void onBackPressed() {

        boolean userBackPressed = liveBackBll.onUserBackPressed();
        if (userBackPressed) {
            return;
        }
        if (!isStudyShow) {
            isBackPressed = true;
            initStudyResult();
        } else {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        liveBackBll.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();
        liveBackBll.onStop();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        mNetWorkType = event.netWorkType;

        if (experienceIrcBll != null) {
            experienceIrcBll.onNetWorkChange(mNetWorkType);
        }
    }

    @Override
    public void onGlobalLayout() {
        boolean isLand = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (!isLand) {
            return;
        }

        int viewWidth = rootLayout.getMeasuredWidth();
        int viewHeight = rootLayout.getMeasuredHeight();

        if (viewWidth <= 0 || viewHeight <= 0 || (savedWidth == viewWidth && savedHeight == viewHeight)) {
            return;
        }

        savedWidth = viewWidth;
        savedHeight = viewHeight;

        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) LiveVideoConfig.VIDEO_WIDTH, (int) LiveVideoConfig.VIDEO_HEIGHT, LiveVideoConfig.VIDEO_RATIO);
        final ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        LiveVideoPoint.initLiveVideoPoint((Activity) mContext, LiveVideoPoint.getInstance(), lp);

        if (mLiveMessagePager != null) {
            mLiveMessagePager.setVideoLayout(LiveVideoPoint.getInstance());
        }

        int topGap = (savedHeight - videoView.getLayoutParams().height) / 2;
        int paddingBottom = (int) (topGap + 15 * ScreenUtils.getScreenDensity());

        if (liveMediaControllerBottom.getPaddingBottom() != paddingBottom) {
            liveMediaControllerBottom.setPadding(0, 0, 0, paddingBottom);
        }


        RelativeLayout.LayoutParams params = null;

        int rightMargin = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / LiveVideoConfig.VIDEO_WIDTH + (savedWidth - lp.width) / 2);
        int leftMargin = (savedWidth - lp.width) / 2;
        int topAndBottom = (savedHeight - lp.height) / 2;

        params = (RelativeLayout.LayoutParams) rlFirstBackgroundView.getLayoutParams();
        params.topMargin = topAndBottom;
        params.leftMargin = leftMargin;
        params.rightMargin = rightMargin;
        rlFirstBackgroundView.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) ivTeacherNotpresent.getLayoutParams();
        params.topMargin = topAndBottom;
        params.leftMargin = leftMargin;
        params.rightMargin = rightMargin;
        ivTeacherNotpresent.setLayoutParams(params);
    }

    @Override
    public void onMediaViewClick(View child) {

    }

    @Override
    protected void onPlayOpenStart() {

    }

    @Override
    protected void onPlayOpenSuccess() {

        if (expLiveInfo.getMode() == COURSE_STATE_1 || expLiveInfo.getMode() == COURSE_STATE_3) {
            sendLogMessage("videoStartPlay",
                    "streamid", getLiveVideo(),
                    "status", "success",
                    "loglevel", "1",
                    "functype", "6");
        } else if (expLiveInfo.getMode() == COURSE_STATE_2) {
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
                if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                    rlFirstBackgroundView.setVisibility(View.GONE);
                }

                if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                }
            }
        };

        rlFirstBackgroundView.postDelayed(action, 1500);

    }

    @Override
    protected void playComplete() {
        super.playComplete();

        if (expLiveInfo.getMode() == COURSE_STATE_2) {
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

        if (arg2 == MediaErrorInfo.PLAY_COMPLETE) {
            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }

            if (expLiveInfo.getMode() == COURSE_STATE_1 || expLiveInfo.getMode() == COURSE_STATE_3) {
                getHandler.postDelayed(playDelayTask, 3 * 1000);
            }

        } else if (arg2 == MediaErrorInfo.PSChannelNotExist) {

            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }

            getHandler.postDelayed(playDelayTask, 3 * 1000);

        } else {
            if (videoPlayState.isPlaying) {
                if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                    ivTeacherNotpresent.setVisibility(View.GONE);
                }

                if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                    rlFirstBackgroundView.setVisibility(View.VISIBLE);
                }
                changeNextLine();
//                playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
            }
        }

        if (arg2 == MediaErrorInfo.PLAY_COMPLETE) {
            return;
        }

        if (expLiveInfo.getMode() == COURSE_STATE_2) {
            sendLogMessage("playFileError",
                    "videopath", getBackVideo(),
                    "errCode", arg2 + "",
                    "errMsg", "",
                    "mode", expLiveInfo.getMode() + "",
                    "status", "failed",
                    "loglevel", "Error",
                    "functype", "6");
        } else if (expLiveInfo.getMode() == COURSE_STATE_1 || expLiveInfo.getMode() == COURSE_STATE_2) {
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
    protected void onPlayError() {
        Log.i("expTess", "onPlayError");

        if (videoPlayState.isPlaying) {
            playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
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
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        playBackEntity = (VideoLivePlayBackEntity) extras.getSerializable("videoliveplayback");
        isArts = extras.getInt("isArts");
        chatCfgServerList = extras.getStringArrayList("roomChatCfgServerList");
        expLiveInfo = (ExpLiveInfo) extras.getSerializable("expLiveInfo");
        expAutoLive = (ExpAutoLive) extras.getSerializable("expAutoLive");
        expChatId = extras.getString("expChatId");
        sex = extras.getString("sex");
        pattern = extras.getInt("pattern");

        List<VideoQuestionEntity> lstVideoQuestion = playBackEntity.getLstVideoQuestion();

        int qSize = lstVideoQuestion != null ? lstVideoQuestion.size() : 0;

        //初始化 老师开关聊天事件
        roomChatEvent = new ArrayList<VideoQuestionEntity>();
        VideoQuestionEntity entity = null;

        for (int i = 0; i < qSize; i++) {
            entity = lstVideoQuestion.get(i);
            if (LocalCourseConfig.CATEGORY_OPEN_CHAT == entity.getvCategory() || LocalCourseConfig.CATEGORY_CLOSE_CHAT == entity.getvCategory()) {
                roomChatEvent.add(lstVideoQuestion.get(i));
            }
        }

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

        liveBackBll = new LiveBackBll(this, playBackEntity);
        liveBackBll.setStuCourId(playBackEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);

        expBusiness = new ExperienceBusiness(this);
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", playBackEntity.getLiveId());
        LiveVideoSAConfig liveVideoSAConfig = null;

        if (isArts == 1) {
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }

        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);

        initlizeTalk();

        experIRCMessBll = new ExperIRCMessBll(this, liveBackBll);
        liveBackBll.addBusinessBll(experIRCMessBll);

        ArrayList<BllConfigEntity> bllConfigEntities = AllExperienceConfig.getExperienceBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }

        liveBackBll.addBusinessBll(new RedPackageExperienceBll(this, liveBackBll, playBackEntity.getChapterId()));
        expRollCallBll = new ExpRollCallBll(this, liveBackBll, expLiveInfo, expAutoLive.getTermId());
        liveBackBll.addBusinessBll(expRollCallBll);

        liveBackBll.onCreate();

        RelativeLayout rlQuestionContent = findViewById(R.id.rl_course_video_live_question_contents);
        LiveViewAction liveViewAction = new LiveViewActionIml(this, null, rlQuestionContent);
        rlQuestionContent.setVisibility(View.VISIBLE);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            experienceIrcBll.addBll(businessBll);
            businessBll.initViewF(liveViewAction, null, rlQuestionContent, new AtomicBoolean(mIsLand));
        }
        expRollCallBll.initSignStatus(expLiveInfo.getIsSignIn());
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
                clazz = businessCreat.getClassName(getIntent());
                if (clazz == null) {
                    return null;
                }
            } else if (LiveBackBaseBll.class.isAssignableFrom(c)) {
                clazz = (Class<? extends LiveBackBaseBll>) c;
            } else {
                return null;
            }
            Constructor<? extends LiveBackBaseBll> constructor = clazz.getConstructor(new Class[]{Activity.class, LiveBackBll.class});
            LiveBackBaseBll liveBaseBll = constructor.newInstance(this, liveBackBll);
            logger.d("creatBll:business=" + className);
            return liveBaseBll;
        } catch (Exception e) {
            logger.d("creatBll:business=" + className, e);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    /**
     * 初始化视图
     */
    protected void initlizeView() {

        rootLayout = findViewById(R.id.rl_course_video_contentview);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);

        bottomContent = findViewById(R.id.rl_course_video_live_question_content);
        rlFirstBackgroundView = findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = findViewById(R.id.iv_course_video_teacher_notpresent);
        rlFirstBackgroundView.setVisibility(View.GONE);
        bottomContent.setVisibility(View.VISIBLE);
        ivTeacherNotpresent.setScaleType(ImageView.ScaleType.CENTER_CROP);

        BaseLiveMediaControllerTop baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);
        liveMediaControllerBottom = new LiveMediaControllerBottom(this, mMediaController, this);
        liveMediaControllerBottom.experience();
        ProxUtil.getProxUtil().put(this, BaseLiveMediaControllerBottom.class, liveMediaControllerBottom);
        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        ivTeacherNotpresent = findViewById(R.id.iv_course_video_teacher_notpresent);

        ivLoading = findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingHint = findViewById(R.id.tv_course_video_loading_content);

        tvLoadingHint.setText("正在加载视频");

        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);

        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
    }

    /**
     * 初始化聊天
     */
    protected void initlizeTalk() {

        rlLiveMessageContent = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlLiveMessageContent, 0, params);

        mNetWorkType = NetWorkHelper.getNetWorkState(this);

        experienceIrcBll = new ExperienceIRCBll(this, expChatId, mGetInfo);
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
        experienceIrcBll.onCreate();
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

                if (mode != COURSE_STATE_4) {
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
                if (expLiveInfo.getMode() != COURSE_STATE_4) {
                    getHandler.postDelayed(liveHeartTask, getHeartInterval());
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                if (expLiveInfo.getMode() != COURSE_STATE_4) {
                    getHandler.postDelayed(liveHeartTask, getHeartInterval());
                }
            }

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (expLiveInfo.getMode() != COURSE_STATE_4) {
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

    /**
     * 课程模式切换（未开始，课前，课中，课后，已结束)
     */
    protected void onModeChanged() {

        getHandler.removeCallbacks(playDelayTask);

        int mode = expLiveInfo.getMode();
        Log.i("expTess", "onModeChanged execute mode=" + mode);
        if (mode == COURSE_STATE_1) {
            // 课前状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        } else if (mode == COURSE_STATE_2) {
            // 课中状态,播放回放视频
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_wait_teacher);
        } else if (mode == COURSE_STATE_3) {
            // 课后状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_wait_teacher);
        } else if (mode == COURSE_STATE_4) {
            // 结束状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
        } else {
            // 等待状态
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        }

        if (videoPlayState.isPlaying) {
            stopPlayer();
        }

        if (mode == COURSE_STATE_1 || mode == COURSE_STATE_3) {
            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }

            String videoPath = getLiveVideo();
            int protocol = MediaPlayer.VIDEO_PROTOCOL_RTMP;
            videoPlayState.isPlaying = true;
            videoPlayState.videoPath = videoPath;
            videoPlayState.protocol = protocol;
            setmDisplayName(playBackEntity.getPlayVideoName());
            playPSVideo(videoPath, protocol);

        } else if (mode == COURSE_STATE_2) {

            sendLogMessage("playVideoFile",
                    "videopath", getBackVideo(),
                    "offset", expAutoLive.getSeekTime() + "",
                    "status", "none",
                    "loglevel", "1",
                    "functype", "6");

            if (ivTeacherNotpresent.getVisibility() != View.GONE) {
                ivTeacherNotpresent.setVisibility(View.GONE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.VISIBLE) {
                rlFirstBackgroundView.setVisibility(View.VISIBLE);
            }

            String videoPath = getBackVideo();
            int protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
            videoPlayState.isPlaying = true;
            videoPlayState.videoPath = videoPath;
            videoPlayState.protocol = protocol;
            setmDisplayName(playBackEntity.getPlayVideoName());
            playPSVideo(videoPath, protocol);
        } else {

            if (ivTeacherNotpresent.getVisibility() != View.VISIBLE) {
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
            }

            if (rlFirstBackgroundView.getVisibility() != View.GONE) {
                rlFirstBackgroundView.setVisibility(View.GONE);
            }
        }

        if (mode == COURSE_STATE_4 && !isStudyShow) {
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
        IrcAction ircAction= ProxUtil.getProvide(this,IrcAction.class);
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
                    finish();
                }

                isStudyShow = true;
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                isStudyShow = true;

                if (isBackPressed) {
                    finish();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                isStudyShow = true;

                if (isBackPressed) {
                    finish();
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
            studyResultDialog = new StudyResultDialog(this);
            studyResultDialog.setCancelable(false);
        }

        studyResultDialog.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                if (which == StudyResultDialog.BUTTON_SHUT) {
                    showExpFeedBack();
                } else if (which == StudyResultDialog.BUTTON_CHAT) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(result.getWechatNum());
                    Toast.makeText(getApplicationContext(), "您已复制老师微信号，快去添加吧!", Toast.LENGTH_LONG).show();

                    if (isBackPressed) {
                        finish();
                    }
                } else if (which == StudyResultDialog.BUTTON_APPLY) {
                    if (result.getUrl() != null) {
                        BrowserActivity.openBrowser(ExperienceThreeScreenActivity.this, result.getUrl());
                    } else {
                        Toast.makeText(getApplicationContext(), "数据异常", Toast.LENGTH_LONG).show();
                    }

                    if (isBackPressed) {
                        finish();
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
            expFeedbackDialog = new ExpFeedbackDialog(this);
            expFeedbackDialog.setCancelable(false);
        }

        expFeedbackDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (isBackPressed) {
                    finish();
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
        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }

        liveBackBll.scanQuestion(position);

        int chatSize = roomChatEvent != null ? roomChatEvent.size() : 0;

        for (int i = 0; i < chatSize; i++) {
            // 处理聊天事件 开闭事件
            handleChatEvent(TimeUtils.gennerSecond(position), roomChatEvent.get(i));
        }
    }

    private int lastCheckTime = 0;
    private static final int MAX_CHECK_TIME_RANG = 2;
    private boolean isRoomChatAvailable = true;
    private boolean isChatSateInited;

    private void handleChatEvent(int playPosition, VideoQuestionEntity chatEntity) {
        //出现视频快进
        if ((playPosition - lastCheckTime) >= MAX_CHECK_TIME_RANG || !isChatSateInited) {
            // isChatSateInited = false;
            boolean roomChatAvalible = recoverChatState(playPosition);
            logger.i("=====> resetRoomChatState_:roomChatAvalible=" + roomChatAvalible + ":" + isChatSateInited);
            isChatSateInited = true;
        } else {
            if (chatEntity != null) {
                logger.i("=====>handleChatEvent:category=" + chatEntity.getvCategory());
                //关闭聊天
                if (LocalCourseConfig.CATEGORY_CLOSE_CHAT == chatEntity.getvCategory()) {
                    logger.i("=====> CATEGORY_CLOSE_CHAT 11111:" + chatEntity.getvQuestionInsretTime() + ":"
                            + playPosition);
                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer close chat called begin");
                        experIRCMessBll.onopenchat(false, "in-class", true);
                        isRoomChatAvailable = false;
                        logger.i("=====> teahcer close chat called end 11111");
                    }
                } else if (LocalCourseConfig.CATEGORY_OPEN_CHAT == chatEntity.getvCategory()) {
                    // 开启聊天
                    logger.i("=====> CATEGORY_OPEN_CHAT  22222:" + chatEntity.getvQuestionInsretTime() + ":" + playPosition);

                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer open chat called begin");
                        mLiveMessagePager.onopenchat(true, "in-class", true);
                        isRoomChatAvailable = true;
                        logger.i("=====> teahcer open chat called  end 111111");
                    }
                }
            }
        }

        lastCheckTime = playPosition;
    }

    /**
     * 当进入直播间 或者 发生 视频快进的情况时
     * 恢复聊天状态
     */
    private boolean recoverChatState(int playPosition) {
        List<VideoQuestionEntity> lstVideoQuestion = playBackEntity.getLstVideoQuestion();
        boolean roomChatAvalible = true;

        if (lstVideoQuestion != null && lstVideoQuestion.size() > 0) {
            for (VideoQuestionEntity entity : lstVideoQuestion) {
                if (entity.getvQuestionInsretTime() <= playPosition) {
                    if (entity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT) {
                        roomChatAvalible = true;
                    } else if (entity.getvCategory() == LocalCourseConfig.CATEGORY_CLOSE_CHAT) {
                        roomChatAvalible = false;
                    }
                }
            }
        }

        if (!roomChatAvalible) {
            experIRCMessBll.onopenchat(false, "in-class", isRoomChatAvailable);
        } else {
            experIRCMessBll.onopenchat(true, "in-class", !isRoomChatAvailable);
        }
        return roomChatAvalible;
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
        if (experienceIrcBll != null) {
            experienceIrcBll.onDestory();
            experienceIrcBll = null;
        }
        ProxUtil.getProxUtil().clear(this);
    }
}
