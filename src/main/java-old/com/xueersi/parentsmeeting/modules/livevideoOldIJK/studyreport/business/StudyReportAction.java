package com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business;

import android.view.View;

/**
 * linyuqiang
 */
public interface StudyReportAction {
    /**
     * 接麦
     *
     * @param uid
     */
    void onFirstRemoteVideoDecoded(int uid);

    /**
     * 接麦用户进入
     *
     * @param uid
     * @param elapsed
     */
    void onUserJoined(int uid, int elapsed);

    /**
     * 接麦用户退出
     *
     * @param uid
     * @param reason
     */
    void onUserOffline(int uid, int reason);

    /**
     * 截图
     *
     * @param type
     * @param view
     * @param cut
     * @param predraw
     */
    void cutImage(int type, View view, boolean cut, boolean predraw);

    /**
     * 截图带视频
     *
     * @param type
     * @param view
     * @param cut
     * @param predraw
     */
    void cutImageAndVideo(int type, View view, boolean cut, boolean predraw);
}
