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

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
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
import com.xueersi.lib.framework.utils.XESToastUtils;
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
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.NewIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.SimpleLiveBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllExperienceConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.ExpFeedbackDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.StudyResultDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.http.ExperienceBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageExperienceBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.ExperienceIrcState;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.ExpRollCallBll;
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
import java.util.Map;
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

    private class ExperkDebug extends SimpleLiveBackDebug {

        ExperkDebug() {
            ProxUtil.getProxUtil().put(ExperienceThreeScreenActivity.this, LiveAndBackDebug.class, this);
        }

        @Override
        public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
            UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
        }

        @Override
        public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
            mData.put("appid", appID);
            mData.put("userid", UserBll.getInstance().getMyUserInfoEntity().getStuId() + "");
            mData.put("usertype", "student");
            mData.put("teacherid", expLiveInfo.getCoachTeacherId() + "");
            mData.put("timestamp", System.currentTimeMillis() + "");
            mData.put("liveid", playBackEntity.getLiveId());
            mData.put("termid", playBackEntity.getChapterId());
            mData.put("uip", IpAddressUtil.USER_IP);

            if (mGetInfo != null && mGetInfo.getStuName() != null) {
                mData.put("uname", mGetInfo.getStuName());
            } else {
                mData.put("uname", "");
            }
            UmsAgentManager.umsAgentOtherBusiness(ExperienceThreeScreenActivity.this, appID, UmsConstants.uploadBehavior, mData);
        }
    }

    // 体验课相关日志的埋点
    private final LiveAndBackDebug ums = new ExperkDebug();

    // IRC 回调处理
    private final IRCCallback mIRCcallback = new IRCCallback() {

        @Override
        public void onStartConnect() {
            Log.i("expTess", "onStartConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onStartConnect();
            }
        }

        @Override
        public void onConnect(IRCConnection connection) {
            Log.i("expTess", "onConnect");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onConnect();
            }
        }

        @Override
        public void onRegister() {
            Log.i("expTess", "onRegister");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onRegister();
            }
        }

        @Override
        public void onDisconnect(IRCConnection connection, boolean isQuitting) {
            Log.i("expTess", "onDisconnect");

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onDisconnect();
            }

        }

        @Override
        public void onMessage(String target, String sender, String login, String hostname, String text) {
            Log.i("expTess", "onMessage");
            if (mLiveMessagePager != null) {
                mLiveMessagePager.onMessage(target, sender, login, hostname, text, "");
            }
        }

        @Override
        public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

            Log.i("expTess", "onPrivateMessage");

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
            Log.i("expTess", "onChannelInfo channel" + channel);
        }

        @Override
        public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice, String channelId) {

            int type = -1;
            JSONObject data = null;

            try {
                data = new JSONObject(notice);
                type = data.getInt("type");
                expRollCallBll.dispatcNotice(sourceNick, target, data, type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("expTess", "onNotice type=" + type);

            if (data == null) {
                return;
            }

            // 老师聊天
            if (type == XESCODE.TEACHER_MESSAGE) {
                String name;
                if (sourceNick.startsWith("t")) {
                    name = "主讲老师";
                    String teacherImg = "";
                    String message = "";
                    try {
                        teacherImg = mGetInfo.getMainTeacherInfo().getTeacherImg();
                        message = data.getString("msg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mLiveMessagePager.onMessage(target, sourceNick, "", "", message, teacherImg);
                } else {
                    name = "辅导老师";
                    String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
                    String to = "";
                    String message = "";

                    try {
                        to = data.optString("to", "All");
                        message = data.getString("msg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if ("All".equals(to) || teamId.equals(to)) {
                        String teacherIMG = mGetInfo.getTeacherIMG();
                        mLiveMessagePager.onMessage(target, sourceNick, "", "", message, teacherIMG);
                    }
                }
            } else if (type == XESCODE.GAG) {
                // 禁言
                try {
                    String id = data.getString("id");
                    boolean disable = data.getBoolean("disable");
                    String nickName = "" + mIRCMessage.getNickname();
                    if (nickName.equals(id)) {
                        mLiveMessagePager.onDisable(disable, true);
                        mGetInfo.getLiveTopic().setDisable(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == XESCODE.OPENCHAT) {

                // 开关聊天区
                try {
                    String mode = "";

                    if (sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                        mode = LiveTopic.MODE_TRANING;
                    } else {
                        mode = LiveTopic.MODE_CLASS;
                    }

                    boolean open = data.getBoolean("open");
                    mExpIrcState.setChatOpen(open);
                    mLiveMessagePager.onopenchat(open, mode, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == XESCODE.ExpLive.XEP_MODE_CHANGE) {
                try {
                    int status = data.getInt("status");
                    setNoticeMode(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
            Log.i("expTess", "onTopic");

            if (!isFirstTopic) {
                return;
            }

            try {
                JSONObject json = new JSONObject(topic);
                isFirstTopic = false;

                handleTopicSpeak(json);
                handleTopicCall(json);
                handleTopicChat(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUserList(String channel, User[] users) {
            Log.i("expTess", "onUserList");
            peopleCount.set(users.length, new Exception());
            int count = users != null ? users.length : 0;

            for (int index = 0; index < count; index++) {
                User user = users[index];
                if (user.getNick().startsWith(COUNTTEACHER_PREFIX)) {
                    // 辅导老师已在直播间
                    teacherNick = user.getNick();
                    peopleCount.set(users.length - 1, new Exception());
                    break;
                }
            }

            if (mLiveMessagePager != null) {
                mLiveMessagePager.onUserList(channel, users);
            }
            logger.i("=====>onUserList end:" + peopleCount);
        }

        @Override
        public void onJoin(String target, String sender, String login, String hostname) {
            Log.i("expTess", "onJoin");
            if (sender.startsWith(COUNTTEACHER_PREFIX)) {
                // 辅导老师进来了
                teacherNick = sender;
            } else {
                peopleCount.set(peopleCount.get() + 1, new Exception(sender));
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.onJoin(target, sender, login, hostname);
                }
            }
        }

        @Override
        public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
            Log.i("expTess", "onQuit");

            if (!sourceNick.startsWith(COUNTTEACHER_PREFIX)) {
                // 辅导老师离开了
                peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
                if (mLiveMessagePager != null) {
                    mLiveMessagePager.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
                }
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


    /**
     * 签到业务
     */
    private ExpRollCallBll expRollCallBll;


    private LiveHttpManager mHttpManager;

    private ExperienceIrcState mExpIrcState;

    private ExperienceBusiness expBusiness;

    private LiveMessageBll liveMessageBll;

    private LiveMessagePager mLiveMessagePager;

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
        logHashMap.put("userid", UserBll.getInstance().getMyUserInfoEntity().getStuId() + "");
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

        AppBll.getInstance().registerAppEvent(this);

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

        AppBll.getInstance().unRegisterAppEvent(this);

        if (videoPlayState.isPlaying) {
            stopPlayer();
        }

        liveBackBll.onDestroy();
        mLiveMessagePager = null;
        mIRCMessage.setCallback(null);

        new Thread() {
            @Override
            public void run() {
                super.run();
                mIRCMessage.destory();
            }
        }.start();

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
        mGetInfo.setStuId(UserBll.getInstance().getMyUserInfoEntity().getStuId());
        mGetInfo.setStuSex(TextUtils.isEmpty(sex) ? "" : sex);

        String stuName = TextUtils.isEmpty(UserBll.getInstance().getMyUserInfoEntity().getRealName())
                ? UserBll.getInstance().getMyUserInfoEntity().getNickName() : UserBll.getInstance()
                .getMyUserInfoEntity().getRealName();
        mGetInfo.setStuName(stuName);
        mGetInfo.setNickname(UserBll.getInstance().getMyUserInfoEntity().getNickName());
        mGetInfo.setHeadImgPath(UserBll.getInstance().getMyUserInfoEntity().getHeadImg());

        mGetInfo.getStudentLiveInfo().setSignStatus(expLiveInfo.getIsSignIn());
    }

    /**
     * 初始化业务
     */
    protected void initlizeBlls() {

        liveBackBll = new LiveBackBll(this, playBackEntity);
        liveBackBll.setStuCourId(playBackEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);

        liveMessageBll = new LiveMessageBll(this, 1);
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

        ArrayList<BllConfigEntity> bllConfigEntities = AllExperienceConfig.getExperienceBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }

        liveBackBll.addBusinessBll(new RedPackageExperienceBll(this, liveBackBll, playBackEntity.getChapterId()));
        expRollCallBll = new ExpRollCallBll(this, liveBackBll, mIRCMessage, expLiveInfo.getSignInUrl(), expLiveInfo.getExpLiveId(), expAutoLive.getTermId());
        liveBackBll.addBusinessBll(expRollCallBll);

        liveBackBll.onCreate();

        RelativeLayout rlQuestionContent = findViewById(R.id.rl_course_video_live_question_contents);
        LiveViewAction liveViewAction = new LiveViewActionIml(this, null, rlQuestionContent);
        rlQuestionContent.setVisibility(View.VISIBLE);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
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

        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + "4" + "_" + expChatId + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();

        mNetWorkType = NetWorkHelper.getNetWorkState(this);

        mIRCMessage = new NewIRCMessage(this, mNetWorkType, mGetInfo.getStuName(), chatRoomUid, mGetInfo, channel);

        mExpIrcState = new ExperienceIrcState(mGetInfo, mGetInfo.getLiveTopic(), mIRCMessage, playBackEntity, mHttpManager);

        mLiveMessagePager = new LiveMessagePager(this, liveMediaControllerBottom, liveMessageLandEntities, null);
        mLiveMessagePager.setGetInfo(mGetInfo);


        // 关联聊天人数
        mLiveMessagePager.setPeopleCount(peopleCount);

        // TODO: 2018/8/11 设置ircState
        //mLiveMessagePager.setLiveBll(mLiveBll);
        mLiveMessagePager.setIrcState(mExpIrcState);


        mLiveMessagePager.onModeChange(mExpIrcState.getMode());
        mLiveMessagePager.setIsRegister(true);

        // 03.22 设置统计日志的公共参数
        mLiveMessagePager.setLiveTermId(playBackEntity.getLiveId(), playBackEntity.getChapterId());

        // 隐藏锁屏按钮
        mLiveMessagePager.hideclock();
        rlLiveMessageContent.addView(mLiveMessagePager.getRootView(), params);


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
        String userId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
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
     * 处理禁言topic
     *
     * @param json
     * @throws Exception
     */
    protected void handleTopicSpeak(JSONObject json) throws Exception {
        if (!json.has("disable_speaking")) {
            return;
        }

        JSONArray disableSpeakingArray = json.getJSONArray("disable_speaking");
        boolean selfDisable = false;

        for (int i = 0; i < disableSpeakingArray.length(); i++) {
            JSONObject object = disableSpeakingArray.getJSONObject(i);
            String id = object.getString("id");

            if (id.equals("" + mIRCMessage.getNickname())) {
                selfDisable = true;
                break;
            }
        }

        if (mGetInfo.getLiveTopic().isDisable() != selfDisable) {
            mGetInfo.getLiveTopic().setDisable(true);
            mLiveMessagePager.onDisable(selfDisable, true);
        }

    }

    /**
     * 处理签到topic
     *
     * @param json
     * @throws Exception
     */
    protected void handleTopicCall(JSONObject json) throws Exception {

        if (expLiveInfo.getIsSignIn() == 2) {
            return;
        }

        if (!json.has("room_2")) {
            return;
        }

        json = json.getJSONObject("room_2");

        if (!json.has("isCalling")) {
            return;
        }

        boolean isCalling = json.getBoolean("isCalling");

        if (isCalling) {
            ClassSignEntity classSignEntity = new ClassSignEntity();
            classSignEntity.setStuName(mGetInfo.getStuName());
            classSignEntity.setTeacherName(mGetInfo.getTeacherName());
            classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
            classSignEntity.setStatus(1);
            expRollCallBll.openSignAuto(classSignEntity);
        }
    }

    /**
     * 处理聊天topic
     *
     * @param json
     * @throws Exception
     */
    protected void handleTopicChat(JSONObject json) throws Exception {
        if (!json.has("room_2")) {
            return;
        }

        json = json.getJSONObject("room_2");

        if (!json.has("isCalling")) {
            return;
        }

        boolean openchat = json.getBoolean("openchat");

        if (mExpIrcState.openchat() != openchat) {
            mExpIrcState.setChatOpen(openchat);
            mLiveMessagePager.onopenchat(openchat, LiveTopic.MODE_TRANING, true);
        }

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
                        mExpIrcState.setChatOpen(false);
                        isRoomChatAvailable = false;
                        logger.i("=====> teahcer close chat called end 11111");
                    }
                } else if (LocalCourseConfig.CATEGORY_OPEN_CHAT == chatEntity.getvCategory()) {
                    // 开启聊天
                    logger.i("=====> CATEGORY_OPEN_CHAT  22222:" + chatEntity.getvQuestionInsretTime() + ":" + playPosition);

                    if (playPosition == chatEntity.getvQuestionInsretTime()) {
                        logger.i("=====> teahcer open chat called begin");
                        mLiveMessagePager.onopenchat(true, "in-class", true);
                        mExpIrcState.setChatOpen(true);
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
            mExpIrcState.setChatOpen(false);
        } else {
            mLiveMessagePager.onopenchat(true, "in-class", !isRoomChatAvailable);
            mExpIrcState.setChatOpen(true);
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
        ProxUtil.getProxUtil().clear(this);
    }
}
