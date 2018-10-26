package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/8.
 */

public interface VideoAudioChatAction {

    /** 举手 @param status 开关状态 */
    void raisehand(String status, String room, String from, String nonce, int msgFrom);

    /**
     * @param onmic             //老师是否着正在连麦
     * @param room              //房间号
     * @param classmateChange   //
     * @param classmateEntities //
     * @param from              //主讲辅导
     */
    void onJoin(String onmic, String room, boolean classmateChange, ArrayList<ClassmateEntity>
            classmateEntities, String from);

    void quit(String status, String room, String from, int msgFrom);

    void raiseHandCount(int num);

    void onStuMic(String status, String room, ArrayList<ClassmateEntity>
            classmateEntities, String from, int msgFrom);
}
