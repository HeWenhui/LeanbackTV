package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;

/**
 * Created by lyqai on 2018/7/17.
 */

public interface RegMediaPlayerControl {
    void addMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl);

    void removeMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl);
}
