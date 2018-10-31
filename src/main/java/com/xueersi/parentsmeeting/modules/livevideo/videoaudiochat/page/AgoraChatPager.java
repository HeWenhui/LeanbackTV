package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.page;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.AGEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business.AgoraVideoChatInter;
import com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business.VideoAudioChatHttp;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.widget.AgoraVolumeWaveView;
import com.xueersi.ui.widget.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

/**
 * Created by linyuqiang on 2018/10/17.
 */
public class AgoraChatPager extends BasePager implements AgoraVideoChatInter {
    private String TAG = "AgoraChatPager";
    private LiveAndBackDebug liveBll;
    private LiveGetInfo getInfo;
    private int netWorkType;
    private boolean isFail = false;
    private Activity activity;
    private WorkerThread mWorkerThread;
    /** 测试网络的 */
    private WorkerThread testWorkerThread;
    private LogToFile mLogtf;
    private AtomicBoolean startRemote;
    private String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;
    private String room;
    private VideoChatEvent videoChatEvent;
    private VideoAudioChatHttp videoChatHttp;
    private RelativeLayout rl_livevideo_chat_voice;
    private AgoraVolumeWaveView vw_livevideo_chat_voice;
    private boolean containMe = false;
    int stuid;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/interaction/";
    private LottieEffectInfo bubbleEffectInfo;
    private boolean initLottile1 = false;
    private boolean initLottile2 = false;
    private int micType;

