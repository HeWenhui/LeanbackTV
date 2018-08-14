package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import com.xueersi.common.http.HttpCallBack;

/**
 * Created by Zhang Yuansun on 2018/8/3.
 */

public interface SpeechBulletScreenHttp {
    public void sendDanmakuMessage(String msg);
    public void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack);
    public String getHeadImgUrl();
}
