package com.xueersi.parentsmeeting.modules.livevideo.business;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2016/9/23.
 * 表扬或批评事件
 */
@Deprecated
public interface PraiseOrEncourageAction {

    /**
     * 表扬或批评
     *
     * @param jsonObject
     */
    void onPraiseOrEncourage(JSONObject jsonObject);
}
