package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.common.base.BaseActivity;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by lyqai on 2018/7/14.
 */

public class LiveVideoLoadActivity extends BaseActivity {
    public static HashMap<String, LiveGetInfo> getInfos = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        final String vStuCourseID = intent.getStringExtra("vStuCourseID");
        String courseId = intent.getStringExtra("courseId");
        final String vSectionID = intent.getStringExtra("vSectionID");
        final int from = intent.getIntExtra("", 0);
        DataLoadEntity dataLoadEntity = new DataLoadEntity(this);
        BaseBll.postDataLoadEvent(dataLoadEntity.beginLoading());
        LiveHttpManager httpManager = new LiveHttpManager(this);
        httpManager.addBodyParam("stuCouId", vStuCourseID);
        httpManager.addBodyParam("liveId", vSectionID);
        httpManager.addBodyParam("from", "" + from);
        httpManager.liveGetInfo("", courseId, vSectionID, 0, new HttpCallBack(dataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                LiveHttpResponseParser mHttpResponseParser = new LiveHttpResponseParser(LiveVideoLoadActivity.this);
                JSONObject object = (JSONObject) responseEntity.getJsonObject();
                LiveTopic mLiveTopic = new LiveTopic();
                LiveGetInfo mGetInfo = mHttpResponseParser.parseLiveGetInfo(object, mLiveTopic, LiveVideoConfig.LIVE_TYPE_LIVE, from);
                if (mGetInfo == null) {
                    XESToastUtils.showToast(LiveVideoLoadActivity.this, "服务器异常");
                    finish();
                    return;
                }
                String stuId = UserBll.getInstance().getMyUserInfoEntity().getStuId();
                getInfos.put(stuId + "-" + vStuCourseID + "-" + vSectionID, mGetInfo);
                bundle.putInt("isArts", mGetInfo.getIsArts());
                bundle.putInt("pattern", mGetInfo.getPattern());
                if (mGetInfo.getPattern() == 2) {
                    StandLiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle, -1);
                } else {

//                    if (mGetInfo.getIsArts() == 1) {
//                        LiveVideoActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
//                    } else {
//                        com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(context, bundle, LiveVideoBusinessConfig.LIVE_REQUEST_CODE);
//                    }
                    com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
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
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, LiveVideoLoadActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(0, 0);
    }
}
