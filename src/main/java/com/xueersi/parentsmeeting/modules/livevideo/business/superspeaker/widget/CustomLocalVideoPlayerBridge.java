package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.content.Context;
import android.view.SurfaceView;

import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;

import java.io.IOException;

public class CustomLocalVideoPlayerBridge implements ILocalVideoPlayer {

    private Context mContext;
    private PlayerService playerService;

    public CustomLocalVideoPlayerBridge(Context mContext) {
        this.mContext = mContext;
        playerService = new PlayerService(mContext);
    }

    @Override
    public void setVideoView(SurfaceView surfaceView) {
//        if (playerService == null) {
//            playerService = new PlayerService(mContext);
//        }
        playerService.setDisplay(surfaceView.getHolder());
    }

    @Override
    public void startPlayVideo(String path, int time) {
        try {
            playerService.playFile(path, time);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        playerService.pause();
    }

    @Override
    public void start() {
        playerService.start();
    }

    @Override
    public void stop() {
        playerService.stop();
    }

    @Override
    public void release() {
        playerService.release();
    }

    @Override
    public void seekTo(long pos) {
        playerService.seekTo(pos);
    }

    @Override
    public void setListener(VPlayerCallBack.VPlayerListener listener) {
        playerService.psInit(MediaPlayer.VIDEO_PLAYER_NAME, 0, listener, false);
        playerService.setVPlayerListener(listener);
    }
}
