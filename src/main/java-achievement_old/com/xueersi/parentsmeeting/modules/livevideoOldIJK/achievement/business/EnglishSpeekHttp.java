package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

/**
 * Created by lyqai on 2018/7/5.
 */

public interface EnglishSpeekHttp {
    void setTotalOpeningLength(long reTryTime, String duration, String speakingNum, final
    String speakingLen, float x, float y);

    void sendDBStudent(int dbDuration);

    void setNotOpeningNum();
}
