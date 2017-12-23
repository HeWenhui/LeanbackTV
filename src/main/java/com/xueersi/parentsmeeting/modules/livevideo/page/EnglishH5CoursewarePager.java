package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.string.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件
 */
public class EnglishH5CoursewarePager extends BaseWebviewPager {
    String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    String url;
    String nonce;
    public boolean isFinish = false;
    String jsSubmitData = "javascript:submitData()";
    EnglishH5CoursewareBll.OnH5ResultClose onClose;
    String id;
    String courseware_type;
    boolean isPlayBack;

    public EnglishH5CoursewarePager(Context context, boolean isPlayBack, EnglishH5CoursewareBll.OnH5ResultClose onClose, String url, String id, final String courseware_type, String nonce) {
        super(context);
        this.url = url;
        this.isPlayBack = isPlayBack;
        this.onClose = onClose;
        this.id = id;
        this.courseware_type = courseware_type;
        this.nonce = nonce;
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
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
        final View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware, null);
        return view;
    }

    public void submitData() {
        isFinish = true;
        wvSubjectWeb.loadUrl(jsSubmitData);
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareEnd");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        onClose.umsAgentDebug2(eventId, mData);
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
        onClose.umsAgentDebug(eventId, mData);
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
        onClose.umsAgentDebug(eventId, mData);
    }

    public void onBack() {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "coursewareClose");
        mData.put("coursewareid", id);
        mData.put("coursewaretype", courseware_type);
        mData.put("closetype", "clickBackButton");
        mData.put("isFinish", "" + isFinish);
        onClose.umsAgentDebug(eventId, mData);
    }

    public void close() {
        onClose.onH5ResultClose();
        onBack();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        if (url.contains("baidu.com")) {
            onClose.onH5ResultClose();
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "coursewareClose");
            mData.put("coursewareid", id);
            mData.put("coursewaretype", courseware_type);
            mData.put("closetype", "clickWebCloseButton");
            onClose.umsAgentDebug(eventId, mData);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void initData() {
        super.initData();
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        String loadUrl = url + "?t=" + System.currentTimeMillis();
        if (isPlayBack) {
            loadUrl += "&isPlayBack=1";
        }
        loadUrl += "&isArts=" + (LiveVideoConfig.IS_SCIENCE ? "0" : "1");
        if (!StringUtils.isEmpty(nonce)) {
            loadUrl += "&nonce=" + nonce;
        }
        Loger.i(TAG, "initData:loadUrl=" + loadUrl);
        loadUrl(loadUrl);
    }

}
