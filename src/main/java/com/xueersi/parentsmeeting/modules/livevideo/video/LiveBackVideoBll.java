package com.xueersi.parentsmeeting.modules.livevideo.video;

import android.app.Activity;
import android.net.Uri;

import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.module.videoplayer.media.VPlayerCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.StandExperienceVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.liveLog.LiveLogBill;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.utils.LiveBackVideoPlayerUtils;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveBackPlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/8/3.
 * 回放的视频播放
 * <p>
 * 直播回放的Bll，主要负责管理回放的视频播放
 * 类似的还有{@link StandExperienceVideoBll}
 */
public class LiveBackVideoBll {
    private String TAG = "LiveBackVideoBll";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private Activity activity;
    /**
     * 视频节对象
     */
    private VideoLivePlayBackEntity mVideoEntity;
    private LiveBackPlayerFragment liveBackPlayVideoFragment;
    /**
     * 节名称
     */
    private String mSectionName;
    private static int index = 0;
    private ArrayList<String> mWebPaths = new ArrayList<>();
    /**
     * 播放器核心服务
     */
    protected PlayerService vPlayer;
    private String mUri = "";
    /**
     * 进度缓存的追加KEY值
     */
    protected String mShareKey = "LiveBack";
    private boolean playbackComplete = false;
    private boolean islocal;

    public LiveBackVideoBll(Activity activity, boolean islocal) {
        this.activity = activity;
        this.islocal = islocal;
    }

    public void setSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public void setVideoEntity(VideoLivePlayBackEntity mVideoEntity) {
        this.mVideoEntity = mVideoEntity;
        String hostPath = "";
        try {
            hostPath = mVideoEntity.getHostPath();
            if (hostPath != null) {
                String videoPathNoHost = mVideoEntity.getVideoPathNoHost();
                JSONArray jsonArray = new JSONArray(hostPath);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.getString(i) + videoPathNoHost;
                    mWebPaths.add(url);
                }
                logger.d("setVideoEntity:hostPath=" + hostPath + ",videoPathNoHost=" + videoPathNoHost + ",mWebPaths=" + mWebPaths.size());
            }

            //英语1v2小组课 使用recordPath字段 替换videoPath字段
            if (mVideoEntity != null && mVideoEntity.getGetInfoStr() != null){
                JSONObject getinfo = new JSONObject(mVideoEntity.getGetInfoStr());
                int patten = getinfo.getInt("pattern");
                String recordPath = getinfo.getString("recordPath");
                if (patten == LiveVideoConfig.LIVE_PATTERN_GROUP_CLASS) {
                    mVideoEntity.setVideoPath(recordPath);
                }
            }
        } catch (Exception e) {
            logger.d("setVideoEntity:hostPath=" + hostPath, e);
        }
        mUri = mVideoEntity.getVideoPath();
        if (mWebPaths.isEmpty()) {
            // 播放视频
            String mWebPath = mVideoEntity.getVideoPath();
            mWebPaths.add(mWebPath);
        }
        if (mVideoEntity != null) {//卡顿后设置日志需要的liveid
            LiveLogBill.getInstance().setLiveId(mVideoEntity.getLiveId());
        }
    }

    public void onResume() {
    }

    public void onPause(long dur) {
    }

    public void onDestroy() {

    }

    public void seekTo(long pos) {
    }

    public void setLiveBackPlayVideoFragment(LiveBackPlayerFragment liveBackPlayVideoFragment) {
        this.liveBackPlayVideoFragment = liveBackPlayVideoFragment;
    }

    /**
     * PSIJK使用，改变线路播放
     */
//    public void changeLine(int pos) {
//        liveBackPlayVideoFragment.changePlayLive(pos, MediaPlayer.VIDEO_PROTOCOL_MP4);
//    }

    /** 切换到下一条线路 */
//    public void changeNextLine() {
//        this.nowPos++;
//        //当前线路小于总线路数
//        if (this.nowPos < totalRouteNum) {
//            changePlayLive(this.nowPos, MediaPlayer.VIDEO_PROTOCOL_MP4);
//        } else {
//            if (totalRouteNum != 0) {
//                this.nowPos = 0;
//                changePlayLive(this.nowPos, MediaPlayer.VIDEO_PROTOCOL_MP4);
//            } else {
//                playNewVideo();
//            }
//        }
//    }

