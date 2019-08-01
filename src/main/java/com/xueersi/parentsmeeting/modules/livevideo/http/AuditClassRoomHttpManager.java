package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.AuditRoomConfig;

/**
 * 旁听课堂网络请求层
 * Created by hua on 2017-06-30.
 */
public class AuditClassRoomHttpManager extends BaseHttpBusiness {

    private int isArts;

    public AuditClassRoomHttpManager(Context context, int isArts) {
        super(context);
        this.isArts = isArts;
    }


    /**
     * 旁听课堂数据
     *
     * @param enstuId
     * @param liveId
     * @param requestCallBack
     */
    public void getLiveCourseUserScoreDetail(String liveId, String stuCouId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
//        params.addBodyParam("enstuId", enstuId);
        params.addBodyParam("stuCouId", stuCouId);
        if (isArts == 1) {
            sendPost(AuditRoomConfig.URL_LIVE_COURSE_LIVE_DETAIL_A, params, requestCallBack);
        } else if (isArts == 2) {
            sendPost(AuditRoomConfig.URL_LIVE_CHS_COURSE, params, requestCallBack);
        } else {
            sendPost(AuditRoomConfig.URL_LIVE_COURSE_LIVE_DETAIL_S, params, requestCallBack);
        }
    }

    /**
     * 是否有旁听课堂数据
     *
     * @param roomId
     * @param requestCallBack
     */
    public void getHasLiveCourse(String roomId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("rid", roomId);
//        params.addBodyParam("enstuId", enstuId);
        sendPost(AuditRoomConfig.URL_HAS_LIVE_COURSE, params, requestCallBack);
    }
}
