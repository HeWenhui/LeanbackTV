package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.utils.log.Loger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/4/24.
 * 帧数统计
 */
public class TotalFrameStat extends PlayerService.SimpleVPlayerListener {
    private String TAG = "TotalFrameStat";
    LiveBll liveBll;
    PlayerService vPlayer;
    ArrayList<String> frames = new ArrayList<>();
    long frameStart;
    Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    /** 是不是开始统计 */
    private boolean isStat = true;

    public TotalFrameStat(Activity activity) {
        this.activity = activity;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    /**
     * 设置播放地址
     *
     * @param lastPlayserverEntity
     */
    public void setLastPlayserverEntity(PlayServerEntity.PlayserverEntity lastPlayserverEntity) {
        this.lastPlayserverEntity = lastPlayserverEntity;
    }

    public boolean isStat() {
        return isStat;
    }

    public void setStat(boolean stat) {
        isStat = stat;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        float lastFps = 0;

        @Override
        public void handleMessage(Message msg) {
//            if (!isStat) {
//                return;
//            }
            try {
                if (vPlayer.isInitialized() && lastPlayserverEntity != null) {
                    if (vPlayer.getPlayer() instanceof IjkMediaPlayer) {
                        IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) vPlayer.getPlayer();
                        float fps = ijkMediaPlayer.getVideoDecodeFramesPerSecond();
                        if (frames.isEmpty()) {
                            frameStart = System.currentTimeMillis();
                        }
                        if (lastFps != 0) {
                            frames.add("" + ((int) ((lastFps + fps) * 5 / 2)));
                        } else {
                            frames.add("" + ((int) (fps * 5)));
                        }
                        lastFps = fps;
                        if (frames.size() == 12) {
                            send("frames12");
                        }
                    }
                }
            } catch (Exception e) {
                Loger.e(BaseApplication.getContext(), TAG, "handleMessage", e, true);
            }
            handler.sendEmptyMessageDelayed(1, 5000);
        }
    };

    private void send(String method) {
        Loger.d(TAG, "send:method=" + method + ",frames=" + frames.size());
        if (frames.isEmpty()) {
            return;
        }
        StringBuilder vdownload = new StringBuilder();
        for (int i = 0; i < frames.size(); i++) {
            vdownload.append(frames.get(i));
            if (i != frames.size() - 1) {
                vdownload.append(",");
            }
        }
        frames.clear();
        long time = System.currentTimeMillis() - frameStart;
        StableLogHashMap stableLogHashMap = new StableLogHashMap("glsbSpeed");
        stableLogHashMap.put("activity", activity.getClass().getSimpleName());
        stableLogHashMap.put("method", method);
        stableLogHashMap.put("time", "" + time);
        stableLogHashMap.put("message", "server: " + lastPlayserverEntity.getAddress() + " vdownload:" + vdownload);
        Loger.e(activity, LiveVideoConfig.LIVE_GSLB, stableLogHashMap.getData(), true);
    }

    public void onPause() {
        handler.removeMessages(1);
        send("onPause");
    }

    public void onReplay() {
        handler.removeMessages(1);
        send("onReplay");
    }

    @Override
    public void onOpenStart() {
        super.onOpenStart();
        handler.removeMessages(1);
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
        send("onOpenFailed");
    }

    @Override
    public void onPlaybackComplete() {
        super.onPlaybackComplete();
        handler.removeMessages(1);
        send("onPlaybackComplete");
    }

    @Override
    public void onPlayError() {
        super.onPlayError();
        handler.removeMessages(1);
        send("onPlayError");
    }

    public void destory() {
        handler.removeMessages(1);
        send("destory");
    }
}
