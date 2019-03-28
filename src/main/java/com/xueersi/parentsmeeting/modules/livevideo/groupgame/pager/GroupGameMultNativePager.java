package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
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
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupItem;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.GroupSurfaceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

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
    GroupSurfaceView groupSurfaceView;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;
    private LinearLayout ll_livevideo_course_item_content;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String url;
    /**
     * 新课件缓存
     */
    private NewCourseCache newCourseCache;
    /** 新课件是否是预加载 */
    private boolean ispreload;
    /** 文理英属性 */
    private int isArts = LiveVideoSAConfig.ART_EN;
    /** 是不是回放 */
    private boolean isPlayBack = false;
    private NewCourseSec newCourseSec;
    /**
     * 新课件是否是预加载
     */
    private boolean isPreload;
    private String learningStage;
    private String liveId;
    private VideoQuestionLiveEntity detailInfo;
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
    private HashMap<String, CourseGroupItem> courseGroupItemHashMap = new HashMap<>();
    private LiveGetInfo liveGetInfo;
    private int stuid;
    static final String TEST_URL = "file:///android_asset/hot_air_balloon/index.html";
    static final String TEST_CONTENT = "This is an apple|apple|banana|traffic";
    private GetStuActiveTeam getStuActiveTeam;
    private TcpMessageReg tcpMessageReg;
    private VoiceProjectile voiceProjectile;

    public GroupGameMultNativePager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity) {
        super(context);
        this.detailInfo = detailInfo;
        this.url = englishH5Entity.getUrl();
        this.liveGetInfo = liveGetInfo;
        stuid = Integer.parseInt(liveGetInfo.getStuId());
        this.learningStage = liveGetInfo.getStudentLiveInfo().getLearning_stage();
        this.liveId = liveGetInfo.getId();
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
        ll_livevideo_course_item_content = view.findViewById(R.id.ll_livevideo_course_item_content);
        return view;
    }

    @Override
    public void initData() {
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.request(null);
        }
//        startSpeechRecognize();
        getStuActiveTeam = ProxUtil.getProxUtil().get(mContext, GetStuActiveTeam.class);
        //有战队pk，才有这种题的多人
        if (getStuActiveTeam != null) {
            getStuActiveTeam.getStuActiveTeam(new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    interactiveTeam = (InteractiveTeam) objData[0];
                    ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
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
                voiceProjectile = new VoiceProjectile();
                boolean change = tcpMessageReg.setTest(LiveQueConfig.EN_COURSE_GAME_TYPE_1, detailInfo.id);
                mLogtf.d("initData(setTest):change=" + change);
                tcpMessageReg.registTcpMessageAction(voiceProjectile);
            }
        }
        newCourseCache = new NewCourseCache(mContext, liveId);
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
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {

            @Override
            public void postMessage(String where, final JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    logger.d("postMessage:type=" + type + ",message=" + message);
                    if (CourseMessage.REC_close.equals(type)) {
                    } else if (CourseMessage.REC_submitAnswer.equals(type)) {

                    } else if (CourseMessage.REC_answer.equals(type)) {
                        onAnswer(message);
                    } else if (CourseMessage.REC_loadComplete.equals(type)) {
                        onLoadComplete(where, message);
                    } else if (CourseMessage.REC_SubmitAnswer.equals(type)) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), "xesApp");
        wvSubjectWeb.loadUrl(TEST_URL);

        groupSurfaceView = new GroupSurfaceView(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 300;
        lp.leftMargin = 300;
        ((ViewGroup) mView).addView(groupSurfaceView, lp);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAttach()) {
                    return;
                }
                volume = random.nextInt(30);
                groupSurfaceView.onVolumeUpdate(volume);
