package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideo.business.courseware.PreloadStaticStorage;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/14.
 * 直播中间的loading
 */
public class LiveVideoLoadActivity extends BaseActivity {
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();
    /** Activity创建次数 */
    public static int CREATE_TIMES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        String token = AppBll.getInstance().getUserToken();
        //如果没有token，只能重新点击进入了
        if (StringUtils.isEmpty(token)) {
            XESToastUtils.showToast(this, "登录信息失效，重新登录");
            StableLogHashMap logHashMap = new StableLogHashMap();
            String rfh = AppBll.getInstance().getUserRfh();
            logHashMap.put("token", "" + token);
            logHashMap.put("rfh", "" + rfh);
            logHashMap.put("create_times", "" + CREATE_TIMES);
            //距离进程创建的时间
            logHashMap.put("app_time", "" + (System.currentTimeMillis() - UmsConstants.PROCRESS_CREATE_TIME));
            UmsAgentManager.umsAgentDebug(this, LogConfig.LIVE_TOKEN_NULL, logHashMap.getData());
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 700);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
            return;
        }
        CREATE_TIMES++;
        initData();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String vSectionID = intent.getStringExtra("vSectionID");
        final int liveType = bundle.getInt("type", 0);
        final int from = intent.getIntExtra("", 0);
        DataLoadEntity dataLoadEntity = new DataLoadEntity(this);
        BaseBll.postDataLoadEvent(dataLoadEntity.beginLoading());
        final LiveHttpManager httpManager = new LiveHttpManager(this);

        if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            httpManager.liveLectureGetInfo("", vSectionID, new HttpCallBack(dataLoadEntity) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    LiveHttpResponseParser mHttpResponseParser = new LiveHttpResponseParser(LiveVideoLoadActivity.this);
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    LiveTopic mLiveTopic = new LiveTopic();
                    LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, liveType, from);


                    if (mGetInfo == null) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                        finish();
                        return;
                    }
//                    performDownLoadPreLoad(httpManager, mGetInfo);
                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
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
                    finish();
                }
            });
        } else if (liveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            final String vStuCourseID = intent.getStringExtra("vStuCourseID");
            String courseId = intent.getStringExtra("courseId");
            httpManager.addBodyParam("stuCouId", vStuCourseID);
            httpManager.addBodyParam("liveId", vSectionID);
            httpManager.addBodyParam("from", "" + from);
            httpManager.liveGetInfo("", courseId, vSectionID, 0, new HttpCallBack(dataLoadEntity) {
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
                    if (isChineseHalfBodyLive(mGetInfo)) {
                        XESToastUtils.showToast(LiveVideoLoadActivity.this, "语文半身直播暂不支持,请升级版本");
                        AppBll.getInstance(mContext).checkPartUpdate("语文半身直播暂不支持,请升级版本");
                        return;
                    }

                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                    getInfos.put(stuId + "-" + vStuCourseID + "-" + vSectionID, mGetInfo);
//                    mGetInfo.setPattern(1);
                    bundle.putString("mode", mGetInfo.getMode());

                    boolean newCourse = isNewCourse(mGetInfo.getId());
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
                    if (mGetInfo.getIsArts() == 0) {
                        bundle.putInt("allowLinkMicNew", mGetInfo.getAllowLinkMicNew());
                    } else {
                        bundle.putInt("smallEnglish", mGetInfo.getSmallEnglish() ? 1 : 0);
                    }
//                if (mGetInfo.getPattern() == 2) {
//                    StandLiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle, -1);
//                } else {
//                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
//                }
                    if (1 == mGetInfo.getIsEnglish()) {
                        gotoEnglish(bundle);
                    } else {
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
                    finish();
                }
            });
        }
    }

    //新课件灰测
    public boolean isNewCourse(String liveId) {
        if (PreloadStaticStorage.preloadLiveId.size() != 0) {
            for (String itemLiveId : PreloadStaticStorage.preloadLiveId) {
                if (itemLiveId.equals(liveId)) {
                    return true;
                }
            }
        } else {
            String liveIds = ShareDataManager.getInstance().getString(ShareDataConfig.SP_PRELOAD_COURSEWARE, "", ShareDataManager.SHAREDATA_USER);
            if (liveIds.contains(",")) {
                String[] preLoadLiveId = liveIds.split(",");
                for (String tempPreLoadLiveId : preLoadLiveId) {
                    if (tempPreLoadLiveId.equals(liveId)) {
                        return true;
                    }
                }
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
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
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
                == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY
                && liveGetInfo.getIsArts() == HalfBodyLiveConfig.LIVE_TYPE_CHINESE;
    }

}
