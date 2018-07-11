package com.xueersi.parentsmeeting.modules.livevideo.remark.business;

import android.app.Activity;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.SampleLiveVPlayerListener;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VPlayerListenerReg;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;

/**
 * Created by lyqai on 2018/7/11.
 */

public class LiveRemarkIRCBll extends LiveBaseBll {
    private PlayerService vPlayer;
    private LiveRemarkBll liveRemarkBll;
    LiveTextureView liveTextureView;
    LiveMediaControllerBottom liveMediaControllerBottom;
    VideoView videoView;

    public LiveRemarkIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
    }

    public void setvPlayer(PlayerService vPlayer) {
        this.vPlayer = vPlayer;
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void setLiveMediaControllerBottom(LiveMediaControllerBottom liveMediaControllerBottom) {
        this.liveMediaControllerBottom = liveMediaControllerBottom;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if ("1".equals(mGetInfo.getIsShowMarkPoint())) {
            VPlayerListenerReg vPlayerListenerReg = ProxUtil.getProxUtil().get(activity, VPlayerListenerReg.class);
            vPlayerListenerReg.addVPlayerListener(new SampleLiveVPlayerListener() {
                @Override
                public void onBufferTimeOutRun() {
                    if (liveRemarkBll != null) {
                        liveRemarkBll.setVideoReady(false);
                    }
                }

                @Override
                public void onOpenFailed(int arg1, int arg2) {
                    if (liveRemarkBll != null) {
                        liveRemarkBll.setVideoReady(false);
                    }
                }

                @Override
                public void onOpenSuccess() {
                    LiveRemarkIRCBll.this.onOpenSuccess();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (liveRemarkBll != null) {
            liveRemarkBll.onPause();
        }
    }

    @Override
    public void onModeChange(String mode, boolean isPresent) {
        super.onModeChange(mode, isPresent);
        if (liveRemarkBll != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    liveRemarkBll.setVideoReady(false);
                }
            });
        }
    }

    public void onOpenSuccess() {
        if (mGetInfo != null && "1".equals(mGetInfo.getIsShowMarkPoint())) {
            if (liveRemarkBll == null) {
                liveRemarkBll = new LiveRemarkBll(mContext, vPlayer);
                VideoChatStartChange videoChatBll = getInstance(VideoChatStartChange.class);
                if (videoChatBll != null) {
                    videoChatBll.addVideoChatStatrtChange(new VideoChatStartChange.ChatStartChange() {
                        @Override
                        public void onVideoChatStartChange(boolean start) {
                            liveRemarkBll.setOnChat(start);
                        }
                    });
                }
                if (mLiveBll != null && liveMediaControllerBottom != null) {
                    if (liveTextureView == null) {
                        ViewStub viewStub = (ViewStub) activity.findViewById(R.id.vs_course_video_video_texture);
                        liveTextureView = (LiveTextureView) viewStub.inflate();
                        liveTextureView.vPlayer = vPlayer;
                        liveTextureView.setLayoutParams(videoView.getLayoutParams());
                    }
                    liveRemarkBll.showBtMark();
                    liveRemarkBll.setTextureView(liveTextureView);
                    liveRemarkBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
                    liveRemarkBll.setLiveAndBackDebug(mLiveBll);
                }
            } else {
                liveRemarkBll.initData();
            }
        }
    }

}
