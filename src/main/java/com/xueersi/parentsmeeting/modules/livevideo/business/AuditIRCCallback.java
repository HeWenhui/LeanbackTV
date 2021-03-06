package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by linyuqiang on 2017/7/20.
 */

public interface AuditIRCCallback extends IRCCallback {
    void onStudentLeave(boolean leave, String stuPushStatus);

    void onStudentError(String status, String msg);

    void onStudentPrivateMessage(String sender, String login, String hostname, String target, String message);
}
