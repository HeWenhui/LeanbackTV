package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
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
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
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
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date on 2019/3/15 18:31
 * @Author zhangyuansun
 * @Description
 */
public class GroupGameNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {

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
    private boolean isPlayBack = false;

    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private GroupGameTestInfosEntity mGroupGameTestInfosEntity;
    private GroupGameTestInfosEntity.TestInfoEntity mTestInfoEntity;
    private List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> mAnswersList;

    private LiveGetInfo liveGetInfo;
    private VideoQuestionLiveEntity detailInfo;
    private EnglishH5Entity englishH5Entity;
    private String url;
    private String learningStage;
    private String liveId;
    private int stuId;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
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

    private PreLoad preLoad;
    private String content = "";
    private int pageNum = 0;
    private int fireNum = 0;
    private int goldNum = 0;
    private int starNum = 0;
    private int singleCount = 0;
    private int rightNum = 0;
    private long singleVoiceTime = 0;
    private long voiceTime = 0;
    private long existingVoiceTime;
    private int successTimes = 0;
    private List<Integer> singleScoreList = new ArrayList<>();
    private List<Integer> allScoreList = new ArrayList<>();
    private JSONObject answerData = new JSONObject();
    private JSONArray userAnswer = new JSONArray();

    private static int MAX_SINGLE_COUNT = 5;

    public GroupGameNativePager(Context context, boolean isPlayBack, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onClose) {
        super(context);
        this.isPlayBack = isPlayBack;
        this.liveGetInfo = liveGetInfo;
        this.detailInfo = detailInfo;
        this.englishH5Entity = englishH5Entity;
        this.url = englishH5Entity.getUrl();
        if (liveGetInfo.getStuId() != null) {
            this.stuId = Integer.parseInt(liveGetInfo.getStuId());
        }
        this.learningStage = liveGetInfo.getStudentLiveInfo().getLearning_stage();
        this.liveId = liveGetInfo.getId();
        this.onClose = onClose;
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
        mWaveView = view.findViewById(R.id.wv_livevideo_groupgame_single_wave);
        preLoad = new MiddleSchool();
        return view;
    }

