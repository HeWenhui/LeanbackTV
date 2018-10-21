package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.page;

import android.app.Activity;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
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
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.ui.widget.CircleImageView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by linyuqiang on 2018/10/17.
 */
public class AgoraChatPager extends BasePager implements VideoChatInter {
    private String TAG = "AgoraChatPager";
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

    public AgoraChatPager(Activity activity, LiveAndBackDebug liveBll, LiveGetInfo getInfo, VideoChatEvent videoChatEvent) {
        this.activity = activity;
        this.videoChatEvent = videoChatEvent;
        this.startRemote = videoChatEvent.getStartRemote();
        this.liveBll = liveBll;
        this.getInfo = getInfo;
        netWorkType = NetWorkHelper.getNetWorkState(activity);
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.d("AgoraChatPager:netWorkType=" + netWorkType);
        initView();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(activity, R.layout.pager_live_video_chat_people, null);
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
        float videoRatio = LiveVideoConfig.VIDEO_RATIO;
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
            videoChatEvent.stopPlay();
            doRenderRemoteUi(uid);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            mLogtf.d("onJoinChannelSuccess:channel=" + channel + ",uid=" + uid);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {

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
            liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
        }

        @Override
        public void onVolume(int volume) {

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
        mWorkerThread = new WorkerThread(activity.getApplicationContext(), stuid, false);
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
        mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLevelChannel() {
            @Override
            public void onLevelChannel(int leaveChannel) {
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
        ViewGroup group = activity.findViewById(R.id.rl_course_video_live_agora_content);
        if (group != null) {
            group.removeAllViews();
        }
    }

    @Override
    public void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities) {
        RelativeLayout rl_livevideo_chat_head1 = mView.findViewById(R.id.rl_livevideo_chat_head1);
        RelativeLayout rl_livevideo_chat_head2 = mView.findViewById(R.id.rl_livevideo_chat_head2);
        int size = classmateEntities.size();
        mLogtf.d("updateUser:size=" + size);
        if (size == 0) {
            rl_livevideo_chat_head1.setVisibility(View.GONE);
            rl_livevideo_chat_head2.setVisibility(View.GONE);
        } else if (size == 1) {
            rl_livevideo_chat_head1.setVisibility(View.VISIBLE);
            rl_livevideo_chat_head2.setVisibility(View.GONE);
            {
                ClassmateEntity classmateEntity1 = classmateEntities.get(0);
                CircleImageView civ_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.civ_livevideo_chat_head1);
                TextView tv_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.tv_livevideo_chat_head1);
                ImageLoader.with(activity).load(classmateEntity1.getImg()).into(civ_livevideo_chat_head1);
                tv_livevideo_chat_head1.setText("1=林玉强" + classmateEntity1.getName());
            }
        } else {
            rl_livevideo_chat_head1.setVisibility(View.VISIBLE);
            rl_livevideo_chat_head2.setVisibility(View.VISIBLE);
            {
                ClassmateEntity classmateEntity1 = classmateEntities.get(0);
                CircleImageView civ_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.civ_livevideo_chat_head1);
                TextView tv_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.tv_livevideo_chat_head1);
                ImageLoader.with(activity).load(classmateEntity1.getImg()).into(civ_livevideo_chat_head1);
                tv_livevideo_chat_head1.setText("1=林玉强" + classmateEntity1.getName());
            }
            {
                ClassmateEntity classmateEntity2 = classmateEntities.get(1);
                CircleImageView civ_livevideo_chat_head2 = rl_livevideo_chat_head1.findViewById(R.id.civ_livevideo_chat_head2);
                TextView tv_livevideo_chat_head2 = rl_livevideo_chat_head1.findViewById(R.id.tv_livevideo_chat_head2);
                ImageLoader.with(activity).load(classmateEntity2.getImg()).into(civ_livevideo_chat_head2);
                tv_livevideo_chat_head2.setText("2=余婧" + classmateEntity2.getName());
            }
        }
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
