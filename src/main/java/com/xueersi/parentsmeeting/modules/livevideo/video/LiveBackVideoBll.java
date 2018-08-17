package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.net.Uri;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
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
    String mUri = "";
    /** 进度缓存的追加KEY值 */
    protected String mShareKey = "LiveBack";

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
        mUri = mVideoEntity.getVideoPath();
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

    public void savePosition(long fromStart) {
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
            vPlayer.stop();
            liveBackPlayVideoFragment.resultFailed(0, 0);
        }
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
