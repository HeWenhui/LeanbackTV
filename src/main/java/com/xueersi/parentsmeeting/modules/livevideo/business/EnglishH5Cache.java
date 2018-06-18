package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.widget.RelativeLayout;

import com.xueersi.common.config.AppConfig;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreCache;
import com.xueersi.parentsmeeting.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.util.ZipProg;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.lib.framework.utils.NetWorkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.common.util.MD5;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import ren.yale.android.cachewebviewlib.CachePreLoadService;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;

/**
 * 英语课件缓存
 * Created by linyuqiang on 2017/12/28.
 */
public class EnglishH5Cache implements EnglishH5CacheAction {
    String TAG = "EnglishH5Cache";
    String eventId = LiveVideoConfig.LIVE_H5_CACHE;
    Context context;
    LiveBll liveBll;
    String liveId;
    File cacheFile;
    RelativeLayout bottomContent;
    ArrayList<CacheWebView> cacheWebViews = new ArrayList<>();
    CacheReceiver cacheReceiver;
    /** 网络类型 */
    private int netWorkType;
    boolean useService = true;
    boolean isStart = true;
    private List<MoreCache> mList;
    private File mMorecachein;
    private File mMorecacheout;
    private ArrayList<String> mUrls;
    private int count = 0;

    public EnglishH5Cache(Context context, LiveBll liveBll, String liveId) {
        this.context = context;
        Activity activity = (Activity) context;
        bottomContent = (RelativeLayout) activity.findViewById(R.id.rl_course_video_live_question_content);
        this.liveBll = liveBll;
        this.liveId = liveId;
        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
//        cacheFile = new File(context.getCacheDir(), "cache/webviewCache");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Loger.d(TAG, "handleMessage:cacheReceiver=" + (cacheReceiver == null));
            if (cacheReceiver == null) {
                return;
            }
            if (msg.what == 1) {
                String url = (String) msg.obj;
                int netWorkType = NetWorkHelper.getNetWorkState(context);
                boolean load = true;
                if (netWorkType == NetWorkHelper.NO_NETWORK) {
                    cacheReceiver.error(url);
                    load = false;
                } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                    cacheReceiver.error(url);
                    load = false;
                }
                if (load) {
                    if (useService) {
                        CacheWebView.servicePreload(context, url);
                    } else {
                        loadUrl(url);
                    }
                }
            }
        }
    };

    @Override
    public void getCourseWareUrl() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        boolean exists = todayLiveCacheDir.exists();
        boolean mkdirs = false;
        if (!todayLiveCacheDir.exists()) {
            mkdirs = todayLiveCacheDir.mkdirs();
        }
        Loger.d(TAG, "getCourseWareUrl:exists=" + exists + ",mkdirs=" + mkdirs);
        CacheWebView.getCacheConfig().init(context, todayLiveCacheDir.getPath(), 1024 * 1024 * 100, 1024 * 1024 * 10)
                .enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间
        //替换x5浏览器，缓存mp3经常出问题
