package com.xueersi.parentsmeeting.modules.livevideoOldIJK.video;

import android.app.Activity;
import android.net.Uri;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveBackPlayerFragment;

import org.json.JSONArray;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

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
    String mUri = "";
    /** 进度缓存的追加KEY值 */
    protected String mShareKey = "LiveBack";
    /** 直播帧数统计 */
    private LivePlayLog livePlayLog;
    boolean playbackComplete = false;
    boolean islocal;

    public LiveBackVideoBll(Activity activity, boolean islocal) {
        this.activity = activity;
        this.islocal = islocal;
        logger = LoggerFactory.getLogger("LiveBackVideoBll");
        if (islocal) {
            return;
        }
        livePlayLog = new LivePlayLog(activity, false);
    }

    public void setSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
        if (livePlayLog != null) {
            livePlayLog.setvPlayer(vPlayer);
        }
    }

    public void setVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
        if (livePlayLog != null) {
            livePlayLog.setChannelname(mVideoEntity.getLiveId());
        }
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
        mUri = mVideoEntity.getVideoPath();
        if (mWebPaths.isEmpty()) {
            // 播放视频
            String mWebPath = mVideoEntity.getVideoPath();
            mWebPaths.add(mWebPath);
        }
    }

    public void onResume() {
        if (livePlayLog != null) {
            livePlayLog.onReplay();
        }
    }

    public void onPause(long dur) {
        if (livePlayLog != null) {
            livePlayLog.onPause(dur);
        }
    }

    public void onDestroy() {

    }

    public void seekTo(long pos) {
        if (livePlayLog != null) {
            livePlayLog.seekTo(pos);
        }
    }

    public void setLiveBackPlayVideoFragment(LiveBackPlayerFragment liveBackPlayVideoFragment) {
        this.liveBackPlayVideoFragment = liveBackPlayVideoFragment;
        liveBackPlayVideoFragment.setLivePlayLog(livePlayLog);
    }

    public void playNewVideo() {
        if (index < 0) {
            index = 0;
        }
        String url = mWebPaths.get(index++ % mWebPaths.size());
        logger.d("playNewVideo:url=" + url);
        liveBackPlayVideoFragment.playNewVideo(Uri.parse(url), mSectionName);
    }

    public void savePosition(long fromStart) {
        if (playbackComplete) {
            return;
        }
        if (vPlayer != null && mUri != null) {
            ShareDataManager.getInstance().put(mUri + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, fromStart,
                    ShareDataManager.SHAREDATA_USER);
        }
    }

    /** 取出当前播放视频上次播放的点位 */
    public long getStartPosition() {
        // if (mStartPos <= 0.0f || mStartPos >= 1.0f)
        try {
            return ShareDataManager.getInstance().getLong(mUri + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, 0,
                    ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            // 有一定不知明原因造成取出的播放点位int转long型失败,故加上这个值确保可以正常观看
            e.printStackTrace();
            return 0L;
        }
        // return mStartPos;
    }

    public void onNetWorkChange(int netWorkType) {
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            boolean isInitialized = vPlayer.isInitialized();
            vPlayer.stop();
            liveBackPlayVideoFragment.resultFailed(0, 0);
            if (isInitialized && livePlayLog != null) {
                livePlayLog.onOpenFailed(0, AvformatOpenInputError.ENETDOWN.getNum());
            }
        }
    }

    public VPlayerCallBack.VPlayerListener getPlayListener() {
        return mPlayListener;
    }

    private VPlayerCallBack.VPlayerListener mPlayListener = new VPlayerCallBack.SimpleVPlayerListener() {
        @Override
        public void onOpenFailed(int arg1, int arg2) {
            logger.d("onOpenFailed:index=" + index + ",arg2=" + arg2);
            super.onOpenFailed(arg1, arg2);
            if (livePlayLog != null) {
                livePlayLog.onOpenFailed(arg1, arg2);
            }
        }

        @Override
        public void onOpenStart() {
            logger.d("onOpenStart");
            super.onOpenStart();
            if (livePlayLog != null) {
                livePlayLog.onOpenStart();
            }
            playbackComplete = false;
        }

        @Override
        public void onOpenSuccess() {
            logger.d("onOpenSuccess:index=" + index);
            index--;
            super.onOpenSuccess();
            if (livePlayLog != null) {
                livePlayLog.onOpenSuccess();
            }
        }

        @Override
        public void onSeekComplete() {
            super.onSeekComplete();
            if (livePlayLog != null) {
                livePlayLog.onSeekComplete();
            }
        }

        @Override
        public void onPlaybackComplete() {
            super.onPlaybackComplete();
            if (livePlayLog != null) {
                livePlayLog.onPlaybackComplete();
            }
            savePosition(0);
            playbackComplete = true;
        }

        @Override
        public void onPlayError() {
            super.onPlayError();
            if (livePlayLog != null) {
                livePlayLog.onPlayError();
            }
        }
    };

}
