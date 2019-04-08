package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
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
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.GetStuActiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.config.GroupGameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.CleanUpEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
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
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

/**
 * @Date on 2019/3/15 18:31
 * @Author zhangyuansun
 * @Description
 */
public class GroupGameMultNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {

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
    /** 是不是回放 */
    private boolean isPlayBack = false;
    //    private NewCourseSec newCourseSec;
    private GroupGameTestInfosEntity mGroupGameTestInfosEntity;
    private List<GroupGameTestInfosEntity.TestInfoEntity> tests = new ArrayList<>();
    /** 每个题是作答次数 */
    private HashMap<String, Integer> wordCount = new HashMap<>();
    private int currentAnswerIndex = 0;
    private boolean gameOver = false;
    /**
     * 新课件是否是预加载
     */
    private boolean isPreload;
    private String learningStage;
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

    private int mPagerIndex = 0;
    private int mSingCount = 0;
    private InteractiveTeam interactiveTeam;
    private WorkerThread mWorkerThread;
    private HashMap<String, BaseCourseGroupItem> courseGroupItemHashMap = new HashMap<>();
    private LiveGetInfo liveGetInfo;
    private int stuid;
    private HashMap<String, CleanUpEntity> cleanUpEntities = new HashMap<>();
    private HashMap<String, VidooCannonEntity> vidooCannonEntities = new HashMap<>();
    private String speechContent = "";
    /** 总的答题，语音炮弹是够数移除。clean up是移除然后增加到用户信息里 */
    private List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> allAnswerList = new ArrayList<>();
    private List<Integer> allScoreList = new ArrayList<>();
    private GetStuActiveTeam getStuActiveTeam;
    private TcpMessageReg tcpMessageReg;
    /** 接收游戏的消息 */
    private TcpMessageAction tcpMessageAction;
    /** 接收用户禁用音视频的消息 */
    private TeamVideoAudioMessage teamVideoAudioMessage;
    private EvaluatorIng evaluatorIng;
    private PreLoad preLoad;
    private JSONArray userAnswer = new JSONArray();
    private int rightNum = 0;

    public GroupGameMultNativePager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity) {
        super(context);
        this.detailInfo = detailInfo;
        gameType = detailInfo.type;
        this.url = englishH5Entity.getUrl();
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
        return view;
    }

    @Override
    public void initData() {
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.request(null);
        }
        //网页消息
        final StaticWeb.OnMessage onMessage;
        if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
            onMessage = new CleanUpOnMessage();
        } else {
            onMessage = new VoiceCannonOnMessage();
        }
//        startSpeechRecognize();
        getStuActiveTeam = ProxUtil.getProxUtil().get(mContext, GetStuActiveTeam.class);
        //有战队pk，才有这种题的多人.目前不会发生了
        if (getStuActiveTeam != null) {
            interactiveTeam = getStuActiveTeam.getStuActiveTeam(new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {

                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    super.onDataFail(errStatus, failMsg);
                }
            });
            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
            if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    CleanUpEntity cleanUpEntity = new CleanUpEntity();
                    cleanUpEntity.teamMemberEntity = teamMemberEntity;
                    cleanUpEntities.put("" + teamMemberEntity.id, cleanUpEntity);
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    VidooCannonEntity vidooCannonEntity = new VidooCannonEntity();
                    vidooCannonEntity.teamMemberEntity = teamMemberEntity;
                    vidooCannonEntities.put("" + teamMemberEntity.id, vidooCannonEntity);
                }
            }
            //有战队pk，才使用有tcp
            tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
            if (tcpMessageReg != null) {
                if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                    CleanUpOnMessage cleanUpOnMessage = (CleanUpOnMessage) onMessage;
                    CleanUpTcpMessage cleanUpTcpMessage = new CleanUpTcpMessage();
                    cleanUpTcpMessage.cleanUpOnMessage = cleanUpOnMessage;
                    tcpMessageAction = cleanUpTcpMessage;
                    evaluatorIng = new CleanEvaluatorIng();
                } else {
                    VoiceCannonOnMessage voiceCannnon = (VoiceCannonOnMessage) onMessage;
                    VoiceProjectile voiceProjectile = new VoiceProjectile();
                    voiceProjectile.voiceCannonOnMessage = voiceCannnon;
                    tcpMessageAction = voiceProjectile;
                    evaluatorIng = new VoiceCannnon();
                }
            }
            joinChannel(entities);
        }
        newCourseCache = new GroupCourseCache(mContext, liveId);
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
                    CrashReport.postCatchedException(new Exception());
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());

        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, onMessage), "xesApp");
