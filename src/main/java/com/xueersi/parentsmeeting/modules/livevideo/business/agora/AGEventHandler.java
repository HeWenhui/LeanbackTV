package com.xueersi.parentsmeeting.modules.livevideo.business.agora;

public interface AGEventHandler {
    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserJoined(int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onError(int err);

    void onVolume(int volume);

    /**
     * https://docs.agora.io/cn/Voice/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html#aaa721f00a7409aa091c9763c3385332e
     *
     * @param uid
     * @param state
     */
    void onRemoteVideoStateChanged(int uid, int state);
}
