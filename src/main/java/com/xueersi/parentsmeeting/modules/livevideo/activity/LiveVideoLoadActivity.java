package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.smtt.sdk.TbsListener;
import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.XrsCrashReport;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.util.LoginEnter;
import com.xueersi.common.util.XrsBroswer;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreloadStaticStorage;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveBusinessResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.share.business.biglive.config.BigLiveCfg;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linyuqiang on 2018/7/14.
 * 直播中间的loading
 */
public class LiveVideoLoadActivity extends BaseActivity {
    String TAG = "LiveVideoLoadActivityLog";
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();
    /**
     * Activity创建次数
     */
    public static int CREATE_TIMES = 0;
    DataLoadEntity mDataLoadEntity;
    protected static ArrayList<LiveVideoLoadActivity> liveVideoLoadActivities = new ArrayList<>();
    private static int statIndex = 0;
    protected int index;

    public static final String OTHER_ACTIVITY_INTENT = "other_intent";

    public LiveVideoLoadActivity() {
        liveVideoLoadActivities.add(this);
        index = statIndex++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        String token = LiveAppUserInfo.getInstance().getTalToken();
        Intent intent = getIntent();
        otherActivityIntent = intent.getParcelableExtra(OTHER_ACTIVITY_INTENT);
//        Bundle bundle = intent.getExtras();
        //scheme 测试
//        if (bundle == null) {
//            bundle = new Bundle();
//            Uri uri = getIntent().getData();
////            xeswangxiao://livevideo/intolive?livetype=2&liveid=10011002
//            String query = uri.getQuery(); //queryParameter=queryString
//            //param
//            int livetype = Integer.parseInt(uri.getQueryParameter("livetype"));
//            String vSectionID = uri.getQueryParameter("liveid");
//            if (livetype == LiveVideoConfig.LIVE_TYPE_LIVE) {
//                String vStuCourseID = uri.getQueryParameter("stuCourseID");
//                intent.putExtra("vStuCourseID", vStuCourseID);
//                String courseId = intent.getStringExtra("courseId");
//                intent.putExtra("courseId", courseId);
//            }
//            bundle.putInt("type", livetype);
//            bundle.putString("vSectionID", vSectionID);
//            intent.putExtras(bundle);
//        }
        //如果没有token，只能重新点击进入了
        if (StringUtils.isEmpty(token)) {
            XESToastUtils.showToast(this, "登录信息失效，重新登录");
            StableLogHashMap logHashMap = new StableLogHashMap();
            logHashMap.put("create_times", "" + CREATE_TIMES);
            //距离进程创建的时间
            logHashMap.put("app_time",
                    "" + (System.currentTimeMillis() - UmsConstants.PROCRESS_CREATE_TIME));
            UmsAgentManager.umsAgentDebug(this, LogConfig.LIVE_TOKEN_NULL, logHashMap.getData());
            LoginEnter.openLogin(mContext,false,null);
            finishAndExit();
            return;
        }
        CREATE_TIMES++;

        mDataLoadEntity = new DataLoadEntity(this);
        mDataLoadEntity.beginLoading();
        DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this,
                mDataLoadEntity);
        initData();

    }

    @Override
    protected void onDestroy() {
        //结束上一个Activity，即直播提醒的activity
        if (otherActivityIntent != null) {
            if (AppConfig.DEBUG) {
                logger.i("发送BroadCastReceiver成功");
            }
            Intent broadCastIntent = new Intent("com.xueersi.parentsmeeting.LIVE_NOTICE_BROADCAST_FILTER");
            sendBroadcast(broadCastIntent);
        }
        logger.i("livevideoloadactivity is destroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (FileLogger.runActivity == this) {
            FileLogger.runActivity = null;
        }
        liveVideoLoadActivities.remove(this);
    }

    private void performDownLoadPreLoad(LiveHttpManager mHttpManager, LiveGetInfo getInfo) {
        String liveId = getInfo.getId();
        int mSubject = getInfo.getIsArts();
        CoursewarePreload coursewarePreload = new CoursewarePreload(this, mSubject);
        coursewarePreload.setmHttpManager(mHttpManager);
        coursewarePreload.getCoursewareInfo(liveId);

//        if (liveId != null && !"".equals(liveId)) {
//
//            if (0 == mSubject) {//理科
//                logger.i("下载理科");
//                mHttpManager.getScienceCourewareInfo(liveId, new CoursewarePreload
//                .CoursewareHttpCallBack());
//            } else if (1 == mSubject) {//英语
//                logger.i("下载英语");
//                mHttpManager.getEnglishCourewareInfo(liveId, new CoursewarePreload
//                .CoursewareHttpCallBack());
//            } else if (2 == mSubject) {//语文
//                logger.i("下载语文");
//                mHttpManager.getArtsCourewareInfo(liveId, new CoursewarePreload
//                .CoursewareHttpCallBack());
//            }
//        }
    }

    private void initData() {
        logger.d("initData:index=" + index + ",size=" + liveVideoLoadActivities.size());
        //已经finish了 https://bugly.qq.com/v2/crash-reporting/crashes/a0df5ed682/336475?pid=1
        if (liveVideoLoadActivities.isEmpty()) {
            XrsCrashReport.d(TAG, "initData:finish=" + isFinishing());
            XrsCrashReport.postCatchedException(new LiveException(TAG));
            return;
        }
        LiveVideoLoadActivity activity = liveVideoLoadActivities.get(0);
        if (activity != this) {
            DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this,
                    mDataLoadEntity.webDataSuccess());
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            boolean init = XrsBroswer.init(this, new TbsListener() {
                @Override
                public void onDownloadFinish(int i) {

                }

                @Override
                public void onInstallFinish(int i) {
                    StableLogHashMap logHashMap = new StableLogHashMap("onInstallFinish");
                    logHashMap.put("code", "" + i);
                    UmsAgentManager.umsAgentDebug(ContextManager.getContext(),
                            LogConfig.LIVE_X5_LOG, logHashMap.getData());
                    initData2();
                }

                @Override
                public void onDownloadProgress(int i) {
                    mDataLoadEntity.setProgressTip("下载中" + (i * 100 / 120) + "%");
                    mDataLoadEntity.beginLoading();
                    mDataLoadEntity.setCurrentLoadingStatus(DataLoadEntity.DATA_PROGRESS);
                    DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this,
                            mDataLoadEntity);
                }
            });
