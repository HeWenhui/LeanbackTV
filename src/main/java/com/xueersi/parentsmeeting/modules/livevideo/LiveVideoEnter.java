package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsListener;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.route.ReflexCenter;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.toast.XesToast;
import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.common.util.XrsBroswer;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassLiveActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.DeviceDetectionActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoTransferActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dispatcher.DispatcherBll;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.SettingEnglishLandActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dataload.DataLoadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 直播模块对外入口
 * Created by linyuqiang on 2016/11/22.
 */
public class LiveVideoEnter {
    /**直播课*/
    /**
     * StudyCenterItem
     */
    public static final int ENTER_FROM_1 = 1;
    /**
     * VideoSectionPager
     */
    public static final int ENTER_FROM_2 = 2;
    /**
     * LiveNoticeItem
     */
    public static final int ENTER_FROM_3 = 3;
    /**
     * intentToLiveVideoWithUrl
     */
    public static final int ENTER_FROM_4 = 4;
    /**
     * PushBll
     */
    public static final int ENTER_FROM_5 = 5;
    /**
     * LiveExpeDetailActivity
     */
    public static final int ENTER_FROM_6 = 6;
    /**直播辅导*/
    /**
     * LivePlayBackActivity
     */
    public static final int ENTER_FROM_11 = 11;
    /**
     * LiveNoticeItem
     */
    public static final int ENTER_FROM_12 = 12;
    /**
     * LiveNoticeItem
     */
    public static final int ENTER_FROM_13 = 13;
    /**直播讲座*/
    /**
     * PublicLiveDetailActivity
     */
    public static final int ENTER_FROM_21 = 21;
    /**
     * PublicLiveDetailActivity
     */
    public static final int ENTER_FROM_22 = 22;
    /**
     * PublicLiveSeriesReserveActivity
     */
    public static final int ENTER_FROM_23 = 23;
    /**
     * LiveNoticeItem
     */
    public static final int ENTER_FROM_24 = 24;
    /**
     * PushBll
     */
    public static final int ENTER_FROM_25 = 25;
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();
    public static final String ENTER_ROOM_FROM = "from";

    /**
     * 跳转到直播,直播课，通过网页,已经废弃
     * //https://live.xueersi.com/Live/index/30641
     *
     * @param context
     * @param mUri    网页地址
     */
    @Deprecated
    public static boolean intentToLiveVideoWithUrl(Activity context, String mUri) {
        return false;
    }

