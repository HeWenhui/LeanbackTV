package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassLiveActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.DeviceDetectionActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.ExperienceLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LectureLivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 直播模块对外入口
 * Created by linyuqiang on 2016/11/22.
 */
public class LiveVideoEnter {
    /**直播课*/
    /** StudyCenterItem */
    public static final int ENTER_FROM_1 = 1;
    /** VideoSectionPager */
    public static final int ENTER_FROM_2 = 2;
    /** LiveNoticeItem */
    public static final int ENTER_FROM_3 = 3;
    /** intentToLiveVideoWithUrl */
    public static final int ENTER_FROM_4 = 4;
    /** PushBll */
    public static final int ENTER_FROM_5 = 5;
    /** LiveExpeDetailActivity */
    public static final int ENTER_FROM_6 = 6;
    /**直播辅导*/
    /** LivePlayBackActivity */
    public static final int ENTER_FROM_11 = 11;
    /** LiveNoticeItem */
    public static final int ENTER_FROM_12 = 12;
    /** LiveNoticeItem */
    public static final int ENTER_FROM_13 = 13;
    /**直播讲座*/
    /** PublicLiveDetailActivity */
    public static final int ENTER_FROM_21 = 21;
    /** PublicLiveDetailActivity */
    public static final int ENTER_FROM_22 = 22;
    /** PublicLiveSeriesReserveActivity */
    public static final int ENTER_FROM_23 = 23;
    /** LiveNoticeItem */
    public static final int ENTER_FROM_24 = 24;
    /** PushBll */
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
//        try {
//            String name = AppBll.getInstance().getAppInfoEntity().getChildName();
//            if (AppConfig.DEBUG || name.contains("100tal.com") || name.contains("xueersi.com")) {
//                int index = mUri.indexOf("index/");
//                String idUrl = mUri.substring(index + 6);
//                for (int i = 0; i < idUrl.length(); i++) {
//                    char c = idUrl.charAt(i);
//                    if (c < '0' || c > '9') {
//                        index = i;
//                        idUrl = idUrl.substring(0, index);
//                        break;
//                    }
//                }
//                if (intentToLiveVideoActivity(context, "", "", idUrl, LiveVideoBusinessConfig.ENTER_FROM_4)) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            return false;
//        }
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
    public static boolean intentToLiveVideoActivity(Activity context, String courseId, String vSectionID, int from) {

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
    public static boolean intentToLiveVideoActivity(final Activity context, final String vStuCourseID, String
            courseId, final String vSectionID, final int from) {

        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        final Bundle bundle = new Bundle();
        bundle.putString("courseId", courseId);
        bundle.putString("vStuCourseID", vStuCourseID);
        bundle.putString("vSectionID", vSectionID);
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LIVE);
        bundle.putInt(ENTER_ROOM_FROM, from);
        LiveVideoLoadActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
//        DataLoadEntity dataLoadEntity = new DataLoadEntity(context);
//        BaseBll.postDataLoadEvent(dataLoadEntity.beginLoading());
//        LiveHttpManager httpManager = new LiveHttpManager(context);
//        httpManager.addBodyParam("stuCouId", vStuCourseID);
//        httpManager.addBodyParam("liveId", vSectionID);
//        httpManager.addBodyParam("from", "" + from);
//        httpManager.liveGetInfo("", courseId, vSectionID, 0, new HttpCallBack(dataLoadEntity) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                LiveHttpResponseParser mHttpResponseParser = new LiveHttpResponseParser(context);
//                JSONObject object = (JSONObject) responseEntity.getJsonObject();
//                LiveTopic mLiveTopic = new LiveTopic();
//                LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, LiveVideoConfig
// .LIVE_TYPE_LIVE, from);
//                if (mGetInfo == null) {
//                    XESToastUtils.showToast(context, "服务器异常");
//                    return;
//                }
//                String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
//                getInfos.put(stuId + "-" + vStuCourseID + "-" + vSectionID, mGetInfo);
//                bundle.putInt("isArts", mGetInfo.getIsArts());
//                bundle.putInt("pattern", mGetInfo.getPattern());
//                if (mGetInfo.getPattern() == 2) {
//                    StandLiveVideoActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
//                } else {
//
////                    if (mGetInfo.getIsArts() == 1) {
////                        LiveVideoActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
////                    } else {
////                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(context,
/// bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
////                    }
//                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(context,
// bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
//                }
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                XESToastUtils.showToast(context, "初始化失败");
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                XESToastUtils.showToast(context, responseEntity.getErrorMsg());
//            }
//        });
        return true;
    }

