package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.GetStuActiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.action.MultModeAction;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.cloud.GroupGameUpload;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.config.GroupGameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.CleanUpEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.PagerShowTime;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.ScoreEnergy;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.SpeechResult;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.VidooCannonEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.BaseCourseGroupItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupMyItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupNoItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.GroupCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveAudioManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.GroupGameLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

/**
 * @Date on 2019/3/15 18:31
 * @Author linyuqiang
 * @Description
 */
public class GroupGameMultNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager, LiveVideoPoint.VideoSizeChange {

    /**
     * 加载中
     */
    private RelativeLayout rlSubjectLoading;
    /**
     * 课件接口失败刷新
     */
    private ImageView ivCourseRefresh;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;
    private TextView tvMyVoiceTip;
    private LinearLayout llCourseItemContent;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String url;
    /**
     * 新课件缓存
     */
    private GroupCourseCache newCourseCache;
    /** 新课件是否是预加载 */
    private boolean ispreload;
    private LiveAndBackDebug liveAndBackDebug;
    /** 文理英属性 */
    private int isArts = LiveVideoSAConfig.ART_EN;
    private LiveAudioManager liveAudioManager;
    /** 最大音量 */
    private int mMaxVolume;
    /** 当前音量 */
    private int mVolume = 0;
    //    private NewCourseSec newCourseSec;
    private GroupGameTestInfosEntity mGroupGameTestInfosEntity;
    private List<GroupGameTestInfosEntity.TestInfoEntity> tests = new ArrayList<>();
    private GroupGameTestInfosEntity.TestInfoEntity testEntity;
    /** 每个题是作答次数 */
    private HashMap<String, Integer> wordCount = new HashMap<>();
    /** 进入时间 */
    private long enterTime;
    /** 语音炮弹单当前页数 */
    private int currentAnswerIndex = 0;
    private boolean gameOver = false;
    private String learningStage;
    private String vad_max_sec = "90";
    private String liveId;
    private VideoQuestionLiveEntity detailInfo;
    private String gameType;
    /**
     * 语音评测
     */
    protected SpeechUtils mIse;
    private SpeechParamEntity mParam;
    /**
     * 语音保存位置
     */
    private File saveVideoFile;
    /**
     * 在网页中嵌入js，只嵌入一次
     */
    private boolean addJs = false;
    private boolean isForce = false;
    private boolean isDestory = false;
    private int mPagerIndex = 0;
    private InteractiveTeam interactiveTeam;
    private WorkerThreadPool mWorkerThread;
    private HashMap<String, BaseCourseGroupItem> courseGroupItemHashMap = new HashMap<>();
    private LiveGetInfo liveGetInfo;
    private int stuid;
    private HashMap<String, CleanUpEntity> cleanUpEntities = new HashMap<>();
    private HashMap<String, VidooCannonEntity> vidooCannonEntities = new HashMap<>();
    private String speechContent = "";
    /** 总的答题，语音炮弹是够数移除。clean up是移除然后增加到用户信息里 */
    private List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> allAnswerList = new ArrayList<>();
    /** 评测时候的语音 */
    private List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> speechAnswerList = new ArrayList<>();
    /** 语音炮弹页面展示时间 */
    private HashMap<Integer, PagerShowTime> voicePagerShowTimeHashMap = new HashMap<>();
    /** cleanUp页面展示时间 */
    private HashMap<Integer, PagerShowTime> cleanUpPagerShowTimeHashMap = new HashMap<>();
    //    private List<ResultEntity> allScoreList = new ArrayList<>();
    private HashMap<Integer, List<SpeechResult>> scoreHashmap = new HashMap<>();
    /** 最小分数 */
    private int minscore = 70;
    private GetStuActiveTeam getStuActiveTeam;
    private TcpMessageReg tcpMessageReg;
    /** 接收游戏的消息 */
    private TcpMessageAction tcpMessageAction;
    /** 接收用户禁用音视频的消息 */
    private TeamVideoAudioMessage teamVideoAudioMessage;
    private MultModeAction multModeAction;
    private EvaluatorIng evaluatorIng;
    private PreLoad preLoad;
    private JSONArray userAnswer = new JSONArray();
    private int rightNum = 0;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private GroupGameUpload groupGameUpload;

    public GroupGameMultNativePager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onClose) {
        super(context);
        this.detailInfo = detailInfo;
        gameType = detailInfo.type;
        this.url = englishH5Entity.getUrl();
        this.onClose = onClose;
        this.liveGetInfo = liveGetInfo;
        stuid = Integer.parseInt(liveGetInfo.getStuId());
        this.learningStage = liveGetInfo.getStudentLiveInfo().getLearning_stage();
        this.liveId = liveGetInfo.getId();
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
        preLoad = new MiddleSchool();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame_multiple, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        llCourseItemContent = view.findViewById(R.id.ll_livevideo_course_item_content);
        tvMyVoiceTip = view.findViewById(R.id.tv_livevideo_course_item_my_tip);
        return view;
    }

    @Override
    public void initData() {
        mLogtf.addCommon("testid", "" + detailInfo.id);
        groupGameUpload = new GroupGameUpload(mContext, liveId, detailInfo.id);
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(0, 0);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.request(null);
        }
        setVoice();
        //网页消息
        if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
            multModeAction = new CleanUpOnMessage();
        } else {
            GroupGameLog.sno2(liveAndBackDebug, detailInfo.id, 1);
            multModeAction = new VoiceCannonOnMessage();
        }
//        startSpeechRecognize();
        getStuActiveTeam = ProxUtil.getProxUtil().get(mContext, GetStuActiveTeam.class);
        //有战队pk，才有这种题的多人.目前不会发生了
        if (getStuActiveTeam != null) {
            interactiveTeam = getStuActiveTeam.getStuActiveTeam(false, null);
            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
            if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    //老师发新题后，视频窗右上角的火焰应该从0开始计数
                    teamMemberEntity.energy = 0;
                    CleanUpEntity cleanUpEntity = new CleanUpEntity();
                    cleanUpEntity.teamMemberEntity = teamMemberEntity;
                    cleanUpEntities.put("" + teamMemberEntity.id, cleanUpEntity);
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    //老师发新题后，视频窗右上角的火焰应该从0开始计数
                    teamMemberEntity.energy = 0;
                    VidooCannonEntity vidooCannonEntity = new VidooCannonEntity();
                    vidooCannonEntity.teamMemberEntity = teamMemberEntity;
                    vidooCannonEntities.put("" + teamMemberEntity.id, vidooCannonEntity);
                }
            }
            //有战队pk，才使用有tcp
            tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
            if (tcpMessageReg != null) {
                if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                    CleanUpOnMessage cleanUpOnMessage = (CleanUpOnMessage) multModeAction;
                    CleanUpTcpMessage cleanUpTcpMessage = new CleanUpTcpMessage();
                    cleanUpTcpMessage.cleanUpOnMessage = cleanUpOnMessage;
                    tcpMessageAction = cleanUpTcpMessage;
                    evaluatorIng = new CleanEvaluatorIng();
                } else {
                    VoiceCannonOnMessage voiceCannnon = (VoiceCannonOnMessage) multModeAction;
                    VoiceProjectile voiceProjectile = new VoiceProjectile();
                    voiceProjectile.voiceCannonOnMessage = voiceCannnon;
                    tcpMessageAction = voiceProjectile;
                    evaluatorIng = new VoiceCannnon();
                }
            }
            joinChannel(entities);
        }
        newCourseCache = new GroupCourseCache(mContext, liveId, liveGetInfo.isNewCourse());
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
                    CrashReport.postCatchedException(new LiveException(TAG));
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());

        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, multModeAction), "xesApp");