    /**
     * 跳转到直播,直播课,体验直播使用
     *
     * @param context
     * @param vSectionID 节id
     * @param from       入口
     */
    @Deprecated
    public static boolean intentToLiveVideoActivity(Activity context, String courseId,
                                                    String vSectionID, int from) {

        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("courseId", courseId);
        bundle.putString("vSectionID", vSectionID);
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LIVE);
        bundle.putInt(ENTER_ROOM_FROM, from);
        LiveVideoLoadActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
        return true;
    }

    /**
     * 跳转到直播,直播课
     *
     * @param context
     * @param vSectionID 节id
     * @param from       入口
     */
    public static boolean intentToLiveVideoActivity(final Activity context,
                                                    final String vStuCourseID, final String
                                                            courseId, final String vSectionID,
                                                    final int from, boolean isBigLive) {

        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        //这样只有一个loading
        final Bundle bundle = new Bundle();
        bundle.putString("courseId", courseId);
        bundle.putString("vStuCourseID", vStuCourseID);
        bundle.putString("vSectionID", vSectionID);
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LIVE);
        bundle.putBoolean("loadAsserts", false);
        bundle.putBoolean("isBigLive", isBigLive);
        bundle.putInt(ENTER_ROOM_FROM, from);
        LiveVideoLoadActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);

        return true;
    }

    /**
     * 进入旁听课堂
     *
     * @param context
     * @param vSectionID
     */
    public static boolean intentToAuditClassActivity(final Activity context,
                                                     final String stuCouId,
                                                     final String vSectionID) {

        //低端机设备检测页拦截
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {
            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return false;
        }

        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                AuditClassLiveActivity.intentTo(context, stuCouId, vSectionID);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });

        return true;
    }


    /**
     * 跳转到直播, 公开直播
     *
     * @param context
     * @param vSectionID 节id
     * @param from       入口
     */
    public static void intentToLiveVideoActivityLecture(final Activity context,
                                                        final String vSectionID,
                                                        final int from, final boolean isBiglive) {
        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {

                Bundle bundle = new Bundle();
                bundle.putString("vSectionID", vSectionID);
                bundle.putBoolean("isBigLive", isBiglive);
                bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LECTURE);
                bundle.putBoolean("loadAsserts", true);
                bundle.putInt(ENTER_ROOM_FROM, from);
                //        LectureLiveVideoActivity.intentTo(context, bundle,
                //        LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
                LiveVideoLoadActivity.intentTo(context, bundle,
                        LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });


    }

    /**
     * 跳转到直播, 公开直播
     *
     * @param context
     * @param vSectionID 节id
     * @param from       入口
     */
    public static void intentToLiveVideoActivityLecture(final Activity context,
                                                        final String vSectionID,
                                                        final int from) {
        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {

                Bundle bundle = new Bundle();
                bundle.putString("vSectionID", vSectionID);
                bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LECTURE);
                bundle.putBoolean("loadAsserts", true);
                bundle.putInt(ENTER_ROOM_FROM, from);
                //        LectureLiveVideoActivity.intentTo(context, bundle,
                //        LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
                LiveVideoLoadActivity.intentTo(context, bundle,
                        LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });


    }

    /**
     * 跳转到直播,直播辅导
     *
     * @param context
     * @param vSectionID    节id
     * @param currentDutyId 正在进行直播的场次ID
     * @param from          入口
     */
    @Deprecated
    public static void intentToLiveVideoActivityTutorial(final Activity context,
                                                         final String vSectionID,
                                                         final String currentDutyId,
                                                         final int from) {
        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "节id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentDutyId)) {
            Toast.makeText(context, "场次id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {

                Bundle bundle = new Bundle();
                bundle.putString("vSectionID", vSectionID);
                bundle.putString("currentDutyId", currentDutyId);
                bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_TUTORIAL);
                bundle.putInt(ENTER_ROOM_FROM, from);
                LiveVideoLoadActivity.intentTo(context, bundle,
                        LiveVideoBusinessConfig.LIVE_REQUEST_CODE);

            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
    }

    /**
     * 直播课直播
     *
     * @param jsonObject
     * @param context
     */
    public static Intent setLiveCourseLiveIntent(JSONObject jsonObject, Context context) {
        if (TextUtils.isEmpty(jsonObject.optString("vSectionId"))) {
            return null;
        }
        //低端机设备检测页拦截
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {

            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("vSectionID", jsonObject.optString("vSectionId"));
        bundle.putString("vStuCourseID", jsonObject.optString("stuCouId"));
        bundle.putString("courseId", jsonObject.optString("courseId"));
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LIVE);
        bundle.putInt(ENTER_ROOM_FROM, LiveVideoBusinessConfig.ENTER_FROM_5);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 录播课直播
     *
     * @param jsonObject
     * @param context
     */
    @Deprecated
    public static Intent setRecordCourseLiveIntent(JSONObject jsonObject, Context context) {

        if (TextUtils.isEmpty(jsonObject.optString("vSectionId")) || TextUtils.isEmpty(jsonObject.optString
                ("currentDutyId"))) {
            return null;
        }
        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("vSectionID", jsonObject.optString("vSectionId"));
        bundle.putString("currentDutyId", jsonObject.optString("currentDutyId"));
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_TUTORIAL);
        bundle.putInt(ENTER_ROOM_FROM, LiveVideoBusinessConfig.ENTER_FROM_13);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 公开课直播
     *
     * @param jsonObject
     * @param context
     */
    public static Intent setPublicCourseLiveIntent(JSONObject jsonObject, Context context) {
        if (TextUtils.isEmpty(jsonObject.optString("courseId"))) {
            return null;
        }

        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("vSectionID", jsonObject.optString("courseId"));
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LECTURE);
        bundle.putInt(ENTER_ROOM_FROM, LiveVideoBusinessConfig.ENTER_FROM_25);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 播放器请求
     */
    public static final int VIDEO_REQUEST = 210;

    /**
     * 跳转到播放器(直播回放)
     *
     * @param context
     * @param bundle
     */
    public static boolean intentTo(final Activity context, final Bundle bundle,
                                   final String where) {

        int pattern = bundle.getInt("pattern", 1);
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {
            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return false;
        }

        //小组课先检测权限
        if (pattern == 8) {
            checkPermisson(context, bundle, where);
        } else {
            loadResource(context, bundle, where);
        }

        return true;


    }

    private static void loadResource(final Activity context, final Bundle bundle,
                                     final String where) {

        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                boolean isNeed = LiveAppUserInfo.getInstance().isNeedEnglishName();
                if (bundle.getInt("pattern") == 8 && !isNeed) {

                    start1v2PlayBack(context, bundle, where);
                } else {
                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(context, bundle,

                            where, VIDEO_REQUEST);
                }
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
    }

    private static void checkPermisson(final Activity context, final Bundle bundle,
                                       final String where) {
        boolean have = XesPermission.checkPermission(context, new LiveActivityPermissionCallback() {

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
                                loadResource(context, bundle, where);
                            }
                        });
                    }
                },
                PermissionConfig.PERMISSION_CODE_AUDIO, PermissionConfig.PERMISSION_CODE_STORAGE);
        if (have) {
            loadResource(context, bundle, where);
        }
    }

    private static void start1v2PlayBack(final Activity context, final Bundle bundle,
                                         final String where) {
        bundle.putBoolean("engish1v2Type", false);
        bundle.putString("where", where);
        XueErSiRouter.startModule(context, "/groupclass/englishname", bundle);
    }

    private static void android5X5Check(final Activity context, final Bundle bundle,
                                        final String where) {
        final DataLoadEntity mDataLoadEntity = new DataLoadEntity(context);

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                boolean init = XrsBroswer.init(context, new TbsListener() {
                    @Override
                    public void onDownloadFinish(int i) {

                    }

                    @Override
                    public void onInstallFinish(int i) {
                        StableLogHashMap logHashMap = new StableLogHashMap("onInstallFinish_back");
                        logHashMap.put("code", "" + i);
                        UmsAgentManager.umsAgentDebug(ContextManager.getContext(),
                                LogConfig.LIVE_X5_LOG, logHashMap.getData());
                        EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(mDataLoadEntity.webDataSuccess()));
                        intentToPlayback(context, bundle, where);
                    }

                    @Override
                    public void onDownloadProgress(int i) {
                        mDataLoadEntity.setProgressTip("下载中" + (i * 100 / 120) + "%");
                        mDataLoadEntity.beginLoading();
                        DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);
                        BaseBll.postDataLoadEvent(mDataLoadEntity);
                    }
                });
