package com.xueersi.parentsmeeting.modules.livevideo.videochat.business;

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
import android.util.Log;
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
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.SmallEnglishMicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.remark.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.MicTipDialog;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.RaiseHandDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.page.AgoraVideoChatPager;
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
    //举手的监听器
    private Button btRaiseHands;
    private VideoChatHttp videoChatHttp;
    private LiveAndBackDebug liveAndBackDebug;
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
    private ArrayList<VideoChatStartChange.ChatStartChange> chatStatusChanges = new ArrayList<>();
    //小英
    private boolean isSmallEnglish = false;
    //    小英Dialog
    private SmallEnglishMicTipDialog smallEnglishDialog;

    public VideoChatBll(Activity activity, VideoChatEvent videoChatEvent) {
        this.activity = activity;
        this.videoChatEvent = videoChatEvent;
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        ProxUtil.getProxUtil().put(activity, VideoChatStartChange.class, new VideoChatStartChange() {
            @Override
            public void addVideoChatStatrtChange(ChatStartChange chatStartChange) {
                chatStatusChanges.add(chatStartChange);
            }

            @Override
            public void removeVideoChatStatrtChange(ChatStartChange chatStartChange) {
                chatStatusChanges.remove(chatStartChange);
            }
        });
    }

    public void setLiveBll(LiveBll liveBll) {
        this.videoChatHttp = liveBll;
        if (liveBll instanceof LiveAndBackDebug) {
            liveAndBackDebug = liveBll;
        }
    }

    public void setVideoChatHttp(VideoChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
    }

    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void onLiveInit(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        if (getInfo != null) {
            isSmallEnglish = getInfo.getSmallEnglish();
        }

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
            MobclickAgent.reportError(activity, new Error(Build.MANUFACTURER + "$" + Build.MODEL + "$" + builder +
                    "-" + -1, t));
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
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate,
                channelConfig,
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
        if (audioRequest != null) {
            audioRequest.request(null);
        }
        for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
            chatStatusChange.onVideoChatStartChange(true);
        }
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
//            videoChatInter = new LicodeVideoChatPager(activity, this, classmateEntities, getInfo, liveBll,
// baseLiveMediaControllerBottom);
                } else if (nativeLibLoaded == 0) {
                    //不会发生
//                    videoChatInter = new VideoChatPager(activity, liveBll, getInfo);
                    return;
                } else {
                    videoChatInter = new AgoraVideoChatPager(activity, liveAndBackDebug, getInfo, videoChatEvent);
                }
                startTime = System.currentTimeMillis();
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                int screenWidth = ScreenUtils.getScreenWidth();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height);
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
                            View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id
                                    .v_livevideo_mediacontrol_bottom);
                            rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom
                                    .getLayoutParams();
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

    //老师开启举手更能后，手机这边点击举手，会调用这里的已举手1，已举手5也会调用
    View.OnClickListener btRaiseHandsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            baseLiveMediaControllerBottom.onChildViewClick(v);
            if (btRaiseHands.getAlpha() == 1.0f && videoChatInter == null && isHasPermission && (!isSuccess &&
                    !isFail)) {
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String nonce = StableLogHashMap.creatNonce();
                        VideoChatLog.sno4(liveAndBackDebug, nonce);
                        raisehand = true;
                        videoChatHttp.requestMicro(nonce, from);
                        videoChatHttp.chatHandAdd(new HttpCallBack(false) {
                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                Loger.d(TAG, "chatHandAdd:onPmSuccess:responseEntity=" + responseEntity.getJsonObject
                                        ());
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
                        BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                        if (!isSmallEnglish) {
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
                        } else {
                            smallEnglishDialog = new SmallEnglishMicTipDialog(activity);
//                            dialog.setText("点击举手参与\n语音互动吧!");
                            smallEnglishDialog.setText("已举手，现在有" + raiseHandCount + "位小朋友在排队哦~");
                            Loger.i(TAG, "已举手1");
                            smallEnglishDialog.showDialogAutoClose(3000);
                            if ("on".equals(onMic)) {
                                final SmallEnglishMicTipDialog finalSmallEnglishMicTipDialog = smallEnglishDialog;
                                bottomContent.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalSmallEnglishMicTipDialog == smallEnglishDialog) {
                                            finalSmallEnglishMicTipDialog.cancelDialog();
                                            raiseHandDialog = null;
                                        }
                                    }
                                }, 3000);
                            }
                        }
                    }
                };
                if (nativeLibLoaded == -1) {
                    checkNativeLibLoaded();
                }
                if (!hasWiredHeadset) {
                    if (!headsetPrompt) {
                        headsetPrompt = true;
                        VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity,
                                activity.getApplication(), false, VerifyCancelAlertDialog
                                .TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
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

    //放弃举手，现在已经去除这部分功能
    private RaiseHandDialog.RaiseHandGiveup raiseHandGiveup = new RaiseHandDialog.RaiseHandGiveup() {

        @Override
        public void onGiveup() {
            btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
            raisehand = false;
            videoChatHttp.giveupMicro(from);
            raiseHandDialog.cancelDialog();
            raiseHandDialog = null;
        }
    };

    @Override
    public void onJoin(final String onmic, final String openhands, final String room, final boolean classmateChange, final
    ArrayList<ClassmateEntity> classmateEntities, final String from) {
        Log.e("VideoChatBill", onmic + " " + openhands + " " + room + " " + classmateChange + " " + from);
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                boolean contain = false;
                onMic = onmic;
                VideoChatBll.this.room = room;
                VideoChatBll.this.from = from;
                VideoChatBll.this.classmateChange = classmateChange;
                VideoChatBll.this.classmateEntities = classmateEntities;
                for (ClassmateEntity classmateEntity : classmateEntities) {
                    if (classmateEntity.getId().equals(getInfo.getStuId())) {
                        contain = true;
                        break;
                    }
                }
                mLogtf.d("onJoin:from=" + from + ",onmic=" + onmic + ",openhands=" + openhands + ",size=" + classmateEntities.size() + "," +
                        "classmateChange=" + classmateChange + ",contain=" + contain + ",isSuccess=" + isSuccess);
                final boolean oldContainMe = containMe;
                containMe = contain;
                final boolean finalContain = contain;
                if (finalContain) {
                    headsetPrompt = true;
                }
                //开麦
                String oldonmicStatus = onmicStatus;
                onmicStatus = onmic;
                final String oldOpenhandsStatus = openhandsStatus;
                openhandsStatus = openhands;
                //老师那边打开开麦状态
                if ("on".equals(onmic)) {
                    btRaiseHands.setAlpha(1.0f);
                    if ("on".equals(oldonmicStatus) && oldContainMe != containMe) {
                        //如果使用小学英语萌萌哒皮肤
                        if (isSmallEnglish) {
                            SmallEnglishMicTipDialog dialog = new SmallEnglishMicTipDialog(activity);
                            if (finalContain) {
                                dialog.setSuccess("老师补位选中了你!", 3000);
                            } else {
                                dialog.setFail("你已被移出语音聊天室！耐心等待下次连麦机会！", 3000);
                            }
//                            dialog.showDialogAutoClose(3000);
                        } else {
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
                    }
                    //老师补位选中
                    if (finalContain) {
                        if (!isSmallEnglish) {
                            if (raiseHandDialog != null) {
                                raiseHandDialog.cancelDialog();
                                raiseHandDialog = null;
                            }
                        } else {
                            if (smallEnglishDialog != null) {
                                smallEnglishDialog.cancelDialog();
                                smallEnglishDialog = null;
                            }
                        }
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                        startRecord(room, "");
                    } else {
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
                        if (!isSmallEnglish) {
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
                        } else {
                            if (smallEnglishDialog != null) {
                                if (smallEnglishDialog != null) {
                                    isFail = true;
                                    boolean set = smallEnglishDialog.setFail("本次没有被选中哦，\n下次还有机会", 3000);
                                    if (set) {
                                        final SmallEnglishMicTipDialog finalSmallEnglishMicTipDialog =
                                                smallEnglishDialog;
                                        btRaiseHands.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (finalSmallEnglishMicTipDialog == smallEnglishDialog) {
                                                    finalSmallEnglishMicTipDialog.cancelDialog();
                                                    smallEnglishDialog = null;
                                                }
                                            }
                                        }, 3000);
                                    }
                                }
                            }
                        }
                        if (videoChatInter != null) {
                            View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id
                                    .v_livevideo_mediacontrol_bottom);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom
                                    .getLayoutParams();
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
                        View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id
                                .v_livevideo_mediacontrol_bottom);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom
                                .getLayoutParams();
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

                                    if (!isSmallEnglish) {
                                        MicTipDialog micTipDialog = new MicTipDialog(activity);
                                        micTipDialog.setSuccessTip("老师已开启举手，\n举手有机会与老师语音对话！");
                                        micTipDialog.showDialog();
                                    } else {
                                        SmallEnglishMicTipDialog smallEnglishMicTipDialog = new
                                                SmallEnglishMicTipDialog(activity);
                                        smallEnglishMicTipDialog.setText("点击举手参与语音互动吧");
                                        smallEnglishMicTipDialog.showDialogAutoClose(3000);
                                    }
//                            XESToastUtils.showToast(activity, "老师已开启举手，\n举手有机会与老师语音对话！");
                                }
                                btRaiseHands.setAlpha(1.0f);
                                if (finalContain) {
                                    if (!isSuccess) {
                                        if (!isSmallEnglish) {
                                            if (raiseHandDialog == null) {
                                                BaseApplication baseApplication = (BaseApplication) BaseApplication
                                                        .getContext();
                                                raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                                                raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                                                raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                                                raiseHandDialog.showDialog();
                                            }
                                            btRaiseHands.setBackgroundResource(R.drawable
                                                    .bg_livevideo_voicechat_raise_hands_check);
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
                                        } else {
                                            if (smallEnglishDialog == null) {
                                                smallEnglishDialog = new SmallEnglishMicTipDialog(activity);
                                                smallEnglishDialog.setText("已举手，现在有" + raiseHandCount + "位" +
                                                        "小朋友在排队哦~");
                                                Loger.i(TAG, "已举手2");
                                                smallEnglishDialog.showDialogAutoClose(3000);
                                            }
                                            btRaiseHands.setBackgroundResource(R.drawable
                                                    .bg_livevideo_voicechat_raise_hands_check);
                                            boolean set = smallEnglishDialog.setSuccess("你被老师选中啦！" +
                                                    "请等待连麦吧！", 3000);
                                            if (set) {
                                                isSuccess = true;
                                                final SmallEnglishMicTipDialog finalSmallEnglishMicTipDialog =
                                                        smallEnglishDialog;
                                                btRaiseHands.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (finalSmallEnglishMicTipDialog == smallEnglishDialog) {
                                                            finalSmallEnglishMicTipDialog.cancelDialog();
                                                            smallEnglishDialog = null;
                                                        }
                                                    }
                                                }, 3000);
                                            }
                                        }
                                    }
                                } else {
                                    isSuccess = false;
                                    btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands);
                                }
                            }
                        };
                        if (!openhands.equals(oldOpenhandsStatus)) {
                            checkPermissionUnPerList(new OnJoinPermissionFinish(onmic, openhands, room, from,
                                    finalContain, runnable));
                            VideoChatLog.sno3(liveAndBackDebug, from, "", isHasPermission);
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
                            if (!isSmallEnglish) {
                                MicTipDialog micTipDialog = new MicTipDialog(activity);
                                micTipDialog.setFail("老师结束了这次举手!");
                                micTipDialog.showDialog();
                            } else {
                                SmallEnglishMicTipDialog smallEnglishMicTipDialog = new SmallEnglishMicTipDialog
                                        (activity);
                                smallEnglishMicTipDialog.setFail("老师结束了这次举手!", 3000);
//                                smallEnglishMicTipDialog.showDialogAutoClose(3000);
                            }
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
//                if (raiseHandDialog != null && (raiseHandDialog.status == RaiseHandDialog.WAIT || raiseHandDialog
// .status == RaiseHandDialog.GIVE_UP)) {
//                    raiseHandDialog.setFail();
//                }
//            }
    }

    /**
     * 检查权限后举手
     */
    private void raisehand() {
        if (!isSmallEnglish) {
            MicTipDialog micTipDialog = new MicTipDialog(activity);
            micTipDialog.setSuccessTip("老师已开启举手，\n举手有机会与老师语音对话！");
            micTipDialog.showDialog();
        } else {
            SmallEnglishMicTipDialog smallEnglishMicTipDialog = new
                    SmallEnglishMicTipDialog(activity);
            smallEnglishMicTipDialog.setText("点击举手参与语音互动吧");
            smallEnglishMicTipDialog.showDialogAutoClose(3000);

        }
        btRaiseHands.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoChatEvent.showLongMediaController();
            }
        }, 2900);
