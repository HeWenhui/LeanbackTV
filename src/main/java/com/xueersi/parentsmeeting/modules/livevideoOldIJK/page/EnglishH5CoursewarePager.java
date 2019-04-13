//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;
//
//import android.content.Context;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.view.View;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.xueersi.parentsmeeting.base.BasePager;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPkBll;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
//import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
//import com.xueersi.xesalib.utils.log.Loger;
//import com.xueersi.xesalib.utils.string.StringUtils;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.File;
//
///**
// * Created by linyuqiang on 2017/3/25.
// * h5 课件
// */
//public class EnglishH5CoursewarePager extends BaseWebviewPager implements BaseEnglishH5CoursewarePager {
//    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
//    private String url;
//    private String reloadurl;
//    private String nonce;
//    private boolean isFinish = false;
//    private String jsSubmitData = "javascript:submitData()";
//    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
//    private String id;
//    private String courseware_type;
//    private boolean isPlayBack;
//    private File cacheFile;
//    private String liveId;
//    private LiveAndBackDebug liveAndBackDebug;
//    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
//    private String isShowRanks;
//    private RelativeLayout rlLivevideoSubjectWeb;
//    private boolean IS_SCIENCE;
//    private int mGoldNum;
//    private int mEnergyNum;
//
//    @Override
//    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
//        mEnglishH5CoursewareBll = englishH5CoursewareBll;
//    }
//
//    public EnglishH5CoursewarePager(Context context, boolean isPlayBack, String liveId, String url, String id, final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose, LiveAndBackDebug liveAndBackDebug, String isShowRanks, boolean IS_SCIENCE) {
//        super(context);
//        this.liveId = liveId;
//        this.url = url;
//        this.isPlayBack = isPlayBack;
//        this.onClose = onClose;
//        this.liveAndBackDebug = liveAndBackDebug;
//        this.id = id;
//        this.courseware_type = courseware_type;
//        this.nonce = nonce;
//        this.isShowRanks = isShowRanks;
//        this.IS_SCIENCE = IS_SCIENCE;
//        initWebView();
//        setErrorTip("H5课件加载失败，请重试");
//        setLoadTip("H5课件正在加载，请稍候");
////        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
//        if (!cacheFile.exists()) {
//            cacheFile.mkdirs();
//        }
//        initData();
//    }
//
//    @Override
//    public void onPause() {
//        wvSubjectWeb.onPause();
//    }
//
//    @Override
//    public void destroy() {
//        wvSubjectWeb.destroy();
//    }
//
//    @Override
//    public void onResume() {
//        wvSubjectWeb.onResume();
//    }
//
//    @Override
//    public String getUrl() {
//        return url;
//    }
//
//    @Override
//    public View initView() {
//        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware, null);
//        rlLivevideoSubjectWeb = (RelativeLayout) view.findViewById(R.id.rl_livevideo_subject_web);
//        return view;
//    }
//
//    @Override
//    public void submitData() {
//        isFinish = true;
//        wvSubjectWeb.loadUrl(jsSubmitData);
//        StableLogHashMap logHashMap = new StableLogHashMap("coursewareEnd");
//        logHashMap.put("coursewareid", id);
//        logHashMap.put("coursewaretype", courseware_type);
//        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
//    }
//
//    @Override
//    protected void onPageFinished(WebView view, String url) {
//        if (isFinish) {
//            wvSubjectWeb.loadUrl(jsSubmitData);
//        }
//        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
//        logHashMap.put("coursewareid", id);
//        logHashMap.put("coursewaretype", courseware_type);
//        logHashMap.put("status", "success");
//        logHashMap.put("loadurl", url);
//        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//    }
//
//    @Override
//    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//        super.onReceivedError(view, errorCode, description, failingUrl);
//        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
//        logHashMap.put("coursewareid", id);
//        logHashMap.put("coursewaretype", courseware_type);
//        logHashMap.put("status", "fail");
//        logHashMap.put("loadurl", url);
//        logHashMap.put("msg", description);
//        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//    }
//
//    @Override
//    public void onBack() {
//        StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
//        logHashMap.put("coursewareid", id);
//        logHashMap.put("coursewaretype", courseware_type);
//        logHashMap.put("closetype", "clickBackButton");
//        logHashMap.put("isFinish", "" + isFinish);
//        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//    }
//
//    @Override
//    public boolean isFinish() {
//        return isFinish;
//    }
//
//    @Override
//    public void close() {
//        onClose.onH5ResultClose(this);
//        onBack();
//    }
//
//    @Override
//    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
//        //      if ("http://baidu.com/".equals(url)) {
//        logger.d( "shouldOverrideUrlLoading:url=" + url);
//        Loger.e("EnglishH5CoursewarePager", "======> shouldOverrideUrlLoading:" + url);
//        reloadurl = url;
//        if (url.contains("baidu.com")) {
//            onClose.onH5ResultClose(this);
//            StableLogHashMap logHashMap = new StableLogHashMap("coursewareClose");
//            logHashMap.put("coursewareid", id);
//            logHashMap.put("coursewaretype", courseware_type);
//            logHashMap.put("closetype", "clickWebCloseButton");
//            liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
//            return true;
//        }
//        if (url.contains("https://submit.com")) {
//            if (mEnglishH5CoursewareBll != null) {
//                mEnglishH5CoursewareBll.onSubmit();
//                return true;
//            }
//        }
//
//
//        if (url.contains(TeamPkBll.TEAMPK_URL_FIFTE)) {
//            try {
//                int startIndex = url.indexOf("goldNum=") + "goldNum=".length();
//                if (startIndex != -1) {
//                    String teamStr = url.substring(startIndex, url.length());
//                    int endIndex = teamStr.indexOf("&");
//                    String goldNUmStr = teamStr.substring(0, endIndex);
//                    if (!TextUtils.isEmpty(goldNUmStr)) {
//                        mGoldNum = Integer.parseInt(goldNUmStr.trim());
//                    }
//                    Loger.e("EnglishH5Courseware", "======> shouldOverrideUrlLoading: mGoldNum=" + mGoldNum);
//                }
//                int satrIndex2 = url.indexOf("energyNum=") + "energyNum=".length();
//                if (satrIndex2 != -1) {
//                    String tempStr2 = url.substring(satrIndex2);
//                    String energyNumStr = null;
//                    if (tempStr2.contains("&")) {
//                        energyNumStr = tempStr2.substring(0, tempStr2.indexOf("&"));
//                    } else {
//                        energyNumStr = tempStr2.substring(0, tempStr2.length());
//                    }
//                    if (!TextUtils.isEmpty(energyNumStr)) {
//                        mEnergyNum = Integer.parseInt(energyNumStr.trim());
//                    }
//                    Loger.e("EnglishH5Courseware", "======> shouldOverrideUrlLoading: mEnergyNum=" + mEnergyNum);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
//
//
//        return super.shouldOverrideUrlLoading(view, url);
//    }
//
//    @Override
//    public void initData() {
//        super.initData();
//
//        WebSettings webSetting = wvSubjectWeb.getSettings();
//        webSetting.setBuiltInZoomControls(true);
//
//        String loadUrl = url + "?t=" + System.currentTimeMillis();
//        if (isPlayBack) {
//            loadUrl += "&isPlayBack=1";
//        }
//        loadUrl += "&isArts=" + (IS_SCIENCE ? "0" : "1");
//        if (!StringUtils.isEmpty(nonce)) {
//            loadUrl += "&nonce=" + nonce;
//        }
//        loadUrl += "&isTowall=" + isShowRanks;
//        logger.i( "initData:loadUrl=" + loadUrl);
//        loadUrl += "&isShowTeamPk=" + (LiveBll.isAllowTeamPk ? "1" : "0");
//        loadUrl(loadUrl);
//        Loger.e("EnglishH5CoursewarePager", "======> loadUrl:" + loadUrl);
//        reloadurl = loadUrl;
//
//        mGoldNum = -1;
//        mEnergyNum = -1;
//
//        mView.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
////                newWebView();
//                loadUrl(reloadurl);
//                v.setVisibility(View.GONE);
//                v.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        v.setVisibility(View.VISIBLE);
//                    }
//                }, 2000);
//            }
//        });
//        mView.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errorView.setVisibility(View.GONE);
////                wvSubjectWeb.setVisibility(View.VISIBLE);
//
//                newWebView();
//
//                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
//                loadView.setVisibility(View.VISIBLE);
//                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//                ((AnimationDrawable) ivLoading.getBackground()).start();
//                loadUrl(reloadurl);
//            }
//        });
//
//        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//                LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent.H5_TYPE_COURSE, id);
//                if (mEnglishH5CoursewareBll != null) {
//                    event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
//                    mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
//                }
//                EventBus.getDefault().post(event);
//                mGoldNum = -1;
//                mEnergyNum = -1;
//            }
//        });
//
//    }
//
//    @Override
//    public BasePager getBasePager() {
//        return this;
//    }
//
//    /**
//     * 设置webview透明
//     *
//     * @param color
//     */
//    @Override
//    public void setWebBackgroundColor(int color) {
//        wvSubjectWeb.setBackgroundColor(color);
//    }
//
//    private void newWebView() {
//        rlLivevideoSubjectWeb.removeView(wvSubjectWeb);
//        wvSubjectWeb = (WebView) View.inflate(mContext, R.layout.page_livevideo_h5_courseware_cacheweb, null);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        rlLivevideoSubjectWeb.addView(wvSubjectWeb, 0, lp);
//
//        addJavascriptInterface();
//        WebSettings webSetting = wvSubjectWeb.getSettings();
//        webSetting.setBuiltInZoomControls(true);
//        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
//        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
//    }
//
//    public String getId() {
//        return id;
//    }
//
//}