//                StableLogHashMap logHashMap = new StableLogHashMap("init_back");
//                logHashMap.put("status", "" + init);
//                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LogConfig
//                .LIVE_X5_LOG, logHashMap.getData());
                if (!init) {
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new AppEvent.OnDataLoadingEvent(mDataLoadEntity.webDataSuccess()));
        }
        intentToPlayback(context, bundle, where);

    }

    private static void intentToPlayback(Activity context, Bundle bundle, String where) {
        com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(context, bundle,
                where, VIDEO_REQUEST);
    }

    /**
     * 跳转到三分屏体验直播播放器
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToExperience(final Activity context, final Bundle bundle,
                                             final String where) {
//        ExperEnter.intentToExperience(context, bundle, where);
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.livevideo.enter" +
                        ".ExperEnter",
                "intentToExperience", new Class[]{Activity.class, Bundle.class, String.class},
                new Object[]{context, bundle, where});
        return true;
    }


    /**
     * 跳转到半身直播体验课
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToHalfBodyExperience(final Activity context, final Bundle bundle,
                                                     final String where) {
//        ExperEnter.intentToHalfBodyExperience(context, bundle, where);
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.livevideo.enter" +
                        ".ExperEnter",
                "intentToHalfBodyExperience", new Class[]{Activity.class, Bundle.class,
                        String.class}, new Object[]{context, bundle, where});
        return true;
    }

    public static boolean intentToLiveBackExperience(final Activity context, final Bundle bundle,
                                                     final String where) {
//        ExperEnter.intentToLiveBackExperience(context, bundle, where);
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.livevideo.enter" +
                        ".ExperEnter",
                "intentToLiveBackExperience", new Class[]{Activity.class, Bundle.class,
                        String.class}, new Object[]{context, bundle, where});
        return true;
    }

    /**
     * 跳转到三分屏AI体验直播播放器
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToAIExperience(final Activity context, final Bundle bundle,
                                               final String where) {
//        ExperEnter.intentToAIExperience(context, bundle, where);
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.livevideo.enter" +
                        ".ExperEnter",
                "intentToAIExperience", new Class[]{Activity.class, Bundle.class, String.class},
                new Object[]{context, bundle, where});
        return true;
    }

    /**
     * 跳转到全身直播体验课
     *
     * @param activity
     * @param bundle
     * @param where
     * @return
     */
    public static boolean intentToStandExperience(final Activity activity, final Bundle bundle,
                                                  final String where) {
        LiveAssetsLoadUtil.loadAssertsResource(activity, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                LivePlaybackVideoActivity.intentTo(activity, bundle,
                        where, VIDEO_REQUEST);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
        return true;
    }

    public static void intentToLivePlayBackVideo(Activity activity, Bundle bundle) {
        LiveVideoTransferActivity.intentTo(activity, bundle);
    }

    /**
     * 跳转到直播回放(已废弃)
     *
     * @param context
     * @param bundle
     */
    public static void intentToLectureLivePlayBackVideo(final Activity context,
                                                        final Bundle bundle, final String where) {

        XESToastUtils.showToast(context, "已暂停服务");
//        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void success() {
//                LectureLivePlayBackVideoActivity.intentTo(context, bundle, where);
//            }
//
//            @Override
//            public void progress(float progress, int type) {
//
//            }
//
//            @Override
//            public void fail(int errorCode, String errorMsg) {
//
//            }
//        });
    }

    /** xesmall进体验课 */
    public static void intentToExper(Context context, String chapterName, String liveId,
                                     final String termId) {
        VideoSectionEntity sectionEntity = new VideoSectionEntity();
        sectionEntity.setvSectionName(chapterName);
        sectionEntity.setvChapterName(chapterName);
        DispatcherBll dispatcherBll = new DispatcherBll(context);
        dispatcherBll.deductStuGolds(sectionEntity, liveId, termId);
    }
}