//        wvSubjectWeb.loadUrl(TEST_URL);
    }

    private void setVoice() {
        liveAudioManager = new LiveAudioManager(mContext, "GroupGameMultNativePager");
        mMaxVolume = liveAudioManager.getmMaxVolume();
        mVolume = liveAudioManager.getmVolume();
        int v = (int) (0.3f * mMaxVolume);
        liveAudioManager.setVolume(v);
    }

    @Override
    public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) llCourseItemContent.getLayoutParams();
        int rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
        if (lp.rightMargin != rightMargin) {
            lp.rightMargin = rightMargin;
            llCourseItemContent.setLayoutParams(lp);
        }
    }

    private class VoiceCannonTurnRun implements Runnable {
        int pagerNum;
        int time;

        public VoiceCannonTurnRun(int pagerNum, int time) {
            this.pagerNum = pagerNum;
            this.time = time;
            logger.d("VoiceCannonTurnRun:pagerNum=" + pagerNum + ",time=" + time + ",all=" + allAnswerList.size());
        }

        @Override
        public void run() {
            JSONObject jsonData = new JSONObject();
            try {
                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = test.getAnswerList();
                boolean remove = false;
                if (!allAnswerList.isEmpty()) {
                    if (pagerNum < answerList.size()) {
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(pagerNum);
                        remove = allAnswerList.remove(answersEntity);
                        if (remove) {
                            currentAnswerIndex++;
                            createSpeechContent("VoiceCannonTurnRun", false);
                        }
                    }
                }
                //到了最后一页
                if (currentAnswerIndex > answerList.size() - 1) {
                    String allId = "";
                    for (int i = 0; i < allAnswerList.size(); i++) {
                        allId += allAnswerList.get(i) + ",";
                    }
                    mLogtf.d("VoiceCannonTurnRun:pagerNum=" + pagerNum + ",currentAnswerIndex=" + currentAnswerIndex + ",allId=" + allId);
                    allAnswerList.clear();
                    createSpeechContent("VoiceCannonTurnRun:end", true);
                } else {
                    //大于1页的时候再翻页
                    if (currentAnswerIndex - pagerNum == 1) {
                        jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                        jsonData.put("score", -1);
//                jsonData.put("studentNum", -1);
                        jsonData.put("turnToPageNum", currentAnswerIndex);
                        wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                        GroupGameLog.sno4(liveAndBackDebug, detailInfo.id, currentAnswerIndex + "", 1);
                        reStartSpeechRecognize();
                    }
                    mLogtf.d("VoiceCannonTurnRun:pagerNum=" + pagerNum + ",currentAnswerIndex=" + currentAnswerIndex + ",remove=" + remove);
                }
            } catch (Exception e) {
                logger.e("VoiceCannonTurnRun:run", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    /**
     * 语音炮弹的网页消息
     */
    class VoiceCannonOnMessage implements MultModeAction {
        VoiceCannonTurnRun turnRun;

        @Override
        public void postMessage(String where, final JSONObject message, String origin) {
            try {
                String type = message.getString("type");
                logger.d("postMessage:type=" + type + ",message=" + message);
                if (CourseMessage.REC_answer.equals(type)) {
                    onAnswer(message);
                } else if (CourseMessage.REC_loadComplete.equals(type)) {
                    onLoadComplete(where, message);
                } else if (CourseMessage.REC_CoursewareDoing.equals(type)) {
                    onCoursewareDoing(where, message);
                }
            } catch (JSONException e) {
                mLogtf.e("VoiceCannonOnMessage:postMessage", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        private void onLoadComplete(String where, JSONObject message) {
            if (enterTime == 0) {
                enterTime = System.currentTimeMillis();
            }
            GroupGameLog.sno3(liveAndBackDebug, detailInfo.id, 1);
            GroupGameLog.sno4(liveAndBackDebug, detailInfo.id, "0", 1);
            final GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
            try {
                mLogtf.d("onLoadComplete:totaltime=" + test.getTotalTime());
                //时间没结束,才倒计时
                if (test.getTotalTime() > 0) {
                    int time = test.getAnswerList().get(currentAnswerIndex).getSingleTime() + 1;
                    if (turnRun == null) {
                        turnRun = new VoiceCannonTurnRun(currentAnswerIndex, time);
                        handler.postDelayed(turnRun, time * 1000);
                    } else {
                        mLogtf.d("onLoadComplete:pagerNum=" + turnRun.pagerNum + "," + currentAnswerIndex);
                        if (turnRun.pagerNum < currentAnswerIndex) {
                            handler.removeCallbacks(turnRun);
                            turnRun = new VoiceCannonTurnRun(currentAnswerIndex, time);
                            handler.postDelayed(turnRun, time * 1000);
                        }
                    }
                }
            } catch (Exception e) {
                mLogtf.e("onLoadComplete", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
            //当前页
            PagerShowTime pagerShowTime = voicePagerShowTimeHashMap.get(currentAnswerIndex);
            if (pagerShowTime != null) {
                if (pagerShowTime.start == 0) {
                    pagerShowTime.start = System.currentTimeMillis();
                }
            }
            //上一页
            pagerShowTime = voicePagerShowTimeHashMap.get(currentAnswerIndex - 1);
            if (pagerShowTime != null) {
                pagerShowTime.end = System.currentTimeMillis();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    preLoad.onStop();
                    ivWebViewRefresh.setVisibility(View.GONE);
                    if (test.getTotalTime() > 0) {
                        coursewareOnloading(currentAnswerIndex);
                    }
                    if (!gameOver) {
                        startSpeechRecognize();
                    }
                }
            });
        }

        private void onCoursewareDoing(String where, final JSONObject message) {
            if (LiveQueConfig.GET_ANSWERTYPE_WHERE_LISTENER.equals(where)) {
                try {
                    //当前页
                    PagerShowTime pagerShowTime = voicePagerShowTimeHashMap.get(currentAnswerIndex);
                    if (pagerShowTime != null) {
                        if (pagerShowTime.start == 0) {
                            pagerShowTime.start = System.currentTimeMillis();
                        }
                    }
                    //上一页
                    pagerShowTime = voicePagerShowTimeHashMap.get(currentAnswerIndex - 1);
                    if (pagerShowTime != null) {
                        pagerShowTime.end = System.currentTimeMillis();
                    }
                    if (currentAnswerIndex >= testEntity.getAnswerList().size()) {
                        return;
                    }
                    int time = testEntity.getAnswerList().get(currentAnswerIndex).getSingleTime() + 1;
                    if (turnRun == null) {
                        turnRun = new VoiceCannonTurnRun(currentAnswerIndex, time);
                        handler.postDelayed(turnRun, time * 1000);
                    } else {
                        mLogtf.d("onCoursewareDoing:pagerNum=" + turnRun.pagerNum + "," + currentAnswerIndex);
                        if (turnRun.pagerNum < currentAnswerIndex) {
                            handler.removeCallbacks(turnRun);
                            turnRun = new VoiceCannonTurnRun(currentAnswerIndex, time);
                            handler.postDelayed(turnRun, time * 1000);
                        }
                    }
                } catch (Exception e) {
                    mLogtf.e("onCoursewareDoing", e);
                    CrashReport.postCatchedException(new LiveException(TAG, e));
                }
            } else {
                logger.e("onCoursewareDoing:where=" + where);
            }
        }

        private void coursewareOnloading(final int pageNum) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                        JSONObject resultData = new JSONObject();
                        resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
                        resultData.put("pageNum", pageNum);
//                        int playTime = (int) (System.currentTimeMillis() - enterTime) / 1000;
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = testInfoEntity.getAnswerList().get(pageNum);
                        resultData.put("restTime", answersEntity.getSingleTime());
                        Integer integer = wordCount.get("" + pageNum);
                        mLogtf.d("coursewareOnloading:pageNum=" + pageNum + ",integer=" + integer + ",singleTime=" + answersEntity.getSingleTime());
                        if (integer == null) {
                            resultData.put("currentRight", 0);
                        } else {
                            resultData.put("currentRight", integer);
                        }
                        resultData.put("isSingle", false);
                        sendToCourseware(wvSubjectWeb, resultData, "*");
                    } catch (Exception e) {
                        mLogtf.e("coursewareOnloading", e);
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
            });
        }

        private void coursewareDoingLoad(final int pageNum) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = testInfoEntity.getAnswerList().get(pageNum);
                        try {
                            JSONObject resultData = new JSONObject();
                            resultData.put("type", CourseMessage.SEND_coursewareDoingLoad);
//                        int playTime = (int) (System.currentTimeMillis() - enterTime) / 1000;
                            resultData.put("restTime", answersEntity.getSingleTime());
                            Integer integer = wordCount.get("" + pageNum);
                            mLogtf.d("coursewareDoingLoad:pageNum=" + pageNum + ",integer=" + integer + ",singleTime=" + answersEntity.getSingleTime());
                            if (integer == null) {
                                resultData.put("currentRight", 0);
                            } else {
                                resultData.put("currentRight", integer);
                            }
                            resultData.put("turnToPageNum", pageNum);
                            sendToCourseware(wvSubjectWeb, resultData, "*");
                            reStartSpeechRecognize();
                        } catch (Exception e) {
                            mLogtf.e("coursewareDoingLoad", e);
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                        //恢复数据，翻页
                        int time = answersEntity.getSingleTime() + 1;
                        if (turnRun == null) {
                            turnRun = new VoiceCannonTurnRun(pageNum, time);
                            handler.postDelayed(turnRun, time * 1000);
                        } else {
                            mLogtf.d("coursewareDoingLoad:pageNum=" + turnRun.pagerNum + ",pageNum=" + pageNum);
                            if (turnRun.pagerNum < pageNum) {
                                handler.removeCallbacks(turnRun);
                                turnRun = new VoiceCannonTurnRun(pageNum, time);
                                handler.postDelayed(turnRun, time * 1000);
                            }
                        }
                    } catch (Exception e) {
                        mLogtf.e("coursewareDoingLoad", e);
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
            });
        }

        @Override
        public void onDestory() {
            if (turnRun != null) {
                handler.removeCallbacks(turnRun);
            }
        }
    }

    private class CleanUpTurnRun implements Runnable {
        int pagerNum;
        int time;

        public CleanUpTurnRun(int pagerNum, int time) {
            this.pagerNum = pagerNum;
            this.time = time;
            logger.d("CleanUpTurnRun:pagerNum=" + pagerNum + ",time=" + time);
        }

        @Override
        public void run() {
            try {
                int oldSize = allAnswerList.size();
                if (oldSize != 0) {
                    allAnswerList.clear();
                    createSpeechContent("CleanUpTurnRun", false);
                }
                mLogtf.d("CleanUpTurnRun:pagerNum=" + pagerNum + ",oldSize=" + oldSize);
            } catch (Exception e) {
                mLogtf.e("CleanUpTurnRun:run", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    /**
     * cleanup的网页消息
     */
    class CleanUpOnMessage implements MultModeAction {
        CleanUpTurnRun cleanUpTurnRun;

        @Override
        public void postMessage(String where, final JSONObject message, String origin) {
            try {
                String type = message.getString("type");
                logger.d("postMessage:type=" + type + ",message=" + message);
                if (CourseMessage.REC_answer.equals(type)) {
                    onAnswer(message);
                } else if (CourseMessage.REC_loadComplete.equals(type)) {
                    onLoadComplete(where, message);
                } else if (CourseMessage.REC_CoursewareDoing.equals(type)) {
                    onCoursewareDoing(where, message);
                }
            } catch (JSONException e) {
                mLogtf.e("CleanUpOnMessage:postMessage", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        private void onLoadComplete(String where, JSONObject message) {
            if (enterTime == 0) {
                enterTime = System.currentTimeMillis();
            }
            final GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
            mLogtf.d("onLoadComplete:totalTime=" + test.getTotalTime());
            //时间没结束,才倒计时
            if (test.getTotalTime() > 0) {
                if (cleanUpTurnRun == null) {
                    int time = test.getTotalTime() + 1;
                    cleanUpTurnRun = new CleanUpTurnRun(0, time);
                    handler.postDelayed(cleanUpTurnRun, time * 1000);
                }
            }
            PagerShowTime pagerShowTime = cleanUpPagerShowTimeHashMap.get(0);
            if (pagerShowTime != null) {
                if (pagerShowTime.start == 0) {
                    pagerShowTime.start = System.currentTimeMillis();
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    preLoad.onStop();
                    ivWebViewRefresh.setVisibility(View.GONE);
                    if (test.getTotalTime() > 0) {
                        onScene("onLoadComplete");
                    }
                    if (!gameOver) {
                        startSpeechRecognize();
                    }
                }
            });
        }

        private void onScene(String method) {
            mLogtf.d("onScene:method=" + method + ",currentAnswerIndex=" + currentAnswerIndex);
            try {
                GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                JSONObject resultData = new JSONObject();
                resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
                resultData.put("pageNum", currentAnswerIndex);
                int playTime = (int) (System.currentTimeMillis() - enterTime) / 1000;
                resultData.put("restTime", testInfoEntity.getTotalTime() - playTime);
                JSONArray studentInfo = new JSONArray();
                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    JSONObject student = new JSONObject();
                    student.put("studentNum", i + 1);
                    student.put("name", teamMemberEntity.name);
                    student.put("avatar", teamMemberEntity.headurl);
//                    student.put("avatar", "http://xesfile.oss-cn-beijing.aliyuncs.com/nrcpb/cjtsg5ybb0000ywow5xr4ne1j.png");
                    {
                        JSONObject rankInfo = new JSONObject();
                        rankInfo.put("grading", 1);
                        rankInfo.put("star", 1);
                        student.put("rankInfo", rankInfo);
                    }
                    {
                        JSONArray rightItem = new JSONArray();
                        CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + teamMemberEntity.id);
                        if (cleanUpEntity != null) {
                            List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = cleanUpEntity.rightAnswerList;
                            for (int j = 0; j < answerList.size(); j++) {
                                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(j);
                                JSONObject rightObj = new JSONObject();
                                rightObj.put("rightId", answersEntity.getId());
                                rightObj.put("getFireCount", answersEntity.getGetFireCount());
                                rightItem.put(rightObj);
                            }
                        }
                        student.put("rightItem", rightItem);
                    }
                    studentInfo.put(student);
                }
                resultData.put("studentInfo", studentInfo);
                mLogtf.d("coursewareOnloading:pageNum=" + entities.size() + ",playTime=" + playTime);
                sendToCourseware(wvSubjectWeb, resultData, "*");
            } catch (Exception e) {
                mLogtf.e("onScene", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        @Override
        public void onDestory() {
            if (cleanUpTurnRun != null) {
                handler.removeCallbacks(cleanUpTurnRun);
            }
        }
    }

    private void joinChannel(ArrayList<TeamMemberEntity> entities) {
        mWorkerThread = new WorkerThreadPool(mContext, stuid, false, true);
        mWorkerThread.eventHandler().addEventHandler(agEventHandler);
        mWorkerThread.setEnableLocalVideo(true);
        mWorkerThread.setOnEngineCreate(new WorkerThreadPool.OnEngineCreate() {
            @Override
            public void onEngineCreate(final RtcEngine mRtcEngine) {
                VideoEncoderConfiguration.VideoDimensions dimensions = new VideoEncoderConfiguration.VideoDimensions(320, 240);
                VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(dimensions,
                        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
                        VideoEncoderConfiguration.STANDARD_BITRATE,
                        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE);
                int setVideoEncoder = mRtcEngine.setVideoEncoderConfiguration(configuration);
                logger.d("onEngineCreate:setVideoEncoder=" + setVideoEncoder);
            }
        });
        addTeam(entities);
//        mWorkerThread.start();
//        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        String channel = liveGetInfo.getId() + "_" + liveGetInfo.getStudentLiveInfo().getClassId() + "_" + getStuActiveTeam.getPkTeamEntity().getPkTeamId() + "_" + interactiveTeam.getInteractive_team_id();
        mLogtf.d("joinChannel:channel=" + channel);
        mWorkerThread.joinChannel("", channel, stuid, new WorkerThreadPool.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                logger.d("onJoinChannel:joinChannel=" + joinChannel);
            }
        });
    }

    private void leaveChannel() {
        if (mWorkerThread != null) {
            mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThreadPool.OnLeaveChannel() {
                @Override
                public void onLeaveChannel(int leaveChannel) {
                    logger.d("leaveChannel:mWorkerThread.joinstart");
                }
            });
            mWorkerThread.eventHandler().removeEventHandler(agEventHandler);
            mWorkerThread.exit();
        }
    }

    private AGEventHandler agEventHandler = new AGEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("onFirstRemoteVideoDecoded:uid=" + uid + ",courseGroupItem=null?" + (courseGroupItem == null));
            if (courseGroupItem != null) {
                doRenderRemoteUi(uid, courseGroupItem);
            }
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, final int state) {
            final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("onRemoteVideoStateChanged:uid=" + uid + ",state=" + state + ",courseGroupItem=null?" + (courseGroupItem == null));
            if (courseGroupItem != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mWorkerThread == null) {
                            return;
                        }
                        courseGroupItem.onRemoteVideoStateChanged(state);
                    }
                });
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            mLogtf.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
            if (stuid == uid) {
                BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
                if (courseGroupItem != null) {
                    preview(uid, courseGroupItem);
                }
            }
        }

        @Override
        public void onUserJoined(final int uid, final int elapsed) {
            final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            mLogtf.d("onUserJoined:uid=" + uid + ",elapsed=" + elapsed + ",item=null?" + (courseGroupItem == null));
            if (courseGroupItem != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        courseGroupItem.onUserJoined();
                    }
                });
            } else {
                final RtcEngine rtcEngine = mWorkerThread.getRtcEngine();
                if (rtcEngine != null) {
                    mWorkerThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            rtcEngine.muteRemoteAudioStream(uid, true);
                        }
                    });
                }
            }
        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            mLogtf.d("onUserOffline:uid=" + uid + ",reason=" + reason);
            final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            if (courseGroupItem != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        courseGroupItem.onUserOffline();
                    }
                });
            }
        }

        @Override
        public void onError(int err) {
            mLogtf.d("onError:err=" + err);
        }

        @Override
        public void onVolume(int volume) {

        }
    };

    private void doRenderRemoteUi(final int uid, final BaseCourseGroupItem courseGroupItem) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mWorkerThread == null) {
                    return;
                }
                SurfaceView surfaceV = RtcEngine.CreateRendererView(mContext);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mWorkerThread.getRtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_FIT, uid));
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    private void preview(final int uid, final BaseCourseGroupItem courseGroupItem) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mWorkerThread == null) {
                    return;
                }
                SurfaceView surfaceV = RtcEngine.CreateRendererView(mContext);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mWorkerThread.preview(true, surfaceV, uid);
                courseGroupItem.doRenderRemoteUi(surfaceV);
            }
        });
    }

    private void addTeam(final ArrayList<TeamMemberEntity> entities) {
        //发送麦克风消息给其他成员
        final JSONArray team_mate = new JSONArray();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View myView = null;
        for (int i = 0; i < entities.size(); i++) {
            TeamMemberEntity teamMemberEntity = entities.get(i);
            BaseCourseGroupItem baseCourseGroupItem;
            if (teamMemberEntity.id == stuid) {
                CourseGroupMyItem courseGroupItem = new CourseGroupMyItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            } else {
                team_mate.put("" + teamMemberEntity.id);
                CourseGroupOtherItem courseGroupItem = new CourseGroupOtherItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            }
            baseCourseGroupItem.setOnVideoAudioClick(new BaseCourseGroupItem.OnVideoAudioClick() {
                @Override
                public void onVideoClick(boolean enable) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("live_id", liveId);
                        jsonObject.put("team_mate", team_mate);
                        JSONObject data = new JSONObject();
                        data.put("id", "" + stuid);
                        data.put("type", GroupGameConfig.OPERATION_VIDEO);
                        data.put("enable", enable ? 1 : 0);
                        jsonObject.put("data", data);
                        tcpMessageReg.send(TcpConstants.AUDIO_TYPE, TcpConstants.AUDIO_SEND, jsonObject.toString());
                    } catch (JSONException e) {
                        mLogtf.e("onVideoClick", e);
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }

                @Override
                public void onAudioClick(boolean enable) {
                    if (!enable) {
                        setTip("小伙伴听不到你的声音啦，\n但不影响答题哦");
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("live_id", liveId);
                        jsonObject.put("team_mate", team_mate);
                        JSONObject data = new JSONObject();
                        data.put("id", "" + stuid);
                        data.put("type", GroupGameConfig.OPERATION_AUDIO);
                        data.put("enable", enable ? 1 : 0);
                        jsonObject.put("data", data);
                        tcpMessageReg.send(TcpConstants.AUDIO_TYPE, TcpConstants.AUDIO_SEND, jsonObject.toString());
                    } catch (JSONException e) {
                        mLogtf.e("onAudioClick", e);
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
            });
            View convertView = mInflater.inflate(baseCourseGroupItem.getLayoutResId(), llCourseItemContent, false);
            baseCourseGroupItem.initViews(convertView);
            baseCourseGroupItem.updateViews(teamMemberEntity, i, teamMemberEntity);
            baseCourseGroupItem.bindListener();
            llCourseItemContent.addView(convertView);
            if (teamMemberEntity.id == stuid) {
                myView = convertView;
            }
            courseGroupItemHashMap.put(teamMemberEntity.id + "", baseCourseGroupItem);
        }
        if (entities.size() < 3) {
            for (int i = entities.size(); i < 3; i++) {
                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                teamMemberEntity.id = -1;
                CourseGroupNoItem courseGroupItem = new CourseGroupNoItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                View convertView = mInflater.inflate(courseGroupItem.getLayoutResId(), llCourseItemContent, false);
                courseGroupItem.initViews(convertView);
                courseGroupItem.updateViews(teamMemberEntity, i, teamMemberEntity);
                courseGroupItem.bindListener();
                //语音炮弹往下排，其他往上排
//                if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
//                    llCourseItemContent.addView(convertView);
//                } else {
//                    llCourseItemContent.addView(convertView, 0);
//                }
                llCourseItemContent.addView(convertView, 0);
            }
        }
        //如果小于最小值
        int screenHeight = ScreenUtils.getScreenHeight();
        int totalHeight = SizeUtils.Dp2Px(mContext, 125) * 3;
        if (screenHeight < totalHeight) {
            int small = (totalHeight - screenHeight) / 3;
            logger.d("addTeam:screenHeight=" + screenHeight + "," + totalHeight + ",small=" + small);
            for (int i = 0; i < llCourseItemContent.getChildCount(); i++) {
                View child = llCourseItemContent.getChildAt(i);
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                lp.height = screenHeight / 3;
                child.setLayoutParams(lp);
                View videoView = child.findViewById(R.id.rl_livevideo_course_item_video);
                if (videoView != null) {
                    ViewGroup.LayoutParams videolp = videoView.getLayoutParams();
                    videolp.height = videolp.height - small;
                    videoView.setLayoutParams(videolp);
                }
            }
        }
        //设置提示的位置
        final View finalMyView = myView;
        if (finalMyView != null) {
            //除了语音大炮。都把自己放右下角
            if (!LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
                llCourseItemContent.removeView(myView);
                llCourseItemContent.addView(myView);
            }
            tvMyVoiceTip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //声音小的提示
                    View myView = finalMyView;
                    RelativeLayout.LayoutParams tipLp = (RelativeLayout.LayoutParams) tvMyVoiceTip.getLayoutParams();
                    tipLp.topMargin = llCourseItemContent.getTop() + myView.getTop() + myView.getHeight() / 2 - tvMyVoiceTip.getHeight() / 2;
                    tvMyVoiceTip.setLayoutParams(tipLp);
                    tvMyVoiceTip.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
        //与课件右对齐
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) llCourseItemContent.getLayoutParams();
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        int rightMargin = videoPoint.screenWidth - videoPoint.x4;
        if (lp.rightMargin != rightMargin) {
            lp.rightMargin = rightMargin;
            llCourseItemContent.setLayoutParams(lp);
        }
    }

    private void onAnswer(JSONObject message) {
    }

    private void onCoursewareDoing(String where, final JSONObject message) {
        if (LiveQueConfig.GET_ANSWERTYPE_WHERE_MESSAGE.equals(where)) {
            boolean isTurnPage = message.optBoolean("isTurnPage");
//            if (isTurnPage) {
//                currentAnswerIndex++;
//            }
            GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
            List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
            if (currentAnswerIndex >= answerList.size() - 1) {
                logger.d("onCoursewareDoing:gameOver");
//            gameOver = true;
//            if (mIse != null) {
//                mIse.cancel();
//            }
            }
            mLogtf.d("onCoursewareDoing:isTurnPage=" + isTurnPage + ",currentAnswerIndex=" + currentAnswerIndex + ",gameOver=" + gameOver);
        } else {
            logger.e("onCoursewareDoing:where=" + where);
        }
    }

    /**
     * 语音炮弹翻页使用
     */
    private void reStartSpeechRecognize() {
        if (mIse != null) {
            mIse.cancel();
        }
        if (saveVideoFile != null) {
            groupGameUpload.uploadWonderMoment(saveVideoFile, speechContent, 0);
        }
        final String finalSpeechContent = speechContent;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //避免多次启动
                boolean equals = ("" + finalSpeechContent).equals(speechContent);
                mLogtf.d("reStartSpeechRecognize:final=" + finalSpeechContent + "," + speechContent + ",equals=" + equals);
                if (equals && !gameOver && !isDestory) {
                    startSpeechRecognize();
                }
            }
        }, 900);
    }

    private void startSpeechRecognize() {
        File dir = LiveCacheFile.geCacheFile(mContext, "groupgamemul");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        mParam = new SpeechParamEntity();
        //语音评测开始
        if (mIse == null) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.prepar();
        }
        mParam.setRecogType(SpeechConfig.SPEECH_GROUP_GAME_EVALUATOR_OFFLINE);
        mParam.setLang(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);
        speechAnswerList.clear();
        speechAnswerList.addAll(allAnswerList);
        mParam.setStrEvaluator(speechContent);
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setPcm(true);
        mParam.setLearning_stage(learningStage);
        mParam.setVad_max_sec(vad_max_sec);
        mParam.setVad_pause_sec(vad_max_sec);
        final File file = saveVideoFile;
        final String speech = speechContent;
        mIse.startRecog(mParam, new EvaluatorListenerWithPCM() {

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech()");
                Set<String> itemKeySet = courseGroupItemHashMap.keySet();
                for (String userId : itemKeySet) {
                    BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(userId);
                    baseCourseGroupItem.onBeginOfSpeech();
                }
            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    mLogtf.d("onEvaluatorSuccess(): score = " + resultEntity.getScore());
                    onRecognizeStop(false);
                    groupGameUpload.uploadWonderMoment(file, speech, 0);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    mLogtf.d("onEvaluatorError: ErrorNo = " + resultEntity.getErrorNo() + ", isOfflineFail =" + mIse.isOfflineFail());
                    onRecognizeStop(true);
                    groupGameUpload.uploadWonderMoment(file, speech, resultEntity.getErrorNo());
                    if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
                        setTip("没听清，请大声点哦");
                    }
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    if (resultEntity.getNewSenIdx() >= 0) {
                        mLogtf.d("onEvaluatoring: newSenIdx = " + resultEntity.getNewSenIdx() + ", score =" + resultEntity.getScore());
                        if (gameOver) {
                            return;
                        }
                        if (tcpMessageReg == null || interactiveTeam == null) {
                            //理论不存在
                            return;
                        }
                        if (evaluatorIng != null) {
                            evaluatorIng.onResult(resultEntity);
                        }
                    }
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
//                logger.d("onVolumeUpdate:volume = " + volume);
//                float floatVolume = (float) volume * 3 / 90;
//                groupSurfaceView.onVolumeUpdate(volume);
                BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                if (courseGroupItem != null) {
                    courseGroupItem.onVolumeUpdate(volume);
                }
            }

            @Override
            public void onRecordPCMData(short[] shorts, int readSize) {
                try {
                    byte[] dest = new byte[readSize * 2];
                    int count = readSize;
                    for (int i = 0; i < count; i++) {
                        dest[i * 2] = (byte) (shorts[i]);
                        dest[i * 2 + 1] = (byte) (shorts[i] >> 8);
                    }
                    if (mWorkerThread != null) {
                        RtcEngine rtcEngine = mWorkerThread.getRtcEngine();
                        if (rtcEngine != null) {
                            rtcEngine.pushExternalAudioFrame(dest, System.currentTimeMillis());
                            rtcEngine.adjustRecordingSignalVolume(400);
                        }
                    }
                } catch (Exception e) {
                    logger.e("onRecordPCMData", e);
                }
            }
        });
    }

    private void setTip(String text) {
        tvMyVoiceTip.setText(text);
        tvMyVoiceTip.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvMyVoiceTip.setVisibility(View.GONE);
            }
        }, 1000);
    }

    private void onRecognizeStop(boolean delay) {
        Set<String> itemKeySet = courseGroupItemHashMap.keySet();
        for (String userId : itemKeySet) {
            BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(userId);
            baseCourseGroupItem.onRecognizeStop();
        }
        if (isAttach()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!gameOver && !isDestory) {
//                        XESToastUtils.showToast(mContext, "评测完成");
                        startSpeechRecognize();
                    }
                }
            }, delay ? 1000 : 10);
        }
    }

    @Override
    public void initListener() {
        ivCourseRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCourseRefresh.setVisibility(View.GONE);
                getCourseWareTests();
            }
        });
        ivWebViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJs = false;
                wvSubjectWeb.reload();
            }
        });
