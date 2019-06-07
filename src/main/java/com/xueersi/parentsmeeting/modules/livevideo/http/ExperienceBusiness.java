package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import org.json.JSONArray;

/**
 * Created by yuanwei2 on 2019/5/30.
 */

public class ExperienceBusiness extends BaseHttpBusiness {
    public ExperienceBusiness(Context context) {
        super(context);
    }

    public void getExpLiveStatus(String url, int expLiveId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("expLiveId", "" + expLiveId);
        sendPost(url, params, callBack);
    }

    public void expUserSign(String signInUrl, int expLiveId, String orderId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("expLiveId", "" + expLiveId);
        params.addBodyParam("orderId", "" + orderId);
        sendPost(signInUrl, params, callBack);
    }

    public void visitTimeHeart(String heartUrl, String liveId, String termId, HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", "" + liveId);
        params.addBodyParam("termId", "" + termId);
        sendPost(heartUrl, params, callBack);
    }

    public void getExperienceResult(String planId, String orderId,String userId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("planId", planId);
        params.addBodyParam("orderId", orderId);
        params.addBodyParam("userId", userId);
        sendPost(LiveVideoConfig.URL_AUTO_LIVE_FEAD_BACK, params, requestCallBack);
    }

    //发送体验课学习反馈
    public void sendExperienceFeedback(String plan_id, String subject_id, String grade_id, String
            order_id, String suggest, JSONArray jsonOption, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();

//        params.addBodyParam("user_id", user_id);
        params.addBodyParam("plan_id", plan_id);
        params.addBodyParam("subject_id", subject_id);
        params.addBodyParam("grade_id", grade_id);
        params.addBodyParam("order_id", order_id);
        params.addBodyParam("suggest", suggest);
        params.addBodyParam("option", jsonOption.toString());
//        sendPost("https://www.easy-mock.com/mock/5b57f6919ddd1140ec2eb47b/xueersi.wx.android
// .app/livevideo/feedback",params,requestCallBack);
        sendPost(LiveVideoConfig.URL_AUTO_LIVE_LEARN_FEED_BACK, params, requestCallBack);
    }

}
