package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class PrimaryClassHttp {
    String TAG = "PrimaryClassHttp";
    Logger logger = LiveLoggerFactory.getLogger(TAG);
    LiveHttpManager liveHttpManager;
    PrimaryClassResponseParser primaryClassResponseParser;
    LiveHttpResponseParser liveHttpResponseParser;

    public PrimaryClassHttp(Context context, LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        primaryClassResponseParser = new PrimaryClassResponseParser();
        liveHttpResponseParser = new LiveHttpResponseParser(context);
    }

    public void setLiveHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
    }

    public void reportUserAppStatus(String classId, String stuId, String status) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", "" + classId);
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("status", "" + status);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(PrimaryClassConfig.URL_LIVE_REPORT_USER_APP_STATUS, params, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("reportUserAppStatus:onPmSuccess:json=" + responseEntity.getJsonObject());

            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("reportUserAppStatus:onPmError:msg=" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.d("reportUserAppStatus:onPmFailure:msg=" + msg);
            }
        });
    }

    public void getMyTeamInfo(String classId, String stuId, String psuser, final AbstractBusinessDataCallBack callBack) {
        final HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", "" + classId);
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("psuser", "" + psuser);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(PrimaryClassConfig.URL_LIVE_GET_MY_TEAM_INFO, params, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getMyTeamInfo:onPmSuccess:json=" + responseEntity.getJsonObject());
                TeamPkTeamInfoEntity teamInfoEntity = liveHttpResponseParser.parseTeamInfoPrimary(responseEntity);
                if (teamInfoEntity != null) {
                    callBack.onDataSucess(teamInfoEntity);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getMyTeamInfo:onPmError:msg=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
                logger.d("getMyTeamInfo:onPmFailure:msg=" + msg);
            }
        });
    }

    public void reportNaughtyBoy(String classId, String reporterId, String reporterName, String naughtyBoyId, String naughtyBoyName, String content, String roomId, String teamName, String teamId, final AbstractBusinessDataCallBack callBack) {
        final HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", "" + classId);
        params.addBodyParam("psuser", "" + reporterId);
        params.addBodyParam("reporterName", "" + reporterName);
        params.addBodyParam("naughtyBoyId", "" + naughtyBoyId);
        params.addBodyParam("naughtyBoyName", "" + naughtyBoyName);
        params.addBodyParam("content", "" + content);
        params.addBodyParam("roomId", "" + roomId);
        params.addBodyParam("teamName", "" + teamName);
        params.addBodyParam("teamId", "" + teamId);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(PrimaryClassConfig.URL_LIVE_REPORT_NAUGHTY_BOY, params, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("reportNaughtyBoy:onPmSuccess:json=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("reportNaughtyBoy:onPmError:msg=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
                logger.d("reportNaughtyBoy:onPmFailure:msg=" + msg);
            }
        });
    }
}
