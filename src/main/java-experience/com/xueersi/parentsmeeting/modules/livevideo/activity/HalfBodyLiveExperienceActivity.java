package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExperienceIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.SimpleLiveBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.module.browser.event.BrowserEvent;
import com.xueersi.parentsmeeting.module.videoplayer.config.AvformatOpenInputError;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BackBusinessCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.business.IIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCCallback;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewActionIml;
import com.xueersi.parentsmeeting.modules.livevideo.business.NewIRCMessage;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.config.AllExperienceConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.BllConfigEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness.ExperienceQuitFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.learnfeedback.business.HalfBodyExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyExpMediaCtrBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.RoundProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 半身直播体验课
 *
 * @author chekun
 * created  at 2019/1/1 11:12
 */
public class HalfBodyLiveExperienceActivity extends LiveVideoActivityBase implements BaseLiveMediaControllerBottom
        .MediaChildViewClick {
    private String TAG = "HalfBodyLiveExperienceActivity";
    LiveBackBll liveBackBll;
    private LiveVideoSAConfig liveVideoSAConfig;
    /**
     * 横屏聊天信息
     */
    private ArrayList<LiveMessageEntity> liveMessageLandEntities = new ArrayList<>();
    private Long timer = 0L;
    private static final Object mIjkLock = new Object();
    private WeakHandler mHandler = new WeakHandler(null);
    private Long startTime;
    private Long mTotaltime;
    /**
     * 播放时长定时任务(心跳)
     */
    private final long mPlayDurTime = 60000;
    /**
     * 正在播放
     */
    private boolean isPlay = false;
    /**
     * 按Home键的进度模拟
     */
    private boolean firstTime = true;
    /**
     * 播放暂停状态的记录
     */
    private boolean pause = false;
    /**
     * 播放时长
     */
    long playTime = 0;
    /**
     * 上次播放统计开始时间
     */
    private Long lastPlayTime;
    /**
     * 播放异常统计开始时间
     */
    private Long errorPlayTime;
    private LiveGetInfo mGetInfo;
    private LiveHttpManager mHttpManager;
    /**
     * 聊天服务器 参数获取   接口地址
     */
    private List<String> chatCfgServerList;
    /**
     * 连接聊天服务器的 chnnel id
     */
    private String expChatId;
    private String sex;

    /**
     * 当前聊天 状态是否初始化完成了
     */
    private boolean isChatSateInited = false;
    /**
     * 视频地址列表
     */
    private List<String> mVideoPaths = new ArrayList<>();

    private int rePlayCount = 0;

    private static final int MAX_REPLAY_COUNT = 4;

    /**
     * 显示弹窗阈值 课程开始25min内进入课程的退出时显示弹窗
     */
    private static final long SHOW_QUIT_DIALOG_THRESHOLD = 1500000;
    private ExperienceQuitFeedbackBll experienceQuitFeedbackBll;
    /**
     * 接口返回的直播类型  1:普通三分屏  2：全身直播   6：半身直播
     */
    private int mPattern;

    ExperienceIRCBll experienceIRCBll;
    private final String IRC_CHANNEL_PREFIX = "#4L";
    /** 是否使用新IRC SDK*/
//    private boolean isNewIRC = false;


    /**
     * 播放时长，5分钟统计
     */
    private Runnable mPlayDuration = new Runnable() {
        @Override
        public void run() {
            if (isPlay && !isFinishing()) {
                // 上传心跳时间
                mHttpManager.uploadExperiencePlayingTime(mVideoEntity.getLiveId(), mVideoEntity.getChapterId(), 60L, new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.e("uploadexperiencetime:" + responseEntity.getJsonObject());
                    }
                });
                mHandler.postDelayed(this, mPlayDurTime);
            }
        }
    };

    /**
     * 体验课相关日志的埋点
     */
    LiveAndBackDebug ums = new SimpleLiveBackDebug() {
        @Override
        public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
            UmsAgentManager.umsAgentDebug(mContext, appID, eventId, mData);
        }

        @Override
        public void umsAgentDebugInter(String eventId, Map<String, String> mData) {
            mData.put("timestamp", System.currentTimeMillis() + "");
            mData.put("liveid", mVideoEntity.getLiveId());
            mData.put("termid", mVideoEntity.getChapterId());
            if (mGetInfo != null && mGetInfo.getStuName() != null) {
                mData.put("uname", mGetInfo.getStuName());
            } else {
                mData.put("uname", "");
            }
            UmsAgentManager.umsAgentOtherBusiness(HalfBodyLiveExperienceActivity.this, appID, UmsConstants
                            .uploadBehavior,
                    mData);

        }

        @Override
        public void umsAgentDebugPv(String eventId, Map<String, String> mData) {

        }

        @Override
        public void umsAgentDebugSys(String eventId, StableLogHashMap stableLogHashMap) {

        }

        @Override
        public void umsAgentDebugInter(String eventId, StableLogHashMap stableLogHashMap) {

        }

        @Override
        public void umsAgentDebugPv(String eventId, StableLogHashMap stableLogHashMap) {

        }
    };

    BaseLiveMediaControllerTop baseLiveMediaControllerTop;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    /** 初始进入播放器时的预加载界面 */
    /**
     * 老师不在直播间
     */
    private ImageView ivTeacherNotpresent;
    RelativeLayout bottomContent;
    /**
     * 缓冲提示
     */
    private TextView tvLoadingHint;
    /**
     * 互动题的布局
     */
    private RelativeLayout rlQuestionContent;
    private LiveViewAction liveViewAction;
    /**
     * 初始进入播放器时的预加载界面
     */
    private RelativeLayout rlFirstBackgroundView;
    /**
     * 是不是播放失败
     */
    boolean resultFailed = false;
    /**
     * 当前是否正在显示互动题
     */
    private boolean mIsShowQuestion = false;
    /**
     * 当前是否正在显示红包
     */
    private boolean mIsShowRedpacket = false;
    /**
     * 当前是否正在显示对话框
     */
    private boolean mIsShowDialog = false;
    /**
     * 是不是点击返回键或者点周围,取消互动题,而没有使用getPopupWindow
     */
    boolean mIsBackDismiss = true;
    /**
     * 视频节对象
     */
    VideoLivePlayBackEntity mVideoEntity;
    String beforeAttach;

    /**
     * 是否显示移动网络提示
     */
    private boolean mIsShowMobileAlert = true;

    /**
     * 我的课程业务层
     */
    LectureLivePlayBackBll lectureLivePlayBackBll;

    /**
     * 声明PopupWindow对象的引用
     */
    private PopupWindow mPopupWindow;

    /**
     * 试题对错弹框
     */
    PopupWindow mAnswerPopupWindow;

    /**
     * 播放路径名
     */
    private String mWebPath;
    /**
     * 节名称
     */
    private String mSectionName;
    /**
     * 显示互动题
     */
    private static final int SHOW_QUESTION = 0;
    /**
     * 没有互动题
     */
    private static final int NO_QUESTION = 1;

    /**
     * 体验课 直播类型
     */
    private static final int EXP_LIVE_TYPE = 4;

    /**
     * 加载视频提示
     */
    private ImageView ivLoading;
    private TextView tvLoadingContent;
    /**
     * 从哪个页面跳转
     */
    String where;
    int isArts;
    /**
     * 区分文理appid
     */
    String appID = UmsConstants.APP_ID;
    boolean IS_SCIENCE;
    /**
     * 本地视频
     */
    boolean islocal;
    static int times = -1;
    long createTime;
    String voicequestionEventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    private PopupWindow mWindow;

    private boolean isFirstGetResult = true;
    private EnglishH5Cache englishH5Cache;
    private HalfBodyExperienceLearnFeedbackBll learnFeedbackBll;

    @Override
    protected boolean onVideoCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        LiveAppBll.getInstance().registerAppEvent(this);
        // 设置不可自动横竖屏
        setAutoOrientation(false);
        Intent intent = getIntent();
        mVideoEntity = (VideoLivePlayBackEntity) intent.getExtras().getSerializable("videoliveplayback");
        islocal = intent.getBooleanExtra("islocal", false);
        rlFirstBackgroundView = (RelativeLayout) findViewById(R.id.rl_course_video_first_backgroud);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        // 加载横屏时互动题的列表布局
        rlQuestionContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_contents);
        rlQuestionContent.setVisibility(View.VISIBLE);
        liveViewAction = new LiveViewActionIml(this, null, rlQuestionContent);
        LiveHalfBodyExpMediaCtrBottom mediaControllerBottom = new LiveHalfBodyExpMediaCtrBottom(this,
                mMediaController, this);
        mediaControllerBottom.onModeChange(LiveTopic.MODE_CLASS);
        ProxUtil.getProxUtil().put(this, BaseLiveMediaControllerBottom.class, mediaControllerBottom);
        liveMediaControllerBottom = mediaControllerBottom;
        initAllBll();
        loadData();
        return true;
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
    protected void onVideoCreateEnd() {
    }

    private LiveGetInfo getRoomInitData() {
        LiveGetInfo getInfo = new LiveGetInfo(new LiveTopic());
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfoEntity = new LiveGetInfo.StudentLiveInfoEntity();
        studentLiveInfoEntity.setClassId(mVideoEntity.getClassId());
        studentLiveInfoEntity.setCourseId(mVideoEntity.getCourseId());
        getInfo.setStudentLiveInfo(studentLiveInfoEntity);

        getInfo.setId(mVideoEntity.getLiveId());
        getInfo.setLiveType(EXP_LIVE_TYPE);
        getInfo.setStuId(LiveAppUserInfo.getInstance().getStuId());
        getInfo.setStuSex(TextUtils.isEmpty(sex) ? "" : sex);

        String stuName = TextUtils.isEmpty(LiveAppUserInfo.getInstance().getRealName())
                ? LiveAppUserInfo.getInstance().getNickName() : LiveAppUserInfo.getInstance().getRealName();
        getInfo.setStuName(stuName);
        getInfo.setNickname(LiveAppUserInfo.getInstance().getNickName());
        getInfo.setHeadImgPath(LiveAppUserInfo.getInstance().getHeadImg());
        getInfo.setIsArts(isArts);
        return getInfo;
    }

    /**
     * 连接 聊天服务器
     */
    private void connectChatServer() {

        //避免多次 连接
        if (experienceIRCBll != null) {
            return;
        }
        mGetInfo = getRoomInitData();
        String channel = IRC_CHANNEL_PREFIX + expChatId;
        String chatRoomUid = "s_" + mGetInfo.getLiveType() + "_"
                + expChatId + "_" + mGetInfo.getStuId() + "_" + mGetInfo.getStuSex();
        logger.i("=====>connectChatServer:channel=" + channel + ":nickname =" +
                chatRoomUid);
        experienceIRCBll = new ExperienceIRCBll(this, expChatId, mGetInfo);
        experienceIRCBll.onCreate(channel, chatRoomUid);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            experienceIRCBll.addBll(businessBll);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {
        logger.i("onEvent:netWorkType=" + event.netWorkType);
        if (experienceIRCBll != null) {
            experienceIRCBll.onNetWorkChange(event);
        }
    }

    private void initAllBll() {
        liveBackBll = new LiveBackBll(this, mVideoEntity);
        mHttpManager = new LiveHttpManager(mContext);
        isArts = getIntent().getIntExtra("isArts", 0);
        if (isArts == 1) {
            IS_SCIENCE = false;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
        } else {
            IS_SCIENCE = true;
            liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
        }
        mHttpManager.setLiveVideoSAConfig(liveVideoSAConfig);
        String id = mVideoEntity.getLiveId();
        mHttpManager.addBodyParam("liveId", id);
    }


    /**
     * 初始化半身直播相关UI
     */
    private void initHalfBodyLiveUi() {

        baseLiveMediaControllerTop = new BaseLiveMediaControllerTop(this, mMediaController, this);
        mMediaController.setControllerTop(baseLiveMediaControllerTop);

        mMediaController.setControllerBottom(liveMediaControllerBottom, false);
        ivTeacherNotpresent = (ImageView) findViewById(R.id.iv_course_video_teacher_notpresent);
        ivTeacherNotpresent.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bottomContent = (RelativeLayout) findViewById(R.id.rl_course_video_live_question_content);
        bottomContent.setVisibility(View.VISIBLE);

        ivLoading = (ImageView) findViewById(R.id.iv_course_video_loading_bg);
        updateLoadingImage();
        tvLoadingHint = (TextView) findViewById(R.id.tv_course_video_loading_content);
        // 预加载布局中退出事件
        findViewById(R.id.iv_course_video_back).setVisibility(View.GONE);
        tvLoadingHint.setText("获取课程信息");
        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, baseLiveMediaControllerTop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        liveViewAction.addView(LiveVideoLevel.LEVEL_CTRl, liveMediaControllerBottom, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void loadData() {
//        mRedPacketDialog = new RedPacketAlertDialog(this, baseApplication, false);
        lectureLivePlayBackBll = new LectureLivePlayBackBll(HalfBodyLiveExperienceActivity.this, "");
        liveBackBll.setStuCourId(mVideoEntity.getStuCourseId());
        liveBackBll.setvPlayer(vPlayer);
        mVideoType = MobEnumUtil.VIDEO_LIVEPLAYBACK;
        where = getIntent().getStringExtra("where");
        // 如果加载不出来
        if (tvLoadingContent != null) {
            tvLoadingContent.setText("正在获取视频资源，请稍候");
        }
        // 视频名
        mSectionName = mVideoEntity.getPlayVideoName();
        // 播放视频
        mVideoPaths = mVideoEntity.getVideoPaths();
        if (mVideoPaths != null && !mVideoPaths.isEmpty()) {
            int index = new Random().nextInt(mVideoPaths.size());
            mWebPath = mVideoPaths.get(index);
        } else {
            mWebPath = mVideoEntity.getVideoPath();
        }
        liveBackBll.addBusinessShareParam("videoView", videoView);
        addBusiness(this);
        List<LiveBackBaseBll> businessBlls = liveBackBll.getLiveBackBaseBlls();
        for (LiveBackBaseBll businessBll : businessBlls) {
            businessBll.initViewF(liveViewAction, null, rlQuestionContent, new AtomicBoolean(mIsLand));
        }
        ProxUtil.getProxUtil().put(this, LiveVideoActivityBase.class, this);
        if (!MediaPlayer.getIsNewIJK()) {
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        } else {
//            playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_MP4);
            String videoPath;
            String url = mVideoEntity.getVideoPath();
            if (url.contains("http") || url.contains("https")) {
                videoPath = DoPSVideoHandle.getPSVideoPath(url);
            } else {
                videoPath = url;
            }
            playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
            setmDisplayName(mSectionName);
        }
        chatCfgServerList = getIntent().getStringArrayListExtra("roomChatCfgServerList");
        expChatId = getIntent().getStringExtra("expChatId");
        sex = getIntent().getStringExtra("sex");
        mPattern = getIntent().getIntExtra("pattern", 0);
    }


    private void addBusiness(Activity activity) {
        ArrayList<BllConfigEntity> bllConfigEntities = AllExperienceConfig.getHalfExperienceBusiness();
        for (int i = 0; i < bllConfigEntities.size(); i++) {
            LiveBackBaseBll liveBaseBll = creatBll(bllConfigEntities.get(i));
            if (liveBaseBll != null) {
                liveBackBll.addBusinessBll(liveBaseBll);
            }
        }
        learnFeedbackBll = new HalfBodyExperienceLearnFeedbackBll(activity, liveBackBll);
        liveBackBll.addBusinessBll(learnFeedbackBll);
        experienceQuitFeedbackBll = new ExperienceQuitFeedbackBll(activity, liveBackBll, false);
        experienceQuitFeedbackBll.setLiveVideo(this);
        liveBackBll.addBusinessBll(experienceQuitFeedbackBll);
        liveBackBll.onCreate();
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
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, HalfBodyLiveExperienceActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPlayOpenSuccess() {
        isPlay = true;
        rePlayCount = 0;
        mTotaltime = getDuration();
        Log.e("mqtt", "mTotaltime:" + mTotaltime);
        Log.e("mqtt", "seekto:" + mVideoEntity.getVisitTimeKey());
        // 03.22 统计用户进入体验播放器的时间
        StableLogHashMap logHashMap = new StableLogHashMap("enterRoom");
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE);
        ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE, logHashMap.getData());
        if (rlFirstBackgroundView != null) {
            rlFirstBackgroundView.setVisibility(View.GONE);
            if (baseLiveMediaControllerTop == null) {
                connectChatServer();
                initHalfBodyLiveUi();
                //preLoadCourseWare();
            }
            if (firstTime) {
                startTime = System.currentTimeMillis();
                firstTime = false;
            }

            Long keyTime = Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000 + (System.currentTimeMillis() -
                    startTime);
            seekTo(keyTime);
            if (mTotaltime < Long.parseLong(mVideoEntity.getVisitTimeKey()) * 1000) {
                // 03.21 提示直播已结束
                ivTeacherNotpresent.setVisibility(View.VISIBLE);
                ivTeacherNotpresent.setImageResource(R.drawable.live_halfbody_class_end);
                vPlayer.releaseSurface();
                vPlayer.stop();
                if (experienceQuitFeedbackBll != null) {
                    experienceQuitFeedbackBll.playComplete();
                }
                // 测试体验课播放器的结果页面
                lectureLivePlayBackBll.getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(),
                        getDataCallBack);
                return;
            }


        }
        // 心跳时间的统计
        mHandler.removeCallbacks(mPlayDuration);
        mHandler.postDelayed(mPlayDuration, mPlayDurTime);
    }

    /**
     * 预加载课件
     */
    private void preLoadCourseWare() {
        englishH5Cache = new EnglishH5Cache(this, mGetInfo);
        englishH5Cache.setHttpManager(mHttpManager);
        englishH5Cache.getCourseWareUrl();
    }

    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            // 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                ExperienceResult mData = (ExperienceResult) objData[0];
                // 测试体验课播放器的结果页面
                if (mData != null && isFirstGetResult) {
                    showPopupwinResult(mData);
                    isFirstGetResult = false;
                    setBackgroundAlpha(0.4f);
                }
            }
        }
    };

    private void showPopupwinResult(final ExperienceResult mData) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View result = inflater.inflate(R.layout.pop_halfbody_experience_learnback, null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow = new PopupWindow(result, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams
                .MATCH_PARENT, false);
        mWindow.setOutsideTouchable(false);
        mWindow.showAtLocation(result, Gravity.CENTER, 0, 0);
        RoundProgressBar mProgressbar = (RoundProgressBar) result.findViewById(R.id.roundProgressBar);
        TextView recommand = (TextView) result.findViewById(R.id.tv_detail_result);
        TextView beat = (TextView) result.findViewById(R.id.tv_result);
        TextView totalscore = (TextView) result.findViewById(R.id.tv_total_score);

        beat.setText("恭喜，你打败了" + mData.getBeat() + "%的学生");
        if (TextUtils.isEmpty(mData.getRecommend())) {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("赶快去报班继续提高成绩吧");
        } else {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("推荐您报名" + mData.getRecommend());
        }
        totalscore.setText(mData.getCorrect() + "%");
        mProgressbar.setMax(100);
        if (mData.getCorrect() > 0) {
            mProgressbar.setProgress(mData.getCorrect());
        } else {
            mProgressbar.setProgress(0);
        }
        ImageButton shut = (ImageButton) result.findViewById(R.id.ib_shut);
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                showPopupwinFeedback();
                mWindow = null;
            }
        });
        Button chat = (Button) result.findViewById(R.id.bt_chat);
        if (TextUtils.isEmpty(mData.getWechatNum())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mData.getWechatNum());
                Toast.makeText(HalfBodyLiveExperienceActivity.this, "您已复制老师微信号，快去添加吧!", Toast.LENGTH_LONG).show();
            }
        });
        Button apply = (Button) result.findViewById(R.id.bt_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.getUrl() != null) {
                    BrowserActivity.openBrowser(HalfBodyLiveExperienceActivity.this, mData.getUrl());
                } else {
                    Toast.makeText(HalfBodyLiveExperienceActivity.this, "数据异常", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showPopupwinFeedback() {
        setBackgroundAlpha(1.0f);
        learnFeedbackBll.showWindow();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onPlayOpenStart() {
        setFirstBackgroundVisible(View.VISIBLE);
        findViewById(R.id.probar_course_video_loading_tip_progress).setVisibility(View.VISIBLE);
    }

    public void setFirstBackgroundVisible(int visible) {
        rlFirstBackgroundView.setVisibility(visible);
        if (visible == View.GONE) {
            ivTeacherNotpresent.setVisibility(View.GONE);
        }
    }

    protected void updateLoadingImage() {
        FooterIconEntity footerIconEntity = mShareDataManager.getCacheEntity(FooterIconEntity.class, false,
                ShareBusinessConfig.SP_EFFICIENT_FOOTER_ICON, ShareDataManager.SHAREDATA_NOT_CLEAR);
        if (footerIconEntity != null) {
            String loadingNoClickUrl = footerIconEntity.getNoClickUrlById("6");
            if (loadingNoClickUrl != null && !"".equals(loadingNoClickUrl)) {
                ImageLoader.with(this).load(loadingNoClickUrl).placeHolder(R.drawable.livevideo_cy_moren_logo_normal)
                        .error(R.drawable.livevideo_cy_moren_logo_normal).into(ivLoading);
            }
        }
    }

    /**
     * 03.13 根据播放的进度获取互动题
     */
    @Override
    protected void playingPosition(long currentPosition, long duration) {
        super.playingPosition(currentPosition, duration);
        if (NetWorkHelper.getNetWorkState(mContext) == NetWorkHelper.NO_NETWORK) {
            return;
        }
        // 扫描互动题
        scanQuestion(currentPosition);
    }

    public void scanQuestion(long position) {
        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
            return;
        }
        liveBackBll.scanQuestion(position);
    }


    private VideoQuestionEntity getOpenChatEntity(int playPosition) {
        return null;
    }

    private VideoQuestionEntity getCloseChatEntity(int playPosition) {
        return null;
    }


    @Override
    protected void resultComplete() {
        // 播放完毕直接退出
//        onUserBackPressed();
        // 直播结束后，显示结束的提示图片
        isPlay = false;
        ivTeacherNotpresent.setVisibility(View.VISIBLE);
        ivTeacherNotpresent.setBackgroundResource(R.drawable.live_halfbody_class_end);

//        liveBackBll.
        // 获取学生的学习反馈
        lectureLivePlayBackBll.getExperienceResult(mVideoEntity.getChapterId(), mVideoEntity.getLiveId(),
                getDataCallBack);
        EventBus.getDefault().post(new BrowserEvent.ExperienceLiveEndEvent(1));
        if (experienceQuitFeedbackBll != null) {
            experienceQuitFeedbackBll.playComplete();
        }
        mHandler.removeCallbacks(mPlayDuration);

    }

    @Override
    protected void onRefresh() {
        if (LiveAppBll.getInstance().isNetWorkAlert()) {
            videoBackgroundRefresh.setVisibility(View.GONE);
            if (!MediaPlayer.getIsNewIJK()) {
                playNewVideo(Uri.parse(mWebPath), mSectionName);
            } else {
//                playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_MP4);
                String videoPath;
                String url = mVideoEntity.getVideoPath();
                if (url.contains("http") || url.contains("https")) {
                    videoPath = DoPSVideoHandle.getPSVideoPath(url);
                } else {
                    videoPath = url;
                }
                changeNextLine();
//                playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
                setmDisplayName(mSectionName);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlay = false;
        // 03.22 统计用户离开体验播放器的时间
        StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayExit");
        logHashMap.put("liveid", mVideoEntity.getLiveId());
        logHashMap.put("termid", mVideoEntity.getChapterId());
        logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_EXIT);
        ums.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_EXIT, logHashMap.getData());
        LiveAppBll.getInstance().unRegisterAppEvent(this);
        liveBackBll.onDestroy();
        if (experienceIRCBll != null) {
            experienceIRCBll.onDestory();
        }
        ProxUtil.getProxUtil().clear(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        isPlay = false;
        pause = true;
        vPlayer.releaseSurface();
        vPlayer.stop();
//        vPlayer.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!MediaPlayer.getIsNewIJK()) {
            playNewVideo(Uri.parse(mWebPath), mSectionName);
        } else {
//            playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_MP4);
            String videoPath;
            String url = mVideoEntity.getVideoPath();
            if (url.contains("http") || url.contains("https")) {
                videoPath = DoPSVideoHandle.getPSVideoPath(url);
            } else {
                videoPath = url;
            }
            playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
            setmDisplayName(mSectionName);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onMediaViewClick(View child) {

    }

    @Override
    protected void resultFailed(int arg1, int arg2) {
        resultFailed = true;
        mIsShowQuestion = mIsShowRedpacket = false;
        String errcode = "";
        String errmsg = "";
        AvformatOpenInputError error = AvformatOpenInputError.getError(arg2);
        if (error != null) {
            errcode = String.valueOf(error.getNum());
            errmsg = error.getTag();
        } else {
            errcode = String.valueOf(arg2);
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("os", "Android");
        mData.put("currenttime", String.valueOf(System.currentTimeMillis()));
        mData.put("playurl", mWebPath);
        mData.put("errcode", errcode);
        mData.put("errmsg", errmsg);
        mData.put("ip", IpAddressUtil.USER_IP);
        mData.put("liveid", mVideoEntity.getLiveId() == null ? "" : mVideoEntity.getLiveId());
        mData.put("orderid", mVideoEntity.getChapterId());
        ums.umsAgentDebugSys(LiveVideoConfig.STAND_EXPERIENCE_LIVE_PLAY_ERROR, mData);
        //循环更换视频地址
        if (mVideoPaths != null && !mVideoPaths.isEmpty()) {
            for (int i = 0; i < mVideoPaths.size(); i++) {
                if (mWebPath.equals(mVideoPaths.get(i))) {
                    mWebPath = mVideoPaths.get((i + 1) % mVideoPaths.size());
                    break;
                }
            }
        } else {
            mWebPath = mVideoEntity.getVideoPath();
        }
        if (rePlayCount < MAX_REPLAY_COUNT) {
            rePlayCount++;
            if (!MediaPlayer.getIsNewIJK()) {
                playNewVideo(Uri.parse(mWebPath), mSectionName);
            } else {
//                playPSVideo(mGetInfo.getChannelname(), MediaPlayer.VIDEO_PROTOCOL_MP4);
                String videoPath;
                String url = mVideoEntity.getVideoPath();
                if (url.contains("http") || url.contains("https")) {
                    videoPath = DoPSVideoHandle.getPSVideoPath(url);
                } else {
                    videoPath = url;
                }
                changeNextLine();
//                playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
                setmDisplayName(mSectionName);
            }
        } else {
            super.resultFailed(arg1, arg2);
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
}
