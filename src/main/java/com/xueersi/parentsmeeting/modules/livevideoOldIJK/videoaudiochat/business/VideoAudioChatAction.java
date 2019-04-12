package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videoaudiochat.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/8.
 */

public interface VideoAudioChatAction {

    /** 举手 @param status 开关状态 */
    void raisehand(String status, String room, String from, String nonce, int micType, String linkmicid, int msgFrom);

    /**
     * @param openNewMic        //老师是否着正在连麦
     * @param room              //房间号
     * @param classmateChange   //
     * @param classmateEntities //
     * @param from              //主讲辅导
     * @param type              //类型。0语音，1视频
     * @param linkmicid
     */
    void onJoin(String openNewMic, String room, boolean classmateChange, ArrayList<ClassmateEntity>
            classmateEntities, String from, int type, String linkmicid);

    void quit(String status, String room, String from, int msgFrom);

    void raiseHandCount(int num);

    void onStuMic(String status, String room, ArrayList<ClassmateEntity>
            onmicClassmateEntities, ArrayList<ClassmateEntity> offmicClassmateEntities, String from, int msgFrom, String nonce);

    void onConnect();

    void onDisconnect();
}
