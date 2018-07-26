package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.string.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件
 */
public class EnglishH5CoursewareX5Pager extends BaseWebviewX5Pager implements BaseEnglishH5CoursewarePager {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    private String url;
    private String reloadurl;
    private String nonce;
    private boolean isFinish = false;
    private String jsSubmitData = "javascript:submitData()";
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private String id;
    private String courseware_type;
    private boolean isPlayBack;
    private File cacheFile;
    private String liveId;
    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
    private String isShowRanks;
    private RelativeLayout rlLivevideoSubjectWeb;
    private boolean IS_SCIENCE;
    private int mGoldNum;
    private int mEnergyNum;
    private EnglishH5Entity englishH5Entity;

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        mEnglishH5CoursewareBll = englishH5CoursewareBll;
    }

    public EnglishH5CoursewareX5Pager(Context context, boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity, final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose, String isShowRanks, boolean IS_SCIENCE) {
        super(context);
        this.liveId = liveId;
        this.englishH5Entity = englishH5Entity;
        this.url = englishH5Entity.getUrl();
        this.isPlayBack = isPlayBack;
        this.onClose = onClose;
        this.id = id;
        this.courseware_type = courseware_type;
        this.nonce = nonce;
        this.isShowRanks = isShowRanks;
        this.IS_SCIENCE = IS_SCIENCE;
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

    @Override
    public void destroy() {
        wvSubjectWeb.destroy();
        onDestroy();
    }

    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_x5, null);
        rlLivevideoSubjectWeb = (RelativeLayout) view.findViewById(R.id.rl_livevideo_subject_web);
        return view;
    }

    @Override
    public void submitData() {
        isFinish = true;
        wvSubjectWeb.loadUrl(jsSubmitData);
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareEnd");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        umsAgentDebugInter(eventId, logHashMap.getData());
    }

    @Override
    protected void onPageFinished(WebView view, String url) {
        if (isFinish) {
            wvSubjectWeb.loadUrl(jsSubmitData);
        }
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("status", "success");
        logHashMap.put("loadurl", url);
        umsAgentDebugSys(eventId, logHashMap.getData());
    }

    @Override
    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("status", "fail");
        logHashMap.put("loadurl", url);
        logHashMap.put("msg", description);
        umsAgentDebugSys(eventId, logHashMap.getData());
    }

    @Override
    public void onBack() {
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("closetype", "clickBackButton");
        logHashMap.put("isFinish", "" + isFinish);
        umsAgentDebugSys(eventId, logHashMap.getData());
    }

    @Override
    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public void close() {
        onClose.onH5ResultClose(this);
        onBack();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        Loger.d(TAG, "shouldOverrideUrlLoading:url=" + url);
        Loger.e(TAG, "======> shouldOverrideUrlLoading:" + url);
        reloadurl = url;
        if (url.contains("baidu.com")) {
            onClose.onH5ResultClose(this);
            StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
            logHashMap.put("coursewareid", id);
            logHashMap.put("coursewaretype", courseware_type);
            logHashMap.put("closetype", "clickWebCloseButton");
            umsAgentDebugSys(eventId, logHashMap.getData());
            return true;
        }
        if (url.contains("https://submit.com")) {
            if (mEnglishH5CoursewareBll != null) {
                mEnglishH5CoursewareBll.onSubmit();
                return true;
            }
        }


        if (url.contains(TeamPkBll.TEAMPK_URL_FIFTE)) {
            try {
                int startIndex = url.indexOf("goldNum=") + "goldNum=".length();
                if (startIndex != -1) {
                    String teamStr = url.substring(startIndex, url.length());
                    int endIndex = teamStr.indexOf("&");
                    String goldNUmStr = teamStr.substring(0, endIndex);
                    if (!TextUtils.isEmpty(goldNUmStr)) {
                        mGoldNum = Integer.parseInt(goldNUmStr.trim());
                    }
                    Loger.e(TAG, "======> shouldOverrideUrlLoading: mGoldNum=" + mGoldNum);
                }
                int satrIndex2 = url.indexOf("energyNum=") + "energyNum=".length();
                if (satrIndex2 != -1) {
                    String tempStr2 = url.substring(satrIndex2);
                    String energyNumStr = null;
                    if (tempStr2.contains("&")) {
                        energyNumStr = tempStr2.substring(0, tempStr2.indexOf("&"));
                    } else {
                        energyNumStr = tempStr2.substring(0, tempStr2.length());
                    }
                    if (!TextUtils.isEmpty(energyNumStr)) {
                        mEnergyNum = Integer.parseInt(energyNumStr.trim());
                    }
                    Loger.e(TAG, "======> shouldOverrideUrlLoading: mEnergyNum=" + mEnergyNum);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        loadUrl += "&isArts=" + (IS_SCIENCE ? "0" : "1");
        if (!StringUtils.isEmpty(nonce)) {
            loadUrl += "&nonce=" + nonce;
        }
        loadUrl += "&isTowall=" + isShowRanks;
        Loger.i(TAG, "initData:loadUrl=" + loadUrl);
        loadUrl += "&isShowTeamPk=" + (LiveBll.isAllowTeamPk ? "1" : "0");
        loadUrl(loadUrl);
        Loger.e(TAG, "======> loadUrl:" + loadUrl);
        reloadurl = loadUrl;
        mGoldNum = -1;
        mEnergyNum = -1;
        mView.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                newWebView();
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
                errorView.setVisibility(View.GONE);
//                wvSubjectWeb.setVisibility(View.VISIBLE);

                newWebView();

                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                loadView.setVisibility(View.VISIBLE);
                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                ((AnimationDrawable) ivLoading.getBackground()).start();
                loadUrl(reloadurl);
            }
        });

        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent.H5_TYPE_COURSE, id);
                if (mEnglishH5CoursewareBll != null) {
                    event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
                    mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
                }
                EventBus.getDefault().post(event);
                mGoldNum = -1;
                mEnergyNum = -1;
            }
        });

    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    /**
     * 设置webview透明
     *
     * @param color
     */
    @Override
    public void setWebBackgroundColor(int color) {
        wvSubjectWeb.setBackgroundColor(color);
    }

    private void newWebView() {
        rlLivevideoSubjectWeb.removeView(wvSubjectWeb);
        wvSubjectWeb = (WebView) View.inflate(mContext, R.layout.page_livevideo_h5_courseware_cacheweb, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlLivevideoSubjectWeb.addView(wvSubjectWeb, 0, lp);

        addJavascriptInterface();
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
                return super.shouldInterceptRequest(view, s);
            }
        });
    }

    public String getId() {
        return id;
    }

}
