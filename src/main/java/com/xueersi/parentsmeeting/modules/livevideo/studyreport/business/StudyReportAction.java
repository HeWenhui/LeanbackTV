package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.view.View;

public interface StudyReportAction {
    /**
     * 接麦
     *
     * @param uid
     */
    void onFirstRemoteVideoDecoded(int uid);

    void onUserJoined(int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void cutImage(int type, View view, boolean cut);
}
