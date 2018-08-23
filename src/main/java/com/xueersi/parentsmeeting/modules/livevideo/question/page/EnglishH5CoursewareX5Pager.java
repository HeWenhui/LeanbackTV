package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;

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
    private String jsforceSubmit = "javascript:forceSubmit()";
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
    private final File mMorecacheout;
    private EnglishH5Entity englishH5Entity;
    private String mLoadUrls;
    private String releasedPageInfos;
    private boolean allowTeamPk;

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        mEnglishH5CoursewareBll = englishH5CoursewareBll;
    }

    public EnglishH5CoursewareX5Pager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity,
                                      final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose,
                                      String isShowRanks, boolean IS_SCIENCE, boolean allowTeamPk) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
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
        this.allowTeamPk = allowTeamPk;
        LiveVideoConfig.englishH5Entity = englishH5Entity;
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
        cacheFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/parentsmeeting/webviewCache");
        if (cacheFile == null) {
            cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webviewCache");
        }
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
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
        wvSubjectWeb.loadUrl(LiveVideoConfig.isNewEnglishH5 ? jsforceSubmit : jsSubmitData);
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareEnd");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        umsAgentDebugInter(eventId, logHashMap.getData());
    }

    @Override
    protected void onPageFinished(WebView view, String url) {
        if (isFinish) {
            wvSubjectWeb.loadUrl(LiveVideoConfig.isNewEnglishH5 ? jsforceSubmit : jsSubmitData);
        }
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
        logHashMap.put("coursewareid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("status", "success");
        logHashMap.put("loadurl", url);
        umsAgentDebugSys(eventId, logHashMap.getData());
        if (LiveVideoConfig.isNewEnglishH5) {
            StableLogHashMap newlogHashMap = new StableLogHashMap("loadPlatformtest");
            newlogHashMap.put("os", "Android");
            newlogHashMap.put("sno", "3");
            newlogHashMap.put("testids", releasedPageInfos);
            newlogHashMap.put("stable", "1");
            newlogHashMap.put("loadurl", mLoadUrls);
            newlogHashMap.put("nonce", newlogHashMap.creatNonce());
            umsAgentDebugPv("live_platformtest", newlogHashMap.getData());
        }
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
        onClose.onH5ResultClose(this, getBaseVideoQuestionEntity());
        onBack();
        LiveVideoConfig.isNewEnglishH5 = false;
        LiveVideoConfig.isMulLiveBack = false;
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        Loger.d(TAG, "shouldOverrideUrlLoading:url=" + url);
        Loger.e(TAG, "======> shouldOverrideUrlLoading:" + url);

        reloadurl = url;
        if (url.contains("baidu.com")) {
            onClose.onH5ResultClose(this, getBaseVideoQuestionEntity());
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

        Loger.e(TAG, "======> reloadUrlLivedshouldurl:" + url);
        Loger.e(TAG, "======> reloadUrlLivedshouldreloadurl:" + reloadurl);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void initData() {
        super.initData();
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        if (LiveVideoConfig.isNewEnglishH5 || LiveVideoConfig.isMulLiveBack) {
            wvSubjectWeb.setWebViewClient(new MyWebViewClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
                    File file;
                    int index = s.indexOf("courseware_pages");
                    if (index != -1) {
                        String url2 = s.substring(index + "courseware_pages".length());
                        int index2 = url2.indexOf("?");
                        if (index2 != -1) {
                            url2 = url2.substring(0, index2);
                        }
                        file = new File(mMorecacheout, url2);
                        Loger.d(TAG, "shouldInterceptRequest:file=" + file + ",file=" + file.exists());
                    } else {
                        file = new File(mMorecacheout, MD5Utils.getMD5(s));
                        index = s.lastIndexOf("/");
                        String name = s;
                        if (index != -1) {
                            name = s.substring(index);
                        }
                        Loger.d(TAG, "shouldInterceptRequest:file2=" + file.getName() + ",name=" + name + ",file=" + file.exists());
                    }
                    if (file.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(file);
                            String extension = MimeTypeMap.getFileExtensionFromUrl(s.toLowerCase());
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "UTF-8", inputStream);
                            return webResourceResponse;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    return super.shouldInterceptRequest(view, s);
                }
            });
            if (LiveVideoConfig.isMulLiveBack) {
                String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                // 一题多发的课件预加载(直播回放)
                String packageId = "";
                String packageSource = "";
                String packageAttr = "";
                releasedPageInfos = LiveVideoConfig.LIVEPLAYBACKINFOS;
                String teamId = LiveVideoConfig.LIVEPLAYBACKTEAMID;
                String stuCouId = LiveVideoConfig.LIVEPLAYBACKSTUID;
                String classId = LiveVideoConfig.LIVEPLAYBACKCLASSID;
                String classTestId = "";
                try {
                    JSONObject jsonObject = new JSONObject(courseware_type.toString());
                    classTestId = jsonObject.optString("ctId");
                    packageAttr = jsonObject.optString("pAttr");
                    packageId = jsonObject.optString("pId");
                    packageSource = jsonObject.optString("pSrc");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mLoadUrls = "https://live.xueersi.com/science/LiveExam/getCourseWareTestHtml?stuId=" + stuId + "&liveId=" + liveId + "&stuCouId=" + stuCouId + "&classId=" + classId + "&teamId=" + teamId + "&packageId=" + packageId + "&packageSource=" + packageSource + "&packageAttr=" + packageAttr + "&releasedPageInfos=" + releasedPageInfos + "&classTestId=" + classTestId + "&educationStage=" + AppConfig.LIVEPLAYBACKSTAGE + "&isPlayBack=1" + "&nonce=" + "" + UUID.randomUUID();
            } else {
                // 一题多发的课件预加载(直播)
                String packageId = "";
                String packageSource = "";
                String packageAttr = "";
                String teamId = "";
                String stuCouId = "";
                String stuId = "";
                String classId = "";
                String classTestId = "";
                try {
                    JSONObject jsonObject = new JSONObject(mShareDataManager.getString(LiveVideoConfig.newEnglishH5, "{}", ShareDataManager.SHAREDATA_USER));
                    packageId = jsonObject.optString("packageId");
                    packageSource = jsonObject.optString("packageSource");
                    packageAttr = jsonObject.optString("packageAttr");
                    releasedPageInfos = jsonObject.optString("releasedPageInfos");
                    stuId = jsonObject.optString("stuId");
                    stuCouId = jsonObject.optString("stuCouId");
                    classId = jsonObject.optString("classId");
                    teamId = jsonObject.optString("teamId");
                    classTestId = jsonObject.optString("classTestId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mLoadUrls = "https://live.xueersi.com/science/LiveExam/getCourseWareTestHtml?stuId=" + stuId + "&liveId=" + liveId + "&stuCouId=" + stuCouId + "&classId=" + classId + "&teamId=" + teamId + "&packageId=" + packageId + "&packageSource=" + packageSource + "&packageAttr=" + packageAttr + "&releasedPageInfos=" + releasedPageInfos + "&classTestId=" + classTestId + "&educationStage=" + LiveVideoConfig.educationstage + "&isPlayBack=0" + "&nonce=" + "" + UUID.randomUUID();
                // 上传接收到教师端指令的日志
                StableLogHashMap logHashMap = new StableLogHashMap("receivePlatformtest");
                logHashMap.put("os", "Android");
                logHashMap.put("sno", "2");
                logHashMap.put("testids", releasedPageInfos);
                logHashMap.put("stable", "1");
                logHashMap.put("nonce", LiveVideoConfig.nonce);
                umsAgentDebugSys("live_platformtest", logHashMap.getData());
            }
            if (LiveBll.isAllowTeamPk) {
                mLoadUrls += "&isShowTeamPk=1";
            }
            loadUrl(mLoadUrls);
            Loger.e(TAG, "======> mulloadUrlLives:" + mLoadUrls);
            reloadurl = mLoadUrls;
            Loger.e(TAG, "======> mulloadUrlLive:" + reloadurl);
        } else {
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
            loadUrl += "&isShowTeamPk=" + (allowTeamPk ? "1" : "0");
            loadUrl(loadUrl);
            Loger.e(TAG, "======> loadUrl:" + loadUrl);
            reloadurl = loadUrl;
            Loger.e(TAG, "======> loadUrlLive:" + reloadurl);
        }
        mGoldNum = -1;
        mEnergyNum = -1;
        mView.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                newWebView();
                Loger.e(TAG, "======> reloadUrlLives:" + mLoadUrls);
                Loger.e(TAG, "======> reloadUrlLive:" + reloadurl);
                if ((LiveVideoConfig.isNewEnglishH5 || LiveVideoConfig.isMulLiveBack) && LiveVideoConfig.isPrimary) {
                    loadUrl(mLoadUrls);
                    Loger.e(TAG, "======> reloadUrlLiveds:" + mLoadUrls);
                } else {
                    String url = reloadurl + "&time=" + System.currentTimeMillis();
                    loadUrl(url);
                    reloadUrl();
                    Loger.e(TAG, "======> reloadUrlLived:" + url);
                }
                v.setVisibility(View.GONE);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.VISIBLE);
                    }
                }, 1000);
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

    public EnglishH5Entity getEnglishH5Entity() {
        return englishH5Entity;
    }

}
