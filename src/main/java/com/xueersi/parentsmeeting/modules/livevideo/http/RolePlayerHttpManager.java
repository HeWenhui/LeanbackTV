package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

/**
 * RolePlay网络请求
 * Created by zouhao on 2018/4/13.
 */

public class RolePlayerHttpManager extends BaseHttpBusiness {

    public RolePlayerHttpManager(Context context) {
        super(context);
    }


    /**
     * 请求试题
     *
     * @param liveId
     * @param stuCouId
     * @param testId
     */
    public void requestRolePlayTestInfos(String liveId, String stuCouId, String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("testId", testId);
        sendPost(LiveVideoConfig.URL_ROLEPLAY_TESTINFOS, params, requestCallBack);
    }

    /**
     * 结果请求
     *
     * @param liveId
     * @param testId
     * @param roler
     * @param answer
     * @param requestCallBack
     */
    public void requestResult(String liveId, String testId, String roler, String answer, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("roler", roler);
        params.addBodyParam("data", answer);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveVideoConfig.URL_ROLEPLAY_RESULT, params, requestCallBack);
    }
}
