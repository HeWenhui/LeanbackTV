package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.CourseGroupItem;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.CourseMessage;
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
import java.util.ArrayList;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

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
    private LinearLayout ll_livevideo_course_item_content;
    /**
     * 新课件缓存
     */
    private NewCourseCache newCourseCache;
    /** 新课件是否是预加载 */
    private boolean ispreload;
    /** 文理英属性 */
    private int isArts = LiveVideoSAConfig.ART_EN;
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

    private WorkerThread mWorkerThread;
    LiveGetInfo liveGetInfo;
    static final String TEST_URL = "file:///android_asset/hot_air_balloon/index.html";
    static final String TEST_CONTENT = "This is an apple|apple|banana|traffic";

    public GroupGameMultNativePager(Context context, LiveGetInfo liveGetInfo) {
        super(context);
        detailInfo = new VideoQuestionLiveEntity();
        detailInfo.id = "1";
        this.liveGetInfo = liveGetInfo;
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
        rlGroupGameControl = view.findViewById(R.id.rl_livevideo_groupgame_control);
        ll_livevideo_course_item_content = view.findViewById(R.id.ll_livevideo_course_item_content);
        return view;
    }

    @Override
    public void initData() {
//        startSpeechRecognize();
        int stuid = Integer.parseInt(liveGetInfo.getStuId());
        mWorkerThread = new WorkerThread(mContext, stuid, false);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
            teamMemberEntity.id = 100 + i;
            teamMemberEntity.name = "测试测试测试测试测试" + i;
            if (i == 2) {
                teamMemberEntity.isMy = true;
            }
            entities.add(teamMemberEntity);
        }
        for (int i = 0; i < entities.size(); i++) {
            TeamMemberEntity teamMemberEntity = entities.get(i);
            CourseGroupItem courseGroupItem = new CourseGroupItem(mWorkerThread);
            View convertView = mInflater.inflate(courseGroupItem.getLayoutResId(), ll_livevideo_course_item_content, false);
            courseGroupItem.initViews(convertView);
            courseGroupItem.updateViews(teamMemberEntity, i, teamMemberEntity);
            ll_livevideo_course_item_content.addView(convertView);
        }
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        mWorkerThread.joinChannel("", liveId + "_11111", Integer.parseInt(liveGetInfo.getStuId()), new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                startSpeechRecognize();
            }
        });
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
                logger.d("onBeginOfSpeech(): volume = " + volume);
                float floatVolume = (float) volume * 3 / 90;

            }

            @Override
            public void onRecordPCMData(short[] shorts, int readSize) {
                logger.d("onRecordPCMData:readSize=" + readSize);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                XESToastUtils.showToast(mContext, "评测完成");
                startSpeechRecognize();
            }
        }, 1000);
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
        if (mIse != null) {
            mIse.cancel();
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

}