    @Override
    public void initData() {
        content = "";
        pageNum = 0;
        fireNum = 0;
        goldNum = 0;
        starNum = 0;
        singleCount = 0;
        rightNum = 0;
        singleVoiceTime = 0;
        voiceTime = 0;
        successTimes = 0;
        singleScoreList.clear();
        allScoreList.clear();
        answerData = new JSONObject();
        userAnswer = new JSONArray();

        wvSubjectWeb.setVisibility(View.VISIBLE);
        rlGroupGameSingle.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveView.initialize();
            }
        }, 300);

        for (int i = 0; i < mAnswersList.size(); i++) {
            content += mAnswersList.get(i).getText();
            if (i != mAnswersList.size() - 1) {
                content += "|";
            }
        }
        restartTimer();
        boolean hasAudidoPermission = XesPermission.hasSelfPermission(mContext, Manifest.permission.RECORD_AUDIO); //
        // 检查用户麦克风权限
        if (hasAudidoPermission) {
            startSpeechRecognize();
        } else {
            //如果没有麦克风权限，申请麦克风权限
            XesPermission.checkPermissionNoAlert(mContext, new LiveActivityPermissionCallback() {
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
                mWaveView.stop();
                wvSubjectWeb.reload();
            }
        });
    }

    private void initWebView() {
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
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {
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
        wvSubjectWeb.loadUrl(mGroupGameTestInfosEntity.getTestInfoList().get(0).getPreviewPath());
    }

    private void restartTimer() {
        handler.removeCallbacks(turnPageRunnable);
        singleScoreList.clear();
        singleVoiceTime = System.currentTimeMillis();
        singleCount = 0;
        if (pageNum >= mAnswersList.size()) {
            submitData();
            showResultPager();
        } else {
            handler.postDelayed(turnPageRunnable, (mAnswersList.get(pageNum).getSingleTime() + 1) * 1000);

        }
    }

    private Runnable turnPageRunnable = new Runnable() {
        @Override
        public void run() {
            saveUserAnswer(0);
            turnPage();
        }
    };

    private void showResultPager() {
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.name = liveGetInfo.getStuName();
        teamMemberEntity.headurl = liveGetInfo.getHeadImgPath();
        teamMemberEntity.gold = goldNum;
        teamMemberEntity.energy = fireNum;
        entities.add(teamMemberEntity);
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
    class CourseWebViewClient extends MyWebViewClient implements OnHttpCode {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl() + "";
            if (url.contains(".html")) {
                if (!addJs) {
                    addJs = true;
                    WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(view, url);
//                    logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse == null));
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
//                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
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

        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
        }
    }

    private void onLoadComplete(String where, JSONObject message) {
        logger.d("onLoadComplete");
        handler.post(new Runnable() {
            @Override
            public void run() {
                preLoad.onStop();
                if (detailInfo.type.equals(LiveQueConfig.EN_COURSE_TYPE_VOICE_CANNON)) {
                    try {
                        JSONObject resultData = new JSONObject();
                        resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
                        resultData.put("pageNum", 0);
                        resultData.put("restTime", mAnswersList.get(0).getSingleTime());
                        resultData.put("currentRight", 0);
                        resultData.put("isSingle", true);
                        StaticWeb.sendToCourseware(wvSubjectWeb, resultData, "*");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                initData();
                initListener();
            }
        });
    }

    private void onCoursewareDoing(String where, JSONObject message) {
        logger.d("onCoursewareDoing");
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
        mParam.setStrEvaluator(content);
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setLearning_stage(learningStage);
        mParam.setVad_max_sec("60");
        mParam.setVad_pause_sec("60");
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
                    logger.d("onEvaluatorError: errorNo = " + resultEntity.getErrorNo() + ", isOfflineFail =" + mIse.isOfflineFail());
                    onRecognizeStop(resultEntity);
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    if (resultEntity.getNewSenIdx() >= 0 && resultEntity.getNewSenIdx() == pageNum) {
                        logger.d("onEvaluatoring: newSenIdx = " + resultEntity.getNewSenIdx() + ", score =" + resultEntity.getScore());
                        onHitSentence(resultEntity.getScore(), resultEntity.getSpeechDuration());
                    }
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
//                logger.d("onVolumeUpdate(): volume = " + volume);
                float floatVolume;
                if (volume > 10) {
                    floatVolume = (float) (volume * 3) / 90.0f;
                } else {
                    floatVolume = 0.3f;
                }
                mWaveView.setWaveAmplitude(floatVolume);
            }
        });
    }

    private void onRecognizeStop(ResultEntity resultEntity) {
        existingVoiceTime = (long) (resultEntity.getSpeechDuration() * 1000);
        if (isAttach()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    XESToastUtils.showToast(mContext, "评测结束");
                    startSpeechRecognize();
                }
            }, 300);
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
    }

    @Override
    public void submitData() {
        int averageScore = 0;
        try {
            answerData.put("tryTimes", allScoreList.size());
            answerData.put("rightNum", "" + rightNum);
            answerData.put("total", mAnswersList.size());
            int sum = 0;
            for (int i = 0; i < allScoreList.size(); i++) {
                sum += allScoreList.get(i);
            }

            if (allScoreList.size() != 0) {
                averageScore = sum / allScoreList.size();
            }
            answerData.put("averageScore", averageScore);
            answerData.put("userAnswer", userAnswer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logger.d("submitData: answerData = " + answerData.toString());
        starNum = calculateStarByScore(averageScore);
        fireNum = (int) Math.ceil(10d * successTimes / (double) (mAnswersList.size()));
        logger.d("submitData: fireNum = " + fireNum + ", goldNum = " + goldNum + ", starNum = " + starNum);
        englishH5CoursewareSecHttp.submitGroupGame(detailInfo, 0, (int) voiceTime, 0, 0, starNum, fireNum, goldNum, 0, (int) voiceTime, 0, 0, answerData.toString(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                logger.d("submitGroupGame -> onDataSucess : objData=" + objData[0].toString());
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                logger.d("submitGroupGame -> onDataFail:" + failMsg);
            }
        });
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        preLoad.onProgressChanged(view, newProgress);
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
                logger.d("getCourseWareTests -> onDataSucess()");
                mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                if (mGroupGameTestInfosEntity.getTestInfoList() == null || mGroupGameTestInfosEntity.getTestInfoList().size() == 0) {
                    return;
                }
                mTestInfoEntity = mGroupGameTestInfosEntity.getTestInfoList().get(0);
                if (mTestInfoEntity.getAnswerList() == null || mTestInfoEntity.getAnswerList().size() == 0) {
                    return;
                }
                mAnswersList = mTestInfoEntity.getAnswerList();
                MAX_SINGLE_COUNT = mTestInfoEntity.getSingleCount();
                initWebView();
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("getCourseWareTests -> onDataFail() : errStatus = " + errStatus + ", failMsg = " + failMsg);
                super.onDataFail(errStatus, failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg + ",请刷新");
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

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

    /**
     * android调JS方法：翻页
     *
     */
    private void turnPage() {
        pageNum++;
        JSONObject resultData = new JSONObject();
        try {
            resultData.put("type", CourseMessage.SEND_CoursewareOnloading);
            resultData.put("pageNum", pageNum);
            StaticWeb.sendToCourseware(wvSubjectWeb, resultData, "*");
            restartTimer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * android调JS方法：上传评分数据
     *
     * @param score
     * @param isTurnPage
     */
    private void uploadScore(int score, boolean isTurnPage) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", CourseMessage.SEND_CoursewareDoing);
            //答对题目学生序号（1/2/3）  单人模式只有2号学生
            jsonData.put("studentNum", 2);
            jsonData.put("score", score);
            jsonData.put("isTurnPage", isTurnPage);
            wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
            if (isTurnPage) {
                pageNum++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 语音评测 - 命中句子
     *
     * @param score
     */
    private void onHitSentence(int score, double speechDuration) {
        logger.d("onHitSentence: score = " + score + ", speechDuration = " + speechDuration);
        singleScoreList.add(score);
        allScoreList.add(score);
        this.voiceTime = existingVoiceTime + (long) (speechDuration * 1000);
        if (score >= 70) {
            singleCount++;
            rightNum++;
//            onFireAdd(10 / MAX_SINGLE_COUNT);
            if (singleCount >= MAX_SINGLE_COUNT) {
                saveUserAnswer(1);
                if (isPlayBack) {
                    goldNum = 1;
                } else {
                    goldNum = 2;
                }
                uploadScore(score, true);
                restartTimer();
            } else {
                uploadScore(score, false);
            }
        } else {
//            onOops();
            uploadScore(score, false);
        }
    }

    private void saveUserAnswer(int isRight) {
        if (isRight == 1) {
            successTimes++;
        }
        long singleVoiceTime = System.currentTimeMillis() - this.singleVoiceTime;
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("text", mAnswersList.get(pageNum).getText());
            JSONArray scoreArray = new JSONArray();
            for (int i = 0; i < singleScoreList.size(); i++) {
                scoreArray.put(singleScoreList.get(i));
            }
            jsonObject.put("scores", scoreArray.toString().substring(1, scoreArray.toString().length() - 1));
            jsonObject.put("voiceTime", (int) singleVoiceTime);
            jsonObject.put("isRight", isRight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userAnswer.put(jsonObject);
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

    /**
     * 评测成功 - 火焰+N
     *
     * @param fireNum
     */
    private void onFireAdd(int fireNum) {
        flFireAdd.setVisibility(View.VISIBLE);
        tvFireAdd.setText("" + fireNum);
        handler.postDelayed(new Runnable() {
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvOops.setVisibility(View.GONE);
            }
        }, 2000);
    }
}
