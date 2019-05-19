package com.xueersi.parentsmeeting.modules.livevideo.teampk.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class TeamPkHttp {
    String TAG = "TeamPkHttp";
    Logger logger = LiveLoggerFactory.getLogger(TAG);
    LiveHttpManager liveHttpManager;
    PrimaryClassResponseParser primaryClassResponseParser;

    public TeamPkHttp(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        primaryClassResponseParser = new PrimaryClassResponseParser();
    }

    public void getMyTeamInfo(String classId, String stuId, String psuser, HttpCallBack requestCallBack) {
        final HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", "" + classId);
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("psuser", "" + psuser);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(PrimaryClassConfig.URL_LIVE_GET_MY_TEAM_INFO, params, requestCallBack);
    }
}