//        wvSubjectWeb.loadUrl(TEST_URL);
    }

    /**
     * 语音炮弹的网页消息
     */
    class VoiceCannonOnMessage implements StaticWeb.OnMessage {

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
                e.printStackTrace();
            }
        }

        private void onLoadComplete(String where, JSONObject message) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    preLoad.onStop();
                    coursewareOnloading(currentAnswerIndex);
                    if (!gameOver) {
                        startSpeechRecognize();
                    }
                }
            });
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
                        resultData.put("restTime", testInfoEntity.getTotalTime());
                        Integer integer = wordCount.get("" + pageNum);
                        mLogtf.d("coursewareOnloading:pageNum=" + pageNum + ",integer=" + integer);
                        if (integer == null) {
                            resultData.put("currentRight", 0);
                        } else {
                            resultData.put("currentRight", integer);
                        }
                        resultData.put("isSingle", false);
                        StaticWeb.sendToCourseware(wvSubjectWeb, resultData, "*");
                    } catch (Exception e) {
                        logger.d("onLoadComplete2", e);
                    }
                }
            });
        }
    }

    /**
     * cleanup的网页消息
     */
    class CleanUpOnMessage implements StaticWeb.OnMessage {

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
                e.printStackTrace();
            }
        }

        private void onLoadComplete(String where, JSONObject message) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    preLoad.onStop();
                    onScene("onLoadComplete");
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
                resultData.put("restTime", testInfoEntity.getTotalTime());
                JSONArray studentInfo = new JSONArray();
                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    TeamMemberEntity teamMemberEntity = entities.get(i);
                    JSONObject student = new JSONObject();
                    student.put("studentNum", 3 - i);
                    student.put("name", teamMemberEntity.name);
