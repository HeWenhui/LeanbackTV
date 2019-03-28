package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NewCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Time;

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
     * 文理英属性
     */
    private int isArts = LiveVideoSAConfig.ART_EN;
    /**
     * 是不是回放
     */
    private boolean isPlayBack = false;

    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private GroupGameTestInfosEntity mGroupGameTestInfosEntity;

    private LiveGetInfo liveGetInfo;
    private VideoQuestionLiveEntity detailInfo;
    private String url;
    private String learningStage;
    private String liveId;
    private int stuId;

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

    private int pageNum = 0;
    private int singleCount = 0;


    static final String TEST_URL = "file:///android_asset/hot_air_balloon/index.html";
    static final String TEST_CONTENT = "This is an apple|apple|banana|traffic";

    static final int MAX_SINGLE_COUNT = 5;

    CountDownTimer mCountDownTimer;

    public GroupGameNativePager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity) {
        super(context);
        this.liveGetInfo = liveGetInfo;
        this.detailInfo = detailInfo;
        this.url = englishH5Entity.getUrl();
        this.stuId = Integer.parseInt(liveGetInfo.getStuId());
        this.learningStage = liveGetInfo.getStudentLiveInfo().getLearning_stage();
        this.liveId = liveGetInfo.getId();
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
        return view;
    }

    @Override
    public void initData() {
        rlGroupGameSingle.setVisibility(View.VISIBLE);
        startSpeechRecognize();
        startTimer();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWaveView.initialize();
            }
        }, 1000);
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
//        wvSubjectWeb.loadUrl(TEST_URL);
        wvSubjectWeb.loadUrl(mGroupGameTestInfosEntity.getTestInfoList().get(0).getPreviewPath());
    }

    private void startTimer() {

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

        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
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
        mParam.setLearning_stage(learningStage);
        mIse.startRecog(mParam, new EvaluatorListener() {
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
                    logger.d("onEvaluatorError: errorNo = " + resultEntity.getErrorNo() + ", isOfflineFail =" + mIse.isOfflineFail());
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
                logger.d("onVolumeUpdate(): volume = " + volume);
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

    private void onRecognizeStop() {
        if (isAttach()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    XESToastUtils.showToast(mContext, "评测结束");
                    startSpeechRecognize();
                }
            }, 1000);
        }
    }

    @Override
    public void initListener() {

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
                logger.d("getCourseWareTests->onDataSucess()");
                mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                initData();
                initListener();
            }
        });
    }

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
        if (mIse != null) {
            mIse.cancel();
        }
    }

    /**
     * android调JS方法：翻页
     *
     * @param pageNum
     */
    private void turnPage(int pageNum) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", "coursewareOnloading");
            jsonData.put("pageNpum", 3);
            wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
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
            jsonData.put("type", "coursewareDoing");
            jsonData.put("score", score);
            jsonData.put("isTurnPage", isTurnPage);
            wvSubjectWeb.loadUrl("javascript:postMessage(" + jsonData + ",'" + "*" + "')");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 语音评测 - 命中句子
     *
     * @param score
     */
    private void onHitSentence(int score) {
        if (score >= 70) {
            singleCount++;
            onFireAdd(10 / MAX_SINGLE_COUNT);
            if (singleCount >= MAX_SINGLE_COUNT) {
                singleCount = 0;
                uploadScore(score, true);
            } else {
                uploadScore(score, false);
            }
        } else {
            onOops();
            uploadScore(score, false);
        }
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
