package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.TalSpeech;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.parentsmeeting.logerhelper.UmsAgentUtil;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseSpeechAssessmentPager;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.AppUtils;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.common.util.audio.AudioPlayer;
import com.xueersi.xesalib.utils.audio.AudioPlayerListening;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.xueersi.xesalib.utils.audio.AudioPlayer.mVoiceUrl;

/**
 * 语音评测WEB页
 * Created by ZouHao on 2017/9/11.
 */
public class SpeechAssessmentWebX5Pager extends BaseSpeechAssessmentPager {

    private WebView wvSubjectWeb;
    private View errorView;

    /** 学生ID */
    private String stuId;
    /** 直播ID */
    private String liveid;
    /** 试题ID */
    private String testId;
    private String nonce;
    /** 是不是直播 */
    private boolean isLive;
    private SpeechEvalAction speechEvalAction;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;

    /** 默认为mSpeechType */
    private String mSpeechType = "1";

    /** 标准语音评测 */
    private static final String SPEECH_NORMAL = "1";
    /** RolePlay语音评测 */
    private static final String SPEECH_ROLEPLAY = "2";
    /** 语文跟读 */
    private static final String SPEECH_FOLLOW = "3";

    /** 标准测评JS前缀 */
    private static final String jsNormalPrefix = "SpeechTouchObj";
    /** RolePlay测评JS前缀 */
    private static final String jsRolePlayPrefix = "Touch.this.methods";

    /** 当前正在录音的名字 */
    private String mCurrentRecordName;

    /** 当前停止测评的标记 */
    private String mStopPrefix = "false";

    /** 当前是否完成了一遍测评 */
    private boolean mIsRecordFinish;

    /** 是否是录音状态中最后一条机读 */
    private boolean isRebotLast = false;

    /** 是否非人为停止 */
    private boolean mIsStop = true;
    /** 当前正在播放的语音地址 */
    private String mCurrentPlayVoiceUrl;

    /** 是否结束当前测评 */
    private boolean mIsFinishCurrentSpeech;

    /** 是否接到停止测评指令 */
    private boolean mIsStopCommand;

    /** 检测等待时长 */
    private final static int WAIT_TIME = 100;

    private final int RECORD_WITE = 11000;
    String stuCouId;
    boolean IS_SCIENCE;
    private boolean isStandingLive = false;
    // private AudioPlayerManager mAudioPlayerManager;
    private LogToFile logToFile;