//    private void changePlayLive(int pos, int protol) {
//        if (liveBackPlayVideoFragment != null) {
//            liveBackPlayVideoFragment.changePlayLive(pos, protol);
//        }
//    }

    /**
     * 播放新的视频
     */
    public void playNewVideo() {
        if (!MediaPlayer.getIsNewIJK()) {
            if (index < 0) {
                index = 0;
            }
            String url = mWebPaths.get(index++ % mWebPaths.size());
            logger.d("playNewVideo:url=" + url);
            liveBackPlayVideoFragment.playNewVideo(Uri.parse(url), mSectionName);
        } else {
            //使用PSIJK播放新视屏

            String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getVideoPath());
            if (!islocal) {
                int protocol = mVideoEntity.getProtocol();
                if (protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
                    videoPath = mVideoEntity.getFileId();
                } else {
                    protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
                }
                liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);
            } else {
                liveBackPlayVideoFragment.playPSFile(videoPath, (int) getStartPosition());
            }
            liveBackPlayVideoFragment.setmDisplayName(mSectionName);
        }
    }

    /**
     * 根据主讲辅导态播放新的视频
     */
    public void playNewVideo(int videoPlayStatus) {
        if (!MediaPlayer.getIsNewIJK()) {
            if (index < 0) {
                index = 0;
            }
            String url = mWebPaths.get(index++ % mWebPaths.size());
            logger.d("playNewVideo:url=" + url);
            liveBackPlayVideoFragment.playNewVideo(Uri.parse(url), mSectionName);
        } else {
            //使用PSIJK播放新视屏
            int protocol = mVideoEntity.getProtocol();
            if (videoPlayStatus == MediaPlayer.VIDEO_TEACHER_TUTOR_BEFORE_CLASS) {
                String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getBeforeClassFileId());
                if (!islocal) {
                    if (protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
                        videoPath = mVideoEntity.getBeforeClassFileId();
                    } else {
                        protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
                    }
                    liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);
                } else {
                    liveBackPlayVideoFragment.playPSFile(videoPath, (int) getStartPosition());
                }
            }else if(videoPlayStatus == MediaPlayer.VIDEO_TEACHER_TUTOR_AFTER_CLASS){
                String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getAfterClassFileId());
                if (!islocal) {
                    if (protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
                        videoPath = mVideoEntity.getAfterClassFileId();
                    } else {
                        protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
                    }
                    liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);
                } else {
                    liveBackPlayVideoFragment.playPSFile(videoPath, (int) getStartPosition());
                }
            }else {
                String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getVideoPath());
                if (!islocal) {
                    if (protocol == MediaPlayer.VIDEO_PROTOCOL_M3U8) {
                        videoPath = mVideoEntity.getFileId();
                    } else {
                        protocol = MediaPlayer.VIDEO_PROTOCOL_MP4;
                    }
                    liveBackPlayVideoFragment.playPSVideo(videoPath, protocol);
                } else {
                    liveBackPlayVideoFragment.playPSFile(videoPath, (int) getStartPosition());
                }
            }
            liveBackPlayVideoFragment.setmDisplayName(mSectionName);
        }
    }

    public void savePosition(long fromStart) {
        if (playbackComplete) {
            return;
        }
        if (vPlayer != null && mUri != null) {
            String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getVideoPath());
            logger.d("savePosition:videoPath=" + videoPath + ",fromStart=" + fromStart);
            ShareDataManager.getInstance().put(videoPath + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, fromStart,
                    ShareDataManager.SHAREDATA_USER);
        }
    }

    /**
     * 取出当前播放视频上次播放的点位
     */
    public long getStartPosition() {
        // if (mStartPos <= 0.0f || mStartPos >= 1.0f)
        try {

            String videoPath = LiveBackVideoPlayerUtils.handleBackVideoPath(mVideoEntity.getVideoPath());
            long pos = ShareDataManager.getInstance().getLong(videoPath + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, 0,
                    ShareDataManager.SHAREDATA_USER);
            logger.d("getStartPosition:videoPath=" + videoPath + ",pos=" + pos);
            return pos;
        } catch (Exception e) {
            // 有一定不知明原因造成取出的播放点位int转long型失败,故加上这个值确保可以正常观看
            e.printStackTrace();
            LiveCrashReport.postCatchedException(TAG, e);
            return 0L;
        }
        // return mStartPos;
    }

    public void onNetWorkChange(int netWorkType) {
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            boolean isInitialized = vPlayer.isInitialized();
            vPlayer.stop();
            liveBackPlayVideoFragment.resultFailed(0, 0);
        }
    }

    private int nowPos = 0;
    private int totalRouteNum = 0;

    public VPlayerCallBack.VPlayerListener getPlayListener() {
        return mPlayListener;
    }

    private VPlayerCallBack.VPlayerListener mPlayListener = new VPlayerCallBack.SimpleVPlayerListener() {

        @Override
        public void getPSServerList(int cur, int total, boolean modeChange) {
            super.getPSServerList(cur, total, modeChange);
            nowPos = cur;
            totalRouteNum = total;
        }

        @Override
        public void onOpenFailed(int arg1, int arg2) {
            logger.d("onOpenFailed:index=" + index + ",arg2=" + arg2);
            super.onOpenFailed(arg1, arg2);
        }

        @Override
        public void onOpenStart() {
            logger.d("onOpenStart");
            super.onOpenStart();
            playbackComplete = false;
        }

        @Override
        public void onOpenSuccess() {
            logger.d("onOpenSuccess:index=" + index);
            index--;
            super.onOpenSuccess();
        }

        @Override
        public void onSeekComplete() {
            super.onSeekComplete();
        }

        @Override
        public void onBufferStart() {
            super.onBufferStart();

        }

        @Override
        public void onPlaybackComplete() {
            super.onPlaybackComplete();
            savePosition(0);
            playbackComplete = true;
        }

        @Override
        public void onPlayError() {
            super.onPlayError();
        }
    };

}
