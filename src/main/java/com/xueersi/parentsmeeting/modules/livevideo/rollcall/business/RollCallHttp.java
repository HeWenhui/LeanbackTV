package com.xueersi.parentsmeeting.modules.livevideo.rollcall.business;

import com.xueersi.common.http.HttpCallBack;

import org.json.JSONObject;

/**
 * Created by lyqai on 2018/7/10.
 */

public interface RollCallHttp {
    public void userSign(String enstuId, String liveId, String classId, String teacherId, HttpCallBack
            requestCallBack);

    void sendRollCallNotice(JSONObject jsonObject, String o);
}
