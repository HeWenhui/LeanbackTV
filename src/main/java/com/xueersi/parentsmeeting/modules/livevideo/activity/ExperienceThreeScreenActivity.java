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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpAutoLive;
import com.xueersi.parentsmeeting.module.videoplayer.entity.ExpLiveInfo;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.IConnectService;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCTalkConf;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.NewIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.SimpleLiveBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.ExpFeedbackDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.StudyResultDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5ExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.NBH5ExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要功能： 课前直播；课中回放；课后直播；互动题；学习报告;(打点数据;禁言；签到；踢人；聊天；学习反馈;数据埋点; 根据时间在看是否开发)
 * <p>
 * Created by yuanwei2 on 2019/5/23.
 */

public class ExperienceThreeScreenActivity extends LiveVideoActivityBase implements BaseLiveMediaControllerBottom.MediaChildViewClick, ViewTreeObserver.OnGlobalLayoutListener {

    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, ExperienceThreeScreenActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    private class VideoPlayState {

        /**
         * 是否正在播放
         */
        private boolean isPlaying;

        /**
         * 视频播放地址
         */
        private String videoPath;

        /**
         * 视频协议
         */
        private int protocol;

        /**
         * 是否上报过
         */
        private boolean reported;
    }

    /**
     * 辅导老师前缀
     */
    public static String COUNTTEACHER_PREFIX = "f_";

    /**
     * 视频宽度
     */
    public static final float VIDEO_WIDTH = 1280f;

    /**
     * 视频高度
     */
    public static final float VIDEO_HEIGHT = 720f;

    /**
     * 视频宽高比
     */
    public static final float VIDEO_RATIO = VIDEO_WIDTH / VIDEO_HEIGHT;


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

    private static final String IRC_CHANNEL_PREFIX = "4L";

    private final Handler getHandler = new Handler();

    // 刷新状态任务
    private final Runnable liveModeTask = new Runnable() {
        @Override
        public void run() {
            freshLiveMode();
        }
    };

    // 体验课相关日志的埋点
    private final LiveAndBackDebug ums = new SimpleLiveBackDebug() {
        @Override
        public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
            UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
        }

        @Override
        public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
            mData.put("timestamp", System.currentTimeMillis() + "");
            mData.put("liveid", playBackEntity.getLiveId());
            mData.put("termid", playBackEntity.getChapterId());

