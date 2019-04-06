package com.xueersi.parentsmeeting.modules.livevideoOldIJK.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;

/**
 * 理科接口请求
 *
 * @author linyuqiang
 * @date 2018/5/16
 */
public class LiveScienceHttpManager {
    LiveHttpManager liveHttpManager;

    public LiveScienceHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
    }

    /**
     * 理科接麦举手接口
     *
     * @param requestCallBack
     */
    public void chatHandAdd(HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(liveHttpManager.liveVideoSAConfigInner.URL_LIVE_HANDADD, params, requestCallBack);
    }
}
