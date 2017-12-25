package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/8.
 */

public interface VideoChatAction {

    /** 举手 @param status 开关状态 */
    void raisehand(String status, String from);

    void raiseHandStatus(String status, int num, String from);

    void onJoin(String onmic, String openhands, String room, boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities, String from);

    void requestAccept(String from);

    void startMicro(String status, String nonce, boolean contain, String room, String from);

    void quit(String status, String room, String from);

    void raiseHandCount(int num);
}