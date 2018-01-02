package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.xesalib.utils.file.FileUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;

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
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import ren.yale.android.cachewebviewlib.CachePreLoadService;
import ren.yale.android.cachewebviewlib.CacheWebView;

/**
 * 英语课件缓存
 * Created by linyuqiang on 2017/12/28.
 */
public class EnglishH5Cache {
    String TAG = "EnglishH5Cache";
    String eventId = LiveVideoConfig.LIVE_H5_CACHE;
    Context context;
    LiveBll liveBll;
    String liveId;
    File cacheFile;
    RelativeLayout bottomContent;
    CacheReceiver cacheReceiver;
    /** 网络类型 */
    private int netWorkType;

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

    public void getCourseWareUrl() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        String today = dateFormat.format(date);
        final File cacheDir = new File(cacheFile, today);
        CacheWebView.getCacheConfig().init(context, cacheDir.getPath(), 1024 * 1024 * 100, 1024 * 1024 * 10)
                .enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间

        liveBll.getCourseWareUrl(new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                if (responseEntity.getJsonObject() instanceof JSONArray) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        File files[] = cacheFile.listFiles();
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                File delectFile = files[i];
                                if (!delectFile.getPath().equals(cacheDir.getPath())) {
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
                        File file = new File(cacheDir, MD5.md5(play_url));
                        if (!file.exists()) {
                            urls.add(play_url);
                        }
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
                        return;
                    }
                    final ArrayList<String> urls2 = new ArrayList<>();
                    urls2.addAll(urls);
                    IntentFilter intentFilter = new IntentFilter(CachePreLoadService.URL_CACHE_ACTION);
                    cacheReceiver = new CacheReceiver(urls2, cacheDir);
                    context.registerReceiver(cacheReceiver, intentFilter);
//                    File cacheFile = new File(this.getCacheDir(), "cache_path_name");
                    for (int i = 0; i < urls.size(); i++) {
                        final int index = i;
                        bottomContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                loadUrl(index);
                                String url = urls.get(index);
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
                                    CacheWebView.servicePreload(context, url);
                                }
//                                CacheWebView.cacheWebView(context).loadUrl(urls.get(index));
                            }
                        }, i * 5000);
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
                    context.stopService(intent);
                    Map<String, String> mData = new HashMap<>();
                    mData.put("liveid", liveId);
                    mData.put("times", "2");
                    mData.put("error", "" + errorUrls.size());
                    mData.put("total", "" + total);
                    liveBll.umsAgentDebug(eventId, mData);
                } else {
                    if (errorUrls.isEmpty()) {
                        context.unregisterReceiver(this);
                        Intent intent = new Intent(context, CachePreLoadService.class);
                        context.stopService(intent);
                        cacheReceiver = null;
                        Map<String, String> mData = new HashMap<>();
                        mData.put("liveid", liveId);
                        mData.put("times", "1");
                        mData.put("error", "0");
                        mData.put("total", "" + total);
                        liveBll.umsAgentDebug(eventId, mData);
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
                final int index = i;
                bottomContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                                loadUrl(index);
                        String url = errorUrls2.get(index);
                        int netWorkType = NetWorkHelper.getNetWorkState(context);
                        boolean load = true;
                        if (netWorkType == NetWorkHelper.NO_NETWORK) {
                            load = false;
                        } else if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                            load = false;
                        }
                        if (load) {
                            CacheWebView.servicePreload(context, url);
                        }
//                                CacheWebView.cacheWebView(context).loadUrl(urls.get(index));
                    }
                }, i * 5000);
            }
        }
    }

    public void destory() {
        Intent intent = new Intent(context, CachePreLoadService.class);
        context.stopService(intent);
        if (cacheReceiver != null) {
            context.unregisterReceiver(cacheReceiver);
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
//    private void loadUrl(int i) {
//        String url = urls.get(i);
//        final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_h5_courseware_web, bottomContent, false);
//        final WebView webView = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
//        webView.setWebViewClient(new MyWebViewClient());
//        WebSettings webSetting = webView.getSettings();
//        File file = new File(cacheFile, liveId);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
//        webSetting.setDatabasePath(cacheFile.getPath());
//        //设置 应用 缓存目录
//        webSetting.setAppCachePath(cacheFile.getPath());
//        //开启 DOM 存储功能
//        webSetting.setDomStorageEnabled(true);
//        //开启 数据库 存储功能
//        webSetting.setDatabaseEnabled(true);
//        //开启 应用缓存 功能
//        webSetting.setAppCacheEnabled(true);
//
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setBuiltInZoomControls(false);
//
//        view.setVisibility(View.GONE);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        bottomContent.addView(view, lp);
//        webView.loadUrl(url);
//        Loger.i(TAG, "loadUrl:url=" + url);
//        bottomContent.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                webView.destroy();
//                bottomContent.removeView(view);
//            }
//        }, 10000);
//    }

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