//        if (com.xueersi.common.config.AppConfig.DEBUG) {
//            RelativeLayout relativeLayout = (RelativeLayout) mView;
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            android.widget.Button button = new android.widget.Button(mContext);
//            lp.rightMargin = LiveVideoPoint.getInstance().getRightMargin() - 90;
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            relativeLayout.addView(button, lp);
//            button.setText("测试");
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ArrayList<TeamMemberEntity> entities = new ArrayList<>();
//                    int[][] engGolds = new int[][]{{3, 50}, {2, 36}, {2, 36}};
//                    String[] names = {"戏2", "izzl", "蒋真"};
////                    int[][] engGolds = new int[][]{{2, 2}, {2, 4}};
//                    if (interactiveTeam != null) {
//                        entities.addAll(interactiveTeam.getEntities());
//                        for (int i = 0; i < entities.size(); i++) {
//                            TeamMemberEntity teamMemberEntity = entities.get(i);
//                            int[] engGold = engGolds[i];
//                            teamMemberEntity.name = names[i];
//                            teamMemberEntity.gold = engGold[0];
//                            teamMemberEntity.energy = engGold[1];
//                        }
//                    }
//                    String[] heads = {"https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=107d95c34134970a537e187df4a3baad/a8014c086e061d95b2d56ad47bf40ad163d9ca4f.jpg",
//                            "https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/crop%3D24%2C0%2C851%2C562%3Bc0%3Dbaike92%2C5%2C5%2C92%2C30/sign=031be2a37f3e6709aa4f1fbf06f5ab11/fd039245d688d43f23098f767d1ed21b0ff43b95.jpg"};
//                    int count = 3;
//                    int i = entities.size();
//                    while (entities.size() < count) {
////                        TeamMemberEntity myTeamMemberEntity = entities.get(0);
////                        myTeamMemberEntity.gold = 11;
////                        myTeamMemberEntity.energy = 12;
//                        int[] engGold = engGolds[i];
//                        TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
//                        teamMemberEntity.id = i;
//                        teamMemberEntity.name = names[i];
//                        teamMemberEntity.headurl = heads[i % heads.length];
//                        teamMemberEntity.gold = engGold[0];
//                        teamMemberEntity.energy = engGold[1];
//                        entities.add(teamMemberEntity);
//                        i++;
//                    }
//                    GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
//                    ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
//                    groupGameMVPMultPager.setOnPagerClose(new OnPagerClose() {
//                        @Override
//                        public void onClose(LiveBasePager basePager) {
//                            ((ViewGroup) mView).removeView(basePager.getRootView());
//                        }
//                    });
//                }
//            });
//            button.setOnClickListener(new View.OnClickListener() {
//                java.util.Random random = new java.util.Random();
//
//                @Override
//                public void onClick(View view) {
//                    JSONObject jsonData = new JSONObject();
//                    try {
//                        jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
//                        jsonData.put("score", 82);
//                        jsonData.put("studentNum", random.nextInt(3));
//                        jsonData.put("turnToPageNum", -1);
//                        wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            button.setOnClickListener(new View.OnClickListener() {
//                java.util.Random random = new java.util.Random();
//
//                @Override
//                public void onClick(View view) {
//                    try {
//                        JSONObject resultData = new JSONObject();
//                        resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
//                        resultData.put("pageNum", random.nextInt(3));
//                        resultData.put("currentRight", random.nextInt(10));
//                        resultData.put("isSingle", false);
//                        sendToCourseware(wvSubjectWeb, resultData, "*");
//                    } catch (Exception e) {
//                        mLogtf.e("coursewareOnloading", e);
//                        CrashReport.postCatchedException(new LiveException(TAG, e));
//                    }
//                }
//            });
//        }
    }

    /**
     * 实现 h5课件基础接口
     */
    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void onBack() {

    }

    @Override
    public void destroy() {
        onDestroy();
    }

    @Override
    public void submitData() {
        isForce = true;
        submit();
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        preLoad.onProgressChanged(view, newProgress);
    }

    /**
     * 课件加载
     */
    interface PreLoad {
        /**
         * 课件开始加载
         */
        void onStart();

        /**
         * 课件加载中进度
         */
        void onProgressChanged(WebView view, int newProgress);

        /**
         * 课件结束加载
         */
        void onStop();
    }


    /**
     * 初中课件加载
     */
    private class MiddleSchool implements PreLoad {
        private ImageView ivLoading;
        private ProgressBar pgCourseProg;
        private TextView tvDataLoadingTip;

        @Override
        public void onStart() {
            ivLoading = mView.findViewById(R.id.iv_data_loading_show);
            pgCourseProg = mView.findViewById(R.id.pg_livevideo_new_course_prog);
            tvDataLoadingTip = mView.findViewById(R.id.tv_data_loading_tip);
            logger.d("MiddleSchool:onStart");
            try {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.animlst_app_loading);
                ivLoading.setBackground(drawable);
                ((AnimationDrawable) drawable).start();
            } catch (Exception e) {
                if (mLogtf != null) {
                    mLogtf.e("onStart", e);
                }
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            logger.d("MiddleSchool:onProgressChanged:newProgress" + newProgress);
            pgCourseProg.setProgress(newProgress);
            tvDataLoadingTip.setText("加载中 " + newProgress + "%");
        }

        @Override
        public void onStop() {
            logger.d("MiddleSchool:onStart:ivLoading=null?" + (ivLoading == null));
            rlSubjectLoading.setVisibility(View.GONE);
            if (ivLoading != null) {
                try {
                    Drawable drawable = ivLoading.getBackground();
                    if (drawable instanceof AnimationDrawable) {
                        ((AnimationDrawable) drawable).stop();
                    }
                } catch (Exception e) {
                    if (mLogtf != null) {
                        mLogtf.e("onStop", e);
                    }
                }
            }
        }
    }

    public void setGroupGameTestInfosEntity(GroupGameTestInfosEntity mGroupGameTestInfosEntity) {
        this.mGroupGameTestInfosEntity = mGroupGameTestInfosEntity;
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        getCourseWareTests();
    }

    private void getCourseWareTests() {
        preLoad.onStart();
        AbstractBusinessDataCallBack callBack = new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                if (mGroupGameTestInfosEntity.isAnswered()) {
                    XESToastUtils.showToast(mContext, "你已作答过此题");
                    onClose.onH5ResultClose(GroupGameMultNativePager.this, detailInfo);
                    return;
                }
                tests = mGroupGameTestInfosEntity.getTestInfoList();
                if (tests.isEmpty()) {
                    XESToastUtils.showToast(mContext, "互动题为空");
                    return;
                }
                testEntity = tests.get(0);
//                if (AppConfig.DEBUG) {
//                    test.setPreviewPath("file:///android_asset/group_game_cleanup/index.html");
//                }
                allAnswerList.addAll(testEntity.getAnswerList());
                long nowTime = mGroupGameTestInfosEntity.getTimeStamp();
                int nowPlayTime = (int) (nowTime - mGroupGameTestInfosEntity.getOperateTimeStamp());
                if (nowPlayTime < 0) {
                    nowPlayTime = 0;
                }
                // 剩余时间
                long lastTime = testEntity.getTotalTime() - nowPlayTime;
//                if (com.xueersi.common.config.AppConfig.DEBUG) {
//                    nowPlayTime = 0;
//                    lastTime = 10000;
//                }
                //小于0直接结束
                mLogtf.d("getCourseWareTests:total=" + testEntity.getTotalTime() + ",nowPlayTime=" + nowPlayTime);
                if (lastTime < 0) {
                    isForce = true;
                    showResult = false;
                    testEntity.setTotalTime(0);
                    allAnswerList.clear();
                } else {
                    testEntity.setTotalTime(testEntity.getTotalTime() - nowPlayTime);
                    if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
                        // 游戏时间
                        long playTime = 0;
                        // 当前页数
                        int startPager = 0;
                        for (int i = 0; i < allAnswerList.size(); i++) {
                            GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = allAnswerList.get(i);
                            PagerShowTime pagerShowTime = new PagerShowTime();
                            voicePagerShowTimeHashMap.put(answersEntity.getId(), pagerShowTime);
                        }
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity currAnswersEntity = null;
                        for (int i = 0; i < allAnswerList.size(); i++) {
                            GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = allAnswerList.get(i);
                            playTime += answersEntity.getSingleTime() + 1;
                            mLogtf.d("getCourseWareTests:i=" + i + ",singleTime=" + answersEntity.getSingleTime() + ",playTime=" + playTime);
                            if (nowPlayTime < playTime) {
                                currAnswersEntity = answersEntity;
                                startPager = i;
                                break;
                            }
                        }
                        if (currAnswersEntity != null) {
                            int more = (int) (currAnswersEntity.getSingleTime() + 1 - (playTime - nowPlayTime));
                            mLogtf.d("getCourseWareTests:id=" + currAnswersEntity.getId() + ",more=" + more);
                            currAnswersEntity.setSingleTime(currAnswersEntity.getSingleTime() - more);
                        }
                        //语音炮弹当前页数
                        if (startPager != 0) {
                            for (int i = 0; i < startPager; i++) {
                                allAnswerList.remove(0);
                            }
                        }
                        mLogtf.d("getCourseWareTests:playTime=" + playTime + ",startPager=" + startPager + ",all=" + allAnswerList.size());
                        currentAnswerIndex = startPager;
                    } else if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                        PagerShowTime pagerShowTime = new PagerShowTime();
                        cleanUpPagerShowTimeHashMap.put(0, pagerShowTime);
                    }
                }
                createSpeechContent("getCourseWareTests", lastTime < 0);
                wvSubjectWeb.loadUrl(testEntity.getPreviewPath());
                int type = newCourseCache.loadCourseWareUrl(testEntity.getPreviewPath());
                if (type != 0) {
                    ispreload = type == 1;
                } else {
                    ispreload = true;
                }
                connectTcp();
            }

            private void connectTcp() {
                if (tcpMessageReg != null) {
                    tcpMessageReg.registTcpMessageAction(tcpMessageAction);
                    if (teamVideoAudioMessage == null) {
                        teamVideoAudioMessage = new TeamVideoAudioMessage();
                        tcpMessageReg.registTcpMessageAction(teamVideoAudioMessage);
                    }
                    tcpMessageReg.onConnect(new TcpMessageReg.OnTcpConnect() {
                        @Override
                        public void onTcpConnect() {
                            sendTest("onTcpConnect");
                        }
                    });
                }
            }

            private void sendTest(String method) {
                logger.d("sendTest:method=" + method);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("live_id", liveId);
                    jsonObject.put("class_id", liveGetInfo.getStudentLiveInfo().getClassId());
                    jsonObject.put("iid", interactiveTeam.getInteractive_team_id());
                    jsonObject.put("test_id", detailInfo.id);
                    PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
                    jsonObject.put("pid", teamEntity.getPkTeamId());
                    tcpMessageReg.send(TcpConstants.GROUP_GAME_TYPE, TcpConstants.GROUP_GAME_SEND, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                mLogtf.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg + ",请刷新");
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
                logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
//                preLoad.onStop();
            }
        };
        mLogtf.d("getCourseWareTests:mGroupGameTestInfosEntity=null?" + (mGroupGameTestInfosEntity == null));
        if (mGroupGameTestInfosEntity == null) {
            englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, callBack);
        } else {
            callBack.onDataSucess(mGroupGameTestInfosEntity);
        }
    }

    boolean submit = false;
    /**
     * 是否显示结果页，时间结束不显示
     */
    private boolean showResult = true;

    /**
     * 提交
     */
    private void submit() {
        if (submit) {
            return;
        }
        submit = true;
        int voiceTime = 0;
        int starNum = 0;
        int energy = 0;
        int gold = 0;
        //视频开启时长，单位：毫秒
        int videoLengthTime = 0;
        //麦克开启时长，单位：毫秒
        int micLengthTime = 0;
        //收他人视频开启时长，单位：毫秒
        int acceptVideoLengthTime = 0;
        //接收他人麦克开启时长，单位：毫秒
        int acceptMicLengthTime = 0;
        PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
        float averageScore = 0;
        int sum = 0;
        int gameGroupId = interactiveTeam.getInteractive_team_id();
        int tryTimes = 0;
        try {
            int size = scoreHashmap.size();
            if (size != 0) {
                Set<Integer> keySet = scoreHashmap.keySet();
                for (Integer key : keySet) {
                    List<SpeechResult> speechResults = scoreHashmap.get(key);
                    for (int i = 0; i < speechResults.size(); i++) {
                        tryTimes++;
                        SpeechResult speechResult = speechResults.get(i);
                        voiceTime += speechResult.speechDuration * 1000;
                        sum += speechResult.score;
                    }
                }
                if (tryTimes != 0) {
                    averageScore = sum / tryTimes;
                }
            }
        } catch (Exception e) {
            mLogtf.e("submit", e);
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
        List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
        JSONObject answerData = new JSONObject();
        if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
            //遍历作答正确，取最大的金币为3
            Set<String> canKeySet = cleanUpEntities.keySet();
            CleanUpEntity maxCleanUpEntity = null;
            int maxRight = 0;
            for (String userId : canKeySet) {
                CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + userId);
                int rightNum = cleanUpEntity.rightAnswerList.size();
                if (rightNum > 0) {
                    cleanUpEntity.teamMemberEntity.gold = 2;
                } else {
                    cleanUpEntity.teamMemberEntity.gold = 0;
                }
                if (rightNum > maxRight) {
                    maxRight = rightNum;
                    maxCleanUpEntity = cleanUpEntity;
                }
            }
            if (maxCleanUpEntity != null) {
                logger.d("submit:userId=" + maxCleanUpEntity.teamMemberEntity.id + ",rightNum=" + maxCleanUpEntity.rightAnswerList.size());
                maxCleanUpEntity.teamMemberEntity.gold = 3;
            }
            try {
                answerData.put("tryTimes", tryTimes);
                JSONArray userAnswer = new JSONArray();
                CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + stuid);
                if (cleanUpEntity != null && !tests.isEmpty()) {
                    answerData.put("rightNum", cleanUpEntity.rightAnswerList.size());
                    HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ScoreEnergy> wordScore = cleanUpEntity.wordScore;
                    for (int ansIndex = 0; ansIndex < answerList.size(); ansIndex++) {
                        JSONObject jsonObject = new JSONObject();
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(ansIndex);
                        jsonObject.put("text", answer.getText());
                        ScoreEnergy scoreEnergy = wordScore.get(answer);
                        if (scoreEnergy != null) {
                            energy += scoreEnergy.energy;
                            jsonObject.put("isRight", 1);
                        } else {
                            jsonObject.put("isRight", 0);
                        }
                        jsonObject.put("voiceTime", 0);
                        jsonObject.put("scores", getScores(answer.getId()));
                        userAnswer.put(jsonObject);
                    }
                } else {
                    answerData.put("rightNum", 0);
                }
                answerData.put("averageScore", averageScore);
                answerData.put("userAnswer", userAnswer);
            } catch (Exception e) {
                mLogtf.e("submit2", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
            energy += 5;
            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                TeamMemberEntity teamMemberEntity = entities.get(i);
                //有用户能量就加5
                teamMemberEntity.energy += 5;
            }
        } else {
            //遍历作答正确，取最大的金币为3
            Set<String> canKeySet = vidooCannonEntities.keySet();
            VidooCannonEntity maxVidooCannonEntity = null;
            int maxRight = 0;
            for (String userId : canKeySet) {
                VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + userId);
                int rightNum = vidooCannonEntity.rightNum;
                if (rightNum > 0) {
                    vidooCannonEntity.teamMemberEntity.gold = 2;
                    if (rightNum > GroupGameConfig.CANNON_MAX_ENERGY) {
                        vidooCannonEntity.teamMemberEntity.setEnergy(GroupGameConfig.CANNON_MAX_ENERGY);
                    } else {
                        vidooCannonEntity.teamMemberEntity.setEnergy(rightNum);
                    }
                } else {
                    int oldGold = vidooCannonEntity.teamMemberEntity.gold;
                    if (oldGold != 0) {
                        mLogtf.d("submit:oldGold=" + oldGold);
                    }
                    vidooCannonEntity.teamMemberEntity.gold = 0;
                }
                if (rightNum > maxRight) {
                    maxRight = rightNum;
                    maxVidooCannonEntity = vidooCannonEntity;
                }
            }
            if (maxVidooCannonEntity != null) {
                mLogtf.d("submit:userId=" + maxVidooCannonEntity.teamMemberEntity.id + ",rightNum=" + maxVidooCannonEntity.rightNum);
                maxVidooCannonEntity.teamMemberEntity.gold = 3;
            }
            try {
                answerData.put("tryTimes", tryTimes);
                JSONArray userAnswer = new JSONArray();
                VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + stuid);
                if (vidooCannonEntity != null && !tests.isEmpty()) {
                    energy = vidooCannonEntity.rightNum;
                    int rightNum = 0;
                    for (int ansIndex = 0; ansIndex < answerList.size(); ansIndex++) {
                        JSONObject jsonObject = new JSONObject();
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(ansIndex);
                        jsonObject.put("text", answer.getText());
                        ArrayList<Integer> arrayList = getAllWord(answer);
                        mLogtf.d("submit:arrayList=" + arrayList.size() + ",singleCount=" + testInfoEntity.getSingleCount());
                        int isRight = arrayList.size() >= testInfoEntity.getSingleCount() ? 1 : 0;
                        if (isRight == 1) {
                            rightNum++;
                        }
                        jsonObject.put("isRight", isRight);
                        PagerShowTime pagerShowTime = voicePagerShowTimeHashMap.get(answer.getId());
                        if (pagerShowTime != null) {
                            if (pagerShowTime.start == 0) {
                                pagerShowTime.start = System.currentTimeMillis();
                            }
                            if (pagerShowTime.end == 0) {
                                pagerShowTime.end = System.currentTimeMillis();
                            }
                            long pageTime = pagerShowTime.end - pagerShowTime.start;
                            if (pageTime < 0) {
                                pageTime = 0;
                            }
                            logger.d("submit:pageTime=" + pageTime);
                            jsonObject.put("voiceTime", pageTime);
                        } else {
                            jsonObject.put("voiceTime", 0);
                        }
                        jsonObject.put("scores", getScores(answer.getId()));
                        jsonObject.put("myword", getMyWord(answer));
                        userAnswer.put(jsonObject);
                    }
                    answerData.put("rightNum", rightNum);
                } else {
                    answerData.put("rightNum", 0);
                }
                answerData.put("averageScore", averageScore);
                answerData.put("userAnswer", userAnswer);
            } catch (JSONException e) {
                mLogtf.e("submit3", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        {
            //遍历用户item。计算时间
            Set<String> itemKeySet = courseGroupItemHashMap.keySet();
            for (String userId : itemKeySet) {
                BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(userId);
                if (userId.equals("" + stuid)) {
                    videoLengthTime = (int) baseCourseGroupItem.getVideoTime();
                    micLengthTime = (int) baseCourseGroupItem.getAudioTime();
                    gold = baseCourseGroupItem.getEntity().gold;
                } else {
                    long videoTime = baseCourseGroupItem.getVideoTime();
                    long audioTime = baseCourseGroupItem.getAudioTime();
                    acceptVideoLengthTime += videoTime;
                    acceptMicLengthTime += audioTime;
                    logger.d("submit:userId=" + userId + ",videoTime=" + videoTime + ",audioTime=" + audioTime);
                }
            }
        }
        if (averageScore < 40) {
            starNum = 1;
        } else if (averageScore < 60) {
            starNum = 2;
        } else if (averageScore < 75) {
            starNum = 3;
        } else if (averageScore < 90) {
            starNum = 4;
        } else if (averageScore < 101) {
            starNum = 5;
        }
        mLogtf.d("submit:averageScore=" + averageScore + ",starNum=" + starNum + ",energy=" + energy);
        if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
            GroupGameLog.sno5(liveAndBackDebug, detailInfo.id, isForce ? "endPublish" : "autoSubmit", voiceTime == 0 ?
                    "0" : "1", 1);
        }
        englishH5CoursewareSecHttp.submitGroupGame(detailInfo, 1, voiceTime, teamEntity.getPkTeamId(), gameGroupId, starNum, energy, gold,
                videoLengthTime, micLengthTime
                , acceptVideoLengthTime, acceptMicLengthTime, answerData.toString(), new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        logger.d("submitGroupGame->onDataSucess:objData=" + objData);
                        if (showResult) {
                            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                            GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
                            ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
                            groupGameMVPMultPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                                @Override
                                public void onClose(LiveBasePager basePager) {
                                    ((ViewGroup) mView).removeView(basePager.getRootView());
                                    onClose.onH5ResultClose(GroupGameMultNativePager.this, detailInfo);
                                }
                            });
                            if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
                                GroupGameLog.sno6(liveAndBackDebug, detailInfo.id, "" + entities.size(), 1);
                            }
                        } else {
                            onClose.onH5ResultClose(GroupGameMultNativePager.this, detailInfo);
                        }
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.d("submitGroupGame->onDataFail:" + failMsg);
                        XESToastUtils.showToast(mContext, failMsg);
