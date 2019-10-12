package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.google.gson.JsonObject;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.parentsmeeting.module.browser.activity.BaseBrowserActivity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;

import org.json.JSONObject;

/**
 * @author anlina
 * @description: 教师评价h5  加载类
 * @date : 2019/9/18 17:19
 */
public class LiveFeedBackSecondPager extends LiveBasePager {

    private WebView webView;
    private LiveGetInfo mLiveGetInfo;

    private String mUrl;

    public boolean isShow = false;
    public boolean showEvaluate = false;
    FeedBackTeacherInterface feedBackTeacherInterface;
    public LiveFeedBackSecondPager(Context context) {
        super(context);
    }

    public LiveFeedBackSecondPager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    public LiveFeedBackSecondPager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }

    public LiveFeedBackSecondPager(Context context, LiveGetInfo liveGetInfo, String url) {

        super(context,null,true);
        mLiveGetInfo = liveGetInfo;
        mUrl = url;

    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_video_feed_back_second, null);
        webView = view.findViewById(R.id.wv_livevideo_feedback_second);
        webViewConfig();
        webView.addJavascriptInterface(this, "xesAppStudyCenter");
        mView = view;
        return mView;
    }

    private void webViewConfig() {
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        //wvBrowser.loadDataWithBaseURL(mCurrentWebUrl,"","text/html","utf-8",null);
        WebSettings webSetting = webView.getSettings();
        // webSetting.setSupportMultipleWindows(true); 如果有这行代码会导致页面中含有
        // target="_blank"的超链接失效
        webView.setInitialScale(0);

        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);

        webSetting.setTextZoom(100);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUserAgentString(webSetting.getUserAgentString() + " jzh");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }

    }

    class MyWebChromeClient extends WebChromeClient{

    }

    class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
        }
    }

    /**
     * 传给h5 老师信息
     */

    @JavascriptInterface
    public void postMessage(JSONObject jsonObject) {
        if (jsonObject != null) {
            String methodName = jsonObject.optString("methodName");
            if (methodName != null) {
                if (methodName.equals("setTeacherInfo")) {
                    JsonObject jsonObject1 = getTeacherInfo();
                    webView.loadUrl("javascript:transmitToWeb({type:'setTeacherInfo',data:" + jsonObject1 + "})");
                } else if (methodName.equals("close")) {
                    onClose();
                }
            }
        }

    }

    private JsonObject getTeacherInfo(){
        /**
         * msg={
         * MainTeacher:{//主讲老师
         * headUrl:"",//头像地址
         * name:"",//教师名称
         * id:""//教师id
         * },
         * CoachTeacher:{//辅导老师
         * headUrl:"",//头像地址
         * name:"",//教师名称
         * id:""//教师id
         * }
         * }
         */
        JsonObject object = new JsonObject();
        JsonObject main = new JsonObject();
        JsonObject coash = new JsonObject();
        main.addProperty("headUrl",mLiveGetInfo.getMainTeacherInfo().getTeacherImg());
        main.addProperty("name",mLiveGetInfo.getMainTeacherInfo().getTeacherName());
        main.addProperty("id",mLiveGetInfo.getMainTeacherInfo().getTeacherId());
        object.add("MainTeacher",main);
        coash.addProperty("headUrl",mLiveGetInfo.getTeacherIMG());
        coash.addProperty("name",mLiveGetInfo.getTeacherName());
        coash.addProperty("id",mLiveGetInfo.getTeacherId());
        object.add("CoachTeacher",coash);
        return object;
    }

    @JavascriptInterface
    public void close() {
        onClose();
    }


    @Override
    public boolean onUserBackPressed() {
//        webView.loadUrl(mUrl);
//        isShow = feedBackTeacherInterface.showPager();
//        return true;
        if (!isShow) {
            webView.loadUrl(mUrl);
            isShow = feedBackTeacherInterface.showPager();
            isShow = true;

        } else {
            isShow = feedBackTeacherInterface.removeView();
        }
        return isShow;

    }
    public void setFeedbackSelectInterface(FeedBackTeacherInterface feedBackTeacherInterface) {
        this.feedBackTeacherInterface = feedBackTeacherInterface;
    }

    private void onClose() {
        if (onPagerClose != null) {
            onPagerClose.onClose(this);
        }
        if (feedBackTeacherInterface != null) {
            feedBackTeacherInterface.onClose();
        }
    }
    CountDownTimer timer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            String time = String.valueOf(millisUntilFinished / 1000);
            //  onSubmitError(time + "s后退出直播间", true);
        }

        @Override
        public void onFinish() {
            if (feedBackTeacherInterface != null) {
                feedBackTeacherInterface.onClose();
            }
        }
    };
}