    public AgoraChatPager(Activity activity, LiveAndBackDebug liveBll, LiveGetInfo getInfo, VideoChatEvent videoChatEvent, VideoAudioChatHttp videoChatHttp, int micType) {
        logger = LoggerFactory.getLogger(TAG);
        this.activity = activity;
        mContext = activity;
        this.videoChatEvent = videoChatEvent;
        this.videoChatHttp = videoChatHttp;
        this.startRemote = videoChatEvent.getStartRemote();
        this.liveBll = liveBll;
        this.getInfo = getInfo;
        this.micType = micType;
        netWorkType = NetWorkHelper.getNetWorkState(activity);
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.d("AgoraChatPager:netWorkType=" + netWorkType);
        initView();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(activity, R.layout.pager_live_video_chat_people, null);
        vw_livevideo_chat_voice = mView.findViewById(R.id.vw_livevideo_chat_voice);
        vw_livevideo_chat_voice.setVisibility(View.GONE);
        rl_livevideo_chat_voice = mView.findViewById(R.id.rl_livevideo_chat_voice);
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
        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "press/data.json";
        bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath);
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
            if (uid == stuid) {
                mLogtf.d("onUserJoined:uid=" + uid + ",elapsed=" + elapsed);
                vw_livevideo_chat_voice.start();
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            mLogtf.d("onUserOffline:uid=" + uid + ",reason=" + reason);
            if (uid == stuid) {
                vw_livevideo_chat_voice.stop();
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
            vw_livevideo_chat_voice.setVolume(volume / 2);
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
    public void startRecord(String method, final String room, final String nonce, final boolean video) {
        logger.d("startRecord:method=" + method + ",room=" + room + ",video=" + video);
        stuid = Integer.parseInt(getInfo.getStuId());
        containMe = true;
        this.room = room;
        if (testWorkerThread != null) {
            testWorkerThread.disableLastmileTest();
        }
        mWorkerThread = new WorkerThread(activity.getApplicationContext(), stuid, true);
        if (video) {
            mWorkerThread.setEnableLocalVideo(true);
            mWorkerThread.setOnEngineCreate(new WorkerThread.OnEngineCreate() {
                @Override
                public void onEngineCreate(final RtcEngine mRtcEngine) {
                    VideoEncoderConfiguration.VideoDimensions dimensions = VideoEncoderConfiguration.VD_320x240;
                    VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(dimensions,
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE);
                    int setVideoEncoder = mRtcEngine.setVideoEncoderConfiguration(configuration);
                    logger.d("onEngineCreate:setVideoEncoder=" + setVideoEncoder);
                    if (AppConfig.DEBUG) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ViewGroup container = activity.findViewById(R.id.rl_course_video_live_agora_content);
                                logger.d("onEngineCreate:containerb=" + container.getChildCount());
                                SurfaceView surfaceView = RtcEngine.CreateRendererView(activity);
                                surfaceView.setZOrderMediaOverlay(true);
                                container.addView(surfaceView, 400, 400);
                                mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
                                logger.d("onEngineCreate:containera=" + container.getChildCount());
                            }
                        });
                    }
                }
            });
        }
        mWorkerThread.eventHandler().addEventHandler(agEventHandler);
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = -1;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        mWorkerThread.joinChannel(null, room, stuid, new WorkerThread.OnJoinChannel() {
            @Override
            public void onJoinChannel(int joinChannel) {
                int colors[] = {0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff};
                vw_livevideo_chat_voice.setColors(colors);
                vw_livevideo_chat_voice.start();
                VideoChatLog.sno8(liveBll, nonce, room, joinChannel);
            }
        });
        show("startRecord");
    }

    public void setTestWorkerThread(WorkerThread testWorkerThread) {
        this.testWorkerThread = testWorkerThread;
    }

    public WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    @Override
    public void stopRecord() {
        logger.d("stopRecord:mWorkerThread=null?" + (mWorkerThread == null));
        if (mWorkerThread != null) {
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
        }

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
    public void removeMe() {
        containMe = false;
        stopRecord();
        hind("removeMe");
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
        } else if (size < 3) {
            rl_livevideo_chat_head1.setVisibility(View.VISIBLE);
            if (size == 1) {
                rl_livevideo_chat_head2.setVisibility(View.GONE);
            } else {
                rl_livevideo_chat_head2.setVisibility(View.VISIBLE);
            }
            {
                final ClassmateEntity classmateEntity1 = classmateEntities.get(0);
                CircleImageView civ_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.civ_livevideo_chat_head1);
                TextView tv_livevideo_chat_head1 = rl_livevideo_chat_head1.findViewById(R.id.tv_livevideo_chat_head1);
                setName(classmateEntity1, civ_livevideo_chat_head1, tv_livevideo_chat_head1);
                final ImageView iv_livevideo_chat_praise1 = rl_livevideo_chat_head1.findViewById(R.id.iv_livevideo_chat_praise1);
                TextView tv_livevideo_chat_count1 = rl_livevideo_chat_head1.findViewById(R.id.tv_livevideo_chat_count1);
                tv_livevideo_chat_count1.setVisibility(View.GONE);
                final LottieAnimationView pressLottileView = rl_livevideo_chat_head1.findViewById(R.id.lav_livevideo_chat_praise1);
                if (classmateEntity1.isMe() || micType == 0) {
                    if (micType == 0) {
                        iv_livevideo_chat_praise1.setVisibility(View.GONE);
                    } else {
                        if (classmateEntity1.isMe()) {
                            iv_livevideo_chat_praise1.setImageResource(R.drawable.live_task_zanhui_icon_normal);
                        }
                    }
                    pressLottileView.setVisibility(View.GONE);
                } else {
                    iv_livevideo_chat_praise1.setVisibility(View.INVISIBLE);
                    pressLottileView.setVisibility(View.VISIBLE);
                    if (!initLottile1) {
                        initLottile1 = true;
                        logger.d("setAnimationFromJson1");
                        pressLottileView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(activity), "press");
                        pressLottileView.useHardwareAcceleration(true);
                        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                            @Override
                            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                                Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(pressLottileView, lottieImageAsset.getFileName(),
                                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), activity);
                                return bitmap;
                            }
                        };
                        pressLottileView.setImageAssetDelegate(imageAssetDelegate);
                        iv_livevideo_chat_praise1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                pressLottileView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    @Override
                                    public boolean onPreDraw() {
                                        pressLottileView.getViewTreeObserver().removeOnPreDrawListener(this);
                                        int width = iv_livevideo_chat_praise1.getWidth();
                                        int height = iv_livevideo_chat_praise1.getHeight();
                                        int left = iv_livevideo_chat_praise1.getLeft();
                                        int top = iv_livevideo_chat_praise1.getTop();
                                        int lottieWidth = pressLottileView.getWidth();
                                        int lottieHeight = pressLottileView.getHeight();
                                        if (lottieWidth == 0 || lottieHeight == 0) {
                                            return false;
                                        }
                                        float scaleX = (46.0f * ScreenUtils.getScreenDensity()) / lottieWidth;
                                        float scaleY = (46.0f * ScreenUtils.getScreenDensity()) / lottieHeight;
                                        pressLottileView.setScaleX(scaleX);
                                        pressLottileView.setScale(scaleY);
                                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) pressLottileView.getLayoutParams();
                                        lp.width = lp.height = Math.min(lottieWidth, lottieHeight);
