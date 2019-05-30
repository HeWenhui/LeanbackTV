package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.business.sharebusiness.http.downloadAppfile.entity.DownLoadFileInfo;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.util.LoadCallback;
import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.common.util.LoadFileUtils;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.courseware.CoursewarePreload;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.courseware.PreloadStaticStorage;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveActivityPermissionCallback;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by linyuqiang on 2018/7/14.
 * 直播中间的loading
 */
public class LiveVideoLoadActivity extends BaseActivity {
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();
    /**
     * Activity创建次数
     */
    public static int CREATE_TIMES = 0;
    DataLoadEntity mDataLoadEntity;

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
        mDataLoadEntity = new DataLoadEntity(this);
        loadAssertsResource();
        //initData();

    }


    /**
     * 加载assert 文件
     */
    private void loadAssertsResource() {

        DownLoadFileInfo info = new DownLoadFileInfo();
        info.fileName = "assets.zip";
        info.fileMD5 = "f94553e8a25d47d107f81fccade5cbcb";
        info.fileType = 0;
        info.fileUrl = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/asserts/livevideo/assets.zip";
        info.needManualDownload = true;
        info.id = 0;
        info.dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();


        LoadFileUtils.loadFileFromServer(this, info, new LoadFileCallBack() {

            @Override
            public void start() {
                //XESToastUtils.showToast(LiveVideoLoadActivity.this, "开始加载");
                //BaseBll.postDataLoadEvent(mDataLoadEntity.beginLoading());
                mDataLoadEntity.beginLoading();
                DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity);
            }

            @Override
            public void success() {
                initData();
                //XESToastUtils.showToast(LiveVideoLoadActivity.this, "加载成功");
            }

            @Override
            public void progress(float progress, int type) {

                //BaseBll.postDataLoadEvent(mDataLoadEntity.setProgressTip("加载中" + (int)(progress)+"%"));
                if(type==0){
                    mDataLoadEntity.setProgressTip("加载中" + (int) (progress) + "%");
                }else{
                    mDataLoadEntity.setProgressTip("解压中...");
                }
                mDataLoadEntity.beginLoading();
                mDataLoadEntity.setCurrentLoadingStatus(DataLoadEntity.DATA_PROGRESS);
                DataLoadManager.newInstance().loadDataStyle(LiveVideoLoadActivity.this, mDataLoadEntity);
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                XESToastUtils.showToast(LiveVideoLoadActivity.this, "加载失败,  请重试");
            }
        });

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
        final LiveHttpManager httpManager = new LiveHttpManager(this);

        if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            httpManager.liveLectureGetInfo("", vSectionID, new HttpCallBack(mDataLoadEntity) {
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
                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                    getInfos.put(liveType + "-" + stuId + "-" + vSectionID, mGetInfo);
                    if (!MediaPlayer.getIsNewIJK()) {
                        com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LecVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                    } else {
                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LecVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                    }
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
            httpManager.liveGetInfo("", courseId, vSectionID, 0, new HttpCallBack(mDataLoadEntity) {
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

                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
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
                    } else if (mGetInfo.isUseGoldMicroPhone() == 1 || mGetInfo.getUseSuperSpeakerShow() == 1) {
                        List<Integer> list = new ArrayList<>();
                        list.add(PermissionConfig.PERMISSION_CODE_AUDIO);
                        list.add(PermissionConfig.PERMISSION_CODE_CAMERA);
                        gotoHalfBodyChinese(bundle, list);
//                        gotoHalfBodyChinese(bundle);
                    } else {
                        if (!MediaPlayer.getIsNewIJK()) {
                            com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                        } else {
                            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                        }
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
        for (String itemLiveId : PreloadStaticStorage.preloadLiveId) {
            if (itemLiveId.equals(liveId)) {
                return true;
            }
        }
        String liveIds = ShareDataManager.getInstance().getString(ShareDataConfig.SP_PRELOAD_COURSEWARE, "", ShareDataManager.SHAREDATA_USER);
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
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!MediaPlayer.getIsNewIJK()) {
                                    com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                } else {
                                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                }
                                finish();
                            }
                        });
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO);
        if (have) {
            if (!MediaPlayer.getIsNewIJK()) {
                com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
            } else {
                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
            }
            finish();
        }
    }

    /**
     *
     */
    void gotoHalfBodyChinese(final Bundle bundle, List<Integer> list) {
        boolean have = XesPermission.checkPermission(this, new com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback() {

                    @Override
                    public void onFinish() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (MediaPlayer.getIsNewIJK()) {
                                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoLoadActivity.this, bundle);
                                } else {
                                    com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoLoadActivity.this, bundle);
                                }
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        logger.i("onDeny");
//                        if (MediaPlayer.getIsNewIJK()) {
//                            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
//                        } else {
//                            com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
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
            if (MediaPlayer.getIsNewIJK()) {
                com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoLoadActivity.this, bundle);
            } else {
                com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.LiveVideoLoadActivity.this, bundle);
            }
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
            Intent intent;
            if (!MediaPlayer.getIsNewIJK()) {
                intent = new Intent(context, DeviceDetectionActivity.class);
            } else {
                intent = new Intent(context, com.xueersi.parentsmeeting.modules.livevideo.activity.DeviceDetectionActivity.class);
            }
            context.startActivity(intent);
            return;
        }

        Intent intent;
        if (!MediaPlayer.getIsNewIJK()) {
            intent = new Intent(context, LiveVideoLoadActivity.class);
        } else {
            intent = new Intent(context, com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity.class);
        }
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
