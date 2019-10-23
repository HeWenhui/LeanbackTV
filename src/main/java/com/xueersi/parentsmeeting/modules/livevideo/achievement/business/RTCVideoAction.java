package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

/**
 * @Date on 2019/10/15 19:44
 * @Author zhangyuansun
 * @Description
 */
public interface RTCVideoAction {
    void initTeamInfo(String token, String userName, String userLogo, String teamName, String teamLogo,boolean haveTeamUser);

    void show();

    void updateSpread(boolean isSpread);

    void updateGold(int gold, float x,float y);
}