    /**
     * 进入旁听课堂
     *
     * @param context
     * @param vSectionID
     */
    public static boolean intentToAuditClassActivity(Activity context, String stuCouId, String vSectionID) {
        //低端机设备检测页拦截
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {
            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return false;
        }
        AuditClassLiveActivity.intentTo(context, stuCouId, vSectionID);
        return true;
    }

    /**
     * 跳转到直播, 公开直播
     *
     * @param context
     * @param vSectionID 节id
     * @param from       入口
     */
    public static void intentToLiveVideoActivityLecture(Activity context, String vSectionID,
                                                        int from) {
        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "直播场次不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("vSectionID", vSectionID);
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_LECTURE);
        bundle.putInt(ENTER_ROOM_FROM, from);
//        LectureLiveVideoActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
        LiveVideoLoadActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
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
    public static void intentToLiveVideoActivityTutorial(Activity context, String vSectionID, String currentDutyId,
                                                         int from) {
        if (TextUtils.isEmpty(vSectionID)) {
            Toast.makeText(context, "节id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentDutyId)) {
            Toast.makeText(context, "场次id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("vSectionID", vSectionID);
        bundle.putString("currentDutyId", currentDutyId);
        bundle.putInt("type", LiveVideoConfig.LIVE_TYPE_TUTORIAL);
        bundle.putInt(ENTER_ROOM_FROM, from);
        LiveVideoLoadActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
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

    /** 播放器请求 */
    public static final int VIDEO_REQUEST = 210;

    /**
     * 跳转到播放器(直播回放)
     *
     * @param context
     * @param bundle
     */
    public static boolean intentTo(Activity context, Bundle bundle, String where) {
//        bundle.putInt("pattern", 2);
        int pattern = bundle.getInt("pattern", 1);
//        if (pattern == 2) {
//            LiveStandPlayBackVideoActivity.intentTo(context, bundle, where, VIDEO_REQUEST);
//        } else {
//            com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(context,
// bundle, where, VIDEO_REQUEST);
////            com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity.intentTo(context,
/// bundle, where, VIDEO_REQUEST);
//        }
        if (ShareDataManager.getInstance().getBoolean(ShareBusinessConfig
                        .SP_APP_DEVICE_NOTICE, false,
                ShareDataManager.SHAREDATA_USER)) {
            Intent intent = new Intent(context, DeviceDetectionActivity.class);
            context.startActivity(intent);
            return false;
        }
        com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(context, bundle,
                where, VIDEO_REQUEST);
        return true;
    }

    /**
     * 跳转到三分屏体验直播播放器
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToExperience(Activity context, Bundle bundle, String where) {
        ExperienceLiveVideoActivity.intentTo(context, bundle, where, VIDEO_REQUEST);
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
    public static boolean intentToStandExperience(Activity activity, Bundle bundle, String where) {
        com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(activity, bundle,
                where, VIDEO_REQUEST);
        return true;
    }

    /**
     * 跳转到直播回放(已废弃)
     *
     * @param context
     * @param bundle
     */
    public static void intentToLectureLivePlayBackVideo(Activity context, Bundle bundle, String where) {
        LectureLivePlayBackVideoActivity.intentTo(context, bundle, where);
    }
}
