package com.xueersi.parentsmeeting.modules.livevideoOldIJK.groupgame.action;

import com.tal.speech.speechrecognizer.ResultEntity;

/**
 * @Date on 2019/4/5 16:25
 * @Author zhangyuansun
 * @Description
 */
public interface SingleModeAction {
    void startTimer();
    void onLoadComplete();
    void onHitSentence(ResultEntity resultEntity);
    void onDestroy();
    void saveUserAnser();
}
