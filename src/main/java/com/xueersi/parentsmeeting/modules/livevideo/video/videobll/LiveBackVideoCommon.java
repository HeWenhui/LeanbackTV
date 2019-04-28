package com.xueersi.parentsmeeting.modules.livevideo.video.videobll;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BasePlayerFragment;

public class LiveBackVideoCommon implements IPlayStrategy {

    private BasePlayerFragment basePlayerFragment;

    public LiveBackVideoCommon(BasePlayerFragment basePlayerFragment) {
        this.basePlayerFragment = basePlayerFragment;
    }


    @Override
    public void playNewVideo(String url, int procotol) {
//        basePlayerFragment.playPSVideo();

        String videoPath;
//        String url = mVideoEntity.getVideoPath();
        if (url.contains("http") || url.contains("https")) {
            videoPath = DoPSVideoHandle.getPSVideoPath(url);
        } else {
            videoPath = url;
        }
        basePlayerFragment.playPSVideo(videoPath, MediaPlayer.VIDEO_PROTOCOL_MP4);
    }
}
