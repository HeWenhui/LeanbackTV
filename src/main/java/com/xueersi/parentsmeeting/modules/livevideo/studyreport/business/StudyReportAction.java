package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;

/**
 * linyuqiang
 */
public interface StudyReportAction extends LiveProvide {
    /**
     * 接麦
     *
     * @param uid
     */
    void onFirstRemoteVideoDecoded(long uid);

    /**
     * 接麦用户进入
     *
     * @param uid
     * @param elapsed
     */
    void onUserJoined(long uid, int elapsed);

    /**
     * 接麦用户退出
     *
     * @param uid
     * @param reason
     */
    void onUserOffline(long uid, int reason);

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
