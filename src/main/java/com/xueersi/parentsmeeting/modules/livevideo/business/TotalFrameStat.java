package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linyuqiang on 2018/4/24.
 * 帧数统计
 */
public class TotalFrameStat extends PlayerService.SimpleVPlayerListener {
    private static String TAG = "TotalFrameStat";
    private LiveBll liveBll;
    private PlayerService vPlayer;
    /** 五秒帧数 */
    private ArrayList<String> frames = new ArrayList<>();
    /** 每秒帧数 */
    private ArrayList<Float> framesPs = new ArrayList<Float>();
    long frameStart;
    private Activity activity;
    private PlayServerEntity.PlayserverEntity lastPlayserverEntity;
    /** 是不是开始统计 */
    private boolean isStat = true;
    private BaseHttpBusiness baseHttpBusiness;
    private long openStart;
    String logurl = LiveVideoConfig.URL_CDN_LOG;

    public TotalFrameStat(Activity activity) {
        this.activity = activity;
        baseHttpBusiness = new BaseHttpBusiness(activity);
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
//        float lastFps = 0;

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
                        framesPs.add(fps);
                        float totalfps = 0;
                        if (framesPs.size() == 5) {
                            for (int i = 0; i < framesPs.size(); i++) {
                                Float f = framesPs.get(i);
                                totalfps += f;
                            }
                            framesPs.clear();
                            frames.add("" + (int) (totalfps / 5));
                            if (frames.size() == 12) {
                                send("frames12");
                            }
                        }
//                        if (lastFps != 0) {
//                            frames.add("" + ((int) ((lastFps + fps) * 5 / 2)));
//                        } else {
//                            frames.add("" + ((int) (fps * 5)));
//                        }
//                        lastFps = fps;
                    }
                }
            } catch (Exception e) {
                Loger.e(BaseApplication.getContext(), TAG, "handleMessage", e, true);
            }
            handler.sendEmptyMessageDelayed(1, 1000);
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
        framesPs.clear();
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
        openStart = System.currentTimeMillis();
    }

    @Override
    public void onOpenSuccess() {
        super.onOpenSuccess();
        handler.sendEmptyMessage(1);
        long openTime = (System.currentTimeMillis() - openStart);
        HashMap<String, String> defaultKey = new HashMap<>();
        defaultKey.put("dataType", "600");
        defaultKey.put("playlatency", "" + openTime);
        sendPostNoBusiness(defaultKey);
    }

    private void sendPostNoBusiness(HashMap<String, String> defaultKey) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("timestamp", "" + System.currentTimeMillis());
        params.addBodyParam("appid", AppConfig.getPsAppId());
        for (String key : defaultKey.keySet()) {
            String value = defaultKey.get(key);
            params.addBodyParam(key, value);
        }
        baseHttpBusiness.sendPostNoBusiness(logurl, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Loger.e(TAG, "sendPostNoBusiness:onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    Loger.d(TAG, "sendPostNoBusiness:onResponse:response=" + response.body().string());
                } else {
                    Loger.d(TAG, "sendPostNoBusiness:onResponse:response=null");
                }
            }

        });
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
