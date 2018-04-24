package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/4/24.
 */
public class TotalFrameStat extends PlayerService.SimpleVPlayerListener {
    LiveBll liveBll;
    PlayerService vPlayer;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
            ijkMediaPlayer.getVideoDecodeFramesPerSecond();
        }
    };

    @Override
    public void onOpenStart() {
        super.onOpenStart();
    }

    @Override
    public void onOpenSuccess() {
        super.onOpenSuccess();
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onOpenFailed(int arg1, int arg2) {
        super.onOpenFailed(arg1, arg2);
        handler.removeMessages(1);
    }

    @Override
    public void onPlaybackComplete() {
        super.onPlaybackComplete();
        handler.removeMessages(1);
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        handler.removeMessages(1);
    }
}
