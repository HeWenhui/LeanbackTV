package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.MicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RaiseHandDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveScienceHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.AgoraVideoChatPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.common.permission.PermissionCallback;
import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

//import com.xueersi.parentsmeeting.modules.livevideo.page.LicodeVideoChatPager;

/**
 * Created by Administrator on 2017/5/8.
 * 直播接麦
 */
public class VideoChatBll implements VideoChatAction {
    private static String TAG = "VideoChatBll";
    private String eventId = LiveVideoConfig.LIVE_LINK_MIRCO;
    private Activity activity;
    private VideoChatEvent videoChatEvent;
    private Button btRaiseHands;
    private LiveBll liveBll;
    private LiveGetInfo getInfo;
    private boolean raisehand = false;
    private RaiseHandDialog raiseHandDialog;
    /** 暂时没用 */
    private int times = 0;
    private LogToFile mLogtf;
    private long startTime;
    private VideoChatInter videoChatInter;
    private RelativeLayout bottomContent;
    private BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
    /** 麦克风权限 */
    private boolean isHasPermission = true;
    /** 举麦权限提示 */
    private boolean permissionPrompt = false;
    /** 举麦失败 */
    private boolean isFail = false;
    private boolean isSuccess = false;
    /** 举麦包含我 */
    private boolean containMe = false;
    /** 连麦状态 */
    private String onMic = "off";
    /** 房间号 */
    private String room = "";
    private static int nativeLibLoaded = 2;
    /*举麦耳机提示*/
    private boolean headsetPrompt = false;
    /** 连麦人数 */
    private ArrayList<ClassmateEntity> classmateEntities = new ArrayList<>();
    /** 连麦人数变化 */
    private boolean classmateChange = true;
    /** 举手人数 */
    private int raiseHandCount = 0;
    /** 举手来源 */
    private String from;
    /** 接麦耳机判断 */
    private boolean hasWiredHeadset = false;
    private WiredHeadsetReceiver wiredHeadsetReceiver;
    private String openhandsStatus = "off";
    private String onmicStatus = "off";
    private LiveRemarkBll mLiveRemarkBll;

