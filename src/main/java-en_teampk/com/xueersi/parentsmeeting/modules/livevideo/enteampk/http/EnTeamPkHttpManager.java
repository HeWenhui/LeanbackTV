package com.xueersi.parentsmeeting.modules.livevideo.enteampk.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnTeamPkHttpManager {
    Logger logger = LiveLoggerFactory.getLogger("EnTeamPkHttpManager");
    LiveHttpManager liveHttpManager;
    EnTeamPkResponseParser enTeamPkResponseParser;

    public EnTeamPkHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        enTeamPkResponseParser = new EnTeamPkResponseParser();
    }

    public void dispatch(String userId, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stu_id", userId);
//        liveHttpManager.sendGet(EnTeamPkHttpConfig.dispatch, httpRequestParams, new HttpCallBack() {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                ArrayList<InetSocketAddress> addresses = enTeamPkResponseParser.parseTcpDispatch(responseEntity);
//                callBack.onDataSucess(addresses);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                super.onPmError(responseEntity);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                super.onPmFailure(error, msg);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
        liveHttpManager.sendGetNoBusiness(EnTeamPkHttpConfig.dispatch, httpRequestParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setJsonObject(new JSONObject(response.body().string()));
                    ArrayList<InetSocketAddress> addresses = enTeamPkResponseParser.parseTcpDispatch(responseEntity);
                    callBack.onDataSucess(addresses);
                } catch (Exception e) {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, e.getMessage());
                }
            }
        });
    }
}