//                                            lp.leftMargin = left - (pressLottileView.getWidth() - iv_livevideo_chat_praise2.getWidth()) / 2;
//                                            lp.topMargin = top - (pressLottileView.getWidth() - iv_livevideo_chat_praise2.getWidth()) / 2;
                                        pressLottileView.setLayoutParams(lp);
                                        logger.d("onPreDraw:left1=" + width + ",lw=" + lottieWidth + ",scale1=" + scaleX + "," + scaleY);
                                        return false;
                                    }
                                });
                                iv_livevideo_chat_praise1.getViewTreeObserver().removeOnPreDrawListener(this);
                                return false;
                            }
                        });
                        pressLottileView.setTranslationX(20);
                        pressLottileView.setTranslationY(20);
                    }
                    pressLottileView.setOnClickListener(new PraiseClick(pressLottileView, classmateEntity1, tv_livevideo_chat_count1));
                }
            }
            if (size == 2) {
                final ClassmateEntity classmateEntity2 = classmateEntities.get(1);
                CircleImageView civ_livevideo_chat_head2 = rl_livevideo_chat_head2.findViewById(R.id.civ_livevideo_chat_head2);
                TextView tv_livevideo_chat_head2 = rl_livevideo_chat_head2.findViewById(R.id.tv_livevideo_chat_head2);
                setName(classmateEntity2, civ_livevideo_chat_head2, tv_livevideo_chat_head2);
                final ImageView iv_livevideo_chat_praise2 = rl_livevideo_chat_head2.findViewById(R.id.iv_livevideo_chat_praise2);
                TextView tv_livevideo_chat_count2 = rl_livevideo_chat_head2.findViewById(R.id.tv_livevideo_chat_count2);
                tv_livevideo_chat_count2.setVisibility(View.GONE);
                final LottieAnimationView pressLottileView = rl_livevideo_chat_head2.findViewById(R.id.lav_livevideo_chat_praise2);
                if (classmateEntity2.isMe() || micType == 0) {
                    if (micType == 0) {
                        iv_livevideo_chat_praise2.setVisibility(View.GONE);
                    } else {
                        if (classmateEntity2.isMe()) {
                            iv_livevideo_chat_praise2.setImageResource(R.drawable.live_task_zanhui_icon_normal);
                        }
                    }
                    pressLottileView.setVisibility(View.GONE);
                } else {
                    iv_livevideo_chat_praise2.setVisibility(View.INVISIBLE);
                    pressLottileView.setVisibility(View.VISIBLE);
                    if (!initLottile2) {
                        initLottile2 = true;
                        logger.d("setAnimationFromJson2");
                        pressLottileView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(activity), "press");
                        pressLottileView.useHardwareAcceleration(true);
                        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                            @Override
                            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                                Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(pressLottileView, lottieImageAsset.getFileName(),
                                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), activity);
                                return bitmap;
                            }
                        };
                        pressLottileView.setImageAssetDelegate(imageAssetDelegate);
                        iv_livevideo_chat_praise2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                pressLottileView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    @Override
                                    public boolean onPreDraw() {
                                        pressLottileView.getViewTreeObserver().removeOnPreDrawListener(this);
                                        int width = iv_livevideo_chat_praise2.getWidth();
                                        int height = iv_livevideo_chat_praise2.getHeight();
                                        int left = iv_livevideo_chat_praise2.getLeft();
                                        int top = iv_livevideo_chat_praise2.getTop();
                                        int lottieWidth = pressLottileView.getWidth();
                                        int lottieHeight = pressLottileView.getHeight();
                                        if (lottieWidth == 0 || lottieHeight == 0) {
                                            return false;
                                        }
                                        float scaleX = (46.0f * ScreenUtils.getScreenDensity()) / lottieWidth;
                                        float scaleY = (46.0f * ScreenUtils.getScreenDensity()) / lottieHeight;
                                        pressLottileView.setScaleX(scaleX);
                                        pressLottileView.setScale(scaleY);
                                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) pressLottileView.getLayoutParams();
                                        lp.width = lp.height = Math.min(lottieWidth, lottieHeight);
