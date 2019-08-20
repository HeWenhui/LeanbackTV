package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.business.sharebusiness.http.downloadAppfile.entity.DownLoadFileInfo;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.route.module.ModuleHandler;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.common.util.LoadFileUtils;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.FileLogger;
import com.xueersi.parentsmeeting.modules.livevideo.LiveAssetsLoadUtil;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreloadStaticStorage;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveBusinessResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
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
    String TAG = "LiveVideoLoadActivity";
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();
    /**
     * Activity创建次数
     */
    public static int CREATE_TIMES = 0;
    DataLoadEntity mDataLoadEntity;
    protected static ArrayList<LiveVideoLoadActivity> liveVideoLoadActivities = new ArrayList<>();
    private static int statIndex = 0;
    protected int index;

    public LiveVideoLoadActivity() {
        liveVideoLoadActivities.add(this);
        index = statIndex++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        String token = LiveAppUserInfo.getInstance().getUserToken();
        //如果没有token，只能重新点击进入了
        if (StringUtils.isEmpty(token)) {
            XESToastUtils.showToast(this, "登录信息失效，重新登录");
            StableLogHashMap logHashMap = new StableLogHashMap();
            String rfh = LiveAppUserInfo.getInstance().getUserRfh();
            logHashMap.put("token", "" + token);
            logHashMap.put("rfh", "" + rfh);
            logHashMap.put("create_times", "" + CREATE_TIMES);
            //距离进程创建的时间
            logHashMap.put("app_time", "" + (System.currentTimeMillis() - UmsConstants.PROCRESS_CREATE_TIME));
            UmsAgentManager.umsAgentDebug(this, LogConfig.LIVE_TOKEN_NULL, logHashMap.getData());
            finishAndExit();
            return;
        }
        CREATE_TIMES++;

        mDataLoadEntity = new DataLoadEntity(this);
        boolean loadAsserts = getIntent().getBooleanExtra("loadAsserts", false);
        if (!loadAsserts && LiveVideoConfig.assetsDownloadTag) {
            loadAssertsResource();
        } else {
            mDataLoadEntity.beginLoading();
            DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity);
            initData();
        }
    }

    private boolean initData = false;

    /**
     * 加载assert 文件
     */
    private void loadAssertsResource() {


        //服务端获取
        DownLoadFileInfo info = LiveVideoConfig.getDownLoadFileInfo();

        LoadFileUtils.loadFileFromServer(this, info, new LoadFileCallBack() {
            @Override
            public void start() {
                //XESToastUtils.showToast(LiveVideoLoadActivity.this, "开始加载");
                mDataLoadEntity.beginLoading();
                DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity);
            }

            @Override
            public void success() {
                if (initData) {
                    logger.e("loadAssertsResource:success", new Exception());
                    LiveCrashReport.postCatchedException(TAG, new Exception());
                    return;
                }
                initData = true;
                initData();
                //XESToastUtils.showToast(LiveVideoLoadActivity.this, "加载成功");
            }

            @Override
            public void progress(float progress, int type) {
                if (type == 0) {
                    mDataLoadEntity.setProgressTip("加载中" + (int) (progress) + "%");
                } else {
                    mDataLoadEntity.setProgressTip("解压中...");
                }
                mDataLoadEntity.beginLoading();
                mDataLoadEntity.setCurrentLoadingStatus(DataLoadEntity.DATA_PROGRESS);
                DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity);

            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                if (!LiveAssetsLoadUtil.planB("livevdieo", LiveVideoLoadActivity.this)) {
                    XESToastUtils.showToast(LiveVideoLoadActivity.this, "加载失败,  请重试");
                }
                UmsAgentManager.umsAgentDebug(LiveVideoLoadActivity.this, ModuleHandler.TAG, "直播加载assets 失败,内部0 ！");
            }
        });

    }

    @Override
    protected void onDestroy() {
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
//                mHttpManager.getScienceCourewareInfo(liveId, new CoursewarePreload.CoursewareHttpCallBack());
//            } else if (1 == mSubject) {//英语
//                logger.i("下载英语");
//                mHttpManager.getEnglishCourewareInfo(liveId, new CoursewarePreload.CoursewareHttpCallBack());
//            } else if (2 == mSubject) {//语文
//                logger.i("下载语文");
//                mHttpManager.getArtsCourewareInfo(liveId, new CoursewarePreload.CoursewareHttpCallBack());
//            }
//        }
    }

    private void initData() {
        logger.d("initData:index=" + index + ",size=" + liveVideoLoadActivities.size());
        LiveVideoLoadActivity activity = liveVideoLoadActivities.get(0);
        if (activity != this) {
            DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity.webDataSuccess());
            finish();
            return;
        }
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String vSectionID = intent.getStringExtra("vSectionID");
        final int liveType = bundle.getInt("type", 0);
        final int from = intent.getIntExtra("", 0);


        Log.e("ckTrac", "====>RoomInit_000000:liveType=" + liveType);
        final LiveHttpManager httpManager = new LiveHttpManager(this);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            //大班整合-讲座
            if (isBigLiveRoom()) {
                int planId = Integer.parseInt(vSectionID);
                httpManager.liveIntegratedGetInfo(planId,liveType,0, new HttpCallBack(mDataLoadEntity) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        //Log.e("ckTrac","========>LiveVideoActivity:onPmSuccess+"+responseEntity.getJsonObject().toString());
                        LiveBusinessResponseParser mHttpResponseParser = new LiveBusinessResponseParser();
                        JSONObject object = (JSONObject) responseEntity.getJsonObject();
                        LiveTopic mLiveTopic = new LiveTopic();
                        LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveEnter(object, mLiveTopic, liveType, from);
                        if (mGetInfo == null) {
                            XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                            finish();
                            return;
                        }
                        String stuId = LiveAppUserInfo.getInstance().getStuId();
                        getInfos.put(liveType + "-" + stuId + "-" + vSectionID, mGetInfo);
                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LecVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                        finish();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "初始化失败");
                        finish();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, responseEntity.getErrorMsg());
                        finishAndExit();
                    }
                });
            } else {
                httpManager.liveLectureGetInfo(vSectionID, new HttpCallBack(mDataLoadEntity) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        LiveHttpResponseParser mHttpResponseParser =
                                new LiveHttpResponseParser(LiveVideoLoadActivity.this);
                        JSONObject object = (JSONObject) responseEntity.getJsonObject();
                        LiveTopic mLiveTopic = new LiveTopic();
                        LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, liveType, from);
                        if (mGetInfo == null) {
                            XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                            finish();
                            return;
                        }
                        String stuId = LiveAppUserInfo.getInstance().getStuId();
                        getInfos.put(liveType + "-" + stuId + "-" + vSectionID, mGetInfo);
                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LecVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                        finish();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "初始化失败");
                        finish();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, responseEntity.getErrorMsg());
                        finishAndExit();
                    }
                });
            }

        } else if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            final String vStuCourseID = intent.getStringExtra("vStuCourseID");
            String courseId = intent.getStringExtra("courseId");
            httpManager.addBodyParam("stuCouId", vStuCourseID);
            httpManager.addBodyParam("liveId", vSectionID);
            httpManager.addBodyParam("from", "" + from);
            httpManager.liveGetInfo(courseId, vSectionID, 0, new HttpCallBack(mDataLoadEntity) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    LiveHttpResponseParser mHttpResponseParser = new LiveHttpResponseParser(LiveVideoLoadActivity.this);
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    LiveTopic mLiveTopic = new LiveTopic();
                    LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, liveType, from);
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
//                    bundle.putIntegerArrayList("preloadliveid", PreloadStaticStorage.preloadLiveId);
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
//                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo
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
                        Log.e("ckTrac", "====>RoomInit_11111111:");
                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                        finish();
                    }
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    XESToastUtils.showToast(LiveVideoLoadActivity.this, "初始化失败");
                    finish();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    XESToastUtils.showToast(LiveVideoLoadActivity.this, responseEntity.getErrorMsg());
                    finishAndExit();
                }
            });
        }
    }

    /**
     * 是否是整合直播间
     * @return
     */
    private boolean isBigLiveRoom() {
        // TODO: 2019-08-19 判断是否是整合直播间
        boolean isBigLive = getIntent().getBooleanExtra("isBigLive", false);

        return true;
    }

    //新课件灰测
    public boolean isNewCourse(String liveId) {
        for (String itemLiveId : PreloadStaticStorage.preloadLiveId) {
            if (itemLiveId.equals(liveId)) {
                return true;
            }
        }
        String liveIds = ShareDataManager.getInstance().getString(ShareDataConfig.SP_PRELOAD_COURSEWARE, "",
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
        boolean have = XesPermission.checkPermission(this, new LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        LiveMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                finish();
                            }
                        });
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO);
        if (have) {
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
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
                                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        logger.i("onDeny");
//                        if (MediaPlayer.getIsNewIJK()) {
//                            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo
//                            (LiveVideoLoadActivity.this, bundle);
//                        } else {
//                            com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo
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
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
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
