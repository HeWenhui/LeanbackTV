package com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.Contract;

import android.view.View;

/**
 * @Date on 2019/5/22 16:52
 * @Author zhangyuansun
 * @Description 回放弹幕view层接口
 */
public interface SpeechbulletPlayBackView {
    void addDanmaku(String name, String msg, String headImgUrl, boolean isGuest);

    void pauseDanmaku();

    void resumeDanmaku();

    void setDanmakuSpeed(float speed);

    View getPager();
}
