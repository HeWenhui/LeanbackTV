package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;

public class LiveTransferHttpManager extends BaseHttpBusiness {

    public LiveTransferHttpManager(Context context) {
        super(context);
    }

    /**
     * 扣除金币
     *
     * @param requestCallBack
     */
    public void deductStuGold(String stuCouId, String courseId, String liveId, int isGetPlanInfo,
                              HttpCallBack
                                      requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("isGetPlanInfo", "" + isGetPlanInfo);
        params.addBodyParam("sessid", LiveAppUserInfo.getInstance().getSessionId());
        sendPost(ShareBusinessConfig.URL_STUDY_GET_LIVE_COURSE_TEST_INFO_FOR_PLAYBACK, params, requestCallBack);
    }

    /**
     * 文科新课件平台回放事件的新接口
     *
     * @param liveId
     * @param requestCallBack
     */
    public void artscoursewarenewpoint(String liveId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        sendPost(LiveHttpConfig.HTTP_APP_ENGLISH_HOST + "/v2/playback/getEvent", params, requestCallBack);
    }

}
