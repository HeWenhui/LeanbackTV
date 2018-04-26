package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LectureLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.utils.log.Loger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/4/24.
 *  帧数统计
 */
public class TotalFrameStat extends PlayerService.SimpleVPlayerListener {
    LiveBll liveBll;
    PlayerService vPlayer;
    ArrayList<String> frames = new ArrayList<>();
    Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    private String TAG = "TotalFrameStat";

    public TotalFrameStat(Activity activity) {
        this.activity = activity;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public void setLastPlayserverEntity(PlayServerEntity.PlayserverEntity lastPlayserverEntity) {
        this.lastPlayserverEntity = lastPlayserverEntity;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (vPlayer.isInitialized() && lastPlayserverEntity != null) {
                if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                    IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                    float fps = ijkMediaPlayer.getVideoDecodeFramesPerSecond();
                    frames.add("" + ((int) (fps * 5)));
                    if (frames.size() == 12) {
                        send();
                    }
                }
            }
            handler.sendEmptyMessageDelayed(1, 5000);
        }
    };

    private void send() {
        Loger.d(TAG, "send:frames=" + frames.size());
        if (frames.isEmpty()) {
            return;
        }
        String vdownload = "";
        for (int i = 0; i < frames.size(); i++) {
            vdownload = frames.get(i) + ",";
        }
        frames.clear();
        Map<String, String> mData = new HashMap<>();
        mData.put("message", "server: " + lastPlayserverEntity.getAddress() + "vdownload:" + vdownload);
        Loger.e(activity, LiveVideoConfig.LIVE_GSLB, mData, true);
    }

    public void onReplay() {
        handler.removeMessages(1);
        send();
    }

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
        send();
    }

    @Override
    public void onPlaybackComplete() {
        super.onPlaybackComplete();
        handler.removeMessages(1);
        send();
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        handler.removeMessages(1);
        send();
    }

    public void destory() {
        handler.removeMessages(1);
        send();
    }
}