//                CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
//                if (courseGroupItem != null) {
//                    courseGroupItem.onVolumeUpdate(volume);
//                }
//                animationView.setProgress((float) volume / 30.f);
                handler.postDelayed(this, 500);
            }
        }, 1000);
    }

    Random random = new Random();
    int volume = 0;

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
        mWorkerThread.joinChannel("", liveId + "_11111", stuid, new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                logger.d("onJoinChannel:joinChannel=" + joinChannel);
                startSpeechRecognize();
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
            CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
            logger.d("onFirstRemoteVideoDecoded:uid=" + uid + ",courseGroupItem=null?" + (courseGroupItem == null));
            if (courseGroupItem != null) {
                doRenderRemoteUi(uid, courseGroupItem);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater mInflater = LayoutInflater.from(mContext);
                        TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                        teamMemberEntity.id = uid;
                        teamMemberEntity.name = "" + uid;
                        CourseGroupItem courseGroupItem1 = new CourseGroupItem(mContext, mWorkerThread, uid, uid == stuid);
                        View convertView = mInflater.inflate(courseGroupItem1.getLayoutResId(), ll_livevideo_course_item_content, false);
                        courseGroupItem1.initViews(convertView);
                        courseGroupItem1.updateViews(teamMemberEntity, courseGroupItemHashMap.size(), teamMemberEntity);
                        courseGroupItem1.bindListener();
                        ll_livevideo_course_item_content.addView(convertView);
                        courseGroupItemHashMap.put(teamMemberEntity.id + "", courseGroupItem1);
                        doRenderRemoteUi(uid, courseGroupItem1);
                    }
                });
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
            if (stuid == uid) {
                CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
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
            final CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + uid);
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

    private void doRenderRemoteUi(final int uid, final CourseGroupItem courseGroupItem) {
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

    private void preview(final int uid, final CourseGroupItem courseGroupItem) {
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
            CourseGroupItem courseGroupItem = new CourseGroupItem(mContext, mWorkerThread, teamMemberEntity.id, teamMemberEntity.id == stuid);
            View convertView = mInflater.inflate(courseGroupItem.getLayoutResId(), ll_livevideo_course_item_content, false);
            courseGroupItem.initViews(convertView);
            courseGroupItem.updateViews(teamMemberEntity, i, teamMemberEntity);
            courseGroupItem.bindListener();
            ll_livevideo_course_item_content.addView(convertView);
            courseGroupItemHashMap.put(teamMemberEntity.id + "", courseGroupItem);
        }
    }

    private void onAnswer(JSONObject message) {
    }

    private void onLoadComplete(String where, JSONObject message) {
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
        mParam.setStrEvaluator(TEST_CONTENT);
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
                        onHitSentence(resultEntity.getScore());
                    }
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
//                logger.d("onVolumeUpdate:volume = " + volume);
//                float floatVolume = (float) volume * 3 / 90;
//                groupSurfaceView.onVolumeUpdate(volume);
                CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stuid);
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
                    RtcEngine rtcEngine = mWorkerThread.getRtcEngine();
                    if (rtcEngine != null) {
                        rtcEngine.pushExternalAudioFrame(dest, System.currentTimeMillis());
                        rtcEngine.adjustRecordingSignalVolume(400);
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
                    XESToastUtils.showToast(mContext, "评测完成");
                    startSpeechRecognize();
                }
            }, 1000);
        }
    }

    @Override
    public void initListener() {
        ivWebViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addJs = false;
                wvSubjectWeb.reload();
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
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        getCourseWareTests();
    }

    private void getCourseWareTests() {
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                newCourseSec = (NewCourseSec) objData[0];
                logger.d("onDataSucess:time=" + (newCourseSec.getEndTime() - newCourseSec.getReleaseTime()));
//                if (newCourseSec.getIsAnswer() == 1 && !isPlayBack) {
//                    rlSubjectLoading.setVisibility(View.GONE);
//                    preLoad.onStop();
//                    showScienceAnswerResult(0);
//                } else {
//                    tests = newCourseSec.getTests();
//                    if (tests.isEmpty()) {
//                        XESToastUtils.showToast(mContext, "互动题为空");
//                        return;
//                    }
//                    if (isArts == LiveVideoSAConfig.ART_EN) {
//                        NewCourseSec.Test test = tests.get(0);
//                        mLogtf.d("getCourseWareTests:oldtype=" + detailInfo.getArtType() + ",testType=" + test.getTestType());
//                        if (StringUtils.isEmpty(detailInfo.getArtType()) || "0".equals(detailInfo.getArtType())) {
//                            detailInfo.setArtType(test.getTestType());
//                        }
//                    }
//                    showControl();
//                    if (quesJson != null) {
//                        for (int i = 0; i < tests.size(); i++) {
//                            NewCourseSec.Test test = tests.get(i);
//                            JSONArray userAnswerContent = quesJson.optJSONArray("" + i);
//                            test.setUserAnswerContent(userAnswerContent);
//                        }
//                    }
//                    setNum(1);
//                    NewCourseSec.Test test = tests.get(0);
//                    currentIndex = 0;
//                    wvSubjectWeb.loadUrl(test.getPreviewPath());
//                    int type = newCourseCache.loadCourseWareUrl(test.getPreviewPath());
//                    if (type != 0) {
//                        ispreload = type == 1;
//                    } else {
//                        ispreload = true;
//                    }
//                    NewCourseLog.sno3(liveAndBackDebug, NewCourseLog.getNewCourseTestIdSec(detailInfo, isArts), getSubtestid(), test.getPreviewPath(), ispreload, test.getId());
//                    //设置作答时间
//                    if (isArts == LiveVideoSAConfig.ART_EN) {
//                        setTimeEn(newCourseSec);
//                    }
//                }
            }

            /**
             * 设置英语时间
             * @param newCourseSec
             */
            private void setTimeEn(NewCourseSec newCourseSec) {
                //英语倒计时
//                final long releaseTime = newCourseSec.getReleaseTime() * 60;
//                final long startTime = System.currentTimeMillis() / 1000;
//                tvCourseTimeText.setText(getTimeNegativeEn(releaseTime, startTime));
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        String timeStr = getTimeNegativeEn(releaseTime, startTime);
//                        if (loadResult || mView.getParent() == null || timeStr == null) {
//                            return;
//                        }
//                        tvCourseTimeText.setText(timeStr);
//                        handler.postDelayed(this, 1000);
//                    }
//                }, 1000);
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
        if (tcpMessageReg != null && voiceProjectile != null) {
            tcpMessageReg.unregistTcpMessageAction(voiceProjectile);
        }
    }

    /**
     * 语音评测 - 命中句子
     *
     * @param score
     */
    private void onHitSentence(int score) {
        mSingCount++;
        boolean isTurnPage = false;
        if (mSingCount >= 5) {
            mSingCount = 0;
            isTurnPage = true;
        }
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", "coursewareDoing");
            jsonData.put("score", score);
            jsonData.put("isTurnPage", isTurnPage);
            wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (tcpMessageReg != null && interactiveTeam != null) {
            PkTeamEntity teamEntity = getStuActiveTeam.getPkTeamEntity();
            if (teamEntity != null) {
                try {
                    JSONObject bodyJson = new JSONObject();
                    bodyJson.put("type", 1);
                    bodyJson.put("live_id", liveGetInfo.getId());
                    LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
                    bodyJson.put("class_id", studentLiveInfo.getClassId());
                    bodyJson.put("test_id", detailInfo.id);
                    bodyJson.put("uid", "" + stuid);
                    bodyJson.put("word_id", "1");
                    bodyJson.put("pk_team_id", teamEntity.getPkTeamId());
                    bodyJson.put("team_type", "interactive");
                    bodyJson.put("interactive_team_id", interactiveTeam.getInteractive_team_id());
                    JSONArray team_mate = new JSONArray();
                    ArrayList<TeamMemberEntity> entities = interactiveTeam.getEntities();
                    for (int i = 0; i < entities.size(); i++) {
                        TeamMemberEntity teamMemberEntity = entities.get(i);
                        team_mate.put(teamMemberEntity.id);
                    }
                    bodyJson.put("team_mate", team_mate);
                    JSONArray userData = new JSONArray();
                    for (int i = 0; i < 1; i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("word_id", "1");
                        jsonObject.put("score", "" + score);
                        jsonObject.put("incry_energy", "3");
                        userData.put(jsonObject);
                    }
                    bodyJson.put("userData", userData);
                    tcpMessageReg.send(TcpConstants.Voice_Projectile_TYPE, TcpConstants.Voice_Projectile_SEND, bodyJson.toString());
                } catch (JSONException e) {
                    logger.d("onHitSentence", e);
                }
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

    /** 客户端发语音炮弹数据 */
    class VoiceProjectile implements TcpMessageAction {

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
                            String who_id = dataObj.getString("who_id");
                            int score = dataObj.getInt("score");
                            if (who_id.equals("" + stuid)) {

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
                            String current_word = dataObj.getString("current_word");
                            JSONObject self = dataObj.getJSONObject("self");
                            {
                                String stu_id = self.getString("stu_id");
                                int total_erengy = self.getInt("total_erengy");
                                JSONObject word_scores = self.getJSONObject("word_scores");
                                Iterator<String> keys = word_scores.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    JSONArray scoresArray = word_scores.optJSONArray(key);
                                    if (scoresArray != null) {

                                    }
                                }
                                CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stu_id);
                                if (courseGroupItem != null) {
                                    courseGroupItem.onScene();
                                }
                            }
                            JSONArray mateArray = dataObj.getJSONArray("mate");
                            for (int i = 0; i < mateArray.length(); i++) {
                                JSONObject mateObj = mateArray.getJSONObject(i);
                                String stu_id = mateObj.getString("stu_id");
                                int total_erengy = mateObj.getInt("total_erengy");
                                JSONArray current_scoresArray = mateObj.optJSONArray("current_scores");
                                if (current_scoresArray != null) {

                                }
                                CourseGroupItem courseGroupItem = courseGroupItemHashMap.get("" + stu_id);
                                if (courseGroupItem != null) {
                                    courseGroupItem.onScene();
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
}