    public SpeechAssessmentWebX5Pager(Context context, String liveid, String testId, String stuId, boolean isLive,
                                      String nonce,
                                      SpeechEvalAction speechEvalAction, String stuCouId, boolean IS_SCIENCE) {
        super(context);
        this.stuId = stuId;
        this.liveid = liveid;
        this.testId = testId;
        this.nonce = nonce;
        this.isLive = isLive;
        this.speechEvalAction = speechEvalAction;
        this.stuCouId = stuCouId;
        this.IS_SCIENCE = IS_SCIENCE;
        dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_english_speech_x5, null);
        wvSubjectWeb = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
        view.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVisibility(View.GONE);
                wvSubjectWeb.setVisibility(View.VISIBLE);
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                /*if (loadView != null) {
                    ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                    ((AnimationDrawable) ivLoading.getBackground()).stop();
                    ViewGroup group = (ViewGroup) loadView.getParent();
                    group.removeView(loadView);
                }*/
                loadView.setVisibility(View.VISIBLE);
                wvSubjectWeb.reload();
            }
        });
        return view;
    }

    public void setStandingLive(boolean standingLive) {
        isStandingLive = standingLive;
    }

    @Override
    public void initData() {
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
        wvSubjectWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        wvSubjectWeb.loadUrl("file:///android_asset/testjs.html");
        ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
        ((AnimationDrawable) ivLoading.getBackground()).start();
        //       wvSubjectWeb.loadUrl("http://172.88.1.180:8084/");
        String host = IS_SCIENCE ? ShareBusinessConfig.LIVE_SCIENCE : ShareBusinessConfig.LIVE_LIBARTS;
//        String url = "http://live.xueersi.com/" + host + "/" + (isLive ? "Live" : "LivePlayBack") + "/speechEval/" +
//                liveid + "/" + stuCouId + "/" + testId + "/" + stuId;
        String url = "http://live.xueersi.com/" + host + "/" + (isLive ? "Live" : "LivePlayBack") + "/speechEval/" +
                liveid + "/" + testId + "/" + stuId;
//        String url = "http://172.88.1.180:8082";
        if (!StringUtils.isEmpty(nonce)) {
            url += "?nonce=" + nonce;
            url += "&stuCouId=" + stuCouId;
        } else {
            url += "?stuCouId=" + stuCouId;
        }
        if (isStandingLive) {
            url += "&isStandingLive=1&isAudio=1";
        }
        wvSubjectWeb.loadUrl(url);
    }

    @android.webkit.JavascriptInterface
    private void addJavascriptInterface() {
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    public String getId() {
        return testId;
    }

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext,
                    mBaseApplication, false, VerifyCancelAlertDialog.MESSAGE_VERIFY_TYPE);
            verifyCancelAlertDialog.initInfo(message);
            verifyCancelAlertDialog.showDialog();
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext,
                    mBaseApplication, false, VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            verifyCancelAlertDialog.initInfo(message);
            verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result.confirm();
                }
            });
            verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result.cancel();
                }
            });
            verifyCancelAlertDialog.showDialog();
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            ConsoleMessage.MessageLevel mLevel = consoleMessage.messageLevel();
            boolean isRequst = false;
            if (mLevel == ConsoleMessage.MessageLevel.ERROR || mLevel == ConsoleMessage.MessageLevel.WARNING) {
                isRequst = true;
            }
            UmsAgentUtil.webConsoleMessage(mContext, wvSubjectWeb.getUrl(), consoleMessage, isRequst);
            Loger.d(TAG, "onConsoleMessage:console=" + consoleMessage.sourceId() + "," + consoleMessage.lineNumber() + "," + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                /*if (loadView != null) {
                    ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                    ((AnimationDrawable) ivLoading.getBackground()).stop();
                    ViewGroup group = (ViewGroup) loadView.getParent();
                    group.removeView(loadView);
                }*/
                loadView.setVisibility(View.GONE);
            }
        }

    }

    public class MyWebViewClient extends WebViewClient {
        String failingUrl;

        @Override
        public void onPageFinished(WebView view, String url) {
            Loger.d(TAG, "onPageFinished:url=" + url);
            if (failingUrl == null) {
                wvSubjectWeb.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Loger.d(TAG, "onPageStarted:url=" + url);
            super.onPageStarted(view, url, favicon);
            failingUrl = null;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            logToFile.d("onReceivedError:url=" + failingUrl + ",errorCode=" + errorCode);
            this.failingUrl = failingUrl;
            wvSubjectWeb.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            ViewGroup group = (ViewGroup) mView.getParent();
            if (group == null) {
                wvSubjectWeb.destroy();
                return true;
            }
            try {
                String deUrl = URLDecoder.decode(url, "UTF-8");
                matchBusiness(deUrl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        }
    }


    /**
     * 拦截网页指令
     *
     * @param deUrl
     */
    private void matchBusiness(String deUrl) {
        if (TextUtils.isEmpty(deUrl)) {
            return;
        }

        int wenIndex = deUrl.indexOf("?");
        String command;
        if (wenIndex != -1) {
            String data = deUrl.substring(wenIndex + 1);
            int firstIndex = data.indexOf("&");
            if (firstIndex == -1) {
                command = data;
                matchCommand(command, null);
            } else {
                command = data.substring(0, firstIndex);
                if (!TextUtils.isEmpty(command)) {
                    String strPram = data.substring(firstIndex + 1);
                    if (!TextUtils.isEmpty(strPram)) {
                        String[] arrStr = strPram.split("&");
                        if (arrStr != null && arrStr.length > 0) {
                            Map<String, String> mParm = new HashMap<>();
                            for (String param : arrStr) {
                                String[] kv = param.split("=");
                                if (kv != null && kv.length == 2) {
                                    mParm.put(kv[0], kv[1]);
                                }
                            }
                            matchCommand(command, mParm);
                        }
                    }
                }
            }
        }
    }

    /**
     * 匹配命令
     *
     * @param command
     * @param mData
     */
    private void matchCommand(String command, Map<String, String> mData) {
        if (TextUtils.isEmpty(command)) {
            return;
        }
        logToFile.d("matchCommand:command=" + command);
        if (command.equals("startRecordEvaluator")) {
            //发起录音
            Loger.i(TAG, "startRecordEvaluator");
            startRecordEvaluator(mData);
        } else if (command.equals("stopRecordEvaluator")) {
            //停止录音
            Loger.i(TAG, "stopRecordEvaluator");
            stopRecordEvaluator(mData);
        } else if (command.equals("clickRecordFileBtn")) {
            //播放录音文件
            Loger.i(TAG, "clickRecordFileBtn");
            playRecordFile(mData);
        } else if (command.equals("pauseRecordFile")) {
            //暂停录音
            //pauseRecordFile();
        } else if (command.equals("closeCurrentPage")) {
            //关闭页面
            Loger.i(TAG, "closeCurrentPage");
            closeCurrentPage();
        } else if (command.equals("reSubmit")) {
            //重新提交
            Loger.i(TAG, "reSubmit");
            reSubmitSpech();
        } else if (command.equals("getAppVersion")) {
            //获取版本号（并告知当前的评测类型）
            Loger.i(TAG, "getAppVersion");
            getAppVersion(mData);
        } else if (command.equals("readingDone")) {
            //对话完毕指示
            Loger.i(TAG, "readingDone");
            mIsRecordFinish = true;
        } else if (command.equals("stopPlaynRoleChat")) {
            //停止播放按钮
            Loger.i(TAG, "stopPlaynRoleChat");
            mIsStop = false;
            stopPlayer();
        }
    }

    /**
     * 获取APP的版本号
     *
     * @param mParam
     */
    private void getAppVersion(Map<String, String> mParam) {
        if (mParam != null) {
            String speechType = mParam.get("speechType");
            if (!TextUtils.isEmpty(speechType)) {
                if (speechType.equals(SPEECH_ROLEPLAY)) {
                    mSpeechType = speechType;
                } else if (speechType.equals(SPEECH_FOLLOW)) {
                    mSpeechType = speechType;
                }
            }
            jsAppVersion();
        }
    }

    /**
     * 发起语音测评
     *
     * @param mParam
     */
    private void startRecordEvaluator(Map<String, String> mParam) {

        mIsFinishCurrentSpeech = false;
        mIsStopCommand = false;

        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
            jsRecordError(1134);
            mIsFinishCurrentSpeech = true;
            return;
        }

        if (AudioPlayer.isPlaying()) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    setPlayStatus(false);
                    AudioPlayer.releaseAudioPlayer(mContext);
                }
            });
        }

        if (mParam != null) {
            String recordFileName = mParam.get("recordFileName");
            if (recordFileName == null) {
                recordFileName = "test";
            }
            if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                //如果是RolePlay则记录名称，并在JS回调接口返回
                mCurrentRecordName = recordFileName;
            }

            String assessRef = mParam.get("assessRef");
            String liveId = mParam.get("liveId");
            String language = mParam.get("language");
            mStopPrefix = mParam.get("isLast");
            if (checkParam(recordFileName, assessRef, liveId, language)) {
                saveVideoFile = new File(dir, recordFileName + ".mp3");
                boolean isEnglish = !mSpeechType.equals(SPEECH_FOLLOW);
                if (mIse == null) {
                    mIse = new SpeechEvaluatorUtils(true);
                }
                if (isEnglish) {
                    speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(assessRef, saveVideoFile.getAbsolutePath(), false,
                            new EvaluatorListener() {
                                @Override
                                public void onBeginOfSpeech() {
                                    jsRecord();
                                }

                                @Override
                                public void onResult(ResultEntity resultEntity) {
                                    if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                                        if (resultEntity.getErrorNo() > 0) {
                                            jsRecordError(resultEntity.getErrorNo());
                                        }
                                        if (mSpeechType.equals(SPEECH_FOLLOW)) {
                                            jsRecordCurrentResult(resultEntity);
                                        } else {
                                            jsRecordResultSuccess(resultEntity.getScore());
                                        }
                                    } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                                        jsRecordError(resultEntity.getErrorNo());
                                    } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                                        jsRecordCurrentResult(resultEntity);
                                    }
                                    if (resultEntity.getStatus() == ResultEntity.SUCCESS || resultEntity.getStatus() == ResultEntity.ERROR) {
                                        mHandler.removeMessages(RECORD_WITE);
                                        if (!mIsFinishCurrentSpeech) {
                                            mIsFinishCurrentSpeech = true;
                                            if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                                                jsStartAnotherReading();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onVolumeUpdate(int i) {
                                    jsUpdateVolume(i);
                                }

                            });
                } else {
                    mIse.startEnglishEvaluator(assessRef, saveVideoFile.getAbsolutePath(), false,
                            new EvaluatorListener() {
                                @Override
                                public void onBeginOfSpeech() {
                                    jsRecord();
                                }

                                @Override
                                public void onResult(ResultEntity resultEntity) {
                                    if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                                        if (resultEntity.getErrorNo() > 0) {
                                            jsRecordError(resultEntity.getErrorNo());
                                        }
                                        if (mSpeechType.equals(SPEECH_FOLLOW)) {
                                            jsRecordCurrentResult(resultEntity);
                                        } else {
                                            jsRecordResultSuccess(resultEntity.getScore());
                                        }
                                    } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                                        jsRecordError(resultEntity.getErrorNo());
                                    } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                                        jsRecordCurrentResult(resultEntity);
                                    }
                                    if (resultEntity.getStatus() == ResultEntity.SUCCESS || resultEntity.getStatus() == ResultEntity.ERROR) {
                                        mHandler.removeMessages(RECORD_WITE);
                                        if (!mIsFinishCurrentSpeech) {
                                            mIsFinishCurrentSpeech = true;
                                            if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                                                jsStartAnotherReading();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onVolumeUpdate(int i) {
                                    jsUpdateVolume(i);
                                }

                            }, false, liveId);
                }
            } else {
                logToFile.d("startRecordEvaluator:assessRef=" + assessRef + ",liveId=" + liveId + ",language=" + language + ",mStopPrefix=" + mStopPrefix);
            }
        }
    }

    /**
     * 停止录音
     */
    private void stopRecordEvaluator(Map<String, String> params) {
        Loger.d(TAG, "stopRecordEvaluator");
        if (params != null) {
            mStopPrefix = params.get("isLast");
        }
        mIsStopCommand = true;
        if (!NetWorkHelper.isNetworkAvailable(mContext)) {
            mIsFinishCurrentSpeech = true;
            jsRecordError(1134);
            if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                jsStartAnotherReading();
            }
        }
        if (mIse != null) {
            mIse.stop();
        }
        boolean isEnglish = !mSpeechType.equals(SPEECH_FOLLOW);
        if (isEnglish) {
            //if (speechEvaluatorInter instanceof TalSpeech) {
            //强制2秒内必须回结果
            if (speechEvaluatorInter instanceof TalSpeech) {
                mHandler.sendEmptyMessageDelayed(RECORD_WITE, 2000);
            }
            //}
        } else {
            //强制2秒内必须回结果
            mHandler.sendEmptyMessageDelayed(RECORD_WITE, 2000);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECORD_WITE) {
                Loger.d(TAG, "handleMessage:jsStartAnotherReading");
                if (!mIsFinishCurrentSpeech) {
                    mIsFinishCurrentSpeech = true;
                    if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                        jsStartAnotherReading();
                    }
                }
            }
        }
    };

    /**
     * 关闭当前页面
     */
    private void closeCurrentPage() {
        wvSubjectWeb.post(new Runnable() {
            @Override
            public void run() {
                if (mIse != null) {
                    mIse.stop();
                }
                if (AudioPlayer.isPlaying()) {
                    new Thread() {
                        @Override
                        public void run() {
                            AudioPlayer.releaseAudioPlayer(mContext);
                            setPlayStatus(false);
                        }
                    }.start();
                }
                ViewGroup group = (ViewGroup) mView.getParent();
                if (group != null) {
                    wvSubjectWeb.destroy();
                    group.removeView(mView);
                    speechEvalAction.stopSpeech(SpeechAssessmentWebX5Pager.this, testId);
                }
            }
        });
    }


    /**
     * 播放录音文件
     */
    private void playRecordFile(Map<String, String> params) {
        if (params != null) {
            //roleplay录音
            final String webMp3Url = params.get("playSrc");
            if (!TextUtils.isEmpty(webMp3Url)) {
                final String tip = params.get("which");
                wvSubjectWeb.post(new Runnable() {
                    @Override
                    public void run() {
                        //AudioPlayer.releaseAudioPlayer(mContext);
                        String playUrl = webMp3Url;
                        if (webMp3Url.startsWith("//")) {
                            playUrl = "https:" + playUrl;
                        } else if (!webMp3Url.startsWith("http")) {
                            saveVideoFile = new File(dir, webMp3Url + ".mp3");
                            playUrl = saveVideoFile.getPath();
                        }
                        logToFile.i("playRecordFile:playUrl=" + playUrl + ",tip=" + tip);
                        if (mCurrentPlayVoiceUrl != null && mCurrentPlayVoiceUrl.equals(playUrl)) {
                            //如果和当前播放的是一样的语音则停止
                            if (AudioPlayer.isPlaying()) {
                                AudioPlayer.stop();
                                AudioPlayer.releaseAudioPlayer(mContext);
                                mCurrentPlayVoiceUrl = "";
                                if (mIsStop) {
                                    if (!TextUtils.isEmpty(tip)) {
                                        isRebotLast = false;
                                        if (tip.equals("false")) {
                                            jsStopRecordBtn();
                                        } else if (tip.equals("last")) {
                                            isRebotLast = true;
                                            jsStopRecordBtn();
                                        }
                                    }
                                }
                                mIsStop = true;
                                return;
                            }
                        } else {
//                            if (mAudioPlayerManager != null && mAudioPlayerManager.getState() == AudioPlayerManager.State.playing) {
//                                mIsStop = false;
//                            }
                            if (AudioPlayer.isPlaying()) {
                                mIsStop = false;
                            }
                        }
                        final boolean result = AudioPlayer.audioPlayerAsyncControl(playUrl, mContext, 1000, new
                                AudioPlayerListening() {
                                    @Override
                                    public void playComplete(int where) {
                                        logToFile.i("playComplete:where=" + where + ",mIsStop=" + mIsStop);
                                        try {
                                            AudioPlayer.stop();
                                        } catch (Exception e) {

                                        }
                                        if (mIsStop) {
                                            if (!TextUtils.isEmpty(tip)) {
                                                isRebotLast = false;
                                                if (tip.equals("false")) {
                                                    jsStopRecordBtn();
                                                } else if (tip.equals("last")) {
                                                    isRebotLast = true;
                                                    jsStopRecordBtn();
                                                }
                                            }
                                        }
                                        mIsStop = true;
                                        Loger.i(TAG, "playComplete");
                                    }

                                    @Override
                                    public void prepared(int duration) {
                                        logToFile.i("prepared:duration=" + duration);
                                        mCurrentPlayVoiceUrl = mVoiceUrl;
                                        AudioPlayer.play();
                                    }

                                    @Override
                                    public void currentDuration(int current, int duration) {
                                        Loger.i(TAG, "currentDuration:current=" + current + ",duration=" +
                                                duration);
                                    }

                                    @Override
                                    public void onError(int what, int code) {
                                        super.onError(what, code);
                                        logToFile.i("onError:what=" + what + ",code=" + code);
                                        if (!TextUtils.isEmpty(tip)) {
                                            isRebotLast = false;
                                            if ("false".equals(tip)) {
                                                jsStopRecordBtn();
                                            } else if ("last".equals(tip)) {
                                                isRebotLast = true;
                                                jsStopRecordBtn();
                                            }
                                        }
                                    }
                                }, false, 0, true);
                        logToFile.i("jsRecordError:result=" + result);
                        if (!result) {
                            jsRecordError(ResultCode.PLAY_RECORD_FAIL);
                        }
                    }
                });
            }
        } else {
            //标准语音测评
            if (saveVideoFile == null) {
                logToFile.i("playRecordFile:saveVideoFile=null");
                jsRecordError(ResultCode.PLAY_RECORD_FAIL);
            } else if (saveVideoFile.exists()) {
                logToFile.i("playRecordFile:saveVideoFile=" + saveVideoFile + ",exists=true");
                if (AudioPlayer.isPlaying()) {
                    wvSubjectWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            setPlayStatus(false);
                            AudioPlayer.releaseAudioPlayer(mContext);
                        }
                    });
                } else {
                    wvSubjectWeb.post(new Runnable() {
                        @Override
                        public void run() {
                            AudioPlayer.releaseAudioPlayer(mContext);
                            boolean result = AudioPlayer.audioPlayerControl(saveVideoFile.getPath(), mContext, 1000, new
                                    AudioPlayerListening() {

                                        @Override
                                        public void playComplete(int where) {
                                            setPlayStatus(false);
                                            Loger.i(TAG, "playComplete");
                                        }

                                        @Override
                                        public void prepared(int duration) {
                                            setPlayStatus(true);
                                            Loger.i(TAG, "prepared:duration=" + duration);
                                        }

                                        @Override
                                        public void currentDuration(int current, int duration) {
                                            Loger.i(TAG, "currentDuration:current=" + current + ",duration=" +
                                                    duration);

                                        }

                                    }, false, 0, true);
                            if (!result) {
                                jsRecordError(ResultCode.PLAY_RECORD_FAIL);
                            }
                        }
                    });
                }
            } else {
                logToFile.i("playRecordFile:saveVideoFile=" + saveVideoFile + ",exists=false");
                jsRecordError(ResultCode.PLAY_RECORD_FAIL);
            }
        }
    }

    /**
     * 停止播放录音文件
     */
    private void pauseRecordFile() {
        new Thread() {
            @Override
            public void run() {
                AudioPlayer.releaseAudioPlayer(mContext);
            }
        }.start();
    }

    /**
     * 重新提交评测
     */
    private void reSubmitSpech() {
        if (mIse != null) {
            mIse.reSubmit();
        }
    }

    /**
     * 音量调节
     */
    private void jsUpdateVolume(final int volume) {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    wvSubjectWeb.loadUrl("javascript:" + getCurrentJsPrefix() + ".updateVolume(" + volume + ")");
                }
            });
        }
    }

    /**
     * 录音准备好，正式开始录音
     */
    private void jsRecord() {
        Loger.i("SpeechAssessTest", "jsRecord:");
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    Loger.i(TAG, "js.record");
                    wvSubjectWeb.loadUrl("javascript:" + getCurrentJsPrefix() + ".record()");
                }
            });
        }
    }

    /**
     * 回调播放状态
     *
     * @param isPlayer
     */
    private void setPlayStatus(final boolean isPlayer) {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    Loger.i(TAG, "js.setPlayBtnStatus");
                    wvSubjectWeb.loadUrl("javascript:" + getCurrentJsPrefix() + ".setPlayBtnStatus(" + (isPlayer ?
                            "1" : "0") + ")");
                }
            });

        }
    }

    /**
     * 停止播放
     */
    public void stopPlayer() {
        if (AudioPlayer.isPlaying()) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    setPlayStatus(false);
                    AudioPlayer.releaseAudioPlayer(mContext);
                }
            });
        }
    }

    /**
     * 测评成功回调
     *
     * @param score
     */
    private void jsRecordResultSuccess(final int score) {
        logToFile.i("jsRecordResultSuccess:score=" + score);
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    if (mSpeechType.equals(SPEECH_NORMAL)) {
                        wvSubjectWeb.loadUrl("javascript:" + getCurrentJsPrefix() + ".recordResultSuccess(" + score +
                                ")");

                    } else if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                        Loger.i(TAG, "js.recordResultSuccess:" + score + " / " + mCurrentRecordName);
                        wvSubjectWeb.loadUrl("javascript:" + getCurrentJsPrefix() + ".recordResultSuccess(" + score +
                                "," + mCurrentRecordName + ")");
                    }
                }
            });
        }
    }

    /**
     * 评测当前结果回调
     *
     * @param entity
     */
    private void jsRecordCurrentResult(final ResultEntity entity) {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    if (mSpeechType.equals(SPEECH_FOLLOW)) {
                        try {
                            String command = "javascript:" + getCurrentJsPrefix() + ".showResult(" + entity.getCurStatus()
                                    + "," + entity.getCurString() + ")";
                            wvSubjectWeb.loadUrl(command);
                            logToFile.i("jsRecordCurrentResult:command=" + command);
                        } catch (Exception e) {
                            logToFile.i("jsRecordCurrentResult:currentResult" + e.getMessage());
                        }
                    }
                }
            });
        }
    }

    /**
     * 测评错误回调
     *
     * @param errStatus
     */
    private void jsRecordError(final int errStatus) {
        logToFile.i("jsRecordError:" + errStatus);
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    Loger.i(TAG, "js.recordError:" + errStatus);
                    if (mSpeechType.equals(SPEECH_NORMAL) || mSpeechType.equals(SPEECH_FOLLOW)) {
                        wvSubjectWeb.loadUrl("javascript: " + getCurrentJsPrefix() + ".recordError(" + errStatus + ")");
                    } else if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                        wvSubjectWeb.loadUrl("javascript: " + getCurrentJsPrefix() + ".recordError(" + errStatus + "," +
                                "" + mCurrentRecordName + ")");
                    }
                }
            });
        }
    }

    /**
     * 用户手动返回，提交评测
     */
    public void jsExamSubmit() {
        if (wvSubjectWeb != null) {
            logToFile.i("jsExamSubmit");
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    wvSubjectWeb.loadUrl("javascript: ajaxExamSubmit()");
                }
            });
        }
    }

    /**
     * RolePlay播放回调
     */
    private void jsStopRecordBtn() {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    logToFile.i("jsStopRecordBtn:mIsRecordFinish=" + mIsRecordFinish + " / " + isRebotLast);
                    if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                        wvSubjectWeb.loadUrl("javascript: " + getCurrentJsPrefix() + ".stopRecordBtn(" +
                                mIsRecordFinish + "," +
                                "" + (isRebotLast ? "true" : "false") + ")");
                    }
                }
            });
        }
    }

    /**
     * 告知网页当前APP版本号
     */
    public void jsAppVersion() {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    Loger.i(TAG, "js.getAppVersion:" + AppUtils.getAppVersionCode(ContextManager
                            .getContext()));
                    wvSubjectWeb.loadUrl("javascript: getAppVersion(" + AppUtils.getAppVersionCode(ContextManager
                            .getContext()) + ")");
                }
            });
        }
    }

    /**
     * 检查参数是否有为空的
     *
     * @param strParam
     */
    private boolean checkParam(String... strParam) {
        for (String param : strParam) {
            if (TextUtils.isEmpty(param)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 老师强制交卷
     */
    public void examSubmitAll() {
        if (wvSubjectWeb != null) {
            Loger.i(TAG, "js.speechExamSubmitAll");
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    wvSubjectWeb.loadUrl("javascript:speechExamSubmitAll()");
                }
            });
        }
    }

    /**
     * 通知可以开始下一条语音评测
     */
    private void jsStartAnotherReading() {
        if (wvSubjectWeb != null) {
            wvSubjectWeb.post(new Runnable() {
                @Override
                public void run() {
                    if (mSpeechType.equals(SPEECH_ROLEPLAY)) {
                        Loger.i(TAG, "js.startAnotherReading:" + (mStopPrefix != null ? mStopPrefix
                                : ""));
                        wvSubjectWeb.loadUrl("javascript: startAnotherReading(" +
                                (mStopPrefix != null ? mStopPrefix : "") + ")");
                    }
                }
            });
        }
    }

    /**
     * 返回当前评测的JS前缀符
     *
     * @return
     */
    private String getCurrentJsPrefix() {
        return mSpeechType.equals(SPEECH_ROLEPLAY) ? jsRolePlayPrefix : jsNormalPrefix;
    }

}
