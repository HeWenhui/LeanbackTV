package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

/**
 * @Date on 2019/10/15 19:44
 * @Author zhangyuansun
 * @Description
 */
public interface RTCVideoAction {
    void initTeamInfo(String token,String userId, String userName, String userLogo,String teamId, String teamName, String teamLogo, boolean
            haveTeamUser);

    void show(boolean falseVideo);

    void close();

    void updateSpread(boolean isSpread);

    void updateGold(int gold, float x, float y, int type);

    /** 更新机器人队友金币 **/
    void updateRobotGold(int gold);

    void startTeamGoldAnimition(int gold, float getX, float getY);

    void onMediaControllerShow();

    void onMediaControllerHide();

    void setReceiveVoice(boolean receiveVoice);

    /** 开启和关闭测评模式 **/
    void updateAssessmentMode(boolean isOpen);

    /** 收音条音量变化回调 **/
    void onVolumeUpdate(float volume);

    int GOLD_TYPE_QUESTION = 1;
    int GOLD_TYPE_REDPACKAGE = 2;
}
