package com.xueersi.parentsmeeting.modules.livevideo.remark.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewStub;

import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.video.SampleLiveVPlayerListener;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VPlayerListenerReg;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStartChange;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/11.
 */

public class LiveRemarkIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private PlayerService vPlayer;
    private LiveRemarkBll liveRemarkBll;
    LiveTextureView liveTextureView;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    VideoView videoView;

    public LiveRemarkIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        vPlayer = (PlayerService) data.get("vPlayer");
        this.videoView = (VideoView) data.get("videoView");
        BaseLiveMediaControllerBottom controllerBottom = (BaseLiveMediaControllerBottom) data.get("liveMediaControllerBottom");
        this.liveMediaControllerBottom = controllerBottom;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if ("1".equals(mGetInfo.getIsShowMarkPoint())) {
            liveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
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

                @Override
                public void onPlaybackComplete() {
                    if (liveRemarkBll != null) {
                        liveRemarkBll.setVideoReady(false);
                    }
                }
            });
        } else {
            mLiveBll.removeBusinessBll(this);
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
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        if (liveRemarkBll != null) {
            post(new Runnable() {
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
                liveRemarkBll.setGaosan(mGetInfo.getIsSeniorOfHighSchool() == 1);
                liveRemarkBll.setBottom(mRootView);
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
                        if (viewStub == null) {
                            viewStub = findViewById(R.id.vs_course_video_video_texture);
                        }
                        if (viewStub == null) {
                            liveTextureView = activity.findViewById(R.id.ltv_course_video_video_texture);
                        } else {
                            liveTextureView = (LiveTextureView) viewStub.inflate();
                        }
                        if (liveTextureView != null) {
                            liveTextureView.vPlayer = vPlayer;
                            liveTextureView.setLayoutParams(videoView.getLayoutParams());
                        }
                    }
                    liveRemarkBll.setLiveId(mLiveId);
                    liveRemarkBll.setLiveMediaControllerBottom(liveMediaControllerBottom);
                    liveRemarkBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                    liveRemarkBll.setHttpManager(getHttpManager());
                    liveRemarkBll.setSysTimeOffset(mLiveBll.getSysTimeOffset());
                    mLogtf.i("setlivebll____onbreak:" + mLiveBll.getLiveTopic().getMainRoomstatus().isOnbreak()
                            + "   stat:" + mGetInfo.getStat() + "   mode:" + mLiveBll.getLiveTopic().getMode());
                    //上课状态并且是主讲或者是高三非专属老师课程
                    if (!mLiveBll.getLiveTopic().getMainRoomstatus().isOnbreak() && (LiveTopic.MODE_CLASS.equals(mLiveBll.getLiveTopic().getMode())
                            || (mGetInfo.getIsSeniorOfHighSchool() == 1 && mGetInfo.ePlanInfo == null))) {
                        liveRemarkBll.setClassReady(true);
                    } else {
                        liveRemarkBll.setClassReady(false);
                    }
                    liveRemarkBll.setVideoLayout(LiveVideoPoint.getInstance());
                }
            } else {
                liveRemarkBll.initData();
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        String msg = "onNotice";
        switch (type) {
            case XESCODE.CLASSBEGIN:
                try {
                    boolean begin = object.getBoolean("begin");
                    mLiveBll.getLiveTopic().getMainRoomstatus().setClassbegin(begin);
                    msg += begin ? "CLASSBEGIN" : "CLASSEND";
                    logger.i("classBegin____onbreak:" + mLiveBll.getLiveTopic().getMainRoomstatus().isOnbreak()
                            + "   mode:" + mLiveBll.getLiveTopic().getMode());
                    //上课状态并且是主讲或者是高三非专属老师课程
                    if (!mLiveBll.getLiveTopic().getMainRoomstatus().isOnbreak() && (liveRemarkBll != null && LiveTopic
                            .MODE_CLASS.equals(mLiveBll.getLiveTopic().getMode()) || (mGetInfo.getIsSeniorOfHighSchool() == 1 && mGetInfo.ePlanInfo == null))) {
                        liveRemarkBll.setClassReady(true);
                    }
                    if (liveRemarkBll != null) {
                        liveRemarkBll.showMarkGuide();
                    }
                } catch (Exception e) {

                }
                break;
            case XESCODE.MARK_POINT_TIP:
                if (liveRemarkBll != null) {
                    liveRemarkBll.showMarkTip(object.optInt("markType"));
                }
                break;
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        if (liveRemarkBll != null) {
            liveRemarkBll.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.CLASSBEGIN, XESCODE.MARK_POINT_TIP};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (liveRemarkBll != null) {
            //主讲
            logger.i("ontopic____onbreak:" + mLiveBll.getLiveTopic().getMainRoomstatus().isOnbreak()
                    + "   mode:" + mLiveBll.getLiveTopic().getMode());
            //上课状态并且是主讲或者是高三非专属老师课程
            if (!liveTopic.getMainRoomstatus().isOnbreak() && (liveTopic.getMode().equals(LiveTopic
                    .MODE_CLASS) || (mGetInfo.getIsSeniorOfHighSchool() == 1 && mGetInfo.ePlanInfo == null))) {
                liveRemarkBll.setClassReady(true);
            } else {
                liveRemarkBll.setClassReady(false);
            }
        }
    }
}
