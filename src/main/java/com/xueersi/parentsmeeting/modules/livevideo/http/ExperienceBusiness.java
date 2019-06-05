package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;

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

    public void expUserSign(String signInUrl, int expLiveId, String orderId,HttpCallBack callBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("expLiveId", "" + expLiveId);
        params.addBodyParam("orderId", "" + orderId);
        sendPost(signInUrl, params, callBack);
    }
}
