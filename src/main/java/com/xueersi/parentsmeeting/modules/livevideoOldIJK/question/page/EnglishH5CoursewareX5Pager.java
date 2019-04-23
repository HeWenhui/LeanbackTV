package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.MimeTypeMap;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.event.AnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件
 */
public class EnglishH5CoursewareX5Pager extends BaseWebviewX5Pager implements BaseEnglishH5CoursewarePager {
    private String eventId = LiveVideoConfig.LIVE_ENGLISH_COURSEWARE;
    private String url;
    private String reloadurl;
    /** 刷新次数，防止预加载文件有问题 */
    private int refreshTimes = 0;
    private String nonce;
    private boolean isFinish = false;
    private String jsSubmitData = "javascript:submitData()";
    private String jsforceSubmit = "javascript:forceSubmit()";
    /** 理科初高中新课件平台 强制提交js */
    private String jsClientSubmit = "javascript:__CLIENT_SUBMIT__()";
    /** 文科新课件平台 强制提交js */
    private String jsArtsForceSubmit = "javascript:examSubmitAll()";
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private String id;
    private String courseware_type;
    private boolean isPlayBack;
    private File cacheFile;
    private String liveId;
    private EnglishH5CoursewareBll mEnglishH5CoursewareBll;
    // TODO 理科小学，提交换到原生
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private String isShowRanks;
    private RelativeLayout rlLivevideoSubjectWeb;
    private int isArts;
    private String educationstage = "";
    private int mGoldNum;
    private int mEnergyNum;
    private final File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private EnglishH5Entity englishH5Entity;
    private String mLoadUrls;
    private String releasedPageInfos;
    private boolean allowTeamPk;
    private VideoQuestionLiveEntity detailInfo;

