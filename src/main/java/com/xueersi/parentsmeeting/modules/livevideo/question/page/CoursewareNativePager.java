package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.StaticWeb;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CoursewareNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {
    private boolean isFinish = false;
    private String liveId;
    EnglishH5Entity englishH5Entity;
    private boolean isPlayBack;
    EnglishH5CoursewareBll.OnH5ResultClose onClose;
    String url;
    String id;
    String courseware_type;
    String nonce;
    String isShowRanks;
    int isArts;
    boolean allowTeamPk;
    boolean isNewArtsCourseware;
    VideoQuestionLiveEntity detailInfo;
    String educationstage;
    private File cacheFile;
    private File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private HashMap header;
    RelativeLayout rl_livevideo_subject_loading;
    ProgressBar pg_livevideo_new_course_prog;
    RelativeLayout rl_livevideo_new_course_control;
    TextView tv_data_loading_tip;
    NewCourseCache newCourseCache;
    ImageView ivLoading;

    public CoursewareNativePager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, boolean isPlayBack, String liveId, String id, EnglishH5Entity englishH5Entity,
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
//        initWebView();
//        setErrorTip("H5课件加载失败，请重试");
//        setLoadTip("H5课件正在加载，请稍候");
//        cacheFile = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/webview/");

        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_native, null);
        ivLoading = (ImageView) view.findViewById(R.id.iv_data_loading_show);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        tv_data_loading_tip = view.findViewById(R.id.tv_data_loading_tip);
        rl_livevideo_subject_loading = view.findViewById(R.id.rl_livevideo_subject_loading);
        pg_livevideo_new_course_prog = view.findViewById(R.id.pg_livevideo_new_course_prog);
        rl_livevideo_new_course_control = view.findViewById(R.id.rl_livevideo_new_course_control);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        cacheFile = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/parentsmeeting/webviewCache");
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
        if (isNewArtsCourseware) {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "artschild");
        } else {
            mMorecacheout = new File(todayLiveCacheDir, liveId + "child");
        }
        mPublicCacheout = new File(cacheFile, EnglishH5Cache.mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        newCourseCache = new NewCourseCache(mContext);
        addJavascriptInterface();
        wvSubjectWeb.setWebChromeClient(new BaseCoursewareNativePager.MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new CourseWebViewClient());
        header = new HashMap();
        header.put("Access-Control-Allow-Origin", "*");
        wvSubjectWeb.addJavascriptInterface(new StaticWeb(mContext, wvSubjectWeb, new StaticWeb.OnMessage() {

            @Override
            public void postMessage(JSONObject message, String origin) {
                try {
                    String type = message.getString("type");
                    if ("close".equals(type)) {
                        wvSubjectWeb.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else if ("submitAnswer".equals(type)) {
//                        submit(message);
                    } else if ("answer".equals(type)) {
                        wvSubjectWeb.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }), "xesApp");
    }

    @Override
    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public void close() {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void onBack() {

    }

    @Override
    public void destroy() {
        isFinish = true;
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void submitData() {

    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    protected void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        pg_livevideo_new_course_prog.setProgress(newProgress);
        tv_data_loading_tip.setText("加载中 " + newProgress + "%");
        if (newProgress == 100) {
            rl_livevideo_subject_loading.setVisibility(View.GONE);
            rl_livevideo_new_course_control.setVisibility(View.VISIBLE);
            try {
                Drawable drawable = ivLoading.getBackground();
                if (drawable instanceof AnimationDrawable) {
                    ((AnimationDrawable) drawable).stop();
                }
            } catch (Exception e) {
                if (mLogtf != null) {
                    mLogtf.e("onProgressChanged", e);
                }
            }
        }
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        try {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.animlst_app_loading);
            ivLoading.setBackground(drawable);
            ((AnimationDrawable) drawable).start();
        } catch (Exception e) {
            if (mLogtf != null) {
                mLogtf.e("initData", e);
            }
        }
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                logger.d("onDataSucess:objData=" + objData);
                NewCourseSec newCourseSec = (NewCourseSec) objData[0];
                ArrayList<NewCourseSec.Test> tests = newCourseSec.getTests();
                NewCourseSec.Test test = tests.get(0);
                wvSubjectWeb.loadUrl(test.getPreviewPath());
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
            }
        });
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return englishH5Entity;
    }

    class CourseWebViewClient extends MyWebViewClient {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl() + "";
            if (url.contains(".html")) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(view, url);
                logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            } else if (WebInstertJs.indexStr().equals(url)) {
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(view, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                }
            }
            WebResourceResponse webResourceResponse = newCourseCache.shouldInterceptRequest(view, url);
            if (webResourceResponse != null) {
                logger.d("shouldInterceptRequest:url=" + url);
                return webResourceResponse;
            }
            return super.shouldInterceptRequest(view, url);
        }
    }
}
