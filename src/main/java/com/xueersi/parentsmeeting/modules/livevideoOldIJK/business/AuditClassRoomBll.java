package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.config.AuditRoomConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AuditClassRoomEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.AuditClassRoomHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.AuditClassRoomHttpResponseParser;
import com.xueersi.ui.dataload.DataLoadEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huadl on 2017/6/22.
 * 旁听课堂 业务类
 */

public class AuditClassRoomBll extends BaseBll {
    private AuditClassRoomHttpManager mAuditClassRoomHttpManager;
    private AuditClassRoomHttpResponseParser mAudtiClassRoomHttpResponseParser;
    Activity activity;
    private int isArts;

    public AuditClassRoomBll(Activity activity, int isArts) {
        super(activity);
        this.activity = activity;
        this.isArts = isArts;
        mAuditClassRoomHttpManager = new AuditClassRoomHttpManager(activity, isArts);
        mAudtiClassRoomHttpResponseParser = new AuditClassRoomHttpResponseParser();
    }

    /**
     * 旁听课堂数据
     *
     * @param liveId
     * @param auditClassRoomRequestCallBack
     * @param dataLoadEntity
     */
    public void getLiveCourseUserScoreDetail(String liveId, String stuCouId, final AbstractBusinessDataCallBack auditClassRoomRequestCallBack, final DataLoadEntity dataLoadEntity) {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mAuditClassRoomHttpManager.getLiveCourseUserScoreDetail(enstuId, liveId, stuCouId, new HttpCallBack(dataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                AuditClassRoomEntity entity = mAudtiClassRoomHttpResponseParser.parserAuditClassRoomUserScore(responseEntity);
                if (!isEmpty(entity, dataLoadEntity)) {
                    auditClassRoomRequestCallBack.onDataSucess(entity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
            }
        });
    }

    /**
     * 旁听课堂数据
     *
     * @param roomId
     * @param auditClassRoomRequestCallBack
     */
    public void getHasLiveCourse(final Handler handler, final long delayMillis, final String roomId, final AbstractBusinessDataCallBack auditClassRoomRequestCallBack) {
        final String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mAuditClassRoomHttpManager.getHasLiveCourse(enstuId, roomId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                LiveCourseEntity liveCourseEntity = mAudtiClassRoomHttpResponseParser.parserHasAuditClassRoom(responseEntity);
                if (!isEmpty(liveCourseEntity)) {
                    auditClassRoomRequestCallBack.onDataSucess(liveCourseEntity, 1, responseEntity.getJsonObject().toString());
                    mShareDataManager.put(AuditRoomConfig.SP_CHAT_ROOM_LOGIN_LAST_TIME + roomId, System.currentTimeMillis(), ShareDataManager.SHAREDATA_USER);
                    mShareDataManager.put(AuditRoomConfig.SP_CHAT_ROOM_LIVE_DATA + roomId, JSON.toJSONString(liveCourseEntity), ShareDataManager.SHAREDATA_USER);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                Loger.e(TAG, "getHasLiveCourse:onPmFailure:msg=" + msg);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!activity.isFinishing()) {
                            getHasLiveCourse(handler, delayMillis + 1000, roomId, auditClassRoomRequestCallBack);
                        }
                    }
                }, delayMillis);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                //     XESToastUtils.showToast(mContext, responseEntity.getErrorMsg() + " 不能旁听");
                Loger.e(TAG, "getHasLiveCourse:onPmFailure:ErrorMsg=" + responseEntity.getErrorMsg());
                auditClassRoomRequestCallBack.onDataFail(0, responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 判断是否需要请求
     *
     * @param roomId
     * @return true需要请求，false不需要请求
     */
    public boolean isRequestLivingData(String roomId) {
        long currentTimeMillis = System.currentTimeMillis();
        long lastTime = mShareDataManager.getLong(AuditRoomConfig.SP_CHAT_ROOM_LOGIN_LAST_TIME + roomId, 0, ShareDataManager.SHAREDATA_USER);
        return currentTimeMillis - lastTime > 1500 * 1000;
    }

    /**
     * 旁听课堂数据
     *
     * @param roomId
     * @param auditClassRoomRequestCallBack
     */
    public boolean getLocalLiveCourse(String roomId, final AbstractBusinessDataCallBack auditClassRoomRequestCallBack) {
        String liveData = mShareDataManager.getString(AuditRoomConfig.SP_CHAT_ROOM_LIVE_DATA + roomId, "", ShareDataManager.SHAREDATA_USER);
        if (!TextUtils.isEmpty(liveData)) {
            JSONObject liveJson = null;
            try {
                liveJson = new JSONObject(liveData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LiveCourseEntity liveCourseEntity = mAudtiClassRoomHttpResponseParser.parserHasAuditClassRoom(liveJson);
            if (!isEmpty(liveCourseEntity)) {
                auditClassRoomRequestCallBack.onDataSucess(liveCourseEntity, 2, liveData);
                return true;
            }
        }
        return false;
    }

}