    private boolean isNewArtsCourseware;
    private HashMap header;
    private String mGold;
    /**是否接收到或者展示过答题结果页面**/
    private boolean isAnswerResultRecived;
    /**
     * 答题结果是否是 由强制提交得到的
     */
    private boolean resultGotByForceSubmit;

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        mEnglishH5CoursewareBll = englishH5CoursewareBll;
    }

    public EnglishH5CoursewareX5Pager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity,
                                      final String courseware_type, String nonce, EnglishH5CoursewareBll.OnH5ResultClose onClose,
                                      String isShowRanks, int isArts, boolean allowTeamPk) {
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
        this.isArts = isArts;
        this.allowTeamPk = allowTeamPk;
        this.isNewArtsCourseware = englishH5Entity.isArtsNewH5Courseware();
        LiveVideoConfig.englishH5Entity = englishH5Entity;
        this.detailInfo = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        if (isArts == 0) {
            this.educationstage = detailInfo.getEducationstage();
        }
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");
        cacheFile = LiveCacheFile.geCacheFile(context, "webviewCache");
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        final File todayCacheDir = new File(cacheFile, today);
        final File todayLiveCacheDir = new File(todayCacheDir, liveId);
        if (isNewArtsCourseware) {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "artschild");
        } else {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
        }
        mPublicCacheout = new File(cacheFile, EnglishH5Cache.mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        initData();
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
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
        if (isFinish) {
            return;
        }
        // 调用此方法 时判断是否已经 收到过 作答结果
        resultGotByForceSubmit = !isAnswerResultRecived;
        isFinish = true;
        String commit;
        if (isNewArtsCourseware && !LiveQueConfig.EN_COURSE_TYPE_NEW_GAME.equals(detailInfo.type)) {
            wvSubjectWeb.loadUrl(jsArtsForceSubmit);
            commit = jsArtsForceSubmit;
            Log.e("Duncan", "js:");
        } else {
            String command;
            if (isArts == 0 && (LiveVideoConfig.EDUCATION_STAGE_3.equals(educationstage) || LiveVideoConfig.EDUCATION_STAGE_4.equals(educationstage))) {
                command = jsClientSubmit;
            } else {
                command = englishH5Entity.getNewEnglishH5() ? jsforceSubmit : jsSubmitData;
            }
            commit = command;
            Log.e("Duncan", "command:" + command);
            wvSubjectWeb.loadUrl(command);
        }
        mLogtf.d("submitData:isNewArtsCourseware=" + isNewArtsCourseware + ",commit=" + commit);
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareEnd");
        logHashMap.put("coursewareid", id);
        logHashMap.put("commit", commit);
        logHashMap.put("coursewaretype", courseware_type);
        umsAgentDebugInter(eventId, logHashMap.getData());
    }

    @Override
    protected void onPageFinished(WebView view, String url) {
        if (isFinish) {
            String command = englishH5Entity.getNewEnglishH5() ? jsforceSubmit : jsSubmitData;
            wvSubjectWeb.loadUrl(command);
        }
        if (englishH5Entity.getNewEnglishH5()) {
            StableLogHashMap newlogHashMap = new StableLogHashMap("loadPlatformtest");
            newlogHashMap.put("os", "Android");
            newlogHashMap.put("sno", "3");
            newlogHashMap.put("testids", releasedPageInfos);
            newlogHashMap.put("stable", "1");
            newlogHashMap.put("loadurl", mLoadUrls);
            newlogHashMap.put("nonce", newlogHashMap.creatNonce());
            umsAgentDebugPv("live_platformtest", newlogHashMap.getData());
        } else {
            if (failingUrl == null) {
                StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
                logHashMap.put("testid", id);
                logHashMap.put("coursewaretype", courseware_type);
                logHashMap.put("status", "success");
                logHashMap.put("loadurl", url);
                logHashMap.put("isplayback", isPlayBack ? "1" : "0");
                umsAgentDebugInter(eventId, logHashMap.getData());
            }
        }
    }

    @Override
    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        StableLogHashMap logHashMap = new StableLogHashMap("coursewareDidLoad");
        logHashMap.put("testid", id);
        logHashMap.put("coursewaretype", courseware_type);
        logHashMap.put("status", "fail");
        logHashMap.put("loadurl", url);
        logHashMap.put("isplayback", isPlayBack ? "1" : "0");
        logHashMap.put("msg", description);
        umsAgentDebugInter(eventId, logHashMap.getData());
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
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        logger.d("shouldOverrideUrlLoading:url=" + url);
        logger.e("======> shouldOverrideUrlLoading:" + url);

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
                    logger.e("======> shouldOverrideUrlLoading: mGoldNum=" + mGoldNum);
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
                    logger.e("======> shouldOverrideUrlLoading: mEnergyNum=" + mEnergyNum);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        logger.e("======> reloadUrlLivedshouldurl:" + url);
        logger.e("======> reloadUrlLivedshouldreloadurl:" + reloadurl);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void initData() {
        super.initData();
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        wvSubjectWeb.addJavascriptInterface(this, "wx_xesapp");
        wvSubjectWeb.setInitialScale(100);
        // 新课件平台
        if (englishH5Entity.getNewEnglishH5() || LiveVideoConfig.isMulLiveBack || isNewArtsCourseware) {
            wvSubjectWeb.setWebViewClient(new MyWebViewClient() {
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
                    if (refreshTimes % 2 != 0) {
                        return super.shouldInterceptRequest(view, s);
                    }
                    File file = null;
                    int index = s.indexOf("courseware_pages");
                    if (index != -1) {
                        String url2 = s.substring(index + "courseware_pages".length());
                        int index2 = url2.indexOf("?");
                        if (index2 != -1) {
                            url2 = url2.substring(0, index2);
                        }
                        file = new File(mMorecacheout, url2);
                        logger.d("shouldInterceptRequest:file=" + file + ",file=" + file.exists());
                    } else {
                        index = s.indexOf("MathJax");
                        if (index != -1) {
                            String name;
                            int questionIndex = s.indexOf("?");
                            if (questionIndex != -1) {
                                name = s.substring(index + 8, questionIndex);
                            } else {
                                name = s.substring(index + 8);
                            }
                            File pubFile = new File(mPublicCacheout, name);
                            file = pubFile;
                        } else {
                            String filemd5 = MD5Utils.getMD5(s);
                            file = new File(mMorecacheout, filemd5);
                        }
                        index = s.lastIndexOf("/");
                        String name = s;
                        if (index != -1) {
                            name = s.substring(index);
                        }
                        logger.d("shouldInterceptRequest:file2=" + file.getName() + ",name=" + name + ",file=" + file
                                .exists());
                    }
                    if (file.exists()) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("url", s);
                            hashMap.put("filepath", file.getPath());
                            hashMap.put("filelength", "" + file.length());
                            UmsAgentManager.umsAgentDebug(mContext, TAG + "_cache", hashMap);
                        } catch (Exception e) {
                            CrashReport.postCatchedException(e);
                        }
                        if (file.length() > 0) {
                            FileInputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(file);
                                String extension = MimeTypeMap.getFileExtensionFromUrl(s.toLowerCase());
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "",
                                        inputStream);
                                webResourceResponse.setResponseHeaders(header);
                                Log.e("Duncan", "artsload");
                                return webResourceResponse;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return super.shouldInterceptRequest(view, s);
                }
            });
            //文科 直播 回放
            if (isNewArtsCourseware) {
                String loadUrl = url;
                loadUrl(loadUrl);
                reloadurl = loadUrl;
                Loger.e(TAG, "======> newArtsH5CourseWare url:" + url);
            } else {
                // 理科回放
                if (isPlayBack) {
                    // 半身直播体验课 试题url 拼接
                    Log.e("HalfBodyExp", "=========>EnglishH5X5pager: called 22222:" + englishH5Entity.getLiveType()
                            + ":" + mLoadUrls);
                    if (englishH5Entity.getLiveType() == LiveVideoConfig.ExperiencLiveType.HALF_BODY) {
                        mLoadUrls = englishH5Entity.getUrl();
                        Log.e("HalfBodyExp", "=========>EnglishH5X5pager: called 5555:" + englishH5Entity.getLiveType()
                                + ":" + mLoadUrls);
                    } else {
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
                            JSONObject jsonObject = new JSONObject(LiveVideoConfig.LIVEPLAYBACKTYPE);
                            classTestId = jsonObject.optString("ctId");
                            packageAttr = jsonObject.optString("pAttr");
                            packageId = jsonObject.optString("pId");
                            packageSource = jsonObject.optString("pSrc");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String realurl = englishH5Entity.getDynamicurl();
                        mLoadUrls = realurl + "?stuId=" + stuId + "&liveId=" + liveId + "&stuCouId=" + stuCouId +
                                "&classId=" + classId + "&teamId=" + teamId + "&packageId=" + packageId +
                                "&packageSource=" + packageSource + "&packageAttr=" + packageAttr +
                                "&releasedPageInfos=" + releasedPageInfos + "&classTestId=" + classTestId +
                                "&educationStage=" + LiveVideoConfig.LIVEPLAYBACKSTAGE + "&isPlayBack=1" + "&nonce="
                                + "" + UUID.randomUUID();
                    }
                } else {
                    // 理科直播
                    // 一题多发的课件预加载(直播)
                    String packageId = "";
                    String packageSource = "";
                    String packageAttr = "";
                    String teamId = "";
                    String stuCouId = "";
                    String stuId = "";
                    String classId = "";
                    String classTestId = "";
                    String string = null;
                    try {
                        string = mShareDataManager.getString(LiveVideoConfig
                                .newEnglishH5, "{}", ShareDataManager.SHAREDATA_USER);
                        JSONObject jsonObject = new JSONObject(string);
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
                        CrashReport.postCatchedException(e);
                        mLogtf.e("initData:string=" + string, e);
                    }
                    if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(stuCouId)) {
                        mLogtf.d("initData:string=" + string);
                    }
                    String defaulturl;
                    boolean useMine = false;
                    if (isArts == 0) {
                        useMine = true;
                        defaulturl = englishH5Entity.getDynamicurl();
                    } else {
                        defaulturl = "https://live.chs.xueersi.com/LiveExam/getCourseWareTestHtml";
                    }
                    String dynamicurl;
                    if (useMine) {
                        dynamicurl = defaulturl;
                    } else {
                        dynamicurl = TextUtils.isEmpty(LiveVideoConfig.LIVEMULH5URL) ? defaulturl : LiveVideoConfig.LIVEMULH5URL;
                    }
                    mLoadUrls = dynamicurl + "?stuId=" + stuId + "&liveId=" + liveId + "&stuCouId=" + stuCouId +
                            "&classId=" + classId + "&teamId=" + teamId + "&packageId=" + packageId +
                            "&packageSource=" + packageSource + "&packageAttr=" + packageAttr + "&releasedPageInfos="
                            + releasedPageInfos + "&classTestId=" + classTestId + "&educationStage=" +
                            LiveVideoConfig.educationstage + "&isPlayBack=0" + "&nonce=" + "" + UUID.randomUUID();
                    // 上传接收到教师端指令的日志
                    StableLogHashMap logHashMap = new StableLogHashMap("receivePlatformtest");
                    logHashMap.put("os", "Android");
                    logHashMap.put("sno", "2");
                    logHashMap.put("testids", releasedPageInfos);
                    logHashMap.put("stable", "1");
                    logHashMap.put("nonce", LiveVideoConfig.nonce);
                    umsAgentDebugSys("live_platformtest", logHashMap.getData());
                }
                if (allowTeamPk) {
                    mLoadUrls += "&isShowTeamPk=1";
                }
                loadUrl(mLoadUrls);
                logger.e("======> mulloadUrlLives:" + mLoadUrls);
                reloadurl = mLoadUrls;
                logger.e("======> mulloadUrlLive:" + reloadurl);
            }
        } else {
                String loadUrl = url + "?t=" + System.currentTimeMillis();
                if (!url.isEmpty() && url.substring(url.length() - 1).equals("&")) {
                    loadUrl = url + "t=" + System.currentTimeMillis();
                }

                // 文理老课件平台 直播回放  url 多拼接一个isPlayBack字段
                if (isPlayBack) {
                    loadUrl += "&isPlayBack=1";
                }
                loadUrl += "&isArts=" + isArts;
                if (!StringUtils.isEmpty(nonce)) {
                    loadUrl += "&nonce=" + nonce;
                }
                loadUrl += "&isTowall=" + isShowRanks;
                logger.i("initData:loadUrl=" + loadUrl);
                loadUrl += "&isShowTeamPk=" + (allowTeamPk ? "1" : "0");
                loadUrl(loadUrl);
                logger.e("======> loadUrl:" + loadUrl);
                reloadurl = loadUrl;
                logger.e("======> loadUrlLive:" + reloadurl);
        }
        if (mLogtf != null) {
            mLogtf.d("initData:reloadurl=" + reloadurl);
        }
        mGoldNum = -1;
        mEnergyNum = -1;
        mView.findViewById(R.id.iv_livevideo_subject_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                newWebView();
                logger.e("======> reloadUrlLives:" + mLoadUrls);
                logger.e("======> reloadUrlLive:" + reloadurl);
                refreshTimes++;
                if (refreshTimes % 2 == 0) {
                    if (wvSubjectWeb instanceof CacheWebView) {
                        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
                        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
                        logger.d("refreshonClick:NORMAL");
                    }
                } else {
                    if (wvSubjectWeb instanceof CacheWebView) {
                        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
                        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NO_CACHE);
                        if (refreshTimes % 3 == 0) {
                            cacheWebView.clearCache();
                        }
                        logger.d("refreshonClick:NO_CACHE");
                    }
                }
                if ((englishH5Entity.getNewEnglishH5() || LiveVideoConfig.isMulLiveBack) && LiveVideoConfig.isPrimary) {
                    loadUrl(mLoadUrls);
                    logger.e("======> reloadUrlLiveds:" + mLoadUrls);
                } else {
                    String url = reloadurl + "&time=" + System.currentTimeMillis();
                    loadUrl(url);
                    reloadUrl();
                    logger.e("======> reloadUrlLived:" + url);
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
            long before;

            @Override
            public void onViewAttachedToWindow(View v) {
                before = System.currentTimeMillis();
                if (mLogtf != null) {
                    mLogtf.d("onViewAttachedToWindow:reloadurl=" + reloadurl);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (mLogtf != null) {
                    mLogtf.d("onViewDetachedFromWindow:reloadurl=" + reloadurl + ",,time=" + (System
                            .currentTimeMillis() - before));
                }
                LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(mGoldNum, mEnergyNum, LiveRoomH5CloseEvent
                        .H5_TYPE_COURSE, id);
                if (mEnglishH5CoursewareBll != null) {
                    event.setCloseByTeahcer(mEnglishH5CoursewareBll.isWebViewCloseByTeacher());
                    mEnglishH5CoursewareBll.setWebViewCloseByTeacher(false);
                }
                event.setScienceNewCourseWare(englishH5Entity.getNewEnglishH5());
                event.setForceSubmit(resultGotByForceSubmit);
                EventBus.getDefault().post(event);
                mGoldNum = -1;
                mEnergyNum = -1;
              /*  if (englishH5Entity.getNewEnglishH5()) {
                    LiveVideoConfig.isNewEnglishH5 = true;
                } else {
                    LiveVideoConfig.isNewEnglishH5 = false;
                }*/
            }
        });

    }


    /**
     * 文科 课件 答题结果回调
     */
    @JavascriptInterface
    public void showAnswerResult_LiveVideo(String data) {
        Loger.e("EnglishH5CourseWareX5Pager",
                "=========>showAnswerResult_LiveVideo:" + data);
        Loger.e(TAG, "======> newArtsH5CourseWare data:" + data);
        isAnswerResultRecived = true;
        EventBus.getDefault().post(new ArtsAnswerResultEvent(data, ArtsAnswerResultEvent.TYPE_H5_ANSWERRESULT));
    }

    /**
     * 理科 课件 答题结果回调
     */
    @JavascriptInterface
    public void onAnswerResult_LiveVideo(String data){
        isAnswerResultRecived =true;
        EventBus.getDefault().post(new AnswerResultEvent(data));
    }


    /**
     * AI体验课 课件 答题结果回调
     */
    @JavascriptInterface
    public void showExperienceResult(String data) {
        Loger.e(TAG, "======> newArtsH5CourseWare showExperienceResult:" + data);
        parseAIData(data);
        Loger.e(TAG, "======> LiveVideoConfig.isAITrue:" + LiveVideoConfig.isAITrue);
    }

    private void parseData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject dataObject = jsonObject.optJSONObject("data");
            if (dataObject.has("total")) {
                JSONObject totalObject = dataObject.getJSONObject("total");
                mGold = totalObject.optString("gold");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseAIData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            LiveVideoConfig.isAITrue = jsonObject.optBoolean("isRight");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rlLivevideoSubjectWeb.addView(wvSubjectWeb, 0, lp);
        Loger.e(TAG, "======> newArtsH5CourseWare refresh:");
        addJavascriptInterface();
        wvSubjectWeb.addJavascriptInterface(this, "wx_xesapp");
        // TODO 理科小学
//        if (isArts == 0) {
//            wvSubjectWeb.addJavascriptInterface(new ScienceStaticWeb(this, wvSubjectWeb, englishH5CoursewareSecHttp), "xesApp");
//        }
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        wvSubjectWeb.setInitialScale(100);
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

    @Override
    public boolean isResultRecived() {
        return isAnswerResultRecived;
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        // TODO 理科小学
//        if (isArts == 0) {
//            wvSubjectWeb.addJavascriptInterface(new ScienceStaticWeb(this, wvSubjectWeb, englishH5CoursewareSecHttp), "xesApp");
//        }
    }
}
