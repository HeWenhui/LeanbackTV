package com.xueersi.parentsmeeting.modules.livevideo.english.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.http.EnTeamPkResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.lib.GroupGameTcp;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnglishHttpManager {
    private String TAG = "EnglishHttpManager";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private LiveHttpManager liveHttpManager;
    private EnTeamPkResponseParser enTeamPkResponseParser;

    public EnglishHttpManager(LiveHttpManager liveHttpManager) {
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
                    String res = response.body().string();
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setJsonObject(new JSONObject(res));
                    ArrayList<InetSocketAddress> addresses = enTeamPkResponseParser.parseTcpDispatch(responseEntity);
                    callBack.onDataSucess(addresses);
                } catch (Exception e) {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, e.getMessage());
                }
            }
        });
    }


    /**
     * 小组互动 - 上报小组互动互动信息
     */
    public void reportOperateGroupGame(final short type, final int operation, final JSONObject bodyJson, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(httpRequestParams);
        JSONObject httpjson = new JSONObject();
        try {
            httpjson.put("ver", TcpConstants.ver);
            httpjson.put("type", type);
            httpjson.put("op", operation);
            httpjson.put("seq", GroupGameTcp.seq++);
            httpjson.put("timestamp", System.currentTimeMillis());
            httpjson.put("body", bodyJson);
        } catch (Exception e) {

        }
        httpRequestParams.setJson(httpjson.toString());
        logger.d("reportOperateGroupGame:" + httpjson.toString());
        String url = "";
        if (AppConfig.DEBUG) {
            url = LiveVideoConfig.APP_GROUP_GAME_TCP_HOST_DEBUG + LiveQueHttpConfig.LIVE_GROUPGAME_REPORT + "?hkey=" + bodyJson.opt("live_id") + "-" + bodyJson.opt("class_id") + "-" + bodyJson.opt("pk_team_id");
        } else {
            url = LiveVideoConfig.APP_GROUP_GAME_TCP_HOST + LiveQueHttpConfig.LIVE_GROUPGAME_REPORT + "?hkey=" + bodyJson.opt("live_id") + "-" + bodyJson.opt("class_id") + "-" + bodyJson.opt("pk_team_id");
        }
        liveHttpManager.baseSendPostNoBusinessJson(url, httpRequestParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                logger.d("reportOperateGroupGame=fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.d("reportOperateGroupGame=success");
            }
        });
    }
}
