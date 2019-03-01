package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CoursewareNativePager extends LiveBasePager implements BaseEnglishH5CoursewarePager {
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
    private final File mMorecacheout;
    /**
     * 公共资源
     */
    private File mPublicCacheout;
    private HashMap header;
    ProgressBar pg_livevideo_new_course_prog;
    WebView wvSubjectWeb;

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
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_native, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        pg_livevideo_new_course_prog = view.findViewById(R.id.pg_livevideo_new_course_prog);
        return view;
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
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
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
}
