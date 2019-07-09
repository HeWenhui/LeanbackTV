package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

public interface IntelligentConstants {
    int PERFECT = 1;
    //
    int GOOD = 2;
    //情况1,50%及以上的单词分数低于60分
    int REPEAT_SENTENCE = 3;
    //情况2，50%及以下的单词分数低于60分
    int REPEAT_WORD = 4;
}