    public VideoChatBll(Activity activity, VideoChatEvent videoChatEvent) {
        this.activity = activity;
        this.videoChatEvent = videoChatEvent;
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void onLiveInit(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        boolean allowLinkMic = getInfo.isAllowLinkMic();
        if (allowLinkMic) {
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        isHasPermission = isHasPermission() != -1;
//                    } catch (Exception e) {
//                        Loger.e(activity, TAG, "onLiveInit", e, true);
//                        isHasPermission = true;
//                    }
//                }
//            }.start();
            wiredHeadsetReceiver = new WiredHeadsetReceiver();
            activity.registerReceiver(wiredHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            btRaiseHands.setVisibility(View.VISIBLE);
        } else {
            btRaiseHands.setVisibility(View.GONE);
        }
//        startRecord();
    }

    private static final int BUFFER_SIZE_FACTOR = 2;
    // Requested size of each recorded buffer provided to the client.
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    // Average number of callbacks per second.
    private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;
    // Default audio data format is PCM 16 bit per sample.
    // Guaranteed to be supported by all devices.
    private static final int BITS_PER_SAMPLE = 16;
    private ByteBuffer byteBuffer;

    private void checkNativeLibLoaded() {
        try {
            System.loadLibrary("jingle_peerconnection_so");
            nativeLibLoaded = 1;
            XesMobAgent.webrtcInit(true);
            mLogtf.i("checkNativeLibLoaded");
        } catch (UnsatisfiedLinkError t) {
            final StringBuilder builder = new StringBuilder();
            if (Build.VERSION.SDK_INT >= 21) {
                String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
                for (int i = 0; i < SUPPORTED_ABIS.length; i++) {
                    builder.append(SUPPORTED_ABIS[i] + ",");
                }
            } else {
                builder.append(Build.CPU_ABI + "," + Build.CPU_ABI2 + ",");
            }
            MobclickAgent.reportError(activity, new Error(Build.MANUFACTURER + "$" + Build.MODEL + "$" + builder + "-" + -1, t));
            XesMobAgent.webrtcInit(false);
            nativeLibLoaded = 0;
            mLogtf.e("checkNativeLibLoaded", t);
        }
    }

    public void setLiveRemarkBll(LiveRemarkBll liveRemarkBll) {
        mLiveRemarkBll = liveRemarkBll;
    }

    private int isHasPermission() {
        int sampleRate = 48000;
        int channels = 1;
        final int framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
        final int channelConfig = channelCountToConfiguration(channels);
        int minBufferSize =
                AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return -1;
        }
        final int bytesPerFrame = channels * (BITS_PER_SAMPLE / 8);
        byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * framesPerBuffer);
        int bufferSizeInBytes = Math.max(BUFFER_SIZE_FACTOR * minBufferSize, byteBuffer.capacity());
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release();
            return -1;
        }
        audioRecord.release();
        return framesPerBuffer;
    }

    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int STATE_UNPLUGGED = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int HAS_MIC = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", STATE_UNPLUGGED);
            int microphone = intent.getIntExtra("microphone", HAS_NO_MIC);
            String name = intent.getStringExtra("name");
            Loger.d(TAG, "WiredHeadsetReceiver.onReceive:a=" + intent.getAction() + ", s="
                    + (state == STATE_UNPLUGGED ? "unplugged" : "plugged") + ", m="
                    + (microphone == HAS_MIC ? "mic" : "no mic") + ", n=" + name);
            hasWiredHeadset = (state == STATE_PLUGGED);
        }
    }

    private int channelCountToConfiguration(int channels) {
        return (channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO);
    }

    public void startRecord(final String room, final String nonce) {
        if (nativeLibLoaded == -1) {
            new Thread() {
                @Override
                public void run() {
                    checkNativeLibLoaded();
                    bottomContent.post(new Runnable() {
                        @Override
                        public void run() {
                            startRecord(room, nonce);
                        }
                    });
                }
            }.start();
            return;
        }
        if (videoChatInter != null) {
            videoChatInter.updateUser(classmateChange, classmateEntities);
            return;
        }
        AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
        audioRequest.request(null);
        if (mLiveRemarkBll != null) {
            mLiveRemarkBll.setOnChat(true);
        }
        if (nativeLibLoaded != 2) {
            videoChatEvent.setVolume(0, 0);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (nativeLibLoaded == 1) {
//            videoChatInter = new LicodeVideoChatPager(activity, this, classmateEntities, getInfo, liveBll, baseLiveMediaControllerBottom);
                } else if (nativeLibLoaded == 0) {
                    //不会发生
//                    videoChatInter = new VideoChatPager(activity, liveBll, getInfo);
                    return;
                } else {
                    videoChatInter = new AgoraVideoChatPager(activity, liveBll, getInfo, videoChatEvent);
                }
                startTime = System.currentTimeMillis();
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                int screenWidth = ScreenUtils.getScreenWidth();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                final View rootView = videoChatInter.getRootView();
                if (nativeLibLoaded == 2) {

                } else {
                    int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
                    lp.rightMargin = wradio;
                    bottomContent.addView(rootView, lp);
                }
                getInfo.setStuLinkMicNum(getInfo.getStuLinkMicNum() + 1);
                if (isHasPermission) {
                    videoChatInter.startRecord("onLiveInit", room, nonce);
                }
                if (nativeLibLoaded != 2) {
                    rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id.v_livevideo_mediacontrol_bottom);
                            rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom.getLayoutParams();
                            int height = rootView.getHeight();
                            if (lp.height != height) {
                                lp.height = height;
//                        vMediacontrolBottom.setLayoutParams(lp);
                                LayoutParamsUtil.setViewLayoutParams(vMediacontrolBottom, lp);
                            }
                            return false;
                        }
                    });
                }
            }
        };
        checkPermissionUnPerList(new OnJoinPermissionFinish(onMic, openhandsStatus, room, from, containMe, runnable));
        if (isHasPermission) {
            runnable.run();
        }
    }

    View.OnClickListener btRaiseHandsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            baseLiveMediaControllerBottom.onChildViewClick(v);
            if (btRaiseHands.getAlpha() == 1.0f && videoChatInter == null && isHasPermission && (!isSuccess && !isFail)) {
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String nonce = StableLogHashMap.creatNonce();
                        VideoChatLog.sno4(liveBll, nonce);
                        raisehand = true;
                        liveBll.requestMicro(nonce, from);
                        LiveScienceHttpManager liveScienceHttpManager = liveBll.getLiveScienceHttpManager();
                        if (liveScienceHttpManager != null) {
                            liveScienceHttpManager.chatHandAdd(new HttpCallBack(false) {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    Loger.d(TAG, "chatHandAdd:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    super.onPmError(responseEntity);
                                    Loger.d(TAG, "chatHandAdd:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    super.onPmFailure(error, msg);
                                    Loger.e(TAG, "chatHandAdd:onPmFailure:responseEntity=" + msg);
                                }
                            });
                        }
                        BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                        raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                        raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                        raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                        raiseHandDialog.showDialog();
                        if ("on".equals(onMic)) {
                            final RaiseHandDialog finalRaiseHandDialog = raiseHandDialog;
                            bottomContent.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalRaiseHandDialog == raiseHandDialog) {
                                        finalRaiseHandDialog.cancelDialog();
                                        raiseHandDialog = null;
                                    }
                                }
                            }, 3000);
                        }
                    }
                };
                if (nativeLibLoaded == -1) {
                    checkNativeLibLoaded();
                }
                if (!hasWiredHeadset) {
                    if (!headsetPrompt) {
                        headsetPrompt = true;
                        VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false, VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                        verifyCancelAlertDialog.initInfo("提醒", "插上耳麦再举手吧，否则上麦会有杂音。");
                        verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runnable.run();
                            }
                        });
                        verifyCancelAlertDialog.showDialog();
                        return;
                    }
                }
                runnable.run();
            }
        }
    };

    public void setControllerBottom(final BaseLiveMediaControllerBottom liveMediaControllerBottom) {
        this.baseLiveMediaControllerBottom = liveMediaControllerBottom;
        Button oldBtRaiseHands = btRaiseHands;
        btRaiseHands = liveMediaControllerBottom.findViewById(R.id.bt_livevideo_voicechat_raise_hands);
        btRaiseHands.setAlpha(0.4f);
        btRaiseHands.setOnClickListener(btRaiseHandsListener);
        if (oldBtRaiseHands != null) {
            Loger.d(TAG, "setControllerBottom:old=" + oldBtRaiseHands.hashCode() + "," + btRaiseHands.hashCode());
        }
    }

    private RaiseHandDialog.RaiseHandGiveup raiseHandGiveup = new RaiseHandDialog.RaiseHandGiveup() {

        @Override
        public void onGiveup() {
            btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
            raisehand = false;
            liveBll.giveupMicro(from);
            raiseHandDialog.cancelDialog();
            raiseHandDialog = null;
        }
    };

    @Override
    public void onJoin(final String onmic, final String openhands, final String room, boolean classmateChange, final ArrayList<ClassmateEntity> classmateEntities, final String from) {
        boolean contain = false;
        onMic = onmic;
        this.room = room;
        this.from = from;
        this.classmateChange = classmateChange;
        this.classmateEntities = classmateEntities;
        for (ClassmateEntity classmateEntity : classmateEntities) {
            if (classmateEntity.getId().equals(getInfo.getStuId())) {
                contain = true;
                break;
            }
        }
        mLogtf.d("onJoin:onmic=" + onmic + ",openhands=" + openhands + ",size=" + classmateEntities.size() + ",classmateChange=" + classmateChange + ",contain=" + contain + ",isSuccess=" + isSuccess);
        final boolean oldContainMe = containMe;
        containMe = contain;
        final boolean finalContain = contain;
        if (finalContain) {
            headsetPrompt = true;
        }
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                //开麦
                String oldonmicStatus = onmicStatus;
                onmicStatus = onmic;
                final String oldOpenhandsStatus = openhandsStatus;
                openhandsStatus = openhands;
                if ("on".equals(onmic)) {
                    btRaiseHands.setAlpha(1.0f);
                    if ("on".equals(oldonmicStatus) && oldContainMe != containMe) {
                        final MicTipDialog micTipDialog = new MicTipDialog(activity);
                        if (finalContain) {
                            micTipDialog.setSuccess("老师补位选中了你!");
                        } else {
                            micTipDialog.setFail("你已被移出语音聊天室！\n" +
                                    "耐心等待下次连麦机会！");
                        }
//                        Map<String, String> mData = new HashMap<>();
//                        mData.put("log_type", "getKick");
//                        mData.put("teacher_type", from);
//                        mData.put("status", finalContain ? "on" : "off");
//                        liveBll.umsAgentDebugSys(eventId, mData);
                        micTipDialog.showDialog();
                    }
                    if (finalContain) {
                        if (raiseHandDialog != null) {
                            raiseHandDialog.cancelDialog();
                            raiseHandDialog = null;
                        }
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                        startRecord(room, "");
                    } else {
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
                        if (raiseHandDialog != null) {
                            boolean set = raiseHandDialog.setFail();
                            isFail = true;
                            if (set) {
                                final RaiseHandDialog finalRaiseHandDialog = raiseHandDialog;
                                btRaiseHands.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalRaiseHandDialog == raiseHandDialog) {
                                            finalRaiseHandDialog.cancelDialog();
                                            raiseHandDialog = null;
                                        }
                                    }
                                }, 3000);
                            }
                        }
                        if (videoChatInter != null) {
                            View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id.v_livevideo_mediacontrol_bottom);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom.getLayoutParams();
                            int height = 1;
                            if (lp.height != height) {
                                lp.height = height;
//                                vMediacontrolBottom.setLayoutParams(lp);
                                LayoutParamsUtil.setViewLayoutParams(vMediacontrolBottom, lp);
                            }
                            bottomContent.removeView(videoChatInter.getRootView());
                            stopRecord();
                            videoChatInter = null;
                            mLogtf.d("MIC_TIME:onJoin(onmic.on):time=" + (System.currentTimeMillis() - startTime));
                            if (nativeLibLoaded != 2) {
                                videoChatEvent.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
                            }
                        }
                    }
                } else {//关麦-举手状态
                    if (videoChatInter != null) {
                        View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id.v_livevideo_mediacontrol_bottom);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom.getLayoutParams();
                        int height = 1;
                        if (lp.height != height) {
                            lp.height = height;
//                            vMediacontrolBottom.setLayoutParams(lp);
                            LayoutParamsUtil.setViewLayoutParams(vMediacontrolBottom, lp);
                        }
                        bottomContent.removeView(videoChatInter.getRootView());
                        stopRecord();
                        videoChatInter = null;
                        mLogtf.d("MIC_TIME:onJoin(onmic.off):time=" + (System.currentTimeMillis() - startTime));
                        if (nativeLibLoaded != 2) {
                            videoChatEvent.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
                        }
                    }
                    if ("on".equals(openhands)) {
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (!openhands.equals(oldOpenhandsStatus)) {
                                    MicTipDialog micTipDialog = new MicTipDialog(activity);
                                    micTipDialog.setSuccessTip("老师已开启举手，\n举手有机会与老师语音对话！");
                                    micTipDialog.showDialog();
//                            XESToastUtils.showToast(activity, "老师已开启举手，\n举手有机会与老师语音对话！");
                                }
                                btRaiseHands.setAlpha(1.0f);
                                if (finalContain) {
                                    if (!isSuccess) {
                                        if (raiseHandDialog == null) {
                                            BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                                            raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                                            raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                                            raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                                            raiseHandDialog.showDialog();
                                        }
                                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                                        boolean set = raiseHandDialog.setSuccess();
                                        if (set) {
                                            isSuccess = true;
                                            final RaiseHandDialog finalRaiseHandDialog = raiseHandDialog;
                                            btRaiseHands.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (finalRaiseHandDialog == raiseHandDialog) {
                                                        finalRaiseHandDialog.cancelDialog();
                                                        raiseHandDialog = null;
                                                    }
                                                }
                                            }, 3000);
                                        }
                                    }
                                } else {
                                    isSuccess = false;
                                    btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
                                }
                            }
                        };
                        if (!openhands.equals(oldOpenhandsStatus)) {
                            checkPermissionUnPerList(new OnJoinPermissionFinish(onmic, openhands, room, from, finalContain, runnable));
                            VideoChatLog.sno3(liveBll, from, "", isHasPermission);
                        }
                        if (!isHasPermission) {
                            if (!permissionPrompt) {
                                permissionPrompt = true;
                                XESToastUtils.showToast(activity, "老师开麦，但是你没有通话权限");
                            }
                            return;
                        }
                        runnable.run();
                    } else {//关麦-未举手状态
                        raisehand = false;
                        isSuccess = false;
                        btRaiseHands.setAlpha(0.4f);
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
                        if (raiseHandDialog != null) {
                            raiseHandDialog.cancelDialog();
                            raiseHandDialog = null;
                        }
                        if ("on".equals(oldOpenhandsStatus)) {
                            MicTipDialog micTipDialog = new MicTipDialog(activity);
                            micTipDialog.setFail("老师已经结束了这次举手!");
                            micTipDialog.showDialog();
                        }
                    }
                }
            }
        });
        //            if (contain) {
