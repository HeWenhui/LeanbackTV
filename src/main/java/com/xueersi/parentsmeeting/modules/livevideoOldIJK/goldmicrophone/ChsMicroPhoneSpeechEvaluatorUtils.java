package com.xueersi.parentsmeeting.modules.livevideoOldIJK.goldmicrophone;//package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//
//import com.tal.speech.asr.JavaTalAsrJni;
//import com.tal.speech.asr.SpeechLog;
//import com.tal.speech.asr.talAsrJni;
//import com.tal.speech.config.SpeechConfig;
//import com.tal.speech.http.SpeechHttpManager;
//import com.tal.speech.offline.CheckCPUPerformance;
//import com.tal.speech.offline.TalOfflineSpeech;
//import com.tal.speech.speechrecognizer.Constants;
//import com.tal.speech.speechrecognizer.EvaluatorConstant;
//import com.tal.speech.speechrecognizer.EvaluatorListener;
//import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
//import com.tal.speech.speechrecognizer.ResultCode;
//import com.tal.speech.speechrecognizer.ResultEntity;
//import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
//import com.tal.speech.speechrecognizer.TalSpeech;
//import com.tal.speech.utils.SpeechUtils;
//import com.tencent.bugly.crashreport.CrashReport;
//import com.xueersi.common.business.AppBll;
//import com.xueersi.common.business.UserBll;
//import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
//import com.xueersi.common.config.AppConfig;
//import com.xueersi.common.http.HttpCallBack;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.logerhelper.LogerTag;
//import com.xueersi.common.network.download.DownLoadInfo;
//import com.xueersi.common.network.download.DownLoader;
//import com.xueersi.common.network.download.DownloadListener;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
//import com.xueersi.lib.analytics.umsagent.UmsConstants;
//import com.xueersi.lib.framework.are.ContextManager;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.framework.utils.file.FileUtils;
//import com.xueersi.lib.framework.utils.string.StringUtils;
//import com.xueersi.lib.log.LoggerFactory;
//import com.xueersi.lib.log.logger.Logger;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created by: WangDe on 2019/2/13
// */
//
//public class ChsMicroPhoneSpeechEvaluatorUtils implements Serializable {
//
//    static String TAG = "ChsMicroPhoneSpeechEvaluatorUtils";
//    static Logger logger = LoggerFactory.getLogger(TAG);
//    final static String eventId = LogerTag.DEBUG_SPEECH_OFFLINE;
//    private static TalSpeech speech;
//    private static SpeechEvaluatorInter speechEvaluatorInter;
//    private static TalOfflineSpeech offlineSpeech;
//    private static boolean offlineFail = false;
//    private static TalOfflineSpeech offlineSpeechMult;
//    private static boolean offlineMultFail = false;
//    private static TalOfflineSpeech offlineSpeechRecog;
//    private static boolean offlineRecogFail = false;
//    private boolean isOffineRecog = false;
//    private static OnFileSuccess mOnFileSuccess;
//    Context context = ContextManager.getContext();
//    public static int RECOGNIZE_CHINESE = 1;
//    public static int RECOGNIZE_ENGLISH = 0;
//    public static String RECOG_TIME = "check_cpu_recog_time";
//    public static String RECOG_RESULT = "check_cpu_recog_result";
//    SpeechHttpManager speechHttpManager;
//    ShareDataManager shareDataManager;
//    public static int lang = -1;
//    /**
//     * 离线so版本 180605的so
//     */
//    public static String TAL_ASSESS_LIB = Constants.TAL_ASSESS_LIB;
//    /**
//     * 初始化是不是开始
//     */
//    static boolean offStart = false;
//    /**
//     * 离线保存路径
//     */
//    File dir = new File(context.getCacheDir(), "speech/record");
//    /**
//     * 模型文件
//     */
//    private static File sAssessFile = null;
//    /**
//     * 是不是旧的模型
//     */
//    private static boolean oldFile = false;
//    /**
//     * 文件版本号
//     */
//    private static String fileVersion;
//    /**
//     * 评测类型
//     */
//    private String speechType;
//    private String liveId;
//
//    private long checkBeginTime;
//    private long checkEndTime;
//
//    static {
//        SpeechLog.outLog = new SpeechLog.OutLog() {
//            @Override
//            public void d(String s, String s1) {
//                logger.d(s + s1);
//            }
//
//            @Override
//            public void i(String s, String s1) {
//                logger.d(s + s1);
//            }
//
//            @Override
//            public void e(String s, String s1, Exception e) {
//                logger.e(s + s1 + e);
//            }
//        };
//    }
//
//    /**
//     * 只检查更新,下载模型，但是不初始化
//     */
////    public static void uploadAssess() {
////        synchronized (eventId) {
////            logger.d("uploadAssess:offStart=" + offStart);
////            if (offStart) {
////                return;
////            }
////            offStart = true;
////        }
////        final speechEvaluatorUtils = new );
////        AtomicInteger downTryCount = new AtomicInteger();
////        speechEvaluatorUtils.checkVoiceBobVersion(downTryCount, new OnFileReady() {
////            @Override
////            public void onFileReady(File s_assess, String version, boolean isOld) {
////                if (lang != -1) {
////                    try {
////                        speechEvaluatorUtils.loadSoLibrary();
////                        speechEvaluatorUtils.initOfflineSpeech(s_assess, lang, isOld);
////                        if (isOld) {
////                            Map<String, String> mData = new HashMap<>();
////                            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
////                            mData.put("logtype", "uploadAssessold");
////                            mData.put("length", "" + s_assess.length());
////                            speechEvaluatorUtils.umsAgentDebug(mData);
////                        } else {
////                            Map<String, String> mData = new HashMap<>();
////                            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
////                            mData.put("logtype", "uploadAssess");
////                            mData.put("version", "" + version);
////                            speechEvaluatorUtils.umsAgentDebug(mData);
////                        }
////
////                    } catch (Throwable e) {
////                        offlineFail = true;
////                        sAssessFile = null;
////                        //模型初始化失败的回调
////                        if (mOnFileSuccess != null) {
////                            mOnFileSuccess.onFileFail();
////                        }
////                    }
////                } else {
////                    offStart = false;
////                }
////            }
////        }, true);
////    }
//    public ChsMicroPhoneSpeechEvaluatorUtils() {
//        speechHttpManager = new SpeechHttpManager(context);
//        shareDataManager = ShareDataManager.getInstance();
//    }
//
//    /**
//     * 使用在线版本
//     */
//    public ChsMicroPhoneSpeechEvaluatorUtils(boolean isOffline) {
//        this(isOffline, Constants.ASSESS_PARAM_LANGUAGE_EN);
//    }
//
//    public ChsMicroPhoneSpeechEvaluatorUtils(boolean isOffline, final int lang) {
//        if (speech == null) {
//            speechEvaluatorInter = speech = new TalSpeech(context);
//        }
//        if (isOffline) {
//            shareDataManager = ShareDataManager.getInstance();
//            synchronized (eventId) {
//                lang = lang;
//                logger.d("offlineSpeech=" + (offlineSpeech == null) + ",lang=" + lang + "," +
//                        "offlineFail=" + offlineFail + ",offStart=" + offStart);
//                if (offlineSpeech != null) {
//                    if (lang != offlineSpeech.getLanguage()) {
//                        long before = System.currentTimeMillis();
//                        talAsrJni.AssessFree();
//                        offlineSpeech = null;
//
////                        offlineSpeechMult = null;
//                        offStart = false;
//                        offlineFail = false;
//
//                        logger.d("AssessFree:time=" + (System.currentTimeMillis() - before));
//                    }
//                }
//                if ((offlineSpeech == null || offlineSpeechRecog == null) && !offlineFail && !offStart) {
//                    offStart = true;
//                    speechHttpManager = new SpeechHttpManager(context);
//                    final StringBuilder builder = new StringBuilder();
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
//                        for (int i = 0; i < SUPPORTED_ABIS.length; i++) {
//                            builder.append(SUPPORTED_ABIS[i] + ",");
//                        }
//                    } else {
//                        builder.append(Build.CPU_ABI + "," + Build.CPU_ABI2 + ",");
//                    }
//                    try {
//                        loadSoLibrary();
//                        AtomicInteger downTryCount = new AtomicInteger();
////                        sAssessFile = new File("/storage/emulated/0/record/s_shurufa_quick_1109");
//                        checkVoiceBobVersion(downTryCount, new OnFileReady() {
//                            @Override
//                            public void onFileReady(File s_assess, String version, boolean isOld) {
//                                initOfflineSpeech(s_assess, lang, isOld);
//                                if (isOld) {
//                                    Map<String, String> mData = new HashMap<>();
//                                    mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                                    mData.put("logtype", "checkvoicebobold");
//                                    mData.put("length", "" + s_assess.length());
//                                    umsAgentDebug(mData);
//                                } else {
//                                    Map<String, String> mData = new HashMap<>();
//                                    mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                                    mData.put("logtype", "checkvoicebobnew");
//                                    mData.put("version", "" + version);
//                                    umsAgentDebug(mData);
//                                }
//                            }
//                        }, false);
//                    } catch (Throwable e) {
//                        offlineFail = true;
//                        sAssessFile = null;
//                        //模型初始化成功的回调
//                        if (mOnFileSuccess != null) {
//                            mOnFileSuccess.onFileFail();
//                        }
//                    }
//                } else if (offlineSpeech != null && offlineSpeechRecog != null && mOnFileSuccess != null) {
//                    mOnFileSuccess.onFileSuccess();
//                }
//            }
//        }
//    }
//
//    private void loadSoLibrary() throws Throwable {
//        final StringBuilder builder = new StringBuilder();
//        if (Build.VERSION.SDK_INT >= 21) {
//            String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
//            for (int i = 0; i < SUPPORTED_ABIS.length; i++) {
//                builder.append(SUPPORTED_ABIS[i] + ",");
//            }
//        } else {
//            builder.append(Build.CPU_ABI + "," + Build.CPU_ABI2 + ",");
//        }
//        try {
//            System.loadLibrary(TAL_ASSESS_LIB);
//            Map<String, String> mData = new HashMap<>();
//            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//            mData.put("logtype", "loadlibraryS");
//            mData.put("cpu", builder.toString());
//            umsAgentDebug(mData);
//        } catch (Throwable e) {
//            Map<String, String> mData = new HashMap<>();
//            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//            mData.put("logtype", "loadlibraryE");
//            mData.put("cpu", builder.toString());
//            mData.put("error", "" + e);
//            umsAgentDebug(mData);
//            throw e;
//        }
//    }
//
//    /**
//     * 离线是不是加载失败。并不百分百保证。
//     *
//     * @return
//     */
//    public static boolean isOfflineFail() {
//        if (offlineFail) {
//            return true;
//        }
//        if (!offStart) {
//            return offlineSpeech == null;
//        }
//        return false;
//    }
//
//    /**
//     * 离线库是否加载成功
//     *
//     * @return
//     */
//    public static boolean isOfflineSuccess() {
//        return offlineSpeech != null;
//    }
//
//    public static boolean isRecogOfflineSuccess() {
//        return offlineSpeechRecog != null;
//    }
//
//    /**
//     * 英语背课文
//     *
//     * @param strEvaluator
//     * @param localSavePath
//     * @param multRef
//     * @param learning_stage
//     * @param early_return_sec
//     * @param vad_pause_sec
//     * @param vad_max_sec
//     * @param isRct            1 表示背诵
//     */
//    public void startEnglishEvaluator(String strEvaluator, String localSavePath, boolean multRef, String
//            learning_stage, String early_return_sec, String vad_pause_sec, String vad_max_sec, String isRct,
//                                      EvaluatorListener speechEvaluatorListener) {
//        int rctMode = talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_RECITE_MODE, isRct);
//        talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_DECODER_NUM, "1");
//
//        speech.setParameter(EvaluatorConstant.EXTRA_IS_LONGSPEECH, "1");
//        startEnglishEvaluatorOffline(strEvaluator, localSavePath,
//                multRef, learning_stage, early_return_sec, vad_pause_sec,
//                vad_max_sec, speechEvaluatorListener);
//
//
//    }
//
//
//    /**
//     * 目前只有英语背课文有暂停之后继续上次测评的需求；区别于stop！！！stop实为结束当次测评
//     *
//     * @param pause
//     */
//    public void setPause(boolean pause) {
//        if (offlineSpeech == null) {
//            offlineSpeech = new TalOfflineSpeech(context);
//        }
//        offlineSpeech.setPause(pause);
//    }
//
//    /**
//     * 在有重读需求的测评中，此方法显得尤为重要，需要在测评前就调用，防止重读的时候进不去录音的逻辑
//     */
//    public void stop() {
//        if (speechEvaluatorInter != null) {
//            speechEvaluatorInter.stop();
//        }
//
//
//    }
//
//    public static void setOnFileSuccess(OnFileSuccess onFileSuccess) {
//        mOnFileSuccess = onFileSuccess;
//    }
//
////    public static void uploadAssess(OnFileSuccess mOnFileSuccess) {
////        mOnFileSuccess = mOnFileSuccess;
////        uploadAssess();
////    }
//
//    public static int ERROR_CODE_PMERROR = 1;
//    public static int ERROR_CODE_LOADSO = 2;
//
//    public static interface OnFileSuccess {
//
//        /**
//         * 模型加载状态
//         *
//         * @param code
//         */
//        void onFileInit(int code);
//
//        /**
//         * 模型初始化成功
//         */
//        void onFileSuccess();
//
//
//        void onFileFail();
//    }
//
//    interface OnFileReady {
//        /**
//         * 下载模型，成功
//         *
//         * @param s_assess
//         * @param version
//         * @param isOld
//         */
//        void onFileReady(File s_assess, String version, boolean isOld);
//    }
//
//    /**
//     * 下载模型
//     *
//     * @param downTryCount 重试次数
//     * @param onFileReady  下载成功以后
//     * @param isFirst
//     */
//    private void checkVoiceBobVersion(final AtomicInteger downTryCount, final OnFileReady onFileReady, final boolean
//            isFirst) {
//        if (sAssessFile != null) {
//            if (onFileReady != null) {
//                onFileReady.onFileReady(sAssessFile, fileVersion, oldFile);
//            }
//            return;
//        }
////        http://client.xesimg.com/audiobob/s_assess_1
//        speechHttpManager.checkVoiceBobVersion(new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
//                JSONObject jsonData = jsonObject.getJSONObject("danmuForTwo");
//                int isart = jsonData.optInt("isArt");
//                if (isart == 0) {
//                    offStart = false;
//                    SpeechUtils speechUtils = SpeechUtils.getInstance(context);
//                    speechUtils.stopService();
//                    return;
//                }
////                if (isFirst) {
////                    if (NetWorkHelper.MOBILE_STATE == NetWorkHelper.getNetWorkState(context)) {
////                        offStart = false;
////                        return;
////                    }
////                }
//                final File s_assessTmp = new File(dir, "s_assess.tmp");
//                final String version = jsonData.getString("version");
//                final String md5 = jsonData.getString("md5");
//                fileVersion = version;
//                final File s_assess = new File(dir, "s_assess_" + version);
//                final String url = jsonData.getString("url");
//                logger.d("checkVoiceBobVersion:version=" + version + ",url=" + url + ",md5=" + md5);
//                if (s_assess.exists() && s_assess.length() > 2000) {
//                    String oldmd5 = FileUtils.getFileMD5ToString(s_assess);
//                    boolean equals = md5.equalsIgnoreCase(oldmd5);
//                    logger.d("checkVoiceBobVersion.old:equals=" + equals);
//                    if (equals) {
//                        sAssessFile = s_assess;
//                        oldFile = true;
//                        if (onFileReady != null) {
//                            onFileReady.onFileReady(s_assess, version, true);
//                        }
//                        return;
//                    }
//                }
//                s_assess.delete();
//                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(url, s_assessTmp.getParent(), s_assess.getName(), md5);
//                final DownLoader downLoader = new DownLoader(context, downLoadInfo);
//                downLoader.start(new SpeechDownloadListener(downTryCount, s_assessTmp, s_assess, onFileReady, version, md5, downLoader));
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                super.onPmError(responseEntity);
//                Map<String, String> mData = new HashMap<>();
//                mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                mData.put("logtype", "checkvoicebobPmError");
//                mData.put("error", "" + responseEntity.getErrorMsg());
//                umsAgentDebug(mData);
//                logger.e("checkVoiceBobVersion:onPmError=" + responseEntity.getErrorMsg());
//                offStart = false;
//                //模型初始化失败的回调
//                if (mOnFileSuccess != null) {
//                    mOnFileSuccess.onFileFail();
//                }
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.e("checkVoiceBobVersion:onFailure:downTryCount=" + downTryCount.get(), error);
//                if (downTryCount.get() > 3) {
//                    boolean useOldFile = useOldFile();
//                    if (useOldFile) {
//                        return;
//                    }
//                    offStart = false;
//                    //模型初始化失败的回调
//                    if (mOnFileSuccess != null) {
//                        mOnFileSuccess.onFileFail();
//                    }
//                    return;
//                }
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        downTryCount.getAndIncrement();
//                        checkVoiceBobVersion(downTryCount, onFileReady, isFirst);
//                    }
//                }, 6000);
//            }
//        });
//    }
//
//    class SpeechDownloadListener implements DownloadListener {
//        AtomicInteger downTryCount;
//        File s_assessTmp;
//        File s_assess;
//        OnFileReady onFileReady;
//        String version;
//        String md5;
//        DownLoader downLoader;
//
//        SpeechDownloadListener(AtomicInteger downTryCount, File s_assessTmp, File s_assess, OnFileReady onFileReady, String version, String md5, DownLoader downLoader) {
//            this.downTryCount = downTryCount;
//            this.downLoader = downLoader;
//            this.s_assess = s_assess;
//            this.s_assessTmp = s_assessTmp;
//            this.version = version;
//            this.md5 = md5;
//            this.onFileReady = onFileReady;
//        }
//
//        @Override
//        public void onStart(String url) {
//
//        }
//
//        @Override
//        public void onProgressChange(long currentLength, long fileLength) {
//
//        }
//
//        @Override
//        public void onSuccess(String folderPath, String fileName) {
//            logger.d("onDownloadSuccess");
////                        XESToastUtils.showToast(context,"重试次数："+downTryCount.get() + "  成功");
//            s_assessTmp.renameTo(s_assess);
//            sAssessFile = s_assess;
//            if (onFileReady != null) {
//                onFileReady.onFileReady(s_assess, version, false);
//            }
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("file", s_assess.getPath());
//                jsonObject.put("md5", md5);
//                jsonObject.put("version", version);
//                shareDataManager.put(ShareBusinessConfig.APP_SPEECH_VERSION_MD5, jsonObject.toString(),
//                        ShareDataManager.SHAREDATA_NOT_CLEAR);
//            } catch (JSONException e) {
//                logger.e("onDownloadSuccess:version=" + version, e);
//            }
//            //删除其他旧文件
//            final File[] fs = dir.listFiles();
//            if (fs != null) {
//                for (int i = 0; i < fs.length; i++) {
//                    File file = fs[i];
//                    if (!file.getPath().equals(s_assess.getPath())) {
//                        file.delete();
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onFail(int errorCode) {
//            logger.e("onDownloadFailed:downTryCount=" + downTryCount.get() + " errorcode=" + errorCode);
//            if (downTryCount.get() > 2) {
//                boolean useOldFile = useOldFile();
//                if (useOldFile) {
//                    return;
//                }
//                offStart = false;
//                //模型初始化失败的回调
//                if (mOnFileSuccess != null) {
//                    mOnFileSuccess.onFileFail();
//                }
//                return;
//            }
////                        XESToastUtils.showToast(context,"重试次数："+downTryCount.get() + "  错误："+ e.toString());
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    downTryCount.getAndIncrement();
//                    downLoader.start(new SpeechDownloadListener(downTryCount, s_assessTmp, s_assess, onFileReady, version, md5, downLoader));
//                }
//            }, 10000);
//        }
//
//        @Override
//        public void onFinish() {
//            downLoader.deleteDownloadListener(this);
//        }
//
//    }
//
//    /**
//     * 请求或者下载失败，使用旧版本
//     *
//     * @return 找到旧版本
//     */
//    private boolean useOldFile() {
//        String string = shareDataManager.getString(ShareBusinessConfig.APP_SPEECH_VERSION_MD5, "{}", ShareDataManager
//                .SHAREDATA_NOT_CLEAR);
//        try {
//            JSONObject jsonObject = new JSONObject(string);
//            if (jsonObject.has("file")) {
//                String fileName = jsonObject.getString("file");
//                String md5 = jsonObject.getString("md5");
//                String oldmd5 = FileUtils.getFileMD5ToString(fileName);
//                boolean equals = md5.equalsIgnoreCase(oldmd5);
//                if (equals) {
//                    try {
//                        //初始化start如果为1.可能上次失败了
//                        String start = shareDataManager.getString(ShareBusinessConfig.APP_SPEECH_INIT, "0",
//                                ShareDataManager.SHAREDATA_NOT_CLEAR);
//                        loadSoLibrary();
//                        Map<String, String> mData = new HashMap<>();
//                        mData.put("logtype", "useOldFile");
//                        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                        mData.put("fileName", "" + fileName);
//                        mData.put("start", "" + start);
//                        umsAgentDebug(mData);
//                        if ("1".equals(start)) {
//                            new File(fileName).delete();
//                        } else {
//                            initOfflineSpeech(new File(fileName), lang, true);
//                            return true;
//                        }
//                    } catch (Throwable e) {
//
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            logger.e("useOldFile:json", e);
//        }
//        return false;
//    }
//
//    private void initOfflineSpeech(final File s_assess, final int lang, final boolean oldFile) {
//        new Thread("initOfflineSpeech") {
//            @Override
//            public void run() {
//                //初始化开始置为1
//                shareDataManager.put(ShareBusinessConfig.APP_SPEECH_INIT, "1", ShareDataManager.SHAREDATA_NOT_CLEAR);
//                long time = 0;
//                long begin = System.currentTimeMillis();
//                initOfflineAssess(s_assess, time, lang, oldFile);
//                initofflineRecog(s_assess, time, lang, oldFile);
//                time = System.currentTimeMillis() - begin;
//                logger.d("time " + time);
//                if (offlineFail || offlineRecogFail) {
//                    sAssessFile = null;
//                    FileUtils.deleteDir(dir);
//                    offStart = false;
//                    if (mOnFileSuccess != null) {
//                        mOnFileSuccess.onFileFail();
//                    }
//                    logger.d("离线模型初始化失败");
//                } else {
//                    logger.d("离线模型初始化成功");
//                    boolean isPassCheck = shareDataManager.getBoolean(RECOG_RESULT, false, ShareDataManager.SHAREDATA_USER);
//                    if (!isPassCheck && lang == Constants.ASSESS_PARAM_LANGUAGE_EN) {
//                        checkRecogCPUPerformance(new EvaluatorListener() {
//                            @Override
//                            public void onBeginOfSpeech() {
//                                logger.d("checkCPU start:" + System.currentTimeMillis());
//                                checkBeginTime = System.currentTimeMillis();
//                            }
//
//                            @Override
//                            public void onResult(ResultEntity result) {
//
//                                logger.d("checkCPU end: status:" + result.getStatus() + " str: " + result.getCurString()
//                                        + " time:" + System.currentTimeMillis());
//                                if (result.getStatus() == ResultEntity.EVALUATOR_ING) {
//                                    checkEndTime = System.currentTimeMillis();
//                                }
//                                if (result.getStatus() == ResultEntity.SUCCESS) {
//                                    long recogTime = checkEndTime - checkBeginTime;
//                                    shareDataManager.put(RECOG_RESULT, recogTime < 3000, ShareDataManager.SHAREDATA_USER);
//                                    shareDataManager.put(RECOG_TIME, recogTime, ShareDataManager.SHAREDATA_USER);
//                                    if (mOnFileSuccess != null) {
//                                        mOnFileSuccess.onFileSuccess();
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onVolumeUpdate(int volume) {
//
//                            }
//                        });
//                    } else {
//                        if (mOnFileSuccess != null) {
//                            mOnFileSuccess.onFileSuccess();
//                        }
//                    }
//
//
//                }
//                //初始化结束置为1
//                shareDataManager.put(ShareBusinessConfig.APP_SPEECH_INIT, "0", ShareDataManager.SHAREDATA_NOT_CLEAR);
//            }
//        }.start();
//    }
//
//    private void initOfflineAssess(final File s_assess, long time, int lang, boolean oldFile) {
//        if (mOnFileSuccess != null) {
//            mOnFileSuccess.onFileInit(SpeechConfig.SPEECH_ASSESS_INIT_BEGIN);
//        }
//        long before = System.currentTimeMillis();
//        if (offlineSpeechMult == null && lang == Constants.ASSESS_PARAM_LANGUAGE_EN) {
//            int KWSInitial = talAsrJni.KWSInitial(s_assess.getPath());
//            time = System.currentTimeMillis() - before;
////            logger.d("speechevaluator"+ " kwsinitial:"+ time);
//            Map<String, String> mData = new HashMap<>();
//            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//            mData.put("logtype", "kwsinitial");
//            mData.put("KWSInitial", "" + KWSInitial);
//            mData.put("time", "" + time);
//            mData.put("oldFile", "" + oldFile);
//            umsAgentDebug(mData);
//            TalOfflineSpeech offlineSpeechMult = null;
//            if (KWSInitial == 0) {
//                offlineSpeechMult = new TalOfflineSpeech(context);
//                offlineSpeechMult.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
//            } else {
//                offlineFail = true;
//            }
//            this.offlineSpeechMult = offlineSpeechMult;
//        }
//        before = System.currentTimeMillis();
//        int AssessSetParam;
//        if (lang == Constants.ASSESS_PARAM_LANGUAGE_EN) {
//            AssessSetParam = talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_LANGUAGE, "" + Constants
//                    .ASSESS_PARAM_LANGUAGE_EN);
//        } else {
//            AssessSetParam = talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_LANGUAGE, "" + Constants
//                    .ASSESS_PARAM_LANGUAGE_CH);
//        }
//        if (AssessSetParam == 0) {
//            int assessInitial = talAsrJni.AssessInitial(lang, s_assess.getPath());
//            time = System.currentTimeMillis() - before;
////            logger.d("speechevaluator"+ " Assessinitial:"+ time);
//            Map<String, String> mData = new HashMap<>();
//            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//            mData.put("logtype", "assessinitial");
//            mData.put("AssessInitial", "" + assessInitial);
//            mData.put("time", "" + time);
//            mData.put("lang", "" + lang);
//            mData.put("oldFile", "" + oldFile);
//            umsAgentDebug(mData);
//            logger.d("initOfflineSpeech:time=" + time + ",lang=" + lang + "," + (System.currentTimeMillis
//                    () -
//                    before));
//            TalOfflineSpeech offlineSpeech = null;
//            if (assessInitial == 0) {
//                offlineSpeech = new TalOfflineSpeech(context);
//                offlineSpeech.setLanguage(lang);
////                    talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_DECODER_NUM, "1");
//            } else {
//                offlineFail = true;
//            }
//            this.offlineSpeech = offlineSpeech;
//
//        } else {
//            offlineFail = true;
//            offlineSpeech = null;
//        }
//        if (!offlineFail && mOnFileSuccess != null) {
//            mOnFileSuccess.onFileInit(SpeechConfig.SPEECH_ASSESS_INIT_SUCCESS);
//        }
//    }
//
//    private void initofflineRecog(final File s_assess, long time, int lang, boolean oldFile) {
//
//        long before = System.currentTimeMillis();
//        if (offlineSpeechRecog == null && lang == Constants.ASSESS_PARAM_LANGUAGE_EN) {
//            if (mOnFileSuccess != null) {
//                mOnFileSuccess.onFileInit(SpeechConfig.SPEECH_RECOG_INIT_BEGIN);
//            }
//            int RecogInitial = talAsrJni.RecogInitial(s_assess.getPath());
//            time = System.currentTimeMillis() - before;
////            logger.d("speechevaluator"+ " recoginitial:"+ time);
//            Map<String, String> mData = new HashMap<>();
//            mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//            mData.put("logtype", "recoginitial");
//            mData.put("RecogInitial", "" + RecogInitial);
//            mData.put("time", "" + time);
//            mData.put("oldFile", "" + oldFile);
//            umsAgentDebug(mData);
//            TalOfflineSpeech offlineSpeechRecog = null;
//            if (RecogInitial == 0) {
//                offlineSpeechRecog = new TalOfflineSpeech(context);
//                offlineSpeechRecog.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
//                if (mOnFileSuccess != null) {
//                    mOnFileSuccess.onFileInit(SpeechConfig.SPEECH_RECOG_INIT_SUCCESS);
//                }
//            } else {
//                offlineRecogFail = true;
//            }
//            this.offlineSpeechRecog = offlineSpeechRecog;
//        }
//    }
//
//
//    /**
//     * 在线语音识别
//     *
//     * @param localPath
//     * @param type
//     * @param listener
//     */
//    public void startOnlineRecognize(String localPath, int type, EvaluatorListener listener) {
//        if (type == RECOGNIZE_CHINESE) {
//            speechEvaluatorInter = speech;
////            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://10.99.1.232:8002/wsAd");
////            speech.setParameter(EvaluatorConstant.EXTRA_PID, "7");
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1300307");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localPath);
//            speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId
//                    ());
//            speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, "b");
//            startEvaluator(speech, listener, null);
//        }
//    }
//
//    /**
//     * 在线语音识别
//     *
//     * @param localPath
//     * @param type
//     * @param listener
//     */
//    public void startOnlineRecognize(String localPath, int type, EvaluatorListener listener, boolean isChsMicrophone) {
//        if (type == RECOGNIZE_CHINESE) {
//            speechEvaluatorInter = speech;
////            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://10.99.1.232:8002/wsAd");
////            speech.setParameter(EvaluatorConstant.EXTRA_PID, "7");
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1300307");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localPath);
//            speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId
//                    ());
//            speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, "b");
//            startEvaluator(speech, listener, null, isChsMicrophone);
//        }
//    }
//
//    /**
//     * 语音弹幕
//     *
//     * @param localPath
//     * @param type
//     * @param listener
//     */
//    public void startSpeechBulletScreenRecognize(String localPath, int type, EvaluatorListener listener) {
//        if (type == RECOGNIZE_CHINESE) {
//            speechEvaluatorInter = speech;
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103813");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localPath);
//            speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId
//                    ());
//            speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, "b");
//            startEvaluator(speech, listener, null);
//        }
//    }
//
//    /**
//     * 语文语音弹幕
//     *
//     * @param localPath
//     * @param type
//     * @param listener
//     */
//    public void startChineseSpeechBulletRecognize(String localPath, int type, EvaluatorListener listener) {
//        if (type == RECOGNIZE_CHINESE) {
//            speechEvaluatorInter = speech;
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103816");
//            speech.setParameter(EvaluatorConstant.EXTRA_POST_PROCESS, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localPath);
//            speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId
//                    ());
//            speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, "b");
//            startEvaluator(speech, listener, null);
//        }
//    }
//
//    /**
//     * 英语评测
//     *
//     * @param strEvaluator
//     * @param localSavePath
//     * @param listener
//     * @param isEnglish
//     * @param liveId
//     */
//    public void startEnglishEvaluator(String strEvaluator, String localSavePath, boolean multRef, final
//    EvaluatorListener listener, boolean isEnglish, String liveId) {
//        startEnglishEvaluator(strEvaluator, localSavePath, EvaluatorConstant.HEAD_COMPRESS_2, multRef, listener,
//                isEnglish, liveId);
//    }
//
//    /**
//     * 语文背课文
//     *
//     * @param strEvaluator
//     * @param localSavePath
//     * @param listener
//     * @param isEnglish
//     * @param liveId
//     */
//    public void startChineseEvaluator(String strEvaluator, String localSavePath, boolean multRef, final
//    EvaluatorListener listener, boolean isEnglish, String liveId) {
//        startChineseDetailEvaluator(strEvaluator, localSavePath, EvaluatorConstant.HEAD_COMPRESS_4, multRef, listener,
//                isEnglish, liveId);
//    }
//
//
//    /**
//     * @param strEvaluator
//     * @param localSavePath
//     * @param compress
//     * @param multRef
//     * @param listener
//     * @param isEnglish
//     * @param liveId
//     */
//    public void startChineseDetailEvaluator(String strEvaluator, String localSavePath, String compress, boolean multRef,
//                                            final EvaluatorListener listener, boolean isEnglish, String
//                                                    liveId) {
//        if (TextUtils.isEmpty(strEvaluator)) {
//            logger.i("RolePlayerDemoTest 评测文本为空");
//            XESToastUtils.showToast(context, "评测文本为空");
//            listener.onBeginOfSpeech();
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    ResultEntity resultEntity = new ResultEntity();
//                    resultEntity.setStatus(ResultEntity.ERROR);
//                    resultEntity.setErrorNo(ResultCode.SPEECH_CONTENT_EMPTY);
//                    listener.onResult(resultEntity);
//                }
//            });
//            return;
//        }
//        if (!TextUtils.isEmpty(liveId)) {
//            this.liveId = liveId;
//        }
//        speechEvaluatorInter = speech;
//        if (AppConfig.DEBUG) {
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://10.99.1.232:8002/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "3");
//            speech.setParameter(EvaluatorConstant.EXTRA_IS_LONGSPEECH, "1");
//        } else {
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "wss://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103803");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//            speech.setParameter(EvaluatorConstant.EXTRA_IS_LONGSPEECH, "1");
//        }
//        speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, strEvaluator);
//        speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localSavePath);
//        speech.setParameter(EvaluatorConstant.EXTRA_HEAD_COMPRESS, compress);
//        speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId());
//        speech.setParameter(EvaluatorConstant.EXTRA_LEARN_STAGE, "");
//        startEvaluator(speech, listener, strEvaluator);
//    }
//
//    /**
//     * 英语评测
//     *
//     * @param strEvaluator
//     * @param localSavePath
//     * @param compress      上传格式
//     * @param listener
//     * @param isEnglish
//     * @param liveId
//     */
//    public void startEnglishEvaluator(String strEvaluator, String localSavePath, String compress, boolean multRef,
//                                      final EvaluatorListener listener, boolean isEnglish, String liveId) {
//        startEnglishEvaluator(strEvaluator, localSavePath, compress, multRef, "5", "5", "30", listener, isEnglish,
//                liveId);
//    }
//
//    /**
//     * @param strEvaluator
//     * @param localSavePath
//     * @param compress
//     * @param multRef
//     * @param early_return_sec 绘本、晨读语音测评修改关闭5s不提分自动结束策略:
//     * @param vad_pause_sec
//     * @param vad_max_sec      绘本、晨读语音测评修改最长测评时间为90s，
//     * @param listener
//     * @param isEnglish
//     * @param liveId
//     */
//    public void startEnglishEvaluator(String strEvaluator, String localSavePath, String compress, boolean multRef,
//                                      String early_return_sec, String vad_pause_sec,
//                                      String vad_max_sec, final EvaluatorListener listener, boolean isEnglish, String
//                                              liveId) {
//        if (TextUtils.isEmpty(strEvaluator)) {
//            logger.i("评测文本为空");
//            listener.onBeginOfSpeech();
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    ResultEntity resultEntity = new ResultEntity();
//                    resultEntity.setStatus(ResultEntity.ERROR);
//                    resultEntity.setErrorNo(ResultCode.SPEECH_CONTENT_EMPTY);
//                    listener.onResult(resultEntity);
//                }
//            });
//            return;
//        }
//        if (!TextUtils.isEmpty(liveId)) {
//            this.liveId = liveId;
//        }
//        speechEvaluatorInter = speech;
//        if (isEnglish) {
//            if (multRef) {
//                speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://asr.xueersi.com/wsAd");
//                speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103804");
//                speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "1");
//            } else {
//                speech.setParameter(EvaluatorConstant.EXTRA_URL, "wss://asr.xueersi.com/wsAd");
//                speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103801");
//                speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//                speech.setParameter(EvaluatorConstant.EXTRA_EARLY_RETURN_SEC, early_return_sec);
//                speech.setParameter(EvaluatorConstant.EXTRA_VAD_PAUSE_SEC, vad_pause_sec);
//                speech.setParameter(EvaluatorConstant.EXTRA_VAD_MAX_SEC, vad_max_sec);
//            }
//        } else {
//            speech.setParameter(EvaluatorConstant.EXTRA_URL, "wss://asr.xueersi.com/wsAd");
//            speech.setParameter(EvaluatorConstant.EXTRA_PID, "1103803");
//            speech.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//        }
//
////        speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://10.99.1.232:8002/wsAd");
////        speech.setParameter(EvaluatorConstant.EXTRA_PID, "1");
//        speech.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, strEvaluator);
//        speech.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localSavePath);
//        speech.setParameter(EvaluatorConstant.EXTRA_HEAD_COMPRESS, compress);
//        speech.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity().getStuId());
//        speech.setParameter(EvaluatorConstant.EXTRA_LEARN_STAGE, "");
//        startEvaluator(speech, listener, strEvaluator);
//    }
//
//
//    /**
//     * 评测，需要离线在线的
//     *
//     * @param strEvaluator  评测内容
//     * @param localSavePath 本地mp3文件路径
//     * @param multRef       是否语音答题
//     * @param listener      评测回调
//     * @return
//     */
//    public SpeechEvaluatorInter startEnglishEvaluatorOffline(String strEvaluator, String localSavePath, boolean
//            multRef, final EvaluatorListener listener) {
//        return startEnglishEvaluatorOffline(strEvaluator, localSavePath, multRef, "-1", listener);
//    }
//
//    /**
//     * 评测，需要离线在线的
//     *
//     * @param strEvaluator   评测内容
//     * @param localSavePath  本地mp3文件路径
//     * @param multRef        是否语音答题
//     * @param learning_stage 童音提分策略,评级,离线默认-1,在线默认空
//     * @param listener       评测回调
//     * @return
//     */
//    public SpeechEvaluatorInter startEnglishEvaluatorOffline(String strEvaluator, String localSavePath, boolean
//            multRef, String learning_stage, final EvaluatorListener listener) {
//        return startEnglishEvaluatorOffline(strEvaluator, localSavePath, multRef, learning_stage, "5", "5", "30",
//                listener);
//    }
//
//    /**
//     * 评测，需要离线在线的
//     *
//     * @param strEvaluator     评测内容
//     * @param localSavePath    本地mp3文件路径
//     * @param multRef          是否语音答题
//     * @param learning_stage   童音提分策略,评级,离线默认-1,在线默认空
//     * @param early_return_sec 不提分自动结束  默认5
//     * @param vad_pause_sec    读了后暂停（静音）%d秒没读就断 默认5
//     * @param vad_max_sec      最长测评时间 默认30
//     * @param listener         评测回调
//     * @return
//     */
//    public SpeechEvaluatorInter startEnglishEvaluatorOffline(final String strEvaluator, String localSavePath, boolean
//            multRef, String learning_stage, String early_return_sec, String vad_pause_sec,
//                                                             String vad_max_sec, final EvaluatorListener listener) {
//        if (TextUtils.isEmpty(strEvaluator)) {
//            XESToastUtils.showToast(context, "评测文本为空");
//            listener.onBeginOfSpeech();
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    ResultEntity resultEntity = new ResultEntity();
//                    resultEntity.setStatus(ResultEntity.ERROR);
//                    resultEntity.setErrorNo(ResultCode.SPEECH_CONTENT_EMPTY);
//                    resultEntity.setCurString(strEvaluator);
//                    listener.onResult(resultEntity);
//                }
//            });
//            return null;
//        }
//        if (multRef) {
//            if (offlineSpeechMult != null) {
//                speechEvaluatorInter = offlineSpeechMult;
//            } else {
//                speechEvaluatorInter = speech;
//            }
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_URL, "wss://asr.xueersi.com/wsAd");
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_PID, "1103804");
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "1");
//        } else {
//            if (offlineSpeech != null) {
//                speechEvaluatorInter = offlineSpeech;
//                if (StringUtils.isEmpty(learning_stage)) {
//                    //离线默认-1
//                    learning_stage = "-1";
//                }
//                if (StringUtils.isEmpty(early_return_sec)) {
//                    //离线默认5
//                    early_return_sec = "5";
//                }
//                if (StringUtils.isEmpty(vad_pause_sec)) {
//                    //离线默认5
//                    vad_pause_sec = "5";
//                }
//                if (StringUtils.isEmpty(vad_max_sec)) {
//                    //离线默认30
//                    vad_max_sec = "30";
//                }
//                talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_LEARNING_STAGE, learning_stage);
//                talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_EARLY_RETURN_SEC, early_return_sec);
//                talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_VAD_PAUSE_SEC, vad_pause_sec);
//                talAsrJni.AssessSetParam(Constants.ASSESS_PARAM_VAD_MAX_SEC, vad_max_sec);
//            } else {
//                speechEvaluatorInter = speech;
//                //在线默认空，如果离线传过来，重置为空
//                if ("-1".equals(learning_stage)) {
//                    learning_stage = "";
//                }
//                speech.setParameter(EvaluatorConstant.EXTRA_EARLY_RETURN_SEC, early_return_sec);
//                speech.setParameter(EvaluatorConstant.EXTRA_VAD_PAUSE_SEC, vad_pause_sec);
//                speech.setParameter(EvaluatorConstant.EXTRA_VAD_MAX_SEC, vad_max_sec);
//                speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_LEARN_STAGE, learning_stage);
//            }
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_URL, "wss://asr.xueersi.com/wsAd");
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_PID, "1103801");
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_MULT_REF, "0");
//        }
////        speech.setParameter(EvaluatorConstant.EXTRA_URL, "ws://10.99.1.232:8002/wsAd");
////        speech.setParameter(EvaluatorConstant.EXTRA_PID, "1");
//        speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_ASSESS_REF, strEvaluator);
//        speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localSavePath);
//        speech.setParameter(EvaluatorConstant.EXTRA_HEAD_COMPRESS, EvaluatorConstant.HEAD_COMPRESS_2);
//        speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity
//                ().getStuId());
//        JavaTalAsrJni javaTalAsrJni = new JavaTalAsrJni();
//        JavaTalAsrJni.javaTalAsrJni = javaTalAsrJni;
//        if (speechEvaluatorInter == offlineSpeechMult) {
//            logger.d("startEnglishEvaluatorOffline:Inter=offlineSpeechMult");
//            javaTalAsrJni.setRefSen(strEvaluator);
//            int build = JavaTalAsrJni.KWSBuildJava(0, strEvaluator);
//            if (build == 0) {
//                offlineSpeechMult.setJavaTalAsrJni(javaTalAsrJni);
//                startEvaluator(offlineSpeechMult, listener, strEvaluator);
//            } else {
//                startEvaluator(speech, listener, strEvaluator);
//                Map<String, String> mData = new HashMap<>();
//                mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                mData.put("logtype", "kwsbuild");
//                mData.put("error", "" + build);
//                mData.put("ref", "" + strEvaluator);
//                umsAgentDebug(mData);
//            }
//        } else if (speechEvaluatorInter == offlineSpeech) {
//            logger.d("startEnglishEvaluatorOffline:Inter=offlineSpeech");
//            javaTalAsrJni.setRefSen(strEvaluator);
//            int build = JavaTalAsrJni.AssessBuild(0, strEvaluator);
//            if (build == 0) {
//                offlineSpeech.setJavaTalAsrJni(javaTalAsrJni);
//                startEvaluator(offlineSpeech, listener, strEvaluator);
//            } else {
//                if (offlineSpeech.getLanguage() == Constants.ASSESS_PARAM_LANGUAGE_CH) {
//                    ResultEntity resultEntity = new ResultEntity();
//                    resultEntity.setCurStatus(ResultEntity.ERROR);
//                    resultEntity.setErrorNo(ResultCode.SPEECH_UNSUPPORT);
//                    resultEntity.setCurString(strEvaluator);
//                    listener.onResult(resultEntity);
//                    return null;
//                }
//                startEvaluator(speech, listener, strEvaluator);
//                Map<String, String> mData = new HashMap<>();
//                mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
//                mData.put("logtype", "assessbuild");
//                mData.put("error", "" + build);
//                mData.put("ref", "" + strEvaluator);
//                umsAgentDebug(mData);
//            }
//        } else {
//            logger.d("startEnglishEvaluatorOffline:Inter=speech");
//            startEvaluator(speech, listener, strEvaluator);
//        }
//        return speechEvaluatorInter;
//    }
//
//    /**
//     * 离线语音识别
//     *
//     * @param localSavePath 本地mp3文件路径
//     * @param vad_pause_sec 读了后暂停（静音）%d秒没读就断 默认5
//     * @param vad_max_sec   最长测评时间 默认30
//     * @param listener      回调
//     * @return
//     */
//    public SpeechEvaluatorInter startSpeechRecognitionOffline(String localSavePath, String vad_pause_sec,
//                                                              String vad_max_sec, final EvaluatorListener listener) {
//        if (offlineSpeechRecog != null) {
//            speechEvaluatorInter = offlineSpeechRecog;
//            JavaTalAsrJni javaTalAsrJni = new JavaTalAsrJni();
//            JavaTalAsrJni.javaTalAsrJni = javaTalAsrJni;
//            int recogReset = javaTalAsrJni.RecogResetJava(1, 0);
//            if (StringUtils.isEmpty(vad_pause_sec)) {
//                //离线默认5
//                vad_pause_sec = "5";
//            }
//            if (StringUtils.isEmpty(vad_max_sec)) {
//                //离线默认30
//                vad_max_sec = "30";
//            }
//            talAsrJni.RecogSetParam(Constants.RECOG_PARAM_MAX_SEC, vad_max_sec);
//            talAsrJni.RecogSetParam(Constants.RECOG_PARAM_PAUSE_SEC, vad_pause_sec);
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_AUDIO_PATH, localSavePath);
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_RECOG, "1");
//            speechEvaluatorInter.setParameter(EvaluatorConstant.EXTRA_USER_ID, UserBll.getInstance().getMyUserInfoEntity
//                    ().getStuId());
//            offlineSpeechRecog.setJavaTalAsrJni(javaTalAsrJni);
//            logger.d("startSpeechRecognitionOffline:Inter=offlineSpeechRecog");
//            startEvaluator(offlineSpeechRecog, listener, "");
//        }
//
//        return speechEvaluatorInter;
//    }
//
//    private void startEvaluator(SpeechEvaluatorInter speechEvaluatorInter, final EvaluatorListener listener, final
//    String strEvaluator) {
//
//        try {
//            speechEvaluatorInter.start(new EvaluatorListenerWithPCM() {
//
//                @Override
//                public void onRecordPCMData(short[] pcmBuffer, int length) {
//                    if (listener instanceof EvaluatorListenerWithPCM) {
//                        EvaluatorListenerWithPCM pcm = (EvaluatorListenerWithPCM) listener;
//                        pcm.onRecordPCMData(pcmBuffer, length);
//                    }
//                }
//
//                @Override
//                public void onBeginOfSpeech() {
//                    listener.onBeginOfSpeech();
//                    speechLoger(true);
//                }
//
//                @Override
//                public void onResult(ResultEntity resultEntity) {
//                    listener.onResult(resultEntity);
//                    try {
//                        if (resultEntity.getStatus() == ResultEntity.ERROR && resultEntity.getErrorNo() == ResultCode
//                                .NO_AUTHORITY) {
//                            speechLoger(false);
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//
//                @Override
//                public void onVolumeUpdate(int i) {
//                    listener.onVolumeUpdate(i);
//                }
//
//            });
//        } catch (IOException e) {
//            //为了统计错误
//            logger.e("startEvaluator", e);
//            CrashReport.postCatchedException(e);
//        }
//    }
//
//    private void startEvaluator(SpeechEvaluatorInter speechEvaluatorInter, final EvaluatorListener listener, final
//    String strEvaluator, boolean isChsMicrophone) {
//
//        try {
//            speechEvaluatorInter.start(new EvaluatorListenerWithPCM() {
//
//                @Override
//                public void onRecordPCMData(short[] pcmBuffer, int length) {
//                    if (listener instanceof EvaluatorListenerWithPCM) {
//                        EvaluatorListenerWithPCM pcm = (EvaluatorListenerWithPCM) listener;
//                        pcm.onRecordPCMData(pcmBuffer, length);
//                    }
//                }
//
//                @Override
//                public void onBeginOfSpeech() {
//                    listener.onBeginOfSpeech();
//                    speechLoger(true);
//                }
//
//                @Override
//                public void onResult(ResultEntity resultEntity) {
//                    listener.onResult(resultEntity);
//                    try {
//                        if (resultEntity.getStatus() == ResultEntity.ERROR && resultEntity.getErrorNo() == ResultCode
//                                .NO_AUTHORITY) {
//                            speechLoger(false);
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//
//                @Override
//                public void onVolumeUpdate(int i) {
//                    listener.onVolumeUpdate(i);
//                }
//
//            });
//        } catch (IOException e) {
//            //为了统计错误
//            logger.e("startEvaluator", e);
//            CrashReport.postCatchedException(e);
//        }
//    }
//
//    public void cancel() {
//        speechEvaluatorInter.cancel();
//    }
//
//    public void reSubmit() {
//        if (speechEvaluatorInter != null) {
//            speechEvaluatorInter.reSubmit();
//        }
//    }
//
//    /**
//     * 语音测评日志
//     *
//     * @param isEnable 初始化是否正常
//     */
//    private void speechLoger(boolean isEnable) {
//        //if (!isEnable) {
//
//        Map<String, String> params = new HashMap<>();
//        if ("3".equals(speechType)) {
//            params.put("eventid", "live_chinesespeech");
//        } else {
//            params.put("eventid", "live_speechtest");
//        }
//        if (!TextUtils.isEmpty(liveId)) {
//            params.put("liveid", liveId);
//        }
//        params.put("sno", "1");
//        params.put("ex", isEnable ? "Y" : "N");
//        params.put("expect", "1");
//        params.put("clits", System.currentTimeMillis() + "");
//        params.put("stable", "1");
//        logger.i("" + params.toString());
//        UmsAgentManager.umsAgentOtherBusiness(ContextManager.getContext(), UmsConstants.ARTS_APP_ID, UmsConstants
//                .uploadShow, params);
//        //}
//    }
//
//    private void umsAgentDebug(Map<String, String> mData) {
//        try {
//            UmsAgentManager.umsAgentDebug(context, eventId, mData);
//        } catch (Exception e) {
//            UmsAgentManager.umsAgentException(context, TAG + ":umsAgentDebug", e);
//        }
//    }
//
//    /**
//     * 固定音频检测识别数据返回情况，使用Listener返回识别结果
//     *
//     * @param listener
//     */
//    public void checkRecogCPUPerformance(EvaluatorListener listener) {
//        logger.d("checkRecogCPUPerformance");
//        JavaTalAsrJni javaTalAsrJni = new JavaTalAsrJni();
//        JavaTalAsrJni.javaTalAsrJni = javaTalAsrJni;
//        javaTalAsrJni.RecogResetJava(1, 0);
//        talAsrJni.RecogSetParam(Constants.RECOG_PARAM_MAX_SEC, "30");
//        talAsrJni.RecogSetParam(Constants.RECOG_PARAM_PAUSE_SEC, "1");
//        CheckCPUPerformance checkCPUPerformance = new CheckCPUPerformance(context);
//        checkCPUPerformance.setJavaTalAsrJni(javaTalAsrJni);
//        try {
//            checkCPUPerformance.start(listener);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
