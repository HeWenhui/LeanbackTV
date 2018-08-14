package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.net.Uri;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveBackPlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/8/3.
 * 回放的视频播放
 */
public class LiveBackVideoBll {
    Logger logger;
    Activity activity;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    LiveBackPlayerFragment liveBackPlayVideoFragment;
    /** 节名称 */
    private String mSectionName;
    static int index = 0;
    ArrayList<String> mWebPaths = new ArrayList<>();
    /** 播放器核心服务 */
    protected PlayerService vPlayer;

    public LiveBackVideoBll(Activity activity) {
        this.activity = activity;
        logger = LoggerFactory.getLogger("LiveBackVideoBll");
    }

    public void setSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public void setVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
        try {
            String hostPath = mVideoEntity.getHostPath();
            String videoPathNoHost = mVideoEntity.getVideoPathNoHost();
            JSONArray jsonArray = new JSONArray(hostPath);
            for (int i = 0; i < jsonArray.length(); i++) {
                String url = jsonArray.getString(i) + videoPathNoHost;
                mWebPaths.add(url);
            }
            logger.d("setVideoEntity:hostPath=" + hostPath + ",videoPathNoHost=" + videoPathNoHost + ",mWebPaths=" + mWebPaths.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mWebPaths.isEmpty()) {
            // 播放视频
            String mWebPath = mVideoEntity.getVideoPath();
            mWebPaths.add(mWebPath);
        }
    }

    public void setLiveBackPlayVideoFragment(LiveBackPlayerFragment liveBackPlayVideoFragment) {
        this.liveBackPlayVideoFragment = liveBackPlayVideoFragment;
    }

    public void playNewVideo() {
        String url = mWebPaths.get(index++ % mWebPaths.size());
        logger.d("playNewVideo:url=" + url);
        liveBackPlayVideoFragment.playNewVideo(Uri.parse(url), mSectionName);
    }

    public PlayerService.VPlayerListener getPlayListener() {
        return mPlayListener;
    }

    private PlayerService.VPlayerListener mPlayListener = new PlayerService.SimpleVPlayerListener() {
        @Override
        public void onOpenFailed(int arg1, int arg2) {
            logger.d("onOpenFailed:index=" + index + ",arg2=" + arg2);
            super.onOpenFailed(arg1, arg2);
        }

        @Override
        public void onOpenStart() {
            logger.d("onOpenStart");
            super.onOpenStart();
        }

        @Override
        public void onOpenSuccess() {
            logger.d("onOpenSuccess:index=" + index);
            index--;
            super.onOpenSuccess();
        }

    };

}
