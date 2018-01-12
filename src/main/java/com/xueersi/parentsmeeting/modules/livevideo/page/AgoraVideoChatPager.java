package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by Administrator on 2017/5/8.
 */

public class AgoraVideoChatPager extends BasePager implements VideoChatInter {
    String TAG = "AgoraVideoChatPager";
    LiveBll liveBll;
    LiveGetInfo getInfo;
    int netWorkType;
    boolean isFail = false;
    LiveVideoActivity activity;
    WorkerThread mWorkerThread;
    private LogToFile mLogtf;
    private AtomicBoolean startRemote;
    String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;
    String room;

    public AgoraVideoChatPager(Activity activity, LiveBll liveBll, LiveGetInfo getInfo) {
        this.activity = (LiveVideoActivity) activity;
        startRemote = ((LiveVideoActivity) activity).getStartRemote();
        this.liveBll = liveBll;
        this.getInfo = getInfo;
        netWorkType = NetWorkHelper.getNetWorkState(activity);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.d("AgoraVideoChatPager:netWorkType=" + netWorkType);
        initView();
        initData();
    }

    @Override
    public View initView() {
        mView = new View(activity);
        mView.setVisibility(View.GONE);
        return mView;
    }

    @Override
    public void initData() {
        View view = activity.findViewById(R.id.rl_course_video_live_agora_content);
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int windowWidth = screenWidth;
        int windowHeight = ScreenUtils.getScreenHeight();
        float windowRatio = windowWidth / (float) windowHeight;
        float videoRatio = LiveVideoActivity.VIDEO_RATIO;
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int paramsWidth, paramsHeight;
        paramsWidth = windowRatio < videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
        paramsHeight = windowRatio > videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
        if (lp.width != paramsWidth || lp.height != paramsHeight) {
            lp.width = paramsWidth;
            lp.height = paramsHeight;
//            view.setLayoutParams(lp);
            LayoutParamsUtil.setViewLayoutParams(view, lp);
        }
    }

    private AGEventHandler agEventHandler = new AGEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            mLogtf.d("onFirstRemoteVideoDecoded:uid=" + uid);
            startRemote.set(true);
            activity.stopPlay();
            doRenderRemoteUi(uid);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            mLogtf.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            mLogtf.d("onUserOffline:uid=" + uid + ",reason=" + reason);
        }

        @Override
        public void onError(int err) {
            mLogtf.d("onError:err=" + err);
            StableLogHashMap logHashMap = new StableLogHashMap("AGEventHandlerError");
            logHashMap.put("channel_name", room);
            logHashMap.put("err", "" + err);
            liveBll.umsAgentDebug(eventId, logHashMap.getData());
        }
    };

    private void doRenderRemoteUi(final int uid) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() || mWorkerThread == null) {
                    return;
                }
                ViewGroup group = (ViewGroup) activity.findViewById(R.id.rl_course_video_live_agora_content);
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
    public void startRecord(String method, final String room, final String nonce) {
        int stuid = Integer.parseInt(getInfo.getStuId());
        this.room = room;
        mWorkerThread = new WorkerThread(activity.getApplicationContext(), stuid);
        mWorkerThread.eventHandler().addEventHandler(agEventHandler);
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        mWorkerThread.joinChannel(null, room, stuid, new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                VideoChatLog.sno4(liveBll, nonce, room, joinChannel);
            }
        });
    }

    @Override
    public void stopRecord() {
        mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLevelChannel() {
            @Override
            public void onLevelChannel(int leaveChannel) {
                StableLogHashMap logHashMap = new StableLogHashMap("getLeaveChannel");
                logHashMap.put("status", (leaveChannel == 0 ? "1" : "0"));
                if (leaveChannel != 0) {
                    logHashMap.put("errcode", "" + leaveChannel);
                }
                liveBll.umsAgentDebug(eventId, logHashMap.getData());
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
            activity.rePlay();
        }
        ViewGroup group = (ViewGroup) activity.findViewById(R.id.rl_course_video_live_agora_content);
        group.removeAllViews();
    }

    @Override
    public void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities) {

    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        Loger.i(TAG, "onNetWorkChange:netWorkType=" + netWorkType + ",isFail=" + isFail);
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            isFail = true;
        } else {
            if (isFail) {
                isFail = false;
            }
        }
    }
}
