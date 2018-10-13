package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

public interface StudyReportAction {
    /**
     * 接麦
     *
     * @param uid
     */
    void onFirstRemoteVideoDecoded(int uid);

    void onUserJoined(int uid, int elapsed);

    void onUserOffline(int uid, int reason);
}
