package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by lyqai on 2018/6/26.
 * 直播心跳
 */
public class UserOnline {
    private LiveGetInfo mGetInfo;
    private int mHbTime = LiveVideoConfig.LIVE_HB_TIME, mHbCount = 0;
    private LiveHttpManager mHttpManager;
    private String mCurrentDutyId;
    private int mLiveType;
    private String mLiveId;
    /** 用户心跳解析错误 */
    private int userOnlineError = 0;
    private LogToFile mLogtf;
    private String TAG = "UserOnline";
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Activity activity;

    public UserOnline(Activity activity, int mLiveType, String mLiveId) {
        this.activity = activity;
        this.mLiveType = mLiveType;
        this.mLiveId = mLiveId;
        mLogtf = new LogToFile(activity, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
    }

    public void setHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
    }

    /**
     * 用户心跳倒计时
     */
    private Runnable mUserOnlineCall = new Runnable() {

        @Override
        public void run() {
            getUserOnline();
        }
    };

    public void start() {
        mainHandler.postDelayed(mUserOnlineCall, mHbTime * 1000);
    }

    public void stop() {
        mainHandler.removeCallbacks(mUserOnlineCall);
    }

    /**
     * 用户在线心跳
     */
    private void getUserOnline() {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String teacherId = "";
        if (mGetInfo != null) {
            teacherId = mGetInfo.getTeacherId();
        }
        final String finalTeacherId = teacherId;
        mHbCount++;
        mHttpManager.liveUserOnline(mLiveType, enstuId, mLiveId, teacherId, mCurrentDutyId, mHbTime, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                onFinished();
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                onFinished();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                onFinished();
            }

            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result).getJSONObject("result");
                    int status = object.getInt("status");
                    if (status == 1) {
                        Object dataObj = object.get("data");
                        if (dataObj instanceof JSONObject) {
                            JSONObject data = (JSONObject) dataObj;
                            mLogtf.d("getUserOnline:time=" + data.get("time"));
                        } else {
                            mLogtf.d("getUserOnline:time=" + dataObj);
                        }
                    } else {
                        mLogtf.d("getUserOnline:result=" + result);
                    }
                    userOnlineError = 0;
                } catch (JSONException e) {
                    if (userOnlineError > 5) {
                        return;
                    }
                    userOnlineError++;
                    if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
                        //liveId
                        //teacherId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "teacherId=" + finalTeacherId +
                                ",result=" + result);
                    } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_TUTORIAL) {
                        //classId
                        //dutyId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "mCurrentDutyId=" +
                                mCurrentDutyId + ",result=" + result);
                    } else if (mLiveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
                        //liveId
                        mLogtf.d("getUserOnline(JSONException):enstuId=" + enstuId + ",mHbCount=" + mHbCount + "," +
                                "result=" + result);
                    }
                    MobAgent.httpResponseParserError(TAG, "getUserOnline", result);
                }
            }

            public void onFinished() {
                postDelayedIfNotFinish(mUserOnlineCall, mHbTime * 1000);
            }
        });
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mainHandler.postDelayed(r, delayMillis);
    }
}
