package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.LogerTag;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewPager;
import com.xueersi.xesalib.utils.file.FileUtils;
import com.xueersi.xesalib.utils.log.Loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import ren.yale.android.cachewebviewlib.CacheWebView;

/**
 * 英语课件缓存
 * Created by linyuqiang on 2017/12/28.
 */
public class EnglishH5Cache {
    String TAG = "EnglishH5Cache";
    Context context;
    LiveBll liveBll;
    String liveId;
    ArrayList<String> urls = new ArrayList<>();
    File cacheFile;
    RelativeLayout bottomContent;

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
        liveBll.getCourseWareUrl(new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                File file = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
                File file2 = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/org.chromium.android_webview");
                FileUtils.copyDir(file, file2);
                final JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                Loger.d(TAG, "getCourseWareUrl:onPmSuccess:jsonObject=" + jsonObject);
                try {
                    JSONObject liveIdObj = jsonObject.getJSONObject(liveId);
//                    JSONArray urlArray = liveIdObj.getJSONArray("url");
//                    for (int i = 0; i < urlArray.length(); i++) {
//                        String play_url = urlArray.getString(i);
//                        urls.add(play_url);
//                    }
                    JSONArray infoArray = liveIdObj.getJSONArray("infos");
                    for (int i = 0; i < infoArray.length(); i++) {
                        JSONObject infoObj = infoArray.getJSONObject(i);
                        String id = infoObj.getString("id");
                        String courseware_type = infoObj.getString("type");
                        String play_url = "https://live.xueersi.com/Live/coursewareH5/" + liveId + "/" + id + "/" + courseware_type
                                + "/123456";
                        Loger.d(TAG, "getCourseWareUrl:onPmSuccess:play_url=" + play_url);
                        urls.add(play_url);
                    }
//                    File cacheFile = new File(this.getCacheDir(), "cache_path_name");

                    CacheWebView.getCacheConfig().init(context, file.getPath(), 1024 * 1024 * 100, 1024 * 1024 * 10)
                            .enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间
                    for (int i = 0; i < urls.size(); i++) {
                        final int index = i;
                        bottomContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                loadUrl(index);
                                CacheWebView.servicePreload(context, urls.get(index));
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

    private void loadUrl(int i) {
        String url = urls.get(i);
        final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_h5_courseware_web, bottomContent, false);
        final WebView webView = (WebView) view.findViewById(R.id.wv_livevideo_subject_web);
        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSetting = webView.getSettings();
        File file = new File(cacheFile, liveId);
        if (!file.exists()) {
            file.mkdirs();
        }
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setDatabasePath(cacheFile.getPath());
        //设置 应用 缓存目录
        webSetting.setAppCachePath(cacheFile.getPath());
        //开启 DOM 存储功能
        webSetting.setDomStorageEnabled(true);
        //开启 数据库 存储功能
        webSetting.setDatabaseEnabled(true);
        //开启 应用缓存 功能
        webSetting.setAppCacheEnabled(true);

        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBuiltInZoomControls(false);

        view.setVisibility(View.GONE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(view, lp);
        webView.loadUrl(url);
        Loger.i(TAG, "loadUrl:url=" + url);
        bottomContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.destroy();
                bottomContent.removeView(view);
            }
        }, 10000);
    }

    public class MyWebViewClient extends WebViewClient {
        String failingUrl;

        @Override
        public void onPageFinished(final WebView view, String url) {
            File file2 = new File(context.getCacheDir(), "org.chromium.android_webview");
            Loger.i(TAG, "onPageFinished:url=" + url + ",size=" + FileUtils.getDirSize(file2));
//            bottomContent.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    View view1 = (View) view.getParent();
//                    bottomContent.removeView(view1);
//                }
//            }, 3000);
            view.destroy();
            View view1 = (View) view.getParent();
            bottomContent.removeView(view1);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            Loger.i(TAG, "onPageStarted");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Loger.d(context, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
                    "&&," + description, true);
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//            Loger.i(TAG, "shouldInterceptRequest:url=" + url);
//            return super.shouldInterceptRequest(view, url);
//        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