//                            student.put("avatar", teamMemberEntity.headurl);
                    student.put("avatar", "http://xesfile.oss-cn-beijing.aliyuncs.com/nrcpb/cjtsg5ybb0000ywow5xr4ne1j.png");
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
                StaticWeb.sendToCourseware(wvSubjectWeb, resultData, "*");
            } catch (Exception e) {
                logger.d("onScene", e);
            }
        }
    }

    private void joinChannel(ArrayList<TeamMemberEntity> entities) {
        mWorkerThread = new WorkerThread(mContext, stuid, false, true);
        mWorkerThread.eventHandler().addEventHandler(agEventHandler);
        mWorkerThread.setEnableLocalVideo(true);
        mWorkerThread.setOnEngineCreate(new WorkerThread.OnEngineCreate() {
            @Override
            public void onEngineCreate(final RtcEngine mRtcEngine) {
                VideoEncoderConfiguration.VideoDimensions dimensions = new VideoEncoderConfiguration.VideoDimensions(256, 192);
                VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(dimensions,
                        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
                        VideoEncoderConfiguration.STANDARD_BITRATE,
                        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE);
                int setVideoEncoder = mRtcEngine.setVideoEncoderConfiguration(configuration);
                logger.d("onEngineCreate:setVideoEncoder=" + setVideoEncoder);
            }
        });
        addTeam(entities);
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        String channel = liveGetInfo.getId() + "_" + liveGetInfo.getStudentLiveInfo().getClassId() + "_" + getStuActiveTeam.getPkTeamEntity().getPkTeamId() + "_" + interactiveTeam.getInteractive_team_id();
        logger.d("joinChannel:channel=" + channel);
        mWorkerThread.joinChannel("", channel, stuid, new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                logger.d("onJoinChannel:joinChannel=" + joinChannel);
            }
        });
    }

    private void leaveChannel() {
        if (mWorkerThread != null) {
            mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLeaveChannel() {
                @Override
                public void onLeaveChannel(int leaveChannel) {

                }
            });
            mWorkerThread.eventHandler().removeEventHandler(agEventHandler);
            mWorkerThread.exit();
            logger.d("leaveChannel:mWorkerThread.joinstart");
            try {
                mWorkerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.d("leaveChannel:mWorkerThread.joinend");
            mWorkerThread = null;
        }
    }

    private AGEventHandler agEventHandler = new AGEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            logger.d("onFirstRemoteVideoDecoded:uid=" + uid + ",courseGroupItem=null?" + (courseGroupItem == null));
            if (courseGroupItem != null) {
                doRenderRemoteUi(uid, courseGroupItem);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
            if (stuid == uid) {
                BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
                if (courseGroupItem != null) {
                    preview(uid, courseGroupItem);
                }
            }
        }

        @Override
        public void onUserJoined(final int uid, final int elapsed) {
            logger.d("onUserJoined:uid=" + uid + ",elapsed=" + elapsed);
        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            logger.d("onUserOffline:uid=" + uid + ",reason=" + reason);
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
            logger.d("onError:err=" + err);
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
        final JSONArray team_mate = new JSONArray();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < entities.size(); i++) {
            TeamMemberEntity teamMemberEntity = entities.get(i);
            team_mate.put("" + teamMemberEntity.id);
            BaseCourseGroupItem baseCourseGroupItem;
            if (teamMemberEntity.id == stuid) {
                CourseGroupMyItem courseGroupItem = new CourseGroupMyItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            } else {
                CourseGroupOtherItem courseGroupItem = new CourseGroupOtherItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            }
            baseCourseGroupItem.setOnVideoAudioClick(new BaseCourseGroupItem.OnVideoAudioClick() {
                @Override
                public void onVideoClick(boolean enable) {
                    if (teamVideoAudioMessage == null) {
                        teamVideoAudioMessage = new TeamVideoAudioMessage();
                        tcpMessageReg.registTcpMessageAction(tcpMessageAction);
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("live_id", liveId);
                        jsonObject.put("team_mate", team_mate);
                        JSONObject data = new JSONObject();
                        data.put("type", GroupGameConfig.OPERATION_VIDEO);
                        data.put("enable", enable ? 0 : 1);
                        jsonObject.put("data", data);
                        tcpMessageReg.send(TcpConstants.AUDIO_TYPE, TcpConstants.AUDIO_SEND, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAudioClick(boolean enable) {
                    if (teamVideoAudioMessage == null) {
                        teamVideoAudioMessage = new TeamVideoAudioMessage();
                        tcpMessageReg.registTcpMessageAction(tcpMessageAction);
                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("live_id", liveId);
                        jsonObject.put("team_mate", team_mate);
                        JSONObject data = new JSONObject();
                        data.put("type", GroupGameConfig.OPERATION_AUDIO);
                        data.put("enable", enable ? 0 : 1);
                        jsonObject.put("data", data);
                        tcpMessageReg.send(TcpConstants.AUDIO_TYPE, TcpConstants.AUDIO_SEND, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            View convertView = mInflater.inflate(baseCourseGroupItem.getLayoutResId(), llCourseItemContent, false);
            baseCourseGroupItem.initViews(convertView);
            baseCourseGroupItem.updateViews(teamMemberEntity, i, teamMemberEntity);
            baseCourseGroupItem.bindListener();
            llCourseItemContent.addView(convertView);
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
                llCourseItemContent.addView(convertView);
            }
        }
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

    private void startSpeechRecognize() {
        File dir = LiveCacheFile.geCacheFile(mContext, "liveSpeech");
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
        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mParam.setLang(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);
        mParam.setStrEvaluator(speechContent);
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setPcm(true);
        mParam.setLearning_stage(learningStage);
        mIse.startRecog(mParam, new EvaluatorListenerWithPCM() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech()");

            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    logger.d("onEvaluatorSuccess(): score = " + resultEntity.getScore());
                    onRecognizeStop();
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    logger.d("onEvaluatorError: ErrorNo = " + resultEntity.getErrorNo() + ", isOfflineFail =" + mIse.isOfflineFail());
                    onRecognizeStop();
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    if (resultEntity.getNewSenIdx() >= 0) {
                        logger.d("onEvaluatoring: newSenIdx = " + resultEntity.getNewSenIdx() + ", score =" + resultEntity.getScore());
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

    private void onRecognizeStop() {
        if (isAttach()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!gameOver) {
                        XESToastUtils.showToast(mContext, "评测完成");
                        startSpeechRecognize();
                    }
                }
            }, 1000);
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
//                if (AppConfig.DEBUG) {
//                    ArrayList<TeamMemberEntity> entities = new ArrayList<>();
//                    if (interactiveTeam != null) {
//                        entities = interactiveTeam.getEntities();
//                        TeamMemberEntity teamMemberEntity = entities.get(0);
//                        teamMemberEntity.gold = 11;
//                        teamMemberEntity.energy = 12;
//                    }
//                    String[] heads = {"https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=107d95c34134970a537e187df4a3baad/a8014c086e061d95b2d56ad47bf40ad163d9ca4f.jpg",
//                            "https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/crop%3D24%2C0%2C851%2C562%3Bc0%3Dbaike92%2C5%2C5%2C92%2C30/sign=031be2a37f3e6709aa4f1fbf06f5ab11/fd039245d688d43f23098f767d1ed21b0ff43b95.jpg"};
//                    int count = 2;
//                    if (entities.size() < count) {
////                        TeamMemberEntity myTeamMemberEntity = entities.get(0);
////                        myTeamMemberEntity.gold = 11;
////                        myTeamMemberEntity.energy = 12;
//                        for (int i = 0; i < 1; i++) {
//                            TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
//                            teamMemberEntity.id = i;
//                            teamMemberEntity.name = "测试测试" + i;
//                            teamMemberEntity.headurl = heads[i];
//                            teamMemberEntity.gold = (i + 2) * 10 + 1;
//                            teamMemberEntity.energy = (i + 2) * 10 + 2;
//                            entities.add(teamMemberEntity);
//                        }
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
            }
        });
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

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        getCourseWareTests();
    }

    private void getCourseWareTests() {
        preLoad.onStart();
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                tests = mGroupGameTestInfosEntity.getTestInfoList();
                if (tests.isEmpty()) {
                    XESToastUtils.showToast(mContext, "互动题为空");
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
//                if (AppConfig.DEBUG) {
//                    test.setPreviewPath("file:///android_asset/group_game_cleanup/index.html");
//                }
                allAnswerList.addAll(test.getAnswerList());
                createSpeechContent("getCourseWareTests");
                wvSubjectWeb.loadUrl(test.getPreviewPath());
                int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
                if (type != 0) {
                    ispreload = type == 1;
                } else {
                    ispreload = true;
                }
                connectTcp();
                NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), test.getPreviewPath(), ispreload, test.getTestId());
            }

            private void connectTcp() {
                if (tcpMessageReg != null) {
                    tcpMessageReg.onConnet(new TcpMessageReg.OnTcpReg() {
                        @Override
                        public void onReg() {
                            boolean change = tcpMessageReg.setTest(LiveQueConfig.EN_COURSE_GAME_TYPE_1, detailInfo.id);
                            mLogtf.d("connectTcp(setTest):change=" + change);
                            tcpMessageReg.registTcpMessageAction(tcpMessageAction);
                        }
                    });
                }
            }

            /**
             * 倒计时
             * @param startTime
             * @return
             */
            private String getTimeNegativeEn(long releaseTime, long startTime) {
                long time = System.currentTimeMillis() / 1000 - startTime;
                long second = (releaseTime - time) % 60;
                long minute = (releaseTime - time) / 60;
                if (releaseTime - time < 0) {
                    return null;
                }
                return minute + "分" + second + "秒";
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg + ",请刷新");
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
                logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
//                preLoad.onStop();
            }
        });
    }

    private void submit() {
        int voiceTime = 0;
        int starNum = 0;
        int energy = 0;
        int gold = 1;
        //视频开启时长，单位：毫秒
        int videoLengthTime = 0;
        //麦克开启时长，单位：毫秒
        int micLengthTime = 0;
        //收他人视频开启时长，单位：毫秒
        int acceptVideoLengthTime = 0;
        //接收他人麦克开启时长，单位：毫秒
        int acceptMicLengthTime = 0;
        PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
        int gameGroupId = interactiveTeam.getInteractive_team_id();
        JSONObject answerData = new JSONObject();
        try {
            answerData.put("tryTimes", allScoreList.size());
            JSONArray userAnswer = new JSONArray();
            VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + stuid);
            if (vidooCannonEntity != null) {
                answerData.put("rightNum", vidooCannonEntity.rightNum);
                HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = vidooCannonEntity.wordScore;
                Set<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> keySet = wordScore.keySet();
                for (GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity : keySet) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("text", answersEntity.getText());
                    ArrayList<Integer> arrayList = wordScore.get(answersEntity);
                    String scores = "";
                    for (int j = 0; j < arrayList.size(); j++) {
                        scores += arrayList.get(j);
                        if (j < arrayList.size() - 1) {
                            scores += ",";
                        }
                    }
                    jsonObject.put("scores", scores);
                    jsonObject.put("voiceTime", 1);
                    jsonObject.put("isRight", 1);
                }
            } else {
                answerData.put("rightNum", 0);
            }
            answerData.put("averageScore", 1);
            answerData.put("userAnswer", userAnswer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        englishH5CoursewareSecHttp.submitGroupGame(detailInfo, 1, voiceTime, teamEntity.getPkTeamId(), gameGroupId, starNum, energy, gold,
                videoLengthTime, micLengthTime
                , acceptVideoLengthTime, acceptMicLengthTime, answerData.toString(), new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        logger.d("submitGroupGame->onDataSucess:objData=" + objData);
                        ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                        GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
                        ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
                        groupGameMVPMultPager.setOnPagerClose(new OnPagerClose() {
                            @Override
                            public void onClose(LiveBasePager basePager) {
                                ((ViewGroup) mView).removeView(basePager.getRootView());
                            }
                        });
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.d("submitGroupGame->onDataFail:" + failMsg);
                        if (AppConfig.DEBUG) {
                            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                            GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
                            ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
                            groupGameMVPMultPager.setOnPagerClose(new OnPagerClose() {
                                @Override
                                public void onClose(LiveBasePager basePager) {
                                    ((ViewGroup) mView).removeView(basePager.getRootView());
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public BasePager getBasePager() {
        return null;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wvSubjectWeb.destroy();
        if (mIse != null) {
            mIse.cancel();
        }
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

    private void createSpeechContent(String method) {
        speechContent = "";
        if (allAnswerList.isEmpty()) {
            gameOver = true;
            if (mIse != null) {
                mIse.cancel();
            }
            XESToastUtils.showToast(mContext, "游戏结束");
            logger.d("createSpeechContent:method=" + method + ",gameOver");
            submit();
            return;
        }
        for (int j = 0; j < allAnswerList.size(); j++) {
            GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = allAnswerList.get(j);
            speechContent += answersEntity.getText() + "|";
        }
        if (speechContent.endsWith("|")) {
            speechContent = speechContent.substring(0, speechContent.length() - 1);
        }
        logger.d("createSpeechContent:method=" + method + ",speechContent=" + speechContent);
    }

    /**
     * 语音评测 - 命中句子
     */
    interface EvaluatorIng {
        void onResult(ResultEntity resultEntity);
    }

    private class VoiceCannnon implements EvaluatorIng {

        @Override
        public void onResult(ResultEntity resultEntity) {
            int score = resultEntity.getScore();
            allScoreList.add(score);
            if (score < 70) {
                BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                if (courseGroupItem != null) {
                    courseGroupItem.onOpps();
                }
                return;
            }
            mSingCount++;
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
                if (newSenIdx < 0 || newSenIdx >= allAnswerList.size()) {
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = allAnswerList.get(newSenIdx);
                GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = testInfoEntity.getAnswerList().get(currentAnswerIndex);
                logger.d("onResult:answersEntity=" + answersEntity.getText() + "," + removeAnswersEntity.getText());
                if (!TextUtils.equals(answersEntity.getText(), removeAnswersEntity.getText())) {
                    BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
                    if (courseGroupItem != null) {
                        courseGroupItem.onOpps();
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
                    bodyJson.put("word_id", "" + answersEntity.getId());
                    bodyJson.put("pk_team_id", teamEntity.getPkTeamId());
                    bodyJson.put("team_type", "interactive");
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
                    userData.put("incry_energy", 3);
                    bodyJson.put("userData", userData);
                    tcpMessageReg.send(TcpConstants.Voice_Projectile_TYPE, TcpConstants.Voice_Projectile_SEND, bodyJson.toString(), new SendCallBack() {
                        String TAG = "SendCallBack:";

                        @Override
                        public void onNoOpen() {
                            logger.d(TAG + "onNoOpen");
                        }

                        @Override
                        public void onStart(int seq) {
                            logger.d(TAG + "onStart:seq=" + seq);
                        }

                        @Override
                        public void onReceiveMeg(short type, int operation, int seq, String msg) {
                            logger.d(TAG + "onReceiveMeg:seq=" + seq);
                        }

                        @Override
                        public void onTimeOut() {
                            logger.d(TAG + "onTimeOut");
                        }
                    });
                }
            } catch (Exception e) {
                logger.d("onResult", e);
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
                int score = resultEntity.getScore();
                allScoreList.add(score);
                if (score < 70) {
                    return;
                }
                mSingCount++;
                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + stuid);

                int newSenIdx = resultEntity.getNewSenIdx();
                logger.d("CleanEvaluatorIng:newSenIdx=" + newSenIdx + ",size" + allAnswerList.size() + ",speechContent=" + speechContent);
                if (newSenIdx < 0 || newSenIdx >= allAnswerList.size()) {
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = allAnswerList.get(newSenIdx);
                {
                    //传入网页。现在通过tcp传
//                    allAnswerList.remove(removeAnswersEntity);
//                    cleanUpEntity.rightAnswerList.add(removeAnswersEntity);
//                    createSpeechContent("CleanEvaluatorIng");
//                    JSONObject jsonData = new JSONObject();
//                    jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
//                    jsonData.put("studentNum", 3);
//                    {
//                        JSONObject rightItem = new JSONObject();
//                        Random random = new Random();
//                        rightItem.put("rightId", removeAnswersEntity.getId());
//                        rightItem.put("getFireCount", 3);
//                        jsonData.put("rightItem", rightItem);
//                    }
//                    jsonData.put("combo", 0);
//                    wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                }
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
                    bodyJson.put("word", "" + removeAnswersEntity.getText());
                    bodyJson.put("pk_team_id", teamEntity.getPkTeamId());
                    bodyJson.put("team_type", "interactive");
                    bodyJson.put("interactive_team_id", interactiveTeam.getInteractive_team_id());
                    bodyJson.put("score", "" + score);
                    JSONArray team_mate = new JSONArray();
                    for (int i = 0; i < entities.size(); i++) {
                        TeamMemberEntity teamMemberEntity = entities.get(i);
                        team_mate.put("" + teamMemberEntity.id);
                    }
                    bodyJson.put("team_mate", team_mate);
                    tcpMessageReg.send(TcpConstants.CLEAN_UP_TYPE, TcpConstants.CLEAN_UP_SEND, bodyJson.toString(), new SendCallBack() {
                        String TAG = "SendCallBack:";

                        @Override
                        public void onNoOpen() {
                            logger.d(TAG + "onNoOpen");
                        }

                        @Override
                        public void onStart(int seq) {
                            logger.d(TAG + "onStart:seq=" + seq);
                        }

                        @Override
                        public void onReceiveMeg(short type, int operation, int seq, String msg) {
                            logger.d(TAG + "onReceiveMeg:seq=" + seq);
                        }

                        @Override
                        public void onTimeOut() {
                            logger.d(TAG + "onTimeOut");
                        }
                    });
                }
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
                logger.d("CleanEvaluatorIng", e);
            }
        }
    }

    /** 客户端发语音炮弹数据,服务端返回 */
    class VoiceProjectile implements TcpMessageAction {
        VoiceCannonOnMessage voiceCannonOnMessage;

        @Override
        public void onMessage(short type, int operation, String msg) {
            logger.d("onMessage:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.Voice_Projectile_TYPE) {
                switch (operation) {
                    case TcpConstants.Voice_Projectile_Statis: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            int word_id = dataObj.getInt("word_id");
                            final int who_id = dataObj.getInt("who_id");
                            final int score = dataObj.getInt("score");
                            Integer integer = wordCount.get("" + word_id);
                            if (integer == null) {
                                integer = 1;
                            } else {
                                integer++;
                            }
                            GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                            VidooCannonEntity vidooCannonEntity = vidooCannonEntities.get("" + who_id);
                            if (vidooCannonEntity != null) {
                                for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                                    if (answer.getId() == word_id) {
                                        vidooCannonEntity.rightNum++;
                                        //一个单词一个能量
                                        vidooCannonEntity.teamMemberEntity.energy++;
                                        break;
                                    }
                                }
                                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
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
                                if (integer >= maxSingCount) {
                                    isTurnPage = true;
                                    for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                                        if (answer.getId() == word_id) {
                                            allAnswerList.remove(allAns);
                                            logger.d("Voice_Projectile_TYPE:currentAnswerIndex=" + currentAnswerIndex);
                                            currentAnswerIndex++;
                                            break;
                                        }
                                    }
                                } else {
                                    isTurnPage = false;
                                }
                                createSpeechContent("Voice_Projectile_TYPE");
                                int studentNum = -1;
                                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                                for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {
                                    TeamMemberEntity teamMemberEntity = entities.get(entityIndex);
                                    if (who_id == teamMemberEntity.id) {
                                        studentNum = 1 + entityIndex;
                                        break;
                                    }
                                }
                                if (studentNum != -1) {
                                    final int finalStudentNum = studentNum;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            JSONObject jsonData = new JSONObject();
                                            try {
                                                jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                                                jsonData.put("score", score);
                                                jsonData.put("studentNum", finalStudentNum);
                                                jsonData.put("isTurnPage", isTurnPage);
                                                wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + who_id);
                                            if (courseGroupItem != null) {
                                                courseGroupItem.onScene();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            logger.d("onMessage:Statis", e);
                            MobAgent.httpResponseParserError(TAG, "onMessage:Statis", e.getMessage());
                        }
                    }
                    break;
                    case TcpConstants.Voice_Projectile_Scene: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            int current_word = jsonObject.optInt("current_word", currentAnswerIndex);
                            GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                            List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
                            for (int i = 0; i < current_word; i++) {
                                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(i);
                                allAnswerList.remove(answersEntity);
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
                                            JSONArray word_score = word_scores.getJSONArray(key);
                                            for (int wordIndex = 0; wordIndex < word_score.length(); wordIndex++) {
                                                int score = word_score.getInt(wordIndex);
                                                allScore.add(score);
                                                rightNum++;
                                                integer++;
                                            }
                                            wordCount.put(key, integer);
                                            int maxSingCount = testInfoEntity.getSingleCount();
                                            logger.d("Projectile_Scene:integer=" + integer + ",maxSingCount=" + maxSingCount);
                                            if (integer >= maxSingCount) {
                                                for (int allAns = 0; allAns < allAnswerList.size(); allAns++) {
                                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(allAns);
                                                    if (answer.getId() == wordId) {
                                                        allAnswerList.remove(allAns);
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
                                                entity.energy = total_energy;
                                                courseGroupItem.onScene();
                                            }
                                        });
                                    }
                                }
                            }
                            createSpeechContent("Voice_Projectile_Scene");
                            currentAnswerIndex = current_word;
                            voiceCannonOnMessage.coursewareOnloading(current_word);
                        } catch (Exception e) {
                            logger.d("onMessage:Scene", e);
                            CrashReport.postCatchedException(e);
                            MobAgent.httpResponseParserError(TAG, "onMessage:Scene", e.getMessage());
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public short[] getMessageFilter() {
            return new short[]{TcpConstants.Voice_Projectile_TYPE};
        }
    }

    /** 客户端发CleanUp.服务端返回 */
    class CleanUpTcpMessage implements TcpMessageAction {
        CleanUpOnMessage cleanUpOnMessage;

        @Override
        public void onMessage(short type, int operation, String msg) {
            logger.d("onMessage:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.CLEAN_UP_TYPE) {
                switch (operation) {
                    case TcpConstants.CLEAN_UP_REC: {
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            final int word_id = jsonObject.getInt("word_id");
                            String word = jsonObject.getString("word");
                            final int who_id = jsonObject.getInt("who_id");
                            final int incr_energy = jsonObject.getInt("incr_energy");
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
                                                    rightAnswerList.add(answer);
                                                    break;
                                                }
                                            }
                                        }
                                        createSpeechContent("CLEAN_UP_REC");
                                        JSONObject jsonData = new JSONObject();
                                        jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                                        jsonData.put("studentNum", 3);
                                        {
                                            JSONObject rightItem = new JSONObject();
                                            rightItem.put("rightId", word_id);
                                            rightItem.put("getFireCount", incr_energy);
                                            jsonData.put("rightItem", rightItem);
                                        }
                                        jsonData.put("combo", 0);
                                        wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                                    } catch (Exception e) {
                                        logger.d("onMessage:CLEAN_UP_REC:postMessage", e);
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
                            JSONArray dataAray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataAray.length(); i++) {
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
//                                    String scores = rob_wordObj.getString("scores");
                                    int incr_energy = rob_wordObj.optInt("incr_energy");
                                    for (int k = 0; k < allAnswerList.size(); k++) {
                                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = allAnswerList.get(k);
                                        if (answer.getId() == word_id) {
                                            allAnswerList.remove(answer);
                                            answer.setGetFireCount(incr_energy);
                                            if (cleanUpEntity != null) {
                                                cleanUpEntity.rightAnswerList.add(answer);
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
                            }
                            createSpeechContent("CLEAN_UP_SECN");
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