//                                            lp.leftMargin = left - (pressLottileView.getWidth() - iv_livevideo_chat_praise2.getWidth()) / 2;
//                                            lp.topMargin = top - (pressLottileView.getWidth() - iv_livevideo_chat_praise2.getWidth()) / 2;
                                        pressLottileView.setLayoutParams(lp);
                                        logger.d("onPreDraw:left2=" + width + ",lw=" + lottieWidth + ",scale2=" + scaleX + "," + scaleY);
                                        return false;
                                    }
                                });
                                iv_livevideo_chat_praise2.getViewTreeObserver().removeOnPreDrawListener(this);
                                return false;
                            }
                        });
                        pressLottileView.setTranslationX(20);
                        pressLottileView.setTranslationY(20);
                    }
                    pressLottileView.setOnClickListener(new PraiseClick(pressLottileView, classmateEntity2, tv_livevideo_chat_count2));
                }
            }
        }
    }

    private class PraiseClick implements View.OnClickListener {
        LottieAnimationView pressLottileView;
        TextView tv_livevideo_chat_count;
        ClassmateEntity classmateEntity;
        long before = 0;
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable praiseRunnable = new Runnable() {
            @Override
            public void run() {
                logger.d("praiseRunnable");
                before = 0;
                videoChatHttp.praise(classmateEntity.getId(), classmateEntity.getLikes());
                classmateEntity.setLikes(0);
            }
        };
        Runnable countRunnable = new Runnable() {
            @Override
            public void run() {
                logger.d("countRunnable");
                tv_livevideo_chat_count.setVisibility(View.GONE);
            }
        };

        public PraiseClick(LottieAnimationView pressLottileView, ClassmateEntity classmateEntity, TextView tv_livevideo_chat_count) {
            this.pressLottileView = pressLottileView;
            this.tv_livevideo_chat_count = tv_livevideo_chat_count;
            this.classmateEntity = classmateEntity;
        }

        @Override
        public void onClick(View v) {
            if (!pressLottileView.isAnimating()) {
                pressLottileView.playAnimation();
            }
            classmateEntity.setLikes(classmateEntity.getLikes() + 1);
            tv_livevideo_chat_count.setVisibility(View.VISIBLE);
            tv_livevideo_chat_count.setText("" + classmateEntity.getLikes());
            videoChatHttp.praise(classmateEntity.getId(), classmateEntity.getLikes());
            if (before == 0) {
                before = System.currentTimeMillis();
            }
            long time = 5000 - (System.currentTimeMillis() - before);
            logger.d("onClick:time=" + time);
            handler.removeCallbacks(praiseRunnable);
            if (time <= 0) {
                praiseRunnable.run();
            } else {
                handler.postDelayed(praiseRunnable, time);
            }
            handler.removeCallbacks(countRunnable);
            handler.postDelayed(countRunnable, 1000);
        }
    }

    private void setName(final ClassmateEntity classmateEntity, final CircleImageView civ_livevideo_chat_head, final TextView tv_livevideo_chat_head) {
        logger.d("setName:id=" + classmateEntity.getId() + ",name=" + classmateEntity.getName() + ",img=" + classmateEntity.getImg());
        tv_livevideo_chat_head.setText(classmateEntity.getName());
        if (StringUtils.isEmpty(classmateEntity.getName()) || StringUtils.isEmpty(classmateEntity.getImg())) {
            if (StringUtils.isEmpty(classmateEntity.getImg())) {
                civ_livevideo_chat_head.setImageResource(R.drawable.defult_head_img);
            } else {
                ImageLoader.with(activity).load(classmateEntity.getImg()).error(R.drawable.defult_head_img).into(civ_livevideo_chat_head);
            }
            videoChatHttp.getStuInfoByIds(classmateEntity.getId(), new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    HashMap<String, ClassmateEntity> classmateEntityHashMap = (HashMap<String, ClassmateEntity>) objData[0];
                    logger.d("onDataSucess:classmateEntityHashMap=" + classmateEntityHashMap.size());
                    ClassmateEntity classmateEntity1 = classmateEntityHashMap.get(classmateEntity.getId());
                    ImageLoader.with(activity).load(classmateEntity1.getImg()).error(R.drawable.defult_head_img).into(civ_livevideo_chat_head);
                    tv_livevideo_chat_head.setText(classmateEntity1.getName());
                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                    super.onDataFail(errStatus, failMsg);
                }
            });
        } else {
            ImageLoader.with(activity).load(classmateEntity.getImg()).error(R.drawable.defult_head_img).into(civ_livevideo_chat_head);
            tv_livevideo_chat_head.setText(classmateEntity.getName());
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

    public void show(String method) {
        logger.d("show:method=" + method);
        rl_livevideo_chat_voice.setVisibility(View.VISIBLE);
        vw_livevideo_chat_voice.setVisibility(View.VISIBLE);
    }

    public void hind(String method) {
        logger.d("hind:method=" + method);
        rl_livevideo_chat_voice.setVisibility(View.GONE);
        vw_livevideo_chat_voice.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        hind("onDestroy");
        super.onDestroy();
    }
}
