package com.xueersi.parentsmeeting.modules.livevideo.http;

import android.content.Context;
import android.util.Log;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import org.xutils.xutils.common.util.MD5;

import java.util.HashMap;

/**
 * NB实验 Http 请求
 * @author chekun
 * created  at 2019/4/3 14:56
 */
public class NBHttpManager extends BaseHttpBusiness {
    HashMap<String, String> defaultKey = new HashMap<>();

    public NBHttpManager(Context context) {
        super(context);
    }

    public void addBodyParam(String key, String value) {
        defaultKey.put(key, value);
    }

    void setDefaultParameter(HttpRequestParams httpRequestParams) {
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            httpRequestParams.addBodyParam(key, value);
        }
    }

    /**
     *  登录 Nb 获取token信息
     * @param stuId       学生id
     * @param nickName    昵称
     * @param userType    用户类型
     * @param requestCallBack
     */
    public void nbLogin(String liveId,String stuId,String nickName,String userType,HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("nickName", nickName);
        params.addBodyParam("type", userType);
        sendPost(LiveVideoConfig.URL_NB_LOGIN, params, requestCallBack);
    }


    /**
     * 上传Nb 答题结果到本方服务器
     * @param liveId
     * @param stuId
     * @param userResult
     * @param isForce
     * @param isPlayBack
     * @param requestCallBack
     */
    public void upLoadNbReuslt(String liveId,String stuId,String stuCouId,
                               String userResult,String isForce,String isPlayBack ,HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("stuCouId", stuCouId);
        params.addBodyParam("userResult", userResult);
        params.addBodyParam("isForce", isForce);
        params.addBodyParam("isPlayBack", isPlayBack);
        sendPost(LiveVideoConfig.URL_NB_RESULT_UPLOAD, params, requestCallBack);
    }


    /**
     * 获取 Nb试题信息
     * @param liveId   直播id
     * @param stuId    学生id
     * @param experimentId  实验id
     * @param token     乐步方登录 token
     * @param requestCallBack  结果回调
     */
    public void getNbTestInfo(String liveId,String stuId,String experimentId,String token,HttpCallBack requestCallBack){
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("experimentId", experimentId);
        params.addBodyParam("token", token);
        sendPost(LiveVideoConfig.URL_NB_COURSE_INFO, params, requestCallBack);
    }

}