//                if (raiseHandDialog != null) {
//                    raiseHandDialog.setSuccess();
//                }
//            } else {
//                if (raiseHandDialog != null && (raiseHandDialog.status == RaiseHandDialog.WAIT || raiseHandDialog.status == RaiseHandDialog.GIVE_UP)) {
//                    raiseHandDialog.setFail();
//                }
//            }
    }

    /**
     * 检查权限后举手
     */
    private void raisehand() {
        MicTipDialog micTipDialog = new MicTipDialog(activity);
        micTipDialog.setSuccessTip("老师已开启举手，\n举手有机会与老师语音对话！");
        micTipDialog.showDialog();
        btRaiseHands.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoChatEvent.showLongMediaController();
            }
        }, 2900);
//                    XESToastUtils.showToast(activity, "老师已开启举手，\n举手有机会与老师语音对话！");
        btRaiseHands.setAlpha(1.0f);
        if (raiseHandDialog != null && raiseHandDialog.status == RaiseHandDialog.WAIT) {
            liveBll.requestMicro("", from);
        }
    }

    @Override
    public void raisehand(final String status, final String from, final String nonce) {
        mLogtf.d("raisehand:status=" + status);
        containMe = false;
        isFail = false;
        this.from = from;
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                openhandsStatus = status;
                if ("on".equals(status)) {
                    VideoChatLog.sno2(liveBll, from, nonce);
                    videoChatEvent.showLongMediaController();
                    checkPermissionUnPerList(new RaiseHandPermissionFinish(status, from, new Runnable() {
                        @Override
                        public void run() {
                            raisehand();
                        }
                    }));
                    VideoChatLog.sno3(liveBll, from, nonce, isHasPermission);
                    if (!isHasPermission) {
                        if (!permissionPrompt) {
                            permissionPrompt = true;
                            XESToastUtils.showToast(activity, "老师开麦，但是你没有通话权限");
                        }
                        return;
                    }
                    raisehand();
                } else {
                    StableLogHashMap logHashMap = new StableLogHashMap("getStopRaiseHand");
                    logHashMap.put("teacher_type", from);
                    liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
                    isSuccess = false;
                    if (raiseHandDialog != null) {
                        raiseHandDialog.cancelDialog();
                        raiseHandDialog = null;
                        raisehand = false;
                    }
                    btRaiseHands.setAlpha(0.4f);
                    MicTipDialog micTipDialog = new MicTipDialog(activity);
                    micTipDialog.setFail("老师已经结束了这次举手!");
                    micTipDialog.showDialog();
                }
            }
        });
    }

    @Override
    public void raiseHandStatus(final String status, int num, String from) {
        raiseHandCount = num;
        this.from = from;
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (raiseHandDialog == null && "on".equals(status) && "off".equals(onMic)) {
                    headsetPrompt = true;
                    raisehand = true;
                    BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                    raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                    raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                    raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                    raiseHandDialog.showDialog();
                }
            }
        });
    }

    @Override
    public void requestAccept(String from, String nonce) {
        onMic = "on";
        isFail = false;
        containMe = true;
        isSuccess = true;
        mLogtf.d("requestAccept");
        StableLogHashMap logHashMap = new StableLogHashMap("getSelection");
        logHashMap.put("teacher_type", from);
        liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (raiseHandDialog == null) {
                    mLogtf.d("requestAccept:raiseHandDialog=null");
                    BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                    raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                    raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                    raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                    raiseHandDialog.showDialog();
                }
                btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                raiseHandDialog.setSuccess();
                final RaiseHandDialog finalRaiseHandDialog = raiseHandDialog;
                btRaiseHands.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRaiseHandDialog == raiseHandDialog) {
                            finalRaiseHandDialog.cancelDialog();
                            raiseHandDialog = null;
                        }
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void startMicro(final String status, final String nonce, final boolean contain, final String room, final String from) {
        isFail = false;
        mLogtf.d("startMicro:status=" + status + ",contain=" + contain);
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                onmicStatus = status;
                if ("on".equals(status)) {
                    VideoChatLog.sno7(liveBll, from, contain ? "1" : "0", nonce);
                    if (contain) {
                        if (raiseHandDialog != null) {
                            raiseHandDialog.cancelDialog();
                            raiseHandDialog = null;
                        }
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                        startRecord(room, nonce);
                    } else {
                        if (raiseHandDialog != null) {
                            raiseHandDialog.setFail();
                            isFail = true;
                            final RaiseHandDialog finalRaiseHandDialog = raiseHandDialog;
                            btRaiseHands.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalRaiseHandDialog == raiseHandDialog) {
                                        finalRaiseHandDialog.cancelDialog();
                                        raiseHandDialog = null;
                                    }
                                }
                            }, 3000);
                        }
                    }
                } else {
                    if (raiseHandDialog != null) {
                        raiseHandDialog.cancelDialog();
                        raiseHandDialog = null;
                    }
                    Loger.d(TAG, "startMicro:raisehand=" + raisehand);
                    if (raisehand) {
                        BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                        raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                        raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                        raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                        raiseHandDialog.showDialog();
                    }
                }
            }
        });
    }

    @Override
    public void raiseHandCount(final int num) {
        this.raiseHandCount = num;
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (raiseHandDialog != null) {
                    raiseHandDialog.setRaiseHandsCount(num);
                }
            }
        });
    }

    @Override
    public void quit(String status, String room, String from) {
        StableLogHashMap logHashMap = new StableLogHashMap("getKick");
        logHashMap.put("teacher_type", from);
        logHashMap.put("status", status);
        liveBll.umsAgentDebugSys(eventId, logHashMap.getData());
        if ("on".equals(status)) {
            startMicro("on", "", true, room, from);
            return;
        }
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (videoChatInter != null) {
                    View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id.v_livevideo_mediacontrol_bottom);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom.getLayoutParams();
                    int height = 1;
                    if (lp.height != height) {
                        lp.height = height;
//                        vMediacontrolBottom.setLayoutParams(lp);
                        LayoutParamsUtil.setViewLayoutParams(vMediacontrolBottom, lp);
                    }
                    bottomContent.removeView(videoChatInter.getRootView());
                    stopRecord();
                    videoChatInter = null;
                    mLogtf.d("MIC_TIME:quit:time=" + (System.currentTimeMillis() - startTime));
                    if (nativeLibLoaded != 2) {
                        videoChatEvent.setVolume(VP.DEFAULT_STEREO_VOLUME, VP.DEFAULT_STEREO_VOLUME);
                    }
                }
            }
        });
    }

    public void onNetWorkChange(int netWorkType) {
        if (videoChatInter != null) {
            videoChatInter.onNetWorkChange(netWorkType);
        }
    }

    public void stopRecord() {
        if (videoChatInter != null) {
            videoChatInter.stopRecord();
            if (activity instanceof AudioRequest) {
                AudioRequest audioRequest = (AudioRequest) activity;
                audioRequest.release();
            }
            if (mLiveRemarkBll != null) {
                mLiveRemarkBll.setOnChat(false);
            }
        }
    }

    public void onDestroy() {
        if (wiredHeadsetReceiver != null) {
            activity.unregisterReceiver(wiredHeadsetReceiver);
            wiredHeadsetReceiver = null;
        }
        if (videoChatInter != null) {
            bottomContent.removeView(videoChatInter.getRootView());
            stopRecord();
            videoChatInter = null;
            mLogtf.d("MIC_TIME:onDestroy:time=" + (System.currentTimeMillis() - startTime));
        }
    }

    /**
     * 弹出toast，判断Video是不是在活动
     *
     * @param text
     */
    public void showToast(String text) {
        if (activity instanceof ActivityStatic) {
            ActivityStatic activityStatic = (ActivityStatic) activity;
            if (activityStatic.isResume()) {
                XESToastUtils.showToast(activity, text);
            }
        }
    }

    RaiseHandPermissionFinish currentPermission;

    /**
     * 举手权限回调
     */
    private class RaiseHandPermissionFinish implements OnPermissionFinish {
        String status;
        String from;
        Runnable runnable;

        public RaiseHandPermissionFinish(String status, String from, Runnable runnable) {
            this.status = status;
            this.from = from;
            this.runnable = runnable;
            currentPermission = this;
        }

        @Override
        public void onFinish() {
            if (status.equals(VideoChatBll.this.openhandsStatus) && from.equals(VideoChatBll.this.from)) {
                if (currentPermission == this) {
                    runnable.run();
                }
            }
        }
    }

    OnJoinPermissionFinish currentPermission2;

    /**
     * 加入房间权限回调
     */
    private class OnJoinPermissionFinish implements OnPermissionFinish {
        String onmic;
        String openhands;
        String room;
        String from;
        boolean contain;
        Runnable runnable;

        public OnJoinPermissionFinish(String onmic, String openhands, String room, String from, boolean contain, Runnable runnable) {
            this.onmic = onmic;
            this.openhands = openhands;
            this.room = room;
            this.from = from;
            this.contain = contain;
            this.runnable = runnable;
            currentPermission2 = this;
        }

        @Override
        public void onFinish() {
            if (onmic.equals(VideoChatBll.this.onMic) && openhands.equals(VideoChatBll.this.openhandsStatus)
                    && room.equals(VideoChatBll.this.room) && from.equals(VideoChatBll.this.from) && contain == containMe) {
                if (currentPermission2 == this) {
                    runnable.run();
                }
            }
        }
    }

    interface OnPermissionFinish {
        void onFinish();
    }

    private void checkPermissionUnPerList(final OnPermissionFinish onPermissionFinish) {
        final List<PermissionItem> unList = new ArrayList<>();
        List<PermissionItem> unList2 = XesPermission.checkPermissionUnPerList(activity, new PermissionCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {
                unList.remove(0);
                if (unList.isEmpty()) {
                    isHasPermission = true;
                    onPermissionFinish.onFinish();
                }
            }
        }, PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);
        unList.addAll(unList2);
        isHasPermission = unList.isEmpty();
    }
}
