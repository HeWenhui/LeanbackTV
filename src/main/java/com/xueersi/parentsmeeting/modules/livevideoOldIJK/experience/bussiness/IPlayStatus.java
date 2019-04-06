package com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.bussiness;

import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;

public interface IPlayStatus {

    void onPlayOpenStart();

    void onPlaySuccess(PlayerService vPlayer);

    void onPlayingPosition(long currentPosition, long duration);

    void onPlayComplete();
}
