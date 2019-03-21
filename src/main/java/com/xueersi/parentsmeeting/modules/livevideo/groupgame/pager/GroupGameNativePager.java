package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.ui.widget.WaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
     * 右侧控制区
     */
    private RelativeLayout rlGroupGameControl;
    /**
     * 课件接口失败刷新
     */
    private ImageView ivCourseRefresh;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;
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
    private boolean isPreload;
    private String learningStage;
    private String liveId;

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


    static final String TEST_URL = "file:///android_asset/hot_air_balloon/index.html";
    static final String TEST_CONTENT = "This is an apple|apple|banana|traffic";

    public GroupGameNativePager(Context context, String learningStage, String liveId) {
        super(context);
        this.learningStage = learningStage;
        this.liveId=liveId;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        rlGroupGameControl = view.findViewById(R.id.rl_livevideo_groupgame_control);
        mWaveView = view.findViewById(R.id.wv_livevideo_groupgame_control_wave);
        return view;
    }

    @Override
    public void initData() {
        startSpeechRecognize();
//        newCourseCache = new NewCourseCache(mContext, liveId);
//        addJavascriptInterface();
//        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                if (("" + consoleMessage.message()).contains("sendToCourseware")) {
//                    CrashReport.postCatchedException(new Exception());
//                }
//                return super.onConsoleMessage(consoleMessage);
//            }
//        });
//        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());
//        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {
//
//            @Override
//            public void postMessage(String where, final JSONObject message, String origin) {
//                try {
//                    String type = message.getString("type");
//                    if (CourseMessage.REC_close.equals(type)) {
//                    } else if (CourseMessage.REC_submitAnswer.equals(type)) {
//
//                    } else if (CourseMessage.REC_answer.equals(type)) {
//                        onAnswer(message);
//                    } else if (CourseMessage.REC_loadComplete.equals(type)) {
//                        onLoadComplete(where, message);
//                    } else if (CourseMessage.REC_SubmitAnswer.equals(type)) {
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }), "xesApp");
        wvSubjectWeb.loadUrl(TEST_URL);
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
        mParam.setStrEvaluator(TEST_CONTENT);
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setLearning_stage(learningStage);
        mIse.startRecog(mParam, new EvaluatorListener() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech()");
                mWaveView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWaveView.initialize();
                    }
                }, 100);
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
                logger.d("onBeginOfSpeech(): volume = " + volume);
                float floatVolume = (float) volume * 3 / 90;
                mWaveView.setWaveAmplitude(floatVolume);
            }
        });
    }

    private void onRecognizeStop() {
        mWaveView.setWaveAmplitude(0.0f);
    }

    @Override
    public void initListener() {

    }

    /**
     * 实现 h5课件基础接口
     *
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
        return null;
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
    }
}
