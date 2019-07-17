package com.xueersi.parentsmeeting.modules.livevideo.videochat.page;

import android.app.Activity;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by linyuqiang on 2017/5/8.
 */
public class AgoraVideoChatPager extends LiveBasePager implements VideoChatInter {
    private String TAG = "AgoraVideoChatPager";
    private LiveAndBackDebug liveBll;
    private LiveGetInfo getInfo;
    private int netWorkType;
    private boolean isFail = false;
    private Activity activity;
    private WorkerThread mWorkerThread;
    private LogToFile mLogtf;
    private AtomicBoolean startRemote;
    private String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;
    private String room;
    private VideoChatEvent videoChatEvent;
    private LiveViewAction liveViewAction;

    public AgoraVideoChatPager(Activity activity, LiveAndBackDebug liveBll, LiveGetInfo getInfo, VideoChatEvent videoChatEvent, LiveViewAction liveViewAction) {
        super(activity, false);
        this.activity = activity;
        this.videoChatEvent = videoChatEvent;
        this.liveViewAction = liveViewAction;
        this.startRemote = videoChatEvent.getStartRemote();
        this.liveBll = liveBll;
        this.getInfo = getInfo;
        netWorkType = NetWorkHelper.getNetWorkState(activity);
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.d("AgoraVideoChatPager:netWorkType=" + netWorkType);
        initView();
        initData();
    }

    @Override
    public View initView() {
        mView = liveViewAction.inflateView(R.layout.layout_livevideo_video_chat);
        return mView;
    }

    @Override
    public void initData() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        int windowWidth = liveVideoPoint.screenWidth;
        int windowHeight = ScreenUtils.getScreenHeight();
        float windowRatio = windowWidth / (float) windowHeight;
        float videoRatio = LiveVideoConfig.VIDEO_RATIO;
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        int paramsWidth, paramsHeight;
        paramsWidth = windowRatio < videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
        paramsHeight = windowRatio > videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
        if (lp.width != paramsWidth || lp.height != paramsHeight) {
            lp.width = paramsWidth;
            lp.height = paramsHeight;
//            view.setLayoutParams(lp);
            LayoutParamsUtil.setViewLayoutParams(mView, lp);
        }
    }

    private AGEventHandler agEventHandler = new AGEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            mLogtf.d("onFirstRemoteVideoDecoded:uid=" + uid);
            if (("" + uid).equals(getInfo.getMainTeacherId()) || ("" + uid).equals(getInfo.getTeacherId())) {
                startRemote.set(true);
                videoChatEvent.stopPlay();
                doRenderRemoteUi(uid);
                StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                if (studyReportAction != null) {
                    studyReportAction.onFirstRemoteVideoDecoded(uid);
                }
            }
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state) {

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            mLogtf.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
            if (studyReportAction != null) {
                studyReportAction.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            mLogtf.d("onUserOffline:uid=" + uid + ",reason=" + reason);
            StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
            if (studyReportAction != null) {
                studyReportAction.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onError(int err) {
            mLogtf.d("onError:err=" + err);
            StableLogHashMap logHashMap = new StableLogHashMap("AGEventHandlerError");
            logHashMap.put("channel_name", room);
            logHashMap.put("err", "" + err);
            liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
        }

        @Override
        public void onVolume(int volume) {

        }
    };

    private void doRenderRemoteUi(final int uid) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() || mWorkerThread == null) {
                    return;
                }
                ViewGroup group = liveViewAction.findViewById(R.id.rl_livevideo_agora_content);
                group.removeAllViews();
                SurfaceView surfaceV = RtcEngine.CreateRendererView(activity);
                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);
                mWorkerThread.getRtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_FIT, uid));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                //lp.rightMargin = 20;
                group.addView(surfaceV, lp);
            }
        });
    }

    @Override
    public void startRecord(String method, final String room, final String nonce, boolean video) {
        int stuid = Integer.parseInt(getInfo.getStuId());
        this.room = room;
        mWorkerThread = new WorkerThread(mContext.getApplicationContext(), stuid, false);
        mWorkerThread.eventHandler().addEventHandler(agEventHandler);
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        mWorkerThread.joinChannel(null, room, stuid, new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                VideoChatLog.sno8(liveBll, nonce, room, joinChannel);
            }
        });
    }

    @Override
    public void stopRecord() {
        mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLeaveChannel() {
            @Override
            public void onLeaveChannel(int leaveChannel) {
                StableLogHashMap logHashMap = new StableLogHashMap("getLeaveChannel");
                logHashMap.put("status", (leaveChannel == 0 ? "1" : "0"));
                if (leaveChannel != 0) {
                    logHashMap.put("errcode", "" + leaveChannel);
                }
                liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
            }
        });
        mWorkerThread.eventHandler().removeEventHandler(agEventHandler);
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
        if (startRemote.get()) {
            startRemote.set(false);
            videoChatEvent.rePlay(false);
        }
        ViewGroup group = liveViewAction.findViewById(R.id.rl_livevideo_agora_content);
        if (group != null) {
            group.removeAllViews();
        }
    }

    @Override
    public void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities) {

    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        logger.i("onNetWorkChange:netWorkType=" + netWorkType + ",isFail=" + isFail);
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            isFail = true;
        } else {
            if (isFail) {
                isFail = false;
            }
        }
    }

}
