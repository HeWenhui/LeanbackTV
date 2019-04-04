package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.CleanUpEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.BaseCourseGroupItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupMyItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupNoItem;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
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
import java.util.Random;
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
    private HashMap<String, Integer> integerHashMap = new HashMap<>();
    private int currentIndex = 0;
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
    private String speechContent = "";
    private GetStuActiveTeam getStuActiveTeam;
    private TcpMessageReg tcpMessageReg;
    private TcpMessageAction tcpMessageAction;
    private EvaluatorIng evaluatorIng;
    private PreLoad preLoad;
    private JSONObject answerData = new JSONObject();
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
            getStuActiveTeam.getStuActiveTeam(new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    interactiveTeam = (InteractiveTeam) objData[0];
                    ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                    if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(gameType)) {
                        for (int i = 0; i < entities.size(); i++) {
                            TeamMemberEntity teamMemberEntity = entities.get(i);
                            CleanUpEntity cleanUpEntity = new CleanUpEntity();
                            if (cleanUpEntities.containsKey("" + teamMemberEntity.id)) {
                                cleanUpEntity = cleanUpEntities.get("" + teamMemberEntity.id);
                            } else {
                                cleanUpEntities.put("" + teamMemberEntity.id, cleanUpEntity);
                            }
                            cleanUpEntity.teamMemberEntity = teamMemberEntity;
                            if (mGroupGameTestInfosEntity != null) {
                                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
                                cleanUpEntity.answerList.addAll(test.getAnswerList());
                            }
                        }
                    }
                    joinChannel(entities);
                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    super.onDataFail(errStatus, failMsg);
                }
            });
            //有战队pk，才使用有tcp
            tcpMessageReg = ProxUtil.getProxUtil().get(mContext, TcpMessageReg.class);
            if (tcpMessageReg != null) {
                tcpMessageReg.onConnet(new TcpMessageReg.OnTcpReg() {
                    @Override
                    public void onReg() {
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
                        boolean change = tcpMessageReg.setTest(LiveQueConfig.EN_COURSE_GAME_TYPE_1, detailInfo.id);
                        mLogtf.d("initData(setTest):change=" + change);
                        tcpMessageReg.registTcpMessageAction(tcpMessageAction);
                    }
                });
            }
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
                        resultData.put("currentRight", 0);
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
                                        rightObj.put("rightId", "" + answersEntity.getId());
                                        rightObj.put("getFireCount", "" + answersEntity.getGetFireCount());
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
                        logger.d("onLoadComplete1", e);
                    }
                    if (!gameOver) {
                        startSpeechRecognize();
                    }
                }
            });
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

    private void addTeam(ArrayList<TeamMemberEntity> entities) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < entities.size(); i++) {
            TeamMemberEntity teamMemberEntity = entities.get(i);
            BaseCourseGroupItem baseCourseGroupItem;
            if (teamMemberEntity.id == stuid) {
                CourseGroupMyItem courseGroupItem = new CourseGroupMyItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            } else {
                CourseGroupOtherItem courseGroupItem = new CourseGroupOtherItem(mContext, teamMemberEntity, mWorkerThread, teamMemberEntity.id);
                baseCourseGroupItem = courseGroupItem;
            }
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
                llCourseItemContent.addView(convertView, 0);
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
        boolean isTurnPage = message.optBoolean("isTurnPage");
        if (isTurnPage) {
            currentAnswerIndex++;
        }
        GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
        List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
        if (currentAnswerIndex >= answerList.size() - 1) {
            gameOver = true;
            if (mIse != null) {
                mIse.cancel();
            }
        }
        mLogtf.d("onCoursewareDoing:isTurnPage=" + isTurnPage + ",currentAnswerIndex=" + currentAnswerIndex + ",gameOver=" + gameOver);
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
                for (int i = 0; i < tests.size(); i++) {
                    GroupGameTestInfosEntity.TestInfoEntity test = tests.get(i);
                    List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = test.getAnswerList();
                    for (int j = 0; j < answerList.size(); j++) {
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(j);
                        speechContent += answersEntity.getText() + "|";
                    }
                    Set<String> keySet = cleanUpEntities.keySet();
                    for (String key : keySet) {
                        CleanUpEntity cleanUpEntity = cleanUpEntities.get(key);
                        cleanUpEntity.answerList.addAll(answerList);
                    }
                }
                if (speechContent.endsWith("|")) {
                    speechContent = speechContent.substring(0, speechContent.length() - 1);
                }
                logger.d("onDataSucess:speechContent=" + speechContent);
                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
//                if (AppConfig.DEBUG) {
//                    test.setPreviewPath("file:///android_asset/group_game_cleanup/index.html");
//                }
                currentIndex = 0;
                wvSubjectWeb.loadUrl(test.getPreviewPath());
                int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
                if (type != 0) {
                    ispreload = type == 1;
                } else {
                    ispreload = true;
                }
                NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), test.getPreviewPath(), ispreload, test.getTestId());
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
//        try {
//            answerData.put("tryTimes", allScoreList.size());
//            answerData.put("rightNum", "" + rightNum);
//            answerData.put("total", mGroupGameTestInfosEntity.getTestInfoList().get(0).getAnswerList().size());
//            int sum = 0;
//            for (int i = 0; i < allScoreList.size(); i++) {
//                sum += allScoreList.get(i);
//            }
//            int averageScore;
//            if (allScoreList.size() != 0) {
//                averageScore = sum / allScoreList.size();
//            } else {
//                averageScore = 0;
//            }
//            answerData.put("averageScore", averageScore);
//            answerData.put("userAnswer", userAnswer.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
        int gameGroupId = interactiveTeam.getInteractive_team_id();
        englishH5CoursewareSecHttp.submitGroupGame(detailInfo, 1, 0, teamEntity.getPkTeamId(), gameGroupId, 1, 1, 1, 0, 0, 0, 0, answerData.toString(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                logger.d("submitGroupGame->onDataSucess:objData=" + objData.toString());
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                logger.d("submitGroupGame->onDataFail:" + failMsg);
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
        if (tcpMessageReg != null && tcpMessageAction != null) {
            tcpMessageReg.unregistTcpMessageAction(tcpMessageAction);
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
     * 语音评测 - 命中句子
     */
    interface EvaluatorIng {
        void onResult(ResultEntity resultEntity);
    }

    private class VoiceCannnon implements EvaluatorIng {

        @Override
        public void onResult(ResultEntity resultEntity) {
            int score = resultEntity.getScore();
            mSingCount++;
            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
            GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
            List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = testInfoEntity.getAnswerList();
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                jsonData.put("score", score);
                jsonData.put("studentNum", 3);
                jsonData.put("isTurnPage", false);
                wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
                PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
                if (teamEntity != null) {
                    try {
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
                        GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(currentAnswerIndex);
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
                        JSONArray userData = new JSONArray();
                        for (int i = 0; i < 1; i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("word_id", "" + answersEntity.getId());
                            jsonObject.put("score", "" + score);
                            jsonObject.put("incry_energy", 3);
                            userData.put(jsonObject);
                        }
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
                    } catch (JSONException e) {
                        logger.d("onHitSentence", e);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CleanEvaluatorIng implements EvaluatorIng {

        @Override
        public void onResult(ResultEntity resultEntity) {
            try {
                int score = resultEntity.getScore();
                mSingCount++;
                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + stuid);
                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = cleanUpEntity.answerList;
                int newSenIdx = resultEntity.getNewSenIdx();
                logger.d("CleanEvaluatorIng:newSenIdx=" + newSenIdx + ",size" + answerList.size() + ",speechContent=" + speechContent);
                if (newSenIdx < 0 || newSenIdx >= answerList.size()) {
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity removeAnswersEntity = answerList.get(newSenIdx);
                answerList.remove(removeAnswersEntity);
                cleanUpEntity.rightAnswerList.add(removeAnswersEntity);
                speechContent = "";
                for (int j = 0; j < answerList.size(); j++) {
                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answersEntity = answerList.get(j);
                    speechContent += answersEntity.getText() + "|";
                }
                if (speechContent.endsWith("|")) {
                    speechContent = speechContent.substring(0, speechContent.length() - 1);
                }
                logger.d("CleanEvaluatorIng:speechContent=" + speechContent);
                JSONObject jsonData = new JSONObject();
                jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                jsonData.put("studentNum", 3);
                {
                    JSONObject rightItem = new JSONObject();
                    Random random = new Random();
                    rightItem.put("rightId", removeAnswersEntity.getId());
                    rightItem.put("getFireCount", 3);
                    jsonData.put("rightItem", rightItem);
                }
                jsonData.put("combo", 0);
                wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
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
                    bodyJson.put("word", "banana");
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
                            String word_id = dataObj.getString("word_id");
                            int who_id = dataObj.getInt("who_id");
                            final int score = dataObj.getInt("score");
                            Integer integer = integerHashMap.get(word_id);
                            if (integer == null) {
                                integer = 1;
                            } else {
                                integer++;
                            }
                            integerHashMap.put(word_id, integer);
                            {
                                GroupGameTestInfosEntity.TestInfoEntity testInfoEntity = tests.get(0);
                                int maxSingCount = testInfoEntity.getSingleCount();
                                final boolean isTurnPage;
                                if (integer >= maxSingCount) {
                                    isTurnPage = true;
                                } else {
                                    isTurnPage = false;
                                }
                                int studentNum = -1;
                                ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                                for (int i = entities.size() - 1; i >= 0; i--) {
                                    TeamMemberEntity teamMemberEntity = entities.get(i);
                                    if (who_id == teamMemberEntity.id) {
                                        studentNum = 3 - i;
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
                            JSONObject dataObj = jsonObject.getJSONObject("data");
                            int current_word = dataObj.getInt("current_word");

                            voiceCannonOnMessage.coursewareOnloading(current_word);

                            integerHashMap.clear();
                            JSONArray mateArray = dataObj.getJSONArray("stu_data");
                            for (int i = 0; i < mateArray.length(); i++) {
                                JSONObject mateObj = mateArray.getJSONObject(i);
                                String stu_id = mateObj.getString("stu_id");
                                final int total_erengy = mateObj.getInt("total_erengy");
                                JSONObject word_scores = mateObj.optJSONObject("word_scores");
                                if (word_scores != null) {
                                    Iterator<String> keys = word_scores.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        Integer integer = integerHashMap.get(key);
                                        if (integer == null) {
                                            integer = new Integer(0);
                                        } else {
                                            integerHashMap.put(key, integer);
                                        }
                                        JSONArray word_score = word_scores.getJSONArray(key);
                                        for (int j = 0; j < word_score.length(); j++) {
                                            String score = word_score.getString(j);
                                            integer++;
                                        }
                                        integerHashMap.put(key, integer);
                                    }
                                }
                                final BaseCourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stu_id);
                                if (courseGroupItem != null) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            TeamMemberEntity entity = courseGroupItem.getEntity();
                                            entity.energy = total_erengy;
                                            courseGroupItem.onScene();
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            logger.d("onMessage:Scene", e);
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
                            int studentNum = -1;
                            ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                            for (int i = entities.size() - 1; i >= 0; i--) {
                                TeamMemberEntity teamMemberEntity = entities.get(i);
                                if (who_id == teamMemberEntity.id) {
                                    studentNum = 3 - i;
                                    break;
                                }
                            }
                            if (studentNum != -1) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            CleanUpEntity cleanUpEntity = cleanUpEntities.get("" + who_id);
                                            if (cleanUpEntity != null) {
                                                //把用户的信息恢复
                                                List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = cleanUpEntity.answerList;
                                                for (int i = 0; i < answerList.size(); i++) {
                                                    GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity answer = answerList.get(i);
                                                    if (answer.getId() == word_id) {
                                                        answerList.remove(i);
                                                        answer.setGetFireCount(incr_energy);
                                                        List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> rightAnswerList = cleanUpEntity.rightAnswerList;
                                                        rightAnswerList.add(answer);
                                                        break;
                                                    }
                                                }
                                            }
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
                            }
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
                                String total_energy = stuObj.getString("total_energy");
                                JSONArray rob_wordsArray = stuObj.getJSONArray("rob_words");
                                for (int j = 0; j < rob_wordsArray.length(); j++) {
                                    JSONObject rob_wordObj = rob_wordsArray.getJSONObject(j);
                                    String word_id = rob_wordObj.getString("word_id");
                                    String word_text = rob_wordObj.getString("word_text");
                                    String scores = rob_wordObj.getString("scores");
                                }
                            }
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
}
