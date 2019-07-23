package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;

/**
 * 直播网络访问类，排行榜
 */
public class RankHttp {
    LiveHttpAction liveHttpAction;

    public RankHttp(LiveHttpAction liveHttpAction) {
        this.liveHttpAction = liveHttpAction;
    }

    /**
     * 中学激励系统学生点赞
     *
     * @param url
     * @param classId   班级id
     * @param planId    场次id
     * @param teamId    小组ID
     * @param listFlag  榜单标识（1：排行榜 2：连对榜）
     * @param bePraised 被点赞的ID
     */
    public void sendEvenDriveLike(String url, String classId, String planId, String teamId, String listFlag, String bePraised, HttpCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("classId", classId);
        httpRequestParams.addBodyParam("planId", planId);
        httpRequestParams.addBodyParam("teamId", teamId);
        httpRequestParams.addBodyParam("listFlag", listFlag);
        httpRequestParams.addBodyParam("bePraised", bePraised);
        liveHttpAction.sendPostDefault(url, httpRequestParams, callBack);
    }

    /**
     * 文科获取 排行信息
     *
     * @param requestCallBack
     */
    public void getNewArtsAllRank(String liveId, String stuCouId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpAction.sendPostDefault(liveHttpAction.getLiveVideoSAConfigInner().URL_ARTS_TEAM_CLASS_RANK, params, requestCallBack);
    }

    public void getAllRanking(String liveId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        liveHttpAction.sendPostDefault(liveHttpAction.getLiveVideoSAConfigInner().URL_LIVE_GET_TEAM_RANK, params, requestCallBack);
    }
}
