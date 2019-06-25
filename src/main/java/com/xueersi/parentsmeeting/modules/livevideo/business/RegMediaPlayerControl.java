package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;

/**
 * Created by linyuqiang on 2018/7/17.
 */

public interface RegMediaPlayerControl extends LiveProvide{
    void addMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl);

    void removeMediaPlayerControl(LiveMediaController.MediaPlayerControl mediaPlayerControl);
}