            if (mGetInfo != null && mGetInfo.getStuName() != null) {
                mData.put("uname", mGetInfo.getStuName());
            } else {
                mData.put("uname", "");
            }
            UmsAgentManager.umsAgentOtherBusiness(ExperienceThreeScreenActivity.this, appID, UmsConstants.uploadBehavior, mData);
        }
    };

    // IRC 回调处理
    private final IRCCallback mIRCcallback = new IRCCallback() {

        @Override
        public void onStartConnect() {
            logger.i("=====>onStartConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onStartConnect();
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            logger.i("=====>onConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onConnect();
            }
        }

        @Override
        public void onRegister() {
            logger.i("=====>onRegister");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onRegister();
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            logger.i("=====>onDisconnect");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onDisconnect();
            }

        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            logger.i("=====>onMessage");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onMessage(target, sender, login, hostname, text, "");
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
            Loger.i("ExperiencLvieAvtiv", "=====>onPrivateMessage:isSelf=" + isSelf);

            if (isSelf && "T".equals(message)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        XESToastUtils.showToast(ExperienceThreeScreenActivity.this, "您的帐号已在其他设备登录，请重新进入直播间");
                        Intent intent = new Intent();
                        intent.putExtra("msg", "您的帐号已在其他设备登录，请重新进入直播间");
                        setResult(ShareBusinessConfig.LIVE_USER_KICK, intent);
                        finish();
                    }
                });
            } else {
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.onPrivateMessage(isSelf, sender, login, hostname, target, message);
                }
            }
        }

        @Override
        public void onChannelInfo(String channel, int userCount, String topic) {
            logger.i("=====>onChannelInfo");

        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String
                notice, String channelId) {
            logger.i("=====>onNotice");
        }

        @Override
        public void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
            logger.i("=====>onTopic");

        }

        @Override
        public void onUserList(String channel, User[] users) {
            logger.i("=====>onUserList start:" + peopleCount);
            peopleCount.set(users.length, new Exception());
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onUserList(channel, users);
            }
            logger.i("=====>onUserList end:" + peopleCount);

            int count = users != null ? users.length : 0;
            for (int index = 0; index < count; index++) {
                User user = users[index];
                if (user.getNick().startsWith(COUNTTEACHER_PREFIX)) {
                    // 辅导老师已在直播间
                    teacherNick = user.getNick();
                    isTeacherIn = true;
                    Log.i("tessPrint", "isTeacherIn=" + isTeacherIn);
                    onModeChanged();
                    break;
                }
            }
            Log.i("tessPrint", "isTeacherIn=" + isTeacherIn);
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {

            logger.i("=====>onJoin start:" + peopleCount);
            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onJoin(target, sender, login, hostname);
            }

            logger.i("=====>onJoin end:" + peopleCount);
            if (sender.startsWith(COUNTTEACHER_PREFIX)) {
                // 辅导老师进来了
                teacherNick = sender;
                isTeacherIn = true;
                Log.i("tessPrint", "isTeacherIn=" + isTeacherIn);
                onModeChanged();
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
            logger.i("=====>onQuit start:" + peopleCount);
            peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
            }
            logger.i("=====>onQuit end:" + peopleCount);

            if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                // 辅导老师离开了
                isTeacherIn = false;
                Log.i("tessPrint", "isTeacherIn=" + isTeacherIn);
                onModeChanged();
            }
        }

        @Override
        public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
                recipientNick, String reason) {
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
            }
        }

        @Override
        public void onUnknown(String line) {

        }
    };

    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();

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
    private IIRCMessage mIRCMessage;
    private LiveGetInfo mGetInfo;
    private String appID = UmsConstants.APP_ID;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);

    private LiveBackBll liveBackBll;

    private LiveBll mLiveBll;

    /**
     * 我的课程业务层
     */
    LectureLivePlayBackBll lectureLivePlayBackBll;

    private LiveHttpManager mHttpManager;

    private ExperienceBusiness expBusiness;

    private LiveMessageBll liveMessageBll;

    private LiveMessagePager mLiveMessagePager;

    private RelativeLayout bottomContent;

    private ViewGroup rootLayout;

    private RelativeLayout rlFirstBackgroundView;

    private RelativeLayout rlLiveMessageContent;

    private RelativeLayout praiselistContent;

    private ImageView ivTeacherNotpresent;

    private ImageView ivLoading;

    private TextView tvLoadingHint;

    LiveMediaControllerBottom liveMediaControllerBottom;

    private StudyResultDialog studyResultDialog;

    private ExpFeedbackDialog expFeedbackDialog;

    private int savedWidth;
    private int savedHeight;

    private int testMode = 0;

    /**
     * 播放器当前状态值
     */
    private VideoPlayState videoPlayState;


    /**
     * 用户是否属于某个分队
     */
    private boolean haveTeam = false;

    /**
     * 辅导老师是否在直播间
     */
    private boolean isTeacherIn = false;

    private String teacherNick = null;

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

        AppBll.getInstance().registerAppEvent(this);

        getHandler.postDelayed(liveModeTask, getModeInterval());
        return true;
    }

    @Override
    public void onDestroy() {
        StableLogHashMap params = new StableLogHashMap("LiveFreePlayExit");
        params.put("liveid", playBackEntity.getLiveId());
        params.put("termid", playBackEntity.getChapterId());
        params.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_EXIT);
        ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_EXIT, params.getData());
        AppBll.getInstance().unRegisterAppEvent(this);
        liveBackBll.onDestory();
        mLiveMessagePager = null;
        getHandler.removeCallbacks(null);

        mIRCMessage.setCallback(null);
        new Thread() {
            @Override
            public void run() {
                super.run();
                mIRCMessage.destory();
            }
        }.start();

        super.onDestroy();
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
        if (!userBackPressed) {
            StableLogHashMap logHashMap = new StableLogHashMap("exitRoom");
            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
            ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
            super.onBackPressed();
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

        if (mIRCMessage != null) {
            mIRCMessage.onNetWorkChange(mNetWorkType);
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

        videoView.setVideoLayout(mVideoMode, VP.DEFAULT_ASPECT_RATIO, (int) VIDEO_WIDTH, (int) VIDEO_HEIGHT, VIDEO_RATIO);
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

        int rightMargin = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * lp.width / VIDEO_WIDTH + (savedWidth - lp.width) / 2);
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
        XESToastUtils.showToast(this, "onPlayOpenStart");
    }

    @Override
    protected void onPlayOpenSuccess() {
        XESToastUtils.showToast(this, "onPlayOpenSuccess");

        if (videoPlayState.isPlaying && videoPlayState.protocol == MediaPlayer.VIDEO_PROTOCOL_MP4) {
            // 如果是播放状态，并且是回放视频，seek到指定位置
            long seekSecond = expAutoLive.getSeekTime();
            if (seekSecond > 0) {
                seekTo(seekSecond * 1000);
            }
        }
    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        XESToastUtils.showToast(this, "resultFailed");
        if (videoPlayState.isPlaying) {
            playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
        }
    }

    @Override
    protected void onPlayError() {
        XESToastUtils.showToast(this, "onPlayError");

        if (videoPlayState.isPlaying) {
            playPSVideo(videoPlayState.videoPath, videoPlayState.protocol);
        }
    }

    @Override
    protected void playingPosition(long currentPosition, long duration) {

        scanQuestion(currentPosition);

        long t1 = TimeUtils.gennerSecond(currentPosition);
        long t2 = TimeUtils.gennerSecond(duration);

        if (!videoPlayState.reported && videoPlayState.protocol == MediaPlayer.VIDEO_PROTOCOL_MP4 && t2 - t1 < 3 * 60) {
            reportToTeacher();
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
        mGetInfo.setStuId(UserBll.getInstance().getMyUserInfoEntity().getStuId());
        mGetInfo.setStuSex(TextUtils.isEmpty(sex) ? "" : sex);

        String stuName = TextUtils.isEmpty(UserBll.getInstance().getMyUserInfoEntity().getRealName())
                ? UserBll.getInstance().getMyUserInfoEntity().getNickName() : UserBll.getInstance()
                .getMyUserInfoEntity().getRealName();
        mGetInfo.setStuName(stuName);
        mGetInfo.setNickname(UserBll.getInstance().getMyUserInfoEntity().getNickName());
        mGetInfo.setHeadImgPath(UserBll.getInstance().getMyUserInfoEntity().getHeadImg());
    }

    /**
     * 初始化业务
     */
    protected void initlizeBlls() {

        liveBackBll = new LiveBackBll(this, playBackEntity);
        liveBackBll.setStuCourId(playBackEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);
        lectureLivePlayBackBll = new LectureLivePlayBackBll(this, "");

        mLiveBll = new LiveBll(this, playBackEntity.getLiveId(), playBackEntity.getChapterId(), expLiveInfo.getLiveType(), 0);
        mLiveBll.setSendMsgListener(new LiveBll.SendMsgListener() {
            @Override
            public void onMessageSend(String msg, String targetName) {
                sendChatMessage(msg, targetName);
            }
        });

        liveMessageBll = new LiveMessageBll(this, 1);
        expBusiness = new ExperienceBusiness(this);
        mHttpManager = new LiveHttpManager(mContext);
        mHttpManager.addBodyParam("liveId", playBackEntity.getLiveId());

        initlizeTalk();

        liveBackBll.addBusinessBll(new QuestionExperienceBll(this, liveBackBll));
        liveBackBll.addBusinessBll(new RedPackageExperienceBll(this, liveBackBll, playBackEntity.getChapterId()));
        liveBackBll.addBusinessBll(new EnglishH5ExperienceBll(this, liveBackBll));
        liveBackBll.addBusinessBll(new NBH5ExperienceBll(this, liveBackBll));
        liveBackBll.addBusinessBll(new ExpRollCallBll(this, liveBackBll, mIRCMessage));
        liveBackBll.onCreate();
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

        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        ivTeacherNotpresent = findViewById(R.id.iv_course_video_teacher_notpresent);

        praiselistContent = findViewById(R.id.rl_course_video_live_praiselist_content);
        praiselistContent.setVisibility(View.VISIBLE);
        ivLoading = findViewById(R.id.iv_course_video_loading_bg);
        tvLoadingHint = findViewById(R.id.tv_course_video_loading_content);

        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);

        tvLoadingHint.setText("获取课程信息");

        bottomContent.addView(baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bottomContent.addView(liveMediaControllerBottom);
    }

    /**
     * 初始化聊天
     */
    protected void initlizeTalk() {

        rlLiveMessageContent = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlLiveMessageContent, params);

        long before = System.currentTimeMillis();
        QuestionBll questionBll = new QuestionBll(this, playBackEntity.getStuCourseId());
        mLiveMessagePager = new LiveMessagePager(this, questionBll, ums, liveMediaControllerBottom, liveMessageLandEntities, null);
        logger.d("initViewLive:time1=" + (System.currentTimeMillis() - before));


        // 关联聊天人数
        mLiveMessagePager.setPeopleCount(peopleCount);
        mLiveMessagePager.setMessageBll(liveMessageBll);

        // TODO: 2018/8/11 设置ircState
        //mLiveMessagePager.setLiveBll(mLiveBll);
        mLiveMessagePager.setIrcState(mLiveBll);


        mLiveMessagePager.onModeChange(mLiveBll.getMode());
        mLiveMessagePager.setIsRegister(true);

        // 03.22 设置统计日志的公共参数
        mLiveMessagePager.setLiveTermId(playBackEntity.getLiveId(), playBackEntity.getChapterId());

        // 隐藏锁屏按钮
        mLiveMessagePager.hideclock();
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);

        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + mGetInfo.getLiveType() + "_" + expChatId + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();

        mNetWorkType = NetWorkHelper.getNetWorkState(this);

        if (MediaPlayer.getIsNewIJK()) {
            mIRCMessage = new NewIRCMessage(this, mNetWorkType, mGetInfo.getStuName(), chatRoomUid, mGetInfo, channel);
        } else {
            // 获取 聊天服务器地址  的接口地址
            ArrayList<TalkConfHost> talkConfHosts = new ArrayList<>();
            TalkConfHost confHost = null;

            if (chatCfgServerList != null && chatCfgServerList.size() > 0) {
                for (int i = 0; i < chatCfgServerList.size(); i++) {
                    confHost = new TalkConfHost();
                    confHost.setHost(chatCfgServerList.get(i));
                    talkConfHosts.add(confHost);
                }
            }

            IRCTalkConf ircTalkConf = new IRCTalkConf(this, mGetInfo, mGetInfo.getLiveType(), mHttpManager, talkConfHosts);

            //聊天连接调度失败日志
            ircTalkConf.setChatServiceError(new IRCTalkConf.ChatServiceError() {
                @Override
                public void getChatUrlFailure(String url, String errMsg, String ip) {
                    Map<String, String> mData = new HashMap<>();
                    mData.put("os", "Android");
                    mData.put("logtype", "Error");
                    mData.put("currenttime", String.valueOf(System.currentTimeMillis()));
                    mData.put("url", url);
                    mData.put("ip", ip);
                    mData.put("errmsg", errMsg);
                    mData.put("liveid", playBackEntity.getLiveId() == null ? "" : playBackEntity.getLiveId());
                    mData.put("orderid", playBackEntity.getChapterId());
                    ums.umsAgentDebugSys(LiveVideoConfig.LIVE_CHAT_GSLB, mData);
                }
            });

            mIRCMessage = new IRCMessage(this, mNetWorkType, mGetInfo.getStuName(), chatRoomUid, channel);
            mIRCMessage.setIrcTalkConf(ircTalkConf);
            //聊天服务器连接失败
            mIRCMessage.setConnectService(new IConnectService() {
                @Override
                public void connectChatServiceError(String serverIp, String serverPort, String errMsg, String ip) {
                    Map<String, String> mData = new HashMap<>();
                    mData.put("os", "Android");
                    mData.put("logtype", "Error");
                    mData.put("currenttime", String.valueOf(System.currentTimeMillis()));
                    mData.put("serverip", serverIp);
                    mData.put("serverport", serverPort);
                    mData.put("errmsg", errMsg);
                    mData.put("ip", ip);
                    mData.put("liveid", playBackEntity.getLiveId() == null ? "" : playBackEntity.getLiveId());
                    mData.put("orderid", playBackEntity.getChapterId());
                    ums.umsAgentDebugSys(LiveVideoConfig.EXPERIENCE_MESSAGE_CONNECT_ERROR, mData);
                }
            });
        }

        mIRCMessage.setCallback(mIRCcallback);
        mIRCMessage.create();

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
//                mode = testMode;
                Log.i("tessPrint", "mode=" + mode);

                if (expLiveInfo.getMode() != mode) {
                    expLiveInfo.setMode(mode);
                    onModeChanged();
                }

                if (mode != COURSE_STATE_4) {
                    getHandler.postDelayed(liveModeTask, getModeInterval());
                }
            }
        });

    }

    /**
     * 发生聊天消息
     */
    protected void sendChatMessage(String msg, String name) {
        logger.i("====>sendMessage:" + msg + ":" + name + ":" + mGetInfo.getStuName());

        if (!mLiveBll.openchat()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.TEACHER_MESSAGE);
            if (StringUtils.isEmpty(name)) {
                name = mGetInfo.getStuName();
            }
            jsonObject.put("name", name);
            jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
            jsonObject.put("msg", msg);

            if (haveTeam) {
                LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = mGetInfo.getStudentLiveInfo();
                String teamId = studentLiveInfo.getTeamId();
                jsonObject.put("from", "android_" + teamId);
                jsonObject.put("to", teamId);
            }
            lectureLivePlayBackBll.sendRecordInteract(playBackEntity.getInteractUrl(), playBackEntity.getChapterId(), 1);
            mIRCMessage.sendMessage(jsonObject.toString());


        } catch (Exception e) {
            UmsAgentManager.umsAgentException(BaseApplication.getContext(), "ExperienceLiveVideoActivity " + "sendMessage", e);
        }
    }

    /**
     * 课程模式切换（未开始，课前，课中，课后，已结束)
     */
    protected void onModeChanged() {

        int mode = expLiveInfo.getMode();

        boolean playVideo = false;

        if (mode == COURSE_STATE_1) {
            // 课前状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setVisibility(isTeacherIn ? View.GONE : View.VISIBLE);
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
            playVideo = isTeacherIn;
        } else if (mode == COURSE_STATE_2) {
            // 课中状态,播放回放视频
            ivTeacherNotpresent.setVisibility(View.GONE);
            playVideo = true;
        } else if (mode == COURSE_STATE_3) {
            // 课后状态,辅导老师在直播间就播放直播
            ivTeacherNotpresent.setVisibility(isTeacherIn ? View.GONE : View.VISIBLE);
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_wait_teacher);
            playVideo = isTeacherIn;
        } else if (mode == COURSE_STATE_4) {
            // 结束状态
            ivTeacherNotpresent.setVisibility(View.VISIBLE);
            ivTeacherNotpresent.setImageResource(R.drawable.live_free_play_end);
        } else {
            // 等待状态
            ivTeacherNotpresent.setVisibility(View.VISIBLE);
            ivTeacherNotpresent.setImageResource(R.drawable.live_course_open_late);
        }


        if (playVideo) {
            // mode = 2 是课中，播放回放视频；其他的播放辅导老师直播视频
            String videoPath = mode == COURSE_STATE_2 ? getBackVideo() : getLiveVideo();
            int protocol = mode == COURSE_STATE_2 ? MediaPlayer.VIDEO_PROTOCOL_MP4 : MediaPlayer.VIDEO_PROTOCOL_RTMP;

            if (!videoPlayState.isPlaying || !videoPath.equals(videoPlayState.videoPath) || protocol != videoPlayState.protocol) {
                videoPlayState.isPlaying = true;
                videoPlayState.videoPath = videoPath;
                videoPlayState.protocol = protocol;
                setmDisplayName(playBackEntity.getPlayVideoName());
                playPSVideo(videoPath, protocol);
            }
        } else {

            if (videoPlayState.isPlaying) {
                stopPlayer();
            }

            videoPlayState.isPlaying = false;
            videoPlayState.videoPath = "";
            videoPlayState.protocol = -1;
        }
    }

    /**
     * 上报视频快播完了
     */
    protected void reportToTeacher() {

        videoPlayState.reported = true;

        if (teacherNick == null) {
            return;
        }

        JSONObject data = new JSONObject();

        try {
            data.put("type", XESCODE.ExpLive.XEP_BACK_FINISH);
            data.put("stuid", UserBll.getInstance().getMyUserInfoEntity().getStuId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 发送IRC指令，告诉辅导老师快播放完了
        mIRCMessage.sendNotice(teacherNick, data.toString());
    }

    /**
     * 初始化学习结果
     */
    protected void initStudyResult() {

        AbstractBusinessDataCallBack callBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                // 获取到数据之后的逻辑处理
                if (objData.length > 0) {
                    ExperienceResult result = (ExperienceResult) objData[0];
                    showStudyResult(result);
                }
            }
        };

        lectureLivePlayBackBll.getExperienceResult(playBackEntity.getChapterId(), playBackEntity.getLiveId(), callBack);
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
                } else if (which == StudyResultDialog.BUTTON_APPLY) {
                    if (result.getUrl() != null) {
                        BrowserActivity.openBrowser(ExperienceThreeScreenActivity.this, result.getUrl());
                    } else {
                        Toast.makeText(getApplicationContext(), "数据异常", Toast.LENGTH_LONG).show();
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
                            logger.d("sendFeedbackSuccess");
                        }
                    };

                    lectureLivePlayBackBll.sendExperienceFeedback(UserBll.getInstance().getMyUserInfoEntity().getStuId(), playBackEntity.getLiveId(), playBackEntity.getSubjectId(), playBackEntity.getGradId(), playBackEntity.getChapterId(), expFeedbackDialog.getSuggest(), jsonArray, callBack);
                }

                StableLogHashMap logHashMap = new StableLogHashMap("afterClassFeedbackClose");
                logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
                logHashMap.put("closetype", closeType);
                ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
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
                        mLiveMessagePager.onopenchat(false, "in-class", true);
                        mLiveBll.setChatOpen(false);
                        isRoomChatAvailable = false;
                        logger.i("=====> teahcer close chat called end 11111");
                    }
                } else if (LocalCourseConfig.CATEGORY_OPEN_CHAT == chatEntity.getvCategory()) {
                    // 开启聊天
                    logger.i("=====> CATEGORY_OPEN_CHAT  22222:" + chatEntity.getvQuestionInsretTime() + ":" + playPosition);

                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer open chat called begin");
                        mLiveMessagePager.onopenchat(true, "in-class", true);
                        mLiveBll.setChatOpen(true);
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
            mLiveMessagePager.onopenchat(false, "in-class", isRoomChatAvailable);
            mLiveBll.setChatOpen(false);
        } else {
            mLiveMessagePager.onopenchat(true, "in-class", !isRoomChatAvailable);
            mLiveBll.setChatOpen(true);
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

    /**
     * mode刷新时间
     *
     * @return
     */
    protected long getModeInterval() {
        return expLiveInfo.getExpLiveQueryInterval() * 1000;
    }

}
