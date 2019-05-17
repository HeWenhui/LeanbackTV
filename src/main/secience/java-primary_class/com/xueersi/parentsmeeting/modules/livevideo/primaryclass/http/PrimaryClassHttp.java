package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.PrimaryClassEntity;

public class PrimaryClassHttp {
    String TAG = "PrimaryClassHttp";
    Logger logger = LoggerFactory.getLogger(TAG);
    LiveHttpManager liveHttpManager;
    PrimaryClassResponseParser primaryClassResponseParser;

    public PrimaryClassHttp(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        primaryClassResponseParser = new PrimaryClassResponseParser();
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
                PrimaryClassEntity primaryClassEntity = primaryClassResponseParser.parsePrimaryClassEntity(responseEntity);
                if (primaryClassEntity != null) {
                    callBack.onDataSucess(primaryClassEntity);
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
}