//            StableLogHashMap logHashMap = new StableLogHashMap("init");
//            logHashMap.put("status", "" + init);
//            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LogConfig.LIVE_X5_LOG,
//            logHashMap.getData());
            if (!init) {
                return;
            }
        }
        initData2();
    }

    private Intent otherActivityIntent;

    private void initData2() {

        Intent intent = getIntent();
        otherActivityIntent = intent.getParcelableExtra(OTHER_ACTIVITY_INTENT);
        final Bundle bundle = intent.getExtras();
        final String vSectionID = intent.getStringExtra("vSectionID");
        final int liveType = bundle.getInt("type", 0);
        bundle.putString("planId", vSectionID);
        final int from = intent.getIntExtra("", 0);
        LiveVideoConfig.isLightLive = false;

        final LiveHttpManager httpManager = new LiveHttpManager(this);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            //大班整合-讲座
            if (isBigLiveRoom()) {
                enterBigLive(bundle, vSectionID, liveType, from, "0", httpManager);
            }
        } else if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {

            if (isBigLiveRoom()) {
                String stuCouId = intent.getStringExtra("vStuCourseID");
                enterBigLive(bundle, vSectionID, liveType, from, stuCouId, httpManager);
            } else {
                //非大班直播
                final String vStuCourseID = intent.getStringExtra("vStuCourseID");
                String courseId = intent.getStringExtra("courseId");
                httpManager.addBodyParam("stuCouId", vStuCourseID);
                httpManager.addBodyParam("liveId", vSectionID);
                httpManager.addBodyParam("from", "" + from);
                httpManager.liveGetInfo(courseId, vSectionID, 0, new HttpCallBack(mDataLoadEntity
                        , false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        LiveHttpResponseParser mHttpResponseParser =
                                new LiveHttpResponseParser(LiveVideoLoadActivity.this);
                        JSONObject object = (JSONObject) responseEntity.getJsonObject();
                        LiveTopic mLiveTopic = new LiveTopic();
                        LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object,
                                mLiveTopic, liveType, from);
                        if (mGetInfo == null) {
                            XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                            finish();
                            return;
                        }
                        // 语文半身直播 暂不支持观看
                  /*  if (isChineseHalfBodyLive(mGetInfo)) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "语文半身直播暂不支持,请升级版本");
                        AppBll.getInstance(mContext).checkPartUpdate("语文半身直播暂不支持,请升级版本");
                        return;
                    }*/

                        String stuId = LiveAppUserInfo.getInstance().getStuId();
                        getInfos.put(stuId + "-" + vStuCourseID + "-" + vSectionID, mGetInfo);
//                    mGetInfo.setPattern(1);
                        bundle.putString("mode", mGetInfo.getMode());

                        int isNewProject = mGetInfo.getIsNewProject();
                        boolean newCourse = (isNewProject == 1) || isNewCourse(mGetInfo.getId());
                        if (newCourse) {
                            bundle.putBoolean("newCourse", true);
                            performDownLoadPreLoad(httpManager, mGetInfo);
                        }
//                    bundle.putIntegerArrayList("preloadliveid", PreloadStaticStorage
//                    .preloadLiveId);
                        bundle.putInt("isArts", mGetInfo.getIsArts());
                        bundle.putInt("pattern", mGetInfo.getPattern());
                        bundle.putBoolean("isPrimary", LiveVideoConfig.isPrimary);
                        bundle.putBoolean("isSmallChinese", LiveVideoConfig.isSmallChinese);
                        bundle.putBoolean("isSmallEnglish", mGetInfo.getSmallEnglish());
                        bundle.putInt("useSkin", mGetInfo.getUseSkin());
                        bundle.putInt("isGoldMicrophone", mGetInfo.isUseGoldMicroPhone());
                        bundle.putInt("useSuperSpeakerShow", mGetInfo.getUseSuperSpeakerShow());
                        if (mGetInfo.getIsArts() == LiveVideoSAConfig.ART_SEC) {
                            bundle.putInt("allowLinkMicNew", mGetInfo.getAllowLinkMicNew());
                        } else {
                            bundle.putInt("smallEnglish", mGetInfo.getSmallEnglish() ? 1 : 0);
                        }
//                if (mGetInfo.getPattern() == 2) {
//                    StandLiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle, -1);
//                } else {
//                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity
//                    .intentTo
//                    (LiveVideoLoadActivity.this, bundle);
//                }
                        if (1 == mGetInfo.getIsEnglish()) {
                            gotoEnglish(bundle);
                        } else if (mGetInfo.isUseGoldMicroPhone() == 1 || mGetInfo.getUseSuperSpeakerShow() == 1) {
                            List<Integer> list = new ArrayList<>();
                            list.add(PermissionConfig.PERMISSION_CODE_AUDIO);
                            list.add(PermissionConfig.PERMISSION_CODE_CAMERA);
                            gotoHalfBodyChinese(bundle, list);
                        } else {
                            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast("初始化失败");
                        finish();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
//                    XESToastUtils.showToast(LiveVideoLoadActivity.this, responseEntity
//                    .getErrorMsg());
                        int status = responseEntity.getmStatus();
                        if (10 == status) {
//                        Intent data = new Intent();
//                        data.putExtra("msg", "请升级APP");
//                        setResult(ShareBusinessConfig.LIVE_APP_UPDATE, data);
//                        finish();
                        } else {
                            XESToastUtils.showToast(responseEntity.getErrorMsg());
                            finishAndExit();
                        }
                    }
                });
            }
        }
    }

    private void gotoGroupClass(final Bundle bundle) {
        //需要申请的权限数量
        int needPermissionCount = -1;
        boolean storagePermissionHave = XesPermission.checkPermissionHave(this,
                PermissionConfig.PERMISSION_CODE_STORAGE);
        if (!storagePermissionHave) {
            ++needPermissionCount;
        }
        boolean cameraPermissionHave = XesPermission.checkPermissionHave(this,
                PermissionConfig.PERMISSION_CODE_CAMERA);
        if (!cameraPermissionHave) {
            ++needPermissionCount;
        }
        boolean audioPermissionHave = XesPermission.checkPermissionHave(this,
                PermissionConfig.PERMISSION_CODE_AUDIO);
        if (!audioPermissionHave) {
            ++needPermissionCount;
        }
        logger.d("storagePermissionHave=" + storagePermissionHave
                + ",cameraPermissionHave=" + cameraPermissionHave + ",audioPermissionHave=" + audioPermissionHave);

        final int finalNeedPermissionCount = needPermissionCount;
        boolean have = XesPermission.checkPermission(this, new LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        logger.d("onDeny,permission=" + permission + ",position=" + position
                                + ",finalNeedPermissionCount=" + finalNeedPermissionCount);
                        LiveMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        logger.d("onGuarantee,permission=" + permission + ",position=" + position
                                + ",finalNeedPermissionCount=" + finalNeedPermissionCount);
                        if (finalNeedPermissionCount == position) {
                            LiveMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String planId = bundle.getString("vSectionID", "");
                                    boolean isSupported = LiveAppUserInfo.getInstance().isSupportedEnglishName();
                                    String skipPlanId = mShareDataManager.getString(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_SKIPED, "-1",
                                            ShareDataManager.SHAREDATA_USER);
                                    boolean isSkip = skipPlanId.equals(planId);
                                    // 没有找到可支持的英文名，且改场次之前没有跳过
                                    if (bundle.getInt("pattern") == 8 && !isSupported && !isSkip) {
                                        XueErSiRouter.startModule(mContext, "/groupclass" +
                                                        "/englishname"
                                                , bundle);
                                    } else {
                                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
                                    }
                                    finish();
                                }
                            });
                        }

                    }
                },
                PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO,
                PermissionConfig.PERMISSION_CODE_STORAGE);
        if (have) {
            String planId = bundle.getString("vSectionID", "");
            boolean isSupported = LiveAppUserInfo.getInstance().isSupportedEnglishName();
            String skipPlanId = ShareDataManager.getInstance().getString(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_SKIPED, "-1",
                    ShareDataManager.SHAREDATA_USER);
            boolean isSkip = skipPlanId.equals(planId);
            if (bundle.getInt("pattern") == 8 && !isSupported && !isSkip) {
                XueErSiRouter.startModule(mContext, "/groupclass/englishname"
                        , bundle);
            } else {
                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
            }
            finish();
        }
    }

    /**
     * 大班直播 入口接口
     *
     * @param bundle
     * @param vSectionID
     * @param liveType
     * @param from
     * @param stuCould
     * @param httpManager
     */
    private void enterBigLive(final Bundle bundle, final String vSectionID, final int liveType,
                              final int from, String stuCould,
                              LiveHttpManager httpManager) {
        int planId = Integer.parseInt(vSectionID);
        int iStuCouId = Integer.parseInt(stuCould);

        httpManager.bigLiveEnter(planId, LiveBusinessResponseParser.getBizIdFromLiveType(liveType),
                iStuCouId, BigLiveCfg.BIGLIVE_CURRENT_ACCEPTPLANVERSION,
                new HttpCallBack(mDataLoadEntity, false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        LiveBusinessResponseParser mHttpResponseParser =
                                new LiveBusinessResponseParser();
                        JSONObject object = (JSONObject) responseEntity.getJsonObject();
                        LiveTopic mLiveTopic = new LiveTopic();
                        LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveEnter(object,
                                mLiveTopic, liveType, from);
                        if (mGetInfo == null) {
                            XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                            finish();
                            return;
                        }

                        // 传递皮肤属性
                        bundle.putInt("skinType", mGetInfo.getSkinType());
                        String stuId = LiveAppUserInfo.getInstance().getStuId();
                        getInfos.put(liveType + "-" + stuId + "-" + vSectionID, mGetInfo);
                        com.xueersi.parentsmeeting.modules.livevideo.fragment.BigLiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
                        finish();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "初始化失败");
                        finish();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        int status = responseEntity.getmStatus();
                        if (10 != status) {
                            XESToastUtils.showToast(responseEntity.getErrorMsg());
                            finishAndExit();
                        }
                    }
                });
    }

    /**
     * 是否是整合直播间
     *
     * @return
     */
    private boolean isBigLiveRoom() {
        Bundle bundle = getIntent().getExtras();
        boolean isBigLive = bundle != null && bundle.getBoolean("isBigLive", false);
        return isBigLive;
    }

    //新课件灰测
    public boolean isNewCourse(String liveId) {
        for (String itemLiveId : PreloadStaticStorage.preloadLiveId) {
            if (itemLiveId.equals(liveId)) {
                return true;
            }
        }
        String liveIds =
                ShareDataManager.getInstance().getString(ShareDataConfig.SP_PRELOAD_COURSEWARE, "",
                        ShareDataManager.SHAREDATA_USER);
        if (liveIds.contains(",")) {
            String[] preLoadLiveId = liveIds.split(",");
            for (String tempPreLoadLiveId : preLoadLiveId) {
                if (tempPreLoadLiveId.equals(liveId)) {
                    return true;
                }
            }
        }
        if (!TextUtils.isEmpty(liveIds)) {
            if (liveIds.equals(liveId)) {
                return true;
            }
        }

        return false;
    }

    void gotoEnglish(final Bundle bundle) {
        if (bundle.getInt("pattern") == 8) {
            gotoGroupClass(bundle);
            return;
        }
        boolean have = XesPermission.checkPermission(this, new LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        LiveMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        LiveMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
                                finish();
                            }
                        });
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO);
        if (have) {
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
            finish();
        }
    }

    /**
     *
     */
    void gotoHalfBodyChinese(final Bundle bundle, List<Integer> list) {
        boolean have = XesPermission.checkPermission(this, new LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {
                        LiveMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        logger.i("onDeny");
//                        if (MediaPlayer.getIsNewIJK()) {
//                            com.xueersi.parentsmeeting.modules.livevideo.fragment
//                            .LiveVideoActivity.intentTo
//                            (LiveVideoLoadActivity.this, bundle);
//                        } else {
//                            com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment
//                            .LiveVideoActivity.intentTo
//                            (LiveVideoLoadActivity.this, bundle);
//                        }
//                        finish();
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        logger.i("onGuarantee");
                    }
                },
                PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);
        //魅族手机无法弹出权限弹窗
        if (have) {
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentToAfterOther(LiveVideoLoadActivity.this, bundle, otherActivityIntent);
            finish();

        }
    }

    private void finishAndExit() {
        LiveMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 700);
        LiveMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 1000);
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {

        //低端机设备检测页拦截
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {

            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return;
        }

        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(0, 0);
    }

    public static void intentAfterTOtherActivity(Activity context, Bundle bundle, int requestCode, Intent otherIntent) {

        //低端机设备检测页拦截
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {

            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return;
        }

        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(OTHER_ACTIVITY_INTENT, otherIntent);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(0, 0);
    }

    /**
     * 是否是 语文半身直播
     *
     * @return
     */
    private boolean isChineseHalfBodyLive(LiveGetInfo liveGetInfo) {
        return liveGetInfo != null && liveGetInfo.getPattern()
                == LiveVideoConfig.LIVE_TYPE_HALFBODY
                && liveGetInfo.getIsArts() == LiveVideoSAConfig.ART_CH;
    }

}
