package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.agora;

public interface AGEventHandler {
    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserJoined(int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onError(int err);

    void onVolume(int volume);
}