//        CacheExtensionConfig.addGlobalExtension("mp3");
//        CacheExtensionConfig.addGlobalExtension("WAV");
//        CacheExtensionConfig.removeNoCacheExtension("mp3");
        liveBll.getCourseWareUrl(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                if (responseEntity.getJsonObject() instanceof JSONArray) {
                    if (context instanceof WebViewRequest) {
                        WebViewRequest webViewRequest = (WebViewRequest) context;
                        webViewRequest.onWebViewEnd();
                    }
                    return;
                }
                if (!isStart) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        File files[] = cacheFile.listFiles();
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                File delectFile = files[i];
                                if (!delectFile.getPath().equals(todayCacheDir.getPath())) {
                                    if (delectFile.isDirectory()) {
                                        FileUtils.deleteDir(delectFile);
                                    } else {
                                        FileUtils.deleteFile(delectFile);
                                    }
                                }
                            }
                        }
                    }
                }.start();
                final JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                Loger.d(TAG, "getCourseWareUrl:onPmSuccess:jsonObject=" + jsonObject);
                try {
                    JSONObject liveIdObj = jsonObject.getJSONObject(liveId);
                    JSONArray urlArray = liveIdObj.getJSONArray("url");
                    final ArrayList<String> urls = new ArrayList<>();
                    for (int i = 0; i < urlArray.length(); i++) {
                        String play_url = urlArray.getString(i);
                        File file = new File(todayLiveCacheDir, MD5.md5(play_url));
                        int index = play_url.indexOf("/index.html");
                        String startUrl = play_url.substring(0, index);
                        if (!file.exists()) {
                            urls.add(play_url);
                        }
//                        urls.add(play_url);
                    }
                    Loger.d(TAG, "getCourseWareUrl:onPmSuccess:urlArray=" + urlArray.length() + ",urls=" + urls.size());
                    JSONArray infoArray = liveIdObj.getJSONArray("infos");
                    for (int i = 0; i < infoArray.length(); i++) {
                        JSONObject infoObj = infoArray.getJSONObject(i);
                        String id = infoObj.getString("id");
                        String courseware_type = infoObj.getString("type");
                        String play_url = "https://live.xueersi.com/Live/coursewareH5/" + liveId + "/" + id + "/" + courseware_type
                                + "/123456";
                        Loger.d(TAG, "getCourseWareUrl:onPmSuccess:play_url=" + play_url);
//                        urls.add(play_url);
                    }
                    if (urls.isEmpty()) {
                        if (context instanceof WebViewRequest) {
                            WebViewRequest webViewRequest = (WebViewRequest) context;
                            webViewRequest.onWebViewEnd();
                        }
                        return;
                    }
                    final ArrayList<String> urls2 = new ArrayList<>();
                    urls2.addAll(urls);
                    IntentFilter intentFilter = new IntentFilter(CachePreLoadService.URL_CACHE_ACTION);
                    cacheReceiver = new CacheReceiver(urls2, todayLiveCacheDir);
                    context.registerReceiver(cacheReceiver, intentFilter);
//                    File cacheFile = new File(this.getCacheDir(), "cache_path_name");
                    for (int i = 0; i < urls.size(); i++) {
                        final String url = urls.get(i);
                        Message msg = handler.obtainMessage(1);
                        msg.what = 1;
                        msg.obj = url;
                        handler.sendMessageDelayed(msg, i * 5000);
                    }
                } catch (JSONException e) {
                    Loger.e(TAG, "onPmSuccess", e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                Loger.e(TAG, "getCourseWareUrl:onFailure:e=" + e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.e(TAG, "getCourseWareUrl:onPmError:e=" + responseEntity.getErrorMsg());
            }
        });
    }

    public void start() {
        isStart = true;
        Loger.d(TAG, "start");
        getCourseWareUrl();
    }

    public void stop() {
        isStart = false;
        Loger.d(TAG, "stop");
        handler.removeMessages(1);
        if (cacheReceiver != null) {
            context.unregisterReceiver(cacheReceiver);
            cacheReceiver = null;
        }
        if (useService) {
            Intent intent = new Intent(context, CachePreLoadService.class);
            intent.setPackage(AppConfig.APPLICATION_ID);
            context.stopService(intent);
        } else {
            for (int i = 0; i < cacheWebViews.size(); i++) {
                WebView view = cacheWebViews.get(i);
                view.destroy();
            }
        }
    }

    class CacheReceiver extends BroadcastReceiver {
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> errorUrls = new ArrayList<>();
        ArrayList<String> successUrls = new ArrayList<>();
        File cacheDir;
        boolean isRetry = false;
        int total;

        public CacheReceiver(ArrayList<String> urls, File cacheDir) {
            this.urls = urls;
            this.cacheDir = cacheDir;
            total = urls.size();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status", 0);
            String url = intent.getStringExtra("url");
            long time = intent.getLongExtra("time", -1);
            if (status == 0) {
                success(url);
                ifOnEnd();
            } else {
                error(url);
            }
            Loger.d(TAG, "onReceive:status=" + status + ",time=" + time + ",url=" + url);
        }

        private void success(String url) {
            urls.remove(url);
            successUrls.add(url);
            File file = new File(cacheDir, MD5.md5(url));
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Loger.d(TAG, "onReceive:success:urls=" + urls.size() + ",errorUrls=" + errorUrls.size());
        }

        private void error(String url) {
            urls.remove(url);
            errorUrls.add(url);
            Loger.d(TAG, "onReceive:error:urls=" + urls.size() + ",errorUrls=" + errorUrls.size());
            ifOnEnd();
        }

        private void ifOnEnd() {
            if (urls.isEmpty()) {
                Loger.d(TAG, "onReceive:ifOnEnd:errorUrls=" + errorUrls.size() + ",successUrls=" + successUrls.size() + ",isRetry=" + isRetry);
                if (isRetry) {
                    if (cacheReceiver != null) {
                        context.unregisterReceiver(cacheReceiver);
                        cacheReceiver = null;
                    }
                    Intent intent = new Intent(context, CachePreLoadService.class);
                    intent.setPackage(AppConfig.APPLICATION_ID);
                    context.stopService(intent);
                    Map<String, String> mData = new HashMap<>();
                    mData.put("liveid", liveId);
                    mData.put("times", "2");
                    mData.put("error", "" + errorUrls.size());
                    mData.put("total", "" + total);
                    liveBll.umsAgentDebugSys(eventId, mData);
                    if (context instanceof WebViewRequest) {
                        WebViewRequest webViewRequest = (WebViewRequest) context;
                        webViewRequest.onWebViewEnd();
                    }
                } else {
                    if (errorUrls.isEmpty()) {
                        context.unregisterReceiver(this);
                        Intent intent = new Intent(context, CachePreLoadService.class);
                        intent.setPackage(AppConfig.APPLICATION_ID);
                        context.stopService(intent);
                        cacheReceiver = null;
                        Map<String, String> mData = new HashMap<>();
                        mData.put("liveid", liveId);
                        mData.put("times", "1");
                        mData.put("error", "0");
                        mData.put("total", "" + total);
                        liveBll.umsAgentDebugSys(eventId, mData);
                        if (context instanceof WebViewRequest) {
                            WebViewRequest webViewRequest = (WebViewRequest) context;
                            webViewRequest.onWebViewEnd();
                        }
                    } else {
                        if (netWorkType == NetWorkHelper.NO_NETWORK) {
                            return;
                        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                            return;
                        }
                        retry();
                    }
                }
            }
        }

        private void retry() {
            isRetry = true;
            Loger.d(TAG, "retry");
            final ArrayList<String> errorUrls2 = new ArrayList<>();
            errorUrls2.addAll(errorUrls);
            errorUrls.clear();
            int size = errorUrls2.size();
            for (int i = 0; i < size; i++) {
                String url = errorUrls2.get(i);
                Message msg = handler.obtainMessage(1);
                msg.what = 1;
                msg.obj = url;
                handler.sendMessageDelayed(msg, i * 5000);
            }
        }
    }

    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            return;
        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
            return;
        }
        if (cacheReceiver != null) {
            Loger.d(TAG, "onNetWorkChange:urls=" + cacheReceiver.urls.size() + ",errorUrls=" + cacheReceiver.errorUrls.size());
            if (cacheReceiver.urls.isEmpty() && !cacheReceiver.errorUrls.isEmpty()) {
                cacheReceiver.retry();
            }
        }
    }

    private void loadUrl(final String url) {
        final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_h5_courseware_cacheweb, bottomContent, false);
        final CacheWebView cacheWebView = (CacheWebView) view.findViewById(R.id.wv_livevideo_subject_web);
        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(view, lp);
        cacheWebView.setWebViewClient(new WebViewClient() {
            long before = System.currentTimeMillis();
            String failingUrl;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                int status = failingUrl == null ? 0 : -3;
                Intent intent1 = new Intent(CachePreLoadService.URL_CACHE_ACTION);
                intent1.putExtra("url", url);
                intent1.putExtra("status", status);
                intent1.putExtra("time", (System.currentTimeMillis() - before));
                context.sendBroadcast(intent1);
                cacheWebViews.remove(cacheWebView);
                bottomContent.removeView(view);
                view.destroy();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                this.failingUrl = failingUrl;
            }
        });
        cacheWebView.loadUrl(url);
        Loger.i(TAG, "loadUrl:url=" + url);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cacheWebViews.contains(cacheWebView)) {
                    cacheWebView.destroy();
                    cacheWebViews.remove(cacheWebView);
                    bottomContent.removeView(view);
                    Intent intent1 = new Intent(CachePreLoadService.URL_CACHE_ACTION);
                    intent1.putExtra("url", url);
                    intent1.putExtra("status", -4);
                    long t = 10000;
                    intent1.putExtra("time", t);
                    context.sendBroadcast(intent1);
                }
            }
        }, 10000);
    }

//    public class MyWebViewClient extends WebViewClient {
//        String failingUrl;
//
//        @Override
//        public void onPageFinished(final WebView view, String url) {
//            File file2 = new File(context.getCacheDir(), "org.chromium.android_webview");
//            Loger.i(TAG, "onPageFinished:url=" + url + ",size=" + FileUtils.getDirSize(file2));
////            bottomContent.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    View view1 = (View) view.getParent();
////                    bottomContent.removeView(view1);
////                }
////            }, 3000);
//            view.destroy();
//            View view1 = (View) view.getParent();
//            bottomContent.removeView(view1);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
////            Loger.i(TAG, "onPageStarted");
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            Loger.d(context, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
//                    "&&," + description, true);
//        }
//
////        @Override
////        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
////            Loger.i(TAG, "shouldInterceptRequest:url=" + url);
////            return super.shouldInterceptRequest(view, url);
////        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
}
