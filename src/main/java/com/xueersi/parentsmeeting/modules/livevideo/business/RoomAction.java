package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.MessageShowEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;

/**
 * 聊天房间事件
 * Created by linyuqiang on 2016/8/18.
 */
public interface RoomAction {

    /** 开始连接 */
    void onStartConnect();

    /** 在连接 */
    void onConnect();

    /** 在注册 */
    void onRegister();

    /** 断开连接 */
    void onDisconnect();

    /** 连接后，用户列表 */
    void onUserList(String channel, User[] users);

    /** 正常消息 */
//    void onMessage(String target, String sender, String login, String hostname, String text, String headurl);

    void onMessage(MessageShowEntity messageShowEntity);

    /** 私聊消息 */
//    void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String
//            message);

    void onPrivateMessage(MessageShowEntity messageShowEntity);

    /** 用户加入 */
    void onJoin(String target, String sender, String login, String hostname);

    /** 用户退出 */
    void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason);

    /** 被踢，目前没用到 */
    void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick,
                String reason);

    /**
     * 被禁言
     *
     * @param disable    是不是被禁言
     * @param fromNotice 从irc的onNotice来的命令，不是禁言状态提示，onTopic的不提示
     */
    void onDisable(boolean disable, boolean fromNotice);

    /**
     * 其他学生被禁言
     *
     * @param id
     * @param name
     * @param disable
     */
    void onOtherDisable(String id, String name, boolean disable);

    /**
     * 关闭开启聊天
     *
     * @param openchat 是否开启聊天
     * @param mode     直播或辅导模式
     */
    void onopenchat(boolean openchat, String mode, boolean fromNotice);

    /** 关闭开启弹幕 */
    void onOpenbarrage(final boolean openbarrage, boolean fromNotice);

    /** 关闭开启语音弹幕 */
    void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice);

    /**
     * 理科辅导老师开启关闭鲜花
     *
     * @param open
     * @param b
     */
    void onFDOpenbarrage(boolean open, boolean b);


    /** 语音聊天状态，弹幕分离，就不需要了*/
//    void videoStatus(String status);


    /**
     * 理科，主讲和辅导切换的时候，给出提示（切流）
     *
     * @param oldMode
     * @param mode
     * @param isShowNoticeTips  为false的时候，默认显示"已切换到 主讲/辅导模式"
     * @param iszjlkOpenbarrage
     * @param isFDLKOpenbarrage
     */
    void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage);

    /**
     * 教师端发起语音相关notic时，给出提示 关闭当前语音聊天
     *
     * @param openVoice
     * @param type
     */
    void onOpenVoiceNotic(final boolean openVoice, String type);
}
