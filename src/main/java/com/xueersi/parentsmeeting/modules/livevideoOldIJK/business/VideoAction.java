package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.video.PlayErrorCode;

/**
 * 视频事件
 * Created by linyuqiang on 2016/8/18.
 */
public interface VideoAction {
    /**
     * 老师不在场
     */
    void onTeacherNotPresent(boolean isBefore);

    /**
     * 老师断线
     */
    void onTeacherQuit(boolean isQuit);

    /**
     * 直播初始化完成
     *
     * @param getInfo
     */
    void onLiveInit(LiveGetInfo getInfo);

    /**
     * 直播开始
     */
    void onLiveStart(PlayServerEntity server, LiveTopic cacheData, boolean modechange);

    /**
     * 直播调度30分钟超时
     */
    void onLiveTimeOut();

    /**
     * 课程规定结束时间半小时之后
     */
    // TODO
    void onClassTimoOut();

    /**
     * 直播模式变化
     *
     * @param mode      模式
     * @param isPresent 老师在不在直播间
     */
    void onModeChange(String mode, boolean isPresent);

    /**
     * 直播请求业务错误
     *
     * @param responseEntity
     */
    void onLiveError(ResponseEntity responseEntity);

    /**
     * 直播公开课不符合
     *
     * @param msg
     */
    void onLiveDontAllow(String msg);

    void onPlayError(int errorCode, PlayErrorCode playErrorCode);
}
