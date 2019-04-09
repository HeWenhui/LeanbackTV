package com.xueersi.parentsmeeting.modules.livevideo.groupgame.action;

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
    void onDestory();
    void saveUserAnser();
}
