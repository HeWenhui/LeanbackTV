package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/8.
 */

public interface VideoChatAction {

    /** 举手 @param status 开关状态 */
    void raisehand(String status, String from, String nonce);

    void raiseHandStatus(String status, int num, String from);

    /**
     * @param onmic             //老师是否着正在连麦
     * @param openhands         //老师是否开启举手
     * @param room              //房间号
     * @param classmateChange   //
     * @param classmateEntities //
     * @param from              //主讲辅导
     */
    void onJoin(String onmic, String openhands, String room, boolean classmateChange, ArrayList<ClassmateEntity>
            classmateEntities, String from);

    void requestAccept(String from, String nonce);

    void startMicro(String status, String nonce, boolean contain, String room, String from);

    void quit(String status, String room, String from);

    void raiseHandCount(int num);
}