//                    XESToastUtils.showToast(activity, "老师已开启举手，\n举手有机会与老师语音对话！");
        btRaiseHands.setAlpha(1.0f);
        if (raiseHandDialog != null && raiseHandDialog.status == RaiseHandDialog.WAIT) {
            videoChatHttp.requestMicro("", from);
        }
    }

    @Override
    public void raisehand(final String status, final String from, final String nonce) {
        mLogtf.d("raisehand:status=" + status);
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                containMe = false;
                isFail = false;
                VideoChatBll.this.from = from;
                openhandsStatus = status;
                if ("on".equals(status)) {
                    VideoChatLog.sno2(liveAndBackDebug, from, nonce);
                    videoChatEvent.showLongMediaController();
                    checkPermissionUnPerList(new RaiseHandPermissionFinish(status, from, new Runnable() {
                        @Override
                        public void run() {
                            raisehand();
                        }
                    }));
                    VideoChatLog.sno3(liveAndBackDebug, from, nonce, isHasPermission);
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
                    liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
                    isSuccess = false;
                    if (raiseHandDialog != null) {
                        raiseHandDialog.cancelDialog();
                        raiseHandDialog = null;
                        raisehand = false;
                    }
                    btRaiseHands.setAlpha(0.4f);
                    if (!isSmallEnglish) {
                        MicTipDialog micTipDialog = new MicTipDialog(activity);
                        micTipDialog.setFail("老师已经结束了这次举手!");
                        micTipDialog.showDialog();
                    } else {
                        SmallEnglishMicTipDialog smallEnglishMicTipDialog = new SmallEnglishMicTipDialog(activity);
                        smallEnglishMicTipDialog.setFail("老师已经结束了这次举手!", 3000);
//                        smallEnglishMicTipDialog.showDialogAutoClose(3000);
                    }
                }
            }
        });
    }

    //老师先打开举麦功能，手机先举麦，退出之后再次进入，直接调用这里的已举手
    @Override
    public void raiseHandStatus(final String status, int num, String from) {
        raiseHandCount = num;
        this.from = from;
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (!isSmallEnglish) {
                    if (raiseHandDialog == null && "on".equals(status) && "off".equals(onMic)) {
                        headsetPrompt = true;
                        raisehand = true;
                        BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                        raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                        raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                        raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                        raiseHandDialog.showDialog();
                    }
                } else {
                    if (smallEnglishDialog == null && "on".equals(status) && "off".equals(onMic)) {
                        headsetPrompt = true;
                        raisehand = true;
//                        BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
//                        raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
//                        raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
//                        raiseHandDialog.setRaiseHandsCount(raiseHandCount);
//                        raiseHandDialog.showDialog();
                        smallEnglishDialog = new SmallEnglishMicTipDialog(activity);
                        smallEnglishDialog.setText("已举手，现在有" + raiseHandCount + "位" +
                                "小朋友在排队哦~");
                        Loger.i(TAG, "已举手3");
                        smallEnglishDialog.showDialogAutoClose(3000);

                    }
                }
            }
        });
    }

    @Override
    public void requestAccept(String from, String nonce) {
        mLogtf.d("requestAccept");
        StableLogHashMap logHashMap = new StableLogHashMap("getSelection");
        logHashMap.put("teacher_type", from);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                onMic = "on";
                isFail = false;
                containMe = true;
                isSuccess = true;
                if (!isSmallEnglish) {
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
                } else {
                    if (smallEnglishDialog == null) {
                        smallEnglishDialog = new SmallEnglishMicTipDialog(activity);
                        smallEnglishDialog.setText("已举手，现在有" + raiseHandCount + "位" +
                                "小朋友在排队哦~");
                        smallEnglishDialog.showDialogAutoClose(3000);
                        Loger.i(TAG, "已举手2");
                    }
                    btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                    smallEnglishDialog.setSuccess("你被老师选中啦！\n" +
                            "请等待连麦吧！", 3000);
                    final SmallEnglishMicTipDialog finalSmallEnglishMicTipDialog = smallEnglishDialog;
                    btRaiseHands.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (finalSmallEnglishMicTipDialog == smallEnglishDialog) {
                                finalSmallEnglishMicTipDialog.cancelDialog();
                                smallEnglishDialog = null;
                            }
                        }
                    }, 3000);
                }
            }
        });
    }

    @Override
    public void startMicro(final String status, final String nonce, final boolean contain, final String room, final
    String from) {
        mLogtf.d("startMicro:status=" + status + ",contain=" + contain);
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                isFail = false;
                onmicStatus = status;
                //是小英不弹提示，其他的放在onJoin里处理
//                if (isSmallEnglish) {
//                    containMe = contain;
//                }
                if ("on".equals(status)) {
                    VideoChatLog.sno7(liveAndBackDebug, from, contain ? "1" : "0", nonce);
                    if (contain) {
                        if (!isSmallEnglish) {
                            if (raiseHandDialog != null) {
                                raiseHandDialog.cancelDialog();
                                raiseHandDialog = null;
                            }
                        } else {
                            if (smallEnglishDialog != null) {
                                smallEnglishDialog.cancelDialog();
                                smallEnglishDialog = null;
                            }
                        }
                        btRaiseHands.setBackgroundResource(R.drawable.bg_livevideo_voicechat_raise_hands_check);
                        startRecord(room, nonce);
                    } else {
                        if (!isSmallEnglish) {
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
                        } else {
                            if (smallEnglishDialog != null) {
                                smallEnglishDialog.setFail("本次没有被选中哦，\n下次还有机会！", 3000);
                                isFail = true;
                                final SmallEnglishMicTipDialog finalSmallEnglishMicTipDialog = smallEnglishDialog;
                                btRaiseHands.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalSmallEnglishMicTipDialog == smallEnglishDialog) {
                                            finalSmallEnglishMicTipDialog.cancelDialog();
                                            smallEnglishDialog = null;
                                        }
                                    }
                                }, 3000);
                            }
                        }
                    }
                } else {
                    if (!isSmallEnglish) {
                        if (raiseHandDialog != null) {
                            raiseHandDialog.cancelDialog();
                            raiseHandDialog = null;
                        }
                    } else {
                        if (smallEnglishDialog != null) {
                            smallEnglishDialog.cancelDialog();
                            smallEnglishDialog = null;
                        }
                    }
                    Loger.d(TAG, "startMicro:raisehand=" + raisehand);
                    if (raisehand) {
                        if (!isSmallEnglish) {
                            BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                            raiseHandDialog = new RaiseHandDialog(activity, baseApplication);
                            raiseHandDialog.setRaiseHandGiveup(raiseHandGiveup);
                            raiseHandDialog.setRaiseHandsCount(raiseHandCount);
                            raiseHandDialog.showDialog();
                        } else {//老师先选中你，然后踢走你，再结束上麦，会回调到这里
//                            smallEnglishDialog = new SmallEnglishMicTipDialog(activity);
//                            smallEnglishDialog.setText("已举手，现在有" + raiseHandCount + "位小朋友在排队哦~");
//                            Loger.i(TAG, "已举手4");
//                            smallEnglishDialog.showDialogAutoClose(3000);
                        }
                    }
                }
            }
        });
    }

    //老师开启举麦功能，学生未举手之前会走这里
    //老师开启举麦功能，学生点击举手之后，也会走这里
    @Override
    public void raiseHandCount(final int num) {
        this.raiseHandCount = num;
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (!isSmallEnglish) {
                    if (raiseHandDialog != null) {
                        raiseHandDialog.setRaiseHandsCount(num);
                    }
                } else {//小英
                    if (smallEnglishDialog != null) {
                        smallEnglishDialog.setText("已举手，现在有" + num + "位小朋友在排队哦~");
                        Loger.i(TAG, "已举手5");
                    }
                }
            }
        });
    }

    @Override
    public void quit(String status, String room, String from) {
        StableLogHashMap logHashMap = new StableLogHashMap("getKick");
        logHashMap.put("teacher_type", from);
        logHashMap.put("status", status);
        liveAndBackDebug.umsAgentDebugSys(eventId, logHashMap.getData());
        if ("on".equals(status)) {
            startMicro("on", "", true, room, from);
            return;
        }
        btRaiseHands.post(new Runnable() {
            @Override
            public void run() {
                if (videoChatInter != null) {
                    View vMediacontrolBottom = baseLiveMediaControllerBottom.findViewById(R.id
                            .v_livevideo_mediacontrol_bottom);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vMediacontrolBottom
                            .getLayoutParams();
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
            AudioRequest audioRequest = ProxUtil.getProxUtil().get(activity, AudioRequest.class);
            if (audioRequest != null) {
                audioRequest.release();
            }
            if (mLiveRemarkBll != null) {
                mLiveRemarkBll.setOnChat(false);
            }
            for (VideoChatStartChange.ChatStartChange chatStatusChange : chatStatusChanges) {
                chatStatusChange.onVideoChatStartChange(false);
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
        chatStatusChanges.clear();
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

        public OnJoinPermissionFinish(String onmic, String openhands, String room, String from, boolean contain,
                                      Runnable
                                              runnable) {
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
                    && room.equals(VideoChatBll.this.room) && from.equals(VideoChatBll.this.from) && contain ==
                    containMe) {
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
        List<PermissionItem> unList2 = XesPermission.checkPermissionUnPerList(activity, new
                LiveActivityPermissionCallback() {
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
