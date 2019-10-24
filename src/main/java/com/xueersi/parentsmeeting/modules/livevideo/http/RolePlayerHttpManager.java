package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.ExperLiveQueHttpConfig;

import java.util.HashMap;

/**
 * RolePlay网络请求
 * Created by zouhao on 2018/4/13.
 */

public class RolePlayerHttpManager extends BaseHttpBusiness {

    HashMap<String, String> defaultKey = new HashMap<>();
    LiveHttpAction liveHttpAction;

    public RolePlayerHttpManager(Context context, LiveHttpAction liveHttpAction) {
        super(context);
        this.liveHttpAction = liveHttpAction;
    }

    public void addBodyParam(String key, String value) {
        defaultKey.put(key, value);
    }

    void setDefaultParameter(HttpRequestParams httpRequestParams) {
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            httpRequestParams.addBodyParam(key, value);
        }
    }


    /**
     * 请求分组
     *
     * @param liveId
     * @param stuCouId
     * @param testId
     */
    public void requestRolePlayGroupInfos(String liveId, String stuCouId, String testId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("testId", testId);
        sendPost(LiveHttpConfig.URL_ROLEPLAY_TESTINFOS, params, requestCallBack);
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
        sendPost(LiveHttpConfig.URL_ROLEPLAY_TESTINFOS, params, requestCallBack);
    }

    /**
     * 文科新课件平台请求试题
     *
     * @param liveId
     * @param stuCouId
     * @param testId
     * @param stuId
     */
    public void requestNewArtsRolePlayTestInfos(String liveId, String stuCouId, String testId, String stuId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        if (liveHttpAction != null) {
            liveHttpAction.sendPostDefault(LiveHttpConfig.URL_ROLEPLAY_NEWARTS_TESTINFOS, params, requestCallBack);
        } else {
            sendPost(LiveHttpConfig.URL_ROLEPLAY_NEWARTS_TESTINFOS, params, requestCallBack);
        }
    }

    /**
     * 文科新课件平台请求试题-体验课
     *
     * @param liveId
     * @param stuCouId
     * @param testId
     * @param stuId
     */
    public void requestExperNewArtsRolePlayTestInfos(String liveId, String stuCouId, String testId, String stuId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        if (liveHttpAction != null) {
            liveHttpAction.sendPostDefault(ExperLiveQueHttpConfig.URL_ROLEPLAY_NEWARTS_TESTINFOS, params, requestCallBack);
        } else {
            sendPost(ExperLiveQueHttpConfig.URL_ROLEPLAY_NEWARTS_TESTINFOS, params, requestCallBack);
        }
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
    public void requestResult(String stuCouId, String liveId, String testId, String roler, String answer, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("roler", roler);
        params.addBodyParam("data", answer);
        params.setWriteAndreadTimeOut(5);
        sendPost(LiveHttpConfig.URL_ROLEPLAY_RESULT, params, requestCallBack);
    }

    /**
     * 文科新课件平台结果请求
     *
     * @param liveId
     * @param testId
     * @param roler
     * @param answer
     * @param type
     * @param requestCallBack
     */
    public void requestNewArtsResult(String stuCouId, String liveId, String testId, String roler, String answer, int type, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("roler", roler);
        params.addBodyParam("type", "" + type);
        params.addBodyParam("data", answer);
        params.setWriteAndreadTimeOut(5);
        setDefaultParameter(params);
        sendPost(LiveHttpConfig.URL_ROLEPLAY_NEWARTS_RESULT, params, requestCallBack);
    }

    /**
     * 文科新课件平台结果请求
     *
     * @param liveId
     * @param testId
     * @param roler
     * @param answer
     * @param type
     * @param requestCallBack
     */
    public void requestExperNewArtsResult(String stuCouId, String liveId, String testId, String roler, String answer, int type, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("roler", roler);
        params.addBodyParam("type", "" + type);
        params.addBodyParam("data", answer);
        params.setWriteAndreadTimeOut(5);
        setDefaultParameter(params);
        sendPost(ExperLiveQueHttpConfig.URL_ROLEPLAY_NEWARTS_RESULT, params, requestCallBack);
    }
}
