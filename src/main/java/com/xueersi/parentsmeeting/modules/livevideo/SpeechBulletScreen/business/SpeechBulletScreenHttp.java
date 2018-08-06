package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

/**
 * Created by Zhang Yuansun on 2018/8/3.
 */

public interface SpeechBulletScreenHttp {
    boolean sendMessage(String msg, String name);

    void sendDanmakuMessage(int ftype);
}
