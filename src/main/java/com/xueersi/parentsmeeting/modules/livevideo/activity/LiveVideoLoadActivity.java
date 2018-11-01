package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        LogToFile.LIVE_TIME++;
        initData();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String vSectionID = intent.getStringExtra("vSectionID");
        final int liveType = bundle.getInt("type", 0);
        final int from = intent.getIntExtra("", 0);
        DataLoadEntity dataLoadEntity = new DataLoadEntity(this);
        BaseBll.postDataLoadEvent(dataLoadEntity.beginLoading());
        LiveHttpManager httpManager = new LiveHttpManager(this);
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
                    String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                    getInfos.put(stuId + "-" + vStuCourseID + "-" + vSectionID, mGetInfo);
//                    mGetInfo.setPattern(1);
                    bundle.putString("mode", mGetInfo.getMode());
                    bundle.putInt("isArts", mGetInfo.getIsArts());
                    bundle.putInt("pattern", mGetInfo.getPattern());
                    bundle.putBoolean("isPrimary", LiveVideoConfig.isPrimary);
                    if (mGetInfo.getIsArts() == 0) {
                        bundle.putInt("allowLinkMicNew", mGetInfo.getAllowLinkMicNew());
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
}
