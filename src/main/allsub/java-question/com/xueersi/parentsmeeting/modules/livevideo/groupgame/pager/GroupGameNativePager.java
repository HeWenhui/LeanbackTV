package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.action.SingleModeAction;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.cloud.GroupGameUpload;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.config.GroupGameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.GroupCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.GroupGameLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveAudioManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Date on 2019/3/15 18:31
 * @Author zhangyuansun
 * @Description
 */
public class GroupGameNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager,
        LiveVideoPoint.VideoSizeChange {

    /**
     * 加载中
     */
    private RelativeLayout rlSubjectLoading;
    /**
     * 小组互动 - 单人模式 右侧互动栏
     */
    private RelativeLayout rlGroupGameSingle;
    /**
     * 课件接口失败刷新
     */
    private ImageView ivCourseRefresh;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;
    /**
     * 火焰累积数
     */
    private TextView tvFireSum;
    /**
     * 火焰数+N父布局
     */
    private FrameLayout flFireAdd;
    /**
     * 火焰数+N
     */
    private TextView tvFireAdd;
    /**
     * oops
     */
    private TextView tvOops;
    /**
     * 没听清,请大声点哦
     */
    private TextView tvVoiceTip;
    /**
     * 测评音量波形
     */
    private WaveView mWaveView;

    /**
     * 新课件缓存
     */
    private NewCourseCache newCourseCache;
    /**
     * 新课件是否是预加载
     */
    private boolean ispreload;
    /**
     * 是不是回放
     */
    private boolean isPlayBack;
    /**
     * 拉取课件接口成功
     */
    private boolean fetchCoursewareSuccess = false;
    /**
     * 游戏结束
     */
    private boolean gameOver = false;

    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private GroupGameTestInfosEntity mGroupGameTestInfosEntity;
    private GroupGameTestInfosEntity.TestInfoEntity mTestInfoEntity;
    private List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> mAnswersList;

    private LiveGetInfo liveGetInfo;
    private VideoQuestionLiveEntity detailInfo;
    private EnglishH5Entity englishH5Entity;
    private String url;
    private String liveId;
    private String stuId;
    private String learningStage;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    /**
     * 语音评测
     */
    protected SpeechUtils mIse;
    /* 语音保存位置 */
    private File saveVideoFile;
    /**
     * 在网页中嵌入js，只嵌入一次
     */
    private boolean addJs = false;
    private boolean isSubmit = false;
    private PreLoad preLoad;
    private SingleModeAction singleModeAction;
    private StringBuilder content = new StringBuilder();
    private int pageNum = 0;
    private int fireNum = 0;
    private int goldNum = 0;
    private int starNum = 0;
    private int singleCount = 0;
    private int rightNum = 0;
    private long voiceTime = 0;
    private long existingVoiceTime = 0;
    private int successTimes = 0;
    private long presentTime = 0;
    private List<Long> presentTimeList = new ArrayList<>();
    private List<List<Integer>> scoreMatrix = new ArrayList<>();
    private JSONObject answerData = new JSONObject();
    private JSONArray userAnswer = new JSONArray();
    /**一次作答尝试次数**/
    private static int MAX_SINGLE_COUNT;
    private LiveAudioManager liveAudioManager;
    private GroupGameUpload groupGameUpload;
    /**
     * 音量管理
     */
    private AudioManager mAM;
    /**
     * 最大音量
     */
    private int mMaxVolume;
    /**
     * 当前音量
     */
    private int mVolume = 0;

    /**
     * 是否恢复了音量
     */
    private boolean isVolumeResume = true;
    private LiveAndBackDebug liveAndBackDebug;
    private View waveContainerView;
    private GroupGameLog mLog;
    private List<String> testsProtocalList;

    public GroupGameNativePager(Context context, boolean isPlayBack, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity
            detailInfo, EnglishH5Entity englishH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onClose) {
        super(context);
        this.isPlayBack = isPlayBack;
        this.liveGetInfo = liveGetInfo;
        this.detailInfo = detailInfo;
        this.englishH5Entity = englishH5Entity;
        this.url = englishH5Entity.getUrl();
        this.liveId = liveGetInfo.getId();
        this.stuId = liveGetInfo.getStuId();
        this.learningStage = liveGetInfo.getStudentLiveInfo().getLearning_stage();
        this.onClose = onClose;
        this.liveAndBackDebug = new ContextLiveAndBackDebug(context);
        preLoad = new MiddleSchoolPreLoad();
        if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(detailInfo.type) || LiveQueConfig
                .EN_COURSE_TYPE_VOICE_CANNON.equals(detailInfo.type) || LiveQueConfig.EN_COURSE_TYPE_WHAT_IS_MISSING
                .equals(detailInfo.type) || LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)
                || LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE
                .equals(detailInfo.type) || LiveQueConfig.EN_COURSE_TYPE_GET_IT
                .equals(detailInfo.type)) {
            singleModeAction = new HotAirBallonAction();
        } else {
            singleModeAction = new CleanUpAction();
        }
        initListener();
        setVoice();
        groupGameUpload = new GroupGameUpload(mContext, liveId, detailInfo.id);
        mLog = new GroupGameLog(detailInfo.type);
        mLog.sno2(liveAndBackDebug, detailInfo.id, 0);
        try {
            testsProtocalList = new ArrayList<>();
            if(isPlayBack){
                if(detailInfo.getAnswerDay()!=null){
                    JSONArray testsProtocalArray = new JSONArray(detailInfo.getAnswerDay());
                    for (int index = 0; index < testsProtocalArray.length(); index++) {
                        testsProtocalList.add(testsProtocalArray.optString(index));
                    }
                }
            } else {
                if(detailInfo.getTestsProtocal()!=null){
                    JSONArray testsProtocalArray = new JSONArray(detailInfo.getTestsProtocal());
                    for (int index = 0; index < testsProtocalArray.length(); index++) {
                        testsProtocalList.add(testsProtocalArray.optString(index));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        /* 小组互动 - 单人模式 右侧互动栏 */
        rlGroupGameSingle = view.findViewById(R.id.rl_livevideo_groupgame_single);
        tvFireSum = view.findViewById(R.id.tv_livevideo_groupgame_single_fire_sum);
        flFireAdd = view.findViewById(R.id.fl_livevideo_groupgame_single_fire_add);
        tvFireAdd = view.findViewById(R.id.tv_livevideo_groupgame_single_fire_add);
        tvOops = view.findViewById(R.id.tv_livevideo_groupgame_single_oops);
        tvVoiceTip = view.findViewById(R.id.tv_livevideo_groupgame_single_voice_tip);
        mWaveView = view.findViewById(R.id.wv_livevideo_groupgame_single_wave);
        waveContainerView = view.findViewById(R.id.iv_livevideo_groupgame_single_wave);
        return view;
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
                newCourseCache.reload();
                rlGroupGameSingle.setVisibility(View.GONE);
                mWaveView.stop();
                wvSubjectWeb.reload();
                singleModeAction.onDestroy();
            }
        });
        mWaveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    public void initData() {
        pageNum = 0;
        fireNum = 0;
        goldNum = 0;
        starNum = 0;
        singleCount = 0;
        rightNum = 0;
        voiceTime = 0;
        successTimes = 0;
        presentTime = 0;
        presentTimeList.clear();
        scoreMatrix.clear();
        for (int i = 0; i < mAnswersList.size(); i++) {
            scoreMatrix.add(i, new ArrayList<Integer>());
        }
        answerData = new JSONObject();
        userAnswer = new JSONArray();

        ivWebViewRefresh.setVisibility(View.VISIBLE);
        wvSubjectWeb.setVisibility(View.VISIBLE);
        rlGroupGameSingle.setVisibility(View.VISIBLE);
        tvFireSum.setText("0");

        //语音宝箱 课件加载成功后 延时一秒展示 收音球（封面动画展示1.2s）
        if(LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)){
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    waveContainerView.setVisibility(View.VISIBLE);
                }
            },1200);
        }else{
            //非语音宝箱直接显示 收音球布局
            waveContainerView.setVisibility(View.VISIBLE);
        }

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveView.initialize();
            }
        }, 50);

        singleModeAction.startTimer();
        videoSizeChange(LiveVideoPoint.getInstance());
    }

    private void setVoice() {
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.request(null);
        }
        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(0, 0);
            logger.d(TAG + ":setVolume:0");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("stop");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("creattime", "" + creattime);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d(TAG + ":setVolume:null");
        }

        liveAudioManager = new LiveAudioManager(mContext, "GroupGameNativePager");
        mMaxVolume = liveAudioManager.getmMaxVolume();
        mVolume = liveAudioManager.getmVolume();
        int v = (int) (0.3f * mMaxVolume);
        liveAudioManager.setVolume(v);
        isVolumeResume = false;
    }

    private void initWebView() {
        newCourseCache = new GroupCourseCache(mContext, liveId, detailInfo.id, liveGetInfo.isNewCourse());
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG));
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, "" + detailInfo.id, creattime, new StaticWeb
                .OnMessage() {
            @Override
            public void postMessage(String where, final JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    if (CourseMessage.REC_loadComplete.equals(type)) {
                        onLoadComplete(where, message);
                    } else if (CourseMessage.REC_CoursewareDoing.equals(type)) {
                        onCoursewareDoing(where, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), "xesApp");
        if (TextUtils.isEmpty(getProtocal())||TextUtils.equals("0", getProtocal())||TextUtils.equals("1", getProtocal())) {
            wvSubjectWeb.loadUrl(mGroupGameTestInfosEntity.getTestInfoList().get(0).getPreviewPath());
        }else {
            wvSubjectWeb.loadUrl(mGroupGameTestInfosEntity.getTestInfoList().get(0).getPreviewPath()+"?cw_platform=android");
        }
    }

    private void showResultPager() {
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.name = liveGetInfo.getStandLiveName();
        teamMemberEntity.headurl = liveGetInfo.getHeadImgPath();
        teamMemberEntity.gold = goldNum;
        teamMemberEntity.energy = fireNum;
        entities.add(teamMemberEntity);

        if (liveAudioManager != null && !isVolumeResume) {
            liveAudioManager.setVolume(mVolume);
            isVolumeResume = true;
        }

        //显示结果页面
        GroupGameMVPMultPager groupGameMVPMultPager = new GroupGameMVPMultPager(mContext, entities);
        groupGameMVPMultPager.setOnPagerClose(new OnPagerClose() {
            @Override
            public void onClose(LiveBasePager basePager) {
                onClose.onH5ResultClose(GroupGameNativePager.this, detailInfo);
            }
        });
        ((ViewGroup) mView).addView(groupGameMVPMultPager.getRootView());
    }

    @Override
    public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rlGroupGameSingle.getLayoutParams();
        int rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
        if (lp.rightMargin != rightMargin) {
            lp.rightMargin = rightMargin;
            rlGroupGameSingle.setLayoutParams(lp);
        }
    }

    class CourseWebViewClient extends MyWebViewClient implements OnHttpCode {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl() + "";
            if (TextUtils.isEmpty(getProtocal())||TextUtils.equals("0", getProtocal())||TextUtils.equals("1", getProtocal())) {
                if (url.contains(".html")) {
                    if (!addJs) {
                        addJs = true;
                        WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(view, url);
                        if (webResourceResponse != null) {
                            return webResourceResponse;
                        } else {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    wvSubjectWeb.stopLoading();
                                }
                            });
                            XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                        }
                    }
                } else if (url.contains(WebInstertJs.indexStr())) {
                    WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                    if (webResourceResponse != null) {
                        return webResourceResponse;
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wvSubjectWeb.stopLoading();
                            }
                        });
                        XESToastUtils.showToast(mContext, "通信文件加载失败，请刷新");
                    }
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

        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
        }
    }

    private void onLoadComplete(String where, JSONObject message) {
        logger.d("onLoadComplete");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                preLoad.onStop();
                singleModeAction.onLoadComplete();
                initData();
            }
        });
        mLog.sno3(liveAndBackDebug, detailInfo.id, 0);
        mLog.sno4(liveAndBackDebug, detailInfo.id, "0", 0);
    }

    private void onCoursewareDoing(String where, JSONObject message) {
        logger.d("onCoursewareDoing");
    }

    private void startSpeechRecognize() {
        boolean hasAudidoPermission = XesPermission.hasSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
        // 检查用户麦克风权限
        if (!hasAudidoPermission) {
            //如果没有麦克风权限，申请麦克风权限
            XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {
                /**
                 * 结束
                 */
                @Override
                public void onFinish() {
                    logger.i("onFinish()");
                }

                /**
                 * 用户拒绝某个权限
                 */
                @Override
                public void onDeny(String permission, int position) {
                    logger.i("onDeny()");
                }

                /**
                 * 用户允许某个权限
                 */
                @Override
                public void onGuarantee(String permission, int position) {
                    logger.i("onGuarantee()");
                    startSpeechRecognize();
                }
            }, PermissionConfig.PERMISSION_CODE_AUDIO);
            return;
        }

        File dir = LiveCacheFile.geCacheFile(mContext, "groupgame");
        //只有第一次删除
        if (saveVideoFile == null) {
            FileUtils.deleteDir(dir);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        SpeechParamEntity mParam = new SpeechParamEntity();
        if (mIse == null) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.prepar();
        }
        mParam.setRecogType(SpeechConfig.SPEECH_GROUP_GAME_EVALUATOR_OFFLINE);
        mParam.setLang(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);
        mParam.setStrEvaluator(content.toString());
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setLearning_stage(learningStage);
        mParam.setVad_max_sec("90");
        mParam.setVad_pause_sec("90");
        mIse.startRecog(mParam, new EvaluatorListener() {

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech()");
            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    logger.d("onEvaluatorSuccess(): score = " + resultEntity.getScore());
                    onRecognizeStop(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    logger.d("onEvaluatorError: errorNo = " + resultEntity.getErrorNo() + ", isOfflineFail =" + mIse
                            .isOfflineFail());
                    onRecognizeStop(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    if (gameOver) {
                        return;
                    }
                    if (resultEntity.getNewSenIdx() >= 0) {
                        singleModeAction.onHitSentence(resultEntity);
                        mainHandler.removeCallbacks(onCoursewareComeOnRunable);
                        isComeOnRunablePosted = false;
                    }
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                if (volume > 10) {
                    if (!isComeOnRunablePosted) {
                        mainHandler.postDelayed(onCoursewareComeOnRunable, 3000);
                        isComeOnRunablePosted = true;
                    }
                }
                float fVolume = (float) volume / 10.0f;
                logger.i("onVolumeUpdate = " + volume + ":" + fVolume);
                mWaveView.setWaveAmplitude(fVolume);
            }
        });
    }

    private void onRecognizeStop(ResultEntity resultEntity) {
        existingVoiceTime = voiceTime;
        if (isAttach() && !gameOver) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechRecognize();
                }
            }, 300);
        }
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
            tvVoiceTip.setVisibility(View.VISIBLE);
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvVoiceTip.setVisibility(View.GONE);
                }
            }, 2000);
        }
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
        return this.url;
    }

    @Override
    public void onBack() {

    }

    @Override
    public void destroy() {
        super.onDestroy();
        if (mIse != null) {
            mIse.cancel();
        }
        wvSubjectWeb.destroy();
        mWaveView.destroy();
        singleModeAction.onDestroy();

        if (liveAudioManager != null && !isVolumeResume) {
            liveAudioManager.setVolume(mVolume);
            isVolumeResume = true;
        }

        AudioRequest audioRequest = ProxUtil.getProxUtil().get(mContext, AudioRequest.class);
        if (audioRequest != null) {
            audioRequest.release();
        }

        BasePlayerFragment videoFragment = ProxUtil.getProxUtil().get(mContext, BasePlayerFragment.class);
        if (videoFragment != null) {
            videoFragment.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
            logger.d("onDestroy:setVolume:1");
            StableLogHashMap stableLogHashMap = new StableLogHashMap("start");
            stableLogHashMap.put("tag", TAG);
            stableLogHashMap.put("creattime", "" + creattime);
            umsAgentDebugSys(LogConfig.LIVE_STOP_VOLUME, stableLogHashMap);
        } else {
            logger.d("onDestroy:setVolume:null");
        }
    }

    @Override
    public void submitData() {
        submitData(true);
    }

    private void submitData(final boolean isForce) {
        if (!fetchCoursewareSuccess || isSubmit) {
            return;
        }
        singleModeAction.saveUserAnser();
        int averageScore = 0;
        int tryTimes = 0;
        int sum = 0;
        for (List<Integer> list : scoreMatrix) {
            tryTimes += list.size();
            for (Integer score : list) {
                sum += score;
            }
        }
        if (tryTimes != 0) {
            averageScore = sum / tryTimes;
        }

        starNum = calculateStarByScore(averageScore);
        //直播有分组才计算能量
        if (!isPlayBack && liveGetInfo.getEnglishPk().hasGroup == 1) {
            if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(detailInfo.type)) {
                fireNum = rightNum < 50 ? rightNum : 50;
            } else if (LiveQueConfig.EN_COURSE_TYPE_WHAT_IS_MISSING.equals(detailInfo.type)) {
                fireNum = rightNum < 30 ? rightNum : 30;
            }else if (LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE.equals(detailInfo.type)) {
                fireNum = rightNum < 30 ? rightNum : 30;
            }else if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(detailInfo.type)) {
                fireNum = (rightNum + 5) < 30 ? ((rightNum + 5)) : 30;
            }else if(LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)){
                //语音开宝箱 火焰计算规则
                fireNum = (int) Math.ceil(10d * successTimes / (double) (mAnswersList.size()));
            } else {
                fireNum = (int) Math.ceil(10d * successTimes / (double) (mAnswersList.size()));
            }
        }
        logger.d("submitData: answerData = " + answerData.toString() + ", submitData: fireNum = " + fireNum + ", " +
                "goldNum = " + goldNum + ", starNum = " + starNum);
        int isRightCount = 0;
        for (int i = 0; i < userAnswer.length(); i++) {
            try {
                JSONObject jsonObject = userAnswer.getJSONObject(i);
                int isRight = jsonObject.optInt("isRight");
                if (isRight == 1) {
                    isRightCount++;
                }
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        try {
            answerData.put("tryTimes", tryTimes);
            answerData.put("rightNum", "" + isRightCount);
            answerData.put("total", mAnswersList.size());
            answerData.put("averageScore", averageScore);
            answerData.put("userAnswer", userAnswer);
        } catch (JSONException e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        isSubmit = true;
        englishH5CoursewareSecHttp.submitGroupGame(detailInfo, 1, (int) voiceTime, 0, 0, starNum, fireNum, goldNum,
                0, (int) voiceTime, 0, 0, answerData.toString(), new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        logger.d("submitGroupGame -> onDataSucess");
                        if (!isForce) {
                            showResultPager();
                        }
                        mLog.sno6(liveAndBackDebug, detailInfo.id, "1", 0);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        super.onDataFail(errStatus, failMsg);
                        logger.d("submitGroupGame -> onDataFail:" + failMsg);
                        isSubmit = false;
                        if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                            XESToastUtils.showToastAtCenter(failMsg);
                        }
                        onClose.onH5ResultClose(GroupGameNativePager.this, detailInfo);
                    }
                });
        mLog.sno5(liveAndBackDebug, detailInfo.id, isForce ? "endPublish" : "autoSubmit", voiceTime == 0 ?
                "0" : "1", 0);
    }

    private int calculateStarByScore(int averageScore) {
        int num;
        if (averageScore < 40) {
            num = 1;
        } else if (averageScore < 60) {
            num = 2;
        } else if (averageScore < 75) {
            num = 3;
        } else if (averageScore < 90) {
            num = 4;
        } else {
            num = 5;
        }
        return num;
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        preLoad.onProgressChanged(view, newProgress);
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
                logger.d("getCourseWareTests -> onDataSucess()");
                mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                if (mGroupGameTestInfosEntity.getTestInfoList() == null || mGroupGameTestInfosEntity.getTestInfoList
                        ().size() == 0) {
                    XESToastUtils.showToast(mContext, "互动题为空");
                    return;
                }
                mTestInfoEntity = mGroupGameTestInfosEntity.getTestInfoList().get(0);
                if (mTestInfoEntity.getAnswerList() == null || mTestInfoEntity.getAnswerList().size() == 0) {
                    XESToastUtils.showToast(mContext, "互动题为空");
                    return;
                }
                mAnswersList = mTestInfoEntity.getAnswerList();
                //构建要识别的句子
                for (int i = 0; i < mAnswersList.size(); i++) {
                    content.append(mAnswersList.get(i).getText());
                    if (i != mAnswersList.size() - 1) {
                        content.append("|");
                    }
                }
                fetchCoursewareSuccess = true;
                initWebView();
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("getCourseWareTests -> onDataFail() : errStatus = " + errStatus + ", failMsg = " + failMsg);
                super.onDataFail(errStatus, failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg);
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
            }
        };
        if (mGroupGameTestInfosEntity == null) {
            englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, callBack);
        } else {
            callBack.onDataSucess(mGroupGameTestInfosEntity);
        }
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
        return englishH5Entity;
    }

    @Override
    public boolean isResultRecived() {
        return false;
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
    private class MiddleSchoolPreLoad implements PreLoad {
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

    /**
     * 热气球,炮弹,语音宝箱交互
     */
    class HotAirBallonAction implements SingleModeAction {
        // 课件开场 动画时间
        private double coursewareStartAnimationTime = 0;
        // 课件结束 动画时间
        private double coursewareEndAnimationTime = 0;

        HotAirBallonAction() {
        }

        private void initGameTime(){
            //what's missing 发送该消息后若为第一题，需要等待(总题数+1)秒再开始倒计时收音  若不为第一题，需要等待1.5秒再开始倒计时和收音
            if (LiveQueConfig.EN_COURSE_TYPE_WHAT_IS_MISSING.equals(detailInfo.type)) {
                coursewareStartAnimationTime = mAnswersList.size() + 1;
                coursewareEndAnimationTime = GroupGameConfig.WHATIS_MISSING_COURSEWARE_END_ANMITION_TIME;
            } else if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(detailInfo.type)) {
                coursewareStartAnimationTime = 0;
                coursewareEndAnimationTime = GroupGameConfig.VOICE_CANNON_END_ANMITION_TIME;
            } else if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(detailInfo.type)) {
                coursewareStartAnimationTime = 0;
                coursewareEndAnimationTime = GroupGameConfig.HOT_AIR_BALLAN_END_ANMITION_TIME;
            } else if(LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)){
                coursewareStartAnimationTime = 0;
                coursewareEndAnimationTime = GroupGameConfig.VOICE_TREASURE_BOX_END_ANIM_TIME;
            } else if (LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE.equals(detailInfo.type)) {
                coursewareStartAnimationTime = 0;
                coursewareEndAnimationTime = GroupGameConfig.SOLITAIRE_END_ANMITION_TIME;
            } else if (LiveQueConfig.EN_COURSE_TYPE_GET_IT.equals(detailInfo.type)) {
                coursewareStartAnimationTime = 0;
                coursewareEndAnimationTime = GroupGameConfig.GET_IT_END_ANMITION_TIME;
            }
        }

        @Override
        public void startTimer() {
            mainHandler.removeCallbacks(onCoursewareComeOnRunable);
            isComeOnRunablePosted = false;
            mainHandler.removeCallbacks(turnPageRunnable);
            presentTime = System.currentTimeMillis();
            singleCount = 0;
            if (pageNum >= mAnswersList.size()) {
                gameOver = true;
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submitData(false);
                    }
                }, (int) (coursewareEndAnimationTime) * 1000);

            } else {
                double startSpeechRecognizeTime;
                double turnPagetime;
                content = new StringBuilder(mGroupGameTestInfosEntity.getTestInfoList().get(0).getAnswerList().get
                        (pageNum).getText());
                if (pageNum == 0) {
                    startSpeechRecognizeTime = coursewareStartAnimationTime;
                    turnPagetime = mAnswersList.get(pageNum).getSingleTime() + coursewareStartAnimationTime;
                } else {
                    startSpeechRecognizeTime = coursewareEndAnimationTime;
                    turnPagetime = mAnswersList.get(pageNum).getSingleTime() + coursewareEndAnimationTime;
                }
                mainHandler.postDelayed(startSpeechRecognizeRunnable, (int)(startSpeechRecognizeTime * 1000));
                mainHandler.postDelayed(turnPageRunnable, (int)(turnPagetime * 1000));
            }
        }

        @Override
        public void onLoadComplete() {
                initGameTime();
            try {
                JSONObject resultData = new JSONObject();
                resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
                resultData.put("pageNum", 0);
                resultData.put("restTime", mAnswersList.get(0).getSingleTime());
                if(LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE
                        .equals(detailInfo.type)){
                    resultData.put("nextStudentNum", 2);
                }
                resultData.put("currentRight", 0);
                resultData.put("isSingle", true);

                //组内学生信息，单人只传自己的
                JSONArray studentInfo = new JSONArray();
                JSONObject student = new JSONObject();
                student.put("studentNum", 2);
                student.put("name", liveGetInfo.getStuName());
                student.put("avatar", liveGetInfo.getHeadImgPath());
                studentInfo.put(student);
                resultData.put("studentInfo", studentInfo);

                sendToCourseware(wvSubjectWeb, resultData, "*");
            } catch (Exception e) {
                e.printStackTrace();
            }
            MAX_SINGLE_COUNT = mTestInfoEntity.getSingleCount();
            if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(detailInfo.type)
                    || LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)) {
                tvFireSum.setVisibility(View.GONE);
            } else {
                //语音炮弹、what's missing单人模式，完成次数减为1/3
                MAX_SINGLE_COUNT = (int) Math.ceil((double) MAX_SINGLE_COUNT / 3d);
            }
        }

        @Override
        public void onHitSentence(ResultEntity resultEntity) {

            if(isVoiceTreasuerBox() && !isTargetWord(resultEntity)){
                return;
            }
            int newSenIndex = resultEntity.getNewSenIdx();
            int score = resultEntity.getScore();
            double speechDuration = resultEntity.getSpeechDuration();
            if (newSenIndex < 0) {
                return;
            }
            logger.d("onHitSentence: newSenIndex = " + newSenIndex + ", score = " + score + ", speechDuration = " +
                    speechDuration);
            scoreMatrix.get(pageNum).add(score);
            voiceTime = existingVoiceTime + (long) (speechDuration * 1000);
            if (score >= 70) {
                singleCount++;
                rightNum++;
                if (!isPlayBack && liveGetInfo.getEnglishPk().hasGroup == 1) {
                    onFireAdd(rightNum);
                }

                if (singleCount >= MAX_SINGLE_COUNT) {
                    if (isPlayBack) {
                        goldNum = 1;
                    } else {
                        goldNum = 2;
                    }
                    uploadScore(score, true);
                    //翻页到下一页
                    startTimer();
                } else {
                    uploadScore(score, false);
                }
            } else {
                onOops();
                uploadScore(score, false);
            }
        }

        @Override
        public void onDestroy() {
            mainHandler.removeCallbacks(turnPageRunnable);
            mainHandler.removeCallbacks(startSpeechRecognizeRunnable);
        }

        @Override
        public void saveUserAnser() {
            for (int i = 0; i < scoreMatrix.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                int isRight = 0;
                int singleCount = 0;
                int presentTime = 0;
                List<Integer> scoreList = scoreMatrix.get(i);
                try {
                    jsonObject.put("text", mAnswersList.get(i).getText());
                    JSONArray scoreArray = new JSONArray();
                    for (int j = 0; j < scoreList.size(); j++) {
                        scoreArray.put(scoreList.get(j));
                        if (scoreList.get(j) >= 70) {
                            singleCount++;
                        }
                    }
                    if (singleCount >= MAX_SINGLE_COUNT) {
                        isRight = 1;
                        successTimes++;
                    }
                    if (i < presentTimeList.size()) {
                        presentTime = (int) ((long) presentTimeList.get(i));
                    }
                    jsonObject.put("scores", scoreArray.toString().substring(1, scoreArray.toString().length() - 1));
                    jsonObject.put("voiceTime", presentTime);
                    jsonObject.put("isRight", isRight);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userAnswer.put(jsonObject);
            }
        }

        private Runnable turnPageRunnable = new Runnable() {
            @Override
            public void run() {
                uploadScore(-1, true);
                startTimer();
            }
        };

        private Runnable startSpeechRecognizeRunnable = new Runnable() {
            @Override
            public void run() {
                startSpeechRecognize();
            }
        };

        /**
         * 发送识别结果课课件
         * @param score      识别分数
         * @param isTurnPage  是否需要翻页
         */
        private void uploadScore(int score, boolean isTurnPage) {
            JSONObject jsonData = new JSONObject();
            try {
                int turnToPageNum = -1;
                if (isTurnPage) {
                    if (mIse != null) {
                        mIse.cancel();
                    }
                    uploadAliCloud();
                    pageNum++;
                    presentTime = System.currentTimeMillis() - presentTime;
                    presentTimeList.add(presentTime);
                    turnToPageNum = pageNum;
                    mLog.sno4(liveAndBackDebug, detailInfo.id, pageNum + "", 0);
                }
                jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                //答对题目学生序号（1/2/3）  单人模式只有2号学生
                if(LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE
                        .equals(detailInfo.type)){
                    if(!isTurnPage)
                        return;
                    jsonData.put("nextStudentNum", 2);
                } else if(LiveQueConfig.EN_COURSE_TYPE_GET_IT
                        .equals(detailInfo.type)){
                    jsonData.put("studentNum", 3);
                } else {
                    jsonData.put("studentNum", 2);
                    jsonData.put("isTurnPage", isTurnPage);
                }
                jsonData.put("score", score);
                jsonData.put("turnToPageNum", turnToPageNum);
                logger.d("uploadScore : jsonData = " + jsonData.toString());
                postMessage(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 评测成功 - 火焰+N
         *
         * @param fireNum
         */
        private void onFireAdd(int fireNum) {
            if (LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON.equals(detailInfo.type)) {
                return;
            }
            // 语音宝箱 答题过程中不显示 火焰
            if(LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type)){
                return;
            }

            if (LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON.equals(detailInfo.type)&&fireNum > 50) {
                return;
            }
            if (LiveQueConfig.EN_COURSE_TYPE_WHAT_IS_MISSING.equals(detailInfo.type)&&fireNum > 30) {
                return;
            }
            if (LiveQueConfig.EN_COURSE_TYPE_SOLITAIRE.equals(detailInfo.type)&&fireNum > 30) {
                return;
            }
            if (LiveQueConfig.EN_COURSE_TYPE_GET_IT.equals(detailInfo.type)&&fireNum > 30) {
                return;
            }
            tvFireSum.setText("" + fireNum);
            flFireAdd.setVisibility(View.VISIBLE);
            tvFireAdd.setText("+1");
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flFireAdd.setVisibility(View.GONE);
                }
            }, 2000);
        }

        /**
         * 评测失败 - oops
         */
        private void onOops() {
            if (detailInfo.type.equals(LiveQueConfig.EN_COURSE_TYPE_HOT_AIR_BALLON)) {
                return;
            }

            if(detailInfo.type.equals(LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX)){
                return;
            }

            tvOops.setVisibility(View.VISIBLE);
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvOops.setVisibility(View.GONE);
                }
            }, 2000);
        }

        private void uploadAliCloud() {
            //音频上传阿里云
            if (saveVideoFile != null && pageNum >= 0 && pageNum < scoreMatrix.size()) {
                JSONArray scoreArray = new JSONArray();
                for (int j = 0; j < scoreMatrix.get(pageNum).size(); j++) {
                    scoreArray.put(scoreMatrix.get(pageNum).get(j));
                }
                groupGameUpload.uploadWonderMoment(saveVideoFile, content.toString(), scoreArray.toString(), 0);
            }
        }
    }

    /**
     * 是否是语音宝箱
     * @return
     */
    private boolean isVoiceTreasuerBox() {
        boolean result = detailInfo!= null && LiveQueConfig.EN_COURSE_TYPE_VOICE_TREASURE_BOX.equals(detailInfo.type);
        return result;
    }

    private boolean isTargetWord(ResultEntity resultEntity) {
        boolean result = false;
        try {
            List<PhoneScore> list =  resultEntity.getLstPhonemeScore();
            if(list != null && list.size() > 0){
                PhoneScore phoneScore = list.get(0);
                result = phoneScore != null && phoneScore.getWord().equalsIgnoreCase(content.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 抢水果交互
     */
    class CleanUpAction implements SingleModeAction {

        HashMap<String, Queue<Integer>> unfinishedfruitIds = new HashMap<>();

        @Override
        public void startTimer() {
            presentTime = System.currentTimeMillis();
            int time = mTestInfoEntity.getAnswerLimitTime() + 1;
            startSpeechRecognize();
            mainHandler.removeCallbacks(stopTimerRunnable);
            mainHandler.postDelayed(stopTimerRunnable, time * 1000);
        }

        @Override
        public void onLoadComplete() {
            JSONObject resultData = new JSONObject();
            try {
                resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
                resultData.put("pageNum", 0);
                resultData.put("restTime", mTestInfoEntity.getAnswerLimitTime());
                JSONArray studentInfo = new JSONArray();
                JSONObject student = new JSONObject();
                student.put("studentNum", 1);
                student.put("name", "" + liveGetInfo.getStuName());
                student.put("avatar", "" + liveGetInfo.getHeadImgPath());

                JSONObject rankInfo = new JSONObject();
                rankInfo.put("grading", 0);
                rankInfo.put("star", 0);
                student.put("rankInfo", rankInfo);

                JSONArray rightItem = new JSONArray();
                student.put("rightItem", rightItem);

                studentInfo.put(student);
                resultData.put("studentInfo", studentInfo);
                sendToCourseware(wvSubjectWeb, resultData, "*");
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                logger.d("onLoadComplete", e);
            }
            for (int i = 0; i < mAnswersList.size(); i++) {
                String text = mAnswersList.get(i).getText();
                Queue<Integer> idHashset;
                if (!unfinishedfruitIds.containsKey(text)) {
                    idHashset = new LinkedList<>();
                } else {
                    idHashset = unfinishedfruitIds.get(text);
                }
                idHashset.offer(i);
                unfinishedfruitIds.put(text, idHashset);
            }
        }

        @Override
        public void onHitSentence(ResultEntity resultEntity) {
            int newSenIndex = resultEntity.getNewSenIdx();
            int score = resultEntity.getScore();
            double speechDuration = resultEntity.getSpeechDuration();
            if (newSenIndex < 0) {
                return;
            }
            logger.d("onHitSentence: newSenIndex = " + newSenIndex + ", score = " + score + ", speechDuration = " +
                    speechDuration);
            String text = mAnswersList.get(newSenIndex).getText();
            Queue<Integer> queue = unfinishedfruitIds.get(text);
            if (!queue.isEmpty()) {
                int id = queue.peek();
                scoreMatrix.get(id).add(score);
                if (score >= 70) {
                    queue.poll();
                    rightNum++;
                    voiceTime = existingVoiceTime + (long) (speechDuration * 1000);
                    if (isPlayBack) {
                        goldNum = 1;
                    } else {
                        if (liveGetInfo.getEnglishPk().hasGroup == 1) {
                            onFireAdd(rightNum);
                        }
                        goldNum = 2;
                    }
                    uploadScore(id);
                    //提前答对所有题目，自动提交
                    if (rightNum >= mAnswersList.size()) {
                        gameOver = true;
                        if (mIse != null) {
                            mIse.cancel();
                        }
                        mainHandler.removeCallbacks(stopTimerRunnable);
                        mainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submitData(false);
                                uploadAliCloud();
                            }
                        }, 1000);
                    }
                } else {
                    onOops();
                }
            }
        }

        @Override
        public void onDestroy() {
            mainHandler.removeCallbacks(stopTimerRunnable);
        }

        @Override
        public void saveUserAnser() {
            presentTime = System.currentTimeMillis() - presentTime;
            for (int i = 0; i < scoreMatrix.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                int isRight = 0;
                List<Integer> scoreList = scoreMatrix.get(i);
                try {
                    jsonObject.put("text", mAnswersList.get(i).getText());
                    JSONArray scoreArray = new JSONArray();
                    for (int j = 0; j < scoreList.size(); j++) {
                        scoreArray.put(scoreList.get(j));
                        if (scoreList.get(j) >= 70) {
                            isRight = 1;
                            successTimes++;
                        }
                    }
                    jsonObject.put("scores", scoreArray.toString().substring(1, scoreArray.toString().length() - 1));
                    jsonObject.put("voiceTime", (int) presentTime);
                    jsonObject.put("isRight", isRight);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userAnswer.put(jsonObject);
            }
        }

        private Runnable stopTimerRunnable = new Runnable() {
            @Override
            public void run() {
                gameOver = true;
                if (mIse != null) {
                    mIse.cancel();
                }
                submitData(false);
                uploadAliCloud();
            }
        };

        private void uploadScore(int newSenIndex) {
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
                jsonData.put("studentNum", 1);

                JSONObject rightItem = new JSONObject();
                rightItem.put("rightId", newSenIndex);
                if (!isPlayBack && liveGetInfo.getEnglishPk().hasGroup == 1) {
                    rightItem.put("getFireCount", 1);
                } else {
                    rightItem.put("getFireCount", 0);
                }
                jsonData.put("rightItem", rightItem);
                jsonData.put("combo", 0);
                postMessage(jsonData);
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                logger.d("uploadScore", e);
            }
        }

        /**
         * 评测成功 - 火焰+N
         *
         * @param fireNum
         */
        private void onFireAdd(int fireNum) {
            if (fireNum > 30) {
                return;
            }
            tvFireSum.setText("" + fireNum);
            flFireAdd.setVisibility(View.VISIBLE);
            tvFireAdd.setText("+1");
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flFireAdd.setVisibility(View.GONE);
                }
            }, 2000);
        }

        /**
         * 评测失败 - oops
         */
        private void onOops() {
            tvOops.setVisibility(View.VISIBLE);
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvOops.setVisibility(View.GONE);
                }
            }, 2000);
        }

        private void uploadAliCloud() {
            if (saveVideoFile != null) {
                groupGameUpload.uploadWonderMoment(saveVideoFile, content.toString(), userAnswer.toString(), 0);
            }
        }
    }

    private void sendToCourseware(final WebView wvSubjectWeb, final JSONObject type, String data) {
        try {
            JSONObject liveinfo = new JSONObject();
            liveinfo.put("liveid", liveId);
            liveinfo.put("userid", stuId);
            liveinfo.put("testid", "" + detailInfo.id);
            liveinfo.put("creattime", "" + creattime);
            liveinfo.put("time", "" + System.currentTimeMillis());
            type.put("liveinfo", liveinfo);
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        StaticWeb.sendToCourseware(wvSubjectWeb, type, data,getProtocal());
    }

    private void postMessage(JSONObject jsonData) {
        try {
            JSONObject liveinfo = new JSONObject();
            liveinfo.put("liveid", liveId);
            liveinfo.put("userid", stuId);
            liveinfo.put("testid", "" + detailInfo.id);
            liveinfo.put("creattime", "" + creattime);
            liveinfo.put("time", "" + System.currentTimeMillis());
            jsonData.put("liveinfo", liveinfo);
        } catch (Exception e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        StaticWeb.sendToCourseware(wvSubjectWeb, jsonData, "*",getProtocal());
//        wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
    }

    /*  课件comeOn接口  */
    private void onCoursewareComeOn() {
        logger.d("onCoursewareComeOn()");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", "coursewareComeOn");
            jsonData.put("comeOn", true);
            if (LiveQueConfig.EN_COURSE_TYPE_GET_IT.equals(detailInfo.type)) {
                jsonData.put("studentNum", 3);
            }
            JSONObject liveinfo = new JSONObject();
            liveinfo.put("liveid", liveId);
            liveinfo.put("userid", stuId);
            liveinfo.put("testid", "" + detailInfo.id);
            liveinfo.put("creattime", "" + creattime);
            liveinfo.put("time", "" + System.currentTimeMillis());
            jsonData.put("liveinfo", liveinfo);
        } catch (JSONException e) {
            e.printStackTrace();
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        postMessage(jsonData);
    }

    private boolean isComeOnRunablePosted = false;
    private Runnable onCoursewareComeOnRunable = new Runnable() {
        @Override
        public void run() {
            onCoursewareComeOn();
            isComeOnRunablePosted = false;
        }
    };

    private String getProtocal(){
        if (testsProtocalList.size()>0){
            return testsProtocalList.get(0);
        }
        return "1";
    }
}