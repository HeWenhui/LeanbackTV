package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

/**
 * Created by linyuqiang on 2018/7/5.
 */

public interface EnglishSpeekHttp {
    void setTotalOpeningLength(long reTryTime, String duration, String speakingNum, final
    String speakingLen, float x, float y);

    void sendDBStudent(int dbDuration);

    void setNotOpeningNum();
}