//                        if (AppConfig.DEBUG) {
//                            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
//                            GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
//                            ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
//                            groupGameMVPMultPager.setOnPagerClose(new OnPagerClose() {
//                                @Override
//                                public void onClose(LiveBasePager basePager) {
//                                    ((ViewGroup) mView).removeView(basePager.getRootView());
//                                    onClose.onH5ResultClose(GroupGameMultNativePager.this, detailInfo);
//                                }
//                            });
//                        }
                    }
                });
    }

    private ArrayList<Integer> getAllWord(GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer) {
        ArrayList<Integer> allList = new ArrayList<>();
        Set<String> keys = vidooCannonEntities.keySet();
        for (String key : keys) {
            VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get(key);
            HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = vidooCannonEntity.wordScore;
            ArrayList<Integer> arrayList = wordScore.get(answer);
            if (arrayList != null) {
                allList.addAll(arrayList);
            }
        }
        return allList;
    }

    private String getMyWord(GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer) {
        String rightWord = "";
        try {
            VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + stuid);
            HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = vidooCannonEntity.wordScore;
            ArrayList<Integer> arrayList = wordScore.get(answer);
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Integer integer = arrayList.get(i);
                    rightWord += integer;
                    if (i < arrayList.size() - 1) {
                        rightWord += ",";
                    }
                }
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return rightWord;
    }

    private String getScores(Integer key) {
        String scores = "";
        List<SpeechResult> speechResults = scoreHashmap.get(key);
        if (speechResults != null) {
            for (int sindex = 0; sindex < speechResults.size(); sindex++) {
                SpeechResult speechResult = speechResults.get(sindex);
                scores += "" + speechResult.score;
                if (sindex < speechResults.size() - 1) {
                    scores += ",";
                }
            }
        }
        mLogtf.d("getScores:key=" + key + ",scores=" + scores);
        return scores;
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return null;
    }

    @Override
    public boolean isResultRecived() {
        return false;
    }

    public void onResume() {
        Set<String> itemKeySet = courseGroupItemHashMap.keySet();
        for (String userId : itemKeySet) {
            BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(userId);
            baseCourseGroupItem.onResume();
        }
    }

    public void onPause() {
        Set<String> itemKeySet = courseGroupItemHashMap.keySet();
        for (String userId : itemKeySet) {
            BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(userId);
            baseCourseGroupItem.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wvSubjectWeb.destroy();
        multModeAction.onDestory();
        if (mIse != null) {
            mIse.cancel();
        }
        isDestory = true;
        leaveChannel();
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }
        if (tcpMessageReg != null) {
            if (tcpMessageAction != null) {
                tcpMessageReg.unregistTcpMessageAction(tcpMessageAction);
            }
            if (teamVideoAudioMessage != null) {
                tcpMessageReg.unregistTcpMessageAction(teamVideoAudioMessage);
            }
        }
        Set<String> keySet = courseGroupItemHashMap.keySet();
        for (String key : keySet) {
            BaseCourseGroupItem baseCourseGroupItem = courseGroupItemHashMap.get(key);
            baseCourseGroupItem.onDestory();
            long videoTime = baseCourseGroupItem.getVideoTime();
            long audioTime = baseCourseGroupItem.getAudioTime();
            logger.d("onDestroy:key=" + key + ",videoTime=" + videoTime + ",audioTime=" + audioTime);
        }
        courseGroupItemHashMap.clear();
        if (liveAudioManager != null) {
            liveAudioManager.setVolume(mVolume);
        }
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
            logger.d("onDestroy:setVolume:1");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("start");
            stableLogHashMap.put("tag", TAG);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d("onDestroy:setVolume:null");
        }
    }

    class CourseWebViewClient extends MyWebViewClient implements OnHttpCode {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl() + "";
            if (url.contains(".html")) {
                if (!addJs) {
                    addJs = true;
                    WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(view, url);
                    logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse == null));
                    if (webResourceResponse != null) {
                        return webResourceResponse;
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                wvSubjectWeb.stopLoading();
                            }
                        });
                        XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                    }
                }
            } else if (WebInstertJs.indexStr().equals(url)) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            wvSubjectWeb.stopLoading();
                        }
                    });
                    XESToastUtils.showToast(mContext, "通信文件加载失败，请刷新");
                }
            }
            WebResourceResponse webResourceResponse = newCourseCache.shouldInterceptRequest(view, url);
            if (webResourceResponse != null) {
                logger.d("shouldInterceptRequest:url=" + url);
                return webResourceResponse;
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        protected void otherMsg(StableLogHashMap logHashMap, String loadUrl) {
            logHashMap.put("testid", NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts));
            logHashMap.put("ispreload", "" + ispreload);
            logHashMap.put("testsource", "" + ispreload);
            logHashMap.put("errtype", "webView");
            logHashMap.put("subtestid", getSubtestid());
            if (XESCODE.ARTS_SEND_QUESTION == detailInfo.noticeType) {
                logHashMap.put("testsource", "PlatformTest");
            } else if (XESCODE.ARTS_H5_COURSEWARE == detailInfo.noticeType) {
                logHashMap.put("testsource", "PlatformCourseware");
            }
            logHashMap.put("eventid", "" + LogConfig.LIVE_H5PLAT);
        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
        }
    }

    /** 课件日志 */
    private String getSubtestid() {
//        if (tests.size() == 1) {
//            return "0";
//        }
//        return "" + (currentIndex + 1);
        return "0";
    }

    /**
     * 创建评测文本，
     *
     * @param method
     * @param delay  等待tcp连接
     */
    private void createSpeechContent(String method, boolean delay) {
        speechContent = "";
        if (gameOver) {
            return;
        }
        if (allAnswerList.isEmpty()) {
            gameOver = true;
            if (mIse != null) {
                mIse.cancel();
            }
            XESToastUtils.showToast(mContext, "游戏结束");
            mLogtf.d("createSpeechContent:method=" + method + ",delay=" + delay);
            if (delay) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submit();
                    }
                }, 1200);
            } else {
                submit();
            }
            return;
        }
        if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(gameType)) {
            GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = allAnswerList.get(0);
            speechContent = answersEntity.getText();
            if (allAnswerList.size() > 1) {
                answersEntity = allAnswerList.get(1);
                speechContent += "|" + answersEntity.getText();
            }
        } else {
            for (int j = 0; j < allAnswerList.size(); j++) {
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = allAnswerList.get(j);
                speechContent += answersEntity.getText() + "|";
            }
            if (speechContent.endsWith("|")) {
                speechContent = speechContent.substring(0, speechContent.length() - 1);
            }
        }
        mLogtf.d("createSpeechContent:method=" + method + ",speechContent=" + speechContent);
    }

    /**
     * 语音评测 - 命中句子
     */
    interface EvaluatorIng {
        void onResult(ResultEntity resultEntity);
    }

    private void addScore(ResultEntity resultEntity) {
        int newSenIndex = resultEntity.getNewSenIdx();
        int score = resultEntity.getScore();
        try {
            if (newSenIndex >= 0 && newSenIndex < speechAnswerList.size()) {
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = speechAnswerList.get(newSenIndex);
                int key = removeAnswersEntity.getId();
                List<SpeechResult> speechResults = scoreHashmap.get(key);
                if (speechResults == null) {
                    speechResults = new ArrayList<>();
                    scoreHashmap.put(key, speechResults);
                }
                SpeechResult speechResult = new SpeechResult();
                speechResult.score = score;
                speechResult.speechDuration = resultEntity.getSpeechDuration();
                speechResults.add(speechResult);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    private void sendToCourseware(final WebView wvSubjectWeb, final JSONObject type, String data) {
        try {
            JSONObject liveinfo = new JSONObject();
            liveinfo.put("liveid", liveId);
            liveinfo.put("userid", stuid);
            liveinfo.put("testid", "" + detailInfo.id);
            liveinfo.put("creattime", "" + creattime);
            liveinfo.put("time", "" + System.currentTimeMillis());
            type.put("liveinfo", liveinfo);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
        StaticWeb.sendToCourseware(wvSubjectWeb, type, data);
    }

    private class VoiceCannnon implements EvaluatorIng {

        @Override
        public void onResult(ResultEntity resultEntity) {
            addScore(resultEntity);
            int newSenIndex = resultEntity.getNewSenIdx();
            int score = resultEntity.getScore();
            if (score < minscore) {
                BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                if (courseGroupItem != null) {
                    courseGroupItem.onOpps();
                    mLogtf.d("onResult:onOpps:score=" + score);
                }
                return;
            }
            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
            try {
                {
                    //调网页
//                    JSONObject jsonData = new JSONObject();
//                    jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
//                    jsonData.put("score", score);
//                    jsonData.put("studentNum", 3);
//                    jsonData.put("isTurnPage", false);
//                    wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                }
                int newSenIdx = resultEntity.getNewSenIdx();
                if (newSenIdx < 0 || newSenIdx >= speechAnswerList.size()) {
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = speechAnswerList.get(newSenIdx);
                GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = testInfoEntity.getAnswerList().get(currentAnswerIndex);
                mLogtf.d("onResult:answersEntity=" + answersEntity.getText() + "," + removeAnswersEntity.getText());
                if (!TextUtils.equals(answersEntity.getText(), removeAnswersEntity.getText())) {
                    BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                    if (courseGroupItem != null) {
                        courseGroupItem.onOpps();
                        mLogtf.d("onResult:onOpps2:score=" + score);
                    }
                    return;
                }
                PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
                if (teamEntity != null) {
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put("type", LiveQueConfig.EN_COURSE_GAME_TYPE_1);
                    bodyJson.put("live_id", liveGetInfo.getId());
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
                    bodyJson.put("class_id", studentLiveInfo.getClassId());
                    bodyJson.put("test_id", detailInfo.id);
//                    if (currentAnswerIndex > answerList.size()) {
//                        return;
//                    }
                    bodyJson.put("uid", "" + stuid);
                    final String word_id = "" + answersEntity.getId();
                    bodyJson.put("word_id", word_id);
                    bodyJson.put("pk_team_id", teamEntity.getPkTeamId());
                    bodyJson.put("team_type", interactiveTeam.getTeam_type());
                    bodyJson.put("interactive_team_id", interactiveTeam.getInteractive_team_id());
                    JSONArray team_mate = new JSONArray();
                    for (int i = 0; i < entities.size(); i++) {
                        TeamMemberEntity teamMemberEntity = entities.get(i);
                        team_mate.put("" + teamMemberEntity.id);
                    }
                    bodyJson.put("team_mate", team_mate);
                    JSONObject userData = new JSONObject();
                    userData.put("word_id", "" + answersEntity.getId());
                    userData.put("score", "" + score);
                    userData.put("incry_energy", 1);
                    bodyJson.put("userData", userData);
                    tcpMessageReg.send(TcpConstants.VOICE_CANNO_TYPE, TcpConstants.VOICE_CANNO_SEND, bodyJson.toString(), new SendCallBack() {
                        String TAG = "SendCallBack:";
                        int seq;

                        @Override
                        public void onNoOpen() {
                            mLogtf.d(TAG + "onNoOpen");
                        }

                        @Override
                        public void onStart(int seq) {
                            this.seq = seq;
                            mLogtf.d(TAG + "onStart:word_id=" + word_id + ",seq=" + seq);
                        }

                        @Override
                        public void onReceiveMeg(short type, int operation, int seq, String msg) {
                            mLogtf.d(TAG + "onReceiveMeg:word_id=" + word_id + ",seq=" + seq);
                        }

                        @Override
                        public void onTimeOut() {
                            mLogtf.d(TAG + "onTimeOut:word_id=" + word_id + ",seq=" + seq);
                        }
                    });
                }
            } catch (Exception e) {
                mLogtf.e("onResult", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    /**
     *
     */
    private class CleanEvaluatorIng implements EvaluatorIng {

        @Override
        public void onResult(ResultEntity resultEntity) {
            try {
                addScore(resultEntity);
                int score = resultEntity.getScore();
                if (score < minscore) {
                    BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                    if (courseGroupItem != null) {
                        courseGroupItem.onOpps();
                        mLogtf.d("onResult:onOpps:score=" + score);
                    }
                    return;
                }
                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                int newSenIdx = resultEntity.getNewSenIdx();
                mLogtf.d("CleanEvaluatorIng:newSenIdx=" + newSenIdx + ",size" + allAnswerList.size() + ",speechContent=" + speechContent);
                if (newSenIdx < 0 || newSenIdx >= speechAnswerList.size()) {
                    BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                    if (courseGroupItem != null) {
                        courseGroupItem.onOpps();
                        mLogtf.d("onResult:onOpps2:score=" + score);
                    }
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = speechAnswerList.get(newSenIdx);
                PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
                if (teamEntity != null) {
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put("type", LiveQueConfig.EN_COURSE_GAME_TYPE_2);
                    bodyJson.put("live_id", liveGetInfo.getId());
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
                    bodyJson.put("class_id", studentLiveInfo.getClassId());
                    bodyJson.put("test_id", detailInfo.id);
//                    if (currentAnswerIndex > answerList.size()) {
//                        return;
//                    }
                    bodyJson.put("uid", "" + stuid);
                    final String word = "" + removeAnswersEntity.getText();
                    bodyJson.put("word", word);
                    bodyJson.put("pk_team_id", teamEntity.getPkTeamId());
                    bodyJson.put("team_type", interactiveTeam.getTeam_type());
                    bodyJson.put("interactive_team_id", interactiveTeam.getInteractive_team_id());
                    bodyJson.put("score", "" + score);
                    bodyJson.put("voiceTime", resultEntity.getSpeechDuration());
                    JSONArray team_mate = new JSONArray();
                    for (int i = 0; i < entities.size(); i++) {
                        TeamMemberEntity teamMemberEntity = entities.get(i);
                        team_mate.put("" + teamMemberEntity.id);
                    }
                    bodyJson.put("team_mate", team_mate);
                    tcpMessageReg.send(TcpConstants.CLEAN_UP_TYPE, TcpConstants.CLEAN_UP_SEND, bodyJson.toString(), new SendCallBack() {
                        String TAG = "SendCallBack:";
                        int seq;

                        @Override
                        public void onNoOpen() {
                            mLogtf.d(TAG + "onNoOpen");
                        }

                        @Override
                        public void onStart(int seq) {
                            this.seq = seq;
                            mLogtf.d(TAG + "onStart:word=" + word + ",seq=" + seq);
                        }

                        @Override
                        public void onReceiveMeg(short type, int operation, int seq, String msg) {
                            mLogtf.d(TAG + "onReceiveMeg:word=" + word + ",seq=" + seq);
                        }

                        @Override
                        public void onTimeOut() {
                            mLogtf.d(TAG + "onTimeOut:word=" + word + ",seq=" + seq);
                        }
                    });
                }
            } catch (Exception e) {
                mLogtf.e("CleanEvaluatorIng", e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    /** 客户端发语音炮弹数据,服务端返回 */
    class VoiceProjectile implements TcpMessageAction {
        VoiceCannonOnMessage voiceCannonOnMessage;

        @Override
        public void onMessage(short type, int operation, String msg) {
            mLogtf.d("onMessage:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.VOICE_CANNO_TYPE) {
                switch (operation) {
                    case TcpConstants.VOICE_CANNO_STATIS: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            String test_id = jsonObject.optString("test_id");
                            if (!detailInfo.id.equals(test_id)) {
                                return;
                            }
                            getCurrent("STATISstart");
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            int word_id = dataObj.getInt("word_id");
                            final int who_id = dataObj.getInt("who_id");
                            final int score = dataObj.getInt("score");
                            int current_word = jsonObject.optInt("current_word", currentAnswerIndex);
                            Integer integer = wordCount.get("" + word_id);
                            if (integer == null) {
                                integer = 1;
                            } else {
                                integer++;
                            }
                            GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                            VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + who_id);
                            if (vidooCannonEntity != null) {
                                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
                                for (int allAns = 0; allAns < answerList.size(); allAns++) {
                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(allAns);
                                    if (answer.getId() == word_id) {
                                        vidooCannonEntity.rightNum++;
                                        //一个单词一个能量
                                        if (vidooCannonEntity.teamMemberEntity.getEnergy() < GroupGameConfig.CANNON_MAX_ENERGY) {
                                            vidooCannonEntity.teamMemberEntity.setEnergy(vidooCannonEntity.teamMemberEntity.getEnergy() + 1);
                                        }
                                        mLogtf.d("VOICE_CANNO_STATIS:word_id=" + word_id + ",who_id=" + who_id + ",energy=" + vidooCannonEntity.teamMemberEntity.getEnergy() + ",contains=" + allAnswerList.contains(answer));
                                        break;
                                    }
                                }
                                for (int ansIndex = 0; ansIndex < answerList.size(); ansIndex++) {
                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(ansIndex);
                                    if (answer.getId() == word_id) {
                                        HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = vidooCannonEntity.wordScore;
                                        ArrayList<Integer> allScore = new ArrayList<>();
                                        if (wordScore.containsKey(answer)) {
                                            allScore = wordScore.get(answer);
                                        } else {
                                            wordScore.put(answer, allScore);
                                        }
                                        allScore.add(score);
                                        break;
                                    }
                                }
                            }
                            wordCount.put("" + word_id, integer);
                            {
                                int maxSingCount = testInfoEntity.getSingleCount();
                                final boolean isTurnPage;
                                mLogtf.d("VOICE_CANNO_STATIS:current_word=" + current_word + "," + currentAnswerIndex);
                                int oldIndex = currentAnswerIndex;
                                //本地比之前的试题小，删除到当前页数-1
                                final AtomicBoolean lessOther = new AtomicBoolean(false);
                                if (currentAnswerIndex < current_word) {
                                    lessOther.set(true);
                                    currentAnswerIndex = current_word;
                                    int oldSize = allAnswerList.size();
                                    if (!allAnswerList.isEmpty()) {
                                        //删除之前的试题
                                        while (oldIndex <= current_word - 1 && !allAnswerList.isEmpty()) {
                                            oldIndex++;
                                            allAnswerList.remove(0);
                                        }
                                        isTurnPage = true;
                                    } else {
                                        isTurnPage = false;
                                    }
                                    mLogtf.d("VOICE_CANNO_STATIS:oldSize=" + oldSize + ",all=" + allAnswerList.size());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            voiceCannonOnMessage.coursewareDoingLoad(currentAnswerIndex);
                                        }
                                    });
                                } else {
                                    if (integer >= maxSingCount) {
                                        isTurnPage = true;
                                        for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                                            GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                                            if (answer.getId() == word_id) {
                                                allAnswerList.remove(allAns);
                                                mLogtf.d("VOICE_CANNO_TYPE:currentAnswerIndex=" + currentAnswerIndex);
                                                currentAnswerIndex++;
                                                break;
                                            }
                                        }
                                    } else {
                                        isTurnPage = false;
                                    }
                                }
                                createSpeechContent("VOICE_CANNO_TYPE", false);
                                //来的单词是不是和当前一致
                                final AtomicBoolean correntPager = new AtomicBoolean(true);
                                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
                                if (oldIndex < test.getAnswerList().size()) {
                                    int id = test.getAnswerList().get(oldIndex).getId();
                                    mLogtf.d("VOICE_CANNO_TYPE:id=" + id + ",word_id=" + word_id);
                                    if (id != word_id) {
                                        correntPager.set(false);
                                    }
                                }
                                int studentNum = -1;
                                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                                String allUserIds = "";
                                for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {
                                    TeamMemberEntity teamMemberEntity = entities.get(entityIndex);
                                    allUserIds += "," + teamMemberEntity.id;
                                    if (who_id == teamMemberEntity.id) {
                                        studentNum = 4 - entities.size() + entityIndex;
                                        break;
                                    }
                                }
                                if (studentNum != -1) {
                                    final int finalStudentNum = studentNum;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!lessOther.get() && correntPager.get()) {
                                                JSONObject jsonData = new JSONObject();
                                                try {
                                                    jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                                                    jsonData.put("score", score);
                                                    jsonData.put("studentNum", finalStudentNum);
                                                    if (isTurnPage) {
                                                        GroupGameLog.sno4(liveAndBackDebug, detailInfo.id, currentAnswerIndex + "", 1);
                                                        jsonData.put("turnToPageNum", currentAnswerIndex);
                                                        reStartSpeechRecognize();
                                                    } else {
                                                        jsonData.put("turnToPageNum", -1);
                                                    }
                                                    wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + who_id);
                                            mLogtf.d("VOICE_CANNO_STATIS:onScene:who_id=" + who_id + ",courseGroupItem=null?" + (courseGroupItem == null));
                                            if (courseGroupItem != null) {
                                                courseGroupItem.onScene("VOICE_CANNO_STATIS");
                                            }
                                        }
                                    });
                                } else {
                                    mLogtf.d("VOICE_CANNO_STATIS:allUserIds=" + allUserIds);
                                }
                            }
                            getCurrent("STATISend");
                        } catch (JSONException e) {
                            mLogtf.e("onMessage:Statis", e);
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                            MobAgent.httpResponseParserError(TAG, "onMessage:Statis", e.getMessage());
                        }
                    }
                    break;
                    case TcpConstants.VOICE_CANNO_SCENE: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            String test_id = jsonObject.optString("test_id");
                            if (!detailInfo.id.equals(test_id)) {
                                return;
                            }
                            getCurrent("SCENEstart");
                            int current_word = jsonObject.optInt("current_word", currentAnswerIndex);
                            List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testEntity.getAnswerList();
                            //删除之前的试题
                            for (int i = 0; i < Math.min(current_word, answerList.size()) - 1; i++) {
                                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(i);
                                boolean remove = allAnswerList.remove(answersEntity);
                                mLogtf.d("VOICE_CANNO_SCENE:id=" + answersEntity.getId() + ",remove=" + remove);
                            }
                            wordCount.clear();
                            JSONArray mateArray = jsonObject.optJSONArray("stu_data");
                            if (mateArray != null) {
                                for (int i = 0; i < mateArray.length(); i++) {
                                    JSONObject mateObj = mateArray.getJSONObject(i);
                                    String stu_id = mateObj.getString("stu_id");
                                    final int total_energy = mateObj.getInt("total_energy");
                                    JSONObject word_scores = mateObj.optJSONObject("word_scores");
                                    if (word_scores != null) {
                                        int rightNum = 0;
                                        VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + stu_id);
                                        Iterator<String> keys = word_scores.keys();
                                        while (keys.hasNext()) {
                                            String key = keys.next();
                                            int wordId = Integer.parseInt(key);
                                            Integer integer = wordCount.get(key);
                                            if (integer == null) {
                                                integer = 0;
                                            }
                                            ArrayList<Integer> allScore = new ArrayList<>();
                                            if (vidooCannonEntity != null) {
                                                for (int j = 0; j < answerList.size(); j++) {
                                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(j);
                                                    if (answer.getId() == wordId) {
                                                        HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = vidooCannonEntity.wordScore;
                                                        wordScore.put(answer, allScore);
                                                        break;
                                                    }
                                                }
                                            }
                                            boolean isEmpty;
                                            List<SpeechResult> speechResults = scoreHashmap.get(wordId);
                                            if (speechResults == null) {
                                                isEmpty = true;
                                                speechResults = new ArrayList<>();
                                                scoreHashmap.put(wordId, speechResults);
                                            } else {
                                                isEmpty = speechResults.isEmpty();
                                            }
                                            JSONArray word_score = word_scores.getJSONArray(key);
                                            for (int wordIndex = 0; wordIndex < word_score.length(); wordIndex++) {
                                                int score = word_score.getInt(wordIndex);
                                                allScore.add(score);
                                                if (isEmpty && stu_id.equals("" + stuid)) {
                                                    SpeechResult speechResult = new SpeechResult();
                                                    speechResult.score = score;
                                                    speechResults.add(speechResult);
                                                }
                                                rightNum++;
                                                integer++;
                                            }
                                            wordCount.put(key, integer);
                                            int maxSingCount = testEntity.getSingleCount();
                                            mLogtf.d("VOICE_CANNO_SCENE:key=" + key + ",integer=" + integer + ",maxSingCount=" + maxSingCount);
                                            if (integer >= maxSingCount) {
                                                for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                                                    if (answer.getId() == wordId) {
                                                        allAnswerList.remove(allAns);
                                                        currentAnswerIndex++;
                                                        //为了以防没有移除完成，偶现。需要查剩下的id数量
                                                        if (currentAnswerIndex > testEntity.getAnswerList().size() - 1) {
                                                            String allId = "";
                                                            for (int aindex = 0; aindex < allAnswerList.size(); aindex++) {
                                                                allId += allAnswerList.get(aindex) + ",";
                                                            }
                                                            mLogtf.d("VOICE_CANNO_SCENE:allId=" + allId);
                                                            allAnswerList.clear();
                                                        } else {
                                                            mLogtf.d("VOICE_CANNO_SCENE:wordId=" + wordId);
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (vidooCannonEntity != null) {
                                            vidooCannonEntity.rightNum = rightNum;
                                        }
                                    }
                                    final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stu_id);
                                    if (courseGroupItem != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                TeamMemberEntity entity = courseGroupItem.getEntity();
                                                if (total_energy > GroupGameConfig.CANNON_MAX_ENERGY) {
                                                    entity.setEnergy(GroupGameConfig.CANNON_MAX_ENERGY);
                                                } else {
                                                    entity.setEnergy(total_energy);
                                                }
                                                courseGroupItem.onScene("VOICE_CANNO_SCENE");
                                            }
                                        });
                                    }
                                }
                            }
//                            if (!allAnswerList.isEmpty()) {
//                                current_word = answerList.size() - allAnswerList.size();
//                            } else {
//                                current_word = answerList.size() - 1;
//                            }
                            if (current_word > currentAnswerIndex) {
                                currentAnswerIndex = current_word;
                            }
                            //删除之前的试题
                            for (int i = 0; i < Math.min(currentAnswerIndex, answerList.size()) - 1; i++) {
                                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(i);
                                boolean remove = allAnswerList.remove(answersEntity);
                                if (remove) {
                                    mLogtf.d("VOICE_CANNO_SCENE:id=" + answersEntity.getId() + ",text=" + answersEntity.getText());
                                }
                            }
                            createSpeechContent("VOICE_CANNO_SCENE", false);
                            getCurrent("SCENEend");
                            voiceCannonOnMessage.coursewareDoingLoad(currentAnswerIndex);
                        } catch (Exception e) {
                            mLogtf.e("onMessage:VOICE_CANNO_SCENE", e);
                            CrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                    break;
                }
            }
        }

        private void getCurrent(String method) {
            try {
                String ids = "";
                for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                    ids += answer.getId() + ",";
                }
                mLogtf.d("getCurrent:method=" + method + ",current=" + currentAnswerIndex + ",ids=" + ids);
            } catch (Exception e) {
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.VOICE_CANNO_TYPE};
        }
    }

    /** 客户端发CleanUp.服务端返回 */
    class CleanUpTcpMessage implements TcpMessageAction {
        CleanUpOnMessage cleanUpOnMessage;

        @Override
        public void onMessage(short type, int operation, String msg) {
            mLogtf.d("onMessage:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.CLEAN_UP_TYPE) {
                switch (operation) {
                    case TcpConstants.CLEAN_UP_REC: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            String test_id = jsonObject.optString("test_id");
                            if (!detailInfo.id.equals(test_id)) {
                                return;
                            }
                            final int word_id = jsonObject.getInt("word_id");
                            String word = jsonObject.getString("word");
                            final int who_id = jsonObject.getInt("who_id");
                            final int incr_energy = jsonObject.getInt("incr_energy");
                            final int scores = jsonObject.optInt("word_score");
                            final int continue_rob = jsonObject.getInt("continue_rob");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + who_id);
                                        if (cleanUpEntity != null) {
                                            //把用户的信息恢复
                                            for (int i = 0; i < allAnswerList.size(); i++) {
                                                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(i);
                                                if (answer.getId() == word_id) {
                                                    allAnswerList.remove(i);
                                                    answer.setGetFireCount(incr_energy);
                                                    List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> rightAnswerList = cleanUpEntity.rightAnswerList;
                                                    cleanUpEntity.wordScore.put(answer, new ScoreEnergy(scores, incr_energy));
                                                    rightAnswerList.add(answer);
                                                    break;
                                                }
                                            }
                                        }
                                        createSpeechContent("CLEAN_UP_REC", false);
                                        int studentNum = -1;
                                        ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                                        for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {
                                            TeamMemberEntity teamMemberEntity = entities.get(entityIndex);
                                            if (who_id == teamMemberEntity.id) {
                                                studentNum = 1 + entityIndex;
                                                break;
                                            }
                                        }
                                        JSONObject jsonData = new JSONObject();
                                        jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                                        jsonData.put("studentNum", studentNum);
                                        {
                                            JSONObject rightItem = new JSONObject();
                                            rightItem.put("rightId", word_id);
                                            rightItem.put("getFireCount", incr_energy);
                                            jsonData.put("rightItem", rightItem);
                                        }
                                        jsonData.put("combo", continue_rob);
                                        wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                                    } catch (Exception e) {
                                        logger.d("onMessage:CLEAN_UP_REC:postMessage", e);
                                    }
                                    BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + who_id);
                                    if (courseGroupItem != null) {
                                        courseGroupItem.getEntity().energy += incr_energy;
                                        courseGroupItem.onScene("CLEAN_UP_REC");
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            logger.d("onMessage:CLEAN_UP_REC", e);
                        }
                    }
                    break;
                    case TcpConstants.CLEAN_UP_SECN: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            String test_id = jsonObject.optString("test_id");
                            if (!detailInfo.id.equals(test_id)) {
                                return;
                            }
                            JSONArray dataAray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataAray.length(); i++) {
                                int totalEnergy = 0;
                                JSONObject stuObj = dataAray.getJSONObject(i);
                                String stu_id = stuObj.getString("stu_id");
                                CleanUpEntity cleanUpEntity = cleanUpEntities.get(stu_id);
                                int total_energy = stuObj.getInt("total_energy");
                                if (cleanUpEntity != null) {
                                    cleanUpEntity.teamMemberEntity.energy = total_energy;
                                }
                                JSONArray rob_wordsArray = stuObj.getJSONArray("rob_words");
                                for (int j = 0; j < rob_wordsArray.length(); j++) {
                                    JSONObject rob_wordObj = rob_wordsArray.getJSONObject(j);
                                    int word_id = rob_wordObj.getInt("word_id");
//                                    String word_text = rob_wordObj.getString("word_text");
                                    int scores = rob_wordObj.optInt("scores");
                                    try {
                                        if (stu_id.equals("" + stuid)) {
                                            //恢复clean up的分数
                                            boolean isEmpty;
                                            List<SpeechResult> speechResults = scoreHashmap.get(word_id);
                                            if (speechResults == null) {
                                                isEmpty = true;
                                                speechResults = new ArrayList<>();
                                                scoreHashmap.put(word_id, speechResults);
                                            } else {
                                                isEmpty = speechResults.isEmpty();
                                            }
                                            if (isEmpty) {
                                                SpeechResult speechResult = new SpeechResult();
                                                speechResult.score = scores;
                                                speechResults.add(speechResult);
                                            }
                                        }
                                    } catch (Exception e) {
                                        CrashReport.postCatchedException(new LiveException(TAG, e));
                                    }
                                    int incr_energy = rob_wordObj.optInt("incr_energy");
                                    totalEnergy += incr_energy;
                                    for (int k = 0; k < allAnswerList.size(); k++) {
                                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(k);
                                        if (answer.getId() == word_id) {
                                            allAnswerList.remove(answer);
                                            answer.setGetFireCount(incr_energy);
                                            if (cleanUpEntity != null) {
                                                cleanUpEntity.rightAnswerList.add(answer);
                                                cleanUpEntity.wordScore.put(answer, new ScoreEnergy(scores, incr_energy));
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (cleanUpEntity == null) {
                                    logger.d("CLEAN_UP_SECN:stu_id=" + stu_id);
                                } else {
                                    logger.d("CLEAN_UP_SECN:stu_id=" + stu_id + ",right=" + cleanUpEntity.rightAnswerList.size());
                                }
                                final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stu_id);
                                if (courseGroupItem != null) {
                                    courseGroupItem.getEntity().energy = totalEnergy;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            courseGroupItem.onScene("CLEAN_UP_SECN");
                                        }
                                    });
                                }
                            }
                            createSpeechContent("CLEAN_UP_SECN", false);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    cleanUpOnMessage.onScene("CLEAN_UP_SECN");
                                }
                            });
                        } catch (Exception e) {
                            logger.d("onMessage:CLEAN_UP_SECN", e);
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.CLEAN_UP_TYPE};
        }
    }

    /**
     * 接收用户禁用音视频的消息
     */
    class TeamVideoAudioMessage implements TcpMessageAction {

        @Override
        public void onMessage(short type, int operation, String msg) {
            if (TcpConstants.AUDIO_TYPE == type) {
                switch (operation) {
                    case TcpConstants.AUDIO_REC: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            String id = jsonObject.getString("id");
                            final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + id);
                            if (courseGroupItem != null) {
                                final int opertype = jsonObject.getInt("type");
                                final boolean enable = jsonObject.getInt("enable") == 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        courseGroupItem.onOtherDis(opertype, enable);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            logger.d("AUDIO_TYPE:onMessage:e=" + e.getMessage());
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.AUDIO_TYPE};
        }
    }
}
