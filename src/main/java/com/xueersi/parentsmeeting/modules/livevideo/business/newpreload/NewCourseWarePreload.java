package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload;

import android.content.Context;
import android.text.TextUtils;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NewCourseWarePreload {
    String TAG = getClass().getSimpleName();
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Context mContext;
    //    private String liveId;
    private int mSubject = -1;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser liveHttpResponseParser;
    File cacheFile;
    private File todayCacheDir;
    public static String mPublicCacheoutName = "publicRes";
    public static String FZY3JW_TTF = "FZY3JW.ttf";

    public static NewCourseWarePreload newCourseWarePreload;
    /**
     * 是否紧急下载
     */
    AtomicBoolean isPrecise = new AtomicBoolean(false);
    /**
     * 下载的科目总数
     */
    private AtomicInteger subjectNum = new AtomicInteger(0);

    List<CoursewareInfoEntity> courseWareInfos = new CopyOnWriteArrayList<>();

    public static NewCourseWarePreload getInstance(Context context, int mSubject) {
        if (newCourseWarePreload == null) {
            synchronized (NewCourseWarePreload.class) {
                if (newCourseWarePreload == null)
                    newCourseWarePreload = new NewCourseWarePreload(context.getApplicationContext(), mSubject);
            }
        }
        return newCourseWarePreload;
    }

    private NewCourseWarePreload(Context context, int subject) {
        mContext = context;
        mSubject = subject;
        liveHttpResponseParser = new LiveHttpResponseParser(context);
        cacheFile = LiveCacheFile.geCacheFile(mContext, "webviewCache");
        logger.d("cache path :" + cacheFile.getAbsolutePath());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    static ThreadPoolExecutor executos = new ThreadPoolExecutor(1, 1,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 是否需要弹出oldFile
     *
     * @param itemFile
     */
    public boolean isDeleteAsyn(File itemFile) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        return (!itemFile.getName().equals(today)
                && !itemFile.getName().equals(mPublicCacheoutName) &&
                isCoursewareDir(itemFile.getName()));
    }

    /**
     * 是否是课件的文件夹(课件文件夹由日期构成)
     *
     * @return
     */
    private boolean isCoursewareDir(String fileName) {
        try {
            Integer.parseInt(fileName);
            return true;
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(TAG, e);
            return false;
        }
    }

    /**
     * 获取课件信息
     */
    public void getCoursewareInfo(String liveId) {
        executos.allowCoreThreadTimeOut(true);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        Date date = new Date();
//        final String today = dateFormat.format(date);
//        todayCacheDir = new File(cacheFile, today);
        DeleteUtils.deleteOldDirAsync(
                cacheFile,
                new DeleteUtils.DeleteCatagry() {
                    @Override
                    public boolean deleteDirAndFile(File itemFile) {
                        return isDeleteAsyn(itemFile);
                    }

                    @Override
                    public boolean deleteSpecel(File itemFile) {
                        return false;
                    }
                });

        //根据传liveid来判断 不为空或者不是""则为直播进入下载资源，否则为学习中心进入下载资源
//        ipPos = new AtomicInteger(0);
//        ipLength = new AtomicInteger();
//        cdnLength = new AtomicInteger();
//        cdnPos = new AtomicInteger(0);
        if (!TextUtils.isEmpty(liveId)) {
            isPrecise.set(true);
            if (0 == mSubject) {//理科
                logger.i("donwload science");
                sendPost(liveId, new CoursewareHttpCallBack(false, "science", liveId));
            } else if (1 == mSubject) {//英语
                logger.i("download english");
                sendPost(liveId, new CoursewareHttpCallBack(false, "english", liveId));
            } else if (2 == mSubject) {//语文
                logger.i("download chs");
                sendPost(liveId, new CoursewareHttpCallBack(false, "chs", liveId));
            }
        } else {//下载当天所有课件资源
            logger.i("donwload all subjects");
            sendPost("", new CoursewareHttpCallBack(false, "science", ""));
            sendPost("", new CoursewareHttpCallBack(false, "english", ""));
            sendPost("", new CoursewareHttpCallBack(false, "chs", ""));
        }
    }

    private void sendPost(String liveId, HttpCallBack httpCallBack) {
        subjectNum.getAndIncrement();
        mHttpManager.getScienceCourewareInfo(liveId, httpCallBack);
    }

    public class CoursewareHttpCallBack extends HttpCallBack {

        private String arts;
        private String liveId;

        public CoursewareHttpCallBack(boolean isShow, String arts, String liveId) {
            super(isShow);
            this.arts = arts;
            this.liveId = liveId;
        }


        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            CoursewareInfoEntity coursewareInfoEntity = liveHttpResponseParser.parseCoursewareInfo(responseEntity);
            logger.i(responseEntity.getJsonObject().toString());
            courseWareInfos.add(coursewareInfoEntity);
            logger.i(arts + " pmSuccess");
            // 加试实验 只从理科资源预加载接口返回
//            if ("science".equals(arts) && coursewareInfoEntity != null) {
//                mNbCoursewareInfo = coursewareInfoEntity.getNbCoursewareInfo();
//            }
            boolean perform = performDownLoad();
            try {
                StableLogHashMap hashMap = new StableLogHashMap();
                hashMap.put("logtype", "onPmSuccess");
                if (coursewareInfoEntity != null) {
                    hashMap.put("size", "" + coursewareInfoEntity.getCoursewaresList().size());
                } else {
                    hashMap.put("size", "-10");
                }
                hashMap.put("arts", "" + arts);
                hashMap.put("subjectnum", "" + subjectNum.get());
                hashMap.put("perform", "" + perform);
                hashMap.put("liveId", "" + liveId);
                hashMap.put("ip", IpAddressUtil.USER_IP);
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), UmsConstants.LIVE_APP_ID,
                        LogConfig.PRE_LOAD_START, hashMap.getData());
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(TAG, e);
            }
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
            super.onPmFailure(error, msg);
            subjectNum.getAndDecrement();
            logger.i("paFailure" + arts);
            performDownLoad();
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
            super.onPmError(responseEntity);
            subjectNum.getAndDecrement();
            performDownLoad();
            if (responseEntity != null) {
                logger.i("onPmError:" + arts + " " + responseEntity.getJsonObject() + "  " + responseEntity.getErrorMsg());
//                if (responseEntity.getJsonObject() != null) {
//                    logger.i("onPmError:" + responseEntity.getJsonObject().toString());
//                }
            }
        }
    }

    private boolean performDownLoad() {
        logger.i("performDownLoad:size=" + courseWareInfos.size() + " " + subjectNum.get());
        if (courseWareInfos.size() == subjectNum.get()) {
            logger.i("perform download ");
            LiveAppBll.getInstance().registerAppEvent(this);
            for (int i = 0; i < courseWareInfos.size(); i++) {

            }
//            storageLiveId();
//            execDownLoad(
//                    sortArrays(),
//                    mergeList(courseWareInfos, 1),
//                    mergeList(courseWareInfos, 2),
//                    mergeList(courseWareInfos, 3));
            return true;
        } else {
            return false;
        }
    }
}
