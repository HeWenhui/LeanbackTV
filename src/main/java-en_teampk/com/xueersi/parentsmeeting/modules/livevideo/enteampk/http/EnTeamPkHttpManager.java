package com.xueersi.parentsmeeting.modules.livevideo.enteampk.http;

import com.google.gson.JsonObject;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoHttpEnConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
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

public class EnTeamPkHttpManager {
    String TAG = "EnTeamPkHttpManager";
    Logger logger = LiveLoggerFactory.getLogger(TAG);
    LiveHttpManager liveHttpManager;
    EnTeamPkResponseParser enTeamPkResponseParser;

    public EnTeamPkHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        enTeamPkResponseParser = new EnTeamPkResponseParser();
    }

    /**
     * 学生上报个人信息
     *
     * @param is_interactive
     * @param requestCallBack
     */
    public void reportInteractiveInfo(String stu_id, String unique_id, boolean is_interactive, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("stu_id", "" + stu_id);
        params.addBodyParam("unique_id", unique_id);
        params.addBodyParam("is_interactive", "" + is_interactive);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(LiveVideoHttpEnConfig.URL_LIVE_REPORT_InteractiveInfo + "?unique_id=" + unique_id, params, requestCallBack);
    }

    public InteractiveTeam parseInteractiveTeam(String userId, JSONObject jsonObject) {
        InteractiveTeam interactiveTeam = enTeamPkResponseParser.parseInteractiveTeam(userId, jsonObject);
        return interactiveTeam;
    }

    public void getStuActiveTeam(String unique_id, final String stu_id, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("unique_id", unique_id);
        httpRequestParams.addBodyParam("stu_id", stu_id);
        liveHttpManager.sendPost(EnTeamPkHttpConfig.GET_STU_ACTIVE_TEAM + "?unique_id=" + unique_id, httpRequestParams, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Object object = responseEntity.getJsonObject();
                if (object instanceof JSONObject) {
                    InteractiveTeam interactiveTeam = parseInteractiveTeam(stu_id, (JSONObject) responseEntity.getJsonObject());
                    callBack.onDataSucess(interactiveTeam, responseEntity.getJsonObject());
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "");
//                    InteractiveTeam interactiveTeam = new InteractiveTeam();
//                    ArrayList<TeamMemberEntity> entities = parseGetStuActiveTeam(responseEntity);
//                    interactiveTeam.setEntities(entities);
//                    callBack.onDataSucess(interactiveTeam, responseEntity.getJsonObject());
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

}
