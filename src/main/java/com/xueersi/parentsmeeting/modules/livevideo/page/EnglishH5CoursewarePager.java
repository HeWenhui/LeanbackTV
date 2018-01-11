package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件
 */
public class EnglishH5CoursewarePager extends BaseWebviewPager {
    String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    String url;
    String reloadurl;
    String nonce;
    public boolean isFinish = false;
    String jsSubmitData = "javascript:submitData()";
    EnglishH5CoursewareBll.OnH5ResultClose onClose;
    String id;
    String courseware_type;
    boolean isPlayBack;
    File cacheFile;
    String liveId;
    LiveAndBackDebug liveAndBackDebug;
    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
    private String isShowRanks;

    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        mEnglishH5CoursewareBll = englishH5CoursewareBll;
    }

    public EnglishH5CoursewarePager(Context context, boolean isPlayBack, String liveId, String url, String id, final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose, LiveAndBackDebug liveAndBackDebug, String isShowRanks) {
        super(context);
        this.liveId = liveId;
        this.url = url;
        this.isPlayBack = isPlayBack;
        this.onClose = onClose;
        this.liveAndBackDebug = liveAndBackDebug;
        this.id = id;
        this.courseware_type = courseware_type;
        this.nonce = nonce;
        this.isShowRanks=isShowRanks;
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        initData();
    }

    @Override
    public void onPause() {
        wvSubjectWeb.onPause();
    }

    public void destroy() {
        wvSubjectWeb.destroy();
    }

    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

    public String getUrl() {
        return url;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware, null);
        return view;
    }

    public void submitData() {
        isFinish = true;
        wvSubjectWeb.loadUrl(jsSubmitData);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareEnd");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        liveAndBackDebug.umsAgentDebug2(eventId, mData);
    }

    @Override
    protected void onPageFinished(WebView view, String url) {
        if (isFinish) {
            wvSubjectWeb.loadUrl(jsSubmitData);
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareDidLoad");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        mData.put("status", "success");
        mData.put("loadurl", url);
        liveAndBackDebug.umsAgentDebug(eventId, mData);
    }

    @Override
    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareDidLoad");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        mData.put("status", "fail");
        mData.put("loadurl", url);
        mData.put("msg", description);
        liveAndBackDebug.umsAgentDebug(eventId, mData);
    }

    public void onBack() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareClose");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        mData.put("closetype", "clickBackButton");
        mData.put("isFinish", "" + isFinish);
        liveAndBackDebug.umsAgentDebug(eventId, mData);
    }

    public void close() {
        onClose.onH5ResultClose();
        onBack();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        Loger.d(TAG, "shouldOverrideUrlLoading:url=" + url);
        reloadurl = url;
        if (url.contains("baidu.com")) {
            onClose.onH5ResultClose();
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "coursewareClose");
            mData.put("coursewareid", id);
            mData.put("coursewaretype", courseware_type);
            mData.put("closetype", "clickWebCloseButton");
            liveAndBackDebug.umsAgentDebug(eventId, mData);
            return true;
        }
        if(url.contains("https://submit.com")){
            if(mEnglishH5CoursewareBll!=null){
                mEnglishH5CoursewareBll.onSubmit();
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void initData() {
        super.initData();
        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
//        webSetting.setUseWideViewPort(false);
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

        String loadUrl = url + "?t=" + System.currentTimeMillis();
        if (isPlayBack) {
            loadUrl += "&isPlayBack=1";
        }
        loadUrl += "&isArts=" + (LiveVideoConfig.IS_SCIENCE ? "0" : "1");
        if (!StringUtils.isEmpty(nonce)) {
            loadUrl += "&nonce=" + nonce;
        }
        loadUrl+="&isTowall="+isShowRanks;
        Loger.i(TAG, "initData:loadUrl=" + loadUrl);
        loadUrl(loadUrl);
        reloadurl = loadUrl;
        mView.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loadUrl(reloadurl);
                v.setVisibility(View.GONE);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        });
        mView.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                wvSubjectWeb.reload();
                errorView.setVisibility(View.GONE);
                wvSubjectWeb.setVisibility(View.VISIBLE);
                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                loadView.setVisibility(View.VISIBLE);
                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                ((AnimationDrawable) ivLoading.getBackground()).start();
                loadUrl(reloadurl);
            }
        });
    }

}
