package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.airbnb.lottie.L;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.http.QuestionParse;
import com.xueersi.parentsmeeting.modules.livevideo.util.ErrorWebViewClient;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ren.yale.android.cachewebviewlib.CacheWebResourceResponse;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.RequestIntercept;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;

/**
 * 互动题缓存
 * Created by linyuqiang on 2018/6/11.
 */
public class QuestionWebCache {
    private String TAG = "QuestionWebCache";
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context context;
    private int newProgress;
    private int urlindex = 0;
    private LiveThreadPoolExecutor threadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    /** 是不是已经开始加载 */
    private static boolean startLoad = false;
    private File mMorecacheout;
    private ThreadPoolExecutor executos;

    public QuestionWebCache(Context context) {
        this.context = context;
    }

    public void startCacheZip(String liveId) {
        if (startLoad) {
            return;
        }
        startLoad = true;
        executos = new ThreadPoolExecutor(1, 1,
                10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//        executos.allowCoreThreadTimeOut(true);
        mMorecacheout = LiveCacheFile.geCacheFile(context, "live_h5test_cache");
        final LiveHttpManager liveHttpManager = new LiveHttpManager(context);
        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        httpRequestParams
        liveHttpManager.sendPost(LiveVideoConfig.HTTP_HOST + "/" + ShareBusinessConfig.LIVE_SCIENCE + "/LiveCourse/getStaticResource",
                httpRequestParams, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        QuestionParse questionParse = new QuestionParse();
                        ArrayList<String> list = questionParse.parseQueCache(responseEntity);
                        final AtomicInteger atomicInteger = new AtomicInteger(0);
                        for (int i = 0; i < list.size(); i++) {
                            final String url = list.get(i);
                            if (!url.startsWith("http")) {
                                continue;
                            }
                            String filename = null;
                            int index = url.lastIndexOf("/");
                            if (index != 1) {
                                filename = url.substring(index + 1);
                            }
                            if (filename == null) {
                                continue;
                            }
                            String name = MD5Utils.getMD5(url) + "_" + filename;
                            final File saveFile = new File(mMorecacheout, name);
                            if (saveFile.exists()) {
                                continue;
                            }
                            final File saveFileTmp = new File(mMorecacheout, name + ".tmp");
                            if (saveFileTmp.exists()) {
                                saveFileTmp.delete();
                            }
                            int get = atomicInteger.getAndIncrement();
                            logger.d("startCache:get=" + get);
                            final String finalName = name;
                            final String finalFilename = filename;
                            liveHttpManager.download(url, saveFileTmp.getPath(), new DownloadCallBack() {
                                @Override
                                protected void onDownloadSuccess() {
                                    int get = atomicInteger.getAndDecrement();
                                    boolean rename = saveFileTmp.renameTo(saveFile);
                                    logger.d("startCache:onDownloadSuccess:url=" + url + ",rename=" + rename + ",get=" + get);
                                    File out;
                                    if (finalName.contains("MathJax")) {
                                        out = new File(mMorecacheout, "MathJax");
                                        //删除旧文件，MathJax只留一个
                                        File[] fs = out.listFiles();
                                        if (fs != null) {
                                            for (int i = 0; i < fs.length; i++) {
                                                File oldFile = fs[i];
                                                FileUtils.deleteDir(oldFile);
                                            }
                                        }
                                    } else {
                                        out = mMorecacheout;
                                    }
                                    QueZipExtractorTask zipExtractorTask = new QueZipExtractorTask(saveFile.getPath(), out.getPath(), true);
                                    zipExtractorTask.executeOnExecutor(executos);
                                    if (get == 1) {
                                        zipEnd();
                                    }
                                }

                                @Override
                                protected void onDownloadFailed() {
                                    int get = atomicInteger.getAndDecrement();
                                    logger.d("startCache:onDownloadFailed:url=" + url + ",get=" + get);
                                    if (get == 1) {
                                        zipEnd();
                                    }
                                }

                                private void zipEnd() {
                                    logger.d("zipEnd:start");
                                    executos.shutdown();
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                executos.awaitTermination(320, TimeUnit.SECONDS);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            logger.d("zipEnd:end");
                                            startLoad = false;
                                        }
                                    }.start();
                                }
                            });
                        }
                        logger.d("startCache:onPmSuccess:list=" + list.size() + ",down=" + atomicInteger.get());
                        if (atomicInteger.get() == 0) {
                            startLoad = false;
                        }
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.d("startCache:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                        startLoad = false;
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.d("startCache:onPmFailure:msg=" + msg);
                        startLoad = false;
                    }
                });
    }

    public void startCache() {
        logger.d("startCache:startLoad=" + startLoad);
        if (startLoad) {
            return;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String lastStr = ShareDataManager.getInstance().getString(ShareDataConfig.LIVE_QUES_CACHE, "20191110", ShareDataManager.SHAREDATA_USER);
            String nowStr = dateFormat.format(new Date());
            logger.d("startCache:last=" + lastStr + ",now=" + nowStr);
            if (TextUtils.equals(nowStr, lastStr)) {
                return;
            }
            ShareDataManager.getInstance().put(ShareDataConfig.LIVE_QUES_CACHE, "" + dateFormat.format(new Date()), ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            ShareDataManager.getInstance().remove(ShareDataManager.SHAREDATA_USER, ShareDataConfig.LIVE_QUES_CACHE);
            LiveCrashReport.postCatchedException(TAG, e);
        }
        startLoad = true;
        final Handler handler = LiveMainHandler.getMainHandler();
        final CacheWebView webView = new CacheWebView(context);
        //20秒超时，权限加载
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.d("startCache:destroy1");
                    startLoad = false;
                    webView.stopLoading();
                    webView.destroy();
                    StableLogHashMap stableLogHashMap = new StableLogHashMap("loadtimeout");
                    stableLogHashMap.put("newProgress", "" + newProgress);
                    UmsAgentManager.umsAgentDebug(context, LiveVideoConfig.LIVE_H5_TEST_PRELOAD, stableLogHashMap.getData());
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
        };
        handler.postDelayed(runnable, 20000);
        webView.setWebViewClient(new MyWebViewClient());
        final String examUrl = "https://live.xueersi.com/science/Live/getMultiTestPaper?liveId=119740&testId=365160-1&stuId=-111&stuName=test@talwx.com&stuCouId=12345654&isArts=0&nonce=45645dasf&isTowall=0";
        webView.loadUrl(examUrl);

        webView.getWebViewCache().setNeedHttpDns(true);
        webView.setRequestIntercept(new RequestIntercept() {
            @Override
            public void onIntercept(final String url, CacheWebResourceResponse webResourceResponse) {
                final int startProgress = newProgress;
                final boolean isIntercept = webResourceResponse != null;
                final boolean ispreload = isIntercept && webResourceResponse.isFile();
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StableLogHashMap stableLogHashMap = new StableLogHashMap("interceptrequestv1");
                            stableLogHashMap.put("courseurl", "" + examUrl);
                            stableLogHashMap.put("url", url);
                            stableLogHashMap.put("urlindex", "" + (urlindex++));
                            stableLogHashMap.put("newProgress", "" + newProgress);
                            stableLogHashMap.put("startProgress", "" + startProgress);
                            stableLogHashMap.put("liveId", "119740");
                            stableLogHashMap.put("testid", "365160-1");
                            stableLogHashMap.put("isIntercept", "" + isIntercept);
                            stableLogHashMap.put("ispreload", "" + ispreload);
                            UmsAgentManager.umsAgentDebug(context, LiveVideoConfig.LIVE_H5_TEST_PRELOAD, stableLogHashMap.getData());
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                        }
                    }
                });
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            boolean hasdestroy = false;

            @Override
            public void onProgressChanged(WebView webView, int i) {
                newProgress = i;
                if (i == 100) {
                    startLoad = false;
                    if (hasdestroy) {
                        return;
                    }
                    hasdestroy = true;
                    handler.removeCallbacks(runnable);
                }
            }
        });
    }

    private class MyWebViewClient extends ErrorWebViewClient {

        public MyWebViewClient() {
            super(TAG);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            logger.d("shouldInterceptRequest:url=" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            webView.destroy();
            logger.d("onPageFinished:s=" + s);
        }
    }

    static class QueZipExtractorTask extends ZipExtractorTask {

        long before;

        public QueZipExtractorTask(String in, String out, boolean replaceAll) {
            super(in, out, replaceAll);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            before = SystemClock.elapsedRealtime();
        }

        @Override
        protected void onPostExecute(Exception exception) {
            super.onPostExecute(exception);
            if (exception == null) {
                logger.d("onPostExecute:input=" + mInput + ",time=" + (SystemClock.elapsedRealtime() - before));
            } else {
                logger.e("onPostExecute:input=" + mInput + ",time=" + (SystemClock.elapsedRealtime() - before), exception);
            }
            if (exception != null && mInput != null) {
                mInput.delete();
            }
        }
    }

    public static File getFile(Context context, Logger logger, String url) {
        try {
            File mMorecacheout = LiveCacheFile.geCacheFile(context, "live_h5test_cache");
            //找类似 https://res12.xesimg.com/live/css 的文件
            String findStr = "com/live/";
            int index = url.indexOf(findStr);
            if (index != -1) {
                url = url.substring(index + findStr.length());
                index = url.indexOf("?");
                if (index != -1) {
                    url = url.substring(0, index);
                }
            } else {
                //找类似 https://lib01.xesimg.com/lib/MathJax/2.6 的文件
                findStr = "com/lib/MathJax/";
                index = url.indexOf(findStr);
                if (index != -1) {
                    url = url.substring(index + findStr.length());
                    index = url.indexOf("?");
                    if (index != -1) {
                        url = url.substring(0, index);
                    }
                    index = url.indexOf("/");
                    if (index != -1) {
                        String version = url.substring(0, index);
                        File out = new File(mMorecacheout, "MathJax");
                        File[] fs = out.listFiles();
                        String replacement = "MathJax-master";
                        //找最新的文件。下载只留一个。
                        if (fs != null && fs.length > 0) {
                            replacement = fs[fs.length - 1].getName();
                        }
                        url = "MathJax/" + url.replaceFirst(version, replacement);
                    } else {
                        return null;
                    }
                } else {
                    //找类似 https://lib01.xesimg.com/lib/webLog 的文件
                    findStr = "com/lib";
                    index = url.indexOf(findStr);
                    if (index != -1) {
                        url = url.substring(index + findStr.length());
                        index = url.indexOf("?");
                        if (index != -1) {
                            url = url.substring(0, index);
                        }
                    } else {
                        return null;
                    }
                }
            }
            File file = new File(mMorecacheout, url);
            return file;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("QuestionWebCache", e);
        }
        return null;
    }

    /** 删除可能有问题的文件 */
    public static File onConsoleMessage(Context context, String TAG, Logger logger, ConsoleMessage consoleMessage) {
        try {
            if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                String sourceId = consoleMessage.sourceId();
                File file = getFile(context, logger, sourceId);
                if (file != null && file.exists()) {
                    boolean delete = file.delete();
                    logger.d("onConsoleMessage:file=" + file + ",delete=" + delete);
                    return file;
                }
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("QuestionWebCache", e);
        }
        return null;
    }

    public static WebResourceResponse shouldInterceptRequest(Context context, Logger logger, String url) {
        try {
            File file = getFile(context, logger, url);
            if (file != null && file.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
                    HashMap map = new HashMap();
                    map.put("Access-Control-Allow-Origin", "*");
                    webResourceResponse.setResponseHeaders(map);
                    logger.d("shouldInterceptRequest:file=" + file);
                    return webResourceResponse;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LiveCrashReport.postCatchedException("QuestionWebCache", e);
        }
        return null;
    }
}
